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
 * eSolutions-app
 * com.cws.us.esolutions.ui
 * VirtualManagerActivity.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.List;
import org.slf4j.Logger;
import android.view.View;
import android.os.Bundle;
import android.app.Activity;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.TextView;
import org.slf4j.LoggerFactory;
import android.widget.RelativeLayout;
import java.util.concurrent.TimeUnit;
import android.view.View.OnClickListener;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.ExecutionException;

import com.cws.esolutions.android.Constants;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.core.processors.dto.Server;
import com.cws.esolutions.android.ApplicationServiceBean;
import com.cws.esolutions.core.processors.enums.ServerType;
import com.cws.esolutions.android.enums.ServerManagementType;
import com.cws.esolutions.android.tasks.ServerManagementTask;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.core.processors.dto.ServerManagementResponse;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 * @see android.app.Activity
 */
public class VirtualManagerActivity extends Activity
{
    private static final ApplicationServiceBean bean = ApplicationServiceBean.getInstance();

    private static final String CNAME = VirtualManagerActivity.class.getName();
    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    @Override
    public void onCreate(final Bundle bundle)
    {
        final String methodName = VirtualManagerActivity.CNAME + "#onCreate(final Bundle bundle)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Bundle: {}", bundle);
        }

        super.onCreate(bundle);
        super.setContentView(R.layout.virtualmanager);
        super.setTitle(R.string.virtualManagerTitle);

        int x = 0;

        final TextView resultView = (TextView) super.findViewById(R.id.tvResponseValue);
        final RelativeLayout layout = (RelativeLayout) super.findViewById(R.layout.virtualmanager);

        if (DEBUG)
        {
            DEBUGGER.debug("RelativeLayout: {}", layout);
        }

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
                        try
                        {
                            ServerManagementTask task = new ServerManagementTask(VirtualManagerActivity.this);
                            task.execute(ServerType.VIRTUALHOST.name());

                            ServerManagementResponse response = (ServerManagementResponse) task.get(bean.getTaskTimeout(), TimeUnit.SECONDS);

                            if (DEBUG)
                            {
                                DEBUGGER.debug("ServerManagementResponse: {}", response);
                            }

                            if ((response == null) || (response.getRequestStatus() != CoreServicesStatus.SUCCESS))
                            {
                                resultView.setText(R.string.errorMessage);

                                return;
                            }

                            List<Server> serverList = response.getServerList();

                            if (DEBUG)
                            {
                                DEBUGGER.debug("List<Server>: {}", serverList);
                            }

                            for (Server server : serverList)
                            {
                                if (DEBUG)
                                {
                                    DEBUGGER.debug("Server: {}", server);
                                }

                                final TextView tName = new TextView(VirtualManagerActivity.this);
                                tName.setText(server.getOperHostName());

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("TextView: {}", tName);
                                }

                                final TextView tGuid = new TextView(VirtualManagerActivity.this);
                                tGuid.setText(R.string.strManageGuest);
                                tGuid.setHint(server.getServerGuid());
                                tGuid.setClickable(true);
                                tGuid.setOnClickListener(new OnClickListener()
                                {
                                    @Override
                                    public void onClick(final View view)
                                    {
                                        final String methodName = VirtualManagerActivity.CNAME + "#onClick(final OnClickListener request)";
                                        
                                        if (DEBUG)
                                        {
                                        	DEBUGGER.debug(methodName);
                                            DEBUGGER.debug("Value: ", view);
                                        }

                                        try
                                        {
                                            ServerManagementTask task = new ServerManagementTask(VirtualManagerActivity.this);
                                            task.execute(ServerManagementType.RETRIEVE.name(), tGuid.getHint().toString());

                                            ServerManagementResponse response = (ServerManagementResponse) task.get(bean.getTaskTimeout(), TimeUnit.SECONDS);

                                            if (DEBUG)
                                            {
                                                DEBUGGER.debug("ServerManagementResponse: {}", response);
                                            }

                                            if ((response != null) && (response.getRequestStatus() == CoreServicesStatus.SUCCESS))
                                            {
                                                Server resServer = response.getServer();

                                                if (DEBUG)
                                                {
                                                    DEBUGGER.debug("Server: {}", resServer);
                                                }

                                                TextView serverType = new TextView(VirtualManagerActivity.this);
                                                serverType.setText(resServer.getServerType().name());

                                                TextView serverStatus = new TextView(VirtualManagerActivity.this);
                                                serverStatus.setText(resServer.getServerStatus().name());

                                                TextView serverRegion = new TextView(VirtualManagerActivity.this);
                                                serverRegion.setText(resServer.getServerRegion().name());

                                                TextView operHostName = new TextView(VirtualManagerActivity.this);
                                                operHostName.setText(resServer.getOperHostName());

                                                TextView operIpAddress = new TextView(VirtualManagerActivity.this);
                                                operIpAddress.setText(resServer.getOperIpAddress());

                                                TextView osName = new TextView(VirtualManagerActivity.this);
                                                osName.setText(resServer.getOsName());

                                                TextView serialNumber = new TextView(VirtualManagerActivity.this);
                                                serialNumber.setText(resServer.getSerialNumber());

                                                TextView cpuCount = new TextView(VirtualManagerActivity.this);
                                                cpuCount.setText(resServer.getCpuCount());

                                                TextView cpuType = new TextView(VirtualManagerActivity.this);
                                                cpuType.setText(resServer.getCpuType());

                                                TextView installedMemory = new TextView(VirtualManagerActivity.this);
                                                installedMemory.setText(resServer.getInstalledMemory());

                                                TextView service = new TextView(VirtualManagerActivity.this);
                                                service.setText(resServer.getService().getName());

                                                TextView serverRack = new TextView(VirtualManagerActivity.this);
                                                serverRack.setText(resServer.getServerRack());

                                                TextView domainName = new TextView(VirtualManagerActivity.this);
                                                domainName.setText(resServer.getDomainName());

                                                TextView serverModel = new TextView(VirtualManagerActivity.this);
                                                serverModel.setText(resServer.getServerModel());

                                                TextView rackPosition = new TextView(VirtualManagerActivity.this);
                                                rackPosition.setText(resServer.getRackPosition());

                                                TextView networkPartition = new TextView(VirtualManagerActivity.this);
                                                networkPartition.setText(resServer.getNetworkPartition().name());

                                                TextView mgmtHostName = new TextView(VirtualManagerActivity.this);
                                                mgmtHostName.setText(resServer.getMgmtHostName());

                                                TextView mgmtIpAddress = new TextView(VirtualManagerActivity.this);
                                                mgmtIpAddress.setText(resServer.getMgmtIpAddress());

                                                TextView bkHostName = new TextView(VirtualManagerActivity.this);
                                                bkHostName.setText(resServer.getBkHostName());

                                                TextView bkIpAddress = new TextView(VirtualManagerActivity.this);
                                                bkIpAddress.setText(resServer.getBkIpAddress());

                                                TextView nasHostName = new TextView(VirtualManagerActivity.this);
                                                nasHostName.setText(resServer.getNasHostName());

                                                TextView nasIpAddress = new TextView(VirtualManagerActivity.this);
                                                nasIpAddress.setText(resServer.getNasIpAddress());

                                                TextView natAddress = new TextView(VirtualManagerActivity.this);
                                                natAddress.setText(resServer.getNatAddress());

                                                TextView serverComments = new TextView(VirtualManagerActivity.this);
                                                serverComments.setText(resServer.getServerComments());

                                                TextView assignedEngineer = new TextView(VirtualManagerActivity.this);
                                                assignedEngineer.setText(resServer.getAssignedEngineer().getDisplayName());

                                                if (DEBUG)
                                                {
                                                    DEBUGGER.debug("TextView: {}", serverType);
                                                    DEBUGGER.debug("TextView: {}", serverStatus);
                                                    DEBUGGER.debug("TextView: {}", serverRegion);
                                                    DEBUGGER.debug("TextView: {}", operHostName);
                                                    DEBUGGER.debug("TextView: {}", operIpAddress);
                                                    DEBUGGER.debug("TextView: {}", osName);
                                                    DEBUGGER.debug("TextView: {}", serialNumber);
                                                    DEBUGGER.debug("TextView: {}", cpuCount);
                                                    DEBUGGER.debug("TextView: {}", cpuType);
                                                    DEBUGGER.debug("TextView: {}", installedMemory);
                                                    DEBUGGER.debug("TextView: {}", service);
                                                    DEBUGGER.debug("TextView: {}", serverRack);
                                                    DEBUGGER.debug("TextView: {}", domainName);
                                                    DEBUGGER.debug("TextView: {}", serverModel);
                                                    DEBUGGER.debug("TextView: {}", rackPosition);
                                                    DEBUGGER.debug("TextView: {}", networkPartition);
                                                    DEBUGGER.debug("TextView: {}", mgmtHostName);
                                                    DEBUGGER.debug("TextView: {}", mgmtIpAddress);
                                                    DEBUGGER.debug("TextView: {}", bkHostName);
                                                    DEBUGGER.debug("TextView: {}", bkIpAddress);
                                                    DEBUGGER.debug("TextView: {}", nasHostName);
                                                    DEBUGGER.debug("TextView: {}", nasIpAddress);
                                                    DEBUGGER.debug("TextView: {}", natAddress);
                                                    DEBUGGER.debug("TextView: {}", serverComments);
                                                    DEBUGGER.debug("TextView: {}", assignedEngineer);
                                                }

                                                layout.removeView(tName);
                                                layout.removeView(tGuid);
                                                layout.addView(serverType);
                                                layout.addView(serverStatus);
                                                layout.addView(serverRegion);
                                                layout.addView(operHostName);
                                                layout.addView(operIpAddress);
                                                layout.addView(osName);
                                                layout.addView(serialNumber);
                                                layout.addView(cpuCount);
                                                layout.addView(cpuType);
                                                layout.addView(installedMemory);
                                                layout.addView(service);
                                                layout.addView(serverRack);
                                                layout.addView(domainName);
                                                layout.addView(serverModel);
                                                layout.addView(rackPosition);
                                                layout.addView(networkPartition);
                                                layout.addView(mgmtHostName);
                                                layout.addView(mgmtIpAddress);
                                                layout.addView(bkHostName);
                                                layout.addView(bkIpAddress);
                                                layout.addView(nasHostName);
                                                layout.addView(nasIpAddress);
                                                layout.addView(natAddress);
                                                layout.addView(serverComments);
                                                layout.addView(assignedEngineer);

                                                return;
                                            }
                                        }
                                        catch (TimeoutException tx)
                                        {
                                            resultView.setText(R.string.errorMessage);

                                            return;
                                        }
                                        catch (InterruptedException ix)
                                        {
                                            resultView.setText(R.string.errorMessage);

                                            return;
                                        }
                                        catch (ExecutionException ee)
                                        {
                                            resultView.setText(R.string.errorMessage);

                                            return;
                                        }
                                    }
                                });

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("TextView: {}", tGuid);
                                }

                                layout.addView(tName, x);
                                layout.addView(tGuid, x);

                                x++;
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

                        return;
                    default:
                        super.getIntent().removeExtra(Constants.USER_DATA);
                        super.getIntent().getExtras().remove(Constants.USER_DATA);
                        super.startActivity(new Intent(VirtualManagerActivity.this, LoginActivity.class));
                        super.finish();

                        return;
                }
            }
        }
        else
        {
            super.getIntent().getExtras().remove(Constants.USER_DATA);
            super.startActivity(new Intent(VirtualManagerActivity.this, LoginActivity.class));
            super.finish();
        }
    }

    @Override
    public void onBackPressed()
    {
        final String methodName = VirtualManagerActivity.CNAME + "#onBackPressed()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        super.finish();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item)
    {
        final String methodName = VirtualManagerActivity.CNAME + "#onOptionsItemSelected(final MenuItem item)";

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
                intent = new Intent(VirtualManagerActivity.this, ApplicationManagementActivity.class);
                intent.putExtra(Constants.USER_DATA, userAccount);

                if (DEBUG)
                {
                    DEBUGGER.debug("Intent: {}", intent);
                }

                super.startActivity(intent);
                super.finish();

                return true;
            case R.id.dnsService:
                intent = new Intent(VirtualManagerActivity.this, DNSServiceActivity.class);
                intent.putExtra(Constants.USER_DATA, userAccount);

                if (DEBUG)
                {
                    DEBUGGER.debug("Intent: {}", intent);
                }

                super.startActivity(intent);
                super.finish();

                return true;
            case R.id.home:
                intent = new Intent(VirtualManagerActivity.this, HomeActivity.class);
                intent.putExtra(Constants.USER_DATA, userAccount);

                if (DEBUG)
                {
                    DEBUGGER.debug("Intent: {}", intent);
                }

                super.startActivity(intent);
                super.finish();

                return true;
            case R.id.serviceManagement:
                intent = new Intent(VirtualManagerActivity.this, ServiceManagementActivity.class);
                intent.putExtra(Constants.USER_DATA, userAccount);

                if (DEBUG)
                {
                    DEBUGGER.debug("Intent: {}", intent);
                }

                super.startActivity(intent);
                super.finish();

                return true;
            case R.id.serviceMessaging:
                intent = new Intent(VirtualManagerActivity.this, ServiceMessagingActivity.class);
                intent.putExtra(Constants.USER_DATA, userAccount);

                if (DEBUG)
                {
                    DEBUGGER.debug("Intent: {}", intent);
                }

                super.startActivity(intent);
                super.finish();

                return true;
            case R.id.systemManagement:
                intent = new Intent(VirtualManagerActivity.this, SystemManagementActivity.class);
                intent.putExtra(Constants.USER_DATA, userAccount);

                if (DEBUG)
                {
                    DEBUGGER.debug("Intent: {}", intent);
                }

                super.startActivity(intent);
                super.finish();

                return true;
            case R.id.userAccount:
                intent = new Intent(VirtualManagerActivity.this, UserAccountActivity.class);
                intent.putExtra(Constants.USER_DATA, userAccount);

                if (DEBUG)
                {
                    DEBUGGER.debug("Intent: {}", intent);
                }
                super.startActivity(intent);
                super.finish();

                return true;
            case R.id.userManagement:
                intent = new Intent(VirtualManagerActivity.this, UserManagementActivity.class);
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

                intent = new Intent(VirtualManagerActivity.this, LoginActivity.class);
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
}
