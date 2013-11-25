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
    public static final void setUp()
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
    public static final void testAddNewPlatform()
    {
        List<Server> webServers = null;
        List<Server> appServers = null;
        Platform platform = new Platform();

        String[] dmgrServers = { "5afecbc1-903e-4cdf-a3a3-b5bfa177d8c5", "a1924d39-c70b-4834-a66c-bd66ea649a60", "e2dfb58c-feeb-41e2-855c-090480e666b4" };

        String[] devWebs = { "1e412acd-c898-40f1-82a5-c1f025d67756", "10c175b5-d6c5-44bf-8d39-255c54484baa", "721a9d5e-6a7e-4732-9e71-e82a44c7a634", "628ba426-fa82-4759-ae6e-07e1e2da6d33" };
        String[] devApps = { "ee73fc6b-522a-4b62-8f63-7e097d13f72d", "4ce4b839-478f-4e28-8aaa-11356d31b15f", "f173e122-dedd-483a-8cbc-b5747bd1fd08", "761034e0-19e6-4bca-baf7-637d774479be" };

        String[] qaWebs = { "431cfb3e-84bf-4daa-9fa8-7093a21fc852", "2f6a5fd0-6e5c-4565-a48b-311d82011b43", "525e4c60-de08-46b9-92d8-7f04643aec20", "5247ab05-70d2-4f05-b482-0317cc2b6784" };
        String[] qaApps = { "a04501f4-a654-4f78-9459-ad64f014e468", "99e10ec6-d6a5-4464-9cc6-5108b438d1a2", "fad84667-24dc-4667-96de-c371d8fe1b14", "db895688-dec2-46d3-9a69-678436488f4c" };

        String[] prdWebs = { "d8714e68-3c0b-464b-b6bd-77eee99061e9", "5e0c9c18-8d3b-49e6-8a25-9647ef9d692b", "ef7d7553-5c8c-4651-8b79-5f0b2e1065f4", "841c3387-736c-47e1-a4ff-e382e174c137" };
        String[] prdApps = { "00abd443-1192-4d40-823f-91e1eca820b5", "e8da9ad2-9183-45d6-ac6a-419953e0e067", "80dac53d-1877-4f3c-bb94-4f4f4c54398b", "0e855ebe-ae76-4068-a976-9f6231e9a06f" };

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
    public static final void testUpdatePlatformData()
    {

    }

    @Test
    public static final void testListPlatforms()
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
    public static final void testListPlatformsByAttributeWithGuid()
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
    public static final void testGetPlatformData()
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
    public static final void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
