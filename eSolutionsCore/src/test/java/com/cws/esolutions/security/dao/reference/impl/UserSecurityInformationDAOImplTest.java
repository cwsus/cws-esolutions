/**
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
package com.cws.esolutions.security.dao.reference.impl;

import org.junit.Test;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import java.sql.SQLException;
import org.apache.commons.lang.RandomStringUtils;

import com.cws.esolutions.security.enums.SaltType;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.security.dao.reference.interfaces.IUserSecurityInformationDAO;
/**
 * eSolutionsCore
 * com.cws.esolutions.security.dao.reference.impl
 * UserSecurityInformationDAOImplTest.java
 *
 * $Id: $
 * $Author: $
 * $Date: $
 * $Revision: $
 * @author kmhuntly@gmail.com
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * 35033355 @ Jun 11, 2013 12:43:18 PM
 *     Created.
 */
public class UserSecurityInformationDAOImplTest
{
    private String resetId = null;
    private String smsCode = null;
    private String resetSalt = null;
    private String logonSalt = null;

    private static final String GUID = "74d9729b-7fb2-4fef-874b-c9ee5d7a5a95";
    private static final IUserSecurityInformationDAO dao = new UserSecurityInformationDAOImpl();

    @Before
    public void setUp() throws Exception
    {
        try
        {
            smsCode = RandomStringUtils.randomAlphanumeric(8);
            resetId = RandomStringUtils.randomAlphanumeric(64);
            resetSalt = RandomStringUtils.randomAlphanumeric(64);
            logonSalt = RandomStringUtils.randomAlphanumeric(64);

            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/config/SecurityLogging.xml");
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            System.exit(1);
        }
    }

    @Test
    public final void testAddUserLogonSalt()
    {
        try
        {
            Assert.assertTrue(dao.addUserSalt(UserSecurityInformationDAOImplTest.GUID, logonSalt, SaltType.LOGON.name()));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public final void testAddUserResetSalt()
    {
        try
        {
            Assert.assertTrue(dao.addUserSalt(UserSecurityInformationDAOImplTest.GUID, resetSalt, SaltType.RESET.name()));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public final void testUpdateUserLoginSalt()
    {
        String salt = RandomStringUtils.randomAlphanumeric(64);

        try
        {
            Assert.assertTrue(dao.updateUserSalt(UserSecurityInformationDAOImplTest.GUID, salt, SaltType.LOGON.name()));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public final void testUpdateUserResetSalt()
    {
        String salt = RandomStringUtils.randomAlphanumeric(64);

        try
        {
            Assert.assertTrue(dao.updateUserSalt(UserSecurityInformationDAOImplTest.GUID, salt, SaltType.RESET.name()));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public final void testGetUserLoginSalt()
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

    @Test
    public final void testGetUserResetSalt()
    {
        try
        {
            Assert.assertNotNull(dao.getUserSalt(UserSecurityInformationDAOImplTest.GUID, SaltType.RESET.name()));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public final void testRemoveUserData()
    {
        try
        {
            Assert.assertTrue(dao.removeUserData(UserSecurityInformationDAOImplTest.GUID));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public final void testInsertResetData()
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

    @Test
    public final void testInsertResetDataWithSms()
    {
        try
        {
            Assert.assertTrue(dao.insertResetData(UserSecurityInformationDAOImplTest.GUID, resetId, smsCode));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public final void testGetResetData()
    {
        try
        {
            Assert.assertNotNull(dao.getResetData(resetId));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public final void testRemoveResetData()
    {
        try
        {
            Assert.assertTrue(dao.removeResetData(UserSecurityInformationDAOImplTest.GUID, resetId));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public final void testVerifySmsForReset()
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

    @After
    public void tearDown() throws Exception
    {
        SecurityServiceInitializer.shutdown();
    }
}
