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
package com.cws.esolutions.agent.processors.dto;
/*
 * Project: eSolutionsAgent
 * Package: com.cws.esolutions.agent.processors.dto
 * File: SystemManagerRequest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly   11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.agent.AgentConstants;
import com.cws.esolutions.agent.processors.enums.SystemCheckType;
import com.cws.esolutions.agent.processors.enums.ListOperationType;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author cws-khuntly
 * @version 1.0
 */
public class ServiceCheckRequest implements Serializable
{
    private int portNumber = 0;
    private String targetHost = null;
    private String processName = null;
    private String extTargetDir = null;
    private SystemCheckType requestType = null;
    private ListOperationType listOperationType = null;

    private static final long serialVersionUID = 9161223960598973053L;
    private static final String CNAME = ServiceCheckRequest.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(AgentConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(AgentConstants.ERROR_LOGGER);

    public final void setRequestType(final SystemCheckType value)
    {
        final String methodName = ServiceCheckRequest.CNAME + "#setRequestType(final SystemCheckType value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.requestType = value;
    }

    public final void setTargetHost(final String value)
    {
        final String methodName = ServiceCheckRequest.CNAME + "#setTargetHost(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.targetHost = value;
    }

    public final void setPortNumber(final int value)
    {
        final String methodName = ServiceCheckRequest.CNAME + "#setPortNumber(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.portNumber = value;
    }

    public final void setProcessName(final String value)
    {
        final String methodName = ServiceCheckRequest.CNAME + "#setProcessName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.processName = value;
    }

    public final void setExtTargetDir(final String value)
    {
        final String methodName = ServiceCheckRequest.CNAME + "#setExtTargetDir(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.extTargetDir = value;
    }

    public final void setListOperationType(final ListOperationType value)
    {
        final String methodName = ServiceCheckRequest.CNAME + "#setListOperationType(final ListOperationType value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.listOperationType = value;
    }

    public final SystemCheckType getRequestType()
    {
        final String methodName = ServiceCheckRequest.CNAME + "#getRequestType()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.requestType);
        }

        return this.requestType;
    }

    public final String getTargetHost()
    {
        final String methodName = ServiceCheckRequest.CNAME + "#getTargetHost()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.targetHost);
        }

        return this.targetHost;
    }

    public final int getPortNumber()
    {
        final String methodName = ServiceCheckRequest.CNAME + "#getPortNumber()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.portNumber);
        }

        return this.portNumber;
    }

    public final String getProcessName()
    {
        final String methodName = ServiceCheckRequest.CNAME + "#getProcessName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.processName);
        }

        return this.processName;
    }

    public final String getExtTargetDir()
    {
        final String methodName = ServiceCheckRequest.CNAME + "#getExtTargetDir()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.extTargetDir);
        }

        return this.extTargetDir;
    }

    public final ListOperationType getListOperationType()
    {
        final String methodName = ServiceCheckRequest.CNAME + "#getListOperationType()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.listOperationType);
        }

        return this.listOperationType;
    }

    @Override
    public final String toString()
    {
        final String methodName = ServiceCheckRequest.CNAME + "#toString()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        StringBuilder sBuilder = new StringBuilder()
            .append("[" + this.getClass().getName() + "]" + AgentConstants.LINE_BREAK + "{" + AgentConstants.LINE_BREAK);

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
                        sBuilder.append("\t" + field.getName() + " --> " + field.get(this) + AgentConstants.LINE_BREAK);
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
