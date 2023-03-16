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
import java.util.List;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.apache.logging.log4j.Logger;
import jakarta.xml.bind.annotation.XmlType;
import org.apache.logging.log4j.LogManager;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElementWrapper;

import com.cws.esolutions.security.enums.SecurityUserRole;
import com.cws.esolutions.security.SecurityServiceConstants;
/**
 * @author cws-khuntly
 * @version 1.0
 * @see java.io.Serializable
 */
@XmlType(name = "userReturningAttributes")
@XmlAccessorType(XmlAccessType.NONE)
public final class UserReturningAttributes implements Serializable
{
    private int lockCount = 0;
    private String userId = null;
    private String surname = null;
    private String userGuid = null;
    private String memberOf = null;
    private String emailAddr = null;
    private String givenName = null;
    private Long lastLoginDate = -1L;
    private String commonName = null;
    private String displayName = null;
    private String telephoneNumber = null;
    private SecurityUserRole userRole = null;
    private List<String> returningAttributes = null;

    private static final long serialVersionUID = -4767557511096921048L;
    private static final String CNAME = UserReturningAttributes.class.getName();

    private static final Logger DEBUGGER = LogManager.getLogger(SecurityServiceConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setCommonName(final String value)
    {
        final String methodName = UserReturningAttributes.CNAME + "#setCommonName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.commonName = value;
    }

    public final void setUserId(final String value)
    {
        final String methodName = UserReturningAttributes.CNAME + "#setUserId(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.userId = value;
    }

    public final void setUserGuid(final String value)
    {
        final String methodName = UserReturningAttributes.CNAME + "#setUserGuid(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.userGuid = value;
    }

    public final void setSurname(final String value)
    {
        final String methodName = UserReturningAttributes.CNAME + "#setSurname(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.surname = value;
    }

    public final void setGivenName(final String value)
    {
        final String methodName = UserReturningAttributes.CNAME + "#setGivenName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.givenName = value;
    }

    public final void setDisplayName(final String value)
    {
        final String methodName = UserReturningAttributes.CNAME + "#setDisplayName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.displayName = value;
    }

    public final void setEmailAddr(final String value)
    {
        final String methodName = UserReturningAttributes.CNAME + "#setEmailAddr(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.emailAddr = value;
    }

    public final void setTelephoneNumber(final String value)
    {
        final String methodName = UserReturningAttributes.CNAME + "#setTelephoneNumber(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.telephoneNumber = value;
    }

    public final void setMemberOf(final String value)
    {
        final String methodName = UserReturningAttributes.CNAME + "#setMemberOf(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.memberOf = value;
    }

    public final void setReturningAttributes(final List<String> value)
    {
        final String methodName = UserReturningAttributes.CNAME + "#setReturningAttributes(final List<String> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.returningAttributes = value;
    }

    public final void setLastLoginDate(final Long value)
    {
        final String methodName = UserReturningAttributes.CNAME + "#setLastLoginDate(final Long value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.lastLoginDate = value;
    }

    public final void setLockCount(final int value)
    {
        final String methodName = UserReturningAttributes.CNAME + "#setLockCount(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.lockCount = value;
    }

    public final void setUserRole(final SecurityUserRole value)
    {
        final String methodName = UserReturningAttributes.CNAME + "#setUserRole(final SecurityUserRole value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.userRole = value;
    }

    @XmlElement(name = "commonName")
    public final String getCommonName()
    {
        final String methodName = UserReturningAttributes.CNAME + "#getCommonName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.commonName);
        }
        
        return this.commonName;
    }

    @XmlElement(name = "userGuid")
    public final String getUserGuid()
    {
        final String methodName = UserReturningAttributes.CNAME + "#getUserGuid()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.userGuid);
        }
        
        return this.userGuid;
    }

    @XmlElement(name = "cwslastlogin")
    public final Long getLastLoginDate()
    {
        final String methodName = UserReturningAttributes.CNAME + "#getLastLoginDate()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.lastLoginDate);
        }
        
        return this.lastLoginDate;
    }

    @XmlElement(name = "cwslastlogin")
    public final int getLockCount()
    {
        final String methodName = UserReturningAttributes.CNAME + "#getLockCount()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.lockCount);
        }
        
        return this.lockCount;
    }

    @XmlElement(name = "userId")
    public final String getUserId()
    {
        final String methodName = UserReturningAttributes.CNAME + "#getUserId()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.userId);
        }
        
        return this.userId;
    }

    @XmlElement(name = "surname")
    public final String getSurname()
    {
        final String methodName = UserReturningAttributes.CNAME + "#getSurname()";

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
        final String methodName = UserReturningAttributes.CNAME + "#getGivenName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.givenName);
        }
        
        return this.givenName;
    }

    @XmlElement(name = "displayName")
    public final String getDisplayName()
    {
        final String methodName = UserReturningAttributes.CNAME + "#getDisplayName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.displayName);
        }
        
        return this.displayName;
    }

    @XmlElement(name = "emailAddr")
    public final String getEmailAddr()
    {
        final String methodName = UserReturningAttributes.CNAME + "#getEmailAddr()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.emailAddr);
        }
        
        return this.emailAddr;
    }

    @XmlElement(name = "telephoneNumber")
    public final String getTelephoneNumber()
    {
        final String methodName = UserReturningAttributes.CNAME + "#getTelephoneNumber()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.telephoneNumber);
        }

        return this.telephoneNumber;
    }

    @XmlElement(name = "memberOf")
    public final String getMemberOf()
    {
        final String methodName = UserReturningAttributes.CNAME + "#getTcAccepted()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.memberOf);
        }
        
        return this.memberOf;
    }

    @XmlElement(name = "userRole")
    public final SecurityUserRole getUserRole()
    {
        final String methodName = UserReturningAttributes.CNAME + "#getUserRole()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.userRole);
        }
        
        return this.userRole;
    }

    @XmlElement(name = "attributeName")
    @XmlElementWrapper(name = "returningAttributes")
    public final List<String> getReturningAttributes()
    {
        final String methodName = UserReturningAttributes.CNAME + "#getReturningAttributes()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.returningAttributes);
        }

        return this.returningAttributes;
    }

    @Override
    public final String toString()
    {
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

        return sBuilder.toString();
    }
}
