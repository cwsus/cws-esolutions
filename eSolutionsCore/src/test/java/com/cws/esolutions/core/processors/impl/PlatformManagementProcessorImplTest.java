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
 * File: PlatformManagementProcessorImplTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.assertj.core.api.Assertions;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.enums.SecurityUserRole;
import com.cws.esolutions.core.processors.dto.Platform;
import com.cws.esolutions.core.processors.dto.PlatformManagementRequest;
import com.cws.esolutions.core.processors.dto.PlatformManagementResponse;
import com.cws.esolutions.core.processors.enums.NetworkPartition;
import com.cws.esolutions.core.processors.enums.ServiceRegion;
import com.cws.esolutions.core.processors.enums.ServiceStatus;
import com.cws.esolutions.core.processors.exception.PlatformManagementException;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.utility.securityutils.processors.dto.RequestHostInfo;
import com.cws.esolutions.core.enums.CoreServicesStatus;
import com.cws.esolutions.core.listeners.CoreServicesInitializer;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.core.processors.interfaces.IPlatformManagementProcessor;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PlatformManagementProcessorImplTest
{
    private static UserAccount userAccount = new UserAccount();
    private static RequestHostInfo hostInfo = new RequestHostInfo();

    private static final IPlatformManagementProcessor processor = (IPlatformManagementProcessor) new PlatformManagementProcessorImpl();

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

    @Test public void addNewPlatform()
    {
    	Platform platform = new Platform();
    	platform.setPlatformDescription("This is a new platform!");
    	platform.setPlatformName("CHDC");
    	platform.setPlatformPartition(NetworkPartition.DRN);
    	platform.setPlatformRegion(ServiceRegion.DEV);
    	platform.setPlatformStatus(ServiceStatus.ACTIVE);

    	PlatformManagementRequest request = new PlatformManagementRequest();
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setApplicationName("eSolutions");
    	request.setPlatform(platform);
    	request.setRequestInfo(hostInfo);
    	request.setUserAccount(userAccount);

    	try
    	{
			PlatformManagementResponse response = processor.addNewPlatform(request);

			Assertions.assertThat(response.getRequestStatus()).isEqualTo(CoreServicesStatus.SUCCESS);
		}
    	catch (PlatformManagementException pmx)
    	{
			Assertions.fail(pmx.getMessage());
		}
    }

    @Test public void updatePlatformData()
    {
    	Platform platform = new Platform();
    	platform.setPlatformDescription("This is a another new platform!");
    	platform.setPlatformName("AnotherPlatformFourteen");
    	platform.setPlatformGuid("224b456f-a18a-453e-b9d1-2fdeaf6dc14b");
    	platform.setPlatformPartition(NetworkPartition.DRN);
    	platform.setPlatformRegion(ServiceRegion.PRD);
    	platform.setPlatformStatus(ServiceStatus.ACTIVE);

    	PlatformManagementRequest request = new PlatformManagementRequest();
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setApplicationName("eSolutions");
    	request.setPlatform(platform);
    	request.setRequestInfo(hostInfo);
    	request.setUserAccount(userAccount);

    	try
    	{
			PlatformManagementResponse response = processor.updatePlatformData(request);

			Assertions.assertThat(response.getRequestStatus()).isEqualTo(CoreServicesStatus.SUCCESS);
		}
    	catch (PlatformManagementException pmx)
    	{
			Assertions.fail(pmx.getMessage());
		}
    }

    @Test public void listPlatforms()
    {
    	PlatformManagementRequest request = new PlatformManagementRequest();
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setApplicationName("eSolutions");
    	request.setRequestInfo(hostInfo);
    	request.setUserAccount(userAccount);

    	try
    	{
			PlatformManagementResponse response = processor.listPlatforms(request);

			Assertions.assertThat(response.getRequestStatus()).isEqualTo(CoreServicesStatus.SUCCESS);
		}
    	catch (PlatformManagementException pmx)
    	{
			Assertions.fail(pmx.getMessage());
		}
    }

    @Test public void getPlatformByAttribute()
    {
    	Platform platform = new Platform();
    	platform.setPlatformName("CHDC");

    	PlatformManagementRequest request = new PlatformManagementRequest();
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setApplicationName("eSolutions");
    	request.setRequestInfo(hostInfo);
    	request.setUserAccount(userAccount);
    	request.setPlatform(platform);

    	try
    	{
			PlatformManagementResponse response = processor.getPlatformByAttribute(request);

			Assertions.assertThat(response.getRequestStatus()).isEqualTo(CoreServicesStatus.SUCCESS);
		}
    	catch (PlatformManagementException pmx)
    	{
			Assertions.fail(pmx.getMessage());
		}
    }

    @Test public void getPlatformData()
    {
    	Platform platform = new Platform();
    	platform.setPlatformGuid("815eff28-faab-40a2-9b0a-cdd95ead1e58");

    	PlatformManagementRequest request = new PlatformManagementRequest();
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setApplicationName("eSolutions");
    	request.setRequestInfo(hostInfo);
    	request.setUserAccount(userAccount);
    	request.setPlatform(platform);

    	try
    	{
			PlatformManagementResponse response = processor.getPlatformData(request);

			Assertions.assertThat(response.getRequestStatus()).isEqualTo(CoreServicesStatus.SUCCESS);
		}
    	catch (PlatformManagementException pmx)
    	{
			Assertions.fail(pmx.getMessage());
		}
    }

    @Test public void removePlatformData()
    {
    	Platform platform = new Platform();
    	platform.setPlatformGuid("224b456f-a18a-453e-b9d1-2fdeaf6dc14b");

    	PlatformManagementRequest request = new PlatformManagementRequest();
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setApplicationName("eSolutions");
    	request.setRequestInfo(hostInfo);
    	request.setUserAccount(userAccount);
    	request.setPlatform(platform);

    	try
    	{
			PlatformManagementResponse response = processor.removePlatformData(request);

			Assertions.assertThat(response.getRequestStatus()).isEqualTo(CoreServicesStatus.SUCCESS);
		}
    	catch (PlatformManagementException pmx)
    	{
			Assertions.fail(pmx.getMessage());
		}
    }

    @AfterAll public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
        CoreServicesInitializer.shutdown();
    }
}
