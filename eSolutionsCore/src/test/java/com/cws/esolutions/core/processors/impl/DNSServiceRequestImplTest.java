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
 * File: DNSServiceRequestImplTest.java
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
import com.cws.esolutions.core.processors.dto.DNSRecord;
import com.cws.esolutions.core.enums.CoreServicesStatus;
import com.cws.esolutions.core.processors.enums.DNSRecordType;
import com.cws.esolutions.core.processors.enums.DNSRequestType;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.core.processors.dto.DNSServiceRequest;
import com.cws.esolutions.core.listeners.CoreServicesInitializer;
import com.cws.esolutions.core.processors.dto.DNSServiceResponse;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.core.processors.exception.DNSServiceException;
import com.cws.esolutions.utility.securityutils.processors.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.interfaces.IDNSServiceRequestProcessor;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DNSServiceRequestImplTest
{
    private static UserAccount userAccount = new UserAccount();
    private static RequestHostInfo hostInfo = new RequestHostInfo();

    private static final IDNSServiceRequestProcessor dnsService = new DNSServiceRequestProcessorImpl();

    @BeforeAll public void setUp()
    {
        hostInfo.setHostAddress("junit");
        hostInfo.setHostName("junit");

        userAccount.setStatus(LoginStatus.SUCCESS);
        userAccount.setGuid("f42fb0ba-4d1e-1126-986f-800cd2650000");
        userAccount.setUsername("khuntly");

        try
        {
            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/logging/logging.xml", false);
            CoreServicesInitializer.initializeService("eSolutionsCore/config/ServiceConfig.xml", "eSolutionsCore/logging/logging.xml", true, false);
        }
        catch (final Exception ex)
        {
            Assertions.fail(ex.getMessage());

            System.exit(-1);
        }
    }

    @Test public void performLookup()
    {
        DNSRecord record = new DNSRecord();
        record.setRecordName("www.google.com");
        record.setRecordType(DNSRecordType.A);

        DNSServiceRequest request = new DNSServiceRequest();
        request.setUseSystemResolver(Boolean.TRUE);
        request.setRecord(record);
        request.setRequestInfo(hostInfo);
        request.setRequestType(DNSRequestType.LOOKUP);
        request.setServiceId("B52B1DE9-37A4-4554-B85E-2EA28C4EE3DD");
        request.setUserAccount(userAccount);
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setApplicationName("eSolutions");

        try
        {
            DNSServiceResponse response = dnsService.performLookup(request);

            Assertions.assertThat(response.getRequestStatus()).isEqualTo(CoreServicesStatus.SUCCESS);
        }
        catch (final DNSServiceException dnsx)
        {
            Assertions.fail(dnsx.getMessage());
        }
    }

    @AfterAll public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
        CoreServicesInitializer.shutdown();
    }
}
