/*
 * Copyright (c) 2009 - 2014 CaspersBox Web Services
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
 * File: ApplicationConfig.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.List;
import org.slf4j.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.cws.esolutions.core.CoreServiceConstants;
/**
 * @author khuntly
 * @version 1.0
 * @see java.io.Serializable
 */
@XmlType(name = "application-config")
@XmlAccessorType(XmlAccessType.NONE)
public final class ApplicationConfig implements Serializable
{
    private String appName = null;
    private String instance = null;
    private String encoding = null;
    private int connectTimeout = 0;
    private int messageIdLength = 0;
    private String algorithm = null;
    private String dateFormat = null;
    private String proxyConfig = null;
    private String nlsFileName = null;
    private String emailAliasId = null;
    private String agentBundleSource = null;
    private String virtualManagerClass = null;
    private List<String> serviceAccount = null;

    private static final long serialVersionUID = -2125011070971484380L;
    private static final String CNAME = ApplicationConfig.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(CoreServiceConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(CoreServiceConstants.ERROR_LOGGER);

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

    public final void setVirtualManagerClass(final String value)
    {
        final String methodName = ApplicationConfig.CNAME + "#setVirtualManagerClass(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.virtualManagerClass = value;
    }

    public final void setAgentBundleSource(final String value)
    {
        final String methodName = ApplicationConfig.CNAME + "#setAgentBundleSource(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.agentBundleSource = value;
    }

    public final void setServiceAccount(final List<String> value)
    {
        final String methodName = ApplicationConfig.CNAME + "#setServiceAccount(final List<String> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serviceAccount = value;
    }

    public final void setAlgorithm(final String value)
    {
        final String methodName = ApplicationConfig.CNAME + "#setAlgorithm(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.algorithm = value;
    }

    public final void setInstance(final String value)
    {
        final String methodName = ApplicationConfig.CNAME + "#setInstance(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.instance = value;
    }

    public final void setEncoding(final String value)
    {
        final String methodName = ApplicationConfig.CNAME + "#setEncoding(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.encoding = value;
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

    @XmlElement(name = "virtualManagerClass")
    public final String getVirtualManagerClass()
    {
        final String methodName = ApplicationConfig.CNAME + "#getVirtualManagerClass()";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.virtualManagerClass);
        }
        
        return this.virtualManagerClass;
    }

    @XmlElement(name = "agentBundleSource")
    public final String getAgentBundleSource()
    {
        final String methodName = ApplicationConfig.CNAME + "#getAgentBundleSource()";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.agentBundleSource);
        }
        
        return this.agentBundleSource;
    }

    @XmlElement(name = "accountInformation")
    @XmlElementWrapper(name = "serviceAccount")
    public final List<String> getServiceAccount()
    {
        final String methodName = ApplicationConfig.CNAME + "#getServiceAccount()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.serviceAccount);
        }

        return this.serviceAccount;
    }

    @XmlElement(name = "algorithm")
    public final String getAlgorithm()
    {
        final String methodName = ApplicationConfig.CNAME + "#getAlgorithm()";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.algorithm);
        }
        
        return this.algorithm;
    }

    @XmlElement(name = "instance")
    public final String getInstance()
    {
        final String methodName = ApplicationConfig.CNAME + "#getInstance()";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.instance);
        }
        
        return this.instance;
    }

    @XmlElement(name = "encoding")
    public final String getEncoding()
    {
        final String methodName = ApplicationConfig.CNAME + "#getEncoding()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.encoding);
        }

        return this.encoding;
    }

    @Override
    public final String toString()
    {
        final String methodName = ApplicationConfig.CNAME + "#toString()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        StringBuilder sBuilder = new StringBuilder()
            .append("[" + this.getClass().getName() + "]" + CoreServiceConstants.LINE_BREAK + "{" + CoreServiceConstants.LINE_BREAK);

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
                        sBuilder.append("\t" + field.getName() + " --> " + field.get(this) + CoreServiceConstants.LINE_BREAK);
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
