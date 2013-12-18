/*
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

import com.cws.esolutions.core.Constants;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.security.audit.processors.interfaces
 * File: IAuditProcessor.java
 *
 * History
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * Kevin Huntly         11/23/2008 22:39:20             Created.
 * Kevin Huntly         12/05/2008 13:36:09             Added method to process change requests
 */
public class ApplicationManagementResponse implements Serializable
{
    private int entryCount = 0;
    private byte[] fileData = null;
    private String currentPath = null;
    private Application appData = null;
    private Project projectData = null;
    private List<String> fileList = null;
    private List<Project> projectList = null;
    private CoreServicesStatus requestStatus = null;
    private List<Application> applicationList = null;

    private static final long serialVersionUID = 6843850571636255437L;
    private static final String CNAME = ApplicationManagementResponse.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER);

    public final void setRequestStatus(final CoreServicesStatus value)
    {
        final String methodName = ApplicationManagementResponse.CNAME + "#setRequestStatus(final CoreServicesStatus value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.requestStatus = value;
    }

    public final void setEntryCount(final int value)
    {
        final String methodName = ApplicationManagementResponse.CNAME + "#setEntryCount(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.entryCount = value;
    }

    public final void setApplication(final Application value)
    {
        final String methodName = ApplicationManagementResponse.CNAME + "#setApplication(final Application value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.appData = value;
    }

    public final void setProject(final Project value)
    {
        final String methodName = ApplicationManagementResponse.CNAME + "#setProject(final Project value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.projectData = value;
    }

    public final void setApplicationList(final List<Application> value)
    {
        final String methodName = ApplicationManagementResponse.CNAME + "#setApplicationList(final List<Application> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.applicationList = value;
    }

    public final void setFileData(final byte[] value)
    {
        final String methodName = ApplicationManagementResponse.CNAME + "#setFileData(final byte[] value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.fileData = value;
    }

    public final void setCurrentPath(final String value)
    {
        final String methodName = ApplicationManagementResponse.CNAME + "#setCurrentPath(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.currentPath = value;
    }

    public final void setFileList(final List<String> value)
    {
        final String methodName = ApplicationManagementResponse.CNAME + "#setFileData(final List<String> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.fileList = value;
    }

    public final void setProjectList(final List<Project> value)
    {
        final String methodName = ApplicationManagementResponse.CNAME + "#setProjectList(final List<Project> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.projectList = value;
    }

    public final CoreServicesStatus getRequestStatus()
    {
        final String methodName = ApplicationManagementResponse.CNAME + "#getRequestStatus()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.requestStatus);
        }

        return this.requestStatus;
    }

    public final int getEntryCount()
    {
        final String methodName = ApplicationManagementResponse.CNAME + "#getEntryCount()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.entryCount);
        }

        return this.entryCount;
    }

    public final Application getApplication()
    {
        final String methodName = ApplicationManagementResponse.CNAME + "#getAppData()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.appData);
        }

        return this.appData;
    }

    public final Project getProject()
    {
        final String methodName = ApplicationManagementResponse.CNAME + "#getProject()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.projectData);
        }

        return this.projectData;
    }

    public final List<Application> getApplicationList()
    {
        final String methodName = ApplicationManagementResponse.CNAME + "#getApplicationList()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.applicationList);
        }

        return this.applicationList;
    }

    public final List<Project> getProjectList()
    {
        final String methodName = ApplicationManagementResponse.CNAME + "#getProjectList()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.projectList);
        }

        return this.projectList;
    }

    public final byte[] getFileData()
    {
        final String methodName = ApplicationManagementResponse.CNAME + "#getFileData()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.fileData);
        }

        return this.fileData;
    }

    public final String getCurrentPath()
    {
        final String methodName = ApplicationManagementResponse.CNAME + "#getCurrentPath()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.currentPath);
        }

        return this.currentPath;
    }

    public final List<String> getFileList()
    {
        final String methodName = ApplicationManagementResponse.CNAME + "#getFileList()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.fileList);
        }

        return this.fileList;
    }

    @Override
    public final String toString()
    {
        final String methodName = ApplicationManagementResponse.CNAME + "#toString()";

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
