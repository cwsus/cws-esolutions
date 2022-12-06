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
 * File: UserSecurityInformationDAOImplTest.java
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
import org.apache.commons.lang.RandomStringUtils;

import com.cws.esolutions.security.processors.enums.SaltType;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.security.dao.reference.interfaces.IUserSecurityInformationDAO;

public class UserSecurityInformationDAOImplTest
{
    private static final String GUID = "f42fb0ba-4d1e-1126-986f-800cd2650000";
    private static final String resetId = RandomStringUtils.randomAlphanumeric(64);
    private static final String smsCode = RandomStringUtils.randomAlphanumeric(8);
    private static final String logonSalt = RandomStringUtils.randomAlphanumeric(64);
    private static final IUserSecurityInformationDAO dao = new UserSecurityInformationDAOImpl();

    @BeforeAll public void setUp()
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

    @Test public void addOrUpdateSalt()
    {
        try
        {
        	Assertions.assertThat(dao.addOrUpdateSalt(UserSecurityInformationDAOImplTest.GUID, logonSalt, SaltType.LOGON.name())).isTrue();
        }
        catch (final SQLException sqx)
        {
            Assertions.fail(sqx.getMessage());
        }
    }

    @Test public void getUserSalt()
    {
        try
        {
            Assertions.assertThat(dao.getUserSalt(UserSecurityInformationDAOImplTest.GUID, SaltType.LOGON.name())).isNotEmpty();
        }
        catch (final SQLException sqx)
        {
            Assertions.fail(sqx.getMessage());
        }
    }

    @Test public void insertResetData()
    {
        try
        {
        	Assertions.assertThat(dao.insertResetData(UserSecurityInformationDAOImplTest.GUID, resetId, null)).isTrue();
        }
        catch (final SQLException sqx)
        {
        	Assertions.fail(sqx.getMessage());
        }
    }

    @Test public void listActiveResets()
    {
        try
        {
        	Assertions.assertThat(dao.listActiveResets()).isNotEmpty();
        }
        catch (final SQLException sqx)
        {
        	Assertions.fail(sqx.getMessage());
        }
    }

    @Test public void getResetData()
    {
        try
        {
        	Assertions.assertThat(dao.getResetData("EgSEz9uTDeaCKvekHLB0PbKT9uzNj7vxm6yo8JklXnXRwNSUicI9ikx6dhpP1iGv")).isNotEmpty();
        }
        catch (final SQLException sqx)
        {
        	Assertions.fail(sqx.getMessage());
        }
    }

    @Test public void verifySmsForReset()
    {
        try
        {
        	Assertions.assertThat(dao.verifySmsForReset(UserSecurityInformationDAOImplTest.GUID, resetId, smsCode)).isTrue();
        }
        catch (final SQLException sqx)
        {
        	Assertions.fail(sqx.getMessage());
        }
    }

    @Test public void removeResetData()
    {
        try
        {
        	Assertions.assertThat(dao.removeResetData(UserSecurityInformationDAOImplTest.GUID, "EgSEz9uTDeaCKvekHLB0PbKT9uzNj7vxm6yo8JklXnXRwNSUicI9ikx6dhpP1iGv")).isTrue();
        }
        catch (final SQLException sqx)
        {
        	Assertions.fail(sqx.getMessage());
        }
    }

    @Test public void removeUserData()
    {
        try
        {
        	Assertions.assertThat(dao.removeUserData(UserSecurityInformationDAOImplTest.GUID, null)).isTrue();
        }
        catch (final SQLException sqx)
        {
        	Assertions.fail(sqx.getMessage());
        }
    }

    @AfterAll public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
