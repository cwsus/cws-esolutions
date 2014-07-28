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
package com.cws.esolutions.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.lang.RandomStringUtils;

import com.cws.esolutions.security.utils.PasswordUtils;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security
 * File: PasswordUtility.java
 *
 * History
 *
 * ----------------------------------------------------------------------------
 * 35033355 @ Jul 28, 2014 1:27:05 PM
 *     Created.
 */
@SuppressWarnings("static-access")
public class PasswordUtility
{
    private static Options options = null;
    private static OptionGroup cryptoOptions = null;

    private static final String CNAME = PasswordUtility.class.getName();

    static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityServiceConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    /**
     * TODO: Add in the method description/comments
     *
     */
    public PasswordUtility()
    {
        final String methodName = PasswordUtility.CNAME
                + "#PasswordUtility()#Constructor";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

    }

    static
    {
        OptionGroup commandOptions = new OptionGroup();
        commandOptions.addOption(OptionBuilder.withLongOpt("encrypt")
            .withDescription("Encrypt the provided string")
            .isRequired(false)
            .create());
        commandOptions.addOption(OptionBuilder.withLongOpt("decrypt")
            .withDescription("Decrypt the provided string")
            .isRequired(false)
            .create());
        commandOptions.addOption(OptionBuilder.withLongOpt("encode")
            .withDescription("Base64-encode the provided string")
            .isRequired(false)
            .create());
        commandOptions.addOption(OptionBuilder.withLongOpt("decode")
            .withDescription("Base64-decode the provided string")
            .isRequired(false)
            .create());

        cryptoOptions = new OptionGroup();
        cryptoOptions.addOption(OptionBuilder.withLongOpt("length")
            .withDescription("The length of the salt to utilize. Only valid with the \"encrypt\" option.")
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
        final String methodName = PasswordUtility.CNAME + "#main(final String[] args)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", (Object) args);
        }

        if (args.length == 0)
        {
            HelpFormatter usage = new HelpFormatter();
            usage.printHelp(PasswordUtility.CNAME, options, true);

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
                        formatter.printHelp(PasswordUtility.CNAME, options, true);

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
                    formatter.printHelp(PasswordUtility.CNAME, options, true);

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
            formatter.printHelp(PasswordUtility.CNAME, options, true);
        }
        catch (SecurityException ssx)
        {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(PasswordUtility.CNAME, options, true);
        }
    }
}
