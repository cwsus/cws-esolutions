/*
 * Copyright (c) 2009 - 2014 CaspersBox Web Services
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
package com.cws.esolutions.agent.mq;
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
import javax.jms.Destination;
import javax.jms.JMSException;
import org.slf4j.LoggerFactory;
import javax.jms.ObjectMessage;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import org.apache.commons.lang.StringUtils;

import com.cws.esolutions.agent.AgentBean;
import com.cws.esolutions.agent.AgentConstants;
import com.cws.esolutions.agent.dto.AgentRequest;
import com.cws.esolutions.agent.dto.AgentResponse;
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
public class MQMessageHandler implements MessageListener
{
    private static final String CNAME = MQMessageHandler.class.getName();
    private static final AgentBean agentBean = AgentBean.getInstance();
    private static final IAgentRequestProcessor processor = new AgentRequestProcessorImpl();
    private static final ServerConfig serverConfig = agentBean.getConfigData().getServerConfig();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(AgentConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(AgentConstants.ERROR_LOGGER + CNAME);

    @Override
    public void onMessage(final Message message)
    {
        final String methodName = MQMessageHandler.CNAME + "#onMessage(final Message message)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Message: {}", message);
        }

        final Session session = agentBean.getSession();
        final MessageProducer producer = agentBean.getProducer();
        final Destination destination = MQMessageHandler.agentBean.getResponseQueue();

        if (DEBUG)
        {
            DEBUGGER.debug("Session: {}", session);
            DEBUGGER.debug("MessageProducer: {}", producer);
            DEBUGGER.debug("Destination: {}", destination);
        }

        try
        {
            ObjectMessage mqMessage = (ObjectMessage) message;

            if (DEBUG)
            {
                DEBUGGER.debug("mqMessage: {}", mqMessage);
            }

            if ((StringUtils.equals(MQMessageHandler.serverConfig.getRequestQueue(), MQMessageHandler.serverConfig.getResponseQueue())) && (mqMessage.getObject() instanceof AgentResponse)) 
            {
                return;
            }

            AgentRequest agentRequest = (AgentRequest) mqMessage.getObject();

            if (DEBUG)
            {
                DEBUGGER.debug("agentRequest: {}", agentRequest);
            }

            mqMessage.acknowledge();

            AgentResponse agentResponse = MQMessageHandler.processor.processRequest(agentRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("AgentResponse: {}", agentResponse);
            }

            ObjectMessage oMessage = session.createObjectMessage(true);
            oMessage.setObject(agentResponse);
            oMessage.setJMSCorrelationID(message.getJMSCorrelationID());

            if (DEBUG)
            {
                DEBUGGER.debug("ObjectMessage: {}", oMessage);
            }

            producer.send(destination, oMessage);
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
