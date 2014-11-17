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
import org.apache.commons.lang.RandomStringUtils;

import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.utils.PasswordUtils;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;

public class PasswordUtilsTest
{
    private static final SecurityServiceBean bean = SecurityServiceBean.getInstance();

    @Before public void setUp()
    {
        /*try
        {
            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/logging/logging.xml");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.fail(e.getMessage());

            System.exit(1);
        }*/
    }

    @Test public void test()
    {
        String salt = RandomStringUtils.randomAlphanumeric(64);
        System.out.println(salt);
        System.out.println(PasswordUtils.encryptText("a?.Zd`5ExI%$wm@g/v;L$oq6yqFM$iFAmjVqx72pB|KwG65sd3,ukUDPo;H,|o.O", salt));
    }

    @Test public void encryptText()
    {
        Assert.assertEquals("xdwcvNbTtdBkcxvtn3g5BTHz1naNiq3tZAn255ai1hZtRUPiA0TyoLPs3fP6lC9YcvyNcreuFqEuse10nnyHAg==",
                PasswordUtils.encryptText("TestPasswordValue", "zHnDJVgtiJy3FNFDfSe9ZK1KW97zd1oDmA8awAoW7QnDR6i2wd9AfV2NmXOOVYJO",
                        bean.getConfigData().getSecurityConfig().getAuthAlgorithm(), bean.getConfigData().getSecurityConfig().getIterations()));
    }

    @Test public void testTwoWayHash()
    {
        Assert.assertEquals("41hvglQql38+cr8Et//rFFmrJk3Zfg8Xh5b4SLwtRZd0PGuC1a2Wq83iA/YY5mrOS8eh8ZElOJK4Ba43hiijGzbHo2skKg4UpLNf7zhpCowmJKYUcIeBmaUy7ivro8fEsxaHXW6WYeDmcAbmuENOjWft3q31KHtuGmZhluUk+b2navuW/4doetGtH/D8VoZI",
                PasswordUtils.encryptText("a?.Zd`5ExI%$wm@g/v;L$oq6yqFM$iFAmjVqx72pB|KwG65sd3,ukUDPo;H,|o.O", "wHqqSZI63Et38DRwksM4WanElRHJoZvQkydokLsAo8YkF3NurF5BoTXllwpCd2Ub"));
    }

    @Test public void decryptText()
    {
        Assert.assertEquals("myconfig", PasswordUtils.decryptText("5De+3W5rQz4J0TmKvWfmHfmHh9+7y93uIbk8JIG2ewk7Rnss5snuHxqKM1TagzBkIvQVOVO86zGY083yxwgpjVVrKF6QtRbkIr4u6JDfgb4=",
                "RdvejAfQ9RZL3ibfkMLhu1EHvGUMmXKZGmVEobwVWqXF6FIVD7JqrayHXPBlpLp2".length()));
    }

    @Test public void validateOtpValue()
    {
        Assert.assertEquals("number", PasswordUtils.validateOtpValue(bean.getConfigData().getSecurityConfig().getOtpVariance(),
                bean.getConfigData().getSecurityConfig().getOtpAlgorithm(),
                "the secret", 0));
    }

    @After public void tearDown()
    {
        // SecurityServiceInitializer.shutdown();
    }
}
