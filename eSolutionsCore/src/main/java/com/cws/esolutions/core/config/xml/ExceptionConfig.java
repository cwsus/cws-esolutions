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
package com.cws.esolutions.core.config.xml;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.config.xml
 * File: ExceptionConfig.java
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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.cws.esolutions.core.CoreServicesConstants;
/**
 * @author cws-khuntly
 * @version 1.0
 * @see java.io.Serializable
 */
@XmlRootElement(name = "exception-config")
@XmlAccessorType(XmlAccessType.NONE)
public final class ExceptionConfig implements Serializable
{
    private String emailFrom = null;
    private List<String> notificationAddress = null;
    private boolean sendExceptionNotifications = false;

    private static final long serialVersionUID = -8486613546271441632L;
    private static final String CNAME = ExceptionConfig.class.getName();

    private static final Logger DEBUGGER = LogManager.getLogger(CoreServicesConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

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
