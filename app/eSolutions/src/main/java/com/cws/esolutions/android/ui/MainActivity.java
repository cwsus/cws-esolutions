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
import org.slf4j.Logger;
import android.view.Menu;
import android.os.Bundle;
import android.app.Activity;
import java.net.InetAddress;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.TextView;
import org.slf4j.LoggerFactory;
import java.net.UnknownHostException;
import android.text.method.LinkMovementMethod;

import com.cws.esolutions.android.Constants;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.dto.ServiceMessage;
import com.cws.esolutions.core.processors.dto.MessagingRequest;
import com.cws.esolutions.core.processors.dto.MessagingResponse;
import com.cws.esolutions.core.processors.enums.DataServicesStatus;
import com.cws.esolutions.core.processors.interfaces.IMessagingProcessor;
import com.cws.esolutions.core.processors.impl.ServiceMessagingProcessorImpl;
import com.cws.esolutions.core.processors.exception.MessagingServiceException;
/**
 * eSolutions
 * com.cws.esolutions.core.ui
 * MainActivity.java
 *
 * TODO: Add class description
 *
 * $Id: MainActivity.java 2289 2013-01-03 21:03:37Z kmhuntly@gmail.com $
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
public class MainActivity extends Activity
{
    private String methodName = null;

    private static final String CNAME = MainActivity.class.getName();
    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger WARN_RECORDER = LoggerFactory.getLogger(Constants.WARN_LOGGER);

    public void onCreate(final Bundle bundle)
    {
        methodName = CNAME + "#onCreate(final Bundle bundle)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Bundle: ", bundle);
        }

        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        setTitle(R.string.mainTitle);

        final UserAccount userAccount = (UserAccount) getIntent().getExtras().getSerializable(Constants.USER_DATA);

        if (userAccount == null)
        {
            // no user, die
            this.startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        else
        {
            try
            {
                final TextView messageView = (TextView) findViewById(R.id.tvMessageList);
                TextView showWelcome = (TextView) findViewById(R.id.tvShowWelcome);
                showWelcome.setText("Welcome, " + userAccount.getDisplayName());

                RequestHostInfo reqInfo = new RequestHostInfo();

                try
                {
                    reqInfo.setHostName(InetAddress.getLocalHost().getHostName());
                    reqInfo.setHostAddress(InetAddress.getLocalHost().getHostAddress());
                }
                catch (UnknownHostException uhx)
                {
                    WARN_RECORDER.warn(uhx.getMessage(), uhx);

                    reqInfo.setHostName("Android");
                    reqInfo.setHostAddress("Android");
                }

                if (DEBUG)
                {
                    DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
                }

                MessagingRequest request = new MessagingRequest();
                request.setRequestInfo(reqInfo);
                request.setUserAccount(userAccount);
                request.setServiceId("some id goes here");

                if (DEBUG)
                {
                    DEBUGGER.debug("MessagingRequest: {}", request);
                }

                final IMessagingProcessor msgProcessor = new ServiceMessagingProcessorImpl();

                MessagingResponse msgResponse = msgProcessor.showMessages(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("MessagingResponse: ", msgResponse);
                }

                if (msgResponse.getRequestStatus() == DataServicesStatus.SUCCESS)
                {
                    List<ServiceMessage> messageList = msgResponse.getSvcMessages();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Message list: ", messageList);
                    }

                    for (ServiceMessage message : messageList)
                    {
                        if (DEBUG)
                        {
                            DEBUGGER.debug("Message: ", message);
                        }

                        messageView.setMovementMethod(LinkMovementMethod.getInstance());

                        messageView.append("Author: <a href=\"mailto:" + message.getAuthorEmail() +
                                "?subject=Contact Message: " +
                                message.getMessageId());
                        messageView.append("Submission date: " + message.getSubmitDate() + "\n");
                        messageView.append("ID: " + message.getMessageId() + "\n");
                        messageView.append(message.getMessageText() + "\n\n");
                    }
                }
            }
            catch (MessagingServiceException msx)
            {
                // don't do anything
            }
        }
    }

    public void onBackPressed()
    {
        methodName = CNAME + "#onBackPressed()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        // do signout here
        this.startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public boolean onCreateOptionsMenu(final Menu menu)
    {
        methodName = CNAME + "#onCreateOptionsMenu(final Menu menu)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Menu: ", menu);
        }

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(final MenuItem item)
    {
        methodName = CNAME + "#onOptionsItemSelected(final MenuItem item)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("MenuItem: ", item);
        }

        final UserAccount userAccount = (UserAccount) getIntent().getExtras().getSerializable(Constants.USER_DATA);

        switch (item.getItemId())
        {
            case R.id.menu_signout:
                getIntent().removeExtra(Constants.USER_DATA);
                getIntent().getExtras().remove(Constants.USER_DATA);

                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                this.startActivity(intent);

                finish();

                break;
            case R.id.menu_dnssvc:
                Intent dnsIntent = new Intent(this, DNSActivity.class);
                dnsIntent.putExtra(Constants.USER_DATA, userAccount);
                this.startActivity(dnsIntent);

                break;
            case R.id.menu_sysmgt:
                Intent sysmIntent = new Intent(this, DNSActivity.class);
                sysmIntent.putExtra(Constants.USER_DATA, userAccount);
                this.startActivity(sysmIntent);

                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }
}
