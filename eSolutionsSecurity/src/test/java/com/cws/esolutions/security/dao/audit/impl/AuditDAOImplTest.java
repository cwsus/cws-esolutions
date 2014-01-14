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
package com.cws.esolutions.security.dao.audit.impl;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.dao.audit.impl
 * File: AuditDAOImplTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.List;
import org.junit.Test;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import java.util.Arrays;
import java.util.ArrayList;
import java.sql.SQLException;
import org.apache.commons.lang.RandomStringUtils;

import com.cws.esolutions.security.processors.enums.AuditType;
import com.cws.esolutions.security.dao.audit.interfaces.IAuditDAO;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;

public class AuditDAOImplTest
{
    private static final IAuditDAO auditDAO = new AuditDAOImpl();

    @Before
    public void setUp()
    {
        try
        {
            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/logging/logging.xml");
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());

            System.exit(1);
        }
    }

    @Test
    public static void testAuditRequestedOperation()
    {
        for (int x = 0; x < 50; x++)
        {
            List<String> auditList = new ArrayList<>(
                Arrays.asList(
                        RandomStringUtils.randomAlphanumeric(32),
                        "khuntly",
                        "74d9729b-7fb2-4fef-874b-c9ee5d7a5a95",
                        "SITEADMIN",
                        "6236B840-88B0-4230-BCBC-8EC33EE837D9",
                        "eSolutions-" + x,
                        AuditType.JUNIT.name(),
                        "junit",
                        "junit"));

            try
            {
            
                auditDAO.auditRequestedOperation(auditList);
            }
            catch (SQLException sqx)
            {
                Assert.fail(sqx.getMessage());
            }
        }
    }

    @Test
    public static void testGetAuditInterval()
    {
        try
        {
            Assert.assertNotNull(auditDAO.getAuditInterval("74d9729b-7fb2-4fef-874b-c9ee5d7a5a95", 1));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @After
    public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
