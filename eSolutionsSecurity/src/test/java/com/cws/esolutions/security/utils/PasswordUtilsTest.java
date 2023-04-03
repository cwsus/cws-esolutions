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
            Assertions.fail(e.getMessage());

            System.exit(1);
        }
    }
    
    @Test public void encrypt()
    {
		char[] plainText = "ANIBbuKHiGkyGANLOjawFZ9cZGXuCVRd".toCharArray();
		String newSalt = "2696b7eb676ac94eddfeb54dc6c6cd80d97e915d08217e42e7f5005487acc39b3e2852dfa4700578e188737bf5d28efd541ad6a76b836a643131810b7f516702";
		String expected = "df2d81c94906ed3d2b0e82fb31dc5fd102d0fe3239039926a31f3c7f15121900cb48023d464d1eecd8b4ddbcc78afe6bc44de1b215429f78c44f6428bfc1e0ef";

    	String encrypted = PasswordUtils.encryptText(plainText, newSalt,
    			bean.getConfigData().getSecurityConfig().getSecretKeyAlgorithm(),
    			bean.getConfigData().getSecurityConfig().getIterations(),
    			bean.getConfigData().getSecurityConfig().getKeyLength(),
    			//bean.getConfigData().getSecurityConfig().getEncryptionAlgorithm(),
    			//bean.getConfigData().getSecurityConfig().getEncryptionInstance(),
    			bean.getConfigData().getSystemConfig().getEncoding());

    	Assertions.assertThat(encrypted).isEqualTo(expected);
    }

    @AfterAll public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
