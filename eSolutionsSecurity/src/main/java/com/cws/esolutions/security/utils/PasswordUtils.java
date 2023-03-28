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
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;
import org.apache.logging.log4j.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidKeyException;
import java.security.AlgorithmParameters;
import jakarta.xml.bind.DatatypeConverter;
import javax.crypto.NoSuchPaddingException;
import org.apache.logging.log4j.LogManager;
import java.io.UnsupportedEncodingException;
import javax.crypto.IllegalBlockSizeException;
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
     * Provides an encryption method for given values
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
    public static final String encryptText(final char[] value, final byte[] salt, final String secretInstance, final int iterations, final int keyBits, final String algorithm, final String cipherInstance, final String encoding) throws SecurityException
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

        String result = null;

        try
        {
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(secretInstance);
            PBEKeySpec keySpec = new PBEKeySpec(value, salt, iterations, keyBits);
            SecretKey keyTmp = keyFactory.generateSecret(keySpec);
            SecretKeySpec sks = new SecretKeySpec(keyTmp.getEncoded(), algorithm);
            Cipher pbeCipher = Cipher.getInstance(cipherInstance);
            pbeCipher.init(Cipher.ENCRYPT_MODE, sks);

            AlgorithmParameters parameters = pbeCipher.getParameters();
            IvParameterSpec ivParameterSpec = parameters.getParameterSpec(IvParameterSpec.class);

            byte[] cryptoText = pbeCipher.doFinal(new String(value).getBytes(encoding));
            byte[] iv = ivParameterSpec.getIV();

            result = PasswordUtils.base64Encode(DatatypeConverter.printBase64Binary(iv) + ":" + DatatypeConverter.printBase64Binary(cryptoText));
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
        catch (final InvalidKeySpecException iksx)
        {
            throw new SecurityException(iksx.getMessage(), iksx);
        }
        catch (final InvalidParameterSpecException ipsx)
        {
            throw new SecurityException(ipsx.getMessage(), ipsx);
        }
        catch (UnsupportedEncodingException uex)
        {
        	throw new SecurityException(uex.getMessage(), uex);
		}

        return result;
    }

    /**
     * Provides an encryption method for given values
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
    public static final String decryptText(final String password, final char[] value, final byte[] salt, final String secretInstance, final int iterations, final int keyBits, final String algorithm, final String cipherInstance, final String encoding) throws SecurityException
    {
        final String methodName = PasswordUtils.CNAME + "#decryptText(final String password, final char[] value, final byte[] salt, final String secretInstance, final int iterations, final int keyBits, final String algorithm, final String cipherInstance, final String encoding) throws SecurityException";

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

        String result = null;
        String base = PasswordUtils.base64Decode(password);
        String iv = base.split(":")[0];
        String property = base.split(":")[1];

        try
        {
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(secretInstance);
            PBEKeySpec keySpec = new PBEKeySpec(value, salt, iterations, keyBits);
            SecretKey keyTmp = keyFactory.generateSecret(keySpec);
            SecretKeySpec sks = new SecretKeySpec(keyTmp.getEncoded(), algorithm);
            Cipher pbeCipher = Cipher.getInstance(cipherInstance);
            pbeCipher.init(Cipher.DECRYPT_MODE, sks, new IvParameterSpec(DatatypeConverter.parseBase64Binary(iv)));

            result = new String(pbeCipher.doFinal(DatatypeConverter.parseBase64Binary(property)));
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
        catch (final InvalidKeySpecException iksx)
        {
            throw new SecurityException(iksx.getMessage(), iksx);
        }
        catch (InvalidAlgorithmParameterException iapx)
        {
        	throw new SecurityException(iapx.getMessage(), iapx);
		}

        return result;
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
            String iv = value.split(":")[0];
            String property = value.split(":")[1];

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
}