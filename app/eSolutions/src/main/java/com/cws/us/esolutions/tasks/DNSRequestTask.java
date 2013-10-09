/**
 * Copyright (c) 2009 - 2012 By: CWS, Inc.
 * 
 * All rights reserved. These materials are confidential and
 * proprietary to CWS N.A and no part of these materials
 * should be reproduced, published in any form by any means,
 * electronic or mechanical, including photocopy or any information
 * storage or retrieval system not should the materials be
 * disclosed to third parties without the express written
 * authorization of CWS N.A.
 */
package com.cws.us.esolutions.tasks;

import java.util.List;
import org.slf4j.Logger;
import java.util.ArrayList;
import android.os.AsyncTask;
import android.app.Activity;
import android.graphics.Color;
import org.slf4j.LoggerFactory;
import android.widget.TextView;
import android.net.NetworkInfo;
import android.content.Context;
import android.net.ConnectivityManager;

import com.cws.us.esolutions.ui.R;
import com.cws.us.esolutions.Constants;
import com.cws.esolutions.core.processors.dto.DNSEntry;
import com.cws.esolutions.core.processors.enums.DNSRecordType;
import com.cws.esolutions.core.processors.enums.DNSRequestType;
import com.cws.esolutions.core.processors.dto.DNSServiceRequest;
import com.cws.esolutions.core.processors.dto.DNSServiceResponse;
import com.cws.esolutions.core.processors.impl.DNSServiceRequestImpl;
import com.cws.esolutions.core.processors.exception.DNSServiceException;
import com.cws.esolutions.core.processors.interfaces.IDNSServiceRequest;
/**
 * eSolutions
 * com.cws.esolutions.core.tasks
 * DNSRequestTask.java
 *
 * TODO: Add class description
 *
 * $Id: DNSRequestTask.java 2139 2012-11-12 00:36:14Z kmhuntly@gmail.com $
 * $Author: kmhuntly@gmail.com $
 * $Date: 2012-11-11 19:36:14 -0500 (Sun, 11 Nov 2012) $
 * $Revision: 2139 $
 * @author khuntly
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Oct 12, 2012 2:56:18 PM
 *     Created.
 */
public class DNSRequestTask extends AsyncTask<String, Object, List<String>>
{
    private String methodName = null;
    private Activity reqActivity = null;

    private static final String CNAME = DNSRequestTask.class.getName();
    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger(Constants.ERROR_LOGGER);
    
    public DNSRequestTask(final Activity activity)
    {
        methodName = CNAME + "#DNSRequestTask(final Activity activity)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Activity: ", activity);
        }

        this.reqActivity = activity;
    }

    @Override
    protected void onPreExecute()
    {
        methodName = CNAME + "#onPreExecute()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        boolean isConnected = false;
        ConnectivityManager connMgr = null;

        connMgr = (ConnectivityManager) reqActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfo = connMgr.getAllNetworkInfo();

        if ((networkInfo.length == 0) || (networkInfo == null))
        {
            // no available network connection
            ERROR_LOGGER.error("No available network connection. Cannot continue.");

            cancel(true);
        }
        else
        {
            for (NetworkInfo networks : networkInfo)
            {
                if (networks.isConnected())
                {
                    isConnected = true;
                }
            }

            if (!(isConnected))
            {
                ERROR_LOGGER.error("Network connections are available but not currently connected.");

                cancel(true);                
            }
        }
    }

    @Override
    protected List<String> doInBackground(final String... request)
    {
        methodName = CNAME + "#doInBackground(final String... request)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);

            for (String str : request)
            {
                DEBUGGER.debug(str);
            }
        }

        DNSServiceRequest dnsRequest = null;
        DNSServiceResponse dnsResponse = null;
        List<String> resultsList = new ArrayList<String>();

        final IDNSServiceRequest dnsProcessor = new DNSServiceRequestImpl();

        try
        {
            DNSEntry dnsEntry = new DNSEntry();
            dnsEntry.setHostName(request[0]);
            dnsEntry.setRecordType(DNSRecordType.valueOf(request[2]));

            if (DEBUG)
            {
                DEBUGGER.debug("DNSEntry: {}", dnsEntry);
            }

            dnsRequest = new DNSServiceRequest();
            dnsRequest.setRequestType(DNSRequestType.LOOKUP);
            dnsRequest.setResolverHost(request[1]);
            dnsRequest.setDnsEntry(dnsEntry);

            if (DEBUG)
            {
                DEBUGGER.debug("DNSRequest: ", dnsRequest);
            }

            dnsResponse = dnsProcessor.performLookup(dnsRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("DNSResponse: ", dnsResponse);

                for (String str : resultsList)
                {
                    DEBUGGER.debug(str);
                }
            }
        }
        catch (DNSServiceException dsx)
        {
            ERROR_LOGGER.error(dsx.getMessage(), dsx);
        }

        return resultsList;
    }

    protected void onPostExecute(final List<String> result)
    {
        methodName = CNAME + "#onPostExecute(final List<String> result)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);

            for (String str : result)
            {
                DEBUGGER.debug(str);
            }
        }

        final TextView responseView = ((TextView) this.reqActivity.findViewById(R.id.tvResponseList));

        if ((result != null) && (result.size() != 0))
        {
            StringBuilder sBuilder = new StringBuilder();

            for (String entry : result)
            {
                if (DEBUG)
                {
                    DEBUGGER.debug(entry);
                }

                sBuilder.append(entry + "\n");
            }

            if (DEBUG)
            {
                DEBUGGER.debug(sBuilder.toString());
            }

            responseView.setText(sBuilder.toString());
        }
        else
        {
            responseView.setTextColor(Color.RED);
            responseView.setText(this.reqActivity.getString(R.string.txtSignonError));            
        }
    }
}
