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

import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.Assert;
import org.apache.commons.lang.RandomStringUtils;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.dto.UserSecurity;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.processors.dto.AccountResetRequest;
import com.cws.esolutions.security.processors.dto.AccountResetResponse;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.security.processors.exception.AccountResetException;
import com.cws.esolutions.security.processors.interfaces.IAccountResetProcessor;
/**
 * eSolutionsCore
 * com.cws.esolutions.security.processors.impl
 * AccountResetProcessorImplTest.java
 *
 * $Id: $
 * $Author: $
 * $Date: $
 * $Revision: $
 * @author kmhuntly@gmail.com
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * 35033355 @ Jul 12, 2013 10:24:03 AM
 *     Created.
 */
public class AccountResetProcessorImplTest
{
    private RequestHostInfo hostInfo = null;

    @Before
    public final void setUp()
    {
        try
        {
            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/config/SecurityLogging.xml");

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
    public final void testResetUserPassword()
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
        account.setTcAccepted(true);
        account.setSessionId(RandomStringUtils.randomAlphanumeric(32));

        AccountResetRequest request = new AccountResetRequest();
        request.setAppName("esolutions");
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
    public final void testVerifyResetRequest()
    {
        IAccountResetProcessor processor = new AccountResetProcessorImpl();

        UserSecurity userSecurity = new UserSecurity();
        userSecurity.setResetRequestId("5elKReQu6KddvYQaXt0hYZ72K61TKH7JoSvk1t2WmsdQ7zTO75fTpzbhEgjYSCdL");

        AccountResetRequest request = new AccountResetRequest();
        request.setAppName("esolutions");
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
