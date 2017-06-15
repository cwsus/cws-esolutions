/*
 * Copyright (c) 2009 - 2017 CaspersBox Web Services
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
 * File: EmailUtility.java
 *
 * History
 * ----------------------------------------------------------------------------
 * cws-khuntly @ Jan 4, 2013 3:36:54 PM
 *     Created.
 */
import java.net.URL;
import org.slf4j.Logger;
import java.util.Arrays;
import java.util.ArrayList;
import org.slf4j.LoggerFactory;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBException;
import javax.mail.MessagingException;
import java.net.MalformedURLException;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.CommandLineParser;

import com.cws.esolutions.core.CoreServiceConstants;
import com.cws.esolutions.core.utils.EmailUtils;
import com.cws.esolutions.core.utils.dto.EmailMessage;
import com.cws.esolutions.core.config.xml.CoreConfigurationData;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author cws-khuntly
 * @version 1.0
 */
@SuppressWarnings("static-access")
public final class EmailUtility
{
    private static Options options = null;

    private static final String CNAME = EmailUtility.class.getName();

    static final Logger DEBUGGER = LoggerFactory.getLogger(CoreServiceConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(CoreServiceConstants.ERROR_LOGGER + CNAME);

    static
    {
        OptionGroup sendOptions = new OptionGroup();
        sendOptions.addOption(OptionBuilder.withLongOpt("to")
            .withDescription("The port number to connect to the server on")
            .hasArg(true)
            .withArgName("RECIPIENTS")
            .withType(String.class)
            .isRequired(true)
            .create());
        sendOptions.addOption(OptionBuilder.withLongOpt("from")
            .withDescription("The port number to connect to the server on")
            .hasArg(true)
            .withArgName("FROM")
            .withType(String.class)
            .isRequired(true)
            .create());
        sendOptions.addOption(OptionBuilder.withLongOpt("subject")
            .withDescription("The port number to connect to the server on")
            .hasArg(true)
            .withArgName("SUBJECT")
            .withType(String.class)
            .isRequired(true)
            .create());

        options = new Options();
        options.addOption(OptionBuilder.withLongOpt("config")
            .withDescription("The mail configuration XML to utilize")
            .hasArg(true)
            .withArgName("CONFIG")
            .withType(String.class)
            .isRequired(true)
            .create("c"));
        options.addOption("s", "send", false, "Send an email with the provided information");
        options.addOptionGroup(sendOptions);
    }

    public static final void main(final String[] args)
    {
        final String methodName = EmailUtility.CNAME + "#main(final String[] args)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", (Object) args);
        }

        if (args.length == 0)
        {
            HelpFormatter usage = new HelpFormatter();
            usage.printHelp(EmailUtility.CNAME, options, true);

            return;
        }

        try
        {
            CommandLineParser parser = new PosixParser();
            CommandLine commandLine = parser.parse(options, args);

            URL xmlURL = null;
            JAXBContext context = null;
            Unmarshaller marshaller = null;
            CoreConfigurationData configData = null;

            xmlURL = FileUtils.getFile(commandLine.getOptionValue("config")).toURI().toURL();
            context = JAXBContext.newInstance(CoreConfigurationData.class);
            marshaller = context.createUnmarshaller();
            configData = (CoreConfigurationData) marshaller.unmarshal(xmlURL);

            EmailMessage message = new EmailMessage();
            message.setMessageTo(new ArrayList<String>(Arrays.asList(commandLine.getOptionValues("to"))));
            message.setMessageSubject(commandLine.getOptionValue("subject"));
            message.setMessageBody((String) commandLine.getArgList().get(0));
            message.setEmailAddr(
                    (StringUtils.isNotEmpty(commandLine.getOptionValue("from"))) ? new ArrayList<String>(Arrays.asList(commandLine.getOptionValue("from")))
                            : new ArrayList<String>(Arrays.asList(configData.getMailConfig().getMailFrom())));

            if (DEBUG)
            {
                DEBUGGER.debug("EmailMessage: {}", message);
            }

            try
            {
                EmailUtils.sendEmailMessage(configData.getMailConfig(), message, false);
            }
            catch (MessagingException mx)
            {
                System.err.println("An error occurred while sending the requested message. Exception: " + mx.getMessage());
            }
        }
        catch (ParseException px)
        {
            px.printStackTrace();
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(EmailUtility.CNAME, options, true);
        }
        catch (MalformedURLException mx)
        {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(EmailUtility.CNAME, options, true);
        }
        catch (JAXBException jx)
        {
            jx.printStackTrace();
            System.err.println("An error occurred while loading the provided configuration file. Cannot continue.");
        }
    }
}
