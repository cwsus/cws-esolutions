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
 * File: VirtualServer.java
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
import org.virtualbox_4_2.FirmwareType;
import org.virtualbox_4_2.MachineState;

import com.cws.esolutions.core.CoreServiceConstants;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 */
public class VirtualServer implements Serializable
{
    private String name = null;
    private String guid = null;
    private Long cpuCount = null;
    private String hwUuid = null;
    private Long memorySize = null;
    private String osTypeId = null;
    private String hwVersion = null;
    private String description = null;
    private MachineState state = null;
    private List<String> groups = null;
    private FirmwareType firmwareType = null;

    private static final long serialVersionUID = 1078898688247607489L;
    private static final String CNAME = VirtualServer.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(CoreServiceConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(CoreServiceConstants.ERROR_LOGGER);

    public final void setGuid(final String value)
    {
        final String methodName = VirtualServer.CNAME + "#setGuid(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.guid = value;
    }

    public final void setName(final String value)
    {
        final String methodName = VirtualServer.CNAME + "#setName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.name = value;
    }

    public final void setDescription(final String value)
    {
        final String methodName = VirtualServer.CNAME + "#setDescription(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.description = value;
    }

    public final void setCpuCount(final Long value)
    {
        final String methodName = VirtualServer.CNAME + "#setCpuCount(final Long value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.cpuCount = value;
    }

    public final void setHwUuid(final String value)
    {
        final String methodName = VirtualServer.CNAME + "#setHwUuid(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.hwUuid = value;
    }

    public final void setMemorySize(final Long value)
    {
        final String methodName = VirtualServer.CNAME + "#setMemorySize(final Long value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.memorySize = value;
    }

    public final void setOsTypeId(final String value)
    {
        final String methodName = VirtualServer.CNAME + "#setOsTypeId(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.osTypeId = value;
    }

    public final void setHwVersion(final String value)
    {
        final String methodName = VirtualServer.CNAME + "#setHwVersion(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.hwVersion = value;
    }

    public final void setState(final MachineState value)
    {
        final String methodName = VirtualServer.CNAME + "#setState(final MachineState value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.state = value;
    }

    public final void setFirmwareType(final FirmwareType value)
    {
        final String methodName = VirtualServer.CNAME + "#setFirmwareType(final FirmwareType value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.firmwareType = value;
    }

    public final void setGroups(final List<String> value)
    {
        final String methodName = VirtualServer.CNAME + "#setGroups(final List<String> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.groups = value;
    }

    public final String getGuid()
    {
        final String methodName = VirtualServer.CNAME + "#getGuid()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.guid);
        }

        return this.guid;
    }

    public final String getName()
    {
        final String methodName = VirtualServer.CNAME + "#getName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.name);
        }

        return this.name;
    }

    public final String getDescription()
    {
        final String methodName = VirtualServer.CNAME + "#getDescription()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.description);
        }

        return this.description;
    }

    public final Long getCpuCount()
    {
        final String methodName = VirtualServer.CNAME + "#getCpuCount()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.cpuCount);
        }

        return this.cpuCount;
    }

    public final String getHwUuid()
    {
        final String methodName = VirtualServer.CNAME + "#getHwUuid()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.hwUuid);
        }

        return this.hwUuid;
    }

    public final Long getMemorySize()
    {
        final String methodName = VirtualServer.CNAME + "#getMemorySize()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.memorySize);
        }

        return this.memorySize;
    }

    public final String getOsTypeId()
    {
        final String methodName = VirtualServer.CNAME + "#getOsTypeId()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.osTypeId);
        }

        return this.osTypeId;
    }

    public final String getHwVersion()
    {
        final String methodName = VirtualServer.CNAME + "#getHwVersion()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.hwVersion);
        }

        return this.hwVersion;
    }

    public final MachineState getState()
    {
        final String methodName = VirtualServer.CNAME + "#getState()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.state);
        }

        return this.state;
    }

    public final FirmwareType getFirmwareType()
    {
        final String methodName = VirtualServer.CNAME + "#getFirmwareType()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.firmwareType);
        }

        return this.firmwareType;
    }

    public final List<String> getGroups()
    {
        final String methodName = VirtualServer.CNAME + "#getGroups()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.groups);
        }

        return this.groups;
    }

    @Override
    public final String toString()
    {
        final String methodName = VirtualServer.CNAME + "#toString()";

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
