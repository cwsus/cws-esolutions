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
 * File: SQLUtils.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.util.Map;
import java.util.HashMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.assertj.core.api.Assertions;

import com.cws.esolutions.core.listeners.CoreServicesInitializer;
import com.cws.esolutions.core.utils.exception.UtilityException;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
/**
 * @author cws-khuntly
 * @version 1.0
 */
public class SQLUtilsTest {

    @BeforeAll public void setUp()
    {
        try
        {
            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/logging/logging.xml", true);
            CoreServicesInitializer.initializeService("eSolutionsCore/config/ServiceConfig.xml", "eSolutionsCore/logging/logging.xml", true, true);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Assertions.fail(ex.getMessage());

            System.exit(-1);
        }
    }

    @Test public void addOrDeleteDataWithParams()
    {
        Map<Integer, Object> params = new HashMap<Integer, Object>();
        params.put(1, "cf60680d-60d6-4046-802e-8a7dad7c80fe");
        params.put(2, "mynewapp2");
        params.put(3, 1.0);
        params.put(4, "/appvol/WAS70/mynewapp2");
        params.put(5, "/nas/apps/mynewapp2");
        params.put(6, "mynewapp2.war");
        params.put(7, "");
        params.put(8, "/appvol/WAS70/logs/mynewapp2");
        params.put(9, "6ff52477-7fb7-4d4d-8861-5cbc1e7ef2ff");

        try
        {
            SQLUtils.addOrDeleteData("{CALL insertNewApplication(?, ?, ?, ?, ?, ?, ?, ?, ?)}", params);
        }
        catch (UtilityException ux)
        {
            ux.printStackTrace();
            Assertions.fail("Exception: " + ux.getMessage() + " : " + ux);
        }
    }

    @AfterAll public void tearDown()
    {
        CoreServicesInitializer.shutdown();
    }
}
