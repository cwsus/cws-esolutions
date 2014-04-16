/*
 * Copyright (c) 2009 - 2014 CaspersBox Web Services
 * 
 * All rights reserved. These materials are confidential and
 * proprietary to CaspersBox Web Services N.A and no part of
 * these materials should be reproduced, published in any form
 * by any means, electronic or mechanical, including photocopy
 * or any information storage or retrieval system not should
 * the materials be disclosed to third parties without the
 * express written authorization of CaspersBox Web Services, N.A.
 */
package com.cws.esolutions.web.dto;
/*
 * Project: eSolutions_java_source
 * Package: com.cws.esolutions.web.dto
 * File: ApplicationRequest.java
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
import org.springframework.web.multipart.MultipartFile;

import com.cws.esolutions.web.Constants;
import com.cws.esolutions.core.processors.enums.DeploymentType;
/**
 * TODO: Add class information/description
 *
 * @author 35033355
 * @version 1.0
 */
public class ApplicationRequest implements Serializable
{
    private double version = 0.0;
    private String scmPath = null;
    private String jvmName = null;
    private String basePath = null;
    private String platform = null;
    private String logsPath = null;
    private String clusterName = null;
    private String installPath = null;
    private String pidDirectory = null;
    private boolean isScmEnabled = false;
    private String applicationGuid = null;
    private String applicationName = null;
    private DeploymentType deploymentType = null;
    private MultipartFile applicationBinary = null;

    private static final long serialVersionUID = -3668811202791320189L;
    private static final String CNAME = ApplicationRequest.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER);

    public final void setApplicationGuid(final String value)
    {
        final String methodName = ApplicationRequest.CNAME + "#setApplicationGuid(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.applicationGuid = value;
    }

    public final void setApplicationName(final String value)
    {
        final String methodName = ApplicationRequest.CNAME + "#setApplicationName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.applicationName = value;
    }

    public final void setPlatform(final String value)
    {
        final String methodName = ApplicationRequest.CNAME + "#setPlatform(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.platform = value;
    }

    public final void setApplicationBinary(final MultipartFile value)
    {
        final String methodName = ApplicationRequest.CNAME + "#setApplicationBinary(final MultipartFile value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.applicationBinary = value;
    }

    public final void setVersion(final double value)
    {
        final String methodName = ApplicationRequest.CNAME + "#setVersion(final double value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.version = value;
    }

    public final void setClusterName(final String value)
    {
        final String methodName = ApplicationRequest.CNAME + "#setClusterName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.clusterName = value;
    }

    public final void setBasePath(final String value)
    {
        final String methodName = ApplicationRequest.CNAME + "#setBasePath(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.basePath = value;
    }

    public final void setLogsPath(final String value)
    {
        final String methodName = ApplicationRequest.CNAME + "#setLogsPath(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.logsPath = value;
    }

    public final void setInstallPath(final String value)
    {
        final String methodName = ApplicationRequest.CNAME + "#setInstallPath(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.installPath = value;
    }

    public final void setPidDirectory(final String value)
    {
        final String methodName = ApplicationRequest.CNAME + "#setPidDirectory(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.pidDirectory = value;
    }

    public final void setScmPath(final String value)
    {
        final String methodName = ApplicationRequest.CNAME + "#setScmPath(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.scmPath = value;
    }

    public final void setJvmName(final String value)
    {
        final String methodName = ApplicationRequest.CNAME + "#setJvmName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.jvmName = value;
    }

    public final void setDeploymentType(final DeploymentType value)
    {
        final String methodName = ApplicationRequest.CNAME + "#setDeploymentType(final DeploymentType value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.deploymentType = value;
    }

    public final void setIsScmEnabled(final boolean value)
    {
        final String methodName = ApplicationRequest.CNAME + "#setIsScmEnabled(final boolean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.isScmEnabled = value;
    }

    public final String getApplicationGuid()
    {
        final String methodName = ApplicationRequest.CNAME + "#getApplicationGuid()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.applicationGuid);
        }

        return this.applicationGuid;
    }

    public final String getApplicationName()
    {
        final String methodName = ApplicationRequest.CNAME + "#getApplicationName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.applicationName);
        }

        return this.applicationName;
    }

    public final String getPlatform()
    {
        final String methodName = ApplicationRequest.CNAME + "#getPlatform()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.platform);
        }

        return this.platform;
    }

    public final MultipartFile getApplicationBinary()
    {
        final String methodName = ApplicationRequest.CNAME + "#getApplicationBinary()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.applicationBinary);
        }

        return this.applicationBinary;
    }

    public final double getVersion()
    {
        final String methodName = ApplicationRequest.CNAME + "#getVersion()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.version);
        }

        return this.version;
    }

    public final String getClusterName()
    {
        final String methodName = ApplicationRequest.CNAME + "#getClusterName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.clusterName);
        }

        return this.clusterName;
    }

    public final String getBasePath()
    {
        final String methodName = ApplicationRequest.CNAME + "#getBasePath()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.basePath);
        }

        return this.basePath;
    }

    public final String getLogsPath()
    {
        final String methodName = ApplicationRequest.CNAME + "#getLogsPath()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.logsPath);
        }

        return this.logsPath;
    }

    public final String getInstallPath()
    {
        final String methodName = ApplicationRequest.CNAME + "#getInstallPath()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.installPath);
        }

        return this.installPath;
    }

    public final String getPidDirectory()
    {
        final String methodName = ApplicationRequest.CNAME + "#getPidDirectory()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.pidDirectory);
        }

        return this.pidDirectory;
    }

    public final String getScmPath()
    {
        final String methodName = ApplicationRequest.CNAME + "#getScmPath()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.scmPath);
        }

        return this.scmPath;
    }

    public final String getJvmName()
    {
        final String methodName = ApplicationRequest.CNAME + "#getJvmName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.jvmName);
        }

        return this.jvmName;
    }

    public final DeploymentType getDeploymentType()
    {
        final String methodName = ApplicationRequest.CNAME + "#getDeploymentType()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.deploymentType);
        }

        return this.deploymentType;
    }

    public final boolean getIsScmEnabled()
    {
        final String methodName = ApplicationRequest.CNAME + "#getIsScmEnabled()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.isScmEnabled);
        }

        return this.isScmEnabled;
    }

    public final boolean isScmEnabled()
    {
        final String methodName = ApplicationRequest.CNAME + "#isScmEnabled()";

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
        final String methodName = ApplicationRequest.CNAME + "#toString()";

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