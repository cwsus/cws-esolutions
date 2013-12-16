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
package com.cws.esolutions.core.controllers;

import java.io.File;
import java.util.Map;
import org.slf4j.Logger;
import java.util.HashMap;
import java.io.IOException;
import java.util.Properties;
import javax.naming.Context;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.io.FileInputStream;
import org.slf4j.LoggerFactory;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import com.unboundid.util.ssl.SSLUtil;
import javax.net.ssl.SSLSocketFactory;
import org.apache.commons.io.FileUtils;
import com.unboundid.ldap.sdk.ResultCode;
import org.apache.commons.lang.StringUtils;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPConnection;
import java.security.GeneralSecurityException;
import org.apache.commons.dbcp.BasicDataSource;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.util.ssl.TrustStoreTrustManager;

import com.cws.esolutions.core.Constants;
import com.cws.esolutions.security.config.xml.AuthRepo;
import com.cws.esolutions.security.utils.PasswordUtils;
import com.cws.esolutions.core.config.xml.DataSourceManager;
import com.cws.esolutions.core.exception.CoreServiceException;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.dao.interfaces
 * File: IPackageDataDAO.java
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Jan 4, 2013 3:36:54 PM
 *     Created.
 */
public class ResourceController
{
    private static final String DS_CONTEXT = "java:comp/env/";
    private static final String CNAME = ResourceController.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + ResourceController.CNAME);

    /**
     * @param authRepo - The <code>AuthRepo</code> object containing connection information
     * @param isContainer - A <code>boolean</code> flag indicating if this is in a container
     * @param resBean - The <code>ResourceControllerBean</code> that holds the connection
     * @throws CoreServiceException if an exception occurs opening the connection
     */
    public synchronized static void configureAndCreateAuthConnection(final AuthRepo authRepo, final boolean isContainer, final ResourceControllerBean resBean) throws CoreServiceException
    {
        String methodName = CNAME + "#configureAndCreateAuthConnection(final AuthRepo authRepo, final boolean isContainer, final ResourceControllerBean resBean) throws CoreServiceException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AuthRepo: {}", authRepo);
            DEBUGGER.debug("isContainer: {}", isContainer);
            DEBUGGER.debug("ResourceControllerBean: {}", resBean);
        }

        int minConnections = 1;
        int maxConnections = 10;
        FileInputStream fileStream = null;

        switch (authRepo.getRepoType())
        {
            case LDAP:
                LDAPConnection ldapConn = null;
                LDAPConnectionOptions connOpts = new LDAPConnectionOptions();

                try
                {
                    File ldapConfigFile = FileUtils.getFile(authRepo.getConfigFile());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("File: {}", ldapConfigFile);
                    }

                    if (ldapConfigFile.canRead())
                    {
                        fileStream = new FileInputStream(ldapConfigFile);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("FileInputStream: {}", fileStream);
                        }

                        if (fileStream.available() != 0)
                        {
                            Properties ldapProperties = new Properties();
                            ldapProperties.load(fileStream);

                            if (DEBUG)
                            {
                                DEBUGGER.debug("Properties: {}", ldapProperties);
                            }

                            connOpts.setAbandonOnTimeout(true);
                            connOpts.setAutoReconnect(true);
                            connOpts.setBindWithDNRequiresPassword(true);
                            connOpts.setConnectTimeoutMillis(Integer.parseInt(ldapProperties.getProperty(authRepo.getRepositoryConnTimeout())));
                            connOpts.setResponseTimeoutMillis(Integer.parseInt(ldapProperties.getProperty(authRepo.getRepositoryReadTimeout())));

                            if (DEBUG)
                            {
                                DEBUGGER.debug("LDAPConnectionOptions: {}", connOpts);
                            }

                            if (Boolean.valueOf(ldapProperties.getProperty(authRepo.getIsSecure())))
                            {
                                SSLUtil sslUtil = new SSLUtil(new TrustStoreTrustManager(
                                        ldapProperties.getProperty(authRepo.getTrustStoreFile()),
                                        ldapProperties.getProperty(authRepo.getTrustStorePass()).toCharArray(),
                                        ldapProperties.getProperty(authRepo.getTrustStoreType()),
                                        true));

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("SSLUtil: {}", sslUtil);
                                }

                                SSLSocketFactory sslSocketFactory = sslUtil.createSSLSocketFactory();

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("SSLSocketFactory: {}", sslSocketFactory);
                                }

                                ldapConn = new LDAPConnection(sslSocketFactory, connOpts, ldapProperties.getProperty(authRepo.getRepositoryHost()),
                                        Integer.parseInt(ldapProperties.getProperty(authRepo.getRepositoryPort())),
                                        ldapProperties.getProperty(authRepo.getRepositoryUser()),
                                        PasswordUtils.decryptText(ldapProperties.getProperty(authRepo.getRepositoryPass()),
                                                ldapProperties.getProperty(authRepo.getRepositorySalt()).length()));
                            }
                            else
                            {
                                ldapConn = new LDAPConnection(connOpts, ldapProperties.getProperty(authRepo.getRepositoryHost()),
                                        Integer.parseInt(ldapProperties.getProperty(authRepo.getRepositoryPort())),
                                        ldapProperties.getProperty(authRepo.getRepositoryUser()),
                                        PasswordUtils.decryptText(ldapProperties.getProperty(authRepo.getRepositoryPass()),
                                                ldapProperties.getProperty(authRepo.getRepositorySalt()).length()));
                            }

                            if (DEBUG)
                            {
                                DEBUGGER.debug("LDAPConnection: {}", ldapConn);
                            }

                            if (ldapConn.isConnected())
                            {
                                LDAPConnectionPool connPool = new LDAPConnectionPool(ldapConn, minConnections, maxConnections);

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("LDAPConnectionPool: {}", connPool);
                                }

                                if (!(connPool.isClosed()))
                                {
                                    resBean.setAuthDataSource(connPool);
                                }
                                else
                                {
                                    throw new LDAPException(ResultCode.CONNECT_ERROR, "Failed to create LDAP connection pool");
                                }
                            }
                            else
                            {
                                throw new LDAPException(ResultCode.CONNECT_ERROR, "Failed to establish an LDAP connection");
                            }
                        }
                        else
                        {
                            throw new IOException("Unable to load LDAP configuration file. Cannot continue.");
                        }
                    }
                    else
                    {
                        throw new IOException("Unable to load LDAP configuration file. Cannot continue.");
                    }
                }
                catch (LDAPException lx)
                {
                    ERROR_RECORDER.error(lx.getMessage(), lx);

                    throw new CoreServiceException(lx.getMessage(), lx);
                }
                catch (IOException iox)
                {
                    ERROR_RECORDER.error(iox.getMessage(), iox);

                    throw new CoreServiceException(iox.getMessage(), iox);
                }
                catch (GeneralSecurityException gsx)
                {
                    ERROR_RECORDER.error(gsx.getMessage(), gsx);

                    throw new CoreServiceException(gsx.getMessage(), gsx);
                }
                finally
                {
                    try
                    {
                        fileStream.close();
                    }
                    catch (IOException iox)
                    {
                        ERROR_RECORDER.error(iox.getMessage(), iox);
                    }
                }

                break;
            case SQL:
                // the isContainer only matters here
                if (isContainer)
                {
                    try
                    {
                        Context initContext = new InitialContext();
                        Context envContext = (Context) initContext.lookup(ResourceController.DS_CONTEXT);

                        resBean.setAuthDataSource(envContext.lookup(authRepo.getRepositoryHost()));
                    }
                    catch (NamingException nx)
                    {
                        ERROR_RECORDER.error(nx.getMessage(), nx);

                        throw new CoreServiceException(nx.getMessage(), nx);
                    }
                }
                else
                {
                    BasicDataSource dataSource = new BasicDataSource();
                    dataSource.setDriverClassName(authRepo.getRepositoryDriver());
                    dataSource.setUrl(authRepo.getRepositoryHost());
                    dataSource.setUsername(authRepo.getRepositoryUser());
                    dataSource.setPassword(PasswordUtils.decryptText(
                            authRepo.getRepositoryPass(),
                            authRepo.getRepositorySalt().length()));

                    resBean.setAuthDataSource(dataSource);
                }

                break;
            default:
                throw new CoreServiceException("Unhandled ResourceType");
        }
    }

    /**
     * @param authRepo - The <code>AuthRepo</code> object containing connection information
     * @param isContainer - A <code>boolean</code> flag indicating if this is in a container
     * @param resBean - The <code>ResourceControllerBean</code> that holds the connection
     * @throws CoreServiceException if an exception occurs closing the connection
     */
    public synchronized static void closeAuthConnection(final AuthRepo authRepo, final boolean isContainer, final ResourceControllerBean resBean) throws CoreServiceException
    {
        String methodName = CNAME + "#closeAuthConnection(final AuthRepo authRepo, final boolean isContainer, final ResourceControllerBean resBean) throws CoreServiceException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AuthRepo: {}", authRepo);
            DEBUGGER.debug("isContainer: {}", isContainer);
            DEBUGGER.debug("ResourceControllerBean: {}", resBean);
        }

        try
        {
            switch (authRepo.getRepoType())
            {
                case LDAP:
                    LDAPConnectionPool ldapPool = (LDAPConnectionPool) resBean.getAuthDataSource();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("LDAPConnectionPool: {}", ldapPool);
                    }

                    if ((ldapPool != null) && (!(ldapPool.isClosed())))
                    {
                        ldapPool.close();
                    }

                    break;
                case SQL:
                    // the isContainer only matters here
                    if (!(isContainer))
                    {
                        BasicDataSource dataSource = (BasicDataSource) resBean.getAuthDataSource();

                        if (DEBUG)
                        {
                            DEBUGGER.debug("BasicDataSource: {}", dataSource);
                        }

                        if ((dataSource != null ) && (!(dataSource.isClosed())))
                        {
                            dataSource.close();
                        }
                    }

                    break;
                default:
                    throw new CoreServiceException("Unhandled ResourceType");
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);
        }
    }

    /**
     * Sets up an application datasource connection
     * via JNDI to the requested resource reference
     *
     * @param dsManager - The <code>DataSourceManager</code> containing the connection information
     * @param resBean - The resource bean to store datasources into
     * @throws CoreServiceException if an error is thrown during processing
     */
    public synchronized static void configureAndCreateDataConnection(final DataSourceManager dsManager, final ResourceControllerBean resBean) throws CoreServiceException
    {
        final String methodName = ResourceController.CNAME + "#configureAndCreateDataConnection(final DataSourceManager dsManager, final ResourceControllerBean resBean) throws CoreServiceException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("DataSourceManager: {}", dsManager);
            DEBUGGER.debug("ResourceControllerBean: {}", resBean);
        }

        Map<String, DataSource> dsMap = resBean.getDataSource();

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
                    Context envContext = (Context) initContext.lookup(DS_CONTEXT);

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

        resBean.setDataSource(dsMap);
    }
}
