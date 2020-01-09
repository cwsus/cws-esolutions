/*
 * Copyright (c) 2009 - 2017 CaspersBox Web Services
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
 * File: DNSRecord.java
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
import com.cws.esolutions.core.processors.enums.DNSRecordClass;
import com.cws.esolutions.core.processors.enums.DNSRecordType;
/**
 * @author cws-khuntly
 * @version 1.0
 * @see java.io.Serializable
 */
public class DNSRecord implements Serializable
{
    private int recordPort = 0; // only used for srv records
    private int recordWeight = 0; // only used for srv/mx records
    private int recordLifetime = 0; // only used for srv records
    private int recordPriority = 10; // only used for srv/mx records
    private String recordName = null; // used for all record types
    private String recordOrigin = ".";
    private String recordAddress = null; // one or more addresses
    private String recordService = null; // only used for srv records
    private String recordProtocol = null; // only used for srv records
    private DNSRecordType recordType = null; // used for all record types
    private List<String> recordAddresses = null; // one or more addresses
    private DNSRecordClass recordClass = DNSRecordClass.IN; // used for all record types

    private static final String CNAME = DNSRecord.class.getName();
    private static final long serialVersionUID = -3108982210099182120L;

    private static final Logger DEBUGGER = LoggerFactory.getLogger(CoreServiceConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(CoreServiceConstants.ERROR_LOGGER);

    /**
     * Utilize this method to set a port number for an associated
     * SRV record
     *
     * @param value The port number to utilize (Only valid for SRV record types)
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
     * Utilize this method to set the weight for a SRV or MX record
     *
     * @param value the record weight
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
     * @param value the record TTL (time to live)
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
     * @param value the record name
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
     * @param value the record type
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
     * @param value The record class (defaults to "IN" if not specified)
     */
    public final void setRecordClass(final DNSRecordClass value)
    {
        final String methodName = DNSRecord.CNAME + "#setRecordClass(final DNSRecordClass value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.recordClass = value;
    }

    /**
     * Utilize this method to set the target for a record.
     *
     * @param value The primary record address
     */
    public final void setRecordAddress(final String value)
    {
        final String methodName = DNSRecord.CNAME + "#setRecordAddress(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.recordAddress = value;
    }

    /**
     * Utilize this method to set the record service for a SRV
     * record, e.g. "sip"
     *
     * @param value the record service
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
     * @param value the record protocol
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
     * @param value the record priority
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
     * Utilize this method to set multiple addresses for a given record
     *
     * @param value A list of addresses
     */
    public final void setRecordAddresses(final List<String> value)
    {
        final String methodName = DNSRecord.CNAME + "#setRecordAddresses(final List<String> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.recordAddresses = value;
    }

    /**
     * Utilize this method to set the record's origin
     *
     * @param value The record origin. If not specified, defaults to "."
     */
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
     * @return The record port (only valid for SRV records)
     */
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

    /**
     * @return The record weight (only valid for MX and SRV records)
     */
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

    /**
     * @return The record TTL (time to live)
     */
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

    /**
     * @return The record name
     */
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

    /**
     * @return The record type
     */
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

    /**
     * @return The record class
     */
    public final DNSRecordClass getRecordClass()
    {
        final String methodName = DNSRecord.CNAME + "#getRecordClass()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.recordClass);
        }

        return this.recordClass;
    }

    /**
     * @return The primary record address
     */
    public final String getRecordAddress()
    {
        final String methodName = DNSRecord.CNAME + "#getRecordAddress()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.recordAddress);
        }

        return this.recordAddress;
    }


    /**
     * @return The primary record address
     */
    public final List<String> getRecordAddresses()
    {
        final String methodName = DNSRecord.CNAME + "#getRecordAddresses()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.recordAddresses);
        }

        return this.recordAddresses;
    }

    /**
     * @return The record service (only valid for SRV records)
     */
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

    /**
     * @return The record protocol
     */
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

    /**
     * @return The record priority
     */
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

    /**
     * @return The primary record address
     */
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

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public final String toString()
    {
        final String methodName = DNSRecord.CNAME + "#toString()";

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
