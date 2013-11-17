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

import java.util.UUID;
import org.junit.Test;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.apache.commons.lang.RandomStringUtils;

import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.dto.UserSecurity;
import com.cws.esolutions.core.processors.dto.Server;
import com.cws.esolutions.core.processors.dto.DataCenter;
import com.cws.esolutions.core.processors.enums.ServerType;
import com.cws.esolutions.core.processors.enums.ServerStatus;
import com.cws.esolutions.core.processors.enums.ServiceRegion;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.core.listeners.CoreServiceInitializer;
import com.cws.esolutions.security.dao.userauth.enums.LoginType;
import com.cws.esolutions.core.processors.enums.NetworkPartition;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.core.processors.dto.ServerManagementRequest;
import com.cws.esolutions.core.processors.dto.ServerManagementResponse;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.security.processors.dto.AuthenticationRequest;
import com.cws.esolutions.security.dao.userauth.enums.AuthenticationType;
import com.cws.esolutions.security.processors.dto.AuthenticationResponse;
import com.cws.esolutions.core.processors.exception.ServerManagementException;
import com.cws.esolutions.security.processors.impl.AuthenticationProcessorImpl;
import com.cws.esolutions.core.processors.interfaces.IServerManagementProcessor;
import com.cws.esolutions.security.processors.interfaces.IAuthenticationProcessor;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.processors.impl
 * ServiceManagementImplTest.java
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
 * 35033355 @ Apr 5, 2013 3:30:30 PM
 *     Created.
 */
public class ServerManagementProcessorImplTest
{
    private UserAccount userAccount = new UserAccount();
    private RequestHostInfo hostInfo = new RequestHostInfo();

    private static final IServerManagementProcessor processor = new ServerManagementProcessorImpl();

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
                userRequest.setApplicationName("esolutions");
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
                        userSecurity.setPassword("Ariana21*");

                        AuthenticationRequest passRequest = new AuthenticationRequest();
                        passRequest.setApplicationName("esolutions");
                        passRequest.setAuthType(AuthenticationType.LOGIN);
                        passRequest.setLoginType(LoginType.PASSWORD);
                        passRequest.setUserAccount(authUser);
                        passRequest.setUserSecurity(userSecurity);
                        passRequest.setApplicationId("B760E92F-827A-42E7-9E8D-64334657BA83");

                        AuthenticationResponse passResponse = agentAuth.processAgentLogon(passRequest);

                        if (passResponse.getRequestStatus() == SecurityRequestStatus.SUCCESS)
                        {
                            userAccount = passResponse.getUserAccount();
                            userAccount.setSessionId(RandomStringUtils.randomAlphanumeric(32));
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
    public final void addNewServerAsDmgr()
    {
        String[] strings = new String [] { "bc55f443-202b-4f7c-9118-47dd80500ffb", "dac2e765-109e-4385-8563-aab66d6713f9", "fde6d6e9-8bac-4a82-99c6-ef225945d846" };

        for (int x = 0; x < 3; x++)
        {
            String name = RandomStringUtils.randomAlphanumeric(8).toLowerCase();

            DataCenter dataCenter = new DataCenter();
            dataCenter.setDatacenterGuid("89c15991-cc05-40f8-8d3e-304d7893713f");

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
            server.setAssignedEngineer("Kevin Huntly");
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
    public final void addServerAsDevAppServer()
    {
        for (int x = 0; x < 4; x++)
        {
            String name = RandomStringUtils.randomAlphanumeric(8).toLowerCase();

            DataCenter dataCenter = new DataCenter();
            dataCenter.setDatacenterGuid("89c15991-cc05-40f8-8d3e-304d7893713f");

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
            server.setAssignedEngineer("Kevin Huntly");
            server.setCpuType("AMD 1.0 GHz");
            server.setCpuCount(1);
            server.setServerModel("Virtual Server");
            server.setSerialNumber("1YU391");
            server.setInstalledMemory(4096);
            server.setOwningDmgr("bc55f443-202b-4f7c-9118-47dd80500ffb");
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
    public final void addServerAsDevWebServer()
    {
        for (int x = 0; x < 4; x++)
        {
            String name = RandomStringUtils.randomAlphanumeric(8).toLowerCase();

            DataCenter dataCenter = new DataCenter();
            dataCenter.setDatacenterGuid("89c15991-cc05-40f8-8d3e-304d7893713f");

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
            server.setAssignedEngineer("Kevin Huntly");
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
    public final void addServerAsQaAppServer()
    {
        for (int x = 0; x < 4; x++)
        {
            String name = RandomStringUtils.randomAlphanumeric(8).toLowerCase();

            DataCenter dataCenter = new DataCenter();
            dataCenter.setDatacenterGuid("89c15991-cc05-40f8-8d3e-304d7893713f");

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
            server.setAssignedEngineer("Kevin Huntly");
            server.setCpuType("AMD 1.0 GHz");
            server.setCpuCount(1);
            server.setServerModel("Virtual Server");
            server.setSerialNumber("1YU391");
            server.setInstalledMemory(4096);
            server.setOwningDmgr("dac2e765-109e-4385-8563-aab66d6713f9");
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
    public final void addServerAsQaWebServer()
    {
        for (int x = 0; x < 4; x++)
        {
            String name = RandomStringUtils.randomAlphanumeric(8).toLowerCase();

            DataCenter dataCenter = new DataCenter();
            dataCenter.setDatacenterGuid("89c15991-cc05-40f8-8d3e-304d7893713f");

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
            server.setAssignedEngineer("Kevin Huntly");
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
    public final void addServerAsPrdAppServer()
    {
        for (int x = 0; x < 4; x++)
        {
            String name = RandomStringUtils.randomAlphanumeric(8).toLowerCase();

            DataCenter dataCenter = new DataCenter();
            dataCenter.setDatacenterGuid("89c15991-cc05-40f8-8d3e-304d7893713f");

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
            server.setAssignedEngineer("Kevin Huntly");
            server.setCpuType("AMD 1.0 GHz");
            server.setCpuCount(1);
            server.setServerModel("Virtual Server");
            server.setSerialNumber("1YU391");
            server.setInstalledMemory(4096);
            server.setOwningDmgr("fde6d6e9-8bac-4a82-99c6-ef225945d846");
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
    public final void addServerAsPrdWebServer()
    {
        for (int x = 0; x < 4; x++)
        {
            String name = RandomStringUtils.randomAlphanumeric(8).toLowerCase();

            DataCenter dataCenter = new DataCenter();
            dataCenter.setDatacenterGuid("89c15991-cc05-40f8-8d3e-304d7893713f");

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
            server.setAssignedEngineer("Kevin Huntly");
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
    public final void addServerAsMasterDnsServer()
    {
        String name = RandomStringUtils.randomAlphanumeric(8).toLowerCase();

        DataCenter dataCenter = new DataCenter();
        dataCenter.setDatacenterGuid("89c15991-cc05-40f8-8d3e-304d7893713f");

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
        server.setAssignedEngineer("Kevin Huntly");
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
    public final void addServerAsSlaveDnsServer()
    {
        for (int x = 0; x < 4; x++)
        {
            String name = RandomStringUtils.randomAlphanumeric(8).toLowerCase();

            DataCenter dataCenter = new DataCenter();
            dataCenter.setDatacenterGuid("89c15991-cc05-40f8-8d3e-304d7893713f");

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
            server.setAssignedEngineer("Kevin Huntly");
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
    public final void addNewServerAsMqServer()
    {
        String name = RandomStringUtils.randomAlphanumeric(8).toLowerCase();

        DataCenter dataCenter = new DataCenter();
        dataCenter.setDatacenterGuid("89c15991-cc05-40f8-8d3e-304d7893713f");

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
        server.setAssignedEngineer("Kevin Huntly");
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
    public final void addNewServerAsVmgr()
    {
        DataCenter dataCenter = new DataCenter();
        dataCenter.setDatacenterGuid("89c15991-cc05-40f8-8d3e-304d7893713f");

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
        server.setAssignedEngineer("Kevin Huntly");
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
    public final void modifyServer()
    {
        Server server = new Server();
        server.setAssignedEngineer("test user");
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
    public final void removeServer()
    {
        Server server = new Server();
        server.setAssignedEngineer("kevin huntly");
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
        server.setServerComments("my new comments");
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
    public final void listServersWithDmgr()
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
    public final void getServerInfo()
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
    public final void getDmgrServerInfo()
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
    public final void getVmgrServerInfo()
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

    @Test
    public final void testRunNetstatCheck()
    {
        Server source = new Server();
        source.setOperHostName("localhost");
        source.setOperIpAddress("127.0.0.1");

        ServerManagementRequest request = new ServerManagementRequest();
        request.setRequestInfo(hostInfo);
        request.setUserAccount(userAccount);
        request.setSourceServer(source);
        request.setPortNumber(61616);

        try
        {
            ServerManagementResponse response = processor.runNetstatCheck(request);

            Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
        }
        catch (ServerManagementException scx)
        {
            Assert.fail(scx.getMessage());
        }
    }

    @Test
    public final void testRunTelnetCheck()
    {
        Server source = new Server();
        source.setOperHostName("localhost");
        source.setOperIpAddress("127.0.0.1");

        Server target = new Server();
        target.setOperHostName("chibcarray.us.hsbc");

        ServerManagementRequest request = new ServerManagementRequest();
        request.setRequestInfo(hostInfo);
        request.setUserAccount(userAccount);
        request.setSourceServer(source);
        request.setTargetServer(target);
        request.setPortNumber(8080);

        try
        {
            ServerManagementResponse response = processor.runTelnetCheck(request);

            Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
        }
        catch (ServerManagementException scx)
        {
            Assert.fail(scx.getMessage());
        }
    }

    @Test
    public final void testRunRemoteDateCheck()
    {
        Server target = new Server();
        target.setOperHostName("localhost");
        target.setOperIpAddress("127.0.0.1");

        ServerManagementRequest request = new ServerManagementRequest();
        request.setRequestInfo(hostInfo);
        request.setUserAccount(userAccount);
        request.setTargetServer(target);

        try
        {
            ServerManagementResponse response = processor.runRemoteDateCheck(request);

            Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
        }
        catch (ServerManagementException scx)
        {
            Assert.fail(scx.getMessage());
        }
    }

    @After
    public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
