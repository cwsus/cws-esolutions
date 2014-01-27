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
import android.os.AsyncTask;
import android.app.Activity;
import org.slf4j.LoggerFactory;
import android.net.NetworkInfo;
import android.content.Context;
import android.net.ConnectivityManager;

import com.cws.esolutions.android.Constants;
import com.cws.esolutions.core.exception.CoreServiceException;
import com.cws.esolutions.core.listeners.CoreServiceInitializer;
import com.cws.esolutions.security.exception.SecurityServiceException;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;

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

        try
        {
            CoreServiceInitializer.initializeService("eSolutionsCore/config/ServiceConfig.xml", "eSolutionsCore/logging/logging.xml");

            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/logging/logging.xml");

            isLoaded = true;
		}
        catch (CoreServiceException csx)
        {
            ERROR_LOGGER.error(csx.getMessage(), csx);
        }
        catch (SecurityServiceException ssx)
        {
            ERROR_LOGGER.error(ssx.getMessage(), ssx);
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

