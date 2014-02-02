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
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.List;
import java.util.Date;
import org.slf4j.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang.StringUtils;

import com.cws.esolutions.security.SecurityServiceConstants;
import com.cws.esolutions.security.processors.enums.LoginStatus;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 */
public class UserAccount implements Serializable
{
    private String guid = null;
    private String surname = null;
    private Date lastLogin = null;
    private Date expiryDate = null;
    private String username = null;
    private String[] groups = null;
    private String emailAddr = null;
    private String givenName = null;
    private boolean olrSetup = false;
    private String displayName = null;
    private boolean olrLocked = false;
    private boolean suspended = false;
    private LoginStatus status = null;
    private String pagerNumber = null;
    private Integer failedCount = null;
    private String telephoneNumber = null;

    private static final String CNAME = UserAccount.class.getName();
    private static final long serialVersionUID = -1860442834878637721L;

    private static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityServiceConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityServiceConstants.ERROR_LOGGER);

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

    public final void setGroups(final String[] value)
    {
        final String methodName = UserAccount.CNAME + "#setGroups(final String[] value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.groups = value;
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

    public final boolean isOlrLocked()
    {
        final String methodName = UserAccount.CNAME + "#isOlrLocked()";

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

    public final String[] getGroups()
    {
        final String methodName = UserAccount.CNAME + "#getGroups()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.groups);
        }

        return this.groups;
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

    public final boolean hasGroup(final String value)
    {
        final String methodName = UserAccount.CNAME + "#hasGroup(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        boolean isAuthorized = false;

        for (UserGroup group : this.groups)
        {
            if (StringUtils.equals(group.getName(), value))
            {
                isAuthorized = true;
            }
        }

        return isAuthorized;
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
