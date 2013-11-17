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
import java.lang.reflect.Modifier;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.cws.esolutions.security.SecurityConstants;
/**
 * SecurityService
 * com.cws.esolutions.security.config
 * AuthRepo.java
 *
 *
 *
 * $Id: AuthRepo.java 2287 2013-01-03 20:52:22Z kmhuntly@gmail.com $
 * $Author: $
 * $Date: 2013-01-03 15:52:22 -0500 (Thu, 03 Jan 2013) $
 * $Revision: 2287 $
 * @author kmhuntly@gmail.com
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Nov 23, 2012 8:21:09 AM
 *     Created.
 */
@XmlType(name = "auth-repo")
@XmlAccessorType(XmlAccessType.NONE)
public final class AuthRepo implements Serializable
{
    private int minConnections = 0;
    private String repoType = null;
    private int repositoryPort = 0;
    private int maxConnections = 10;
    private String repositoryHost = null;
    private String repositoryUser = null;
    private String repositoryPass = null;
    private String repositorySalt = null;
    private int repositoryConnTimeout = 0;
    private int repositoryReadTimeout = 0;
    private String baseObjectClass = null;
    private String repositoryDriver = null;
    private String repositoryBaseDN = null;
    private String repositoryAppBase = null;
    private String repositoryUserBase = null;

    private static final String CNAME = AuthRepo.class.getName();
    private static final long serialVersionUID = 6881939686004481606L;

    private static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityConstants.ERROR_LOGGER);

    public final void setRepoType(final String value)
    {
        final String methodName = AuthRepo.CNAME + "#setRepoType(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.repoType = value;
    }

    public final void setRepositoryPort(final int value)
    {
        final String methodName = AuthRepo.CNAME + "#setRepositoryPort(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.repositoryPort = value;
    }

    public final void setRepositoryHost(final String value)
    {
        final String methodName = AuthRepo.CNAME + "#setRepositoryHost(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.repositoryHost = value;
    }

    public final void setRepositoryUser(final String value)
    {
        final String methodName = AuthRepo.CNAME + "#setRepositoryUser(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.repositoryUser = value;
    }

    public final void setRepositoryPass(final String value)
    {
        final String methodName = AuthRepo.CNAME + "#setRepositoryPass(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.repositoryPass = value;
    }

    public final void setRepositorySalt(final String value)
    {
        final String methodName = AuthRepo.CNAME + "#setRepositorySalt(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.repositorySalt = value;
    }

    public final void setRepositoryConnTimeout(final int value)
    {
        final String methodName = AuthRepo.CNAME + "#setRepositoryConnTimeout(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.repositoryConnTimeout = value;
    }

    public final void setRepositoryReadTimeout(final int value)
    {
        final String methodName = AuthRepo.CNAME + "#setRepositoryReadTimeout(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.repositoryReadTimeout = value;
    }

    public final void setRepositoryDriver(final String value)
    {
        final String methodName = AuthRepo.CNAME + "#setRepositoryDriver(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.repositoryDriver = value;
    }

    public final void setBaseObjectClass(final String value)
    {
        final String methodName = AuthRepo.CNAME + "#setBaseObjectClass(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.baseObjectClass = value;
    }

    public final void setRepositoryBaseDN(final String value)
    {
        final String methodName = AuthRepo.CNAME + "#setRepositoryBaseDN(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.repositoryBaseDN = value;
    }

    public final void setRepositoryAppBase(final String value)
    {
        final String methodName = AuthRepo.CNAME + "#setRepositoryAppBase(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.repositoryAppBase = value;
    }

    public final void setRepositoryUserBase(final String value)
    {
        final String methodName = AuthRepo.CNAME + "#setRepositoryUserBase(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.repositoryUserBase = value;
    }

    public final void setMinConnections(final int value)
    {
        final String methodName = AuthRepo.CNAME + "#setMinConnections(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.minConnections = value;
    }

    public final void setMaxConnections(final int value)
    {
        final String methodName = AuthRepo.CNAME + "#setMaxConnections(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.maxConnections = value;
    }

    @XmlElement(name = "repoType")
    public final String getRepoType()
    {
        final String methodName = AuthRepo.CNAME + "#getRepoType()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.repoType);
        }

        return this.repoType;
    }

    @XmlElement(name = "repositoryPort")
    public final int getRepositoryPort()
    {
        final String methodName = AuthRepo.CNAME + "#getRepositoryPort()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.repositoryPort);
        }
        
        return this.repositoryPort;
    }

    @XmlElement(name = "repositoryHost")
    public final String getRepositoryHost()
    {
        final String methodName = AuthRepo.CNAME + "#getRepositoryHost()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.repositoryHost);
        }
        
        return this.repositoryHost;
    }

    @XmlElement(name = "repositoryUser")
    public final String getRepositoryUser()
    {
        final String methodName = AuthRepo.CNAME + "#getRepositoryUser()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.repositoryUser);
        }
        
        return this.repositoryUser;
    }

    @XmlElement(name = "repositoryPass")
    public final String getRepositoryPass()
    {
        final String methodName = AuthRepo.CNAME + "#getRepositoryPass()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.repositoryPass);
        }
        
        return this.repositoryPass;
    }

    @XmlElement(name = "repositorySalt")
    public final String getRepositorySalt()
    {
        final String methodName = AuthRepo.CNAME + "#getRepositorySalt()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.repositorySalt);
        }
        
        return this.repositorySalt;
    }

    @XmlElement(name = "repositoryConnTimeout")
    public final int getRepositoryConnTimeout()
    {
        final String methodName = AuthRepo.CNAME + "#getRepositoryConnTimeout()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.repositoryConnTimeout);
        }
        
        return this.repositoryConnTimeout;
    }

    @XmlElement(name = "repositoryReadTimeout")
    public final int getRepositoryReadTimeout()
    {
        final String methodName = AuthRepo.CNAME + "#getRepositoryReadTimeout()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.repositoryReadTimeout);
        }
        
        return this.repositoryReadTimeout;
    }

    @XmlElement(name = "repositoryDriver")
    public final String getRepositoryDriver()
    {
        final String methodName = AuthRepo.CNAME + "#getRepositoryDriver()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.repositoryDriver);
        }
        
        return this.repositoryDriver;
    }

    @XmlElement(name = "baseObjectClass")
    public final String getBaseObjectClass()
    {
        final String methodName = AuthRepo.CNAME + "#getBaseObjectClass()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.baseObjectClass);
        }
        
        return this.baseObjectClass;
    }

    @XmlElement(name = "repositoryBaseDN")
    public final String getRepositoryBaseDN()
    {
        final String methodName = AuthRepo.CNAME + "#getRepositoryBaseDN()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.repositoryBaseDN);
        }
        
        return this.repositoryBaseDN;
    }

    @XmlElement(name = "repositoryAppBase")
    public final String getRepositoryAppBase()
    {
        final String methodName = AuthRepo.CNAME + "#getRepositoryAppBase()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.repositoryAppBase);
        }
        
        return this.repositoryAppBase;
    }

    @XmlElement(name = "repositoryUserBase")
    public final String getRepositoryUserBase()
    {
        final String methodName = AuthRepo.CNAME + "#getRepositoryUserBase()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.repositoryUserBase);
        }
        
        return this.repositoryUserBase;
    }

    @XmlElement(name = "minConnections")
    public final int getMinConnections()
    {
        final String methodName = AuthRepo.CNAME + "#getMinConnections()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.minConnections);
        }
        
        return this.minConnections;
    }

    @XmlElement(name = "maxConnections")
    public final int getMaxConnections()
    {
        final String methodName = AuthRepo.CNAME + "#getMaxConnections()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.maxConnections);
        }
        
        return this.maxConnections;
    }

    public final String toString()
    {
        final String methodName = AuthRepo.CNAME + "#toString()";

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

            if (field.getModifiers() != Modifier.STATIC)
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
