/*
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
package com.cws.esolutions.agent.config.xml;
/*
 * Project: eSolutionsAgent
 * Package: com.cws.esolutions.agent.config.xml
 * File: ServerConfig.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;

import com.cws.esolutions.agent.AgentConstants;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 */
@XmlRootElement(name = "server-config")
@XmlAccessorType(XmlAccessType.NONE)
public final class ServerConfig implements Serializable
{
    private String salt = null;
	private int connectTimeout = 0;
    private String username = null;
    private String password = null;
    private String requestQueue = null;
    private String responseQueue = null;
    private String connectionName = null;

    private static final String CNAME = ServerConfig.class.getName();
    private static final long serialVersionUID = 9144720470986353417L;

    private static final Logger DEBUGGER = LoggerFactory.getLogger(AgentConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(AgentConstants.ERROR_LOGGER);

    public final void setConnectionName(final String value)
    {
        final String methodName = ServerConfig.CNAME + "#setConnectionName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: ", value);
        }
        
        this.connectionName = value;
    }

    public final void setConnectTimeout(final int value)
    {
        final String methodName = ServerConfig.CNAME + "#setConnectTimeout(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.connectTimeout = value;
    }

    public final void setRequestQueue(final String value)
    {
        final String methodName = ServerConfig.CNAME + "#setRequestQueue(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: ", value);
        }
        
        this.requestQueue = value;
    }

    public final void setResponseQueue(final String value)
    {
        final String methodName = ServerConfig.CNAME + "#setResponseQueue(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: ", value);
        }
        
        this.responseQueue = value;
    }

    public final void setUsername(final String value)
    {
        final String methodName = ServerConfig.CNAME + "#setUsername(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: ", value);
        }

        this.username = value;
    }

    public final void setPassword(final String value)
    {
        final String methodName = ServerConfig.CNAME + "#setPassword(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        this.password = value;
    }

    public final void setSalt(final String value)
    {
        final String methodName = ServerConfig.CNAME + "#setSalt(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        this.salt = value;
    }

    @XmlElement(name = "connectionName")
    public final String getConnectionName()
    {
        final String methodName = ServerConfig.CNAME + "#getConnectionName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: ", this.connectionName);
        }

        return this.connectionName;
    }

    @XmlElement(name = "connectTimeout")
    public final int getConnectTimeout()
    {
        final String methodName = ServerConfig.CNAME + "#getConnectTimeout()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.connectTimeout);
        }

        return this.connectTimeout;
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

    @XmlElement(name = "username")
    public final String getUsername()
    {
        final String methodName = ServerConfig.CNAME + "#getUsername()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: ", this.username);
        }

        return this.username;
    }

    @XmlElement(name = "password")
    public final String getPassword()
    {
        final String methodName = ServerConfig.CNAME + "#getPassword()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        return this.password;
    }

    @XmlElement(name = "salt")
    public final String getSalt()
    {
        final String methodName = ServerConfig.CNAME + "#getSalt()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        return this.salt;
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
            .append("[" + this.getClass().getName() + "]" + AgentConstants.LINE_BREAK + "{" + AgentConstants.LINE_BREAK);

        for (Field field : this.getClass().getDeclaredFields())
        {
            if (DEBUG)
            {
                DEBUGGER.debug("field: ", field);
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
                        sBuilder.append("\t" + field.getName() + " --> " + field.get(this) + AgentConstants.LINE_BREAK);
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
            DEBUGGER.debug("sBuilder: ", sBuilder);
        }

        return sBuilder.toString();
    }
}
