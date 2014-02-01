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
 * File: PasswordUtils.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import javax.crypto.Mac;
import org.slf4j.Logger;
import javax.crypto.Cipher;
import org.slf4j.LoggerFactory;
import java.security.MessageDigest;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.BadPaddingException;
import java.security.InvalidKeyException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import javax.crypto.IllegalBlockSizeException;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Base64;
import java.security.NoSuchAlgorithmException;

import com.cws.esolutions.security.SecurityServiceConstants;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 */
public final class PasswordUtils
{
    private static final String CNAME = PasswordUtils.class.getName();

    static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityServiceConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public static final String encryptText(final String plainText, final String salt) throws SecurityException
    {
        final String methodName = PasswordUtils.CNAME + "#encryptText(final String plainText, final String salt) throws SecurityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        String encPass = null;

        final byte[] SECRET_KEY = new byte[] { 'A', 'M', 'C', 'P', 'a', 's', 's', 'C', 'r', 'y', 't', 'o', 'K', 'e', 'y', '!' };

        try
        {
            SecretKeySpec sks = new SecretKeySpec(SECRET_KEY, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, sks);

            encPass = new String(Base64.encodeBase64(cipher.doFinal(new String(salt + plainText).getBytes("UTF-8"))));
        }
        catch (InvalidKeyException ikx)
        {
            throw new SecurityException(ikx.getMessage(), ikx);
        }
        catch (NoSuchAlgorithmException nsx)
        {
            throw new SecurityException(nsx.getMessage(), nsx);
        }
        catch (NoSuchPaddingException npx)
        {
            throw new SecurityException(npx.getMessage(), npx);
        }
        catch (IllegalBlockSizeException ibx)
        {
            throw new SecurityException(ibx.getMessage(), ibx);
        }
        catch (BadPaddingException bpx)
        {
            throw new SecurityException(bpx.getMessage(), bpx);
        }
        catch (UnsupportedEncodingException uex)
        {
            throw new SecurityException(uex.getMessage(), uex);
        }

        return encPass;
    }

    public static final String encryptText(final String plainText, final String salt, final String algorithm, final int iterations) throws SecurityException
    {
        final String methodName = PasswordUtils.CNAME + "#encryptText(final String plainText, final String salt, final String algorithm, final int iterations) throws SecurityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        String response = null;

        try
        {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.reset();
            md.update(salt.getBytes("UTF-8"));
            byte[] input = md.digest(plainText.getBytes("UTF-8"));

            for (int x = 0; x < iterations; x++)
            {
                md.reset();
                input = md.digest(input);
            }

            response = new String(Base64.encodeBase64(input));
        }
        catch (NoSuchAlgorithmException nsx)
        {
            throw new SecurityException(nsx.getMessage(), nsx);
        }
        catch (UnsupportedEncodingException uex)
        {
            throw new SecurityException(uex.getMessage(), uex);
        }

        return response;
    }

    public static final String decryptText(final String encrypted, final int saltLength) throws SecurityException
    {
        final String methodName = PasswordUtils.CNAME + "#decryptText(final String encrypted, final int saltLength) throws SecurityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        String decPass = null;

        final byte[] SECRET_KEY = new byte[] { 'A', 'M', 'C', 'P', 'a', 's', 's', 'C', 'r', 'y', 't', 'o', 'K', 'e', 'y', '!' };

        try
        {
            SecretKeySpec sks = new SecretKeySpec(SECRET_KEY, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, sks);

            decPass = new String(cipher.doFinal(Base64.decodeBase64(encrypted.getBytes("UTF-8")))).substring(saltLength);
        }
        catch (InvalidKeyException ikx)
        {
            throw new SecurityException(ikx.getMessage(), ikx);
        }
        catch (NoSuchAlgorithmException nsx)
        {
            throw new SecurityException(nsx.getMessage(), nsx);
        }
        catch (NoSuchPaddingException npx)
        {
            throw new SecurityException(npx.getMessage(), npx);
        }
        catch (IllegalBlockSizeException ibx)
        {
            throw new SecurityException(ibx.getMessage(), ibx);
        }
        catch (BadPaddingException bpx)
        {
            throw new SecurityException(bpx.getMessage(), bpx);
        }
        catch (UnsupportedEncodingException uex)
        {
            throw new SecurityException(uex.getMessage(), uex);
        }

        return decPass;
    }

    public static final boolean validateOtpValue(final int variance, final String algorithm, final String secret, final int code) throws SecurityException
    {
        final String methodName = PasswordUtils.CNAME + "#validateOtpValue(final int variance, final String algorithm, final String secret, final int code) throws SecurityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("int: {}", variance);
            DEBUGGER.debug("String: {}", algorithm);
        }

        long truncatedHash = 0;
        byte[] data = new byte[8];
        long timeIndex = System.currentTimeMillis() / 1000 / 30;

        final Base32 codec = new Base32();
        final byte[] decoded = codec.decode(secret);
        SecretKeySpec signKey = new SecretKeySpec(decoded, algorithm);

        if (DEBUG)
        {
            DEBUGGER.debug("long: {}", timeIndex);
        }

        try
        {
            for (int i = 8; i-- > 0; timeIndex >>>= 8)
            {
                data[i] = (byte) timeIndex;
            }

            Mac mac = Mac.getInstance(algorithm);
            mac.init(signKey);
            byte[] hash = mac.doFinal(data);
            int offset = hash[20 - 1] & 0xF;
            
            for (int i = 0; i < 4; i++)
            {
                truncatedHash <<= 8;
                truncatedHash |= (hash[offset + i] & 0xFF);
            }

            truncatedHash &= 0x7FFFFFFF;
            truncatedHash %= 1000000;

            if (DEBUG)
            {
                DEBUGGER.debug("truncatedHash: {}", truncatedHash);
            }

            return (truncatedHash == code);
        }
        catch (InvalidKeyException ikx)
        {
            throw new SecurityException(ikx.getMessage(), ikx);
        }
        catch (NoSuchAlgorithmException nsx)
        {
            throw new SecurityException(nsx.getMessage(), nsx);
        }
    }
}
