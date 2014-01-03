/**
 * Copyright (c) 2009 - 2012 By: CaspersBox Web Services
 * 
 * All rights reserved. These materials are confidential and
 * proprietary to CWS N.A and no part of these materials
 * should be reproduced, published in any form by any means,
 * electronic or mechanical, including photocopy or any information
 * storage or retrieval system not should the materials be
 * disclosed to third parties without the express written
 * authorization of CWS N.A.
 */
package com.cws.esolutions.android.ui;

import java.util.List;
import java.util.Arrays;
import org.slf4j.Logger;
import android.view.View;
import android.os.Bundle;
import java.util.ArrayList;
import android.app.Activity;
import android.text.InputType;
import android.graphics.Color;
import org.slf4j.LoggerFactory;
import android.widget.EditText;
import android.widget.TextView;
import org.apache.commons.lang.StringUtils;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import com.cws.esolutions.android.Constants;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.android.tasks.UserAuthenticationTask;
import com.cws.esolutions.security.dao.userauth.enums.LoginType;
import com.cws.esolutions.security.dao.userauth.enums.AuthenticationType;
/**
 * NewProject
 * com.cws.esolutions.core.ui
 * LoginActivity.java
 *
 * TODO: Add class description
 *
 * $Id: LoginActivity.java 2289 2013-01-03 21:03:37Z kmhuntly@gmail.com $
 * $Author: kmhuntly@gmail.com $
 * $Date: 2013-01-03 16:03:37 -0500 (Thu, 03 Jan 2013) $
 * $Revision: 2289 $
 * @author khuntly
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Oct 11, 2012 1:22:30 PM
 *     Created.
 */
public class LoginActivity extends Activity
{
    private String sessionId = null;
    private String methodName = null;
    private LoginType loginType = null;
    private TextView tvRequestName = null;
    private EditText etRequestValue = null;
    private TextView tvResponseValue = null;
    
    private static final String CNAME = LoginActivity.class.getName();
    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public void onCreate(final Bundle bundle)
    {
        methodName = CNAME + "#onCreate(final Bundle bundle)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Bundle: ", bundle);
        }

        super.onCreate(bundle);

        setContentView(R.layout.activity_login);
        setTitle(R.string.loginTitle);

        tvRequestName = (TextView) findViewById(R.id.tvRequestName);
        etRequestValue = (EditText) findViewById(R.id.etRequestValue);
        tvResponseValue = (TextView) findViewById(R.id.tvResponseValue);

        if (getIntent().getExtras() != null)
        {
            if (getIntent().getExtras().containsKey(Constants.USER_DATA))
            {
                UserAccount userAccount = (UserAccount) getIntent().getExtras().getSerializable(Constants.USER_DATA);

                if (userAccount != null)
                {
                    // user already went through and submitted uid
                    sessionId = userAccount.getSessionId();
                    loginType = LoginType.PASSWORD;
                    tvRequestName.setText(R.string.tvPassword);
                    etRequestValue.setHint(R.string.hintPassword);
                    etRequestValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                else
                {
                    getIntent().getExtras().remove(Constants.USER_DATA);

                    loginType = LoginType.USERNAME;
                    tvRequestName.setText(R.string.tvUsername);
                    etRequestValue.setHint(R.string.hintUsername);
                    etRequestValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                }
            }
        }
        else
        {
            loginType = LoginType.USERNAME;
            tvRequestName.setText(R.string.tvUsername);
            etRequestValue.setHint(R.string.hintUsername);
            etRequestValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        }
    }

    public void onBackPressed()
    {
        methodName = CNAME + "#onBackPressed()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        finish();
    }

    @SuppressWarnings("unchecked")
    public void executeUserLogin(final View view) throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        methodName = CNAME + "#executeUserLogin(final View view)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("View: ", view);
        }

        if (StringUtils.isEmpty(etRequestValue.getText().toString()))
        {
            // no data provided
            // route through the request type
            // to display the right message
            switch (loginType)
            {
                case USERNAME:
                    tvResponseValue.setTextColor(Color.RED);
                    tvResponseValue.setText(R.string.txtUsernameRequired);

                    break;
                case PASSWORD:
                    tvResponseValue.setTextColor(Color.RED);
                    tvResponseValue.setText(R.string.txtPasswordRequired);

                    break;
                case COMBINED:
                    break;
                case OTP:
                    break;
                case SECCONFIG:
                    break;
                default:
                    break;
            }
        }
        else
        {
            UserAccount userAccount = null;

            final List<Object> authRequest = new ArrayList<Object>(
                    Arrays.asList(
                            AuthenticationType.LOGIN,
                            loginType,
                            sessionId));
            final UserAuthenticationTask userLogin = new UserAuthenticationTask(
                    LoginActivity.this, MainActivity.class);

            if (DEBUG)
            {
                DEBUGGER.debug("authRequest: ", authRequest);
                DEBUGGER.debug("UserAuthenticationTask: ", userLogin);
            }

            switch (loginType)
            {
                case USERNAME:
                    authRequest.add(3, etRequestValue.getText().toString());

                    break;
                case PASSWORD:
                    if (DEBUG)
                    {
                        DEBUGGER.debug("UserAccount: ", userAccount);
                    }

                    authRequest.add(3, getIntent().getSerializableExtra("userData"));
                    authRequest.add(4, etRequestValue.getText().toString());

                    break;
                default:
                    tvResponseValue.setTextColor(Color.RED);
                    tvResponseValue.setText(R.string.txtSignonError);

                    return;
            }

            if (DEBUG)
            {
                for (Object obj: authRequest)
                {
                    DEBUGGER.debug("Object: ", obj);
                }
            }

            // send the logon
            userLogin.execute(authRequest);

            if (userLogin.isCancelled())
            {
                tvResponseValue.setTextColor(Color.RED);
                tvResponseValue.setText(R.string.txtSignonError);
            }
        }
    }
}
