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
    public static final void setUp()
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
    public static final void testAddProjectIdForUser()
    {
        try
        {
            Assert.assertTrue(userSvcDAO.addProjectIdForUser("guid", "AD94CBC0-A159-4B88-8422-F10F2CB991F3"));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public static final void testVerifyProjectForUser()
    {
        try
        {
            Assert.assertTrue(userSvcDAO.verifyProjectForUser("guid", "AD94CBC0-A159-4B88-8422-F10F2CB991F3"));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public static final void testRemoveProjectIdForUser()
    {
        try
        {
            Assert.assertTrue(userSvcDAO.removeProjectIdForUser("guid", "AD94CBC0-A159-4B88-8422-F10F2CB991F3"));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public static final void testReturnUserAuthorizedProjects()
    {
        try
        {
            Assert.assertNotNull(userSvcDAO.returnUserAuthorizedProjects("guid"));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public static final void testAddServiceToUser()
    {
        try
        {
            Assert.assertTrue(userSvcDAO.addServiceToUser("guid", "4EFF8D2E-32F6-44BD-B399-520F39D73197"));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public static final void testVerifyServiceForUser()
    {
        try
        {
            Assert.assertTrue(userSvcDAO.verifyServiceForUser("guid", "32537750-CDE2-11E2-8B8B-0800200C9A66"));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public static final void testRemoveServiceFromUser()
    {
        try
        {
            Assert.assertTrue(userSvcDAO.removeServiceFromUser("guid", "4EFF8D2E-32F6-44BD-B399-520F39D73197"));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public static final void testListServicesForUser()
    {
        try
        {
            Assert.assertNotNull(userSvcDAO.listServicesForUser("guid"));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @After
    public static final void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
