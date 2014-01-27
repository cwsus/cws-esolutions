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
 * LoaderTask.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;
import android.os.AsyncTask;
import android.app.Activity;
import org.slf4j.LoggerFactory;
import android.net.NetworkInfo;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.content.res.AssetManager;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.dbcp.BasicDataSource;

import com.cws.esolutions.android.Constants;
import com.cws.esolutions.security.utils.PasswordUtils;
import android.util.*;

public class LoaderTask extends AsyncTask<Void, Void, Boolean>
{
    private Activity reqActivity = null;

    private static final String CNAME = LoaderTask.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger(Constants.ERROR_LOGGER);

    public LoaderTask(final Activity activity)
    {
        final String methodName = LoaderTask.CNAME + "#DNSRequestTask(final Activity activity)";

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
        final String methodName = LoaderTask.CNAME + "#onPreExecute()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        boolean isConnected = false;

        final ConnectivityManager connMgr = (ConnectivityManager) this.reqActivity.getSystemService(Context.CONNECTIVITY_SERVICE);

		System.out.print(connMgr);
        if (DEBUG)
        {
            DEBUGGER.debug("ConnectivityManager: {}", connMgr);
        }

        NetworkInfo[] networkInfo = connMgr.getAllNetworkInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("NetworkInfo[]: {}", networkInfo);
        }

        if ((networkInfo.length == 0) || (networkInfo == null))
        {
            // no available network connection
            ERROR_LOGGER.error("No available network connection. Cannot continue.");

            super.cancel(true);
        }
        else
        {
            for (NetworkInfo network : networkInfo)
            {
                if (DEBUG)
                {
                    DEBUGGER.debug("NetworkInfo: {}", network);
                }

                if (network.isConnected())
                {
                    isConnected = true;

                    break;
                }
            }

            if (!(isConnected))
            {
                ERROR_LOGGER.error("Network connections are available but not currently connected.");

                super.cancel(true);
            }
        }
    }

    @Override
    protected Boolean doInBackground(final Void... value)
    {
        final String methodName = LoaderTask.CNAME + "#doInBackground(final Context... vlaue)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Context: {}", value);
        }

        boolean isLoaded = false;
        InputStream iStream = null;

        try
        {
            Resources resources = this.reqActivity.getResources();

            if (DEBUG)
            {
                DEBUGGER.debug("Resources: {}", resources);
            }

            AssetManager assetMgr = resources.getAssets();

            if (DEBUG)
            {
                DEBUGGER.debug("AssetManager: {}", assetMgr);
            }

            iStream = assetMgr.open("application.properties");

            if (DEBUG)
            {
                DEBUGGER.debug("InputStream: {}", iStream);
            }

            Properties props = new Properties();
            props.load(iStream);

            if (DEBUG)
            {
                DEBUGGER.debug("Properties: {}", props);
			}

            String[] dataSources = StringUtils.split(props.getProperty("datasources"), ",");

            if (DEBUG)
            {
                DEBUGGER.debug("String[]: {}", dataSources);
            }

            for (String source : dataSources)
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

				StringBuilder sBuilder = new StringBuilder()
					.append("connectTimeout=" + dsProps.getProperty("connTimeout") + ";")
					.append("socketTimeout=" + dsProps.getProperty("connTimeout") + ";")
					.append("autoReconnect=" + dsProps.getProperty("autoReconnect") + ";")
					.append("zeroDateTimeBehavior=convertToNull");

				if (DEBUG)
				{
					DEBUGGER.debug("StringBuilder: {}", sBuilder);
				}

				BasicDataSource dataSource = new BasicDataSource();
				dataSource.setDriverClassName(dsProps.getProperty("driver"));
				dataSource.setUrl(dsProps.getProperty("dsUrl"));
				dataSource.setUsername(dsProps.getProperty("username"));
				dataSource.setConnectionProperties(sBuilder.toString());
				dataSource.setPassword(PasswordUtils.decryptText(
                    dsProps.getProperty("password"),
                    dsProps.getProperty("salt").length()));

				if (DEBUG)
				{
					DEBUGGER.debug("BasicDataSource: {}", dataSource);
				}

                dsStream.close();
			}

            isLoaded = true;
		}
        catch (IOException iox)
        {
            ERROR_LOGGER.error(iox.getMessage(), iox);
        }
        finally
        {
            try
            {
                iStream.close();
			}
            catch (IOException iox)
            {
                ERROR_LOGGER.error(iox.getMessage(), iox);
            }
		}

        return isLoaded;
    }

    protected void onPostExecute(final boolean value)
    {
        final String methodName = LoaderTask.CNAME + "#onPostExecute(final boolean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
		System.out.println(value);
    }
}

