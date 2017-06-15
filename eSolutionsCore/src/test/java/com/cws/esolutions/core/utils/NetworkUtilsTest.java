/*
 * Copyright (c) 2009 - 2017 CaspersBox Web Services
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
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.utils
 * File: NetworkUtilsTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly   11/23/2008 22:39:20             Created.
 */
import org.junit.Test;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Assert;

import com.cws.esolutions.core.utils.exception.UtilityException;
import com.cws.esolutions.core.listeners.CoreServiceInitializer;

public class NetworkUtilsTest
{
    @Before public void setUp()
    {
        try
        {
            CoreServiceInitializer.initializeService("eSolutionsCore/config/ServiceConfig.xml", "eSolutionsCore/logging/logging.xml", false, false);
        }
        catch (Exception ex)
        {
            Assert.fail(ex.getMessage());

            System.exit(-1);
        }
    }

    @Test public void executeTelnetRequest()
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

    @Test public void executeDNSLookup()
    {
        try
        {
        	List<List<String>> responseData = NetworkUtils.executeDNSLookup(null, "google.com", "SOA", null);

        	for (List<String> response : responseData)
        	{
        		System.out.println(response);
        	}
        }
        catch (UtilityException ux)
        {
            Assert.fail(ux.getMessage());
        }
    }

    @After public void tearDown()
    {
        CoreServiceInitializer.shutdown();
    }
}
