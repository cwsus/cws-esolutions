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
package com.cws.esolutions.core.dao.processors.impl;

import java.util.List;
import java.util.UUID;

import org.junit.Test;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;

import java.util.ArrayList;
import java.sql.SQLException;

import org.apache.commons.lang.RandomStringUtils;

import com.cws.esolutions.core.processors.enums.ServerType;
import com.cws.esolutions.core.processors.enums.ServiceRegion;
import com.cws.esolutions.core.processors.enums.ServiceStatus;
import com.cws.esolutions.core.listeners.CoreServiceInitializer;
import com.cws.esolutions.core.dao.processors.interfaces.IServerDataDAO;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.dao.processors.impl
 * ServerDataDAOImplTest.java
 *
 *
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
 * 35033355 @ Jun 4, 2013 3:10:12 PM
 *     Created.
 */
public class ServerDataDAOImplTest
{
    private static final IServerDataDAO dao = new ServerDataDAOImpl();

    @Before
    public final void setUp() throws Exception
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
    public final void testAddNewServer()
    {
        List<Object> data = new ArrayList<Object>
        (
            Arrays.asList
            (
                UUID.randomUUID().toString(),
                "CentOS",
                ServiceStatus.ACTIVE.name(),
                ServiceRegion.DEV.name(),
                ServerType.APPSERVER.name(),
                "caspersbox.com",
                "AMD Athlon 1.0 GHz",
                1,
                "VPS",
                RandomStringUtils.randomAlphanumeric(8).toUpperCase(),
                512,
                "127.0.0.1",
                RandomStringUtils.randomAlphanumeric(8).toLowerCase(),
                "127.0.0.1",
                RandomStringUtils.randomAlphanumeric(8).toLowerCase(),
                "127.0.0.1",
                RandomStringUtils.randomAlphanumeric(8).toLowerCase(),
                "127.0.0.1",
                RandomStringUtils.randomAlphanumeric(8).toLowerCase(),
                "Unconfigured",
                "JUnit test",
                "khuntly",
                "Unconfigured",
                0,
                "Unconfigured",
                "Unconfigured",
                "Unconfigured"
            )
        );

        try
        {
            Assert.assertTrue(dao.addNewServer(data));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public final void testRemoveExistingServer()
    {

    }

    @Test
    public final void testGetInstalledServers()
    {

    }

    @Test
    public final void testGetInstalledServer()
    {

    }

    @Test
    public final void testGetServersByAttribute()
    {

    }

    @Test
    public final void testModifyServerData()
    {

    }
}
