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
package com.cws.esolutions.security.processors.dto;

import java.io.File;
import org.slf4j.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.core.Constants;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.dto.UserSecurity;
import com.cws.esolutions.security.SecurityConstants;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
/**
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.security.processors.dto
 * File: FileSecurityRequest.java
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
 * kh05451 @ Oct 29, 2012 9:44:46 AM
 *     Created.
 */
public class FileSecurityRequest implements Serializable
{
    private String appName = null;
    private File signedFile = null;
    private String algorithm = null;
    private File unsignedFile = null;
    private File decryptedFile = null;
    private File encryptedFile = null;
    private String applicationId = null;
    private UserAccount userAccount = null;
    private RequestHostInfo hostInfo = null;
    private UserSecurity userSecurity = null;

    private static final long serialVersionUID = 3260829311647276588L;
    private static final String CNAME = FileSecurityRequest.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityConstants.ERROR_LOGGER);

    public final void setHostInfo(final RequestHostInfo value)
    {
        final String methodName = FileSecurityRequest.CNAME + "#setBaseDN(final RequestHostInfo value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.hostInfo = value;
    }

    public final void setUserAccount(final UserAccount value)
    {
        final String methodName = FileSecurityRequest.CNAME + "#setLoginType(final UserAccount value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.userAccount = value;
    }

    public final void setUserSecurity(final UserSecurity value)
    {
        final String methodName = FileSecurityRequest.CNAME + "#setLoginType(final UserSecurity value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.userSecurity = value;
    }

    public final void setAppName(final String value)
    {
        final String methodName = FileSecurityRequest.CNAME + "#setAppName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.appName = value;
    }

    public final void setApplicationId(final String value)
    {
        final String methodName = FileSecurityRequest.CNAME + "#setApplicationId(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.applicationId = value;
    }

    public final void setUnsignedFile(final File value)
    {
        final String methodName = FileSecurityRequest.CNAME + "#setUnsignedFile(final File value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.unsignedFile = value;
    }

    public final void setSignedFile(final File value)
    {
        final String methodName = FileSecurityRequest.CNAME + "#setSignedFile(final File value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.signedFile = value;
    }

    public final void setEncryptedFile(final File value)
    {
        final String methodName = FileSecurityRequest.CNAME + "#setEncryptedFile(final File value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.encryptedFile = value;
    }

    public final void setDecryptedFile(final File value)
    {
        final String methodName = FileSecurityRequest.CNAME + "#setDecryptedFile(final File value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.decryptedFile = value;
    }

    public final void setAlgorithm(final String value)
    {
        final String methodName = FileSecurityRequest.CNAME + "#setAlgorithm(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.algorithm = value;
    }

    public final RequestHostInfo getHostInfo()
    {
        final String methodName = FileSecurityRequest.CNAME + "#getHostInfo()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.hostInfo);
        }

        return this.hostInfo;
    }

    public final UserAccount getUserAccount()
    {
        final String methodName = FileSecurityRequest.CNAME + "#getLoginType()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.userAccount);
        }

        return this.userAccount;
    }

    public final UserSecurity getUserSecurity()
    {
        final String methodName = FileSecurityRequest.CNAME + "#getUserSecurity()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.userSecurity);
        }

        return this.userSecurity;
    }

    public final String getAppName()
    {
        final String methodName = FileSecurityRequest.CNAME + "#getAppName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.appName);
        }

        return this.appName;
    }

    public final String getApplicationId()
    {
        final String methodName = FileSecurityRequest.CNAME + "#getApplicationId()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.applicationId);
        }

        return this.applicationId;
    }

    public final File getUnsignedFile()
    {
        final String methodName = FileSecurityRequest.CNAME + "#getUnsignedFile()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.unsignedFile);
        }

        return this.unsignedFile;
    }

    public final File getSignedFile()
    {
        final String methodName = FileSecurityRequest.CNAME + "#getSignedFile()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.signedFile);
        }

        return this.signedFile;
    }

    public final File getEncryptedFile()
    {
        final String methodName = FileSecurityRequest.CNAME + "#getEncryptedFile()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.encryptedFile);
        }

        return this.encryptedFile;
    }

    public final File getDecryptedFile()
    {
        final String methodName = FileSecurityRequest.CNAME + "#getDecryptedFile()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.decryptedFile);
        }

        return this.decryptedFile;
    }

    public final String getAlgorithm()
    {
        final String methodName = FileSecurityRequest.CNAME + "#getAlgorithm()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.algorithm);
        }

        return this.algorithm;
    }

    @Override
    public final String toString()
    {
        final String methodName = FileSecurityRequest.CNAME + "#toString()";

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
