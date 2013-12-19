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
package com.cws.esolutions.agent.jmx.impl;
/*
 * Project: eSolutionsAgent
 * Package: com.cws.esolutions.agent.jmx.impl
 * File: WebSphereJMXConnectorTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import org.junit.Test;
import org.junit.Before;
import org.junit.Assert;
import com.ibm.websphere.management.AdminClient;

import com.cws.esolutions.agent.AgentBean;
import com.cws.esolutions.agent.AgentDaemon;
import com.cws.esolutions.agent.jmx.dto.JMXConnectorObject;
import com.cws.esolutions.agent.jmx.interfaces.JMXConnection;
import com.cws.esolutions.agent.jmx.factory.JMXConnectionFactory;
import com.cws.esolutions.agent.jmx.exception.JMXConnectorException;

public class WebSphereJMXConnectorTest
{
    private static final AgentBean agentBean = AgentBean.getInstance();

    @Before
    public void setUp()
    {
        System.setProperty("LOG_ROOT", "C:/temp");
        System.setProperty("appConfig", "/src/main/resources/eSolutionsServer/config/eSolutionsServer.xml");
        System.setProperty("logConfig", "/src/main/resources/logging/logging.xml");
        
        AgentDaemon.main(new String[] {"start"});
    }

    @Test
    public final void testGetJMXConnector()
    {
        try
        {
            JMXConnection jmxConn = JMXConnectionFactory.createConnector(agentBean.getConfigData().getJmxConfig().getJmxHandler());
            JMXConnectorObject jmxObject = jmxConn.getJMXConnector(agentBean.getConfigData().getJmxConfig().getJmxHandler());

            AdminClient adminClient = (AdminClient) jmxObject.getConnector();

            Assert.assertTrue(adminClient != null);
        }
        catch (JMXConnectorException jcx)
        {
            Assert.fail(jcx.getMessage());
        }
    }
}
