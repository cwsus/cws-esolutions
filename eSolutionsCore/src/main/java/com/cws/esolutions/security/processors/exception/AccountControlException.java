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
package com.cws.esolutions.security.processors.exception;

import com.cws.esolutions.security.exception.SecurityServiceException;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.security.audit.processors.interfaces
 * File: IAuditProcessor.java
 *
 * History
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * Kevin Huntly         11/23/2008 22:39:20             Created.
 */
/**
 * @see com.cws.esolutions.security.exception.SecurityServiceException
 */
public class AccountControlException extends SecurityServiceException
{
    private static final long serialVersionUID = 7886333091966853193L;

    public AccountControlException(final String message)
    {
        super(message);
    }

    public AccountControlException(final Throwable throwable)
    {
        super(throwable);
    }

    public AccountControlException(final String message, final Throwable throwable)
    {
        super(message, throwable);
    }
}
