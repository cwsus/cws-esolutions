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
package com.cws.esolutions.core.processors.dto;

import java.util.List;
import org.slf4j.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.core.Constants;
import com.cws.esolutions.core.processors.enums.ServiceRegion;
import com.cws.esolutions.core.processors.enums.ServiceStatus;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.processors.dto
 * Article.java
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
 * kh05451 @ Oct 30, 2012 2:51:43 PM
 *     Created.
 */
public class Platform implements Serializable
{
    private String description = null;
    private String platformGuid = null;
    private String platformName = null;
    private Server platformDmgr = null;
    private ServiceStatus status = null;
    private List<Server> appServers = null;
    private List<Server> webServers = null;
    private ServiceRegion platformRegion = null;

    private static final String CNAME = Platform.class.getName();
    private static final long serialVersionUID = -4382390818947300324L;

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER);

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

    public final void setDescription(final String value)
    {
        final String methodName = Platform.CNAME + "#setDescription(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.description = value;
    }

    public final void setStatus(final ServiceStatus value)
    {
        final String methodName = Platform.CNAME + "#setStatus(final ServiceStatus value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.status = value;
    }

    public final void setPlatformDmgr(final Server value)
    {
        final String methodName = Platform.CNAME + "#setPlatformDmgr(final Server value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.platformDmgr = value;
    }

    public final void setAppServers(final List<Server> value)
    {
        final String methodName = Platform.CNAME + "#setAppServers(final List<Server> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.appServers = value;
    }

    public final void setWebServers(final List<Server> value)
    {
        final String methodName = Platform.CNAME + "#setWebServers(final List<Server> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.webServers = value;
    }

    public final void setPlatformRegion(final ServiceRegion value)
    {
        final String methodName = Platform.CNAME + "#setPlatformRegion(final ServiceRegion value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.platformRegion = value;
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

    public final String getDescription()
    {
        final String methodName = Platform.CNAME + "#getDescription()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.description);
        }

        return this.description;
    }

    public final ServiceStatus getStatus()
    {
        final String methodName = Platform.CNAME + "#getStatus()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.status);
        }

        return this.status;
    }

    public final Server getPlatformDmgr()
    {
        final String methodName = Platform.CNAME + "#getPlatformDmgr()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.platformDmgr);
        }

        return this.platformDmgr;
    }

    public final List<Server> getAppServers()
    {
        final String methodName = Platform.CNAME + "#getAppServers()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.appServers);
        }

        return this.appServers;
    }

    public final List<Server> getWebServers()
    {
        final String methodName = Platform.CNAME + "#getWebServers()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.webServers);
        }

        return this.webServers;
    }

    public final ServiceRegion getPlatformRegion()
    {
        final String methodName = Platform.CNAME + "#getPlatformRegion()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.platformRegion);
        }

        return this.platformRegion;
    }

    @Override
    public final String toString()
    {
        final String methodName = Platform.CNAME + "#toString()";

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
