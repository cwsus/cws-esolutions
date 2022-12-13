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
package com.cws.esolutions.core.processors.dto;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.processors.dto
 * File: MessagingResponse.java
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
import org.apache.logging.log4j.LogManager;

import com.cws.esolutions.core.CoreServicesConstants;
import com.cws.esolutions.core.utils.dto.EmailMessage;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
/**
 * @author cws-khuntly
 * @version 1.0
 * @see java.io.Serializable
 */
public class MessagingResponse implements Serializable
{
    private String messageId = null;
    private ServiceMessage svcMessage = null;
    private EmailMessage emailMessage = null;
    private List<EmailMessage> emailMessages = null;
    private List<ServiceMessage> svcMessages = null;
    private CoreServicesStatus requestStatus = null;

    private static final long serialVersionUID = 8541078429981220746L;
    private static final String CNAME = MessagingResponse.class.getName();

    private static final Logger DEBUGGER = LogManager.getLogger(CoreServicesConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setRequestStatus(final CoreServicesStatus value)
    {
        final String methodName = MessagingResponse.CNAME + "#setRequestStatus(final CoreServicesStatus value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.requestStatus = value;
    }

    public final void setMessageId(final String value)
    {
        final String methodName = MessagingResponse.CNAME + "#setMessageId(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug(value);
        }

        this.messageId = value;
    }

    public final void setEmailMessage(final EmailMessage value)
    {
        final String methodName = MessagingResponse.CNAME + "#setEmailMessage(final EmailMessage value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Message: {}", value);
        }

        this.emailMessage = value;
    }

    public final void setServiceMessage(final ServiceMessage value)
    {
        final String methodName = MessagingResponse.CNAME + "#setServiceMessage(final ServiceMessage value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Message: {}", value);
        }

        this.svcMessage = value;
    }

    public final void setEmailMessages(final List<EmailMessage> value)
    {
        final String methodName = MessagingResponse.CNAME + "#setEmailMessages(final List<EmailMessage> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.emailMessages = value;
    }

    public final void setSvcMessages(final List<ServiceMessage> value)
    {
        final String methodName = MessagingResponse.CNAME + "#setSvcMessages(final List<ServiceMessage> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.svcMessages = value;
    }

    public final CoreServicesStatus getRequestStatus()
    {
        final String methodName = MessagingResponse.CNAME + "#getRequestStatus()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.requestStatus);
        }

        return this.requestStatus;
    }

    public final String getMessageId()
    {
        final String methodName = MessagingResponse.CNAME + "#getMessageId()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug(this.messageId);
        }

        return this.messageId;
    }

    public final EmailMessage getEmailMessage()
    {
        final String methodName = MessagingResponse.CNAME + "#getEmailMessage()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Message: {}", this.emailMessage);
        }

        return this.emailMessage;
    }

    public final ServiceMessage getSvcMessage()
    {
        final String methodName = MessagingResponse.CNAME + "#getSvcMessage()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Message: {}", this.svcMessage);
        }

        return this.svcMessage;
    }

    public final List<EmailMessage> getEmailMessages()
    {
        final String methodName = MessagingResponse.CNAME + "#getEmailMessages()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.emailMessages);
        }

        return this.emailMessages;
    }

    public final List<ServiceMessage> getSvcMessages()
    {
        final String methodName = MessagingResponse.CNAME + "#getSvcMessages()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.svcMessages);
        }

        return this.svcMessages;
    }

    @Override
    public String toString()
    {
        StringBuilder sBuilder = new StringBuilder()
            .append("[" + this.getClass().getName() + "]" + CoreServicesConstants.LINE_BREAK + "{" + CoreServicesConstants.LINE_BREAK);

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
                        sBuilder.append("\t" + field.getName() + " --> " + field.get(this) + CoreServicesConstants.LINE_BREAK);
                    }
                }
                catch (final IllegalAccessException iax) {}
            }
        }

        sBuilder.append('}');

        return sBuilder.toString();
    }
}
