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
package com.cws.esolutions.agent.jmx.mbeans.dto;

import java.io.File;
import org.slf4j.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.agent.Constants;
import com.cws.esolutions.agent.jmx.mbeans.enums.MBeanRequestType;
/**
 * eSolutionsAgent
 * com.cws.esolutions.agent.jmx.mbeans.dto
 * MBeanRequest.java
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
 * kh05451 @ Oct 29, 2012 10:33:14 AM
 *     Created.
 */
public class MBeanRequest implements Serializable
{
    private File binary = null;
    private String cellName = null;
    private int requestTimeout = 120;
    private String targetName = null;
    private String virtualHost = null;
    private String appVersion = "1.0";
    private String clusterName = null;
    private String installPath = null;
    private String application = null;
    private boolean isLibrary = false;
    private String nodeAgentName = null;
    private boolean forceOperation = false;
    private MBeanRequestType requestType = null;

    private static final String CNAME = MBeanRequest.class.getName();
    private static final long serialVersionUID = -1852529687446941508L;

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setAppVersion(final String value)
    {
        final String methodName = MBeanRequest.CNAME + "#setAppVersion(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.appVersion = value;
    }

    public final void setVirtualHost(final String value)
    {
        final String methodName = MBeanRequest.CNAME + "#setVirtualHost(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.virtualHost = value;
    }

    public final void setInstallPath(final String value)
    {
        final String methodName = MBeanRequest.CNAME + "#setInstallPath(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.installPath = value;
    }

    public final void setTargetName(final String value)
    {
        final String methodName = MBeanRequest.CNAME + "#setTargetName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.targetName = value;
    }

    public final void setBinary(final File value)
    {
        final String methodName = MBeanRequest.CNAME + "#setBinary(final File value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.binary = value;
    }

    public final void setIsLibrary(final boolean value)
    {
        final String methodName = MBeanRequest.CNAME + "#setIsLibrary(final boolean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.isLibrary = value;
    }

    public final void setCellName(final String value)
    {
        final String methodName = MBeanRequest.CNAME + "#setCellName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.cellName = value;
    }

    public final void setClusterName(final String value)
    {
        final String methodName = MBeanRequest.CNAME + "#setClusterName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.clusterName = value;
    }

    public final void setRequestType(final MBeanRequestType value)
    {
        final String methodName = MBeanRequest.CNAME + "#setRequestType(final MBeanRequestType value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.requestType = value;
    }

    public final void setForceOperation(final boolean value)
    {
        final String methodName = MBeanRequest.CNAME + "#setForceOperation(final boolean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.forceOperation = value;
    }

    public final void setApplication(final String value)
    {
        final String methodName = MBeanRequest.CNAME + "#setApplicationName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.application = value;
    }

    public final void setRequestTimeout(final int value)
    {
        final String methodName = MBeanRequest.CNAME + "#setRequestTimeout(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.requestTimeout = value;
    }

    public final void setNodeAgentName(final String value)
    {
        final String methodName = MBeanRequest.CNAME + "#setNodeAgentName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.nodeAgentName = value;
    }

    public final MBeanRequestType getRequestType()
    {
        final String methodName = MBeanRequest.CNAME + "#getRequestType()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.requestType);
        }

        return this.requestType;
    }

    public final String getAppVersion()
    {
        final String methodName = MBeanRequest.CNAME + "#getAppVersion()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.appVersion);
        }

        return this.appVersion;
    }

    public final String getInstallPath()
    {
        final String methodName = MBeanRequest.CNAME + "#getInstallPath()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.installPath);
        }

        return this.installPath;
    }

    public final String getVirtualHost()
    {
        final String methodName = MBeanRequest.CNAME + "#getVirtualHost()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.virtualHost);
        }

        return this.virtualHost;
    }

    public final File getBinary()
    {
        final String methodName = MBeanRequest.CNAME + "#getBinary()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.binary);
        }

        return this.binary;
    }

    public final boolean isLibrary()
    {
        final String methodName = MBeanRequest.CNAME + "#isLibrary()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.isLibrary);
        }

        return this.isLibrary;
    }

    public final String getTargetName()
    {
        final String methodName = MBeanRequest.CNAME + "#getTargetName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.targetName);
        }

        return this.targetName;
    }

    public final String getClusterName()
    {
        final String methodName = MBeanRequest.CNAME + "#getClusterName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.clusterName);
        }

        return this.clusterName;
    }

    public final boolean getForceOperation()
    {
        final String methodName = MBeanRequest.CNAME + "#getForceOperation()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.forceOperation);
        }

        return this.forceOperation;
    }

    public final int getRequestTimeout()
    {
        final String methodName = MBeanRequest.CNAME + "#getRequestTimeout()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.requestTimeout);
        }

        return this.requestTimeout;
    }

    public final String getApplication()
    {
        final String methodName = MBeanRequest.CNAME + "#getApplication()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.application);
        }

        return this.application;
    }

    public final String getNodeAgentName()
    {
        final String methodName = MBeanRequest.CNAME + "#getNodeAgentName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.nodeAgentName);
        }

        return this.nodeAgentName;
    }

    public final String getCellName()
    {
        final String methodName = MBeanRequest.CNAME + "#getCellName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.cellName);
        }

        return this.cellName;
    }

    @Override
    public final String toString()
    {
        final String methodName = MBeanRequest.CNAME + "#toString()";

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
