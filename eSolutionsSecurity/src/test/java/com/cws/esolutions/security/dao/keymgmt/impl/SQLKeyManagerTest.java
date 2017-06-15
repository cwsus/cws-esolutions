/*
 * Copyright (c) 2009 - 2017 CaspersBox Web Services
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
 * File: SQLKeyManagerTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly   11/23/2008 22:39:20             Created.
 */
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.Assert;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.security.dao.keymgmt.interfaces.KeyManager;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.security.dao.keymgmt.factory.KeyManagementFactory;
import com.cws.esolutions.security.dao.keymgmt.exception.KeyManagementException;

public class SQLKeyManagerTest
{
    private static UserAccount userAccount = new UserAccount();
    private static RequestHostInfo hostInfo = new RequestHostInfo();

    @Before public void setUp()
    {
        try
        {
            hostInfo.setHostAddress("junit");
            hostInfo.setHostName("junit");

            userAccount.setStatus(LoginStatus.SUCCESS);
            userAccount.setGuid("f42fb0ba-4d1e-1126-986f-800cd2650000");
            userAccount.setUsername("junit");

            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/logging/logging.xml", true);
        }
        catch (Exception ex)
        {
            Assert.fail(ex.getMessage());

            System.exit(-1);
        }
    }

    @Test public void createKeys()
    {
        KeyManager processor = KeyManagementFactory.getKeyManager("com.cws.esolutions.security.dao.keymgmt.impl.SQLKeyManager");

        try
        {
            Assert.assertTrue(processor.createKeys(userAccount.getGuid()));
        }
        catch (KeyManagementException kmx)
        {
            Assert.fail(kmx.getMessage());
        }
    }

    @Test public void returnKeys()
    {
        KeyManager processor = KeyManagementFactory.getKeyManager("com.cws.esolutions.security.dao.keymgmt.impl.SQLKeyManager");

        try
        {
            Assert.assertNotNull(processor.returnKeys(userAccount.getGuid()));
        }
        catch (KeyManagementException kmx)
        {
            Assert.fail(kmx.getMessage());
        }
    }

    @Test public void removeKeys()
    {
        KeyManager processor = KeyManagementFactory.getKeyManager("com.cws.esolutions.security.dao.keymgmt.impl.SQLKeyManager");

        try
        {
            Assert.assertTrue(processor.removeKeys(userAccount.getGuid()));
        }
        catch (KeyManagementException kmx)
        {
            Assert.fail(kmx.getMessage());
        }
    }

    @After public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
