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
 * File: SSHConfig.java
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
@XmlRootElement(name = "ssh-config")
@XmlAccessorType(XmlAccessType.NONE)
public final class SSHConfig implements Serializable
{
    private int timeout = 10; // default to 10 seconds
    private String sshKey = null;
    private String sshSalt= null;
    private String sshAccount = null;
    private String sshPassword = null;
    private String sshProperties = null;

    private static final String CNAME = SSHConfig.class.getName();
    private static final long serialVersionUID = -4670903285628304991L;

    private static final Logger DEBUGGER = LoggerFactory.getLogger(CoreServiceConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(CoreServiceConstants.ERROR_LOGGER);

    public final void setSshAccount(final String value)
    {
        final String methodName = SSHConfig.CNAME + "#setSshAccount(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.sshAccount = value;
    }

    public final void setSshPassword(final String value)
    {
        final String methodName = SSHConfig.CNAME + "#setSshPassword(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.sshPassword = value;
    }

    public final void setSshSalt(final String value)
    {
        final String methodName = SSHConfig.CNAME + "#setSshSalt(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.sshSalt = value;
    }

    public final void setSshKey(final String value)
    {
        final String methodName = SSHConfig.CNAME + "#setSshKey(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.sshKey = value;
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

    @XmlElement(name = "sshAccount")
    public final String getSshAccount()
    {
        final String methodName = SSHConfig.CNAME + "#getSshAccount()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.sshAccount);
        }

        return this.sshAccount;
    }

    @XmlElement(name = "sshPassword")
    public final String getSshPassword()
    {
        final String methodName = SSHConfig.CNAME + "#getSshPassword()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.sshPassword);
        }

        return this.sshPassword;
    }

    @XmlElement(name = "sshSalt")
    public final String getSshSalt()
    {
        final String methodName = SSHConfig.CNAME + "#getSshSalt()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.sshSalt);
        }

        return this.sshSalt;
    }

    @XmlElement(name = "sshKey")
    public final String getSshKey()
    {
        final String methodName = SSHConfig.CNAME + "#getSshKey()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.sshKey);
        }

        return this.sshKey;
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
