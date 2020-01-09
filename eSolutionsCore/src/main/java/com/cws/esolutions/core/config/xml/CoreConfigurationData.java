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
 * File: CoreConfigurationData.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly   		11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;

import com.cws.esolutions.core.CoreServiceConstants;
/**
 * @author cws-khuntly
 * @version 1.0
 * @see java.io.Serializable
 */
@XmlRootElement(name = "configuration")
@XmlAccessorType(XmlAccessType.NONE)
public final class CoreConfigurationData implements Serializable
{
    private SSHConfig sshConfig = null;
    private FTPConfig ftpConfig = null;
    private DNSConfig dnsConfig = null;
    private MailConfig mailConfig = null;
    private HTTPConfig httpConfig = null;
    private AgentConfig agentConfig = null;
    private ProxyConfig proxyConfig = null;
    private ScriptConfig scriptConfig = null;
    private SystemConfig systemConfig = null;
    private ApplicationConfig appConfig = null;
    private ResourceConfig resourceConfig = null;
    private ExceptionConfig exceptionConfig = null;
    private DeploymentConfig deploymentConfig = null;

    private static final long serialVersionUID = -3876853588713098436L;
    private static final String CNAME = CoreConfigurationData.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(CoreServiceConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(CoreServiceConstants.ERROR_LOGGER);

    public final void setAppConfig(final ApplicationConfig value)
    {
        final String methodName = CoreConfigurationData.CNAME + "#setAppConfig(final ApplicationConfig value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.appConfig = value;
    }

    public final void setScriptConfig(final ScriptConfig value)
    {
        final String methodName = CoreConfigurationData.CNAME + "#setScriptConfig(final ScriptConfig value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.scriptConfig = value;
    }

    public final void setResourceConfig(final ResourceConfig value)
    {
        final String methodName = CoreConfigurationData.CNAME + "#setHostName(final ResourceConfig value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.resourceConfig = value;
    }

    public final void setProxyConfig(final ProxyConfig value)
    {
        final String methodName = CoreConfigurationData.CNAME + "#setProxyConfig(final ProxyConfig value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.proxyConfig = value;
    }

    public final void setHttpConfig(final HTTPConfig value)
    {
        final String methodName = CoreConfigurationData.CNAME + "#setHttpConfig(final HTTPConfig value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.httpConfig = value;
    }

    public final void setDNSConfig(final DNSConfig value)
    {
        final String methodName = CoreConfigurationData.CNAME + "#setDNSConfig(final DNSConfig value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.dnsConfig = value;
    }

    public final void setSshConfig(final SSHConfig value)
    {
        final String methodName = CoreConfigurationData.CNAME + "#setSshConfig(final SSHConfig value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.sshConfig = value;
    }

    public final void setFtpConfig(final FTPConfig value)
    {
        final String methodName = CoreConfigurationData.CNAME + "#setFtpConfig(final FTPConfig value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.ftpConfig = value;
    }

    public final void setExceptionConfig(final ExceptionConfig value)
    {
        final String methodName = CoreConfigurationData.CNAME + "#setExceptionConfig(final ExceptionConfig value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.exceptionConfig = value;
    }

    public final void setDeploymentConfig(final DeploymentConfig value)
    {
        final String methodName = CoreConfigurationData.CNAME + "#setDeploymentConfig(final DeploymentConfig value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.deploymentConfig = value;
    }

    public final void setAgentConfig(final AgentConfig value)
    {
        final String methodName = CoreConfigurationData.CNAME + "#setAgentConfig(final AgentConfig value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.agentConfig = value;
    }

    public final void setMailConfig(final MailConfig value)
    {
        final String methodName = CoreConfigurationData.CNAME + "#setMailConfig(final MailConfig value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.mailConfig = value;
    }

    public final void setSystemConfig(final SystemConfig value)
    {
        final String methodName = CoreConfigurationData.CNAME + "#setSystemConfig(final SystemConfig value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.systemConfig = value;
    }

    @XmlElement(name = "application-config")
    public final ApplicationConfig getAppConfig()
    {
        final String methodName = CoreConfigurationData.CNAME + "#getAppConfig()";

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
        final String methodName = CoreConfigurationData.CNAME + "#getScriptConfig()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.scriptConfig);
        }

        return this.scriptConfig;
    }

    @XmlElement(name = "proxy-config")
    public final ProxyConfig getProxyConfig()
    {
        final String methodName = CoreConfigurationData.CNAME + "#getProxyConfig()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.proxyConfig);
        }

        return this.proxyConfig;
    }

    @XmlElement(name = "http-config")
    public final HTTPConfig getHttpConfig()
    {
        final String methodName = CoreConfigurationData.CNAME + "#getHttpConfig()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.httpConfig);
        }

        return this.httpConfig;
    }

    @XmlElement(name = "dns-config")
    public final DNSConfig getDNSConfig()
    {
        final String methodName = CoreConfigurationData.CNAME + "#getDNSConfig()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.dnsConfig);
        }

        return this.dnsConfig;
    }

    @XmlElement(name = "resource-config")
    public final ResourceConfig getResourceConfig()
    {
        final String methodName = CoreConfigurationData.CNAME + "#getResourceConfig()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.resourceConfig);
        }

        return this.resourceConfig;
    }

    @XmlElement(name = "ssh-config")
    public final SSHConfig getSshConfig()
    {
        final String methodName = CoreConfigurationData.CNAME + "#getSshConfig()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.sshConfig);
        }

        return this.sshConfig;
    }

    @XmlElement(name = "ftp-config")
    public final FTPConfig getFtpConfig()
    {
        final String methodName = CoreConfigurationData.CNAME + "#getFtpConfig()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.ftpConfig);
        }

        return this.ftpConfig;
    }

    @XmlElement(name = "exception-config")
    public final ExceptionConfig getExceptionConfig()
    {
        final String methodName = CoreConfigurationData.CNAME + "#getExceptionConfig()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.exceptionConfig);
        }

        return this.exceptionConfig;
    }

    @XmlElement(name = "deployment-config")
    public final DeploymentConfig getDeploymentConfig()
    {
        final String methodName = CoreConfigurationData.CNAME + "#getDeploymentConfig()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.deploymentConfig);
        }

        return this.deploymentConfig;
    }

    @XmlElement(name = "agent-config")
    public final AgentConfig getAgentConfig()
    {
        final String methodName = CoreConfigurationData.CNAME + "#getAgentConfig()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.agentConfig);
        }

        return this.agentConfig;
    }

    @XmlElement(name = "mail-config")
    public final MailConfig getMailConfig()
    {
        final String methodName = CoreConfigurationData.CNAME + "#getMailConfig()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.mailConfig);
        }

        return this.mailConfig;
    }

    @XmlElement(name = "system-config")
    public final SystemConfig getSystemConfig()
    {
        final String methodName = CoreConfigurationData.CNAME + "#getSystemConfig()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.systemConfig);
        }

        return this.systemConfig;
    }

    @Override
    public final String toString()
    {
        final String methodName = CoreConfigurationData.CNAME + "#toString()";

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
