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
import java.util.List;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import java.util.Arrays;
import java.util.ArrayList;

import com.cws.esolutions.security.enums.Role;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.core.processors.dto.Server;
import com.cws.esolutions.core.processors.dto.Project;
import com.cws.esolutions.core.processors.dto.Platform;
import com.cws.esolutions.core.processors.dto.Application;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.core.listeners.CoreServiceInitializer;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.core.processors.dto.ApplicationManagementRequest;
import com.cws.esolutions.core.processors.dto.ApplicationManagementResponse;
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
    private static UserAccount userAccount = new UserAccount();
    private static RequestHostInfo hostInfo = new RequestHostInfo();

    private static final IApplicationManagementProcessor appProcess = new ApplicationManagementProcessorImpl();

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
    public void testAddNewApplication()
    {
        Project project = new Project();
        project.setProjectGuid("0fd1b62d-7361-4165-a7be-27235f6ab540");

        String[] platforms = { "4b6411c2-568d-49b6-91d7-0212a42ae873", "68c12b68-a990-454f-8b4e-52cd2ef25a0a", "84b7f9df-f1e8-45ab-aee5-35b577473ec3" };

        for (int x = 0; x < 3; x++)
        {
            List<Platform> platformList = new ArrayList<>();

            for (String str : platforms)
            {
                Platform platform = new Platform();
                platform.setPlatformGuid(str);

                platformList.add(platform);
            }

            Application app = new Application();
            app.setApplicationGuid(UUID.randomUUID().toString());
            app.setApplicationName("eSolutions");
            app.setApplicationPlatforms(platformList);
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
    }

    @Test
    public void testDeleteApplicationData()
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
    public void testListApplications()
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
    public void testListApplicationsByProject()
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
    public void testGetApplicationData()
    {
        Application app = new Application();
        app.setApplicationGuid("d3da855d-8ce8-4b7d-b14a-0c472f196aff");

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
    public void testApplicationFileRequest()
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
    public void testApplicationFileRequestGetFile()
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
    public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
