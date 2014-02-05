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
package com.cws.esolutions.android.utils;
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
import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;
import java.sql.SQLException;
import org.slf4j.LoggerFactory;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import com.unboundid.util.ssl.SSLUtil;
import javax.net.ssl.SSLSocketFactory;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPConnection;
import java.security.GeneralSecurityException;
import org.apache.commons.dbcp.BasicDataSource;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.util.ssl.TrustStoreTrustManager;

import com.cws.esolutions.android.Constants;
import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.utils.PasswordUtils;
import com.cws.esolutions.security.config.enums.AuthRepositoryType;
import com.cws.esolutions.security.exception.SecurityServiceException;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 */
public final class DatasourceUtils
{
    private static final String REPO_TYPE = "repoType";
    private static final String IS_SECURE = "isSecure";
    private static final String TRUST_FILE= "trustStoreFile";
    private static final String TRUST_PASS = "trustStorePass";
    private static final String TRUST_TYPE = "trustStoreType";
    private static final String CONN_DRIVER = "repositoryDriver";
    private static final String REPOSITORY_HOST = "repositoryHost";
    private static final String REPOSITORY_PORT = "repositoryPort";
    private static final String MIN_CONNECTIONS = "minConnections";
    private static final String MAX_CONNECTIONS = "maxConnections";
    private static final String REPOSITORY_USER = "repositoryUser";
    private static final String REPOSITORY_PASS = "repositoryPass";
    private static final String REPOSITORY_SALT = "repositorySalt";
    private static final String CONN_TIMEOUT = "repositoryConnTimeout";
    private static final String READ_TIMEOUT = "repositoryReadTimeout";
    private static final String CNAME = DatasourceUtils.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + DatasourceUtils.CNAME);

    /**
     * @param authRepo - The <code>AuthRepo</code> object containing connection information
     * @param isContainer - A <code>boolean</code> flag indicating if this is in a container
     * @param bean - The <code>SecurityServiceBean</code> that holds the connection
     * @throws SecurityServiceException if an exception occurs opening the connection
     */
    public synchronized static void configureAndCreateAuthConnection(final InputStream properties, final SecurityServiceBean bean) throws SecurityServiceException
    {
        String methodName = DatasourceUtils.CNAME + "#configureAndCreateAuthConnection(final InputStream properties, final SecurityServiceBean bean) throws SecurityServiceException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("File: {}", properties);
            DEBUGGER.debug("SecurityServiceBean: {}", bean);
        }

        try
        {
            Properties connProps = new Properties();
            connProps.load(properties);

            if (DEBUG)
            {
                DEBUGGER.debug("Properties: {}", connProps);
            }

            AuthRepositoryType repoType = AuthRepositoryType.valueOf(connProps.getProperty(DatasourceUtils.REPO_TYPE));

            if (DEBUG)
            {
                DEBUGGER.debug("AuthRepositoryType: {}", repoType);
            }

            switch (repoType)
            {
                case LDAP:
                    LDAPConnection ldapConn = null;
                    LDAPConnectionOptions connOpts = new LDAPConnectionOptions();

                    connOpts.setAutoReconnect(true);
                    connOpts.setAbandonOnTimeout(true);
                    connOpts.setBindWithDNRequiresPassword(true);
                    connOpts.setConnectTimeoutMillis(Integer.parseInt(connProps.getProperty(DatasourceUtils.CONN_TIMEOUT)));
                    connOpts.setResponseTimeoutMillis(Integer.parseInt(connProps.getProperty(DatasourceUtils.READ_TIMEOUT)));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("LDAPConnectionOptions: {}", connOpts);
                    }

                    if (Boolean.valueOf(connProps.getProperty(DatasourceUtils.IS_SECURE)))
                    {
                        SSLUtil sslUtil = new SSLUtil(new TrustStoreTrustManager(
														  connProps.getProperty(DatasourceUtils.TRUST_FILE),
														  connProps.getProperty(DatasourceUtils.TRUST_PASS).toCharArray(),
														  connProps.getProperty(DatasourceUtils.TRUST_TYPE),
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

                        ldapConn = new LDAPConnection(sslSocketFactory, connOpts, connProps.getProperty(DatasourceUtils.REPOSITORY_HOST),
													  Integer.parseInt(connProps.getProperty(DatasourceUtils.REPOSITORY_PORT)),
													  connProps.getProperty(DatasourceUtils.REPOSITORY_USER),
													  PasswordUtils.decryptText(connProps.getProperty(DatasourceUtils.REPOSITORY_PASS),
																				connProps.getProperty(DatasourceUtils.REPOSITORY_SALT).length()));
                    }
                    else
                    {
                        ldapConn = new LDAPConnection(connOpts, connProps.getProperty(DatasourceUtils.REPOSITORY_HOST),
													  Integer.parseInt(connProps.getProperty(DatasourceUtils.REPOSITORY_PORT)),
													  connProps.getProperty(DatasourceUtils.REPOSITORY_USER),
													  PasswordUtils.decryptText(connProps.getProperty(DatasourceUtils.REPOSITORY_PASS),
																				connProps.getProperty(DatasourceUtils.REPOSITORY_SALT).length()));
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
																		 Integer.parseInt(connProps.getProperty(DatasourceUtils.MIN_CONNECTIONS)),
																		 Integer.parseInt(connProps.getProperty(DatasourceUtils.MAX_CONNECTIONS)));

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
                    BasicDataSource dataSource = new BasicDataSource();
                    dataSource.setInitialSize(Integer.parseInt(connProps.getProperty(DatasourceUtils.MIN_CONNECTIONS)));
                    dataSource.setMaxActive(Integer.parseInt(connProps.getProperty(DatasourceUtils.MAX_CONNECTIONS)));
                    dataSource.setDriverClassName(connProps.getProperty(DatasourceUtils.CONN_DRIVER));
                    dataSource.setUrl(connProps.getProperty(DatasourceUtils.REPOSITORY_HOST));
                    dataSource.setUsername(connProps.getProperty(DatasourceUtils.REPOSITORY_USER));
                    dataSource.setPassword(PasswordUtils.decryptText(
											   connProps.getProperty(DatasourceUtils.REPOSITORY_PASS),
											   connProps.getProperty(DatasourceUtils.REPOSITORY_SALT).length()));

                    bean.setAuthDataSource(dataSource);

                    break;
                default:
			        return;
            }
        }
        catch (LDAPException lx)
        {
            ERROR_RECORDER.error(lx.getMessage(), lx);

            throw new SecurityServiceException(lx.getMessage(), lx);
        }
        catch (GeneralSecurityException gsx)
        {
            ERROR_RECORDER.error(gsx.getMessage(), gsx);

            throw new SecurityServiceException(gsx.getMessage(), gsx);
        }
        catch (FileNotFoundException fnfx)
        {
            ERROR_RECORDER.error(fnfx.getMessage(), fnfx);

            throw new SecurityServiceException(fnfx.getMessage(), fnfx);
        }
        catch (IOException iox)
        {
            ERROR_RECORDER.error(iox.getMessage(), iox);

            throw new SecurityServiceException(iox.getMessage(), iox);
        }
    }

    /**
     * @param authRepo - The <code>AuthRepo</code> object containing connection information
     * @param isContainer - A <code>boolean</code> flag indicating if this is in a container
     * @param bean - The <code>SecurityServiceBean</code> that holds the connection
     * @throws SecurityServiceException if an exception occurs closing the connection
     */
    public synchronized static void closeAuthConnection(final File properties, final SecurityServiceBean bean) throws SecurityServiceException
    {
        String methodName = DatasourceUtils.CNAME + "#closeAuthConnection(final File properties, final boolean isContainer, final SecurityServiceBean bean) throws SecurityServiceException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("File: {}", properties);
            DEBUGGER.debug("SecurityServiceBean: {}", bean);
        }

        try
        {
            Properties connProps = new Properties();
            connProps.load(new FileInputStream(properties));

            if (DEBUG)
            {
                DEBUGGER.debug("Properties: {}", connProps);
            }

            AuthRepositoryType repoType = AuthRepositoryType.valueOf(connProps.getProperty(DatasourceUtils.REPO_TYPE));

            if (DEBUG)
            {
                DEBUGGER.debug("AuthRepositoryType: {}", repoType);
            }

            switch (repoType)
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
                    BasicDataSource dataSource = (BasicDataSource) bean.getAuthDataSource();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("BasicDataSource: {}", dataSource);
                    }

                    if ((dataSource != null ) && (!(dataSource.isClosed())))
                    {
                        dataSource.close();
                    }

                    break;
                default:
                    return;
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);
        }
        catch (FileNotFoundException fnfx)
        {
            ERROR_RECORDER.error(fnfx.getMessage(), fnfx);
        }
        catch (IOException iox)
        {
            ERROR_RECORDER.error(iox.getMessage(), iox);
        }
    }
}
