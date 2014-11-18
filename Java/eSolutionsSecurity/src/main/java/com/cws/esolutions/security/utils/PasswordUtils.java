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

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.AlgorithmParameters;

import javax.crypto.NoSuchPaddingException;

import java.io.UnsupportedEncodingException;

import javax.crypto.IllegalBlockSizeException;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Base64;

import java.security.NoSuchAlgorithmException;

import com.cws.esolutions.security.SecurityServiceConstants;
/**
 * Performs password related functions, such as string encryption
 * and (where necessary) decryption, base64 decode/encode.
 *
 * @author khuntly
 * @version 1.0
 */
public final class PasswordUtils
{
    private static final String CNAME = PasswordUtils.class.getName();

    static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityServiceConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    /**
     * Provides two-way (reversible) encryption of a provided string. Can be used where reversibility
     * is required but encryption (obfuscation, technically) is required.
     *
     * @param plainText - The plain text data to encrypt
     * @param salt - The salt value to utilize for the request
     * @return The encrypted string in a reversible format
     * @throws SecurityException {@link java.lang.SecurityException} if an exception occurs during processing
     */
    public static final String encryptText(final String plainText, final String salt, final String algorithm, final String instance, final String encoding) throws SecurityException
    {
        final String methodName = PasswordUtils.CNAME + "#encryptText(final String plainText, final String salt, final String algorithm, final String instance, final String encoding) throws SecurityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", plainText);
            DEBUGGER.debug("Value: {}", salt);
            DEBUGGER.debug("Value: {}", algorithm);
            DEBUGGER.debug("Value: {}", instance);
            DEBUGGER.debug("Value: {}", encoding);
        }

        String encPass = null;

        final byte[] SECRET_KEY = new byte[] { 'A', 'M', 'C', 'P', 'a', 's', 's', 'C', 'r', 'y', 't', 'o', 'K', 'e', 'y', '!' };

        try
        {
            SecretKeySpec sks = new SecretKeySpec(SECRET_KEY, algorithm);
            Cipher cipher = Cipher.getInstance(instance);
            cipher.init(Cipher.ENCRYPT_MODE, sks);

            encPass = new String(Base64.encodeBase64(cipher.doFinal(new String(salt + plainText).getBytes(encoding))));
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

    /**
     * Provides one-way (irreversible) encryption of a provided string.
     *
     * @param plainText - The plain text data to encrypt
     * @param salt - The salt value to utilize for the request
     * @param algorithm - The encryption algorithm (e.g. SHA-256) to utilize
     * @param iterations - The number of times the value should be re-encrypted
     * @return The encrypted string
     * @throws SecurityException {@link java.lang.SecurityException} if an exception occurs during processing
     */
    public static final String encryptText(final String plainText, final String salt, final String instance, final int iterations, final String encoding) throws SecurityException
    {
        final String methodName = PasswordUtils.CNAME + "#encryptText(final String plainText, final String salt, final String algorithm, final String instance, final int iterations, final String encoding) throws SecurityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", plainText);
            DEBUGGER.debug("Value: {}", salt);
            DEBUGGER.debug("Value: {}", instance);
            DEBUGGER.debug("Value: {}", iterations);
            DEBUGGER.debug("Value: {}", encoding);
        }

        String response = null;

        try
        {
            MessageDigest md = MessageDigest.getInstance(instance);
            md.reset();
            md.update(salt.getBytes(encoding));
            byte[] input = md.digest(plainText.getBytes(encoding));

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

    /**
     * Provides a reversal process for two-way encrypted strings
     *
     * @param encrypted - The encrypted data to decrypt
     * @param saltLength - The length of the salt used to encrypt the data
     * @return The decrypted string
     * @throws SecurityException {@link java.lang.SecurityException} if an exception occurs during processing
     */
    public static final String decryptText(final String encrypted, final int saltLength, final String algorithm, final String instance, final String encoding) throws SecurityException
    {
        final String methodName = PasswordUtils.CNAME + "#decryptText(final String encrypted, final int saltLength, final String algorithm, final String instance, final String encoding) throws SecurityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", encrypted);
            DEBUGGER.debug("Value: {}", saltLength);
            DEBUGGER.debug("Value: {}", algorithm);
            DEBUGGER.debug("Value: {}", instance);
            DEBUGGER.debug("Value: {}", encoding);
        }

        String decPass = null;

        final byte[] SECRET_KEY = new byte[] { 'A', 'M', 'C', 'P', 'a', 's', 's', 'C', 'r', 'y', 't', 'o', 'K', 'e', 'y', '!' };

        try
        {
            SecretKeySpec sks = new SecretKeySpec(SECRET_KEY, algorithm);
            Cipher cipher = Cipher.getInstance(instance);
            AlgorithmParameters params = cipher.getParameters();
            cipher.init(Cipher.DECRYPT_MODE, sks, params);

            decPass = new String(cipher.doFinal(Base64.decodeBase64(encrypted.getBytes(encoding)))).substring(saltLength);
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
        catch (InvalidAlgorithmParameterException iapx)
        {
            throw new SecurityException(iapx.getMessage(), iapx);
        }

        return decPass;
    }

    /**
     * Base64 encodes a given string
     *
     * @param text - The text to base64 encode
     * @return The base64-encoded string
     * @throws SecurityException {@link java.lang.SecurityException} if an exception occurs during processing
     */
    public static final String base64Encode(final String text, final String encoding) throws SecurityException
    {
        final String methodName = PasswordUtils.CNAME + "#base64Encode(final String text, final String encoding) throws SecurityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", text);
            DEBUGGER.debug("Value: {}", encoding);
        }

        String response = null;

        try
        {
            response = Base64.encodeBase64String(text.getBytes(encoding));
        }
        catch (UnsupportedEncodingException uex)
        {
            throw new SecurityException(uex.getMessage(), uex);
        }

        return response;
    }

    /**
     * Base64 decodes a given string
     *
     * @param text - The text to base64 decode
     * @return The base64-decoded string
     * @throws SecurityException {@link java.lang.SecurityException} if an exception occurs during processing
     */
    public static final String base64Decode(final String text, final String encoding) throws SecurityException
    {
        final String methodName = PasswordUtils.CNAME + "#base64Decode(final String text, final String encoding) throws SecurityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", text);
            DEBUGGER.debug("Value: {}", encoding);
        }

        String response = null;

        try
        {
            response = new String(Base64.decodeBase64(text.getBytes(encoding)));
        }
        catch (UnsupportedEncodingException uex)
        {
            throw new SecurityException(uex.getMessage(), uex);
        }

        return response;
    }

    /**
     * Base64 decodes a given string
     *
     * @param variance - The allowed differences in OTP values
     * @param algorithm - The algorithm used for the OTP hash
     * @param secret - The OTP secret
     * @param code - The OTP code
     * @return <code>true</code> if successful, <code>false</code> otherwise
     * @throws SecurityException {@link java.lang.SecurityException} if an exception occurs during processing
     */
    public static final boolean validateOtpValue(final int variance, final String algorithm, final String instance, final String secret, final int code) throws SecurityException
    {
        final String methodName = PasswordUtils.CNAME + "#validateOtpValue(final int variance, final String algorithm, final String instance, final String secret, final int code) throws SecurityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", variance);
            DEBUGGER.debug("Value: {}", algorithm);
            DEBUGGER.debug("Value: {}", instance);
            DEBUGGER.debug("Value: {}", secret);
            DEBUGGER.debug("Value: {}", code);
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

            Mac mac = Mac.getInstance(instance);
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
