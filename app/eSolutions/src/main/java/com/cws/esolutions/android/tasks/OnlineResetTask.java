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
package com.cws.esolutions.android.tasks;
/*
 * eSolutions
 * com.cws.esolutions.core.tasks
 * OnlineResetTask.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.List;
import org.slf4j.Logger;
import java.util.Arrays;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.sql.Connection;
import java.util.Properties;
import java.net.InetAddress;
import android.os.AsyncTask;
import android.app.Activity;
import java.sql.SQLException;
import android.content.Intent;
import android.graphics.Color;
import org.slf4j.LoggerFactory;
import android.widget.TextView;
import com.unboundid.util.ssl.SSLUtil;
import javax.net.ssl.SSLSocketFactory;
import android.content.res.AssetManager;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPConnection;
import java.security.GeneralSecurityException;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang.RandomStringUtils;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.util.ssl.TrustStoreTrustManager;

import com.cws.esolutions.android.ui.R;
import com.cws.esolutions.android.Constants;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.android.utils.NetworkUtils;
import com.cws.esolutions.security.utils.PasswordUtils;
import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.android.ApplicationServiceBean;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.security.config.enums.AuthRepositoryType;
import com.cws.esolutions.security.processors.dto.AuthenticationData;
import com.cws.esolutions.security.processors.dto.AuthenticationRequest;
import com.cws.esolutions.security.processors.dto.AuthenticationResponse;
import com.cws.esolutions.security.processors.impl.AuthenticationProcessorImpl;
import com.cws.esolutions.security.processors.exception.AuthenticationException;
import com.cws.esolutions.security.processors.interfaces.IAuthenticationProcessor;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 * @see android.os.AsyncTask
 */
public class OnlineResetTask extends AsyncTask<String, Integer, AuthenticationResponse>
{
    private Activity reqActivity = null;
    private AuthRepositoryType repoType = null;

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
    private static final String CNAME = UserAuthenticationTask.class.getName();
    private static final SecurityServiceBean bean = SecurityServiceBean.getInstance();
	private static final ApplicationServiceBean appBean = ApplicationServiceBean.getInstance();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + OnlineResetTask.class.getSimpleName());

    public OnlineResetTask(final Activity request)
    {
        final String methodName = OnlineResetTask.CNAME + "#UserAuthenticationTask(final Activity request)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Activity: {}", request);
        }

        this.reqActivity = request;
    }

    @Override
    protected void onPreExecute()
    {
        final String methodName = OnlineResetTask.CNAME + "#onPreExecute()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        InputStream iStream = null;

        if (!(NetworkUtils.checkNetwork(this.reqActivity)))
        {
            ERROR_RECORDER.error("Network connections are available but not currently connected.");

            super.cancel(true);
        }

        AssetManager assetMgr = this.reqActivity.getResources().getAssets();

        if (DEBUG)
        {
            DEBUGGER.debug("AssetManager: {}", assetMgr);
        }

        try
        {
            iStream = assetMgr.open(this.reqActivity.getResources().getString(R.string.authRepoConfig));

            if (DEBUG)
            {
                DEBUGGER.debug("InputStream: {}", iStream);
            }

            if ((iStream == null) || (iStream.available() == 0))
            {
                ERROR_RECORDER.error("Unable to load application properties. Cannot continue.");

                super.cancel(true);
            }

            Properties connProps = new Properties();
            connProps.load(iStream);

            if (DEBUG)
            {
                DEBUGGER.debug("Properties: {}", connProps);
            }

            this.repoType = AuthRepositoryType.valueOf(connProps.getProperty(OnlineResetTask.REPO_TYPE));

            if (DEBUG)
            {
                DEBUGGER.debug("AuthRepositoryType: {}", this.repoType);
            }

            switch (this.repoType)
            {
                case LDAP:
                    LDAPConnection ldapConn = null;
                    LDAPConnectionOptions connOpts = new LDAPConnectionOptions();

                    connOpts.setAutoReconnect(true);
                    connOpts.setAbandonOnTimeout(true);
                    connOpts.setBindWithDNRequiresPassword(true);
                    connOpts.setConnectTimeoutMillis(Integer.parseInt(connProps.getProperty(OnlineResetTask.CONN_TIMEOUT)));
                    connOpts.setResponseTimeoutMillis(Integer.parseInt(connProps.getProperty(OnlineResetTask.READ_TIMEOUT)));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("LDAPConnectionOptions: {}", connOpts);
                    }

                    if (Boolean.valueOf(connProps.getProperty(OnlineResetTask.IS_SECURE)))
                    {
                        SSLUtil sslUtil = new SSLUtil(new TrustStoreTrustManager(
														  connProps.getProperty(OnlineResetTask.TRUST_FILE),
														  connProps.getProperty(OnlineResetTask.TRUST_PASS).toCharArray(),
														  connProps.getProperty(OnlineResetTask.TRUST_TYPE),
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

                        ldapConn = new LDAPConnection(sslSocketFactory, connOpts, connProps.getProperty(OnlineResetTask.REPOSITORY_HOST),
													  Integer.parseInt(connProps.getProperty(OnlineResetTask.REPOSITORY_PORT)),
													  connProps.getProperty(OnlineResetTask.REPOSITORY_USER),
													  PasswordUtils.decryptText(connProps.getProperty(OnlineResetTask.REPOSITORY_PASS),
																				connProps.getProperty(OnlineResetTask.REPOSITORY_SALT).length()));
                    }
                    else
                    {
                        ldapConn = new LDAPConnection(connOpts, connProps.getProperty(OnlineResetTask.REPOSITORY_HOST),
													  Integer.parseInt(connProps.getProperty(OnlineResetTask.REPOSITORY_PORT)),
													  connProps.getProperty(OnlineResetTask.REPOSITORY_USER),
													  PasswordUtils.decryptText(connProps.getProperty(OnlineResetTask.REPOSITORY_PASS),
																				connProps.getProperty(OnlineResetTask.REPOSITORY_SALT).length()));
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("LDAPConnection: {}", ldapConn);
                    }

                    if (!(ldapConn.isConnected()))
                    {
                        ERROR_RECORDER.error("Failed to establish an LDAP connection");

						super.cancel(true);
                    }

                    LDAPConnectionPool connPool = new LDAPConnectionPool(ldapConn,
																		 Integer.parseInt(connProps.getProperty(OnlineResetTask.MIN_CONNECTIONS)),
																		 Integer.parseInt(connProps.getProperty(OnlineResetTask.MAX_CONNECTIONS)));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("LDAPConnectionPool: {}", connPool);
                    }

                    if (connPool.isClosed())
                    {
                        ERROR_RECORDER.error("Failed to establish an LDAP connection");

						super.cancel(true);
                    }

                    bean.setAuthDataSource(connPool);

                    break;
                case SQL:
                    BasicDataSource dataSource = new BasicDataSource();
                    dataSource.setInitialSize(Integer.parseInt(connProps.getProperty(OnlineResetTask.MIN_CONNECTIONS)));
                    dataSource.setMaxActive(Integer.parseInt(connProps.getProperty(OnlineResetTask.MAX_CONNECTIONS)));
                    dataSource.setDriverClassName(connProps.getProperty(OnlineResetTask.CONN_DRIVER));
                    dataSource.setUrl(connProps.getProperty(OnlineResetTask.REPOSITORY_HOST));
                    dataSource.setUsername(connProps.getProperty(OnlineResetTask.REPOSITORY_USER));
                    dataSource.setPassword(PasswordUtils.decryptText(
											   connProps.getProperty(OnlineResetTask.REPOSITORY_PASS),
											   connProps.getProperty(OnlineResetTask.REPOSITORY_SALT).length()));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("BasicDataSource: {}", dataSource);
                    }

                    Connection conn = null;

                    try
                    {
                        conn = dataSource.getConnection();

                        bean.setAuthDataSource(conn);
                    }
                    catch (SQLException sqx)
                    {
                        ERROR_RECORDER.error(sqx.getMessage(), sqx);

                        super.cancel(true);
                    }
                    finally
                    {
                        try
                        {
                            if ((conn != null) && (!(conn.isClosed())))
                            {
                                conn.close();
                            }
                        }
                        catch (SQLException sqx)
                        {
                            ERROR_RECORDER.error(sqx.getMessage(), sqx);
                        }
                    }

                    break;
                default:
                    ERROR_RECORDER.error("No acceptable authentication datasource has been configured.");

                    super.cancel(true);
            }
        }
        catch (IOException iox)
        {
            ERROR_RECORDER.error(iox.getMessage(), iox);

            super.cancel(true);
        }
        catch (GeneralSecurityException gsx)
        {
            ERROR_RECORDER.error(gsx.getMessage(), gsx);

            super.cancel(true);
        }
        catch (NumberFormatException nfx)
        {
            ERROR_RECORDER.error(nfx.getMessage(), nfx);

            super.cancel(true);
        }
        catch (LDAPException lx)
        {
            ERROR_RECORDER.error(lx.getMessage(), lx);

            super.cancel(true);
        }
        catch (SecurityException sx)
        {
            ERROR_RECORDER.error(sx.getMessage(), sx);

            super.cancel(true);
        }
        finally
        {
            try
            {
                iStream.close();
            }
            catch (IOException iox)
            {
                ERROR_RECORDER.error(iox.getMessage(), iox);
            }
        }
    }

    @Override
    protected AuthenticationResponse doInBackground(final String... request)
    {
        final String methodName = OnlineResetTask.CNAME + "#doInBackground(final List<String>... request)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Request: {}", request);
        }

        AuthenticationResponse response = null;
        int lockCount = ((this.reqActivity.getIntent().getExtras() != null) ?
            this.reqActivity.getIntent().getExtras().getInt("lockCount") : 0);

        UserAccount userAccount = new UserAccount();
        userAccount.setUsername(request[0]);

		if (DEBUG)
		{
			DEBUGGER.debug("UserAccount: {}", userAccount);
		}

        AuthenticationData userSecurity = new AuthenticationData();
        userSecurity.setPassword(request[1]);

		AuthenticationRequest authReq = new AuthenticationRequest();
		authReq.setApplicationName(Constants.APPLICATION_NAME);
		authReq.setUserAccount(userAccount);
        authReq.setUserSecurity(userSecurity);
		authReq.setApplicationId(Constants.APPLICATION_ID);
		authReq.setHostInfo(appBean.getReqInfo());
        authReq.setCount(lockCount);

		if (DEBUG)
		{
			DEBUGGER.debug("AuthenticationRequest: {}", authReq);
		}

        final IAuthenticationProcessor processor = new AuthenticationProcessorImpl();

        try
        {
            response = processor.processAgentLogon(authReq);

            if (DEBUG)
            {
                DEBUGGER.debug("AuthenticationResponse: {}", response);
            }
        }
        catch (AuthenticationException ax)
        {
            ERROR_RECORDER.error(ax.getMessage(), ax);
        }
        finally
        {
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
                        try
                        {
                            dataSource.close();
						}
                        catch (SQLException sqx)
                        {
                            ERROR_RECORDER.error(sqx.getMessage(), sqx);
						}
                    }

                    break;
                default:
                    break;
            }
		}

        return response;
    }
}
