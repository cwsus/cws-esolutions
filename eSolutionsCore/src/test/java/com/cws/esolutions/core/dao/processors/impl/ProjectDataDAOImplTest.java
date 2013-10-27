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

import java.util.UUID;
import java.util.List;
import org.junit.Test;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import java.util.Arrays;
import java.util.ArrayList;
import java.sql.SQLException;

import com.cws.esolutions.core.processors.enums.ServiceRegion;
import com.cws.esolutions.core.listeners.CoreServiceInitializer;
import com.cws.esolutions.core.dao.processors.interfaces.IProjectDataDAO;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.dao.processors.impl
 * ProjectDataDAOImplTest.java
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
 * 35033355 @ May 24, 2013 10:19:08 AM
 *     Created.
 */
public class ProjectDataDAOImplTest
{
    private IProjectDataDAO dao = new ProjectDataDAOImpl();

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
    public final void addNewProject()
    {
        List<String> projectDetail = new ArrayList<String>(
                Arrays.asList(
                        UUID.randomUUID().toString(),
                        "MyProject",
                        ServiceRegion.DEV.name(),
                        "Kevin Huntly",
                        "UNCONFIGURED",
                        "esolutions@caspersbox.corp",
                        "inqueue",
                        "chgqueue",
                        "ACTIVE"));

        try
        {
            Assert.assertTrue(dao.addNewProject(projectDetail));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public final void deleteProjectData()
    {
        try
        {
            Assert.assertTrue(dao.deleteProjectData("1d8e1bae-90f2-4e39-9a30-c17bf76a79c6"));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public final void getProjectData()
    {
        try
        {
            List<String> projectList = dao.getProjectData("1d8e1bae-90f2-4e39-9a30-c17bf76a79c6");

            Assert.assertNotNull(projectList);
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public final void listAvailableProjects()
    {
        try
        {
            List<String[]> responseMap = dao.listAvailableProjects();

            Assert.assertNotNull(responseMap);
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @After
    public void tearDown() throws Exception
    {

    }
}
