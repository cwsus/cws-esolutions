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
package com.cws.esolutions.core.quartz;

import java.util.Map;
import java.util.List;
import org.quartz.Job;
import java.util.Arrays;
import org.slf4j.Logger;
import java.util.ArrayList;
import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;
import org.slf4j.LoggerFactory;
import java.io.LineNumberReader;
import javax.mail.MessagingException;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.cws.esolutions.core.Constants;
import com.cws.esolutions.core.utils.EmailUtils;
import com.cws.esolutions.security.utils.PasswordUtils;
import com.cws.esolutions.core.processors.dto.EmailMessage;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.quartz
 * CheckEmailMessagesJob.java
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
 * kh05451 @ Oct 29, 2012 9:44:46 AM
 *     Created.
 */
public class CheckEmailMessages implements Job
{
    private static final String CNAME = CheckEmailMessages.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CheckEmailMessages.CNAME);

    public CheckEmailMessages()
    {
        final String methodName = CheckEmailMessages.CNAME + "#CheckEmailMessagesJob()#Constructor";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }
    }

    @Override
    public void execute(final JobExecutionContext jec) throws JobExecutionException
    {
        final String methodName = CheckEmailMessages.CNAME + "#execute(final JobExecutionContext jec) throws JobExecutionException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("JobExecutionContext: {}", jec);
        }

        String strLine = null;
        Properties props = new Properties();
        Map<String, Object> jobDataMap = null;
        List<EmailMessage> messageList = null;

        try
        {
            jobDataMap = jec.getJobDetail().getJobDataMap();

            if (DEBUG)
            {
                DEBUGGER.debug("jobDataMap: {}", jobDataMap);
            }

            for (String key : jobDataMap.keySet())
            {
                if (DEBUG)
                {
                    DEBUGGER.debug("Key: " + key + ", Value: {}", jobDataMap.get(key));
                }

                props.put(key, jobDataMap.get(key));
            }

            if (DEBUG)
            {
                DEBUGGER.debug("props: {}", props);
            }

            String name = (String) props.get("userAccount");
            String pass = (String) props.get("userPass");

            messageList = EmailUtils.readEmailMessages(props, true,
                    new ArrayList<>(
                            Arrays.asList(
                                    name,
                                    PasswordUtils.decryptText(pass, pass.length()))));

            if (DEBUG)
            {
                DEBUGGER.debug("messageList: {}", messageList);
            }

            for (EmailMessage message : messageList)
            {
                if (DEBUG)
                {
                    DEBUGGER.debug("EmailMessage: {}", message);
                }

                LineNumberReader lReader = new LineNumberReader(new StringReader(message.getMessageBody()));

                while ((strLine = lReader.readLine()) != null)
                {
                    if (DEBUG)
                    {
                        DEBUGGER.debug("Line: " + lReader.getLineNumber() + " String: " + strLine);
                    }
                }
            }
        }
        catch (MessagingException mx)
        {
            ERROR_RECORDER.error(mx.getMessage(), mx);
        }
        catch (IOException iox)
        {
            ERROR_RECORDER.error(iox.getMessage(), iox);
        }
    }
}
