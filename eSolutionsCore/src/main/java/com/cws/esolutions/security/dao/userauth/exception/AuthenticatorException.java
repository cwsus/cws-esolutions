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
package com.cws.esolutions.security.dao.userauth.exception;

import com.unboundid.ldap.sdk.ResultCode;
import com.cws.esolutions.security.exception.SecurityServiceException;
/**
 * SecurityService
 * com.cws.esolutions.security.exception
 * AuthenticatorException.java
 *
 *
 *
 * $Id: AuthenticatorException.java 2287 2013-01-03 20:52:22Z kmhuntly@gmail.com $
 * $Author: $
 * $Date: 2013-01-03 15:52:22 -0500 (Thu, 03 Jan 2013) $
 * $Revision: 2287 $
 * @author kmhuntly@gmail.com
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Oct 26, 2012 4:45:25 PM
 *     Created.
 */
public class AuthenticatorException extends SecurityServiceException
{
    private ResultCode resultCode = null;

    private static final long serialVersionUID = -8824085932178422693L;

    public final void setResultCode(final ResultCode value)
    {
        this.resultCode = value;
    }

    public final ResultCode getResultCode()
    {
        return this.resultCode;
    }

    public AuthenticatorException(final String message)
    {
        super(message);
    }

    public AuthenticatorException(final Throwable throwable)
    {
        super(throwable);
    }

    public AuthenticatorException(final String message, final Throwable throwable)
    {
        super(message, throwable);
    }

    public AuthenticatorException(final ResultCode code, final String message, final Throwable throwable)
    {
        super(code, message, throwable);

        this.resultCode = code;
    }
}
