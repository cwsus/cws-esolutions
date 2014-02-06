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
import org.slf4j.LoggerFactory;
import android.widget.EditText;
import android.widget.TextView;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.ExecutionException;

import com.cws.esolutions.android.Constants;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.android.tasks.DNSRequestTask;
import com.cws.esolutions.core.processors.dto.DNSRecord;
import com.cws.esolutions.android.ApplicationServiceBean;
import com.cws.esolutions.core.processors.dto.DNSServiceResponse;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 * @see android.app.Activity
 */
public class DNSServiceActivity extends Activity
{
    private static final ApplicationServiceBean bean = ApplicationServiceBean.getInstance();

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

        if ((super.getIntent().getExtras() != null) && (super.getIntent().getExtras().containsKey(Constants.USER_DATA)))
        {
            UserAccount userAccount = (UserAccount) super.getIntent().getExtras().getSerializable(Constants.USER_DATA);

            if (DEBUG)
            {
                DEBUGGER.debug("UserAccount: {}", userAccount);
            }

            if (userAccount != null)
            {
                switch (userAccount.getStatus())
                {
                    case SUCCESS:
                        return;
                    default:
                        super.getIntent().removeExtra(Constants.USER_DATA);
                        super.getIntent().getExtras().remove(Constants.USER_DATA);
                        super.startActivity(new Intent(DNSServiceActivity.this, LoginActivity.class));
                        super.finish();

                        return;
                }
            }
        }
        else
        {
            super.getIntent().getExtras().remove(Constants.USER_DATA);
            super.startActivity(new Intent(DNSServiceActivity.this, LoginActivity.class));
            super.finish();
        }
    }

    @Override
    public void onBackPressed()
    {
        final String methodName = DNSServiceActivity.CNAME + "#onBackPressed()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        super.finish();
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

        super.getMenuInflater().inflate(R.menu.main, menu);

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
                intent = new Intent(DNSServiceActivity.this, ApplicationManagementActivity.class);
                intent.putExtra(Constants.USER_DATA, userAccount);

                if (DEBUG)
                {
                    DEBUGGER.debug("Intent: {}", intent);
                }

                super.startActivity(intent);
                super.finish();

                return true;
            case R.id.home:
                intent = new Intent(DNSServiceActivity.this, HomeActivity.class);
                intent.putExtra(Constants.USER_DATA, userAccount);

                if (DEBUG)
                {
                    DEBUGGER.debug("Intent: {}", intent);
                }

                super.startActivity(intent);
                super.finish();

                return true;
            case R.id.serviceManagement:
                intent = new Intent(DNSServiceActivity.this, ServiceManagementActivity.class);
                intent.putExtra(Constants.USER_DATA, userAccount);

                if (DEBUG)
                {
                    DEBUGGER.debug("Intent: {}", intent);
                }

                super.startActivity(intent);
                super.finish();

                return true;
            case R.id.serviceMessaging:
                intent = new Intent(DNSServiceActivity.this, ServiceMessagingActivity.class);
                intent.putExtra(Constants.USER_DATA, userAccount);

                if (DEBUG)
                {
                    DEBUGGER.debug("Intent: {}", intent);
                }

                super.startActivity(intent);
                super.finish();

                return true;
            case R.id.systemManagement:
                intent = new Intent(DNSServiceActivity.this, SystemManagementActivity.class);
                intent.putExtra(Constants.USER_DATA, userAccount);

                if (DEBUG)
                {
                    DEBUGGER.debug("Intent: {}", intent);
                }

                super.startActivity(intent);
                super.finish();

                return true;
            case R.id.userAccount:
                intent = new Intent(DNSServiceActivity.this, UserAccountActivity.class);
                intent.putExtra(Constants.USER_DATA, userAccount);

                if (DEBUG)
                {
                    DEBUGGER.debug("Intent: {}", intent);
                }

                super.startActivity(intent);
                super.finish();

                return true;
            case R.id.userManagement:
                intent = new Intent(DNSServiceActivity.this, UserManagementActivity.class);
                intent.putExtra(Constants.USER_DATA, userAccount);

                if (DEBUG)
                {
                    DEBUGGER.debug("Intent: {}", intent);
                }

                super.startActivity(intent);
                super.finish();

                return true;
            case R.id.virtualManager:
                intent = new Intent(DNSServiceActivity.this, VirtualManagerActivity.class);
                intent.putExtra(Constants.USER_DATA, userAccount);

                if (DEBUG)
                {
                    DEBUGGER.debug("Intent: {}", intent);
                }

                super.startActivity(intent);
                super.finish();

                return true;
            case R.id.signout:
                super.getIntent().removeExtra(Constants.USER_DATA);
                super.getIntent().getExtras().remove(Constants.USER_DATA);

                intent = new Intent(DNSServiceActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                if (DEBUG)
                {
                    DEBUGGER.debug("Intent: {}", intent);
                }

                super.startActivity(intent);
                super.finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
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
        final TextView resultView = (TextView) super.findViewById(R.id.tvResponseValue);

        if (DEBUG)
        {
            DEBUGGER.debug("EditText: {}", hostName);
            DEBUGGER.debug("EditText: {}", serverName);
            DEBUGGER.debug("DNSRequestTask: {}", dnsRequest);
            DEBUGGER.debug("TextView: {}", resultView);
        }

        try
        {
            dnsRequest.execute(hostName.getText().toString(), serverName.getText().toString());

            if (dnsRequest.isCancelled())
            {
                resultView.setText(R.string.errorMessage);

                return;
            }

            DNSServiceResponse response = (DNSServiceResponse) dnsRequest.get(bean.getTaskTimeout(), TimeUnit.SECONDS);

            if (DEBUG)
            {
                DEBUGGER.debug("DNSServiceResponse: {}", response);
            }

            if ((response == null) || (response.getRequestStatus() != CoreServicesStatus.SUCCESS))
            {
                resultView.setText(R.string.errorMessage);

                return;
            }

            if ((response.getDnsRecords() != null) && (response.getDnsRecords().size() != 0))
            {
                // multiple records were returned
                for (DNSRecord record : response.getDnsRecords())
                {
                    if (DEBUG)
                    {
                        DEBUGGER.debug("DNSRecord: {}", record);
                    }

                    resultView.setText(record.toString());
                }
            }
            else if (response.getDnsRecord() != null)
            {
                resultView.setText(response.getDnsRecord().toString());
            }
            else
            {
                resultView.setText(R.string.errorMessage);
            }
        }
        catch (TimeoutException tx)
        {
            resultView.setText(R.string.errorMessage);
        }
        catch (InterruptedException ix)
        {
            resultView.setText(R.string.errorMessage);
        }
        catch (ExecutionException ee)
        {
            resultView.setText(R.string.errorMessage);
        }
    }
}
