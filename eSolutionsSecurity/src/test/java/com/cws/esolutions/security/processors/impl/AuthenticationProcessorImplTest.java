/*
 * Copyright (c) 2009 - 2020 CaspersBox Web Services
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
package com.cws.esolutions.security.processors.impl;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.processors.impl
 * File: AuthenticationProcessorImplTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly   11/23/2008 22:39:20             Created.
 */
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.Assert;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.security.processors.dto.AuthenticationData;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.security.processors.dto.AuthenticationRequest;
import com.cws.esolutions.security.processors.dto.AuthenticationResponse;
import com.cws.esolutions.security.processors.exception.AuthenticationException;
import com.cws.esolutions.security.processors.interfaces.IAuthenticationProcessor;

public class AuthenticationProcessorImplTest
{
    private static RequestHostInfo hostInfo = null;

    private static final IAuthenticationProcessor agentAuth = new AuthenticationProcessorImpl();

    @Before public void setUp()
    {
        try
        {
            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/logging/logging.xml", true);

            hostInfo = new RequestHostInfo();
            hostInfo.setHostAddress("junit");
            hostInfo.setHostName("junit");
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            System.exit(1);
        }
    }

    @Test public void processAgentLogon()
    {
        UserAccount account = new UserAccount();
        account.setUsername("junit");

        AuthenticationData userSecurity = new AuthenticationData();
        userSecurity.setPassword("junit");

        AuthenticationRequest request = new AuthenticationRequest();
        request.setApplicationName("esolutions");
        request.setUserAccount(account);
        request.setUserSecurity(userSecurity);
        request.setHostInfo(hostInfo);

        try
        {
            AuthenticationResponse response = agentAuth.processAgentLogon(request);

            Assert.assertEquals(response.getRequestStatus(), SecurityRequestStatus.SUCCESS);
        }
        catch (AuthenticationException ax)
        {
            Assert.fail(ax.getMessage());
        }
    }

    @Test public void processOtpLogon()
    {
        UserAccount account = new UserAccount();
        account.setUsername("junit");
        account.setGuid("f42fb0ba-4d1e-1126-986f-800cd2650000");

        AuthenticationData userSecurity = new AuthenticationData();
        userSecurity.setPassword("junit");
        userSecurity.setSecret("RHmJrNj6KISffPbnksYZDuKr9pookp0oxThyHa0rqkrID+tX8PTVcTl6D/MoA0FCp2r7lv+HaHrRrR/w/FaGSA==");
        userSecurity.setOtpValue(790269);

        AuthenticationRequest request = new AuthenticationRequest();
        request.setApplicationName("esolutions");
        request.setUserAccount(account);
        request.setUserSecurity(userSecurity);
        request.setHostInfo(hostInfo);

        try
        {
            AuthenticationResponse response = agentAuth.processOtpLogon(request);

            Assert.assertEquals(SecurityRequestStatus.SUCCESS, response.getRequestStatus());
        }
        catch (AuthenticationException ax)
        {
            ax.printStackTrace();
            Assert.fail(ax.getMessage());
        }
    }

    @After public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
