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
package com.cws.esolutions.core.config.xml;

import org.slf4j.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.cws.esolutions.core.Constants;
import com.cws.esolutions.core.config.enums.AgentListenerType;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.config
 * File: AgentConfig.java
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Jan 4, 2013 3:36:54 PM
 *     Created.
 */
@XmlType(name = "agent-config")
@XmlAccessorType(XmlAccessType.NONE)
public final class AgentConfig implements Serializable
{
    private int tcpPort = 0;
    private String requestQueue = null;
    private String responseQueue = null;
    private String connectionName = null;
	private String trustStoreFile = null;
	private String trustStorePass = null;
	private String trustStoreType = null;
	private AgentListenerType listenerType = null;

    private static final String CNAME = AgentConfig.class.getName();
    private static final long serialVersionUID = 9144720470986353417L;

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER);

    public final void setListenerType(final AgentListenerType value)
    {
        final String methodName = AgentConfig.CNAME + "#setListenerType(final AgentListenerType value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: ", value);
        }

        this.listenerType = value;
    }

    public final void setConnectionName(final String value)
    {
        final String methodName = AgentConfig.CNAME + "#setConnectionName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: ", value);
        }
        
        this.connectionName = value;
    }

    public final void setTcpPort(final int value)
    {
        final String methodName = AgentConfig.CNAME + "#setTcpPort(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: ", value);
        }

        this.tcpPort = value;
    }

    public final void setRequestQueue(final String value)
    {
        final String methodName = AgentConfig.CNAME + "#setRequestQueue(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: ", value);
        }
        
        this.requestQueue = value;
    }

    public final void setTrustStoreFile(final String value)
    {
        final String methodName = AgentConfig.CNAME + "#setTrustStoreFile(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: ", value);
        }

        this.trustStoreFile = value;
    }

    public final void setTrustStorePass(final String value)
    {
        final String methodName = AgentConfig.CNAME + "#setTrustStorePass(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: ", value);
        }

        this.trustStorePass = value;
    }

    public final void setTrustStoreType(final String value)
    {
        final String methodName = AgentConfig.CNAME + "#setTrustStoreType(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: ", value);
        }

        this.trustStoreType = value;
    }

    @XmlElement(name = "listenerType")
    public final AgentListenerType getListenerType()
    {
        final String methodName = AgentConfig.CNAME + "#getListenerType()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: ", this.listenerType);
        }

        return this.listenerType;
    }

    @XmlElement(name = "connectionName")
    public final String getConnectionName()
    {
        final String methodName = AgentConfig.CNAME + "#getConnectionName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: ", this.connectionName);
        }

        return this.connectionName;
    }

    @XmlElement(name = "tcpPort")
    public final int getTcpPort()
    {
        final String methodName = AgentConfig.CNAME + "#getTcpPort()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: ", this.tcpPort);
        }

        return this.tcpPort;
    }

    @XmlElement(name = "requestQueue")
    public final String getRequestQueue()
    {
        final String methodName = AgentConfig.CNAME + "#getRequestQueue()";

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
        final String methodName = AgentConfig.CNAME + "#getResponseQueue()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: ", this.responseQueue);
        }
        
        return this.responseQueue;
    }

    @XmlElement(name = "trustStoreFile")
    public final String getTrustStoreFile()
    {
        final String methodName = AgentConfig.CNAME + "#getTrustStoreFile()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: ", this.trustStoreFile);
        }

        return this.trustStoreFile;
    }

    @XmlElement(name = "trustStorePass")
    public final String getTrustStorePass()
    {
        final String methodName = AgentConfig.CNAME + "#getTrustStorePass()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: ", this.trustStorePass);
        }

        return this.trustStorePass;
    }

    @XmlElement(name = "trustStoreType")
    public final String getTrustStoreType()
    {
        final String methodName = AgentConfig.CNAME + "#getTrustStoreType()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: ", this.trustStoreType);
        }

        return this.trustStoreType;
    }

    @Override
    public final String toString()
    {
        final String methodName = AgentConfig.CNAME + "#toString()";

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
            DEBUGGER.debug("sBuilder: ", sBuilder);
        }

        return sBuilder.toString();
    }
}
