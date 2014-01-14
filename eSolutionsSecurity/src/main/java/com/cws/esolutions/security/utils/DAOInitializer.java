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
package com.cws.esolutions.security.utils;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.utils
 * File: DAOInitializer.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.io.File;
import org.slf4j.Logger;
import java.io.IOException;
import java.util.Properties;
import javax.naming.Context;
import java.sql.SQLException;
import java.io.FileInputStream;
import org.slf4j.LoggerFactory;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import com.unboundid.util.ssl.SSLUtil;
import javax.net.ssl.SSLSocketFactory;
import org.apache.commons.io.FileUtils;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPConnection;
import java.security.GeneralSecurityException;
import org.apache.commons.dbcp.BasicDataSource;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.util.ssl.TrustStoreTrustManager;

import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.config.xml.AuthRepo;
import com.cws.esolutions.security.utils.PasswordUtils;
import com.cws.esolutions.security.SecurityServiceConstants;
import com.cws.esolutions.security.exception.SecurityServiceException;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 */
public final class DAOInitializer
{
    private static final String DS_CONTEXT = "java:comp/env/";
    private static final String CNAME = DAOInitializer.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityServiceConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityServiceConstants.ERROR_LOGGER + DAOInitializer.CNAME);

    /**
     * @param authRepo - The <code>AuthRepo</code> object containing connection information
     * @param isContainer - A <code>boolean</code> flag indicating if this is in a container
     * @param bean - The <code>SecurityServiceBean</code> that holds the connection
     * @throws SecurityServiceException if an exception occurs opening the connection
     */
    public synchronized static void configureAndCreateAuthConnection(final AuthRepo authRepo, final boolean isContainer, final SecurityServiceBean bean) throws SecurityServiceException
    {
        String methodName = DAOInitializer.CNAME + "#configureAndCreateAuthConnection(final AuthRepo authRepo, final boolean isContainer, final SecurityServiceBean bean) throws SecurityServiceException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AuthRepo: {}", authRepo);
            DEBUGGER.debug("isContainer: {}", isContainer);
            DEBUGGER.debug("SecurityServiceBean: {}", bean);
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
                                    bean.setAuthDataSource(connPool);
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

                    throw new SecurityServiceException(lx.getMessage(), lx);
                }
                catch (IOException iox)
                {
                    ERROR_RECORDER.error(iox.getMessage(), iox);

                    throw new SecurityServiceException(iox.getMessage(), iox);
                }
                catch (GeneralSecurityException gsx)
                {
                    ERROR_RECORDER.error(gsx.getMessage(), gsx);

                    throw new SecurityServiceException(gsx.getMessage(), gsx);
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
                        Context envContext = (Context) initContext.lookup(DAOInitializer.DS_CONTEXT);

                        bean.setAuthDataSource(envContext.lookup(authRepo.getRepositoryHost()));
                    }
                    catch (NamingException nx)
                    {
                        ERROR_RECORDER.error(nx.getMessage(), nx);

                        throw new SecurityServiceException(nx.getMessage(), nx);
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

                    bean.setAuthDataSource(dataSource);
                }

                break;
            default:
                throw new SecurityServiceException("Unhandled ResourceType");
        }
    }

    /**
     * @param authRepo - The <code>AuthRepo</code> object containing connection information
     * @param isContainer - A <code>boolean</code> flag indicating if this is in a container
     * @param bean - The <code>SecurityServiceBean</code> that holds the connection
     * @throws SecurityServiceException if an exception occurs closing the connection
     */
    public synchronized static void closeAuthConnection(final AuthRepo authRepo, final boolean isContainer, final SecurityServiceBean bean) throws SecurityServiceException
    {
        String methodName = DAOInitializer.CNAME + "#closeAuthConnection(final AuthRepo authRepo, final boolean isContainer, final SecurityServiceBean bean) throws SecurityServiceException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AuthRepo: {}", authRepo);
            DEBUGGER.debug("isContainer: {}", isContainer);
            DEBUGGER.debug("SecurityServiceBean: {}", bean);
        }

        try
        {
            switch (authRepo.getRepoType())
            {
                case LDAP:
                    LDAPConnectionPool ldapPool = (LDAPConnectionPool) bean.getAuthDataSource();

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
                        BasicDataSource dataSource = (BasicDataSource) bean.getAuthDataSource();

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
                    throw new SecurityServiceException("Unhandled ResourceType");
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);
        }
    }
}
