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
package com.cws.esolutions.core.config;

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

import com.cws.esolutions.core.Constants;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.config
 * ExceptionConfig.java
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
 * kh05451 @ Nov 23, 2012 8:21:09 AM
 *     Created.
 */
@XmlRootElement(name = "exception-config")
@XmlAccessorType(XmlAccessType.NONE)
public final class ExceptionConfig implements Serializable
{
    private String emailFrom = null;
    private boolean sendNotifications = true;
    private List<String> notificationAddress = null;

    private static final long serialVersionUID = -8486613546271441632L;
    private static final String CNAME = ExceptionConfig.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER);

    public final void setSendNotifications(final boolean value)
    {
        final String methodName = ExceptionConfig.CNAME + "#setSendNotifications(final boolean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.sendNotifications = value;
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
            DEBUGGER.debug("Value: {}", this.sendNotifications);
        }

        return this.sendNotifications;
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
            .append("[" + this.getClass().getName() + "]" + Constants.LINE_BREAK + "{" + Constants.LINE_BREAK);

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
