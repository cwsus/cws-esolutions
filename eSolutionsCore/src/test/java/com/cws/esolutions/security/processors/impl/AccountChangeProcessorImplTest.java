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
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.security.processors.enums.ModificationType;
import com.cws.esolutions.security.processors.dto.AccountChangeRequest;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.security.processors.dto.AccountChangeResponse;
import com.cws.esolutions.security.processors.dto.AuthenticationRequest;
import com.cws.esolutions.security.dao.userauth.enums.AuthenticationType;
import com.cws.esolutions.security.processors.dto.AuthenticationResponse;
import com.cws.esolutions.security.processors.exception.AccountChangeException;
import com.cws.esolutions.security.processors.interfaces.IAccountChangeProcessor;
import com.cws.esolutions.security.processors.interfaces.IAuthenticationProcessor;
/**
 * SecurityService
 * com.cws.esolutions.security.processors.impl
 * AccountControlProcessorImplTest.java
 *
 * $Id: AccountControlProcessorImplTest.java 2276 2013-01-03 16:32:52Z kmhuntly@gmail.com $
 * $Author: $
 * $Date: $
 * $Revision: $
 * @author kmhuntly@gmail.com
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Nov 12, 2012 8:51:00 AM
 *     Created.
 */
public final class AccountChangeProcessorImplTest
{
     private UserAccount userAccount = new UserAccount();
     private RequestHostInfo hostInfo = new RequestHostInfo();
     private IAccountChangeProcessor processor = new AccountChangeProcessorImpl();

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
            hostInfo.setSessionId(RandomStringUtils.randomAlphanumeric(32));

            try
            {
                AuthenticationRequest userRequest = new AuthenticationRequest();
                userRequest.setApplicationName("eSolutions");
                userRequest.setAuthType(AuthenticationType.LOGIN);
                userRequest.setLoginType(LoginType.USERNAME);
                userRequest.setUserAccount(account);
                userRequest.setApplicationId("B760E92F-827A-42E7-9E8D-64334657BA83");
                userRequest.setHostInfo(hostInfo);

                AuthenticationResponse userResponse = agentAuth.processAgentLogon(userRequest);

                if (userResponse.getRequestStatus() == SecurityRequestStatus.SUCCESS)
                {
                    UserAccount authUser = userResponse.getUserAccount();
                    hostInfo.setSessionId(hostInfo.getSessionId());

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
                        passRequest.setHostInfo(hostInfo);

                        AuthenticationResponse passResponse = agentAuth.processAgentLogon(passRequest);

                        if (passResponse.getRequestStatus() == SecurityRequestStatus.SUCCESS)
                        {
                            userAccount = passResponse.getUserAccount();
                            hostInfo.setSessionId(RandomStringUtils.randomAlphanumeric(32));

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

                System.exit(1);
            }
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());

            System.exit(1);
        }
    }

    @Test
    public final void testChangeUserEmail()
    {
        UserAccount account = userAccount;
        account.setEmailAddr("test@test.com");

        UserSecurity userSecurity = new UserSecurity();
        userSecurity.setPassword("Ariana21*");

        AccountChangeRequest request = new AccountChangeRequest();
        request.setHostInfo(hostInfo);
        request.setUserAccount(account);
        request.setUserSecurity(userSecurity);
        request.setApplicationName("eSolutions");
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setModType(ModificationType.EMAIL);
        request.setRequestor(userAccount);
        request.setIsReset(false);
        request.setIsLogonRequest(false);

        try
        {
            AccountChangeResponse response = processor.changeUserEmail(request);

            Assert.assertEquals(SecurityRequestStatus.SUCCESS, response.getRequestStatus());
        }
        catch (AccountChangeException acx)
        {
            Assert.fail(acx.getMessage());
        }
    }

    @Test
    public final void testChangeUserContactChangeTelephone()
    {
        UserAccount account = userAccount;
        account.setTelephoneNumber("716-341-1697");

        UserSecurity userSecurity = new UserSecurity();
        userSecurity.setPassword("Ariana21*");

        AccountChangeRequest request = new AccountChangeRequest();
        request.setHostInfo(hostInfo);
        request.setUserAccount(account);
        request.setUserSecurity(userSecurity);
        request.setApplicationName("eSolutions");
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setModType(ModificationType.EMAIL);
        request.setRequestor(userAccount);
        request.setIsReset(false);
        request.setIsLogonRequest(false);

        try
        {
            AccountChangeResponse response = processor.changeUserContact(request);

            Assert.assertEquals(SecurityRequestStatus.SUCCESS, response.getRequestStatus());
        }
        catch (AccountChangeException acx)
        {
            Assert.fail(acx.getMessage());
        }
    }

    @Test
    public final void testChangeUserContactChangePager()
    {
        UserAccount account = userAccount;
        account.setPagerNumber("716-341-1697");

        UserSecurity userSecurity = new UserSecurity();
        userSecurity.setPassword("Ariana21*");

        AccountChangeRequest request = new AccountChangeRequest();
        request.setHostInfo(hostInfo);
        request.setUserAccount(account);
        request.setUserSecurity(userSecurity);
        request.setApplicationName("eSolutions");
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setModType(ModificationType.EMAIL);
        request.setRequestor(userAccount);
        request.setIsReset(false);
        request.setIsLogonRequest(false);

        try
        {
            AccountChangeResponse response = processor.changeUserContact(request);

            Assert.assertEquals(SecurityRequestStatus.SUCCESS, response.getRequestStatus());
        }
        catch (AccountChangeException acx)
        {
            Assert.fail(acx.getMessage());
        }
    }

    @Test
    public final void testChangeUserPassword()
    {
        UserSecurity userSecurity = new UserSecurity();
        userSecurity.setPassword("Ariana21*");
        userSecurity.setNewPassword("Ariana16*");

        AccountChangeRequest request = new AccountChangeRequest();
        request.setHostInfo(hostInfo);
        request.setUserAccount(userAccount);
        request.setUserSecurity(userSecurity);
        request.setApplicationName("eSolutions");
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setModType(ModificationType.EMAIL);
        request.setRequestor(userAccount);
        request.setIsReset(false);
        request.setIsLogonRequest(false);

        try
        {
            AccountChangeResponse response = processor.changeUserPassword(request);

            Assert.assertEquals(SecurityRequestStatus.SUCCESS, response.getRequestStatus());
        }
        catch (AccountChangeException acx)
        {
            Assert.fail(acx.getMessage());
        }
    }

    @Test
    public final void testChangeUserSecurity()
    {
        UserSecurity userSecurity = new UserSecurity();
        userSecurity.setPassword("Ariana16*");
        userSecurity.setSecQuestionOne("What is your favourite car ?");
        userSecurity.setSecQuestionTwo("What is your least favourite colour ?");
        userSecurity.setSecAnswerOne("answerone");
        userSecurity.setSecAnswerTwo("answertwo");

        AccountChangeRequest request = new AccountChangeRequest();
        request.setHostInfo(hostInfo);
        request.setUserAccount(userAccount);
        request.setUserSecurity(userSecurity);
        request.setApplicationName("eSolutions");
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setModType(ModificationType.SECINFO);
        request.setRequestor(userAccount);
        request.setIsReset(false);
        request.setIsLogonRequest(false);

        try
        {
            AccountChangeResponse response = processor.changeUserSecurity(request);

            Assert.assertEquals(SecurityRequestStatus.SUCCESS, response.getRequestStatus());
        }
        catch (AccountChangeException acx)
        {
            Assert.fail(acx.getMessage());
        }
    }

    @Test
    public final void testChangeUserKeys()
    {
        UserSecurity userSecurity = new UserSecurity();
        userSecurity.setPassword("Ariana21*");

        AccountChangeRequest request = new AccountChangeRequest();
        request.setHostInfo(hostInfo);
        request.setUserAccount(userAccount);
        request.setUserSecurity(userSecurity);
        request.setApplicationName("eSolutions");
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setModType(ModificationType.EMAIL);
        request.setRequestor(userAccount);
        request.setIsReset(false);
        request.setIsLogonRequest(false);

        try
        {
            AccountChangeResponse response = processor.changeUserKeys(request);

            Assert.assertEquals(SecurityRequestStatus.SUCCESS, response.getRequestStatus());
        }
        catch (AccountChangeException acx)
        {
            Assert.fail(acx.getMessage());
        }
    }

    @After
    public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
