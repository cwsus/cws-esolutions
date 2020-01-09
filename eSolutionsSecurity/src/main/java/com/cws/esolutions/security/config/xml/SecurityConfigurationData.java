/*
 * Copyright (c) 2009 - 2017 CaspersBox Web Services
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
package com.cws.esolutions.security.config.xml;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.config.xml
 * File: SecurityConfigurationData.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly   		11/23/2008 22:39:20             Created.
 */
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import java.util.Map.Entry;
import java.io.Serializable;
import java.lang.reflect.Field;

import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;

import com.cws.esolutions.security.SecurityServiceConstants;
/**
 * @author cws-khuntly
 * @version 1.0
 * @see java.io.Serializable
 */
@XmlRootElement(name = "configuration")
@XmlAccessorType(XmlAccessType.NONE)
public final class SecurityConfigurationData implements Serializable
{
    private KeyConfig keyConfig = null;
    private SystemConfig systemConfig = null;
    private RepositoryConfig repoConfig = null;
    private CertificateConfig certConfig = null;
    private SecurityConfig securityConfig = null;
    private ResourceConfig resourceConfig = null;
    private ExceptionConfig exceptionConfig = null;
    private PasswordRepositoryConfig passwordRepo = null;
    private FileSecurityConfig fileSecurityConfig = null;

    private static final long serialVersionUID = -795898942156658458L;
    private static final String CNAME = SecurityConfigurationData.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityServiceConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityServiceConstants.ERROR_LOGGER);

    public final void setSystemConfig(final SystemConfig value)
    {
        final String methodName = SecurityConfigurationData.CNAME + "#setSystemConfig(final SystemConfig value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.systemConfig = value;
    }

    public final void setSecurityConfig(final SecurityConfig value)
    {
        final String methodName = SecurityConfigurationData.CNAME + "#setSecurityConfig(final SecurityConfig value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.securityConfig = value;
    }

    public final void setRepoConfig(final RepositoryConfig value)
    {
        final String methodName = SecurityConfigurationData.CNAME + "#setRepoConfig(final RepositoryConfig value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.repoConfig = value;
    }

    public final void setResourceConfig(final ResourceConfig value)
    {
        final String methodName = SecurityConfigurationData.CNAME + "#setResourceConfig(final ResourceConfig value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.resourceConfig = value;
    }

    public final void setKeyConfig(final KeyConfig value)
    {
        final String methodName = SecurityConfigurationData.CNAME + "#setKeyConfig(final KeyConfig value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.keyConfig = value;
    }

    public final void setExceptionConfig(final ExceptionConfig value)
    {
        final String methodName = SecurityConfigurationData.CNAME + "#setExceptionConfig(final ExceptionConfig value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.exceptionConfig = value;
    }

    public final void setPasswordRepo(final PasswordRepositoryConfig value)
    {
        final String methodName = SecurityConfigurationData.CNAME + "#setPasswordRepo(final PasswordRepositoryConfig value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.passwordRepo = value;
    }

    public final void setFileSecurityConfig(final FileSecurityConfig value)
    {
        final String methodName = SecurityConfigurationData.CNAME + "#setFileSecurityConfig(final FileSecurityConfig value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.fileSecurityConfig = value;
    }

    public final void setCertConfig(final CertificateConfig value)
    {
        final String methodName = SecurityConfigurationData.CNAME + "#setCertConfig(final CertificateConfig value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.certConfig = value;
    }

    @XmlElement(name = "system-config")
    public final SystemConfig getSystemConfig()
    {
        final String methodName = SecurityConfigurationData.CNAME + "#getSystemConfig()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.systemConfig);
        }

        return this.systemConfig;
    }

    @XmlElement(name = "security-config")
    public final SecurityConfig getSecurityConfig()
    {
        final String methodName = SecurityConfigurationData.CNAME + "#getSecurityConfig()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.securityConfig);
        }

        return this.securityConfig;
    }

    @XmlElement(name = "repository-config")
    public final RepositoryConfig getRepoConfig()
    {
        final String methodName = SecurityConfigurationData.CNAME + "#getRepoConfig()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.repoConfig);
        }

        return this.repoConfig;
    }

    @XmlElement(name = "resource-config")
    public final ResourceConfig getResourceConfig()
    {
        final String methodName = SecurityConfigurationData.CNAME + "#getResourceConfig()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.resourceConfig);
        }

        return this.resourceConfig;
    }

    @XmlElement(name = "key-config")
    public final KeyConfig getKeyConfig()
    {
        final String methodName = SecurityConfigurationData.CNAME + "#getKeyConfig()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.keyConfig);
        }

        return this.keyConfig;
    }

    @XmlElement(name = "exception-config")
    public final ExceptionConfig getExceptionConfig()
    {
        final String methodName = SecurityConfigurationData.CNAME + "#getExceptionConfig()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.exceptionConfig);
        }

        return this.exceptionConfig;
    }

    @XmlElement(name = "file-security-config")
    public final FileSecurityConfig getFileSecurityConfig()
    {
        final String methodName = SecurityConfigurationData.CNAME + "#getFileSecurityConfig()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.fileSecurityConfig);
        }

        return this.fileSecurityConfig;
    }

    @XmlElement(name = "password-repository")
    public final PasswordRepositoryConfig getPasswordRepo()
    {
        final String methodName = SecurityConfigurationData.CNAME + "#getPasswordRepo()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.passwordRepo);
        }

        return this.passwordRepo;
    }

    @XmlElement(name = "certificate-config")
    public final CertificateConfig getCertConfig()
    {
        final String methodName = SecurityConfigurationData.CNAME + "#getCertConfig()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.certConfig);
        }

        return this.certConfig;
    }

    public static final String expandEnvVars(final String value)
    {
        final String methodName = SecurityConfigurationData.CNAME + "#expandEnvVars(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        String returnValue = null;

        if (!(StringUtils.contains(value, "$")))
        {
            return null;
        }

        final Properties sysProps = System.getProperties();
        final Map<String, String> envMap = System.getenv();
        final String text = StringUtils.replaceEachRepeatedly(value.split("=")[1].trim(), new String[] {"${", "}" }, new String[] { "", "" }).trim();

        if (DEBUG)
        {
            DEBUGGER.debug("Properties sysProps: {}", sysProps);
            DEBUGGER.debug("Map<String, String> envMap: {}", envMap);
            DEBUGGER.debug("String text: {}", text);
        }

        for (Entry<Object, Object> property : sysProps.entrySet())
        {
            if (DEBUG)
            {
                DEBUGGER.debug("Entry<Object, Object> property: {}", property);
            }

            String key = (String) property.getKey();

            if (DEBUG)
            {
                DEBUGGER.debug("String key: {}", key);
            }

            if (StringUtils.equals(key.trim(), text))
            {
                returnValue = sysProps.getProperty(key.trim());

                break;
            }
        }

        for (Entry<String, String> entry : envMap.entrySet())
        {
            if (DEBUG)
            {
                DEBUGGER.debug("Entry<String, String> entry: {}", entry);
            }

            String key = entry.getKey();

            if (DEBUG)
            {
                DEBUGGER.debug("String key: {}", key);
            }

            if (StringUtils.equals(key.trim(), text))
            {
                returnValue = entry.getValue();

                break;
            }
        }

        if (DEBUG)
        {
            DEBUGGER.debug("String returnValue: {}", returnValue);
        }

        return returnValue;
    }

    @Override
    public final String toString()
    {
        final String methodName = SecurityConfigurationData.CNAME + "#toString()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        StringBuilder sBuilder = new StringBuilder()
            .append("[" + this.getClass().getName() + "]" + SecurityServiceConstants.LINE_BREAK + "{" + SecurityServiceConstants.LINE_BREAK);

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
                        sBuilder.append("\t" + field.getName() + " --> " + field.get(this) + SecurityServiceConstants.LINE_BREAK);
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
