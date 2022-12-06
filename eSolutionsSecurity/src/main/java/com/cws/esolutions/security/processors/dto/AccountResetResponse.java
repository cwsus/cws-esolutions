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
package com.cws.esolutions.security.processors.dto;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.processors.dto
 * File: AccountResetResponse.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.util.List;
import org.slf4j.Logger;
import java.io.Serializable;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Field;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.SecurityServiceConstants;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.processors.dto.AuthenticationData;
/**
 * @author cws-khuntly
 * @version 1.0
 * @see java.io.Serializable
 */
public class AccountResetResponse implements Serializable
{
    private int count = 0;
    private String resetId = null;
    private String smsCode = null;
    private UserAccount userAccount = null;
    private List<String> questionList = null;
    private AuthenticationData userSecurity = null;
    private SecurityRequestStatus requestStatus = null;

    private static final long serialVersionUID = -3110651267063305566L;
    private static final String CNAME = AccountResetResponse.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityServiceConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setRequestStatus(final SecurityRequestStatus value)
    {
        final String methodName = AccountResetResponse.CNAME + "#setRequestStatus(final SecurityRequestStatus value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.requestStatus = value;
    }

    public final void setUserAccount(final UserAccount value)
    {
        final String methodName = AccountResetResponse.CNAME + "#setUserAccount(final UserAccount value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.userAccount = value;
    }

    public final void setResetId(final String value)
    {
        final String methodName = AccountResetResponse.CNAME + "#setResetId(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        this.resetId = value;
    }

    public final void setSmsCode(final String value)
    {
        final String methodName = AccountResetResponse.CNAME + "#setSmsCode(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        this.smsCode = value;
    }

    public final void setCount(final int value)
    {
        final String methodName = AccountResetResponse.CNAME + "#setCount(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.count = value;
    }

    public final void setQuestionList(final List<String> value)
    {
        final String methodName = AccountResetResponse.CNAME + "#setQuestionList(final List<String> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.questionList = value;
    }

    public final void setUserSecurity(final AuthenticationData value)
    {
        final String methodName = AccountResetResponse.CNAME + "#setUserSecurity(final AuthenticationData value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        this.userSecurity = value;
    }

    public final SecurityRequestStatus getRequestStatus()
    {
        final String methodName = AccountResetResponse.CNAME + "#getRequestStatus()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.requestStatus);
        }

        return this.requestStatus;
    }

    public final UserAccount getUserAccount()
    {
        final String methodName = AccountResetResponse.CNAME + "#getUserAccount()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.userAccount);
        }

        return this.userAccount;
    }

    public final String getResetId()
    {
        final String methodName = AccountResetResponse.CNAME + "#getResetId()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        return this.resetId;
    }

    public final String getSmsCode()
    {
        final String methodName = AccountResetResponse.CNAME + "#getSmsCode()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        return this.smsCode;
    }

    public final List<String> getQuestionList()
    {
        final String methodName = AccountResetResponse.CNAME + "#getQuestionList()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.questionList);
        }

        return this.questionList;
    }

    public final AuthenticationData getUserSecurity()
    {
        final String methodName = AccountResetResponse.CNAME + "#getUserSecurity()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        return this.userSecurity;
    }

    public final int getCount()
    {
        final String methodName = AccountResetResponse.CNAME + "#getCount()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.count);
        }

        return this.count;
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
                    (!(field.getName().equals("resetId"))) &&
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
