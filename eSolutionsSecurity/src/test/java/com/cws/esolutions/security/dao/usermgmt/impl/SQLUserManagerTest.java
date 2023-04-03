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

import com.cws.esolutions.security.utils.PasswordUtils;
import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.enums.SecurityUserRole;
import com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.security.dao.usermgmt.factory.UserManagerFactory;
import com.cws.esolutions.security.dao.usermgmt.exception.UserManagementException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SQLUserManagerTest
{
	private static final String GUID = "99aaefc1-8a2a-4877-bed5-20b73d971e56";
	private static final SecurityServiceBean bean = SecurityServiceBean.getInstance();
    private static final UserManager manager = UserManagerFactory.getUserManager("com.cws.esolutions.security.dao.usermgmt.impl.SQLUserManager");

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

    @Test public void addUserAccount()
    {
        try
        {
            Assertions.assertThat(manager.addUserAccount(
                    new ArrayList<String>(
                            Arrays.asList(
                            		GUID,
                            		"junit-test",
                            		PasswordUtils.encryptText(
                            				RandomStringUtils.randomAlphanumeric(32).toCharArray(),
                            				PasswordUtils.returnGeneratedSalt(
                            						bean.getConfigData().getSecurityConfig().getRandomGenerator(), bean.getConfigData().getSecurityConfig().getSaltLength()),
                            				bean.getConfigData().getSecurityConfig().getSecretKeyAlgorithm(),
                            				bean.getConfigData().getSecurityConfig().getIterations(),
                            				bean.getConfigData().getSecurityConfig().getKeyLength(),
                            				//bean.getConfigData().getSecurityConfig().getEncryptionAlgorithm(),
                            				//bean.getConfigData().getSecurityConfig().getEncryptionInstance(),
                                            bean.getConfigData().getSystemConfig().getEncoding()),
                            		SecurityUserRole.NONE.toString(),
                            		"junit",
                            		"test",
                            		"junit@test.com",
                            		"1234567890",
                            		"1987654321")))).isTrue();
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
            Assertions.assertThat(manager.validateUserAccount("junit-test", GUID)).isTrue();
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
            Assertions.assertThat(manager.modifyUserEmail(SQLUserManagerTest.GUID, "foo@bar.com")).isTrue();
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
                            Arrays.asList("9078963341", "9089177215"))));
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
            Assertions.assertThat(manager.modifyUserRole(SQLUserManagerTest.GUID, SecurityUserRole.USER.toString()));
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
