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

        String[] dmgrServers = { "bc55f443-202b-4f7c-9118-47dd80500ffb", "dac2e765-109e-4385-8563-aab66d6713f9", "fde6d6e9-8bac-4a82-99c6-ef225945d846" };

        String[] devWebs = { "ff613974-a936-4dc0-8cfc-84bc4316e810", "1930e5de-1d33-4d80-8404-1ea8204842b9", "239210c0-7c0f-4843-9060-e210aa8cbe1f", "f17a037d-e5fe-4a68-a416-a7ffbb9110e9" };
        String[] devApps = { "782d5e5a-3960-42e1-a97a-b27b9545006e", "0964b7cf-4209-42e6-b0c7-93bb87eddfb5", "891e9fe0-5427-46db-8339-f41bfbd1c956", "9d750935-b005-4726-aca5-b174ff1f2bdf" };

        String[] qaWebs = { "7dbe55ba-ed0a-4846-9363-33350b4db4b5", "483a7126-4d21-430f-962f-ed6399997f8f", "acf9c6d6-26cf-40da-90dc-d26859ff9375", "8a531441-e680-4cc8-8b6d-2fa79439ceef" };
        String[] qaApps = { "828ff830-0973-43c6-a43c-dae9d8353136", "37afebed-aa44-4413-814d-46ee73cefd40", "2f93996a-fc75-4ec5-a7a1-768856024a20", "3e9a62b1-1c18-44fa-9486-3e770ee7f1f7" };

        String[] prdWebs = { "13ba5c2d-7f02-44e9-8f42-172d7cd36ed9", "dbf96d3d-a507-4bdb-b22e-0fda7e521636", "a6935d90-9c9d-4607-9929-59c4534d4883", "5cfde183-e2c8-4e8a-9d8d-ae7c9e52d473" };
        String[] prdApps = { "d0b0c2ec-92df-4147-b075-0768538f1f8b", "778dd013-89a9-406f-baab-91254ac94160", "e8ab22c1-9c48-46ef-b516-5f892bf05388", "b83f437f-b899-4731-a2b3-1511768dac2c" };

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
