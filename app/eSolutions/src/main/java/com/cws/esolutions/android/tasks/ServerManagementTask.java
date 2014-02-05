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
 * ServerManagementTask.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.List;
import org.slf4j.Logger;
import android.os.AsyncTask;
import android.app.Activity;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.android.Constants;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.android.utils.NetworkUtils;
import com.cws.esolutions.core.processors.dto.Server;
import com.cws.esolutions.android.ApplicationServiceBean;
import com.cws.esolutions.core.processors.enums.ServerType;
import com.cws.esolutions.android.enums.ServerManagementType;
import com.cws.esolutions.core.processors.dto.ServerManagementRequest;
import com.cws.esolutions.core.processors.dto.ServerManagementResponse;
import com.cws.esolutions.core.processors.impl.ServerManagementProcessorImpl;
import com.cws.esolutions.core.processors.exception.ServerManagementException;
import com.cws.esolutions.core.processors.interfaces.IServerManagementProcessor;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 * @see android.os.AsyncTask
 */
public class ServerManagementTask extends AsyncTask<String, List<Object>, ServerManagementResponse>
{
    private Activity reqActivity = null;

    private static final String CNAME = ServerManagementTask.class.getName();
    private static final ApplicationServiceBean appBean = ApplicationServiceBean.getInstance();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + ServerManagementTask.CNAME);

    public ServerManagementTask(final Activity activity)
    {
        final String methodName = ServerManagementTask.CNAME + "#ServerDataTask(final Activity activity)#Constructor()";

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
        final String methodName = ServerManagementTask.CNAME + "#onPreExecute()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        if (!(NetworkUtils.checkNetwork(this.reqActivity)))
        {
            super.cancel(true);
        }
    }

    @Override
    protected ServerManagementResponse doInBackground(final String... value)
    {
        final String methodName = ServerManagementTask.CNAME + "#doInBackground(final String... vlaue)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", (Object) value);
        }

        Server server = null;
        ServerManagementRequest request = null;
        ServerManagementResponse response = null;

        final ServerManagementType type = ServerManagementType.valueOf(value[0]);
        final IServerManagementProcessor processor = new ServerManagementProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("ServerManagementType: {}", type);
        }

        try
        {
            switch (type)
            {
                case ADD:
                    break;
                case LIST:
                    request = new ServerManagementRequest();
                    request.setApplicationId(Constants.APPLICATION_ID);
                    request.setApplicationName(Constants.APPLICATION_NAME);
                    request.setRequestInfo(appBean.getReqInfo());
                    request.setUserAccount((UserAccount) this.reqActivity.getIntent().getExtras().getSerializable(Constants.USER_DATA));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("ServerManagementRequest: {}", request);
                    }

                    response = processor.listServers(request);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("ServerManagementResponse: {}", response);
                    }

                    return response;
                case LISTBYATTRIB:
                    server = new Server();
                    server.setServerType(ServerType.valueOf(value[0]));
                    
                    request = new ServerManagementRequest();
                    request.setApplicationId(Constants.APPLICATION_ID);
                    request.setApplicationName(Constants.APPLICATION_NAME);
                    request.setRequestInfo(appBean.getReqInfo());
                    request.setUserAccount((UserAccount) this.reqActivity.getIntent().getExtras().getSerializable(Constants.USER_DATA));
                    request.setTargetServer(server);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("ServerManagementRequest: {}", request);
                    }

                    response = processor.listServersByAttribute(request);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("ServerManagementResponse: {}", response);
                    }

                    return response;
                case REMOVE:
                    server = new Server();
                    server.setServerGuid((String) value[1]);
                    
                    request = new ServerManagementRequest();
                    request.setApplicationId(Constants.APPLICATION_ID);
                    request.setApplicationName(Constants.APPLICATION_NAME);
                    request.setRequestInfo(appBean.getReqInfo());
                    request.setUserAccount((UserAccount) this.reqActivity.getIntent().getExtras().getSerializable(Constants.USER_DATA));
                    request.setTargetServer(server);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("ServerManagementRequest: {}", request);
                    }

                    response = processor.removeServerData(request);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("ServerManagementResponse: {}", response);
                    }

                    return response;
                case RETRIEVE:
                    server = new Server();
                    server.setServerGuid((String) value[1]);
                    
                    request = new ServerManagementRequest();
                    request.setApplicationId(Constants.APPLICATION_ID);
                    request.setApplicationName(Constants.APPLICATION_NAME);
                    request.setRequestInfo(appBean.getReqInfo());
                    request.setUserAccount((UserAccount) this.reqActivity.getIntent().getExtras().getSerializable(Constants.USER_DATA));
                    request.setTargetServer(server);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("ServerManagementRequest: {}", request);
                    }

                    response = processor.getServerData(request);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("ServerManagementResponse: {}", response);
                    }

                    return response;
                case UPDATE:
                    break;
                default:
                    break;
            }
        }
        catch (ServerManagementException smx)
        {
            ERROR_RECORDER.error(smx.getMessage(), smx);
        }

        return response;
    }
}

