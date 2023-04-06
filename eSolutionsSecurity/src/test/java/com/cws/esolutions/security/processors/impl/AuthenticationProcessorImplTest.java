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
 * File: AuthenticationProcessorImplTest.java
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
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.processors.dto.AuthenticationData;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.security.processors.dto.AuthenticationRequest;
import com.cws.esolutions.security.processors.dto.AuthenticationResponse;
import com.cws.esolutions.security.processors.exception.AuthenticationException;
import com.cws.esolutions.security.processors.interfaces.IAuthenticationProcessor;
import com.cws.esolutions.utility.securityutils.processors.dto.RequestHostInfo;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthenticationProcessorImplTest
{
    private static RequestHostInfo hostInfo = null;

    private static final IAuthenticationProcessor processor = (IAuthenticationProcessor) new AuthenticationProcessorImpl();

    @BeforeAll public void setUp()
    {
        try
        {
            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/logging/logging.xml", true);

            hostInfo = new RequestHostInfo();
            hostInfo.setHostAddress("junit");
            hostInfo.setHostName("junit");
        }
        catch (final Exception e)
        {
        	Assertions.fail(e.getMessage());
            System.exit(1);
        }
    }

    @Test public void processAgentLogon()
    {
        UserAccount account = new UserAccount();
        account.setUsername("khuntly");

        AuthenticationData userSecurity = new AuthenticationData();
        userSecurity.setPassword("ANIBbuKHiGkyGANLOjawFZ9cZGXuCVRd".toCharArray());

        AuthenticationRequest request = new AuthenticationRequest();
        request.setApplicationName("esolutions");
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setUserAccount(account);
        request.setUserSecurity(userSecurity);
        request.setHostInfo(hostInfo);

        try
        {
            AuthenticationResponse response = processor.processAgentLogon(request);

            Assertions.assertThat(response.getRequestStatus()).isEqualTo(SecurityRequestStatus.SUCCESS);
        }
        catch (final AuthenticationException ax)
        {
            Assertions.fail(ax.getMessage());
        }
    }

    @Test public void processAgentLogoff()
    {
        UserAccount account = new UserAccount();
        account.setUsername("khuntly");
        account.setGuid("98a7414b-e4d8-40f3-a8a5-f516cde049c3");
        account.setAuthToken("cca9e0226f8b9c45ffc53b34a0ef0bb7198081afcc615c0221472710744f1a32d27cb4d2c023e62cd03d666b53a14cdd5a05c5294c5eddb37a7c2601829d588f");

        AuthenticationRequest request = new AuthenticationRequest();
        request.setApplicationName("esolutions");
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setUserAccount(account);
        request.setHostInfo(hostInfo);

        try
        {
            AuthenticationResponse response = processor.processAgentLogoff(request);

            Assertions.assertThat(response.getRequestStatus()).isEqualTo(SecurityRequestStatus.SUCCESS);
        }
        catch (final AuthenticationException ax)
        {
            Assertions.fail(ax.getMessage());
        }
    }

    @AfterAll public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
