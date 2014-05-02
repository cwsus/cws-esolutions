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
 * File: LDAPUserManagerTest.java
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
import com.cws.esolutions.security.dao.usermgmt.factory.UserManagerFactory;
import com.cws.esolutions.security.dao.usermgmt.exception.UserManagementException;

public class LDAPUserManagerTest
{
    private static final String GUID = UUID.randomUUID().toString();
    private static final UserManager manager = UserManagerFactory.getUserManager("com.cws.esolutions.security.dao.usermgmt.impl.LDAPUserManager");

    @Before public void setUp()
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

    @Test public void validateUserAccount()
    {
        try
        {
            Assert.assertTrue(manager.validateUserAccount("junit-test", LDAPUserManagerTest.GUID));
        }
        catch (UserManagementException umx)
        {
            Assert.fail(umx.getMessage());
        }
    }

    @Test public void addUserAccount()
    {
        try
        {
            Assert.assertTrue(manager.addUserAccount(
                    new ArrayList<>(
                            Arrays.asList(
                                    "junit-test",
                                    RandomStringUtils.randomAlphanumeric(64),
                                    "Test",
                                    "User",
                                    "test@test.com",
                                    LDAPUserManagerTest.GUID,
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

    @Test public void listUserAccounts()
    {
        try
        {
            Assert.assertNotNull(manager.listUserAccounts());
        }
        catch (UserManagementException umx)
        {
            Assert.fail(umx.getMessage());
        }
    }

    @Test public void searchUsers()
    {
        try
        {
            Assert.assertNotNull(manager.searchUsers("junit-test"));
        }
        catch (UserManagementException umx)
        {
            Assert.fail(umx.getMessage());
        }
    }

    @Test public void loadUserAccount()
    {
        try
        {
            Assert.assertNotNull(manager.loadUserAccount(LDAPUserManagerTest.GUID));
        }
        catch (UserManagementException umx)
        {
            Assert.fail(umx.getMessage());
        }
    }

    @Test public void modifyUserEmail()
    {
        try
        {
            Assert.assertTrue(manager.modifyUserEmail(LDAPUserManagerTest.GUID, "test@test.com"));
        }
        catch (UserManagementException umx)
        {
            Assert.fail(umx.getMessage());
        }
    }

    @Test public void modifyUserContact()
    {
        try
        {
            Assert.assertTrue(manager.modifyUserContact(LDAPUserManagerTest.GUID,
                    new ArrayList<>(
                            Arrays.asList("555-555-1212", "555-555-1213"))));
        }
        catch (UserManagementException umx)
        {
            Assert.fail(umx.getMessage());
        }
    }

    @Test public void modifyUserSuspension()
    {
        try
        {
            Assert.assertTrue(manager.modifyUserSuspension(LDAPUserManagerTest.GUID, true));
            Assert.assertTrue(manager.modifyUserSuspension(LDAPUserManagerTest.GUID, false));
        }
        catch (UserManagementException umx)
        {
            Assert.fail(umx.getMessage());
        }
    }

    @Test public void modifyUserGroups()
    {
        try
        {
            Assert.assertTrue(manager.modifyUserGroups(LDAPUserManagerTest.GUID, new Object[] { "Service Admins" }));
        }
        catch (UserManagementException umx)
        {
            Assert.fail(umx.getMessage());
        }
    }

    @Test public void modifyOlrLock()
    {
        try
        {
            Assert.assertTrue(manager.modifyOlrLock(LDAPUserManagerTest.GUID, true));
            Assert.assertTrue(manager.modifyOlrLock(LDAPUserManagerTest.GUID, false));
        }
        catch (UserManagementException umx)
        {
            Assert.fail(umx.getMessage());
        }
    }

    @Test public void modifyUserLock()
    {
        try
        {
            Assert.assertTrue(manager.modifyUserLock(LDAPUserManagerTest.GUID, true, 0));
            Assert.assertTrue(manager.modifyUserLock(LDAPUserManagerTest.GUID, false, 1));
            Assert.assertTrue(manager.modifyUserLock(LDAPUserManagerTest.GUID, false, 0));
        }
        catch (UserManagementException umx)
        {
            Assert.fail(umx.getMessage());
        }
    }

    @Test public void modifyUserPassword()
    {
        try
        {
            Assert.assertTrue(manager.modifyUserPassword(LDAPUserManagerTest.GUID, RandomStringUtils.randomAlphanumeric(64)));
        }
        catch (UserManagementException umx)
        {
            Assert.fail(umx.getMessage());
        }
    }

    @Test public void modifyUserSecurity()
    {
        try
        {
            Assert.assertTrue(manager.modifyUserSecurity(LDAPUserManagerTest.GUID, new ArrayList<>(
                    Arrays.asList(
                            RandomStringUtils.randomAlphanumeric(64),
                            RandomStringUtils.randomAlphanumeric(64),
                            RandomStringUtils.randomAlphanumeric(64),
                            RandomStringUtils.randomAlphanumeric(64)))));
        }
        catch (UserManagementException umx)
        {
            Assert.fail(umx.getMessage());
        }
    }

    @Test public void modifyOtpSecret()
    {
        try
        {
            Assert.assertTrue(manager.modifyOtpSecret(LDAPUserManagerTest.GUID, true, RandomStringUtils.randomAlphanumeric(64)));
            Assert.assertTrue(manager.modifyOtpSecret(LDAPUserManagerTest.GUID, false, null));
        }
        catch (UserManagementException umx)
        {
            Assert.fail(umx.getMessage());
        }
    }

    @Test public void removeUserAccount()
    {
        try
        {
            Assert.assertTrue(manager.removeUserAccount(LDAPUserManagerTest.GUID));
        }
        catch (UserManagementException umx)
        {
            Assert.fail(umx.getMessage());
        }
    }

    @After public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
