/**
 * Copyright (c) 2009 - 2012 By: CWS, Inc.
 * 
 * All rights reserved. These materials are confidential and
 * proprietary to CWS N.A and no part of these materials
 * should be reproduced, published in any form by any means,
 * electronic or mechanical, including photocopy or any information
 * storage or retrieval system not should the materials be
 * disclosed to third parties without the express written
 * authorization of CWS N.A.
 */
package com.cws.us.esolutions.tasks;

import java.util.List;
import java.util.Arrays;
import org.slf4j.Logger;
import java.io.IOException;
import java.util.ArrayList;
import android.os.AsyncTask;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import org.slf4j.LoggerFactory;
import android.widget.TextView;
import android.content.Context;
import android.net.NetworkInfo;
import org.ksoap2.SoapEnvelope;
import android.net.ConnectivityManager;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.serialization.PropertyInfo;
import org.xmlpull.v1.XmlPullParserException;
import org.ksoap2.serialization.SoapPrimitive;
import org.apache.commons.lang.RandomStringUtils;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.cws.us.esolutions.ui.R;
import com.cws.us.esolutions.Constants;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.dto.UserSecurity;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.dao.userauth.enums.LoginType;
import com.cws.esolutions.security.processors.dto.AuthenticationRequest;
import com.cws.esolutions.security.dao.userauth.enums.AuthenticationType;
import com.cws.esolutions.security.processors.dto.AuthenticationResponse;
import com.cws.esolutions.security.processors.exception.AuthenticationException;
/**
 * eSolutions
 * com.cws.esolutions.core.tasks
 * UserAuthenticationTask.java
 *
 * TODO: Add class description
 *
 * $Id: UserAuthenticationTask.java 2289 2013-01-03 21:03:37Z kmhuntly@gmail.com $
 * $Author: kmhuntly@gmail.com $
 * $Date: 2013-01-03 16:03:37 -0500 (Thu, 03 Jan 2013) $
 * $Revision: 2289 $
 * @author khuntly
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * khuntly @ Oct 12, 2012 2:56:18 PM
 *     Created.
 */
public class UserAuthenticationTask extends AsyncTask<List<Object>, Integer, List<Object>>
{
    private String methodName = null;
    private Activity reqActivity = null;
    private Class<?> resActivity = null;

    private static final String CNAME = UserAuthenticationTask.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER);

    /**
     * @param request - The requesting activity
     * @param response - The target activity
     */
    public UserAuthenticationTask(final Activity request, final Class<?> response)
    {
        this.methodName = UserAuthenticationTask.CNAME + "#UserAuthenticationTask(final Activity request, final Class<?> response)";

        if (DEBUG)
        {
            DEBUGGER.debug(this.methodName);
            DEBUGGER.debug("Activity: ", request);
            DEBUGGER.debug("Class: ", response);
        }

        this.reqActivity = request;
        this.resActivity = response;
    }

    @Override
    protected void onPreExecute()
    {
        this.methodName = UserAuthenticationTask.CNAME + "#onPreExecute()";

        if (DEBUG)
        {
            DEBUGGER.debug(this.methodName);
        }

        boolean isConnected = false;
        ConnectivityManager connMgr = null;

        connMgr = (ConnectivityManager) reqActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfo = connMgr.getAllNetworkInfo();

        if ((networkInfo.length == 0) || (networkInfo == null))
        {
            // no available network connection
            ERROR_RECORDER.error("No available network connection. Cannot continue.");

            cancel(true);
        }
        else
        {
            for (NetworkInfo networks : networkInfo)
            {
                if (networks.isConnected())
                {
                    isConnected = true;
                }
            }

            if (!(isConnected))
            {
                ERROR_RECORDER.error("Network connections are available but not currently connected.");

                cancel(true);                
            }
        }
    }

    @Override
    protected List<Object> doInBackground(final List<Object>... request)
    {
        this.methodName = UserAuthenticationTask.CNAME + "#doInBackground(final List<Object>... request)";

        if (DEBUG)
        {
            DEBUGGER.debug(this.methodName);
            DEBUGGER.debug("Request: {}", request);
        }

        UserAccount userAccount = new UserAccount();
        UserSecurity userSecurity = new UserSecurity();
        AuthenticationRequest authRequest = new AuthenticationRequest();
        AuthenticationResponse authResponse = new AuthenticationResponse();

        final List<Object> requestList = (List<Object>) request[0];
        final List<Object> responseList = new ArrayList<Object>(
                Arrays.asList(
                        requestList.get(1)));
        final AuthenticationType authType = (AuthenticationType) requestList.get(0);
        final LoginType loginType = (LoginType) requestList.get(1);
        final String sessionId = (String) requestList.get(2);

        try
        {
            authRequest.setAuthType((AuthenticationType) requestList.get(0));
            authRequest.setLoginType((LoginType) requestList.get(1));
            authRequest.setAppName(Constants.APPLICATION_NAME);

            userAccount.setSessionId(sessionId);

            switch (authType)
            {
                case LOGIN:
                    switch(loginType)
                    {
                        case USERNAME:
                            userAccount.setUsername((String) requestList.get(3));
                            userAccount.setSessionId(RandomStringUtils.randomAlphanumeric(32));

                            break;
                        case PASSWORD:
                            userAccount = (UserAccount) requestList.get(3);
                            userSecurity.setPassword((String) requestList.get(4));

                            break;
                        default:
                            throw new AuthenticationException("No login type was provided.");
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("UserAccount: ", userAccount);
                        DEBUGGER.debug("AuthenticationRequest: ", authRequest);
                    }

                    authRequest.setUserAccount(userAccount);
                    authRequest.setUserSecurity(userSecurity);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("AuthenticationRequest: ", authRequest);
                    }

                    SoapObject soapObject = new SoapObject("http://agent.caspersbox.corp/s?q=esolutions", "sayHello");

                    PropertyInfo propInfo = new PropertyInfo();
                    propInfo.name = "name";
                    propInfo.type = PropertyInfo.STRING_CLASS;
                    propInfo.setValue("hello");

                    PropertyInfo info = new PropertyInfo();
                    info.setType(AuthenticationRequest.class);
                    info.setValue(authRequest);
                    info.setName("request");

                    soapObject.addProperty(propInfo);
                    //soapObject.addProperty(info);

                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.setOutputSoapObject(soapObject);

                    HttpTransportSE httpTransport = new HttpTransportSE("http://161.86.145.22:8181/eSolutions/eSolutionsService?wsdl");

                    httpTransport.call("http://agent.caspersbox.corp/s?q=esolutions/sayHello", envelope);

                    SoapPrimitive response = (SoapPrimitive) envelope.getResponse();

                    //authResponse = agentAuth.processAgentLogon(authRequest);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("AuthenticationResponse: ", authResponse);
                    }

                    responseList.add(authResponse);

                    break;
                default:
                    authResponse = null;

                    throw new AuthenticationException("No authentication type was provided.");
            }
        }
        catch (AuthenticationException ax)
        {
            ERROR_RECORDER.error(ax.getMessage(), ax);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (XmlPullParserException e)
        {
            e.printStackTrace();
        }

        return responseList;
    }

    @Override
    protected void onPostExecute(final List<Object> responseList)
    {
        this.methodName = UserAuthenticationTask.CNAME + "#onPostExecute(final List<Object> responseList)";

        if (DEBUG)
        {
            DEBUGGER.debug(this.methodName);
            DEBUGGER.debug("responseList: ", responseList);
        }

        final LoginType loginType = (LoginType) responseList.get(0);
        final AuthenticationResponse authResponse = (AuthenticationResponse) responseList.get(1);
        final TextView responseView = ((TextView) this.reqActivity.findViewById(R.id.tvResponseValue));

        if (authResponse != null)
        {
            Intent intent = null;

            if (authResponse.getRequestStatus() == SecurityRequestStatus.SUCCESS)
            {
                UserAccount resAccount = authResponse.getUserAccount();

                if (DEBUG)
                {
                    DEBUGGER.debug("UserAccount: ", resAccount);
                }

                switch(resAccount.getStatus())
                {
                    case SUCCESS:
                        switch (loginType)
                        {
                            case USERNAME:
                                intent = new Intent(this.reqActivity, this.reqActivity.getClass());

                                break;
                            default:
                                intent = new Intent(this.reqActivity, this.resActivity);

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("Intent: ", intent);
                                }

                                break;
                        }

                        intent.putExtra("userData", authResponse.getUserAccount());
                        this.reqActivity.startActivity(intent);
                        this.reqActivity.finish();

                        break;
                    case FAILURE:
                        responseView.setTextColor(Color.RED);
                        responseView.setText(this.reqActivity.getString(R.string.txtSignonError));

                        break;
                    case LOCKOUT:
                        responseView.setTextColor(Color.RED);
                        responseView.setText(this.reqActivity.getString(R.string.txtAccountLocked));

                        break;
                    case SESSION_EXISTS:
                        responseView.setTextColor(Color.RED);
                        responseView.setText(this.reqActivity.getString(R.string.txtAccountSignedIn));

                        break;
                    case SUSPENDED:
                        responseView.setTextColor(Color.RED);
                        responseView.setText(this.reqActivity.getString(R.string.txtAccountSuspended));

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
