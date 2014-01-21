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
package com.cws.esolutions.android.ui;
/*
 * NewProject
 * com.cws.esolutions.core.ui
 * LoginActivity.java
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
            DEBUGGER.debug("Bundle: {}", bundle);
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
            DEBUGGER.debug("View: {}", view);
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
                DEBUGGER.debug("authRequest: {}", authRequest);
                DEBUGGER.debug("UserAuthenticationTask: {}", userLogin);
            }

            switch (loginType)
            {
                case USERNAME:
                    authRequest.add(3, etRequestValue.getText().toString());

                    break;
                case PASSWORD:
                    if (DEBUG)
                    {
                        DEBUGGER.debug("UserAccount: {}", userAccount);
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
                    DEBUGGER.debug("Object: {}", obj);
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
