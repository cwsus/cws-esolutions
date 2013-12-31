/*
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
package com.cws.esolutions.core.dao.impl;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.dao.impl
 * File: PlatformDataDAOImplTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.UUID;
import java.util.List;
import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;
import java.util.Arrays;
import java.util.ArrayList;
import java.sql.SQLException;

import com.cws.esolutions.core.dao.interfaces.IPlatformDataDAO;
import com.cws.esolutions.core.listeners.CoreServiceInitializer;

public class PlatformDataDAOImplTest
{
    private static final IPlatformDataDAO dao = new PlatformDataDAOImpl();

    @Before
    public void setUp()
    {
        try
        {
            CoreServiceInitializer.initializeService("eSolutionsCore/config/ServiceConfig.xml", "logging/logging.xml");
        }
        catch (Exception ex)
        {
            Assert.fail(ex.getMessage());

            System.exit(-1);
        }
    }

    @Test
    public void testAddNewPlatform()
    {
        List<String> appServers = new ArrayList<>(
                Arrays.asList(
                        "eb98ef5a-5fc0-44b3-a01b-4bdb382aa716",
                        "2a515fa2-d1a8-4817-91b0-6a0547fea48b"));

        List<String> webServers = new ArrayList<>(
                Arrays.asList(
                        "250892d5-03fb-436c-8125-3d9d30de8f68",
                        "40d4ba5e-ed50-4a83-a6a3-fd7d32631a26"));

        List<String> platformData = new ArrayList<>(
                Arrays.asList(
                        UUID.randomUUID().toString(), // guid
                        "VH_I", // name
                        "DEV", // region
                        "cbddebc8-9bdb-4a6b-8c70-b39959ace0ce", // dmgr
                        appServers.toString(), // appservers
                        webServers.toString(), // webservers
                        "ACTIVE", // status
                        "Test Platform")); // desc

        try
        {
            Assert.assertTrue(PlatformDataDAOImplTest.dao.addNewPlatform(platformData));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public void testGetPlatformData()
    {
        try
        {
            Assert.assertNotNull(PlatformDataDAOImplTest.dao.getPlatformData("c0b20624-0a0c-4cf6-a8dc-62efc5a46e18"));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }
}
