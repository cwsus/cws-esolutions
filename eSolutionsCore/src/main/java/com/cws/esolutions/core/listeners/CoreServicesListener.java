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
package com.cws.esolutions.core.listeners;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.listeners
 * File: CoreServiceListener.java
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
import javax.naming.Context;
import javax.sql.DataSource;
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

import com.cws.esolutions.core.CoreServicesBean;
import com.cws.esolutions.core.config.xml.DataSourceManager;
import com.cws.esolutions.core.exception.CoreServicesException;
import com.cws.esolutions.core.config.xml.CoreConfigurationData;
/**
 * @author cws-khuntly
 * @version 1.0
 * @see javax.servlet.ServletContextListener
 */
public class CoreServicesListener implements ServletContextListener
{
    private static final CoreServicesBean appBean = CoreServicesBean.getInstance();

    private static final String INIT_SYSAPP_FILE = "eSolutionsCoreConfig";
    private static final String INIT_SYSLOGGING_FILE = "eSolutionsCoreLogger";

    public void contextInitialized(final ServletContextEvent contextEvent)
    {
        URL xmlURL = null;
        JAXBContext context = null;
        Unmarshaller marshaller = null;
        CoreConfigurationData configData = null;

        final ServletContext sContext = contextEvent.getServletContext();
        final ClassLoader classLoader = CoreServicesListener.class.getClassLoader();

        try
        {
            if (sContext != null)
            {
            	if (StringUtils.isEmpty(sContext.getInitParameter(CoreServicesListener.INIT_SYSLOGGING_FILE)))
                {
                    System.err.println("eSolutionsCore: Logging configuration not found. No logging enabled !");
                }
                else
                {
                    DOMConfigurator.configure(Loader.getResource(sContext.getInitParameter(CoreServicesListener.INIT_SYSLOGGING_FILE)));
                }

                if (sContext.getInitParameter(CoreServicesListener.INIT_SYSAPP_FILE) != null)
                {
                    xmlURL = classLoader.getResource(sContext.getInitParameter(CoreServicesListener.INIT_SYSAPP_FILE));
                }
                else
                {
                    throw new CoreServicesException("eSolutionsCore: System configuration file location not provided by application. Cannot continue.");
                }

                if (xmlURL != null)
                {
                	System.out.println("eSolutionsCore: xmlURL provided was valid and found, continuing configuration");

                    // set the app configuration
                    context = JAXBContext.newInstance(CoreConfigurationData.class);
                    marshaller = context.createUnmarshaller();
                    configData = (CoreConfigurationData) marshaller.unmarshal(xmlURL);
                    appBean.setConfigData(configData);

                    // set up the resource connections
                    Context initContext = new InitialContext();
                    Map<String, DataSource> dsMap = new HashMap<String, DataSource>();

                    for (DataSourceManager mgr : configData.getResourceConfig().getDsManager())
                    {
                        dsMap.put(mgr.getDsName(), (DataSource) initContext.lookup(mgr.getDataSource()));
                    }

                    appBean.setDataSources(dsMap);
                }
                else
                {
                    throw new CoreServicesException("eSolutionsCore: Unable to load configuration. Cannot continue.");
                }
            }
            else
            {
                throw new CoreServicesException("eSolutionsCore: Failed to load servlet context!");
            }
        }
        catch (final NamingException nx)
        {
        	System.out.println(nx.getMessage());
        }
        catch (final JAXBException jx)
        {
        	System.out.println(jx.getMessage());
        }
        catch (final CoreServicesException csx)
        {
        	System.out.println(csx.getMessage());
        }
    }

    public void contextDestroyed(final ServletContextEvent contextEvent)
    {
    }
}
