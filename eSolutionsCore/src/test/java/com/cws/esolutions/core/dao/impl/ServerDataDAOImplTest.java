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
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.assertj.core.api.Assertions;
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

    @BeforeAll public void setUp()
    {
        try
        {
            CoreServiceInitializer.initializeService("eSolutionsCore/config/ServiceConfig.xml", "logging/logging.xml", true, false);
        }
        catch (Exception ex)
        {
            Assertions.fail(ex.getMessage());

            System.exit(-1);
        }
    }

    @Test public void addNewServer()
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
            Assertions.assertThat(dao.addServer(data)).isTrue();
        }
        catch (SQLException sqx)
        {
            Assertions.fail(sqx.getMessage());
        }
    }

    @Test public void modifyServerData()
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
            Assertions.assertThat(dao.updateServer("DMGRSERVER", data)).isNotNull();
        }
        catch (SQLException sqx)
        {
            Assertions.fail(sqx.getMessage());
        }
    }

    @Test public void getInstalledServer()
    {
        try
        {
            Assertions.assertThat(dao.getServer("DMGRSERVER")).isNotEmpty();
        }
        catch (SQLException sqx)
        {
            Assertions.fail(sqx.getMessage());
        }
    }

    @Test public void getInstalledServers()
    {
        try
        {
            Assertions.assertThat(dao.listServers(0)).isNotEmpty();
        }
        catch (SQLException sqx)
        {
            Assertions.fail(sqx.getMessage());
        }
    }

    @Test public void getServersByAttribute()
    {
        try
        {
            Assertions.assertThat(dao.getServersByAttribute("DMGRSERVER DEV", 0)).isNotEmpty();
        }
        catch (SQLException sqx)
        {
            Assertions.fail(sqx.getMessage());
        }
    }

    @Test public void removeExistingServer()
    {
        try
        {
            Assertions.assertThat(dao.removeServer(this.guid)).isNotNull();
        }
        catch (SQLException sqx)
        {
            Assertions.fail(sqx.getMessage());
        }
    }

    @AfterAll public void tearDown()
    {
        CoreServiceInitializer.shutdown();
    }
}
