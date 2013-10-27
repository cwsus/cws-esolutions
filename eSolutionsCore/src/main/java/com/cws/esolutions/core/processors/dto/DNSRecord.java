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
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.core.Constants;
import com.cws.esolutions.core.processors.enums.DNSRecordType;
/**
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.processors.dto
 * File: DNSRecord.java
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
 * 35033355 @ Jul 19, 2013 3:40:09 PM
 *     Created.
 */
public class DNSRecord implements Serializable
{
    private int recordPort = 0; // only used for srv records
    private int recordWeight = 0; // only used for srv/mx records
    private int recordLifetime = 0; // only used for srv records
    private int recordPriority = 10; // only used for srv/mx records
    private String spfRecord = null; // spf record for mx
    private String recordName = null; // used for all record types
    private String recordClass = "IN"; // used for all record types
    private String recordOrigin = null; // all records have an origin, apex will be .
    private boolean mailRecord = false; // set this to true if the record is an mx record
    private String recordService = null; // only used for srv records
    private String recordProtocol = null; // only used for srv records
    private DNSRecordType recordType = null; // used for all record types
    private List<String> primaryAddress = null; // one or more addresses
    private List<String> tertiaryAddress = null; // one or more addresses
    private List<String> secondaryAddress = null; // one or more addresses

    private static final String CNAME = DNSRecord.class.getName();
    private static final long serialVersionUID = -3108982210099182120L;

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setRecordOrigin(final String value)
    {
        final String methodName = DNSRecord.CNAME + "#setRecordOrigin(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.recordOrigin = value;
    }

    /**
     * Utilize this method to set a port number for an associated
     * SRV record
     *
     * @param value
     */
    public final void setRecordPort(final int value)
    {
        final String methodName = DNSRecord.CNAME + "#setRecordPort(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.recordPort = value;
    }

    /**
     * Utilize this method to set the weight for a SRV record
     *
     * @param value
     */
    public final void setRecordWeight(final int value)
    {
        final String methodName = DNSRecord.CNAME + "#setRecordWeight(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.recordWeight = value;
    }

    /**
     * Utilize this method to set the TTL for a SRV record
     *
     * @param value
     */
    public final void setRecordLifetime(final int value)
    {
        final String methodName = DNSRecord.CNAME + "#setRecordLifetime(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.recordLifetime = value;
    }

    /**
     * Utilize this method to set the name for the associated
     * record. This can be utilized for all record types
     *
     * @param value
     */
    public final void setRecordName(final String value)
    {
        final String methodName = DNSRecord.CNAME + "#setRecordName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.recordName = value;
    }

    /**
     * Utilize this method to set the associated record type
     * (e.g. A, CNAME, MX, etc)
     *
     * @param value
     */
    public final void setRecordType(final DNSRecordType value)
    {
        final String methodName = DNSRecord.CNAME + "#setRecordPort(final DNSRecordType value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.recordType = value;
    }

    /**
     * Utilize this method to set the record class. The most common
     * class will be "IN", which is the default.
     *
     * @param value
     */
    public final void setRecordClass(final String value)
    {
        final String methodName = DNSRecord.CNAME + "#setRecordClass(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.recordClass = value;
    }

    /**
     * Utilize this method to set the target for a SRV record.
     *
     * @param value
     */
    public final void setPrimaryAddress(final List<String> value)
    {
        final String methodName = DNSRecord.CNAME + "#setPrimaryAddress(final List<String> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.primaryAddress = value;
    }

    public final void setSecondaryAddress(final List<String> value)
    {
        final String methodName = DNSRecord.CNAME + "#setSecondaryAddress(final List<String> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.secondaryAddress = value;
    }

    public final void setTertiaryAddress(final List<String> value)
    {
        final String methodName = DNSRecord.CNAME + "#setTertiaryAddress(final List<String> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.tertiaryAddress = value;
    }

    /**
     * Utilize this method to set the record service for a SRV
     * record, e.g. "sip"
     *
     * @param value
     */
    public final void setRecordService(final String value)
    {
        final String methodName = DNSRecord.CNAME + "#setRecordService(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.recordService = value;
    }

    /**
     * Utilize this method to set the protocol for a SRV record,
     * e.g. "tcp"
     *
     * @param value
     */
    public final void setRecordProtocol(final String value)
    {
        final String methodName = DNSRecord.CNAME + "#setRecordProtocol(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.recordProtocol = value;
    }

    /**
     * Utilize this method to set the priority for a SRV
     * or MX record
     *
     * @param value
     */
    public final void setRecordPriority(final int value)
    {
        final String methodName = DNSRecord.CNAME + "#setRecordPriority(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.recordPriority = value;
    }

    /**
     * Utilize this method to set multiple address records
     * for a given entry - e.g.
     * proxy IN A 192.168.10.6
             IN A 192.168.10.8
     *
     * @param value
     */
    public final void setSpfRecord(final String value)
    {
        final String methodName = DNSRecord.CNAME + "#setSpfRecord(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.spfRecord = value;
    }

    public final String getRecordOrigin()
    {
        final String methodName = DNSRecord.CNAME + "#getRecordOrigin()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.recordOrigin);
        }

        return this.recordOrigin;
    }

    public final void setMailRecord(final boolean value)
    {
        final String methodName = DNSRecord.CNAME + "#setMailRecord(final boolean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.mailRecord = value;
    }

    public final int getRecordPort()
    {
        final String methodName = DNSRecord.CNAME + "#getRecordPort()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.recordPort);
        }

        return this.recordPort;
    }

    public final int getRecordWeight()
    {
        final String methodName = DNSRecord.CNAME + "#getRecordWeight()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.recordWeight);
        }

        return this.recordWeight;
    }

    public final int getRecordLifetime()
    {
        final String methodName = DNSRecord.CNAME + "#getRecordLifetime()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.recordLifetime);
        }

        return this.recordLifetime;
    }

    public final String getRecordName()
    {
        final String methodName = DNSRecord.CNAME + "#getRecordName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.recordName);
        }

        return this.recordName;
    }

    public final DNSRecordType getRecordType()
    {
        final String methodName = DNSRecord.CNAME + "#getRecordPort()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.recordType);
        }

        return this.recordType;
    }

    public final String getRecordClass()
    {
        final String methodName = DNSRecord.CNAME + "#getRecordClass()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.recordClass);
        }

        return this.recordClass;
    }

    public final List<String> getPrimaryAddress()
    {
        final String methodName = DNSRecord.CNAME + "#getPrimaryAddress()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.primaryAddress);
        }

        return this.primaryAddress;
    }

    public final List<String> getSecondaryAddress()
    {
        final String methodName = DNSRecord.CNAME + "#getSecondaryAddress()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.secondaryAddress);
        }

        return this.secondaryAddress;
    }

    public final List<String> getTertiaryAddress()
    {
        final String methodName = DNSRecord.CNAME + "#getTertiaryAddress()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.tertiaryAddress);
        }

        return this.tertiaryAddress;
    }

    public final String getRecordService()
    {
        final String methodName = DNSRecord.CNAME + "#getRecordService()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.recordService);
        }

        return this.recordService;
    }

    public final String getRecordProtocol()
    {
        final String methodName = DNSRecord.CNAME + "#getRecordProtocol()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.recordProtocol);
        }

        return this.recordProtocol;
    }

    public final int getRecordPriority()
    {
        final String methodName = DNSRecord.CNAME + "#getRecordPriority()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.recordPriority);
        }

        return this.recordPriority;
    }

    public final String getSpfRecord()
    {
        final String methodName = DNSRecord.CNAME + "#getSpfRecord()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.spfRecord);
        }

        return this.spfRecord;
    }

    public final boolean isMailRecord()
    {
        final String methodName = DNSRecord.CNAME + "#isMailRecord()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.mailRecord);
        }

        return this.mailRecord;
    }

    @Override
    public final String toString()
    {
        final String methodName = DNSRecord.CNAME + "#toString()";

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
                    // don't do anything with it
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
