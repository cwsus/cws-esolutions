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
package com.cws.esolutions.agent.exception;
/**
 * eSolutionsAgent
 * com.cws.esolutions.agent.exception
 * AgentException.java
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
 * kh05451 @ Nov 5, 2012 8:46:14 AM
 *     Created.
 */
public class AgentException extends Exception
{
    private static final long serialVersionUID = -3556978204620917677L;

    public AgentException(final String message)
    {
        super(message);
    }

    public AgentException(final Throwable throwable)
    {
        super(throwable);
    }

    public AgentException(final String message, final Throwable throwable)
    {
        super(message, throwable);
    }
}
