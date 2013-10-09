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
package com.cws.esolutions.agent.jmx.interfaces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.agent.Constants;
import com.cws.esolutions.agent.AgentBean;
import com.cws.esolutions.agent.config.JMXConfig;
import com.cws.esolutions.agent.jmx.dto.JMXConnectorObject;
import com.cws.esolutions.agent.jmx.exception.JMXConnectorException;
/**
 * eSolutionsAgent
 * com.cws.esolutions.agent.jmx.interfaces
 * JMXConnection.java
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
 * kh05451 @ Oct 29, 2012 10:19:47 AM
 *     Created.
 */
public interface JMXConnection
{
    static final AgentBean appBean = AgentBean.getInstance();

    static final String JNDI_ROOT= "/jndi/";
    static final String CNAME = JMXConnection.class.getName();
    static final JMXConfig jmxConfig = appBean.getConfigData().getJmxConfig();

    // loggers
    static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger WARN_RECORDER = LoggerFactory.getLogger(Constants.WARN_LOGGER + CNAME);
    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CNAME);

    JMXConnectorObject getJMXConnector(final String mbeanName) throws JMXConnectorException;

    JMXConnectorObject getDeploymentConnector() throws JMXConnectorException;
}
