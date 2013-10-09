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
package com.cws.us.esolutions.ui;

import org.slf4j.Logger;
import android.os.Bundle;
import android.view.Menu;
import android.app.Activity;
import android.view.MenuItem;
import android.content.Intent;
import org.slf4j.LoggerFactory;

import com.cws.us.esolutions.Constants;
import com.cws.esolutions.security.dto.UserAccount;
/**
 * eSolutions
 * com.cws.us.esolutions.ui
 * KnowledgeBaseActivity.java
 *
 * TODO: Add class description
 *
 * $Id: KnowledgeBaseActivity.java 2289 2013-01-03 21:03:37Z kmhuntly@gmail.com $
 * $Author: kmhuntly@gmail.com $
 * $Date: 2013-01-03 16:03:37 -0500 (Thu, 03 Jan 2013) $
 * $Revision: 2289 $
 * @author kh05451
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Oct 31, 2012 4:05:56 PM
 *     Created.
 */
public class KnowledgeBaseActivity extends Activity
{
    private String methodName = null;

    private static final String CNAME = KnowledgeBaseActivity.class.getName();
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
        setContentView(R.layout.activity_login);
        setTitle(R.string.loginTitle);

        final UserAccount userAccount = (UserAccount) getIntent().getExtras().getSerializable(Constants.USER_DATA);

        if (userAccount == null)
        {
            // no user, die
            this.startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        else
        {
           // do stuff here
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
                getIntent().getExtras().remove(Constants.USER_DATA);
                getIntent().removeExtra(Constants.USER_DATA);

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
