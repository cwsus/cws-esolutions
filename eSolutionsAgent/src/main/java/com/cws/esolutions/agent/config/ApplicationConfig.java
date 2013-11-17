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
package com.cws.esolutions.agent.config;

import java.io.File;
import org.slf4j.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Modifier;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.cws.esolutions.agent.Constants;
/**
 * eSolutionsAgent
 * com.cws.esolutions.agent.config
 * ApplicationConfig.java
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
@XmlType(name = "application-config")
@XmlAccessorType(XmlAccessType.NONE)
public final class ApplicationConfig implements Serializable
{
    private String appName = null;
    private int connectTimeout = 0;
    private int messageIdLength = 0;
    private String dateFormat = null;
    private String proxyConfig = null;
    private String nlsFileName = null;
    private String emailAliasId = null;
    private File serviceRootDirectory = null;

    private static final long serialVersionUID = 8635811418615468202L;
    private static final String CNAME = ApplicationConfig.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER);

    public final void setEmailAliasId(final String value)
    {
        final String methodName = ApplicationConfig.CNAME + "#setEmailAliasId(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.emailAliasId = value;
    }

    public final void setAppName(final String value)
    {
        final String methodName = ApplicationConfig.CNAME + "#setAppName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.appName = value;
    }

    public final void setConnectTimeout(final int value)
    {
        final String methodName = ApplicationConfig.CNAME + "#setConnectTimeout(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.connectTimeout = value;
    }

    public final void setMessageIdLength(final int value)
    {
        final String methodName = ApplicationConfig.CNAME + "#setMessageIdLength(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageIdLength = value;
    }

    public final void setDateFormat(final String value)
    {
        final String methodName = ApplicationConfig.CNAME + "#setDateFormat(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.dateFormat = value;
    }

    public final void setNlsFileName(final String value)
    {
        final String methodName = ApplicationConfig.CNAME + "#setNlsFileName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.nlsFileName = value;
    }

    public final void setProxyConfigFile(final String value)
    {
        final String methodName = ApplicationConfig.CNAME + "#setProxyConfigFile(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.proxyConfig = value;
    }

    public final void setServiceRootDirectory(final File value)
    {
        final String methodName = ApplicationConfig.CNAME + "#setServiceRootDirectory(final File value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serviceRootDirectory = value;
    }

    @XmlElement(name = "appName")
    public final String getAppName()
    {
        final String methodName = ApplicationConfig.CNAME + "#getAppName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.appName);
        }

        return this.appName;
    }

    @XmlElement(name = "emailAlias")
    public final String getEmailAliasId()
    {
        final String methodName = ApplicationConfig.CNAME + "#getEmailAliasId()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.emailAliasId);
        }

        return this.emailAliasId;
    }

    @XmlElement(name = "connectTimeout")
    public final int getConnectTimeout()
    {
        final String methodName = ApplicationConfig.CNAME + "#getConnectTimeout()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.connectTimeout);
        }

        return this.connectTimeout;
    }

    @XmlElement(name = "messageIdLength")
    public final int getMessageIdLength()
    {
        final String methodName = ApplicationConfig.CNAME + "#getMessageIdLength()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.messageIdLength);
        }

        return this.messageIdLength;
    }

    @XmlElement(name = "dateFormat")
    public final String getDateFormat()
    {
        final String methodName = ApplicationConfig.CNAME + "#getDateFormat()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.dateFormat);
        }

        return this.dateFormat;
    }

    @XmlElement(name = "nlsFileName")
    public final String getNlsFileName()
    {
        final String methodName = ApplicationConfig.CNAME + "#getNlsFileName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.nlsFileName);
        }

        return this.nlsFileName;
    }

    @XmlElement(name = "proxyConfigFile")
    public final String getProxyConfigFile()
    {
        final String methodName = ApplicationConfig.CNAME + "#getProxyConfigFile()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.proxyConfig);
        }

        return this.proxyConfig;
    }

    @XmlElement(name = "serviceRootDirectory")
    public final File getServiceRootDirectory()
    {
        final String methodName = ApplicationConfig.CNAME + "#getServiceRootDirectory()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.serviceRootDirectory);
        }

        return this.serviceRootDirectory;
    }

    public final String toString()
    {
        final String methodName = ApplicationConfig.CNAME + "#toString()";

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
