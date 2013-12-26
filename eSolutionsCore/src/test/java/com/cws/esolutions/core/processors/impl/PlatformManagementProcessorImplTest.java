/*
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
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.processors.impl
 * File: PlatformManagementProcessorImplTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.List;
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
import com.cws.esolutions.core.processors.enums.ServiceRegion;
import com.cws.esolutions.core.processors.enums.ServiceStatus;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.core.listeners.CoreServiceInitializer;
import com.cws.esolutions.core.processors.enums.NetworkPartition;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.core.processors.dto.PlatformManagementRequest;
import com.cws.esolutions.core.processors.dto.PlatformManagementResponse;
import com.cws.esolutions.core.processors.exception.PlatformManagementException;
import com.cws.esolutions.core.processors.interfaces.IPlatformManagementProcessor;

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
        List<String> devServers = new ArrayList<>(
                Arrays.asList(
                        "1c03d56a-a626-4efa-b7b1-9e6dbe9dba39",
                        "8ed3305f-a7f1-46b2-aee1-f90590bd3818",
                        "90542d9a-7dd8-449d-ada3-193872b93015",
                        "d4d2cd32-b8e7-4083-8f05-ae16cb121094",
                        "81b3e6fe-632f-4b5b-aff4-c643a8c50dce",
                        "4a68a86b-2d13-44a3-9cb2-1fd2c55ebeee",
                        "f625068e-f31a-43b1-8b14-87856f435d72",
                        "04d180fd-b697-47d8-a43f-39844f78790f",
                        "ad0487fe-d7fa-4692-bd70-adeaca80b248",
                        "00dbf607-481c-4965-9b18-722ea2a52c9b",
                        "e6f208a9-9f7c-47b0-aeb6-5b9fd5b53966",
                        "2c7cf9d5-914c-41cb-9019-ba6c86370f70",
                        "c5c8d1e7-aa15-46ca-b9b4-81048853d471",
                        "607d5ceb-6957-49e5-a639-e7d74f45f4bf",
                        "85ab3069-fd3c-4490-a4ba-0c59b7cebab2",
                        "f9acb920-791a-42bb-b9ed-ee09610d2294",
                        "71b68ab0-f750-4c82-88f1-e29d61624e6b"));

        List<String> qaServers = new ArrayList<>(
                Arrays.asList(
                        "582761a9-8697-4ce4-8a63-c4c7be4bdb97",
                        "337f3482-0603-4dee-a1d3-085a1c9a0458",
                        "56306b11-9473-444f-a830-b6a515a85031",
                        "61137da6-4d67-4689-a6f8-abb4ba0d36b1",
                        "12d1ecf4-adc3-4c30-80e2-73f51cd72e8c",
                        "56e23728-e23d-4585-b65e-863727c2f028",
                        "1fe5b7bb-9cdb-4170-a73f-4eccdc1ccf47",
                        "86771cc3-21b4-4e82-9e5d-f1a94aa07439",
                        "e4cf84f1-9403-4c30-99d9-d8155a4a3801",
                        "3fdf1628-9cc7-47c7-a242-0171fb771d13",
                        "af373ae5-8536-46e1-a8b7-137599962fdf",
                        "70a0643b-7057-4347-9d78-df5b7fd2e99e",
                        "ed91ea4a-8eb8-4260-8b79-98cbb76ebfd3",
                        "4e7d09a1-3834-426f-aed5-ec473940b870",
                        "f850a61c-e019-48e5-a22c-b51de7641ceb",
                        "e3e2c870-7e9c-4746-8194-60ceec16c398",
                        "00f3fbd2-6730-457e-9eec-c473a91e166a"));

        List<String> prdServers = new ArrayList<>(
                Arrays.asList(
                        "fbcb8f0e-15c5-427d-92e8-174903d22839",
                        "0ec50874-f333-442f-ac80-efe6af1ba142",
                        "f905aef1-49e4-4b1d-ae0d-0214a6588a3f",
                        "111c6993-85b7-4064-9069-607e0cdb4c57",
                        "248bad12-2b90-45c5-b858-9685e11f0b10",
                        "19942d3f-e585-42b4-b96b-0911b256c42c",
                        "4265a88d-96ba-4ced-b206-3d270f39c2e5",
                        "b4246ef7-d769-486d-94ba-dcd68807d51c",
                        "e8e84447-e87c-4a62-af2c-412170b92171",
                        "5ce9a68d-de39-45f0-a7d9-ccd3b30079ad",
                        "4274b507-bedb-4ff7-90e0-2e557f3c2516",
                        "90b6bfa5-9591-408d-a235-baea1541d98e",
                        "e6e7907f-4818-4020-ad97-156e2f4a63da",
                        "a8116127-3580-4e68-ba9d-3a13a87907fc",
                        "b1b5d9d1-1480-4214-82fd-b2838b08c6f5",
                        "b335e664-cce8-43f4-8156-5164573fac79",
                        "bbf9ced2-2070-45eb-8675-18ed5b747db4"));

        for (int x = 0; x < 3; x++)
        {
            List<Server> servers = new ArrayList<>();

            Platform platform = new Platform();

            if (x == 0)
            {
                platform.setRegion(ServiceRegion.DEV);

                for (String str : devServers)
                {
                    Server server = new Server();
                    server.setServerGuid(str);

                    servers.add(server);
                }
            }
            else if (x == 1)
            {
                platform.setRegion(ServiceRegion.QA);

                for (String str : qaServers)
                {
                    Server server = new Server();
                    server.setServerGuid(str);

                    servers.add(server);
                }
            }
            else if (x == 2)
            {
                platform.setRegion(ServiceRegion.PRD);

                for (String str : prdServers)
                {
                    Server server = new Server();
                    server.setServerGuid(str);

                    servers.add(server);
                }
            }

            platform.setName(RandomStringUtils.randomAlphabetic(8));
            platform.setStatus(ServiceStatus.ACTIVE);
            platform.setDescription("Test Platform");
            platform.setPartition(NetworkPartition.DMZ);
            platform.setServers(servers);

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
        platform.setName("BBFvgNHa");

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
            platform.setGuid(str);

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
