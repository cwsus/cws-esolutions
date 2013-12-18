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
package com.cws.esolutions.core.utils;

import org.junit.Test;
import org.junit.Before;
import org.junit.Assert;

import com.cws.esolutions.core.utils.exception.UtilityException;
import com.cws.esolutions.core.listeners.CoreServiceInitializer;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.utils
 * NetworkUtilsTest.java
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
 * 35033355 @ May 30, 2013 9:32:47 AM
 *     Created.
 */
public class NetworkUtilsTest
{
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
    public void testExecuteTelnetRequest()
    {
        try
        {
            NetworkUtils.executeTelnetRequest("chibcarray.us.hsbc", 8080, 10000);
        }
        catch (UtilityException ux)
        {
            Assert.fail(ux.getMessage());
        }
    }

    @Test
    public void testIsHostValid()
    {
        Assert.assertTrue(NetworkUtils.isHostValid("localhost"));
        Assert.assertFalse(NetworkUtils.isHostValid("notlocalhost"));
    }
}
