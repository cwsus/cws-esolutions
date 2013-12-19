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
import java.util.Arrays;
import org.junit.Before;
import org.junit.Assert;
import java.util.ArrayList;

import com.cws.esolutions.security.enums.Role;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.dto.UserSecurity;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.core.listeners.CoreServiceInitializer;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.security.processors.enums.ModificationType;
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
             userAccount.setSurname("Huntly");
             userAccount.setFailedCount(0);
             userAccount.setOlrLocked(false);
             userAccount.setOlrSetup(false);
             userAccount.setSuspended(false);
             userAccount.setTcAccepted(false);
             userAccount.setRole(Role.SITEADMIN);
             userAccount.setDisplayName("Kevin Huntly");
             userAccount.setEmailAddr("kmhuntly@gmail.com");
             userAccount.setGivenName("Kevin");
             userAccount.setUsername("khuntly");
             userAccount.setPagerNumber("716-341-5669");
             userAccount.setTelephoneNumber("716-341-5669");
             userAccount.setServiceList(new ArrayList<>(
                 Arrays.asList(
                     "96E4E53E-FE87-446C-AF03-0F5BC6527B9D",
                     "0C1C5F83-3EDD-4635-9F1E-6A9B5383747E",
                     "B52B1DE9-37A4-4554-B85E-2EA28C4EE3DD",
                     "F7D1DAB8-DADB-4E7B-8596-89D1BE230E75",
                     "4B081972-92C3-455B-9403-B81E68C538B6",
                     "5C0B0A54-2456-45C9-A435-B485ED36FAC7",
                     "D1B5D088-32B3-4AA1-9FCF-822CB476B649",
                     "A0F3C71F-5FAF-45B4-AA34-9779F64D397E",
                     "7CE2B9E8-9FCF-4096-9CAE-10961F50FA81",
                     "45F6BC9E-F45C-4E2E-B5BF-04F93C8F512E",
                     "3F0D3FB5-56C9-4A90-B177-4E1593088DBF",
                     "AEB46994-57B4-4E92-90AA-A4046F60B830")));

             CoreServiceInitializer.initializeService("eSolutionsCore/config/ServiceConfig.xml", "logging/logging.xml");

             SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/config/SecurityLogging.xml");
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
    public void testChangeUserContactChangeTelephone()
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
    public void testChangeUserContactChangePager()
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
    public void testChangeUserPassword()
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
    public void testChangeUserSecurity()
    {
        UserSecurity userSecurity = new UserSecurity();
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

    @Test
    public void testChangeUserKeys()
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
