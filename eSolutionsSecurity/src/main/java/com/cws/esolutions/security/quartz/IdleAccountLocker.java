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
package com.cws.esolutions.security.quartz;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.quartz
 * File: IdleAccountLocker.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.Date;
import java.util.List;
import org.quartz.Job;
import org.slf4j.Logger;
import java.util.Calendar;
import org.slf4j.LoggerFactory;
import org.quartz.JobExecutionContext;

import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.SecurityServiceConstants;
import com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager;
import com.cws.esolutions.security.dao.usermgmt.factory.UserManagerFactory;
import com.cws.esolutions.security.dao.usermgmt.exception.UserManagementException;
/**
 * @see org.quartz.Job
 */
public class IdleAccountLocker implements Job
{
    private static final String CNAME = IdleAccountLocker.class.getName();
    private static final SecurityServiceBean bean = SecurityServiceBean.getInstance();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityServiceConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityServiceConstants.ERROR_LOGGER + IdleAccountLocker.CNAME);

    public IdleAccountLocker()
    {
        final String methodName = IdleAccountLocker.CNAME + "#PasswordExpirationNotifier()#Constructor";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }
    }

    @Override
    public void execute(final JobExecutionContext context)
    {
        final String methodName = IdleAccountLocker.CNAME + "#execute(final JobExecutionContext context)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("JobExecutionContext: {}", context);
        }

        final int inactive = Integer.parseInt((String) context.getJobDetail().getJobDataMap().get("inactive"));

        if (DEBUG)
        {
            DEBUGGER.debug("inactive: {}", inactive);
        }

        try
        {
            // this is NOT going to route through the processor.
            // its going to use the DAO directly
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
            cal.add(Calendar.DATE, inactive);

            if (DEBUG)
            {
                DEBUGGER.debug("Calendar: {}", cal);
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

                String guid = (String) accountDetail.get(0);
                String username = (String) accountDetail.get(1);
                Date lastLogin = (Date) accountDetail.get(3);

                if (DEBUG)
                {
                    DEBUGGER.debug("String: {}", guid);
                    DEBUGGER.debug("String: {}", username);
                    DEBUGGER.debug("Date: {}", lastLogin);
                }

                if (cal.getTime().before(lastLogin))
                {
                    boolean isComplete = manager.modifyUserSuspension(username, guid, true);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("isComplete: {}", isComplete);
                    }

                    if (!(isComplete))
                    {
                        ERROR_RECORDER.error("Failed to suspend account " + username);
                    }
                }
            }
        }
        catch (UserManagementException umx)
        {
            ERROR_RECORDER.error(umx.getMessage(), umx);
        }
    }
}
