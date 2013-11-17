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
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Modifier;

import com.cws.esolutions.core.Constants;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.processors.dto
 * ServiceMessage.java
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
 * kh05451 @ Oct 31, 2012 8:34:20 AM
 *     Created.
 */
public class ServiceMessage implements Serializable
{
    private Long expiryDate = null;
    private Long submitDate = null;
    private String messageId = null;
    private boolean isActive = false;
    private String messageText = null;
    private String authorEmail = null;
    private boolean doesExpire = false;
    private String messageTitle = null;
    private String messageAuthor = null;
    private boolean isNewMessage = false;

    private static final long serialVersionUID = 5693111856955648085L;
    private static final String CNAME = ServiceMessage.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER);

    public final void setMessageId(final String value)
    {
        final String methodName = ServiceMessage.CNAME + "#setMessageId(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageId = value;
    }

    public final void setIsNewMessage(final boolean value)
    {
        final String methodName = ServiceMessage.CNAME + "#setIsNewMessage(final boolean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.isNewMessage = value;
    }

    public final void setMessageTitle(final String value)
    {
        final String methodName = ServiceMessage.CNAME + "#setMessageTitle(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageTitle = value;
    }

    public final void setMessageText(final String value)
    {
        final String methodName = ServiceMessage.CNAME + "#setMessageText(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageText = value;
    }

    public final void setMessageAuthor(final String value)
    {
        final String methodName = ServiceMessage.CNAME + "#setMessageAuthor(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageAuthor = value;
    }

    public final void setAuthorEmail(final String value)
    {
        final String methodName = ServiceMessage.CNAME + "#setAuthorEmail(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.authorEmail = value;
    }

    public final void setSubmitDate(final Long value)
    {
        final String methodName = ServiceMessage.CNAME + "#setSubmitDate(final Long value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.submitDate = value;
    }

    public final void setIsActive(final boolean value)
    {
        final String methodName = ServiceMessage.CNAME + "#setIsActive(final boolean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.isActive = value;
    }

    public final void setDoesExpire(final boolean value)
    {
        final String methodName = ServiceMessage.CNAME + "#setDoesExpire(final boolean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.doesExpire = value;
    }

    public final void setExpiryDate(final Long value)
    {
        final String methodName = ServiceMessage.CNAME + "#setExpiryDate(final Long value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.expiryDate = value;
    }

    public final String getMessageId()
    {
        final String methodName = ServiceMessage.CNAME + "#getMessageId()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.messageId);
        }

        return this.messageId;
    }

    public final boolean getIsNewMessage()
    {
        final String methodName = ServiceMessage.CNAME + "#getIsNewMessage()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.isNewMessage);
        }

        return this.isNewMessage;
    }

    public final String getMessageTitle()
    {
        final String methodName = ServiceMessage.CNAME + "#getMessageTitle()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.messageTitle);
        }

        return this.messageTitle;
    }

    public final String getMessageText()
    {
        final String methodName = ServiceMessage.CNAME + "#getMessageText()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.messageText);
        }

        return this.messageText;
    }

    public final String getMessageAuthor()
    {
        final String methodName = ServiceMessage.CNAME + "#getMessageAuthor()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.messageAuthor);
        }

        return this.messageAuthor;
    }

    public final String getAuthorEmail()
    {
        final String methodName = ServiceMessage.CNAME + "#getAuthorEmail()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.authorEmail);
        }

        return this.authorEmail;
    }

    public final Long getSubmitDate()
    {
        final String methodName = ServiceMessage.CNAME + "#getSubmitDate()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.submitDate);
        }

        return this.submitDate;
    }

    public final boolean getIsActive()
    {
        final String methodName = ServiceMessage.CNAME + "#getIsActive()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.isActive);
        }

        return this.isActive;
    }

    public final boolean getDoesExpire()
    {
        final String methodName = ServiceMessage.CNAME + "#getDoesExpire()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.doesExpire);
        }

        return this.doesExpire;
    }

    public final Long getExpiryDate()
    {
        final String methodName = ServiceMessage.CNAME + "#getExpiryDate()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.expiryDate);
        }

        return this.expiryDate;
    }

    @Override
    public final String toString()
    {
        final String methodName = ServiceMessage.CNAME + "#toString()";

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
