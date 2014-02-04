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
 * MainActivity.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.app.AlertDialog;
import org.slf4j.LoggerFactory;
import java.lang.InterruptedException;
import android.content.DialogInterface;
import java.util.concurrent.ExecutionException;

import com.cws.esolutions.android.Constants;
import com.cws.esolutions.android.ui.LoginActivity;
import com.cws.esolutions.android.tasks.CoreServiceLoader;
import com.cws.esolutions.android.tasks.ApplicationLoader;
import com.cws.esolutions.android.tasks.SecurityServiceLoader;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 * @see android.app.Activity
 */
public class MainActivity extends Activity
{
    private static final String CNAME = MainActivity.class.getName();
    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.DEBUGGER + MainActivity.class.getSimpleName());

    @Override
    public void onCreate(final Bundle bundle)
    {
        final String methodName = MainActivity.CNAME + "#onCreate(final Bundle bundle)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Bundle: {}", bundle);
        }

        super.onCreate(bundle);
        super.setTitle(R.string.mainTitle);
        super.setContentView(R.layout.main);

        final ApplicationLoader loader = new ApplicationLoader(this);
        loader.execute();

        final SecurityServiceLoader secLoader = new SecurityServiceLoader(this);
        secLoader.execute();

        final CoreServiceLoader coreLoader = new CoreServiceLoader(this);
        coreLoader.execute();

        try
        {
            if ((loader.isCancelled()) || (!(loader.get())))
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setMessage("An error occurred while initializing the application. Cannot continue.")
                    .setCancelable(false)
                    .setNeutralButton("Exit",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(final DialogInterface dialogInterface, final int which)
                            {
                                MainActivity.this.finish();
                            }
                        });

                AlertDialog error = builder.create();
                error.show();

                return;
            }

            this.startActivity(new Intent(this, LoginActivity.class));
            super.finish();
        }
        catch (InterruptedException ix)
        {
            ERROR_RECORDER.error(ix.getMessage(), ix);

            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage("An error occurred while initializing the application. Cannot continue.")
                .setCancelable(false)
                .setNeutralButton("Exit",
                    new DialogInterface.OnClickListener()
                    {
                        public void onClick(final DialogInterface dialogInterface, final int which)
                        {
                            MainActivity.this.finish();
                        }
                    });

            AlertDialog error = builder.create();
            error.show();

            return;
        }
        catch (ExecutionException ex)
        {
            ERROR_RECORDER.error(ex.getMessage(), ex);

            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage("An error occurred while initializing the application. Cannot continue.")
                .setCancelable(false)
                .setNeutralButton("Exit",
                    new DialogInterface.OnClickListener()
                    {
                        public void onClick(final DialogInterface dialogInterface, final int which)
                        {
                            MainActivity.this.finish();
                        }
                    });

            AlertDialog error = builder.create();
            error.show();

            return;
        }
    }

    @Override
    public void onBackPressed()
    {
        final String methodName = MainActivity.CNAME + "#onBackPressed()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        // do signout here
        super.finish();

        return;
    }
}
