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
package com.cws.esolutions.core.config;

import org.slf4j.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;

import com.cws.esolutions.core.Constants;
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
@XmlRootElement(name = "ssh-config")
@XmlAccessorType(XmlAccessType.NONE)
public final class SSHConfig implements Serializable
{
    private int timeout = 10000; // default to 10 seconds
    private String sshKeyFile = null;
    private String sshKeySalt = null;
    private String sshUserSalt = null;
    private String sshProperties = null;
    private String sshUserAccount = null;
    private String sshKeyPassword = null;
    private String sshUserPassword = null;

    private static final String CNAME = SSHConfig.class.getName();
    private static final long serialVersionUID = -4670903285628304991L;

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER);

    public final void setSshUserAccount(final String value)
    {
        final String methodName = SSHConfig.CNAME + "#setSshUserAccount(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.sshUserAccount = value;
    }

    public final void setSshUserPassword(final String value)
    {
        final String methodName = SSHConfig.CNAME + "#setSshUserPassword(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.sshUserPassword = value;
    }

    public final void setSshUserSalt(final String value)
    {
        final String methodName = SSHConfig.CNAME + "#setSshUserSalt(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.sshUserSalt = value;
    }

    public final void setSshKeyFile(final String value)
    {
        final String methodName = SSHConfig.CNAME + "#setSshKeyFile(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.sshKeyFile = value;
    }

    public final void setSshKeyPassword(final String value)
    {
        final String methodName = SSHConfig.CNAME + "#setSshKeyPassword(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.sshKeyPassword = value;
    }

    public final void setSshKeySalt(final String value)
    {
        final String methodName = SSHConfig.CNAME + "#setSshKeySalt(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.sshKeySalt = value;
    }

    public final void setTimeout(final int value)
    {
        final String methodName = SSHConfig.CNAME + "#setTimeout(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.timeout = value;
    }

    public final void setSshProperties(final String value)
    {
        final String methodName = SSHConfig.CNAME + "#setSshProperties(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.sshProperties = value;
    }

    @XmlElement(name = "sshUserAccount")
    public final String getSshUserAccount()
    {
        final String methodName = SSHConfig.CNAME + "#getSshUserAccount()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.sshUserAccount);
        }

        return this.sshUserAccount;
    }

    @XmlElement(name = "sshUserPassword")
    public final String getSshUserPassword()
    {
        final String methodName = SSHConfig.CNAME + "#getSshUserPassword()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.sshUserPassword);
        }

        return this.sshUserPassword;
    }

    @XmlElement(name = "sshUserSalt")
    public final String getSshUserSalt()
    {
        final String methodName = SSHConfig.CNAME + "#getSshUserSalt()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.sshUserSalt);
        }

        return this.sshUserSalt;
    }

    @XmlElement(name = "sshKeyFile")
    public final String getSshKeyFile()
    {
        final String methodName = SSHConfig.CNAME + "#getSshKeyFile()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.sshKeyFile);
        }

        return this.sshKeyFile;
    }

    @XmlElement(name = "sshKeyPassword")
    public final String getSshKeyPassword()
    {
        final String methodName = SSHConfig.CNAME + "#getSshKeyPassword()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.sshKeyPassword);
        }

        return this.sshKeyPassword;
    }

    @XmlElement(name = "sshKeySalt")
    public final String getSshKeySalt()
    {
        final String methodName = SSHConfig.CNAME + "#getSshKeySalt()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.sshKeySalt);
        }

        return this.sshKeySalt;
    }

    @XmlElement(name = "timeout")
    public final int getTimeout()
    {
        final String methodName = SSHConfig.CNAME + "#getTimeout()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.timeout);
        }

        return this.timeout;
    }

    @XmlElement(name = "sshProperties")
    public final String getSshProperties()
    {
        final String methodName = SSHConfig.CNAME + "#getSshProperties()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.sshProperties);
        }

        return this.sshProperties;
    }

    @Override
    public final String toString()
    {
        final String methodName = SSHConfig.CNAME + "#toString()";

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
            DEBUGGER.debug("sBuilder: {}", sBuilder);
        }

        return sBuilder.toString();
    }
}
