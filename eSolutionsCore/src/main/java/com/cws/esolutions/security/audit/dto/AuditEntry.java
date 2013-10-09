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
package com.cws.esolutions.security.audit.dto;

import org.slf4j.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.SecurityConstants;
import com.cws.esolutions.security.audit.enums.AuditType;

/**
 * eSolutionsCore
 * com.cws.esolutions.security.audit.dto
 * AuditEntry.java
 *
 *
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
 * 35033355 @ Apr 5, 2013 9:23:49 AM
 *     Created.
 */
public class AuditEntry implements Serializable
{
    private long auditDate = 0;
    private AuditType auditType = null;
    private UserAccount userAccount = null;
    private RequestHostInfo reqInfo = null;

    private static final String CNAME = AuditEntry.class.getName();
    private static final long serialVersionUID = 6018575153093937981L;

    private static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setAuditDate(final long value)
    {
        final String methodName = AuditEntry.CNAME + "#setAuditDate(final long value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.auditDate = value;
    }

    public final void setReqInfo(final RequestHostInfo value)
    {
        final String methodName = AuditEntry.CNAME + "#setReqInfo(final RequestHostInfo value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.reqInfo = value;
    }

    public final void setAuditType(final AuditType value)
    {
        final String methodName = AuditEntry.CNAME + "#setUserAccount(final AuditType value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.auditType = value;
    }

    public final void setUserAccount(final UserAccount value)
    {
        final String methodName = AuditEntry.CNAME + "#setUserAccount(final UserAccount value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.userAccount = value;
    }

    public final long getAuditDate()
    {
        final String methodName = AuditEntry.CNAME + "#getAuditDate()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.auditDate);
        }

        return this.auditDate;
    }

    public final RequestHostInfo getReqInfo()
    {
        final String methodName = AuditEntry.CNAME + "#getReqInfo()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.reqInfo);
        }

        return this.reqInfo;
    }

    public final AuditType getAuditType()
    {
        final String methodName = AuditEntry.CNAME + "#getAuditType()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.auditType);
        }

        return this.auditType;
    }

    public final UserAccount getUserAccount()
    {
        final String methodName = AuditEntry.CNAME + "#getUserAccount()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.userAccount);
        }

        return this.userAccount;
    }

    @Override
    public final String toString()
    {
        final String methodName = AuditEntry.CNAME + "#toString()";

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
