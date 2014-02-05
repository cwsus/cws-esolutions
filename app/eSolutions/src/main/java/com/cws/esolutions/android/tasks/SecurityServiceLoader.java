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
 * SecurityServiceLoader.java
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
import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;
import android.os.AsyncTask;
import android.app.Activity;
import javax.sql.DataSource;
import org.slf4j.LoggerFactory;
import org.apache.log4j.helpers.Loader;
import android.content.res.AssetManager;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.commons.dbcp.BasicDataSource;
import javax.xml.parsers.FactoryConfigurationError;
import android.content.res.Resources.NotFoundException;

import com.cws.esolutions.android.ui.R;
import com.cws.esolutions.android.Constants;
import com.cws.esolutions.android.utils.NetworkUtils;
import com.cws.esolutions.security.utils.PasswordUtils;
import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.config.xml.AuthData;
import com.cws.esolutions.security.config.xml.KeyConfig;
import com.cws.esolutions.android.ApplicationServiceBean;
import com.cws.esolutions.security.config.xml.SecurityConfig;
import com.cws.esolutions.security.config.xml.SecurityConfigurationData;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 * @see android.os.AsyncTask
 */
public class SecurityServiceLoader extends AsyncTask<Void, Void, Boolean>
{
    private Activity reqActivity = null;

    private static final String CNAME = SecurityServiceLoader.class.getName();
    private static final SecurityServiceBean secBean = SecurityServiceBean.getInstance();
    private static final ApplicationServiceBean appBean = ApplicationServiceBean.getInstance();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + SecurityServiceLoader.CNAME);

    public SecurityServiceLoader(final Activity activity)
    {
        final String methodName = SecurityServiceLoader.CNAME + "#LoaderTask(final Activity activity)#Constructor()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Activity: {}", activity);
        }

        this.reqActivity = activity;
    }


    @Override
    protected void onPreExecute()
    {
        final String methodName = SecurityServiceLoader.CNAME + "#onPreExecute()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        InputStream authStream = null;
        InputStream propsStream = null;

        try
        {
            if (!(NetworkUtils.checkNetwork(this.reqActivity)))
            {
                System.out.println("cancel no nw");
                super.cancel(true);
            }

            AssetManager assetMgr = this.reqActivity.getResources().getAssets();

            if (DEBUG)
            {
                DEBUGGER.debug("AssetManager: {}", assetMgr);
            }

            propsStream = assetMgr.open(this.reqActivity.getResources().getString(R.string.securityConfigFile));

            if (DEBUG)
            {
                DEBUGGER.debug("InputStream: {}", propsStream);
            }

            if ((propsStream == null) || (propsStream.available() == 0))
            {
                ERROR_RECORDER.error("Unable to load core properties. Cannot continue.");

                super.cancel(true);
            }

            Properties secProps = new Properties();
            secProps.load(propsStream);

            if (DEBUG)
            {
                DEBUGGER.debug("Properties: {}", secProps);
            }

            SecurityServiceLoader.appBean.setSecProperties(secProps);

            authStream = assetMgr.open(this.reqActivity.getResources().getString(R.string.authRepoConfig));

            if (DEBUG)
            {
                DEBUGGER.debug("InputStream: {}", authStream);
            }

            if ((authStream == null) || (authStream.available() == 0))
            {
                ERROR_RECORDER.error("Unable to load core properties. Cannot continue.");

                super.cancel(true);
            }

            Properties authProps = new Properties();
            authProps.load(authStream);

            if (DEBUG)
            {
                DEBUGGER.debug("Properties: {}", authProps);
            }

            SecurityServiceLoader.appBean.setAuthProperties(authProps);
        }
        catch (IOException iox)
        {
            ERROR_RECORDER.error(iox.getMessage(), iox);

            super.cancel(true);
        }
        finally
        {
            try
            {
                if (propsStream != null)
                {
                    propsStream.close();
                }

                if (authStream != null)
                {
                    authStream.close();
                }
            }
            catch (IOException iox)
            {
                ERROR_RECORDER.error(iox.getMessage(), iox);
            }
        }
    }


    @Override
    protected Boolean doInBackground(final Void... value)
    {
        final String methodName = SecurityServiceLoader.CNAME + "#doInBackground(final Void... vlaue)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        boolean isLoaded = false;

        final AssetManager assetMgr = this.reqActivity.getResources().getAssets();
        final Properties properties = SecurityServiceLoader.appBean.getSecProperties();

        if (DEBUG)
        {
            DEBUGGER.debug("AssetManager: {}", assetMgr);
            DEBUGGER.debug("Properties", properties);
        }

        try
        {
            AuthData authData = new AuthData();
            authData.setUserId(properties.getProperty("userId"));
            authData.setPublicKey(properties.getProperty("publicKey"));
            authData.setUserPassword(properties.getProperty("userPassword"));
            authData.setSecret(properties.getProperty("secret"));
            authData.setLockCount(properties.getProperty("lockCount"));
            authData.setLastLogin(properties.getProperty("lastLogin"));
            authData.setSurname(properties.getProperty("surname"));
            authData.setGivenName(properties.getProperty("givenName"));
            authData.setExpiryDate(properties.getProperty("expiryDate"));
            authData.setSecQuestionOne(properties.getProperty("secQuestionOne"));
            authData.setSecQuestionTwo(properties.getProperty("secQuestionTwo"));
            authData.setSecAnswerOne(properties.getProperty("secAnswerOne"));
            authData.setSecAnswerTwo(properties.getProperty("secAnswerTwo"));
            authData.setEmailAddr(properties.getProperty("emailAddr"));
            authData.setIsSuspended(properties.getProperty("isSuspended"));
            authData.setCommonName(properties.getProperty("commonName"));
            authData.setOlrSetupReq(properties.getProperty("olrSetupReq"));
            authData.setOlrLocked(properties.getProperty("olrLocked"));
            authData.setDisplayName(properties.getProperty("displayName"));
            authData.setMemberOf(properties.getProperty("memberOf"));
            authData.setPagerNumber(properties.getProperty("pagerNumber"));
            authData.setTelephoneNumber(properties.getProperty("telephoneNumber"));
            authData.setRepositoryBaseDN(properties.getProperty("repositoryBaseDN"));
            authData.setBaseObject(properties.getProperty("baseObjectClass"));
            authData.setRepositoryUserBase(properties.getProperty("repositoryUserBase"));
            authData.setRepositoryRoleBase(properties.getProperty("repositoryRoleBase"));

            if (DEBUG)
            {
                DEBUGGER.debug("AuthData: {}", authData);
            }

            KeyConfig keyConfig = new KeyConfig();
            keyConfig.setKeyManager(properties.getProperty("keyManager"));
            keyConfig.setKeyDirectory(properties.getProperty("keyDirectory"));
            keyConfig.setKeyAlgorithm(properties.getProperty("keyAlgorithm"));
            keyConfig.setKeySize(Integer.parseInt(properties.getProperty("keySize")));

            if (DEBUG)
            {
                DEBUGGER.debug("KeyConfig: {}", keyConfig);
            }

            SecurityConfig secConfig = new SecurityConfig();
            secConfig.setMaxAttempts(Integer.parseInt(properties.getProperty("maxAttempts")));
            secConfig.setPasswordExpiration(Integer.parseInt(properties.getProperty("passwordExpiration")));
            secConfig.setPasswordMinLength(Integer.parseInt(properties.getProperty("passwordMinLength")));
            secConfig.setPasswordMaxLength(Integer.parseInt(properties.getProperty("passwordMaxLength")));
            secConfig.setIterations(Integer.parseInt(properties.getProperty("iterations")));
            secConfig.setAuthAlgorithm(properties.getProperty("authAlgorithm"));
            secConfig.setOtpAlgorithm(properties.getProperty("otpAlgorithm"));
            secConfig.setSaltLength(Integer.parseInt(properties.getProperty("saltLength")));
            secConfig.setAuthManager(properties.getProperty("authManager"));
            secConfig.setUserManager(properties.getProperty("userManager"));
            secConfig.setPerformAudit(Boolean.valueOf(properties.getProperty("performAudit")));
            secConfig.setResetIdLength(Integer.parseInt(properties.getProperty("resetIdLength")));
            secConfig.setIsSmsResetEnabled(Boolean.valueOf(properties.getProperty("smsResetEnabled")));
            secConfig.setSmsCodeLength(Integer.parseInt(properties.getProperty("smsCodeLength")));
            secConfig.setResetTimeout(Integer.parseInt(properties.getProperty("resetTimeout")));
            secConfig.setOtpVariance(Integer.parseInt(properties.getProperty("otpVariance")));
            secConfig.setAuthConfig(properties.getProperty("authConfig"));

            SecurityConfigurationData secSvcConfig = new SecurityConfigurationData();
            secSvcConfig.setAuthData(authData);
            secSvcConfig.setKeyConfig(keyConfig);
            secSvcConfig.setSecurityConfig(secConfig);

            String[] secDataSources = StringUtils.split(properties.getProperty("datasources"), ",");

            if (DEBUG)
            {
                DEBUGGER.debug("String[]: {}", (Object) secDataSources);
            }

            if ((secDataSources != null) && (secDataSources.length != 0))
            {
				Map<String, DataSource> dsMap = SecurityServiceLoader.secBean.getDataSources();

				if (DEBUG)
				{
					DEBUGGER.debug("dsMap: {}", dsMap);
				}

				if (dsMap == null)
				{
					dsMap = new HashMap<String, DataSource>();
				}

                for (String source : secDataSources)
                {
                    if (DEBUG)
                    {
                        DEBUGGER.debug("String: {}", source.trim());
                    }

                    InputStream dsStream = assetMgr.open(source.trim() + ".properties");

                    if (DEBUG)
                    {
                        DEBUGGER.debug("InputStream: {}", dsStream);
                    }

                    Properties dsProps = new Properties();
                    dsProps.load(dsStream);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Properties: {}", dsProps);
                    }

					BasicDataSource dataSource = new BasicDataSource();
					dataSource.setDriverClassName(dsProps.getProperty("driver"));
					dataSource.setUrl(dsProps.getProperty("datasource"));
					dataSource.setUsername(dsProps.getProperty("username"));
					dataSource.setConnectionProperties(dsProps.getProperty("connProps"));
					dataSource.setPassword(PasswordUtils.decryptText(dsProps.getProperty("password"),
						dsProps.getProperty("salt").length()));

					if (DEBUG)
					{
						DEBUGGER.debug("BasicDataSource: {}", dataSource);
					}

					dsMap.put(dsProps.getProperty("name"), dataSource);
                    dsStream.close();
                }

				if (DEBUG)
				{
					DEBUGGER.debug("Map<String, DataSource>: {}", dsMap);
				}

                secBean.setDataSources(dsMap);
            }

            if (DEBUG)
            {
                DEBUGGER.debug("SecurityConfigurationData: {}", secSvcConfig);
            }

            secBean.setConfigData(secSvcConfig);

            isLoaded = true;
        }
        catch (IOException iox)
        {
            ERROR_RECORDER.error(iox.getMessage(), iox);
        }
        catch (NotFoundException nfx)
        {
            ERROR_RECORDER.error(nfx.getMessage(), nfx);
        }

        return isLoaded;
    }
}

