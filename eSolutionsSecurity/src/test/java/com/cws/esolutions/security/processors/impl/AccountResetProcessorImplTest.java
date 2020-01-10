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
 * File: AccountResetProcessorImplTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.Assert;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.security.processors.dto.AuthenticationData;
import com.cws.esolutions.security.processors.dto.AccountResetRequest;
import com.cws.esolutions.security.processors.dto.AccountResetResponse;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.security.processors.exception.AccountResetException;
import com.cws.esolutions.security.processors.interfaces.IAccountResetProcessor;

public class AccountResetProcessorImplTest
{
    private static RequestHostInfo hostInfo = null;

    private static final IAccountResetProcessor processor = new AccountResetProcessorImpl();

    @Before public void setUp()
    {
        try
        {
            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/logging/logging.xml", true);

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

    @Test public void findUserAccount()
    {
        AccountResetRequest request = new AccountResetRequest();
        request.setApplicationId("f42fb0ba-4d1e-1126-986f-800cd2650000");
        request.setApplicationName("eSolutions");
        request.setHostInfo(hostInfo);
        request.setSearchData("junit");

        try
        {
            AccountResetResponse response = processor.findUserAccount(request);

            Assert.assertEquals(SecurityRequestStatus.SUCCESS, response.getRequestStatus());
        }
        catch (AccountResetException ax)
        {
            Assert.fail(ax.getMessage());
        }
    }

    @Test public void obtainUserSecurityConfig()
    {
        UserAccount account = new UserAccount();
        account.setUsername("junit");
        account.setGuid("f42fb0ba-4d1e-1126-986f-800cd2650000");

        AccountResetRequest request = new AccountResetRequest();
        request.setApplicationId("f42fb0ba-4d1e-1126-986f-800cd2650000");
        request.setApplicationName("eSolutions");
        request.setHostInfo(hostInfo);
        request.setUserAccount(account);

        try
        {
            AccountResetResponse response = processor.obtainUserSecurityConfig(request);

            Assert.assertEquals(SecurityRequestStatus.SUCCESS, response.getRequestStatus());
        }
        catch (AccountResetException ax)
        {
            Assert.fail(ax.getMessage());
        }
    }

    @Test public void verifyUserSecurityConfig()
    {
        UserAccount account = new UserAccount();
        account.setUsername("junit");
        account.setGuid("f42fb0ba-4d1e-1126-986f-800cd2650000");

        AuthenticationData userSecurity = new AuthenticationData();
        userSecurity.setSecAnswerOne("answerone");
        userSecurity.setSecAnswerTwo("answertwo");

        AccountResetRequest request = new AccountResetRequest();
        request.setApplicationId("f42fb0ba-4d1e-1126-986f-800cd2650000");
        request.setApplicationName("eSolutions");
        request.setHostInfo(hostInfo);
        request.setUserAccount(account);
        request.setUserSecurity(userSecurity);

        try
        {
            AccountResetResponse response = processor.verifyUserSecurityConfig(request);

            Assert.assertEquals(SecurityRequestStatus.SUCCESS, response.getRequestStatus());
        }
        catch (AccountResetException ax)
        {
            Assert.fail(ax.getMessage());
        }
    }

    @Test public void resetUserPassword()
    {
        UserAccount account = new UserAccount();
        account.setGuid("f42fb0ba-4d1e-1126-986f-800cd2650000");
        account.setUsername("junit");
        account.setEmailAddr("cws-khuntly");

        AccountResetRequest request = new AccountResetRequest();
        request.setApplicationName("esolutions");
        request.setHostInfo(hostInfo);
        request.setUserAccount(account);

        try
        {
            AccountResetResponse response = processor.resetUserPassword(request);

            Assert.assertEquals(SecurityRequestStatus.SUCCESS, response.getRequestStatus());
        }
        catch (AccountResetException arx)
        {
            arx.printStackTrace();
            Assert.fail(arx.getMessage());
        }
    }

    @Test public void verifyResetRequest()
    {
        AccountResetRequest request = new AccountResetRequest();
        request.setApplicationName("esolutions");
        request.setHostInfo(hostInfo);
        request.setResetRequestId("hJRr61LbqEx9NngsgGbwNDdqVgB8eDy9HTsJoWPY4vTEj7QYPZK9hCbrlg9PyIYv");

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

    @After public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
