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
package com.cws.esolutions.security.dao.certmgmt.impl;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.keymgmt.impl
 * File: FileKeyManagerTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import org.junit.Test;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Assert;
import java.util.Arrays;
import java.util.ArrayList;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.security.dao.certmgmt.interfaces.ICertificateManager;

public class CertificateManagerTest
{
    private static UserAccount userAccount = new UserAccount();
    private static RequestHostInfo hostInfo = new RequestHostInfo();
    private static ICertificateManager processor = new CertificateManagerImpl();

    @Before public void setUp()
    {
        try
        {
            hostInfo.setHostAddress("junit");
            hostInfo.setHostName("junit");

            userAccount.setStatus(LoginStatus.SUCCESS);
            userAccount.setGuid("f42fb0ba-4d1e-1126-986f-800cd2650000");
            userAccount.setUsername("junit");

            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/logging/logging.xml", true);
        }
        catch (Exception ex)
        {
        	ex.printStackTrace();
            Assert.fail(ex.getMessage());

            System.exit(-1);
        }
    }

    @Test public void createCertificateRequest()
    {
        List<String> subjectData = new ArrayList<String>(
        		Arrays.asList(
        				"kevin.com",
        				"myorgunit",
        				"myorgname",
        				"Buffalo",
        				"New York",
        				"US",
        				"secadm@caspersbox.com"));

        try
        {
        	processor.createCertificateRequest(subjectData, "mypassword", 365, 4096);
        }
        catch (Exception ex)
        {
        	ex.printStackTrace();
            Assert.fail(ex.getMessage());
        }
    }

    @After public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
