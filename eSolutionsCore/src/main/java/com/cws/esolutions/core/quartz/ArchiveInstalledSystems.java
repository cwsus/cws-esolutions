/*
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
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.quartz
 * File: CheckEmailMessages.java
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Jan 4, 2013 3:36:54 PM
 *     Created.
 */
import java.util.List;
import org.quartz.Job;
import org.slf4j.Logger;
import java.sql.SQLException;
import org.slf4j.LoggerFactory;
import org.quartz.JobExecutionContext;

import com.cws.esolutions.core.CoreServiceConstants;
import com.cws.esolutions.core.dao.impl.ServerDataDAOImpl;
import com.cws.esolutions.core.dao.interfaces.IServerDataDAO;
/**
 * Archival process for installed systems that have been retired. This will
 * move those systems into the historical database and remove them from the
 * current - this is done as housekeeping/cleanup.
 *
 * @author khuntly
 * @version 1.0
 */
public class ArchiveInstalledSystems implements Job
{
    private static final String CNAME = ArchiveInstalledSystems.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(CoreServiceConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(CoreServiceConstants.ERROR_LOGGER + ArchiveInstalledSystems.CNAME);

    public ArchiveInstalledSystems()
    {
        final String methodName = ArchiveInstalledSystems.CNAME + "#ArchiveInstalledSystems()#Constructor";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }
    }

    @Override
    public void execute(final JobExecutionContext jec)
    {
        final String methodName = ArchiveInstalledSystems.CNAME + "#execute(final JobExecutionContext jec)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("JobExecutionContext: {}", jec);
        }

        try
        {
            // this is NOT going to route through the processor.
            // its going to use the DAO directly
            IServerDataDAO dao = new ServerDataDAOImpl();
            List<String[]> serverList = dao.getRetiredServers(0);

            if (DEBUG)
            {
                DEBUGGER.debug("List<String[]>: {}", serverList);
            }

            if ((serverList != null) && (serverList.size() != 0))
            {
                for (String[] data : serverList)
                {
                    if (DEBUG)
                    {
                        DEBUGGER.debug("data: {}", data);
                    }

                    try
                    {
                        dao.archiveServerData(data[0]);
                    }
                    catch (SQLException sqx)
                    {
                        ERROR_RECORDER.error(sqx.getMessage(), sqx);
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
