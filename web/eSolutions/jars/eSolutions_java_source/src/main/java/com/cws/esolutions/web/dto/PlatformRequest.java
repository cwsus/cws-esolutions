/*
 * Copyright (c) 2009 - 2014 CaspersBox Web Services
 * 
 * All rights reserved. These materials are confidential and
 * proprietary to CaspersBox Web Services N.A and no part of
 * these materials should be reproduced, published in any form
 * by any means, electronic or mechanical, including photocopy
 * or any information storage or retrieval system not should
 * the materials be disclosed to third parties without the
 * express written authorization of CaspersBox Web Services, N.A.
 */
package com.cws.esolutions.web.dto;
/*
 * Project: eSolutions_java_source
 * Package: com.cws.esolutions.web.dto
 * File: PlatformRequest.java
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
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.web.Constants;
import com.cws.esolutions.core.processors.enums.ServiceRegion;
import com.cws.esolutions.core.processors.enums.ServiceStatus;
/**
 * @author khuntly
 * @version 1.0
 */
public class PlatformRequest implements Serializable
{
    private String description = null;
    private String platformName = null;
    private ServiceStatus status = null;
    private ServiceRegion region = null;
    private List<String> platformServers = null;

    private static final long serialVersionUID = 6181577121963540025L;
    private static final String CNAME = PlatformRequest.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER);

    public final void setPlatformName(final String value)
    {
        final String methodName = PlatformRequest.CNAME + "#setPlatformName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.platformName = value;
    }

    public final void setStatus(final ServiceStatus value)
    {
        final String methodName = PlatformRequest.CNAME + "#setStatus(final ServiceStatus value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.status = value;
    }

    public final void setRegion(final ServiceRegion value)
    {
        final String methodName = PlatformRequest.CNAME + "#setRegion(final ServiceRegion value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.region = value;
    }

    public final void setPlatformServers(final List<String> value)
    {
        final String methodName = PlatformRequest.CNAME + "#setWebServers(final List<String> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.platformServers = value;
    }

    public final void setDescription(final String value)
    {
        final String methodName = PlatformRequest.CNAME + "#setDescription(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.description = value;
    }

    public final String getPlatformName()
    {
        final String methodName = PlatformRequest.CNAME + "#getPlatformName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.platformName);
        }

        return this.platformName;
    }

    public final ServiceStatus getStatus()
    {
        final String methodName = PlatformRequest.CNAME + "#getStatus()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.status);
        }

        return this.status;
    }

    public final ServiceRegion getRegion()
    {
        final String methodName = PlatformRequest.CNAME + "#getRegion()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.region);
        }

        return this.region;
    }

    public final List<String> getPlatformServers()
    {
        final String methodName = PlatformRequest.CNAME + "#getPlatformServers()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.platformServers);
        }

        return this.platformServers;
    }

    public final String getDescription()
    {
        final String methodName = PlatformRequest.CNAME + "#getDescription()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.description);
        }

        return this.description;
    }

    @Override
    public final String toString()
    {
        final String methodName = PlatformRequest.CNAME + "#toString()";

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