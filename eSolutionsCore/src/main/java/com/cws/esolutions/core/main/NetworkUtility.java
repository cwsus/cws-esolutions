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
package com.cws.esolutions.core.main;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.utils
 * File: NetworkUtils.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.net.URL;
import java.util.List;
import java.util.Arrays;
import org.slf4j.Logger;
import java.util.ArrayList;
import org.slf4j.LoggerFactory;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import java.net.MalformedURLException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.CommandLineParser;

import com.cws.esolutions.core.CoreServicesBean;
import com.cws.esolutions.core.utils.NetworkUtils;
import com.cws.esolutions.core.CoreServicesConstants;
import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.core.exception.CoreServicesException;
import com.cws.esolutions.core.config.xml.CoreConfigurationData;
import com.cws.esolutions.core.listeners.CoreServicesInitializer;
import com.cws.esolutions.security.exception.SecurityServiceException;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.security.config.xml.SecurityConfigurationData;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author cws-khuntly
 * @version 1.0
 */
@SuppressWarnings("static-access")
public final class NetworkUtility
{
    private static Options options = null;

    private static final String CNAME = NetworkUtility.class.getName();
    private static final CoreServicesBean appBean = CoreServicesBean.getInstance();
    private static final SecurityServiceBean secBean = SecurityServiceBean.getInstance();
    private static final String CORE_LOG_CONFIG = System.getProperty("user.home") + "/etc/eSolutionsCore/logging/logging.xml";
    private static final String CORE_SVC_CONFIG = System.getProperty("user.home") + "/etc/eSolutionsCore/config/ServiceConfig.xml";
    private static final String SEC_LOG_CONFIG = System.getProperty("user.home") + "/etc/SecurityService/logging/logging.xml";
    private static final String SEC_SVC_CONFIG = System.getProperty("user.home") + "/etc/SecurityService/config/ServiceConfig.xml";

    private static final Logger DEBUGGER = LoggerFactory.getLogger(CoreServicesConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(CoreServicesConstants.ERROR_LOGGER + CNAME);

    static
    {
        Option userNameOption = OptionBuilder.withLongOpt("username")
            .withArgName("username")
            .withDescription("Username to make the connection against")
            .withType(String.class)
            .isRequired(false)
            .hasArg(true)
            .create();

        Option hostNameOption = OptionBuilder.withLongOpt("hostname")
            .withArgName("hostname")
            .withDescription("Hostname to make the connection against")
            .withType(String.class)
            .isRequired(false)
            .hasArg(true)
            .create();

        Option portNumberOption = OptionBuilder.withLongOpt("port")
            .withArgName("port")
            .withDescription("Port number to make the connection against")
            .withType(Integer.class)
            .isRequired(false)
            .hasArg(true)
            .create();

        Option sourceFileOption = OptionBuilder.withLongOpt("sourceFile")
            .withArgName("sourceFile")
            .withDescription("File path/name on the source system")
            .withType(List.class)
            .isRequired(false)
            .hasArgs()
            .create();

        Option targetFileOption = OptionBuilder.withLongOpt("targetFile")
            .withArgName("targetFile")
            .withDescription("File path/name on the target system")
            .withType(List.class)
            .isRequired(false)
            .hasArgs()
            .withValueSeparator(",".charAt(0))
            .create();

        if (DEBUG)
        {
            DEBUGGER.debug("Option userNameOption: {}", userNameOption);
            DEBUGGER.debug("Option hostNameOption: {}", hostNameOption);
            DEBUGGER.debug("Option portNumberOption: {}", portNumberOption);
            DEBUGGER.debug("Option portNumberOption: {}", sourceFileOption);
            DEBUGGER.debug("Option portNumberOption: {}", targetFileOption);
        }

        Option sshOption = OptionBuilder.withLongOpt("ssh")
            .hasArg(false)
            .withDescription("Perform an ssh connection to a target host")
            .isRequired(false)
            .create();

        if (DEBUG)
        {
            DEBUGGER.debug("Option sshOption: {}", sshOption);
        }

        OptionGroup sshOptions = new OptionGroup()
            .addOption(sshOption)
            .addOption(userNameOption)
            .addOption(hostNameOption)
            .addOption(portNumberOption);

        if (DEBUG)
        {
            DEBUGGER.debug("OptionGroup sshOptions: {}", sshOptions);
        }

        Option copyFilesOption = OptionBuilder.withLongOpt("copyFiles")
            .hasArg(false)
            .withDescription("Performs a file copy to/from provided systems")
            .isRequired(false)
            .create();

        Option copyFilesWithSSH = OptionBuilder.withLongOpt("withSSH")
            .hasArg(true)
            .hasArgs(1)
            .withDescription("Performs the file copy using SSH with SFTP or SCP")
            .withArgName("sshOption")
            .withType(String.class)
            .create();

        Option copyFilesWithFTP = OptionBuilder.withLongOpt("withFTP")
            .hasArg(true)
            .hasArgs(1)
            .withDescription("Performs the file copy using FTP, optionally with SSL")
            .withArgName("withSSL")
            .withType(Boolean.class)
            .create();

        if (DEBUG)
        {
            DEBUGGER.debug("Option copyFilesOption: {}", copyFilesOption);
            DEBUGGER.debug("Option copyFilesWithSCP: {}", copyFilesWithSSH);
            DEBUGGER.debug("Option copyFilesWithSFTP: {}", copyFilesWithFTP);
        }

        OptionGroup copyOptionsGroup = new OptionGroup()
            .addOption(copyFilesOption)
            .addOption(copyFilesWithSSH)
            .addOption(copyFilesWithFTP)
            .addOption(userNameOption)
            .addOption(hostNameOption)
            .addOption(portNumberOption)
            .addOption(sourceFileOption)
            .addOption(targetFileOption);

        if (DEBUG)
        {
            DEBUGGER.debug("OptionGroup copyOptionsGroup: {}", copyOptionsGroup);
        }

        Option httpOption = OptionBuilder.withLongOpt("http")
            .hasArg(false)
            .withDescription("Performs an HTTP request to a provided host")
            .isRequired(false)
            .create();

        Option httpMethod = OptionBuilder.withLongOpt("method")
            .hasArg(true)
            .withDescription("HTTP Method to perform, e.g. get, head")
            .withType(String.class)
            .isRequired(false)
            .create();

        if (DEBUG)
        {
            DEBUGGER.debug("Option httpOption: {}", httpOption);
            DEBUGGER.debug("Option httpMethod: {}", httpMethod);
        }

        OptionGroup httpOptionsGroup = new OptionGroup()
            .addOption(httpOption)
            .addOption(httpMethod)
            .addOption(hostNameOption);

        if (DEBUG)
        {
            DEBUGGER.debug("OptionGroup httpOptionsGroup: {}", httpOptionsGroup);
        }

        options = new Options();
        options.addOptionGroup(sshOptions);
        options.addOptionGroup(copyOptionsGroup);
        options.addOptionGroup(httpOptionsGroup);

        if (DEBUG)
        {
            DEBUGGER.debug("Options options: {}", options);
        }
    }

    public static final void main(final String[] args)
    {
        final String methodName = NetworkUtility.CNAME + "#main(final String[] args)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", (Object) args);
        }

        if (args.length == 0)
        {
            new HelpFormatter().printHelp(NetworkUtility.CNAME, options, true);

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
            }

            String coreConfiguration = (StringUtils.isBlank(System.getProperty("coreConfigFile"))) ? NetworkUtility.CORE_SVC_CONFIG : System.getProperty("coreConfigFile");
            String securityConfiguration = (StringUtils.isBlank(System.getProperty("secConfigFile"))) ? NetworkUtility.CORE_LOG_CONFIG : System.getProperty("secConfigFile");
            String coreLogging = (StringUtils.isBlank(System.getProperty("coreLogConfig"))) ? NetworkUtility.SEC_SVC_CONFIG : System.getProperty("coreLogConfig");
            String securityLogging = (StringUtils.isBlank(System.getProperty("secLogConfig"))) ? NetworkUtility.SEC_LOG_CONFIG : System.getProperty("secLogConfig");

            if (DEBUG)
            {
                DEBUGGER.debug("String coreConfiguration: {}", coreConfiguration);
                DEBUGGER.debug("String securityConfiguration: {}", securityConfiguration);
                DEBUGGER.debug("String coreLogging: {}", coreLogging);
                DEBUGGER.debug("String securityLogging: {}", securityLogging);
            }

            SecurityServiceInitializer.initializeService(securityConfiguration, securityLogging, false);
            CoreServicesInitializer.initializeService(coreConfiguration, coreLogging, false, true);

            final CoreConfigurationData coreConfigData = NetworkUtility.appBean.getConfigData();
            final SecurityConfigurationData secConfigData = NetworkUtility.secBean.getConfigData();

            if (DEBUG)
            {
                DEBUGGER.debug("CoreConfigurationData coreConfigData: {}", coreConfigData);
                DEBUGGER.debug("SecurityConfigurationData secConfig: {}", secConfigData);
            }

            if (commandLine.hasOption("ssh"))
            {
                if (!(commandLine.hasOption("hostname")) || (!(StringUtils.isBlank(commandLine.getOptionValue("hostname")))))
                {
                    throw new CoreServicesException("No target host was provided. Unable to process request.");
                }
                else if (!(commandLine.hasOption("username")) && (StringUtils.isBlank(commandLine.getOptionValue("username"))))
                {
                    throw new CoreServicesException("No username was provided. Using default.");
                }

                // NetworkUtils.executeSshConnection(commandLine.getOptionValue("hostname"), commandList);
            }
            else if (commandLine.hasOption("http"))
            {
                if (!(commandLine.hasOption("hostname")) || (!(StringUtils.isBlank(commandLine.getOptionValue("hostname")))))
                {
                    throw new CoreServicesException("No target host was provided. Unable to process request.");
                }
                else if (!(commandLine.hasOption("method")) && (StringUtils.isBlank(commandLine.getOptionValue("method")))) 
                {
                    throw new CoreServicesException("No HTTP method was provided. Unable to process request.");
                }

                NetworkUtils.executeHttpConnection(new URL(commandLine.getOptionValue("hostname")), commandLine.getOptionValue("method"));
            }
            else if (commandLine.hasOption("copyFiles"))
            {
                if (!(commandLine.hasOption("hostname")) || (StringUtils.isBlank(commandLine.getOptionValue("hostname"))))
                {
                    throw new CoreServicesException("No target host was provided. Unable to process request.");
                }
                else if (!(commandLine.hasOption("sourceFile")) && (StringUtils.isBlank(commandLine.getOptionValue("sourceFile")))) 
                {
                    throw new CoreServicesException("No source file(s) were provided. Unable to process request.");
                }
                else if (!(commandLine.hasOption("targetFile")) && (StringUtils.isBlank(commandLine.getOptionValue("targetFile")))) 
                {
                    throw new CoreServicesException("No target file(s) were provided. Unable to process request.");
                }

                List<String> filesToTransfer = new ArrayList<String>(
                    Arrays.asList(commandLine.getOptionValues("sourceFile")));
                List<String> filesToCreate = new ArrayList<String>(
                    Arrays.asList(commandLine.getOptionValues("targetFile")));

                if (DEBUG)
                {
                    DEBUGGER.debug("List<String> filesToTransfer: {}", filesToTransfer);
                    DEBUGGER.debug("List<String> filesToCreate: {}", filesToCreate);
                }

                if (commandLine.hasOption("withSSH"))
                {
                    for (String fileName : filesToTransfer)
                    {
                        if (DEBUG)
                        {
                            DEBUGGER.debug("String fileName: {}", fileName);
                        }

                        NetworkUtils.executeSftpTransfer(fileName, filesToCreate.get(filesToTransfer.indexOf(fileName)),
                            commandLine.getOptionValue("hostname"), FileUtils.getFile(fileName).exists());
                    }
                }

                else if (commandLine.hasOption("withFTP"))
                {
                    if ((commandLine.hasOption("withSSL")) && (Boolean.valueOf(commandLine.getOptionValue("withSSL"))))
                    {
                        // ftp/s
                    }

                    // NetworkUtils.executeFtpConnection(sourceFile, targetFile, targetHost, isUpload);
                }
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
        catch (CoreServicesException csx)
        {
            ERROR_RECORDER.error(csx.getMessage(), csx);

            System.err.println("An error occurred during processing: " + csx.getMessage());
        }
        catch (MalformedURLException mux)
        {
            ERROR_RECORDER.error(mux.getMessage(), mux);

            System.err.println("An error occurred during processing: " + mux.getMessage());
        }
    }
}
