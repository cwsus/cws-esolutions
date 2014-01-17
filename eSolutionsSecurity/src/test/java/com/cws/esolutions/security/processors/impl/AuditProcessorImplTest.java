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
package com.cws.esolutions.security.processors.impl;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.processors.processors.impl
 * File: AuditProcessorImplTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.Assert;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.processors.dto.AuditEntry;
import com.cws.esolutions.security.processors.enums.AuditType;
import com.cws.esolutions.security.processors.dto.AuditRequest;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.security.processors.dto.AuditResponse;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.security.processors.interfaces.IAuditProcessor;
import com.cws.esolutions.security.processors.exception.AuditServiceException;

public class AuditProcessorImplTest
{
    private static UserAccount userAccount = new UserAccount();
    private static RequestHostInfo hostInfo = new RequestHostInfo();

    private static final IAuditProcessor processor = new AuditProcessorImpl();

    @Before
    public void setUp()
    {
        try
        {
            AuditProcessorImplTest.hostInfo.setHostAddress("junit");
            AuditProcessorImplTest.hostInfo.setHostName("junit");

            AuditProcessorImplTest.userAccount.setStatus(LoginStatus.SUCCESS);
            AuditProcessorImplTest.userAccount.setGuid("74d9729b-7fb2-4fef-874b-c9ee5d7a5a95");
            AuditProcessorImplTest.userAccount.setUsername("khuntly");

            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/logging/logging.xml");
        }
        catch (Exception ex)
        {
            Assert.fail(ex.getMessage());

            System.exit(-1);
        }
    }

    @Test
    public void testAuditRequest()
    {
        AuditEntry auditEntry = new AuditEntry();
        auditEntry.setApplicationId("JUNIT");
        auditEntry.setApplicationName("JUNIT");
        auditEntry.setAuditType(AuditType.JUNIT);
        auditEntry.setHostInfo(AuditProcessorImplTest.hostInfo);
        auditEntry.setUserAccount(AuditProcessorImplTest.userAccount);

        AuditRequest auditRequest = new AuditRequest();
        auditRequest.setAuditEntry(auditEntry);

        try
        {
            AuditProcessorImplTest.processor.auditRequest(auditRequest);
        }
        catch (AuditServiceException asx)
        {
            Assert.fail(asx.getMessage());
        }
    }

    @Test
    public void testGetAuditEntries()
    {
        AuditEntry auditEntry = new AuditEntry();
        auditEntry.setUserAccount(AuditProcessorImplTest.userAccount);

        AuditRequest request = new AuditRequest();
        request.setAuditEntry(auditEntry);
        request.setApplicationId("myid");
        request.setApplicationName("esolutions");
        request.setHostInfo(AuditProcessorImplTest.hostInfo);
        request.setUserAccount(AuditProcessorImplTest.userAccount);
        

        try
        {
            AuditResponse response = AuditProcessorImplTest.processor.getAuditEntries(request);

            Assert.assertEquals(SecurityRequestStatus.SUCCESS, response.getRequestStatus());
        }
        catch (AuditServiceException asx)
        {
            Assert.fail(asx.getMessage());
        }
    }

    @After
    public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
