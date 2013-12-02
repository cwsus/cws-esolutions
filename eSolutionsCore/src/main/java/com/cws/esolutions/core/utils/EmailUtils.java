/**
 * Copyright (c) 2009 - 2013 By: CWS, Inc.
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

import java.util.Map;
import java.util.List;
import javax.mail.Part;
import org.slf4j.Logger;
import javax.mail.Store;
import javax.mail.Flags;
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
import javax.mail.Authenticator;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.Message.RecipientType;
import javax.mail.PasswordAuthentication;
import javax.mail.internet.InternetAddress;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.mail.javamail.JavaMailSender;

import com.cws.esolutions.core.Constants;
import com.cws.esolutions.core.CoreServiceBean;
import com.cws.esolutions.core.config.MailConfig;
import com.cws.esolutions.core.processors.dto.EmailMessage;
import com.cws.esolutions.security.access.control.impl.EmailControlServiceImpl;
import com.cws.esolutions.security.access.control.interfaces.IEmailControlService;
import com.cws.esolutions.security.access.control.exception.EmailControlServiceException;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.utils
 * NetworkUtils.java
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
 * kh05451 @ Dec 31, 2012 9:45:18 AM
 *     Add in http method
 */
public final class EmailUtils
{
    private static JavaMailSender mailSender = null;

    private static final String INIT_DS_CONTEXT = "java:comp/env/";
    private static final String CNAME = EmailUtils.class.getName();
    private static final CoreServiceBean appBean = CoreServiceBean.getInstance();

    static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CNAME);

    public final void setMailSender(final JavaMailSender value)
    {
        final String methodName = EmailUtils.CNAME + "#setValidator(final JavaMailSender value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        EmailUtils.mailSender = value;
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
         * @param userName
         * @param password
         * @return PasswordAuthentication
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
     * @param emailMessage - The email message
     * @throws MessagingException
     */
    public static final synchronized void sendEmailMessage(final EmailMessage emailMessage) throws MessagingException
    {
        final String methodName = EmailUtils.CNAME + "#sendEmailMessage(final EmailMessage emailMessage) throws MessagingException";

        Session mailSession = null;

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("emailMessage: {}", emailMessage);
        }

        try
        {
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
            if (emailMessage.getMessageTo().size() != 0)
            {
                for (String to : emailMessage.getMessageTo())
                {
                    if (DEBUG)
                    {
                        DEBUGGER.debug(to);
                    }

                    mailMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
                }

                mailMessage.setFrom(new InternetAddress(emailMessage.getEmailAddr().get(0)));
                mailMessage.setSubject("[" + RandomStringUtils.randomAlphanumeric(16) + "] " + emailMessage.getMessageSubject());
                mailMessage.setContent(emailMessage.getMessageBody(), "text/html");

                if (emailMessage.isAlert())
                {
                    mailMessage.setHeader("Importance", "High");
                }

                EmailUtils.mailSender.send(mailMessage);
            }
        }
        catch (MessagingException mex)
        {
            ERROR_RECORDER.error(mex.getMessage(), mex);

            throw new MessagingException(mex.getMessage(), mex);
        }
    }

    /**
     * Processes and sends an email message as generated by the requesting
     * application. This method is utilized with a JNDI datasource.
     *
     * @param emailMessage - The email message
     * @throws MessagingException
     */
    public static final synchronized void sendEmailMessage(final EmailMessage emailMessage, final boolean isWeb) throws MessagingException
    {
        final String methodName = EmailUtils.CNAME + "#sendEmailMessage(final EmailMessage emailMessage, final boolean isWeb) throws MessagingException";

        Session mailSession = null;

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("emailMessage: {}", emailMessage);
            DEBUGGER.debug("emailMessage: {}", isWeb);
        }

        SMTPAuthenticator smtpAuth = null;

        final MailConfig mailConfig = appBean.getConfigData().getMailConfig();

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
                if (StringUtils.equals((String) mailConfig.getMailProps().get("mail.smtp.auth"), "true"))
                {
                    smtpAuth = new SMTPAuthenticator();
                    mailSession = Session.getDefaultInstance(mailConfig.getMailProps(), smtpAuth);
                }
                else
                {
                    mailSession = Session.getDefaultInstance(mailConfig.getMailProps());
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
            if (emailMessage.getMessageTo().size() != 0)
            {
                String messageID = (StringUtils.isBlank(emailMessage.getMessageId())) ? emailMessage.getMessageId() : RandomStringUtils.randomAlphanumeric(16);

                for (String to : emailMessage.getMessageTo())
                {
                    if (DEBUG)
                    {
                        DEBUGGER.debug(to);
                    }

                    mailMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
                }

                mailMessage.setFrom(new InternetAddress(emailMessage.getEmailAddr().get(0)));
                mailMessage.setSubject("[" + messageID + "] " + emailMessage.getMessageSubject());
                mailMessage.setContent(emailMessage.getMessageBody(), "text/html");

                if (emailMessage.isAlert())
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
            ERROR_RECORDER.error(mex.getMessage(), mex);

            throw new MessagingException(mex.getMessage(), mex);
        }
        catch (NamingException nx)
        {
            ERROR_RECORDER.error(nx.getMessage(), nx);

            throw new MessagingException(nx.getMessage(), nx);
        }
    }

    /**
     * Processes and sends an email message as generated by the requesting
     * application. This method is utilized with a JNDI datasource.
     *
     * @param dataSource - The email message
     * @param authRequired
     * @param authList
     * @return List<EmailMessage>
     * @throws MessagingException
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

        final String HOSTNAME = appBean.getHostName();
        final Long TIME_PERIOD = cal.getTimeInMillis();
        final URLName URL_NAME = (authRequired) ? new URLName(dataSource.getProperty("mailtype"), dataSource.getProperty("host"),
                Integer.parseInt(dataSource.getProperty("port")), null, authList.get(0), authList.get(1))
                : new URLName(dataSource.getProperty("mailtype"), dataSource.getProperty("host"),
                        Integer.parseInt(dataSource.getProperty("port")), null, null, null);

        if (DEBUG)
        {
            DEBUGGER.debug("HOSTNAME: {}", HOSTNAME);
            DEBUGGER.debug("timePeriod: {}", TIME_PERIOD);
            DEBUGGER.debug("URL_NAME: {}", URL_NAME);
        }

        try
        {
            // set up the controller
            IEmailControlService emailControl = new EmailControlServiceImpl();

            if (DEBUG)
            {
                DEBUGGER.debug("EmailControlServiceImpl: {}", emailControl);
            }

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
                                boolean isAuthorized = emailControl.isEmailAuthorized(message.getFrom()[0].toString(),
                                        message.getHeader("Received"), false);

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("isAuthorized: {}", isAuthorized);
                                }

                                if (isAuthorized)
                                {
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
                    }
                    else
                    {
                        // cant open folder
                        ERROR_RECORDER.error("Failed to open requested folder. Cannot continue");

                        throw new MessagingException("Failed to open requested folder. Cannot continue");
                    }
                }
                else
                {
                    // folder doesnt exist
                    ERROR_RECORDER.error("Requested folder does not exist. Cannot continue.");

                    throw new MessagingException("Requested folder does not exist. Cannot continue.");
                }
            }
            else
            {
                // couldnt connect to service
                ERROR_RECORDER.error("Failed to connect to mail service. Cannot continue.");

                throw new MessagingException("Failed to connect to mail service. Cannot continue.");
            }
        }
        catch (IOException iox)
        {
            ERROR_RECORDER.error(iox.getMessage(), iox);

            throw new MessagingException(iox.getMessage(), iox);
        }
        catch (MessagingException mex)
        {
            ERROR_RECORDER.error(mex.getMessage(), mex);

            throw new MessagingException(mex.getMessage(), mex);
        }
        catch (EmailControlServiceException ecsx)
        {
            ERROR_RECORDER.error(ecsx.getMessage(), ecsx);

            throw new MessagingException(ecsx.getMessage(), ecsx);
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
