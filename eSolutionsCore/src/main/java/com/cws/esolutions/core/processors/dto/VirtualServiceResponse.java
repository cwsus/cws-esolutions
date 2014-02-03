/*
 * Copyright (c) 2009 - 2014 CaspersBox Web Services
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
 * File: VirtualServiceResponse.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.List;
import org.slf4j.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.core.CoreServiceConstants;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 */
public class VirtualServiceResponse implements Serializable
{
    private String apiVersion = null;
    private String hostVersion = null;
    private boolean isComplete = false;
    private VirtualServer server = null;
    private List<VirtualServer> serverList = null;
    private CoreServicesStatus requestStatus = null;

    private static final long serialVersionUID = -2100329293282061334L;
    private static final String CNAME = VirtualServiceResponse.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(CoreServiceConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(CoreServiceConstants.ERROR_LOGGER);

    public final void setRequestStatus(final CoreServicesStatus value)
    {
        final String methodName = VirtualServiceResponse.CNAME + "#setRequestStatus(final CoreServicesStatus value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.requestStatus = value;
    }

    public final void setApiVersion(final String value)
    {
        final String methodName = VirtualServiceResponse.CNAME + "#setApiVersion(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.apiVersion = value;
    }

    public final void setHostVersion(final String value)
    {
        final String methodName = VirtualServiceResponse.CNAME + "#setHostVersion(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.hostVersion = value;
    }

    public final void setServer(final VirtualServer value)
    {
        final String methodName = VirtualServiceResponse.CNAME + "#setMachines(final VirtualServer value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.server = value;
    }

    public final void setServerList(final List<VirtualServer> value)
    {
        final String methodName = VirtualServiceResponse.CNAME + "#setServerList(final List<VirtualServer> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serverList = value;
    }

    public final void setIsComplete(final boolean value)
    {
        final String methodName = VirtualServiceResponse.CNAME + "#setIsComplete(final boolean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.isComplete = value;
    }

    public final CoreServicesStatus getRequestStatus()
    {
        final String methodName = VirtualServiceResponse.CNAME + "#getRequestStatus()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.requestStatus);
        }

        return this.requestStatus;
    }

    public final VirtualServer getServer()
    {
        final String methodName = VirtualServiceResponse.CNAME + "#getMachines()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.server);
        }

        return this.server;
    }

    public final List<VirtualServer> getServerList()
    {
        final String methodName = VirtualServiceResponse.CNAME + "#getServerList()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.serverList);
        }

        return this.serverList;
    }

    public final boolean isComplete()
    {
        final String methodName = VirtualServiceResponse.CNAME + "#isComplete()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.isComplete);
        }

        return this.isComplete;
    }

    public final String getApiVersion()
    {
        final String methodName = VirtualServiceResponse.CNAME + "#getApiVersion()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.apiVersion);
        }

        return this.apiVersion;
    }

    public final String getHostVersion()
    {
        final String methodName = VirtualServiceResponse.CNAME + "#getHostVersion()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.hostVersion);
        }

        return this.hostVersion;
    }

    @Override
    public final String toString()
    {
        final String methodName = VirtualServiceResponse.CNAME + "#toString()";

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
