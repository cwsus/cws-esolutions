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
package com.cws.esolutions.android.tasks;
/*
 * eSolutions
 * com.cws.esolutions.core.tasks
 * DNSRequestTask.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.List;
import org.slf4j.Logger;
import java.util.ArrayList;
import android.os.AsyncTask;
import android.app.Activity;
import android.graphics.Color;
import org.slf4j.LoggerFactory;
import android.widget.TextView;

import com.cws.esolutions.android.ui.R;
import com.cws.esolutions.android.Constants;
import com.cws.esolutions.android.utils.NetworkUtils;
import com.cws.esolutions.core.processors.dto.DNSRecord;
import com.cws.esolutions.core.processors.enums.DNSRecordType;
import com.cws.esolutions.core.processors.enums.DNSRequestType;
import com.cws.esolutions.core.processors.dto.DNSServiceRequest;
import com.cws.esolutions.core.processors.dto.DNSServiceResponse;
import com.cws.esolutions.core.processors.exception.DNSServiceException;
import com.cws.esolutions.core.processors.impl.DNSServiceRequestProcessorImpl;
import com.cws.esolutions.core.processors.interfaces.IDNSServiceRequestProcessor;

public class DNSRequestTask extends AsyncTask<String, Object, List<String>>
{
    private Activity reqActivity = null;

    private static final String CNAME = DNSRequestTask.class.getName();
    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + DNSRequestTask.class.getSimpleName());

    public DNSRequestTask(final Activity value)
    {
        final String methodName = DNSRequestTask.CNAME + "#DNSRequestTask(final Activity value)#Constructor()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Activity: {}", value);
        }

        this.reqActivity = value;
    }

    @Override
    protected void onPreExecute()
    {
        final String methodName = DNSRequestTask.CNAME + "#onPreExecute()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

		if (!(NetworkUtils.checkNetwork(this.reqActivity)))
		{
			super.cancel(true);
		}
    }

    @Override
    protected List<String> doInBackground(final String... value)
    {
        final String methodName = DNSRequestTask.CNAME + "#doInBackground(final String... value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        List<String> resultsList = new ArrayList<String>();

        final IDNSServiceRequestProcessor dnsProcessor = new DNSServiceRequestProcessorImpl();

        try
        {
            DNSRecord record = new DNSRecord();
            record.setRecordName(value[0]);
            record.setRecordType(DNSRecordType.valueOf(value[2]));

            if (DEBUG)
            {
                DEBUGGER.debug("DNSRecord: {}", record);
            }

            DNSServiceRequest dnsRequest = new DNSServiceRequest();
            dnsRequest.setRequestType(DNSRequestType.LOOKUP);
            dnsRequest.setResolverHost(value[1]);
            dnsRequest.setRecord(record);

            if (DEBUG)
            {
                DEBUGGER.debug("DNSRequest: {}", dnsRequest);
            }

            DNSServiceResponse response = dnsProcessor.performLookup(dnsRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("DNSResponse: {}", response);
            }
        }
        catch (DNSServiceException dsx)
        {
            ERROR_RECORDER.error(dsx.getMessage(), dsx);
        }

        return resultsList;
    }

    protected void onPostExecute(final List<String> value)
    {
        final String methodName = DNSRequestTask.CNAME + "#onPostExecute(final List<String> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        final TextView responseView = ((TextView) this.reqActivity.findViewById(R.id.tvResponseList));

        if ((value != null) && (value.size() != 0))
        {
            StringBuilder sBuilder = new StringBuilder();

            for (String entry : value)
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
