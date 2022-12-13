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
 * File: PasswordUtils.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import javax.crypto.Mac;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.security.MessageDigest;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;
import org.apache.logging.log4j.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidKeyException;
import java.security.AlgorithmParameters;
import javax.crypto.NoSuchPaddingException;
import org.apache.logging.log4j.LogManager;
import java.io.UnsupportedEncodingException;
import javax.crypto.IllegalBlockSizeException;
import org.apache.commons.codec.binary.Base32;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.InvalidAlgorithmParameterException;

import com.cws.esolutions.security.SecurityServiceConstants;
/**
 * Performs password related functions, such as string encryption
 * and (where necessary) decryption, base64 decode/encode.
 *
 * @author cws-khuntly
 * @version 1.0
 */
public final class PasswordUtils
{
    private static final String CNAME = PasswordUtils.class.getName();

    static final Logger DEBUGGER = LogManager.getLogger(SecurityServiceConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    /**
     * Provides two-way (reversible) encryption of a provided string. Can be used where reversibility
     * is required but encryption (obfuscation, technically) is required.
     *
     * @param value - The plain text data to encrypt
     * @param salt - The salt value to utilize for the request
     * @param secretInstance - The cryptographic instance to use for the SecretKeyFactory
     * @param iterations - The number of times to loop through the keyspec
     * @param keyBits - The size of the key, in bits
     * @param algorithm - The algorithm to encrypt the data with
     * @param cipherInstance - The cipher instance to utilize
     * @param encoding - The text encoding
     * @return The encrypted string in a reversible format
     * @throws SecurityException {@link java.lang.SecurityException} if an exception occurs during processing
     */
    public static final String encryptText(final String value, final String salt, final String secretInstance, final int iterations, final int keyBits, final String algorithm, final String cipherInstance, final String encoding) throws SecurityException
    {
        final String methodName = PasswordUtils.CNAME + "#encryptText(final String value, final String salt, final String secretInstance, final int iterations, final int keyBits, final String algorithm, final String cipherInstance, final String encoding) throws SecurityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", secretInstance);
            DEBUGGER.debug("Value: {}", iterations);
            DEBUGGER.debug("Value: {}", keyBits);
            DEBUGGER.debug("Value: {}", algorithm);
            DEBUGGER.debug("Value: {}", cipherInstance);
            DEBUGGER.debug("Value: {}", encoding);
        }

        String encPass = null;

        try
        {
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(secretInstance);
            PBEKeySpec keySpec = new PBEKeySpec(salt.toCharArray(), salt.getBytes(), iterations, keyBits);
            SecretKey keyTmp = keyFactory.generateSecret(keySpec);
            SecretKeySpec sks = new SecretKeySpec(keyTmp.getEncoded(), algorithm);

            Cipher pbeCipher = Cipher.getInstance(cipherInstance);
            pbeCipher.init(Cipher.ENCRYPT_MODE, sks);

            AlgorithmParameters parameters = pbeCipher.getParameters();
            IvParameterSpec ivParameterSpec = parameters.getParameterSpec(IvParameterSpec.class);

            byte[] cryptoText = pbeCipher.doFinal(value.getBytes(encoding));
            byte[] iv = ivParameterSpec.getIV();

            String combined = Base64.getEncoder().encodeToString(iv) + ":" + Base64.getEncoder().encodeToString(cryptoText);

            encPass = Base64.getEncoder().encodeToString(combined.getBytes());
        }
        catch (final InvalidKeyException ikx)
        {
            throw new SecurityException(ikx.getMessage(), ikx);
        }
        catch (final NoSuchAlgorithmException nsx)
        {
            throw new SecurityException(nsx.getMessage(), nsx);
        }
        catch (final NoSuchPaddingException npx)
        {
            throw new SecurityException(npx.getMessage(), npx);
        }
        catch (final IllegalBlockSizeException ibx)
        {
            throw new SecurityException(ibx.getMessage(), ibx);
        }
        catch (final BadPaddingException bpx)
        {
            throw new SecurityException(bpx.getMessage(), bpx);
        }
        catch (final UnsupportedEncodingException uex)
        {
            throw new SecurityException(uex.getMessage(), uex);
        }
        catch (final InvalidKeySpecException iksx)
        {
            throw new SecurityException(iksx.getMessage(), iksx);
        }
        catch (final InvalidParameterSpecException ipsx)
        {
            throw new SecurityException(ipsx.getMessage(), ipsx);
        }

        return encPass;
    }

    /**
     * Provides one-way (irreversible) encryption of a provided string.
     *
     * @param plainText - The plain text data to encrypt
     * @param salt - The salt value to utilize for the request
     * @param instance - The security instance to utilize
     * @param iterations - The number of times the value should be re-encrypted
     * @param encoding - The text encoding
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

            response = Base64.getEncoder().encodeToString(input);
        }
        catch (final NoSuchAlgorithmException nsx)
        {
            throw new SecurityException(nsx.getMessage(), nsx);
        }
        catch (final UnsupportedEncodingException uex)
        {
            throw new SecurityException(uex.getMessage(), uex);
        }

        return response;
    }

    /**
     * Provides two-way (reversible) encryption of a provided string. Can be used where reversibility
     * is required but encryption (obfuscation, technically) is required.
     *
     * @param value - The plain text data to encrypt
     * @param salt - The salt value to utilize for the request
     * @param secretInstance - The cryptographic instance to use for the SecretKeyFactory
     * @param iterations - The number of times to loop through the keyspec
     * @param keyBits - The size of the key, in bits
     * @param algorithm - The algorithm to encrypt the data with
     * @param cipherInstance - The cipher instance to utilize
     * @param encoding - The text encoding
     * @return The encrypted string in a reversible format
     * @throws SecurityException {@link java.lang.SecurityException} if an exception occurs during processing
     */
    public static final String decryptText(final String value, final String salt, final String secretInstance, final int iterations, final int keyBits, final String algorithm, final String cipherInstance, final String encoding) throws SecurityException
    {
        final String methodName = PasswordUtils.CNAME + "#encryptText(final String value, final String salt, final String secretInstance, final int iterations, final int keyBits, final String algorithm, final String cipherInstance, final String encoding) throws SecurityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", secretInstance);
            DEBUGGER.debug("Value: {}", iterations);
            DEBUGGER.debug("Value: {}", keyBits);
            DEBUGGER.debug("Value: {}", algorithm);
            DEBUGGER.debug("Value: {}", cipherInstance);
            DEBUGGER.debug("Value: {}", encoding);
        }

        String decPass = null;

        try
        {
            String decoded = new String(Base64.getDecoder().decode(value));
            String iv = decoded.split(":")[0];
            String property = decoded.split(":")[1];

            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(secretInstance);
            PBEKeySpec keySpec = new PBEKeySpec(salt.toCharArray(), salt.getBytes(), iterations, keyBits);
            SecretKey keyTmp = keyFactory.generateSecret(keySpec);
            SecretKeySpec sks = new SecretKeySpec(keyTmp.getEncoded(), algorithm);

            Cipher pbeCipher = Cipher.getInstance(cipherInstance);
            pbeCipher.init(Cipher.DECRYPT_MODE, sks, new IvParameterSpec(Base64.getDecoder().decode(iv)));
            decPass = new String(pbeCipher.doFinal(Base64.getDecoder().decode(property)), encoding);
        }
        catch (final InvalidKeyException ikx)
        {
            throw new SecurityException(ikx.getMessage(), ikx);
        }
        catch (final NoSuchAlgorithmException nsx)
        {
            throw new SecurityException(nsx.getMessage(), nsx);
        }
        catch (final NoSuchPaddingException npx)
        {
            throw new SecurityException(npx.getMessage(), npx);
        }
        catch (final IllegalBlockSizeException ibx)
        {
            throw new SecurityException(ibx.getMessage(), ibx);
        }
        catch (final BadPaddingException bpx)
        {
            throw new SecurityException(bpx.getMessage(), bpx);
        }
        catch (final UnsupportedEncodingException uex)
        {
            throw new SecurityException(uex.getMessage(), uex);
        }
        catch (final InvalidAlgorithmParameterException iapx)
        {
            throw new SecurityException(iapx.getMessage(), iapx);
        }
        catch (final InvalidKeySpecException iksx)
        {
            throw new SecurityException(iksx.getMessage(), iksx);
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
    public static final String base64Encode(final String text) throws SecurityException
    {
        final String methodName = PasswordUtils.CNAME + "#base64Encode(final String text) throws SecurityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", text);
        }

        return Base64.getEncoder().encodeToString(text.getBytes());
    }

    /**
     * Base64 decodes a given string
     *
     * @param text - The text to base64 decode
     * @return The base64-decoded string
     * @throws SecurityException {@link java.lang.SecurityException} if an exception occurs during processing
     */
    public static final String base64Decode(final String text) throws SecurityException
    {
        final String methodName = PasswordUtils.CNAME + "#base64Decode(final String text) throws SecurityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", text);
        }

        return new String(Base64.getDecoder().decode(text.getBytes()));
    }

    /**
     * Base64 decodes a given string
     *
     * @param variance - The allowed differences in OTP values
     * @param algorithm - The algorithm to encrypt the data with
     * @param instance - The security instance to utilize
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
        catch (final InvalidKeyException ikx)
        {
            throw new SecurityException(ikx.getMessage(), ikx);
        }
        catch (final NoSuchAlgorithmException nsx)
        {
            throw new SecurityException(nsx.getMessage(), nsx);
        }
    }
}