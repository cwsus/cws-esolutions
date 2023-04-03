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
 * File: DatacenterManagementProcessorImplTest.java
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
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.TestInstance;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.core.enums.CoreServicesStatus;
import com.cws.esolutions.core.processors.dto.Datacenter;
import com.cws.esolutions.security.enums.SecurityUserRole;
import com.cws.esolutions.core.processors.enums.ServiceStatus;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.core.listeners.CoreServicesInitializer;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.core.processors.dto.DatacenterManagementRequest;
import com.cws.esolutions.core.processors.dto.DatacenterManagementResponse;
import com.cws.esolutions.core.processors.exception.DatacenterManagementException;
import com.cws.esolutions.core.processors.interfaces.IDatacenterManagementProcessor;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DatacenterManagementProcessorImplTest
{
    private static UserAccount userAccount = new UserAccount();
    private static RequestHostInfo hostInfo = new RequestHostInfo();

    private static final IDatacenterManagementProcessor processor = (IDatacenterManagementProcessor) new DatacenterManagementProcessorImpl();

    @BeforeAll public void setUp()
    {
        hostInfo.setHostAddress("junit");
        hostInfo.setHostName("junit");

        userAccount.setStatus(LoginStatus.SUCCESS);
        userAccount.setGuid("f42fb0ba-4d1e-1126-986f-800cd2650000");
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

    @Test public void addNewDatacenter()
    {
    	Datacenter datacenter = new Datacenter();
    	datacenter.setName("MYDC17");
    	datacenter.setStatus(ServiceStatus.ACTIVE);
    	datacenter.setDescription("17 test datacenter");

    	DatacenterManagementRequest request = new DatacenterManagementRequest();
    	request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
    	request.setApplicationName("eSolutions");
    	request.setDatacenter(datacenter);
    	request.setRequestInfo(hostInfo);
    	request.setServiceId("D1B5D088-32B3-4AA1-9FCF-822CB476B649");
    	request.setUserAccount(userAccount);

    	try
    	{
    		DatacenterManagementResponse response = processor.addNewDatacenter(request);

    		Assertions.assertThat(response.getRequestStatus()).isEqualTo(CoreServicesStatus.SUCCESS);
    	}
    	catch (final DatacenterManagementException dmx)
        {

    		Assertions.fail(dmx.getMessage());
        }
    }

    @Test public void updateDatacenter()
    {
    	Datacenter datacenter = new Datacenter();
    	datacenter.setGuid("0f44783c-b618-4440-a4cf-0c271748a47a"); // get a value out of the db to use
    	datacenter.setName("CH27");
    	datacenter.setStatus(ServiceStatus.ACTIVE);
    	datacenter.setDescription("MyTestDatacenter");
    	datacenter.setDescription("my description");

    	DatacenterManagementRequest request = new DatacenterManagementRequest();
    	request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
    	request.setApplicationName("eSolutions");
    	request.setDatacenter(datacenter);
    	request.setRequestInfo(hostInfo);
    	request.setServiceId("D1B5D088-32B3-4AA1-9FCF-822CB476B649");
    	request.setUserAccount(userAccount);

    	try
    	{
    		DatacenterManagementResponse response = processor.updateDatacenter(request);

    		Assertions.assertThat(response.getRequestStatus()).isEqualTo(CoreServicesStatus.SUCCESS);
    	}
    	catch (final DatacenterManagementException dmx)
        {
    		Assertions.fail(dmx.getMessage());
        }
    }

    @Test public void removeDatacenter()
    {
    	Datacenter datacenter = new Datacenter();
    	datacenter.setGuid("ada799db-624b-4204-be9b-dba45005736a");

    	DatacenterManagementRequest request = new DatacenterManagementRequest();
    	request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
    	request.setApplicationName("eSolutions");
    	request.setDatacenter(datacenter);
    	request.setRequestInfo(hostInfo);
    	request.setServiceId("D1B5D088-32B3-4AA1-9FCF-822CB476B649");
    	request.setUserAccount(userAccount);

    	try
    	{
    		DatacenterManagementResponse response = processor.removeDatacenter(request);

    		Assertions.assertThat(response.getRequestStatus()).isEqualTo(CoreServicesStatus.SUCCESS);
    	}
    	catch (final DatacenterManagementException dmx)
        {
    		Assertions.fail(dmx.getMessage());
        }
    }

    @Test public void listDatacenters()
    {
    	DatacenterManagementRequest request = new DatacenterManagementRequest();
    	request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
    	request.setApplicationName("eSolutions");
    	request.setRequestInfo(hostInfo);
    	request.setServiceId("D1B5D088-32B3-4AA1-9FCF-822CB476B649");
    	request.setUserAccount(userAccount);

    	try
    	{
    		DatacenterManagementResponse response = processor.listDatacenters(request);

    		Assertions.assertThat(response.getRequestStatus()).isEqualTo(CoreServicesStatus.SUCCESS);
    	}
    	catch (final DatacenterManagementException dmx)
        {
    		Assertions.fail(dmx.getMessage());
        }
    }

    @Test public void getDatacenterByAttribute()
    {
    	Datacenter dataCenter = new Datacenter();
    	dataCenter.setName("CH2");

    	DatacenterManagementRequest request = new DatacenterManagementRequest();
    	request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
    	request.setApplicationName("eSolutions");
    	request.setRequestInfo(hostInfo);
    	request.setServiceId("D1B5D088-32B3-4AA1-9FCF-822CB476B649");
    	request.setUserAccount(userAccount);
    	request.setDatacenter(dataCenter);

    	try
    	{
    		DatacenterManagementResponse response = processor.getDatacenterByAttribute(request);

    		Assertions.assertThat(response.getRequestStatus()).isEqualTo(CoreServicesStatus.SUCCESS);
    	}
    	catch (final DatacenterManagementException dmx)
        {
    		Assertions.fail(dmx.getMessage());
        }
    }

    @Test public void getDatacenterData()
    {
    	Datacenter dataCenter = new Datacenter();
    	dataCenter.setGuid("484d1923-a384-4e9e-8386-c9b07de60776");

    	DatacenterManagementRequest request = new DatacenterManagementRequest();
    	request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
    	request.setApplicationName("eSolutions");
    	request.setRequestInfo(hostInfo);
    	request.setServiceId("D1B5D088-32B3-4AA1-9FCF-822CB476B649");
    	request.setUserAccount(userAccount);
    	request.setDatacenter(dataCenter);

    	try
    	{
    		DatacenterManagementResponse response = processor.getDatacenterData(request);

    		Assertions.assertThat(response.getRequestStatus()).isEqualTo(CoreServicesStatus.SUCCESS);
    	}
    	catch (final DatacenterManagementException dmx)
        {
    		Assertions.fail(dmx.getMessage());
        }
    }

    @AfterAll public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
        CoreServicesInitializer.shutdown();
    }
}
