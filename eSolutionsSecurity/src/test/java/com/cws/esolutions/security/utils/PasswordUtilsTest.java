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

    @Test public void encrypt()
    {
    	String plainText = "zYB790qWv2GmMVljY1yb";
    	String salt = "F7niDTJvDam470jFdHwIm8SmqzHxXH5N";

    	String encrypted = PasswordUtils.encryptText(plainText.toCharArray(), salt.getBytes(), bean.getConfigData().getSecurityConfig().getSecretKeyAlgorithm(),
    			bean.getConfigData().getSecurityConfig().getIterations(), bean.getConfigData().getSecurityConfig().getKeyLength(),
    			bean.getConfigData().getSecurityConfig().getEncryptionAlgorithm(), bean.getConfigData().getSecurityConfig().getEncryptionInstance(),
    			bean.getConfigData().getSystemConfig().getEncoding());

    	String decrypted = PasswordUtils.decryptText(encrypted, plainText.toCharArray(), salt.getBytes(), bean.getConfigData().getSecurityConfig().getSecretKeyAlgorithm(),
    			bean.getConfigData().getSecurityConfig().getIterations(), bean.getConfigData().getSecurityConfig().getKeyLength(),
    			bean.getConfigData().getSecurityConfig().getEncryptionAlgorithm(), bean.getConfigData().getSecurityConfig().getEncryptionInstance(),
    			bean.getConfigData().getSystemConfig().getEncoding());

    	Assertions.assertThat(decrypted).isEqualTo(plainText);
    }

    @AfterAll public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
