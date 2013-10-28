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
package com.cws.esolutions.security.keymgmt.impl;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.Assert;
import org.apache.commons.lang.RandomStringUtils;

import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.dto.UserSecurity;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.security.dao.userauth.enums.LoginType;
import com.cws.esolutions.security.keymgmt.interfaces.KeyManager;
import com.cws.esolutions.security.keymgmt.dto.KeyManagementRequest;
import com.cws.esolutions.security.keymgmt.dto.KeyManagementResponse;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.security.processors.dto.AuthenticationRequest;
import com.cws.esolutions.security.dao.userauth.enums.AuthenticationType;
import com.cws.esolutions.security.processors.dto.AuthenticationResponse;
import com.cws.esolutions.security.keymgmt.exception.KeyManagementException;
import com.cws.esolutions.security.processors.impl.AuthenticationProcessorImpl;
import com.cws.esolutions.security.processors.interfaces.IAuthenticationProcessor;
/**
 * eSolutionsCore
 * com.cws.esolutions.security.keymgmt.impl
 * SQLKeyManagerTest.java
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
 * 35033355 @ Apr 5, 2013 2:21:01 PM
 *     Created.
 */
public class SQLKeyManagerTest
{
    private UserAccount userAccount = new UserAccount();
    private RequestHostInfo hostInfo = new RequestHostInfo();

    @Before
    public final void setUp()
    {
        try
        {
            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/config/SecurityLogging.xml");

            IAuthenticationProcessor agentAuth = new AuthenticationProcessorImpl();
            hostInfo.setHostAddress("junit");
            hostInfo.setHostName("junit");

            UserAccount account = new UserAccount();
            account.setUsername("khuntly");
            account.setSessionId(RandomStringUtils.randomAlphanumeric(32));

            try
            {
                AuthenticationRequest userRequest = new AuthenticationRequest();
                userRequest.setApplicationName("eSolutions");
                userRequest.setAuthType(AuthenticationType.LOGIN);
                userRequest.setLoginType(LoginType.USERNAME);
                userRequest.setUserAccount(account);
                userRequest.setApplicationId("B760E92F-827A-42E7-9E8D-64334657BA83");

                AuthenticationResponse userResponse = agentAuth.processAgentLogon(userRequest);

                if (userResponse.getRequestStatus() == SecurityRequestStatus.SUCCESS)
                {
                    UserAccount authUser = userResponse.getUserAccount();

                    if (authUser.getStatus() == LoginStatus.SUCCESS)
                    {
                        UserSecurity userSecurity = new UserSecurity();
                        userSecurity.setPassword("Ariana16*");

                        AuthenticationRequest passRequest = new AuthenticationRequest();
                        passRequest.setApplicationName("eSolutions");
                        passRequest.setAuthType(AuthenticationType.LOGIN);
                        passRequest.setLoginType(LoginType.PASSWORD);
                        passRequest.setUserAccount(authUser);
                        passRequest.setUserSecurity(userSecurity);
                        passRequest.setApplicationId("B760E92F-827A-42E7-9E8D-64334657BA83");

                        AuthenticationResponse passResponse = agentAuth.processAgentLogon(passRequest);

                        if (passResponse.getRequestStatus() == SecurityRequestStatus.SUCCESS)
                        {
                            userAccount = passResponse.getUserAccount();
                            userAccount.setSessionId(RandomStringUtils.randomAlphanumeric(32));

                            Assert.assertEquals(LoginStatus.SUCCESS, userAccount.getStatus());
                        }
                        else
                        {
                            Assert.fail("Account login failed");
                        }
                    }
                    else
                    {
                        Assert.fail("Account login failed");
                    }
                }
                else
                {
                    Assert.fail("Account login failed");
                }
            }
            catch (Exception e)
            {
                Assert.fail(e.getMessage());
            }
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            System.exit(1);
        }
    }

    @Test
    public final void testCreateKeys()
    {
        KeyManager keyManager = new SQLKeyManager();

        KeyManagementRequest request = new KeyManagementRequest();
        request.setKeyAlgorithm("DSA");
        request.setKeySize(1024);
        request.setGuid(userAccount.getGuid());

        try
        {
            KeyManagementResponse response = keyManager.createKeys(request);

            Assert.assertEquals(SecurityRequestStatus.SUCCESS, response.getRequestStatus());
        }
        catch (KeyManagementException kmx)
        {
            Assert.fail(kmx.getMessage());
        }
    }

    @Test
    public final void testReturnKeys()
    {
        KeyManager keyManager = new SQLKeyManager();

        KeyManagementRequest request = new KeyManagementRequest();
        request.setKeyAlgorithm("RSA");
        request.setGuid(userAccount.getGuid());

        try
        {
            KeyManagementResponse response = keyManager.returnKeys(request);

            Assert.assertEquals(SecurityRequestStatus.SUCCESS, response.getRequestStatus());
        }
        catch (KeyManagementException kmx)
        {
            Assert.fail(kmx.getMessage());
        }
    }

    @Test
    public final void testDeleteKeys()
    {
        KeyManager keyManager = new SQLKeyManager();

        KeyManagementRequest request = new KeyManagementRequest();
        request.setGuid(userAccount.getGuid());

        try
        {
            KeyManagementResponse response = keyManager.removeKeys(request);

            Assert.assertEquals(SecurityRequestStatus.SUCCESS, response.getRequestStatus());
        }
        catch (KeyManagementException kmx)
        {
            Assert.fail(kmx.getMessage());
        }
    }

    @After
    public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
