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
package com.cws.esolutions.security.processors.dto;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.security.processors.dto
 * File: AccountControlResponse.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.List;
import org.slf4j.Logger;
import java.io.Serializable;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Field;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.SecurityConstants;
import com.cws.esolutions.security.processors.dto.AuditEntry;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 */
public class AccountControlResponse implements Serializable
{
    private int entryCount = 0;
    private String smsCode = null;
    private String resetId = null;
    private UserAccount userAccount = null;
    private List<String> questionList = null;
    private List<UserAccount> userList = null;
    private List<AuditEntry> auditEntries = null;
    private SecurityRequestStatus requestStatus = null;

    private static final long serialVersionUID = 7424992844092841578L;
    private static final String CNAME = AccountControlResponse.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityConstants.ERROR_LOGGER);

    public final void setRequestStatus(final SecurityRequestStatus value)
    {
        final String methodName = AccountControlResponse.CNAME + "#setRequestStatus(final SecurityRequestStatus value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.requestStatus = value;
    }

    public final void setEntryCount(final int value)
    {
        final String methodName = AccountControlResponse.CNAME + "#setEntryCount(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.entryCount = value;
    }

    public final void setUserAccount(final UserAccount value)
    {
        final String methodName = AccountControlResponse.CNAME + "#setUserAccount(final UserAccount value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.userAccount = value;
    }

    public final void setUserList(final List<UserAccount> value)
    {
        final String methodName = AccountControlResponse.CNAME + "#setUserList(final List<UserAccount> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.userList = value;
    }

    public final void setResetId(final String value)
    {
        final String methodName = AccountControlResponse.CNAME + "#setResetId(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        this.resetId = value;
    }

    public final void setSmsCode(final String value)
    {
        final String methodName = AccountControlResponse.CNAME + "#setSmsCode(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        this.smsCode = value;
    }

    public final void setQuestionList(final List<String> value)
    {
        final String methodName = AccountControlResponse.CNAME + "#setQuestionList(final List<String> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.questionList = value;
    }

    public final void setAuditEntries(final List<AuditEntry> value)
    {
        final String methodName = AccountControlResponse.CNAME + "#setAuditEntries(final List<AuditEntry> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.auditEntries = value;
    }

    public final SecurityRequestStatus getRequestStatus()
    {
        final String methodName = AccountControlResponse.CNAME + "#getMgmtType()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("SecurityRequestStatus: {}", this.requestStatus);
        }

        return this.requestStatus;
    }

    public final int getEntryCount()
    {
        final String methodName = AccountControlResponse.CNAME + "#getEntryCount()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.entryCount);
        }

        return this.entryCount;
    }

    public final UserAccount getUserAccount()
    {
        final String methodName = AccountControlResponse.CNAME + "#getUserAccount()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.userAccount);
        }

        return this.userAccount;
    }

    public final List<UserAccount> getUserList()
    {
        final String methodName = AccountControlResponse.CNAME + "#getUserList()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.userList);
        }

        return this.userList;
    }

    public final String getResetId()
    {
        final String methodName = AccountControlResponse.CNAME + "#getResetId()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        return this.resetId;
    }

    public final String getSmsCode()
    {
        final String methodName = AccountControlResponse.CNAME + "#getSmsCode()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        return this.smsCode;
    }

    public final List<String> getQuestionList()
    {
        final String methodName = AccountControlResponse.CNAME + "#getQuestionList()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.questionList);
        }

        return this.questionList;
    }

    public final List<AuditEntry> getAuditEntries()
    {
        final String methodName = AccountControlResponse.CNAME + "#getAuditEntries()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.auditEntries);
        }

        return this.auditEntries;
    }

    @Override
    public final String toString()
    {
        final String methodName = AccountControlResponse.CNAME + "#toString()";

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
                    (!(field.getName().equals("resetId"))) &&
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
