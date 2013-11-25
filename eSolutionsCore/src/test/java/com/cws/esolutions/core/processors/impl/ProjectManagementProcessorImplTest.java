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

import org.junit.Test;
import org.junit.After;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Before;
import java.util.ArrayList;
import org.apache.commons.lang.RandomStringUtils;

import com.cws.esolutions.security.enums.Role;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.core.processors.dto.Project;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.enums.ServiceStatus;
import com.cws.esolutions.core.listeners.CoreServiceInitializer;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.core.processors.dto.ProjectManagementRequest;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.core.processors.dto.ProjectManagementResponse;
import com.cws.esolutions.core.processors.impl.ProjectManagementProcessorImpl;
import com.cws.esolutions.core.processors.exception.ProjectManagementException;
import com.cws.esolutions.core.processors.interfaces.IProjectManagementProcessor;
/**
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.processors.impl
 * File: ProjectManagementProcessorImplTest.java
 *
 * $Id: $
 * $Author: $
 * $Date: $
 * $Revision: $
 * @author 35033355
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * 35033355 @ Oct 11, 2013 12:24:53 PM
 *     Created.
 */
public class ProjectManagementProcessorImplTest
{
    private static UserAccount userAccount = new UserAccount();
    private static RequestHostInfo hostInfo = new RequestHostInfo();

    private static final IProjectManagementProcessor processor = new ProjectManagementProcessorImpl();

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
    public static final void testAddNewProject()
    {
        for (int x = 0; x < 3; x++)
        {
            Project project = new Project();
            project.setProjectGuid(java.util.UUID.randomUUID().toString());
            project.setProjectCode(RandomStringUtils.randomAlphabetic(8));
            project.setChangeQueue("change-queue");
            project.setDevEmail("dev@email.com");
            project.setProdEmail("prod@email.com");
            project.setIncidentQueue("ticket-queue");
            project.setPrimaryContact("primary@domain.com");
            project.setSecondaryContact("secondary@domain.com");
            project.setProjectStatus(ServiceStatus.ACTIVE);

            ProjectManagementRequest request = new ProjectManagementRequest();
            request.setUserAccount(userAccount);
            request.setRequestInfo(hostInfo);
            request.setServiceId("A0F3C71F-5FAF-45B4-AA34-9779F64D397E");
            request.setProject(project);

            try
            {
                ProjectManagementResponse response = processor.addNewProject(request);

                Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
            }
            catch (ProjectManagementException pmx)
            {
                Assert.fail(pmx.getMessage());
            }
        }
    }

    @Test
    public static final void testUpdateProjectData()
    {
    }

    @Test
    public static final void testListProjects()
    {
        ProjectManagementRequest request = new ProjectManagementRequest();
        request.setUserAccount(userAccount);
        request.setRequestInfo(hostInfo);
        request.setServiceId("A0F3C71F-5FAF-45B4-AA34-9779F64D397E");

        try
        {
            ProjectManagementResponse response = processor.listProjects(request);

            Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
        }
        catch (ProjectManagementException pmx)
        {
            Assert.fail(pmx.getMessage());
        }
    }

    @Test
    public static final void testListProjectsByAttribute()
    {
        Project project = new Project();
        project.setProjectGuid("f85bcf1c-63e4-46ec-8903-0f74bb49de00");

        ProjectManagementRequest request = new ProjectManagementRequest();
        request.setUserAccount(userAccount);
        request.setRequestInfo(hostInfo);
        request.setServiceId("A0F3C71F-5FAF-45B4-AA34-9779F64D397E");
        request.setProject(project);

        try
        {
            ProjectManagementResponse response = processor.listProjectsByAttribute(request);

            Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
        }
        catch (ProjectManagementException pmx)
        {
            Assert.fail(pmx.getMessage());
        }
    }

    @Test
    public static final void testGetProjectData()
    {
        Project project = new Project();
        project.setProjectGuid("a8bcb1d5-6088-4264-ade9-8cb878eb4f57");

        ProjectManagementRequest request = new ProjectManagementRequest();
        request.setUserAccount(userAccount);
        request.setRequestInfo(hostInfo);
        request.setServiceId("A0F3C71F-5FAF-45B4-AA34-9779F64D397E");
        request.setProject(project);

        try
        {
            ProjectManagementResponse response = processor.getProjectData(request);

            Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
        }
        catch (ProjectManagementException pmx)
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
