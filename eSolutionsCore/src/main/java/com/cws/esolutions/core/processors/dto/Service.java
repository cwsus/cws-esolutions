/*
 * Copyright (c) 2009 - 2017 CaspersBox Web Services
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
 * File: Service.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly   		11/23/2008 22:39:20             Created.
 */
import java.util.List;
import org.slf4j.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.core.CoreServiceConstants;
import com.cws.esolutions.core.processors.enums.ServiceType;
import com.cws.esolutions.core.processors.enums.ServiceRegion;
import com.cws.esolutions.core.processors.enums.ServiceStatus;
import com.cws.esolutions.core.processors.enums.NetworkPartition;
/**
 * @author cws-khuntly
 * @version 1.0
 * @see java.io.Serializable
 */
public class Service implements Serializable
{
    private double score = 0.0;
    private String guid = null;
    private String name = null;
    private ServiceType type = null;
    private String description = null;
    private ServiceStatus status = null;
    private List<Server> servers = null;
    private ServiceRegion region = null;
    private NetworkPartition partition = null;

    private static final String CNAME = Service.class.getName();
    private static final long serialVersionUID = -4382390818947300324L;

    private static final Logger DEBUGGER = LoggerFactory.getLogger(CoreServiceConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(CoreServiceConstants.ERROR_LOGGER);

    public final void setScore(final double value)
    {
        final String methodName = Service.CNAME + "#setScore(final double value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.score = value;
    }

    public final void setGuid(final String value)
    {
        final String methodName = Service.CNAME + "#setGuid(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.guid = value;
    }

    public final void setType(final ServiceType value)
    {
        final String methodName = Service.CNAME + "#setType(final ServiceType value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.type = value;
    }

    public final void setName(final String value)
    {
        final String methodName = Service.CNAME + "#setName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.name = value;
    }

    public final void setDescription(final String value)
    {
        final String methodName = Service.CNAME + "#setDescription(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.description = value;
    }

    public final void setStatus(final ServiceStatus value)
    {
        final String methodName = Service.CNAME + "#setStatus(final ServiceStatus value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.status = value;
    }

    public final void setServers(final List<Server> value)
    {
        final String methodName = Service.CNAME + "#setServers(final List<Server> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.servers = value;
    }

    public final void setRegion(final ServiceRegion value)
    {
        final String methodName = Service.CNAME + "#setRegion(final ServiceRegion value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.region = value;
    }

    public final void setPartition(final NetworkPartition value)
    {
        final String methodName = Service.CNAME + "#setPartition(final NetworkPartition value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.partition = value;
    }

    public final double getScore()
    {
        final String methodName = Service.CNAME + "#getScore()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.score);
        }

        return this.score;
    }

    public final String getGuid()
    {
        final String methodName = Service.CNAME + "#getGuid()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.guid);
        }

        return this.guid;
    }

    public final ServiceType getType()
    {
        final String methodName = Service.CNAME + "#getType()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.type);
        }

        return this.type;
    }

    public final String getName()
    {
        final String methodName = Service.CNAME + "#getName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.name);
        }

        return this.name;
    }

    public final String getDescription()
    {
        final String methodName = Service.CNAME + "#getDescription()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.description);
        }

        return this.description;
    }

    public final ServiceStatus getStatus()
    {
        final String methodName = Service.CNAME + "#getStatus()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.status);
        }

        return this.status;
    }

    public final List<Server> getServers()
    {
        final String methodName = Service.CNAME + "#getServers()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.servers);
        }

        return this.servers;
    }

    public final ServiceRegion getRegion()
    {
        final String methodName = Service.CNAME + "#getRegion()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.region);
        }

        return this.region;
    }

    public final NetworkPartition getPartition()
    {
        final String methodName = Service.CNAME + "#getPartition()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.partition);
        }

        return this.partition;
    }

    @Override
    public final String toString()
    {
        final String methodName = Service.CNAME + "#toString()";

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
