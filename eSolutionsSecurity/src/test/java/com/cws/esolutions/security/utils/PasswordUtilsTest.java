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
package com.cws.esolutions.security.utils;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.Assert;

import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.utils.PasswordUtils;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.utils
 * File: PasswordUtilsTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly   		11/23/2008 22:39:20             Created.
 */

public class PasswordUtilsTest
{
    private static final SecurityServiceBean bean = SecurityServiceBean.getInstance();

    @Before public void setUp()
    {
        try
        {
            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/logging/logging.xml", false);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.fail(e.getMessage());

            System.exit(1);
        }
    }

    @Test public void twoWayEncrypt()
    {
    	final String password = "appuser10";
    	final String salt = "hg4Q1qymhVY5ZICwyXuYFvdegQVyrAbg";
    	final String expected = "T25jNUFNZVp6TzlmZ3B3ZkNoeWdsQT09OnhscWh2VzFyTUtYRkErZDE4V3d1Nmc9PQ==";

    	try
    	{
    		Assert.assertEquals(expected, PasswordUtils.encryptText(password, salt,
    				bean.getConfigData().getSecurityConfig().getSecretAlgorithm(),
    				bean.getConfigData().getSecurityConfig().getIterations(),
    				bean.getConfigData().getSecurityConfig().getKeyBits(),
    				bean.getConfigData().getSecurityConfig().getEncryptionAlgorithm(),
	    			bean.getConfigData().getSecurityConfig().getEncryptionInstance(),
	    			bean.getConfigData().getSystemConfig().getEncoding()));
    	}
    	catch (SecurityException sx)
    	{
    		sx.printStackTrace();
    		Assert.fail();
    	}
    }

    @Test public void twoWayDecrypt()
    {
    	final String expected = "appuser10";
    	final String encrypted = "T25jNUFNZVp6TzlmZ3B3ZkNoeWdsQT09OnhscWh2VzFyTUtYRkErZDE4V3d1Nmc9PQ==";
    	final String salt = "hg4Q1qymhVY5ZICwyXuYFvdegQVyrAbg";

    	try
		{
    		Assert.assertEquals(expected, PasswordUtils.decryptText(encrypted, salt,
    				bean.getConfigData().getSecurityConfig().getSecretAlgorithm(),
    				bean.getConfigData().getSecurityConfig().getIterations(),
    				bean.getConfigData().getSecurityConfig().getKeyBits(),
    				bean.getConfigData().getSecurityConfig().getEncryptionAlgorithm(),
	    			bean.getConfigData().getSecurityConfig().getEncryptionInstance(),
	    			bean.getConfigData().getSystemConfig().getEncoding()));
		}
		catch (Exception sx)
		{
			sx.printStackTrace();
			Assert.fail();
		}
    }

    @Test public void validateOtpValue()
    {
    	try
		{
            Assert.assertTrue(PasswordUtils.validateOtpValue(bean.getConfigData().getSecurityConfig().getOtpVariance(),
                    bean.getConfigData().getSecurityConfig().getOtpAlgorithm(),
                    bean.getConfigData().getSecurityConfig().getEncryptionInstance(),
                    "the secret", 0));
		}
		catch (SecurityException sx)
		{
			sx.printStackTrace();
			Assert.fail();
		}
    }

    @After public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
