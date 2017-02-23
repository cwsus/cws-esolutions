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
package com.cws.esolutions.core.utils;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.utils
 * File: EmailUtils.java
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Jan 4, 2013 3:36:54 PM
 *     Created.
 */
import java.net.URL;
import java.util.Map;
import java.util.List;
import javax.mail.Part;
import org.slf4j.Logger;
import javax.mail.Store;
import javax.mail.Flags;
import java.util.Arrays;
import java.util.HashMap;
import javax.mail.Folder;
import java.util.Calendar;
import javax.mail.Session;
import javax.mail.Message;
import javax.mail.URLName;
import javax.mail.Address;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.mail.BodyPart;
import javax.mail.Transport;
import javax.naming.Context;
import java.util.Properties;
import javax.mail.Multipart;
import org.slf4j.LoggerFactory;
import java.io.FileInputStream;
import javax.mail.Authenticator;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.naming.InitialContext;
import javax.xml.bind.JAXBException;
import javax.naming.NamingException;
import javax.mail.MessagingException;
import java.net.MalformedURLException;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import javax.mail.internet.MimeMessage;
import javax.mail.Message.RecipientType;
import javax.mail.PasswordAuthentication;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang.StringUtils;
import javax.mail.internet.InternetAddress;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.CommandLineParser;

import com.cws.esolutions.core.CoreServiceConstants;
import com.cws.esolutions.core.config.xml.MailConfig;
import com.cws.esolutions.core.utils.dto.EmailMessage;
import com.cws.esolutions.core.config.xml.CoreConfigurationData;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 */
@SuppressWarnings("static-access")
public final class EmailUtils
{
    private static Options options = null;

    private static final String INIT_DS_CONTEXT = "java:comp/env/";
    private static final String CNAME = EmailUtils.class.getName();

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
        final String methodName = EmailUtils.CNAME + "#main(final String[] args)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", (Object) args);
        }

        if (args.length == 0)
        {
            HelpFormatter usage = new HelpFormatter();
            usage.printHelp(EmailUtils.CNAME, options, true);

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

            System.out.println(configData.getMailConfig());
            EmailMessage message = new EmailMessage();
            message.setMessageTo(new ArrayList<>(Arrays.asList(commandLine.getOptionValues("to"))));
            message.setMessageSubject(commandLine.getOptionValue("subject"));
            message.setMessageBody((String) commandLine.getArgList().get(0));
            message.setEmailAddr(
                    (StringUtils.isNotEmpty(commandLine.getOptionValue("from"))) ? new ArrayList<>(Arrays.asList(commandLine.getOptionValue("from")))
                            : new ArrayList<>(Arrays.asList(configData.getMailConfig().getMailFrom())));

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
            formatter.printHelp(EmailUtils.CNAME, options, true);
        }
        catch (MalformedURLException mx)
        {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(EmailUtils.CNAME, options, true);
        }
        catch (JAXBException jx)
        {
            jx.printStackTrace();
            System.err.println("An error occurred while loading the provided configuration file. Cannot continue.");
        }
    }

    /**
     * eSolutionsCore
     * com.cws.esolutions.core.utils
     * EmailUtils.java$SMTPAuthenticator
     *
     * Inner class performing extending <code>Authenticator</code> This will
     * be utilized when authentication is required to the specified SMTP
     * server, as configured.
     *
     * $Id: $
     * $Author: $
     * $Date: $
     * $Revision: $
     * @author kmhuntly@gmail.com
     * @version 1.0
     *
     * History
     * ----------------------------------------------------------------------------
     * kh05451 @ Dec 26, 2012 12:54:17 PM
     *     Created.
     */
    public static final class SMTPAuthenticator extends Authenticator
    {
        static final String CNAME = SMTPAuthenticator.class.getName();

        /**
         * Returns an instance of PasswordAuthentication to provide
         * an authentication mechanism for a target SMTP server
         *
         * @param userName - The username to utilize
         * @param password - The password to utilize
         * @return PasswordAuthentication - The password authentication object
         */
        public static final synchronized PasswordAuthentication getPasswordAuthentication(final String userName, final String password)
        {
            final String methodName = SMTPAuthenticator.CNAME + "#getPasswordAuthentication(final String userName, final String password)";

            if (DEBUG)
            {
                DEBUGGER.debug(methodName);
                DEBUGGER.debug(userName);
                DEBUGGER.debug(password);
            }

            return new PasswordAuthentication(userName, password);
        }
    }

    /**
     * Processes and sends an email message as generated by the requesting
     * application. This method is utilized with a JNDI datasource.
     *
     * @param mailConfig - The {@link com.cws.esolutions.core.config.xml.MailConfig} to utilize
     * @param message - The email message
     * @param isWeb - <code>true</code> if this came from a container, <code>false</code> otherwise
     * @throws MessagingException {@link javax.mail.MessagingException} if an exception occurs sending the message
     */
    public static final synchronized void sendEmailMessage(final MailConfig mailConfig, final EmailMessage message, final boolean isWeb) throws MessagingException
    {
        final String methodName = EmailUtils.CNAME + "#sendEmailMessage(final MailConfig mailConfig, final EmailMessage message, final boolean isWeb) throws MessagingException";

        Session mailSession = null;

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", mailConfig);
            DEBUGGER.debug("Value: {}", message);
            DEBUGGER.debug("Value: {}", isWeb);
        }

        SMTPAuthenticator smtpAuth = null;

        if (DEBUG)
        {
            DEBUGGER.debug("MailConfig: {}", mailConfig);
        }

        try
        {
            if (isWeb)
            {
                Context initContext = new InitialContext();
                Context envContext = (Context) initContext.lookup(EmailUtils.INIT_DS_CONTEXT);

                if (DEBUG)
                {
                    DEBUGGER.debug("InitialContext: {}", initContext);
                    DEBUGGER.debug("Context: {}", envContext);
                }

                if (envContext != null)
                {
                    mailSession = (Session) envContext.lookup(mailConfig.getDataSourceName());
                }
            }
            else
            {
                Properties mailProps = new Properties();

                try
                {
                    mailProps.load(EmailUtils.class.getResourceAsStream(mailConfig.getPropertyFile()));
                }
                catch (NullPointerException npx)
                {
                    try
                    {
                        mailProps.load(new FileInputStream(mailConfig.getPropertyFile()));
                    }
                    catch (IOException iox)
                    {
                        throw new MessagingException(iox.getMessage(), iox);
                    }
                }
                catch (IOException iox)
                {
                    throw new MessagingException(iox.getMessage(), iox);
                }

                if (DEBUG)
                {
                    DEBUGGER.debug("Properties: {}", mailProps);
                }

                if (StringUtils.equals((String) mailProps.get("mail.smtp.auth"), "true"))
                {
                    smtpAuth = new SMTPAuthenticator();
                    mailSession = Session.getDefaultInstance(mailProps, smtpAuth);
                }
                else
                {
                    mailSession = Session.getDefaultInstance(mailProps);
                }
            }

            if (DEBUG)
            {
                DEBUGGER.debug("Session: {}", mailSession);
            }

            if (mailSession == null)
            {
                throw new MessagingException("Unable to configure email services");
            }

            mailSession.setDebug(DEBUG);
            MimeMessage mailMessage = new MimeMessage(mailSession);

            // Our emailList parameter should contain the following
            // items (in this order):
            // 0. Recipients
            // 1. From Address
            // 2. Generated-From (if blank, a default value is used)
            // 3. The message subject
            // 4. The message content
            // 5. The message id (optional)
            // We're only checking to ensure that the 'from' and 'to'
            // values aren't null - the rest is really optional.. if
            // the calling application sends a blank email, we aren't
            // handing it here.
            if (message.getMessageTo().size() != 0)
            {
                for (String to : message.getMessageTo())
                {
                    if (DEBUG)
                    {
                        DEBUGGER.debug(to);
                    }

                    mailMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
                }

                mailMessage.setFrom(new InternetAddress(message.getEmailAddr().get(0)));
                mailMessage.setSubject(message.getMessageSubject());
                mailMessage.setContent(message.getMessageBody(), "text/html");

                if (message.isAlert())
                {
                    mailMessage.setHeader("Importance", "High");
                }

                Transport mailTransport = mailSession.getTransport("smtp");

                if (DEBUG)
                {
                    DEBUGGER.debug("Transport: {}", mailTransport);
                }

                mailTransport.connect();

                if (mailTransport.isConnected())
                {
                    Transport.send(mailMessage);
                }
            }
        }
        catch (MessagingException mex)
        {
            throw new MessagingException(mex.getMessage(), mex);
        }
        catch (NamingException nx)
        {
            throw new MessagingException(nx.getMessage(), nx);
        }
    }

    /**
     * Processes and sends an email message as generated by the requesting
     * application. This method is utilized with a JNDI datasource.
     *
     * @param dataSource - The email message
     * @param authRequired - <code>true</code> if authentication is required, <code>false</code> otherwise
     * @param authList - If authRequired is true, this must be populated with the auth info
     * @return List - The list of email messages in the mailstore
     * @throws MessagingException {@link javax.mail.MessagingException} if an exception occurs during processing
     */
    public static final synchronized List<EmailMessage> readEmailMessages(final Properties dataSource, final boolean authRequired, final List<String> authList) throws MessagingException
    {
        final String methodName = EmailUtils.CNAME + "#readEmailMessages(final Properties dataSource, final boolean authRequired, final List<String> authList) throws MessagingException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("dataSource: {}", dataSource);
            DEBUGGER.debug("authRequired: {}", authRequired);
            DEBUGGER.debug("authList: {}", authList);
        }

        Folder mailFolder = null;
        Session mailSession = null;
        Folder archiveFolder = null;
        List<EmailMessage> emailMessages = null;

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, -24);

        final Long TIME_PERIOD = cal.getTimeInMillis();
        final URLName URL_NAME = (authRequired) ? new URLName(dataSource.getProperty("mailtype"), dataSource.getProperty("host"),
                Integer.parseInt(dataSource.getProperty("port")), null, authList.get(0), authList.get(1))
                : new URLName(dataSource.getProperty("mailtype"), dataSource.getProperty("host"),
                        Integer.parseInt(dataSource.getProperty("port")), null, null, null);

        if (DEBUG)
        {
            DEBUGGER.debug("timePeriod: {}", TIME_PERIOD);
            DEBUGGER.debug("URL_NAME: {}", URL_NAME);
        }

        try
        {
            // Set up mail session
            mailSession = (authRequired) ? Session.getDefaultInstance(dataSource, new SMTPAuthenticator()) : Session.getDefaultInstance(dataSource);

            if (DEBUG)
            {
                DEBUGGER.debug("mailSession: {}", mailSession);
            }

            if (mailSession == null)
            {
                throw new MessagingException("Unable to configure email services");
            }

            mailSession.setDebug(DEBUG);
            Store mailStore = mailSession.getStore(URL_NAME);
            mailStore.connect();

            if (DEBUG)
            {
                DEBUGGER.debug("mailStore: {}", mailStore);
            }

            if (mailStore.isConnected())
            {
                mailFolder = mailStore.getFolder("inbox");
                archiveFolder = mailStore.getFolder("archive");

                if (mailFolder.exists())
                {
                    mailFolder.open(Folder.READ_WRITE);

                    if ((mailFolder.isOpen()) && (mailFolder.hasNewMessages()))
                    {
                        if (!(archiveFolder.exists()))
                        {
                            archiveFolder.create(Folder.HOLDS_MESSAGES);
                        }

                        Message[] mailMessages = mailFolder.getMessages();

                        if (mailMessages.length != 0)
                        {
                            emailMessages = new ArrayList<>();

                            for (Message message : mailMessages)
                            {
                                if (DEBUG)
                                {
                                    DEBUGGER.debug("MailMessage: {}", message);
                                }

                                // validate the message here
                                String messageId = message.getHeader("Message-ID")[0];
                                Long messageDate = message.getReceivedDate().getTime();

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("messageId: {}", messageId);
                                    DEBUGGER.debug("messageDate: {}", messageDate);
                                }

                                // only get emails for the last 24 hours
                                // this should prevent us from pulling too
                                // many emails
                                if (messageDate >= TIME_PERIOD)
                                {
                                    // process it
                                    Multipart attachment = (Multipart) message.getContent();
                                    Map<String, InputStream> attachmentList = new HashMap<>();

                                    for (int x = 0; x < attachment.getCount(); x++)
                                    {
                                        BodyPart bodyPart = attachment.getBodyPart(x);

                                        if (!(Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())))
                                        {
                                            continue;
                                        }

                                        attachmentList.put(bodyPart.getFileName(), bodyPart.getInputStream());
                                    }

                                    List<String> toList = new ArrayList<>();
                                    List<String> ccList = new ArrayList<>();
                                    List<String> bccList = new ArrayList<>();
                                    List<String> fromList = new ArrayList<>();

                                    for (Address from : message.getFrom())
                                    {
                                        fromList.add(from.toString());
                                    }

                                    if ((message.getRecipients(RecipientType.TO) != null) && (message.getRecipients(RecipientType.TO).length != 0))
                                    {
                                        for (Address to : message.getRecipients(RecipientType.TO))
                                        {
                                            toList.add(to.toString());
                                        }
                                    }

                                    if ((message.getRecipients(RecipientType.CC) != null) && (message.getRecipients(RecipientType.CC).length != 0))
                                    {
                                        for (Address cc : message.getRecipients(RecipientType.CC))
                                        {
                                            ccList.add(cc.toString());
                                        }
                                    }

                                    if ((message.getRecipients(RecipientType.BCC) != null) && (message.getRecipients(RecipientType.BCC).length != 0))
                                    {
                                        for (Address bcc : message.getRecipients(RecipientType.BCC))
                                        {
                                            bccList.add(bcc.toString());
                                        }
                                    }

                                    EmailMessage emailMessage = new EmailMessage();
                                    emailMessage.setMessageTo(toList);
                                    emailMessage.setMessageCC(ccList);
                                    emailMessage.setMessageBCC(bccList);
                                    emailMessage.setEmailAddr(fromList);
                                    emailMessage.setMessageAttachments(attachmentList);
                                    emailMessage.setMessageDate(message.getSentDate());
                                    emailMessage.setMessageSubject(message.getSubject());
                                    emailMessage.setMessageBody(message.getContent().toString());
                                    emailMessage.setMessageSources(message.getHeader("Received"));

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("emailMessage: {}", emailMessage);
                                    }

                                    emailMessages.add(emailMessage);

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("emailMessages: {}", emailMessages);
                                    }
                                }

                                // archive it
                                archiveFolder.open(Folder.READ_WRITE);

                                if (archiveFolder.isOpen())
                                {
                                    mailFolder.copyMessages(new Message[] { message }, archiveFolder);
                                    message.setFlag(Flags.Flag.DELETED, true);
                                }
                            }
                        }
                    }
                    else
                    {
                        // cant open folder
                        throw new MessagingException("Failed to open requested folder. Cannot continue");
                    }
                }
                else
                {
                    // folder doesnt exist
                    throw new MessagingException("Requested folder does not exist. Cannot continue.");
                }
            }
            else
            {
                // couldnt connect to service
                throw new MessagingException("Failed to connect to mail service. Cannot continue.");
            }
        }
        catch (IOException iox)
        {
            throw new MessagingException(iox.getMessage(), iox);
        }
        catch (MessagingException mex)
        {
            throw new MessagingException(mex.getMessage(), mex);
        }
        finally
        {
            try
            {
                if ((mailFolder != null) && (mailFolder.isOpen()))
                {
                    mailFolder.close(true);
                }

                if ((archiveFolder != null) && (archiveFolder.isOpen()))
                {
                    archiveFolder.close(false);
                }
            }
            catch (MessagingException mx)
            {
                ERROR_RECORDER.error(mx.getMessage(), mx);
            }
        }

        return emailMessages;
    }
}
