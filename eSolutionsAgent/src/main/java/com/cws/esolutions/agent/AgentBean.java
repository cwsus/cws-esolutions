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
package com.cws.esolutions.agent;

import org.slf4j.Logger;

import java.lang.reflect.Field;

import org.slf4j.LoggerFactory;

import com.cws.esolutions.agent.config.ConfigurationData;
/**
 * eSolutionsAgent
 * com.cws.esolutions.agent
 * AgentBean.java
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
 * kh05451 @ Oct 29, 2012 9:44:46 AM
 *     Created.
 */
public class AgentBean
{
    private String osType = null;
    private String hostName = null;
    private boolean stopServer = false;
    private ConfigurationData configData = null;

    private static AgentBean instance = null;

    private static final String CNAME = AgentBean.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER);

    /**
     * Returns a static instance of this bean
     *
     * @return InitializerBean
     */
    public static final AgentBean getInstance()
    {
        final String method = CNAME + "#getInstance()";

        if (DEBUG)
        {
            DEBUGGER.debug(method);
            DEBUGGER.debug("instance: {}", AgentBean.instance);
        }

        if (AgentBean.instance == null)
        {
            AgentBean.instance = new AgentBean();
        }

        return AgentBean.instance;
    }

    /**
     * Sets a static copy of the Application configuration as defined in the
     * configuration xml files.
     *
     * @param value - The complete copy of application configuration information
     */
    public final void setConfigData(final ConfigurationData value)
    {
        final String methodName = AgentBean.CNAME + "#setConfigData(final ConfigurationData value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.configData = value;
    }

    /**
     * Sets a static copy of the Application configuration as defined in the
     * configuration xml files.
     *
     * @param value - The system-provided Operating System name that the service
     * is running on
     */
    public final void setOsType(final String value)
    {
        final String methodName = AgentBean.CNAME + "#setOsType(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.osType = value;
    }

    /**
     * Sets a static copy of the Application configuration as defined in the
     * configuration xml files.
     *
     * @param value - The system-provided hostname that the service is running
     * on
     */
    public final void setHostName(final String value)
    {
        final String methodName = AgentBean.CNAME + "#setHostName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.hostName = value;
    }

    public final void setStopServer(final boolean value)
    {
        final String methodName = AgentBean.CNAME + "#setStopServer(final boolean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.stopServer = value;
    }

    /**
     * Returns a static copy of the Application configuration as defined in the
     * configuration xml files.
     *
     * @return ConfigurationData - A complete copy of the application
     * configuration data.
     */
    public final ConfigurationData getConfigData()
    {
        final String methodName = AgentBean.CNAME + "#getConfigData()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.configData);
        }

        return this.configData;
    }

    /**
     * Returns the string representation of the system-provided Operating
     * System name.
     *
     * @return String - The string representation of the system-provided
     * Operating System name.
     */
    public final String getOsType()
    {
        final String methodName = AgentBean.CNAME + "#getOsType()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.osType);
        }

        return this.osType;
    }

    /**
     * Returns the string representation of the system-provided hostname.
     *
     * @return String - The system-provided hostname
     */
    public final String getHostName()
    {
        final String methodName = AgentBean.CNAME + "#getHostName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.hostName);
        }

        return this.hostName;
    }

    public final boolean getStopServer()
    {
        final String methodName = AgentBean.CNAME + "#getStopServer()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.stopServer);
        }

        return this.stopServer;
    }

    public final String toString()
    {
        final String methodName = AgentBean.CNAME + "#toString()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        final StringBuilder sBuilder = new StringBuilder()
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
                    (!(field.getName().equals("instance"))) &&
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
