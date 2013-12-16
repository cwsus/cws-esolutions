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
package com.cws.esolutions.core.utils;

import org.junit.Test;
import org.junit.Before;
import org.junit.Assert;

import com.cws.esolutions.core.utils.MQUtils;
import com.cws.esolutions.core.CoreServiceBean;
import com.cws.esolutions.agent.dto.AgentRequest;
import com.cws.esolutions.core.listeners.CoreServiceInitializer;
import com.cws.esolutions.core.utils.exception.UtilityException;
import com.cws.esolutions.agent.processors.enums.SystemCheckType;
import com.cws.esolutions.agent.processors.dto.SystemManagerRequest;
import com.cws.esolutions.agent.processors.enums.SystemManagementType;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.utils
 * MQUtilsTest.java
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
 * 35033355 @ Jun 11, 2013 12:41:03 PM
 *     Created.
 */
public class MQUtilsTest
{
    CoreServiceBean bean = CoreServiceBean.getInstance();

    @Before
    public void setUp()
    {
        try
        {
            CoreServiceInitializer.initializeService("eSolutionsCore/config/ServiceConfig.xml", "logging/logging.xml");
        }
        catch (Exception ex)
        {
            Assert.fail(ex.getMessage());

            System.exit(-1);
        }
    }

    @Test
    public void testSendMqMessage()
    {
        SystemManagerRequest system = new SystemManagerRequest();
        system.setInstallAgent(false);
        system.setMgmtType(SystemManagementType.SYSTEMCHECK);
        system.setRequestType(SystemCheckType.REMOTEDATE);
        system.setTargetServer("localhost");

        AgentRequest request = new AgentRequest();
        request.setAppName("eSolutions");
        request.setProjectId("1");
        request.setRequestPayload(system);

        try
        {
            Assert.assertNotNull(MQUtils.sendMqMessage(this.bean.getConfigData().getAgentConfig().getConnectionName(), this.bean.getConfigData().getAgentConfig().getRequestQueue(), request));
        }
        catch (UtilityException ux)
        {
            Assert.fail(ux.getMessage());
        }
    }

    @Test
    public void testGetMqMessage()
    {
        try
        {
            Assert.assertNotNull(MQUtils.getMqMessage(this.bean.getConfigData().getAgentConfig().getConnectionName(), this.bean.getConfigData().getAgentConfig().getResponseQueue(), "5Np8bVEpOEhqMuKxqOvlGM7Zh8ASkqZddsjLyyV50OnsuNgMwBzS8SLDHGHQgZIe"));
        }
        catch (UtilityException ux)
        {
            Assert.fail(ux.getMessage());
        }
    }
}
