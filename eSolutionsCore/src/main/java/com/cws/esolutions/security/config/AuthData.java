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
 * Package: com.cws.esolutions.security.audit.processors.interfaces
 * File: IAuditProcessor.java
 *
 * History
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * Kevin Huntly         11/23/2008 22:39:20             Created.
 * Kevin Huntly         12/05/2008 13:36:09             Added method to process change requests
 */
@XmlType(name = "auth-data")
@XmlAccessorType(XmlAccessType.NONE)
public final class AuthData implements Serializable
{
    private String userId = null;
    private String surname = null;
    private String memberOf = null;
    private String userRole = null;
    private String lockCount = null;
    private String lastLogin = null;
    private String publicKey = null;
    private String emailAddr = null;
    private String olrLocked = null;
    private String givenName = null;
    private String expiryDate = null;
    private String tcAccepted = null;
    private String commonName = null;
    private String objectClass = null;
    private String olrSetupReq = null;
    private String isSuspended = null;
    private String displayName = null;
    private String pagerNumber = null;
    private String userPassword = null;
    private String secAnswerOne = null;
    private String secAnswerTwo = null;
    private String secQuestionOne = null;
    private String secQuestionTwo = null;
    private String telephoneNumber = null;

    private static final long serialVersionUID = -6969755465434590684L;
    private static final String CNAME = AuthData.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityConstants.ERROR_LOGGER);

    public final void setObjectClass(final String value)
    {
        final String methodName = AuthData.CNAME + "#setObjectClass(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.objectClass = value;
    }

    public final void setUserId(final String value)
    {
        final String methodName = AuthData.CNAME + "#setUserId(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.userId = value;
    }

    public final void setPublicKey(final String value)
    {
        final String methodName = AuthData.CNAME + "#setPublicKey(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.publicKey = value;
    }

    public final void setUserPassword(final String value)
    {
        final String methodName = AuthData.CNAME + "#setUserPassword(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.userPassword = value;
    }

    public final void setUserRole(final String value)
    {
        final String methodName = AuthData.CNAME + "#setUserRole(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.userRole = value;
    }

    public final void setLockCount(final String value)
    {
        final String methodName = AuthData.CNAME + "#setLockCount(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.lockCount = value;
    }

    public final void setLastLogin(final String value)
    {
        final String methodName = AuthData.CNAME + "#setLastLogin(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.lastLogin = value;
    }

    public final void setSurname(final String value)
    {
        final String methodName = AuthData.CNAME + "#setSurname(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.surname = value;
    }

    public final void setGivenName(final String value)
    {
        final String methodName = AuthData.CNAME + "#setGivenName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.givenName = value;
    }

    public final void setExpiryDate(final String value)
    {
        final String methodName = AuthData.CNAME + "#setExpiryDate(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.expiryDate = value;
    }

    public final void setSecQuestionOne(final String value)
    {
        final String methodName = AuthData.CNAME + "#setSecQuestionOne(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.secQuestionOne = value;
    }

    public final void setSecQuestionTwo(final String value)
    {
        final String methodName = AuthData.CNAME + "#setSecQuestionTwo(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.secQuestionTwo = value;
    }

    public final void setSecAnswerOne(final String value)
    {
        final String methodName = AuthData.CNAME + "#setSecAnswerOne(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.secAnswerOne = value;
    }

    public final void setSecAnswerTwo(final String value)
    {
        final String methodName = AuthData.CNAME + "#setSecAnswerTwo(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.secAnswerTwo = value;
    }

    public final void setEmailAddr(final String value)
    {
        final String methodName = AuthData.CNAME + "#setEmailAddr(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.emailAddr = value;
    }

    public final void setIsSuspended(final String value)
    {
        final String methodName = AuthData.CNAME + "#setIsSuspended(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.isSuspended = value;
    }

    public final void setCommonName(final String value)
    {
        final String methodName = AuthData.CNAME + "#setCommonName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.commonName = value;
    }

    public final void setOlrSetupReq(final String value)
    {
        final String methodName = AuthData.CNAME + "#setOlrSetupReq(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.olrSetupReq = value;
    }

    public final void setOlrLocked(final String value)
    {
        final String methodName = AuthData.CNAME + "#setOlrLocked(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.olrLocked = value;
    }

    public final void setDisplayName(final String value)
    {
        final String methodName = AuthData.CNAME + "#setDisplayName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.displayName = value;
    }

    public final void setTcAccepted(final String value)
    {
        final String methodName = AuthData.CNAME + "#setTcAccepted(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.tcAccepted = value;
    }

    public final void setMemberOf(final String value)
    {
        final String methodName = AuthData.CNAME + "#setMemberOf(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.memberOf = value;
    }

    public final void setPagerNumber(final String value)
    {
        final String methodName = AuthData.CNAME + "#setPagerNumber(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.pagerNumber = value;
    }

    public final void setTelephoneNumber(final String value)
    {
        final String methodName = AuthData.CNAME + "#setTelephoneNumber(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.telephoneNumber = value;
    }

    @XmlElement(name = "objectClass")
    public final String getObjectClass()
    {
        final String methodName = AuthData.CNAME + "#getObjectClass()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.objectClass);
        }
        
        return this.objectClass;
    }

    @XmlElement(name = "userId")
    public final String getUserId()
    {
        final String methodName = AuthData.CNAME + "#getUserId()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.userId);
        }
        
        return this.userId;
    }

    @XmlElement(name = "userPassword")
    public final String getUserPassword()
    {
        final String methodName = AuthData.CNAME + "#getUserPassword()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.userPassword);
        }
        
        return this.userPassword;
    }

    @XmlElement(name = "userRole")
    public final String getUserRole()
    {
        final String methodName = AuthData.CNAME + "#getUserRole()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.userRole);
        }
        
        return this.userRole;
    }

    @XmlElement(name = "publicKey")
    public final String getPublicKey()
    {
        final String methodName = AuthData.CNAME + "#getPublicKey()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.publicKey);
        }

        return this.publicKey;
    }

    @XmlElement(name = "lockCount")
    public final String getLockCount()
    {
        final String methodName = AuthData.CNAME + "#getLockCount()";

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
        final String methodName = AuthData.CNAME + "#getLastLogin()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.lastLogin);
        }
        
        return this.lastLogin;
    }

    @XmlElement(name = "surname")
    public final String getSurname()
    {
        final String methodName = AuthData.CNAME + "#getSurname()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.surname);
        }
        
        return this.surname;
    }

    @XmlElement(name = "givenName")
    public final String getGivenName()
    {
        final String methodName = AuthData.CNAME + "#getGivenName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.givenName);
        }
        
        return this.givenName;
    }

    @XmlElement(name = "expiryDate")
    public final String getExpiryDate()
    {
        final String methodName = AuthData.CNAME + "#getExpiryDate()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.expiryDate);
        }
        
        return this.expiryDate;
    }

    @XmlElement(name = "secQuestionOne")
    public final String getSecQuestionOne()
    {
        final String methodName = AuthData.CNAME + "#getSecQuestionOne()";

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
        final String methodName = AuthData.CNAME + "#getSecQuestionTwo()";

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
        final String methodName = AuthData.CNAME + "#getSecAnswerOne()";

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
        final String methodName = AuthData.CNAME + "#getSecAnswerTwo()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.secAnswerTwo);
        }
        
        return this.secAnswerTwo;
    }

    @XmlElement(name = "emailAddr")
    public final String getEmailAddr()
    {
        final String methodName = AuthData.CNAME + "#getEmailAddr()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.emailAddr);
        }
        
        return this.emailAddr;
    }

    @XmlElement(name = "isSuspended")
    public final String getIsSuspended()
    {
        final String methodName = AuthData.CNAME + "#getIsSuspended()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.isSuspended);
        }
        
        return this.isSuspended;
    }

    @XmlElement(name = "commonName")
    public final String getCommonName()
    {
        final String methodName = AuthData.CNAME + "#getCommonName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.commonName);
        }
        
        return this.commonName;
    }

    @XmlElement(name = "olrSetupReq")
    public final String getOlrSetupReq()
    {
        final String methodName = AuthData.CNAME + "#getOlrSetupReq()";

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
        final String methodName = AuthData.CNAME + "#getOlrLocked()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.olrLocked);
        }
        
        return this.olrLocked;
    }

    @XmlElement(name = "displayName")
    public final String getDisplayName()
    {
        final String methodName = AuthData.CNAME + "#getDisplayName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.displayName);
        }
        
        return this.displayName;
    }

    @XmlElement(name = "tcAccepted")
    public final String getTcAccepted()
    {
        final String methodName = AuthData.CNAME + "#getTcAccepted()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.tcAccepted);
        }
        
        return this.tcAccepted;
    }

    @XmlElement(name = "memberOf")
    public final String getMemberOf()
    {
        final String methodName = AuthData.CNAME + "#getTcAccepted()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.memberOf);
        }
        
        return this.memberOf;
    }

    @XmlElement(name = "pagerNumber")
    public final String getPagerNumber()
    {
        final String methodName = AuthData.CNAME + "#getPagerNumber()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.pagerNumber);
        }

        return this.pagerNumber;
    }

    @XmlElement(name = "telephoneNumber")
    public final String getTelephoneNumber()
    {
        final String methodName = AuthData.CNAME + "#getTelephoneNumber()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.telephoneNumber);
        }

        return this.telephoneNumber;
    }

    @Override
    public final String toString()
    {
        final String methodName = AuthData.CNAME + "#toString()";

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
