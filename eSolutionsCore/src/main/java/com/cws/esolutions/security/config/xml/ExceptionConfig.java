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
package com.cws.esolutions.security.config.xml;

import java.util.List;
import org.slf4j.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.cws.esolutions.security.SecurityConstants;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.security.audit.processors.interfaces
 * File: IAuditProcessor.java
 *
 * History
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * Kevin Huntly         11/23/2008 22:39:20             Created.
 * Kevin Huntly         12/05/2008 13:36:09             Added method to process change requests
 */
@XmlRootElement(name = "exception-config")
@XmlAccessorType(XmlAccessType.NONE)
public final class ExceptionConfig implements Serializable
{
    private String emailFrom = null;
    private List<String> notificationAddress = null;
    private boolean sendExceptionNotifications = true;

    private static final long serialVersionUID = 5834154758068491231L;
    private static final String CNAME = ExceptionConfig.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityConstants.ERROR_LOGGER);

    public final void setSendExceptionNotifications(final boolean value)
    {
        final String methodName = ExceptionConfig.CNAME + "#setSendExceptionNotifications(final boolean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.sendExceptionNotifications = value;
    }

    public final void setEmailFrom(final String value)
    {
        final String methodName = ExceptionConfig.CNAME + "#setEmailFrom(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.emailFrom = value;
    }

    public final void setNotificationAddress(final List<String> value)
    {
        final String methodName = ExceptionConfig.CNAME + "#setNotificationAddress(final List<String> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.notificationAddress = value;
    }

    @XmlElement(name = "sendExceptionNotifications")
    public final boolean getSendExceptionNotifications()
    {
        final String methodName = ExceptionConfig.CNAME + "#getSendExceptionNotifications()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.sendExceptionNotifications);
        }

        return this.sendExceptionNotifications;
    }

    @XmlElement(name = "emailFrom")
    public final String getEmailFrom()
    {
        final String methodName = ExceptionConfig.CNAME + "#getEmailFrom()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.emailFrom);
        }

        return this.emailFrom;
    }

    @XmlElement(name = "emailAddress")
    @XmlElementWrapper(name = "notificationAddresses")
    public final List<String> getNotificationAddress()
    {
        final String methodName = ExceptionConfig.CNAME + "#getNotificationAddress()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.notificationAddress);
        }

        return this.notificationAddress;
    }

    @Override
    public final String toString()
    {
        final String methodName = ExceptionConfig.CNAME + "#toString()";

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
