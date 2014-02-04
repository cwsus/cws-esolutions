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
 * CoreServiceLoader.java
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
import java.net.InetAddress;
import java.util.Properties;
import android.os.AsyncTask;
import android.app.Activity;
import org.slf4j.LoggerFactory;
import android.content.res.AssetManager;
import org.apache.commons.lang.StringUtils;
import android.content.res.Resources.NotFoundException;

import com.cws.esolutions.android.ui.R;
import com.cws.esolutions.android.Constants;
import com.cws.esolutions.core.CoreServiceBean;
import com.cws.esolutions.core.config.xml.DNSConfig;
import com.cws.esolutions.android.utils.NetworkUtils;
import com.cws.esolutions.core.config.xml.AgentConfig;
import com.cws.esolutions.android.ApplicationServiceBean;
import com.cws.esolutions.core.config.xml.ResourceConfig;
import com.cws.esolutions.core.config.xml.ApplicationConfig;
import com.cws.esolutions.core.config.xml.DataSourceManager;
import com.cws.esolutions.core.config.xml.CoreConfigurationData;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 * @see android.os.AsyncTask
 */
public class CoreServiceLoader extends AsyncTask<Void, Void, Boolean>
{
    private Activity reqActivity = null;

    private static final String CNAME = CoreServiceLoader.class.getName();
    private static final CoreServiceBean coreBean = CoreServiceBean.getInstance();
    private static final ApplicationServiceBean appBean = ApplicationServiceBean.getInstance();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CoreServiceLoader.class.getSimpleName());

    public CoreServiceLoader(final Activity activity)
    {
        final String methodName = CoreServiceLoader.CNAME + "#LoaderTask(final Activity activity)#Constructor()";

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
        final String methodName = CoreServiceLoader.CNAME + "#onPreExecute()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        InputStream iStream = null;

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

            iStream = assetMgr.open(this.reqActivity.getResources().getString(R.string.coreConfigFile));

            if (DEBUG)
            {
                DEBUGGER.debug("InputStream: {}", iStream);
            }

            if ((iStream == null) || (iStream.available() == 0))
            {
                ERROR_RECORDER.error("Unable to load core properties. Cannot continue.");

                super.cancel(true);
            }

            Properties properties = new Properties();
            properties.load(iStream);

            if (DEBUG)
            {
                DEBUGGER.debug("Properties: {}", properties);
            }

            CoreServiceLoader.appBean.setCoreProperties(properties);
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
                if (iStream != null)
                {
                    iStream.close();
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
        final String methodName = CoreServiceLoader.CNAME + "#doInBackground(final Void... vlaue)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Context: {}", value);
        }

        boolean isLoaded = false;

        final AssetManager assetMgr = this.reqActivity.getResources().getAssets();
        final Properties properties = CoreServiceLoader.appBean.getCoreProperties();

        if (DEBUG)
        {
            DEBUGGER.debug("AssetManager: {}", assetMgr);
            DEBUGGER.debug("Properties: {}", properties);
        }

        try
        {
            DNSConfig dnsConfig = new DNSConfig();
            dnsConfig.setAdminName(properties.getProperty("adminName"));
            dnsConfig.setTTLInterval(Integer.parseInt(properties.getProperty("ttlInterval")));
            dnsConfig.setRetryInterval(Integer.parseInt(properties.getProperty("retryInterval")));
            dnsConfig.setDomainName(properties.getProperty("domainName"));
            dnsConfig.setCacheInterval(Integer.parseInt(properties.getProperty("cacheInterval")));
            dnsConfig.setRefreshInterval(Integer.parseInt(properties.getProperty("refreshInterval")));
            dnsConfig.setExpirationInterval(Integer.parseInt(properties.getProperty("expirationInterval")));
            dnsConfig.setSearchServiceHost(properties.getProperty("searchServiceHost"));
            dnsConfig.setZoneFilePath(properties.getProperty(" "));
            dnsConfig.setZoneRootDir(properties.getProperty("zoneRootDir"));
            dnsConfig.setNamedRootDir(properties.getProperty("namedRootDir"));

            if (DEBUG)
            {
                DEBUGGER.debug("DNSConfig: {}", dnsConfig);
            }

            AgentConfig agentConfig = new AgentConfig();
            agentConfig.setConnectionName(properties.getProperty("connectionName"));
            agentConfig.setClientId(properties.getProperty("clientId"));
            agentConfig.setRequestQueue(properties.getProperty("requestQueue"));
            agentConfig.setResponseQueue(properties.getProperty("responseQueue"));
            agentConfig.setUsername(properties.getProperty("username"));
            agentConfig.setPassword(properties.getProperty("password"));
            agentConfig.setSalt(properties.getProperty("salt"));
            agentConfig.setTimeout(Long.valueOf(properties.getProperty("timeout")));

            if (DEBUG)
            {
                DEBUGGER.debug("AgentConfig: {}", agentConfig);
            }

            ApplicationConfig appConfig = new ApplicationConfig();
            appConfig.setEmailAliasId(properties.getProperty(""));
            appConfig.setAppName(properties.getProperty("appName"));
            appConfig.setConnectTimeout(Integer.parseInt(properties.getProperty("connectTimeout")));
            appConfig.setMessageIdLength(Integer.parseInt(properties.getProperty("messageIdLength")));
            appConfig.setDateFormat(properties.getProperty("dateFormat"));
            appConfig.setNlsFileName(properties.getProperty(""));
            appConfig.setProxyConfigFile(properties.getProperty(""));
            appConfig.setVirtualManagerClass(properties.getProperty("virtualManagerClass"));
            appConfig.setAgentBundleSource(properties.getProperty("agentBundleSource"));

            if (DEBUG)
            {
                DEBUGGER.debug("ApplicationConfig: {}", appConfig);
            }

            CoreConfigurationData coreConfig = new CoreConfigurationData();
            coreConfig.setAgentConfig(agentConfig);
            coreConfig.setAppConfig(appConfig);
            coreConfig.setDNSConfig(dnsConfig);

            String[] coreDataSources = StringUtils.split(properties.getProperty("datasources"), ",");

            if (DEBUG)
            {
                DEBUGGER.debug("String[]: {}", coreDataSources);
            }

            if ((coreDataSources != null) && (coreDataSources.length != 0))
            {
                List<DataSourceManager> dsList = new ArrayList<DataSourceManager>();

                for (String source : coreDataSources)
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

                ResourceConfig coreResourceConfig = new ResourceConfig();
                coreResourceConfig.setDsManager(dsList);

                if (DEBUG)
                {
                    DEBUGGER.debug("ResourceConfig: {}", coreResourceConfig);
                }

                coreConfig.setResourceConfig(coreResourceConfig);
            }
            
            if (DEBUG)
            {
                DEBUGGER.debug("CoreConfigurationData: {}", coreConfig);
            }

            // coreBean.setDataSources(value);
            coreBean.setConfigData(coreConfig);
            coreBean.setOsType(System.getProperty("os.name"));
            coreBean.setHostName(InetAddress.getLocalHost().getHostAddress());

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

