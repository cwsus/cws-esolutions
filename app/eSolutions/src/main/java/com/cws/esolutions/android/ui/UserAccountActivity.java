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
 * UserAccountActivity.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
import android.view.Menu;
import android.os.Bundle;
import android.app.Activity;
import android.view.MenuItem;
import android.content.Intent;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.android.Constants;
import com.cws.esolutions.security.dto.UserAccount;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 * @see android.app.Activity
 */
public class UserAccountActivity extends Activity
{
    private static final String CNAME = UserAccountActivity.class.getName();
    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    @Override
    public void onCreate(final Bundle bundle)
    {
        final String methodName = UserAccountActivity.CNAME + "#onCreate(final Bundle bundle)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Bundle: {}", bundle);
        }

        super.onCreate(bundle);
        super.setTitle(R.string.userAccountTitle);
        super.setContentView(R.layout.useraccount);

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
                        // do stuff

                        return;
                    default:
                        super.getIntent().removeExtra(Constants.USER_DATA);
                        super.getIntent().getExtras().remove(Constants.USER_DATA);
                        super.startActivity(new Intent(UserAccountActivity.this, LoginActivity.class));
                        super.finish();

                        return;
                }
            }
        }
        else
        {
            super.getIntent().getExtras().remove(Constants.USER_DATA);
            super.startActivity(new Intent(UserAccountActivity.this, LoginActivity.class));
            super.finish();
        }
    }

    @Override
    public void onBackPressed()
    {
        final String methodName = UserAccountActivity.CNAME + "#onBackPressed()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        super.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu)
    {
        final String methodName = UserAccountActivity.CNAME + "#onCreateOptionsMenu(final Menu menu)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Menu: {}", menu);
        }

        super.getMenuInflater().inflate(R.menu.useraccount, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item)
    {
        final String methodName = UserAccountActivity.CNAME + "#onOptionsItemSelected(final MenuItem item)";

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
            case R.id.email:
                return true;
            case R.id.contact:
                return true;
            case R.id.password:
                return true;
            case R.id.security:
                return true;
            case R.id.keys:
                return true;
            case R.id.otp:
                return true;
            case R.id.signout:
                super.getIntent().removeExtra(Constants.USER_DATA);
                super.getIntent().getExtras().remove(Constants.USER_DATA);

                intent = new Intent(UserAccountActivity.this, LoginActivity.class);
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
