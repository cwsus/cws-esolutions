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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.assertj.core.api.Assertions;

import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.security.dao.userauth.interfaces.Authenticator;
import com.cws.esolutions.security.dao.userauth.factory.AuthenticatorFactory;

public class AutoResponseAuthenticatorTest {

	@BeforeAll public void setUp()
    {
        try
        {
            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/logging/logging.xml", false);
        }
        catch (final Exception e)
        {
            System.exit(1);
        }
    }

	/**
	 * Test method for {@link com.cws.esolutions.security.dao.userauth.impl.AutoResponseAuthenticator#performLogon(java.lang.String, java.lang.String)}.
	 */
	@Test
	public final void testPerformLogon()
	{
		Authenticator authenticator = AuthenticatorFactory.getAuthenticator("com.cws.esolutions.security.dao.userauth.impl.AutoResponseAuthenticator");

		try
		{
			authenticator.performLogon("0f645ed1-a8bb-4d5c-b4d6-2276f1dba592", "khuntly", "mypass");
		}
		catch (final Exception ex)
		{
			ex.printStackTrace();
			Assertions.fail(ex.getMessage());
		}
	}

	/**
	 * Test method for {@link com.cws.esolutions.security.dao.userauth.impl.AutoResponseAuthenticator#obtainSecurityData(java.lang.String, java.lang.String)}.
	 */
	@Test
	public final void testObtainSecurityData()
	{
		Assertions.fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link com.cws.esolutions.security.dao.userauth.impl.AutoResponseAuthenticator#obtainOtpSecret(java.lang.String, java.lang.String)}.
	 */
	@Test
	public final void testObtainOtpSecret()
	{
		Assertions.fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link com.cws.esolutions.security.dao.userauth.impl.AutoResponseAuthenticator#verifySecurityData(java.lang.String, java.lang.String, java.util.List)}.
	 */
	@Test
	public final void testVerifySecurityData()
	{
		Assertions.fail("Not yet implemented"); // TODO
	}

    @AfterAll public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
