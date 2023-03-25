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
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.TestInstance;

import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
/**
 * @author khuntly
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PasswordUtilsTest
{
    private static final SecurityServiceBean bean = SecurityServiceBean.getInstance();

    @BeforeAll public void setUp()
    {
        try
        {
            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/logging/logging.xml", false);
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            Assertions.fail(e.getMessage());

            System.exit(1);
        }
    }

    @Test public void testTwoWayEncryption()
    {
        final String plainText = "Answer 2";
        final String salt = "atkGoycIgsixtZuuaDzQmgskraO9Iv3A";

        try
        {
        	String encr = PasswordUtils.encryptText(plainText.toCharArray(), salt.getBytes(), bean.getConfigData().getSecurityConfig().getSecretKeyAlgorithm(),
        			bean.getConfigData().getSecurityConfig().getIterations(), bean.getConfigData().getSecurityConfig().getKeyLength(),
        			bean.getConfigData().getSecurityConfig().getEncryptionAlgorithm(), bean.getConfigData().getSecurityConfig().getEncryptionInstance(),
        			bean.getConfigData().getSystemConfig().getEncoding());

        	System.out.println("Encrypted: " + encr);

        	Assertions.assertThat(encr).isNotEmpty();

        	String decrypted = PasswordUtils.decryptText(encr, encr.toCharArray(), salt.getBytes(), bean.getConfigData().getSecurityConfig().getSecretKeyAlgorithm(),
        			bean.getConfigData().getSecurityConfig().getIterations(), bean.getConfigData().getSecurityConfig().getKeyLength(),
        			bean.getConfigData().getSecurityConfig().getEncryptionAlgorithm(), bean.getConfigData().getSecurityConfig().getEncryptionInstance(),
        			bean.getConfigData().getSystemConfig().getEncoding());

        	System.out.println("Decrypted: " + decrypted);
        	//String decr = PasswordUtils.decryptText(encr, salt, bean.getConfigData().getSecurityConfig().getSecretKeyAlgorithm(), bean.getConfigData().getSecurityConfig().getIterations(),
        	//		bean.getConfigData().getSecurityConfig().getKeyBits(), bean.getConfigData().getSecurityConfig().getEncryptionAlgorithm(), bean.getConfigData().getSecurityConfig().getEncryptionInstance(),
        	//		bean.getConfigData().getSystemConfig().getEncoding());

        	//System.out.println(decr);
        	//Assertions.assertThat(decr).isEqualTo(plainText);
        }
        catch (final Exception sx)
        {
        	sx.printStackTrace();
            Assertions.fail(sx.getMessage());
        }
    }

    @Test public void testDecryption()
    {
    	final String salt = "atkGoycIgsixtZuuaDzQmgskraO9Iv3A";
    	final String encrypted = "cV1jiEWewtYRMNVaU3/ieEghoUJdtLGuQn76NucXw21ldsk1xUIru+PbC+6FlFvvSkRySXDR9hwAEK3Rl/Na6Q==";

    	try
    	{
    		String decrypted = PasswordUtils.decryptText(encrypted, salt, bean.getConfigData().getSecurityConfig().getSecretKeyAlgorithm(), bean.getConfigData().getSecurityConfig().getIterations(),
    			bean.getConfigData().getSecurityConfig().getKeyLength(), bean.getConfigData().getSecurityConfig().getEncryptionAlgorithm(), bean.getConfigData().getSecurityConfig().getEncryptionInstance(),
				bean.getConfigData().getSystemConfig().getEncoding());

    		System.out.println(decrypted);
    	}
    	catch (final Exception ex)
    	{
    		ex.printStackTrace();
    		Assertions.fail(ex.getMessage());
    	}
    }
    @Test public void testOneWayEncryption()
    {
        final String plainText = "some-password";
        final String salt = "ToqXs54ODtiENnlb8217Hq6LfIi9xYAg";
        final String expected = "wDkPVRWF4VehbzWNrP25iR6jZeGiEzLhqtjJlcXOwK8nWw6f31LYXmtovfl/0nGJgM+yMgyV+r+J2VfZntIL/w==";

        try
        {
        	String encrypted = PasswordUtils.encryptText(plainText, salt, bean.getConfigData().getSecurityConfig().getMessageDigest(),
        			bean.getConfigData().getSecurityConfig().getIterations(), bean.getConfigData().getSystemConfig().getEncoding());

        	Assertions.assertThat(encrypted).isEqualTo(expected);
        }
        catch (final Exception sx)
        {
            sx.printStackTrace();
            Assertions.fail(sx.getMessage());
        }
    }

    @Test public void validateOtpValue()
    {
        try
        {
            Assertions.assertThat(PasswordUtils.validateOtpValue(bean.getConfigData().getSecurityConfig().getOtpVariance(),
                    bean.getConfigData().getSecurityConfig().getOtpAlgorithm(),
                    bean.getConfigData().getSecurityConfig().getEncryptionInstance(),
                    "the secret", 0)).isTrue();
        }
        catch (final SecurityException sx)
        {
            sx.printStackTrace();
            Assertions.fail(sx.getMessage());
        }
    }

    @AfterAll public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
