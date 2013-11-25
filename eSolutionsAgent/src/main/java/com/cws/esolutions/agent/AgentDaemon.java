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
package com.cws.esolutions.agent;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBException;
import org.apache.commons.daemon.Daemon;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;

import com.cws.esolutions.agent.Constants;
import com.cws.esolutions.agent.config.ConfigurationData;
import com.cws.esolutions.agent.exception.AgentException;
import com.cws.esolutions.agent.server.factory.AgentServerFactory;
/**
 * eSolutionsAgent
 * com.cws.esolutions.agent
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
public class AgentDaemon implements Daemon
{
    private Thread thread = null;

    private static final String LOG_CONFIG = "logConfig";
    private static final String APP_CONFIG = "appConfig";
    private static final String CNAME = AgentDaemon.class.getName();
    private static final AgentBean agentBean = AgentBean.getInstance();
    private static final String CURRENT_DIRECTORY = System.getProperty("user.dir");

    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + AgentDaemon.CNAME);
    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public AgentDaemon()
    {
        if (StringUtils.isBlank(System.getProperty("LOG_ROOT")))
        {
            File logDir = new File(AgentDaemon.CURRENT_DIRECTORY + "/logs");

            logDir.mkdirs();

            System.setProperty("LOG_ROOT", AgentDaemon.CURRENT_DIRECTORY + "/logs");
        }

        DOMConfigurator.configure(AgentDaemon.CURRENT_DIRECTORY + System.getProperty(AgentDaemon.LOG_CONFIG));
    }

    public static void main(final String[] args)
    {
        AgentDaemon daemon = new AgentDaemon();

        try
        {
            daemon.init(null);
            daemon.start();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
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

        final File xmlFile = new File(AgentDaemon.CURRENT_DIRECTORY + System.getProperty(AgentDaemon.APP_CONFIG));

        if (DEBUG)
        {
            DEBUGGER.debug("xmlFile: {}", xmlFile);
        }

        try
        {
            if (xmlFile.canRead())
            {
                // set the app configuration
                context = JAXBContext.newInstance(ConfigurationData.class);
                marshaller = context.createUnmarshaller();
                ConfigurationData configData = (ConfigurationData) marshaller.unmarshal(xmlFile);

                if (DEBUG)
                {
                    DEBUGGER.debug("ConfigurationData: {}", configData);
                }

                agentBean.setConfigData(configData);
            }
            else
            {
                // no xml url
                throw new AgentException("No configuration file was located. Shutting down !");
            }
        }
        catch (JAXBException jx)
        {
            ERROR_RECORDER.error(jx.getMessage(), jx);

            throw new DaemonInitException(jx.getMessage(), jx);
        }
        catch (AgentException ax)
        {
            ERROR_RECORDER.error(ax.getMessage(), ax);

            throw new DaemonInitException(ax.getMessage(), ax);
        }
    }

    @Override
    public void start() throws Exception
    {
        final String methodName = AgentDaemon.CNAME + "#start() throws Exception";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        try
        {
            this.thread = (Thread) AgentServerFactory.getAgentServer(agentBean.getConfigData().getServerConfig().getServerClass());
            this.thread.start();
        }
        catch (Exception ex)
        {
            ERROR_RECORDER.error(ex.getMessage(), ex);

            System.err.println("Failed to start service: " + ex.getMessage());

            System.exit(1);
        }
    }

    @Override
    public void stop()
    {
        final String methodName = AgentDaemon.CNAME + "#stop()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        try
        {
            agentBean.setStopServer(true);

            this.thread.interrupt();

            System.exit(0);
        }
        catch (Exception ex)
        {
            ERROR_RECORDER.error(ex.getMessage(), ex);

            System.err.println("Failed to stop service: " + ex.getMessage());

            System.exit(1);
        }
    }

    @Override
    public void destroy()
    {
        final String methodName = AgentDaemon.CNAME + "#destroy()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }
    }
}
