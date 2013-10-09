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
package com.cws.esolutions.security.dao.reference.impl;

import java.util.Map;
import java.util.List;
import org.junit.Test;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import java.sql.SQLException;

import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.security.dao.reference.interfaces.IUserServiceInformationDAO;
/**
 * eSolutionsCore
 * com.cws.esolutions.security.dao.reference.impl
 * UserServiceInformationDAOImplTest.java
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
 * 35033355 @ Apr 5, 2013 1:40:02 PM
 *     Created.
 */
public class UserServiceInformationDAOImplTest
{
    private static final IUserServiceInformationDAO userSvcDAO = new UserServiceInformationDAOImpl();

    @Before
    public final void setUp()
    {
        try
        {
            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/config/SecurityLogging.xml");
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            System.exit(1);
        }
    }

    @Test
    public final void testAddProjectIdForUser()
    {
        try
        {
            boolean isComplete = userSvcDAO.addProjectIdForUser("guid", "AD94CBC0-A159-4B88-8422-F10F2CB991F3");

            Assert.assertTrue(isComplete);
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public final void testVerifyProjectForUser()
    {
        try
        {
            boolean isComplete = userSvcDAO.verifyProjectForUser("guid", "AD94CBC0-A159-4B88-8422-F10F2CB991F3");

            Assert.assertTrue(isComplete);
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public final void testRemoveProjectIdForUser()
    {
        try
        {
            boolean isComplete = userSvcDAO.removeProjectIdForUser("guid", "AD94CBC0-A159-4B88-8422-F10F2CB991F3");

            Assert.assertTrue(isComplete);
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public final void testReturnUserAuthorizedProjects()
    {
        try
        {
            List<String> responseList = userSvcDAO.returnUserAuthorizedProjects("guid");

            Assert.assertNotNull(responseList);
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public final void testAddServiceToUser()
    {
        try
        {
            boolean isComplete = userSvcDAO.addServiceToUser("guid", "4EFF8D2E-32F6-44BD-B399-520F39D73197");

            Assert.assertTrue(isComplete);
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public final void testVerifyServiceForUser()
    {
        try
        {
            boolean isComplete = userSvcDAO.verifyServiceForUser("guid", "32537750-CDE2-11E2-8B8B-0800200C9A66");

            Assert.assertTrue(isComplete);
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public final void testRemoveServiceFromUser()
    {
        try
        {
            boolean isComplete = userSvcDAO.removeServiceFromUser("guid", "4EFF8D2E-32F6-44BD-B399-520F39D73197");

            Assert.assertTrue(isComplete);
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public final void testListServicesForUser()
    {
        try
        {
            Map<String, String> responseList = userSvcDAO.listServicesForUser("guid");

            Assert.assertNotNull(responseList);
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @After
    public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
