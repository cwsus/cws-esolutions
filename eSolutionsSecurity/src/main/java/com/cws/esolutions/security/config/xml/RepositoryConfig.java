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
 * File: RepositoryConfig.java
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

import com.cws.esolutions.security.SecurityServiceConstants;
/**
 * @author khuntly
 * @version 1.0
 * @see java.io.Serializable
 */
@XmlType(name = "repositoryConfiguration")
@XmlAccessorType(XmlAccessType.NONE)
public final class RepositoryConfig implements Serializable
{
    private String baseObject = null;
    private String repositoryBaseDN = null;
    private String repositoryUserBase = null;
    private String repositoryRoleBase = null;
    private UserReturningAttributes userAttributes = null;
    private SecurityReturningAttributes securityAttributes = null;
    private String saltFile = System.getProperty("user.home") + "/etc/secret.properties";
    private String passwordFile = System.getProperty("user.home") + "/etc/repository.properties";

    private static final long serialVersionUID = -4767557511096921048L;
    private static final String CNAME = RepositoryConfig.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityServiceConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityServiceConstants.ERROR_LOGGER);

    public final void setSaltFile(final String value)
    {
        final String methodName = RepositoryConfig.CNAME + "#setSaltFile(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.saltFile = value;
    }

    public final void setPasswordFile(final String value)
    {
        final String methodName = RepositoryConfig.CNAME + "#setPasswordFile(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.passwordFile = value;
    }

    public final void setBaseObject(final String value)
    {
        final String methodName = RepositoryConfig.CNAME + "#setBaseObject(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.baseObject = value;
    }

    public final void setRepositoryBaseDN(final String value)
    {
        final String methodName = RepositoryConfig.CNAME + "#setRepositoryBaseDN(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.repositoryBaseDN = value;
    }

    public final void setRepositoryUserBase(final String value)
    {
        final String methodName = RepositoryConfig.CNAME + "#setRepositoryUserBase(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.repositoryUserBase = value;
    }

    public final void setRepositoryRoleBase(final String value)
    {
        final String methodName = RepositoryConfig.CNAME + "#setRepositoryRoleBase(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.repositoryRoleBase = value;
    }

    public final void setSecurityAttributes(final SecurityReturningAttributes value)
    {
        final String methodName = RepositoryConfig.CNAME + "#setSecurityAttributes(final SecurityReturningAttributes value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.securityAttributes = value;
    }

    public final void setUserAttributes(final UserReturningAttributes value)
    {
        final String methodName = RepositoryConfig.CNAME + "#setUserAttributes(final UserReturningAttributes value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.userAttributes = value;
    }

    @XmlElement(name = "passwordFile")
    public final String getPasswordFile()
    {
        final String methodName = RepositoryConfig.CNAME + "#getPasswordFile()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.passwordFile);
        }

        return this.passwordFile;
    }

    @XmlElement(name = "saltFile")
    public final String getSaltFile()
    {
        final String methodName = RepositoryConfig.CNAME + "#getSaltFile()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.saltFile);
        }

        return this.saltFile;
    }

    @XmlElement(name = "baseObjectClass")
    public final String getBaseObject()
    {
        final String methodName = RepositoryConfig.CNAME + "#getBaseObject()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.baseObject);
        }

        return this.baseObject;
    }

    @XmlElement(name = "repositoryBaseDN")
    public final String getRepositoryBaseDN()
    {
        final String methodName = RepositoryConfig.CNAME + "#getRepositoryBaseDN()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.repositoryBaseDN);
        }

        return this.repositoryBaseDN;
    }

    @XmlElement(name = "repositoryUserBase")
    public final String getRepositoryUserBase()
    {
        final String methodName = RepositoryConfig.CNAME + "#getRepositoryUserBase(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.repositoryRoleBase);
        }

        return this.repositoryUserBase;
    }

    @XmlElement(name = "repositoryRoleBase")
    public final String getRepositoryRoleBase()
    {
        final String methodName = RepositoryConfig.CNAME + "#getRepositoryRoleBase()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.repositoryRoleBase);
        }

        return this.repositoryRoleBase;
    }

    @XmlElement(name = "userReturningAttributes")
    public final UserReturningAttributes getUserAttributes()
    {
        final String methodName = RepositoryConfig.CNAME + "#getUserAttributes()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.userAttributes);
        }
        
        return this.userAttributes;
    }

    @XmlElement(name = "securityReturningAttributes")
    public final SecurityReturningAttributes getSecurityAttributes()
    {
        final String methodName = RepositoryConfig.CNAME + "#getSecurityAttributes()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.securityAttributes);
        }
        
        return this.securityAttributes;
    }

    @Override
    public final String toString()
    {
        final String methodName = RepositoryConfig.CNAME + "#toString()";

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
