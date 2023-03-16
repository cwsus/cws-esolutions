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
package com.cws.esolutions.core.config.xml;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.config.xml
 * File: DeploymentConfig.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.io.File;
import java.util.List;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElementWrapper;

import com.cws.esolutions.core.CoreServicesConstants;
/**
 * @author cws-khuntly
 * @version 1.0
 * @see java.io.Serializable
 */
@XmlRootElement(name = "deployment-config")
@XmlAccessorType(XmlAccessType.NONE)
public final class DeploymentConfig implements Serializable
{
    private String controlFile = null;
    private File activeDeployDir = null;
    private File archiveDeployDir = null;
    private List<String> deploymentSuffixes = null;

    private static final long serialVersionUID = 3233640981512960130L;
    private static final String CNAME = DeploymentConfig.class.getName();

    private static final Logger DEBUGGER = LogManager.getLogger(CoreServicesConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setActiveDeployDir(final File value)
    {
        final String methodName = DeploymentConfig.CNAME + "#setActiveDeployDir(final File value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.activeDeployDir = value;
    }

    public final void setArchiveDeployDir(final File value)
    {
        final String methodName = DeploymentConfig.CNAME + "#setArchiveDeployDir(final File value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.archiveDeployDir = value;
    }

    public final void setControlFile(final String value)
    {
        final String methodName = DeploymentConfig.CNAME + "#setControlFile(final File value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.controlFile = value;
    }

    public final void setDeploymentSuffixes(final List<String> value)
    {
        final String methodName = DeploymentConfig.CNAME + "#setDeploymentSuffixes(final List<String> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.deploymentSuffixes = value;
    }

    @XmlElement(name = "deploymentActiveDirectory")
    public final File getActiveDeployDir()
    {
        final String methodName = DeploymentConfig.CNAME + "#getActiveDeployDir()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.activeDeployDir);
        }

        return this.activeDeployDir;
    }

    @XmlElement(name = "deploymentArchiveDirectory")
    public final File getArchiveDeployDir()
    {
        final String methodName = DeploymentConfig.CNAME + "#getArchiveDeployDir()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.archiveDeployDir);
        }

        return this.archiveDeployDir;
    }

    @XmlElement(name = "deploymentControlFile")
    public final String getControlFile()
    {
        final String methodName = DeploymentConfig.CNAME + "#getControlFile()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.controlFile);
        }

        return this.controlFile;
    }

    @XmlElement(name = "deploymentFileSuffixes")
    @XmlElementWrapper(name = "suffix")
    public final List<String> getDeploymentSuffixes()
    {
        final String methodName = DeploymentConfig.CNAME + "#getDeploymentSuffixes()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.deploymentSuffixes);
        }

        return this.deploymentSuffixes;
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
