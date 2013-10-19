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

import java.util.List;
import org.quartz.Job;
import org.slf4j.Logger;
import java.util.Calendar;
import org.slf4j.LoggerFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.cws.esolutions.core.Constants;
import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager;
import com.cws.esolutions.security.dao.usermgmt.factory.UserManagerFactory;
import com.cws.esolutions.security.dao.usermgmt.exception.UserManagementException;
/**
 * eSolutionsCore
 * com.cws.esolutions.security.quartz
 * IdleAccountLocker.java
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
 * 35033355 @ Jul 10, 2013 1:57:45 PM
 *     Created.
 */
public class IdleAccountLocker implements Job
{
    private static final String CNAME = IdleAccountLocker.class.getName();
    private static final SecurityServiceBean bean = SecurityServiceBean.getInstance();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + IdleAccountLocker.CNAME);

    public IdleAccountLocker()
    {
        final String methodName = IdleAccountLocker.CNAME + "#PasswordExpirationNotifier()#Constructor";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }
    }

    @Override
    public void execute(final JobExecutionContext context) throws JobExecutionException
    {
        final String methodName = IdleAccountLocker.CNAME + "#execute(final JobExecutionContext context)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("JobExecutionContext: {}", context);
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
                    long lastLogin = Long.valueOf(account[8]);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("commonName: {}", commonName);
                        DEBUGGER.debug("userName: {}", userName);
                        DEBUGGER.debug("lastLogin: {}", lastLogin);
                    }

                    if (lastLogin >= expiryTime)
                    {
                        boolean isComplete = manager.modifyUserSuspension(userName, commonName, true);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("isComplete: {}", isComplete);
                        }

                        if (!(isComplete))
                        {
                            ERROR_RECORDER.error("Failed to suspend account " + userName);
                        }
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
