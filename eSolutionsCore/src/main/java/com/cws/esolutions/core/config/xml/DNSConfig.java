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
package com.cws.esolutions.core.config.xml;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.config.xml
 * File: DNSConfig.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.lang.reflect.Field;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessorType;

import com.cws.esolutions.core.CoreServicesConstants;
/**
 * @author cws-khuntly
 * @version 1.0
 * @see java.io.Serializable
 */
@XmlRootElement(name = "dns-config")
@XmlAccessorType(XmlAccessType.NONE)
public final class DNSConfig
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

    private static final Logger DEBUGGER = LogManager.getLogger(CoreServicesConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

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

    @Override
    public final String toString()
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
