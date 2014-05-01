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
import org.apache.commons.cli.Options;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.BadPaddingException;
import java.security.InvalidKeyException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.CommandLine;
import javax.crypto.NoSuchPaddingException;
import org.apache.commons.cli.OptionBuilder;
import java.io.UnsupportedEncodingException;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import javax.crypto.IllegalBlockSizeException;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Base64;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.lang.RandomStringUtils;

import com.cws.esolutions.security.SecurityServiceConstants;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 */
@SuppressWarnings("static-access")
public final class PasswordUtils
{
    private static Options options = null;
    private static OptionGroup cryptoOptions = null;

    private static final String CNAME = PasswordUtils.class.getName();

    static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityServiceConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    static
    {
        OptionGroup commandOptions = new OptionGroup();
        commandOptions.addOption(OptionBuilder.withLongOpt("encrypt")
            .withDescription("Perform an SSH connection to a target host")
            .isRequired(false)
            .create());
        commandOptions.addOption(OptionBuilder.withLongOpt("decrypt")
            .withDescription("Perform an SCP connection to a target host")
            .isRequired(false)
            .create());
        commandOptions.addOption(OptionBuilder.withLongOpt("encode")
            .withDescription("Perform an SSH connection to a target host")
            .isRequired(false)
            .create());
        commandOptions.addOption(OptionBuilder.withLongOpt("decode")
            .withDescription("Perform an SCP connection to a target host")
            .isRequired(false)
            .create());

        cryptoOptions = new OptionGroup();
        cryptoOptions.addOption(OptionBuilder.withLongOpt("length")
            .withDescription("The port number to connect to the server on")
            .hasArg(true)
            .withArgName("LENGTH")
            .withType(Integer.class)
            .isRequired(true)
            .create());
        cryptoOptions.addOption(OptionBuilder.withLongOpt("reversible")
            .withDescription("Perform a two-way (reversible) encryption against the string provided")
            .isRequired(false)
            .create());
        cryptoOptions.addOption(OptionBuilder.withLongOpt("salt")
            .withDescription("The salt value the string was originally encrypted with. Required if option 'decrypt' is selected.")
            .withArgName("SALT")
            .withType(String.class)
            .isRequired(false)
            .create());
        cryptoOptions.addOption(OptionBuilder.withLongOpt("algorithm")
            .withDescription("Algorithm to utilize for encryption. Required unless reversible encryption is requested.")
            .withArgName("ALGORITHM")
            .withType(String.class)
            .isRequired(false)
            .create());
        cryptoOptions.addOption(OptionBuilder.withLongOpt("iterations")
            .withDescription("Number of iterations to pass for encryption. Required unless reversible encryption is requested.")
            .withArgName("ITERATIONS")
            .withType(Integer.class)
            .isRequired(false)
            .create());

        options = new Options();
        options.addOptionGroup(commandOptions);
        options.addOptionGroup(cryptoOptions);
    }

    public static final void main(final String[] args)
    {
        final String methodName = PasswordUtils.CNAME + "#main(final String[] args)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", (Object) args);
        }

        if (args.length == 0)
        {
            HelpFormatter usage = new HelpFormatter();
            usage.printHelp(PasswordUtils.CNAME, options, true);

            return;
        }

        try
        {
            CommandLineParser parser = new PosixParser();
            CommandLine commandLine = parser.parse(options, args);

            if (commandLine.hasOption("encrypt"))
            {
                if (commandLine.hasOption("reversible"))
                {
                    final String salt = RandomStringUtils.randomAlphanumeric(Integer.parseInt(commandLine.getOptionValue("length", "64")));
                    final String encrypted = PasswordUtils.encryptText((String) commandLine.getArgList().get(0), salt);
                    
                    System.out.println("Plain: " + commandLine.getArgList().get(0) + " - Salt: " + salt + ", Encrypted: " + encrypted);
                }
                else
                {
                    if (!(commandLine.hasOption("algorithm")) || (!(commandLine.hasOption("iterations"))))
                    {
                        HelpFormatter formatter = new HelpFormatter();
                        formatter.printHelp(PasswordUtils.CNAME, options, true);

                        return;
                    }

                    final String salt = RandomStringUtils.randomAlphanumeric(Integer.parseInt(commandLine.getOptionValue("length", "64")));
                    final String encrypted = PasswordUtils.encryptText((String) commandLine.getArgList().get(0), salt,
                        commandLine.getOptionValue("algorithm"),
                        Integer.parseInt(commandLine.getOptionValue("iterations", "65535")));

                    System.out.println("Plain: " + commandLine.getArgList().get(0) + " - Salt: " + salt + ", Encrypted: " + encrypted);
                }
            }
            else if (commandLine.hasOption("decrypt"))
            {
                if (!(commandLine.hasOption("length")))
                {
                    HelpFormatter formatter = new HelpFormatter();
                    formatter.printHelp(PasswordUtils.CNAME, options, true);

                    return;
                }

                System.out.println("Encrypted: " + commandLine.getArgList().get(0) + ", Decrypted: " +
                    PasswordUtils.decryptText((String) commandLine.getArgList().get(0),
                            Integer.parseInt(commandLine.getOptionValue("length", "64"))));
            }
            else if (commandLine.hasOption("encode"))
            {
                System.out.println(PasswordUtils.base64Encode((String) commandLine.getArgList().get(0)));
            }
            else if (commandLine.hasOption("decode"))
            {
                System.out.println(PasswordUtils.base64Decode((String) commandLine.getArgList().get(0)));
            }
        }
        catch (ParseException px)
        {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(PasswordUtils.CNAME, options, true);
        }
        catch (SecurityException ssx)
        {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(PasswordUtils.CNAME, options, true);
        }
    }

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

    public static final String base64Encode(final String text) throws SecurityException
    {
        final String methodName = PasswordUtils.CNAME + "#base64Encode(final String text) throws SecurityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        String response = null;

        try
        {
            response = Base64.encodeBase64String(text.getBytes("UTF-8"));
        }
        catch (UnsupportedEncodingException uex)
        {
            throw new SecurityException(uex.getMessage(), uex);
        }

        return response;
    }

    public static final String base64Decode(final String text) throws SecurityException
    {
        final String methodName = PasswordUtils.CNAME + "#base64Decode(final String text) throws SecurityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        String response = null;

        try
        {
            response = new String(Base64.decodeBase64(text.getBytes("UTF-8")));
        }
        catch (UnsupportedEncodingException uex)
        {
            throw new SecurityException(uex.getMessage(), uex);
        }

        return response;
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
