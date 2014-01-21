/*
 * Copyright (c) 2009 - 2014 CaspersBox Web Services
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

        FileInputStream fileStream = null;

        final File configFile = FileUtils.getFile(authRepo.getConfigFile());

        if (DEBUG)
        {
            DEBUGGER.debug("File: {}", configFile);
        }

        if (!(configFile.canRead()))
        {
            throw new SecurityServiceException("Unable to read configuration file. Cannot continue.");
        }

        try
        {
            fileStream = new FileInputStream(configFile);

            if (DEBUG)
            {
                DEBUGGER.debug("FileInputStream: {}", fileStream);
            }

            if (fileStream.available() == 0)
            {
                throw new SecurityServiceException("Unable to read configuration file. Cannot continue.");
            }

            Properties connProps = new Properties();
            connProps.load(fileStream);

            if (DEBUG)
            {
                DEBUGGER.debug("Properties: {}", connProps);
            }

            switch (authRepo.getRepoType())
            {
                case LDAP:
                    LDAPConnection ldapConn = null;
                    LDAPConnectionOptions connOpts = new LDAPConnectionOptions();

                    connOpts.setAutoReconnect(true);
                    connOpts.setAbandonOnTimeout(true);
                    connOpts.setBindWithDNRequiresPassword(true);
                    connOpts.setConnectTimeoutMillis(Integer.parseInt(connProps.getProperty(authRepo.getRepositoryConnTimeout())));
                    connOpts.setResponseTimeoutMillis(Integer.parseInt(connProps.getProperty(authRepo.getRepositoryReadTimeout())));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("LDAPConnectionOptions: {}", connOpts);
                    }

                    if (Boolean.valueOf(connProps.getProperty(authRepo.getIsSecure())))
                    {
                        SSLUtil sslUtil = new SSLUtil(new TrustStoreTrustManager(
                                connProps.getProperty(authRepo.getTrustStoreFile()),
                                connProps.getProperty(authRepo.getTrustStorePass()).toCharArray(),
                                connProps.getProperty(authRepo.getTrustStoreType()),
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

                        ldapConn = new LDAPConnection(sslSocketFactory, connOpts, connProps.getProperty(authRepo.getRepositoryHost()),
                                Integer.parseInt(connProps.getProperty(authRepo.getRepositoryPort())),
                                connProps.getProperty(authRepo.getRepositoryUser()),
                                PasswordUtils.decryptText(connProps.getProperty(authRepo.getRepositoryPass()),
                                        connProps.getProperty(authRepo.getRepositorySalt()).length()));
                    }
                    else
                    {
                        ldapConn = new LDAPConnection(connOpts, connProps.getProperty(authRepo.getRepositoryHost()),
                                Integer.parseInt(connProps.getProperty(authRepo.getRepositoryPort())),
                                connProps.getProperty(authRepo.getRepositoryUser()),
                                PasswordUtils.decryptText(connProps.getProperty(authRepo.getRepositoryPass()),
                                        connProps.getProperty(authRepo.getRepositorySalt()).length()));
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("LDAPConnection: {}", ldapConn);
                    }

                    if (!(ldapConn.isConnected()))
                    {
                        throw new LDAPException(ResultCode.CONNECT_ERROR, "Failed to establish an LDAP connection");
                    }

                    LDAPConnectionPool connPool = new LDAPConnectionPool(ldapConn,
                            Integer.parseInt(connProps.getProperty(authRepo.getMinConnections())),
                            Integer.parseInt(connProps.getProperty(authRepo.getMaxConnections())));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("LDAPConnectionPool: {}", connPool);
                    }

                    if (connPool.isClosed())
                    {
                        throw new LDAPException(ResultCode.CONNECT_ERROR, "Failed to establish an LDAP connection");
                    }

                    bean.setAuthDataSource(connPool);

                    break;
                case SQL:
                    // the isContainer only matters here
                    if (isContainer)
                    {
                        Context initContext = new InitialContext();
                        Context envContext = (Context) initContext.lookup(DAOInitializer.DS_CONTEXT);

                        bean.setAuthDataSource(envContext.lookup(authRepo.getRepositoryHost()));
                    }
                    else
                    {
                        BasicDataSource dataSource = new BasicDataSource();
                        dataSource.setInitialSize(Integer.parseInt(connProps.getProperty(authRepo.getMinConnections())));
                        dataSource.setMaxActive(Integer.parseInt(connProps.getProperty(authRepo.getMaxConnections())));
                        dataSource.setDriverClassName(connProps.getProperty(authRepo.getRepositoryDriver()));
                        dataSource.setUrl(connProps.getProperty(authRepo.getRepositoryHost()));
                        dataSource.setUsername(connProps.getProperty(authRepo.getRepositoryUser()));
                        dataSource.setPassword(PasswordUtils.decryptText(
                                connProps.getProperty(authRepo.getRepositoryPass()),
                                connProps.getProperty(authRepo.getRepositorySalt()).length()));

                        bean.setAuthDataSource(dataSource);
                    }

                    break;
                default:
                    throw new SecurityServiceException("Unhandled ResourceType");
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
        catch (NamingException nx)
        {
            ERROR_RECORDER.error(nx.getMessage(), nx);

            throw new SecurityServiceException(nx.getMessage(), nx);
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
