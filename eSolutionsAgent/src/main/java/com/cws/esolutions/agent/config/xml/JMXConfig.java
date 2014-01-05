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
 * File: JMXConfig.java
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
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;
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
@XmlType(name = "jmx-config")
@XmlAccessorType(XmlAccessType.NONE)
public final class JMXConfig implements Serializable
{
    private int nmPort = 0;
    private int dmgrPort = 0;
    private String protocol = null;
    private String dmgrHost = null;
    private boolean isSecure = true;
    private String keyConfig = null;
    private int requestTimeout = 300;
    private String serverName = null;
    private String userConfig = null;
    private String jmxHandler = null;
    private String sslKeystore = null;
    private String mbeanHandler = null;
    private String keystorePass = null;
    private String sslTruststore = null;
    private String truststorePass = null;

    private static final long serialVersionUID = 6140525804689763849L;
    private static final String CNAME = JMXConfig.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(AgentConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(AgentConstants.ERROR_LOGGER);

    public final void setDmgrPort(final int value)
    {
        final String methodName = JMXConfig.CNAME + "#setDmgrPort(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.dmgrPort = value;
    }

    public final void setDmgrHost(final String value)
    {
        final String methodName = JMXConfig.CNAME + "#setDmgrHost(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.dmgrHost = value;
    }

    public final void setNmPort(final int value)
    {
        final String methodName = JMXConfig.CNAME + "#setNmPort(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.nmPort = value;
    }

    public final void setRequestTimeout(final int value)
    {
        final String methodName = JMXConfig.CNAME + "#setRequestTimeout(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.requestTimeout = value;
    }

    public final void setProtocol(final String value)
    {
        final String methodName = JMXConfig.CNAME + "#setProtocol(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.protocol = value;
    }

    public final void setUserConfig(final String value)
    {
        final String methodName = JMXConfig.CNAME + "#setUserConfig(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.userConfig = value;
    }

    public final void setKeyConfig(final String value)
    {
        final String methodName = JMXConfig.CNAME + "#setKeyConfig(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.keyConfig = value;
    }

    public final void setServerName(final String value)
    {
        final String methodName = JMXConfig.CNAME + "#setServerName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serverName = value;
    }

    public final void setJmxHandler(final String value)
    {
        final String methodName = JMXConfig.CNAME + "#setJmxHandler(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.jmxHandler = value;
    }

    public final void setMbeanHandler(final String value)
    {
        final String methodName = JMXConfig.CNAME + "#setMbeanHandler(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.mbeanHandler = value;
    }

    public final void setIsSecure(final boolean value)
    {
        final String methodName = JMXConfig.CNAME + "#setIsSecure(final boolean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.isSecure = value;
    }

    public final void setSslKeystore(final String value)
    {
        final String methodName = JMXConfig.CNAME + "#setSslKeystore(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.sslKeystore = value;
    }

    public final void setSslTruststore(final String value)
    {
        final String methodName = JMXConfig.CNAME + "#setSslTruststore(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.sslTruststore = value;
    }

    public final void setKeystorePass(final String value)
    {
        final String methodName = JMXConfig.CNAME + "#setKeystorePass(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.keystorePass = value;
    }

    public final void setTruststorePass(final String value)
    {
        final String methodName = JMXConfig.CNAME + "#setTruststorePass(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.truststorePass = value;
    }

    @XmlElement(name = "dmgrPort")
    public final int getDmgrPort()
    {
        final String methodName = JMXConfig.CNAME + "#getDmgrPort()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.dmgrPort);
        }

        return this.dmgrPort;
    }

    @XmlElement(name = "dmgrHost")
    public final String getDmgrHost()
    {
        final String methodName = JMXConfig.CNAME + "#getDmgrHost()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.dmgrHost);
        }

        return this.dmgrHost;
    }

    @XmlElement(name = "nmPort")
    public final int getNmPort()
    {
        final String methodName = JMXConfig.CNAME + "#getNmPort()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.nmPort);
        }

        return this.nmPort;
    }

    @XmlElement(name = "requestTimeout")
    public final int getRequestTimeout()
    {
        final String methodName = JMXConfig.CNAME + "#getRequestTimeout()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.requestTimeout);
        }

        return this.requestTimeout;
    }

    @XmlElement(name = "protocol")
    public final String getProtocol()
    {
        final String methodName = JMXConfig.CNAME + "#getProtocol()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.protocol);
        }

        return this.protocol;
    }

    @XmlElement(name = "userConfig")
    public final String getUserConfig()
    {
        final String methodName = JMXConfig.CNAME + "#getUserConfig()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.userConfig);
        }

        return this.userConfig;
    }

    @XmlElement(name = "keyConfig")
    public final String getKeyConfig()
    {
        final String methodName = JMXConfig.CNAME + "#getKeyConfig()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.keyConfig);
        }

        return this.keyConfig;
    }

    @XmlElement(name = "serverName")
    public final String getServerName()
    {
        final String methodName = JMXConfig.CNAME + "#getServerName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.serverName);
        }

        return this.serverName;
    }

    @XmlElement(name = "jmxHandler")
    public final String getJmxHandler()
    {
        final String methodName = JMXConfig.CNAME + "#getJmxHandler()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.jmxHandler);
        }

        return this.jmxHandler;
    }

    @XmlElement(name = "mbeanHandler")
    public final String getMbeanHandler()
    {
        final String methodName = JMXConfig.CNAME + "#getMbeanHandler()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.mbeanHandler);
        }

        return this.mbeanHandler;
    }

    @XmlElement(name = "isSecure")
    public final boolean getIsSecure()
    {
        final String methodName = JMXConfig.CNAME + "#getIsSecure()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.isSecure);
        }

        return this.isSecure;
    }

    @XmlElement(name = "sslKeystore")
    public final String getSslKeystore()
    {
        final String methodName = JMXConfig.CNAME + "#getSslKeystore()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.sslKeystore);
        }

        return this.sslKeystore;
    }

    @XmlElement(name = "keystorePass")
    public final String getKeystorePass()
    {
        final String methodName = JMXConfig.CNAME + "#getKeystorePass()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.keystorePass);
        }

        return this.keystorePass;
    }

    @XmlElement(name = "sslTruststore")
    public final String getSslTruststore()
    {
        final String methodName = JMXConfig.CNAME + "#getSslTruststore()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.sslTruststore);
        }

        return this.sslTruststore;
    }

    @XmlElement(name = "truststorePass")
    public final String getTruststorePass()
    {
        final String methodName = JMXConfig.CNAME + "#getTruststorePass()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.truststorePass);
        }

        return this.truststorePass;
    }

    @Override
    public final String toString()
    {
        final String methodName = JMXConfig.CNAME + "#toString()";

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
            DEBUGGER.debug("sBuilder: {}", sBuilder);
        }

        return sBuilder.toString();
    }
}
