/**
 * Copyright (c) 2009 - 2013 By: CWS, Inc.
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
package com.cws.esolutions.core.processors.dto;

import java.util.List;
import org.slf4j.Logger;
import java.io.Serializable;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Field;

import com.cws.esolutions.core.Constants;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.processors.dto
 * DNSServiceResponse.java
 *
 * $Id: $
 * $Author: $
 * $Date: $
 * $Revision: $
 * @author kmhuntly@gmail.com
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Oct 29, 2012 9:44:46 AM
 *     Created.
 */
public class DNSServiceResponse implements Serializable
{
    private String response = null;
    private DNSEntry dnsEntry = null;
    private DNSRecord dnsRecord = null;
    private StringBuilder zoneData = null;
    private List<DNSEntry> dnsEntries = null;
    private List<DNSRecord> dnsRecords = null;
    private CoreServicesStatus requestStatus = null;

    private static final long serialVersionUID = -1730196918896683625L;
    private static final String CNAME = DNSServiceResponse.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER);

    public final void setRequestStatus(final CoreServicesStatus value)
    {
        final String methodName = DNSServiceResponse.CNAME + "#setRequestStatus(final CoreServicesStatus value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.requestStatus = value;
    }

    public final void setResponse(final String value)
    {
        final String methodName = DNSServiceResponse.CNAME + "#setResponse(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.response = value;
    }

    public final void setDnsEntry(final DNSEntry value)
    {
        final String methodName = DNSServiceResponse.CNAME + "#setDnsEntry(final DNSEntry value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.dnsEntry = value;
    }

    public final void setDnsEntries(final List<DNSEntry> value)
    {
        final String methodName = DNSServiceResponse.CNAME + "#setDnsEntries(final List<DNSEntry> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.dnsEntries = value;
    }

    public final void setZoneData(final StringBuilder value)
    {
        final String methodName = DNSServiceResponse.CNAME + "#setZoneData(final StringBuilder value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.zoneData = value;
    }

    public final void setDnsRecord(final DNSRecord value)
    {
        final String methodName = DNSServiceResponse.CNAME + "#setDnsRecord(final DNSRecord value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.dnsRecord = value;
    }

    public final void setDnsRecords(final List<DNSRecord> value)
    {
        final String methodName = DNSServiceResponse.CNAME + "#setDnsRecord(final List<DNSRecord> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.dnsRecords = value;
    }

    public final CoreServicesStatus getRequestStatus()
    {
        final String methodName = DNSServiceResponse.CNAME + "#getRequestStatus()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.requestStatus);
        }

        return this.requestStatus;
    }

    public final String getResponse()
    {
        final String methodName = DNSServiceResponse.CNAME + "#getResponse()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.response);
        }

        return this.response;
    }

    public final DNSEntry getDnsEntry()
    {
        final String methodName = DNSServiceResponse.CNAME + "#getDnsEntry()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.dnsEntry);
        }

        return this.dnsEntry;
    }

    public final List<DNSEntry> getDnsEntries()
    {
        final String methodName = DNSServiceResponse.CNAME + "#getDnsEntries()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.dnsEntries);
        }

        return this.dnsEntries;
    }

    public final StringBuilder getZoneData()
    {
        final String methodName = DNSServiceResponse.CNAME + "#getZoneData()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.zoneData);
        }

        return this.zoneData;
    }

    public final DNSRecord getDnsRecord()
    {
        final String methodName = DNSServiceResponse.CNAME + "#getDnsRecord()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.dnsRecord);
        }

        return this.dnsRecord;
    }

    public final List<DNSRecord> getDnsRecords()
    {
        final String methodName = DNSServiceResponse.CNAME + "#getDnsRecords()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.dnsRecord);
        }

        return this.dnsRecords;
    }

    @Override
    public final String toString()
    {
        final String methodName = DNSServiceResponse.CNAME + "#toString()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        StringBuilder sBuilder = new StringBuilder()
            .append("[" + this.getClass().getName() + "]" + Constants.LINE_BREAK + "{" + Constants.LINE_BREAK);

        for (Field field : this.getClass().getDeclaredFields())
        {
            if (DEBUG)
            {
                DEBUGGER.debug("field: {}", field);
            }

            if (!(field.getName().equals("methodName")) &&
                    (!(field.getName().equals("CNAME"))) &&
                    (!(field.getName().equals("DEBUGGER"))) &&
                    (!(field.getName().equals("DEBUG"))) &&
                    (!(field.getName().equals("serialVersionUID"))))
            {
                try
                {
                    if (field.get(this) != null)
                    {
                        sBuilder.append("\t" + field.getName() + " --> " + field.get(this) + Constants.LINE_BREAK);
                    }
                }
                catch (IllegalAccessException iax)
                {
                    ERROR_RECORDER.error(iax.getMessage(), iax);
                }
            }
        }

        sBuilder.append('}');

        if (DEBUG)
        {
            DEBUGGER.debug("sBuilder: {}", sBuilder);
        }

        return sBuilder.toString();
    }
}
