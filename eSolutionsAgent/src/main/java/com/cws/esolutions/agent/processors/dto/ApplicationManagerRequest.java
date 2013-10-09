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
package com.cws.esolutions.agent.processors.dto;

import java.io.File;
import org.slf4j.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.agent.Constants;
import com.cws.esolutions.agent.processors.enums.DeploymentType;
import com.cws.esolutions.agent.processors.enums.StateManagementType;
import com.cws.esolutions.agent.processors.enums.ApplicationManagementType;
/**
 * eSolutionsAgent
 * com.cws.esolutions.agent.processors.dto
 * ApplicationManagerRequest.java
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
 * 35033355 @ Apr 1, 2013 4:12:25 PM
 *     Created.
 */
public class ApplicationManagerRequest implements Serializable
{
    private int timeoutValue = 0;
    private File appBinary = null;
    private String jvmName = null;
    private String applName = null;
    private String processId = null;
    private String targetFile = null;
    private String targetServer = null;
    private String rootDirectory = null;
    private String deploymentFile = null;
    private String targetDirectory = null;
    private boolean forceOperation = false;
    private DeploymentType deploymentType = null;
    private StateManagementType stateMgmtType = null;
    private ApplicationManagementType mgmtType = null;

    private static final long serialVersionUID = -6382475745170249113L;
    private static final String CNAME = ApplicationManagerRequest.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setProcessId(final String value)
    {
        final String methodName = ApplicationManagerRequest.CNAME + "#setProcessId(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.processId = value;
    }

    public final void setMgmtType(final ApplicationManagementType value)
    {
        final String methodName = ApplicationManagerRequest.CNAME + "#setMgmtType(final ApplicationManagementType value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.mgmtType = value;
    }

    public final void setApplName(final String value)
    {
        final String methodName = ApplicationManagerRequest.CNAME + "#setApplName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.applName = value;
    }

    public final void setAppBinary(final File value)
    {
        final String methodName = ApplicationManagerRequest.CNAME + "#setAppBinary(final File value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.appBinary = value;
    }

    public final void setTargetServer(final String value)
    {
        final String methodName = ApplicationManagerRequest.CNAME + "#setTargetServer(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.targetServer = value;
    }

    public final void setRootDirectory(final String value)
    {
        final String methodName = ApplicationManagerRequest.CNAME + "#setRootDirectory(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.rootDirectory = value;
    }

    public final void setTargetDirectory(final String value)
    {
        final String methodName = ApplicationManagerRequest.CNAME + "#setTargetDirectory(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.targetDirectory = value;
    }

    public final void setTargetFile(final String value)
    {
        final String methodName = ApplicationManagerRequest.CNAME + "#setTargetFile(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.targetFile = value;
    }

    public final void setStateMgmtType(final StateManagementType value)
    {
        final String methodName = ApplicationManagerRequest.CNAME + "#setAppRequestType(final StateManagementType value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.stateMgmtType = value;
    }

    public final void setDeploymentType(final DeploymentType value)
    {
        final String methodName = ApplicationManagerRequest.CNAME + "#setAppRequestType(final DeploymentType value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.deploymentType = value;
    }

    public final void setTimeoutValue(final int value)
    {
        final String methodName = ApplicationManagerRequest.CNAME + "#setTimeoutValue(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.timeoutValue = value;
    }

    public final void setJvmName(final String value)
    {
        final String methodName = ApplicationManagerRequest.CNAME + "#setJvmName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.jvmName = value;
    }

    public final void setForceOperation(final boolean value)
    {
        final String methodName = ApplicationManagerRequest.CNAME + "#setForceOperation(final boolean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.forceOperation = value;
    }

    public final void setDeploymentFile(final String value)
    {
        final String methodName = ApplicationManagerRequest.CNAME + "#setDeploymentFile(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.deploymentFile = value;
    }

    public final String getProcessId()
    {
        final String methodName = ApplicationManagerRequest.CNAME + "#getProcessId()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.processId);
        }

        return this.processId;
    }

    public final ApplicationManagementType getMgmtType()
    {
        final String methodName = ApplicationManagerRequest.CNAME + "#getMgmtType()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.mgmtType);
        }

        return this.mgmtType;
    }

    public final String getApplName()
    {
        final String methodName = ApplicationManagerRequest.CNAME + "#getApplName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.applName);
        }

        return this.applName;
    }

    public final File getAppBinary()
    {
        final String methodName = ApplicationManagerRequest.CNAME + "#getAppBinary()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.appBinary);
        }

        return this.appBinary;
    }

    public final String getTargetServer()
    {
        final String methodName = ApplicationManagerRequest.CNAME + "#getTargetServer()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.targetServer);
        }

        return this.targetServer;
    }

    public final String getRootDirectory()
    {
        final String methodName = ApplicationManagerRequest.CNAME + "#getRootDirectory()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.rootDirectory);
        }

        return this.rootDirectory;
    }

    public final String getTargetDirectory()
    {
        final String methodName = ApplicationManagerRequest.CNAME + "#getTargetDirectory()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.targetDirectory);
        }

        return this.targetDirectory;
    }

    public final String getTargetFile()
    {
        final String methodName = ApplicationManagerRequest.CNAME + "#getTargetFile()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.targetFile);
        }

        return this.targetFile;
    }

    public final StateManagementType getStateMgmtType()
    {
        final String methodName = ApplicationManagerRequest.CNAME + "#getStateMgmtType()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.stateMgmtType);
        }

        return this.stateMgmtType;
    }

    public final DeploymentType getDeploymentType()
    {
        final String methodName = ApplicationManagerRequest.CNAME + "#getDeploymentType()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.deploymentType);
        }

        return this.deploymentType;
    }

    public final int getTimeoutValue()
    {
        final String methodName = ApplicationManagerRequest.CNAME + "#getTimeoutValue()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.timeoutValue);
        }

        return this.timeoutValue;
    }

    public final String getJvmName()
    {
        final String methodName = ApplicationManagerRequest.CNAME + "#getJvmName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.jvmName);
        }

        return this.jvmName;
    }

    public final boolean forceOperation()
    {
        final String methodName = ApplicationManagerRequest.CNAME + "#forceOperation()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.forceOperation);
        }

        return this.forceOperation;
    }

    public final String getDeploymentFile()
    {
        final String methodName = ApplicationManagerRequest.CNAME + "#getDeploymentFile()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.deploymentFile);
        }

        return this.deploymentFile;
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
