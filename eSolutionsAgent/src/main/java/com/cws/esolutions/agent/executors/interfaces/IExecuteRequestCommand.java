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
package com.cws.esolutions.agent.executors.interfaces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.agent.Constants;
import com.cws.esolutions.agent.executors.dto.ExecuteCommandRequest;
import com.cws.esolutions.agent.executors.dto.ExecuteCommandResponse;
import com.cws.esolutions.agent.executors.exception.ExecuteCommandException;
/**
 * eSolutionsAgent
 * com.cws.esolutions.agent.executors.interfaces
 * IExecuteRequestCommand.java
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
 * kh05451 @ Oct 29, 2012 9:44:46 AM
 *     Created.
 */
public interface IExecuteRequestCommand
{
    static final String CNAME = IExecuteRequestCommand.class.getName();

    static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CNAME);

    /**
     * 
     * @author kmhuntly@gmail.com
     * @param request
     * @return
     * @throws ExecuteCommandException
     */
    ExecuteCommandResponse executeCommand(final ExecuteCommandRequest request) throws ExecuteCommandException;
}
