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
package com.cws.esolutions.security.dao.userauth.impl;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.dao.userauth.impl
 * File: SQLAuthenticatorTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import org.junit.Test;
import org.junit.After;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Assert;
import java.util.ArrayList;

import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.security.dao.userauth.interfaces.Authenticator;
import com.cws.esolutions.security.dao.userauth.factory.AuthenticatorFactory;
import com.cws.esolutions.security.dao.userauth.exception.AuthenticatorException;

public class SQLAuthenticatorTest
{
    @Before public void setUp()
    {
        try
        {
            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/logging/logging.xml", true);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            System.exit(1);
        }
    }

    @Test public void performLogon()
    {
        Authenticator authenticator = AuthenticatorFactory.getAuthenticator("com.cws.esolutions.security.dao.userauth.impl.SQLAuthenticator");

        try
        {
            Assert.assertNotNull(authenticator.performLogon("junit", "VOpqGWznp1flygXFED8FVTxXTRHG9QG/Dj+apuuyeh59JWVbYd9hOgZTOfpLdBWRlPDb1TZnvt7XE3llHOPQQQ=="));
        }
        catch (AuthenticatorException e)
        {
            Assert.fail(e.getMessage());
        }
    }

    @Test public void obtainSecurityData()
    {
        Authenticator authenticator = AuthenticatorFactory.getAuthenticator("com.cws.esolutions.security.dao.userauth.impl.SQLAuthenticator");

        try
        {
            Assert.assertNotNull(authenticator.obtainSecurityData("junit", "f42fb0ba-4d1e-1126-986f-800cd2650000"));
        }
        catch (AuthenticatorException e)
        {
            Assert.fail(e.getMessage());
        }
    }

    @Test public void obtainOtpSecret()
    {
        Authenticator authenticator = AuthenticatorFactory.getAuthenticator("com.cws.esolutions.security.dao.userauth.impl.SQLAuthenticator");

        try
        {
            Assert.assertNotNull(authenticator.obtainOtpSecret("junit", "f42fb0ba-4d1e-1126-986f-800cd2650000"));
        }
        catch (AuthenticatorException e)
        {
            Assert.fail(e.getMessage());
        }
    }

    @Test public void verifySecurityData()
    {
        Authenticator authenticator = AuthenticatorFactory.getAuthenticator("com.cws.esolutions.security.dao.userauth.impl.SQLAuthenticator");

        try
        {
            Assert.assertTrue(authenticator.verifySecurityData("junit", "f42fb0ba-4d1e-1126-986f-800cd2650000",
                    new ArrayList<String>(
                            Arrays.asList("nnVRD0xm0quQrHv2k9AHSfQIHJLoJ6Hp9HWPgiqpiV9zOMDUaboAUOUzI4Vn5lWlqczMl/TzjWWrt6YhHhRjng==",
                                    "FEwX3hCErzIAaeZThAznod4cIOG1eboOwttIBiW6Fz1Rbe/JzczTN6ANjIdW9KNkKx6Q+g1fDtIZgYX/xEYlOA=="))));
                                    
        }
        catch (AuthenticatorException e)
        {
            Assert.fail(e.getMessage());
        }
    }

    @After public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
