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
package com.cws.esolutions.security.config;

import org.slf4j.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;

import com.cws.esolutions.security.SecurityConstants;
/**
 * SecurityService
 * com.cws.esolutions.security.config
 * SecurityServiceConfiguration.java
 *
 *
 *
 * $Id: SecurityServiceConfiguration.java 2287 2013-01-03 20:52:22Z kmhuntly@gmail.com $
 * $Author: $
 * $Date: 2013-01-03 15:52:22 -0500 (Thu, 03 Jan 2013) $
 * $Revision: 2287 $
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
public final class SecurityServiceConfiguration implements Serializable
{
    private String emailAddr = null;
    private AuthData authData = null;
    private AuthRepo authRepo = null;
    private KeyConfig keyConfig = null;
    private SecurityConfig securityConfig = null;
    private ResourceConfig resourceConfig = null;
    private ExceptionConfig exceptionConfig = null;

    private static final long serialVersionUID = -795898942156658458L;
    private static final String CNAME = SecurityServiceConfiguration.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityConstants.ERROR_LOGGER);

    public final void setEmailAddr(final String value)
    {
        final String methodName = SecurityServiceConfiguration.CNAME + "#setEmailAddr(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.emailAddr = value;
    }

    public final void setSecurityConfig(final SecurityConfig value)
    {
        final String methodName = SecurityServiceConfiguration.CNAME + "#setHostName(final SecurityConfig value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.securityConfig = value;
    }

    public final void setAuthData(final AuthData value)
    {
        final String methodName = SecurityServiceConfiguration.CNAME + "#setHostName(final AuthData value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.authData = value;
    }

    public final void setAuthRepo(final AuthRepo value)
    {
        final String methodName = SecurityServiceConfiguration.CNAME + "#setHostName(final AuthRepo value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.authRepo = value;
    }

    public final void setResourceConfig(final ResourceConfig value)
    {
        final String methodName = SecurityServiceConfiguration.CNAME + "#setHostName(final ResourceConfig value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.resourceConfig = value;
    }

    public final void setKeyConfig(final KeyConfig value)
    {
        final String methodName = SecurityServiceConfiguration.CNAME + "#setKeyConfig(final KeyConfig value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.keyConfig = value;
    }

    public final void setExceptionConfig(final ExceptionConfig value)
    {
        final String methodName = SecurityServiceConfiguration.CNAME + "#setExceptionConfig(final ExceptionConfig value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.exceptionConfig = value;
    }

    @XmlElement(name = "email-address")
    public final String getEmailAddr()
    {
        final String methodName = SecurityServiceConfiguration.CNAME + "#getEmailAddr()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.emailAddr);
        }

        return this.emailAddr;
    }

    @XmlElement(name = "security-config")
    public final SecurityConfig getSecurityConfig()
    {
        final String methodName = SecurityServiceConfiguration.CNAME + "#getSecurityConfig()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.securityConfig);
        }
        
        return this.securityConfig;
    }

    @XmlElement(name = "auth-data")
    public final AuthData getAuthData()
    {
        final String methodName = SecurityServiceConfiguration.CNAME + "#getAuthData()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.authData);
        }
        
        return this.authData;
    }

    @XmlElement(name = "auth-repo")
    public final AuthRepo getAuthRepo()
    {
        final String methodName = SecurityServiceConfiguration.CNAME + "#getAuthRepo()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.authRepo);
        }
        
        return this.authRepo;
    }

    @XmlElement(name = "resource-config")
    public final ResourceConfig getResourceConfig()
    {
        final String methodName = SecurityServiceConfiguration.CNAME + "#getResourceConfig()";

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
        final String methodName = SecurityServiceConfiguration.CNAME + "#getKeyConfig()";

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
        final String methodName = SecurityServiceConfiguration.CNAME + "#getExceptionConfig()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.exceptionConfig);
        }
        
        return this.exceptionConfig;
    }

    public final String toString()
    {
        final String methodName = SecurityServiceConfiguration.CNAME + "#toString()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        StringBuilder sBuilder = new StringBuilder()
            .append("[" + this.getClass().getName() + "]" + SecurityConstants.LINE_BREAK + "{" + SecurityConstants.LINE_BREAK);

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
                        sBuilder.append("\t" + field.getName() + " --> " + field.get(this) + SecurityConstants.LINE_BREAK);
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
