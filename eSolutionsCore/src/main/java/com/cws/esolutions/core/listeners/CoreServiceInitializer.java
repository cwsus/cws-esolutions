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
 * File: CoreServiceInitializer.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.net.URL;
import java.util.Map;
import org.slf4j.Logger;
import java.util.HashMap;
import javax.sql.DataSource;
import java.sql.SQLException;
import org.slf4j.LoggerFactory;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBException;
import java.net.MalformedURLException;
import org.apache.log4j.helpers.Loader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.commons.dbcp.BasicDataSource;

import com.cws.esolutions.core.CoreServicesBean;
import com.cws.esolutions.core.CoreServiceConstants;
import com.cws.esolutions.security.utils.PasswordUtils;
import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.core.config.xml.DataSourceManager;
import com.cws.esolutions.security.config.xml.SecurityConfig;
import com.cws.esolutions.core.exception.CoreServiceException;
import com.cws.esolutions.core.config.xml.CoreConfigurationData;
import com.cws.esolutions.security.config.xml.SecurityConfigurationData;
/**
 * @author cws-khuntly
 * @version 1.0
 */
public class CoreServiceInitializer
{
    private static final String CNAME = CoreServiceInitializer.class.getName();
    private static final CoreServicesBean appBean = CoreServicesBean.getInstance();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(CoreServiceConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(CoreServiceConstants.ERROR_LOGGER + CNAME);

    /**
     * Initializes the core service in a standalone mode - used for applications outside of a container or when
     * run as a standalone jar.
     *
     * @param configFile - The service configuration file to utilize
     * @param logConfig - The logging configuration file to utilize
     * @param loadSecurity - Flag to start security
     * @param startConnections - Flag to start connections
     * @throws CoreServiceException @{link com.cws.esolutions.core.exception.CoreServiceException}
     * if an exception occurs during initialization
     */
    public static void initializeService(final String configFile, final String logConfig, final boolean loadSecurity, final boolean startConnections) throws CoreServiceException
    {
        URL xmlURL = null;
        JAXBContext context = null;
        Unmarshaller marshaller = null;
        SecurityConfig secConfig = null;
        CoreConfigurationData configData = null;
        SecurityConfigurationData secConfigData = null;

        if (loadSecurity)
        {
            secConfigData = SecurityServiceBean.getInstance().getConfigData();
            secConfig = secConfigData.getSecurityConfig();
        }

        final String serviceConfig = (StringUtils.isBlank(configFile)) ? System.getProperty("coreConfigFile") : configFile;
        final String loggingConfig = (StringUtils.isBlank(logConfig)) ? System.getProperty("coreLogConfig") : logConfig;

        try
        {
            try
            {
                DOMConfigurator.configure(Loader.getResource(loggingConfig));
            }
            catch (NullPointerException npx)
            {
                try
                {
                    DOMConfigurator.configure(FileUtils.getFile(loggingConfig).toURI().toURL());
                }
                catch (NullPointerException npx1)
                {
                    System.err.println("Unable to load logging configuration. No logging enabled!");
                    System.err.println("");
                    npx1.printStackTrace();
                }
            }

            xmlURL = CoreServiceInitializer.class.getClassLoader().getResource(serviceConfig);

            if (xmlURL == null)
            {
                // try loading from the filesystem
                xmlURL = FileUtils.getFile(configFile).toURI().toURL();
            }

            context = JAXBContext.newInstance(CoreConfigurationData.class);
            marshaller = context.createUnmarshaller();
            configData = (CoreConfigurationData) marshaller.unmarshal(xmlURL);

            CoreServiceInitializer.appBean.setConfigData(configData);

            if (startConnections)
            {
                Map<String, DataSource> dsMap = CoreServiceInitializer.appBean.getDataSources();

                if (DEBUG)
                {
                    DEBUGGER.debug("dsMap: {}", dsMap);
                }

                if (dsMap == null)
                {
                    dsMap = new HashMap<String, DataSource>();
                }

                for (DataSourceManager mgr : configData.getResourceConfig().getDsManager())
                {
                    if (!(dsMap.containsKey(mgr.getDsName())))
                    {
                        StringBuilder sBuilder = new StringBuilder()
                            .append("connectTimeout=" + mgr.getConnectTimeout() + ";")
                            .append("socketTimeout=" + mgr.getConnectTimeout() + ";")
                            .append("autoReconnect=" + mgr.getAutoReconnect() + ";")
                            .append("zeroDateTimeBehavior=convertToNull");

                        if (DEBUG)
                        {
                            DEBUGGER.debug("StringBuilder: {}", sBuilder);
                        }

                        BasicDataSource dataSource = new BasicDataSource();
                            dataSource.setDriverClassName(mgr.getDriver());
                            dataSource.setUrl(mgr.getDataSource());
                            dataSource.setUsername(mgr.getDsUser());
                            dataSource.setConnectionProperties(sBuilder.toString());
                            dataSource.setPassword(PasswordUtils.decryptText(mgr.getDsPass(), mgr.getSalt(),
                                    secConfig.getSecretAlgorithm(), secConfig.getIterations(), secConfig.getKeyBits(),
                                    secConfig.getEncryptionAlgorithm(), secConfig.getEncryptionInstance(),
                                    configData.getAppConfig().getEncoding()));
                        if (DEBUG)
                        {
                            DEBUGGER.debug("BasicDataSource: {}", dataSource);
                        }

                        dsMap.put(mgr.getDsName(), dataSource);
                    }
                }

                if (DEBUG)
                {
                    DEBUGGER.debug("dsMap: {}", dsMap);
                }

                CoreServiceInitializer.appBean.setDataSources(dsMap);
            }
        }
        catch (JAXBException jx)
        {
            jx.printStackTrace();
            throw new CoreServiceException(jx.getMessage(), jx);
        }
        catch (MalformedURLException mux)
        {
            mux.printStackTrace();
            throw new CoreServiceException(mux.getMessage(), mux);
        }
    }

    /**
     * Shuts down the running core service process.
     */
    public static void shutdown()
    {
        final String methodName = CoreServiceInitializer.CNAME + "#shutdown()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        final Map<String, DataSource> datasources = CoreServiceInitializer.appBean.getDataSources();

        try
        {
            if ((datasources != null) && (datasources.size() != 0))
            {
                for (String key : datasources.keySet())
                {
                    if (DEBUG)
                    {
                        DEBUGGER.debug("Key: {}", key);
                    }

                    BasicDataSource dataSource = (BasicDataSource) datasources.get(key);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("BasicDataSource: {}", dataSource);
                    }

                    if ((dataSource != null ) && (!(dataSource.isClosed())))
                    {
                        dataSource.close();
                    }
                }
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);
        }
    }
}
