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

import com.cws.esolutions.security.enums.Role;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.core.processors.dto.Server;
import com.cws.esolutions.core.processors.dto.Platform;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.enums.ServiceRegion;
import com.cws.esolutions.core.processors.enums.ServiceStatus;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.core.listeners.CoreServiceInitializer;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.core.processors.dto.PlatformManagementRequest;
import com.cws.esolutions.core.processors.dto.PlatformManagementResponse;
import com.cws.esolutions.core.processors.exception.PlatformManagementException;
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
    private static UserAccount userAccount = new UserAccount();
    private static RequestHostInfo hostInfo = new RequestHostInfo();

    private static final IPlatformManagementProcessor processor = new PlatformManagementProcessorImpl();

    @Before
    public void setUp()
    {
        try
        {
            hostInfo.setHostAddress("junit");
            hostInfo.setHostName("junit");

            userAccount.setStatus(LoginStatus.SUCCESS);
            userAccount.setGuid("74d9729b-7fb2-4fef-874b-c9ee5d7a5a95");
            userAccount.setSurname("Huntly");
            userAccount.setFailedCount(0);
            userAccount.setOlrLocked(false);
            userAccount.setOlrSetup(false);
            userAccount.setSuspended(false);
            userAccount.setTcAccepted(false);
            userAccount.setRole(Role.SITEADMIN);
            userAccount.setDisplayName("Kevin Huntly");
            userAccount.setEmailAddr("kmhuntly@gmail.com");
            userAccount.setGivenName("Kevin");
            userAccount.setUsername("khuntly");
            userAccount.setPagerNumber("716-341-5669");
            userAccount.setTelephoneNumber("716-341-5669");
            userAccount.setServiceList(new ArrayList<>(
                Arrays.asList(
                    "96E4E53E-FE87-446C-AF03-0F5BC6527B9D",
                    "0C1C5F83-3EDD-4635-9F1E-6A9B5383747E",
                    "B52B1DE9-37A4-4554-B85E-2EA28C4EE3DD",
                    "F7D1DAB8-DADB-4E7B-8596-89D1BE230E75",
                    "4B081972-92C3-455B-9403-B81E68C538B6",
                    "5C0B0A54-2456-45C9-A435-B485ED36FAC7",
                    "D1B5D088-32B3-4AA1-9FCF-822CB476B649",
                    "A0F3C71F-5FAF-45B4-AA34-9779F64D397E",
                    "7CE2B9E8-9FCF-4096-9CAE-10961F50FA81",
                    "45F6BC9E-F45C-4E2E-B5BF-04F93C8F512E",
                    "3F0D3FB5-56C9-4A90-B177-4E1593088DBF",
                    "AEB46994-57B4-4E92-90AA-A4046F60B830")));

            CoreServiceInitializer.initializeService("eSolutionsCore/config/ServiceConfig.xml", "logging/logging.xml");

            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/config/SecurityLogging.xml");
        }
        catch (Exception ex)
        {
            Assert.fail(ex.getMessage());

            System.exit(-1);
        }
    }

    @Test
    public void testAddNewPlatform()
    {
        Server dmgrServer = null;
        List<Server> webServers = null;
        List<Server> appServers = null;
        Platform platform = new Platform();

        String[] devApps = { "f42c6cfa-09dc-4466-8d6b-f8dd3dc1b4cc", "0d069a14-6784-4062-a3a9-f15ae016fc14", "646e4092-e94a-441b-b8ae-e389db572f82", "dad6aea6-d6a9-4b0f-a344-75d04839483a" };
        String[] devWebs = { "f21dba8a-1ad9-41f7-a649-c611efd877d0", "baad4160-2e13-4416-8a21-a4e2dcd20903", "935df58d-679a-4371-a849-0eaa0cf21c0b", "253ef6b2-4b55-4cd3-a70e-ad1d745031c5" };

        String[] qaApps = { "b84641e3-7668-47ab-8b92-68ac46d2cdd7", "db1f7113-29da-4602-a495-97c3b756dc66", "7203c86d-3942-41d4-b153-1e58ffb82c5e", "8302e2ab-2d78-4e02-85ee-fff372e2ed13" };
        String[] qaWebs = { "be004f67-4cdf-45d1-8cd7-aaa2afa5da0b", "4fbc14b1-d47f-4aa7-81db-ab014ba2d3da", "f4b1b42a-b309-4656-afc0-690ccacf176f", "5c6391f8-4d16-47f4-9b1d-3f2139354f26" };

        String[] prdApps = { "b0f6450c-06dc-4461-9b6e-0bc9bef1ed53", "60f19ffe-6d2f-45ac-9e92-b98cdcb9579f", "07e1b4fe-2263-41d4-83d7-6bdc77bd48af", "0bf33b7b-b84e-46d5-90cd-ab554cf0298e" };
        String[] prdWebs = { "b834950b-353e-4f44-8ea0-d1a9c12d6f5c", "6c061f27-fca8-44e4-a0f4-6190813e4c4b", "19728549-1f3f-47a5-af24-b8eeb8270219", "58e440ea-80b0-43c8-8b44-102c8203d6d9" };

        for (int x = 0; x < 3; x++)
        {
            platform.setPlatformGuid(UUID.randomUUID().toString());
            platform.setPlatformName(RandomStringUtils.randomAlphabetic(8));
            platform.setStatus(ServiceStatus.ACTIVE);
            platform.setDescription("Test Platform");

            if (x == 0)
            {
                dmgrServer = new Server();
                dmgrServer.setServerGuid("7269ccd3-c867-40ec-a44a-89d1ebca8e16");

                platform.setPlatformRegion(ServiceRegion.DEV);

                webServers = new ArrayList<>();

                for (String str : devWebs)
                {
                    Server server = new Server();
                    server.setServerGuid(str);

                    webServers.add(server);
                }

                appServers = new ArrayList<>();

                for (String str : devApps)
                {
                    Server server = new Server();
                    server.setServerGuid(str);

                    appServers.add(server);
                }
            }
            else if (x == 1)
            {
                dmgrServer = new Server();
                dmgrServer.setServerGuid("774307b5-5f8c-4dc3-9c71-06ea05df7da8");

                platform.setPlatformRegion(ServiceRegion.QA);

                webServers = new ArrayList<>();

                for (String str : qaWebs)
                {
                    Server server = new Server();
                    server.setServerGuid(str);

                    webServers.add(server);
                }

                appServers = new ArrayList<>();

                for (String str : qaApps)
                {
                    Server server = new Server();
                    server.setServerGuid(str);

                    appServers.add(server);
                }
            }
            else if (x == 2)
            {
                dmgrServer = new Server();
                dmgrServer.setServerGuid("1d9c28ef-5d65-4251-a486-e2b6cf335911");

                platform.setPlatformRegion(ServiceRegion.PRD);

                webServers = new ArrayList<>();

                for (String str : prdWebs)
                {
                    Server server = new Server();
                    server.setServerGuid(str);

                    webServers.add(server);
                }

                appServers = new ArrayList<>();

                for (String str : prdApps)
                {
                    Server server = new Server();
                    server.setServerGuid(str);

                    appServers.add(server);
                }
            }

            platform.setPlatformDmgr(dmgrServer);
            platform.setAppServers(appServers);
            platform.setWebServers(webServers);

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
    }

    @Test
    public void testListPlatforms()
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
    public void testListPlatformsByAttributeWithName()
    {
        Platform platform = new Platform();
        platform.setPlatformName("BBFvgNHa");

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
    public void testGetPlatformData()
    {
        String[] platforms = { "046119de-9dc0-406d-bf4f-32f32b199f5c", "3eedf26a-a789-4bbf-8ad7-b5f4bbfefe33", "7cc7cd82-aa87-4af8-a967-f2493cc7c095" };

        for (String str : platforms)
        {
            Platform platform = new Platform();
            platform.setPlatformGuid(str);

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
    }

    @After
    public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
