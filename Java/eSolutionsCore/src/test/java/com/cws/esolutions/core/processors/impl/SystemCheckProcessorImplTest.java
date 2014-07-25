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
package com.cws.esolutions.core.processors.impl;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.processors.impl
 * File: SystemCheckProcessorImplTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import org.junit.Test;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.core.processors.dto.Server;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.core.listeners.CoreServiceInitializer;
import com.cws.esolutions.core.processors.dto.SystemCheckRequest;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.dto.SystemCheckResponse;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.core.processors.exception.SystemCheckException;
import com.cws.esolutions.core.processors.interfaces.ISystemCheckProcessor;

public class SystemCheckProcessorImplTest
{
    private static UserAccount userAccount = new UserAccount();
    private static RequestHostInfo hostInfo = new RequestHostInfo();

    private static final ISystemCheckProcessor processor = new SystemCheckProcessorImpl();

    @Before
    public void setUp()
    {
        try
        {
            hostInfo.setHostAddress("junit");
            hostInfo.setHostName("junit");

            userAccount.setStatus(LoginStatus.SUCCESS);
            userAccount.setGuid("f42fb0ba-4d1e-1126-986f-800cd2650000");
            userAccount.setUsername("khuntly");

            CoreServiceInitializer.initializeService("eSolutionsCore/config/ServiceConfig.xml", "logging/logging.xml");

            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/logging/logging.xml");
        }
        catch (Exception ex)
        {
            Assert.fail(ex.getMessage());

            System.exit(-1);
        }
    }

    @Test
    public void runNetstatCheck()
    {
        Server source = new Server();
        source.setOperHostName("localhost");
        source.setOperIpAddress("127.0.0.1");

        SystemCheckRequest request = new SystemCheckRequest();
        request.setRequestInfo(hostInfo);
        request.setUserAccount(userAccount);
        request.setSourceServer(source);
        request.setPortNumber(61616);

        try
        {
            SystemCheckResponse response = processor.runNetstatCheck(request);

            Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
        }
        catch (SystemCheckException scx)
        {
            Assert.fail(scx.getMessage());
        }
    }

    @Test
    public void runTelnetCheck()
    {
        Server source = new Server();
        source.setOperHostName("localhost");
        source.setOperIpAddress("127.0.0.1");

        Server target = new Server();
        target.setOperHostName("chibcarray.us.hsbc");

        SystemCheckRequest request = new SystemCheckRequest();
        request.setRequestInfo(hostInfo);
        request.setUserAccount(userAccount);
        request.setSourceServer(source);
        request.setTargetServer(target);
        request.setPortNumber(8080);

        try
        {
            SystemCheckResponse response = processor.runTelnetCheck(request);

            Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
        }
        catch (SystemCheckException scx)
        {
            Assert.fail(scx.getMessage());
        }
    }

    @Test
    public void runRemoteDateCheck()
    {
        Server target = new Server();
        target.setOperHostName("localhost");
        target.setOperIpAddress("127.0.0.1");

        SystemCheckRequest request = new SystemCheckRequest();
        request.setRequestInfo(hostInfo);
        request.setUserAccount(userAccount);
        request.setTargetServer(target);

        try
        {
            SystemCheckResponse response = processor.runRemoteDateCheck(request);

            Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
        }
        catch (SystemCheckException scx)
        {
            Assert.fail(scx.getMessage());
        }
    }

    @After
    public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
        CoreServiceInitializer.shutdown();
    }
}
