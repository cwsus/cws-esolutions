/**
 * Copyright (c) 2009 - 2013 By: CWS, Inc.
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
package com.cws.esolutions.agent;

import org.slf4j.Logger;
import javax.crypto.Cipher;
import org.slf4j.LoggerFactory;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.BadPaddingException;
import java.security.InvalidKeyException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.IllegalBlockSizeException;
import org.apache.commons.codec.binary.Base64;
import java.security.NoSuchAlgorithmException;
/**
 * eSolutionsAgent
 * com.cws.esolutions.agent
 * PasswordFunction.java
 *
 * $Id: $
 * $Author: $
 * $Date: $
 * $Revision: $
 * @author kmhuntly@gmail.com
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Jan 2, 2013 1:49:02 PM
 *     Created.
 */
public final class PasswordFunction
{
    private static String methodName = null;

    private static final String AES = "AES";
    private static final int NUM_OF_ITERATION = 2;
    private static final String CNAME = PasswordFunction.class.getName();
    private static final byte[] SECRET_KEY = new byte[] { 'A', 'M', 'C', 'P', 'a', 's', 's', 'C', 'r', 'y', 't', 'o', 'K', 'e', 'y', '!' };

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CNAME);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public static String decryptText(final String encrypted, final String salt) throws SecurityException
    {
        methodName = CNAME + "#decryptText(final String encrypted, final String salt) throws SecurityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        String clear = null;
        String decPass = null;

        try
        {
            SecretKeySpec sks = new SecretKeySpec(SECRET_KEY, AES);
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.DECRYPT_MODE, sks);

            clear = encrypted;

            for (int i = 0; i < NUM_OF_ITERATION; i++)
            {
                byte[] decryptedBytes = Base64.decodeBase64(clear.getBytes());
                byte[] decryptedSalt = cipher.doFinal(decryptedBytes);
                decPass = new String(decryptedSalt).substring(salt.length());
            }
        }
        catch (InvalidKeyException ikx)
        {
            ERROR_RECORDER.error(ikx.getMessage(), ikx);

            throw new SecurityException(ikx.getMessage(), ikx);
        }
        catch (NoSuchAlgorithmException nsx)
        {
            ERROR_RECORDER.error(nsx.getMessage(), nsx);

            throw new SecurityException(nsx.getMessage(), nsx);
        }
        catch (NoSuchPaddingException npx)
        {
            ERROR_RECORDER.error(npx.getMessage(), npx);

            throw new SecurityException(npx.getMessage(), npx);
        }
        catch (IllegalBlockSizeException ibx)
        {
            ERROR_RECORDER.error(ibx.getMessage(), ibx);

            throw new SecurityException(ibx.getMessage(), ibx);
        }
        catch (BadPaddingException bpx)
        {
            ERROR_RECORDER.error(bpx.getMessage(), bpx);

            throw new SecurityException(bpx.getMessage(), bpx);
        }

        return decPass;
    }
}
