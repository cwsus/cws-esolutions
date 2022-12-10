/*
 * Copyright (c) 2009 - 2020 CaspersBox Web Services
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
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.listeners
 * File: SecurityServiceListener.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.net.URL;
import java.util.Map;
import java.util.HashMap;
import javax.sql.DataSource;
import javax.naming.Context;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.naming.InitialContext;
import javax.xml.bind.JAXBException;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import org.apache.log4j.helpers.Loader;
import javax.servlet.ServletContextEvent;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.xml.DOMConfigurator;
import javax.servlet.ServletContextListener;

import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.utils.DAOInitializer;
import com.cws.esolutions.security.config.xml.DataSourceManager;
import com.cws.esolutions.security.exception.SecurityServiceException;
import com.cws.esolutions.security.config.xml.SecurityConfigurationData;
/**
 * @see javax.servlet.ServletContextListener
 */
public class SecurityServiceListener implements ServletContextListener
{
    private static final String INIT_SYSCONFIG_FILE = "SecurityServiceConfig";
    private static final String INIT_SYSLOGGING_FILE = "SecurityServiceLogger";
    private static final SecurityServiceBean svcBean = SecurityServiceBean.getInstance();

    /**
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    public void contextInitialized(final ServletContextEvent sContextEvent)
    {
        URL xmlURL = null;
        JAXBContext context = null;
        Unmarshaller marshaller = null;
        SecurityConfigurationData configData = null;

        final ServletContext sContext = sContextEvent.getServletContext();
        final ClassLoader classLoader = SecurityServiceListener.class.getClassLoader();

        try
        {
            if (sContext != null)
            {
            	if (StringUtils.isEmpty(sContext.getInitParameter(SecurityServiceListener.INIT_SYSLOGGING_FILE)))
                {
                    System.err.println("Logging configuration not found. No logging enabled !");
                }
                else
                {
                    DOMConfigurator.configure(Loader.getResource(sContext.getInitParameter(SecurityServiceListener.INIT_SYSLOGGING_FILE)));
                }

                if (StringUtils.isBlank(SecurityServiceListener.INIT_SYSCONFIG_FILE))
                {
                    throw new SecurityServiceException("System configuration file location not provided by application. Cannot continue.");
                }
                else
                {
                	xmlURL = classLoader.getResource(sContext.getInitParameter(SecurityServiceListener.INIT_SYSCONFIG_FILE));
                }

                if (xmlURL != null)
                {
                	System.out.println("SecurityService: xmlURL provided was valid and found, continuing configuration");

                    context = JAXBContext.newInstance(SecurityConfigurationData.class);
                    marshaller = context.createUnmarshaller();
                    configData = (SecurityConfigurationData) marshaller.unmarshal(xmlURL);
                    svcBean.setConfigData(configData);

                    DAOInitializer.configureAndCreateAuthConnection(null, true, svcBean);
                    DAOInitializer.configureAndCreateAuditConnection(null, true, svcBean);

                    Context initContext = new InitialContext();
                    Map<String, DataSource> dsMap = new HashMap<String, DataSource>();

                    for (DataSourceManager mgr : configData.getResourceConfig().getDsManager())
                    {
                    	dsMap.put(mgr.getDsName(), (DataSource) initContext.lookup(mgr.getDataSource()));
                    }

                    svcBean.setDataSources(dsMap);
                }
                else
                {
                    throw new SecurityServiceException("SecurityService: Unable to load configuration. Cannot continue.");
                }
            }
            else
            {
                throw new SecurityServiceException("SecurityService: Servlet context was null. Cannot continue.");
            }
        }
        catch (final NamingException nx)
        {
        	System.err.println(nx.getMessage());
        }
        catch (final SecurityServiceException ssx)
        {
        	System.err.println(ssx.getMessage());
        }
        catch (final JAXBException jx)
        {
        	System.err.println(jx.getMessage());
        }
    }

    /**
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    public void contextDestroyed(final ServletContextEvent sContextEvent)
    {
        DAOInitializer.closeAuthConnection(null, true, svcBean);
        DAOInitializer.closeAuditConnection(null, true, svcBean);
    }
}
