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
 * File: ServiceMessagingProcessorImplTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import org.junit.Test;
import org.junit.After;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;

import java.util.ArrayList;

import org.apache.commons.lang.RandomStringUtils;

import com.cws.esolutions.security.enums.Role;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.core.processors.dto.ServiceMessage;
import com.cws.esolutions.core.processors.dto.MessagingRequest;
import com.cws.esolutions.core.processors.dto.MessagingResponse;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.core.listeners.CoreServiceInitializer;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.web.processors.impl.ServiceMessagingProcessorImpl;
import com.cws.esolutions.web.processors.interfaces.IMessagingProcessor;
import com.cws.esolutions.core.processors.exception.MessagingServiceException;

public class ServiceMessagingProcessorImplTest
{
    private static UserAccount userAccount = new UserAccount();
    private static RequestHostInfo hostInfo = new RequestHostInfo();

    private static final String SERVICEID = "5C0B0A54-2456-45C9-A435-B485ED36FAC7";
    private static final IMessagingProcessor processor = new ServiceMessagingProcessorImpl();

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
            userAccount.setRoles(new ArrayList<Role>(Arrays.asList(Role.SITEADMIN)));
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

            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/logging/logging.xml");
        }
        catch (Exception ex)
        {
            Assert.fail(ex.getMessage());

            System.exit(-1);
        }
    }

    @Test
    public void addNewMessage()
    {
        ServiceMessage message = new ServiceMessage();
        message.setMessageId(RandomStringUtils.randomAlphanumeric(16));
        message.setMessageTitle("eSolutions - Open Demonstration");
        message.setMessageText("Welcome to the eSolutions service demonstration site. This is also our development site,"
                + "so there may be service interruptions from time to time as we make updates."
                + "Please do let us know what you think of the site and its features. If you have any concerns, don't"
                + "hestitate to contact us."
                + "<br /><br />"
                + "Note that the demo account password will be reset to its default values hourly, so if it has been changed"
                + "just try again in an hour and it should work normally.");
        message.setMessageAuthor(userAccount);
        message.setDoesExpire(false);
        message.setIsActive(true);
        message.setIsAlert(true);

        MessagingRequest request = new MessagingRequest();
        request.setRequestInfo(hostInfo);
        request.setServiceId(ServiceMessagingProcessorImplTest.SERVICEID);
        request.setUserAccount(userAccount);
        request.setSvcMessage(message);

        try
        {
            MessagingResponse response = processor.addNewMessage(request);

            Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
        }
        catch (MessagingServiceException msx)
        {
            Assert.fail(msx.getMessage());
        }
    }

    @Test
    public void deleteExistingMessage()
    {
        Assert.fail("Not yet implemented");
    }

    public void updateExistingMessage()
    {
        Assert.fail("Not yet implemented");
    }

    @Test
    public void showMessages()
    {
        MessagingRequest request = new MessagingRequest();
        request.setRequestInfo(hostInfo);
        request.setUserAccount(userAccount);
        request.setServiceId(ServiceMessagingProcessorImplTest.SERVICEID);

        try
        {
            MessagingResponse response = processor.showMessages(request);

            Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
        }
        catch (MessagingServiceException msx)
        {
            Assert.fail(msx.getMessage());
        }
    }

    @Test
    public void showMessage()
    {
        ServiceMessage message = new ServiceMessage();
        message.setMessageId("muUlODU6k1kA0L3q");

        MessagingRequest request = new MessagingRequest();
        request.setRequestInfo(hostInfo);
        request.setServiceId(ServiceMessagingProcessorImplTest.SERVICEID);
        request.setUserAccount(userAccount);
        request.setSvcMessage(message);

        try
        {
            MessagingResponse response = processor.showMessage(request);

            Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
        }
        catch (MessagingServiceException msx)
        {
            Assert.fail(msx.getMessage());
        }
    }

    @After
    public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
        CoreServiceInitializer.shutdown();
    }
}
