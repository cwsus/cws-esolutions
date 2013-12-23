/*
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
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.processors.dto
 * File: DNSServiceRequest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.List;
import org.slf4j.Logger;
import java.io.Serializable;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Field;

import com.cws.esolutions.core.Constants;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.enums.ServiceRegion;
import com.cws.esolutions.core.processors.enums.DNSRequestType;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 */
public class DNSServiceRequest implements Serializable
{
    private String serviceId = null;
    private String searchURL = null;
    private DNSRecord record = null;
    private DNSEntry dnsEntry = null;
    private String[] searchPath = null;
    private String resolverHost = null;
    private String changeRequest = null;
    private String applicationId = null;
    private String applicationName = null;
    private UserAccount userAccount = null;
    private List<DNSEntry> dnsEntries = null;
    private DNSRequestType requestType = null;
    private RequestHostInfo requestInfo = null;
    private ServiceRegion serviceRegion = null;

    private static final long serialVersionUID = -1572590232161993918L;
    private static final String CNAME = DNSServiceRequest.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER);

    public final void setUserAccount(final UserAccount value)
    {
        final String methodName = DNSServiceRequest.CNAME + "#setUserAccount(final UserAccount value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.userAccount = value;
    }

    public final void setRequestInfo(final RequestHostInfo value)
    {
        final String methodName = DNSServiceRequest.CNAME + "#setRequestInfo(final RequestHostInfo value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.requestInfo = value;
    }

    public final void setServiceId(final String value)
    {
        final String methodName = DNSServiceRequest.CNAME + "#setServiceId(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serviceId = value;
    }

    public final void setApplicationName(final String value)
    {
        final String methodName = DNSServiceRequest.CNAME + "#setApplicationName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.applicationName = value;
    }

    public final void setApplicationId(final String value)
    {
        final String methodName = DNSServiceRequest.CNAME + "#setApplicationId(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.applicationId = value;
    }

    public final void setDnsEntry(final DNSEntry value)
    {
        final String methodName = DNSServiceRequest.CNAME + "#setDnsEntry(final DNSEntry value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.dnsEntry = value;
    }

    public final void setDnsEntries(final List<DNSEntry> value)
    {
        final String methodName = DNSServiceRequest.CNAME + "#setDnsEntries(final List<DNSEntry> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.dnsEntries = value;
    }

    public final void setSearchURL(final String value)
    {
        final String methodName = DNSServiceRequest.CNAME + "#setSearchURL(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.searchURL = value;
    }

    public final void setRequestType(final DNSRequestType value)
    {
        final String methodName = DNSServiceRequest.CNAME + "#setRequestType(final DNSRequestType value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.requestType = value;
    }

    public final void setChangeRequest(final String value)
    {
        final String methodName = DNSServiceRequest.CNAME + "#setChangeRequest(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.changeRequest = value;
    }

    public final void setSearchPath(final String[] value)
    {
        final String methodName = DNSServiceRequest.CNAME + "#setSearchPath(final String[] value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);

            for (String str : value)
            {
                DEBUGGER.debug("Value: {}", str);
            }
        }

        this.searchPath = value;
    }

    public final void setResolverHost(final String value)
    {
        final String methodName = DNSServiceRequest.CNAME + "#setResolverHost(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.resolverHost = value;
    }

    public final void setServiceRegion(final ServiceRegion value)
    {
        final String methodName = DNSServiceRequest.CNAME + "#setServiceRegion(final ServiceRegion value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serviceRegion = value;
    }

    public final void setRecord(final DNSRecord value)
    {
        final String methodName = DNSServiceRequest.CNAME + "#setRecord(final DNSRecord value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.record = value;
    }

    public final UserAccount getUserAccount()
    {
        final String methodName = DNSServiceRequest.CNAME + "#getUserAccount()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.userAccount);
        }

        return this.userAccount;
    }

    public final RequestHostInfo getRequestInfo()
    {
        final String methodName = DNSServiceRequest.CNAME + "#getRequestInfo()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.requestInfo);
        }

        return this.requestInfo;
    }

    public final String getServiceId()
    {
        final String methodName = DNSServiceRequest.CNAME + "#getServiceId()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.serviceId);
        }

        return this.serviceId;
    }

    public final String getApplicationName()
    {
        final String methodName = DNSServiceRequest.CNAME + "#getApplicationName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.applicationName);
        }

        return this.applicationName;
    }

    public final String getApplicationId()
    {
        final String methodName = DNSServiceRequest.CNAME + "#getApplicationId()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.applicationId);
        }

        return this.applicationId;
    }

    public final DNSEntry getDnsEntry()
    {
        final String methodName = DNSServiceRequest.CNAME + "#getDnsEntry()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.dnsEntry);
        }

        return this.dnsEntry;
    }

    public final List<DNSEntry> getDnsEntries()
    {
        final String methodName = DNSServiceRequest.CNAME + "#getDnsEntries()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.dnsEntries);
        }

        return this.dnsEntries;
    }

    public final DNSRequestType getRequestType()
    {
        final String methodName = DNSServiceRequest.CNAME + "#getRequestType()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("DNSRequestType: {}", this.requestType);
        }

        return this.requestType;
    }

    public final String getChangeRequest()
    {
        final String methodName = DNSServiceRequest.CNAME + "#getChangeRequest()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.changeRequest);
        }

        return this.changeRequest;
    }

    public final String[] getSearchPath()
    {
        final String methodName = DNSServiceRequest.CNAME + "#getSearchPath()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);

            if (this.searchPath != null)
            {
                for (String str : this.searchPath)
                {
                    DEBUGGER.debug("Value: {}", str);
                }
            }
        }

        return this.searchPath;
    }

    public final String getSearchURL()
    {
        final String methodName = DNSServiceRequest.CNAME + "#getSearchURL()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.searchURL);
        }

        return this.searchURL;
    }

    public final String getResolverHost()
    {
        final String methodName = DNSServiceRequest.CNAME + "#getResolverHost()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.resolverHost);
        }

        return this.resolverHost;
    }

    public final ServiceRegion getServiceRegion()
    {
        final String methodName = DNSServiceRequest.CNAME + "#getServiceRegion()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.serviceRegion);
        }

        return this.serviceRegion;
    }

    public final DNSRecord getRecord()
    {
        final String methodName = DNSServiceRequest.CNAME + "#getRecord()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.record);
        }

        return this.record;
    }

    @Override
    public final String toString()
    {
        final String methodName = DNSServiceRequest.CNAME + "#toString()";

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
                    (!(field.getName().equals("ERROR_RECORDER"))) &&
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
