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
package com.cws.esolutions.security.dto;

import java.util.Date;
import org.slf4j.Logger;
import java.io.Serializable;
import java.security.KeyPair;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.security.enums.Role;
import com.cws.esolutions.security.SecurityConstants;
import com.cws.esolutions.security.processors.enums.LoginStatus;
/**
 * eSolutions
 * com.cws.esolutions.security.auth.dto
 * UserAccount.java
 *
 * $Id: $
 * $Author: $
 * $Date: $
 * $Revision: $
 * @author kmhuntly@gmail.com
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Oct 15, 2012 9:14:24 AM
 *     Created.
 */
public class UserAccount implements Serializable
{
    private Role role = null;
    private String guid = null;
    private String surname = null;
    private Date lastLogin = null;
    private Long expiryDate = null;
    private String username = null;
    private KeyPair userKeys = null;
    private String emailAddr = null;
    private String givenName = null;
    private String sessionId = null;
    private boolean olrSetup = false;
    private String displayName = null;
    private boolean olrLocked = false;
    private boolean suspended = false;
    private LoginStatus status = null;
    private Integer failedCount = null;
    private boolean tcAccepted = false;

    private static final String CNAME = UserAccount.class.getName();
    private static final long serialVersionUID = -1860442834878637721L;

    private static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

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

    public final void setExpiryDate(final Long value)
    {
        final String methodName = UserAccount.CNAME + "#setExpiryDate(final Long value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.expiryDate = value;
    }

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

    public final void setOlrLocked(final boolean value)
    {
        final String methodName = UserAccount.CNAME + "#setOlrLocked(final boolean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.olrLocked = value;
    }

    public final void setOlrSetup(final boolean value)
    {
        final String methodName = UserAccount.CNAME + "#setOlrSetup(final boolean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.olrSetup = value;
    }

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

    public final void setTcAccepted(final boolean value)
    {
        final String methodName = UserAccount.CNAME + "#setTcAccepted(final boolean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.tcAccepted = value;
    }

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

    public final void setRole(final Role value)
    {
        final String methodName = UserAccount.CNAME + "#setRole(final Role value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.role = value;
    }

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

    public final void setUserKeys(final KeyPair value)
    {
        final String methodName = UserAccount.CNAME + "#setUserKeys(final KeyPair value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.userKeys = value;
    }

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

    public final Long getExpiryDate()
    {
        final String methodName = UserAccount.CNAME + "#getExpiryDate()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.expiryDate);
        }

        return this.expiryDate;
    }

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

    public final boolean getOlrLocked()
    {
        final String methodName = UserAccount.CNAME + "#getOlrLocked()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.olrLocked);
        }

        return this.olrLocked;
    }

    public final boolean getOlrSetup()
    {
        final String methodName = UserAccount.CNAME + "#getOlrSetup()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.olrSetup);
        }

        return this.olrSetup;
    }

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

    public final boolean isTcAccepted()
    {
        final String methodName = UserAccount.CNAME + "#isTcAccepted()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.tcAccepted);
        }

        return this.tcAccepted;
    }

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

    public final Role getRole()
    {
        final String methodName = UserAccount.CNAME + "#getRole()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.role);
        }

        return this.role;
    }

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

    public final KeyPair getUserKeys()
    {
        final String methodName = UserAccount.CNAME + "#getUserKeys()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.userKeys);
        }

        return this.userKeys;
    }

    @Override
    public final String toString()
    {
        final String methodName = UserAccount.CNAME + "#toString()";

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
                    // don't do anything with it
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
