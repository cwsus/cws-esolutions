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
package com.cws.esolutions.agent.jmx.mbeans.exception;
/*
 * Project: eSolutionsAgent
 * Package: com.cws.esolutions.agent
 * File: ServiceMBeanException.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import com.cws.esolutions.agent.exception.AgentException;
/**
 * @see com.cws.esolutions.agent.exception.AgentException
 */
public class ServiceMBeanException extends AgentException
{
    private static final long serialVersionUID = 64416270822325805L;

    public ServiceMBeanException(final String message)
    {
        super(message);
    }

    public ServiceMBeanException(final Throwable throwable)
    {
        super(throwable);
    }

    public ServiceMBeanException(final String message, final Throwable throwable)
    {
        super(message, throwable);
    }
}
