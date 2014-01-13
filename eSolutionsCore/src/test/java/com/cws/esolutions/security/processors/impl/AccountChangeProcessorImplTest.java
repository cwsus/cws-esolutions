/*
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
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.security.processors.impl
 * File: AccountChangeProcessorImplTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.Assert;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.core.listeners.CoreServiceInitializer;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.security.processors.enums.ModificationType;
import com.cws.esolutions.security.processors.dto.AuthenticationData;
import com.cws.esolutions.security.processors.dto.AccountChangeRequest;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.security.processors.dto.AccountChangeResponse;
import com.cws.esolutions.security.processors.exception.AccountChangeException;
import com.cws.esolutions.security.processors.interfaces.IAccountChangeProcessor;

public final class AccountChangeProcessorImplTest
{
     private static UserAccount userAccount = new UserAccount();
     private static RequestHostInfo hostInfo = new RequestHostInfo();
     private static final IAccountChangeProcessor processor = new AccountChangeProcessorImpl();

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
    public void testChangeUserEmail()
    {
        UserAccount account = userAccount;
        account.setEmailAddr("test@test.com");

        AuthenticationData userSecurity = new AuthenticationData();
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
    public void testChangeUserContactChangeTelephone()
    {
        UserAccount account = userAccount;
        account.setTelephoneNumber("716-341-1697");

        AuthenticationData userSecurity = new AuthenticationData();
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
    public void testChangeUserContactChangePager()
    {
        UserAccount account = userAccount;
        account.setPagerNumber("716-341-1697");

        AuthenticationData userSecurity = new AuthenticationData();
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
    public void testChangeUserPassword()
    {
        AuthenticationData userSecurity = new AuthenticationData();
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
    public void testChangeUserSecurity()
    {
        AuthenticationData userSecurity = new AuthenticationData();
        userSecurity.setPassword("Ariana21*");
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

    @After
    public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
        CoreServiceInitializer.shutdown();
    }
}
