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

import java.io.File;
import org.slf4j.Logger;
import java.io.IOException;
import org.slf4j.LoggerFactory;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.lang.RandomStringUtils;

import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.utils.PasswordUtils;
import com.cws.esolutions.security.SecurityServiceConstants;
import com.cws.esolutions.security.config.xml.SecurityConfig;
import com.cws.esolutions.security.exception.SecurityServiceException;
import com.cws.esolutions.security.config.xml.RepositoryConfiguration;
import com.cws.esolutions.security.config.xml.SecurityConfigurationData;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
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

    private static final String CNAME = PasswordUtility.class.getName();
    private static final SecurityServiceBean svcBean = SecurityServiceBean.getInstance();
    private static final String LOG_CONFIG = System.getProperty("user.home") + "/etc/logging.xml";
    private static final String SEC_CONFIG = System.getProperty("user.home") + "/etc/ServiceConfig.xml";

    static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityServiceConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityServiceConstants.ERROR_LOGGER + CNAME);

    static
    {
        Option configOption = OptionBuilder.withLongOpt("configFile")
            .withArgName("configFile")
            .withDescription("Provide location of configuration file")
            .isRequired(false)
            .hasArg(true)
            .create();

        if (DEBUG)
        {
            DEBUGGER.debug("Option configOption: {}", configOption);
        }

        OptionGroup configOptions = new OptionGroup().addOption(configOption);

        if (DEBUG)
        {
            DEBUGGER.debug("OptionGroup configOptions: {}", configOptions);
        }

        Option entryNameOption = OptionBuilder.withLongOpt("entry")
            .hasArg(true)
            .withArgName("entry")
            .withDescription("Name for the entry")
            .isRequired(false)
            .create();

        Option usernameOption = OptionBuilder.withLongOpt("username")
            .hasArg(true)
            .withArgName("username")
            .withDescription("Username for the entry")
            .isRequired(false)
            .create();

        Option passwordOption = OptionBuilder.withLongOpt("password")
            .hasArg(true)
            .withArgName("password")
            .withDescription("Username for the entry")
            .isRequired(false)
            .create();

        if (DEBUG)
        {
            DEBUGGER.debug("Option entryNameOption: {}", entryNameOption);
            DEBUGGER.debug("Option usernameOption: {}", usernameOption);
            DEBUGGER.debug("Option passwordOption: {}", passwordOption);
        }

        Option encryptOption = OptionBuilder.withLongOpt("encrypt")
            .hasArg(false)
            .withArgName("entry")
            .withArgName("username")
            .withArgName("password")
            .withDescription("Encrypt the provided string")
            .isRequired(false)
            .create();

        if (DEBUG)
        {
            DEBUGGER.debug("Option encryptOption: {}", encryptOption);
        }

        OptionGroup encryptOptionsGroup = new OptionGroup()
            .addOption(encryptOption)
            .addOption(entryNameOption)
            .addOption(usernameOption)
            .addOption(passwordOption);

        if (DEBUG)
        {
            DEBUGGER.debug("OptionGroup encryptOptionsGroup: {}", encryptOptionsGroup);
        }

        Option decryptOption = OptionBuilder.withLongOpt("decrypt")
            .hasArg(false)
            .withDescription("Decrypt the provided string")
            .isRequired(false)
            .create();

        if (DEBUG)
        {
            DEBUGGER.debug("Option decryptOption: {}", decryptOption);
        }

        OptionGroup decryptOptionsGroup = new OptionGroup()
            .addOption(decryptOption)
            .addOption(entryNameOption);

        if (DEBUG)
        {
            DEBUGGER.debug("OptionGroup decryptOptionsGroup: {}", decryptOptionsGroup);
        }

        options = new Options();
        options.addOptionGroup(configOptions);
        options.addOptionGroup(encryptOptionsGroup);
        options.addOptionGroup(decryptOptionsGroup);

        if (DEBUG)
        {
            DEBUGGER.debug("Options options: {}", options);
        }

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

            if (DEBUG)
            {
                DEBUGGER.debug("CommandLineParser parser: {}", parser);
                DEBUGGER.debug("CommandLine commandLine: {}", commandLine);
                DEBUGGER.debug("CommandLine commandLine.getOptions(): {}", commandLine.getOptions());
                DEBUGGER.debug("CommandLine commandLine.getArgList(): {}", commandLine.getArgList());
            }

            if ((commandLine.hasOption("configFile")) && (!(StringUtils.isBlank(commandLine.getOptionValue("configFile")))))
            {
                SecurityServiceInitializer.initializeService(commandLine.getOptionValue("configFile"), PasswordUtility.LOG_CONFIG, false);
            }
            else
            {
                SecurityServiceInitializer.initializeService(PasswordUtility.SEC_CONFIG, PasswordUtility.LOG_CONFIG, false);
            }

            final SecurityConfigurationData secConfigData = PasswordUtility.svcBean.getConfigData();
            final SecurityConfig secConfig = secConfigData.getSecurityConfig();
            final RepositoryConfiguration repoConfig = secConfigData.getRepoConfig();

            if (DEBUG)
            {
                DEBUGGER.debug("SecurityConfigurationData secConfig: {}", secConfigData);
                DEBUGGER.debug("SecurityConfig secConfig: {}", secConfig);
                DEBUGGER.debug("RepositoryConfiguration secConfig: {}", repoConfig);
            }

            if (commandLine.hasOption("encrypt"))
            {
                String entryName = (String) commandLine.getOptionValue("entry");
                String username = (String) commandLine.getOptionValue("username");
                String password = (String) commandLine.getOptionValue("password");
                int length = (commandLine.hasOption("iterations")) ? Integer.parseInt(commandLine.getOptionValue("length")) : secConfig.getSaltLength();

                if (DEBUG)
                {
                    DEBUGGER.debug("String entryName: {}", entryName);
                    DEBUGGER.debug("String username: {}", username);
                    DEBUGGER.debug("String password: {}", password);
                    DEBUGGER.debug("String length: {}", length);
                }

                final String salt = RandomStringUtils.randomAlphanumeric(length);
                final String encrypted = PasswordUtils.encryptText(password, salt);

                if (DEBUG)
                {
                    DEBUGGER.debug("String salt: {}", salt);
                    DEBUGGER.debug("String encrypted: {}", encrypted);
                }

                if (StringUtils.isNotBlank(repoConfig.getPasswordFile()))
                {
                    try
                    {
                        File passwordFile = FileUtils.getFile(repoConfig.getPasswordFile());
                        File saltFile = FileUtils.getFile(repoConfig.getSaltFile());

                        if (DEBUG)
                        {
                            DEBUGGER.debug("File passwordFile: {}", passwordFile);
                            DEBUGGER.debug("File saltFile: {}", saltFile);
                        }

                        new File(passwordFile.getPath()).mkdirs();
                        new File(saltFile.getPath()).mkdirs();
                        passwordFile.createNewFile();
                        saltFile.createNewFile();
                    }
                    catch (IOException iox)
                    {
                        ERROR_RECORDER.error(iox.getMessage(), iox);
                    }
                }

                System.out.println("Entry Name: " + entryName + "; Username: " + username + "; Plain Text: " + password + "; Salt: " + salt + "; Encrypted: " + encrypted);
            }
            else if (commandLine.hasOption("decrypt"))
            {
                String encrypted = (String) commandLine.getArgList().get(0);
                int length = encrypted.length();

                if (!(commandLine.hasOption("length")))
                {
                    HelpFormatter formatter = new HelpFormatter();
                    formatter.printHelp(PasswordUtility.CNAME, options, true);

                    return;
                }

                System.out.println("Encrypted: " + commandLine.getArgList().get(0) + ", Decrypted: " +
                        PasswordUtils.decryptText((String) commandLine.getArgList().get(0), length));
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
            ERROR_RECORDER.error(px.getMessage(), px);

            System.err.println("An error occurred during processing: " + px.getMessage());
        }
        catch (SecurityException sx)
        {
            ERROR_RECORDER.error(sx.getMessage(), sx);

            System.err.println("An error occurred during processing: " + sx.getMessage());
        }
        catch (SecurityServiceException ssx)
        {
            ERROR_RECORDER.error(ssx.getMessage(), ssx);

            System.err.println("An error occurred during processing: " + ssx.getMessage());
        }
    }
}
