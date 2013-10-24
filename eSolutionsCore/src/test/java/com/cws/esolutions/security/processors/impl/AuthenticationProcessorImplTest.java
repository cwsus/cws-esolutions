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
package com.cws.esolutions.security.processors.impl;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.Assert;
import org.apache.commons.lang.RandomStringUtils;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.dto.UserSecurity;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.dao.userauth.enums.LoginType;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.security.processors.dto.AuthenticationRequest;
import com.cws.esolutions.security.dao.userauth.enums.AuthenticationType;
import com.cws.esolutions.security.processors.dto.AuthenticationResponse;
import com.cws.esolutions.security.processors.exception.AuthenticationException;
import com.cws.esolutions.security.processors.interfaces.IAuthenticationProcessor;
/**
 * SecurityService
 * com.cws.esolutions.security.processors.impl
 * AgentAuthenticationProcessorImplTest.java
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
 * kh05451 @ Oct 22, 2012 12:55:33 PM
 *     Created.
 */
public class AuthenticationProcessorImplTest
{
    private RequestHostInfo hostInfo = null;

    private static final IAuthenticationProcessor agentAuth = new AuthenticationProcessorImpl();

    @Before
    public final void setUp()
    {
        try
        {
            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/config/SecurityLogging.xml");

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

    @Test
    public void testUsernameAuthentication()
    {
        UserAccount account = new UserAccount();
        account.setUsername("khuntly");
        account.setSessionId(RandomStringUtils.randomAlphanumeric(32));

        AuthenticationRequest request = new AuthenticationRequest();
        request.setApplicationName("eSolutions");
        request.setAuthType(AuthenticationType.LOGIN);
        request.setLoginType(LoginType.USERNAME);
        request.setUserAccount(account);
        request.setApplicationId("74d9729b-7fb2-4fef-874b-c9ee5d7a5a95");
        request.setApplicationName("esolutions");
        request.setHostInfo(hostInfo);

        try
        {
            AuthenticationResponse response = agentAuth.processAgentLogon(request);

            Assert.assertEquals(SecurityRequestStatus.SUCCESS, response.getRequestStatus());
        }
        catch (AuthenticationException ax)
        {
            Assert.fail(ax.getMessage());
        }
    }

    @Test
    public void testPasswordAuthentication()
    {
        UserAccount account = new UserAccount();
        account.setGuid("74d9729b-7fb2-4fef-874b-c9ee5d7a5a95");
        account.setUsername("khuntly");
        account.setSessionId(RandomStringUtils.randomAlphanumeric(32));

        UserSecurity userSecurity = new UserSecurity();
        userSecurity.setPassword("Ariana16*");

        AuthenticationRequest request = new AuthenticationRequest();
        request.setApplicationName("esolutions");
        request.setAuthType(AuthenticationType.LOGIN);
        request.setLoginType(LoginType.PASSWORD);
        request.setUserAccount(account);
        request.setUserSecurity(userSecurity);
        request.setHostInfo(hostInfo);

        try
        {
            AuthenticationResponse response = agentAuth.processAgentLogon(request);

            Assert.assertEquals(SecurityRequestStatus.SUCCESS, response.getRequestStatus());
        }
        catch (AuthenticationException ax)
        {
            Assert.fail(ax.getMessage());
        }
    }

    @Test
    public void testCombinedAuthentication()
    {
        UserAccount account = new UserAccount();
        account.setGuid("74d9729b-7fb2-4fef-874b-c9ee5d7a5a95");
        account.setUsername("khuntly");
        account.setSessionId(RandomStringUtils.randomAlphanumeric(32));

        UserSecurity userSecurity = new UserSecurity();
        userSecurity.setPassword("Ariana16*");

        AuthenticationRequest request = new AuthenticationRequest();
        request.setApplicationName("esolutions");
        request.setAuthType(AuthenticationType.LOGIN);
        request.setLoginType(LoginType.COMBINED);
        request.setUserAccount(account);
        request.setUserSecurity(userSecurity);
        request.setHostInfo(hostInfo);

        try
        {
            AuthenticationResponse response = agentAuth.processAgentLogon(request);

            Assert.assertEquals(SecurityRequestStatus.SUCCESS, response.getRequestStatus());
        }
        catch (AuthenticationException ax)
        {
            Assert.fail(ax.getMessage());
        }
    }

    @Test
    public void testFailedResponse()
    {
        UserAccount account = new UserAccount();
        account.setUsername("khuntly");
        account.setSessionId(RandomStringUtils.randomAlphanumeric(32));

        UserSecurity userSecurity = new UserSecurity();
        userSecurity.setPassword("Ariana18*");

        AuthenticationRequest request = new AuthenticationRequest();
        request.setApplicationName("esolutions");
        request.setAuthType(AuthenticationType.LOGIN);
        request.setLoginType(LoginType.COMBINED);
        request.setUserAccount(account);
        request.setUserSecurity(userSecurity);
        request.setHostInfo(hostInfo);

        try
        {
            AuthenticationResponse response = agentAuth.processAgentLogon(request);

            Assert.assertEquals(SecurityRequestStatus.FAILURE, response.getRequestStatus());
        }
        catch (AuthenticationException ax)
        {
            Assert.fail(ax.getMessage());
        }
    }

    @Test
    public void testObtainUserSecurityConfig()
    {
        UserAccount account = new UserAccount();
        account.setUsername("khuntly");
        account.setGuid("74d9729b-7fb2-4fef-874b-c9ee5d7a5a95");
        account.setSessionId(RandomStringUtils.randomAlphanumeric(32));

        AuthenticationRequest request = new AuthenticationRequest();
        request.setApplicationName("eSolutions");
        request.setAuthType(AuthenticationType.LOGIN);
        request.setLoginType(LoginType.USERNAME);
        request.setUserAccount(account);
        request.setApplicationId("74d9729b-7fb2-4fef-874b-c9ee5d7a5a95");
        request.setApplicationName("esolutions");
        request.setHostInfo(hostInfo);

        try
        {
            AuthenticationResponse response = agentAuth.obtainUserSecurityConfig(request);

            Assert.assertEquals(SecurityRequestStatus.SUCCESS, response.getRequestStatus());
        }
        catch (AuthenticationException ax)
        {
            Assert.fail(ax.getMessage());
        }
    }

    @Test
    public void testVerifyUserSecurityConfig()
    {
        UserAccount account = new UserAccount();
        account.setUsername("khuntly");
        account.setGuid("74d9729b-7fb2-4fef-874b-c9ee5d7a5a95");
        account.setSessionId(RandomStringUtils.randomAlphanumeric(32));

        UserSecurity userSecurity = new UserSecurity();
        userSecurity.setSecAnswerOne("answerone");
        userSecurity.setSecAnswerTwo("answertwo");

        AuthenticationRequest request = new AuthenticationRequest();
        request.setApplicationName("eSolutions");
        request.setAuthType(AuthenticationType.LOGIN);
        request.setLoginType(LoginType.USERNAME);
        request.setUserAccount(account);
        request.setUserSecurity(userSecurity);
        request.setApplicationId("74d9729b-7fb2-4fef-874b-c9ee5d7a5a95");
        request.setApplicationName("esolutions");
        request.setHostInfo(hostInfo);

        try
        {
            AuthenticationResponse response = agentAuth.verifyUserSecurityConfig(request);

            Assert.assertEquals(SecurityRequestStatus.SUCCESS, response.getRequestStatus());
        }
        catch (AuthenticationException ax)
        {
            Assert.fail(ax.getMessage());
        }
    }

    @After
    public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
