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
 * File: DatacenterManagementProcessorImplTest.java
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
import com.cws.esolutions.core.processors.dto.DataCenter;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.enums.ServiceStatus;
import com.cws.esolutions.core.listeners.CoreServiceInitializer;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.core.processors.dto.DatacenterManagementRequest;
import com.cws.esolutions.core.processors.dto.DatacenterManagementResponse;
import com.cws.esolutions.core.processors.exception.DatacenterManagementException;
import com.cws.esolutions.core.processors.interfaces.IDatacenterManagementProcessor;

public class DatacenterManagementProcessorImplTest
{
    private static UserAccount userAccount = new UserAccount();
    private static RequestHostInfo hostInfo = new RequestHostInfo();

    private static final String serviceId = "0C1C5F83-3EDD-4635-9F1E-6A9B5383747E";
    private static final IDatacenterManagementProcessor processor = new DatacenterManagementProcessorImpl();

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
    public void testAddNewDatacenter()
    {
        DataCenter dataCenter = new DataCenter();
        dataCenter.setDatacenterGuid(UUID.randomUUID().toString());
        dataCenter.setDatacenterName(RandomStringUtils.randomAlphabetic(8));
        dataCenter.setDatacenterStatus(ServiceStatus.ACTIVE);
        dataCenter.setDatacenterDesc("Test DataCenter");

        DatacenterManagementRequest request = new DatacenterManagementRequest();
        request.setDataCenter(dataCenter);
        // request.setRequestInfo(hostInfo);
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
    public void testListDatacenters()
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
    public void testGetDatacenter()
    {
        DataCenter dataCenter = new DataCenter();
        dataCenter.setDatacenterGuid("4af213f1-a03c-4665-a28f-62922f82e5e5");

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

    @After
    public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
