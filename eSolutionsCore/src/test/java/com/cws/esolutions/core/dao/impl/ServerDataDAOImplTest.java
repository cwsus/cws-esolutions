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
 * File: ServerDataDAOImplTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.List;
import org.junit.Test;
import org.junit.After;
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
import com.cws.esolutions.core.dao.interfaces.IServerDataDAO;

public class ServerDataDAOImplTest
{
    private String guid = "77adebf4-a7b7-4aea-8dbd-a54bc0b5897f";
    private static final IServerDataDAO dao = new ServerDataDAOImpl();

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
    public void addNewServer()
    {
        List<Object> data = new ArrayList<Object>
        (
            Arrays.asList
            (
                this.guid,
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
    public void modifyServerData()
    {
        List<Object> data = new ArrayList<Object>
        (
            Arrays.asList
            (
                this.guid,
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
            Assert.assertNotNull(dao.modifyServerData("DMGRSERVER", data));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public void getInstalledServer()
    {
        try
        {
            Assert.assertNotNull(dao.getInstalledServer("DMGRSERVER"));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public void validateServerHostName()
    {
        try
        {
            Assert.assertNotNull(dao.validateServerHostName("DMGRSERVER"));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public void getInstalledServers()
    {
        try
        {
            Assert.assertNotNull(dao.getInstalledServers(0));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public void getServersByAttribute()
    {
        try
        {
            Assert.assertNotNull(dao.getServersByAttribute("DMGRSERVER DEV", 0));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public void getServerCount()
    {
        try
        {
            Assert.assertNotNull(dao.getServerCount());
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public void removeExistingServer()
    {
        try
        {
            Assert.assertNotNull(dao.removeExistingServer(this.guid));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public void archiveServerData()
    {
        try
        {
            dao.archiveServerData(this.guid);
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public void getRetiredServers()
    {
        try
        {
            Assert.assertNotNull(dao.getRetiredServers(0));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public void getRetiredServer()
    {
        try
        {
            Assert.assertNotNull(dao.getRetiredServer(this.guid));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @After
    public void tearDown()
    {
        CoreServiceInitializer.shutdown();
    }
}
