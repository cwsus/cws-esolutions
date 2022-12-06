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
 * File: OpenLDAPAuthenticatorTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.util.Arrays;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.assertj.core.api.Assertions;

import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.security.dao.userauth.interfaces.Authenticator;
import com.cws.esolutions.security.dao.userauth.factory.AuthenticatorFactory;
import com.cws.esolutions.security.dao.userauth.exception.AuthenticatorException;

public class OpenLDAPAuthenticatorTest
{
    @BeforeAll public void setUp()
    {
        try
        {
            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/logging/logging.xml", true);
        }
        catch (final Exception e)
        {
            System.exit(1);
        }
    }

    @Test public void performLogon()
    {
        Authenticator authenticator = AuthenticatorFactory.getAuthenticator("com.cws.esolutions.security.dao.userauth.impl.OpenLDAPAuthenticator");

        try
        {
            Assertions.assertThat(authenticator.performLogon("0f645ed1-a8bb-4d5c-b4d6-2276f1dba592", "junit", "4Uq2ssAjH4S1jquzLkgh466xswdpcrPPNKeQjFFG6jpxV6L9mD5U+bSFF7slFWY6mKK6bJiy84u24qwDmXfuWA==")).isTrue();
        }
        catch (final AuthenticatorException e)
        {
        	Assertions.fail(e.getMessage());
        }
    }

    @Test public void obtainSecurityData()
    {
        Authenticator authenticator = AuthenticatorFactory.getAuthenticator("com.cws.esolutions.security.dao.userauth.impl.OpenLDAPAuthenticator");

        try
        {
        	Assertions.assertThat(authenticator.obtainSecurityData("junit", "f42fb0ba-4d1e-1126-986f-800cd2650000")).isNotEmpty();
        }
        catch (final AuthenticatorException e)
        {
        	Assertions.fail(e.getMessage());
        }
    }

    @Test public void obtainOtpSecret()
    {
        Authenticator authenticator = AuthenticatorFactory.getAuthenticator("com.cws.esolutions.security.dao.userauth.impl.OpenLDAPAuthenticator");

        try
        {
        	Assertions.assertThat(authenticator.obtainOtpSecret("junit", "f42fb0ba-4d1e-1126-986f-800cd2650000")).isNotEmpty();
        }
        catch (final AuthenticatorException e)
        {
        	Assertions.fail(e.getMessage());
        }
    }

    @Test public void verifySecurityData()
    {
        Authenticator authenticator = AuthenticatorFactory.getAuthenticator("com.cws.esolutions.security.dao.userauth.impl.OpenLDAPAuthenticator");

        try
        {
        	Assertions.assertThat(authenticator.verifySecurityData("junit", "f42fb0ba-4d1e-1126-986f-800cd2650000",
                    new ArrayList<String>(
                            Arrays.asList("9XSqpcg8ChMQSThsMfF+ZlP6BPxt5urURelW8V+qedVw3nWNnPDhtF2G4jF91WsZ4qlrFzB/qnpCtqSseGvqfg==",
                                    "zd48ynVDm0t07RRzY07Gs9OqhI7YjUKa0CYrmcp2ESRR4neQOQ8nIgsNJhPVlIGo8n1rfbkYU11VoCqoNv2qPg==")))).isTrue();
                                    
        }
        catch (final AuthenticatorException e)
        {
        	Assertions.fail(e.getMessage());
        }
    }

    @AfterAll public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
