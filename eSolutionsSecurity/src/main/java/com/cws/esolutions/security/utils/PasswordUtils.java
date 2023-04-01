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
import java.security.spec.KeySpec;
import java.security.SecureRandom;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;
import org.apache.logging.log4j.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidKeyException;
import javax.crypto.NoSuchPaddingException;
import org.apache.logging.log4j.LogManager;
import java.io.UnsupportedEncodingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.lang3.RandomStringUtils;
import java.security.spec.InvalidKeySpecException;
import org.apache.commons.codec.digest.DigestUtils;
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
     * @param encoding - The text encoding
     * @return The encrypted string in a reversible format
     * @throws SecurityException {@link java.lang.SecurityException} if an exception occurs during processing
     */
    public static final String encryptText(final char[] value, final String salt, final String secretInstance, final int iterations, final int keyBits, final String encoding) throws SecurityException
    {
        final String methodName = PasswordUtils.CNAME + "#encryptText(final char[] value, final String salt, final String secretInstance, final int iterations, final int keyBits, final String encoding) throws SecurityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
            DEBUGGER.debug("Value: {}", salt);
            DEBUGGER.debug("Value: {}", secretInstance);
            DEBUGGER.debug("Value: {}", iterations);
            DEBUGGER.debug("Value: {}", keyBits);
            DEBUGGER.debug("Value: {}", encoding);
        }

        String response = null;

        try
        {
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(secretInstance);
            KeySpec keySpec = new PBEKeySpec(value, salt.getBytes(encoding), iterations, keyBits);

            if (DEBUG)
            {
            	DEBUGGER.debug("SecretKeyFactory: {}", keyFactory);
            	DEBUGGER.debug("KeySpec: {}", keySpec);
            }

            byte[] hashed = keyFactory.generateSecret(keySpec).getEncoded();

            if (DEBUG)
            {
            	DEBUGGER.debug("byte[]: {}", hashed);
            }

            response = DigestUtils.sha512Hex(hashed);

            if (DEBUG)
            {
            	DEBUGGER.debug("response: {}", response);
            }
            
        }
        catch (final NoSuchAlgorithmException nsx)
        {
            throw new SecurityException(nsx.getMessage(), nsx);
        }
        catch (final InvalidKeySpecException iksx)
        {
            throw new SecurityException(iksx.getMessage(), iksx);
        }
        catch (UnsupportedEncodingException uex)
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

    public static final String returnGeneratedSalt(final String generator, final int length)
    {
        final String methodName = PasswordUtils.CNAME + "#returnGeneratedSalt(final String generator, final int length)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", generator);
            DEBUGGER.debug("Value: {}", length);
        }

        String newSalt = null;

		try
		{
			SecureRandom sRandom = SecureRandom.getInstance(generator);
        	byte[] salt = new byte[length];
        	sRandom.nextBytes(salt);

            if (DEBUG)
            {
                DEBUGGER.debug("SecureRandom: {}", sRandom);
                DEBUGGER.debug("salt: {}", salt);
            }

        	newSalt = DigestUtils.sha512Hex(salt);
		}
		catch (NoSuchAlgorithmException nsax)
		{
			newSalt = RandomStringUtils.randomAlphanumeric(length);
		}

		return newSalt;
    }
}