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
package com.cws.esolutions.core.listeners;

import java.net.URL;
import java.util.Map;
import org.slf4j.Logger;
import java.util.HashMap;
import javax.naming.Context;
import javax.sql.DataSource;
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

import com.cws.esolutions.core.Constants;
import com.cws.esolutions.core.CoreServiceBean;
import com.cws.esolutions.core.config.ConfigurationData;
import com.cws.esolutions.core.config.DataSourceManager;
import com.cws.esolutions.core.exception.CoreServiceException;
import com.cws.esolutions.core.controllers.ResourceControllerBean;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.listeners
 * SecurityServiceListener.java
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
 * kh05451 @ Nov 16, 2012 4:57:44 PM
 *     Created.
 */
public class CoreServiceListener implements ServletContextListener
{
    private static final CoreServiceBean appBean = CoreServiceBean.getInstance();

    private static final String DS_CONTEXT = "java:comp/env/";
    private static final String INIT_SYSAPP_FILE = "eSolutionsCoreConfig";
    private static final String CNAME = CoreServiceListener.class.getName();
    private static final String INIT_SYSLOGGING_FILE = "eSolutionsCoreLogger";

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CNAME);

    @Override
    public void contextInitialized(final ServletContextEvent contextEvent)
    {
        URL xmlURL = null;
        JAXBContext context = null;
        Unmarshaller marshaller = null;
        ConfigurationData configData = null;
        Map<String, DataSource> dsMap = new HashMap<String, DataSource>();

        final ServletContext sContext = contextEvent.getServletContext();
        final ClassLoader classLoader = CoreServiceListener.class.getClassLoader();
        final ResourceControllerBean resBean = ResourceControllerBean.getInstance();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletContext: {}", sContext);
            DEBUGGER.debug("ClassLoader: {}", classLoader);
        }

        try
        {
            if (sContext != null)
            {
                if (sContext.getInitParameter(CoreServiceListener.INIT_SYSLOGGING_FILE) == null)
                {
                    System.err.println("Logging configuration not found. No logging enabled !");
                }
                else
                {
                    DOMConfigurator.configure(Loader.getResource(sContext.getInitParameter(CoreServiceListener.INIT_SYSLOGGING_FILE)));
                }

                if (sContext.getInitParameter(CoreServiceListener.INIT_SYSAPP_FILE) == null)
                {
                    ERROR_RECORDER.error("System configuration not found. Shutting down !");

                    throw new CoreServiceException("System configuration file location not provided by application. Cannot continue.");
                }
                else
                {
                    xmlURL = classLoader.getResource(sContext.getInitParameter(CoreServiceListener.INIT_SYSAPP_FILE));
                }

                if (DEBUG)
                {
                    DEBUGGER.debug("xmlURL: {}", xmlURL);
                }

                if (xmlURL != null)
                {
                    // set the app configuration
                    context = JAXBContext.newInstance(ConfigurationData.class);
                    marshaller = context.createUnmarshaller();
                    configData = (ConfigurationData) marshaller.unmarshal(xmlURL);

                    appBean.setConfigData(configData);
                    appBean.setResourceBean(resBean);

                    // set up the resource connections
                    Context initContext = new InitialContext();
                    Context envContext = (Context) initContext.lookup(CoreServiceListener.DS_CONTEXT);

                    for (DataSourceManager mgr : configData.getResourceConfig().getDsManager())
                    {
                        dsMap.put(mgr.getDsName(), (DataSource) envContext.lookup(mgr.getDataSource()));
                    }

                    resBean.setDataSource(dsMap);
                }
            }
            else
            {
                throw new CoreServiceException("Failed to load servlet context");
            }
        }
        catch (NamingException nx)
        {
            ERROR_RECORDER.error(nx.getMessage(), nx);
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
    public void contextDestroyed(final ServletContextEvent contextEvent)
    {
        final String methodName = CoreServiceListener.CNAME + "#contextDestroyed(final ServletContextEvent contextEvent)";
        
        if (DEBUG)
        {
        	DEBUGGER.debug(methodName);
            DEBUGGER.debug("ServletContextEvent: ", contextEvent);
        }
    }
}
