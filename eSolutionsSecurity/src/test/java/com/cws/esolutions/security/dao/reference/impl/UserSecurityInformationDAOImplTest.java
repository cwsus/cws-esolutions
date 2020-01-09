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
 * cws-khuntly   11/23/2008 22:39:20             Created.
 */
import org.junit.Test;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import java.sql.SQLException;
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

    @Before public void setUp()
    {
        try
        {
            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/logging/logging.xml", true);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            System.exit(1);
        }
    }

    @Test public void addOrUpdateSalt()
    {
        try
        {
            Assert.assertTrue(dao.addOrUpdateSalt(UserSecurityInformationDAOImplTest.GUID, logonSalt, SaltType.LOGON.name()));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test public void getUserSalt()
    {
        try
        {
            Assert.assertNotNull(dao.getUserSalt(UserSecurityInformationDAOImplTest.GUID, SaltType.LOGON.name()));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test public void insertResetData()
    {
        try
        {
            Assert.assertTrue(dao.insertResetData(UserSecurityInformationDAOImplTest.GUID, resetId, null));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test public void listActiveResets()
    {
        try
        {
            Assert.assertNotNull(dao.listActiveResets());
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test public void getResetData()
    {
        try
        {
            Assert.assertNotNull(dao.getResetData("EgSEz9uTDeaCKvekHLB0PbKT9uzNj7vxm6yo8JklXnXRwNSUicI9ikx6dhpP1iGv"));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test public void verifySmsForReset()
    {
        try
        {
            Assert.assertTrue(dao.verifySmsForReset(UserSecurityInformationDAOImplTest.GUID, resetId, smsCode));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test public void removeResetData()
    {
        try
        {
            Assert.assertTrue(dao.removeResetData(UserSecurityInformationDAOImplTest.GUID, "EgSEz9uTDeaCKvekHLB0PbKT9uzNj7vxm6yo8JklXnXRwNSUicI9ikx6dhpP1iGv"));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test public void removeUserData()
    {
        try
        {
            Assert.assertTrue(dao.removeUserData(UserSecurityInformationDAOImplTest.GUID, null));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @After public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
