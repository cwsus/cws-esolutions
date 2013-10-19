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
import org.junit.Assert;
import org.junit.Before;
import org.apache.commons.lang.RandomStringUtils;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.dto.UserSecurity;
import com.cws.esolutions.core.processors.dto.Project;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.enums.ServiceStatus;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.core.listeners.CoreServiceInitializer;
import com.cws.esolutions.security.dao.userauth.enums.LoginType;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.core.processors.dto.ProjectManagementRequest;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.security.processors.dto.AuthenticationRequest;
import com.cws.esolutions.core.processors.dto.ProjectManagementResponse;
import com.cws.esolutions.security.dao.userauth.enums.AuthenticationType;
import com.cws.esolutions.security.processors.dto.AuthenticationResponse;
import com.cws.esolutions.core.processors.impl.ProjectManagementProcessorImpl;
import com.cws.esolutions.core.processors.exception.ProjectManagementException;
import com.cws.esolutions.security.processors.impl.AuthenticationProcessorImpl;
import com.cws.esolutions.core.processors.interfaces.IProjectManagementProcessor;
import com.cws.esolutions.security.processors.interfaces.IAuthenticationProcessor;
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
    private UserAccount userAccount = new UserAccount();
    private RequestHostInfo hostInfo = new RequestHostInfo();

    private static final IProjectManagementProcessor processor = new ProjectManagementProcessorImpl();

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
                userRequest.setAppName("esolutions");
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
                        passRequest.setAppName("esolutions");
                        passRequest.setAuthType(AuthenticationType.LOGIN);
                        passRequest.setLoginType(LoginType.PASSWORD);
                        passRequest.setUserAccount(authUser);
                        passRequest.setUserSecurity(userSecurity);
                        passRequest.setApplicationId("B760E92F-827A-42E7-9E8D-64334657BA83");

                        AuthenticationResponse passResponse = agentAuth.processAgentLogon(passRequest);

                        if (passResponse.getRequestStatus() == SecurityRequestStatus.SUCCESS)
                        {
                            userAccount = passResponse.getUserAccount();
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
    public final void testAddNewProject()
    {
        for (int x = 0; x < 3; x++)
        {
            Project project = new Project();
            project.setProjectGuid(java.util.UUID.randomUUID().toString());
            project.setProjectCode(RandomStringUtils.randomAlphabetic(8));
            project.setChangeQueue("change-queue");
            project.setContactEmail("email@domain.com");
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
    public final void testUpdateProjectData()
    {
    }

    @Test
    public final void testListProjects()
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
    public final void testListProjectsByAttribute()
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
    public final void testGetProjectData()
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
            ProjectManagementResponse response = processor.getProjectData(request);

            Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
        }
        catch (ProjectManagementException pmx)
        {
            Assert.fail(pmx.getMessage());
        }
    }

    @After
    public void tearDown() throws Exception
    {
        SecurityServiceInitializer.shutdown();
    }
}
