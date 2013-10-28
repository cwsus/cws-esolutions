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
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Before;
import java.util.ArrayList;
import org.apache.commons.lang.RandomStringUtils;

import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.dto.UserSecurity;
import com.cws.esolutions.core.processors.dto.Server;
import com.cws.esolutions.core.processors.dto.Project;
import com.cws.esolutions.core.processors.dto.Platform;
import com.cws.esolutions.core.processors.dto.Application;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.core.listeners.CoreServiceInitializer;
import com.cws.esolutions.security.dao.userauth.enums.LoginType;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.security.processors.dto.AuthenticationRequest;
import com.cws.esolutions.security.dao.userauth.enums.AuthenticationType;
import com.cws.esolutions.security.processors.dto.AuthenticationResponse;
import com.cws.esolutions.core.processors.dto.ApplicationManagementRequest;
import com.cws.esolutions.core.processors.dto.ApplicationManagementResponse;
import com.cws.esolutions.security.processors.impl.AuthenticationProcessorImpl;
import com.cws.esolutions.security.processors.interfaces.IAuthenticationProcessor;
import com.cws.esolutions.core.processors.exception.ApplicationManagementException;
import com.cws.esolutions.core.processors.interfaces.IApplicationManagementProcessor;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.processors.impl
 * ApplicationManagementProcessorImplTest.java
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
 * 35033355 @ Jun 4, 2013 11:36:21 AM
 *     Created.
 */
public class ApplicationManagementProcessorImplTest
{
    private UserAccount userAccount = new UserAccount();
    private RequestHostInfo hostInfo = new RequestHostInfo();

    private static final IApplicationManagementProcessor appProcess = new ApplicationManagementProcessorImpl();

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
                        userSecurity.setPassword("Ariana16*");

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
    public final void testAddNewApplication()
    {
        Project project = new Project();
        project.setProjectGuid("f85bcf1c-63e4-46ec-8903-0f74bb49de00");

        Platform platform = new Platform();
        platform.setPlatformGuid("b372bc09-dc19-4a57-a1b6-7fbcfbcbf42a");

        Application app = new Application();
        app.setApplicationGuid(UUID.randomUUID().toString());
        app.setApplicationName("eSolutions");
        app.setApplicationPlatforms(new ArrayList<Platform>(Arrays.asList(platform)));
        app.setApplicationVersion("1.0");
        app.setApplicationCluster("eSolutions");
        app.setApplicationLogsPath("/appvol/ATS70/eSolutions/applogs");
        app.setApplicationProject(project);
        app.setApplicationInstallPath("/appvol/ATS70/eSolutions/eSolutions_web_source-1.0.war");
        app.setPidDirectory("/appvol/ATS70/eSolutions/syslog/");
        app.setScmPath("scm:git:git@github.com:cws-us/cws-esolutions.git");
        app.setJvmName("eSolutions");
        app.setBasePath("/appvol/ATS70/eSolutions");

        ApplicationManagementRequest request = new ApplicationManagementRequest();
        request.setApplication(app);
        request.setServiceId("96E4E53E-FE87-446C-AF03-0F5BC6527B9D");
        request.setRequestInfo(hostInfo);
        request.setUserAccount(userAccount);

        try
        {
            ApplicationManagementResponse response = appProcess.addNewApplication(request);

            Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
        }
        catch (ApplicationManagementException amx)
        {
            Assert.fail(amx.getMessage());
        }
    }

    @Test
    public final void testUpdateApplicationData()
    {

    }

    @Test
    public final void testDeleteApplicationData()
    {
        Application app = new Application();
        app.setApplicationGuid("6625fc8c-09ed-4579-a3d6-eb43d26b679f");

        ApplicationManagementRequest request = new ApplicationManagementRequest();
        request.setApplication(app);
        request.setServiceId("96E4E53E-FE87-446C-AF03-0F5BC6527B9D");
        request.setRequestInfo(hostInfo);
        request.setUserAccount(userAccount);

        try
        {
            ApplicationManagementResponse response = appProcess.deleteApplicationData(request);

            Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
        }
        catch (ApplicationManagementException amx)
        {
            Assert.fail(amx.getMessage());
        }
    }

    @Test
    public final void testListApplications()
    {
        ApplicationManagementRequest request = new ApplicationManagementRequest();
        request.setServiceId("96E4E53E-FE87-446C-AF03-0F5BC6527B9D");
        request.setRequestInfo(hostInfo);
        request.setUserAccount(userAccount);

        try
        {
            ApplicationManagementResponse response = appProcess.listApplications(request);

            Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
        }
        catch (ApplicationManagementException amx)
        {
            Assert.fail(amx.getMessage());
        }
    }

    @Test
    public final void testListApplicationsByProject()
    {
        Project project = new Project();
        project.setProjectGuid("7c2e3991-1b01-47db-9c78-bd9c453bd07c");

        Application app = new Application();
        app.setApplicationProject(project);

        ApplicationManagementRequest request = new ApplicationManagementRequest();
        request.setApplication(app);
        request.setServiceId("96E4E53E-FE87-446C-AF03-0F5BC6527B9D");
        request.setRequestInfo(hostInfo);
        request.setUserAccount(userAccount);

        try
        {
            ApplicationManagementResponse response = appProcess.listApplicationsByProject(request);

            Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
        }
        catch (ApplicationManagementException amx)
        {
            Assert.fail(amx.getMessage());
        }
    }

    @Test
    public final void testGetApplicationData()
    {
        Application app = new Application();
        app.setApplicationGuid("604a6f7f-54af-4013-ab96-98716b4df40e");

        ApplicationManagementRequest request = new ApplicationManagementRequest();
        request.setApplication(app);
        request.setServiceId("96E4E53E-FE87-446C-AF03-0F5BC6527B9D");
        request.setRequestInfo(hostInfo);
        request.setUserAccount(userAccount);

        try
        {
            ApplicationManagementResponse response = appProcess.getApplicationData(request);

            Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
        }
        catch (ApplicationManagementException amx)
        {
            Assert.fail(amx.getMessage());
        }
    }

    @Test
    public final void testApplicationFileRequest()
    {
        Application app = new Application();
        app.setApplicationGuid("b10edcea-23d8-4209-9d94-d5704e8e08bc");

        Server server = new Server();
        server.setServerGuid("B75CCD70-FCB3-43B7-9667-357508DE2B75");

        ApplicationManagementRequest request = new ApplicationManagementRequest();
        request.setApplication(app);
        request.setServer(server);
        request.setServiceId("96E4E53E-FE87-446C-AF03-0F5BC6527B9D");
        request.setRequestInfo(hostInfo);
        request.setUserAccount(userAccount);

        try
        {
            ApplicationManagementResponse response = appProcess.applicationFileRequest(request);

            Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
        }
        catch (ApplicationManagementException amx)
        {
            Assert.fail(amx.getMessage());
        }
    }

    @Test
    public final void testApplicationFileRequestGetFile()
    {
        Application app = new Application();
        app.setApplicationGuid("b10edcea-23d8-4209-9d94-d5704e8e08bc");

        Server server = new Server();
        server.setServerGuid("B75CCD70-FCB3-43B7-9667-357508DE2B75");

        ApplicationManagementRequest request = new ApplicationManagementRequest();
        request.setApplication(app);
        request.setServer(server);
        request.setRequestFile("WEB-INF/web.xml");
        request.setServiceId("96E4E53E-FE87-446C-AF03-0F5BC6527B9D");
        request.setRequestInfo(hostInfo);
        request.setUserAccount(userAccount);

        try
        {
            ApplicationManagementResponse response = appProcess.applicationFileRequest(request);

            Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
        }
        catch (ApplicationManagementException amx)
        {
            Assert.fail(amx.getMessage());
        }
    }

    @After
    public void tearDown() throws Exception
    {
        SecurityServiceInitializer.shutdown();
    }
}
