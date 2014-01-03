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
/**
 * eSolutions
 * com.cws.esolutions.core.ui
 * DNSActivity.java
 *
 * TODO: Add class description
 *
 * $Id: DNSActivity.java 2289 2013-01-03 21:03:37Z kmhuntly@gmail.com $
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
public class DNSActivity extends Activity
{
    private String methodName = null;
    private EditText hostName = null;
    private EditText serverName = null;

    private static final String CNAME = DNSActivity.class.getName();
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
        setContentView(R.layout.activity_dns);
        setTitle(R.string.dnsTitle);

        final UserAccount userAccount = (UserAccount) getIntent().getExtras().getSerializable(Constants.USER_DATA);

        if (userAccount == null)
        {
            // no user, die
            this.startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        else
        {
            hostName = (EditText) findViewById(R.id.etHostName);
            serverName = (EditText) findViewById(R.id.etDNSServer);

            if (DEBUG)
            {
                DEBUGGER.debug("hostName: ", hostName);
                DEBUGGER.debug("serverName: ", serverName);
            }            
        }
    }

    /**
     * @author kh05451
     * @param view - the view
     */
    public void executeDnsLookup(final View view)
    {
        methodName = CNAME + "#executeDnsLookup(final View view)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("View: ", view);
        }

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(serverName.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);


        final DNSRequestTask dnsRequest = new DNSRequestTask(DNSActivity.this);

        dnsRequest.execute(hostName.getText().toString(), serverName.getText().toString());

        if (dnsRequest.isCancelled())
        {
            TextView resultView = new TextView(this);
            resultView.setText("No network connection is available.");
            setContentView(resultView);
        }
    }

    public boolean onCreateOptionsMenu(final Menu menu)
    {
        methodName = CNAME + "#onCreateOptionsMenu(final menu menu)";

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
