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
package com.cws.esolutions.security.main;

import java.io.File;
import java.util.List;
import java.util.Arrays;
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
import com.cws.esolutions.security.config.xml.SystemConfig;
import com.cws.esolutions.security.SecurityServiceConstants;
import com.cws.esolutions.security.config.xml.SecurityConfig;
import com.cws.esolutions.security.exception.SecurityServiceException;
import com.cws.esolutions.security.config.xml.PasswordRepositoryConfig;
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
    private static final String LOG_CONFIG = System.getProperty("user.home") + "/.etc/SecurityService/logging/logging.xml";
    private static final String SEC_CONFIG = System.getProperty("user.home") + "/.etc/SecurityService/config/ServiceConfig.xml";

    static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityServiceConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityServiceConstants.ERROR_LOGGER + CNAME);

    static
    {
        Option entryNameOption = OptionBuilder.withLongOpt("entry")
            .hasArg(true)
            .withArgName("entry")
            .withDescription("Name for the entry")
            .withType(String.class)
            .isRequired(false)
            .create();

        Option usernameOption = OptionBuilder.withLongOpt("username")
            .hasArg(true)
            .withArgName("username")
            .withDescription("Username for the entry")
            .withType(String.class)
            .isRequired(false)
            .create();

        Option passwordOption = OptionBuilder.withLongOpt("password")
            .hasArg(true)
            .withArgName("password")
            .withDescription("Username for the entry")
            .withType(String.class)
            .isRequired(false)
            .create();

        Option writeToFile = OptionBuilder.withLongOpt("store")
            .hasArg(false)
            .withDescription("Store the entry in the data files")
            .isRequired(false)
            .withType(String.class)
            .create();

        if (DEBUG)
        {
            DEBUGGER.debug("Option entryNameOption: {}", entryNameOption);
            DEBUGGER.debug("Option usernameOption: {}", usernameOption);
            DEBUGGER.debug("Option passwordOption: {}", passwordOption);
            DEBUGGER.debug("Option writeToFile: {}", writeToFile);
        }

        Option encryptOption = OptionBuilder.withLongOpt("encrypt")
            .hasArg(false)
            .withArgName("entry")
            .withArgName("username")
            .withArgName("password")
            .withArgName("store")
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
            .addOption(passwordOption)
            .addOption(writeToFile);

        if (DEBUG)
        {
            DEBUGGER.debug("OptionGroup encryptOptionsGroup: {}", encryptOptionsGroup);
        }

        Option decryptOption = OptionBuilder.withLongOpt("decrypt")
            .hasArg(false)
            .withArgName("entry")
            .withArgName("username")
            .withDescription("Decrypt the provided string")
            .withType(String.class)
            .isRequired(false)
            .create();

        if (DEBUG)
        {
            DEBUGGER.debug("Option decryptOption: {}", decryptOption);
        }

        OptionGroup decryptOptionsGroup = new OptionGroup()
            .addOption(decryptOption)
            .addOption(entryNameOption)
            .addOption(usernameOption);

        if (DEBUG)
        {
            DEBUGGER.debug("OptionGroup decryptOptionsGroup: {}", decryptOptionsGroup);
        }

        options = new Options();
        options.addOptionGroup(encryptOptionsGroup);
        options.addOptionGroup(decryptOptionsGroup);

        if (DEBUG)
        {
            DEBUGGER.debug("Options options: {}", options);
        }
    }

    public static void main(final String[] args)
    {
        final String methodName = PasswordUtility.CNAME + "#main(final String[] args)";

        if (args.length == 0)
        {
            HelpFormatter usage = new HelpFormatter();
            usage.printHelp(PasswordUtility.CNAME, options, true);

            System.exit(1);
        }

        try
        {
            // load service config first !!
            SecurityServiceInitializer.initializeService(PasswordUtility.SEC_CONFIG, PasswordUtility.LOG_CONFIG, false);

            if (DEBUG)
            {
                DEBUGGER.debug(methodName);

                for (String arg : args)
                {
                    DEBUGGER.debug("Value: {}", arg);
                }
            }

            CommandLineParser parser = new PosixParser();
            CommandLine commandLine = parser.parse(options, args);

            if (DEBUG)
            {
                DEBUGGER.debug("CommandLineParser parser: {}", parser);
                DEBUGGER.debug("CommandLine commandLine: {}", commandLine);
                DEBUGGER.debug("CommandLine commandLine.getOptions(): {}", (Object[]) commandLine.getOptions());
                DEBUGGER.debug("CommandLine commandLine.getArgList(): {}", commandLine.getArgList());
            }

            final SecurityConfigurationData secConfigData = PasswordUtility.svcBean.getConfigData();
            final SecurityConfig secConfig = secConfigData.getSecurityConfig();
            final PasswordRepositoryConfig repoConfig = secConfigData.getPasswordRepo();
            final SystemConfig systemConfig = secConfigData.getSystemConfig();

            if (DEBUG)
            {
                DEBUGGER.debug("SecurityConfigurationData secConfig: {}", secConfigData);
                DEBUGGER.debug("SecurityConfig secConfig: {}", secConfig);
                DEBUGGER.debug("RepositoryConfig secConfig: {}", repoConfig);
                DEBUGGER.debug("SystemConfig systemConfig: {}", systemConfig);
            }

            if (commandLine.hasOption("encrypt"))
            {
                if ((StringUtils.isBlank(repoConfig.getPasswordFile())) || (StringUtils.isBlank(repoConfig.getSaltFile())))
                {
                    System.err.println("The password/salt files are not configured. Entries will not be stored!");
                }

                File passwordFile = FileUtils.getFile(repoConfig.getPasswordFile());
                File saltFile = FileUtils.getFile(repoConfig.getSaltFile());

                if (DEBUG)
                {
                    DEBUGGER.debug("String entryName: {}", passwordFile);
                    DEBUGGER.debug("String username: {}", saltFile);
                }

                String entryName = commandLine.getOptionValue("entry");
                String username = commandLine.getOptionValue("username");
                String password = commandLine.getOptionValue("password");
                int length = (commandLine.hasOption("iterations")) ? Integer.parseInt(commandLine.getOptionValue("length")) : secConfig.getSaltLength();

                if (DEBUG)
                {
                    DEBUGGER.debug("String entryName: {}", entryName);
                    DEBUGGER.debug("String username: {}", username);
                    DEBUGGER.debug("String password: {}", password);
                    DEBUGGER.debug("String length: {}", length);
                }

                final String salt = RandomStringUtils.randomAlphanumeric(length);
                final String encodedUserName = PasswordUtils.base64Encode(username, systemConfig.getEncoding());
                final String encodedPassword = PasswordUtils.encryptText(password, salt, secConfig.getEncryptionAlgorithm(), secConfig.getEncryptionInstance(), systemConfig.getEncoding());

                if (DEBUG)
                {
                    DEBUGGER.debug("String salt: {}", salt);
                    DEBUGGER.debug("String encodedUserName: {}", encodedUserName);
                    DEBUGGER.debug("String encodedPassword: {}", encodedPassword);
                }

                if (commandLine.hasOption("store"))
                {
                    try
                    {
                        new File(passwordFile.getParent()).mkdirs();
                        new File(saltFile.getParent()).mkdirs();

                        boolean saltFileExists = (saltFile.exists()) ? true : saltFile.createNewFile();

                        // write the salt out first
                        if (!(saltFileExists))
                        {
                            throw new IOException("Unable to create salt file");
                        }

                        boolean passwordFileExists = (passwordFile.exists()) ? true : passwordFile.createNewFile();

                        if (!(passwordFileExists))
                        {
                            throw new IOException("Unable to create password file");
                        }

                        FileUtils.writeStringToFile(saltFile, entryName + "," + encodedUserName + "," + salt + System.getProperty("line.separator"), true);
                        FileUtils.writeStringToFile(passwordFile, entryName + "," + encodedUserName + "," + encodedPassword + System.getProperty("line.separator"), true);
                    }
                    catch (IOException iox)
                    {
                        ERROR_RECORDER.error(iox.getMessage(), iox);
                    }
                }

                System.out.println("Entry Name: " + entryName + "; Username: " + username + "; Plain Text: " + password + "; Salt: " + salt + "; Encrypted: " + encodedPassword);
            }

            if (commandLine.hasOption("decrypt"))
            {
                String saltEntryName = null;
                String passwordEntryName = null;
                String saltEntryUsername = null;
                String saltEntryPassword = null;
                String passwordEntryUsername = null;
                String passwordEntryPassword = null;

                if (StringUtils.isEmpty(commandLine.getOptionValue("entry")))
                {
                    throw new ParseException("no entry provided to decrypt");
                }

                if (StringUtils.isEmpty(commandLine.getOptionValue("username")))
                {
                    throw new ParseException("no entry provided to decrypt");
                }

                String entryName = commandLine.getOptionValue("entry");
                String username = commandLine.getOptionValue("username");

                if (DEBUG)
                {
                    DEBUGGER.debug("String entryName: {}", entryName);
                    DEBUGGER.debug("String username: {}", username);
                }

                File passwordFile = FileUtils.getFile(repoConfig.getPasswordFile());
                File saltFile = FileUtils.getFile(repoConfig.getSaltFile());

                if (DEBUG)
                {
                    DEBUGGER.debug("File passwordFile: {}", passwordFile);
                    DEBUGGER.debug("File saltFile: {}", saltFile);
                }

                if (!(saltFile.canRead()))
                {
                    throw new IOException("Unable to read configured salt file");
                }

                if (!(passwordFile.canRead()))
                {
                    throw new IOException("Unable to read configured password file");
                }

                List<String> saltArray = FileUtils.readLines(saltFile, systemConfig.getEncoding());
                List<String> passwordArray = FileUtils.readLines(passwordFile, systemConfig.getEncoding());

                if (DEBUG)
                {
                    DEBUGGER.debug("List<String> saltArray: {}", saltArray);
                    DEBUGGER.debug("List<String> passwordArray: {}", passwordArray);
                }

                if (saltArray.isEmpty())
                {
                    throw new SecurityException("No entries were loaded from the configured password file");
                }

                if (passwordArray.isEmpty())
                {
                    throw new SecurityException("No entries were loaded from the configured salt file");
                }

                for (String saltEntry : saltArray)
                {
                    if (DEBUG)
                    {
                        DEBUGGER.debug("String saltEntry: {}", saltEntry);
                    }

                    List<String> saltEntryData = Arrays.asList(saltEntry.split(","));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("List<String> saltEntryData: {}", saltEntryData);
                    }

                    if (saltEntryData.contains(entryName))
                    {
                        // pull out the entry
                        saltEntryName = saltEntryData.get(0).trim();
                        String encodedSaltEntryUsername = saltEntryData.get(1).trim();
                        saltEntryPassword = saltEntryData.get(2).trim();

                        if (DEBUG)
                        {
                            DEBUGGER.debug("String saltEntryName: {}", saltEntryName);
                            DEBUGGER.debug("String encodedSaltEntryUsername: {}", encodedSaltEntryUsername);
                            DEBUGGER.debug("String saltEntryPassword: {}", saltEntryPassword);
                        }

                        saltEntryUsername = PasswordUtils.base64Decode(encodedSaltEntryUsername, systemConfig.getEncoding());

                        if (DEBUG)
                        {
                            DEBUGGER.debug("String saltEntryName: {}", saltEntryName);
                            DEBUGGER.debug("String saltEntryUsername: {}", saltEntryUsername);
                            DEBUGGER.debug("String saltEntryPassword: {}", saltEntryPassword);
                        }

                        break;
                    }
                }

                for (String passwordEntry : passwordArray)
                {
                    if (DEBUG)
                    {
                        DEBUGGER.debug("String passwordEntry: {}", passwordEntry);
                    }

                    List<String> passwordEntryData = Arrays.asList(passwordEntry.split(","));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("List<String> passwordEntryData: {}", passwordEntryData);
                    }

                    if (passwordEntryData.contains(entryName))
                    {
                        passwordEntryName = passwordEntryData.get(0).trim();
                        String encodedPasswordEntryUsername = passwordEntryData.get(1).trim();
                        passwordEntryPassword = passwordEntryData.get(2).trim();

                        if (DEBUG)
                        {
                            DEBUGGER.debug("String saltEntryName: {}", saltEntryName);
                            DEBUGGER.debug("String encodedPasswordEntryUsername: {}", encodedPasswordEntryUsername);
                            DEBUGGER.debug("String passwordEntryPassword: {}", passwordEntryPassword);
                        }

                        passwordEntryUsername = PasswordUtils.base64Decode(encodedPasswordEntryUsername, systemConfig.getEncoding());

                        if (DEBUG)
                        {
                            DEBUGGER.debug("String passwordEntryName: {}", passwordEntryName);
                            DEBUGGER.debug("String passwordEntryUsername: {}", passwordEntryUsername);
                            DEBUGGER.debug("String passwordEntryPassword: {}", passwordEntryPassword);
                        }

                        break;
                    }
                }

                if ((StringUtils.isEmpty(saltEntryName)) || (StringUtils.isEmpty(passwordEntryName)))
                {
                    throw new SecurityException("No entries were found that matched the provided information");
                }

                if ((!(StringUtils.equals(saltEntryName, passwordEntryName))) ||
                        (!(StringUtils.equals(saltEntryUsername, passwordEntryUsername))))
                {
                    throw new SecurityException("Salt entry does not match password entry");
                }

                String decrypted = PasswordUtils.decryptText(passwordEntryPassword, saltEntryPassword.length(),
                        secConfig.getEncryptionAlgorithm(), secConfig.getEncryptionInstance(), systemConfig.getEncoding());

                if (DEBUG)
                {
                    DEBUGGER.debug("String decrypted: {}", decrypted);
                }

                System.out.println("Entry Name: " + entryName + "; Username: " + username + "; Plain Text: " + decrypted);
            }
            else if (commandLine.hasOption("encode"))
            {
                System.out.println(PasswordUtils.base64Encode((String) commandLine.getArgList().get(0), systemConfig.getEncoding()));
            }
            else if (commandLine.hasOption("decode"))
            {
                System.out.println(PasswordUtils.base64Decode((String) commandLine.getArgList().get(0), systemConfig.getEncoding()));
            }
        }
        catch (IOException iox)
        {
            ERROR_RECORDER.error(iox.getMessage(), iox);

            System.err.println("An error occurred during processing: " + iox.getMessage());

            System.exit(1);
        }
        catch (ParseException px)
        {
            ERROR_RECORDER.error(px.getMessage(), px);

            System.err.println("An error occurred during processing: " + px.getMessage());

            System.exit(1);
        }
        catch (SecurityException sx)
        {
            ERROR_RECORDER.error(sx.getMessage(), sx);

            System.err.println("An error occurred during processing: " + sx.getMessage());

            System.exit(1);
        }
        catch (SecurityServiceException ssx)
        {
            ERROR_RECORDER.error(ssx.getMessage(), ssx);

            System.err.println("An error occurred during processing: " + ssx.getMessage());

            System.exit(1);
        }

        System.exit(0);
    }
}
