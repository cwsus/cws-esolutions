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
import org.junit.Assert;
import org.junit.Before;
import org.apache.commons.lang.RandomStringUtils;

import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.dto.UserSecurity;
import com.cws.esolutions.core.processors.dto.DataCenter;
import com.cws.esolutions.core.processors.enums.ServiceStatus;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.core.listeners.CoreServiceInitializer;
import com.cws.esolutions.security.dao.userauth.enums.LoginType;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.security.processors.dto.AuthenticationRequest;
import com.cws.esolutions.security.dao.userauth.enums.AuthenticationType;
import com.cws.esolutions.security.processors.dto.AuthenticationResponse;
import com.cws.esolutions.core.processors.dto.DatacenterManagementRequest;
import com.cws.esolutions.core.processors.dto.DatacenterManagementResponse;
import com.cws.esolutions.security.processors.impl.AuthenticationProcessorImpl;
import com.cws.esolutions.security.processors.interfaces.IAuthenticationProcessor;
import com.cws.esolutions.core.processors.exception.DatacenterManagementException;
import com.cws.esolutions.core.processors.interfaces.IDatacenterManagementProcessor;
/**
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.processors.impl
 * File: DatacenterManagementProcessorImplTest.java
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
 * 35033355 @ Oct 22, 2013 12:42:00 PM
 *     Created.
 */
public class DatacenterManagementProcessorImplTest
{
    private UserAccount userAccount = new UserAccount();
    private RequestHostInfo hostInfo = new RequestHostInfo();

    private static final String serviceId = "0C1C5F83-3EDD-4635-9F1E-6A9B5383747E";
    private static final IDatacenterManagementProcessor processor = new DatacenterManagementProcessorImpl();


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
            hostInfo.setSessionId(RandomStringUtils.randomAlphanumeric(32));

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
                            hostInfo.setSessionId(RandomStringUtils.randomAlphanumeric(32));
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
    public final void testAddNewDatacenter()
    {
        DataCenter dataCenter = new DataCenter();
        dataCenter.setDatacenterGuid(UUID.randomUUID().toString());
        dataCenter.setDatacenterName(RandomStringUtils.randomAlphabetic(8));
        dataCenter.setDatacenterStatus(ServiceStatus.ACTIVE);
        dataCenter.setDatacenterDesc("Test DataCenter");

        DatacenterManagementRequest request = new DatacenterManagementRequest();
        request.setDataCenter(dataCenter);
        request.setRequestInfo(hostInfo);
        request.setServiceId(DatacenterManagementProcessorImplTest.serviceId);
        request.setUserAccount(userAccount);

        try
        {
            DatacenterManagementResponse response = processor.addNewDatacenter(request);

            Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
        }
        catch (DatacenterManagementException dmx)
        {
            Assert.fail(dmx.getMessage());
        }
    }

    @Test
    public final void testListDatacenters()
    {
        DatacenterManagementRequest request = new DatacenterManagementRequest();
        request.setRequestInfo(hostInfo);
        request.setServiceId(DatacenterManagementProcessorImplTest.serviceId);
        request.setUserAccount(userAccount);

        try
        {
            DatacenterManagementResponse response = processor.listDatacenters(request);

            Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
        }
        catch (DatacenterManagementException dmx)
        {
            Assert.fail(dmx.getMessage());
        }
    }

    @Test
    public final void testGetDatacenter()
    {
        DataCenter dataCenter = new DataCenter();
        dataCenter.setDatacenterGuid("dcee7e07-0452-4da2-a40c-e93a28344c87");

        DatacenterManagementRequest request = new DatacenterManagementRequest();
        request.setDataCenter(dataCenter);
        request.setRequestInfo(hostInfo);
        request.setServiceId(DatacenterManagementProcessorImplTest.serviceId);
        request.setUserAccount(userAccount);

        try
        {
            DatacenterManagementResponse response = processor.getDatacenter(request);

            Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
        }
        catch (DatacenterManagementException dmx)
        {
            Assert.fail(dmx.getMessage());
        }
    }
}
