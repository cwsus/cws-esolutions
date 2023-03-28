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
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.apache.commons.lang3.RandomStringUtils;

import com.cws.esolutions.security.processors.enums.AuditType;
import com.cws.esolutions.security.dao.audit.interfaces.IAuditDAO;
import com.cws.esolutions.security.enums.SecurityUserRole;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuditDAOImplTest
{
    private static final IAuditDAO auditDAO = new AuditDAOImpl();

    @BeforeAll
    public void setUp()
    {
        try
        {
            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/logging/logging.xml", true);
        }
        catch (final Exception e)
        {
        	Assertions.fail(e.getMessage());

            System.exit(1);
        }
    }

    @Test
    public void auditRequestedOperation()
    {
        for (int x = 0; x < 50; x++)
        {
            List<String> auditList = new ArrayList<String>(
                Arrays.asList(
                        RandomStringUtils.randomAlphanumeric(128),
                        "junit",
                        "99aaefc1-8a2a-4877-bed5-20b73d971e56",
                        SecurityUserRole.NONE.toString(),
                        "6236B840-88B0-4230-BCBC-8EC33EE837D9",
                        "eSolutions-" + x,
                        AuditType.JUNIT.name(),
                        "ACTION",
                        "junit",
                        "junit"));

            try
            {
                auditDAO.auditRequestedOperation(auditList);
            }
            catch (final SQLException sqx)
            {
                Assertions.fail(sqx.getMessage());
            }
        }
    }

    @Test
    public void getAuditInterval()
    {
        try
        {
        	Assertions.assertThat(auditDAO.getAuditInterval("99aaefc1-8a2a-4877-bed5-20b73d971e56", 1)).isNotEmpty();
        }
        catch (final SQLException sqx)
        {
        	sqx.printStackTrace();
        	Assertions.fail(sqx.getMessage());
        }
    }

    @AfterAll
    public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
