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
package com.cws.esolutions.core.config;

import org.slf4j.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;

import com.cws.esolutions.core.Constants;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.config
 * DNSConfig.java
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
 * kh05451 @ Nov 23, 2012 8:21:09 AM
 *     Created.
 */
@XmlRootElement(name = "dns-config")
@XmlAccessorType(XmlAccessType.NONE)
public final class DNSConfig implements Serializable
{
    private int ttlInterval = 900;
    private String adminName = null;
    private int retryInterval = 3600;
    private String domainName = null;
    private int cacheInterval = 3600;
    private int refreshInterval = 900;
    private String zoneRootDir = null;
    private String namedRootDir = null;
    private String zoneFilePath = null;
    private int expirationInterval = 604800;
    private String searchServiceHost = null;

    private static final String CNAME = DNSConfig.class.getName();
    private static final long serialVersionUID = -8781098886379821126L;

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER);

    public final void setAdminName(final String value)
    {
        final String methodName = DNSConfig.CNAME + "#setAdminName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.adminName = value;
    }

    public final void setTTLInterval(final int value)
    {
        final String methodName = DNSConfig.CNAME + "#setTTLInterval(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.ttlInterval = value;
    }

    public final void setRetryInterval(final int value)
    {
        final String methodName = DNSConfig.CNAME + "#setRetryInterval(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.retryInterval = value;
    }

    public final void setDomainName(final String value)
    {
        final String methodName = DNSConfig.CNAME + "#setDomainName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.domainName = value;
    }

    public final void setCacheInterval(final int value)
    {
        final String methodName = DNSConfig.CNAME + "#setCacheInterval(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.cacheInterval = value;
    }

    public final void setRefreshInterval(final int value)
    {
        final String methodName = DNSConfig.CNAME + "#setRefreshInterval(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.refreshInterval = value;
    }

    public final void setExpirationInterval(final int value)
    {
        final String methodName = DNSConfig.CNAME + "#setExpirationInterval(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.expirationInterval = value;
    }

    public final void setSearchServiceHost(final String value)
    {
        final String methodName = DNSConfig.CNAME + "#setSearchServiceHost(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.searchServiceHost = value;
    }

    public final void setZoneFilePath(final String value)
    {
        final String methodName = DNSConfig.CNAME + "#setZoneFilePath(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.zoneFilePath = value;
    }

    public final void setZoneRootDir(final String value)
    {
        final String methodName = DNSConfig.CNAME + "#setZoneRootDir(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.zoneRootDir = value;
    }

    public final void setNamedRootDir(final String value)
    {
        final String methodName = DNSConfig.CNAME + "#setNamedRootDir(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.namedRootDir = value;
    }

    @XmlElement(name = "adminName")
    public final String getAdminName()
    {
        final String methodName = DNSConfig.CNAME + "#getAdminName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.adminName);
        }

        return this.adminName;
    }

    @XmlElement(name = "ttlInterval")
    public final int getTTLInterval()
    {
        final String methodName = DNSConfig.CNAME + "#getTTLInterval()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.ttlInterval);
        }

        return this.ttlInterval;
    }

    @XmlElement(name = "retryInterval")
    public final int getRetryInterval()
    {
        final String methodName = DNSConfig.CNAME + "#getRetryInterval()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.retryInterval);
        }

        return this.retryInterval;
    }

    @XmlElement(name = "domainName")
    public final String getDomainName()
    {
        final String methodName = DNSConfig.CNAME + "#getDomainName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.domainName);
        }

        return this.domainName;
    }

    @XmlElement(name = "cacheInterval")
    public final int getCacheInterval()
    {
        final String methodName = DNSConfig.CNAME + "#getCacheInterval()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.cacheInterval);
        }

        return this.cacheInterval;
    }

    @XmlElement(name = "refreshInterval")
    public final int getRefreshInterval()
    {
        final String methodName = DNSConfig.CNAME + "#getRefreshInterval()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.refreshInterval);
        }

        return this.refreshInterval;
    }

    @XmlElement(name = "expirationInterval")
    public final int getExpirationInterval()
    {
        final String methodName = DNSConfig.CNAME + "#getExpirationInterval()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.expirationInterval);
        }

        return this.expirationInterval;
    }

    @XmlElement(name = "searchServiceHost")
    public final String getSearchServiceHost()
    {
        final String methodName = DNSConfig.CNAME + "#getSearchServiceHost()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.searchServiceHost);
        }

        return this.searchServiceHost;
    }

    @XmlElement(name = "zoneFilePath")
    public final String getZoneFilePath()
    {
        final String methodName = DNSConfig.CNAME + "#getZoneFilePath()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.zoneFilePath);
        }

        return this.zoneFilePath;
    }

    @XmlElement(name = "zoneRootDir")
    public final String getZoneRootDir()
    {
        final String methodName = DNSConfig.CNAME + "#getZoneRootDir()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.zoneRootDir);
        }

        return this.zoneRootDir;
    }

    @XmlElement(name = "namedRootDir")
    public final String getNamedRootDir()
    {
        final String methodName = DNSConfig.CNAME + "#getNamedRootDir()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.namedRootDir);
        }

        return this.namedRootDir;
    }

    public final String toString()
    {
        final String methodName = DNSConfig.CNAME + "#toString()";

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
                    (!(field.getName().equals("ERROR_RECORDER"))) &&
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
