/*
 * Copyright (c) 2009 - 2014 CaspersBox Web Services
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
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
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
    private static final Authenticator authenticator = AuthenticatorFactory.getAuthenticator("com.cws.esolutions.security.dao.userauth.SQLAuthenticator");

    @Before public void setUp()
    {
        try
        {
            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/logging/logging.xml");
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            System.exit(1);
        }
    }

    @Test public void performLogon()
    {
        try
        {
            Assert.assertNotNull(authenticator.performLogon("khuntly", "VOpqGWznp1flygXFED8FVTxXTRHG9QG/Dj+apuuyeh59JWVbYd9hOgZTOfpLdBWRlPDb1TZnvt7XE3llHOPQQQ=="));
        }
        catch (AuthenticatorException e)
        {
            Assert.fail(e.getMessage());
        }
    }

    @Test public void obtainSecurityData()
    {
        try
        {
            Assert.assertNotNull(authenticator.obtainSecurityData("khuntly", "74d9729b-7fb2-4fef-874b-c9ee5d7a5a95"));
        }
        catch (AuthenticatorException e)
        {
            Assert.fail(e.getMessage());
        }
    }

    @Test public void obtainOtpSecret()
    {
        try
        {
            Assert.assertNotNull(authenticator.obtainOtpSecret("khuntly", "74d9729b-7fb2-4fef-874b-c9ee5d7a5a95"));
        }
        catch (AuthenticatorException e)
        {
            Assert.fail(e.getMessage());
        }
    }

    @Test public void verifySecurityData()
    {
        try
        {
            Assert.assertTrue(authenticator.verifySecurityData("khuntly", "74d9729b-7fb2-4fef-874b-c9ee5d7a5a95",
                    new ArrayList<String>(
                            Arrays.asList("2kAghHhR1Kaq4vuXukaY9NDOnt9J9rZzGvn6Fgg2EIsj2kHjsYv4mIp2SGsATUL8d839aqL27+KOVxh704hk5Q==",
                                    "JeqSyqq3trkDkOwMZDf8lIHCfa2NWPSMJ/rFVnPmooGKXcAzUtwR/wJ18uc5wOQtbsMaa3cK3lB2kwqqi/SpWA=="))));
                                    
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
