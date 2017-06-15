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
package com.cws.esolutions.agent.processors.impl;
/*
 * Project: eSolutionsAgent
 * Package: com.cws.esolutions.agent.processors.impl
 * File: SystemManagementImplTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly   11/23/2008 22:39:20             Created.
 */
import org.junit.Test;
import org.junit.Before;
import org.junit.Assert;

import com.cws.esolutions.agent.AgentDaemon;
import com.cws.esolutions.agent.enums.AgentStatus;
import com.cws.esolutions.agent.processors.enums.SystemCheckType;
import com.cws.esolutions.agent.processors.dto.ServiceCheckRequest;
import com.cws.esolutions.agent.processors.dto.ServiceCheckResponse;
import com.cws.esolutions.agent.processors.exception.ServiceCheckException;
import com.cws.esolutions.agent.processors.interfaces.IServiceCheckProcessor;

public class ServiceCheckProcessorImplTest
{
    private static final IServiceCheckProcessor processor = new ServiceCheckProcessorImpl();

    @Before
    public void setUp()
    {
        System.setProperty("LOG_ROOT", "C:/temp");
        System.setProperty("appConfig", "/src/main/resources/eSolutionsServer/config/eSolutionsServer.xml");
        System.setProperty("logConfig", "/src/main/resources/logging/logging.xml");
        
        AgentDaemon.main(new String[] {"start"});
    }

    @Test
    public final void testRunSystemCheckNetstatNoPort()
    {
        ServiceCheckRequest request = new ServiceCheckRequest();
        request.setRequestType(SystemCheckType.NETSTAT);

        try
        {
            ServiceCheckResponse response = processor.runSystemCheck(request);

            Assert.assertEquals(AgentStatus.SUCCESS, response.getRequestStatus());
        }
        catch (ServiceCheckException smx)
        {
            Assert.fail(smx.getMessage());
        }
    }

    @Test
    public final void testRunSystemCheckNetstatWithPort()
    {
        ServiceCheckRequest request = new ServiceCheckRequest();
        request.setRequestType(SystemCheckType.NETSTAT);
        request.setPortNumber(8080);

        try
        {
            ServiceCheckResponse response = processor.runSystemCheck(request);

            Assert.assertEquals(AgentStatus.SUCCESS, response.getRequestStatus());
        }
        catch (ServiceCheckException smx)
        {
            Assert.fail(smx.getMessage());
        }
    }

    @Test
    public final void testRunSystemCheckRemoteDate()
    {
        ServiceCheckRequest request = new ServiceCheckRequest();
        request.setRequestType(SystemCheckType.REMOTEDATE);

        try
        {
            ServiceCheckResponse response = processor.runSystemCheck(request);

            Assert.assertEquals(AgentStatus.SUCCESS, response.getRequestStatus());
        }
        catch (ServiceCheckException smx)
        {
            Assert.fail(smx.getMessage());
        }
    }

    @Test
    public final void testRunSystemCheckTelnetWithHostname()
    {
        ServiceCheckRequest request = new ServiceCheckRequest();
        request.setRequestType(SystemCheckType.TELNET);
        request.setPortNumber(8080);
        request.setTargetHost("chibcarray.us.hsbc");

        try
        {
            ServiceCheckResponse response = processor.runSystemCheck(request);

            Assert.assertEquals(AgentStatus.SUCCESS, response.getRequestStatus());
        }
        catch (ServiceCheckException smx)
        {
            Assert.fail(smx.getMessage());
        }
    }

    @Test
    public final void testRunSystemCheckTelnetWithIpAddress()
    {
        ServiceCheckRequest request = new ServiceCheckRequest();
        request.setRequestType(SystemCheckType.TELNET);
        request.setPortNumber(8080);
        request.setTargetHost("161.130.41.93");

        try
        {
            ServiceCheckResponse response = processor.runSystemCheck(request);

            Assert.assertEquals(AgentStatus.SUCCESS, response.getRequestStatus());
        }
        catch (ServiceCheckException smx)
        {
            Assert.fail(smx.getMessage());
        }
    }
}
