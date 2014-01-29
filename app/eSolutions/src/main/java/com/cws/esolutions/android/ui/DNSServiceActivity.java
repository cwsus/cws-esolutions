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
 * DNSServiceActivity.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;

import android.view.Menu;
import android.view.View;
import android.os.Bundle;
import android.app.Activity;
import android.view.MenuItem;
import android.content.Intent;
import android.content.Context;

import org.slf4j.LoggerFactory;

import android.widget.EditText;
import android.widget.TextView;
import android.view.inputmethod.InputMethodManager;

import com.cws.esolutions.android.Constants;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.android.tasks.DNSRequestTask;

public class DNSServiceActivity extends Activity
{
    private static final String CNAME = DNSServiceActivity.class.getName();
    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    @Override
    public void onCreate(final Bundle bundle)
    {
        final String methodName = DNSServiceActivity.CNAME + "#onCreate(final Bundle bundle)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Bundle: {}", bundle);
        }

        super.onCreate(bundle);
        super.setTitle(R.string.dnsServiceTitle);
        super.setContentView(R.layout.dnsservice);

        final UserAccount userAccount = (UserAccount) super.getIntent().getExtras().getSerializable(Constants.USER_DATA);

        if (DEBUG)
        {
            DEBUGGER.debug("UserAccount: {}", userAccount);
        }

        if (userAccount == null)
        {
            // no user, die
            this.startActivity(new Intent(this, LoginActivity.class));
            super.finish();
        }
        else
        {
            final EditText hostName = (EditText) findViewById(R.id.etHostName);
            final EditText serverName = (EditText) findViewById(R.id.etDNSServer);

            if (DEBUG)
            {
                DEBUGGER.debug("EditText: {}", hostName);
                DEBUGGER.debug("EditText: {}", serverName);
            }            
        }
    }

    public void executeDnsLookup(final View view)
    {
        final String methodName = DNSServiceActivity.CNAME + "#executeDnsLookup(final View view)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("View: {}", view);
        }

        final EditText hostName = (EditText) super.findViewById(R.id.etHostName);
        final EditText serverName = (EditText) super.findViewById(R.id.etDNSServer);
        final DNSRequestTask dnsRequest = new DNSRequestTask(DNSServiceActivity.this);

        if (DEBUG)
        {
            DEBUGGER.debug("EditText: {}", hostName);
            DEBUGGER.debug("EditText: {}", serverName);
            DEBUGGER.debug("DNSRequestTask: {}", dnsRequest);
        }

        InputMethodManager imm = (InputMethodManager) super.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(serverName.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);

        dnsRequest.execute(hostName.getText().toString(), serverName.getText().toString());

        if (dnsRequest.isCancelled())
        {
            TextView resultView = new TextView(this);
            resultView.setText("No network connection is available.");

            if (DEBUG)
            {
                DEBUGGER.debug("TextView: {}", resultView);
            }

            super.setContentView(resultView);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu)
    {
        final String methodName = DNSServiceActivity.CNAME + "#onCreateOptionsMenu(final menu menu)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Menu: {}", menu);
        }

        super.getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item)
    {
        final String methodName = DNSServiceActivity.CNAME + "#onOptionsItemSelected(final MenuItem item)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("MenuItem: {}", item);
        }

        Intent intent = null;

        final UserAccount userAccount = (UserAccount) super.getIntent().getExtras().getSerializable(Constants.USER_DATA);

        if (DEBUG)
        {
            DEBUGGER.debug("UserAccount: {}", userAccount);
        }

        switch (item.getItemId())
        {
            case R.id.applicationManagement:
                intent = new Intent(this, ApplicationManagementActivity.class);
                intent.putExtra(Constants.USER_DATA, userAccount);

                if (DEBUG)
                {
                    DEBUGGER.debug("Intent: {}", intent);
                }

                this.startActivity(intent);

                super.finish();

                return true;
            case R.id.home:
                intent = new Intent(this, HomeActivity.class);
                intent.putExtra(Constants.USER_DATA, userAccount);

                if (DEBUG)
                {
                    DEBUGGER.debug("Intent: {}", intent);
                }

                this.startActivity(intent);

                super.finish();

                return true;
            case R.id.serviceManagement:
                intent = new Intent(this, ServiceManagementActivity.class);
                intent.putExtra(Constants.USER_DATA, userAccount);

                if (DEBUG)
                {
                    DEBUGGER.debug("Intent: {}", intent);
                }

                this.startActivity(intent);

                super.finish();

                return true;
            case R.id.serviceMessaging:
                intent = new Intent(this, ServiceMessagingActivity.class);
                intent.putExtra(Constants.USER_DATA, userAccount);

                if (DEBUG)
                {
                    DEBUGGER.debug("Intent: {}", intent);
                }

                this.startActivity(intent);

                super.finish();

                return true;
            case R.id.systemManagement:
                intent = new Intent(this, SystemManagementActivity.class);
                intent.putExtra(Constants.USER_DATA, userAccount);

                if (DEBUG)
                {
                    DEBUGGER.debug("Intent: {}", intent);
                }

                this.startActivity(intent);

                super.finish();

                return true;
            case R.id.userAccount:
                intent = new Intent(this, UserAccountActivity.class);
                intent.putExtra(Constants.USER_DATA, userAccount);

                if (DEBUG)
                {
                    DEBUGGER.debug("Intent: {}", intent);
                }

                this.startActivity(intent);

                super.finish();

                return true;
            case R.id.userManagement:
                intent = new Intent(this, UserManagementActivity.class);
                intent.putExtra(Constants.USER_DATA, userAccount);

                if (DEBUG)
                {
                    DEBUGGER.debug("Intent: {}", intent);
                }

                this.startActivity(intent);

                super.finish();

                return true;
            case R.id.virtualManager:
                intent = new Intent(this, VirtualManagerActivity.class);
                intent.putExtra(Constants.USER_DATA, userAccount);

                if (DEBUG)
                {
                    DEBUGGER.debug("Intent: {}", intent);
                }

                this.startActivity(intent);

                super.finish();

                return true;
            case R.id.signout:
                super.getIntent().removeExtra(Constants.USER_DATA);
                super.getIntent().getExtras().remove(Constants.USER_DATA);

                intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                if (DEBUG)
                {
                    DEBUGGER.debug("Intent: {}", intent);
                }

                this.startActivity(intent);

                super.finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
