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
/*
 * SecurityService
 * com.cws.esolutions.security.access.control.exception
 * public class UserControlServiceException{ }
 *
 *
 *
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * khuntly              Nov 17, 2012 12:43:26 PM                 Created
 */
public class UserControlServiceException extends SecurityServiceException
{
    private static final long serialVersionUID = 1950481349510842592L;

    public UserControlServiceException(final String message)
    {
        super(message);
    }

    public UserControlServiceException(final Throwable throwable)
    {
        super(throwable);
    }

    public UserControlServiceException(final String message, final Throwable throwable)
    {
        super(message, throwable);
    }
}
