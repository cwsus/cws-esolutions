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
 * File: PlatformDataDAOImplTest.java
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
import org.junit.jupiter.api.TestInstance;

import com.cws.esolutions.core.dao.interfaces.IDatacenterDataDAO;
import com.cws.esolutions.core.listeners.CoreServicesInitializer;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DatacenterDAOImplTest
{
    private static final IDatacenterDataDAO dao = (IDatacenterDataDAO) new DatacenterDataDAOImpl();

    @BeforeAll public void setUp()
    {
        try
        {
            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/logging/logging.xml", true);
            CoreServicesInitializer.initializeService("eSolutionsCore/config/ServiceConfig.xml", "eSolutionsCore/logging/logging.xml", true, true);
        }
        catch (final Exception ex)
        {
            Assertions.fail(ex.getMessage());

            System.exit(-1);
        }
    }

    @Test public void addDatacenter()
    {
    	List<String> data = new ArrayList<String>(
    			Arrays.asList(
    					"0771a4c5-683a-47d1-8d56-f5ec89760d80",
    					"TD",
    					"ACTIVE",
    					"Test Datacenter"));

        try
        {
            Assertions.assertThat(dao.addDatacenter(data)).isTrue();
        }
        catch (final SQLException sqx)
        {
            Assertions.fail(sqx.getMessage());
        }
    }

    @Test public void updateDatacenter()
    {
    	List<String> data = new ArrayList<String>(
    			Arrays.asList(
    					"0771a4c5-683a-47d1-8d56-f5ec89760d80",
    					"CH",
    					"ACTIVE",
    					"Test Datacenter"));

        try
        {
            Assertions.assertThat(dao.updateDatacenter(data)).isTrue();
        }
        catch (final SQLException sqx)
        {
            Assertions.fail(sqx.getMessage());
        }
    }

    @Test public void removeDatacenter()
    {
        try
        {
            Assertions.assertThat(dao.removeDatacenter("0771a4c5-683a-47d1-8d56-f5ec89760d79")).isTrue();
        }
        catch (final SQLException sqx)
        {
            Assertions.fail(sqx.getMessage());
        }
    }

    @Test public void listDatacenters()
    {
        try
        {
            Assertions.assertThat(dao.listDatacenters(0)).isNotEmpty();
        }
        catch (final SQLException sqx)
        {
            Assertions.fail(sqx.getMessage());
        }
    }

    @Test public void getDatacentersByAttribute()
    {
        try
        {
            Assertions.assertThat(dao.getDatacentersByAttribute("CH", 0)).isNotEmpty();
        }
        catch (final SQLException sqx)
        {
            Assertions.fail(sqx.getMessage());
        }
    }

    @Test public void getDatacenter()
    {
        try
        {
            Assertions.assertThat(dao.getDatacenter("0771a4c5-683a-47d1-8d56-f5ec89760d79")).isNotEmpty();
        }
        catch (final SQLException sqx)
        {
            Assertions.fail(sqx.getMessage());
        }
    }

    @AfterAll public void tearDown()
    {
        CoreServicesInitializer.shutdown();
        SecurityServiceInitializer.shutdown();
    }
}
