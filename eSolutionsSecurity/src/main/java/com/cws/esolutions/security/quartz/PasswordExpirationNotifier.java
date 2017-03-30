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
package com.cws.esolutions.security.quartz;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.quartz
 * File: PasswordExpirationNotifier.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.Map;
import java.util.List;
import org.quartz.Job;
import org.slf4j.Logger;
import java.util.Calendar;
import org.slf4j.LoggerFactory;
import org.apache.commons.mail.Email;
import org.quartz.JobExecutionContext;
import org.apache.commons.mail.SimpleEmail;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.DefaultAuthenticator;

import com.cws.esolutions.security.utils.PasswordUtils;
import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.config.xml.SystemConfig;
import com.cws.esolutions.security.SecurityServiceConstants;
import com.cws.esolutions.security.config.xml.SecurityConfig;
import com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager;
import com.cws.esolutions.security.dao.usermgmt.factory.UserManagerFactory;
import com.cws.esolutions.security.dao.usermgmt.exception.UserManagementException;
/**
 * @see org.quartz.Job
 */
public class PasswordExpirationNotifier implements Job
{
    private static final String CNAME = PasswordExpirationNotifier.class.getName();
    private static final SecurityServiceBean bean = SecurityServiceBean.getInstance();
    private static final SystemConfig systemConfig = bean.getConfigData().getSystemConfig();
    private static final SecurityConfig secConfig = bean.getConfigData().getSecurityConfig();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityServiceConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityServiceConstants.ERROR_LOGGER + PasswordExpirationNotifier.CNAME);

    /**
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    public void execute(final JobExecutionContext context)
    {
        final String methodName = PasswordExpirationNotifier.CNAME + "#execute(final JobExecutionContext jobContext)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("JobExecutionContext: {}", context);
        }

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

            if ((accounts == null) || (accounts.size() == 0))
            {
                return;
            }

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
                    DEBUGGER.debug("Account: {}", (Object) account);
                }

                List<Object> accountDetail = manager.loadUserAccount(account[0]);

                if (DEBUG)
                {
                    DEBUGGER.debug("List<Object>: {}", accountDetail);
                }

                try
                {
                    Email email = new SimpleEmail();
                    email.setHostName((String) jobData.get("mailHost"));
                    email.setSmtpPort(Integer.parseInt((String) jobData.get("portNumber")));

                    if ((Boolean) jobData.get("isSecure"))
                    {
                        email.setSSLOnConnect(true);
                    }

                    if ((Boolean) jobData.get("isAuthenticated"))
                    {
                        email.setAuthenticator(new DefaultAuthenticator((String) jobData.get("username"),
                                PasswordUtils.decryptText(
                                        (String) jobData.get("password"),
                                        ((String) jobData.get("salt")).length(),
                                        secConfig.getEncryptionAlgorithm(),
                                        secConfig.getEncryptionInstance(),
                                        systemConfig.getEncoding())));
                    }

                    email.setFrom((String) jobData.get("emailAddr"));
                    email.addTo((String) accountDetail.get(6));
                    email.setSubject((String) jobData.get("messageSubject"));
                    email.setMsg(String.format((String) jobData.get("messageBody"),
                            (String) accountDetail.get(4)));
                    
                    if (DEBUG)
                    {
                        DEBUGGER.debug("SimpleEmail: {}", email);
                    }

                    email.send();
                }
                catch (EmailException ex)
                {
                    ERROR_RECORDER.error(ex.getMessage(), ex);
                }
            }
        }
        catch (UserManagementException umx)
        {
            ERROR_RECORDER.error(umx.getMessage(), umx);
        }
    }
}
