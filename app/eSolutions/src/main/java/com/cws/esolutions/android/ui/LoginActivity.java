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
import com.cws.esolutions.android.tasks.LoaderTask;
import com.cws.esolutions.android.tasks.UserAuthenticationTask;
import com.cws.esolutions.security.dao.userauth.enums.LoginType;
import com.cws.esolutions.security.dao.userauth.enums.AuthenticationType;

public class LoginActivity extends Activity
{
    private LoginType loginType = null;

    private static final String CNAME = LoginActivity.class.getName();
    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    @Override
    public void onCreate(final Bundle bundle)
    {
        final String methodName = LoginActivity.CNAME + "#onCreate(final Bundle bundle)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Bundle: {}", bundle);
        }

        super.onCreate(bundle);
        super.setContentView(R.layout.activity_login);
        super.setTitle(R.string.loginTitle);

        final TextView tvRequestName = (TextView) super.findViewById(R.id.tvRequestName);
        final EditText etRequestValue = (EditText) super.findViewById(R.id.etRequestValue);
        final TextView tvResponseValue = (TextView) super.findViewById(R.id.tvResponseValue);

        if (DEBUG)
        {
            DEBUGGER.debug("TextView: {}", tvRequestName);
            DEBUGGER.debug("EditText: {}", etRequestValue);
            DEBUGGER.debug("TextView: {}", tvResponseValue);
        }

		new LoaderTask(LoginActivity.this).execute();

        if (super.getIntent().getExtras() != null)
        {
            if (super.getIntent().getExtras().containsKey(Constants.USER_DATA))
            {
                UserAccount userAccount = (UserAccount) super.getIntent().getExtras().getSerializable(Constants.USER_DATA);

                if (DEBUG)
                {
                    DEBUGGER.debug("UserAccount: {}", userAccount);
                }

                if (userAccount != null)
                {
                    // user already went through and submitted uid
                    this.loginType = LoginType.PASSWORD;
                    tvRequestName.setText(R.string.tvPassword);
                    etRequestValue.setHint(R.string.hintPassword);
                    etRequestValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("LoginType: {}", this.loginType);
                        DEBUGGER.debug("TextView: {}", tvRequestName);
                        DEBUGGER.debug("EditText: {}", etRequestValue);
                        DEBUGGER.debug("TextView: {}", tvResponseValue);
                    }
                }
                else
                {
                    super.getIntent().getExtras().remove(Constants.USER_DATA);

                    this.loginType = LoginType.USERNAME;
                    tvRequestName.setText(R.string.tvUsername);
                    etRequestValue.setHint(R.string.hintUsername);
                    etRequestValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("LoginType: {}", this.loginType);
                        DEBUGGER.debug("TextView: {}", tvRequestName);
                        DEBUGGER.debug("EditText: {}", etRequestValue);
                        DEBUGGER.debug("TextView: {}", tvResponseValue);
                    }
                }
            }
        }
        else
        {
            this.loginType = LoginType.USERNAME;
            tvRequestName.setText(R.string.tvUsername);
            etRequestValue.setHint(R.string.hintUsername);
            etRequestValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

            if (DEBUG)
            {
                DEBUGGER.debug("LoginType: {}", this.loginType);
                DEBUGGER.debug("TextView: {}", tvRequestName);
                DEBUGGER.debug("EditText: {}", etRequestValue);
                DEBUGGER.debug("TextView: {}", tvResponseValue);
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        final String methodName = LoginActivity.CNAME + "#onBackPressed()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        super.finish();
    }

    @SuppressWarnings("unchecked")
    public void executeUserLogin(final View view) throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        final String methodName = LoginActivity.CNAME + "#executeUserLogin(final View view)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("View: {}", view);
        }

        final TextView tvRequestName = (TextView) super.findViewById(R.id.tvRequestName);
        final EditText etRequestValue = (EditText) super.findViewById(R.id.etRequestValue);
        final TextView tvResponseValue = (TextView) super.findViewById(R.id.tvResponseValue);

        if (DEBUG)
        {
            DEBUGGER.debug("TextView: {}", tvRequestName);
            DEBUGGER.debug("EditText: {}", etRequestValue);
            DEBUGGER.debug("TextView: {}", tvResponseValue);
        }

        if (StringUtils.isEmpty(etRequestValue.getText().toString()))
        {
            // no data provided
            // route through the request type
            // to display the right message
            switch (this.loginType)
            {
                case USERNAME:
                    tvResponseValue.setTextColor(Color.RED);
                    tvResponseValue.setText(R.string.txtUsernameRequired);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("TextView: {}", tvResponseValue);
                    }

                    break;
                case PASSWORD:
                    tvResponseValue.setTextColor(Color.RED);
                    tvResponseValue.setText(R.string.txtPasswordRequired);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("TextView: {}", tvResponseValue);
                    }

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
                            this.loginType));
            final UserAuthenticationTask userLogin = new UserAuthenticationTask(LoginActivity.this, MainActivity.class);

            if (DEBUG)
            {
                DEBUGGER.debug("List<Object>: {}", authRequest);
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

                if (DEBUG)
                {
                    DEBUGGER.debug("TextView: {}", tvResponseValue);
                }
            }
        }
    }
}
