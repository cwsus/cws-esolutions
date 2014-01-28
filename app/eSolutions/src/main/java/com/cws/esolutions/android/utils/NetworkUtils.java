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
 * Project: eSolutions
 * Package: com.cws.esolutions.android.utils
 * File: NetworkUtils.java
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Jan 4, 2013 3:36:54 PM
 *     Created.
 */
import org.slf4j.Logger;
import android.os.AsyncTask;
import java.net.InetAddress;
import org.slf4j.LoggerFactory;
import android.net.NetworkInfo;
import android.content.Context;
import java.net.UnknownHostException;
import android.net.ConnectivityManager;

import com.cws.esolutions.android.Constants;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 */
public final class NetworkUtils
{
    private static final String CNAME = NetworkUtils.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + NetworkUtils.CNAME);

    public synchronized static boolean checkNetwork(final Context context)
    {
        final String methodName = NetworkUtils.CNAME + "#checkNetwork(final Context context)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        boolean isConnected = false;

        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

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
            ERROR_RECORDER.error("No available network connection. Cannot continue.");
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
                ERROR_RECORDER.error("Network connections are available but not currently connected.");
            }
        }

        return isConnected;
    }

    public static final synchronized boolean isHostValid(final String hostName)
    {
        final String methodName = NetworkUtils.CNAME + "#isHostValid(final String hostName)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", hostName);
        }

        boolean validHost = false;

        try
        {
            synchronized(new Object())
            {
                if (InetAddress.getByName(hostName) != null)
                {
                    validHost = true;
                }
            }
        }
        catch (UnknownHostException ux)
        {
            ERROR_RECORDER.error(ux.getMessage(), ux);
        }

        return validHost;
    }
}
