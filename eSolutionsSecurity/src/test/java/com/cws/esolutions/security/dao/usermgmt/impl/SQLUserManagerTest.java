/*
 * Copyright (c) 2009 - 2014 CaspersBox Web Services
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
package com.cws.esolutions.security.dao.usermgmt.impl;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.dao.usermgmt.impl
 * File: SQLUserManagerTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.UUID;
import org.junit.Test;
import org.junit.After;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Assert;
import java.util.ArrayList;
import org.apache.commons.lang.RandomStringUtils;

import com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.security.dao.usermgmt.exception.UserManagementException;

public class SQLUserManagerTest
{
    private static final String GUID = UUID.randomUUID().toString();

    @Before
    public void setUp()
    {
        try
        {
            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/logging/logging.xml");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.fail(e.getMessage());
            System.exit(1);
        }
    }

    public void addUserAccount()
    {
        try
        {
            final UserManager manager = new LDAPUserManager();

            Assert.assertTrue(manager.addUserAccount(
                    new ArrayList<>(
                            Arrays.asList(
                                    "junit-test",
                                    RandomStringUtils.randomAlphanumeric(64),
                                    "Test",
                                    "User",
                                    "test@test.com",
                                    SQLUserManagerTest.GUID,
                                    "Test User")),
                    new ArrayList<>(
                            Arrays.asList(
                                    "USER"))));
        }
        catch (UserManagementException umx)
        {
            Assert.fail(umx.getMessage());
        }
    }

    @Test
    public void modifyUserEmail()
    {
        try
        {
            final UserManager manager = new LDAPUserManager();

            Assert.assertTrue(manager.modifyUserEmail("junit-test", "test@test.com"));
        }
        catch (UserManagementException umx)
        {
            Assert.fail(umx.getMessage());
        }
    }

    @Test
    public void modifyUserContact()
    {
        try
        {
            final UserManager manager = new LDAPUserManager();

            Assert.assertTrue(manager.modifyUserContact("junit-test", new ArrayList<>(
                    Arrays.asList(
                            "716-341-5669",
                            "716-341-5669"))));
        }
        catch (UserManagementException umx)
        {
            Assert.fail(umx.getMessage());
        }
    }

    @Test
    public void modifyUserSuspension()
    {
        try
        {
            final UserManager manager = new LDAPUserManager();

            Assert.assertTrue(manager.modifyUserSuspension("junit-test", true));
        }
        catch (UserManagementException umx)
        {
            Assert.fail(umx.getMessage());
        }
    }

    @Test
    public void modifyUserRole()
    {
        try
        {
            final UserManager manager = new LDAPUserManager();

            Assert.assertTrue(manager.modifyUserGroups("junit-test", new Object[] { "Service Operator" } ));
        }
        catch (UserManagementException umx)
        {
            Assert.fail(umx.getMessage());
        }
    }

    @Test
    public void lockOnlineReset()
    {
        try
        {
            final UserManager manager = new LDAPUserManager();

            Assert.assertTrue(manager.modifyOlrLock("junit-test", true));
        }
        catch (UserManagementException umx)
        {
            Assert.fail(umx.getMessage());
        }
    }

    @Test
    public void clearLockCount()
    {
        try
        {
            final UserManager manager = new LDAPUserManager();

            Assert.assertTrue(manager.modifyUserLock("junit-test", false, 0));
        }
        catch (UserManagementException umx)
        {
            Assert.fail(umx.getMessage());
        }
    }

    @Test
    public void lockUserAccount()
    {
        try
        {
            final UserManager manager = new LDAPUserManager();

            Assert.assertTrue(manager.modifyUserLock("junit-test", true, 0));
        }
        catch (UserManagementException umx)
        {
            Assert.fail(umx.getMessage());
        }
    }

    @Test
    public void changeUserPassword()
    {
        try
        {
            final UserManager manager = new SQLUserManager();

            Assert.assertTrue(manager.modifyUserPassword("junit-test",
                    "6TPeXOxpCKce2wPZMM3nIGtbN2BRk31guOO7utNwfvtjmGxqvLhBi/Atd0ZzxDlR2/5l0cKJgqiMTHCoXPjhwQ=="));
        }
        catch (UserManagementException umx)
        {
            Assert.fail(umx.getMessage());
        }
    }

    @Test
    public void changeUserSecurity()
    {
        try
        {
            final UserManager manager = new LDAPUserManager();

            Assert.assertTrue(manager.modifyUserSecurity("junit-test",
                    new ArrayList<>(
                            Arrays.asList(
                                    "question 1",
                                    "question 2",
                                    "answer 1",
                                    "answer 2"))));
        }
        catch (UserManagementException umx)
        {
            Assert.fail(umx.getMessage());
        }
    }

    @Test
    public void searchUsers()
    {
        try
        {
            final UserManager manager = new LDAPUserManager();

            Assert.assertNotNull(manager.searchUsers(SQLUserManagerTest.GUID));
        }
        catch (UserManagementException umx)
        {
            Assert.fail(umx.getMessage());
        }
    }

    @Test
    public void loadUserAccount()
    {
        try
        {
            final UserManager manager = new LDAPUserManager();

            Assert.assertNotNull(manager.loadUserAccount(SQLUserManagerTest.GUID));
        }
        catch (UserManagementException umx)
        {
            Assert.fail(umx.getMessage());
        }
    }

    @Test
    public void removeUserAccount()
    {
        try
        {
            final UserManager manager = new LDAPUserManager();

            Assert.assertTrue(manager.removeUserAccount("junit-test"));
        }
        catch (UserManagementException umx)
        {
            Assert.fail(umx.getMessage());
        }
    }

    @After
    public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
