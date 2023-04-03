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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.TestInstance;

import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.security.dao.userauth.interfaces.Authenticator;
import com.cws.esolutions.security.dao.userauth.factory.AuthenticatorFactory;
import com.cws.esolutions.security.dao.userauth.exception.AuthenticatorException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SQLAuthenticatorTest
{
	private static final String GUID = "b7f6432d-10e1-4258-ad97-f3ea9ac81207";

    @BeforeAll public void setUp()
    {
        try
        {
            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/logging/logging.xml", true);
        }
        catch (final Exception e)
        {
            Assertions.fail(e.getMessage());
            System.exit(1);
        }
    }

    @Test public void performLogon()
    {
        Authenticator authenticator = AuthenticatorFactory.getAuthenticator("com.cws.esolutions.security.dao.userauth.impl.SQLAuthenticator");

        try
        {
        	Assertions.assertThat(authenticator.performLogon(GUID, null, null)).isTrue(); // TODO
        }
        catch (final AuthenticatorException e)
        {
        	Assertions.fail(e.getMessage());
        }
    }

    @Test public void obtainSecurityData()
    {
        Authenticator authenticator = AuthenticatorFactory.getAuthenticator("com.cws.esolutions.security.dao.userauth.impl.SQLAuthenticator");

        try
        {
        	Assertions.assertThat(authenticator.getSecurityAnswers(GUID)).isNotEmpty();
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
