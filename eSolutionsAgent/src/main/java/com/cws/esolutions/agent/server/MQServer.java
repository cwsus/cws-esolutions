/*
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
package com.cws.esolutions.agent.server;
/*
 * Project: eSolutionsAgent
 * Package: com.cws.esolutions.agent
 * File: AgentConstants.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
import javax.jms.Session;
import javax.jms.Message;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import org.slf4j.LoggerFactory;
import javax.jms.ObjectMessage;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.MessageConsumer;
import javax.jms.ExceptionListener;
import javax.jms.ConnectionFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.activemq.ActiveMQConnectionFactory;

import com.cws.esolutions.agent.AgentBean;
import com.cws.esolutions.agent.AgentConstants;
import com.cws.esolutions.agent.dto.AgentRequest;
import com.cws.esolutions.agent.dto.AgentResponse;
import com.cws.esolutions.agent.utils.PasswordUtils;
import com.cws.esolutions.agent.config.xml.ServerConfig;
import com.cws.esolutions.agent.exception.AgentException;
import com.cws.esolutions.agent.processors.impl.AgentRequestProcessorImpl;
import com.cws.esolutions.agent.processors.interfaces.IAgentRequestProcessor;
/**
 * TODO: Add class information/description
 *
 * @author 35033355
 * @version 1.0
 */
public class MQServer extends Thread implements MessageListener, ExceptionListener
{
    private Connection conn = null;
    private Session session = null;
    private Destination request = null;
    private Destination response = null;
    private MessageConsumer consumer = null;
    private MessageProducer producer = null;
    private ConnectionFactory connFactory = null;

    private static final String CNAME = MQServer.class.getName();
    private static final AgentBean agentBean = AgentBean.getInstance();
    private static final IAgentRequestProcessor processor = new AgentRequestProcessorImpl();
    private static final ServerConfig serverConfig = agentBean.getConfigData().getServerConfig();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(AgentConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(AgentConstants.ERROR_LOGGER + CNAME);

    @Override
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
            this.connFactory = new ActiveMQConnectionFactory(serverConfig.getConnectionName());

            if (DEBUG)
            {
                DEBUGGER.debug("ConnectionFactory: {}", this.connFactory);
            }

            this.conn = this.connFactory.createConnection(serverConfig.getUsername(),
                    PasswordUtils.decryptText(serverConfig.getPassword(), serverConfig.getSalt().length()));
            this.conn.start();

            if (DEBUG)
            {
                DEBUGGER.debug("Connection: {}", this.conn);
            }

            this.session = this.conn.createSession(false, Session.AUTO_ACKNOWLEDGE);

            if (DEBUG)
            {
                DEBUGGER.debug("Session: {}", this.session);
            }

            this.request = this.session.createTopic(serverConfig.getRequestQueue());

            if (DEBUG)
            {
                DEBUGGER.debug("Destination: {}", this.request);
            }

            this.response = this.session.createQueue(serverConfig.getResponseQueue());

            if (DEBUG)
            {
                DEBUGGER.debug("Destination: {}", this.response);
            }

            this.consumer = this.session.createConsumer(this.request, "targetHost='" + agentBean.getHostName() + "'");
            this.consumer.setMessageListener(this);

            if (DEBUG)
            {
                DEBUGGER.debug("MessageConsumer: {}", this.consumer);
            }

            this.producer = this.session.createProducer(this.response);

            if (DEBUG)
            {
                DEBUGGER.debug("MessageProducer: {}", this.producer);
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

        try
        {
            ObjectMessage mqMessage = (ObjectMessage) message;

            if (DEBUG)
            {
                DEBUGGER.debug("mqMessage: ", mqMessage);
            }

            if ((StringUtils.equals(serverConfig.getRequestQueue(), serverConfig.getResponseQueue())) && (mqMessage.getObject() instanceof AgentResponse)) 
            {
                return;
            }

            AgentRequest agentRequest = (AgentRequest) mqMessage.getObject();

            if (DEBUG)
            {
                DEBUGGER.debug("agentRequest: ", agentRequest);
            }

            mqMessage.acknowledge();

            AgentResponse agentResponse = processor.processRequest(agentRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("AgentResponse: ", agentResponse);
            }

            ObjectMessage oMessage = this.session.createObjectMessage(true);
            oMessage.setObject(agentResponse);
            oMessage.setJMSCorrelationID(message.getJMSCorrelationID());

            if (DEBUG)
            {
                DEBUGGER.debug("ObjectMessage: {}", oMessage);
            }

            this.producer.send(this.response, oMessage);
        }
        catch (JMSException jx)
        {
            ERROR_RECORDER.error(jx.getMessage(), jx);
        }
        catch (AgentException ax)
        {
            ERROR_RECORDER.error(ax.getMessage(), ax);
        }
    }
}
