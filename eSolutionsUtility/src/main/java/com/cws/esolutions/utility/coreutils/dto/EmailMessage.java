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
package com.cws.esolutions.core.utils.dto;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.utils.dto
 * File: EmailMessage.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.util.Map;
import java.util.Date;
import java.util.List;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.cws.esolutions.core.CoreServicesConstants;
/**
 * @author cws-khuntly
 * @version 1.0
 * @see java.io.Serializable
 */
public class EmailMessage implements Serializable
{
	private String lastName = null;
    private Date messageDate = null;
    private String messageId = null;
    private boolean isAlert = false;
    private String firstName = null;
    private String messageBody = null;
    private String messageSubject = null;
    private List<String> emailAddr = null;
    private List<String> messageCC = null;
    private List<String> messageTo = null;
    private List<String> messageBCC = null;
    private String[] messageSources = null;
    private Map<String, InputStream> messageAttachments = null;

    private static final String CNAME = EmailMessage.class.getName();
    private static final long serialVersionUID = -2799691169985063289L;

    private static final Logger DEBUGGER = LogManager.getLogger(CoreServicesConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LogManager.getLogger(CoreServicesConstants.ERROR_LOGGER);

    public final void setIsAlert(final boolean value)
    {
        final String methodName = EmailMessage.CNAME + "#setIsAlert(final boolean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.isAlert = value;
    }

    public final void setMessageId(final String value)
    {
        final String methodName = EmailMessage.CNAME + "#setMessageId(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug(value);
        }

        this.messageId = value;
    }

    public final void setMessageSubject(final String subject)
    {
        final String methodName = EmailMessage.CNAME + "#setMessageSubject(final String subject)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug(subject);
        }

        this.messageSubject = subject;
    }

    public final void setMessageBCC(final List<String> bcc)
    {
        final String methodName = EmailMessage.CNAME + "#setMessageBCC(final List<String bcc)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Address: {}", bcc);
        }

        this.messageBCC = bcc;
    }

    public final void setMessageCC(final List<String> cc)
    {
        final String methodName = EmailMessage.CNAME + "#setMessageCC(final List<String> cc)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Address: {}", cc);
        }

        this.messageCC = cc;
    }

    public final void setMessageTo(final List<String> to)
    {
        final String methodName = EmailMessage.CNAME + "#setMessageTo(final String[] to)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Address: {}", to);
        }

        this.messageTo = to;
    }

    public final void setEmailAddr(final List<String> from)
    {
        final String methodName = EmailMessage.CNAME + "#setEmailAddr(final List<String> from)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Address: {}", from);
        }

        this.emailAddr = from;
    }

    public final void setMessageSources(final String[] sources)
    {
        final String methodName = EmailMessage.CNAME + "#setMessageSources(final String[] sources)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);

            for (String source : sources)
            {
                DEBUGGER.debug(source);
            }
        }

        this.messageSources = sources;
    }

    public final void setMessageBody(final String value)
    {
        final String methodName = EmailMessage.CNAME + "#setMessageBody(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Content: {}", value);
        }

        this.messageBody = value;
    }

    public final void setMessageAttachments(final Map<String, InputStream> value)
    {
        final String methodName = EmailMessage.CNAME + "#setMessageAttachments(final Map<String, InputStream> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageAttachments = value;
    }

    public final void setMessageDate(final Date date)
    {
        final String methodName = EmailMessage.CNAME + "#etMessageDate(final Date value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", date);
        }

        this.messageDate = date;
    }

    public final void setFirstName(final String value)
    {
        final String methodName = EmailMessage.CNAME + "#setFirstName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.firstName = value;
    }

    public final void setLastName(final String value)
    {
        final String methodName = EmailMessage.CNAME + "#setLastName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.lastName = value;
    }

    public final boolean isAlert()
    {
        final String methodName = EmailMessage.CNAME + "#isAlert()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("isAlert: {}", this.isAlert);
        }

        return this.isAlert;
    }

    public final String getMessageId()
    {
        final String methodName = EmailMessage.CNAME + "#getMessageId()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("MessageId: {}", this.messageId );
        }

        return this.messageId;
    }

    public final String getMessageSubject()
    {
        final String methodName = EmailMessage.CNAME + "#getMessageSubject()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug(this.messageSubject);
        }

        return this.messageSubject;
    }

    public final List<String> getMessageBCC()
    {
        final String methodName = EmailMessage.CNAME + "#getMessageBCC()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Address: {}", this.messageBCC);
        }

        return this.messageBCC;
    }

    public final List<String> getMessageCC()
    {
        final String methodName = EmailMessage.CNAME + "#getMessageCC()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Address: {}", this.messageCC);
        }

        return this.messageCC;
    }

    public final List<String> getMessageTo()
    {
        final String methodName = EmailMessage.CNAME + "#getMessageTo()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Address: {}", this.messageTo);
        }

        return this.messageTo;
    }

    public final List<String> getEmailAddr()
    {
        final String methodName = EmailMessage.CNAME + "#getEmailAddr()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Address: {}", this.emailAddr);
        }

        return this.emailAddr;
    }

    public final String[] getMessageSources()
    {
        final String methodName = EmailMessage.CNAME + "#getMessageSources()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);

            for (String str : this.messageSources)
            {
                DEBUGGER.debug("Source: {}", str);
            }
        }

        return this.messageSources;
    }

    public final String getMessageBody()
    {
        final String methodName = EmailMessage.CNAME + "#getMessageBody()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Content: {}", this.messageBody);
        }

        return this.messageBody;
    }

    public final Map<String, InputStream> getMessageAttachments()
    {
        final String methodName = EmailMessage.CNAME + "#getMessageAttachments()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.messageAttachments);
        }

        return this.messageAttachments;
    }

    public final Date getMessageDate()
    {
        final String methodName = EmailMessage.CNAME + "#getMessageDate()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.messageDate);
        }

        return this.messageDate;
    }

    public final String getFirstName()
    {
        final String methodName = EmailMessage.CNAME + "#getFirstName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.firstName);
        }

        return this.firstName;
    }

    public final String getLastName()
    {
        final String methodName = EmailMessage.CNAME + "#getLastName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.lastName);
        }

        return this.lastName;
    }

    @Override
    public final String toString()
    {
        final String methodName = EmailMessage.CNAME + "#toString()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

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
                catch (final IllegalAccessException iax)
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
