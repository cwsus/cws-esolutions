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
package com.cws.esolutions.security.config;

import org.slf4j.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.cws.esolutions.security.SecurityConstants;
/**
 * SecurityService
 * com.cws.esolutions.security.config
 * Configuration.java
 *
 *
 *
 * $Id: SecurityConfig.java 2287 2013-01-03 20:52:22Z kmhuntly@gmail.com $
 * $Author: $
 * $Date: 2013-01-03 15:52:22 -0500 (Thu, 03 Jan 2013) $
 * $Revision: 2287 $
 * @author kmhuntly@gmail.com
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Nov 23, 2012 8:21:09 AM
 *     Created.
 */
@XmlType(name = "security-config")
@XmlAccessorType(XmlAccessType.NONE)
public final class SecurityConfig implements Serializable
{
    private int saltLength = 64; // default to 64
    private int maxAttempts = 3; // default of 3
    private int resetTimeout = 30; // default of 30 minutes
    private int smsCodeLength = 8; // default of 8
    private int iterations = 65535; // default to 65535
    private int resetIdLength = 32; // default of 32
    private String authManager = null;
    private String userManager = null;
    private int passwordMinLength = 8; // default of 8 characters
    private int passwordMaxLength = 32; // default of 32 characters
    private int passwordExpiration = 90; // 90 day lifetime
    private String authAlgorithm = null;
    private boolean performAudit = true; // default true to perform audit
    private String passwordManager = null;
    private boolean allowUserReset = false;
    private String signingAlgorithm = null;
    private boolean smsResetEnabled = false;
    private String encryptionAlgorithm = null;

    private static final long serialVersionUID = -338675198961732554L;
    private static final String CNAME = SecurityConfig.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityConstants.ERROR_LOGGER);

    public final void setMaxAttempts(final int value)
    {
        final String methodName = SecurityConfig.CNAME + "#setMaxAttempts(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.maxAttempts = value;
    }

    public final void setPasswordManager(final String value)
    {
        final String methodName = SecurityConfig.CNAME + "#setPasswordManager(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.passwordManager = value;
    }

    public final void setPasswordExpiration(final int value)
    {
        final String methodName = SecurityConfig.CNAME + "#setPasswordExpiration(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.passwordExpiration = value;
    }
    
    public final void setPasswordMinLength(final int value)
    {
        final String methodName = SecurityConfig.CNAME + "#setPasswordMinLength(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.passwordMinLength = value;
    }

    public final void setIterations(final int value)
    {
        final String methodName = SecurityConfig.CNAME + "#setIterations(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.iterations = value;
    }

    public final void setPasswordMaxLength(final int value)
    {
        final String methodName = SecurityConfig.CNAME + "#setPasswordMaxLength(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.passwordMaxLength = value;
    }

    public final void setAllowUserReset(final boolean value)
    {
        final String methodName = SecurityConfig.CNAME + "#setAllowUserReset(final boolean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.allowUserReset = value;
    }

    public final void setAuthAlgorithm(final String value)
    {
        final String methodName = SecurityConfig.CNAME + "#setAuthAlgorithm(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.authAlgorithm = value;
    }

    public final void setSaltLength(final int value)
    {
        final String methodName = SecurityConfig.CNAME + "#setSaltLength(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        if (value >= this.saltLength)
        {
            this.saltLength = value;
        }
    }

    public final void setAuthManager(final String value)
    {
        final String methodName = SecurityConfig.CNAME + "#setAuthManager(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.authManager = value;
    }

    public final void setUserManager(final String value)
    {
        final String methodName = SecurityConfig.CNAME + "#setUserManager(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.userManager = value;
    }

    public final void setPerformAudit(final boolean value)
    {
        final String methodName = SecurityConfig.CNAME + "#setPerformAudit(final boolean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }
        
        this.performAudit = value;
    }

    public final void setResetIdLength(final int value)
    {
        final String methodName = SecurityConfig.CNAME + "#setResetIdLength(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.resetIdLength = value;
    }

    public final void setIsSmsResetEnabled(final boolean value)
    {
        final String methodName = SecurityConfig.CNAME + "#setIsSmsResetEnabled(final boolean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.smsResetEnabled = value;
    }

    public final void setSmsCodeLength(final int value)
    {
        final String methodName = SecurityConfig.CNAME + "#setSmsCodeLength(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.smsCodeLength = value;
    }

    public final void setResetTimeout(final int value)
    {
        final String methodName = SecurityConfig.CNAME + "#setResetTimeout(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.resetTimeout = value;
    }

    public final void setSigningAlgorithm(final String value)
    {
        final String methodName = SecurityConfig.CNAME + "#setSigningAlgorithm(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.signingAlgorithm = value;
    }

    public final void setEncryptionAlgorithm(final String value)
    {
        final String methodName = SecurityConfig.CNAME + "#setEncryptionAlgorithm(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.encryptionAlgorithm = value;
    }

    @XmlElement(name = "maxAttempts")
    public final int getMaxAttempts()
    {
        final String methodName = SecurityConfig.CNAME + "#getMaxAttempts()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.maxAttempts);
        }
        
        return this.maxAttempts;
    }

    @XmlElement(name = "iterations")
    public final int getIterations()
    {
        final String methodName = SecurityConfig.CNAME + "#getIterations()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.iterations);
        }
        
        return this.iterations;
    }

    @XmlElement(name = "authAlgorithm")
    public final String getAuthAlgorithm()
    {
        final String methodName = SecurityConfig.CNAME + "#getAuthAlgorithm()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.authAlgorithm);
        }

        return this.authAlgorithm;
    }

    @XmlElement(name = "passwordManager")
    public final String getPasswordManager()
    {
        final String methodName = SecurityConfig.CNAME + "#getPasswordManager()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.passwordManager);
        }

        return this.passwordManager;
    }

    @XmlElement(name = "passwordExpiration")
    public final int getPasswordExpiration()
    {
        final String methodName = SecurityConfig.CNAME + "#getPasswordExpiration()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.passwordExpiration);
        }

        return this.passwordExpiration;
    }

    @XmlElement(name = "passwordMinLength")
    public final int getPasswordMinLength()
    {
        final String methodName = SecurityConfig.CNAME + "#getPasswordMinLength()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.passwordMinLength);
        }

        return this.passwordMinLength;
    }

    @XmlElement(name = "passwordMaxLength")
    public final int getPasswordMaxLength()
    {
        final String methodName = SecurityConfig.CNAME + "#getPasswordMaxLength()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.passwordMaxLength);
        }

        return this.passwordMaxLength;
    }

    @XmlElement(name = "allowUserReset")
    public final boolean getAllowUserReset()
    {
        final String methodName = SecurityConfig.CNAME + "#getAllowUserReset()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.allowUserReset);
        }

        return this.allowUserReset;
    }

    @XmlElement(name = "saltLength")
    public final int getSaltLength()
    {
        final String methodName = SecurityConfig.CNAME + "#getSaltLength()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.saltLength);
        }

        return this.saltLength;
    }

    @XmlElement(name = "authManager")
    public final String getAuthManager()
    {
        final String methodName = SecurityConfig.CNAME + "#getAuthManager()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.authManager);
        }

        return this.authManager;
    }

    @XmlElement(name = "userManager")
    public final String getUserManager()
    {
        final String methodName = SecurityConfig.CNAME + "#getUserManager()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.userManager);
        }

        return this.userManager;
    }

    @XmlElement(name = "performAudit")
    public final boolean getPerformAudit()
    {
        final String methodName = SecurityConfig.CNAME + "#getPerformAudit()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.performAudit);
        }

        return this.performAudit;
    }

    @XmlElement(name = "resetIdLength")
    public final int getResetIdLength()
    {
        final String methodName = SecurityConfig.CNAME + "#getResetIdLength()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.resetIdLength);
        }

        return this.resetIdLength;
    }

    @XmlElement(name = "smsResetEnabled")
    public final boolean getSmsResetEnabled()
    {
        final String methodName = SecurityConfig.CNAME + "#getSmsResetEnabled()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.smsResetEnabled);
        }

        return this.smsResetEnabled;
    }

    @XmlElement(name = "smsCodeLength")
    public final int getSmsCodeLength()
    {
        final String methodName = SecurityConfig.CNAME + "#getSmsCodeLength()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.smsCodeLength);
        }

        return this.smsCodeLength;
    }

    @XmlElement(name = "resetTimeout")
    public final int getResetTimeout()
    {
        final String methodName = SecurityConfig.CNAME + "#getResetTimeout()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.resetTimeout);
        }

        return this.resetTimeout;
    }

    @XmlElement(name = "signingAlgorithm")
    public final String getSigningAlgorithm()
    {
        final String methodName = SecurityConfig.CNAME + "#getSigningAlgorithm()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.signingAlgorithm);
        }

        return this.signingAlgorithm;
    }

    @XmlElement(name = "encryptionAlgorithm")
    public final String getEncryptionAlgorithm()
    {
        final String methodName = SecurityConfig.CNAME + "#getEncryptionAlgorithm()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.encryptionAlgorithm);
        }

        return this.encryptionAlgorithm;
    }

    public final String toString()
    {
        final String methodName = SecurityConfig.CNAME + "#toString()";

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
