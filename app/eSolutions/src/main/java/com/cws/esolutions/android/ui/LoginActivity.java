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
import android.content.Intent;
import org.slf4j.LoggerFactory;
import android.widget.EditText;
import android.widget.TextView;
import org.apache.commons.lang.StringUtils;
import java.util.concurrent.ExecutionException;

import com.cws.esolutions.android.Constants;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.android.tasks.UserAuthenticationTask;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.processors.dto.AuthenticationResponse;
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

    public void loadForgotUsername(final View view)
    {
        final String methodName = LoginActivity.CNAME + "#loadForgotUsername(final View view)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("View: {}", view);
        }

        Intent intent = new Intent(this, OnlineResetActivity.class);
        intent.putExtra("forgotUsername", true);

        if (DEBUG)
        {
            DEBUGGER.debug("Intent: {}", intent);
        }

        super.startActivity(intent);
        super.finish();

        return;
	}

    public void loadForgotPassword(final View view)
    {
        final String methodName = LoginActivity.CNAME + "#loadForgotPassword(final View view)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("View: {}", view);
        }

        Intent intent = new Intent(this, OnlineResetActivity.class);
        intent.putExtra("forgotPassword", true);

        if (DEBUG)
        {
            DEBUGGER.debug("Intent: {}", intent);
        }

        super.startActivity(intent);
        super.finish();

        return;
    }

    public void executeUserLogin(final View view)
    {
        final String methodName = LoginActivity.CNAME + "#executeUserLogin(final View view)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("View: {}", view);
        }

        final EditText etUsername = (EditText) super.findViewById(R.id.etUsername);
        final EditText etPassword = (EditText) super.findViewById(R.id.etPassword);
		final TextView tvPassword = (TextView) super.findViewById(R.id.tvPassword);
        final TextView tvResponseValue = (TextView) super.findViewById(R.id.tvResponseValue);
        final UserAuthenticationTask userLogin = new UserAuthenticationTask(this);

        if (DEBUG)
        {
            DEBUGGER.debug("EditText: {}", etUsername);
            DEBUGGER.debug("EditText: {}", etPassword);
            DEBUGGER.debug("TextView: {}", tvResponseValue);
            DEBUGGER.debug("UserAuthenticationTask: {}", userLogin);
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

            return;
        }

		tvResponseValue.setTextColor(Color.BLUE);
		tvResponseValue.setText(R.string.txtPleaseWait);
        etUsername.setEnabled(false);
        etPassword.setEnabled(false);

        try
        {
            // send the logon
            userLogin.execute(new String[] { etUsername.getText().toString(), etPassword.getText().toString() });

            if (userLogin.isCancelled())
            {
				etUsername.setEnabled(true);
				etPassword.setEnabled(true);
				etUsername.setText("");
                etPassword.setText("");
				tvResponseValue.setTextColor(Color.RED);
				tvResponseValue.setText(super.getString(R.string.txtSignonError));

                if (DEBUG)
                {
                    DEBUGGER.debug("TextView: {}", tvResponseValue);
                }

                return;
            }

            AuthenticationResponse response = userLogin.get();

            if (DEBUG)
            {
                DEBUGGER.debug("AuthenticationResponse: {}", response);
			}

            if ((response == null) || (response.getRequestStatus() != SecurityRequestStatus.SUCCESS))
            {
                super.getIntent().putExtra("lockCount", response.getCount());

				etUsername.setEnabled(true);
				etPassword.setEnabled(true);
				etUsername.setText("");
                etPassword.setText("");
				tvResponseValue.setTextColor(Color.RED);
				tvResponseValue.setText(super.getString(R.string.txtSignonError));

                return;
            }

            UserAccount resAccount = response.getUserAccount();

            if (DEBUG)
            {
                DEBUGGER.debug("UserAccount: {}", resAccount);
            }

            switch(resAccount.getStatus())
            {
                case SUCCESS:
                    Intent intent = new Intent(this, HomeActivity.class);
                    intent.putExtra("userData", response.getUserAccount());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Intent: {}", intent);
                    }

                    super.startActivity(intent);
                    super.finish();

                    return;
                case CONTINUE:
					etUsername.setEnabled(false);
					etUsername.setText(resAccount.getUsername());
					tvPassword.setText(super.getString(R.string.tvOtpPassword));
					etPassword.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);

					super.getIntent().putExtra("userData", resAccount);

                    return;
                default:
				    super.getIntent().putExtra("lockCount", response.getCount());

					etUsername.setEnabled(true);
					etPassword.setEnabled(true);
					etUsername.setText("");
					etPassword.setText("");
					tvResponseValue.setTextColor(Color.RED);
					tvResponseValue.setText(super.getString(R.string.txtSignonError));

                    return;
            }
        }
        catch (InterruptedException ix)
        {
			etUsername.setEnabled(true);
			etPassword.setEnabled(true);
			etUsername.setText("");
			etPassword.setText("");
			tvResponseValue.setTextColor(Color.RED);
			tvResponseValue.setText(super.getString(R.string.txtSignonError));

            return;
        }
        catch (ExecutionException ex)
        {
			etUsername.setEnabled(true);
			etPassword.setEnabled(true);
			etUsername.setText("");
			etPassword.setText("");
			tvResponseValue.setTextColor(Color.RED);
			tvResponseValue.setText(super.getString(R.string.txtSignonError));

            return;
        }
    }
}
