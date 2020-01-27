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
package com.cws.esolutions.security.main;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.listeners
 * File: SecurityServiceInitializer.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly           11/23/2008 22:39:20             Created.
 */
import java.io.File;
import org.slf4j.Logger;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
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
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
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

    public PasswordUtility()
    {
    	System.out.println("instantiate");
    }
    static
    {
        Option entryNameOption = OptionBuilder.withLongOpt("entry")
            .hasArg(true)
            .withArgName("entry")
            .withDescription("Name for the entry")
            .withType(String.class)
            .isRequired(true)
            .create();

        Option usernameOption = OptionBuilder.withLongOpt("username")
            .hasArg(true)
            .withArgName("username")
            .withDescription("Username for the entry")
            .withType(String.class)
            .isRequired(true)
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
            .withType(Boolean.class)
            .create();

        Option replaceEntry = OptionBuilder.withLongOpt("replace")
                .hasArg(false)
                .withDescription("Replace an existing entry")
                .isRequired(false)
                .withType(Boolean.class)
                .create();

        Option encryptOption = OptionBuilder.withLongOpt("encrypt")
            .hasArg(false)
            .withArgName("entry")
            .withArgName("username")
            .withArgName("password")
            .withArgName("store")
            .withDescription("Encrypt the provided string")
            .withType(Boolean.class)
            .isRequired(false)
            .create();

        OptionGroup encryptOptionsGroup = new OptionGroup()
            .addOption(encryptOption)
            .addOption(entryNameOption)
            .addOption(usernameOption)
            .addOption(passwordOption)
            .addOption(writeToFile)
            .addOption(replaceEntry);

        Option decryptOption = OptionBuilder.withLongOpt("decrypt")
            .hasArg(false)
            .withArgName("entry")
            .withArgName("username")
            .withDescription("Decrypt the provided string")
            .withType(Boolean.class)
            .isRequired(false)
            .create();

        OptionGroup decryptOptionsGroup = new OptionGroup()
            .addOption(decryptOption)
            .addOption(entryNameOption)
            .addOption(usernameOption);

        options = new Options();
        options.addOptionGroup(encryptOptionsGroup);
        options.addOptionGroup(decryptOptionsGroup);
    }

    public static void main(final String[] args)
    {
        final String methodName = PasswordUtility.CNAME + "#main(final String[] args)";

        if (DEBUG)
        {
            DEBUGGER.debug("Value: {}", methodName);
        }

        if (args.length == 0)
        {
            HelpFormatter usage = new HelpFormatter();
            usage.printHelp(PasswordUtility.CNAME, options, true);

            System.exit(1);
        }

        BufferedReader bReader = null;
        BufferedWriter bWriter = null;

        try
        {
            // load service config first !!
            SecurityServiceInitializer.initializeService(PasswordUtility.SEC_CONFIG, PasswordUtility.LOG_CONFIG, false);

            if (DEBUG)
            {
                DEBUGGER.debug("Options options: {}", options);

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
                    DEBUGGER.debug("File passwordFile: {}", passwordFile);
                    DEBUGGER.debug("File saltFile: {}", saltFile);
                }

                final String entryName = commandLine.getOptionValue("entry");
                final String username = commandLine.getOptionValue("username");
                final String password = commandLine.getOptionValue("password");
                final String salt = RandomStringUtils.randomAlphanumeric(secConfig.getSaltLength());

                if (DEBUG)
                {
                    DEBUGGER.debug("String entryName: {}", entryName);
                    DEBUGGER.debug("String username: {}", username);
                    DEBUGGER.debug("String password: {}", password);
                    DEBUGGER.debug("String salt: {}", salt);
                }

                final String encodedSalt = PasswordUtils.base64Encode(salt);
                final String encodedUserName = PasswordUtils.base64Encode(username);
                final String encryptedPassword = PasswordUtils.encryptText(password, salt,
                        secConfig.getSecretAlgorithm(), secConfig.getIterations(), secConfig.getKeyBits(),
                        secConfig.getEncryptionAlgorithm(), secConfig.getEncryptionInstance(), systemConfig.getEncoding());
                final String encodedPassword = PasswordUtils.base64Encode(encryptedPassword);

                if (DEBUG)
                {
                    DEBUGGER.debug("String encodedSalt: {}", encodedSalt);
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

                        if (DEBUG)
                        {
                            DEBUGGER.debug("saltFileExists: {}", saltFileExists);
                        }

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

                        if (commandLine.hasOption("replace"))
                        {
                            File[] files = new File[] { saltFile, passwordFile };

                            if (DEBUG)
                            {
                                DEBUGGER.debug("File[] files: {}", (Object) files);
                            }

                            for (File file : files)
                            {
                                if (DEBUG)
                                {
                                    DEBUGGER.debug("File: {}", file);
                                }

                                String currentLine = null;
                                File tmpFile = new File(FileUtils.getTempDirectory() + "/" + "tmpFile");

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("File tmpFile: {}", tmpFile);
                                }

                                bReader = new BufferedReader(new FileReader(file));
                                bWriter = new BufferedWriter(new FileWriter(tmpFile));

                                while ((currentLine = bReader.readLine()) !=  null)
                                {
                                    if (!(StringUtils.equals(currentLine.trim().split(",")[0], entryName)))
                                    {
                                        bWriter.write(currentLine + System.getProperty("line.separator"));
                                        bWriter.flush();
                                    }
                                }

                                bWriter.close();

                                FileUtils.deleteQuietly(file);
                                FileUtils.copyFile(tmpFile, file);
                                FileUtils.deleteQuietly(tmpFile);
                            }
                        }

                        FileUtils.writeStringToFile(saltFile, entryName + "," + encodedUserName + "," + encodedSalt + System.getProperty("line.separator"), true);
                        FileUtils.writeStringToFile(passwordFile, entryName + "," + encodedUserName + "," + encodedPassword + System.getProperty("line.separator"), true);
                    }
                    catch (IOException iox)
                    {
                        ERROR_RECORDER.error(iox.getMessage(), iox);
                    }
                    finally
                    {
                    	System.out.println("Entry Name " + entryName + " stored.");
                    }
                }
                else
                {
                	System.out.println("Entry: " + encodedUserName + ", Password: " + encodedPassword);
                }
            }

            if (commandLine.hasOption("decrypt"))
            {
                String saltEntryName = null;
                String saltEntryValue = null;
                String decryptedPassword = null;
                String passwordEntryName = null;

                if ((StringUtils.isEmpty(commandLine.getOptionValue("entry")) && (StringUtils.isEmpty(commandLine.getOptionValue("username")))))
                {
                    throw new ParseException("No entry or username was provided to decrypt.");
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

                if ((!(saltFile.canRead())) || (!(passwordFile.canRead())))
                {
                    throw new IOException("Unable to read configured password/salt file. Please check configuration and/or permissions.");
                }

                for (String lineEntry : FileUtils.readLines(saltFile, systemConfig.getEncoding()))
                {
                    saltEntryName = lineEntry.split(",")[0];

                    if (DEBUG)
                    {
                        DEBUGGER.debug("String saltEntryName: {}", saltEntryName);
                    }

                    if (StringUtils.equals(saltEntryName, entryName))
                    {
                        saltEntryValue = PasswordUtils.base64Decode(lineEntry.split(",")[2]);

                        break;
                    }
                }

                if (StringUtils.isEmpty(saltEntryValue))
                {
                    throw new SecurityException("No entries were found that matched the provided information");
                }

                for (String lineEntry : FileUtils.readLines(passwordFile, systemConfig.getEncoding()))
                {
                    passwordEntryName = lineEntry.split(",")[0];

                    if (DEBUG)
                    {
                        DEBUGGER.debug("String passwordEntryName: {}", passwordEntryName);
                    }

                    if (StringUtils.equals(passwordEntryName, saltEntryName))
                    {
                        String decodedPassword = PasswordUtils.base64Decode(lineEntry.split(",")[2]);

                        decryptedPassword = PasswordUtils.decryptText(decodedPassword, saltEntryValue,
                                secConfig.getSecretAlgorithm(), secConfig.getIterations(), secConfig.getKeyBits(),
                                secConfig.getEncryptionAlgorithm(), secConfig.getEncryptionInstance(), systemConfig.getEncoding());

                        break;
                    }
                }

                if (StringUtils.isEmpty(decryptedPassword))
                {
                    throw new SecurityException("No entries were found that matched the provided information");
                }

                System.out.println(decryptedPassword);
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
            System.exit(1);
        }
        finally
        {
            try
            {
                if (bReader != null)
                {
                    bReader.close();
                }

                if (bWriter != null)
                {
                    bReader.close();
                }
            }
            catch (IOException iox) {}
        }

        System.exit(0);
    }
}
