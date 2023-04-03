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
 * Package: com.cws.esolutions.security.processors.processors.impl
 * File: AuditProcessorImplTest.java
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

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.enums.SecurityUserRole;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.security.processors.dto.AccountSearchRequest;
import com.cws.esolutions.security.processors.dto.AccountSearchResponse;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.security.processors.exception.AccountSearchException;
import com.cws.esolutions.security.processors.interfaces.IAccountSearchProcessor;
/**
 * @author cws-khuntly
 * @version 1.0
 * @see java.io.Serializable
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountSearchProcessorImplTest
{
	private static final IAccountSearchProcessor searcher = (IAccountSearchProcessor) new AccountSearchProcessorImpl();

    @BeforeAll public void setUp()
    {
        try
        {
            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/logging/logging.xml", true);
        }
        catch (final Exception ex)
        {
            Assertions.fail(ex.getMessage());

            System.exit(-1);
        }
    }

	@Test
	final void findUserAccount()
	{
		RequestHostInfo hostInfo = new RequestHostInfo();
		hostInfo.setHostAddress("junit");
		hostInfo.setHostName("junit");

		AccountSearchRequest request = new AccountSearchRequest();
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setApplicationName("eSolutions");
        request.setHostInfo(hostInfo);
        request.setSearchTerms("kmhuntly@gmail.com");

        try
        {
            AccountSearchResponse response = searcher.findUserAccount(request);

            Assertions.assertThat(response.getRequestStatus()).isEqualTo(SecurityRequestStatus.SUCCESS);
        }
        catch (final AccountSearchException asx)
        {
            Assertions.fail(asx.getMessage());
        }
	}


    @Test public void searchAccounts()
    {
		RequestHostInfo hostInfo = new RequestHostInfo();
		hostInfo.setHostAddress("junit");
		hostInfo.setHostName("junit");

		UserAccount userAccount = new UserAccount();
		userAccount.setGuid("5d2eafcd-03b8-43f4-93d4-19e93c8d0ced");
		userAccount.setUsername("khuntly");
		userAccount.setUserRole(SecurityUserRole.SITE_ADMIN);

		AccountSearchRequest request = new AccountSearchRequest();
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setApplicationName("eSolutions");
        request.setHostInfo(hostInfo);
        request.setUserAccount(userAccount);
        request.setSearchTerms("khuntly");

        try
        {
            AccountSearchResponse response = searcher.searchAccounts(request);

            Assertions.assertThat(response.getRequestStatus()).isEqualTo(SecurityRequestStatus.SUCCESS);
        }
        catch (final AccountSearchException asx)
        {
            Assertions.fail(asx.getMessage());
        }
    }

    @AfterAll public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
