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

        String[] devApps = { "337b03a9-3183-4308-b342-fa95ac1b0054", "c4489015-3d81-4196-88b8-263600ac22af", "ddd5003b-416e-48a5-8b20-e34b614c0961", "8df742ca-6877-48f4-837f-d495bdc30d14" };
        String[] devWebs = { "ba13b6dc-f457-4fb6-ac2c-500742355802", "a57aa00c-a48b-423c-a562-32b976b3d43d", "184d6578-1272-452a-ae5b-1651fac23cc6", "a89e412b-4c4d-495c-9092-70a739124ea2" };

        String[] qaApps = { "16d7382d-4222-4868-ad22-339df1b965d9", "1f7f91e5-4027-4355-b559-50acd0810d76", "d15b195c-dcde-4607-8eb5-8ef45029c93f", "1419e626-206e-463d-b712-e9737542c40b" };
        String[] qaWebs = { "f7c0e1ad-4d41-4603-94dd-972b5bbce025", "427b1c7e-7b8e-4225-8537-b293734fbae1", "66c290b3-4500-4183-82b5-66adcb84845a", "a482701c-9de4-4c51-81ad-086f7f780f9c" };

        String[] prdApps = { "da58c050-2a23-4e9b-bfa7-078a0f5689a9", "88b7227a-6348-440d-ba43-481690787cb9", "b74b97c7-24bf-4849-b9cc-fda6922c96a8", "4f385034-6835-4c8c-85b8-5cbf57e297c2" };
        String[] prdWebs = { "9cc509a3-bdd2-412c-821a-255e889d9463", "0728f077-f95d-4216-832a-51029f16162f", "e47cea1f-4b8c-4aec-b528-b8294c506c30", "e3c1f2de-50a9-4af8-9c79-a4192684661e" };

        for (int x = 0; x < 3; x++)
        {
            platform.setPlatformGuid(UUID.randomUUID().toString());
            platform.setPlatformName(RandomStringUtils.randomAlphabetic(8));
            platform.setStatus(ServiceStatus.ACTIVE);
            platform.setDescription("Test Platform");

            if (x == 0)
            {
                dmgrServer = new Server();
                dmgrServer.setServerGuid("baa0be9a-562c-44a6-9f3b-ee70582e021f");

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
                dmgrServer.setServerGuid("f2584d54-27d7-4dd6-914c-f2f16d6c4abd");

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
                dmgrServer.setServerGuid("a42748df-96de-4449-a8fd-b58095f3bd10");

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
        Platform platform = new Platform();
        platform.setPlatformGuid("1b5a340d-3cdd-4818-ad81-f64e3d1f2acb");

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
