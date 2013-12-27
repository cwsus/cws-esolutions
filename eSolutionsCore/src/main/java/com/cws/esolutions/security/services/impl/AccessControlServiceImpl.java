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
package com.cws.esolutions.security.services.impl;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.security.services.impl
 * File: AccessControlServiceImpl.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.List;
import java.net.InetAddress;
import java.sql.SQLException;
import java.net.UnknownHostException;
import org.apache.commons.lang.StringUtils;

import com.cws.esolutions.security.enums.Role;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.core.processors.enums.ServerType;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.processors.enums.ControlType;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.security.services.enums.AdminControlType;
import com.cws.esolutions.security.dao.usermgmt.enums.SearchRequestType;
import com.cws.esolutions.security.processors.dto.AccountControlRequest;
import com.cws.esolutions.security.processors.dto.AccountControlResponse;
import com.cws.esolutions.security.services.interfaces.IAccessControlService;
import com.cws.esolutions.security.processors.impl.AccountControlProcessorImpl;
import com.cws.esolutions.security.processors.exception.AccountControlException;
import com.cws.esolutions.security.processors.interfaces.IAccountControlProcessor;
import com.cws.esolutions.security.services.exception.AccessControlServiceException;
/**
 * @see com.cws.esolutions.security.services.interfaces.IAccessControlService
 */
public class AccessControlServiceImpl implements IAccessControlService
{
    /**
     * @see com.cws.esolutions.security.services.interfaces.IAccessControlService#isUserAuthorizedForService(com.cws.esolutions.security.dto.UserAccount, java.lang.String)
     */
    @Override
    public boolean isUserAuthorizedForService(final UserAccount userAccount, final String serviceGuid) throws AccessControlServiceException
    {
        final String methodName = IAccessControlService.CNAME + "#isUserAuthorizedForService(final UserAccount userAccount, final String serviceGuid) throws AccessControlServiceException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("serviceGuid: {}", serviceGuid);
        }

        boolean isUserAuthorized = false;

        switch (userAccount.getRole())
        {
            case SITEADMIN:
                isUserAuthorized = true;

                break;
            default:
                try
                {
                    isUserAuthorized = sqlServiceDAO.verifyServiceForUser(userAccount.getGuid(), serviceGuid);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("isUserAuthorized: {}", isUserAuthorized);
                    }
                }
                catch (SQLException sqx)
                {
                    ERROR_RECORDER.error(sqx.getMessage(), sqx);

                    throw new AccessControlServiceException(sqx.getMessage(), sqx);
                }

                break;
        }

        return isUserAuthorized;
    }

    /**
     * @see com.cws.esolutions.security.services.interfaces.IAccessControlService#isEmailAuthorized(java.lang.String, java.lang.String[], boolean)
     */
    @Override
    public boolean isEmailAuthorized(final String sender, final String[] sources, final boolean isException) throws AccessControlServiceException
    {
        final String methodName = IAccessControlService.CNAME + "#isEmailAuthorized(final String sender, final String[] sources) throws AccessControlServiceException";

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
                    List<String[]> approvedSources = serverDAO.getServersByAttribute(ServerType.MAILSERVER.name(), 0);

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
            request.setSearchType(SearchRequestType.ALL);
            request.setIsLogonRequest(false);

            if (DEBUG)
            {
                DEBUGGER.debug("AccountControlRequest: {}", request);
            }

            IAccountControlProcessor processor = new AccountControlProcessorImpl();
            response = processor.searchAccounts(request);

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
                        isAuthorized = isUserAuthorizedForService(user, EMAIL_SVC_ID);

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

            throw new AccessControlServiceException(acx.getMessage(), acx);
        }
        catch (AccessControlServiceException ucsx)
        {
            ERROR_RECORDER.error(ucsx.getMessage(), ucsx);

            throw new AccessControlServiceException(ucsx.getMessage(), ucsx);
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new AccessControlServiceException(sqx.getMessage(), sqx);
        }

        return isAuthorized;
    }

    /**
     * @see com.cws.esolutions.security.services.interfaces.IAccessControlService#accessControlService(com.cws.esolutions.security.dto.UserAccount)
     */
    @Override
    public boolean accessControlService(final UserAccount userAccount)
    {
        final String methodName = IAccessControlService.CNAME + "#IAccessControlService(final UserAccount userAccount) throws AccessControlServiceException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("UserAccount", userAccount);
        }

        if (StringUtils.isNotEmpty(userAccount.getGuid()))
        {
            if ((userAccount.getRole() == Role.ADMIN) || (userAccount.getRole() == Role.SITEADMIN))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * @see com.cws.esolutions.security.services.interfaces.IAccessControlService#accessControlService(com.cws.esolutions.security.dto.UserAccount, com.cws.esolutions.security.services.enums.AdminControlType)
     */
    @Override
    public boolean accessControlService(final UserAccount userAccount, final AdminControlType controlType)
    {
        final String methodName = IAccessControlService.CNAME + "#IAccessControlService(final UserAccount userAccount, final AdminControlType controlType) throws AccessControlServiceException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("UserAccount", userAccount);
            DEBUGGER.debug("AdminControlType", controlType);
        }

        boolean isAuthorized = false;

        if (userAccount.getRole() == Role.SITEADMIN)
        {
            return true;
        }

        switch (controlType)
        {
            case SERVICE_ADMIN:
                if ((userAccount.getRole() == Role.ADMIN) || (userAccount.getRole() == Role.SERVICEADMIN))
                {
                    isAuthorized = true;
                }

                break;
            case SERVICE_REQUEST:
                if (userAccount.getRole() == Role.ADMIN)
                {
                    isAuthorized = true;
                }

                break;
            case USER_ADMIN:
                if ((userAccount.getRole() == Role.ADMIN) || (userAccount.getRole() == Role.USERADMIN))
                {
                    isAuthorized = true;
                }

                break;
        }

        return isAuthorized;
    }
}
