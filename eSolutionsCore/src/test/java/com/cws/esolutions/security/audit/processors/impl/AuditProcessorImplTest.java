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
package com.cws.esolutions.security.audit.processors.impl;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.Assert;
import org.apache.commons.lang.RandomStringUtils;

import com.cws.esolutions.security.audit.dto.AuditEntry;
import com.cws.esolutions.security.audit.dto.AuditRequest;
import com.cws.esolutions.security.audit.dto.AuditResponse;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.security.audit.enums.AuditType;
import com.cws.esolutions.security.audit.exception.AuditServiceException;
import com.cws.esolutions.security.audit.processors.interfaces.IAuditProcessor;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.dto.UserSecurity;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.security.dao.userauth.enums.LoginType;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.security.processors.dto.AuthenticationRequest;
import com.cws.esolutions.security.dao.userauth.enums.AuthenticationType;
import com.cws.esolutions.security.processors.dto.AuthenticationResponse;
import com.cws.esolutions.security.processors.impl.AuthenticationProcessorImpl;
import com.cws.esolutions.security.processors.interfaces.IAuthenticationProcessor;
/**
 * eSolutionsCore
 * com.cws.esolutions.security.audit.processors.impl
 * AuditProcessorImplTest.java
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
 * 35033355 @ Apr 5, 2013 12:54:41 PM
 *     Created.
 */
public class AuditProcessorImplTest
{
    private UserAccount userAccount = new UserAccount();
    private RequestHostInfo hostInfo = new RequestHostInfo();

    private static final IAuditProcessor auditor = new AuditProcessorImpl();

    @Before
    public final void setUp()
    {
        try
        {
            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/config/SecurityLogging.xml");

            IAuthenticationProcessor agentAuth = new AuthenticationProcessorImpl();
            hostInfo.setHostAddress("junit");
            hostInfo.setHostName("junit");

            UserAccount account = new UserAccount();
            account.setUsername("khuntly");
            hostInfo.setSessionId(RandomStringUtils.randomAlphanumeric(32));

            try
            {
                AuthenticationRequest userRequest = new AuthenticationRequest();
                userRequest.setHostInfo(hostInfo);
                userRequest.setUserAccount(account);
                userRequest.setApplicationName("esolutions");
                userRequest.setApplicationId("B760E92F-827A-42E7-9E8D-64334657BA83");
                userRequest.setAuthType(AuthenticationType.LOGIN);
                userRequest.setLoginType(LoginType.USERNAME);
                userRequest.setTimeoutValue(10000);

                AuthenticationResponse userResponse = agentAuth.processAgentLogon(userRequest);

                if (userResponse.getRequestStatus() == SecurityRequestStatus.SUCCESS)
                {
                    UserAccount authUser = userResponse.getUserAccount();

                    if (authUser.getStatus() == LoginStatus.SUCCESS)
                    {
                        UserSecurity userSecurity = new UserSecurity();
                        userSecurity.setPassword("Ariana16*");

                        AuthenticationRequest passRequest = new AuthenticationRequest();
                        passRequest.setHostInfo(hostInfo);
                        passRequest.setUserAccount(account);
                        passRequest.setApplicationName("esolutions");
                        passRequest.setApplicationId("B760E92F-827A-42E7-9E8D-64334657BA83");
                        passRequest.setAuthType(AuthenticationType.LOGIN);
                        passRequest.setLoginType(LoginType.PASSWORD);
                        passRequest.setTimeoutValue(10000);
                        passRequest.setUserSecurity(userSecurity);

                        AuthenticationResponse passResponse = agentAuth.processAgentLogon(passRequest);

                        if (passResponse.getRequestStatus() == SecurityRequestStatus.SUCCESS)
                        {
                            userAccount = passResponse.getUserAccount();
                            hostInfo.setSessionId(RandomStringUtils.randomAlphanumeric(32));

                            Assert.assertEquals(LoginStatus.SUCCESS, userAccount.getStatus());
                        }
                        else
                        {
                            Assert.fail("Account login failed");
                        }
                    }
                    else
                    {
                        Assert.fail("Account login failed");
                    }
                }
                else
                {
                    Assert.fail("Account login failed");
                }
            }
            catch (Exception e)
            {
                Assert.fail(e.getMessage());

                System.exit(1);
            }
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());

            System.exit(1);
        }
    }

    @Test
    public void testAuditRequest()
    {
        AuditEntry auditEntry = new AuditEntry();
        auditEntry.setApplicationId("JUNIT");
        auditEntry.setApplicationName("JUNIT");
        auditEntry.setAuditType(AuditType.JUNIT);
        auditEntry.setHostInfo(hostInfo);
        auditEntry.setUserAccount(userAccount);

        AuditRequest auditRequest = new AuditRequest();
        auditRequest.setAuditEntry(auditEntry);

        try
        {
            auditor.auditRequest(auditRequest);
        }
        catch (AuditServiceException asx)
        {
            Assert.fail("An error occurred while performing the test");
        }
    }

    @Test
    public void testGetAuditEntries()
    {
        AuditEntry auditEntry = new AuditEntry();
        auditEntry.setUserAccount(userAccount);

        AuditRequest request = new AuditRequest();
        request.setAuditEntry(auditEntry);

        try
        {
            AuditResponse response = auditor.getAuditEntries(request);

            Assert.assertEquals(SecurityRequestStatus.SUCCESS, response.getRequestStatus());
        }
        catch (AuditServiceException asx)
        {
            Assert.fail("An error occurred while performing the test");
        }
    }

    @After
    public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
