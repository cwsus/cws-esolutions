/*
 * Copyright (c) 2009 - 2014 CaspersBox Web Services
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
 * File: AuthData.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */

import java.util.List;
import org.slf4j.Logger;
import java.util.Arrays;
import java.util.ArrayList;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.cws.esolutions.security.SecurityServiceConstants;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 */
@XmlType(name = "auth-data")
@XmlAccessorType(XmlAccessType.NONE)
public final class AuthData implements Serializable
{
    private String secret = null;
    private String userId = null;
    private String surname = null;
    private String memberOf = null;
    private String lockCount = null;
    private String lastLogin = null;
    private String publicKey = null;
    private String emailAddr = null;
    private String olrLocked = null;
    private String givenName = null;
    private String expiryDate = null;
    private String commonName = null;
    private String olrSetupReq = null;
    private String isSuspended = null;
    private String displayName = null;
    private String pagerNumber = null;
    private String userPassword = null;
    private String secAnswerOne = null;
    private String secAnswerTwo = null;
    private List<String> entries = null;
    private String secQuestionOne = null;
    private String secQuestionTwo = null;
    private String telephoneNumber = null;

    private String baseObject = null;
    private String repositoryBaseDN = null;
    private String repositoryUserBase = null;
    private String repositoryRoleBase = null;

    private static final long serialVersionUID = -6969755465434590684L;
    private static final String CNAME = AuthData.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityServiceConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityServiceConstants.ERROR_LOGGER);

    public final void setBaseObject(final String value)
    {
        final String methodName = AuthData.CNAME + "#setBaseObject(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.baseObject = value;
    }

    public final void setRepositoryBaseDN(final String value)
    {
        final String methodName = AuthData.CNAME + "#setRepositoryBaseDN(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.repositoryBaseDN = value;
    }

    public final void setRepositoryUserBase(final String value)
    {
        final String methodName = AuthData.CNAME + "#setRepositoryUserBase(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.repositoryUserBase = value;
    }

    public final void setRepositoryRoleBase(final String value)
    {
        final String methodName = AuthData.CNAME + "#setRepositoryRoleBase(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.repositoryRoleBase = value;
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

    public final void setSecret(final String value)
    {
        final String methodName = AuthData.CNAME + "#setSecret(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.secret = value;
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

    @XmlElement(name = "baseObjectClass")
    public final String getBaseObject()
    {
        final String methodName = AuthData.CNAME + "#getBaseObject()";

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
        final String methodName = AuthData.CNAME + "#getRepositoryBaseDN()";

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
        final String methodName = AuthData.CNAME + "#getRepositoryUserBase()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.repositoryUserBase);
        }

        return this.repositoryUserBase;
    }

    @XmlElement(name = "repositoryRoleBase")
    public final String getRepositoryRoleBase()
    {
        final String methodName = AuthData.CNAME + "#getRepositoryRoleBase()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.repositoryRoleBase);
        }

        return this.repositoryRoleBase;
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

    @XmlElement(name = "secret")
    public final String getSecret()
    {
        final String methodName = AuthData.CNAME + "#getSecret()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.secret);
        }
        
        return this.secret;
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

    public final List<String> getEntries()
    {
        final String methodName = AuthData.CNAME + "#getEntries()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        this.entries = new ArrayList<>(
            Arrays.asList(
                this.secret,
                this.userId,
                this.surname,
                this.memberOf,
                this.lockCount,
                this.lastLogin,
                this.emailAddr,
                this.olrLocked,
                this.givenName,
                this.expiryDate,
                this.commonName,
                this.olrSetupReq,
                this.isSuspended,
                this.displayName,
                this.pagerNumber,
                this.telephoneNumber));

        return this.entries;
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
