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
package com.cws.esolutions.agent.processors.dto;

import java.util.List;
import org.slf4j.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.agent.Constants;
import com.cws.esolutions.agent.enums.AgentStatus;
/**
 * Project: eSolutionsAgent
 * Package: com.cws.esolutions.agent.processors.dto
 * File: FileManagerResponse.java
 *
 * $Id: $
 * $Author: $
 * $Date: $
 * $Revision: $
 * @author 35033355
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * 35033355 @ Aug 15, 2013 12:02:37 PM
 *     Created.
 */
public class FileManagerResponse implements Serializable
{
    private Long checksum = null;
    private byte[] fileData = null;
    private String fileName = null;
    private String filePath = null;
    private String response = null;
    private List<String> dirListing = null;
    private AgentStatus requestStatus = null;

    private static final long serialVersionUID = 1675501067920065831L;
    private static final String CNAME = FileManagerResponse.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setRequestStatus(final AgentStatus value)
    {
        final String methodName = FileManagerResponse.CNAME + "#setRequestStatus(final AgentStatus value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AgentStatus: {}", value);
        }

        this.requestStatus = value;
    }

    public final void setResponse(final String value)
    {
        final String methodName = FileManagerResponse.CNAME + "#setResponse(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug(value);
        }

        this.response = value;
    }

    public final void setFileData(final byte[] value)
    {
        final String methodName = FileManagerResponse.CNAME + "#setFileData(final byte[] value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.fileData = value;
    }

    public final void setFileName(final String value)
    {
        final String methodName = FileManagerResponse.CNAME + "#setFileName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.fileName = value;
    }

    public final void setFilePath(final String value)
    {
        final String methodName = FileManagerResponse.CNAME + "#setFilePath(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.filePath = value;
    }

    public final void setChecksum(final Long value)
    {
        final String methodName = FileManagerResponse.CNAME + "#setChecksum(final Long value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.checksum = value;
    }

    public final void setDirListing(final List<String> value)
    {
        final String methodName = FileManagerResponse.CNAME + "#setChecksum(final List<String> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.dirListing = value;
    }

    public final AgentStatus getRequestStatus()
    {
        final String methodName = FileManagerResponse.CNAME + "#getRequestStatus()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AgentStatus: {}", this.requestStatus);
        }

        return this.requestStatus;
    }

    public final String getResponse()
    {
        final String methodName = FileManagerResponse.CNAME + "#getResponse()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug(this.response);
        }

        return this.response;
    }

    public final byte[] getFileData()
    {
        final String methodName = FileManagerResponse.CNAME + "#getFileData()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.fileData);
        }

        return this.fileData;
    }

    public final String getFileName()
    {
        final String methodName = FileManagerResponse.CNAME + "#getFileName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.fileName);
        }

        return this.fileName;
    }

    public final String getFilePath()
    {
        final String methodName = FileManagerResponse.CNAME + "#getFilePath()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.filePath);
        }

        return this.filePath;
    }

    public final Long getChecksum()
    {
        final String methodName = FileManagerResponse.CNAME + "#getChecksum()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.checksum);
        }

        return this.checksum;
    }

    public final List<String> getDirListing()
    {
        final String methodName = FileManagerResponse.CNAME + "#getChecksum()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.dirListing);
        }

        return this.dirListing;
    }

    @Override
    public final String toString()
    {
        final String methodName = FileManagerResponse.CNAME + "#toString()";

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
