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
package com.cws.esolutions.agent.config;

import org.slf4j.Logger;

import java.io.Serializable;
import java.lang.reflect.Field;

import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;

import com.cws.esolutions.agent.Constants;
/**
 * eSolutionsAgent
 * com.cws.esolutions.agent.config
 * ServerConfig.java
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
@XmlRootElement(name = "server-config")
@XmlAccessorType(XmlAccessType.NONE)
public final class ServerConfig implements Serializable
{
    private int backlogCount = 0;
    private int portNumber = 10180; // default port if not configured
    private String sslKeySalt = null;
    private String sslProtocol = null;
    private String serverClass = null;
    private String requestQueue = null;
    private String responseQueue = null;
    private String transportType = null;
    private boolean isSSLEnabled = false;
    private String sslKeyDatabase = null;
    private String sslKeyPassword = null;
    private boolean isTCPDisabled = false;
    private String listenAddress = "0.0.0.0"; // bind to all available addresses if not configured

    private static final String CNAME = ServerConfig.class.getName();
    private static final long serialVersionUID = 3657200015017596527L;

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER);

    public final void setPortNumber(final int value)
    {
        final String methodName = ServerConfig.CNAME + "#setPortNumber(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.portNumber = value;
    }

    public final void setBacklogCount(final int value)
    {
        final String methodName = ServerConfig.CNAME + "#setBacklogCount(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.backlogCount = value;
    }

    public final void setSslKeySalt(final String value)
    {
        final String methodName = ServerConfig.CNAME + "#setSslKeySalt(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.sslKeySalt = value;
    }

    public final void setListenAddress(final String value)
    {
        final String methodName = ServerConfig.CNAME + "#setListenAddress(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.listenAddress = value;
    }

    public final void setTransportType(final String value)
    {
        final String methodName = ServerConfig.CNAME + "#setTransportType(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.transportType = value;
    }

    public final void setSslProtocol(final String value)
    {
        final String methodName = ServerConfig.CNAME + "#setSslProtocol(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.sslProtocol = value;
    }

    public final void setIsSSLEnabled(final boolean value)
    {
        final String methodName = ServerConfig.CNAME + "#setIsSSLEnabled(final boolean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.isSSLEnabled = value;
    }

    public final void setSslKeyDatabase(final String value)
    {
        final String methodName = ServerConfig.CNAME + "#setSslKeyDatabase(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.sslKeyDatabase = value;
    }

    public final void setSslKeyPassword(final String value)
    {
        final String methodName = ServerConfig.CNAME + "#setSslKeyPassword(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.sslKeyPassword = value;
    }

    public final void setIsTCPDisabled(final boolean value)
    {
        final String methodName = ServerConfig.CNAME + "#setIsTCPDisabled(final boolean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.isTCPDisabled = value;
    }

    public void setRequestQueue(final String value)
    {
        final String methodName = ServerConfig.CNAME + "#setRequestQueue(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: ", value);
        }
        
        this.requestQueue = value;
    }

    public void setResponseQueue(final String value)
    {
        final String methodName = ServerConfig.CNAME + "#setResponseQueue(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: ", value);
        }
        
        this.responseQueue = value;
    }

    public void setServerClass(final String value)
    {
        final String methodName = ServerConfig.CNAME + "#setServerClass(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: ", value);
        }
        
        this.serverClass = value;
    }

    @XmlElement(name = "portNumber")
    public final int getPortNumber()
    {
        final String methodName = ServerConfig.CNAME + "#getPortNumber()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.portNumber);
        }

        return this.portNumber;
    }

    @XmlElement(name = "backlog")
    public final int getBacklogCount()
    {
        final String methodName = ServerConfig.CNAME + "#getBacklogCount()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.backlogCount);
        }

        return this.backlogCount;
    }

    @XmlElement(name = "sslKeySalt")
    public final String getSslKeySalt()
    {
        final String methodName = ServerConfig.CNAME + "#getSslKeySalt()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.sslKeySalt);
        }

        return this.sslKeySalt;
    }

    @XmlElement(name = "listenAddress")
    public final String getListenAddress()
    {
        final String methodName = ServerConfig.CNAME + "#getListenAddress()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.listenAddress);
        }

        return this.listenAddress;
    }

    @XmlElement(name = "transportType")
    public final String getTransportType()
    {
        final String methodName = ServerConfig.CNAME + "#getTransportType()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.transportType);
        }

        return this.transportType;
    }

    @XmlElement(name = "sslEnabled")
    public final boolean isSSLEnabled()
    {
        final String methodName = ServerConfig.CNAME + "#getIsSSLEnabled()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.isSSLEnabled);
        }

        return this.isSSLEnabled;
    }

    @XmlElement(name = "sslKeystore")
    public final String getSslKeyDatabase()
    {
        final String methodName = ServerConfig.CNAME + "#getSslKeyDatabase()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.sslKeyDatabase);
        }

        return this.sslKeyDatabase;
    }

    @XmlElement(name = "sslKeyPassword")
    public final String getSslKeyPassword()
    {
        final String methodName = ServerConfig.CNAME + "#getSslKeyPassword()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.sslKeyPassword);
        }

        return this.sslKeyPassword;
    }

    @XmlElement(name = "sslProtocol")
    public final String getSslProtocol()
    {
        final String methodName = ServerConfig.CNAME + "#getSslProtocol()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.sslProtocol);
        }

        return this.sslProtocol;
    }

    @XmlElement(name = "tcpEnabled")
    public final boolean isTCPDisabled()
    {
        final String methodName = ServerConfig.CNAME + "#isTCPDisabled()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.isTCPDisabled);
        }

        return this.isTCPDisabled;
    }

    @XmlElement(name = "requestQueue")
    public final String getRequestQueue()
    {
        final String methodName = ServerConfig.CNAME + "#getRequestQueue()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: ", this.requestQueue);
        }
        
        return this.requestQueue;
    }

    @XmlElement(name = "responseQueue")
    public final String getResponseQueue()
    {
        final String methodName = ServerConfig.CNAME + "#getResponseQueue()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: ", this.responseQueue);
        }
        
        return this.responseQueue;
    }

    @XmlElement(name = "serverClass")
    public final String getServerClass()
    {
        final String methodName = ServerConfig.CNAME + "#getServerClass()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: ", this.serverClass);
        }
        
        return this.serverClass;
    }

    @Override
    public final String toString()
    {
        final String methodName = ServerConfig.CNAME + "#toString()";

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
