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
package com.cws.esolutions.security.utils;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.utils
 * File: PasswordUtilsTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.Assert;

import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.utils.PasswordUtils;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;

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
    	String password = "appuser10";
    	String salt = "VY772zetrNG3qfttpqoUWKM86J7KEpT6ii83YmPE70jnUh3tbPFOURSJJTLCgJol";
    	String expected = "YQtDf58ru+MeUMW+vo+VHorHWoWcFm/jptbImCzohKWkifLPYl8ZVM8DmKbQVn5C+6WrwDn1jFFUPxVF7Cer2QlHmPOGXcOOxjHPUZ27lFI=";

    	String response = PasswordUtils.encryptText(password, salt,
    			bean.getConfigData().getSecurityConfig().getEncryptionAlgorithm(),
    			bean.getConfigData().getSecurityConfig().getEncryptionInstance(),
    			bean.getConfigData().getSystemConfig().getEncoding());

    	System.out.println("<break>" + response + "<break>");
        Assert.assertEquals(response, expected);
    }

    @Test public void twoWayDecrypt()
    {
    	String expected = "appuser10";
    	String salt = "VY772zetrNG3qfttpqoUWKM86J7KEpT6ii83YmPE70jnUh3tbPFOURSJJTLCgJol";
    	String encrypted = "gs7NHN8ILKEogAlCXaXyWGEkYgSrTG6a2xemMntLMy9zmaOYxNwhBtJ4s0j6Nv+bAT3hLhkz41Z4bkjuHxt1+HobG5CDgnKZqL91jCvBvKo=";

    	String response = PasswordUtils.decryptText(encrypted, salt.length(),
    			bean.getConfigData().getSecurityConfig().getEncryptionAlgorithm(),
    			bean.getConfigData().getSecurityConfig().getEncryptionInstance(),
    			bean.getConfigData().getSystemConfig().getEncoding());

        Assert.assertEquals(expected, response);
    }

    @Test public void validateOtpValue()
    {
        Assert.assertTrue(PasswordUtils.validateOtpValue(bean.getConfigData().getSecurityConfig().getOtpVariance(),
                bean.getConfigData().getSecurityConfig().getOtpAlgorithm(),
                bean.getConfigData().getSecurityConfig().getEncryptionInstance(),
                "the secret", 0));
    }

    @After public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
