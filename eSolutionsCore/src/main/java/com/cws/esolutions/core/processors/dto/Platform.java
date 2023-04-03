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
 * File: Server.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.util.List;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.cws.esolutions.core.CoreServicesConstants;
import com.cws.esolutions.core.processors.enums.ServiceRegion;
import com.cws.esolutions.core.processors.enums.ServiceStatus;
import com.cws.esolutions.core.processors.enums.NetworkPartition;
/**
 * @author cws-khuntly
 * @version 1.0
 * @see java.io.Serializable
 */
public class Platform implements Serializable
{
    private String platformGuid = null;
    private String platformName = null;
    private String platformDescription = null;
    private ServiceRegion platformRegion = null;
    private ServiceStatus platformStatus = null;
    private List<String> platformServers = null;
    private NetworkPartition platformPartition = null;

    private static final String CNAME = Platform.class.getName();
    private static final long serialVersionUID = -8528308516956592964L;

    private static final Logger DEBUGGER = LogManager.getLogger(CoreServicesConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setPlatformGuid(final String value)
    {
        final String methodName = Platform.CNAME + "#setPlatformGuid(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.platformGuid = value;
    }

    public final void setPlatformName(final String value)
    {
        final String methodName = Platform.CNAME + "#setPlatformName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.platformName = value;
    }

    public final void setPlatformDescription(final String value)
    {
        final String methodName = Platform.CNAME + "#setPlatformDescription(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.platformDescription = value;
    }

    public final void setPlatformRegion(final ServiceRegion value)
    {
        final String methodName = Platform.CNAME + "#setVirtualId(final ServiceRegion value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.platformRegion = value;
    }

    public final void setPlatformStatus(final ServiceStatus value)
    {
        final String methodName = Platform.CNAME + "#setPlatformStatus(final ServiceStatus value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.platformStatus = value;
    }

    public final void setPlatformServers(final List<String> value)
    {
        final String methodName = Platform.CNAME + "#setPlatformServers(final List<String> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.platformServers = value;
    }

    public final void setPlatformPartition(final NetworkPartition value)
    {
        final String methodName = Platform.CNAME + "#setPlatformPartition(final NetworkPartition value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.platformPartition = value;
    }
    public final String getPlatformGuid()
    {
        final String methodName = Platform.CNAME + "#getPlatformGuid()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.platformGuid);
        }

        return this.platformGuid;
    }

    public final String getPlatformName()
    {
        final String methodName = Platform.CNAME + "#getPlatformName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.platformName);
        }

        return this.platformName;
    }

    public final String getPlatformDescription()
    {
        final String methodName = Platform.CNAME + "#getPlatformDescription()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.platformDescription);
        }

        return this.platformDescription;
    }

    public final ServiceRegion getPlatformRegion()
    {
        final String methodName = Platform.CNAME + "#getVirtualId()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.platformRegion);
        }

        return this.platformRegion;
    }

    public final ServiceStatus getPlatformStatus()
    {
        final String methodName = Platform.CNAME + "#getPlatformStatus()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.platformStatus);
        }

        return this.platformStatus;
    }

    public final List<String> getPlatformServers()
    {
        final String methodName = Platform.CNAME + "#getPlatformServers()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.platformServers);
        }

        return this.platformServers;
    }

    public final NetworkPartition getPlatformPartition()
    {
        final String methodName = Platform.CNAME + "#getPlatformPartition()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.platformPartition);
        }

        return this.platformPartition;
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
