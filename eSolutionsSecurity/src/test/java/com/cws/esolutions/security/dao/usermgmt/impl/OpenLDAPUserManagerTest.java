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
package com.cws.esolutions.security.dao.usermgmt.impl;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.dao.usermgmt.impl
 * File: OpenLDAPUserManagerTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.util.UUID;
import java.util.Arrays;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.assertj.core.api.Assertions;
import org.apache.commons.lang.RandomStringUtils;

import com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.security.dao.usermgmt.factory.UserManagerFactory;
import com.cws.esolutions.security.dao.usermgmt.exception.UserManagementException;

public class OpenLDAPUserManagerTest
{
    private static final String GUID = UUID.randomUUID().toString();
    private static final UserManager manager = UserManagerFactory.getUserManager("com.cws.esolutions.security.dao.usermgmt.impl.OpenLDAPUserManager");

    @BeforeAll public void setUp()
    {
        try
        {
            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/logging/logging.xml", true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
            System.exit(1);
        }
    }

    @Test public void validateUserAccount()
    {
        try
        {
        	Assertions.assertThat(manager.validateUserAccount("junit-test", OpenLDAPUserManagerTest.GUID)).isTrue();
        }
        catch (UserManagementException umx)
        {
        	Assertions.fail(umx.getMessage());
        }
    }

    @Test public void addUserAccount()
    {
        try
        {
        	Assertions.assertThat(manager.addUserAccount(
                    new ArrayList<String>(
                            Arrays.asList(
                                    "junit-test",
                                    RandomStringUtils.randomAlphanumeric(64),
                                    "Test",
                                    "User",
                                    "test@test.com",
                                    OpenLDAPUserManagerTest.GUID,
                                    "Test User")),
                    new ArrayList<String>(
                            Arrays.asList(
                                    "USER")))).isTrue();
        }
        catch (UserManagementException umx)
        {
        	Assertions.fail(umx.getMessage());
        }
    }

    @Test public void listUserAccounts()
    {
        try
        {
        	Assertions.assertThat(manager.listUserAccounts()).isNotEmpty();
        }
        catch (UserManagementException umx)
        {
        	Assertions.fail(umx.getMessage());
        }
    }

    @Test public void searchUsers()
    {
        try
        {
        	Assertions.assertThat(manager.searchUsers("cws-khuntly").isEmpty()).isFalse();
        }
        catch (UserManagementException umx)
        {
        	Assertions.fail(umx.getMessage());
        }
    }

    @Test public void loadUserAccount()
    {
        try
        {
        	Assertions.assertThat(manager.loadUserAccount("cws-khuntly").isEmpty()).isFalse();
        }
        catch (UserManagementException umx)
        {
        	Assertions.fail(umx.getMessage());
        }
    }

    @Test public void modifyUserEmail()
    {
        try
        {
        	Assertions.assertThat(manager.modifyUserEmail(OpenLDAPUserManagerTest.GUID, "test@test.com")).isTrue();
        }
        catch (UserManagementException umx)
        {
        	Assertions.fail(umx.getMessage());
        }
    }

    @Test public void modifyUserContact()
    {
        try
        {
        	Assertions.assertThat(manager.modifyUserContact(OpenLDAPUserManagerTest.GUID,
                    new ArrayList<String>(
                            Arrays.asList("555-555-1212", "555-555-1213")))).isTrue();
        }
        catch (UserManagementException umx)
        {
        	Assertions.fail(umx.getMessage());
        }
    }

    @Test public void modifyUserSuspension()
    {
        try
        {
        	Assertions.assertThat(manager.modifyUserSuspension(OpenLDAPUserManagerTest.GUID, true)).isTrue();
        	Assertions.assertThat(manager.modifyUserSuspension(OpenLDAPUserManagerTest.GUID, false)).isTrue();
        }
        catch (UserManagementException umx)
        {
        	Assertions.fail(umx.getMessage());
        }
    }

    @Test public void modifyUserGroups()
    {
        try
        {
        	Assertions.assertThat(manager.modifyUserGroups(OpenLDAPUserManagerTest.GUID, new Object[] { "Service Admins" })).isTrue();
        }
        catch (UserManagementException umx)
        {
        	Assertions.fail(umx.getMessage());
        }
    }

    @Test public void modifyOlrLock()
    {
        try
        {
        	Assertions.assertThat(manager.modifyOlrLock(OpenLDAPUserManagerTest.GUID, true)).isTrue();
        	Assertions.assertThat(manager.modifyOlrLock(OpenLDAPUserManagerTest.GUID, false)).isTrue();
        }
        catch (UserManagementException umx)
        {
        	Assertions.fail(umx.getMessage());
        }
    }

    @Test public void modifyUserLock()
    {
        try
        {
            Assertions.assertThat(manager.modifyUserLock(OpenLDAPUserManagerTest.GUID, true, 0)).isTrue();
            Assertions.assertThat(manager.modifyUserLock(OpenLDAPUserManagerTest.GUID, false, 1)).isTrue();
            Assertions.assertThat(manager.modifyUserLock(OpenLDAPUserManagerTest.GUID, false, 0)).isTrue();
        }
        catch (UserManagementException umx)
        {
            Assertions.fail(umx.getMessage());
        }
    }

    @Test public void modifyUserPassword()
    {
        try
        {
            Assertions.assertThat(manager.modifyUserPassword(OpenLDAPUserManagerTest.GUID, RandomStringUtils.randomAlphanumeric(64))).isTrue();
        }
        catch (UserManagementException umx)
        {
            Assertions.fail(umx.getMessage());
        }
    }

    @Test public void modifyUserSecurity()
    {
        try
        {
            Assertions.assertThat(manager.modifyUserSecurity(OpenLDAPUserManagerTest.GUID, new ArrayList<String>(
                    Arrays.asList(
                            RandomStringUtils.randomAlphanumeric(64),
                            RandomStringUtils.randomAlphanumeric(64),
                            RandomStringUtils.randomAlphanumeric(64),
                            RandomStringUtils.randomAlphanumeric(64))))).isTrue();
        }
        catch (UserManagementException umx)
        {
            Assertions.fail(umx.getMessage());
        }
    }

    @Test public void modifyOtpSecret()
    {
        try
        {
            Assertions.assertThat(manager.modifyOtpSecret(OpenLDAPUserManagerTest.GUID, true, RandomStringUtils.randomAlphanumeric(64))).isTrue();
            Assertions.assertThat(manager.modifyOtpSecret(OpenLDAPUserManagerTest.GUID, false, null)).isTrue();
        }
        catch (UserManagementException umx)
        {
            Assertions.fail(umx.getMessage());
        }
    }

    @Test public void removeUserAccount()
    {
        try
        {
            Assertions.assertThat(manager.removeUserAccount(OpenLDAPUserManagerTest.GUID)).isTrue();
        }
        catch (UserManagementException umx)
        {
            Assertions.fail(umx.getMessage());
        }
    }

    @AfterAll public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
