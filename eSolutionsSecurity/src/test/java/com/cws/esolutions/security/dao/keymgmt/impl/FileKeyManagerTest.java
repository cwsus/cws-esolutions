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
package com.cws.esolutions.security.dao.keymgmt.impl;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.keymgmt.impl
 * File: FileKeyManagerTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.TestInstance;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.utility.securityutils.processors.dto.RequestHostInfo;
import com.cws.esolutions.security.dao.keymgmt.interfaces.KeyManager;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.security.dao.keymgmt.factory.KeyManagementFactory;
import com.cws.esolutions.security.dao.keymgmt.exception.KeyManagementException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FileKeyManagerTest
{
    private static UserAccount userAccount = new UserAccount();
    private static RequestHostInfo hostInfo = new RequestHostInfo();

    KeyManager keyManager = (KeyManager) KeyManagementFactory.getKeyManager("com.cws.esolutions.security.dao.keymgmt.impl.FileKeyManager");

    @BeforeAll public void setUp()
    {
        try
        {
            hostInfo.setHostAddress("127.0.0.1");
            hostInfo.setHostName("junit.test.com");

            userAccount.setStatus(LoginStatus.SUCCESS);
            userAccount.setGuid("f42fb0ba-4d1e-1126-986f-800cd2650000");
            userAccount.setUsername("khuntly");

            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/logging/logging.xml", true);
        }
        catch (final Exception ex)
        {
            Assertions.fail(ex.getMessage());

            System.exit(-1);
        }
    }

	/**
	 * Test method for {@link com.cws.esolutions.security.dao.keymgmt.impl.FileKeyManager#createKeys(java.lang.String)}.
	 */
    @Test public void createKeys()
    {
        try
        {
        	Assertions.assertThat(keyManager.createKeys(userAccount.getGuid())).isTrue();
        }
        catch (final Exception kmx)
        {
        	Assertions.fail(kmx.getMessage());
        }
    }

    @Test public void returnKeys()
    {
        try
        {
        	Assertions.assertThat(keyManager.returnKeys(userAccount.getGuid())).isNotNull();
        }
        catch (final KeyManagementException kmx)
        {
        	Assertions.fail(kmx.getMessage());
        }
    }

    @Test public void removeKeys()
    {
        try
        {
        	Assertions.assertThat(keyManager.removeKeys(userAccount.getGuid())).isTrue();
        }
        catch (final KeyManagementException kmx)
        {
        	Assertions.fail(kmx.getMessage());
        }
    }

    @AfterAll public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
