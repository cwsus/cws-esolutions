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
package com.cws.esolutions.security.listeners;

import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBException;
import org.apache.log4j.helpers.Loader;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.xml.DOMConfigurator;

import com.cws.esolutions.security.SecurityConstants;
import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.core.config.DataSourceManager;
import com.cws.esolutions.core.controllers.ResourceController;
import com.cws.esolutions.core.exception.CoreServiceException;
import com.cws.esolutions.core.controllers.ResourceControllerBean;
import com.cws.esolutions.security.exception.SecurityServiceException;
import com.cws.esolutions.security.config.SecurityServiceConfiguration;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.security.audit.processors.interfaces
 * File: IAuditProcessor.java
 *
 * History
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * Kevin Huntly         11/23/2008 22:39:20             Created.
 */
public class SecurityServiceInitializer
{
    private static final String CNAME = SecurityServiceInitializer.class.getName();
    private static final SecurityServiceBean svcBean = SecurityServiceBean.getInstance();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityConstants.ERROR_LOGGER + CNAME);

    public static void initializeService(final String secConfig, final String logConfig) throws SecurityServiceException
    {
        URL xmlURL = null;
        JAXBContext context = null;
        Unmarshaller marshaller = null;
        SecurityServiceConfiguration configData = null;

        final ResourceControllerBean resBean = ResourceControllerBean.getInstance();
        final ClassLoader classLoader = SecurityServiceInitializer.class.getClassLoader();

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
                throw new SecurityServiceException("Failed to load service configuration.");
            }

            context = JAXBContext.newInstance(SecurityServiceConfiguration.class);
            marshaller = context.createUnmarshaller();
            configData = (SecurityServiceConfiguration) marshaller.unmarshal(xmlURL);

            svcBean.setConfigData(configData);
            svcBean.setResourceBean(resBean);

            ResourceController.configureAndCreateAuthConnection(configData.getAuthRepo(), false, resBean);

            for (DataSourceManager mgr : configData.getResourceConfig().getDsManager())
            {
                ResourceController.configureAndCreateDataConnection(mgr, resBean);
            }
        }
        catch (JAXBException jx)
        {
            throw new SecurityServiceException(jx.getMessage(), jx);
        }
        catch (CoreServiceException csx)
        {
            throw new SecurityServiceException(csx.getMessage(), csx);
        }
    }

    public static void shutdown()
    {
        final String methodName = SecurityServiceInitializer.CNAME + "#shutdown()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        final SecurityServiceConfiguration configData = svcBean.getConfigData();

        if (DEBUG)
        {
            DEBUGGER.debug("SecurityServiceConfiguration: {}", configData);
        }

        try
        {
            ResourceController.closeAuthConnection(configData.getAuthRepo(), false, svcBean.getResourceBean());
        }
        catch (CoreServiceException csx)
        {
            ERROR_RECORDER.error(csx.getMessage(), csx);
        }
    }

    public static void initializeService(final String secConfig, final String logConfig, final ClassLoader classLoader) throws SecurityServiceException
    {
        URL xmlURL = null;
        JAXBContext context = null;
        Unmarshaller marshaller = null;
        SecurityServiceConfiguration configData = null;

        final ResourceControllerBean resBean = svcBean.getResourceBean();

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
                throw new SecurityServiceException("Failed to load service configuration.");
            }

            context = JAXBContext.newInstance(SecurityServiceConfiguration.class);
            marshaller = context.createUnmarshaller();
            configData = (SecurityServiceConfiguration) marshaller.unmarshal(xmlURL);

            svcBean.setConfigData(configData);
            svcBean.setResourceBean(resBean);

            ResourceController.configureAndCreateAuthConnection(configData.getAuthRepo(), false, resBean);

            for (DataSourceManager mgr : configData.getResourceConfig().getDsManager())
            {
                ResourceController.configureAndCreateDataConnection(mgr, resBean);
            }
        }
        catch (JAXBException jx)
        {
            throw new SecurityServiceException(jx.getMessage(), jx);
        }
        catch (CoreServiceException csx)
        {
            throw new SecurityServiceException(csx.getMessage(), csx);
        }
    }
}
