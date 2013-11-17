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

import java.io.File;
import java.util.List;
import org.slf4j.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.core.Constants;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.processors.dto
 * Article.java
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
 * kh05451 @ Oct 30, 2012 2:51:43 PM
 *     Created.
 */
public class Application implements Serializable
{
    private String scmPath = null;
    private String jvmName = null;
    private String basePath = null;
    private String pidDirectory = null;
    private String applicationGuid = null;
    private String applicationName = null;
    private File applicationBinary = null;
    private String applicationCluster = null;
    private String applicationVersion = "1.0";
    private String applicationLogsPath = null;
    private Project applicationProject = null;
    private String applicationInstallPath = null;
    private List<Platform> applicationPlatforms = null;

    private static final String CNAME = Application.class.getName();
    private static final long serialVersionUID = -7939041322590386615L;

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER);

    public final void setApplicationGuid(final String value)
    {
        final String methodName = Application.CNAME + "#setApplicationGuid(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.applicationGuid = value;
    }

    public final void setApplicationName(final String value)
    {
        final String methodName = Application.CNAME + "#setApplicationName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.applicationName = value;
    }

    public final void setBasePath(final String value)
    {
        final String methodName = Application.CNAME + "#setBasePath(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.basePath = value;
    }

    public final void setScmPath(final String value)
    {
        final String methodName = Application.CNAME + "#setScmPath(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.scmPath = value;
    }

    public final void setApplicationPlatforms(final List<Platform> value)
    {
        final String methodName = Application.CNAME + "#setApplicationPlatforms(final List<Platform> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.applicationPlatforms = value;
    }

    public final void setApplicationBinary(final File value)
    {
        final String methodName = Application.CNAME + "#setApplicationBinary(final File value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.applicationBinary = value;
    }

    public final void setApplicationVersion(final String value)
    {
        final String methodName = Application.CNAME + "#setApplicationVersion(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.applicationVersion = value;
    }

    public final void setApplicationCluster(final String value)
    {
        final String methodName = Application.CNAME + "#setApplicationCluster(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.applicationCluster = value;
    }

    public final void setApplicationLogsPath(final String value)
    {
        final String methodName = Application.CNAME + "#setApplicationLogsPath(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.applicationLogsPath = value;
    }

    public final void setApplicationProject(final Project value)
    {
        final String methodName = Application.CNAME + "#setApplicationProject(final Project value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.applicationProject = value;
    }

    public final void setApplicationInstallPath(final String value)
    {
        final String methodName = Application.CNAME + "#setApplicationInstallPath(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.applicationInstallPath = value;
    }

    public final void setPidDirectory(final String value)
    {
        final String methodName = Application.CNAME + "#setPidDirectory(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.pidDirectory = value;
    }

    public final void setJvmName(final String value)
    {
        final String methodName = Application.CNAME + "#setJvmName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.jvmName = value;
    }

    public final String getApplicationGuid()
    {
        final String methodName = Application.CNAME + "#getApplicationGuid()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.applicationGuid);
        }

        return this.applicationGuid;
    }

    public final String getApplicationName()
    {
        final String methodName = Application.CNAME + "#getApplicationName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.applicationName);
        }

        return this.applicationName;
    }

    public final List<Platform> getApplicationPlatforms()
    {
        final String methodName = Application.CNAME + "#getApplicationPlatforms()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.applicationPlatforms);
        }

        return this.applicationPlatforms;
    }

    public final File getApplicationBinary()
    {
        final String methodName = Application.CNAME + "#getApplicationBinary()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.applicationBinary);
        }

        return this.applicationBinary;
    }

    public final String getApplicationVersion()
    {
        final String methodName = Application.CNAME + "#getApplicationVersion()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.applicationVersion);
        }

        return this.applicationVersion;
    }

    public final String getBasePath()
    {
        final String methodName = Application.CNAME + "#getBasePath()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.basePath);
        }

        return this.basePath;
    }

    public final String getScmPath()
    {
        final String methodName = Application.CNAME + "#getScmPath()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.scmPath);
        }

        return this.scmPath;
    }

    public final String getApplicationCluster()
    {
        final String methodName = Application.CNAME + "#getApplicationCluster()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.applicationCluster);
        }

        return this.applicationCluster;
    }

    public final String getApplicationLogsPath()
    {
        final String methodName = Application.CNAME + "#getApplicationLogsPath()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.applicationLogsPath);
        }

        return this.applicationLogsPath;
    }

    public final Project getApplicationProject()
    {
        final String methodName = Application.CNAME + "#getApplicationProject()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.applicationProject);
        }

        return this.applicationProject;
    }

    public final String getApplicationInstallPath()
    {
        final String methodName = Application.CNAME + "#getApplicationInstallPath()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.applicationInstallPath);
        }

        return this.applicationInstallPath;
    }

    public final String getPidDirectory()
    {
        final String methodName = Application.CNAME + "#getPidDirectory()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.pidDirectory);
        }

        return this.pidDirectory;
    }

    public final String getJvmName()
    {
        final String methodName = Application.CNAME + "#getJvmName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.jvmName);
        }

        return this.jvmName;
    }

    @Override
    public final String toString()
    {
        final String methodName = Application.CNAME + "#toString()";

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
