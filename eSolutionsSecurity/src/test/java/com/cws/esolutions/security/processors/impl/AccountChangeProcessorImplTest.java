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
 * File: AccountChangeProcessorImplTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.assertj.core.api.Assertions;
import org.apache.commons.lang3.RandomStringUtils;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.security.processors.dto.AuthenticationData;
import com.cws.esolutions.security.processors.dto.AccountChangeRequest;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.security.processors.dto.AccountChangeResponse;
import com.cws.esolutions.security.processors.exception.AccountChangeException;
import com.cws.esolutions.security.processors.interfaces.IAccountChangeProcessor;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public final class AccountChangeProcessorImplTest
{
    private static UserAccount userAccount = new UserAccount();
    private static RequestHostInfo hostInfo = new RequestHostInfo();
    private static final IAccountChangeProcessor processor = new AccountChangeProcessorImpl();

    @BeforeAll public void setUp()
    {
        try
        {
            hostInfo.setHostAddress("junit");
            hostInfo.setHostName("junit");

            userAccount.setStatus(LoginStatus.SUCCESS);
            userAccount.setGuid("d4320dd2-fb97-4da8-806b-c424be78f0b4");
            userAccount.setUsername("khuntly");

            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/logging/logging.xml", true);
        }
        catch (final Exception ex)
        {
            Assertions.fail(ex.getMessage());

            System.exit(-1);
        }
    }

    @Test public void enableOtpAuth()
    {
    	AuthenticationData authData = new AuthenticationData();
    	authData.setUsername("junit");

        AccountChangeRequest request = new AccountChangeRequest();
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setApplicationName("eSolutions");
        request.setHostInfo(AccountChangeProcessorImplTest.hostInfo);
        request.setIsReset(false);
        request.setUserAccount(AccountChangeProcessorImplTest.userAccount);
        request.setRequestor(AccountChangeProcessorImplTest.userAccount);
        request.setUserSecurity(authData);

        try
        {
            AccountChangeResponse response = processor.enableOtpAuth(request);

            Assertions.assertThat(response.getRequestStatus()).isEqualTo(SecurityRequestStatus.SUCCESS);
        }
        catch (final AccountChangeException acx)
        {
        	Assertions.fail(acx.getMessage());
        }
    }

    @Test public void disableOtpAuth()
    {
    	AuthenticationData authData = new AuthenticationData();
    	authData.setUsername("junit");

        AccountChangeRequest request = new AccountChangeRequest();
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setApplicationName("eSolutions");
        request.setHostInfo(AccountChangeProcessorImplTest.hostInfo);
        request.setIsReset(false);
        request.setUserAccount(AccountChangeProcessorImplTest.userAccount);
        request.setRequestor(AccountChangeProcessorImplTest.userAccount);
        request.setUserSecurity(authData);

        try
        {
            AccountChangeResponse response = processor.disableOtpAuth(request);

            Assertions.assertThat(response.getRequestStatus()).isEqualTo(SecurityRequestStatus.SUCCESS);
        }
        catch (final AccountChangeException acx)
        {
        	Assertions.fail(acx.getMessage());
        }
    }

    @Test public void changeUserEmail()
    {
    	AuthenticationData authData = new AuthenticationData();
    	authData.setUsername("junit");

        AccountChangeProcessorImplTest.userAccount.setEmailAddr("test@test.com");

        AccountChangeRequest request = new AccountChangeRequest();
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setApplicationName("eSolutions");
        request.setHostInfo(AccountChangeProcessorImplTest.hostInfo);
        request.setIsReset(false);
        request.setUserAccount(AccountChangeProcessorImplTest.userAccount);
        request.setRequestor(AccountChangeProcessorImplTest.userAccount);
        request.setUserSecurity(authData);

        try
        {
            AccountChangeResponse response = processor.changeUserEmail(request);

            Assertions.assertThat(response.getRequestStatus()).isEqualTo(SecurityRequestStatus.SUCCESS);
        }
        catch (final AccountChangeException acx)
        {
        	Assertions.fail(acx.getMessage());
        }
    }

    @Test public void changeUserSecurity()
    {
    	AuthenticationData authData = new AuthenticationData();
    	authData.setUsername("khuntly");
    	authData.setPassword("ANIBbuKHiGkyGANLOjawFZ9cZGXuCVRd");
        authData.setSecQuestionOne("What is your mother's maiden name ?");
        authData.setSecQuestionTwo("What is your favourite cartoon ?");
        authData.setSecAnswerOne(RandomStringUtils.randomAlphanumeric(64));
        authData.setSecAnswerTwo(RandomStringUtils.randomAlphanumeric(64));

        AccountChangeRequest request = new AccountChangeRequest();
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setApplicationName("eSolutions");
        request.setHostInfo(AccountChangeProcessorImplTest.hostInfo);
        request.setIsReset(false);
        request.setUserAccount(AccountChangeProcessorImplTest.userAccount);
        request.setRequestor(AccountChangeProcessorImplTest.userAccount);
        request.setUserSecurity(authData);

        try
        {
            AccountChangeResponse response = processor.changeUserSecurity(request);

            Assertions.assertThat(response.getRequestStatus()).isEqualTo(SecurityRequestStatus.SUCCESS);
        }
        catch (final AccountChangeException acx)
        {
        	acx.printStackTrace();
        	Assertions.fail(acx.getMessage());
        }
    }

    @Test public void changeUserContact()
    {
    	AuthenticationData authData = new AuthenticationData();
    	authData.setUsername("junit");

        AccountChangeProcessorImplTest.userAccount.setPagerNumber("555-555-1212");
        AccountChangeProcessorImplTest.userAccount.setTelephoneNumber("555-555-1213");

        AccountChangeRequest request = new AccountChangeRequest();
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setApplicationName("eSolutions");
        request.setHostInfo(AccountChangeProcessorImplTest.hostInfo);
        request.setIsReset(false);
        request.setUserAccount(AccountChangeProcessorImplTest.userAccount);
        request.setRequestor(AccountChangeProcessorImplTest.userAccount);
        request.setUserSecurity(authData);

        try
        {
            AccountChangeResponse response = processor.changeUserContact(request);

            Assertions.assertThat(response.getRequestStatus()).isEqualTo(SecurityRequestStatus.SUCCESS);
        }
        catch (final AccountChangeException acx)
        {
        	Assertions.fail(acx.getMessage());
        }
    }

    @Test public void changeUserKeys()
    {
    	AuthenticationData authData = new AuthenticationData();
    	authData.setUsername("junit");

        AccountChangeRequest request = new AccountChangeRequest();
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setApplicationName("eSolutions");
        request.setHostInfo(AccountChangeProcessorImplTest.hostInfo);
        request.setIsReset(false);
        request.setUserAccount(AccountChangeProcessorImplTest.userAccount);
        request.setRequestor(AccountChangeProcessorImplTest.userAccount);
        request.setUserSecurity(authData);

        try
        {
            AccountChangeResponse response = processor.changeUserKeys(request);

            Assertions.assertThat(response.getRequestStatus()).isEqualTo(SecurityRequestStatus.SUCCESS);
        }
        catch (final AccountChangeException acx)
        {
            acx.printStackTrace();
            Assertions.fail(acx.getMessage());
        }
    }

    @Test public void changeUserPassword()
    {
    	AuthenticationData authData = new AuthenticationData();
    	authData.setNewPassword("Hailey27*");
    	authData.setUsername("junit-runner");
    	authData.setPassword("naB8QUXNTWFA7MCpFYvT");

        AccountChangeRequest request = new AccountChangeRequest();
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setApplicationName("eSolutions");
        request.setHostInfo(AccountChangeProcessorImplTest.hostInfo);
        request.setIsReset(false);
        request.setUserAccount(AccountChangeProcessorImplTest.userAccount);
        request.setRequestor(AccountChangeProcessorImplTest.userAccount);
        request.setUserSecurity(authData);

        try
        {
            AccountChangeResponse response = processor.changeUserPassword(request);

            Assertions.assertThat(response.getRequestStatus()).isEqualTo(SecurityRequestStatus.SUCCESS);
        }
        catch (final AccountChangeException acx)
        {
        	acx.printStackTrace();
        	Assertions.fail(acx.getMessage());
        }
    }

    @AfterAll public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
