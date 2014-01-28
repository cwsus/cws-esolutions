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
import java.util.Map;
import org.slf4j.Logger;
import java.util.HashMap;
import java.sql.Connection;
import java.io.InputStream;
import java.io.IOException;
import javax.sql.DataSource;
import java.util.Properties;
import android.os.AsyncTask;
import android.app.Activity;
import java.sql.SQLException;
import org.slf4j.LoggerFactory;
import android.content.res.AssetManager;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.dbcp.BasicDataSource;

import com.cws.esolutions.android.Constants;
import com.cws.esolutions.android.utils.NetworkUtils;
import com.cws.esolutions.security.utils.PasswordUtils;
import com.cws.esolutions.android.ApplicationServiceBean;

public class LoaderTask extends AsyncTask<Void, Void, Boolean>
{
    private InputStream iStream = null;
    private Activity reqActivity = null;

    private static final String CNAME = LoaderTask.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + LoaderTask.class.getSimpleName());

    public LoaderTask(final Activity activity)
    {
        final String methodName = LoaderTask.CNAME + "#LoaderTask(final Activity activity)#Constructor()";

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

			this.iStream = assetMgr.open("application.properties"); // TODO: make this configurableS

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
        final String methodName = LoaderTask.CNAME + "#doInBackground(final Void... vlaue)";

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

            if (DEBUG)
            {
                DEBUGGER.debug("Properties: {}", props);
			}

            String[] dataSources = StringUtils.split(props.getProperty("datasources"), ",");

            if (DEBUG)
            {
                DEBUGGER.debug("String[]: {}", dataSources);
            }

			if ((dataSources.length == 0) || (dataSources == null))
			{
				return true;
			}

			Map<String, DataSource> dsMap = bean.getDataSources();

            if (DEBUG)
            {
                DEBUGGER.debug("Map<String, DataSource>: {}", dsMap);
			}

            if ((dsMap == null) || (dsMap.isEmpty()))
			{
				dsMap = new HashMap<String, DataSource>();
			}

            for (String source : dataSources)
            {
                if (DEBUG)
                {
                    DEBUGGER.debug("String: {}", source);
                }

				if (!(dsMap.containsKey(source)))
				{
				    InputStream dsStream = assetMgr.open(source + ".properties");

				    if (DEBUG)
				    {
					    DEBUGGER.debug("InputStream: {}", dsStream);
				    }

	                if ((dsStream == null) || (dsStream.available() == 0))
	                {
	                    ERROR_RECORDER.error("Unable to load datasource properties.");
	                }
	                else
	                {
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

                        Connection conn = null;

                        try
                        {
                            conn = dataSource.getConnection();
					    }
                        catch (SQLException sqx)
                        {
					    	ERROR_RECORDER.error(sqx.getMessage(), sqx);

						    return false;
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
    			    }

	                dsStream.close();
			    }
            }

            isLoaded = true;
		}
        catch (IOException iox)
        {
            ERROR_RECORDER.error(iox.getMessage(), iox);
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

