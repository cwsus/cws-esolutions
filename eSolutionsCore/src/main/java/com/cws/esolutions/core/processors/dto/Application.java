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
import java.io.Serializable;
import java.lang.reflect.Field;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.cws.esolutions.core.CoreServicesConstants;
import com.cws.esolutions.core.processors.enums.DeploymentType;
/**
 * @author cws-khuntly
 * @version 1.0
 * @see java.io.Serializable
 */
public class Application implements Serializable
{
    private String name = null;
    private String guid = null;
    private File basePath = null;
    private File logsPath = null;
    private File binaries = null;
    private String scmPath = null;
    private String jvmName = null;
    private String version = "1.0";
    private Date onlineDate = null;
    private Date offlineDate = null;
    private Platform platform = null;
    private String clusterName = null;
    private File installPath = null;
    private String platformGuid = null;
    private File pidDirectory = null;
    private boolean isScmEnabled = false;
    private File packageLocation = null;
    private File packageInstaller = null;
    private String installerOptions = null;
    private DeploymentType deploymentType = null;

    private static final String CNAME = Application.class.getName();
    private static final long serialVersionUID = 5766570647243160904L;

    private static final Logger DEBUGGER = LogManager.getLogger(CoreServicesConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setPlatform(final Platform value)
    {
        final String methodName = Application.CNAME + "#setPlatform(final Platform value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.platform = value;
    }

    public final void setBinaries(final File value)
    {
        final String methodName = Application.CNAME + "#setBinaries(final File value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.binaries = value;
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

    public final void setBasePath(final File file)
    {
        final String methodName = Application.CNAME + "#setBasePath(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", file);
        }

        this.basePath = file;
    }

    public final void setLogsPath(final File value)
    {
        final String methodName = Application.CNAME + "#setLogsPath(final File value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.logsPath = value;
    }

    public final void setPidDirectory(final File value)
    {
        final String methodName = Application.CNAME + "#setPidDirectory(final File value)";

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

    public final void setVersion(final String value)
    {
        final String methodName = Application.CNAME + "#setVersion(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.version = value;
    }

    public final void setInstallPath(final File value)
    {
        final String methodName = Application.CNAME + "#setInstallPath(final File value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.installPath = value;
    }

    public final void setPackageLocation(final File value)
    {
        final String methodName = Application.CNAME + "#setPackageLocation(final File value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.packageLocation = value;
    }

    public final void setPackageInstaller(final File value)
    {
        final String methodName = Application.CNAME + "#setPackageInstaller(final File value)";

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

    public final void setPlatformGuid(final String value)
    {
        final String methodName = Application.CNAME + "#setPlatformGuid(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.platformGuid = value;
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

    public final File getPackageLocation()
    {
        final String methodName = Application.CNAME + "#getPackageLocation()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.packageLocation);
        }

        return this.packageLocation;
    }

    public final File getPackageInstaller()
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

    public final Platform getPlatform()
    {
        final String methodName = Application.CNAME + "#getPlatform()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.platform);
        }

        return this.platform;
    }

    public final File getBinaries()
    {
        final String methodName = Application.CNAME + "#getBinaries()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.binaries);
        }

        return this.binaries;
    }

    public final String getVersion()
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

    public final File getBasePath()
    {
        final String methodName = Application.CNAME + "#getBasePath()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.basePath);
        }

        return this.basePath;
    }

    public final File getLogsPath()
    {
        final String methodName = Application.CNAME + "#getLogsPath()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.logsPath);
        }

        return this.logsPath;
    }

    public final File getInstallPath()
    {
        final String methodName = Application.CNAME + "#getInstallPath()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.installPath);
        }

        return this.installPath;
    }

    public final File getPidDirectory()
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

    public final String getPlatformGuid()
    {
        final String methodName = Application.CNAME + "#getPlatformGuid()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.platformGuid);
        }

        return this.platformGuid;
    }

    @Override
    public final String toString()
    {
        StringBuilder sBuilder = new StringBuilder()
            .append("[" + this.getClass().getName() + "]" + CoreServicesConstants.LINE_BREAK + "{" + CoreServicesConstants.LINE_BREAK);

        for (Field field : this.getClass().getDeclaredFields())
        {
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
                        sBuilder.append("\t" + field.getName() + " --> " + field.get(this) + CoreServicesConstants.LINE_BREAK);
                    }
                }
                catch (final IllegalAccessException iax) {}
            }
        }

        sBuilder.append('}');

        return sBuilder.toString();
    }
}
