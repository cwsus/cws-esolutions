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

            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/logging/logging.xml", true);
        }
        catch (final Exception ex)
        {
            Assertions.fail(ex.getMessage());

            System.exit(-1);
        }
    }

    @Test public void changeUserEmail()
    {
    	UserAccount account = new UserAccount();
    	account.setUsername("khuntly");
    	account.setGuid("393ac161-3941-428b-9d60-31d72b839cf3");
    	account.setEmailAddr("kmhuntly@gmail.com");

    	AuthenticationData authData = new AuthenticationData();
    	authData.setUsername("khuntly");
    	authData.setPassword("ANIBbuKHiGkyGANLOjawFZ9cZGXuCVRd".toCharArray());

        AccountChangeRequest request = new AccountChangeRequest();
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setApplicationName("eSolutions");
        request.setHostInfo(AccountChangeProcessorImplTest.hostInfo);
        request.setIsReset(false);
        request.setUserAccount(account);
        request.setRequestor(account);
        request.setUserSecurity(authData);

        try
        {
            AccountChangeResponse response = processor.changeUserEmail(request);

            Assertions.assertThat(response.getRequestStatus()).isEqualTo(SecurityRequestStatus.SUCCESS);
        }
        catch (final AccountChangeException acx)
        {
        	acx.printStackTrace();
        	Assertions.fail(acx.getMessage());
        }
    }

    @Test public void changeUserSecurity()
    {
    	String secAnsOne = RandomStringUtils.randomAlphanumeric(32);
    	String secAnsTwo = RandomStringUtils.randomAlphanumeric(32);

    	System.out.println(secAnsOne);
    	System.out.println(secAnsTwo);

    	UserAccount account = new UserAccount();
    	account.setUsername("khuntly");
    	account.setGuid("129e32f6-9f94-446b-b8ef-2451083a5681");

    	AuthenticationData authData = new AuthenticationData();
    	authData.setUsername("khuntly");
    	authData.setPassword("ANIBbuKHiGkyGANLOjawFZ9cZGXuCVRd".toCharArray());
        authData.setSecQuestionOne("What is your mother's maiden name ?");
        authData.setSecQuestionTwo("What is your favourite cartoon ?");
        authData.setSecAnswerOne(secAnsOne.toCharArray());
        authData.setSecAnswerTwo(secAnsTwo.toCharArray());

        AccountChangeRequest request = new AccountChangeRequest();
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setApplicationName("eSolutions");
        request.setHostInfo(AccountChangeProcessorImplTest.hostInfo);
        request.setIsReset(false);
        request.setUserAccount(account);
        request.setRequestor(account);
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
    	authData.setUsername("khuntly");
    	authData.setPassword("ANIBbuKHiGkyGANLOjawFZ9cZGXuCVRd".toCharArray());

        AccountChangeProcessorImplTest.userAccount.setPagerNumber("5555561212");
        AccountChangeProcessorImplTest.userAccount.setTelephoneNumber("5555551213");

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
    	authData.setNewPassword("naB8QUXNTWFA7MCpFYvT".toCharArray());
    	authData.setUsername("khuntly");

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
