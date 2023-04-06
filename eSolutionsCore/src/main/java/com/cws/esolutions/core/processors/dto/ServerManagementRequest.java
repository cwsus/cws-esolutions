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
 * File: ServerManagementRequest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.io.Serializable;
import java.lang.reflect.Field;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.utility.securityutils.processors.dto.RequestHostInfo;
import com.cws.esolutions.core.CoreServicesConstants;
import com.cws.esolutions.core.processors.enums.ServiceCheckType;
/**
 * @author cws-khuntly
 * @version 1.0
 * @see java.io.Serializable
 */
public class ServerManagementRequest implements Serializable
{
	private int startPage = 0;
    private String attribute = null;
    private String serviceId = null;
    private Server sourceServer = null;
    private Server targetServer = null;
    private String applicationId = null;
    private boolean installAgent = false;
    private String applicationName = null;
    private UserAccount userAccount = null;
    private RequestHostInfo requestInfo = null;
    private ServiceCheckType requestType = null;

    private static final long serialVersionUID = 784606731297721639L;
    private static final String CNAME = ServerManagementRequest.class.getName();

    private static final Logger DEBUGGER = LogManager.getLogger(CoreServicesConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setUserAccount(final UserAccount value)
    {
        final String methodName = ServerManagementRequest.CNAME + "#setUserAccount(final UserAccount value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.userAccount = value;
    }

    public final void setRequestInfo(final RequestHostInfo value)
    {
        final String methodName = ServerManagementRequest.CNAME + "#setRequestInfo(final RequestHostInfo value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.requestInfo = value;
    }

    public final void setServiceId(final String value)
    {
        final String methodName = ServerManagementRequest.CNAME + "#setServiceId(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serviceId = value;
    }

    public final void setApplicationName(final String value)
    {
        final String methodName = ServerManagementRequest.CNAME + "#setApplicationName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.applicationName = value;
    }

    public final void setApplicationId(final String value)
    {
        final String methodName = ServerManagementRequest.CNAME + "#setApplicationId(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.applicationId = value;
    }

    public final void setStartPage(final int value)
    {
        final String methodName = ServerManagementRequest.CNAME + "#setStartPage(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.startPage = value;
    }

    public final void setAttribute(final String value)
    {
        final String methodName = ServerManagementRequest.CNAME + "#setAttribute(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.attribute = value;
    }

    public final void setSourceServer(final Server value)
    {
        final String methodName = ServerManagementRequest.CNAME + "#setSourceServer(final Server value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.sourceServer = value;
    }

    public final void setTargetServer(final Server value)
    {
        final String methodName = ServerManagementRequest.CNAME + "#setTargetServer(final Server value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.targetServer = value;
    }

    public final void setRequestType(final ServiceCheckType value)
    {
        final String methodName = ServerManagementRequest.CNAME + "#setRequestType(final ServiceCheckType value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.requestType = value;
    }

    public final void setInstallAgent(final boolean value)
    {
        final String methodName = ServerManagementRequest.CNAME + "#setInstallAgent(final boolean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.installAgent = value;
    }

    public final UserAccount getUserAccount()
    {
        final String methodName = ServerManagementRequest.CNAME + "#getUserAccount()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.userAccount);
        }

        return this.userAccount;
    }

    public final RequestHostInfo getRequestInfo()
    {
        final String methodName = ServerManagementRequest.CNAME + "#getRequestInfo()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.requestInfo);
        }

        return this.requestInfo;
    }

    public final String getServiceId()
    {
        final String methodName = ServerManagementRequest.CNAME + "#getServiceId()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.serviceId);
        }

        return this.serviceId;
    }

    public final String getApplicationName()
    {
        final String methodName = ServerManagementRequest.CNAME + "#getApplicationName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.applicationName);
        }

        return this.applicationName;
    }

    public final String getApplicationId()
    {
        final String methodName = ServerManagementRequest.CNAME + "#getApplicationId()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.applicationId);
        }

        return this.applicationId;
    }

    public final int getStartPage()
    {
        final String methodName = ServerManagementRequest.CNAME + "#getStartPage()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.startPage);
        }

        return this.startPage;
    }

    public final String getAttribute()
    {
        final String methodName = ServerManagementRequest.CNAME + "#getAttribute()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.attribute);
        }

        return this.attribute;
    }

    public final Server getSourceServer()
    {
        final String methodName = ServerManagementRequest.CNAME + "#getSourceServer()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.sourceServer);
        }

        return this.sourceServer;
    }

    public final Server getTargetServer()
    {
        final String methodName = ServerManagementRequest.CNAME + "#getTargetServer()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.targetServer);
        }

        return this.targetServer;
    }

    public final ServiceCheckType getRequestType()
    {
        final String methodName = ServerManagementRequest.CNAME + "#getRequestType()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.requestType);
        }

        return this.requestType;
    }

    public final boolean installAgent()
    {
        final String methodName = ServerManagementRequest.CNAME + "#installAgent()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.installAgent);
        }

        return this.installAgent;
    }

    @Override
    public String toString()
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
