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
 * File: AccountResetProcessorImplTest.java
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
import org.apache.commons.lang.RandomStringUtils;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.dto.UserSecurity;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.security.processors.dto.AccountResetRequest;
import com.cws.esolutions.security.processors.dto.AccountResetResponse;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.security.processors.exception.AccountResetException;
import com.cws.esolutions.security.processors.interfaces.IAccountResetProcessor;

public class AccountResetProcessorImplTest
{
    private static RequestHostInfo hostInfo = null;

    @Before
    public void setUp()
    {
        try
        {
            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/logging/logging.xml");

            hostInfo = new RequestHostInfo();
            hostInfo.setHostAddress("junit");
            hostInfo.setHostName("junit");
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            System.exit(1);
        }
    }

    @Test
    public void testResetUserPassword()
    {
        IAccountResetProcessor processor = new AccountResetProcessorImpl();

        UserAccount account = new UserAccount();
        account.setGuid("74d9729b-7fb2-4fef-874b-c9ee5d7a5a95");
        account.setUsername("khuntly");
        account.setSurname("Huntly");
        account.setEmailAddr("kmhuntly@gmail.com");
        account.setGivenName("Kevin");
        account.setOlrSetup(false);
        account.setOlrLocked(false);
        account.setSuspended(false);
        account.setFailedCount(0);
        hostInfo.setSessionId(RandomStringUtils.randomAlphanumeric(32));

        AccountResetRequest request = new AccountResetRequest();
        request.setApplicationName("esolutions");
        request.setHostInfo(hostInfo);
        request.setRequestor(account);
        request.setUserAccount(account);

        try
        {
            AccountResetResponse response = processor.resetUserPassword(request);

            Assert.assertEquals(SecurityRequestStatus.SUCCESS, response.getRequestStatus());
        }
        catch (AccountResetException arx)
        {
            Assert.fail(arx.getMessage());
        }
    }

    @Test
    public void testVerifyResetRequest()
    {
        IAccountResetProcessor processor = new AccountResetProcessorImpl();

        UserSecurity userSecurity = new UserSecurity();
        userSecurity.setResetRequestId("5elKReQu6KddvYQaXt0hYZ72K61TKH7JoSvk1t2WmsdQ7zTO75fTpzbhEgjYSCdL");

        AccountResetRequest request = new AccountResetRequest();
        request.setApplicationName("esolutions");
        request.setHostInfo(hostInfo);
        request.setUserSecurity(userSecurity);

        try
        {
            AccountResetResponse response = processor.verifyResetRequest(request);

            Assert.assertEquals(SecurityRequestStatus.SUCCESS, response.getRequestStatus());
        }
        catch (AccountResetException arx)
        {
            Assert.fail(arx.getMessage());
        }
    }

    @After
    public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
