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
package com.cws.esolutions.security.processors.dto;

import org.slf4j.Logger;
import java.io.Serializable;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Field;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.dto.UserSecurity;
import com.cws.esolutions.security.SecurityConstants;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.security.dao.userauth.enums.LoginType;
import com.cws.esolutions.security.dao.userauth.enums.AuthenticationType;
/**
 * SecurityService
 * com.cws.esolutions.security.usermgmt.dto
 * AuthenticationRequest.java
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
 * kh05451 @ Oct 31, 2012 10:35:55 AM
 *     Created.
 */
public class AuthenticationRequest implements Serializable
{
    private int count = 0;
    private int timeoutValue = 0;
    private String resetSmsCode = null;
    private LoginType loginType = null;
    private String applicationId = null;
    private String resetRequestId = null;
    private String applicationName = null;
    private UserAccount userAccount = null;
    private RequestHostInfo hostInfo = null;
    private UserSecurity userSecurity = null;
    private AuthenticationType authType = null;

    private static final long serialVersionUID = -201074803920605226L;
    private static final String CNAME = AuthenticationRequest.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityConstants.ERROR_LOGGER);

    public final void setHostInfo(final RequestHostInfo value)
    {
        final String methodName = AuthenticationRequest.CNAME + "#setBaseDN(final RequestHostInfo value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.hostInfo = value;
    }

    public final void setUserAccount(final UserAccount value)
    {
        final String methodName = AuthenticationRequest.CNAME + "#setLoginType(final UserAccount value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.userAccount = value;
    }

    public final void setUserSecurity(final UserSecurity value)
    {
        final String methodName = AuthenticationRequest.CNAME + "#setLoginType(final UserSecurity value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.userSecurity = value;
    }

    public final void setApplicationName(final String value)
    {
        final String methodName = AuthenticationRequest.CNAME + "#setApplicationName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.applicationName = value;
    }

    public final void setApplicationId(final String value)
    {
        final String methodName = AuthenticationRequest.CNAME + "#setApplicationId(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.applicationId = value;
    }

    public final void setAuthType(final AuthenticationType value)
    {
        final String methodName = AuthenticationRequest.CNAME + "#setAuthType(final AuthenticationType value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AuthenticationType: {}", value);
        }

        this.authType = value;
    }

    public final void setLoginType(final LoginType type)
    {
        final String methodName = AuthenticationRequest.CNAME + "#setLoginType(final LoginType type)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("LoginType: {}", type);
        }

        this.loginType = type;
    }

    public final void setTimeoutValue(final int value)
    {
        final String methodName = AuthenticationRequest.CNAME + "#setTimeoutValue(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.timeoutValue = value;
    }

    public final void setResetRequestId(final String value)
    {
        final String methodName = AuthenticationRequest.CNAME + "#setResetRequestId(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.resetRequestId = value;
    }

    public final void setResetSmsCode(final String value)
    {
        final String methodName = AuthenticationRequest.CNAME + "#setResetSmsCode(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.resetSmsCode = value;
    }

    public final void setCount(final int value)
    {
        final String methodName = AuthenticationRequest.CNAME + "#setCount(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.count = value;
    }

    public final RequestHostInfo getHostInfo()
    {
        final String methodName = AuthenticationRequest.CNAME + "#getHostInfo()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.hostInfo);
        }

        return this.hostInfo;
    }

    public final UserAccount getUserAccount()
    {
        final String methodName = AuthenticationRequest.CNAME + "#getLoginType()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.userAccount);
        }

        return this.userAccount;
    }

    public final UserSecurity getUserSecurity()
    {
        final String methodName = AuthenticationRequest.CNAME + "#getUserSecurity()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.userSecurity);
        }

        return this.userSecurity;
    }

    public final String getApplicationName()
    {
        final String methodName = AuthenticationRequest.CNAME + "#getApplicationName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.applicationName);
        }

        return this.applicationName;
    }

    public final String getApplicationId()
    {
        final String methodName = AuthenticationRequest.CNAME + "#getApplicationId()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.applicationId);
        }

        return this.applicationId;
    }

    public final AuthenticationType getAuthType()
    {
        final String methodName = AuthenticationRequest.CNAME + "#getAuthType()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AuthenticationType: {}", this.authType);
        }

        return this.authType;
    }

    public final LoginType getLoginType()
    {
        final String methodName = AuthenticationRequest.CNAME + "#getLoginType()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("LoginType: {}", this.loginType);
        }

        return this.loginType;
    }

    public final int getTimeoutValue()
    {
        final String methodName = AuthenticationRequest.CNAME + "#getTimeoutValue()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.timeoutValue);
        }

        return this.timeoutValue;
    }

    public final String getResetRequestId()
    {
        final String methodName = AuthenticationRequest.CNAME + "#getResetRequestId()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.resetRequestId);
        }

        return this.resetRequestId;
    }

    public final String getResetSmsCode()
    {
        final String methodName = AuthenticationRequest.CNAME + "#getResetSmsCode()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.resetSmsCode);
        }

        return this.resetSmsCode;
    }

    public final int getCount()
    {
        final String methodName = AuthenticationRequest.CNAME + "#getCount()";

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
        final String methodName = AuthenticationRequest.CNAME + "#toString()";

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
