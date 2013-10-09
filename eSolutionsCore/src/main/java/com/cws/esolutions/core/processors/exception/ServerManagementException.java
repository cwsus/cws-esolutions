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
package com.cws.esolutions.core.processors.exception;

import com.cws.esolutions.core.exception.CoreServiceException;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.exception
 * ServerManagementException.java
 *
 *
 *
 * $Id: ServerManagementException.java 2287 2013-01-03 20:52:22Z kmhuntly@gmail.com $
 * $Author: $
 * $Date: 2013-01-03 15:52:22 -0500 (Thu, 03 Jan 2013) $
 * $Revision: 2287 $
 * @author kmhuntly@gmail.com
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Oct 29, 2012 9:44:46 AM
 *     Created.
 */
public class ServerManagementException extends CoreServiceException
{
    private static final long serialVersionUID = -5781381750308274171L;

    public ServerManagementException(final String message)
    {
        super(message);
    }

    public ServerManagementException(final Throwable throwable)
    {
        super(throwable);
    }

    public ServerManagementException(final String message, final Throwable throwable)
    {
        super(message, throwable);
    }
}
