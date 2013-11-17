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
import java.lang.reflect.Modifier;

import com.cws.esolutions.core.Constants;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.processors.dto
 * PlatformManagementResponse.java
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
 * 35033355 @ Apr 1, 2013 4:12:25 PM
 *     Created.
 */
public class PlatformManagementResponse implements Serializable
{
    private int entryCount = 0;
    private String response = null;
    private Platform platformData = null;
    private List<Platform> platformList = null;
    private CoreServicesStatus requestStatus = null;

    private static final long serialVersionUID = -4054815151986531024L;
    private static final String CNAME = PlatformManagementResponse.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER);

    public final void setRequestStatus(final CoreServicesStatus value)
    {
        final String methodName = PlatformManagementResponse.CNAME + "#setRequestStatus(final CoreServicesStatus value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.requestStatus = value;
    }

    public final void setResponse(final String value)
    {
        final String methodName = PlatformManagementResponse.CNAME + "#setResponse(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.response = value;
    }

    public final void setEntryCount(final int value)
    {
        final String methodName = PlatformManagementResponse.CNAME + "#setEntryCount(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.entryCount = value;
    }

    public final void setPlatformData(final Platform value)
    {
        final String methodName = PlatformManagementResponse.CNAME + "#setPlatformData(final Platform value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.platformData = value;
    }

    public final void setPlatformList(final List<Platform> value)
    {
        final String methodName = PlatformManagementResponse.CNAME + "#setPlatformList(final List<Platform> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.platformList = value;
    }

    public final CoreServicesStatus getRequestStatus()
    {
        final String methodName = PlatformManagementResponse.CNAME + "#getRequestStatus()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.requestStatus);
        }

        return this.requestStatus;
    }

    public final String getResponse()
    {
        final String methodName = PlatformManagementResponse.CNAME + "#getResponse()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.response);
        }

        return this.response;
    }

    public final int getEntryCount()
    {
        final String methodName = PlatformManagementResponse.CNAME + "#getEntryCount()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.entryCount);
        }

        return this.entryCount;
    }

    public final Platform getPlatformData()
    {
        final String methodName = PlatformManagementResponse.CNAME + "#getPlatformData()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.platformData);
        }

        return this.platformData;
    }

    public final List<Platform> getPlatformList()
    {
        final String methodName = PlatformManagementResponse.CNAME + "#getPlatformList()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.platformList);
        }

        return this.platformList;
    }

    @Override
    public final String toString()
    {
        final String methodName = PlatformManagementResponse.CNAME + "#toString()";

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

            if (field.getModifiers() != Modifier.STATIC)
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
