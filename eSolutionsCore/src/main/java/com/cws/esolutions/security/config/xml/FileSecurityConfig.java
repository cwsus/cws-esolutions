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

import org.slf4j.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

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
@XmlType(name = "file-security-config")
@XmlAccessorType(XmlAccessType.NONE)
public final class FileSecurityConfig implements Serializable
{
    private String signatureAlgorithm = null;
    private String encryptionAlgorithm = null;

    private static final String CNAME = FileSecurityConfig.class.getName();
    private static final long serialVersionUID = -6570315707032254956L;

    private static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityConstants.ERROR_LOGGER);

    public final void setSignatureAlgorithm(final String value)
    {
        final String methodName = FileSecurityConfig.CNAME + "#setSignatureAlgorithm(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.signatureAlgorithm = value;
    }

    public final void setEncryptionAlgorithm(final String value)
    {
        final String methodName = FileSecurityConfig.CNAME + "#setEncryptionAlgorithm(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.encryptionAlgorithm = value;
    }

    @XmlElement(name = "signatureAlgorithm")
    public final String getSignatureAlgorithm()
    {
        final String methodName = FileSecurityConfig.CNAME + "#getSignatureAlgorithm()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.signatureAlgorithm);
        }

        return this.signatureAlgorithm;
    }

    @XmlElement(name = "encryptionAlgorithm")
    public final String getEncryptionAlgorithm()
    {
        final String methodName = FileSecurityConfig.CNAME + "#getEncryptionAlgorithm()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.encryptionAlgorithm);
        }

        return this.encryptionAlgorithm;
    }

    @Override
    public final String toString()
    {
        final String methodName = FileSecurityConfig.CNAME + "#toString()";

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
