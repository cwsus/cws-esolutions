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
 * ConfigurationData.java
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
 * kh05451 @ Nov 23, 2012 8:57:04 AM
 *     Created.
 */
@XmlRootElement(name = "configuration")
@XmlAccessorType(XmlAccessType.NONE)
public final class ConfigurationData implements Serializable
{
    private JMXConfig jmxConfig = null;
    private ServerConfig serverConfig = null;
    private ScriptConfig scriptConfig = null;
    private ApplicationConfig appConfig = null;

    private static final long serialVersionUID = -5174050371773232789L;
    private static final String CNAME = ConfigurationData.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setJmxConfig(final JMXConfig value)
    {
        final String methodName = ConfigurationData.CNAME + "#setJmxConfig(final JMXConfig value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.jmxConfig = value;
    }

    public final void setAppConfig(final ApplicationConfig value)
    {
        final String methodName = ConfigurationData.CNAME + "#setAppConfig(final ApplicationConfig value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.appConfig = value;
    }

    public final void setScriptConfig(final ScriptConfig value)
    {
        final String methodName = ConfigurationData.CNAME + "#setScriptConfig(final ScriptConfig value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.scriptConfig = value;
    }

    public final void setServerConfig(final ServerConfig value)
    {
        final String methodName = ConfigurationData.CNAME + "#setServerConfig(final ServerConfig value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serverConfig = value;
    }

    @XmlElement(name = "jmx-config")
    public final JMXConfig getJmxConfig()
    {
        final String methodName = ConfigurationData.CNAME + "#getJmxConfig()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.jmxConfig);
        }

        return this.jmxConfig;
    }

    @XmlElement(name = "application-config")
    public final ApplicationConfig getAppConfig()
    {
        final String methodName = ConfigurationData.CNAME + "#getAppConfig()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.appConfig);
        }

        return this.appConfig;
    }

    @XmlElement(name = "script-config")
    public final ScriptConfig getScriptConfig()
    {
        final String methodName = ConfigurationData.CNAME + "#getScriptConfig()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.scriptConfig);
        }

        return this.scriptConfig;
    }

    @XmlElement(name = "server-config")
    public final ServerConfig getServerConfig()
    {
        final String methodName = ConfigurationData.CNAME + "#getServerConfig()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.serverConfig);
        }

        return this.serverConfig;
    }

    public final String toString()
    {
        final String methodName = ConfigurationData.CNAME + "#toString()";

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
                    // don't do anything with it
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
