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
import com.cws.esolutions.security.utils.PasswordUtils;
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
        catch (Exception e)
        {
            e.printStackTrace();
            Assertions.fail(e.getMessage());

            System.exit(1);
        }
    }

    @Test public void testEncryption()
    {
        final String plainText = "aQPqJsO1sbrPXmFdxwJi";
        final String salt = "QYosWmY0q8o3xMb8Arsaq7rJFdbFukbG";

        try
        {
        	String encrypted = PasswordUtils.encryptText(plainText, salt, // encrypt
                    bean.getConfigData().getSecurityConfig().getSecretAlgorithm(),
                    bean.getConfigData().getSecurityConfig().getIterations(),
                    bean.getConfigData().getSecurityConfig().getKeyBits(),
                    bean.getConfigData().getSecurityConfig().getEncryptionAlgorithm(),
                    bean.getConfigData().getSecurityConfig().getEncryptionInstance(),
                    bean.getConfigData().getSystemConfig().getEncoding());

        	System.out.println(encrypted);
        	Assertions.assertThat(encrypted);

        	String decrypted = PasswordUtils.decryptText(encrypted, salt, //decrypt and validate
                    bean.getConfigData().getSecurityConfig().getSecretAlgorithm(),
                    bean.getConfigData().getSecurityConfig().getIterations(),
                    bean.getConfigData().getSecurityConfig().getKeyBits(),
                    bean.getConfigData().getSecurityConfig().getEncryptionAlgorithm(),
                    bean.getConfigData().getSecurityConfig().getEncryptionInstance(),
                    bean.getConfigData().getSystemConfig().getEncoding());

        	System.out.println(decrypted);
        	Assertions.assertThat(decrypted);
        }
        catch (Exception sx)
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
        catch (SecurityException sx)
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
