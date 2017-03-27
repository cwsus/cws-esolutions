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
 * File: /
public class ResetRequestReaper.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.List;
import org.quartz.Job;
import org.slf4j.Logger;
import java.util.Calendar;
import java.sql.SQLException;
import org.slf4j.LoggerFactory;
import org.quartz.JobExecutionContext;

import com.cws.esolutions.security.SecurityServiceConstants;
import com.cws.esolutions.security.dao.reference.impl.UserSecurityInformationDAOImpl;
import com.cws.esolutions.security.dao.reference.interfaces.IUserSecurityInformationDAO;
/**
 * @see org.quartz.Job
 */
public class ResetRequestReaper implements Job
{
    private static final String CNAME = ResetRequestReaper.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityServiceConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger AUDIT_RECORDER = LoggerFactory.getLogger(SecurityServiceConstants.AUDIT_LOGGER + ResetRequestReaper.CNAME);
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityServiceConstants.ERROR_LOGGER + ResetRequestReaper.CNAME);

    /**
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    public void execute(final JobExecutionContext context)
    {
        final String methodName = ResetRequestReaper.CNAME + "#execute(final JobExecutionContext context)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("JobExecutionContext: {}", context);
        }

        try
        {
            IUserSecurityInformationDAO dao = new UserSecurityInformationDAOImpl();

            if (DEBUG)
            {
                DEBUGGER.debug("IUserSecurityInformationDAO: {}", dao);
            }

            List<String[]> activeResets = dao.listActiveResets();

            if (DEBUG)
            {
                DEBUGGER.debug("activeResets: {}", activeResets);
            }

            if ((activeResets == null) || (activeResets.size() == 0))
            {
                return;
            }

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, 30);
            long expiryTime = cal.getTimeInMillis();

            if (DEBUG)
            {
                DEBUGGER.debug("Calendar: {}", cal);
                DEBUGGER.debug("expiryTime: {}", expiryTime);
            }

            for (String[] reset : activeResets)
            {
                if (DEBUG)
                {
                    for (String str : reset)
                    {
                        DEBUGGER.debug("str : {}", str);
                    }
                }

                String commonName = reset[0];
                String resetKey = reset[1];
                long createTime = Long.valueOf(reset[2]);

                if (DEBUG)
                {
                    DEBUGGER.debug("commonName: {}", commonName);
                    DEBUGGER.debug("resetKey: {}", resetKey);
                    DEBUGGER.debug("createTime: {}", createTime);
                }

                if (createTime <= expiryTime)
                {
                    AUDIT_RECORDER.info("Removing expired reset request: {}", resetKey);

                    boolean isComplete = dao.removeResetData(commonName, resetKey);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("isComplete: {}", isComplete);
                    }

                    if (!(isComplete))
                    {
                        ERROR_RECORDER.error("Failed to remove expired reset request " + resetKey);
                    }
                }
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);
        }
    }
}
