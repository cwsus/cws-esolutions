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
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.processors.dto
 * File: Application.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.core.Constants;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 */
public class Application implements Serializable
{
    private String applicationGuid = null;
    private String applicationName = null;
    private double applicationVersion = 1.0;
    private String installPath = null;
    private String clusterName = null;
    private String packageLocation = null;
    private String packageInstaller = null;
    private String installerOptions = null;
    private String logsDirectory = null;
    private Project project = null;
    private List<Platform> platforms = null;
    private Date onlineDate = null;
    private Date offlineDate = null;

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

    public final void setApplicationVersion(final double value)
    {
        final String methodName = Application.CNAME + "#setApplicationVersion(final double value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.applicationVersion = value;
    }

    public final void setInstallPath(final String value)
    {
        final String methodName = Application.CNAME + "#setInstallPath(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.installPath = value;
    }

    public final void setClusterName(final String value)
    {
        final String methodName = Application.CNAME + "#setClusterName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.clusterName = value;
    }

    public final void setPackageLocation(final String value)
    {
        final String methodName = Application.CNAME + "#setPackageLocation(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.packageLocation = value;
    }

    public final void setPackageInstaller(final String value)
    {
        final String methodName = Application.CNAME + "#setPackageInstaller(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.packageInstaller = value;
    }

    public final void setInstallerOptions(final String value)
    {
        final String methodName = Application.CNAME + "#setInstallerOptions(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.installerOptions = value;
    }

    public final void setLogsDirectory(final String value)
    {
        final String methodName = Application.CNAME + "#setLogsDirectory(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.logsDirectory = value;
    }

    public final void setProject(final Project value)
    {
        final String methodName = Application.CNAME + "#setProject(final Project value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.project = value;
    }

    public final void setPlatforms(final List<Platform> value)
    {
        final String methodName = Application.CNAME + "#setPlatforms(final List<Platform> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.platforms = value;
    }

    public final void setOnlineDate(final Date value)
    {
        final String methodName = Application.CNAME + "#setOnlineDate(final Date value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.onlineDate = value;
    }

    public final void setOfflineDate(final Date value)
    {
        final String methodName = Application.CNAME + "#setOfflineDate(final Date value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.offlineDate = value;
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

    public final double getApplicationVersion()
    {
        final String methodName = Application.CNAME + "#getApplicationVersion()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.applicationVersion);
        }

        return this.applicationVersion;
    }

    public final String getInstallPath()
    {
        final String methodName = Application.CNAME + "#getInstallPath()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.installPath);
        }

        return this.installPath;
    }

    public final String getClusterName()
    {
        final String methodName = Application.CNAME + "#getClusterName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.clusterName);
        }

        return this.clusterName;
    }

    public final String getPackageLocation()
    {
        final String methodName = Application.CNAME + "#getPackageLocation()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.packageLocation);
        }

        return this.packageLocation;
    }

    public final String getPackageInstaller()
    {
        final String methodName = Application.CNAME + "#getPackageInstaller()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.packageInstaller);
        }

        return this.packageInstaller;
    }

    public final String getInstallerOptions()
    {
        final String methodName = Application.CNAME + "#getInstallerOptions()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.installerOptions);
        }

        return this.installerOptions;
    }

    public final String getLogsDirectory()
    {
        final String methodName = Application.CNAME + "#getLogsDirectory()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.logsDirectory);
        }

        return this.logsDirectory;
    }

    public final Project getProject()
    {
        final String methodName = Application.CNAME + "#getProject()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.project);
        }

        return this.project;
    }

    public final List<Platform> getPlatforms()
    {
        final String methodName = Application.CNAME + "#getPlatforms()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.platforms);
        }

        return this.platforms;
    }

    public final Date getOnlineDate(final Date value)
    {
        final String methodName = Application.CNAME + "#getOnlineDate(final Date value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.onlineDate);
        }

        return this.onlineDate;
    }

    public final Date getOfflineDate(final Date value)
    {
        final String methodName = Application.CNAME + "#getOfflineDate(final Date value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.offlineDate);
        }

        return this.offlineDate;
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
