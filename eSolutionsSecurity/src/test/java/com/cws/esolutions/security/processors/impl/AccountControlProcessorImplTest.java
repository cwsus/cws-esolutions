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
package com.cws.esolutions.security.processors.impl;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.processors.impl
 * File: AccountControlProcessorImplTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.UUID;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.Assert;
import org.apache.commons.lang.RandomStringUtils;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.processors.enums.ControlType;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.security.processors.enums.ModificationType;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.security.processors.dto.AccountControlRequest;
import com.cws.esolutions.security.dao.usermgmt.enums.SearchRequestType;
import com.cws.esolutions.security.processors.dto.AccountControlResponse;
import com.cws.esolutions.security.processors.exception.AccountControlException;
import com.cws.esolutions.security.processors.interfaces.IAccountControlProcessor;

public class AccountControlProcessorImplTest
{
    private static UserAccount userAccount = new UserAccount();
    private static RequestHostInfo hostInfo = new RequestHostInfo();
    private static final IAccountControlProcessor processor = new AccountControlProcessorImpl();

    @Before
    public void setUp()
    {
        try
        {
            hostInfo.setHostAddress("junit");
            hostInfo.setHostName("junit");

            userAccount.setStatus(LoginStatus.SUCCESS);
            userAccount.setGuid("74d9729b-7fb2-4fef-874b-c9ee5d7a5a95");
            userAccount.setUsername("khuntly");

            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/logging/logging.xml");
        }
        catch (Exception ex)
        {
            Assert.fail(ex.getMessage());

            System.exit(-1);
        }
    }

    @Test
    public void testCreateNewUserNoApplication()
    {
        UserAccount newUser = new UserAccount();
        newUser.setDisplayName("Test User");
        newUser.setEmailAddr("test@domain.com");
        newUser.setFailedCount(0);
        newUser.setGivenName("Test");
        newUser.setOlrLocked(false);
        newUser.setOlrSetup(true);
        newUser.setSurname("User");
        newUser.setSuspended(false);
        newUser.setUsername(RandomStringUtils.randomAlphabetic(8));

        AccountControlRequest request = new AccountControlRequest();
        request.setControlType(ControlType.CREATE);
        request.setHostInfo(hostInfo);
        request.setRequestor(userAccount);
        request.setUserAccount(newUser);
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
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
    public void testCreateNewUserWithApplication()
    {
        UserAccount newUser = new UserAccount();
        newUser.setDisplayName("Test User");
        newUser.setEmailAddr("test@domain.com");
        newUser.setFailedCount(0);
        newUser.setGivenName("Test");
        newUser.setGuid(UUID.randomUUID().toString());
        newUser.setOlrLocked(false);
        newUser.setOlrSetup(true);
        newUser.setSurname("User");
        newUser.setSuspended(false);
        newUser.setUsername(RandomStringUtils.randomAlphabetic(8));

        AccountControlRequest request = new AccountControlRequest();
        request.setControlType(ControlType.CREATE);
        request.setHostInfo(hostInfo);
        request.setRequestor(userAccount);
        request.setUserAccount(newUser);
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
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
    public void testSearchAccounts()
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
    public void testModifyUserSuspension()
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
    public void testModifyUserLockCount()
    {
        UserAccount account = new UserAccount();
        account.setUsername("khuntly");
        account.setGuid("74d9729b-7fb2-4fef-874b-c9ee5d7a5a95");
        account.setFailedCount(0);

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

    @After
    public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
