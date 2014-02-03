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
 * OnlineResetActivity.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
import android.view.View;
import android.os.Bundle;
import android.app.Activity;
import android.text.InputType;
import android.content.Intent;
import android.graphics.Color;
import android.widget.EditText;
import android.widget.TextView;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang.StringUtils;
import java.util.concurrent.ExecutionException;

import com.cws.esolutions.android.Constants;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.android.tasks.OnlineResetTask;
import com.cws.esolutions.android.enums.ResetRequestType;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.processors.dto.AuthenticationData;
import com.cws.esolutions.security.processors.dto.AccountResetResponse;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 * @see android.app.Activity
 */
public class OnlineResetActivity extends Activity
{
    private ResetRequestType resetType = null;

    private static final String CNAME = OnlineResetActivity.class.getName();
    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    @Override
    public void onCreate(final Bundle bundle)
    {
        final String methodName = OnlineResetActivity.CNAME + "#onCreate(final Bundle bundle)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Bundle: {}", bundle);
        }

        super.onCreate(bundle);
        super.setTitle(R.string.mainTitle);
        super.setContentView(R.layout.onlinereset);
        this.resetType = (ResetRequestType) super.getIntent().getExtras().get("resetType");

        final TextView tvRequest = (TextView) super.findViewById(R.id.tvRequestInput);
        final EditText etRequest = (EditText) super.findViewById(R.id.etRequestInput);

        if (DEBUG)
        {
            DEBUGGER.debug("ResetRequestType: {}", this.resetType);
            DEBUGGER.debug("TextView: {}", tvRequest);
            DEBUGGER.debug("EditText: {}", etRequest);
        }

        switch (this.resetType)
        {
            case USERNAME:
                tvRequest.setText(R.string.strEmailAddr);
                tvRequest.setVisibility(View.VISIBLE);
                etRequest.setHint(R.string.strEmailAddr);
                etRequest.setVisibility(View.VISIBLE);
				etRequest.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

                return;
            case PASSWORD:
                tvRequest.setText(R.string.strUsername);
                tvRequest.setVisibility(View.VISIBLE);
                etRequest.setHint(R.string.strUsername);
                etRequest.setVisibility(View.VISIBLE);

                return;
            default:
                this.startActivity(new Intent(this, LoginActivity.class));
                super.finish();

                return;
        }
    }

    @Override
    public void onBackPressed()
    {
        final String methodName = OnlineResetActivity.CNAME + "#onBackPressed()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        // do signout here
        this.startActivity(new Intent(this, LoginActivity.class));
        super.finish();
    }

    public void submitOnlineReset(final View view)
    {
        final String methodName = OnlineResetActivity.CNAME + "#executeSubmission(final View view)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("View: {}", view);
        }

        UserAccount userAccount = null;
        AccountResetResponse response = null;
        AuthenticationData userSecurity = null;

        final OnlineResetTask task = new OnlineResetTask(this);
        final TextView tvRequest = (TextView) super.findViewById(R.id.tvRequestInput);
        final EditText etRequest = (EditText) super.findViewById(R.id.etRequestInput);
        final EditText etSecQuesTwo = (EditText) super.findViewById(R.id.secQuestionTwo);
        final TextView tvSecQuesOne = (TextView) super.findViewById(R.id.tvSecQuestionOne);
        final TextView tvSecQuesTwo = (TextView) super.findViewById(R.id.tvSecQuestionTwo);
        final EditText etSecQuesOne = (EditText) super.findViewById(R.id.etSecQuestionOne);
        final TextView tvResponseValue = (TextView) super.findViewById(R.id.tvResponseValue);

        if (DEBUG)
        {
            DEBUGGER.debug("OnlineResetTask: {}", task);
            DEBUGGER.debug("TextView: {}", tvRequest);
            DEBUGGER.debug("EditText: {}", etRequest);
            DEBUGGER.debug("EditText: {}", etSecQuesTwo);
            DEBUGGER.debug("TextView: {}", tvSecQuesOne);
            DEBUGGER.debug("TextView: {}", tvSecQuesTwo);
            DEBUGGER.debug("EditText: {}", etSecQuesOne);
            DEBUGGER.debug("TextView: {}", tvResponseValue);
        }

        if (StringUtils.isEmpty(etRequest.getText().toString()))
        {
            etRequest.setEnabled(true);
            etRequest.setText("");
            tvResponseValue.setTextColor(Color.RED);
            tvResponseValue.setText(super.getString(R.string.txtSignonError));

            return;
        }

        try
        {
            switch (this.resetType)
            {
                case USERNAME:
                    task.execute(this.resetType.name(), etRequest.getText().toString());

                    if (task.isCancelled())
                    {
                        tvResponseValue.setTextColor(Color.RED);
                        tvResponseValue.setText(super.getString(R.string.txtSignonError));

                        if (DEBUG)
                        {
                            DEBUGGER.debug("TextView: {}", tvResponseValue);
                        }

                        return;
                    }

                    response = task.get();

                    if (response.getRequestStatus() != SecurityRequestStatus.SUCCESS)
                    {
                        tvResponseValue.setTextColor(Color.RED);
                        tvResponseValue.setText(super.getString(R.string.txtSignonError));

                        return;
                    }

                    etRequest.setText("");
                    tvResponseValue.setTextColor(Color.BLUE);
                    tvResponseValue.setText(String.format(super.getString(R.string.strUserResponse, response.getUserAccount().getUsername())));

                    return;
                case PASSWORD:
                    task.execute(this.resetType.name(), etRequest.getText().toString());

                    if (task.isCancelled())
                    {
                        etRequest.setEnabled(true);
                        etRequest.setText("");
                        tvResponseValue.setTextColor(Color.RED);
                        tvResponseValue.setText(super.getString(R.string.txtSignonError));

                        if (DEBUG)
                        {
                            DEBUGGER.debug("TextView: {}", tvResponseValue);
                        }

                        return;
                    }

                    response = task.get();

                    if (response.getRequestStatus() != SecurityRequestStatus.SUCCESS)
                    {
                        etRequest.setEnabled(true);
                        etRequest.setText("");
                        tvResponseValue.setTextColor(Color.RED);
                        tvResponseValue.setText(super.getString(R.string.txtSignonError));

                        return;
                    }

                    UserAccount account = response.getUserAccount();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("UserAccount: {}", account);
                    }

                    super.getIntent().getExtras().putSerializable(Constants.USER_DATA, userAccount);

                    userSecurity = response.getUserSecurity();

                    tvRequest.setVisibility(View.GONE);
                    etRequest.setVisibility(View.GONE);

                    tvSecQuesOne.setText(userSecurity.getSecQuestionOne());
                    tvSecQuesTwo.setText(userSecurity.getSecQuestionTwo());
                    tvSecQuesOne.setVisibility(View.VISIBLE);
                    tvSecQuesTwo.setVisibility(View.VISIBLE);
                    etSecQuesOne.setVisibility(View.VISIBLE);
                    etSecQuesTwo.setVisibility(View.VISIBLE);

                    return;
                case QUESTIONS:
                    task.execute(this.resetType.name(), 
                        ((UserAccount) super.getIntent().getExtras().getSerializable(Constants.USER_DATA)).getUsername(),
                        etSecQuesOne.getText().toString(), etSecQuesTwo.getText().toString());

                    if (task.isCancelled())
                    {
                        tvSecQuesOne.setVisibility(View.GONE);
                        tvSecQuesTwo.setVisibility(View.GONE);
                        etSecQuesOne.setVisibility(View.GONE);
                        etSecQuesTwo.setVisibility(View.GONE);

                        etRequest.setEnabled(true);
                        etRequest.setText("");
                        tvResponseValue.setTextColor(Color.RED);
                        tvResponseValue.setText(super.getString(R.string.txtSignonError));

                        if (DEBUG)
                        {
                            DEBUGGER.debug("TextView: {}", tvResponseValue);
                        }

                        return;
                    }

                    response = task.get();

                    if (response.getRequestStatus() != SecurityRequestStatus.SUCCESS)
                    {
                        etRequest.setEnabled(true);
                        etRequest.setText("");
                        tvResponseValue.setTextColor(Color.RED);
                        tvResponseValue.setText(super.getString(R.string.txtSignonError));

                        return;
                    }

                    userAccount = response.getUserAccount();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("UserAccount: {}", userAccount);
                    }

                    userSecurity = response.getUserSecurity();

                    tvSecQuesOne.setText(userSecurity.getSecQuestionOne());
                    tvSecQuesTwo.setText(userSecurity.getSecQuestionTwo());
                    tvSecQuesOne.setVisibility(View.VISIBLE);
                    tvSecQuesTwo.setVisibility(View.VISIBLE);
                    etSecQuesOne.setVisibility(View.VISIBLE);
                    etSecQuesTwo.setVisibility(View.VISIBLE);

                    return;
                default:
                    etRequest.setEnabled(true);
                    etRequest.setText("");
                    tvResponseValue.setTextColor(Color.RED);
                    tvResponseValue.setText(super.getString(R.string.txtSignonError));

                    return;
            }
        }
        catch (InterruptedException ix)
        {
            etRequest.setEnabled(true);
            etRequest.setText("");
            tvResponseValue.setTextColor(Color.RED);
            tvResponseValue.setText(super.getString(R.string.txtSignonError));

            return;
        }
        catch (ExecutionException ex)
        {
            etRequest.setEnabled(true);
            etRequest.setText("");
            tvResponseValue.setTextColor(Color.RED);
            tvResponseValue.setText(super.getString(R.string.txtSignonError));

            return;
        }
    }
}
