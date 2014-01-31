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
import java.util.List;
import org.slf4j.Logger;
import java.util.ArrayList;
import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;
import android.os.AsyncTask;
import android.app.Activity;
import org.slf4j.LoggerFactory;
import android.content.res.AssetManager;
import org.apache.commons.lang.StringUtils;
import android.content.res.Resources.NotFoundException;

import com.cws.esolutions.android.ui.R;
import com.cws.esolutions.android.Constants;
import com.cws.esolutions.android.utils.NetworkUtils;
import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.config.xml.AuthData;
import com.cws.esolutions.security.config.xml.KeyConfig;
import com.cws.esolutions.security.utils.DAOInitializer;
import com.cws.esolutions.android.ApplicationServiceBean;
import com.cws.esolutions.security.config.xml.ResourceConfig;
import com.cws.esolutions.security.config.xml.SecurityConfig;
import com.cws.esolutions.security.config.xml.DataSourceManager;
import com.cws.esolutions.security.exception.SecurityServiceException;
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
    private InputStream securityStream = null;

    private static final String CNAME = SecurityServiceLoader.class.getName();
    private static final SecurityServiceBean secBean = SecurityServiceBean.getInstance();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + SecurityServiceLoader.class.getSimpleName());

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

        try
        {
            if (!(NetworkUtils.checkNetwork(this.reqActivity)))
            {
                super.cancel(true);
            }

            AssetManager assetMgr = this.reqActivity.getResources().getAssets();

            if (DEBUG)
            {
                DEBUGGER.debug("AssetManager: {}", assetMgr);
            }
            
            this.securityStream = assetMgr.open(this.reqActivity.getResources().getString(R.string.securityConfigFile));

            if (DEBUG)
            {
                DEBUGGER.debug("InputStream: {}", this.securityStream);
            }

            if ((this.securityStream == null) || (this.securityStream.available() == 0))
            {
                ERROR_RECORDER.error("Unable to load security properties. Cannot continue.");

                super.cancel(true);
            }
        }
        catch (IOException iox)
        {
            ERROR_RECORDER.error(iox.getMessage(), iox);

            super.cancel(true);
        }
    }


    @Override
    protected Boolean doInBackground(final Void... value)
    {
        final String methodName = SecurityServiceLoader.CNAME + "#doInBackground(final Void... vlaue)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Context: {}", value);
        }

        boolean isLoaded = false;

        final ApplicationServiceBean bean = ApplicationServiceBean.getInstance();
        final AssetManager assetMgr = this.reqActivity.getResources().getAssets();

        if (DEBUG)
        {
            DEBUGGER.debug("ApplicationServiceBean: {}", bean);
            DEBUGGER.debug("AssetManager: {}", assetMgr);
        }

        try
        {
            Properties securityProperties = new Properties();
            securityProperties.load(this.securityStream);

            if (DEBUG)
            {
                DEBUGGER.debug("Properties: {}", securityProperties);
            }

            AuthData authData = new AuthData();
            authData.setUserId(securityProperties.getProperty("userId"));
            authData.setPublicKey(securityProperties.getProperty("publicKey"));
            authData.setUserPassword(securityProperties.getProperty("userPassword"));
            authData.setSecret(securityProperties.getProperty("secret"));
            authData.setLockCount(securityProperties.getProperty("lockCount"));
            authData.setLastLogin(securityProperties.getProperty("lastLogin"));
            authData.setSurname(securityProperties.getProperty("surname"));
            authData.setGivenName(securityProperties.getProperty("givenName"));
            authData.setExpiryDate(securityProperties.getProperty("expiryDate"));
            authData.setSecQuestionOne(securityProperties.getProperty("secQuestionOne"));
            authData.setSecQuestionTwo(securityProperties.getProperty("secQuestionTwo"));
            authData.setSecAnswerOne(securityProperties.getProperty("secAnswerOne"));
            authData.setSecAnswerTwo(securityProperties.getProperty("secAnswerTwo"));
            authData.setEmailAddr(securityProperties.getProperty("emailAddr"));
            authData.setIsSuspended(securityProperties.getProperty("isSuspended"));
            authData.setCommonName(securityProperties.getProperty("commonName"));
            authData.setOlrSetupReq(securityProperties.getProperty("olrSetupReq"));
            authData.setOlrLocked(securityProperties.getProperty("olrLocked"));
            authData.setDisplayName(securityProperties.getProperty("displayName"));
            authData.setMemberOf(securityProperties.getProperty("memberOf"));
            authData.setPagerNumber(securityProperties.getProperty("pagerNumber"));
            authData.setTelephoneNumber(securityProperties.getProperty("telephoneNumber"));

            if (DEBUG)
            {
                DEBUGGER.debug("AuthData: {}", authData);
            }

            KeyConfig keyConfig = new KeyConfig();
            keyConfig.setKeyManager(securityProperties.getProperty("keyManager"));
            keyConfig.setKeyDirectory(securityProperties.getProperty("keyDirectory"));
            keyConfig.setKeyAlgorithm(securityProperties.getProperty("keyAlgorithm"));
            keyConfig.setKeySize(Integer.parseInt(securityProperties.getProperty("keySize")));

            if (DEBUG)
            {
                DEBUGGER.debug("KeyConfig: {}", keyConfig);
            }

            SecurityConfig secConfig = new SecurityConfig();
            secConfig.setMaxAttempts(Integer.parseInt(securityProperties.getProperty("maxAttempts")));
            secConfig.setPasswordExpiration(Integer.parseInt(securityProperties.getProperty("passwordExpiration")));
            secConfig.setPasswordMinLength(Integer.parseInt(securityProperties.getProperty("passwordMinLength")));
            secConfig.setPasswordMaxLength(Integer.parseInt(securityProperties.getProperty("passwordMaxLength")));
            secConfig.setIterations(Integer.parseInt(securityProperties.getProperty("iterations")));
            secConfig.setAuthAlgorithm(securityProperties.getProperty("authAlgorithm"));
            secConfig.setOtpAlgorithm(securityProperties.getProperty("otpAlgorithm"));
            secConfig.setSaltLength(Integer.parseInt(securityProperties.getProperty("saltLength")));
            secConfig.setAuthManager(securityProperties.getProperty("authManager"));
            secConfig.setUserManager(securityProperties.getProperty("userManager"));
            secConfig.setPerformAudit(Boolean.valueOf(securityProperties.getProperty("performAudit")));
            secConfig.setResetIdLength(Integer.parseInt(securityProperties.getProperty("resetIdLength")));
            secConfig.setIsSmsResetEnabled(Boolean.valueOf(securityProperties.getProperty("smsResetEnabled")));
            secConfig.setSmsCodeLength(Integer.parseInt(securityProperties.getProperty("smsCodeLength")));
            secConfig.setResetTimeout(Integer.parseInt(securityProperties.getProperty("resetTimeout")));
            secConfig.setOtpVariance(Integer.parseInt(securityProperties.getProperty("otpVariance")));

            SecurityConfigurationData secSvcConfig = new SecurityConfigurationData();
            secSvcConfig.setAuthData(authData);
            secSvcConfig.setKeyConfig(keyConfig);
            secSvcConfig.setSecurityConfig(secConfig);

            String[] secDataSources = StringUtils.split(securityProperties.getProperty("datasources"), ",");

            if (DEBUG)
            {
                DEBUGGER.debug("String[]: {}", secDataSources);
            }

            if ((secDataSources != null) && (secDataSources.length != 0))
            {
                List<DataSourceManager> dsList = new ArrayList<DataSourceManager>();

                for (String source : secDataSources)
                {
                    if (DEBUG)
                    {
                        DEBUGGER.debug("String: {}", source);
                    }

                    InputStream dsStream = assetMgr.open(source + ".properties");

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
                    
                    DataSourceManager dsManager = new DataSourceManager();
                    dsManager.setDsName(dsProps.getProperty("dsName"));
                    dsManager.setDataSource(dsProps.getProperty("datasource"));
                    dsManager.setDriver(dsProps.getProperty("driver"));
                    dsManager.setDsUser(dsProps.getProperty("dsUser"));
                    dsManager.setDsPass(dsProps.getProperty("dsPass"));
                    dsManager.setSalt(dsProps.getProperty("salt"));
                    dsManager.setConnectTimeout(Integer.parseInt(dsProps.getProperty("connTimeout")));
                    dsManager.setAutoReconnect(Boolean.valueOf(dsProps.getProperty("autoReconnect")));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("DataSourceManager: {}", dsManager);
                    }

                    dsList.add(dsManager);

                    dsStream.close();
                }

                if (DEBUG)
                {
                    DEBUGGER.debug("List<DataSourceManager: {}", dsList);
                }

                ResourceConfig securityResourceConfig = new ResourceConfig();
                securityResourceConfig.setDsManager(dsList);

                if (DEBUG)
                {
                    DEBUGGER.debug("ResourceConfig: {}", securityResourceConfig);
                }

                secSvcConfig.setResourceConfig(securityResourceConfig);
            }

            if (DEBUG)
            {
                DEBUGGER.debug("SecurityConfigurationData: {}", secSvcConfig);
            }

            DAOInitializer.configureAndCreateAuthConnection(assetMgr.open(this.reqActivity.getResources().getString(R.string.coreConfigFile)),
                    false, SecurityServiceLoader.secBean);

            secBean.setAuthDataSource(value);
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
        catch (SecurityServiceException ssx)
        {
            ERROR_RECORDER.error(ssx.getMessage(), ssx);
        }
        finally
        {
            try
            {
                this.securityStream.close();
            }
            catch (IOException iox)
            {
                ERROR_RECORDER.error(iox.getMessage(), iox);
            }
        }

        return isLoaded;
    }
}

