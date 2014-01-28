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
 * UserAuthenticationTask.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.List;
import java.util.Arrays;
import org.slf4j.Logger;
import java.io.IOException;
import java.util.ArrayList;
import java.net.InetAddress;
import android.os.AsyncTask;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import org.slf4j.LoggerFactory;
import android.widget.TextView;
import org.apache.commons.lang.RandomStringUtils;

import com.cws.esolutions.android.ui.R;
import com.cws.esolutions.android.Constants;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.android.utils.NetworkUtils;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.security.processors.dto.AuthenticationData;
import com.cws.esolutions.security.processors.dto.AuthenticationRequest;
import com.cws.esolutions.security.dao.userauth.enums.AuthenticationType;
import com.cws.esolutions.security.processors.dto.AuthenticationResponse;
import com.cws.esolutions.security.processors.exception.AuthenticationException;
import com.cws.esolutions.security.processors.impl.AuthenticationProcessorImpl;
import com.cws.esolutions.security.processors.interfaces.IAuthenticationProcessor;

public class UserAuthenticationTask extends AsyncTask<List<Object>, Integer, List<Object>>
{
    private Activity reqActivity = null;
    private Class<?> resActivity = null;

    private static final String CNAME = UserAuthenticationTask.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + UserAuthenticationTask.class.getSimpleName());

    public UserAuthenticationTask(final Activity request, final Class<?> response)
    {
        final String methodName = UserAuthenticationTask.CNAME + "#UserAuthenticationTask(final Activity request, final Class<?> response)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Activity: {}", request);
            DEBUGGER.debug("Class: {}", response);
        }

        this.reqActivity = request;
        this.resActivity = response;
    }

    @Override
    protected void onPreExecute()
    {
        final String methodName = UserAuthenticationTask.CNAME + "#onPreExecute()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

		if (!(NetworkUtils.checkNetwork(this.reqActivity)))
		{
			ERROR_RECORDER.error("Network connections are available but not currently connected.");

			super.cancel(true);
		}
    }

    @Override
    protected List<Object> doInBackground(final List<Object>... request)
    {
        final String methodName = UserAuthenticationTask.CNAME + "#doInBackground(final List<Object>... request)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Request: {}", request);
        }

        UserAccount userAccount = new UserAccount();
        AuthenticationData userSecurity = new AuthenticationData();

        final List<Object> requestList = (List<Object>) request[0];
        final List<Object> responseList = new ArrayList<Object>(
                Arrays.asList(
                        requestList.get(1)));
        final IAuthenticationProcessor processor = new AuthenticationProcessorImpl();

        try
        {
            userAccount = (UserAccount) requestList.get(3);
            userAccount.setUsername((String) requestList.get(3));
            userSecurity.setPassword((String) requestList.get(4));

            if (DEBUG)
            {
                DEBUGGER.debug("UserAccount: {}", userAccount);
            }

            RequestHostInfo reqInfo = new RequestHostInfo();
            reqInfo.setHostAddress(InetAddress.getLocalHost().getHostAddress());
            reqInfo.setHostName(InetAddress.getLocalHost().getHostName());
            reqInfo.setSessionId(RandomStringUtils.randomAlphanumeric(32));

            if (DEBUG)
            {
                DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            }

            AuthenticationRequest authReq = new AuthenticationRequest();
            authReq.setApplicationName(Constants.APPLICATION_NAME);
            authReq.setAuthType((AuthenticationType) requestList.get(0));
            authReq.setUserAccount(userAccount);
            authReq.setApplicationId(Constants.APPLICATION_ID);
            authReq.setHostInfo(reqInfo);

            if (DEBUG)
            {
                DEBUGGER.debug("AuthenticationRequest: {}", authReq);
            }

            AuthenticationResponse authResponse = processor.processAgentLogon(authReq);

            if (DEBUG)
            {
                DEBUGGER.debug("AuthenticationResponse: {}", authResponse);
            }

            responseList.add(authResponse);
        }
        catch (AuthenticationException ax)
        {
            ERROR_RECORDER.error(ax.getMessage(), ax);
        }
        catch (IOException iox)
        {
            ERROR_RECORDER.error(iox.getMessage(), iox);
        }

        return responseList;
    }

    @Override
    protected void onPostExecute(final List<Object> responseList)
    {
        final String methodName = UserAuthenticationTask.CNAME + "#onPostExecute(final List<Object> responseList)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("responseList: {}", responseList);
        }

        final AuthenticationResponse authResponse = (AuthenticationResponse) responseList.get(1);
        final TextView responseView = ((TextView) this.reqActivity.findViewById(R.id.tvResponseValue));

        if (authResponse != null)
        {
            if (authResponse.getRequestStatus() == SecurityRequestStatus.SUCCESS)
            {
                UserAccount resAccount = authResponse.getUserAccount();

                if (DEBUG)
                {
                    DEBUGGER.debug("UserAccount: {}", resAccount);
                }

                switch(resAccount.getStatus())
                {
                    case SUCCESS:
                        Intent intent = new Intent(this.reqActivity, this.resActivity);
                        intent.putExtra("userData", authResponse.getUserAccount());

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Intent: {}", intent);
                        }

                        this.reqActivity.startActivity(intent);
                        this.reqActivity.finish();

                        break;
                    default:
                        responseView.setTextColor(Color.RED);
                        responseView.setText(this.reqActivity.getString(R.string.txtSignonError));

                        break;
                }
            }
            else
            {
                responseView.setTextColor(Color.RED);
                responseView.setText(this.reqActivity.getString(R.string.txtSignonError));
            }
        }
        else
        {
            ERROR_RECORDER.error("authResponse was null. Cannot continue");

            responseView.setTextColor(Color.RED);
            responseView.setText(this.reqActivity.getString(R.string.txtSignonError));
        }
    }
}
