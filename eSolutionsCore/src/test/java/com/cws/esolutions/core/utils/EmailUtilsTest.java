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
package com.cws.esolutions.core.utils;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.utils
 * File: EmailUtilsTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;
import java.io.InputStream;
import java.util.ArrayList;
import java.io.FileInputStream;
import org.junit.jupiter.api.Test;
import javax.mail.MessagingException;
import java.io.FileNotFoundException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.assertj.core.api.Assertions;
import org.apache.commons.io.FileUtils;

import com.cws.esolutions.core.CoreServicesBean;
import com.cws.esolutions.core.utils.dto.EmailMessage;
import com.cws.esolutions.core.listeners.CoreServiceInitializer;

public class EmailUtilsTest
{
    private static final CoreServicesBean bean = CoreServicesBean.getInstance();

    @BeforeAll public void setUp()
    {
        try
        {
            CoreServiceInitializer.initializeService("eSolutionsCore/config/ServiceConfig.xml", "eSolutionsCore/logging/logging.xml", false, false);
        }
        catch (Exception ex)
        {
            Assertions.fail(ex.getMessage());

            System.exit(-1);
        }
    }

    @Test public void sendEmailMessage()
    {
        EmailMessage message = new EmailMessage();
        message.setIsAlert(false);
        message.setMessageSubject("Test Message");
        message.setMessageBCC(new ArrayList<String>(Arrays.asList("cws-khuntly")));
        message.setMessageCC(new ArrayList<String>(Arrays.asList("cws-khuntly")));
        message.setMessageTo(new ArrayList<String>(Arrays.asList("cws-khuntly")));
        message.setEmailAddr(new ArrayList<String>(Arrays.asList("cws-khuntly")));
        message.setMessageBody("This is a test message");

        try
        {
            EmailUtils.sendEmailMessage(EmailUtilsTest.bean.getConfigData().getMailConfig(), message, false);
        }
        catch (MessagingException mx)
        {
            Assertions.fail(mx.getMessage());
        }
    }

    @Test public void sendEmailMessageWithAttachment()
    {
        try
        {
            Map<String, InputStream> attachments = new HashMap<String, InputStream>()
            {
                private static final long serialVersionUID = 1L;

                {
                    put("myFile", new FileInputStream(FileUtils.getFile("C:/temp", "myFile")));
                }
            };

            EmailMessage message = new EmailMessage();
            message.setIsAlert(false);
            message.setMessageSubject("Test Message");
            message.setMessageBCC(new ArrayList<String>(Arrays.asList("cws-khuntly")));
            message.setMessageCC(new ArrayList<String>(Arrays.asList("cws-khuntly")));
            message.setMessageTo(new ArrayList<String>(Arrays.asList("cws-khuntly")));
            message.setEmailAddr(new ArrayList<String>(Arrays.asList("cws-khuntly")));
            message.setMessageBody("This is a test message");
            message.setMessageAttachments(attachments);

            EmailUtils.sendEmailMessage(EmailUtilsTest.bean.getConfigData().getMailConfig(), message, false);
        }
        catch (MessagingException mx)
        {
            Assertions.fail(mx.getMessage());
        }
        catch (FileNotFoundException fnfx)
        {
            Assertions.fail(fnfx.getMessage());
        }
    }

    @Test public void sendEmailMessageAsSMS()
    {
        EmailMessage message = new EmailMessage();
        message.setIsAlert(false);
        message.setMessageSubject("Test Message");
        message.setMessageBCC(new ArrayList<String>(Arrays.asList("cws-khuntly")));
        message.setMessageCC(new ArrayList<String>(Arrays.asList("cws-khuntly")));
        message.setMessageTo(new ArrayList<String>(Arrays.asList("7163415669@vmobl.com")));
        message.setEmailAddr(new ArrayList<String>(Arrays.asList("cws-khuntly")));
        message.setMessageBody("This is a test message");

        try
        {
            EmailUtils.sendEmailMessage(EmailUtilsTest.bean.getConfigData().getMailConfig(), message, false);
        }
        catch (MessagingException mx)
        {
            Assertions.fail(mx.getMessage());
        }
    }

    /*
    @Test public final void readEmailMessages()
    {
        try
        {
            EmailUtils.readEmailMessages(appBean.getConfigData().getMailConfig(), false, null);
        }
        catch (MessagingException mx)
        {
            Assertions.fail(mx.getMessage());
        }
    }
    */

    @AfterAll public void tearDown()
    {
        CoreServiceInitializer.shutdown();
    }
}
