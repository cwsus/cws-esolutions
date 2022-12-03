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
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.assertj.core.api.Assertions;

import com.cws.esolutions.core.utils.exception.UtilityException;
import com.cws.esolutions.core.listeners.CoreServicesInitializer;

public class NetworkUtilsTest
{
    @BeforeAll public void setUp()
    {
        try
        {
            CoreServicesInitializer.initializeService("eSolutionsCore/config/ServiceConfig.xml", "eSolutionsCore/logging/logging.xml", false, false);
        }
        catch (Exception ex)
        {
            Assertions.fail(ex.getMessage());

            System.exit(-1);
        }
    }

    @Test public void executeTelnetRequest()
    {
        try
        {
            NetworkUtils.executeTelnetRequest("proxy.caspersbox.com", 8080, 10000);
        }
        catch (UtilityException ux)
        {
            Assertions.fail(ux.getMessage());
        }
    }

    @Test public void executeDNSLookup()
    {
        try
        {
            List<List<String>> responseData = NetworkUtils.executeDNSLookup(null, "google.com", "A", null);

            Assertions.assertThat(responseData).isNotEmpty();
        }
        catch (UtilityException ux)
        {
            Assertions.fail(ux.getMessage());
        }
    }

    @AfterAll public void tearDown()
    {
        CoreServicesInitializer.shutdown();
    }
}
