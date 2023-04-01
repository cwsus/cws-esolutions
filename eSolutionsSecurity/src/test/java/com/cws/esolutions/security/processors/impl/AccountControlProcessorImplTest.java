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
 * File: AccountControlProcessorImplTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.apache.commons.lang3.RandomStringUtils;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.enums.SecurityUserRole;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.security.processors.dto.AuthenticationData;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.security.processors.dto.AccountControlRequest;
import com.cws.esolutions.security.processors.dto.AccountControlResponse;
import com.cws.esolutions.security.processors.exception.AccountControlException;
import com.cws.esolutions.security.processors.interfaces.IAccountControlProcessor;
/**
 * @author khuntly
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccountControlProcessorImplTest
{
    private static UserAccount userAccount = new UserAccount();
    private static RequestHostInfo hostInfo = new RequestHostInfo();
    private static final IAccountControlProcessor processor = new AccountControlProcessorImpl();

    @BeforeAll public void setUp()
    {
        try
        {
            hostInfo.setHostAddress("junit");
            hostInfo.setHostName("junit");

            userAccount.setStatus(LoginStatus.SUCCESS);
            userAccount.setGuid("1c260aba-5be1-4854-8695-d5ae9309d4e7");
            userAccount.setUsername("junit");
            userAccount.setUserRole(SecurityUserRole.SITE_ADMIN);
            userAccount.setSessionId(RandomStringUtils.randomAlphanumeric(128));

            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/logging/logging.xml", true);
        }
        catch (final Exception ex)
        {
        	ex.printStackTrace();

        	Assertions.fail(ex.getMessage());
            System.exit(-1);
        }
    }

    @Test public void createNewUser()
    {
    	UserAccount account = new UserAccount();
    	account.setUsername("khuntly");
    	account.setSurname("Huntly");
    	account.setGivenName("Kevin");
    	account.setTelephoneNumber("8623999098");
    	account.setPagerNumber("7162491027");
    	account.setUsername("khuntly");
    	account.setUserRole(SecurityUserRole.SITE_ADMIN);
    	account.setEmailAddr("kmhuntly@gmail.com");

        AuthenticationData authSec = new AuthenticationData();
        authSec.setPassword("ANIBbuKHiGkyGANLOjawFZ9cZGXuCVRd".toCharArray());
        authSec.setUsername("khuntly");

        AccountControlRequest request = new AccountControlRequest();
        request.setHostInfo(hostInfo);
        request.setRequestor(userAccount);
        request.setUserAccount(account);
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setApplicationName("eSolutions");
        request.setUserSecurity(authSec);

        try
        {
            AccountControlResponse response = processor.createNewUser(request);

            Assertions.assertThat(response.getRequestStatus()).isEqualTo(SecurityRequestStatus.SUCCESS);
        }
        catch (final AccountControlException acx)
        {
        	acx.printStackTrace();
            Assertions.fail(acx.getMessage());
        }
    }

    @Test public void loadUserAccount()
    {
    	UserAccount account = new UserAccount();
    	account.setUsername("khuntly");
    	account.setGuid("03a6c07e-a69f-452d-814f-d22fb497140b");
    	account.setUserRole(SecurityUserRole.SITE_ADMIN);

        AccountControlRequest request = new AccountControlRequest();
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setApplicationName("eSolutions");
        request.setHostInfo(hostInfo);
        request.setRequestor(userAccount);
        request.setUserAccount(account);

        try
        {
            AccountControlResponse response = processor.loadUserAccount(request);

            Assertions.assertThat(response.getRequestStatus()).isEqualTo(SecurityRequestStatus.SUCCESS);
        }
        catch (final AccountControlException acx)
        {
            Assertions.fail(acx.getMessage());
        }
    }

    @Test public void modifyUserSuspension()
    {
    	UserAccount account = new UserAccount();
    	account.setUsername("khuntly");
    	account.setGuid("d2ddb6f4-b86d-4927-a688-dd0431448abf");
    	account.setSuspended(false);
    	account.setUserRole(SecurityUserRole.SITE_ADMIN);

        AccountControlRequest request = new AccountControlRequest();
        request.setHostInfo(hostInfo);
        request.setUserAccount(account);
        request.setApplicationName("esolutions");
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setRequestor(userAccount);

        try
        {
            AccountControlResponse response = processor.modifyUserSuspension(request);

            Assertions.assertThat(response.getRequestStatus()).isEqualTo(SecurityRequestStatus.SUCCESS);
        }
        catch (final AccountControlException acx)
        {
            Assertions.fail(acx.getMessage());
        }
    }

    @Test public void modifyUserRole()
    {
    	UserAccount account = new UserAccount();
    	account.setUsername("khuntly");
    	account.setGuid("03a6c07e-a69f-452d-814f-d22fb497140b");
        account.setUserRole(SecurityUserRole.USER_ADMIN);

        AccountControlRequest request = new AccountControlRequest();
        request.setHostInfo(hostInfo);
        request.setUserAccount(account);
        request.setApplicationName("esolutions");
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setRequestor(userAccount);

        try
        {
            AccountControlResponse response = processor.modifyUserRole(request);

            Assertions.assertThat(response.getRequestStatus()).isEqualTo(SecurityRequestStatus.SUCCESS);
        }
        catch (final AccountControlException acx)
        {
            Assertions.fail(acx.getMessage());
        }
    }

    @Test public void modifyUserPassword()
    {
    	UserAccount account = new UserAccount();
    	account.setUsername("khuntly");
    	account.setGuid("567e3182-f38b-4f62-a804-e619dab8e9df");
        account.setUserRole(SecurityUserRole.USER_ADMIN);

        AuthenticationData authData = new AuthenticationData();
        authData.setUsername("khuntly");
        authData.setNewPassword("F3FbPBZUCrJvNq6RaCsk".toCharArray());

        AccountControlRequest request = new AccountControlRequest();
        request.setHostInfo(hostInfo);
        request.setUserAccount(account);
        request.setApplicationName("esolutions");
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setRequestor(account);
        request.setUserSecurity(authData);

        try
        {
            AccountControlResponse response = processor.modifyUserPassword(request);

            Assertions.assertThat(response.getRequestStatus()).isEqualTo(SecurityRequestStatus.SUCCESS);
        }
        catch (final AccountControlException acx)
        {
            Assertions.fail(acx.getMessage());
        }
    }

    @Test public void modifyUserLockout()
    {
    	UserAccount account = new UserAccount();
    	account.setUsername("khuntly");
    	account.setGuid("03a6c07e-a69f-452d-814f-d22fb497140b");
    	account.setFailedCount(0);

        AccountControlRequest request = new AccountControlRequest();
        request.setHostInfo(hostInfo);
        request.setUserAccount(account);
        request.setApplicationName("esolutions");
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setRequestor(userAccount);
        request.setServiceId("AEB46994-57B4-4E92-90AA-A4046F60B830");

        try
        {
            AccountControlResponse response = processor.modifyUserLockout(request);

            Assertions.assertThat(response.getRequestStatus()).isEqualTo(SecurityRequestStatus.SUCCESS);
        }
        catch (final AccountControlException acx)
        {
            Assertions.fail(acx.getMessage());
        }
    }

    @Test public void removeUserAccount()
    {
    	UserAccount account = new UserAccount();
    	account.setUsername("khuntly");
    	account.setGuid("03a6c07e-a69f-452d-814f-d22fb497140b");

        AccountControlRequest request = new AccountControlRequest();
        request.setHostInfo(hostInfo);
        request.setUserAccount(account);
        request.setApplicationName("esolutions");
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setRequestor(userAccount);
        request.setServiceId("AEB46994-57B4-4E92-90AA-A4046F60B830");

        try
        {
            AccountControlResponse response = processor.removeUserAccount(request);

            Assertions.assertThat(response.getRequestStatus()).isEqualTo(SecurityRequestStatus.SUCCESS);
        }
        catch (final AccountControlException acx)
        {
            Assertions.fail(acx.getMessage());
        }
    }

    @AfterAll public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
