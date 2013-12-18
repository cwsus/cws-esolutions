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
package com.cws.esolutions.security.processors.dto;

import java.io.File;
import org.slf4j.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.security.SecurityConstants;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
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
public class FileSecurityResponse implements Serializable
{
    private File signedFile = null;
    private File encryptedFile = null;
    private boolean isSignatureValid = false;
    private SecurityRequestStatus requestStatus = null;

    private static final long serialVersionUID = 1435610483075480864L;
    private static final String CNAME = FileSecurityResponse.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityConstants.ERROR_LOGGER);

    public final void setRequestStatus(final SecurityRequestStatus value)
    {
        final String methodName = FileSecurityResponse.CNAME + "#setRequestStatus(final SecurityRequestStatus value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.requestStatus = value;
    }

    public final void setSignedFile(final File value)
    {
        final String methodName = FileSecurityResponse.CNAME + "#setSignedFile(final File value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.signedFile = value;
    }

    public final void setEncryptedFile(final File value)
    {
        final String methodName = FileSecurityResponse.CNAME + "#setEncryptedFile(final File value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.encryptedFile = value;
    }

    public final void setIsSignatureValid(final boolean value)
    {
        final String methodName = FileSecurityResponse.CNAME + "#setIsSignatureValid(final boolean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.isSignatureValid = value;
    }

    public final SecurityRequestStatus getRequestStatus()
    {
        final String methodName = FileSecurityResponse.CNAME + "#getMgmtType()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("SecurityRequestStatus: {}", this.requestStatus);
        }

        return this.requestStatus;
    }

    public final File getSignedFile()
    {
        final String methodName = FileSecurityResponse.CNAME + "#getSignedFile()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.signedFile);
        }

        return this.signedFile;
    }

    public final File getEncryptedFile()
    {
        final String methodName = FileSecurityResponse.CNAME + "#getEncryptedFile()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.encryptedFile);
        }

        return this.encryptedFile;
    }

    public final boolean isSignatureValid()
    {
        final String methodName = FileSecurityResponse.CNAME + "#isSignatureValid()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.isSignatureValid);
        }

        return this.isSignatureValid;
    }

    @Override
    public final String toString()
    {
        final String methodName = FileSecurityResponse.CNAME + "#toString()";

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
