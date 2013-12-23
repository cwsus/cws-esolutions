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
package com.cws.esolutions.core.dao.processors.impl;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.dao.processors.impl
 * File: ApplicationDataDAOImplTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.List;
import java.util.UUID;
import org.junit.Test;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Before;
import java.util.ArrayList;
import java.sql.SQLException;

import com.cws.esolutions.core.listeners.CoreServiceInitializer;
import com.cws.esolutions.core.dao.processors.interfaces.IApplicationDataDAO;

public class ApplicationDataDAOImplTest
{
    private static final IApplicationDataDAO dao = new ApplicationDataDAOImpl();

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
    public void testAddNewApplication()
    {
        List<Object> appData = new ArrayList<Object>(
                Arrays.asList(
                        UUID.randomUUID().toString(),
                        "eSolutions",
                        "c0b20624-0a0c-4cf6-a8dc-62efc5a46e18",
                        "CWS",
                        "1.0",
                        "/appvol/ATS70/eSolutions/applogs",
                        "2aa547e9-3a6e-4720-95d9-6521c862ef2a",
                        "/appvol/ATS70/eSolutions/eSolutions_web_source-1.0.war",
                        "/appvol/ATS70/eSolutions/syslogs/"));

        try
        {
            Assert.assertTrue(ApplicationDataDAOImplTest.dao.addNewApplication(appData));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public void testListInstalledApplications()
    {
        try
        {
            Assert.assertNotNull(ApplicationDataDAOImplTest.dao.listInstalledApplications(0));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public void testGetApplicationData()
    {
        try
        {
            Assert.assertNotNull(ApplicationDataDAOImplTest.dao.getApplicationData("6625fc8c-09ed-4579-a3d6-eb43d26b679f"));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public void testGetApplicationsByAttributeAppName()
    {
        try
        {
            Assert.assertNotNull(ApplicationDataDAOImplTest.dao.getApplicationsByAttribute("eSolutions", 0));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }
}
