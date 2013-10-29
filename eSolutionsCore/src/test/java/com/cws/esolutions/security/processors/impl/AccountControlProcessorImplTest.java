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

import java.util.UUID;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.Assert;
import org.apache.commons.lang.RandomStringUtils;

import com.cws.esolutions.security.enums.Role;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.dto.UserSecurity;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.dao.userauth.enums.LoginType;
import com.cws.esolutions.security.processors.enums.ControlType;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.security.processors.enums.ModificationType;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.security.processors.dto.AccountControlRequest;
import com.cws.esolutions.security.processors.dto.AuthenticationRequest;
import com.cws.esolutions.security.dao.usermgmt.enums.SearchRequestType;
import com.cws.esolutions.security.dao.userauth.enums.AuthenticationType;
import com.cws.esolutions.security.processors.dto.AccountControlResponse;
import com.cws.esolutions.security.processors.dto.AuthenticationResponse;
import com.cws.esolutions.security.processors.exception.AccountControlException;
import com.cws.esolutions.security.processors.interfaces.IAccountControlProcessor;
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
public class AccountControlProcessorImplTest
{
    private UserAccount userAccount = new UserAccount();
    private RequestHostInfo hostInfo = new RequestHostInfo();
    private IAccountControlProcessor processor = new AccountControlProcessorImpl();

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
                userRequest.setHostInfo(hostInfo);

                AuthenticationResponse userResponse = agentAuth.processAgentLogon(userRequest);

                if (userResponse.getRequestStatus() == SecurityRequestStatus.SUCCESS)
                {
                    UserAccount authUser = userResponse.getUserAccount();
                    authUser.setSessionId(account.getSessionId());

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
    public final void testCreateNewUserNoApplication()
    {
        UserAccount newUser = new UserAccount();
        newUser.setDisplayName("Test User");
        newUser.setEmailAddr("test@domain.com");
        newUser.setFailedCount(0);
        newUser.setGivenName("Test");
        newUser.setGuid(UUID.randomUUID().toString());
        newUser.setOlrLocked(false);
        newUser.setOlrSetup(true);
        newUser.setRole(Role.USER);
        newUser.setSurname("User");
        newUser.setSuspended(false);
        newUser.setUsername("testuser");

        AccountControlRequest request = new AccountControlRequest();
        request.setControlType(ControlType.CREATE);
        request.setHostInfo(hostInfo);
        request.setRequestor(userAccount);
        request.setUserAccount(newUser);

        try
        {
            AccountControlResponse response = processor.createNewUser(request);

            Assert.assertEquals(SecurityRequestStatus.SUCCESS, response.getRequestStatus());
        }
        catch (AccountControlException acx)
        {
            Assert.fail(acx.getMessage());
        }
    }

    @Test
    public final void testCreateNewUserWithApplication()
    {
        UserAccount newUser = new UserAccount();
        newUser.setDisplayName("Test User");
        newUser.setEmailAddr("test@domain.com");
        newUser.setFailedCount(0);
        newUser.setGivenName("Test");
        newUser.setGuid(UUID.randomUUID().toString());
        newUser.setOlrLocked(false);
        newUser.setOlrSetup(true);
        newUser.setRole(Role.USER);
        newUser.setSurname("User");
        newUser.setSuspended(false);
        newUser.setUsername("testuser");

        AccountControlRequest request = new AccountControlRequest();
        request.setControlType(ControlType.CREATE);
        request.setHostInfo(hostInfo);
        request.setRequestor(userAccount);
        request.setUserAccount(newUser);
        request.setApplicationName("esolutions");

        try
        {
            AccountControlResponse response = processor.createNewUser(request);

            Assert.assertEquals(SecurityRequestStatus.SUCCESS, response.getRequestStatus());
        }
        catch (AccountControlException acx)
        {
            Assert.fail(acx.getMessage());
        }
    }

    @Test
    public final void testModifyEmail()
    {
        UserAccount modUser = userAccount;
        modUser.setEmailAddr("kmhuntly@caspersbox.com");

        UserSecurity userSecurity = new UserSecurity();
        userSecurity.setPassword("Ariana16*");

        AccountControlRequest request = new AccountControlRequest();
        request.setAlgorithm("MD5");
        request.setApplicationName("esolutions");
        request.setHostInfo(hostInfo);
        request.setIsLogonRequest(false);
        request.setModType(ModificationType.SECINFO);
        request.setRequestor(userAccount);
        request.setUserAccount(modUser);
        request.setControlType(ControlType.MODIFY);
        request.setModType(ModificationType.EMAIL);
        request.setUserSecurity(userSecurity);

        try
        {
            // then permsuspend
            AccountControlResponse response = processor.changeUserEmail(request);

            Assert.assertEquals(SecurityRequestStatus.SUCCESS, response.getRequestStatus());
        }
        catch (AccountControlException acx)
        {
            Assert.fail(acx.getMessage());
        }
    }

    @Test
    public final void testSelfChangeUserPassword()
    {
        UserAccount userAccount = new UserAccount();
        userAccount.setStatus(LoginStatus.SUCCESS);
        userAccount.setGuid("74d9729b-7fb2-4fef-874b-c9ee5d7a5a95");
        userAccount.setUsername("khuntly");
        userAccount.setSessionId(RandomStringUtils.randomAlphanumeric(32));
        userAccount.setGivenName("Kevin");
        userAccount.setSurname("Huntly");
        userAccount.setDisplayName("Kevin Huntly");
        userAccount.setEmailAddr("kmhuntly@gmail.com");
        userAccount.setFailedCount(0);
        userAccount.setOlrLocked(false);
        userAccount.setOlrSetup(false);
        userAccount.setSuspended(false);
        userAccount.setTcAccepted(true);
        userAccount.setRole(Role.SITEADMIN);

        UserSecurity reqSecurity = new UserSecurity();
        reqSecurity.setPassword("Hailey27*");
        reqSecurity.setNewPassword(RandomStringUtils.randomAlphanumeric(32));

        AccountControlRequest request = new AccountControlRequest();
        request.setAlgorithm("SHA-512");
        request.setApplicationName("esolutions");
        request.setHostInfo(hostInfo);
        request.setIsLogonRequest(false);
        request.setModType(ModificationType.SECINFO);
        request.setRequestor(userAccount);
        request.setUserAccount(userAccount);
        request.setControlType(ControlType.MODIFY);
        request.setModType(ModificationType.PASSWORD);
        request.setUserSecurity(reqSecurity);

        try
        {
            AccountControlResponse response = processor.changeUserPassword(request);

            Assert.assertEquals(SecurityRequestStatus.SUCCESS, response.getRequestStatus());
        }
        catch (AccountControlException acx)
        {
            Assert.fail(acx.getMessage());
        }
    }

    @Test
    public final void testChangeUserPassword()
    {
        UserAccount newUser = new UserAccount();
        newUser.setDisplayName("Test User");
        newUser.setEmailAddr("test@domain.com");
        newUser.setFailedCount(0);
        newUser.setGivenName("Test");
        newUser.setGuid("6f3a5f8d-732c-4859-b9e0-3968f080ab76");
        newUser.setOlrLocked(false);
        newUser.setOlrSetup(true);
        newUser.setRole(Role.USER);
        newUser.setSurname("User");
        newUser.setSuspended(false);
        newUser.setUsername("testuser");

        AccountControlRequest request = new AccountControlRequest();
        request.setAlgorithm("SHA-512");
        request.setApplicationName("esolutions");
        request.setHostInfo(hostInfo);
        request.setIsLogonRequest(false);
        request.setModType(ModificationType.SECINFO);
        request.setRequestor(userAccount);
        request.setUserAccount(newUser);
        request.setControlType(ControlType.MODIFY);
        request.setModType(ModificationType.PASSWORD);

        // change the email
        UserSecurity reqSecurity = new UserSecurity();
        reqSecurity.setPassword(RandomStringUtils.randomAlphanumeric(32));

        try
        {
            AccountControlResponse response = processor.changeUserPassword(request);

            Assert.assertEquals(SecurityRequestStatus.SUCCESS, response.getRequestStatus());
        }
        catch (AccountControlException acx)
        {
            Assert.fail(acx.getMessage());
        }
    }

    @Test
    public final void testSelfChangePassword()
    {
        // change the email
        UserSecurity reqSecurity = new UserSecurity();
        reqSecurity.setNewPassword("Ariana17*");
        reqSecurity.setPassword("ariana16");

        AccountControlRequest request = new AccountControlRequest();
        request.setApplicationName("esolutions");
        request.setHostInfo(hostInfo);
        request.setIsLogonRequest(false);
        request.setModType(ModificationType.PASSWORD);
        request.setRequestor(userAccount);
        request.setUserAccount(userAccount);
        request.setUserSecurity(reqSecurity);
        request.setControlType(ControlType.RESETPASS);
        request.setModType(ModificationType.PASSWORD);

        try
        {
            AccountControlResponse response = processor.changeUserPassword(request);

            Assert.assertEquals(SecurityRequestStatus.SUCCESS, response.getRequestStatus());
        }
        catch (AccountControlException acx)
        {
            Assert.fail(acx.getMessage());
        }
    }

    @Test
    public final void testChangePasswordWithReset()
    {
        // change the email
        UserAccount account = new UserAccount();
        account.setUsername("khuntly");
        account.setGuid("74d9729b-7fb2-4fef-874b-c9ee5d7a5a95");
        account.setSessionId(RandomStringUtils.randomAlphanumeric(32));
        account.setOlrSetup(false);
        account.setOlrLocked(false);
        account.setSuspended(false);
        account.setStatus(LoginStatus.SUCCESS);
        account.setTcAccepted(true);

        UserSecurity reqSecurity = new UserSecurity();
        reqSecurity.setNewPassword("Ariana17*");

        AccountControlRequest request = new AccountControlRequest();
        request.setApplicationName("esolutions");
        request.setHostInfo(hostInfo);
        request.setIsLogonRequest(false);
        request.setModType(ModificationType.PASSWORD);
        request.setRequestor(account);
        request.setUserAccount(account);
        request.setUserSecurity(reqSecurity);
        request.setControlType(ControlType.RESETPASS);
        request.setModType(ModificationType.PASSWORD);

        try
        {
            AccountControlResponse response = processor.changeUserPassword(request);

            Assert.assertEquals(SecurityRequestStatus.SUCCESS, response.getRequestStatus());
        }
        catch (AccountControlException acx)
        {
            Assert.fail(acx.getMessage());
        }
    }

    @Test
    public final void testSearchAccounts()
    {
        UserAccount searchUser = new UserAccount();
        searchUser.setUsername("khuntly");

        AccountControlRequest request = new AccountControlRequest();
        request.setApplicationName("esolutions");
        request.setHostInfo(hostInfo);
        request.setIsLogonRequest(false);
        request.setRequestor(userAccount);
        request.setUserAccount(searchUser);
        request.setSearchType(SearchRequestType.USERNAME);

        try
        {
            AccountControlResponse response = processor.searchAccounts(request);

            Assert.assertEquals(SecurityRequestStatus.SUCCESS, response.getRequestStatus());
        }
        catch (AccountControlException acx)
        {
            Assert.fail(acx.getMessage());
        }
    }

    @Test
    public final void testModifyUserSuspension()
    {
        UserAccount suspendAccount = new UserAccount();
        suspendAccount.setUsername("khuntly");
        suspendAccount.setGuid("74d9729b-7fb2-4fef-874b-c9ee5d7a5a95");
        suspendAccount.setSuspended(false);

        AccountControlRequest request = new AccountControlRequest();
        request.setHostInfo(hostInfo);
        request.setUserAccount(suspendAccount);
        request.setApplicationName("esolutions");
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setControlType(ControlType.SUSPEND);
        request.setModType(ModificationType.NONE);
        request.setRequestor(userAccount);
        request.setIsLogonRequest(false);
        request.setServiceId("AEB46994-57B4-4E92-90AA-A4046F60B830");

        try
        {
            AccountControlResponse response = processor.modifyUserSuspension(request);

            Assert.assertEquals(SecurityRequestStatus.SUCCESS, response.getRequestStatus());
        }
        catch (AccountControlException acx)
        {
            Assert.fail(acx.getMessage());
        }
    }

    @Test
    public final void testModifyUserLockCount()
    {
        UserAccount account = new UserAccount();
        account.setUsername("khuntly");
        account.setGuid("74d9729b-7fb2-4fef-874b-c9ee5d7a5a95");
        account.setFailedCount(50);

        AccountControlRequest request = new AccountControlRequest();
        request.setHostInfo(hostInfo);
        request.setUserAccount(account);
        request.setApplicationName("esolutions");
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setControlType(ControlType.SUSPEND);
        request.setModType(ModificationType.NONE);
        request.setRequestor(userAccount);
        request.setIsLogonRequest(false);
        request.setServiceId("AEB46994-57B4-4E92-90AA-A4046F60B830");

        try
        {
            AccountControlResponse response = processor.modifyUserLockout(request);

            Assert.assertEquals(SecurityRequestStatus.SUCCESS, response.getRequestStatus());
        }
        catch (AccountControlException acx)
        {
            Assert.fail(acx.getMessage());
        }
    }

    @Test
    public final void testChangeUserSecurity()
    {
        UserSecurity userSecurity = new UserSecurity();
        userSecurity.setSecQuestionOne("What is your least favourite colour ?");
        userSecurity.setSecQuestionTwo("Who was your childhood best friend ?");
        userSecurity.setSecAnswerOne("myanswer");
        userSecurity.setSecAnswerTwo("anotheranswer");
        userSecurity.setPassword("Ariana21*");

        AccountControlRequest request = new AccountControlRequest();
        request.setRequestor(userAccount);
        request.setApplicationName("esolutions");
        request.setControlType(ControlType.MODIFY);
        request.setHostInfo(hostInfo);
        request.setIsLogonRequest(false);
        request.setModType(ModificationType.SECINFO);
        request.setUserAccount(userAccount);
        request.setRequestor(userAccount);
        request.setUserSecurity(userSecurity);

        try
        {
            AccountControlResponse response = processor.changeUserSecurity(request);

            Assert.assertEquals(SecurityRequestStatus.SUCCESS, response.getRequestStatus());
        }
        catch (AccountControlException acx)
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
