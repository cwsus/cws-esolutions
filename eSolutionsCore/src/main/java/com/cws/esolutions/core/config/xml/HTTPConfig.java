/*
 *
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
 * File: HTTPConfig.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.cws.esolutions.core.CoreServiceConstants;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 */
@XmlType(name = "http-config")
@XmlAccessorType(XmlAccessType.NONE)
public final class HTTPConfig implements Serializable
{
    private int soTimeout = 10;
    private int socketLinger = 1;
    private int connTimeout = 10;
    private int socketTimeout = 10;
    private Long connMgrTimeout = 1L;
    private String httpVersion = null;
    private boolean staleCheck = true;
    private String keyStoreType = null;
    private String keyStoreFile = null;
    private String keyStorePass = null;
    private String keyStoreSalt = null;
    private String trustStoreType = null;
    private String trustStoreFile = null;
    private String trustStorePass = null;
    private String trustStoreSalt = null;

    private static final String CNAME = HTTPConfig.class.getName();
    private static final long serialVersionUID = 1351165475299389034L;

    private static final Logger DEBUGGER = LoggerFactory.getLogger(CoreServiceConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(CoreServiceConstants.ERROR_LOGGER);

    public final void setSoTimeout(final int value)
    {
        final String methodName = HTTPConfig.CNAME + "#setSoTimeout(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.soTimeout = value;
    }

    public final void setSocketLinger(final int value)
    {
        final String methodName = HTTPConfig.CNAME + "#setSocketLinger(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.socketLinger = value;
    }

    public final void setSocketTimeout(final int value)
    {
        final String methodName = HTTPConfig.CNAME + "#setSocketTimeout(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.socketTimeout = value;
    }

    public final void setConnTimeout(final int value)
    {
        final String methodName = HTTPConfig.CNAME + "#setConnTimeout(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.connTimeout = value;
    }

    public final void setConnMgrTimeout(final Long value)
    {
        final String methodName = HTTPConfig.CNAME + "#setConnMgrTimeout(final Long value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.connMgrTimeout = value;
    }

    public final void setHttpVersion(final String value)
    {
        final String methodName = HTTPConfig.CNAME + "#setHttpVersion(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.httpVersion = value;
    }

    public final void setStaleCheck(final boolean value)
    {
        final String methodName = HTTPConfig.CNAME + "#setStaleCheck(final boolean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.staleCheck = value;
    }

    public final void setTrustStoreType(final String value)
    {
        final String methodName = HTTPConfig.CNAME + "#setTrustStoreType(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.trustStoreType = value;
    }

    public final void setTrustStoreFile(final String value)
    {
        final String methodName = HTTPConfig.CNAME + "#setTrustStoreFile(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.trustStoreFile = value;
    }

    public final void setTrustStorePass(final String value)
    {
        final String methodName = HTTPConfig.CNAME + "#setTrustStorePass(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.trustStorePass = value;
    }

    public final void setTrustStoreSalt(final String value)
    {
        final String methodName = HTTPConfig.CNAME + "#setTrustStoreSalt(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.trustStoreSalt = value;
    }

    public final void setKeyStoreType(final String value)
    {
        final String methodName = HTTPConfig.CNAME + "#setKeyStoreType(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.keyStoreType = value;
    }

    public final void setKeyStoreFile(final String value)
    {
        final String methodName = HTTPConfig.CNAME + "#setKeyStoreFile(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.keyStoreFile = value;
    }

    public final void setKeyStorePass(final String value)
    {
        final String methodName = HTTPConfig.CNAME + "#setKeyStorePass(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.keyStorePass = value;
    }

    public final void setKeyStoreSalt(final String value)
    {
        final String methodName = HTTPConfig.CNAME + "#setKeyStoreSalt(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.keyStoreSalt = value;
    }

    @XmlElement(name = "soTimeout")
    public final int getSoTimeout()
    {
        final String methodName = HTTPConfig.CNAME + "#getSoTimeout()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.soTimeout);
        }

        return this.soTimeout;
    }

    @XmlElement(name = "socketLinger")
    public final int getSocketLinger()
    {
        final String methodName = HTTPConfig.CNAME + "#getSocketLinger()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.socketLinger);
        }

        return this.socketLinger;
    }

    @XmlElement(name = "socketTimeout")
    public final int getSocketTimeout()
    {
        final String methodName = HTTPConfig.CNAME + "#getSocketTimeout()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.socketTimeout);
        }

        return this.socketTimeout;
    }

    @XmlElement(name = "connTimeout")
    public final int getConnTimeout()
    {
        final String methodName = HTTPConfig.CNAME + "#getConnTimeout()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.connTimeout);
        }

        return this.connTimeout;
    }

    @XmlElement(name = "connMgrTimeout")
    public final Long getConnMgrTimeout()
    {
        final String methodName = HTTPConfig.CNAME + "#getConnMgrTimeout()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.connMgrTimeout);
        }

        return this.connMgrTimeout;
    }

    @XmlElement(name = "httpVersion")
    public final String getHttpVersion()
    {
        final String methodName = HTTPConfig.CNAME + "#getHttpVersion()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.httpVersion);
        }

        return this.httpVersion;
    }

    @XmlElement(name = "staleCheck")
    public final boolean getStaleCheck()
    {
        final String methodName = HTTPConfig.CNAME + "#getStaleCheck()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.staleCheck);
        }

        return this.staleCheck;
    }

    @XmlElement(name = "trustStoreType")
    public final String getTrustStoreType()
    {
        final String methodName = HTTPConfig.CNAME + "#getTrustStoreType()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.trustStoreType);
        }

        return this.trustStoreType;
    }

    @XmlElement(name = "trustStoreFile")
    public final String getTrustStoreFile()
    {
        final String methodName = HTTPConfig.CNAME + "#getTrustStoreFile()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.trustStoreFile);
        }

        return this.trustStoreFile;
    }

    @XmlElement(name = "trustStorePass")
    public final String getTrustStorePass()
    {
        final String methodName = HTTPConfig.CNAME + "#getTrustStorePass()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        return this.trustStorePass;
    }

    @XmlElement(name = "trustStoreSalt")
    public final String getTrustStoreSalt()
    {
        final String methodName = HTTPConfig.CNAME + "#getTrustStoreSalt()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        return this.trustStoreSalt;
    }

    @XmlElement(name = "keyStoreType")
    public final String getKeyStoreType()
    {
        final String methodName = HTTPConfig.CNAME + "#getKeyStoreType()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.keyStoreType);
        }

        return this.keyStoreType;
    }

    @XmlElement(name = "keyStoreFile")
    public final String getKeyStoreFile()
    {
        final String methodName = HTTPConfig.CNAME + "#getKeyStoreFile()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.keyStoreFile);
        }

        return this.keyStoreFile;
    }

    @XmlElement(name = "keyStorePass")
    public final String getKeyStorePass()
    {
        final String methodName = HTTPConfig.CNAME + "#getKeyStorePass()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        return this.keyStorePass;
    }

    @XmlElement(name = "keyStoreSalt")
    public final String getKeyStoreSalt()
    {
        final String methodName = HTTPConfig.CNAME + "#getKeyStoreSalt()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        return this.keyStoreSalt;
    }

    @Override
    public final String toString()
    {
        final String methodName = HTTPConfig.CNAME + "#toString()";

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
                    (!(field.getName().equals("serialVersionUID"))) &&
                    (!(field.getName().equals("keyStoreSalt"))) &&
                    (!(field.getName().equals("keyStorePass"))) &&
                    (!(field.getName().equals("trustStoreSalt"))) &&
                    (!(field.getName().equals("trustStorePass"))))
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
