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
package com.cws.esolutions.security.listeners;

import java.net.URL;
import java.util.Map;
import org.slf4j.Logger;
import java.util.HashMap;
import javax.sql.DataSource;
import javax.naming.Context;
import org.slf4j.LoggerFactory;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.naming.InitialContext;
import javax.xml.bind.JAXBException;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import org.apache.log4j.helpers.Loader;
import javax.servlet.ServletContextEvent;
import org.apache.log4j.xml.DOMConfigurator;
import javax.servlet.ServletContextListener;

import com.cws.esolutions.security.SecurityConstants;
import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.core.config.DataSourceManager;
import com.cws.esolutions.core.controllers.ResourceController;
import com.cws.esolutions.core.exception.CoreServiceException;
import com.cws.esolutions.core.controllers.ResourceControllerBean;
import com.cws.esolutions.security.exception.SecurityServiceException;
import com.cws.esolutions.security.config.SecurityServiceConfiguration;
/*
 * InitializeApplication
 * SecurityServiceInitializerServlet for application. Currently loads logging
 *
 * History
 *
 * Author               Date                           Comments
 * ----------------------------------------------------------------------------
 * Kevin Huntly         11/23/2008 22:39:20            Created.
 */
public class SecurityServiceListener implements ServletContextListener
{
    private static final SecurityServiceBean svcBean = SecurityServiceBean.getInstance();

    private static final String INIT_SYSCONFIG_FILE = "SecurityServiceConfig";
    private static final String INIT_SYSLOGGING_FILE = "SecurityServiceLogger";
    private static final String CNAME = SecurityServiceListener.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityConstants.ERROR_LOGGER + CNAME);

    @Override
    public void contextInitialized(final ServletContextEvent sContextEvent)
    {
        final String methodName = SecurityServiceListener.CNAME + "#contextInitialized(final ServletContextEvent sContextEvent)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ServletContextEvent: {}", sContextEvent);
        }

        URL xmlURL = null;
        JAXBContext context = null;
        Unmarshaller marshaller = null;
        SecurityServiceConfiguration configData = null;
        Map<String, DataSource> dsMap = new HashMap<String, DataSource>();

        final ServletContext sContext = sContextEvent.getServletContext();
        final ResourceControllerBean resBean = ResourceControllerBean.getInstance();
        final ClassLoader classLoader = SecurityServiceListener.class.getClassLoader();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletContext: {}", sContext);
            DEBUGGER.debug("ClassLoader: {}", classLoader);
        }

        try
        {
            if (sContext != null)
            {
                if (sContext.getInitParameter(SecurityServiceListener.INIT_SYSLOGGING_FILE) == null)
                {
                    System.err.println("Logging configuration not found. No logging enabled !");
                }
                else
                {
                    DOMConfigurator.configure(Loader.getResource(sContext.getInitParameter(SecurityServiceListener.INIT_SYSLOGGING_FILE)));
                }

                if (sContext.getInitParameter(SecurityServiceListener.INIT_SYSCONFIG_FILE) == null)
                {
                    ERROR_RECORDER.error("System configuration not found. Shutting down !");

                    throw new SecurityServiceException("System configuration file location not provided by application. Cannot continue.");
                }
                else
                {
                    xmlURL = classLoader.getResource(sContext.getInitParameter(SecurityServiceListener.INIT_SYSCONFIG_FILE));
                }

                if (DEBUG)
                {
                    DEBUGGER.debug("xmlURL: {}", xmlURL);
                }

                if (xmlURL != null)
                {
                    context = JAXBContext.newInstance(SecurityServiceConfiguration.class);
                    marshaller = context.createUnmarshaller();
                    configData = (SecurityServiceConfiguration) marshaller.unmarshal(xmlURL);

                    svcBean.setConfigData(configData);
                    svcBean.setResourceBean(resBean);

                    Context initContext = new InitialContext();
                    Context envContext = (Context) initContext.lookup(SecurityConstants.DS_CONTEXT);

                    ResourceController.configureAndCreateAuthConnection(configData.getAuthRepo(), true, resBean);

                    for (DataSourceManager mgr : configData.getResourceConfig().getDsManager())
                    {
                        dsMap.put(mgr.getDsName(), (DataSource) envContext.lookup(mgr.getDataSource()));
                    }

                    resBean.setDataSource(dsMap);
                }
                else
                {
                    throw new SecurityServiceException("Unable to load configuration. Cannot continue.");
                }
            }
            else
            {
                throw new SecurityServiceException("Unable to load configuration. Cannot continue.");
            }
        }
        catch (NamingException nx)
        {
            ERROR_RECORDER.error(nx.getMessage(), nx);
        }
        catch (SecurityServiceException ssx)
        {
            ERROR_RECORDER.error(ssx.getMessage(), ssx);
        }
        catch (JAXBException jx)
        {
            ERROR_RECORDER.error(jx.getMessage(), jx);
        }
        catch (CoreServiceException csx)
        {
            ERROR_RECORDER.error(csx.getMessage(), csx);
        }
    }

    @Override
    public void contextDestroyed(final ServletContextEvent sContextEvent)
    {
        final String methodName = SecurityServiceListener.CNAME + "#contextDestroyed(final ServletContextEvent sContextEvent)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ServletContextEvent: {}", sContextEvent);
        }

        final SecurityServiceConfiguration configData = svcBean.getConfigData();

        if (DEBUG)
        {
            DEBUGGER.debug("SecurityServiceConfiguration: {}", configData);
        }

        try
        {
            ResourceController.closeAuthConnection(configData.getAuthRepo(), true, svcBean.getResourceBean());
        }
        catch (CoreServiceException csx)
        {
            ERROR_RECORDER.error(csx.getMessage(), csx);
        }
    }
}
