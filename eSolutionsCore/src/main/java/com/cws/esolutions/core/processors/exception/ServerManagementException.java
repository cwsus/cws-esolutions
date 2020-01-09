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
package com.cws.esolutions.core.processors.exception;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.processors.exception
 * File: ServerManagementException.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly   		11/23/2008 22:39:20             Created.
 */
import com.cws.esolutions.core.exception.CoreServiceException;
/**
 * @see com.cws.esolutions.core.exception.CoreServiceException
 */
public class ServerManagementException extends CoreServiceException
{
    private static final long serialVersionUID = -5781381750308274171L;

    /**
     * @see com.cws.esolutions.core.exception.CoreServiceException#CoreServiceException(java.lang.String)
     *
     * @param message - The message for the exception
     */
    public ServerManagementException(final String message)
    {
        super(message);
    }

    /**
     * @see com.cws.esolutions.core.exception.CoreServiceException#CoreServiceException(java.lang.String)
     *
     * @param throwable - The throwable for the exception
     */
    public ServerManagementException(final Throwable throwable)
    {
        super(throwable);
    }

    /**
     * @see com.cws.esolutions.core.exception.CoreServiceException#CoreServiceException(java.lang.String)
     *
     * @param message - The message for the exception
     * @param throwable - The throwable for the exception
     */
    public ServerManagementException(final String message, final Throwable throwable)
    {
        super(message, throwable);
    }
}
