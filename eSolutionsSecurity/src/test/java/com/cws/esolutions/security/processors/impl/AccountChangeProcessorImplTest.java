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
import com.cws.esolutions.security.enums.SecurityUserRole;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.processors.dto.AccountChangeData;
import com.cws.esolutions.security.processors.dto.AuthenticationData;
import com.cws.esolutions.security.processors.enums.ResetRequestType;
import com.cws.esolutions.security.processors.dto.AccountChangeRequest;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.security.processors.dto.AccountChangeResponse;
import com.cws.esolutions.security.processors.exception.AccountChangeException;
import com.cws.esolutions.utility.securityutils.processors.dto.RequestHostInfo;
import com.cws.esolutions.security.processors.interfaces.IAccountChangeProcessor;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public final class AccountChangeProcessorImplTest
{
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
    	account.setGuid("e1006d6d-e815-4b27-9a8c-fb91227cc2b5");
    	account.setEmailAddr("foo@gmail.com");
    	account.setUserRole(SecurityUserRole.SITE_ADMIN);

        AccountChangeRequest request = new AccountChangeRequest();
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setApplicationName("eSolutions");
        request.setHostInfo(AccountChangeProcessorImplTest.hostInfo);
        request.setIsReset(false);
        request.setUserAccount(account);
        request.setRequestor(account);

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
    	String secAnsOne = RandomStringUtils.randomAlphanumeric(32);
    	String secAnsTwo = RandomStringUtils.randomAlphanumeric(32);

    	System.out.println(secAnsOne);
    	System.out.println(secAnsTwo);

    	UserAccount account = new UserAccount();
    	account.setUsername("khuntly");
    	account.setGuid("e1006d6d-e815-4b27-9a8c-fb91227cc2b5");
    	account.setUserRole(SecurityUserRole.SITE_ADMIN);

    	AccountChangeData changeData = new AccountChangeData();
    	changeData.setResetType(ResetRequestType.QUESTIONS);
    	changeData.setSecAnswerOne(secAnsOne.toCharArray());
    	changeData.setSecAnswerTwo(secAnsTwo.toCharArray());
    	changeData.setSecQuestionOne("What is your mother's maiden name ?");
    	changeData.setSecQuestionTwo("What is your favourite cartoon ?");

        AccountChangeRequest request = new AccountChangeRequest();
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setApplicationName("eSolutions");
        request.setHostInfo(AccountChangeProcessorImplTest.hostInfo);
        request.setIsReset(false);
        request.setUserAccount(account);
        request.setRequestor(account);
        request.setChangeData(changeData);

        try
        {
            AccountChangeResponse response = processor.changeUserSecurity(request);

            Assertions.assertThat(response.getRequestStatus()).isEqualTo(SecurityRequestStatus.SUCCESS);
        }
        catch (final AccountChangeException acx)
        {
        	Assertions.fail(acx.getMessage());
        }
    }

    @Test public void changeUserContact()
    {
    	UserAccount account = new UserAccount();
    	account.setUsername("khuntly");
    	account.setGuid("34538095-df16-445d-8690-e0f09f2d91e6");
    	account.setAuthToken("e932b80430c4e3a6c8e84be88d2b2fe7cbe4596f98b86e116db8e4a83b40667306d3b69b9ac833d988fcb473cb45cb027fe293ea068e4012074d321a0aff6613");
    	account.setUserRole(SecurityUserRole.SITE_ADMIN);
    	account.setTelephoneNumber("8623999098");
    	account.setPagerNumber("7162491027");

        AccountChangeRequest request = new AccountChangeRequest();
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setApplicationName("eSolutions");
        request.setHostInfo(AccountChangeProcessorImplTest.hostInfo);
        request.setIsReset(false);
        request.setUserAccount(account);
        request.setRequestor(account);

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
    	// TODO
    }

    @Test public void changeUserPassword()
    {
    	UserAccount account = new UserAccount();
    	account.setUsername("khuntly");
    	account.setGuid("e1006d6d-e815-4b27-9a8c-fb91227cc2b5");
    	account.setUserRole(SecurityUserRole.SITE_ADMIN);

    	AuthenticationData authData = new AuthenticationData();
    	authData.setNewPassword("naB8QUXNTWFA7MCpFYvT".toCharArray());
    	authData.setPassword("ANIBbuKHiGkyGANLOjawFZ9cZGXuCVRd".toCharArray());
    	authData.setUsername("khuntly");

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
            AccountChangeResponse response = processor.changeUserPassword(request);

            Assertions.assertThat(response.getRequestStatus()).isEqualTo(SecurityRequestStatus.SUCCESS);
        }
        catch (final AccountChangeException acx)
        {
        	Assertions.fail(acx.getMessage());
        }
    }

    @AfterAll public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
