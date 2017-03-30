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
 * File: ApplicationManagerRequestManagerRequest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.agent.AgentConstants;
/**
 * Interface for the ApplicationManagerRequest Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * ApplicationManagerRequest information.
 *
 * @author khuntly
 * @version 1.0
 */
public class ApplicationManagerRequest implements Serializable
{
    private double version = 0.0;
    private String installPath = null;
    private String packageName = null;
    private String packageLocation = null;
    private String packageInstaller = null;
    private String installerOptions = null;

    private static final String CNAME = ApplicationManagerRequest.class.getName();
    private static final long serialVersionUID = -7939041322590386615L;

    private static final Logger DEBUGGER = LoggerFactory.getLogger(AgentConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(AgentConstants.ERROR_LOGGER);

    public final void setPackageName(final String value)
    {
        final String methodName = ApplicationManagerRequest.CNAME + "#setPackageName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.packageName = value;
    }

    public final void setInstallPath(final String value)
    {
        final String methodName = ApplicationManagerRequest.CNAME + "#setInstallPath(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.installPath = value;
    }

    public final void setPackageLocation(final String value)
    {
        final String methodName = ApplicationManagerRequest.CNAME + "#setPackageLocation(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.packageLocation = value;
    }

    public final void setVersion(final double value)
    {
        final String methodName = ApplicationManagerRequest.CNAME + "#setPackageLocation(final double value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.version = value;
    }

    public final void setPackageInstaller(final String value)
    {
        final String methodName = ApplicationManagerRequest.CNAME + "#setPackageInstaller(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.packageInstaller = value;
    }

    public final void setInstallerOptions(final String value)
    {
        final String methodName = ApplicationManagerRequest.CNAME + "#setInstallerOptions(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.installerOptions = value;
    }

    public final String getPackageName()
    {
        final String methodName = ApplicationManagerRequest.CNAME + "#getPackageName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.packageName);
        }

        return this.packageName;
    }

    public final String getInstallPath()
    {
        final String methodName = ApplicationManagerRequest.CNAME + "#getInstallPath()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.installPath);
        }

        return this.installPath;
    }

    public final String getPackageLocation()
    {
        final String methodName = ApplicationManagerRequest.CNAME + "#getPackageLocation()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.packageLocation);
        }

        return this.packageLocation;
    }

    public final String getPackageInstaller()
    {
        final String methodName = ApplicationManagerRequest.CNAME + "#getPackageInstaller()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.packageInstaller);
        }

        return this.packageInstaller;
    }

    public final String getInstallerOptions()
    {
        final String methodName = ApplicationManagerRequest.CNAME + "#getInstallerOptions()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.installerOptions);
        }

        return this.installerOptions;
    }

    public final double getVersion()
    {
        final String methodName = ApplicationManagerRequest.CNAME + "#getVersion()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.version);
        }

        return this.version;
    }

    @Override
    public final String toString()
    {
        final String methodName = ApplicationManagerRequest.CNAME + "#toString()";

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
