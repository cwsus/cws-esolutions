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
 * File: SecurityServiceInitializer.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly           11/23/2008 22:39:20             Created.
 */
import java.net.URL;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import javax.sql.DataSource;
import java.sql.SQLException;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.JAXBException;
import java.net.MalformedURLException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.dbcp2.BasicDataSource;

import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.utils.PasswordUtils;
import com.cws.esolutions.security.config.xml.DataSourceManager;
import com.cws.esolutions.security.exception.SecurityServiceException;
import com.cws.esolutions.security.config.xml.SecurityConfigurationData;
/**
 * @author cws-khuntly
 * @version 1.0
 */
public class SecurityServiceInitializer
{
    private static final SecurityServiceBean svcBean = SecurityServiceBean.getInstance();

    /**
     * Initializes the security service in a standalone mode - used for applications outside of a container or when
     * run as a standalone jar.
     *
     * @param configFile - The security configuration file to utilize
     * @param logConfig - The logging configuration file to utilize
     * @param startConnections - Configure, load and start repository connections
     * @throws SecurityServiceException @{link com.cws.esolutions.security.exception.SecurityServiceException}
     * if an exception occurs during initialization
     */
    public static void initializeService(final String configFile, final String logConfig, final boolean startConnections) throws SecurityServiceException
    {
        URL xmlURL = null;
        JAXBContext context = null;
        Unmarshaller marshaller = null;
        SecurityConfigurationData configData = null;

        final ClassLoader classLoader = SecurityServiceInitializer.class.getClassLoader();
        final String serviceConfig = (StringUtils.isBlank(configFile)) ? System.getProperty("configFile") : configFile;

        try
        {
            xmlURL = classLoader.getResource(serviceConfig);

            if (Objects.isNull(xmlURL))
            {
                // try loading from the filesystem
                xmlURL = FileUtils.getFile(serviceConfig).toURI().toURL();
            }

            context = JAXBContext.newInstance(SecurityConfigurationData.class);
            marshaller = context.createUnmarshaller();
            configData = (SecurityConfigurationData) marshaller.unmarshal(xmlURL);

            svcBean.setConfigData(configData);

            if (startConnections)
            {
                Map<String, DataSource> dsMap = svcBean.getDataSources();

                if (configData.getResourceConfig() != null)
                {
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
                                .append("autoReconnect=" + mgr.getAutoReconnect() + ";");

                            BasicDataSource dataSource = new BasicDataSource();
                            dataSource.setDriverClassName(mgr.getDriver());
                            dataSource.setUrl(mgr.getDataSource());
                            dataSource.setUsername(mgr.getDsUser());
                            dataSource.setConnectionProperties(sBuilder.toString());
                            dataSource.setPassword(PasswordUtils.decryptText(mgr.getDsPass(), mgr.getDsSalt(), svcBean.getConfigData().getSecurityConfig().getSecretKeyAlgorithm(), svcBean.getConfigData().getSecurityConfig().getIterations(),
                        			svcBean.getConfigData().getSecurityConfig().getKeyLength(), svcBean.getConfigData().getSecurityConfig().getEncryptionAlgorithm(), svcBean.getConfigData().getSecurityConfig().getEncryptionInstance(),
                        			svcBean.getConfigData().getSystemConfig().getEncoding()));

                            dsMap.put(mgr.getDsName(), dataSource);
                        }
                    }

                    svcBean.setDataSources(dsMap);
                }
            }
        }
        catch (final JAXBException jx)
        {
            jx.printStackTrace();
            throw new SecurityServiceException(jx.getMessage(), jx);
        }
        catch (final MalformedURLException mux)
        {
            mux.printStackTrace();
            throw new SecurityServiceException(mux.getMessage(), mux);
        }
        catch (final SecurityException sx)
        {
            sx.printStackTrace();
            throw new SecurityServiceException(sx.getMessage(), sx);
        }
    }

    /**
     * Shuts down the running security service process.
     */
    public static void shutdown()
    {
        Map<String, DataSource> dsMap = SecurityServiceInitializer.svcBean.getDataSources();

        try
        {
            if (dsMap != null)
            {
                for (String key : dsMap.keySet())
                {
                    BasicDataSource dataSource = (BasicDataSource) dsMap.get(key);

                    if ((dataSource != null) && (!(dataSource.isClosed())))
                    {
                        dataSource.close();
                    }
                }
            }
        }
        catch (final SQLException sqx)
        {
        	System.err.println("SecurityServiceInitializer#shutdown(): Exception occurred while shutting down: " + sqx.getMessage());
        }
    }
}
