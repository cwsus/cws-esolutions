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
package com.cws.esolutions.security.access.control.impl;

import java.util.List;
import java.net.InetAddress;
import java.sql.SQLException;
import java.net.UnknownHostException;
import org.apache.commons.lang.StringUtils;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.core.processors.enums.ServerType;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.processors.enums.ControlType;
import com.cws.esolutions.security.processors.enums.ModificationType;
import com.cws.esolutions.security.dao.usermgmt.enums.SearchRequestType;
import com.cws.esolutions.security.processors.dto.AccountControlRequest;
import com.cws.esolutions.security.processors.dto.AccountControlResponse;
import com.cws.esolutions.security.processors.exception.AccountControlException;
import com.cws.esolutions.security.access.control.interfaces.IEmailControlService;
import com.cws.esolutions.security.access.control.exception.UserControlServiceException;
import com.cws.esolutions.security.access.control.exception.EmailControlServiceException;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.security.processors.impl
 * File: FileSecurityProcessorImpl.java
 *
 * History
 * ----------------------------------------------------------------------------
 * 35033355 @ Jul 12, 2013 3:04:41 PM
 *     Created.
 */
/**
 * @see com.cws.esolutions.security.processors.interfaces.IFileSecurityProcessor
 */
public class EmailControlServiceImpl implements IEmailControlService
{
    /**
     * @see com.cws.esolutions.security.access.control.interfaces.IEmailControlService#isEmailAuthorized(java.lang.String, java.lang.String[], boolean)
     */
    @Override
    public boolean isEmailAuthorized(final String sender, final String[] sources, final boolean isException) throws EmailControlServiceException
    {
        final String methodName = IEmailControlService.CNAME + "#isEmailAuthorized(final String sender, final String[] sources) throws EmailControlServiceException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("sender: {}", sender);

            for (String str : sources)
            {
                DEBUGGER.debug("Source: {}", str);
            }
        }

        boolean isAuthorized = false;
        List<UserAccount> userList = null;
        AccountControlRequest request = null;
        AccountControlResponse response = null;

        try
        {
            for (String str : sources)
            {
                if (DEBUG)
                {
                    DEBUGGER.debug(str);
                }

                if (!(isException))
                {
                    int errCount = 0;
                    List<Object[]> approvedSources = serverDAO.getServersByAttribute(ServerType.MAILSERVER.name(), 0);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("approvedSources: {}", approvedSources);
                    }

                    if ((approvedSources != null) && (approvedSources.size() != 0))
                    {
                        for (Object[] entry : approvedSources)
                        {
                            if (DEBUG)
                            {
                                for (Object entr : entry)
                                {
                                    DEBUGGER.debug("entry: {}", entr);
                                }
                            }

                            if (StringUtils.equals((String) entry[5], str))
                            {
                                errCount = 0;
                                break;
                            }

                            errCount++;
                        }

                        if (errCount != 0)
                        {
                            return false;
                        }
                    }
                    else
                    {
                        // no approved sources, do not process
                        return false;
                    }
                }
            }

            // server is authorized, authorize sender
            // make sure the sender has an account
            RequestHostInfo hostInfo = new RequestHostInfo();

            try
            {
                hostInfo.setHostAddress(InetAddress.getLocalHost().getHostName());
                hostInfo.setHostName(InetAddress.getLocalHost().getHostAddress());
            }
            catch (UnknownHostException uhx)
            {
                ERROR_RECORDER.error(uhx.getMessage(), uhx);

                hostInfo.setHostAddress("localhost");
                hostInfo.setHostName("127.0.0.1");
            }

            if (DEBUG)
            {
                DEBUGGER.debug("RequestHostInfo: {}", hostInfo);
            }

            UserAccount searchUser = new UserAccount();
            searchUser.setEmailAddr(sender);

            if (DEBUG)
            {
                DEBUGGER.debug("UserAccount: {}", searchUser);
            }

            request = new AccountControlRequest();
            request.setHostInfo(hostInfo);
            request.setUserAccount(searchUser);
            request.setControlType(ControlType.LOOKUP);
            request.setModType(ModificationType.NONE);
            request.setSearchType(SearchRequestType.ALL);
            request.setIsLogonRequest(false);

            if (DEBUG)
            {
                DEBUGGER.debug("AccountControlRequest: {}", request);
            }

            response = controlMgr.searchAccounts(request);

            if (DEBUG)
            {
                DEBUGGER.debug("AccountControlResponse: {}", response);
            }

            if (response.getRequestStatus() == SecurityRequestStatus.SUCCESS)
            {
                userList = response.getUserList();

                if (DEBUG)
                {
                    DEBUGGER.debug("userList: {}", userList);
                }

                for (UserAccount user : userList)
                {
                    if (StringUtils.equals(user.getEmailAddr(), sender))
                    {
                        isAuthorized = userControl.isUserAuthorizedForService(user, EMAIL_SVC_ID);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("isAuthorized: {}", isAuthorized);
                        }

                        if (!(isAuthorized))
                        {
                            break;
                        }
                    }
                }
            }
        }
        catch (AccountControlException acx)
        {
            ERROR_RECORDER.error(acx.getMessage(), acx);

            throw new EmailControlServiceException(acx.getMessage(), acx);
        }
        catch (UserControlServiceException ucsx)
        {
            ERROR_RECORDER.error(ucsx.getMessage(), ucsx);

            throw new EmailControlServiceException(ucsx.getMessage(), ucsx);
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new EmailControlServiceException(sqx.getMessage(), sqx);
        }

        return isAuthorized;
    }
}
