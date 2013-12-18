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
import org.junit.Test;
import org.junit.Before;
import org.junit.Assert;
import java.util.Arrays;
import java.util.HashMap;
import java.io.InputStream;
import java.util.ArrayList;
import java.io.FileInputStream;
import javax.mail.MessagingException;
import java.io.FileNotFoundException;
import org.apache.commons.io.FileUtils;

import com.cws.esolutions.core.CoreServiceBean;
import com.cws.esolutions.core.utils.dto.EmailMessage;
import com.cws.esolutions.core.listeners.CoreServiceInitializer;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.utils
 * EmailUtilsTest.java
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
 * 35033355 @ Jun 11, 2013 12:40:46 PM
 *     Created.
 */
public class EmailUtilsTest
{
    private static final CoreServiceBean bean = CoreServiceBean.getInstance();

    @Before
    public void setUp()
    {
        try
        {
            CoreServiceInitializer.initializeService("eSolutionsCore/config/ServiceConfig.xml", "logging/logging.xml");
        }
        catch (Exception ex)
        {
            Assert.fail(ex.getMessage());

            System.exit(-1);
        }
    }

    @Test
    public void testSendEmailMessage()
    {
        EmailMessage message = new EmailMessage();
        message.setIsAlert(false);
        message.setMessageSubject("Test Message");
        message.setMessageBCC(new ArrayList<>(Arrays.asList("kmhuntly@gmail.com")));
        message.setMessageCC(new ArrayList<>(Arrays.asList("kmhuntly@gmail.com")));
        message.setMessageTo(new ArrayList<>(Arrays.asList("kmhuntly@gmail.com")));
        message.setEmailAddr(new ArrayList<>(Arrays.asList("kmhuntly@gmail.com")));
        message.setMessageBody("This is a test message");

        try
        {
            EmailUtils.sendEmailMessage(EmailUtilsTest.bean.getConfigData().getMailConfig(), message, false);
        }
        catch (MessagingException mx)
        {
            Assert.fail(mx.getMessage());
        }
    }

    @Test
    public void testSendEmailMessageWithAttachment()
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
            message.setMessageBCC(new ArrayList<>(Arrays.asList("kmhuntly@gmail.com")));
            message.setMessageCC(new ArrayList<>(Arrays.asList("kmhuntly@gmail.com")));
            message.setMessageTo(new ArrayList<>(Arrays.asList("kmhuntly@gmail.com")));
            message.setEmailAddr(new ArrayList<>(Arrays.asList("kmhuntly@gmail.com")));
            message.setMessageBody("This is a test message");
            message.setMessageAttachments(attachments);

            EmailUtils.sendEmailMessage(EmailUtilsTest.bean.getConfigData().getMailConfig(), message, false);
        }
        catch (MessagingException mx)
        {
            Assert.fail(mx.getMessage());
        }
        catch (FileNotFoundException fnfx)
        {
            Assert.fail(fnfx.getMessage());
        }
    }

    @Test
    public void testSendSmsMessage()
    {
        EmailMessage message = new EmailMessage();
        message.setIsAlert(false);
        message.setMessageSubject("Test Message");
        message.setMessageBCC(new ArrayList<>(Arrays.asList("kmhuntly@gmail.com")));
        message.setMessageCC(new ArrayList<>(Arrays.asList("kmhuntly@gmail.com")));
        message.setMessageTo(new ArrayList<>(Arrays.asList("7163415669@vmobl.com")));
        message.setEmailAddr(new ArrayList<>(Arrays.asList("kmhuntly@gmail.com")));
        message.setMessageBody("This is a test message");

        try
        {
            EmailUtils.sendEmailMessage(EmailUtilsTest.bean.getConfigData().getMailConfig(), message, false);
        }
        catch (MessagingException mx)
        {
            Assert.fail(mx.getMessage());
        }
    }

    /*
    @Test
    public final void testReadEmailMessages()
    {
        try
        {
            EmailUtils.readEmailMessages(appBean.getConfigData().getMailConfig(), false, null);
        }
        catch (MessagingException mx)
        {
            Assert.fail(mx.getMessage());
        }
    }
    */
}
