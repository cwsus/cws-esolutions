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
 * File: ServiceMessagingProcessorImplTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly   11/23/2008 22:39:20             Created.
 */
import org.junit.Test;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.core.listeners.CoreServiceInitializer;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.core.processors.dto.MessagingRequest;
import com.cws.esolutions.core.processors.exception.MessagingServiceException;
import com.cws.esolutions.core.processors.interfaces.IWebMessagingProcessor;

public class ServiceMessagingProcessorImplTest
{
    private static UserAccount userAccount = new UserAccount();
    private static RequestHostInfo hostInfo = new RequestHostInfo();

    private static final IWebMessagingProcessor processor = new ServiceMessagingProcessorImpl();

	@Before public void setUp() throws Exception
	{
        hostInfo.setHostAddress("junit");
        hostInfo.setHostName("junit");

        userAccount.setStatus(LoginStatus.SUCCESS);
        userAccount.setGuid("f42fb0ba-4d1e-1126-986f-800cd2650000");
        userAccount.setUsername("khuntly");

        try
        {
        	SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/logging/logging.xml", false);
        	CoreServiceInitializer.initializeService("eSolutionsCore/config/ServiceConfig.xml", "eSolutionsCore/logging/logging.xml", true, true);
        }
        catch (Exception ex)
        {
        	Assert.fail(ex.getMessage());

        	System.exit(-1);
        }
	}

	@Test public void testAddNewMessage()
	{
		MessagingRequest request = new MessagingRequest();
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setApplicationName("eSolutions");

		try
		{
			processor.addNewMessage(request);
		}
		catch (MessagingServiceException msx)
		{
			Assert.fail(msx.getMessage());
		}
	}

	@Test public void testUpdateExistingMessage()
	{
		MessagingRequest request = new MessagingRequest();
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setApplicationName("eSolutions");

		try
		{
			processor.updateExistingMessage(request);
		}
		catch (MessagingServiceException msx)
		{
			Assert.fail(msx.getMessage());
		}
	}

	@Test public void testShowMessages()
	{
		MessagingRequest request = new MessagingRequest();
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setApplicationName("eSolutions");

		try
		{
			processor.showMessage(request);
		}
		catch (MessagingServiceException msx)
		{
			Assert.fail(msx.getMessage());
		}
	}

	@Test public void testShowAlertMessages()
	{
		MessagingRequest request = new MessagingRequest();
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setApplicationName("eSolutions");

		try
		{
		processor.showAlertMessages(request);
		}
		catch (MessagingServiceException msx)
		{
			Assert.fail(msx.getMessage());
		}
	}

	@Test public void testShowMessage()
	{
		MessagingRequest request = new MessagingRequest();
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setApplicationName("eSolutions");

		try
		{
			processor.showMessage(request);
		}
		catch (MessagingServiceException msx)
		{
			Assert.fail(msx.getMessage());
		}
	}

	@After public void tearDown() throws Exception
	{
        SecurityServiceInitializer.shutdown();
        CoreServiceInitializer.shutdown();
	}
}
