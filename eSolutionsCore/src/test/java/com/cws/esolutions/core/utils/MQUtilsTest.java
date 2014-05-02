/*
 * Copyright (c) 2009 - 2014 CaspersBox Web Services
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
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.utils
 * File: MQUtilsTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import org.junit.Test;
import org.junit.After;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Assert;
import java.util.ArrayList;

import com.cws.esolutions.core.utils.MQUtils;
import com.cws.esolutions.core.CoreServiceBean;
import com.cws.esolutions.agent.dto.AgentRequest;
import com.cws.esolutions.core.listeners.CoreServiceInitializer;
import com.cws.esolutions.core.utils.exception.UtilityException;
import com.cws.esolutions.agent.processors.dto.SystemManagerRequest;
import com.cws.esolutions.agent.processors.enums.SystemManagementType;

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
    public void sendMqMessage()
    {
        SystemManagerRequest system = new SystemManagerRequest();
        system.setMgmtType(SystemManagementType.SYSTEMCHECK);

        AgentRequest request = new AgentRequest();
        request.setAppName("eSolutions");
        request.setProjectId("1");
        request.setRequestPayload(system);

        try
        {
            Assert.assertNotNull(MQUtils.sendMqMessage(this.bean.getConfigData().getAgentConfig().getConnectionName(),
                    new ArrayList<>(
                            Arrays.asList(
                                    this.bean.getConfigData().getAgentConfig().getUsername(),
                                    this.bean.getConfigData().getAgentConfig().getPassword(),
                                    this.bean.getConfigData().getAgentConfig().getSalt())),
                                    this.bean.getConfigData().getAgentConfig().getRequestQueue(),
                                    "N840B7LZZ8FLAM8",
                                    request));
        }
        catch (UtilityException ux)
        {
            Assert.fail(ux.getMessage());
        }
    }

    @Test
    public void getMqMessage()
    {
        try
        {
            Assert.assertNotNull(MQUtils.getMqMessage(this.bean.getConfigData().getAgentConfig().getConnectionName(),
                    new ArrayList<>(
                            Arrays.asList(
                                    this.bean.getConfigData().getAgentConfig().getUsername(),
                                    this.bean.getConfigData().getAgentConfig().getPassword(),
                                    this.bean.getConfigData().getAgentConfig().getSalt())),
                                    this.bean.getConfigData().getAgentConfig().getResponseQueue(),
                                    this.bean.getConfigData().getAgentConfig().getTimeout(),
                                    "6mUhCACmfS47lysyKfkPlg56g3p92W6Q7CcAq1julEST3NLpSpV20EL3WycSh3mi"));
        }
        catch (UtilityException ux)
        {
            Assert.fail(ux.getMessage());
        }
    }

    @After
    public void tearDown()
    {
        CoreServiceInitializer.shutdown();
    }
}
