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
package com.cws.esolutions.agent.connectors.interfaces;
/*
 * Project: eSolutionsAgent
 * Package: com.cws.esolutions.agent.connectors.interfaces
 * File: AgentConnector.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly   11/23/2008 22:39:20             Created.
 */
import com.cws.esolutions.agent.config.xml.JMXConfig;
import com.cws.esolutions.agent.connectors.exception.AgentConnectorException;
/**
 * eSolutionsAgent
 * com.cws.esolutions.agent.connectors.interfaces
 * AgentConnector.java
 *
 * TODO: Add class description
 *
 * @author cws-khuntly
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * cws-khuntly @ Dec 2, 2014 10:14:17 AM
 *     Created.
 */
public interface AgentConnector
{
    /**
     * 
     * TODO: Add in the method description/comments
     *
     * @param mbeanName - The mbean to load
     * @param jmxConfig - The JMX configuration to use
     * @return AgentConnector - The AgentConnector class
     * @throws AgentConnectorException - AgentConnectorException
     */
    public Object getJMXConnector(final String mbeanName, final JMXConfig jmxConfig) throws AgentConnectorException;
}
