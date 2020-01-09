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
 * File: SQLUtility.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly   		11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.ParseException;

import com.cws.esolutions.core.CoreServiceConstants;
import com.cws.esolutions.core.exception.CoreServiceException;
import com.cws.esolutions.core.listeners.CoreServiceInitializer;
import com.cws.esolutions.security.exception.SecurityServiceException;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
/**
 * @author cws-khuntly
 * @version 1.0
 */
@SuppressWarnings("static-access")
public class SQLUtility
{
    private static Options options = null;

    private static final String CNAME = SQLUtility.class.getName();
    private static final String CORE_LOG_CONFIG = System.getProperty("user.home") + "/etc/eSolutionsCore/logging/logging.xml";
    private static final String CORE_SVC_CONFIG = System.getProperty("user.home") + "/etc/eSolutionsCore/config/ServiceConfig.xml";
    private static final String SEC_LOG_CONFIG = System.getProperty("user.home") + "/etc/SecurityService/logging/logging.xml";
    private static final String SEC_SVC_CONFIG = System.getProperty("user.home") + "/etc/SecurityService/config/ServiceConfig.xml";

    private static final Logger DEBUGGER = LoggerFactory.getLogger(CoreServiceConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public enum LoadType
    {
        INSERT,
        APPEND,
        TRUNCATE,
        REPLACE;
    }

    static
    {
        OptionGroup commandOptions = new OptionGroup();
        commandOptions.addOption(OptionBuilder.withLongOpt("ssh")
            .withDescription("Perform an SSH connection to a target host")
            .isRequired(true)
            .create());
        commandOptions.addOption(OptionBuilder.withLongOpt("scp")
            .withDescription("Perform an SCP connection to a target host")
            .isRequired(true)
            .create());
        commandOptions.addOption(OptionBuilder.withLongOpt("telnet")
            .withDescription("Perform an telnet connection to a target host")
            .isRequired(true)
            .create());
        commandOptions.addOption(OptionBuilder.withLongOpt("tcp")
            .withDescription("Perform an TCP connection to a target host and put data on the request")
            .isRequired(true)
            .create());
        commandOptions.addOption(OptionBuilder.withLongOpt("http")
            .withDescription("Perform an HTTP request to a target host")
            .isRequired(true)
            .create());
    }

    public static final void main(final String[] args)
    {
        final String methodName = SQLUtility.CNAME + "#main(final String[] args)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", (Object) args);
        }

        if (args.length == 0)
        {
            HelpFormatter usage = new HelpFormatter();
            usage.printHelp(SQLUtility.CNAME, options, true);

            return;
        }

        final String coreConfiguration = (StringUtils.isBlank(System.getProperty("coreConfigFile"))) ? SQLUtility.CORE_SVC_CONFIG : System.getProperty("coreConfigFile");
        final String securityConfiguration = (StringUtils.isBlank(System.getProperty("secConfigFile"))) ? SQLUtility.CORE_LOG_CONFIG : System.getProperty("secConfigFile");
        final String coreLogging = (StringUtils.isBlank(System.getProperty("coreLogConfig"))) ? SQLUtility.SEC_SVC_CONFIG : System.getProperty("coreLogConfig");
        final String securityLogging = (StringUtils.isBlank(System.getProperty("secLogConfig"))) ? SQLUtility.SEC_LOG_CONFIG : System.getProperty("secLogConfig");

        if (DEBUG)
        {
            DEBUGGER.debug("String coreConfiguration: {}", coreConfiguration);
            DEBUGGER.debug("String securityConfiguration: {}", securityConfiguration);
            DEBUGGER.debug("String coreLogging: {}", coreLogging);
            DEBUGGER.debug("String securityLogging: {}", securityLogging);
        }

        try
        {
            SecurityServiceInitializer.initializeService(securityConfiguration, securityLogging, false);
            CoreServiceInitializer.initializeService(coreConfiguration, coreLogging, false, true);
        }
        catch (CoreServiceException csx)
        {
            System.err.println("An error occurred while loading configuration data: " + csx.getCause().getMessage());

            System.exit(1);
        }
        catch (SecurityServiceException sx)
        {
            System.err.println("An error occurred while loading configuration data: " + sx.getCause().getMessage());

            System.exit(1);
		}

        Options options = new Options();

        try
        {
            throw new ParseException("nothing to see here");
        }
        catch (ParseException px)
        {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(SQLUtility.CNAME, options, true);
        }
    }
}
