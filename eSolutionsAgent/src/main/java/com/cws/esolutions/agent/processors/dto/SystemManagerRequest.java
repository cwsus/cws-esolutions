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

import org.slf4j.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.agent.Constants;
import com.cws.esolutions.agent.processors.enums.SystemCheckType;
import com.cws.esolutions.agent.processors.enums.ListOperationType;
import com.cws.esolutions.agent.processors.enums.StateManagementType;
import com.cws.esolutions.agent.processors.enums.ServiceOperationType;
import com.cws.esolutions.agent.processors.enums.SystemManagementType;
/**
 * eSolutionsAgent
 * com.cws.esolutions.agent.processors.dto
 * SystemManagerRequest.java
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
 * kh05451 @ Oct 29, 2012 9:44:46 AM
 *     Created.
 */
public class SystemManagerRequest implements Serializable
{
    private int portNumber = 0;
    private String targetServer = null;
    private String extTargetDir = null;
    private boolean installAgent = false;
    private SystemCheckType requestType = null;
    private SystemManagementType mgmtType = null;
    private ServiceOperationType serviceType = null;
    private StateManagementType stateMgmtType = null;
    private ListOperationType listOperationType = null;

    private static final long serialVersionUID = 2750044850950337356L;
    private static final String CNAME = SystemManagerRequest.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setMgmtType(final SystemManagementType value)
    {
        final String methodName = SystemManagerRequest.CNAME + "#setMgmtType(final SystemManagementType value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.mgmtType = value;
    }

    public final void setTargetServer(final String value)
    {
        final String methodName = SystemManagerRequest.CNAME + "#setTargetServer(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.targetServer = value;
    }

    public final void setRequestType(final SystemCheckType value)
    {
        final String methodName = SystemManagerRequest.CNAME + "#setRequestType(final SystemCheckType value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.requestType = value;
    }

    public final void setServiceType(final ServiceOperationType value)
    {
        final String methodName = SystemManagerRequest.CNAME + "#setServiceType(final ServiceOperationType value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serviceType = value;
    }

    public final void setPortNumber(final int value)
    {
        final String methodName = SystemManagerRequest.CNAME + "#setPortNumber(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.portNumber = value;
    }

    public final void setExtTargetDir(final String value)
    {
        final String methodName = SystemManagerRequest.CNAME + "#setExtTargetDir(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.extTargetDir = value;
    }

    public final void setInstallAgent(final boolean value)
    {
        final String methodName = SystemManagerRequest.CNAME + "#setInstallAgent(final boolean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.installAgent = value;
    }

    public final void setListOperationType(final ListOperationType value)
    {
        final String methodName = SystemManagerRequest.CNAME + "#setListOperationType(final ListOperationType value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.listOperationType = value;
    }

    public final void setStateMgmtType(final StateManagementType value)
    {
        final String methodName = SystemManagerRequest.CNAME + "#setStateMgmtType(final StateManagementType value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.stateMgmtType = value;
    }

    public final SystemManagementType getMgmtType()
    {
        final String methodName = SystemManagerRequest.CNAME + "#getMgmtType()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.mgmtType);
        }

        return this.mgmtType;
    }

    public final String getTargetServer()
    {
        final String methodName = SystemManagerRequest.CNAME + "#getTargetServer()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.targetServer);
        }

        return this.targetServer;
    }

    public final SystemCheckType getRequestType()
    {
        final String methodName = SystemManagerRequest.CNAME + "#getRequestType()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.requestType);
        }

        return this.requestType;
    }

    public final ServiceOperationType getServiceType()
    {
        final String methodName = SystemManagerRequest.CNAME + "#getServiceType()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.serviceType);
        }

        return this.serviceType;
    }

    public final int getPortNumber()
    {
        final String methodName = SystemManagerRequest.CNAME + "#getPortNumber()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.portNumber);
        }

        return this.portNumber;
    }

    public final String getExtTargetDir()
    {
        final String methodName = SystemManagerRequest.CNAME + "#getExtTargetDir()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.extTargetDir);
        }

        return this.extTargetDir;
    }

    public final boolean installAgent()
    {
        final String methodName = SystemManagerRequest.CNAME + "#installAgent()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.installAgent);
        }

        return this.installAgent;
    }

    public final ListOperationType getListOperationType()
    {
        final String methodName = SystemManagerRequest.CNAME + "#getListOperationType()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.listOperationType);
        }

        return this.listOperationType;
    }

    public final StateManagementType getStateMgmtType()
    {
        final String methodName = SystemManagerRequest.CNAME + "#getStateMgmtType()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.stateMgmtType);
        }

        return this.stateMgmtType;
    }

    @Override
    public final String toString()
    {
        final String methodName = SystemManagerRequest.CNAME + "#toString()";

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
