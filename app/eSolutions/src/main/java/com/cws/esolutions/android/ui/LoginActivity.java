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
 * eSolutions
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
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 * @see android.app.Activity
 */
public class LoginActivity extends Activity
{
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
        super.setContentView(R.layout.login);
        super.setTitle(R.string.loginTitle);

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
                    // home
                }
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

        final EditText etUsername = (EditText) super.findViewById(R.id.etUsername);
        final EditText etPassword = (EditText) super.findViewById(R.id.etPassword);
        final TextView tvResponseValue = (TextView) super.findViewById(R.id.tvResponseValue);

        if (DEBUG)
        {
            DEBUGGER.debug("EditText: {}", etUsername);
            DEBUGGER.debug("EditText: {}", etPassword);
            DEBUGGER.debug("TextView: {}", tvResponseValue);
        }

        if ((StringUtils.isEmpty(etUsername.getText().toString())) || (StringUtils.isEmpty(etUsername.getText().toString())))
        {
            // no data provided
            // route through the request type
            // to display the right message
            tvResponseValue.setTextColor(Color.RED);
            tvResponseValue.setText(R.string.txtUsernameRequired);
            tvResponseValue.setTextColor(Color.RED);
            tvResponseValue.setText(R.string.txtPasswordRequired);
        }
        else
        {
			tvResponseValue.setTextColor(Color.BLUE);
			tvResponseValue.setText(R.string.txtPleaseWait);
            final UserAuthenticationTask userLogin = new UserAuthenticationTask(LoginActivity.this, MainActivity.class);

            if (DEBUG)
            {
                DEBUGGER.debug("UserAuthenticationTask: {}", userLogin);
            }

            // send the logon
            userLogin.execute(new String[] { etUsername.getText().toString(), etPassword.getText().toString() });

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
