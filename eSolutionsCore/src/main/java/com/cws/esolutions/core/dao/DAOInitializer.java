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
package com.cws.esolutions.core.dao;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.dao
 * File: DAOInitializer.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.Map;
import org.slf4j.Logger;
import java.util.HashMap;
import javax.naming.Context;
import javax.sql.DataSource;
import java.sql.SQLException;
import org.slf4j.LoggerFactory;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.dbcp.BasicDataSource;

import com.cws.esolutions.core.CoreServiceConstants;
import com.cws.esolutions.core.CoreServiceBean;
import com.cws.esolutions.security.utils.PasswordUtils;
import com.cws.esolutions.core.config.xml.DataSourceManager;
import com.cws.esolutions.core.exception.CoreServiceException;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 */
public class DAOInitializer
{
    private static final String DS_CONTEXT = "java:comp/env/";
    private static final String CNAME = DAOInitializer.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(CoreServiceConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(CoreServiceConstants.ERROR_LOGGER + DAOInitializer.CNAME);

    /**
     * Sets up an application datasource connection
     * via JNDI to the requested resource reference
     *
     * @param dsManager - The <code>DataSourceManager</code> containing the connection information
     * @param resBean - The resource bean to store datasources into
     * @throws CoreServiceException if an error is thrown during processing
     */
    public synchronized static void configureAndCreateDataConnection(final DataSourceManager dsManager, final CoreServiceBean bean) throws CoreServiceException
    {
        final String methodName = DAOInitializer.CNAME + "#configureAndCreateDataConnection(final DataSourceManager dsManager, final CoreServiceBean bean) throws CoreServiceException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("DataSourceManager: {}", dsManager);
            DEBUGGER.debug("ResourceControllerBean: {}", bean);
        }

        Map<String, DataSource> dsMap = bean.getDataSource();

        if (DEBUG)
        {
            DEBUGGER.debug("dsMap: {}", dsMap);
        }

        if (dsMap == null)
        {
            dsMap = new HashMap<>();
        }

        if (!(dsMap.containsKey(dsManager.getDsName())))
        {
            if (StringUtils.isNotEmpty(dsManager.getDriver()))
            {
                StringBuilder sBuilder = new StringBuilder()
                    .append("connectTimeout=" + dsManager.getConnectTimeout() + ";")
                    .append("socketTimeout=" + dsManager.getConnectTimeout() + ";")
                    .append("autoReconnect=" + dsManager.getAutoReconnect() + ";")
                    .append("zeroDateTimeBehavior=convertToNull");

                BasicDataSource dataSource = new BasicDataSource();
                dataSource.setDriverClassName(dsManager.getDriver());
                dataSource.setUrl(dsManager.getDataSource());
                dataSource.setUsername(dsManager.getDsUser());
                dataSource.setConnectionProperties(sBuilder.toString());

                // handle both encrypted and non-encrypted passwords
                // prefer encrypted
                if (StringUtils.isNotEmpty(dsManager.getSalt()))
                {
                    dataSource.setPassword(PasswordUtils.decryptText(
                            dsManager.getDsPass(),
                            dsManager.getSalt().length()));
                }
                else
                {
                    dataSource.setPassword(dsManager.getDsPass());
                }

                dsMap.put(dsManager.getDsName(), dataSource);
            }
            else
            {
                try
                {
                    Context initContext = new InitialContext();
                    Context envContext = (Context) initContext.lookup(DAOInitializer.DS_CONTEXT);

                    DataSource dataSource = (DataSource) envContext.lookup(dsManager.getDataSource());

                    dsMap.put(dsManager.getDsName(), dataSource);
                }
                catch (NamingException nx)
                {
                    ERROR_RECORDER.error(nx.getMessage(), nx);

                    throw new CoreServiceException(nx.getMessage(), nx);
                }
            }
        }

        bean.setDataSource(dsMap);
    }

    /**
     * @param dsManager - The <code>AuthRepo</code> object containing connection information
     * @param isContainer - A <code>boolean</code> flag indicating if this is in a container
     * @param bean - The <code>SecurityServiceBean</code> that holds the connection
     * @throws CoreServiceException if an exception occurs closing the connection
     */
    public synchronized static void closeDataConnection(final DataSourceManager dsManager, final boolean isContainer, final CoreServiceBean bean) throws CoreServiceException
    {
        String methodName = DAOInitializer.CNAME + "#closeDataConnection(final DataSourceManager dsManager, final boolean isContainer, final CoreServiceBean bean) throws CoreServiceException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("DataSourceManager: {}", dsManager);
            DEBUGGER.debug("CoreServiceBean: {}", bean);
        }

        try
        {
            if (!(isContainer))
            {
                Map<String, DataSource> datasources = bean.getDataSource();

                if (DEBUG)
                {
                    DEBUGGER.debug("Map<String, DataSource>: {}", datasources);
                }

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
