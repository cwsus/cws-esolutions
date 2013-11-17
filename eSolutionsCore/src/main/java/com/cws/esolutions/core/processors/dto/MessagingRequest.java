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
package com.cws.esolutions.core.processors.dto;

import org.slf4j.Logger;
import java.io.Serializable;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.cws.esolutions.core.Constants;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.processors.dto
 * MessagingRequest.java
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
 * kh05451 @ Oct 30, 2012 7:57:29 AM
 *     Created.
 */
public class MessagingRequest implements Serializable
{
    private String serviceId = null;
    private boolean webRequest = false;
    private String applicationId = null;
    private String applicationName = null;
    private UserAccount userAccount = null;
    private EmailMessage emailMessage = null;
    private ServiceMessage svcMessage = null;
    private RequestHostInfo requestInfo = null;

    private static final long serialVersionUID = -4178330895599726483L;
    private static final String CNAME = MessagingRequest.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER);

    public final void setUserAccount(final UserAccount value)
    {
        final String methodName = MessagingRequest.CNAME + "#setUserAccount(final UserAccount value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.userAccount = value;
    }

    public final void setRequestInfo(final RequestHostInfo value)
    {
        final String methodName = MessagingRequest.CNAME + "#setRequestInfo(final RequestHostInfo value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.requestInfo = value;
    }

    public final void setServiceId(final String value)
    {
        final String methodName = MessagingRequest.CNAME + "#setServiceId(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serviceId = value;
    }

    public final void setApplicationName(final String value)
    {
        final String methodName = MessagingRequest.CNAME + "#setApplicationName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.applicationName = value;
    }

    public final void setApplicationId(final String value)
    {
        final String methodName = MessagingRequest.CNAME + "#setApplicationId(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.applicationId = value;
    }

    public final void setEmailMessage(final EmailMessage value)
    {
        final String methodName = MessagingRequest.CNAME + "#setEmailMessage(final Message value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.emailMessage = value;
    }

    public final void setSvcMessage(final ServiceMessage value)
    {
        final String methodName = MessagingRequest.CNAME + "#setSvcMessage(final ServiceMessage value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.svcMessage = value;
    }

    public final void setWebRequest(final boolean value)
    {
        final String methodName = MessagingRequest.CNAME + "#setWebRequest(final boolean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.webRequest = value;
    }

    public final UserAccount getUserAccount()
    {
        final String methodName = MessagingRequest.CNAME + "#getUserAccount()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.userAccount);
        }

        return this.userAccount;
    }

    public final RequestHostInfo getRequestInfo()
    {
        final String methodName = MessagingRequest.CNAME + "#getRequestInfo()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.requestInfo);
        }

        return this.requestInfo;
    }

    public final String getServiceId()
    {
        final String methodName = MessagingRequest.CNAME + "#getServiceId()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.serviceId);
        }

        return this.serviceId;
    }

    public final String getApplicationName()
    {
        final String methodName = MessagingRequest.CNAME + "#getApplicationName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.applicationName);
        }

        return this.applicationName;
    }

    public final String getApplicationId()
    {
        final String methodName = MessagingRequest.CNAME + "#getApplicationId()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.applicationId);
        }

        return this.applicationId;
    }

    public final EmailMessage getEmailMessage()
    {
        final String methodName = MessagingRequest.CNAME + "#getEmailMessage()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.emailMessage);
        }

        return this.emailMessage;
    }

    public final ServiceMessage getServiceMessage()
    {
        final String methodName = MessagingRequest.CNAME + "#getEmailMessage()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.svcMessage);
        }

        return this.svcMessage;
    }

    public final boolean isWebRequest()
    {
        final String methodName = MessagingRequest.CNAME + "#isWebRequest()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.webRequest);
        }

        return this.webRequest;
    }

    @Override
    public final String toString()
    {
        final String methodName = MessagingRequest.CNAME + "#toString()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        StringBuilder sBuilder = new StringBuilder()
            .append("[" + this.getClass().getName() + "]" + Constants.LINE_BREAK + "{" + Constants.LINE_BREAK);

        for (Field field : this.getClass().getDeclaredFields())
        {
            if (DEBUG)
            {
                DEBUGGER.debug("field: {}", field);
            }

            if (field.getModifiers() != Modifier.STATIC)
            {
                try
                {
                    if (field.get(this) != null)
                    {
                        sBuilder.append("\t" + field.getName() + " --> " + field.get(this) + Constants.LINE_BREAK);
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
