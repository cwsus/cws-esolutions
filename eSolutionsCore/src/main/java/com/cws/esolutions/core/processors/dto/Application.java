/*
 * Copyright (c) 2009 - 2020 CaspersBox Web Services
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
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.io.File;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.core.CoreServiceConstants;
import com.cws.esolutions.core.processors.enums.DeploymentType;
/**
 * @author cws-khuntly
 * @version 1.0
 * @see java.io.Serializable
 */
public class Application implements Serializable
{
    private double score = 0.0;
    private String name = null;
    private String guid = null;
    private double version = 1.0;
    private String scmPath = null;
    private String jvmName = null;
    private Date onlineDate = null;
    private String basePath = null;
    private String platform = null;
    private String logsPath = null;
    private Date offlineDate = null;
    private String clusterName = null;
    private String installPath = null;
    private String pidDirectory = null;
    private String logsDirectory = null;
    private boolean isScmEnabled = false;
    private String applicationGuid = null;
    private String applicationName = null;
    private File applicationBinary = null;
    private String packageLocation = null;
    private String packageInstaller = null;
    private String installerOptions = null;
    private List<Service> platforms = null;
    private DeploymentType deploymentType = null;

    private static final String CNAME = Application.class.getName();
    private static final long serialVersionUID = -7939041322590386615L;

    private static final Logger DEBUGGER = LoggerFactory.getLogger(CoreServiceConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(CoreServiceConstants.ERROR_LOGGER);

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

    public final void setPlatform(final String value)
    {
        final String methodName = Application.CNAME + "#setPlatform(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.platform = value;
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

    public final void setLogsPath(final String value)
    {
        final String methodName = Application.CNAME + "#setLogsPath(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.logsPath = value;
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

    public final void setDeploymentType(final DeploymentType value)
    {
        final String methodName = Application.CNAME + "#setDeploymentType(final DeploymentType value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.deploymentType = value;
    }

    public final void setIsScmEnabled(final boolean value)
    {
        final String methodName = Application.CNAME + "#setIsScmEnabled(final boolean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.isScmEnabled = value;
    }

    public final void setScore(final double value)
    {
        final String methodName = Application.CNAME + "#setScore(final double value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.score = value;
    }

    public final void setGuid(final String value)
    {
        final String methodName = Application.CNAME + "#setGuid(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.guid = value;
    }

    public final void setName(final String value)
    {
        final String methodName = Application.CNAME + "#setName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.name = value;
    }

    public final void setVersion(final double value)
    {
        final String methodName = Application.CNAME + "#setVersion(final double value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.version = value;
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

    public final void setPlatforms(final List<Service> value)
    {
        final String methodName = Application.CNAME + "#setPlatforms(final List<Service> value)";

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

    public final double getScore()
    {
        final String methodName = Application.CNAME + "#getScore()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.score);
        }

        return this.score;
    }

    public final String getGuid()
    {
        final String methodName = Application.CNAME + "#getGuid()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.guid);
        }

        return this.guid;
    }

    public final String getName()
    {
        final String methodName = Application.CNAME + "#getName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.name);
        }

        return this.name;
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

    public final List<Service> getPlatforms()
    {
        final String methodName = Application.CNAME + "#getPlatforms()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.platforms);
        }

        return this.platforms;
    }

    public final Date getOnlineDate()
    {
        final String methodName = Application.CNAME + "#getOnlineDate()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.onlineDate);
        }

        return this.onlineDate;
    }

    public final Date getOfflineDate()
    {
        final String methodName = Application.CNAME + "#getOfflineDate()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.offlineDate);
        }

        return this.offlineDate;
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

    public final String getPlatform()
    {
        final String methodName = Application.CNAME + "#getPlatform()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.platform);
        }

        return this.platform;
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

    public final double getVersion()
    {
        final String methodName = Application.CNAME + "#getVersion()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.version);
        }

        return this.version;
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

    public final String getLogsPath()
    {
        final String methodName = Application.CNAME + "#getLogsPath()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.logsPath);
        }

        return this.logsPath;
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

    public final DeploymentType getDeploymentType()
    {
        final String methodName = Application.CNAME + "#getDeploymentType()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.deploymentType);
        }

        return this.deploymentType;
    }

    public final boolean getIsScmEnabled()
    {
        final String methodName = Application.CNAME + "#getIsScmEnabled()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.isScmEnabled);
        }

        return this.isScmEnabled;
    }

    public final boolean isScmEnabled()
    {
        final String methodName = Application.CNAME + "#isScmEnabled()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.isScmEnabled);
        }

        return this.isScmEnabled;
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
            .append("[" + this.getClass().getName() + "]" + CoreServiceConstants.LINE_BREAK + "{" + CoreServiceConstants.LINE_BREAK);

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
                        sBuilder.append("\t" + field.getName() + " --> " + field.get(this) + CoreServiceConstants.LINE_BREAK);
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
