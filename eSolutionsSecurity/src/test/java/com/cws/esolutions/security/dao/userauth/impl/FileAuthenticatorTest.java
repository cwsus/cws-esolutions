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
 * File: LDAPAuthenticatorTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import org.junit.Test;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.security.dao.userauth.interfaces.Authenticator;
import com.cws.esolutions.security.dao.userauth.factory.AuthenticatorFactory;

public class FileAuthenticatorTest {

	@Before public void setUp()
    {
        try
        {
            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/logging/logging.xml", false);
        }
        catch (Exception e)
        {
            System.exit(1);
        }
    }

	/**
	 * Test method for {@link com.cws.esolutions.security.dao.userauth.impl.FileAuthenticator#performLogon(java.lang.String, java.lang.String)}.
	 */
	@Test
	public final void testPerformLogon()
	{
		Authenticator authenticator = AuthenticatorFactory.getAuthenticator("com.cws.esolutions.security.dao.userauth.impl.FileAuthenticator");

		try
		{
			authenticator.performLogon("khuntly", "mypass");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			Assert.fail(ex.getMessage());
		}
	}

	/**
	 * Test method for {@link com.cws.esolutions.security.dao.userauth.impl.FileAuthenticator#obtainSecurityData(java.lang.String, java.lang.String)}.
	 */
	@Test
	public final void testObtainSecurityData()
	{
		Assert.fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link com.cws.esolutions.security.dao.userauth.impl.FileAuthenticator#obtainOtpSecret(java.lang.String, java.lang.String)}.
	 */
	@Test
	public final void testObtainOtpSecret()
	{
		Assert.fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link com.cws.esolutions.security.dao.userauth.impl.FileAuthenticator#verifySecurityData(java.lang.String, java.lang.String, java.util.List)}.
	 */
	@Test
	public final void testVerifySecurityData()
	{
		Assert.fail("Not yet implemented"); // TODO
	}

    @After public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
