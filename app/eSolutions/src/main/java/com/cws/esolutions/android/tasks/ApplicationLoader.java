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
import org.apache.commons.lang.RandomStringUtils;
import android.content.res.Resources.NotFoundException;

import com.cws.esolutions.android.ui.R;
import com.cws.esolutions.android.Constants;
import com.cws.esolutions.android.utils.NetworkUtils;
import com.cws.esolutions.android.ApplicationServiceBean;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 * @see android.os.AsyncTask
 */
public class ApplicationLoader extends AsyncTask<Void, Void, Boolean>
{
    private InputStream iStream = null;
    private Activity reqActivity = null;

    private static final String CNAME = ApplicationLoader.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + ApplicationLoader.class.getSimpleName());

    public ApplicationLoader(final Activity activity)
    {
        final String methodName = ApplicationLoader.CNAME + "#LoaderTask(final Activity activity)#Constructor()";

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
        final String methodName = ApplicationLoader.CNAME + "#onPreExecute()";

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
            
            this.iStream = assetMgr.open(this.reqActivity.getResources().getString(R.string.applicationConfigFile));

            if (DEBUG)
            {
                DEBUGGER.debug("InputStream: {}", this.iStream);
            }

            if ((this.iStream == null) || (this.iStream.available() == 0))
            {
                ERROR_RECORDER.error("Unable to load application properties. Cannot continue.");

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
        final String methodName = ApplicationLoader.CNAME + "#doInBackground(final Void... vlaue)";

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
            Properties props = new Properties();
            props.load(this.iStream);

            RequestHostInfo reqInfo = new RequestHostInfo();
            reqInfo.setHostAddress(InetAddress.getLocalHost().getHostAddress());
            reqInfo.setHostName(InetAddress.getLocalHost().getHostName());
            reqInfo.setSessionId(RandomStringUtils.randomAlphanumeric(Integer.parseInt(props.getProperty("sessIdLength"), 32)));

            if (DEBUG)
            {
                DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            }

            bean.setReqInfo(reqInfo);

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
        finally
        {
            try
            {
                this.iStream.close();
            }
            catch (IOException iox)
            {
                ERROR_RECORDER.error(iox.getMessage(), iox);
            }
        }

        return isLoaded;
    }
}

