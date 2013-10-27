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
 * com.cws.esolutions.core.processors.exception
 * SystemCheckException.java
 *
 *
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
 * 35033355 @ Jun 6, 2013 9:44:34 AM
 *     Created.
 */
public class SystemCheckException extends CoreServiceException
{
    private static final long serialVersionUID = 6179561480017277813L;

    public SystemCheckException(final String message)
    {
        super(message);
    }

    public SystemCheckException(final Throwable throwable)
    {
        super(throwable);
    }

    public SystemCheckException(final String message, final Throwable throwable)
    {
        super(message, throwable);
    }
}
