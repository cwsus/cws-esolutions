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
import org.slf4j.Logger;
import java.net.InetAddress;
import org.slf4j.LoggerFactory;
import java.net.UnknownHostException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.CommandLineParser;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.SecurityServiceConstants;
import com.cws.esolutions.security.config.xml.SecurityConfig;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.security.exception.SecurityServiceException;
import com.cws.esolutions.security.config.xml.SecurityConfigurationData;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.security.processors.dto.AccountControlRequest;
import com.cws.esolutions.security.processors.dto.AccountControlResponse;
import com.cws.esolutions.security.processors.impl.AccountControlProcessorImpl;
import com.cws.esolutions.security.processors.interfaces.IAccountControlProcessor;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security
 * File: UserManagementUtility.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
@SuppressWarnings("static-access")
public class UserManagementUtility
{
    private static Options options = null;

    private static final SecurityServiceBean svcBean = SecurityServiceBean.getInstance();
    private static final String LOG_CONFIG = System.getProperty("user.home") + "/etc/logging.xml";
    private static final String SEC_CONFIG = System.getProperty("user.home") + "/etc/ServiceConfig.xml";

    private static final String CNAME = UserManagementUtility.class.getName();
    private static final IAccountControlProcessor processor = new AccountControlProcessorImpl();
    private static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityServiceConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityServiceConstants.ERROR_LOGGER);

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

        Option searchOption = OptionBuilder.withLongOpt("search")
            .hasArg(true)
            .withArgName("entry")
            .withDescription("Search for the provided entry")
            .isRequired(false)
            .create();

        Option loadOption = OptionBuilder.withLongOpt("load")
            .hasArg(true)
            .withArgName("entry")
            .withDescription("Search for the provided entry")
            .isRequired(false)
            .create();

        if (DEBUG)
        {
            DEBUGGER.debug("Option searchOption: {}", searchOption);
            DEBUGGER.debug("Option loadOption: {}", loadOption);
        }

        OptionGroup searchOptions = new OptionGroup()
            .addOption(searchOption);

        if (DEBUG)
        {
            DEBUGGER.debug("OptionGroup searchOptions: {}", searchOptions);
        }

        OptionGroup loadOptions = new OptionGroup()
            .addOption(loadOption);

        if (DEBUG)
        {
            DEBUGGER.debug("OptionGroup loadOptions: {}", loadOptions);
        }

        options = new Options();
        options.addOptionGroup(configOptions);
        options.addOptionGroup(searchOptions);
        options.addOptionGroup(loadOptions);

        if (DEBUG)
        {
            DEBUGGER.debug("Options options: {}", options);
        }

    }

    public static final void main(final String[] args)
    {
        final String methodName = UserManagementUtility.CNAME + "#main(final String[] args)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", (Object) args);
        }

        if (args.length == 0)
        {
            HelpFormatter usage = new HelpFormatter();
            usage.printHelp(UserManagementUtility.CNAME, options, true);

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
                DEBUGGER.debug("CommandLine commandLine.getOptions(): {}", (Object[]) commandLine.getOptions());
                DEBUGGER.debug("CommandLine commandLine.getArgList(): {}", commandLine.getArgList());
            }

            if ((commandLine.hasOption("configFile")) && (!(StringUtils.isBlank(commandLine.getOptionValue("configFile")))))
            {
                SecurityServiceInitializer.initializeService(commandLine.getOptionValue("configFile"), UserManagementUtility.LOG_CONFIG, true);
            }
            else
            {
                SecurityServiceInitializer.initializeService(UserManagementUtility.SEC_CONFIG, UserManagementUtility.LOG_CONFIG, true);
            }

            AccountControlResponse response = null;

            final UserAccount userAccount = new UserAccount();
            final RequestHostInfo reqInfo = new RequestHostInfo();
            final SecurityConfigurationData secConfigData = UserManagementUtility.svcBean.getConfigData();
            final SecurityConfig secConfig = secConfigData.getSecurityConfig();

            try
            {
                reqInfo.setHostAddress(InetAddress.getLocalHost().getHostAddress());
                reqInfo.setHostName(InetAddress.getLocalHost().getHostName());
            }
            catch (final UnknownHostException uhx)
            {
                reqInfo.setHostAddress("127.0.0.1");
                reqInfo.setHostName("localhost");
            }

            if (DEBUG)
            {
                DEBUGGER.debug("SecurityConfigurationData secConfig: {}", secConfigData);
                DEBUGGER.debug("SecurityConfig secConfig: {}", secConfig);
                DEBUGGER.debug("RequestHostInfo reqInfo: {}", reqInfo);
            }

            AccountControlRequest request = new AccountControlRequest();
            request.setApplicationId(secConfig.getApplicationId());
            request.setApplicationName(secConfig.getApplicationName());
            request.setHostInfo(reqInfo);
            request.setRequestor(secConfig.getSvcAccount());

            if (DEBUG)
            {
                DEBUGGER.debug("AccountControlRequest request: {}", request);
            }

            if (commandLine.hasOption("search"))
            {
                if (StringUtils.isEmpty(commandLine.getOptionValue("search")))
                {
                    throw new ParseException("No entry option was provided. Cannot continue.");
                }

                userAccount.setEmailAddr(commandLine.getOptionValue("search"));

                if (DEBUG)
                {
                    DEBUGGER.debug("UserAccount userAccount: {}", userAccount);
                }

                request.setUserAccount(userAccount);

                if (DEBUG)
                {
                    DEBUGGER.debug("AccountControlRequest: {}", request);
                }

                response = processor.searchAccounts(request);
            }
            else if (commandLine.hasOption("load"))
            {
                if (StringUtils.isEmpty(commandLine.getOptionValue("load")))
                {
                    throw new ParseException("No entry option was provided. Cannot continue.");
                }

                userAccount.setGuid(commandLine.getOptionValue("load"));

                request.setUserAccount(userAccount);

                if (DEBUG)
                {
                    DEBUGGER.debug("AccountControlRequest: {}", request);
                }

                response = processor.loadUserAccount(request);
            }

            if (DEBUG)
            {
                DEBUGGER.debug("AccountControlResponse response: {}", response);
            }

            if ((response != null) && (response.getRequestStatus() == SecurityRequestStatus.SUCCESS))
            {
                UserAccount account = response.getUserAccount();

                if (DEBUG)
                {
                    DEBUGGER.debug("UserAccount: {}", account);
                }

                System.out.println(account);
            }
        }
        catch (final ParseException px)
        {
            ERROR_RECORDER.error(px.getMessage(), px);

            System.err.println("An error occurred during processing: " + px.getMessage());
        }
        catch (final SecurityException sx)
        {
            ERROR_RECORDER.error(sx.getMessage(), sx);

            System.err.println("An error occurred during processing: " + sx.getMessage());
        }
        catch (final SecurityServiceException ssx)
        {
            ERROR_RECORDER.error(ssx.getMessage(), ssx);

            System.err.println("An error occurred during processing: " + ssx.getMessage());
        }
    }
}
