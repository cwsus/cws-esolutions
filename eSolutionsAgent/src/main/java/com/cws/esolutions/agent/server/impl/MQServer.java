/**
 * Copyright (c) 2009 - 2013 By: CWS, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cws.esolutions.agent.server.impl;

import javax.jms.Session;
import javax.jms.Message;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.MessageConsumer;
import javax.jms.ExceptionListener;
import javax.jms.ConnectionFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.activemq.ActiveMQConnectionFactory;

import com.cws.esolutions.agent.AgentBean;
import com.cws.esolutions.agent.dto.AgentRequest;
import com.cws.esolutions.agent.dto.AgentResponse;
import com.cws.esolutions.agent.config.ServerConfig;
import com.cws.esolutions.agent.server.interfaces.AgentServer;
import com.cws.esolutions.agent.server.processors.impl.AgentRequestProcessorImpl;
import com.cws.esolutions.agent.server.processors.interfaces.IAgentRequestProcessor;
/**
 * eSolutionsAgent
 * com.cws.esolutions.agent.server.impl
 * AgentDaemon.java
 *
 * $Id: $
 * $Author: $
 * $Date: $
 * $Revision: $
 * @author khuntly
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * khuntly @ Nov 17, 2012 10:23:31 PM
 *     Created.
 */
public class MQServer extends Thread implements AgentServer, MessageListener, ExceptionListener
{
    private Connection conn = null;
    private Session session = null;
    private Destination request = null;
    private Destination response = null;
    private MessageConsumer consumer = null;
    private MessageProducer producer = null;
    private ConnectionFactory connFactory = null;

    private static final AgentBean agentBean = AgentBean.getInstance();
    private static final IAgentRequestProcessor processor = new AgentRequestProcessorImpl();
    private static final ServerConfig serverConfig = agentBean.getConfigData().getServerConfig();

    public void run()
    {
        final String methodName = MQServer.CNAME + "#run()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        try
        {
            // always a tcp conn, i have yet to find an mq implementation that uses udp
            connFactory = new ActiveMQConnectionFactory("tcp://" + serverConfig.getListenAddress() + ":" + serverConfig.getPortNumber());

            if (DEBUG)
            {
                DEBUGGER.debug("ConnectionFactory: {}", connFactory);
            }

            conn = connFactory.createConnection();
            conn.start();

            if (DEBUG)
            {
                DEBUGGER.debug("Connection: {}", conn);
            }

            session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);

            if (DEBUG)
            {
                DEBUGGER.debug("Session: {}", session);
            }

            request = session.createQueue(serverConfig.getRequestQueue());

            if (DEBUG)
            {
                DEBUGGER.debug("Destination: {}", request);
            }

            response = session.createQueue(serverConfig.getResponseQueue());

            if (DEBUG)
            {
                DEBUGGER.debug("Destination: {}", response);
            }

            consumer = session.createConsumer(request);
            consumer.setMessageListener(this);

            if (DEBUG)
            {
                DEBUGGER.debug("MessageConsumer: {}", consumer);
            }

            producer = session.createProducer(response);

            if (DEBUG)
            {
                DEBUGGER.debug("MessageProducer: {}", producer);
            }
        }
        catch (JMSException jx)
        {
            ERROR_RECORDER.error(jx.getMessage(), jx);
        }
    }

    @Override
    public void onException(final JMSException exception)
    {
        final String methodName = MQServer.CNAME + "#onException(final JMSException exception)";
        
        if (DEBUG)
        {
        	DEBUGGER.debug(methodName);
            DEBUGGER.debug("JMSException: ", exception);
        }
    }

    @Override
    public void onMessage(final Message message)
    {
        final String methodName = MQServer.CNAME + "#onMessage(final Message message)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Message: ", message);
        }

        ObjectMessage mqMessage = null;
        AgentRequest agentRequest = null;
        AgentResponse agentResponse = null;

        try
        {
            mqMessage = (ObjectMessage) message;

            if (DEBUG)
            {
                DEBUGGER.debug("mqMessage: ", mqMessage);
            }

            if ((StringUtils.equals(serverConfig.getRequestQueue(), serverConfig.getResponseQueue())) && (mqMessage.getObject() instanceof AgentResponse)) 
            {
                return;
            }

            agentRequest = (AgentRequest) mqMessage.getObject();

            if (DEBUG)
            {
                DEBUGGER.debug("agentRequest: ", agentRequest);
            }

            agentResponse = processor.processRequest(agentRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("AgentResponse: ", agentResponse);
            }

            ObjectMessage oMessage = session.createObjectMessage(true);
            oMessage.setObject(agentResponse);
            oMessage.setJMSCorrelationID(message.getJMSCorrelationID());

            if (DEBUG)
            {
                DEBUGGER.debug("ObjectMessage: {}", oMessage);
            }

            producer.send(response, oMessage);
        }
        catch (Exception ex)
        {
            ERROR_RECORDER.error(ex.getMessage(), ex);
        }
    }
}
