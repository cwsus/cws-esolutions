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
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.assertj.core.api.Assertions;
import org.apache.commons.lang.RandomStringUtils;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.core.processors.dto.Server;
import com.cws.esolutions.core.processors.dto.Service;
import com.cws.esolutions.core.processors.enums.ServiceType;
import com.cws.esolutions.core.processors.enums.ServiceRegion;
import com.cws.esolutions.core.processors.enums.ServiceStatus;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.core.listeners.CoreServicesInitializer;
import com.cws.esolutions.core.processors.enums.NetworkPartition;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.core.processors.dto.ServiceManagementRequest;
import com.cws.esolutions.core.processors.dto.ServiceManagementResponse;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.core.processors.exception.ServiceManagementException;
import com.cws.esolutions.core.processors.interfaces.IServiceManagementProcessor;

public class ServiceManagementProcessorImplTest
{
    private static UserAccount userAccount = new UserAccount();
    private static RequestHostInfo hostInfo = new RequestHostInfo();

    private static final IServiceManagementProcessor processor = new ServiceManagementProcessorImpl();

    @BeforeAll public void setUp()
    {
        hostInfo.setHostAddress("junit");
        hostInfo.setHostName("junit");

        userAccount.setStatus(LoginStatus.SUCCESS);
        userAccount.setGuid("f42fb0ba-4d1e-1126-986f-800cd2650000");
        userAccount.setUsername("khuntly");
        
        try
        {
            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/logging/logging.xml", false);
            CoreServicesInitializer.initializeService("eSolutionsCore/config/ServiceConfig.xml", "eSolutionsCore/logging/logging.xml", true, true);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Assertions.fail(ex.getMessage());

            System.exit(-1);
        }
    }

    @Test public void addNewServiceAsPlatform()
    {
        List<String> devServers = new ArrayList<String>(
            Arrays.asList(
                "fc7a3a40-7d36-479e-b1e1-d27ce11ff0e4",
                "03783b9e-63af-48ba-8efb-38bd1744c2e1",
                "2d8d44f4-b205-469c-aadc-8f1046361bf2",
                "91fcabf8-ba76-4e1d-b86d-4e56142133c0",
                "a41bdec8-ca54-4ace-99d1-7d3dd78b4b9d",
                "6afd12c3-f132-4663-bb4e-a61605a3f8e6",
                "d1ed7ec7-0215-45d6-add1-bf67fe64bf85",
                "8ee97887-1664-4a89-891d-b59313d36f5c",
                "a56df26d-bb7a-4bac-af80-631da570fcec",
                "ece5bcbc-8f0d-48f5-9bf1-f797e120ae60",
                "7771f7de-4ae4-4470-9376-ff509024d6dc",
                "1cc124ad-8cc7-4531-beac-109050e03ad2",
                "29fc6cd0-ae72-4a63-962f-f751d48b64d4",
                "8ace1144-dc36-44cf-b497-c8dffe38c08f",
                "04c5168a-12e3-49d7-938f-dd8a516f9570",
                "3b303d2f-29d7-44d6-9984-2f2f3ef268ff",
                "5fc1a31f-967d-47ae-9c97-82cb4186c7f4"));

        List<String> qaServers = new ArrayList<String>(
            Arrays.asList(
                "42209338-ad01-45fd-8786-6bb4080662d8",
                "303c1556-6a01-4e71-b75c-7ac54ecbd075",
                "8ae0d02a-18a4-43a2-9bcd-22e2fb7c8eda",
                "f5c11015-e3cc-4023-a5bc-7fbede7f52bf",
                "471d1386-24c2-482b-903d-795b89affe41",
                "3100a13c-2988-4714-a09a-362232d87a25",
                "fed7bbcc-c9cd-4a3a-9c3c-f3ca5ac8afc4",
                "826f348c-9eff-478a-b5ec-ef78522eb18e",
                "b764bf80-9f68-47fb-afb5-fcc84df26938",
                "244616ae-93ba-4f63-86e5-f40f7ef0cf0e",
                "913f3b33-76e9-460a-888e-578b320b6f04",
                "06b2800d-37db-49cd-9669-bf10ef3d64d8",
                "1965299e-9d6c-4287-8af1-f46e5089abed",
                "7b9323fc-9389-4b70-86aa-3cbcb282d8c6",
                "def055fb-9f94-405e-b82b-7c8332db875d",
                "77d9e1c9-f39a-4f85-b72a-70c84c7c12ae",
                "a462e029-8854-4ff0-92de-d55535cfad08"));

        List<String> prdServers = new ArrayList<String>(
            Arrays.asList(
                "f8040ce6-86c0-4107-8a07-6ae8fd063018",
                "a4507b8f-34f9-4e8c-aa74-45071ea0d2d3",
                "02617e53-0906-4947-8345-7ed2eea9a155",
                "fed4bb9b-1e55-4aa7-b577-ea6872a1c04e",
                "7e340535-7511-415c-b4c8-890d3f390d0f",
                "f6e9c404-638c-420f-be5f-32b6fcaa6ee4",
                "a27d8f5a-4707-4ac7-a73f-d9fa6df2fde6",
                "ec8447cf-85c3-46c4-b267-1dc7ee54131c",
                "6c37b082-3f33-4515-82d8-c28809ff294d",
                "116f558d-fcc8-46a1-9d3d-1d9e1200dfeb",
                "c85a53e5-430b-457b-9c93-8a3a8026e6ce",
                "76bb5d6d-79bf-4af8-8efb-8ea0b99d1e0a",
                "49f40358-21d1-4344-8a96-2421baebde6b",
                "dd2f3732-fc47-4a2d-a7c6-a887fc662d09",
                "4cc0d74d-e036-4d9a-8568-09b43ba4dc03",
                "449af7ce-9b1e-41dc-ab56-6ae24d48978d",
                "eb2e567c-92ed-4951-8949-973400010c0e"));

        for (int x = 0; x < 3; x++)
        {
            List<Server> servers = new ArrayList<Server>();

            Service service = new Service();

            if (x == 0)
            {
                service.setRegion(ServiceRegion.DEV);

                for (String str : devServers)
                {
                    Server server = new Server();
                    server.setServerGuid(str);

                    servers.add(server);
                }
            }
            else if (x == 1)
            {
                service.setRegion(ServiceRegion.QA);

                for (String str : qaServers)
                {
                    Server server = new Server();
                    server.setServerGuid(str);

                    servers.add(server);
                }
            }
            else if (x == 2)
            {
                service.setRegion(ServiceRegion.PRD);

                for (String str : prdServers)
                {
                    Server server = new Server();
                    server.setServerGuid(str);

                    servers.add(server);
                }
            }

            service.setType(ServiceType.PLATFORM);
            service.setName(RandomStringUtils.randomAlphabetic(8));
            service.setStatus(ServiceStatus.ACTIVE);
            service.setDescription("Test Service");
            service.setPartition(NetworkPartition.DMZ);
            service.setServers(servers);

            ServiceManagementRequest request = new ServiceManagementRequest();
            request.setService(service);
            request.setRequestInfo(hostInfo);
            request.setServiceId("D1B5D088-32B3-4AA1-9FCF-822CB476B649");
            request.setUserAccount(userAccount);
            request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
            request.setApplicationName("eSolutions");

            try
            {
                ServiceManagementResponse response = processor.addNewService(request);

                Assertions.assertThat(response.getRequestStatus()).isEqualTo(CoreServicesStatus.SUCCESS);
            }
            catch (ServiceManagementException pmx)
            {
                Assertions.fail(pmx.getMessage());
            }
        }
    }

    @Test public void addNewServiceAsDatacenter()
    {
        Service service = new Service();
        service.setType(ServiceType.DATACENTER);
        service.setRegion(ServiceRegion.DEV);
        service.setName(RandomStringUtils.randomAlphabetic(8));
        service.setStatus(ServiceStatus.ACTIVE);
        service.setDescription("Test Service X");
        service.setPartition(NetworkPartition.DMZ);

        ServiceManagementRequest request = new ServiceManagementRequest();
        request.setService(service);
        request.setRequestInfo(hostInfo);
        request.setServiceId("D1B5D088-32B3-4AA1-9FCF-822CB476B649");
        request.setUserAccount(userAccount);
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setApplicationName("eSolutions");

        try
        {
            ServiceManagementResponse response = processor.addNewService(request);

            Assertions.assertThat(response.getRequestStatus()).isEqualTo(CoreServicesStatus.SUCCESS);
        }
        catch (ServiceManagementException pmx)
        {
            Assertions.fail(pmx.getMessage());
        }
    }

    @Test public void listServicesForPlatforms()
    {
        ServiceManagementRequest request = new ServiceManagementRequest();
        request.setRequestInfo(hostInfo);
        request.setUserAccount(userAccount);
        request.setServiceId("D1B5D088-32B3-4AA1-9FCF-822CB476B649");
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setApplicationName("eSolutions");

        try
        {
            ServiceManagementResponse response = processor.listServices(request);

            Assertions.assertThat(response.getRequestStatus()).isEqualTo(CoreServicesStatus.SUCCESS);
        }
        catch (ServiceManagementException pmx)
        {
            Assertions.fail(pmx.getMessage());
        }
    }

    @Test public void listServicesForDatacenters()
    {
        ServiceManagementRequest request = new ServiceManagementRequest();
        request.setRequestInfo(hostInfo);
        request.setUserAccount(userAccount);
        request.setServiceId("D1B5D088-32B3-4AA1-9FCF-822CB476B649");
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setApplicationName("eSolutions");

        try
        {
            ServiceManagementResponse response = processor.listServices(request);

            Assertions.assertThat(response.getRequestStatus()).isEqualTo(CoreServicesStatus.SUCCESS);
        }
        catch (ServiceManagementException pmx)
        {
            Assertions.fail(pmx.getMessage());
        }
    }

    @Test public void getServiceByAttribute()
    {
        Service service = new Service();
        service.setName("BBFvgNHa");

        ServiceManagementRequest request = new ServiceManagementRequest();
        request.setRequestInfo(hostInfo);
        request.setUserAccount(userAccount);
        request.setServiceId("D1B5D088-32B3-4AA1-9FCF-822CB476B649");
        request.setService(service);
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setApplicationName("eSolutions");

        try
        {
            ServiceManagementResponse response = processor.getServiceByAttribute(request);

            Assertions.assertThat(response.getRequestStatus()).isEqualTo(CoreServicesStatus.SUCCESS);
        }
        catch (ServiceManagementException pmx)
        {
            Assertions.fail(pmx.getMessage());
        }
    }

    @Test public void getServiceDataForPlatform()
    {
        Service platform = new Service();
        platform.setGuid("046119de-9dc0-406d-bf4f-32f32b199f5c");

        ServiceManagementRequest request = new ServiceManagementRequest();
        request.setRequestInfo(hostInfo);
        request.setUserAccount(userAccount);
        request.setServiceId("D1B5D088-32B3-4AA1-9FCF-822CB476B649");
        request.setService(platform);
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setApplicationName("eSolutions");

        try
        {
            ServiceManagementResponse response = processor.getServiceData(request);

            Assertions.assertThat(response.getRequestStatus()).isEqualTo(CoreServicesStatus.SUCCESS);
        }
        catch (ServiceManagementException pmx)
        {
            Assertions.fail(pmx.getMessage());
        }
    }

    @Test public void getServiceDataForDatacenter()
    {
        Service datacenter = new Service();
        datacenter.setGuid("046119de-9dc0-406d-bf4f-32f32b199f5c");

        ServiceManagementRequest request = new ServiceManagementRequest();
        request.setRequestInfo(hostInfo);
        request.setUserAccount(userAccount);
        request.setServiceId("D1B5D088-32B3-4AA1-9FCF-822CB476B649");
        request.setService(datacenter);
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setApplicationName("eSolutions");

        try
        {
            ServiceManagementResponse response = processor.getServiceData(request);

            Assertions.assertThat(response.getRequestStatus()).isEqualTo(CoreServicesStatus.SUCCESS);
        }
        catch (ServiceManagementException pmx)
        {
            Assertions.fail(pmx.getMessage());
        }
    }

    @AfterAll public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
        CoreServicesInitializer.shutdown();
    }
}
