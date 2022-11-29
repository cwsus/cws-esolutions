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
package com.cws.esolutions.security.dao.reference.impl;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.dao.reference.impl
 * File: SecurityReferenceDAOImplTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.assertj.core.api.Assertions;

import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.security.dao.reference.interfaces.ISecurityReferenceDAO;

public class SecurityReferenceDAOImplTest
{
    private static final ISecurityReferenceDAO secRef = new SecurityReferenceDAOImpl();

    @BeforeAll public void setUp()
    {
        try
        {
            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/logging/logging.xml", true);
        }
        catch (Exception e)
        {
            Assertions.fail(e.getMessage());
            System.exit(1);
        }
    }

    @Test public void obtainApprovedServers()
    {
        try
        {
        	Assertions.assertThat(secRef.obtainApprovedServers()).isNotNull();
        }
        catch (SQLException sqx)
        {
        	Assertions.fail(sqx.getMessage());
        }
    }

    @Test public void obtainSecurityQuestionList()
    {
        try
        {
            Assertions.assertThat(secRef.obtainSecurityQuestionList()).isNotNull();
        }
        catch (SQLException sqx)
        {
        	Assertions.fail(sqx.getMessage());
        }
    }

    @AfterAll public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
