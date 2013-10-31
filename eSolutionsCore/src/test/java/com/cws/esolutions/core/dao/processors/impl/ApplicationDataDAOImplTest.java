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

import com.cws.esolutions.core.listeners.CoreServiceInitializer;
import com.cws.esolutions.core.dao.processors.interfaces.IApplicationDataDAO;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.dao.processors.impl
 * ApplicationDataDAOImplTest.java
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
 * 35033355 @ Jun 4, 2013 9:21:51 AM
 *     Created.
 */
public class ApplicationDataDAOImplTest
{
    private static final IApplicationDataDAO dao = new ApplicationDataDAOImpl();

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
    public final void testAddNewApplication()
    {
        List<String> appData = new ArrayList<String>(
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
            Assert.assertTrue(dao.addNewApplication(appData));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public final void testUpdateApplication()
    {

    }

    @Test
    public final void testDeleteApplication()
    {

    }

    @Test
    public final void testListInstalledApplications()
    {
        try
        {
            Assert.assertNotNull(dao.listInstalledApplications(0));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public final void testGetApplicationData()
    {
        try
        {
            Assert.assertNotNull(dao.getApplicationData("6625fc8c-09ed-4579-a3d6-eb43d26b679f"));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public final void testGetApplicationsByAttributeAppName()
    {
        try
        {
            Assert.assertNotNull(dao.getApplicationsByAttribute("eSolutions", 0));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }
}
