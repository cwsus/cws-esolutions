/*
 * Copyright (c) 2009 - 2014 CaspersBox Web Services
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
 * File: ApplicationDataDAOImplTest.java
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

import com.cws.esolutions.core.listeners.CoreServiceInitializer;
import com.cws.esolutions.core.dao.interfaces.IApplicationDataDAO;

public class ApplicationDataDAOImplTest
{
    private String guid = "828f58b4-55f8-473a-9d75-17ae6cad3f6b";
    private static final IApplicationDataDAO dao = new ApplicationDataDAOImpl();

    @Before public void setUp()
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

    @Test public void addNewApplication()
    {
        List<Object> appData = new ArrayList<Object>(
                Arrays.asList(
                        this.guid,
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
            Assert.assertTrue(ApplicationDataDAOImplTest.dao.addApplication(appData));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test public void updateApplication()
    {
        List<Object> appData = new ArrayList<Object>(
                Arrays.asList(
                        this.guid,
                        "eSolutions",
                        "c0b20624-0a0c-4cf6-a8dc-62efc5a46e18",
                        "CWS",
                        "1.1",
                        "/appvol/ATS70/eSolutions/applogs",
                        "2aa547e9-3a6e-4720-95d9-6521c862ef2a",
                        "/appvol/ATS70/eSolutions/eSolutions_web_source-1.0.war",
                        "/appvol/ATS70/eSolutions/syslogs/"));

        try
        {
            Assert.assertTrue(ApplicationDataDAOImplTest.dao.addApplication(appData));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test public void listInstalledApplications()
    {
        try
        {
            Assert.assertNotNull(ApplicationDataDAOImplTest.dao.listApplications(0));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test public void getApplicationData()
    {
        try
        {
            Assert.assertNotNull(ApplicationDataDAOImplTest.dao.getApplication(this.guid));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test public void getApplicationsByAttribute()
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

    @Test public void deleteApplication()
    {
        try
        {
            Assert.assertTrue(ApplicationDataDAOImplTest.dao.removeApplication(this.guid));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @After public void tearDown()
    {
        CoreServiceInitializer.shutdown();
    }
}
