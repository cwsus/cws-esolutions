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
package com.cws.esolutions.security.config.xml;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.config.xml
 * File: SecurityConfig.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.io.Serializable;
import java.lang.reflect.Field;
import org.apache.logging.log4j.Logger;
import jakarta.xml.bind.annotation.XmlType;
import org.apache.logging.log4j.LogManager;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.SecurityServiceConstants;
/**
 * @author cws-khuntly
 * @version 1.0
 * @see java.io.Serializable
 */
@XmlType(name = "security-config")
@XmlAccessorType(XmlAccessType.NONE)
public final class SecurityConfig implements Serializable
{
    private int keyBits = 256;
    private int otpVariance = 0;
    private int saltLength = 32; // default to 64
    private int maxAttempts = 3; // default of 3
    private int resetTimeout = 30; // default of 30 minutes
    private int smsCodeLength = 8; // default of 8
    private int iterations = 65535; // default to 65535
    private int resetIdLength = 32; // default of 32
    private String userSecDAO = null;
    private String authConfig = null; // FULL path to connection configuration file
    private String auditConfig = null; // FULL path to connection configuration file
    private String authManager = null;
    private String userManager = null;
    private int passwordMinLength = 8; // default of 8 characters
    private String otpAlgorithm = null;
    private int passwordMaxLength = 128; // default of 32 characters
    private int passwordExpiration = 45; // 90 day lifetime
    private String messageDigest = null;
    private String applicationId = null; // only required if its not already fed
    private boolean performAudit = true; // default true to perform audit
    private boolean enableSecurity = true;
    private String applicationName = null; // only required if its not already fed
    private UserAccount svcAccount = null; // service account, if necessary
    private boolean smsResetEnabled = false;
    private String encryptionAlgorithm = "AES"; // algorithm
    private String secretKeyAlgorithm = "PBKDF2WithHmacSHA512"; // secret instance
    private String encryptionInstance = "AES/CBC/PKCS5Padding";  // cipher instance

    private static final long serialVersionUID = -338675198961732554L;
    private static final String CNAME = SecurityConfig.class.getName();

    private static final Logger DEBUGGER = LogManager.getLogger(SecurityServiceConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setApplicationId(final String value)
    {
        final String methodName = SecurityConfig.CNAME + "#setApplicationId(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.applicationId = value;
    }

    public final void setApplicationName(final String value)
    {
        final String methodName = SecurityConfig.CNAME + "#setApplicationName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.applicationName = value;
    }

    public final void setMaxAttempts(final int value)
    {
        final String methodName = SecurityConfig.CNAME + "#setMaxAttempts(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        if (value >= this.maxAttempts)
        {
            this.maxAttempts = value;
        }
    }

    public final void setPasswordExpiration(final int value)
    {
        final String methodName = SecurityConfig.CNAME + "#setPasswordExpiration(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        if (value >= this.passwordExpiration)
        {
            this.passwordExpiration = value;
        }
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

    public final void setPasswordMaxLength(final int value)
    {
        final String methodName = SecurityConfig.CNAME + "#setPasswordMaxLength(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        if (value >= this.passwordMaxLength)
        {
            this.passwordMaxLength = value;
        }
    }

    public final void setIterations(final int value)
    {
        final String methodName = SecurityConfig.CNAME + "#setIterations(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        if (value >= this.iterations)
        {
            this.iterations = value;
        }
    }

    public final void setMessageDigest(final String value)
    {
        final String methodName = SecurityConfig.CNAME + "#setMessageDigest(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageDigest = value;
    }

    public final void setOtpAlgorithm(final String value)
    {
        final String methodName = SecurityConfig.CNAME + "#setOtpAlgorithm(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.otpAlgorithm = value;
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

    public final void setEncryptionInstance(final String value)
    {
        final String methodName = SecurityConfig.CNAME + "#setEncryptionInstance(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.encryptionInstance = value;
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

    public final void setUserSecDAO(final String value)
    {
        final String methodName = SecurityConfig.CNAME + "#setUserSecDAO(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.userSecDAO = value;
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

    public final void setAuthConfig(final String value)
    {
        final String methodName = SecurityConfig.CNAME + "#setAuthConfig(final File value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.authConfig = value;
    }

    public final void setAuditConfig(final String value)
    {
        final String methodName = SecurityConfig.CNAME + "#setAuditConfig(final File value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.auditConfig = value;
    }

    public final void setOtpVariance(final int value)
    {
        final String methodName = SecurityConfig.CNAME + "#setOtpVariance(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.otpVariance = value;
    }

    public final void setSvcAccount(final UserAccount value)
    {
        final String methodName = SecurityConfig.CNAME + "#setSvcAccount(final UserAccount value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.svcAccount = value;
    }

    public final void setEnableSecurity(final boolean value)
    {
        final String methodName = SecurityConfig.CNAME + "#setEnableSecurity(final boolean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.enableSecurity = value;
    }

    public final void setKeyBits(final int value)
    {
        final String methodName = SecurityConfig.CNAME + "#setKeyBits(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        if (value >= this.keyBits)
        {
            this.keyBits = value;
        }
    }

    public final void setSecretKeyAlgorithm(final String value)
    {
        final String methodName = SecurityConfig.CNAME + "#setSecretKeyAlgorithm(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.secretKeyAlgorithm = value;
    }

    @XmlElement(name = "applicationId")
    public final String getApplicationId()
    {
        final String methodName = SecurityConfig.CNAME + "#getApplicationId()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.applicationId);
        }

        return this.applicationId;
    }

    @XmlElement(name = "applicationName")
    public final String getApplicationName()
    {
        final String methodName = SecurityConfig.CNAME + "#getApplicationName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.applicationName);
        }

        return this.applicationName;
    }

    @XmlElement(name = "enableSecurity")
    public final boolean getEnableSecurity()
    {
        final String methodName = SecurityConfig.CNAME + "#getEnableSecurity()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.enableSecurity);
        }

        return this.enableSecurity;
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

    @XmlElement(name = "messageDigest")
    public final String getMessageDigest()
    {
        final String methodName = SecurityConfig.CNAME + "#getMessageDigest()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.messageDigest);
        }

        return this.messageDigest;
    }

    @XmlElement(name = "otpAlgorithm")
    public final String getOtpAlgorithm()
    {
        final String methodName = SecurityConfig.CNAME + "#getOtpAlgorithm()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.otpAlgorithm);
        }

        return this.otpAlgorithm;
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

    @XmlElement(name = "encryptionInstance")
    public final String getEncryptionInstance()
    {
        final String methodName = SecurityConfig.CNAME + "#getEncryptionInstance()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.encryptionInstance);
        }

        return this.encryptionInstance;
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

    @XmlElement(name = "userSecDAO")
    public final String getUserSecDAO()
    {
        final String methodName = SecurityConfig.CNAME + "#getUserSecDAO()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.userSecDAO);
        }

        return this.userSecDAO;
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

    @XmlElement(name = "authConfig")
    public final String getAuthConfig()
    {
        final String methodName = SecurityConfig.CNAME + "#getAuthConfig()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.authConfig);
        }
        
        return this.authConfig;
    }

    @XmlElement(name = "auditConfig")
    public final String getAuditConfig()
    {
        final String methodName = SecurityConfig.CNAME + "#getAuditConfig()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.auditConfig);
        }
        
        return this.auditConfig;
    }

    @XmlElement(name = "otpVariance")
    public final int getOtpVariance()
    {
        final String methodName = SecurityConfig.CNAME + "#getOtpVariance()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.otpVariance);
        }
        
        return this.otpVariance;
    }

    @XmlElement(name = "serviceAccount")
    public final UserAccount getSvcAccount()
    {
        final String methodName = SecurityConfig.CNAME + "#getSvcAccount()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.svcAccount);
        }

        return this.svcAccount;
    }

    @XmlElement(name = "keyBits")
    public final int getKeyBits()
    {
        final String methodName = SecurityConfig.CNAME + "#getKeyBits()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.keyBits);
        }

        return this.keyBits;
    }

    @XmlElement(name = "secretKeyAlgorithm")
    public final String getSecretKeyAlgorithm()
    {
        final String methodName = SecurityConfig.CNAME + "#getSecretKeyAlgorithm()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.secretKeyAlgorithm);
        }

        return this.secretKeyAlgorithm;
    }

    @Override
    public final String toString()
    {
        StringBuilder sBuilder = new StringBuilder()
            .append("[" + this.getClass().getName() + "]" + SecurityServiceConstants.LINE_BREAK + "{" + SecurityServiceConstants.LINE_BREAK);

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
                        sBuilder.append("\t" + field.getName() + " --> " + field.get(this) + SecurityServiceConstants.LINE_BREAK);
                    }
                }
                catch (final IllegalAccessException iax) {}
            }
        }

        sBuilder.append('}');

        return sBuilder.toString();
    }
}
