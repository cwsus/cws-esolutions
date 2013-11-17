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
package com.cws.esolutions.security.keymgmt.dto;

import java.io.File;
import org.slf4j.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Modifier;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.dto.UserSecurity;
import com.cws.esolutions.security.SecurityConstants;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
/**
 * eSolutions
 * com.cws.esolutions.security.auth.dto
 * UserAccount.java
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
 * kh05451 @ Oct 15, 2012 9:14:24 AM
 *     Created.
 */
public class KeyManagementRequest implements Serializable
{
    private int keySize = 2048; // default to 2048 (this is the largest size a dsa key can be)
    private String guid = null;
    private String appName = null;
    private File keyDirectory = null;
    private String pubKeyField = null;
    private String keyAlgorithm = null;
    private String applicationId = null;
    private UserAccount userAccount = null;
    private RequestHostInfo hostInfo = null;
    private UserSecurity userSecurity = null;

    private static final long serialVersionUID = -1681539999956583454L;
    private static final String CNAME = KeyManagementRequest.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityConstants.ERROR_LOGGER);

    public final void setHostInfo(final RequestHostInfo value)
    {
        final String methodName = KeyManagementRequest.CNAME + "#setBaseDN(final RequestHostInfo value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.hostInfo = value;
    }

    public final void setUserAccount(final UserAccount value)
    {
        final String methodName = KeyManagementRequest.CNAME + "#setLoginType(final UserAccount value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.userAccount = value;
    }

    public final void setUserSecurity(final UserSecurity value)
    {
        final String methodName = KeyManagementRequest.CNAME + "#setLoginType(final UserSecurity value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.userSecurity = value;
    }

    public final void setAppName(final String value)
    {
        final String methodName = KeyManagementRequest.CNAME + "#setAppName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.appName = value;
    }

    public final void setApplicationId(final String value)
    {
        final String methodName = KeyManagementRequest.CNAME + "#setApplicationId(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.applicationId = value;
    }

    public final void setGuid(final String value)
    {
        final String methodName = KeyManagementRequest.CNAME + "#setGuid(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.guid = value;
    }

    public final void setPubKeyField(final String value)
    {
        final String methodName = KeyManagementRequest.CNAME + "#setPubKeyField(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.pubKeyField = value;
    }

    public final void setKeyAlgorithm(final String value)
    {
        final String methodName = KeyManagementRequest.CNAME + "#setKeyAlgorithm(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.keyAlgorithm = value;
    }

    public final void setKeySize(final int value)
    {
        final String methodName = KeyManagementRequest.CNAME + "#setKeySize(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.keySize = value;
    }

    public final void setKeyDirectory(final File value)
    {
        final String methodName = KeyManagementRequest.CNAME + "#setKeyDirectory(final File value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.keyDirectory = value;
    }

    public final RequestHostInfo getHostInfo()
    {
        final String methodName = KeyManagementRequest.CNAME + "#getHostInfo()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.hostInfo);
        }

        return this.hostInfo;
    }

    public final UserAccount getUserAccount()
    {
        final String methodName = KeyManagementRequest.CNAME + "#getLoginType()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.userAccount);
        }

        return this.userAccount;
    }

    public final UserSecurity getUserSecurity()
    {
        final String methodName = KeyManagementRequest.CNAME + "#getUserSecurity()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.userSecurity);
        }

        return this.userSecurity;
    }

    public final String getAppName()
    {
        final String methodName = KeyManagementRequest.CNAME + "#getAppName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.appName);
        }

        return this.appName;
    }

    public final String getApplicationId()
    {
        final String methodName = KeyManagementRequest.CNAME + "#getApplicationId()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.applicationId);
        }

        return this.applicationId;
    }

    public final String getGuid()
    {
        final String methodName = KeyManagementRequest.CNAME + "#getGuid()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.guid);
        }

        return this.guid;
    }

    public final String getPubKeyField()
    {
        final String methodName = KeyManagementRequest.CNAME + "#getPubKeyField()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.pubKeyField);
        }

        return this.pubKeyField;
    }

    public final String getKeyAlgorithm()
    {
        final String methodName = KeyManagementRequest.CNAME + "#getKeyAlgorithm()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.keyAlgorithm);
        }

        return this.keyAlgorithm;
    }

    public final int getKeySize()
    {
        final String methodName = KeyManagementRequest.CNAME + "#getKeySize()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.keySize);
        }

        return this.keySize;
    }

    public final File getKeyDirectory()
    {
        final String methodName = KeyManagementRequest.CNAME + "#getKeyDirectory()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.keyDirectory);
        }

        return this.keyDirectory;
    }

    @Override
    public final String toString()
    {
        final String methodName = KeyManagementRequest.CNAME + "#toString()";

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

            if (field.getModifiers() != Modifier.STATIC)
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
