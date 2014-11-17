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

import java.util.List;
import org.slf4j.Logger;
import java.net.InetAddress;
import org.slf4j.LoggerFactory;
import java.net.UnknownHostException;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.CommandLineParser;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.config.xml.SecurityConfig;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.security.exception.SecurityServiceException;
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
 * ----------------------------------------------------------------------------
 * 35033355 @ Jul 28, 2014 1:27:05 PM
 *     Created.
 */
@SuppressWarnings("static-access")
public class UserManagementUtility
{
    private static Options options = null;

    private static OptionGroup ldapOptions = null;
    private static SecurityConfig secConfig = null;
    private static SecurityServiceBean bean = null;

    private static final String CNAME = UserManagementUtility.class.getName();
    private static final IAccountControlProcessor processor = new AccountControlProcessorImpl();
    private static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityServiceConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityServiceConstants.ERROR_LOGGER);

    static
    {
        OptionGroup commandOptions = new OptionGroup();
        commandOptions.addOption(OptionBuilder.withLongOpt("search")
            .withDescription("Search for an account in the user repository with the provided information")
            .isRequired(false)
            .create());
        commandOptions.addOption(OptionBuilder.withLongOpt("load")
            .withDescription("Load an account in the user repository with the provided information")
            .isRequired(false)
            .create());

        ldapOptions = new OptionGroup();
        ldapOptions.addOption(OptionBuilder.withLongOpt("searchOption")
            .withDescription("The string to search for in the user repository.")
            .hasArg(true)
            .withArgName("STRING")
            .withType(String.class)
            .isRequired(false)
            .create());
        ldapOptions.addOption(OptionBuilder.withLongOpt("loadOption")
                .withDescription("The username of the account to perform the load against.")
                .hasArg(true)
                .withArgName("STRING")
                .withType(String.class)
                .isRequired(false)
                .create());

        options = new Options();
        options.addOptionGroup(commandOptions);
        options.addOptionGroup(ldapOptions);
    }

    public static final void main(final String[] args)
    {
        final String methodName = UserManagementUtility.CNAME + "#main(final String[] args)";

        try
        {
            SecurityServiceInitializer.initializeService(System.getProperty("serviceConfig"), System.getProperty("logConfig"), false);
        }
        catch (SecurityException sx)
        {
            ERROR_RECORDER.error(sx.getMessage(), sx);

            System.err.println("An error occurred loading configuration: " + sx.getMessage());

            sx.printStackTrace();

            System.exit(1);
        }
        catch (SecurityServiceException ssx)
        {
            ERROR_RECORDER.error(ssx.getMessage(), ssx);

            System.err.println("An error occurred loading configuration: " + ssx.getMessage());

            ssx.printStackTrace();

            System.exit(1);
        }

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", (Object) args);
        }

        if (args.length == 0)
        {
            HelpFormatter usage = new HelpFormatter();
            usage.printHelp(UserManagementUtility.CNAME, UserManagementUtility.options, true);

            System.exit(3);
        }

        int RETURN_CODE=1;

        try
        {
            UserManagementUtility.bean = SecurityServiceBean.getInstance();
            UserManagementUtility.secConfig = bean.getConfigData().getSecurityConfig();

            CommandLineParser parser = new PosixParser();
            CommandLine commandLine = parser.parse(UserManagementUtility.options, args);

            final RequestHostInfo reqInfo = new RequestHostInfo();

            try
            {
                reqInfo.setHostAddress(InetAddress.getLocalHost().getHostAddress());
                reqInfo.setHostName(InetAddress.getLocalHost().getHostName());
            }
            catch (UnknownHostException uhx)
            {
                reqInfo.setHostAddress("127.0.0.1");
                reqInfo.setHostName("localhost");
            }

            if (DEBUG)
            {
                DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            }

            if (commandLine.hasOption("search"))
            {
                final UserAccount loadAccount = new UserAccount();
                loadAccount.setEmailAddr(commandLine.getOptionValue("searchOption"));

                if (DEBUG)
                {
                    DEBUGGER.debug("UserAccount: {}", loadAccount);
                }

                AccountControlRequest request = new AccountControlRequest();
                request.setApplicationId(secConfig.getApplicationId());
                request.setApplicationName(secConfig.getApplicationName());
                request.setHostInfo(reqInfo);
                request.setRequestor(secConfig.getSvcAccount());
                request.setUserAccount(loadAccount);

                if (DEBUG)
                {
                    DEBUGGER.debug("AccountControlRequest: {}", request);
                }

                AccountControlResponse response = processor.searchAccounts(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("AccountControlResponse: {}", response);
                }

                if (response.getRequestStatus() == SecurityRequestStatus.SUCCESS)
                {
                    List<UserAccount> accountList = response.getUserList();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("List<UserAccount>: {}", accountList);
                    }

                    for (UserAccount account : accountList)
                    {
                        System.out.println(account);
                    }

                    RETURN_CODE=0;
                }

                RETURN_CODE=1;
            }
            else if (commandLine.hasOption("load"))
            {
                final UserAccount loadAccount = new UserAccount();
                loadAccount.setGuid(commandLine.getOptionValue("loadOption"));

                if (DEBUG)
                {
                    DEBUGGER.debug("UserAccount: {}", loadAccount);
                }

                AccountControlRequest request = new AccountControlRequest();
                request.setApplicationId(secConfig.getApplicationId());
                request.setApplicationName(secConfig.getApplicationName());
                request.setHostInfo(reqInfo);
                request.setRequestor(secConfig.getSvcAccount());
                request.setUserAccount(loadAccount);

                if (DEBUG)
                {
                    DEBUGGER.debug("AccountControlRequest: {}", request);
                }

                AccountControlResponse response = processor.loadUserAccount(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("AccountControlResponse: {}", response);
                }

                if (response.getRequestStatus() == SecurityRequestStatus.SUCCESS)
                {
                    UserAccount account = response.getUserAccount();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("UserAccount: {}", account);
                    }

                    System.out.println(account);

                    RETURN_CODE=0;
                }

                RETURN_CODE=1;
            }
        }
        catch (ParseException px)
        {
            ERROR_RECORDER.error(px.getMessage(), px);

            px.printStackTrace();

            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(UserManagementUtility.CNAME, UserManagementUtility.options, true);

            RETURN_CODE=1;
        }
        catch (SecurityException sx)
        {
            ERROR_RECORDER.error(sx.getMessage(), sx);

            sx.printStackTrace();

            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(UserManagementUtility.CNAME, UserManagementUtility.options, true);

            RETURN_CODE=1;
        }
        catch (SecurityServiceException ssx)
        {
            ERROR_RECORDER.error(ssx.getMessage(), ssx);

            ssx.printStackTrace();

            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(UserManagementUtility.CNAME, UserManagementUtility.options, true);

            RETURN_CODE=1;
        }

        System.exit(RETURN_CODE);
    }
}
