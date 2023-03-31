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
package com.cws.esolutions.security.dto;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.dto
 * File: UserAccount.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly           11/23/2008 22:39:20             Created.
 */
import java.util.Date;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.cws.esolutions.security.enums.SecurityUserRole;
import com.cws.esolutions.security.SecurityServiceConstants;
import com.cws.esolutions.security.processors.enums.LoginStatus;
/**
 * @author cws-khuntly
 * @version 1.0
 * @see java.io.Serializable
 */
public class UserAccount implements Serializable
{
	private String guid = null;
    private Date lastLogin = null;
    private String surname = null;
    private Date expiryDate = null;
    private String username = null;
    private String emailAddr = null;
    private String givenName = null;
    private String sessionId = null;
    private boolean accepted = false;
    private String pagerNumber = null;
    private String displayName = null;
    private boolean suspended = false;
    private LoginStatus status = null;
    private Integer failedCount = null;
    private String telephoneNumber = null;
    private SecurityUserRole userRole = null;

    private static final String CNAME = UserAccount.class.getName();
    private static final long serialVersionUID = -4373126337438707230L;

    private static final Logger DEBUGGER = LogManager.getLogger(SecurityServiceConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    /**
     * @param value - The {@link com.cws.esolutions.security.processors.enums.LoginStatus} for the account
     */
    public final void setStatus(final LoginStatus value)
    {
        final String methodName = UserAccount.CNAME + "#setStatus(final LoginStatus value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.status = value;
    }

    /**
     * @param value - The GUID associated with the account
     */
    public final void setGuid(final String value)
    {
        final String methodName = UserAccount.CNAME + "#setGuid(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.guid = value;
    }

    /**
     * @param value - The surname associated with the account
     */
    public final void setSurname(final String value)
    {
        final String methodName = UserAccount.CNAME + "#setSurname(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.surname = value;
    }

    /**
     * @param value - The expiration date associated with the account
     */
    public final void setExpiryDate(final Date value)
    {
        final String methodName = UserAccount.CNAME + "#setExpiryDate(final Date value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.expiryDate = value;
    }

    /**
     * @param value - The failed password count associated with the account
     */
    public final void setFailedCount(final Integer value)
    {
        final String methodName = UserAccount.CNAME + "#setFailedCount(final Integer value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.failedCount = value;
    }

    /**
     * @param value - The OLR setup flag associated with the account
     */
    public final void setAccepted(final boolean value)
    {
        final String methodName = UserAccount.CNAME + "#setAccepted(final boolean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.accepted = value;
    }

    /**
     * @param value - The suspension flag associated with the account
     */
    public final void setSuspended(final boolean value)
    {
        final String methodName = UserAccount.CNAME + "#setSuspended(final boolean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.suspended = value;
    }

    /**
     * @param value - The last login timestamp associated with the account
     */
    public final void setLastLogin(final Date value)
    {
        final String methodName = UserAccount.CNAME + "#setLastLogin(final Date value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.lastLogin = value;
    }

    /**
     * @param value - The display name associated with the account
     */
    public final void setDisplayName(final String value)
    {
        final String methodName = UserAccount.CNAME + "#setDisplayName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.displayName = value;
    }

    /**
     * @param value - The email address associated with the account
     */
    public final void setEmailAddr(final String value)
    {
        final String methodName = UserAccount.CNAME + "#setEmailAddr(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.emailAddr = value;
    }

    /**
     * @param value - The given name associated with the account
     */
    public final void setGivenName(final String value)
    {
        final String methodName = UserAccount.CNAME + "#setGivenName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.givenName = value;
    }

    /**
     * @param value - The username associated with the account
     */
    public final void setUsername(final String value)
    {
        final String methodName = UserAccount.CNAME + "#setUsername(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.username = value;
    }

    /**
     * @param value - The pager number associated with the account
     */
    public final void setPagerNumber(final String value)
    {
        final String methodName = UserAccount.CNAME + "#setPagerNumber(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.pagerNumber = value;
    }

    /**
     * @param value - The telephone number associated with the account
     */
    public final void setTelephoneNumber(final String value)
    {
        final String methodName = UserAccount.CNAME + "#setTelephoneNumber(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.telephoneNumber = value;
    }

    /**
     * @param value - The SecurityUserRole for the provided user
     */
    public final void setUserRole(final SecurityUserRole value)
    {
        final String methodName = UserAccount.CNAME + "#setUserRole(final SecurityUserRole value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.userRole = value;
    }

    /**
     * @param value - The SessionID
     */
    public final void setSessionId(final String value)
    {
        final String methodName = UserAccount.CNAME + "#setSessionId(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.sessionId = value;
    }

    /**
     * @return The {@link com.cws.esolutions.security.processors.enums.LoginStatus} for the account
     */
    public final LoginStatus getStatus()
    {
        final String methodName = UserAccount.CNAME + "#getStatus()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.status);
        }

        return this.status;
    }

    /**
     * @return The GUID associated with the account
     */
    public final String getGuid()
    {
        final String methodName = UserAccount.CNAME + "#getGuid()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.guid);
        }

        return this.guid;
    }

    /**
     * @return The username associated with the account
     */
    public final String getUsername()
    {
        final String methodName = UserAccount.CNAME + "#getUsername()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.username);
        }

        return this.username;
    }

    /**
     * @return The surname associated with the account
     */
    public final String getSurname()
    {
        final String methodName = UserAccount.CNAME + "#getSurname()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.surname);
        }

        return this.surname;
    }

    /**
     * @return The expiration date associated with the account
     */
    public final Date getExpiryDate()
    {
        final String methodName = UserAccount.CNAME + "#getExpiryDate()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.expiryDate);
        }

        return this.expiryDate;
    }

    /**
     * @return The failed password count associated with the account
     */
    public final Integer getFailedCount()
    {
        final String methodName = UserAccount.CNAME + "#getFailedCount()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.failedCount);
        }

        return this.failedCount;
    }

    /**
     * @return The suspension flag associated with the account
     */
    public final boolean isSuspended()
    {
        final String methodName = UserAccount.CNAME + "#isSuspended()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.suspended);
        }

        return this.suspended;
    }

    /**
     * @return The suspension flag associated with the account
     */
    public final boolean isAccepted()
    {
        final String methodName = UserAccount.CNAME + "#isAccepted()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.accepted);
        }

        return this.accepted;
    }

    /**
     * @return The last login timestamp associated with the account
     */
    public final Date getLastLogin()
    {
        final String methodName = UserAccount.CNAME + "#getLastLogin()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.lastLogin);
        }

        return this.lastLogin;
    }

    /**
     * @return The display name associated with the account
     */
    public final String getDisplayName()
    {
        final String methodName = UserAccount.CNAME + "#getDisplayName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.displayName);
        }

        return this.displayName;
    }

    /**
     * @return The email address associated with the account
     */
    public final String getEmailAddr()
    {
        final String methodName = UserAccount.CNAME + "#getEmailAddr()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.emailAddr);
        }

        return this.emailAddr;
    }

    /**
     * @return The given name associated with the account
     */
    public final String getGivenName()
    {
        final String methodName = UserAccount.CNAME + "#getGivenName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.givenName);
        }

        return this.givenName;
    }

    /**
     * @return The pager number associated with the account
     */
    public final String getPagerNumber()
    {
        final String methodName = UserAccount.CNAME + "#getPagerNumber()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.pagerNumber);
        }

        return this.pagerNumber;
    }

    /**
     * @return The telephone number associated with the account
     */
    public final String getTelephoneNumber()
    {
        final String methodName = UserAccount.CNAME + "#getTelephoneNumber()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.telephoneNumber);
        }

        return this.telephoneNumber;
    }

    /**
     * @return SecurityUserRole
     */
    public final SecurityUserRole getUserRole()
    {
        final String methodName = UserAccount.CNAME + "#getUserRole()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.userRole);
        }

        return this.userRole;
    }

    /**
     * @return String
     */
    public final String getSessionId()
    {
        final String methodName = UserAccount.CNAME + "#getSessionId()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.sessionId);
        }

        return this.sessionId;
    }

    /**
     * @see java.lang.Object#toString()
     */
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
                    (!(field.getName().equals("userKeys"))) &&
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
