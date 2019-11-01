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
package com.cws.esolutions.agent.connectors.factory;
/*
 * Project: eSolutionsAgent
 * Package: com.cws.esolutions.agent.connectors.factory
 * File: AgentConnectorFactory.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly   11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.InvocationTargetException;

import com.cws.esolutions.agent.AgentConstants;
import com.cws.esolutions.agent.connectors.interfaces.AgentConnector;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author cws-khuntly
 * @version 1.0
 */
public class AgentConnectorFactory
{
    private static AgentConnector agentConnector = null;

    private static final Logger DEBUGGER = LoggerFactory.getLogger(AgentConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(AgentConstants.ERROR_LOGGER);

    private static final String CNAME = AgentConnectorFactory.class.getName();

    /**
     * Static method to provide a new or existing instance of an AgentConnector
     *
     * @param className - The fully qualified class name to return
     * @return an instance of a {@link com.cws.esolutions.agent.connectors.interfaces.AgentConnector}
     */
    public static final AgentConnector createAgentConnector(final String className)
    {
        final String methodName = AgentConnectorFactory.CNAME + "#createAgentConnector()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", className);
        }

        if (agentConnector == null)
        {
            try
            {
            	agentConnector = AgentConnector.class.getDeclaredConstructor(Class.forName(className)).newInstance();
                // agentConnector = (AgentConnector) Class.forName(className).newInstance();

                if (DEBUG)
                {
                    DEBUGGER.debug("AgentConnector: {}", agentConnector);
                }
            }
            catch (InstantiationException ix)
            {
                ERROR_RECORDER.error(ix.getMessage(), ix);
            }
            catch (IllegalAccessException iax)
            {
                ERROR_RECORDER.error(iax.getMessage(), iax);
            }
            catch (ClassNotFoundException cnx)
            {
                ERROR_RECORDER.error(cnx.getMessage(), cnx);
            }
            catch (IllegalArgumentException iax)
            {
            	ERROR_RECORDER.error(iax.getMessage(), iax);
			}
            catch (InvocationTargetException itx)
            {
				ERROR_RECORDER.error(itx.getMessage(), itx);
			}
            catch (NoSuchMethodException nsx)
            {
				ERROR_RECORDER.error(nsx.getMessage(), nsx);
			}
            catch (SecurityException sx)
            {
				ERROR_RECORDER.error(sx.getMessage(), sx);
			}
        }

        return agentConnector;
    }
}
