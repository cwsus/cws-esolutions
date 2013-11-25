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
package com.cws.esolutions.agent.executors.dto;

import java.util.List;

import org.slf4j.Logger;

import java.io.Serializable;
import java.lang.reflect.Field;

import org.slf4j.LoggerFactory;

import com.cws.esolutions.agent.Constants;
/**
 * eSolutionsAgent
 * com.cws.esolutions.agent.executors.dto
 * ExecuteCommandRequest.java
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
 * kh05451 @ Nov 16, 2012 10:55:12 AM
 *     Created.
 */
public class ExecuteCommandRequest implements Serializable
{
    private long timeout = 0;
    private boolean printError = true;
    private boolean printOutput = true;
    private List<String> command = null;

    private static final long serialVersionUID = -2995144040596681519L;
    private static final String CNAME = ExecuteCommandRequest.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER);

    public final void setCommand(final List<String> value)
    {
        final String methodName = ExecuteCommandRequest.CNAME + "#setCommand(final List<String> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.command = value;
    }

    public final void setPrintOutput(final boolean value)
    {
        final String methodName = ExecuteCommandRequest.CNAME + "#setPrintOutput(final boolean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.printOutput = value;
    }

    public final void setPrintError(final boolean value)
    {
        final String methodName = ExecuteCommandRequest.CNAME + "#setPrintError(final boolean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.printError = value;
    }

    public final void setTimeout(final long value)
    {
        final String methodName = ExecuteCommandRequest.CNAME + "#setTimeout(final long value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.timeout = value;
    }

    public final List<String> getCommand()
    {
        final String methodName = ExecuteCommandRequest.CNAME + "#getCommand()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.command);
        }

        return this.command;
    }

    public final boolean printOutput()
    {
        final String methodName = ExecuteCommandRequest.CNAME + "#printOutput()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.printOutput);
        }

        return this.printOutput;
    }

    public final boolean printError()
    {
        final String methodName = ExecuteCommandRequest.CNAME + "#printError()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.printError);
        }

        return this.printError;
    }

    public final Long getTimeout()
    {
        final String methodName = ExecuteCommandRequest.CNAME + "#getTimeout()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.timeout);
        }

        return this.timeout;
    }

    @Override
    public final String toString()
    {
        final String methodName = ExecuteCommandRequest.CNAME + "#toString()";

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
