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
        List<Server> webServers = null;
        List<Server> appServers = null;
        Platform platform = new Platform();

        String[] dmgrServers = { "8c367378-5ce3-45f9-9cb7-ddb001e5fd78", "6c935f13-1d44-421b-8be4-39007f76d7ce", "a865da0e-17ea-48ff-a654-4aa1e1a80090" };

        String[] devWebs = { "ff613974-a936-4dc0-8cfc-84bc4316e810", "1930e5de-1d33-4d80-8404-1ea8204842b9", "239210c0-7c0f-4843-9060-e210aa8cbe1f", "f17a037d-e5fe-4a68-a416-a7ffbb9110e9" };
        String[] devApps = { "89d8a5a7-ca30-41b9-91c5-f861dd142545", "90a64d5a-69d4-426a-ae45-fe44f2c7f56c", "d0f8dc22-d2e5-48dc-aea4-82dae7d42608", "4b16dd91-b686-4c16-bb16-e46e1123fd07" };

        String[] qaWebs = { "258c0cdb-488e-434b-a584-6b57b8c0624c", "6d38fa8d-fca2-4cf0-969f-65b11ad2719f", "36923e56-5b9b-44e5-b052-195ef71e2085", "69fbc95a-42a6-4eef-bd1a-d31d431f6b7e" };
        String[] qaApps = { "bb1a9333-aa40-438a-b5dd-d343259483b6", "c56ecc2a-644a-4e9a-af81-9fb039abc478", "8f57fa0a-5ab4-4078-83a8-35fc53325965", "c98576d7-9c07-4dbe-afa4-c26893aada66" };

        String[] prdWebs = { "b734d038-9388-4b6c-b8cb-41ea0003ab5f", "1ff9f763-d8be-474f-a154-e6bca5b8849f", "ae6c52e4-ae74-4f38-8a2f-e63c094b0558", "2a6eb578-7b83-4e88-961e-f75b42b946d0" };
        String[] prdApps = { "0ca57a1b-8079-4cad-b12d-c5e150b5d2b2", "4b522e0c-71f9-44e9-9199-e3401a3d8758", "b049a7dd-b735-48d1-8776-dbf2ecfbe44d", "68054f16-012d-488c-ae45-d25cbe796b5b" };

        for (int x = 0; x < 3; x++)
        {
            platform.setPlatformGuid(UUID.randomUUID().toString());
            platform.setPlatformName(RandomStringUtils.randomAlphabetic(8));
            platform.setStatus(ServiceStatus.ACTIVE);
            platform.setDescription("Test Platform");

            Server dmgrServer = new Server();
            dmgrServer.setServerGuid(dmgrServers[x]);

            platform.setPlatformDmgr(dmgrServer);

            if (x == 0)
            {
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

                    webServers.add(server);
                }
            }
            else if (x == 1)
            {
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

                    webServers.add(server);
                }
            }
            else if (x == 2)
            {
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

                    webServers.add(server);
                }
            }

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
    public void testListPlatformsByAttributeWithGuid()
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
    public void testGetPlatformData()
    {
        Platform platform = new Platform();
        platform.setPlatformGuid("f9d0cd75-d751-4eca-84e6-abd14019e230");

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
    public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
