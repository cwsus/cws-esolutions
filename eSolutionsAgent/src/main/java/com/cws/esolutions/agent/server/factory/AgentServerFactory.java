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
package com.cws.esolutions.agent.server.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.agent.Constants;
import com.cws.esolutions.agent.server.interfaces.AgentServer;
import com.cws.esolutions.agent.server.exception.AgentServerException;
/**
 * eSolutionsAgent
 * com.cws.esolutions.agent.server.factory
 * AgentServerFactory.java
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
 * kh05451 @ Jan 2, 2013 12:42:15 PM
 *     Created.
 */
public class AgentServerFactory
{
    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER);

    private static final String CNAME = AgentServerFactory.class.getName();

    public static final AgentServer getAgentServer(final String className) throws AgentServerException
    {
        final String methodName = AgentServerFactory.CNAME + "#getAgentServer()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        AgentServer agentServer = null;

        try
        {
            agentServer = (AgentServer) Class.forName(className).newInstance();
        }
        catch (InstantiationException ix)
        {
            ERROR_RECORDER.error(ix.getMessage(), ix);

            throw new AgentServerException(ix.getMessage(), ix);
        }
        catch (IllegalAccessException iax)
        {
            ERROR_RECORDER.error(iax.getMessage(), iax);

            throw new AgentServerException(iax.getMessage(), iax);
        }
        catch (ClassNotFoundException cnx)
        {
            ERROR_RECORDER.error(cnx.getMessage(), cnx);

            throw new AgentServerException(cnx.getMessage(), cnx);
        }

        return agentServer;
    }
}