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
package com.cws.esolutions.core.utils;

import org.slf4j.Logger;
import javax.jms.Session;
import java.io.Serializable;
import javax.jms.Connection;
import javax.naming.Context;
import javax.jms.Destination;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.slf4j.LoggerFactory;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ConnectionFactory;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.activemq.ActiveMQConnectionFactory;

import com.cws.esolutions.core.Constants;
import com.cws.esolutions.core.CoreServiceBean;
import com.cws.esolutions.core.config.MQConfig;
import com.cws.esolutions.core.utils.exception.UtilityException;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.utils
 * MQUtils.java
 *
 * MQ Utility class for sending/receiving messages
 *
 * $Id: $
 * $Author: $
 * $Date: $
 * $Revision: $
 * @author kmhuntly@gmail.com
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * 35033355 @ Jun 11, 2013 9:07:22 AM
 *     Created.
 */
public final class MQUtils
{
    private static final String INIT_CONTEXT = "java:comp/env";
    private static final String CNAME = MQUtils.class.getName();
    private static final CoreServiceBean appBean = CoreServiceBean.getInstance();
    private static final MQConfig mqConfig = appBean.getConfigData().getMqConfig();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CNAME);

    public static final synchronized String sendMqMessage(final Serializable value) throws UtilityException
    {
        final String methodName = MQUtils.CNAME + "sendMqMessage(final Serializable value) throws UtilityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        Connection conn = null;
        Session session = null;
        Context envContext = null;
        InitialContext initCtx = null;
        MessageProducer producer = null;
        ConnectionFactory connFactory = null;

        final String connName = mqConfig.getConnectionName();
        final String requestQueue = mqConfig.getRequestQueue();
        final String correlationId = RandomStringUtils.randomAlphanumeric(64);

        if (DEBUG)
        {
            DEBUGGER.debug("String: {}", connName);
            DEBUGGER.debug("String: {}", requestQueue);
            DEBUGGER.debug("correlationId: {}", correlationId);
        }

        try
        {
            try
            {
                initCtx = new InitialContext();
                envContext = (Context) initCtx.lookup(MQUtils.INIT_CONTEXT);

                connFactory = (ConnectionFactory) envContext.lookup(connName);
            }
            catch (NamingException nx)
            {
                // we're probably not in a container
                connFactory = new ActiveMQConnectionFactory(connName);
            }

            if (DEBUG)
            {
                DEBUGGER.debug("ConnectionFactory: ", connFactory);
            }

            // Create a Connection
            conn = connFactory.createConnection();
            conn.start();

            if (DEBUG)
            {
                DEBUGGER.debug("Connection: ", conn);
            }

            // Create a Session
            session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);

            if (DEBUG)
            {
                DEBUGGER.debug("Session: ", session);
            }

            // Create a MessageProducer from the Session to the Topic or Queue
            try
            {
                producer = session.createProducer((Destination) envContext.lookup(requestQueue));
            }
            catch (NamingException nx)
            {
                // we're probably not in a container
                Destination destination = session.createQueue(requestQueue);

                if (DEBUG)
                {
                    DEBUGGER.debug("Destination: ", destination);
                }

                producer = session.createProducer(destination);
            }

            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            if (DEBUG)
            {
                DEBUGGER.debug("MessageProducer: {}", producer);
            }

            ObjectMessage message = session.createObjectMessage(true);
            message.setJMSCorrelationID(correlationId);

            if (DEBUG)
            {
                DEBUGGER.debug("correlationId: {}", correlationId);
            }

            message.setObject(value);

            if (DEBUG)
            {
                DEBUGGER.debug("ObjectMessage: ", message);
            }

            producer.send(message);
        }
        catch (JMSException jx)
        {
            ERROR_RECORDER.error(jx.getMessage(), jx);

            throw new UtilityException(jx.getMessage(), jx);
        }
        finally
        {
            try
            {
                // Clean up
                if (!(session == null))
                {
                    session.close();
                }

                if (!(conn == null))
                {
                    conn.close();
                }
            }
            catch (JMSException jx)
            {
                ERROR_RECORDER.error(jx.getMessage(), jx);
            }
        }

        return correlationId;
    }

    public static final synchronized Object getMqMessage(final String messageId) throws UtilityException
    {
        final String methodName = MQUtils.CNAME + "getMqMessage(final String messageId) throws UtilityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("messageId: {}", messageId);
        }

        Connection conn = null;
        Session session = null;
        Object response = null;
        Context envContext = null;
        InitialContext initCtx = null;
        MessageConsumer consumer = null;
        ConnectionFactory connFactory = null;

        final String connName = mqConfig.getConnectionName();
        final String responseQueue = mqConfig.getResponseQueue();

        if (DEBUG)
        {
            DEBUGGER.debug("String: {}", connName);
            DEBUGGER.debug("String: {}", responseQueue);
        }

        try
        {
            try
            {
                initCtx = new InitialContext();
                envContext = (Context) initCtx.lookup(MQUtils.INIT_CONTEXT);

                connFactory = (ConnectionFactory) envContext.lookup(connName);
            }
            catch (NamingException nx)
            {
                // we're probably not in a container
                connFactory = new ActiveMQConnectionFactory(connName);
            }

            if (DEBUG)
            {
                DEBUGGER.debug("ConnectionFactory: ", connFactory);
            }

            // Create a Connection
            conn = connFactory.createConnection();
            conn.start();

            if (DEBUG)
            {
                DEBUGGER.debug("Connection: ", conn);
            }

            // Create a Session
            session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            
            if (DEBUG)
            {
                DEBUGGER.debug("Session: ", session);
            }

            try
            {
                consumer = session.createConsumer((Destination) envContext.lookup(responseQueue), "JMSCorrelationID='" + messageId + "'");
            }
            catch (NamingException nx)
            {
                // we're probably not in a container
                Destination destination = session.createQueue((String) envContext.lookup(responseQueue));

                if (DEBUG)
                {
                    DEBUGGER.debug("Destination: {}", destination);
                }

                consumer = session.createConsumer(destination, "JMSCorrelationID='" + messageId + "'");
            }

            if (DEBUG)
            {
                DEBUGGER.debug("MessageConsumer: {}", consumer);
            }

            ObjectMessage message = (ObjectMessage) consumer.receive();

            if (DEBUG)
            {
                DEBUGGER.debug("ObjectMessage: {}", message);
            }

            response = message.getObject();

            if (DEBUG)
            {
                DEBUGGER.debug("Object: {}", response);
            }
        }
        catch (JMSException jx)
        {
            ERROR_RECORDER.error(jx.getMessage(), jx);

            throw new UtilityException(jx.getMessage(), jx);
        }
        catch (NamingException nx)
        {
            ERROR_RECORDER.error(nx.getMessage(), nx);

            throw new UtilityException(nx.getMessage(), nx);
        }
        finally
        {
            try
            {
                // Clean up
                if (!(session == null))
                {
                    session.close();
                }

                if (!(conn == null))
                {
                    conn.close();
                }
            }
            catch (JMSException jx)
            {
                ERROR_RECORDER.error(jx.getMessage(), jx);
            }
        }

        return response;
    }
}
