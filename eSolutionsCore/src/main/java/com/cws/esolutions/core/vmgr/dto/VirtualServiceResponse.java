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
package com.cws.esolutions.core.vmgr.dto;

import java.util.Map;
import org.slf4j.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Modifier;

import com.cws.esolutions.core.Constants;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.vmgr.dto
 * VirtualServiceResponse.java
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
 * kh05451 @ Oct 31, 2012 2:16:06 PM
 *     Created.
 */
public class VirtualServiceResponse implements Serializable
{
    private String response = null;
    private boolean isComplete = false;
    private Map<String, String> machines = null;
    private CoreServicesStatus requestStatus = null;

    private static final long serialVersionUID = -2100329293282061334L;
    private static final String CNAME = VirtualServiceResponse.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER);

    public final void setRequestStatus(final CoreServicesStatus value)
    {
        final String methodName = VirtualServiceResponse.CNAME + "#setRequestStatus(final CoreServicesStatus value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.requestStatus = value;
    }

    public final void setResponse(final String value)
    {
        final String methodName = VirtualServiceResponse.CNAME + "#setResponse(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.response = value;
    }

    public final void setMachines(final Map<String, String> value)
    {
        final String methodName = VirtualServiceResponse.CNAME + "#setMachines(final Map<String, String> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.machines = value;
    }

    public final void setIsComplete(final boolean value)
    {
        final String methodName = VirtualServiceResponse.CNAME + "#setIsComplete(final boolean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.isComplete = value;
    }

    public final CoreServicesStatus getRequestStatus()
    {
        final String methodName = VirtualServiceResponse.CNAME + "#getRequestStatus()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.requestStatus);
        }

        return this.requestStatus;
    }

    public final String getResponse()
    {
        final String methodName = VirtualServiceResponse.CNAME + "#getResponse()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.response);
        }

        return this.response;
    }

    public final Map<String, String> getMachines()
    {
        final String methodName = VirtualServiceResponse.CNAME + "#getMachines()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.machines);
        }

        return this.machines;
    }

    public final boolean isComplete()
    {
        final String methodName = VirtualServiceResponse.CNAME + "#isComplete()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.isComplete);
        }

        return this.isComplete;
    }

    @Override
    public final String toString()
    {
        final String methodName = VirtualServiceResponse.CNAME + "#toString()";

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
