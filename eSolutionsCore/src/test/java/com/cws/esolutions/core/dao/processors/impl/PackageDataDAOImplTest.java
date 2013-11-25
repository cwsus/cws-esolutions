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

import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;

import com.cws.esolutions.core.listeners.CoreServiceInitializer;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.dao.processors.impl
 * PackageDataDAOImplTest.java
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
 * 35033355 @ Jun 4, 2013 3:09:50 PM
 *     Created.
 */
public class PackageDataDAOImplTest
{
    @Before
    public static final void setUp()
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
    public static final void testRetrievePackageData()
    {

    }

    @Test
    public static final void testAddNewPackage()
    {

    }

    @Test
    public static final void testDeletePackageData()
    {

    }

    @Test
    public static final void testGetPackageData()
    {

    }

    @Test
    public static final void testGetPackagesByAttribute()
    {

    }
}
