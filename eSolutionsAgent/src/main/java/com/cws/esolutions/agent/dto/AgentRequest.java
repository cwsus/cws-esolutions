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
package com.cws.esolutions.agent.dto;

import org.slf4j.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Modifier;

import com.cws.esolutions.agent.Constants;
/**
 * eSolutionsAgent
 * com.cws.esolutions.agent.dto
 * AgentRequest.java
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
 * kh05451 @ Nov 5, 2012 10:57:29 AM
 *     Created.
 */
public class AgentRequest implements Serializable
{
    private String appName = null;
    private String projectId = null;
    private String serviceId = null;
    private String hostname = null;
    private Object requestPayload = null;

    private static final String CNAME = AgentRequest.class.getName();
    private static final long serialVersionUID = -7910758710669328293L;

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER);

    public final void setAppName(final String value)
    {
        final String methodName = AgentRequest.CNAME + "#setAppName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.appName = value;
    }

    public final void setProjectId(final String value)
    {
        final String methodName = AgentRequest.CNAME + "#setProjectId(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.projectId = value;
    }

    public final void setServiceId(final String value)
    {
        final String methodName = AgentRequest.CNAME + "#setServiceId(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serviceId = value;
    }

    public final void setHostname(final String value)
    {
        final String methodName = AgentRequest.CNAME + "#setHostname(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.hostname = value;
    }

    public final void setRequestPayload(final Object value)
    {
        final String methodName = AgentRequest.CNAME + "#setRequestPayload(final Object value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.requestPayload = value;
    }

    public final String getAppName()
    {
        final String methodName = AgentRequest.CNAME + "#getAppName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.appName);
        }

        return this.appName;
    }

    public final String getProjectId()
    {
        final String methodName = AgentRequest.CNAME + "#getProjectId()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.projectId);
        }

        return this.projectId;
    }

    public final String getServiceId()
    {
        final String methodName = AgentRequest.CNAME + "#getServiceId()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.serviceId);
        }

        return this.serviceId;
    }

    public final String getHostname()
    {
        final String methodName = AgentRequest.CNAME + "#getHostname()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.hostname);
        }

        return this.hostname;
    }

    public final Object getRequestPayload()
    {
        final String methodName = AgentRequest.CNAME + "#getRequestPayload()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.requestPayload);
        }

        return this.requestPayload;
    }

    @Override
    public String toString()
    {
        final String methodName = AgentRequest.CNAME + "#toString()";

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
