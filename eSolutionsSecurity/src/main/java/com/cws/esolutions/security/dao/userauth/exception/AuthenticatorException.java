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
package com.cws.esolutions.security.dao.userauth.exception;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.dao.userauth.exception
 * File: AuthenticatorException.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import com.unboundid.ldap.sdk.ResultCode;
import com.cws.esolutions.security.exception.SecurityServiceException;
/**
 * @see com.cws.esolutions.security.exception.SecurityServiceException
 */
public class AuthenticatorException extends SecurityServiceException
{
    private ResultCode resultCode = null;

    private static final long serialVersionUID = -8824085932178422693L;

    /**
     * @param value - The <code>ResultCode</code> value
     */
    public final void setResultCode(final ResultCode value)
    {
        this.resultCode = value;
    }

    /**
     * @return ResultCode - The <code>ResultCode</code> value
     */
    public final ResultCode getResultCode()
    {
        return this.resultCode;
    }

    /**
     * @param message - The thrown exception message
     * @see com.cws.esolutions.security.exception.SecurityServiceException#SecurityServiceException(java.lang.String)
     */
    public AuthenticatorException(final String message)
    {
        super(message);
    }

    /**
     * @param throwable - The thrown exception
     * @see com.cws.esolutions.security.exception.SecurityServiceException#SecurityServiceException(java.lang.Throwable)
     */
    public AuthenticatorException(final Throwable throwable)
    {
        super(throwable);
    }

    /**
     * @param message - The thrown exception message
     * @param throwable - The thrown exception
     * @see com.cws.esolutions.security.exception.SecurityServiceException#SecurityServiceException(java.lang.String, java.lang.Throwable)
     */
    public AuthenticatorException(final String message, final Throwable throwable)
    {
        super(message, throwable);
    }

    /**
     * @param code - The <code>ResultCode</code> value
     * @param message - The thrown exception message
     * @param throwable - The thrown exception
     * @see com.cws.esolutions.security.exception.SecurityServiceException#SecurityServiceException(com.unboundid.ldap.sdk.ResultCode, java.lang.String, java.lang.Throwable)
     */
    public AuthenticatorException(final ResultCode code, final String message, final Throwable throwable)
    {
        super(code, message, throwable);

        this.resultCode = code;
    }
}
