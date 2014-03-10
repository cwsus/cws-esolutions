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
package com.cws.esolutions.core.utils;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.utils
 * File: MQUtils.java
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Jan 4, 2013 3:36:54 PM
 *     Created.
 */
import java.util.List;
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

import com.cws.esolutions.core.CoreServiceConstants;
import com.cws.esolutions.security.utils.PasswordUtils;
import com.cws.esolutions.core.utils.exception.UtilityException;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 */
public final class MQUtils
{
    private static final String INIT_CONTEXT = "java:comp/env";
    private static final String CNAME = MQUtils.class.getName();

    static final Logger DEBUGGER = LoggerFactory.getLogger(CoreServiceConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(CoreServiceConstants.ERROR_LOGGER + CNAME);

    public static final void main(final String[] args)
    {
        final String methodName = MQUtils.CNAME + "#main(final String[] args)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", args);
        }
    }

    /**
     * TODO: Add in the method description/comments
     *
     * @param value - A serializable object to be sent to the MQ queue
     * @return <code>String</code> - the JMS correlation ID associated with the message
     * @throws UtilityException if an error occurs while putting the message on the configured queue
     */
    public static final synchronized String sendMqMessage(final String connName, final List<String> authData, final String requestQueue, final String targetHost, final Serializable value) throws UtilityException
    {
        final String methodName = MQUtils.CNAME + "sendMqMessage(final String connName, final List<String> authData, final String requestQueue, final String targetHost, final Serializable value) throws UtilityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", connName);
            DEBUGGER.debug("Value: {}", requestQueue);
            DEBUGGER.debug("Value: {}", targetHost);
            DEBUGGER.debug("Value: {}", value);
        }

        Connection conn = null;
        Session session = null;
        Context envContext = null;
        InitialContext initCtx = null;
        MessageProducer producer = null;
        ConnectionFactory connFactory = null;

        final String correlationId = RandomStringUtils.randomAlphanumeric(64);

        if (DEBUG)
        {
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
                DEBUGGER.debug("ConnectionFactory: {}", connFactory);
            }

            if (connFactory == null)
            {
                throw new UtilityException("Unable to create connection factory for provided name");
            }

            // Create a Connection
            conn = connFactory.createConnection(authData.get(0), PasswordUtils.decryptText(authData.get(1), authData.get(2).length()));
            conn.start();

            if (DEBUG)
            {
                DEBUGGER.debug("Connection: {}", conn);
            }

            // Create a Session
            session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);

            if (DEBUG)
            {
                DEBUGGER.debug("Session: {}", session);
            }

            // Create a MessageProducer from the Session to the Topic or Queue
            if (envContext != null)
            {
                try
                {
                    producer = session.createProducer((Destination) envContext.lookup(requestQueue));
                }
                catch (NamingException nx)
                {
                    throw new UtilityException(nx.getMessage(), nx);
                }
            }
            else
            {
                Destination destination = session.createTopic(requestQueue);

                if (DEBUG)
                {
                    DEBUGGER.debug("Destination: {}", destination);
                }

                producer = session.createProducer(destination);
            }

            if (producer == null)
            {
                throw new JMSException("Failed to create a producer object");
            }

            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            if (DEBUG)
            {
                DEBUGGER.debug("MessageProducer: {}", producer);
            }

            ObjectMessage message = session.createObjectMessage(true);
            message.setJMSCorrelationID(correlationId);
            message.setStringProperty("targetHost", targetHost);

            if (DEBUG)
            {
                DEBUGGER.debug("correlationId: {}", correlationId);
            }

            message.setObject(value);

            if (DEBUG)
            {
                DEBUGGER.debug("ObjectMessage: {}", message);
            }

            producer.send(message);
        }
        catch (JMSException jx)
        {
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
                    conn.stop();
                }
            }
            catch (JMSException jx)
            {
                ERROR_RECORDER.error(jx.getMessage(), jx);
            }
        }

        return correlationId;
    }

    /**
     * TODO: Add in the method description/comments
     *
     * @param messageId - The correlation ID associated with the message
     * @return Object - A serializable object as obtained from the MQ message
     * @throws UtilityException if an error occurs during the MQ get operation
     */
    public static final synchronized Object getMqMessage(final String connName, final List<String> authData, final String responseQueue, final long timeout, final String messageId) throws UtilityException
    {
        final String methodName = MQUtils.CNAME + "getMqMessage(final String connName, final List<String> authData, final String responseQueue, final long timeout, final String messageId) throws UtilityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", connName);
            DEBUGGER.debug("Value: {}", responseQueue);
            DEBUGGER.debug("Value: {}", timeout);
            DEBUGGER.debug("Value: {}", messageId);
        }

        Connection conn = null;
        Session session = null;
        Object response = null;
        Context envContext = null;
        MessageConsumer consumer = null;
        ConnectionFactory connFactory = null;

        try
        {
            try
            {
                InitialContext initCtx = new InitialContext();
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
                DEBUGGER.debug("ConnectionFactory: {}", connFactory);
            }

            if (connFactory == null)
            {
                throw new UtilityException("Unable to create connection factory for provided name");
            }

            // Create a Connection
            conn = connFactory.createConnection(authData.get(0), PasswordUtils.decryptText(authData.get(1), authData.get(2).length()));
            conn.start();

            if (DEBUG)
            {
                DEBUGGER.debug("Connection: {}", conn);
            }

            // Create a Session
            session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            
            if (DEBUG)
            {
                DEBUGGER.debug("Session: {}", session);
            }

            if (envContext != null)
            {
                try
                {
                    consumer = session.createConsumer((Destination) envContext.lookup(responseQueue), "JMSCorrelationID='" + messageId + "'");
                }
                catch (NamingException nx)
                {
                    throw new UtilityException(nx.getMessage(), nx);
                }
            }
            else
            {
                Destination destination = session.createQueue(responseQueue);

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

            ObjectMessage message = (ObjectMessage) consumer.receive(timeout);

            if (DEBUG)
            {
                DEBUGGER.debug("ObjectMessage: {}", message);
            }

            if (message == null)
            {
                throw new UtilityException("Failed to retrieve message within the timeout specified.");
            }

            response = message.getObject();
            message.acknowledge();

            if (DEBUG)
            {
                DEBUGGER.debug("Object: {}", response);
            }
        }
        catch (JMSException jx)
        {
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
                    conn.stop();
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
