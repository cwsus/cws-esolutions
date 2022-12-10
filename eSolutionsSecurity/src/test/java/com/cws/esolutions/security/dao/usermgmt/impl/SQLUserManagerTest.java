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
 * File: SQLUserManagerTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.util.Arrays;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.apache.commons.lang3.RandomStringUtils;

import com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.security.dao.usermgmt.factory.UserManagerFactory;
import com.cws.esolutions.security.dao.usermgmt.exception.UserManagementException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SQLUserManagerTest
{
	private static final String GUID = "99aaefc1-8a2a-4877-bed5-20b73d971e56";
    private static final UserManager manager = UserManagerFactory.getUserManager("com.cws.esolutions.security.dao.usermgmt.impl.SQLUserManager");

    @BeforeAll public void setUp()
    {
        try
        {
            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/logging/logging.xml", true);
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
            System.exit(1);
        }
    }

    @Test public void addUserAccount()
    {
        try
        {
            Assertions.assertThat(manager.addUserAccount(
                    new ArrayList<Object>(
                            Arrays.asList(
                                    SQLUserManagerTest.GUID, // stmt.setString(1, (String) userAccount.get(0)); // commonName NVARCHAR(128),
                            		"junit-test", // stmt.setString(2, (String) userAccount.get(1)); // uid NVARCHAR(45),
                            		"Junit", // stmt.setString(3, (String) userAccount.get(2)); // givenname NVARCHAR(100),
                            		"Test", // stmt.setString(4, (String) userAccount.get(3)); // sn NVARCHAR(100),
                            		"JUnit Test", // stmt.setString(5, (String) userAccount.get(4)); // displayname NVARCHAR(100),
                            		"junit@caspersbox.com", // stmt.setString(6, (String) userAccount.get(5)); // email NVARCHAR(50),
                            		"USER", // stmt.setString(7, (String) userAccount.get(6)); // cwsrole NVARCHAR(45),
                            		0, // stmt.setInt(8, (int) userAccount.get(7)); // cwsfailedpwdcount MEDIUMINT(9),
                            		false, // stmt.setBoolean(9, (Boolean) userAccount.get(8)); // cwsissuspended BOOLEAN,
                            		true, // stmt.setBoolean(10, (Boolean) userAccount.get(9)); // cwsisolrsetup BOOLEAN,
                            		false, // stmt.setBoolean(11, (Boolean) userAccount.get(10)); // cwsisolrlocked BOOLEAN,
                            		false, // stmt.setBoolean(12, (Boolean) userAccount.get(11)); // cwsistcaccepted BOOLEAN,
                            		"7161231234", // stmt.setString(13, (String) userAccount.get(12)); // telephonenumber NVARCHAR(10),
                            		"7161231234", // stmt.setString(14, (String) userAccount.get(13)); // pager NVARCHAR(10),
                            		RandomStringUtils.randomAlphanumeric(64))), // stmt.setString(15, (String) userAccount.get(14)); // userpassword NVARCHAR(255)
                    new ArrayList<String>(
                            Arrays.asList(
                                    "USER")))).isTrue();
        }
        catch (final UserManagementException umx)
        {
            Assertions.fail(umx.getMessage());
        }
    }

    @Test public void validateUserAccount()
    {
        try
        {
            Assertions.assertThat(manager.validateUserAccount("junit-test", "99aaefc1-8a2a-4877-bed5-20b73d971e56")).isTrue();
        }
        catch (final UserManagementException umx)
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
        catch (final UserManagementException umx)
        {
            Assertions.fail(umx.getMessage());
        }
    }

    @Test public void searchUsers()
    {
        try
        {
            Assertions.assertThat(manager.searchUsers("junit-test")).isNotEmpty();
        }
        catch (final UserManagementException umx)
        {
            Assertions.fail(umx.getMessage());
        }
    }

    @Test public void loadUserAccount()
    {
        try
        {
            Assertions.assertThat(manager.loadUserAccount(SQLUserManagerTest.GUID)).isNotEmpty();
        }
        catch (final UserManagementException umx)
        {
            Assertions.fail(umx.getMessage());
        }
    }

    @Test public void modifyUserEmail()
    {
        try
        {
            Assertions.assertThat(manager.modifyUserEmail(SQLUserManagerTest.GUID, "test@test.com")).isTrue();
        }
        catch (final UserManagementException umx)
        {
            Assertions.fail(umx.getMessage());
        }
    }

    @Test public void modifyUserContact()
    {
        try
        {
            Assertions.assertThat(manager.modifyUserContact(SQLUserManagerTest.GUID,
                    new ArrayList<String>(
                            Arrays.asList("555-555-1212", "555-555-1213"))));
        }
        catch (final UserManagementException umx)
        {
            Assertions.fail(umx.getMessage());
        }
    }

    @Test public void modifyUserSuspension()
    {
        try
        {
            Assertions.assertThat(manager.modifyUserSuspension(SQLUserManagerTest.GUID, true));
            Assertions.assertThat(manager.modifyUserSuspension(SQLUserManagerTest.GUID, false));
        }
        catch (final UserManagementException umx)
        {
            Assertions.fail(umx.getMessage());
        }
    }

    @Test public void modifyUserGroups()
    {
        try
        {
            Assertions.assertThat(manager.modifyUserGroups(SQLUserManagerTest.GUID, new Object[] { "Service Admins" }));
        }
        catch (final UserManagementException umx)
        {
            Assertions.fail(umx.getMessage());
        }
    }

    @Test public void modifyOlrLock()
    {
        try
        {
            Assertions.assertThat(manager.modifyOlrLock(SQLUserManagerTest.GUID, true));
            Assertions.assertThat(manager.modifyOlrLock(SQLUserManagerTest.GUID, false));
        }
        catch (final UserManagementException umx)
        {
            Assertions.fail(umx.getMessage());
        }
    }

    @Test public void modifyUserLock()
    {
        try
        {
            Assertions.assertThat(manager.modifyUserLock(SQLUserManagerTest.GUID, true, 0));
            Assertions.assertThat(manager.modifyUserLock(SQLUserManagerTest.GUID, false, 1));
            Assertions.assertThat(manager.modifyUserLock(SQLUserManagerTest.GUID, false, 0));
        }
        catch (final UserManagementException umx)
        {
            Assertions.fail(umx.getMessage());
        }
    }

    @Test public void modifyUserPassword()
    {
        try
        {
            Assertions.assertThat(manager.modifyUserPassword(SQLUserManagerTest.GUID, RandomStringUtils.randomAlphanumeric(64)));
        }
        catch (final UserManagementException umx)
        {
            Assertions.fail(umx.getMessage());
        }
    }

    @Test public void modifyUserSecurity()
    {
        try
        {
            Assertions.assertThat(manager.modifyUserSecurity(SQLUserManagerTest.GUID, new ArrayList<String>(
                    Arrays.asList(
                            RandomStringUtils.randomAlphanumeric(64),
                            RandomStringUtils.randomAlphanumeric(64),
                            RandomStringUtils.randomAlphanumeric(64),
                            RandomStringUtils.randomAlphanumeric(64)))));
        }
        catch (final UserManagementException umx)
        {
            Assertions.fail(umx.getMessage());
        }
    }

    @Test public void modifyOtpSecret()
    {
        try
        {
            Assertions.assertThat(manager.modifyOtpSecret(SQLUserManagerTest.GUID, true, RandomStringUtils.randomAlphanumeric(64)));
            Assertions.assertThat(manager.modifyOtpSecret(SQLUserManagerTest.GUID, false, null));
        }
        catch (final UserManagementException umx)
        {
            Assertions.fail(umx.getMessage());
        }
    }

    @Test public void removeUserAccount()
    {
        try
        {
            Assertions.assertThat(manager.removeUserAccount(SQLUserManagerTest.GUID));
        }
        catch (final UserManagementException umx)
        {
            Assertions.fail(umx.getMessage());
        }
    }

    @AfterAll public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
