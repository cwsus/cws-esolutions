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
package com.cws.esolutions.agent;
/*
 * Project: eSolutionsAgent
 * Package: com.cws.esolutions.agent
 * File: AgentDaemon.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.io.File;
import org.slf4j.Logger;
import javax.jms.Session;
import javax.jms.Connection;
import java.net.InetAddress;
import javax.jms.Destination;
import javax.jms.JMSException;
import org.slf4j.LoggerFactory;
import javax.jms.MessageProducer;
import javax.jms.MessageConsumer;
import javax.xml.bind.JAXBContext;
import javax.jms.ConnectionFactory;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBException;
import java.net.UnknownHostException;
import org.apache.commons.daemon.Daemon;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.commons.daemon.DaemonContext;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.daemon.DaemonInitException;

import com.cws.esolutions.agent.AgentConstants;
import com.cws.esolutions.agent.mq.MQMessageHandler;
import com.cws.esolutions.agent.config.enums.OSType;
import com.cws.esolutions.agent.mq.MQExceptionHandler;
import com.cws.esolutions.security.utils.PasswordUtils;
import com.cws.esolutions.agent.config.xml.ConfigurationData;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @see org.apache.commons.daemon.Daemon
 */
public class AgentDaemon implements Daemon
{
    private int exitCode = -1;
    private Connection conn = null;
    private Session session = null;
    private Destination request = null;
    private Destination response = null;
    private MessageConsumer consumer = null;
    private MessageProducer producer = null;
    private ConnectionFactory connFactory = null;

    private static final String LOG_CONFIG = "logConfig";
    private static final String APP_CONFIG = "appConfig";
    private static final String CNAME = AgentDaemon.class.getName();
    private static final AgentBean agentBean = AgentBean.getInstance();

    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(AgentConstants.ERROR_LOGGER + AgentDaemon.CNAME);
    private static final Logger DEBUGGER = LoggerFactory.getLogger(AgentConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public AgentDaemon()
    {
        if (StringUtils.isBlank(System.getProperty("LOG_ROOT")))
        {
            File logDir = new File(AgentConstants.CURRENT_DIRECTORY + "/logs");

            logDir.mkdirs();

            System.setProperty("LOG_ROOT", AgentConstants.CURRENT_DIRECTORY + "/logs");
        }

        DOMConfigurator.configure(AgentConstants.CURRENT_DIRECTORY + System.getProperty(AgentDaemon.LOG_CONFIG));
    }

    public static void main(final String[] args)
    {
        AgentDaemon daemon = new AgentDaemon();

        if (args.length != 1)
        {
            AgentDaemon.usage();

            return;
        }

        try
        {
            if (StringUtils.equals("start", args[0]))
            {
                daemon.init(null);
                daemon.start();
            }
            else if (StringUtils.equals("stop", args[0]))
            {
                daemon.stop();
            }
            else
            {
                AgentDaemon.usage();
            }
        }
        catch (DaemonInitException dix)
        {
            ERROR_RECORDER.error(dix.getMessage(), dix);

            System.exit(-1);
        }
    }

    public void init(final DaemonContext dContext) throws DaemonInitException
    {
        final String methodName = AgentDaemon.CNAME + "#init(final DaemonContext dContext) throws DaemonInitException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("DaemonContext: {}", dContext);
        }

        JAXBContext context = null;
        Unmarshaller marshaller = null;

        final File xmlFile = new File(AgentConstants.CURRENT_DIRECTORY + System.getProperty(AgentDaemon.APP_CONFIG));

        if (DEBUG)
        {
            DEBUGGER.debug("xmlFile: {}", xmlFile);
        }

        try
        {
            if (!(xmlFile.canRead()))
            {
                throw new DaemonInitException("No configuration file was located. Shutting down !");
            }

            // set the app configuration
            context = JAXBContext.newInstance(ConfigurationData.class);
            marshaller = context.createUnmarshaller();
            ConfigurationData configData = (ConfigurationData) marshaller.unmarshal(xmlFile);

            if (DEBUG)
            {
                DEBUGGER.debug("ConfigurationData: {}", configData);
            }

            String osName = System.getProperty("os.name").toLowerCase();

            if (DEBUG)
            {
                DEBUGGER.debug("osName: {}", osName);
            }

            if (osName.indexOf("win") >= 0)
            {
                AgentDaemon.agentBean.setOsType(OSType.WINDOWS);
            }
            else if (osName.indexOf("mac") >= 0)
            {
                AgentDaemon.agentBean.setOsType(OSType.MAC);
            }
            else if ((osName.indexOf("nix") >= 0) || (osName.indexOf("sunos") >= 0) || (osName.indexOf("aix") >= 0))
            {
                AgentDaemon.agentBean.setOsType(OSType.UNIX);
            }

            AgentDaemon.agentBean.setHostName(InetAddress.getLocalHost().getHostName());
            AgentDaemon.agentBean.setConfigData(configData);
        }
        catch (JAXBException jx)
        {
            ERROR_RECORDER.error(jx.getMessage(), jx);

            this.exitCode = 1;
            stop();
        }
        catch (UnknownHostException uhx)
        {
            ERROR_RECORDER.error(uhx.getMessage(), uhx);

            this.exitCode = 1;
            stop();
        }
    }

    public void start()
    {
        final String methodName = AgentDaemon.CNAME + "#start()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        try
        {
            this.connFactory = new ActiveMQConnectionFactory(AgentDaemon.agentBean.getConfigData().getServerConfig().getConnectionName());

            if (DEBUG)
            {
                DEBUGGER.debug("ConnectionFactory: {}", this.connFactory);
            }

            this.conn = this.connFactory.createConnection(AgentDaemon.agentBean.getConfigData().getServerConfig().getUsername(),
                    PasswordUtils.decryptText(AgentDaemon.agentBean.getConfigData().getServerConfig().getPassword(),
                            AgentDaemon.agentBean.getConfigData().getServerConfig().getSalt().length(),
                            AgentDaemon.agentBean.getConfigData().getServerConfig().getEncryptionAlgorithm(),
                            AgentDaemon.agentBean.getConfigData().getServerConfig().getEncryptionInstance(),
                            AgentDaemon.agentBean.getConfigData().getServerConfig().getEncoding()));
            
            this.conn.setExceptionListener(new MQExceptionHandler());
            this.conn.setClientID(AgentDaemon.agentBean.getConfigData().getServerConfig().getClientId());
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

            this.request = this.session.createTopic(AgentDaemon.agentBean.getConfigData().getServerConfig().getRequestQueue());
            this.response = this.session.createQueue(AgentDaemon.agentBean.getConfigData().getServerConfig().getResponseQueue());

            if (DEBUG)
            {
                DEBUGGER.debug("Destination: {}", this.request);
                DEBUGGER.debug("Destination: {}", this.response);
            }

            this.consumer = this.session.createConsumer(this.request, "targetHost='" + AgentDaemon.agentBean.getHostName() + "'");
            this.consumer.setMessageListener(new MQMessageHandler());

            if (DEBUG)
            {
                DEBUGGER.debug("MessageConsumer: {}", this.consumer);
            }

            this.producer = this.session.createProducer(this.response);

            if (DEBUG)
            {
                DEBUGGER.debug("MessageProducer: {}", this.producer);
            }

            AgentDaemon.agentBean.setResponseQueue(this.response);
            AgentDaemon.agentBean.setSession(this.session);
            AgentDaemon.agentBean.setProducer(this.producer);
        }
        catch (SecurityException sx)
        {
            ERROR_RECORDER.error(sx.getMessage(), sx);

            this.exitCode = 1;
            stop();
        }
        catch (JMSException jx)
        {
            ERROR_RECORDER.error(jx.getMessage(), jx);

            this.exitCode = 1;
            stop();
        }
    }

    public void stop()
    {
        final String methodName = AgentDaemon.CNAME + "#stop()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        final MessageProducer producer = AgentDaemon.agentBean.getProducer();
        final Session session = AgentDaemon.agentBean.getSession();

        try
        {
            if (producer != null)
            {
                AgentDaemon.agentBean.getProducer().close();
            }

            if (session != null)
            {
                AgentDaemon.agentBean.getSession().close();
            }

            if (this.consumer != null)
            {
                this.consumer.close();
            }

            if (this.conn != null)
            {
                this.conn.close();
                this.conn.stop();
            }
        }
        catch (JMSException jx)
        {
            ERROR_RECORDER.error(jx.getMessage(), jx);

            this.exitCode = 1;
        }

        destroy();
    }

    public void destroy()
    {
        final String methodName = AgentDaemon.CNAME + "#destroy()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        this.conn = null;
        this.session = null;
        this.request = null;
        this.response = null;
        this.consumer = null;
        this.producer = null;
        this.connFactory = null;

        System.exit(this.exitCode);
    }

    private static final void usage()
    {
        System.out.println("eSolutionsAgent Usage");
        System.out.println("\t Java Options: ");
        System.out.println("\t\t appConfig: The full path to the server configuration file.");
        System.out.println("\t\t logConfig: The full path to the logging configuration file.");
        System.out.println("\t\t logRoot: The full path to the desired location for application logs.");
        System.out.println("\t Program Options: ");
        System.out.println("\t\t start: Start the agent daemon");
        System.out.println("\t\t stop: Stop the agent daemon");

        System.exit(-1);
    }
}
