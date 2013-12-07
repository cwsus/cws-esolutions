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
package com.cws.esolutions.security.exception;

import com.unboundid.ldap.sdk.ResultCode;
/**
 * SecurityService
 * com.cws.esolutions.security.exception
 * SecurityServiceException.java
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
 * kh05451 @ Oct 29, 2012 3:41:35 PM
 *     Created.
 */
public class SecurityServiceException extends Exception
{
    private static final long serialVersionUID = -5953286132656674063L;

    public SecurityServiceException(final String message)
    {
        super(message);
    }

    public SecurityServiceException(final Throwable throwable)
    {
        super(throwable);
    }

    public SecurityServiceException(final String message, final Throwable throwable)
    {
        super(message, throwable);
    }

    public SecurityServiceException(@SuppressWarnings("unused") final ResultCode code, final String message, final Throwable throwable)
    {
        super(message, throwable);
    }
}
