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
package com.cws.esolutions.core.processors.impl;

import java.util.List;
import java.util.UUID;
import org.junit.Test;
import org.junit.After;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Before;
import java.util.ArrayList;
import org.apache.commons.lang.RandomStringUtils;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.dto.UserSecurity;
import com.cws.esolutions.core.processors.dto.Server;
import com.cws.esolutions.core.processors.dto.Platform;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.enums.ServiceRegion;
import com.cws.esolutions.core.processors.enums.ServiceStatus;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.dao.userauth.enums.LoginType;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.core.listeners.CoreServiceInitializer;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.security.processors.dto.AuthenticationRequest;
import com.cws.esolutions.core.processors.dto.PlatformManagementRequest;
import com.cws.esolutions.security.dao.userauth.enums.AuthenticationType;
import com.cws.esolutions.security.processors.dto.AuthenticationResponse;
import com.cws.esolutions.core.processors.dto.PlatformManagementResponse;
import com.cws.esolutions.security.processors.impl.AuthenticationProcessorImpl;
import com.cws.esolutions.core.processors.exception.PlatformManagementException;
import com.cws.esolutions.security.processors.interfaces.IAuthenticationProcessor;
import com.cws.esolutions.core.processors.interfaces.IPlatformManagementProcessor;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.processors.impl
 * PlatformManagementProcessorImplTest.java
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
 * 35033355 @ Jun 4, 2013 1:46:05 PM
 *     Created.
 */
public class PlatformManagementProcessorImplTest
{
    private UserAccount userAccount = new UserAccount();
    private RequestHostInfo hostInfo = new RequestHostInfo();

    private static final IPlatformManagementProcessor processor = new PlatformManagementProcessorImpl();

    @Before
    public final void setUp() throws Exception
    {
        try
        {
            CoreServiceInitializer.initializeService("eSolutionsCore/config/ServiceConfig.xml", "logging/logging.xml");

            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/config/SecurityLogging.xml");

            IAuthenticationProcessor agentAuth = new AuthenticationProcessorImpl();
            hostInfo.setHostAddress("127.0.0.1");
            hostInfo.setHostName("localhost");

            UserAccount account = new UserAccount();
            account.setUsername("khuntly");
            account.setSessionId(RandomStringUtils.randomAlphanumeric(32));

            try
            {
                AuthenticationRequest userRequest = new AuthenticationRequest();
                userRequest.setAppName("esolutions");
                userRequest.setAuthType(AuthenticationType.LOGIN);
                userRequest.setLoginType(LoginType.USERNAME);
                userRequest.setUserAccount(account);
                userRequest.setApplicationId("B760E92F-827A-42E7-9E8D-64334657BA83");

                AuthenticationResponse userResponse = agentAuth.processAgentLogon(userRequest);

                if (userResponse.getRequestStatus() == SecurityRequestStatus.SUCCESS)
                {
                    UserAccount authUser = userResponse.getUserAccount();

                    if (authUser.getStatus() == LoginStatus.SUCCESS)
                    {
                        UserSecurity userSecurity = new UserSecurity();
                        userSecurity.setPassword("Ariana16*");

                        AuthenticationRequest passRequest = new AuthenticationRequest();
                        passRequest.setAppName("esolutions");
                        passRequest.setAuthType(AuthenticationType.LOGIN);
                        passRequest.setLoginType(LoginType.PASSWORD);
                        passRequest.setUserAccount(authUser);
                        passRequest.setUserSecurity(userSecurity);
                        passRequest.setApplicationId("B760E92F-827A-42E7-9E8D-64334657BA83");

                        AuthenticationResponse passResponse = agentAuth.processAgentLogon(passRequest);

                        if (passResponse.getRequestStatus() == SecurityRequestStatus.SUCCESS)
                        {
                            userAccount = passResponse.getUserAccount();
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
            }
        }
        catch (Exception ex)
        {
            Assert.fail(ex.getMessage());

            System.exit(-1);
        }
    }

    @Test
    public final void testAddNewPlatform()
    {
        Server dmgrServer = new Server();
        dmgrServer.setServerGuid("bc55f443-202b-4f7c-9118-47dd80500ffb");

        Server appServer1 = new Server();
        appServer1.setServerGuid("a8fb1dbf-e915-46d9-b8ce-21dc33afd236");

        Server appServer2 = new Server();
        appServer2.setServerGuid("63acb156-acd2-414d-883e-279f10a8f13b");

        Server appServer3 = new Server();
        appServer3.setServerGuid("44dc7ab7-f8c0-473a-a228-8169d222c4ab");

        Server appServer4 = new Server();
        appServer4.setServerGuid("28898aa1-28be-4e19-be9f-1d3226fb7595");

        Server webServer1 = new Server();
        webServer1.setServerGuid("fdc8c335-cda2-49f4-a66a-06417312dc4b");

        Server webServer2 = new Server();
        webServer2.setServerGuid("f1417fab-47d3-4375-8e8b-f41c222be689");

        Server webServer3 = new Server();
        webServer3.setServerGuid("737d91c8-6a40-459d-ab1e-b28fc368bcaa");

        Server webServer4 = new Server();
        webServer4.setServerGuid("4f69a63d-ce47-4c6e-a783-dc16a7bfe330");

        List<Server> appServerList = new ArrayList<Server>(
                Arrays.asList(
                        appServer1,
                        appServer2,
                        appServer3,
                        appServer4));

        List<Server> webServerList = new ArrayList<Server>(
                Arrays.asList(
                        webServer1,
                        webServer2,
                        webServer3,
                        webServer4));

        Platform platform = new Platform();
        platform.setPlatformGuid(UUID.randomUUID().toString());
        platform.setPlatformName("PLATFORM_X");
        platform.setStatus(ServiceStatus.ACTIVE);
        platform.setPlatformRegion(ServiceRegion.DEV);
        platform.setDescription("Test Platform");
        platform.setPlatformDmgr(dmgrServer);
        platform.setAppServers(appServerList);
        platform.setWebServers(webServerList);

        PlatformManagementRequest request = new PlatformManagementRequest();
        request.setPlatform(platform);
        request.setRequestInfo(hostInfo);
        request.setServiceId("D1B5D088-32B3-4AA1-9FCF-822CB476B649");
        request.setUserAccount(userAccount);

        try
        {
            PlatformManagementResponse response = processor.addNewPlatform(request);

            Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
        }
        catch (PlatformManagementException pmx)
        {
            Assert.fail(pmx.getMessage());
        }
    }

    @Test
    public final void testUpdatePlatformData()
    {

    }

    @Test
    public final void testListPlatforms()
    {
        PlatformManagementRequest request = new PlatformManagementRequest();
        request.setRequestInfo(hostInfo);
        request.setUserAccount(userAccount);
        request.setServiceId("D1B5D088-32B3-4AA1-9FCF-822CB476B649");

        try
        {
            PlatformManagementResponse response = processor.listPlatforms(request);

            Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
        }
        catch (PlatformManagementException pmx)
        {
            Assert.fail(pmx.getMessage());
        }
    }

    @Test
    public final void testListPlatformsByAttributeWithGuid()
    {
        Platform platform = new Platform();
        platform.setPlatformName("PLATFORM_X");

        PlatformManagementRequest request = new PlatformManagementRequest();
        request.setRequestInfo(hostInfo);
        request.setUserAccount(userAccount);
        request.setServiceId("D1B5D088-32B3-4AA1-9FCF-822CB476B649");
        request.setPlatform(platform);

        try
        {
            PlatformManagementResponse response = processor.listPlatformsByAttribute(request);

            Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
        }
        catch (PlatformManagementException pmx)
        {
            Assert.fail(pmx.getMessage());
        }
    }

    @Test
    public final void testGetPlatformData()
    {
        Platform platform = new Platform();
        platform.setPlatformGuid("1397a271-3c92-4063-aad1-da878cfca2c2");

        PlatformManagementRequest request = new PlatformManagementRequest();
        request.setRequestInfo(hostInfo);
        request.setUserAccount(userAccount);
        request.setServiceId("D1B5D088-32B3-4AA1-9FCF-822CB476B649");
        request.setPlatform(platform);

        try
        {
            PlatformManagementResponse response = processor.getPlatformData(request);

            Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
        }
        catch (PlatformManagementException pmx)
        {
            Assert.fail(pmx.getMessage());
        }
    }

    @After
    public void tearDown() throws Exception
    {
        SecurityServiceInitializer.shutdown();
    }
}
