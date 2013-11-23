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
package com.cws.esolutions.security.quartz;

import java.util.Map;
import java.util.List;
import org.quartz.Job;
import java.util.Arrays;
import org.slf4j.Logger;
import java.util.Calendar;
import java.util.ArrayList;
import org.slf4j.LoggerFactory;
import javax.mail.MessagingException;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.cws.esolutions.core.utils.EmailUtils;
import com.cws.esolutions.security.SecurityConstants;
import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.core.processors.dto.EmailMessage;
import com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager;
import com.cws.esolutions.security.dao.usermgmt.factory.UserManagerFactory;
import com.cws.esolutions.security.dao.usermgmt.exception.UserManagementException;
/**
 * eSolutionsCore
 * com.cws.esolutions.security.quartz
 * PasswordExpirationNotifier.java
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
 * 35033355 @ Jul 10, 2013 1:30:34 PM
 *     Created.
 */
public class PasswordExpirationNotifier implements Job
{
    private static final String CNAME = PasswordExpirationNotifier.class.getName();
    private static final SecurityServiceBean bean = SecurityServiceBean.getInstance();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityConstants.ERROR_LOGGER + PasswordExpirationNotifier.CNAME);

    public PasswordExpirationNotifier()
    {
        final String methodName = PasswordExpirationNotifier.CNAME + "#PasswordExpirationNotifier()#Constructor";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }
    }

    @Override
    public void execute(final JobExecutionContext context) throws JobExecutionException
    {
        final String methodName = PasswordExpirationNotifier.CNAME + "#execute(final JobExecutionContext jobContext)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("JobExecutionContext: {}", context);
        }

        final String messageBody = "messageBody";
        final String sendMessageAs = "sendMessageAs";
        final String messageSubject = "messageSubject";
        final Map<String, Object> jobData = context.getJobDetail().getJobDataMap();

        if (DEBUG)
        {
            DEBUGGER.debug("jobData: {}", jobData);
        }

        try
        {
            UserManager manager = UserManagerFactory.getUserManager(bean.getConfigData().getSecurityConfig().getUserManager());

            if (DEBUG)
            {
                DEBUGGER.debug("UserManager: {}", manager);
            }

            List<String[]> accounts = manager.listUserAccounts();

            if (DEBUG)
            {
                DEBUGGER.debug("accounts: {}", accounts);
            }

            if ((accounts != null) && (accounts.size() != 0))
            {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, 30);
                Long expiryTime = cal.getTimeInMillis();

                if (DEBUG)
                {
                    DEBUGGER.debug("Calendar: {}", cal);
                    DEBUGGER.debug("expiryTime: {}", expiryTime);
                }

                for (String[] account : accounts)
                {
                    if (DEBUG)
                    {
                        for (String str : account)
                        {
                            DEBUGGER.debug("account: {}", str);
                        }
                    }

                    String commonName = account[0];
                    String userName = account[1];
                    String emailAddr = account[5];
                    long passExpiry = Long.valueOf(account[9]);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("commonName: {}", commonName);
                        DEBUGGER.debug("userName: {}", userName);
                        DEBUGGER.debug("emailAddr: {}", emailAddr);
                        DEBUGGER.debug("passExpiry: {}", passExpiry);
                    }

                    if (passExpiry >= expiryTime)
                    {
                        // generate an email for the user
                        EmailMessage message = new EmailMessage();
                        message.setIsAlert(false);
                        message.setEmailAddr(new ArrayList<>(Arrays.asList((String) jobData.get(sendMessageAs))));
                        message.setMessageTo(new ArrayList<>(Arrays.asList(emailAddr)));
                        message.setMessageSubject(messageSubject);
                        message.setMessageBody(messageBody);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("EmailMessage: {}", message);
                        }

                        EmailUtils.sendEmailMessage(message);
                    }
                }
            }
        }
        catch (UserManagementException umx)
        {
            ERROR_RECORDER.error(umx.getMessage(), umx);
        }
        catch (MessagingException mx)
        {
            ERROR_RECORDER.error(mx.getMessage(), mx);
        }
    }
}
