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
package com.cws.esolutions.agent.mq;
/*
 * Project: eSolutionsAgent
 * Package: com.cws.esolutions.agent
 * File: AgentConstants.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly   11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
import javax.jms.JMSException;
import org.slf4j.LoggerFactory;
import javax.jms.ExceptionListener;

import com.cws.esolutions.agent.AgentConstants;
/**
 * MQ Exception Handler
 *
 * @author cws-khuntly
 * @version 1.0
 */
public class MQExceptionHandler implements ExceptionListener
{
    private static final String CNAME = MQExceptionHandler.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(AgentConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(AgentConstants.ERROR_LOGGER + CNAME);

    public void onException(final JMSException exception)
    {
        final String methodName = MQExceptionHandler.CNAME + "#onException(final JMSException exception)";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("JMSException: {}", exception);
        }

        ERROR_RECORDER.error(exception.getMessage(), exception);
    }
}
