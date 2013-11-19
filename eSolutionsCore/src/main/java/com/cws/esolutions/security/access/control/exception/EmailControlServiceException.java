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
package com.cws.esolutions.security.access.control.exception;

import com.cws.esolutions.security.exception.SecurityServiceException;
/**
 * eSolutionsCore
 * com.cws.esolutions.security.access.control.exception
 * EmailControlServiceException.java
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
 * kh05451 @ Nov 17, 2012 12:41:13 PM
 *     Created.
 */
public class EmailControlServiceException extends SecurityServiceException
{
    private static final long serialVersionUID = -1245777962692294793L;

    public EmailControlServiceException(final String message)
    {
        super(message);
    }

    public EmailControlServiceException(final Throwable throwable)
    {
        super(throwable);
    }

    public EmailControlServiceException(final String message, final Throwable throwable)
    {
        super(message, throwable);
    }
}
