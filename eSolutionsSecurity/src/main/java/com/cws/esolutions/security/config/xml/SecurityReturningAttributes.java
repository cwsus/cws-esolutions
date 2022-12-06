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
 * cws-khuntly          11/23/2008 22:39:20             Created.
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
 * @author cws-khuntly
 * @version 1.0
 * @see java.io.Serializable
 */
@XmlType(name = "securityReturningAttributes")
@XmlAccessorType(XmlAccessType.NONE)
public final class SecurityReturningAttributes implements Serializable
{
    private String secret = null;
    private String olrLocked = null;
    private String lastLogin = null;
    private String lockCount = null;
    private String expiryDate = null;
    private String olrSetupReq = null;
    private String isSuspended = null;
    private String secAnswerTwo = null;
    private String secAnswerOne = null;
    private String userPassword = null;
    private String secQuestionTwo = null;
    private String secQuestionOne = null;

    private static final long serialVersionUID = -6606124907043164731L;    
    private static final String CNAME = SecurityReturningAttributes.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityServiceConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setUserPassword(final String value)
    {
        final String methodName = SecurityReturningAttributes.CNAME + "#setUserPassword(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.userPassword = value;
    }

    public final void setSecret(final String value)
    {
        final String methodName = SecurityReturningAttributes.CNAME + "#setSecret(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.secret = value;
    }

    public final void setSecQuestionOne(final String value)
    {
        final String methodName = SecurityReturningAttributes.CNAME + "#setSecQuestionOne(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.secQuestionOne = value;
    }

    public final void setSecQuestionTwo(final String value)
    {
        final String methodName = SecurityReturningAttributes.CNAME + "#setSecQuestionTwo(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.secQuestionTwo = value;
    }

    public final void setSecAnswerOne(final String value)
    {
        final String methodName = SecurityReturningAttributes.CNAME + "#setSecAnswerOne(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.secAnswerOne = value;
    }

    public final void setSecAnswerTwo(final String value)
    {
        final String methodName = SecurityReturningAttributes.CNAME + "#setSecAnswerTwo(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.secAnswerTwo = value;
    }

    public final void setLockCount(final String value)
    {
        final String methodName = SecurityReturningAttributes.CNAME + "#setLockCount(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.lockCount = value;
    }

    public final void setLastLogin(final String value)
    {
        final String methodName = SecurityReturningAttributes.CNAME + "#setLastLogin(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.lastLogin = value;
    }

    public final void setExpiryDate(final String value)
    {
        final String methodName = SecurityReturningAttributes.CNAME + "#setExpiryDate(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.expiryDate = value;
    }

    public final void setIsSuspended(final String value)
    {
        final String methodName = SecurityReturningAttributes.CNAME + "#setIsSuspended(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.isSuspended = value;
    }

    public final void setOlrSetupReq(final String value)
    {
        final String methodName = SecurityReturningAttributes.CNAME + "#setOlrSetupReq(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.olrSetupReq = value;
    }

    public final void setOlrLocked(final String value)
    {
        final String methodName = SecurityReturningAttributes.CNAME + "#setOlrLocked(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.olrLocked = value;
    }

    @XmlElement(name = "userPassword")
    public final String getUserPassword()
    {
        final String methodName = SecurityReturningAttributes.CNAME + "#getUserPassword()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.userPassword);
        }
        
        return this.userPassword;
    }

    @XmlElement(name = "secret")
    public final String getSecret()
    {
        final String methodName = SecurityReturningAttributes.CNAME + "#getSecret()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.secret);
        }
        
        return this.secret;
    }

    @XmlElement(name = "secQuestionOne")
    public final String getSecQuestionOne()
    {
        final String methodName = SecurityReturningAttributes.CNAME + "#getSecQuestionOne()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.secQuestionOne);
        }
        
        return this.secQuestionOne;
    }

    @XmlElement(name = "secQuestionTwo")
    public final String getSecQuestionTwo()
    {
        final String methodName = SecurityReturningAttributes.CNAME + "#getSecQuestionTwo()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.secQuestionTwo);
        }
        
        return this.secQuestionTwo;
    }

    @XmlElement(name = "secAnswerOne")
    public final String getSecAnswerOne()
    {
        final String methodName = SecurityReturningAttributes.CNAME + "#getSecAnswerOne()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.secAnswerOne);
        }
        
        return this.secAnswerOne;
    }

    @XmlElement(name = "secAnswerTwo")
    public final String getSecAnswerTwo()
    {
        final String methodName = SecurityReturningAttributes.CNAME + "#getSecAnswerTwo()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.secAnswerTwo);
        }
        
        return this.secAnswerTwo;
    }

    @XmlElement(name = "lockCount")
    public final String getLockCount()
    {
        final String methodName = SecurityReturningAttributes.CNAME + "#getLockCount()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.lockCount);
        }
        
        return this.lockCount;
    }

    @XmlElement(name = "lastLogin")
    public final String getLastLogin()
    {
        final String methodName = SecurityReturningAttributes.CNAME + "#getLastLogin()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.lastLogin);
        }
        
        return this.lastLogin;
    }

    @XmlElement(name = "expiryDate")
    public final String getExpiryDate()
    {
        final String methodName = SecurityReturningAttributes.CNAME + "#getExpiryDate()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.expiryDate);
        }
        
        return this.expiryDate;
    }

    @XmlElement(name = "isSuspended")
    public final String getIsSuspended()
    {
        final String methodName = SecurityReturningAttributes.CNAME + "#getIsSuspended()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.isSuspended);
        }
        
        return this.isSuspended;
    }

    @XmlElement(name = "olrSetupReq")
    public final String getOlrSetupReq()
    {
        final String methodName = SecurityReturningAttributes.CNAME + "#getOlrSetupReq()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.olrSetupReq);
        }
        
        return this.olrSetupReq;
    }

    @XmlElement(name = "olrLocked")
    public final String getOlrLocked()
    {
        final String methodName = SecurityReturningAttributes.CNAME + "#getOlrLocked()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.olrLocked);
        }
        
        return this.olrLocked;
    }

    @Override
    public final String toString()
    {
        final String methodName = SecurityReturningAttributes.CNAME + "#toString()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        StringBuilder sBuilder = new StringBuilder()
            .append("[" + this.getClass().getName() + "]" + SecurityServiceConstants.LINE_BREAK + "{" + SecurityServiceConstants.LINE_BREAK);

        for (Field field : this.getClass().getDeclaredFields())
        {
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
                catch (final IllegalAccessException iax) {}
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
