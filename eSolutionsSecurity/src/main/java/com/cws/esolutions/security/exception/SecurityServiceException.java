/*
 * Copyright (c) 2009 - 2017 CaspersBox Web Services
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
package com.cws.esolutions.security.exception;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.exception
 * File: SecurityServiceException.java
 *
 * History
 * ----------------------------------------------------------------------------
 * cws-khuntly @ Jan 4, 2013 3:36:54 PM
 *     Created.
 */
import com.unboundid.ldap.sdk.ResultCode;
/**
 * @see java.lang.Exception
 */
public class SecurityServiceException extends Exception
{
    private static final long serialVersionUID = -5953286132656674063L;

    /**
     * @param message - The thrown exception message
     * @see java.lang.Exception#Exception(java.lang.String)
     */
    public SecurityServiceException(final String message)
    {
        super(message);
    }

    /**
     * @param throwable - The thrown exception
     * @see java.lang.Exception#Exception(java.lang.Throwable)
     */
    public SecurityServiceException(final Throwable throwable)
    {
        super(throwable);
    }

    /**
     * @param message - The thrown exception message
     * @param throwable - The thrown exception
     * @see java.lang.Exception#Exception(java.lang.String, java.lang.Throwable)
     */
    public SecurityServiceException(final String message, final Throwable throwable)
    {
        super(message, throwable);
    }

    /**
     * @param code - The <code>ResultCode</code> of the operation
     * @param message - The thrown exception message
     * @param throwable - The thrown exception
     */
    public SecurityServiceException(final ResultCode code, final String message, final Throwable throwable)
    {
        super(message + " : " + code, throwable);
    }
}
