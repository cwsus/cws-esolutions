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
package com.cws.esolutions.core.listeners;

import java.net.URL;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBException;
import org.apache.log4j.helpers.Loader;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.xml.DOMConfigurator;

import com.cws.esolutions.core.CoreServiceBean;
import com.cws.esolutions.core.config.xml.DataSourceManager;
import com.cws.esolutions.core.controllers.ResourceController;
import com.cws.esolutions.core.exception.CoreServiceException;
import com.cws.esolutions.core.config.xml.CoreConfigurationData;
import com.cws.esolutions.core.controllers.ResourceControllerBean;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.listeners
 * File: CoreServiceInitializer.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 */
public class CoreServiceInitializer
{
    private static final CoreServiceBean appBean = CoreServiceBean.getInstance();

    public static void initializeService(final String secConfig, final String logConfig) throws CoreServiceException
    {
        URL xmlURL = null;
        JAXBContext context = null;
        Unmarshaller marshaller = null;
        CoreConfigurationData configData = null;

        final ResourceControllerBean resBean = ResourceControllerBean.getInstance();
        final ClassLoader classLoader = CoreServiceInitializer.class.getClassLoader();

        try
        {
            if (StringUtils.isEmpty(logConfig))
            {
                System.err.println("Logging configuration not found. No logging enabled !");
            }
            else
            {
                // Load logging
                DOMConfigurator.configure(Loader.getResource(logConfig));
            }

            xmlURL = classLoader.getResource(secConfig);

            if (xmlURL == null)
            {
                throw new CoreServiceException("Failed to load service configuration.");
            }

            context = JAXBContext.newInstance(CoreConfigurationData.class);
            marshaller = context.createUnmarshaller();
            configData = (CoreConfigurationData) marshaller.unmarshal(xmlURL);

            appBean.setConfigData(configData);
            appBean.setResourceBean(resBean);

            for (DataSourceManager mgr : configData.getResourceConfig().getDsManager())
            {
                ResourceController.configureAndCreateDataConnection(mgr, resBean);
            }
        }
        catch (JAXBException jx)
        {
            throw new CoreServiceException(jx.getMessage(), jx);
        }
        catch (CoreServiceException csx)
        {
            throw new CoreServiceException(csx.getMessage(), csx);
        }
    }

    public static void initializeService(final String secConfig, final String logConfig, final ClassLoader classLoader) throws CoreServiceException
    {
        URL xmlURL = null;
        JAXBContext context = null;
        Unmarshaller marshaller = null;
        CoreConfigurationData configData = null;

        final ResourceControllerBean resBean = ResourceControllerBean.getInstance();

        try
        {
            if (StringUtils.isEmpty(logConfig))
            {
                System.err.println("Logging configuration not found. No logging enabled !");
            }
            else
            {
                // Load logging
                DOMConfigurator.configure(Loader.getResource(logConfig));
            }

            xmlURL = classLoader.getResource(secConfig);

            if (xmlURL == null)
            {
                throw new CoreServiceException("Failed to load service configuration.");
            }

            context = JAXBContext.newInstance(CoreConfigurationData.class);
            marshaller = context.createUnmarshaller();
            configData = (CoreConfigurationData) marshaller.unmarshal(xmlURL);

            appBean.setConfigData(configData);
            appBean.setResourceBean(resBean);

            for (DataSourceManager mgr : configData.getResourceConfig().getDsManager())
            {
                ResourceController.configureAndCreateDataConnection(mgr, resBean);
            }
        }
        catch (JAXBException jx)
        {
            throw new CoreServiceException(jx.getMessage(), jx);
        }
        catch (CoreServiceException csx)
        {
            throw new CoreServiceException(csx.getMessage(), csx);
        }
    }
}
