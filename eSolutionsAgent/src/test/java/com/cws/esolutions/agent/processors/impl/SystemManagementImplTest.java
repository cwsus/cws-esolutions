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
package com.cws.esolutions.agent.processors.impl;

import org.junit.Test;
import org.junit.Before;
import org.junit.Assert;

import com.cws.esolutions.agent.AgentDaemon;
import com.cws.esolutions.agent.enums.AgentStatus;
import com.cws.esolutions.agent.processors.enums.SystemCheckType;
import com.cws.esolutions.agent.processors.dto.SystemManagerRequest;
import com.cws.esolutions.agent.processors.dto.SystemManagerResponse;
import com.cws.esolutions.agent.processors.enums.SystemManagementType;
import com.cws.esolutions.agent.processors.exception.SystemManagerException;
import com.cws.esolutions.agent.processors.interfaces.ISystemManagerProcessor;
/**
 * eSolutionsAgent
 * com.cws.esolutions.agent.processors.impl
 * SystemManagementImplTest.java
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
 * 35033355 @ May 20, 2013 9:08:29 AM
 *     Created.
 */
public class SystemManagementImplTest
{
    private static final ISystemManagerProcessor systemMgr = new SystemManagerProcessorImpl();

    @Before
    public void setUp() throws Exception
    {
        System.setProperty("LOG_ROOT", "C:/temp");
        System.setProperty("appConfig", "/src/main/resources/eSolutionsServer/config/eSolutionsServer.xml");
        System.setProperty("logConfig", "/src/main/resources/logging/logging.xml");
        
        AgentDaemon.main(new String[] {"start"});
    }

    @Test
    public final void testRunSystemCheckNetstatNoPort()
    {
        SystemManagerRequest request = new SystemManagerRequest();
        request.setInstallAgent(false);
        request.setMgmtType(SystemManagementType.SYSTEMCHECK);
        request.setRequestType(SystemCheckType.NETSTAT);
        request.setTargetServer("localhost");

        try
        {
            SystemManagerResponse response = systemMgr.runSystemCheck(request);

            Assert.assertEquals(AgentStatus.SUCCESS, response.getRequestStatus());
        }
        catch (SystemManagerException smx)
        {
            Assert.fail(smx.getMessage());
        }
    }

    @Test
    public final void testRunSystemCheckNetstatWithPort()
    {
        SystemManagerRequest request = new SystemManagerRequest();
        request.setInstallAgent(false);
        request.setMgmtType(SystemManagementType.SYSTEMCHECK);
        request.setRequestType(SystemCheckType.NETSTAT);
        request.setPortNumber(8080);
        request.setTargetServer("localhost");

        try
        {
            SystemManagerResponse response = systemMgr.runSystemCheck(request);

            Assert.assertEquals(AgentStatus.SUCCESS, response.getRequestStatus());
        }
        catch (SystemManagerException smx)
        {
            Assert.fail(smx.getMessage());
        }
    }

    @Test
    public final void testRunSystemCheckRemoteDate()
    {
        SystemManagerRequest request = new SystemManagerRequest();
        request.setInstallAgent(false);
        request.setMgmtType(SystemManagementType.SYSTEMCHECK);
        request.setRequestType(SystemCheckType.REMOTEDATE);
        request.setTargetServer("localhost");

        try
        {
            SystemManagerResponse response = systemMgr.runSystemCheck(request);

            Assert.assertEquals(AgentStatus.SUCCESS, response.getRequestStatus());
        }
        catch (SystemManagerException smx)
        {
            Assert.fail(smx.getMessage());
        }
    }

    @Test
    public final void testRunSystemCheckTelnetWithHostname()
    {
        SystemManagerRequest request = new SystemManagerRequest();
        request.setInstallAgent(false);
        request.setMgmtType(SystemManagementType.SYSTEMCHECK);
        request.setRequestType(SystemCheckType.TELNET);
        request.setPortNumber(8080);
        request.setTargetServer("chibcarray.us.hsbc");

        try
        {
            SystemManagerResponse response = systemMgr.runSystemCheck(request);

            Assert.assertEquals(AgentStatus.SUCCESS, response.getRequestStatus());
        }
        catch (SystemManagerException smx)
        {
            Assert.fail(smx.getMessage());
        }
    }

    @Test
    public final void testRunSystemCheckTelnetWithIpAddress()
    {
        SystemManagerRequest request = new SystemManagerRequest();
        request.setInstallAgent(false);
        request.setMgmtType(SystemManagementType.SYSTEMCHECK);
        request.setRequestType(SystemCheckType.TELNET);
        request.setPortNumber(8080);
        request.setTargetServer("161.130.41.93");

        try
        {
            SystemManagerResponse response = systemMgr.runSystemCheck(request);

            Assert.assertEquals(AgentStatus.SUCCESS, response.getRequestStatus());
        }
        catch (SystemManagerException smx)
        {
            Assert.fail(smx.getMessage());
        }
    }
}
