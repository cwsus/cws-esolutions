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
package com.cws.esolutions.security.config;

import org.slf4j.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.cws.esolutions.security.SecurityConstants;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.dao.interfaces
 * File: IPackageDataDAO.java
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Jan 4, 2013 3:36:54 PM
 *     Created.
 */
@XmlType(name = "auth-repo")
@XmlAccessorType(XmlAccessType.NONE)
public final class AuthRepo implements Serializable
{
    private String repoType = null;
    private String isSecure = null;
    private String configFile = null;
    private String trustStoreFile= null;
    private String trustStorePass= null;
    private String trustStoreType= null;
    private String minConnections = null;
    private String maxConnections = null;
    private String repositoryPort = null;
    private String repositoryHost = null;
    private String repositoryUser = null;
    private String repositoryPass = null;
    private String repositorySalt = null;
    private String baseObjectClass = null;
    private String repositoryDriver = null;
    private String repositoryBaseDN = null;
    private String repositoryAppBase = null;
    private String repositoryUserBase = null;
    private String repositoryConnTimeout = null;
    private String repositoryReadTimeout = null;

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

    public final void setConfigFile(final String value)
    {
        final String methodName = AuthRepo.CNAME + "#setConfigFile(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.configFile = value;
    }

    public final void setTrustStoreFile(final String value)
    {
        final String methodName = AuthRepo.CNAME + "#setTrustStoreFile(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.trustStoreFile = value;
    }

    public final void setTrustStorePass(final String value)
    {
        final String methodName = AuthRepo.CNAME + "#setTrustStorePass(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.trustStorePass = value;
    }

    public final void setTrustStoreType(final String value)
    {
        final String methodName = AuthRepo.CNAME + "#setTrustStoreType(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.trustStoreType = value;
    }

    public final void setRepositoryPort(final String value)
    {
        final String methodName = AuthRepo.CNAME + "#setRepositoryPort(final String value)";

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

    public final void setRepositoryConnTimeout(final String value)
    {
        final String methodName = AuthRepo.CNAME + "#setRepositoryConnTimeout(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.repositoryConnTimeout = value;
    }

    public final void setRepositoryReadTimeout(final String value)
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

    public final void setMinConnections(final String value)
    {
        final String methodName = AuthRepo.CNAME + "#setMinConnections(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.minConnections = value;
    }

    public final void setMaxConnections(final String value)
    {
        final String methodName = AuthRepo.CNAME + "#setMaxConnections(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.maxConnections = value;
    }

    public final void setIsSecure(final String value)
    {
        final String methodName = AuthRepo.CNAME + "#setIsSecure(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.isSecure = value;
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

    @XmlElement(name = "configFile")
    public final String getConfigFile()
    {
        final String methodName = AuthRepo.CNAME + "#getConfigFile()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.configFile);
        }
        
        return this.configFile;
    }

    @XmlElement(name = "repositoryPort")
    public final String getRepositoryPort()
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
    public final String getRepositoryConnTimeout()
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
    public final String getRepositoryReadTimeout()
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
    public final String getMinConnections()
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
    public final String getMaxConnections()
    {
        final String methodName = AuthRepo.CNAME + "#getMaxConnections()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.maxConnections);
        }
        
        return this.maxConnections;
    }

    @XmlElement(name = "isSecure")
    public final String getIsSecure()
    {
        final String methodName = AuthRepo.CNAME + "#getIsSecure()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.isSecure);
        }
        
        return this.isSecure;
    }

    @XmlElement(name = "trustStoreFile")
    public final String getTrustStoreFile()
    {
        final String methodName = AuthRepo.CNAME + "#getTrustStoreFile()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.trustStoreFile);
        }
        
        return this.trustStoreFile;
    }

    @XmlElement(name = "trustStorePass")
    public final String getTrustStorePass()
    {
        final String methodName = AuthRepo.CNAME + "#getTrustStorePass()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.trustStorePass);
        }
        
        return this.trustStorePass;
    }

    @XmlElement(name = "trustStoreType")
    public final String getTrustStoreType()
    {
        final String methodName = AuthRepo.CNAME + "#getTrustStoreType()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.trustStoreType);
        }
        
        return this.trustStoreType;
    }

    @Override
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
