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
 * File: ApplicationManagementProcessorImplTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.io.File;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.TestInstance;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.enums.SecurityUserRole;
import com.cws.esolutions.core.processors.dto.Application;
import com.cws.esolutions.core.enums.CoreServicesStatus;
import com.cws.esolutions.core.listeners.CoreServicesInitializer;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.core.processors.dto.ApplicationManagementRequest;
import com.cws.esolutions.core.processors.dto.ApplicationManagementResponse;
import com.cws.esolutions.core.processors.dto.Platform;
import com.cws.esolutions.core.processors.enums.DeploymentType;
import com.cws.esolutions.core.processors.exception.ApplicationManagementException;
import com.cws.esolutions.core.processors.interfaces.IApplicationManagementProcessor;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ApplicationManagementProcessorImplTest
{
    private static UserAccount userAccount = new UserAccount();
    private static RequestHostInfo hostInfo = new RequestHostInfo();

    private static final IApplicationManagementProcessor processor = new ApplicationManagementProcessorImpl();

    @BeforeAll public void setUp()
    {
        hostInfo.setHostAddress("junit");
        hostInfo.setHostName("junit");

        userAccount.setStatus(LoginStatus.SUCCESS);
        userAccount.setGuid("98a7414b-e4d8-40f3-a8a5-f516cde049c3");
        userAccount.setUsername("khuntly");
        userAccount.setUserRole(SecurityUserRole.SITE_ADMIN);

        try
        {
            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/logging/logging.xml", false);
            CoreServicesInitializer.initializeService("eSolutionsCore/config/ServiceConfig.xml", "eSolutionsCore/logging/logging.xml", true, true);
        }
        catch (final Exception ex)
        {
            Assertions.fail(ex.getMessage());

            System.exit(-1);
        }
    }

    @Test public void addNewApplication()
    {
    	Platform platform = new Platform();
    	platform.setPlatformGuid("7f73819d-7963-4051-9c6d-aa6d82b95382");

        Application application = new Application();
        application.setName("My Application Name");
        application.setVersion("1.0-SNAPSHOT");
        application.setInstallPath("/opt/cws/eSolutions");
        application.setPackageLocation("/opt/installs");
        application.setPackageInstaller("/opt/installs/eSolutions.app");
        application.setInstallerOptions("none");
        application.setLogsPath(new File("/opt/cws/logs"));
        application.setPlatform(platform);

        ApplicationManagementRequest request = new ApplicationManagementRequest();
        request.setApplication(application);
        request.setServiceId("96E4E53E-FE87-446C-AF03-0F5BC6527B9D");
        request.setRequestInfo(hostInfo);
        request.setUserAccount(userAccount);
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setApplicationName("eSolutions");

        try
        {
            ApplicationManagementResponse response = processor.addNewApplication(request);

            Assertions.assertThat(response.getRequestStatus()).isEqualTo(CoreServicesStatus.SUCCESS);
        }
        catch (final ApplicationManagementException amx)
        {
            Assertions.fail(amx.getMessage());
        }
    }

    @Test public void updateApplicationData()
    {
    	Platform platform = new Platform();
    	platform.setPlatformGuid("815eff28-faab-40a2-9b0a-cdd95ead1e58");

        Application app = new Application();
        app.setGuid("74b2cfad-ee63-486b-a0a4-ac862ef2e876");
        app.setBasePath(new File("/opt/cws/eSolutions"));
        app.setBinaries(new File("/opt/cws/installer"));
        app.setClusterName("cluster1");
        app.setDeploymentType(DeploymentType.APP);
        app.setInstallerOptions("none");
        app.setInstallPath("/opt/cws/eSolutions");
        app.setIsScmEnabled(true);
        app.setJvmName("AppSrv01");
        app.setLogsPath(new File("/opt/cws/logs"));
        app.setName("My Application Name");
        app.setPackageInstaller("/opt/installs/eSolutions.app");
        app.setPackageLocation("/opt/installs");
        app.setPidDirectory("/run/esolutions/app.pid");
        app.setPlatform(platform);
        app.setScmPath("/");
        app.setVersion("1.0-SNAPSHOT");

        ApplicationManagementRequest request = new ApplicationManagementRequest();
        request.setApplication(app);
        request.setServiceId("96E4E53E-FE87-446C-AF03-0F5BC6527B9D");
        request.setRequestInfo(hostInfo);
        request.setUserAccount(userAccount);
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setApplicationName("eSolutions");

        try
        {
            ApplicationManagementResponse response = processor.updateApplicationData(request);

            Assertions.assertThat(response.getRequestStatus()).isEqualTo(CoreServicesStatus.SUCCESS);
        }
        catch (final ApplicationManagementException amx)
        {
            Assertions.fail(amx.getMessage());
        }
    }

    @Test public void deleteApplicationData()
    {
        Application app = new Application();
        app.setGuid("6625fc8c-09ed-4579-a3d6-eb43d26b679f");

        ApplicationManagementRequest request = new ApplicationManagementRequest();
        request.setApplication(app);
        request.setServiceId("96E4E53E-FE87-446C-AF03-0F5BC6527B9D");
        request.setRequestInfo(hostInfo);
        request.setUserAccount(userAccount);
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setApplicationName("eSolutions");

        try
        {
            ApplicationManagementResponse response = processor.deleteApplicationData(request);

            Assertions.assertThat(response.getRequestStatus()).isEqualTo(CoreServicesStatus.SUCCESS);
        }
        catch (final ApplicationManagementException amx)
        {
            Assertions.fail(amx.getMessage());
        }
    }

    @Test public void listApplications()
    {
        ApplicationManagementRequest request = new ApplicationManagementRequest();
        request.setServiceId("96E4E53E-FE87-446C-AF03-0F5BC6527B9D");
        request.setRequestInfo(hostInfo);
        request.setUserAccount(userAccount);
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setApplicationName("eSolutions");

        try
        {
            ApplicationManagementResponse response = processor.listApplications(request);

            Assertions.assertThat(response.getRequestStatus()).isEqualTo(CoreServicesStatus.SUCCESS);
        }
        catch (final ApplicationManagementException amx)
        {
            Assertions.fail(amx.getMessage());
        }
    }

    @Test public void getApplicationData()
    {
        Application app = new Application();
        app.setGuid("74b2cfad-ee63-486b-a0a4-ac862ef2e876");

        ApplicationManagementRequest request = new ApplicationManagementRequest();
        request.setApplication(app);
        request.setServiceId("96E4E53E-FE87-446C-AF03-0F5BC6527B9D");
        request.setRequestInfo(hostInfo);
        request.setUserAccount(userAccount);
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setApplicationName("eSolutions");

        try
        {
            ApplicationManagementResponse response = processor.getApplicationData(request);

            Assertions.assertThat(response.getRequestStatus()).isEqualTo(CoreServicesStatus.SUCCESS);
        }
        catch (final ApplicationManagementException amx)
        {
            Assertions.fail(amx.getMessage());
        }
    }

    @AfterAll public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
        CoreServicesInitializer.shutdown();
    }
}
