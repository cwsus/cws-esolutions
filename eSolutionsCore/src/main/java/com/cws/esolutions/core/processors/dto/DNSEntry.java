/*
 * Copyright (c) 2009 - 2020 CaspersBox Web Services
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
 * File: DNSEntry.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly   		11/23/2008 22:39:20             Created.
 */
import java.util.List;
import org.slf4j.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.core.CoreServiceConstants;
/**
 * @author cws-khuntly
 * @version 1.0
 * @see java.io.Serializable
 */
public class DNSEntry implements Serializable
{
	private int retry = 3600;
	private int refresh = 900;
    private int lifetime = 900;
    private int minimum = 3600;
    private String owner = null;
    private String origin = ".";
    private int expiry = 604800;
    private String master = null;
    private int serialNumber = -1;
    private String fileName = null;
    private String siteName = null;
    private String projectCode = null;
    private List<DNSRecord> subRecords = null;
    private List<DNSRecord> apexRecords = null;

    private static final String CNAME = DNSEntry.class.getName();
    private static final long serialVersionUID = 3314079583199404196L;

    private static final Logger DEBUGGER = LoggerFactory.getLogger(CoreServiceConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(CoreServiceConstants.ERROR_LOGGER);

    public final void setProjectCode(final String value)
    {
        final String methodName = DNSEntry.CNAME + "#setProjectCode(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.projectCode = value;
    }

    public final void setFileName(final String value)
    {
        final String methodName = DNSEntry.CNAME + "#setFileName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.fileName = value;
    }

    public final void setOrigin(final String value)
    {
        final String methodName = DNSEntry.CNAME + "#setOrigin(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.origin = value;
    }

    public final void setLifetime(final int value)
    {
        final String methodName = DNSEntry.CNAME + "#setLifetime(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.lifetime = value;
    }

    public final void setSiteName(final String value)
    {
        final String methodName = DNSEntry.CNAME + "#setSiteName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.siteName = value;
    }

    public final void setMaster(final String value)
    {
        final String methodName = DNSEntry.CNAME + "#setMaster(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.master = value;
    }

    public final void setOwner(final String value)
    {
        final String methodName = DNSEntry.CNAME + "#setOwner(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.owner = value;
    }

    public final void setSerialNumber(final int value)
    {
        final String methodName = DNSEntry.CNAME + "#setSerialNumber(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serialNumber = value;
    }

    public final void setRefresh(final int value)
    {
        final String methodName = DNSEntry.CNAME + "#setRefresh(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.refresh = value;
    }

    public final void setRetry(final int value)
    {
        final String methodName = DNSEntry.CNAME + "#setRetry(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.retry = value;
    }

    public final void setExpiry(final int value)
    {
        final String methodName = DNSEntry.CNAME + "#setExpiry(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.expiry = value;
    }

    public final void setMinimum(final int value)
    {
        final String methodName = DNSEntry.CNAME + "#setMinimum(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.minimum = value;
    }

    public final void setSubRecords(final List<DNSRecord> value)
    {
        final String methodName = DNSEntry.CNAME + "#setSubRecords(final List<DNSRecord> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.subRecords = value;
    }

    public final void setApexRecords(final List<DNSRecord> value)
    {
        final String methodName = DNSEntry.CNAME + "#setApexRecords(final List<DNSRecord> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.apexRecords = value;
    }

    public final String getProjectCode()
    {
        final String methodName = DNSEntry.CNAME + "#getProjectCode()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.projectCode);
        }

        return this.projectCode;
    }

    public final String getFileName()
    {
        final String methodName = DNSEntry.CNAME + "#getFileName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.fileName);
        }

        return this.fileName;
    }

    public final String getOrigin()
    {
        final String methodName = DNSEntry.CNAME + "#getOrigin()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.origin);
        }

        return this.origin;
    }

    public final int getLifetime()
    {
        final String methodName = DNSEntry.CNAME + "#getLifetime()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.lifetime);
        }

        return this.lifetime;
    }

    public final String getSiteName()
    {
        final String methodName = DNSEntry.CNAME + "#getSiteName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.siteName);
        }

        return this.siteName;
    }

    public final String getMaster()
    {
        final String methodName = DNSEntry.CNAME + "#getMaster()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.master);
        }

        return this.master;
    }

    public final String getOwner()
    {
        final String methodName = DNSEntry.CNAME + "#getOwner()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.owner);
        }

        return this.owner;
    }

    public final int getSerialNumber()
    {
        final String methodName = DNSEntry.CNAME + "#getSerialNumber()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.serialNumber);
        }

        return this.serialNumber;
    }

    public final int getRefresh()
    {
        final String methodName = DNSEntry.CNAME + "#getRefresh()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.refresh);
        }

        return this.refresh;
    }

    public final int getRetry()
    {
        final String methodName = DNSEntry.CNAME + "#getRetry()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.retry);
        }

        return this.retry;
    }

    public final int getExpiry()
    {
        final String methodName = DNSEntry.CNAME + "#getExpiry()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.expiry);
        }

        return this.expiry;
    }

    public final int getMinimum()
    {
        final String methodName = DNSEntry.CNAME + "#getMinimum()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.minimum);
        }

        return this.minimum;
    }

    public final List<DNSRecord> getSubRecords()
    {
        final String methodName = DNSEntry.CNAME + "#getSubRecords()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.subRecords);
        }

        return this.subRecords;
    }

    public final List<DNSRecord> getApexRecords()
    {
        final String methodName = DNSEntry.CNAME + "#getApexRecords()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.apexRecords);
        }

        return this.apexRecords;
    }

    @Override
    public final String toString()
    {
        final String methodName = DNSEntry.CNAME + "#toString()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        StringBuilder sBuilder = new StringBuilder()
            .append("[" + this.getClass().getName() + "]" + CoreServiceConstants.LINE_BREAK + "{" + CoreServiceConstants.LINE_BREAK);

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
                        sBuilder.append("\t" + field.getName() + " --> " + field.get(this) + CoreServiceConstants.LINE_BREAK);
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
