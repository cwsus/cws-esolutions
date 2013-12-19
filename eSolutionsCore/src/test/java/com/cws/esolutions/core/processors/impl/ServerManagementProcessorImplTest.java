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
 * File: ServerManagementProcessorImplTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
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
import com.cws.esolutions.core.processors.dto.DataCenter;
import com.cws.esolutions.core.processors.enums.ServerType;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.enums.ServerStatus;
import com.cws.esolutions.core.processors.enums.ServiceRegion;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.core.listeners.CoreServiceInitializer;
import com.cws.esolutions.core.processors.enums.NetworkPartition;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.core.processors.dto.ServerManagementRequest;
import com.cws.esolutions.core.processors.dto.ServerManagementResponse;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.core.processors.exception.ServerManagementException;
import com.cws.esolutions.core.processors.interfaces.IServerManagementProcessor;

public class ServerManagementProcessorImplTest
{
    private static UserAccount userAccount = new UserAccount();
    private static RequestHostInfo hostInfo = new RequestHostInfo();

    private static final IServerManagementProcessor processor = new ServerManagementProcessorImpl();

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
    public void addNewServerAsDmgr()
    {
        String[] strings = new String [] { "bc55f443-202b-4f7c-9118-47dd80500ffb", "dac2e765-109e-4385-8563-aab66d6713f9", "fde6d6e9-8bac-4a82-99c6-ef225945d846" };

        for (int x = 0; x < 3; x++)
        {
            String name = RandomStringUtils.randomAlphanumeric(8).toLowerCase();

            DataCenter dataCenter = new DataCenter();
            dataCenter.setDatacenterGuid("de6ab13b-d3f6-4fe3-ba69-03c442df3d74");

            Server server = new Server();
            server.setServerGuid(strings[x]);
            server.setOsName("CentOS");
            server.setDomainName("caspersbox.corp");
            server.setOperIpAddress("192.168.10.60");
            server.setOperHostName(name);
            server.setMgmtIpAddress("192.168.10.160");
            server.setMgmtHostName(name + "-mgt");
            server.setBkIpAddress("172.16.10.60");
            server.setBkHostName(name + "-dmgr-bak");
            server.setNasIpAddress("172.15.10.61");
            server.setNasHostName(name + "-dmgr-nas");
            
            if (x == 0)
            {
                server.setServerRegion(ServiceRegion.DEV);
            }
            else if (x == 1)
            {
                server.setServerRegion(ServiceRegion.QA);
            }
            else if (x == 2)
            {
                server.setServerRegion(ServiceRegion.PRD);
            }

            server.setServerStatus(ServerStatus.ONLINE);
            server.setServerType(ServerType.DMGRSERVER);
            server.setServerComments("dmgr server");
            server.setAssignedEngineer(userAccount);
            server.setCpuType("AMD 1.0 GHz");
            server.setCpuCount(1);
            server.setServerModel("Virtual Server");
            server.setSerialNumber("1YU341");
            server.setInstalledMemory(4096);
            server.setMgrUrl("https://dmgr.myserver.org:18003/console");
            server.setDmgrPort(18003);
            server.setDatacenter(dataCenter);
            server.setNetworkPartition(NetworkPartition.DMZ);

            ServerManagementRequest request = new ServerManagementRequest();
            request.setRequestInfo(hostInfo);
            request.setUserAccount(userAccount);
            request.setServiceId("45F6BC9E-F45C-4E2E-B5BF-04F93C8F512E");
            request.setTargetServer(server);

            try
            {
                ServerManagementResponse response = processor.addNewServer(request);

                Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
            }
            catch (ServerManagementException smx)
            {
                Assert.fail(smx.getMessage());
            }
        }
    }

    @Test
    public void addServerAsDevAppServer()
    {
        Server dmgrServer = new Server();
        dmgrServer.setServerGuid("bc55f443-202b-4f7c-9118-47dd80500ffb");

        for (int x = 0; x < 4; x++)
        {
            String name = RandomStringUtils.randomAlphanumeric(8).toLowerCase();

            DataCenter dataCenter = new DataCenter();
            dataCenter.setDatacenterGuid("de6ab13b-d3f6-4fe3-ba69-03c442df3d74");

            Server server = new Server();
            server.setServerGuid(UUID.randomUUID().toString());
            server.setOsName("CentOS");
            server.setDomainName("caspersbox.corp");
            server.setOperIpAddress("192.168.10.55");
            server.setOperHostName(RandomStringUtils.randomAlphanumeric(8).toLowerCase());
            server.setMgmtIpAddress("192.168.10.155");
            server.setMgmtHostName(name + "-mgt");
            server.setBkIpAddress("172.16.10.55");
            server.setBkHostName(name + "-bak");
            server.setNasIpAddress("172.15.10.55");
            server.setNasHostName(name + "-nas");
            server.setServerRegion(ServiceRegion.DEV);
            server.setServerStatus(ServerStatus.ONLINE);
            server.setServerType(ServerType.APPSERVER);
            server.setServerComments("app server");
            server.setAssignedEngineer(userAccount);
            server.setCpuType("AMD 1.0 GHz");
            server.setCpuCount(1);
            server.setServerModel("Virtual Server");
            server.setSerialNumber("1YU391");
            server.setInstalledMemory(4096);
            server.setOwningDmgr(dmgrServer);
            server.setNetworkPartition(NetworkPartition.DMZ);
            server.setDatacenter(dataCenter);

            ServerManagementRequest request = new ServerManagementRequest();
            request.setRequestInfo(hostInfo);
            request.setUserAccount(userAccount);
            request.setServiceId("45F6BC9E-F45C-4E2E-B5BF-04F93C8F512E");
            request.setTargetServer(server);

            try
            {
                ServerManagementResponse response = processor.addNewServer(request);

                Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
            }
            catch (ServerManagementException smx)
            {
                Assert.fail(smx.getMessage());
            }
        }
    }

    @Test
    public void addServerAsDevWebServer()
    {
        for (int x = 0; x < 4; x++)
        {
            String name = RandomStringUtils.randomAlphanumeric(8).toLowerCase();

            DataCenter dataCenter = new DataCenter();
            dataCenter.setDatacenterGuid("de6ab13b-d3f6-4fe3-ba69-03c442df3d74");

            Server server = new Server();
            server.setServerGuid(UUID.randomUUID().toString());
            server.setOsName("CentOS");
            server.setDomainName("caspersbox.corp");
            server.setOperIpAddress("192.168.10.55");
            server.setOperHostName(RandomStringUtils.randomAlphanumeric(8).toLowerCase());
            server.setMgmtIpAddress("192.168.10.155");
            server.setMgmtHostName(name + "-mgt");
            server.setBkIpAddress("172.16.10.55");
            server.setBkHostName(name + "-bak");
            server.setNasIpAddress("172.15.10.55");
            server.setNasHostName(name + "-nas");
            server.setServerRegion(ServiceRegion.DEV);
            server.setServerStatus(ServerStatus.ONLINE);
            server.setServerType(ServerType.WEBSERVER);
            server.setServerComments("app server");
            server.setAssignedEngineer(userAccount);
            server.setCpuType("AMD 1.0 GHz");
            server.setCpuCount(1);
            server.setServerModel("Virtual Server");
            server.setSerialNumber("1YU391");
            server.setInstalledMemory(4096);
            server.setNetworkPartition(NetworkPartition.DMZ);
            server.setDatacenter(dataCenter);

            ServerManagementRequest request = new ServerManagementRequest();
            request.setRequestInfo(hostInfo);
            request.setUserAccount(userAccount);
            request.setServiceId("45F6BC9E-F45C-4E2E-B5BF-04F93C8F512E");
            request.setTargetServer(server);

            try
            {
                ServerManagementResponse response = processor.addNewServer(request);

                Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
            }
            catch (ServerManagementException smx)
            {
                Assert.fail(smx.getMessage());
            }
        }
    }

    @Test
    public void addServerAsQaAppServer()
    {
        Server dmgrServer = new Server();
        dmgrServer.setServerGuid("dac2e765-109e-4385-8563-aab66d6713f9");

        for (int x = 0; x < 4; x++)
        {
            String name = RandomStringUtils.randomAlphanumeric(8).toLowerCase();

            DataCenter dataCenter = new DataCenter();
            dataCenter.setDatacenterGuid("de6ab13b-d3f6-4fe3-ba69-03c442df3d74");

            Server server = new Server();
            server.setServerGuid(UUID.randomUUID().toString());
            server.setOsName("CentOS");
            server.setDomainName("caspersbox.corp");
            server.setOperIpAddress("192.168.10.55");
            server.setOperHostName(RandomStringUtils.randomAlphanumeric(8).toLowerCase());
            server.setMgmtIpAddress("192.168.10.155");
            server.setMgmtHostName(name + "-mgt");
            server.setBkIpAddress("172.16.10.55");
            server.setBkHostName(name + "-bak");
            server.setNasIpAddress("172.15.10.55");
            server.setNasHostName(name + "-nas");
            server.setServerRegion(ServiceRegion.QA);
            server.setServerStatus(ServerStatus.ONLINE);
            server.setServerType(ServerType.APPSERVER);
            server.setServerComments("app server");
            server.setAssignedEngineer(userAccount);
            server.setCpuType("AMD 1.0 GHz");
            server.setCpuCount(1);
            server.setServerModel("Virtual Server");
            server.setSerialNumber("1YU391");
            server.setInstalledMemory(4096);
            server.setOwningDmgr(dmgrServer);
            server.setNetworkPartition(NetworkPartition.DMZ);
            server.setDatacenter(dataCenter);

            ServerManagementRequest request = new ServerManagementRequest();
            request.setRequestInfo(hostInfo);
            request.setUserAccount(userAccount);
            request.setServiceId("45F6BC9E-F45C-4E2E-B5BF-04F93C8F512E");
            request.setTargetServer(server);

            try
            {
                ServerManagementResponse response = processor.addNewServer(request);

                Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
            }
            catch (ServerManagementException smx)
            {
                Assert.fail(smx.getMessage());
            }
        }
    }

    @Test
    public void addServerAsQaWebServer()
    {
        for (int x = 0; x < 4; x++)
        {
            String name = RandomStringUtils.randomAlphanumeric(8).toLowerCase();

            DataCenter dataCenter = new DataCenter();
            dataCenter.setDatacenterGuid("de6ab13b-d3f6-4fe3-ba69-03c442df3d74");

            Server server = new Server();
            server.setServerGuid(UUID.randomUUID().toString());
            server.setOsName("CentOS");
            server.setDomainName("caspersbox.corp");
            server.setOperIpAddress("192.168.10.55");
            server.setOperHostName(RandomStringUtils.randomAlphanumeric(8).toLowerCase());
            server.setMgmtIpAddress("192.168.10.155");
            server.setMgmtHostName(name + "-mgt");
            server.setBkIpAddress("172.16.10.55");
            server.setBkHostName(name + "-bak");
            server.setNasIpAddress("172.15.10.55");
            server.setNasHostName(name + "-nas");
            server.setServerRegion(ServiceRegion.QA);
            server.setServerStatus(ServerStatus.ONLINE);
            server.setServerType(ServerType.WEBSERVER);
            server.setServerComments("app server");
            server.setAssignedEngineer(userAccount);
            server.setCpuType("AMD 1.0 GHz");
            server.setCpuCount(1);
            server.setServerModel("Virtual Server");
            server.setSerialNumber("1YU391");
            server.setInstalledMemory(4096);
            server.setNetworkPartition(NetworkPartition.DMZ);
            server.setDatacenter(dataCenter);

            ServerManagementRequest request = new ServerManagementRequest();
            request.setRequestInfo(hostInfo);
            request.setUserAccount(userAccount);
            request.setServiceId("45F6BC9E-F45C-4E2E-B5BF-04F93C8F512E");
            request.setTargetServer(server);

            try
            {
                ServerManagementResponse response = processor.addNewServer(request);

                Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
            }
            catch (ServerManagementException smx)
            {
                Assert.fail(smx.getMessage());
            }
        }
    }

    @Test
    public void addServerAsPrdAppServer()
    {
        Server dmgrServer = new Server();
        dmgrServer.setServerGuid("fde6d6e9-8bac-4a82-99c6-ef225945d846");

        for (int x = 0; x < 4; x++)
        {
            String name = RandomStringUtils.randomAlphanumeric(8).toLowerCase();

            DataCenter dataCenter = new DataCenter();
            dataCenter.setDatacenterGuid("de6ab13b-d3f6-4fe3-ba69-03c442df3d74");

            Server server = new Server();
            server.setServerGuid(UUID.randomUUID().toString());
            server.setOsName("CentOS");
            server.setDomainName("caspersbox.corp");
            server.setOperIpAddress("192.168.10.55");
            server.setOperHostName(RandomStringUtils.randomAlphanumeric(8).toLowerCase());
            server.setMgmtIpAddress("192.168.10.155");
            server.setMgmtHostName(name + "-mgt");
            server.setBkIpAddress("172.16.10.55");
            server.setBkHostName(name + "-bak");
            server.setNasIpAddress("172.15.10.55");
            server.setNasHostName(name + "-nas");
            server.setServerRegion(ServiceRegion.PRD);
            server.setServerStatus(ServerStatus.ONLINE);
            server.setServerType(ServerType.APPSERVER);
            server.setServerComments("app server");
            server.setAssignedEngineer(userAccount);
            server.setCpuType("AMD 1.0 GHz");
            server.setCpuCount(1);
            server.setServerModel("Virtual Server");
            server.setSerialNumber("1YU391");
            server.setInstalledMemory(4096);
            server.setOwningDmgr(dmgrServer);
            server.setNetworkPartition(NetworkPartition.DMZ);
            server.setDatacenter(dataCenter);

            ServerManagementRequest request = new ServerManagementRequest();
            request.setRequestInfo(hostInfo);
            request.setUserAccount(userAccount);
            request.setServiceId("45F6BC9E-F45C-4E2E-B5BF-04F93C8F512E");
            request.setTargetServer(server);

            try
            {
                ServerManagementResponse response = processor.addNewServer(request);

                Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
            }
            catch (ServerManagementException smx)
            {
                Assert.fail(smx.getMessage());
            }
        }
    }

    @Test
    public void addServerAsPrdWebServer()
    {
        for (int x = 0; x < 4; x++)
        {
            String name = RandomStringUtils.randomAlphanumeric(8).toLowerCase();

            DataCenter dataCenter = new DataCenter();
            dataCenter.setDatacenterGuid("de6ab13b-d3f6-4fe3-ba69-03c442df3d74");

            Server server = new Server();
            server.setServerGuid(UUID.randomUUID().toString());
            server.setOsName("CentOS");
            server.setDomainName("caspersbox.corp");
            server.setOperIpAddress("192.168.10.55");
            server.setOperHostName(RandomStringUtils.randomAlphanumeric(8).toLowerCase());
            server.setMgmtIpAddress("192.168.10.155");
            server.setMgmtHostName(name + "-mgt");
            server.setBkIpAddress("172.16.10.55");
            server.setBkHostName(name + "-bak");
            server.setNasIpAddress("172.15.10.55");
            server.setNasHostName(name + "-nas");
            server.setServerRegion(ServiceRegion.PRD);
            server.setServerStatus(ServerStatus.ONLINE);
            server.setServerType(ServerType.WEBSERVER);
            server.setServerComments("app server");
            server.setAssignedEngineer(userAccount);
            server.setCpuType("AMD 1.0 GHz");
            server.setCpuCount(1);
            server.setServerModel("Virtual Server");
            server.setSerialNumber("1YU391");
            server.setInstalledMemory(4096);
            server.setNetworkPartition(NetworkPartition.DMZ);
            server.setDatacenter(dataCenter);

            ServerManagementRequest request = new ServerManagementRequest();
            request.setRequestInfo(hostInfo);
            request.setUserAccount(userAccount);
            request.setServiceId("45F6BC9E-F45C-4E2E-B5BF-04F93C8F512E");
            request.setTargetServer(server);

            try
            {
                ServerManagementResponse response = processor.addNewServer(request);

                Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
            }
            catch (ServerManagementException smx)
            {
                Assert.fail(smx.getMessage());
            }
        }
    }

    @Test
    public void addServerAsMasterDnsServer()
    {
        String name = RandomStringUtils.randomAlphanumeric(8).toLowerCase();

        DataCenter dataCenter = new DataCenter();
        dataCenter.setDatacenterGuid("de6ab13b-d3f6-4fe3-ba69-03c442df3d74");

        Server server = new Server();
        server.setServerGuid(UUID.randomUUID().toString());
        server.setOsName("CentOS");
        server.setDomainName("caspersbox.corp");
        server.setOperIpAddress("192.168.10.55");
        server.setOperHostName(RandomStringUtils.randomAlphanumeric(8).toLowerCase());
        server.setMgmtIpAddress("192.168.10.155");
        server.setMgmtHostName(name + "-mgt");
        server.setBkIpAddress("172.16.10.55");
        server.setBkHostName(name + "-bak");
        server.setNasIpAddress("172.15.10.55");
        server.setNasHostName(name + "-nas");
        server.setServerRegion(ServiceRegion.PRD);
        server.setServerStatus(ServerStatus.ONLINE);
        server.setServerType(ServerType.DNSMASTER);
        server.setServerComments("app server");
        server.setAssignedEngineer(userAccount);
        server.setCpuType("AMD 1.0 GHz");
        server.setCpuCount(1);
        server.setServerModel("Virtual Server");
        server.setSerialNumber("1YU391");
        server.setInstalledMemory(4096);
        server.setNetworkPartition(NetworkPartition.DMZ);
        server.setDatacenter(dataCenter);

        ServerManagementRequest request = new ServerManagementRequest();
        request.setRequestInfo(hostInfo);
        request.setUserAccount(userAccount);
        request.setServiceId("45F6BC9E-F45C-4E2E-B5BF-04F93C8F512E");
        request.setTargetServer(server);

        try
        {
            ServerManagementResponse response = processor.addNewServer(request);

            Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
        }
        catch (ServerManagementException smx)
        {
            Assert.fail(smx.getMessage());
        }
    }

    @Test
    public void addServerAsSlaveDnsServer()
    {
        for (int x = 0; x < 4; x++)
        {
            String name = RandomStringUtils.randomAlphanumeric(8).toLowerCase();

            DataCenter dataCenter = new DataCenter();
            dataCenter.setDatacenterGuid("de6ab13b-d3f6-4fe3-ba69-03c442df3d74");

            Server server = new Server();
            server.setServerGuid(UUID.randomUUID().toString());
            server.setOsName("CentOS");
            server.setDomainName("caspersbox.corp");
            server.setOperIpAddress("192.168.10.55");
            server.setOperHostName(RandomStringUtils.randomAlphanumeric(8).toLowerCase());
            server.setMgmtIpAddress("192.168.10.155");
            server.setMgmtHostName(name + "-mgt");
            server.setBkIpAddress("172.16.10.55");
            server.setBkHostName(name + "-bak");
            server.setNasIpAddress("172.15.10.55");
            server.setNasHostName(name + "-nas");
            server.setServerRegion(ServiceRegion.PRD);
            server.setServerStatus(ServerStatus.ONLINE);
            server.setServerType(ServerType.DNSSLAVE);
            server.setServerComments("app server");
            server.setAssignedEngineer(userAccount);
            server.setCpuType("AMD 1.0 GHz");
            server.setCpuCount(1);
            server.setServerModel("Virtual Server");
            server.setSerialNumber("1YU391");
            server.setInstalledMemory(4096);
            server.setNetworkPartition(NetworkPartition.DMZ);
            server.setDatacenter(dataCenter);

            ServerManagementRequest request = new ServerManagementRequest();
            request.setRequestInfo(hostInfo);
            request.setUserAccount(userAccount);
            request.setServiceId("45F6BC9E-F45C-4E2E-B5BF-04F93C8F512E");
            request.setTargetServer(server);

            try
            {
                ServerManagementResponse response = processor.addNewServer(request);

                Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
            }
            catch (ServerManagementException smx)
            {
                Assert.fail(smx.getMessage());
            }
        }
    }

    @Test
    public void addNewServerAsDevMqServer()
    {
        String name = RandomStringUtils.randomAlphanumeric(8).toLowerCase();

        DataCenter dataCenter = new DataCenter();
        dataCenter.setDatacenterGuid("de6ab13b-d3f6-4fe3-ba69-03c442df3d74");

        for (int x = 0; x < 2; x++)
        {
            Server server = new Server();
            server.setServerGuid(UUID.randomUUID().toString());
            server.setOsName("CentOS");
            server.setDomainName("caspersbox.corp");
            server.setOperIpAddress("192.168.10.55");
            server.setOperHostName(RandomStringUtils.randomAlphanumeric(8).toLowerCase());
            server.setMgmtIpAddress("192.168.10.155");
            server.setMgmtHostName(name + "-mgt");
            server.setBkIpAddress("172.16.10.55");
            server.setBkHostName(name + "-bak");
            server.setNasIpAddress("172.15.10.55");
            server.setNasHostName(name + "-nas");
            server.setServerRegion(ServiceRegion.DEV);
            server.setServerStatus(ServerStatus.ONLINE);
            server.setServerType(ServerType.MQSERVER);
            server.setServerComments("app server");
            server.setAssignedEngineer(userAccount);
            server.setCpuType("AMD 1.0 GHz");
            server.setCpuCount(1);
            server.setServerModel("Virtual Server");
            server.setSerialNumber("1YU391");
            server.setInstalledMemory(4096);
            server.setNetworkPartition(NetworkPartition.DMZ);
            server.setDatacenter(dataCenter);
    
            ServerManagementRequest request = new ServerManagementRequest();
            request.setRequestInfo(hostInfo);
            request.setUserAccount(userAccount);
            request.setServiceId("45F6BC9E-F45C-4E2E-B5BF-04F93C8F512E");
            request.setTargetServer(server);
    
            try
            {
                ServerManagementResponse response = processor.addNewServer(request);
    
                Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
            }
            catch (ServerManagementException smx)
            {
                Assert.fail(smx.getMessage());
            }
        }
    }

    @Test
    public void addNewServerAsQaMqServer()
    {
        String name = RandomStringUtils.randomAlphanumeric(8).toLowerCase();

        DataCenter dataCenter = new DataCenter();
        dataCenter.setDatacenterGuid("de6ab13b-d3f6-4fe3-ba69-03c442df3d74");

        for (int x = 0; x < 2; x++)
        {
            Server server = new Server();
            server.setServerGuid(UUID.randomUUID().toString());
            server.setOsName("CentOS");
            server.setDomainName("caspersbox.corp");
            server.setOperIpAddress("192.168.10.55");
            server.setOperHostName(RandomStringUtils.randomAlphanumeric(8).toLowerCase());
            server.setMgmtIpAddress("192.168.10.155");
            server.setMgmtHostName(name + "-mgt");
            server.setBkIpAddress("172.16.10.55");
            server.setBkHostName(name + "-bak");
            server.setNasIpAddress("172.15.10.55");
            server.setNasHostName(name + "-nas");
            server.setServerRegion(ServiceRegion.QA);
            server.setServerStatus(ServerStatus.ONLINE);
            server.setServerType(ServerType.MQSERVER);
            server.setServerComments("app server");
            server.setAssignedEngineer(userAccount);
            server.setCpuType("AMD 1.0 GHz");
            server.setCpuCount(1);
            server.setServerModel("Virtual Server");
            server.setSerialNumber("1YU391");
            server.setInstalledMemory(4096);
            server.setNetworkPartition(NetworkPartition.DMZ);
            server.setDatacenter(dataCenter);
    
            ServerManagementRequest request = new ServerManagementRequest();
            request.setRequestInfo(hostInfo);
            request.setUserAccount(userAccount);
            request.setServiceId("45F6BC9E-F45C-4E2E-B5BF-04F93C8F512E");
            request.setTargetServer(server);
    
            try
            {
                ServerManagementResponse response = processor.addNewServer(request);
    
                Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
            }
            catch (ServerManagementException smx)
            {
                Assert.fail(smx.getMessage());
            }
        }
    }

    @Test
    public void addNewServerAsPrdMqServer()
    {
        String name = RandomStringUtils.randomAlphanumeric(8).toLowerCase();

        DataCenter dataCenter = new DataCenter();
        dataCenter.setDatacenterGuid("de6ab13b-d3f6-4fe3-ba69-03c442df3d74");

        for (int x = 0; x < 2; x++)
        {
            Server server = new Server();
            server.setServerGuid(UUID.randomUUID().toString());
            server.setOsName("CentOS");
            server.setDomainName("caspersbox.corp");
            server.setOperIpAddress("192.168.10.55");
            server.setOperHostName(RandomStringUtils.randomAlphanumeric(8).toLowerCase());
            server.setMgmtIpAddress("192.168.10.155");
            server.setMgmtHostName(name + "-mgt");
            server.setBkIpAddress("172.16.10.55");
            server.setBkHostName(name + "-bak");
            server.setNasIpAddress("172.15.10.55");
            server.setNasHostName(name + "-nas");
            server.setServerRegion(ServiceRegion.PRD);
            server.setServerStatus(ServerStatus.ONLINE);
            server.setServerType(ServerType.MQSERVER);
            server.setServerComments("app server");
            server.setAssignedEngineer(userAccount);
            server.setCpuType("AMD 1.0 GHz");
            server.setCpuCount(1);
            server.setServerModel("Virtual Server");
            server.setSerialNumber("1YU391");
            server.setInstalledMemory(4096);
            server.setNetworkPartition(NetworkPartition.DMZ);
            server.setDatacenter(dataCenter);
    
            ServerManagementRequest request = new ServerManagementRequest();
            request.setRequestInfo(hostInfo);
            request.setUserAccount(userAccount);
            request.setServiceId("45F6BC9E-F45C-4E2E-B5BF-04F93C8F512E");
            request.setTargetServer(server);
    
            try
            {
                ServerManagementResponse response = processor.addNewServer(request);
    
                Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
            }
            catch (ServerManagementException smx)
            {
                Assert.fail(smx.getMessage());
            }
        }
    }

    @Test
    public void addNewServerAsVmgr()
    {
        DataCenter dataCenter = new DataCenter();
        dataCenter.setDatacenterGuid("de6ab13b-d3f6-4fe3-ba69-03c442df3d74");

        Server server = new Server();
        server.setServerGuid(UUID.randomUUID().toString());
        server.setOsName("CentOS");
        server.setDomainName("caspersbox.corp");
        server.setOperIpAddress("192.168.10.250");
        server.setOperHostName("caspersb-vbox1");
        server.setMgmtIpAddress("192.168.11.250");
        server.setMgmtHostName("caspersb-vbox1-mgt");
        server.setBkIpAddress("172.16.10.55");
        server.setBkHostName("caspersb-vbox1-bak");
        server.setNasIpAddress("172.15.10.55");
        server.setNasHostName("caspersb-vbox1-nas");
        server.setServerRegion(ServiceRegion.PRD);
        server.setServerStatus(ServerStatus.ONLINE);
        server.setServerType(ServerType.VIRTUALHOST);
        server.setServerComments("app server");
        server.setAssignedEngineer(userAccount);
        server.setCpuType("AMD 1.0 GHz");
        server.setCpuCount(1);
        server.setServerModel("Virtual Server");
        server.setSerialNumber("1YU391");
        server.setMgrUrl("https://192.168.10.250:10981/index.html");
        server.setInstalledMemory(4096);
        server.setNetworkPartition(NetworkPartition.DMZ);
        server.setDatacenter(dataCenter);

        ServerManagementRequest request = new ServerManagementRequest();
        request.setRequestInfo(hostInfo);
        request.setUserAccount(userAccount);
        request.setServiceId("45F6BC9E-F45C-4E2E-B5BF-04F93C8F512E");
        request.setTargetServer(server);

        try
        {
            ServerManagementResponse response = processor.addNewServer(request);

            Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
        }
        catch (ServerManagementException smx)
        {
            Assert.fail(smx.getMessage());
        }
    }

    @Test
    public void modifyServer()
    {
        Server server = new Server();
        server.setAssignedEngineer(userAccount);
        server.setBkHostName("bak.myserver.org");
        server.setBkIpAddress("1.2.3.4");
        server.setMgmtHostName("mgmt.myserver.org");
        server.setMgmtIpAddress("1.2.3.4");
        server.setNasHostName("nas.myserver.org");
        server.setNasIpAddress("1.2.3.4");
        server.setNatAddress("1.2.3.4");
        server.setOperHostName("oper.myserver.org");
        server.setOperIpAddress("4.3.4.1");
        server.setOsName("Linux");
        server.setServerComments("other comments");
        server.setServerRegion(ServiceRegion.DEV);
        server.setServerStatus(ServerStatus.ONLINE);
        server.setServerType(ServerType.APPSERVER);
        server.setServerGuid("9EADCA3A-A1A3-4986-A71C-6FE8924C9443");

        ServerManagementRequest request = new ServerManagementRequest();
        request.setRequestInfo(hostInfo);
        request.setUserAccount(userAccount);
        request.setServiceId("45F6BC9E-F45C-4E2E-B5BF-04F93C8F512E");
        request.setTargetServer(server);

        try
        {
            ServerManagementResponse response = processor.updateServerData(request);

            Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
        }
        catch (ServerManagementException smx)
        {
            Assert.fail(smx.getMessage());
        }
    }

    @Test
    public void removeServer()
    {
        Server server = new Server();
        server.setServerGuid("9EADCA3A-A1A3-4986-A71C-6FE8924C9443");

        ServerManagementRequest request = new ServerManagementRequest();
        request.setRequestInfo(hostInfo);
        request.setUserAccount(userAccount);
        request.setServiceId("45F6BC9E-F45C-4E2E-B5BF-04F93C8F512E");
        request.setTargetServer(server);

        try
        {
            ServerManagementResponse response = processor.updateServerData(request);

            Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
        }
        catch (ServerManagementException smx)
        {
            Assert.fail(smx.getMessage());
        }
    }

    @Test
    public void listServersWithDmgr()
    {
        Server server = new Server();
        server.setServerGuid("cbddebc8-9bdb-4a6b-8c70-b39959ace0ce");

        ServerManagementRequest request = new ServerManagementRequest();
        request.setRequestInfo(hostInfo);
        request.setUserAccount(userAccount);
        request.setServiceId("45F6BC9E-F45C-4E2E-B5BF-04F93C8F512E");
        request.setSourceServer(server);

        try
        {
            ServerManagementResponse response = processor.getServerData(request);

            Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
        }
        catch (ServerManagementException smx)
        {
            Assert.fail(smx.getMessage());
        }
    }

    @Test
    public void getServerInfo()
    {
        Server server = new Server();
        server.setOperHostName("caspersb-aws1");

        ServerManagementRequest request = new ServerManagementRequest();
        request.setRequestInfo(hostInfo);
        request.setUserAccount(userAccount);
        request.setServiceId("45F6BC9E-F45C-4E2E-B5BF-04F93C8F512E");
        request.setSourceServer(server);

        try
        {
            ServerManagementResponse response = processor.updateServerData(request);

            Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
        }
        catch (ServerManagementException smx)
        {
            Assert.fail(smx.getMessage());
        }
    }

    @Test
    public void getDmgrServerInfo()
    {
        Server server = new Server();
        server.setServerGuid("40d13d8a-da67-4f95-a3ac-47954fc734c8");

        ServerManagementRequest request = new ServerManagementRequest();
        request.setRequestInfo(hostInfo);
        request.setUserAccount(userAccount);
        request.setServiceId("45F6BC9E-F45C-4E2E-B5BF-04F93C8F512E");
        request.setSourceServer(server);

        try
        {
            ServerManagementResponse response = processor.updateServerData(request);

            Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
        }
        catch (ServerManagementException smx)
        {
            Assert.fail(smx.getMessage());
        }
    }

    @Test
    public void getVmgrServerInfo()
    {
        Server server = new Server();
        server.setServerGuid("3ce8a22f-637e-4efc-bd4d-740ccfd6acea");

        ServerManagementRequest request = new ServerManagementRequest();
        request.setRequestInfo(hostInfo);
        request.setUserAccount(userAccount);
        request.setServiceId("45F6BC9E-F45C-4E2E-B5BF-04F93C8F512E");
        request.setSourceServer(server);

        try
        {
            ServerManagementResponse response = processor.updateServerData(request);

            Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
        }
        catch (ServerManagementException smx)
        {
            Assert.fail(smx.getMessage());
        }
    }

    @After
    public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
