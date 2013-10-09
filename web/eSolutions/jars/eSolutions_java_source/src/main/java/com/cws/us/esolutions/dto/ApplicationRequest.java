/**
 * Copyright (c) 2009 - 2013 By: CWS, Inc.
 * 
 * All rights reserved. These materials are confidential and
 * proprietary to CaspersBox Web Services N.A and no part of
 * these materials should be reproduced, published in any form
 * by any means, electronic or mechanical, including photocopy
 * or any information storage or retrieval system not should
 * the materials be disclosed to third parties without the
 * express written authorization of CaspersBox Web Services, N.A.
 */
package com.cws.us.esolutions.dto;

import java.io.File;
import org.slf4j.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.core.Constants;
import com.cws.esolutions.core.processors.enums.AppServerType;
/**
 * eSolutions_java_source
 * com.cws.us.esolutions.dto
 * ApplicationRequest.java
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
 * 35033355 @ May 28, 2013 10:37:49 AM
 *     Created.
 */
public class ApplicationRequest implements Serializable
{
    private String scmPath = null;
    private String jvmName = null;
    private String project = null;
    private String platform = null;
    private String version = "1.0";
    private String logsPath = null;
    private String clusterName = null;
    private String installPath = null;
    private String pidDirectory = null;
    private String applicationGuid = null;
    private String applicationName = null;
    private File applicationBinary = null;
    private AppServerType serverType = null;

    private static final long serialVersionUID = -3668811202791320189L;
    private static final String CNAME = ApplicationRequest.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

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

    public final void setApplicationBinary(final File value)
    {
        final String methodName = ApplicationRequest.CNAME + "#setApplicationBinary(final File value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.applicationBinary = value;
    }

    public final void setVersion(final String value)
    {
        final String methodName = ApplicationRequest.CNAME + "#setVersion(final String value)";

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

    public final void setProject(final String value)
    {
        final String methodName = ApplicationRequest.CNAME + "#setProject(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.project = value;
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

    public final void setServerType(final AppServerType value)
    {
        final String methodName = ApplicationRequest.CNAME + "#setServerType(final AppServerType value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serverType = value;
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

    public final File getApplicationBinary()
    {
        final String methodName = ApplicationRequest.CNAME + "#getApplicationBinary()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.applicationBinary);
        }

        return this.applicationBinary;
    }

    public final String getVersion()
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

    public final String getProject()
    {
        final String methodName = ApplicationRequest.CNAME + "#getProject()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.project);
        }

        return this.project;
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

    public final AppServerType getServerType()
    {
        final String methodName = ApplicationRequest.CNAME + "#getServerType()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.serverType);
        }

        return this.serverType;
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
