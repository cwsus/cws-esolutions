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
package com.cws.esolutions.security.access.control.impl;

import java.sql.SQLException;

import com.cws.esolutions.security.access.control.interfaces.IUserControlService;
import com.cws.esolutions.security.access.control.exception.UserControlServiceException;
import com.cws.esolutions.security.dto.UserAccount;
/*
 * AdminControlServiceImpl
 * Determines if the provided user has the proper level of authority
 * to perform an administrative task.
 *
 * While not currently implemented in this class, the long-term vision
 * is to provide this as a service.
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * khuntly              Oct 31, 2009
 */
public class UserControlServiceImpl implements IUserControlService
{
    @Override
    public boolean isUserAuthorizedForService(final UserAccount userAccount, final String serviceGuid) throws UserControlServiceException
    {
        final String methodName = IUserControlService.CNAME + "#isUserAuthorizedForService(final UserAccount userAccount, final String serviceGuid) throws UserControlServiceException";

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

                    throw new UserControlServiceException(sqx.getMessage(), sqx);
                }

                break;
        }

        return isUserAuthorized;
    }

    @Override
    public boolean isUserAuthorizedForProject(final UserAccount userAccount, final String serviceGuid) throws UserControlServiceException
    {
        final String methodName = IUserControlService.CNAME + "#isUserAuthorizedForProject(final UserAccount userAccount, final String serviceGuid) throws UserControlServiceException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("serviceGuid: {}", serviceGuid);
        }

        boolean isUserAuthorized = false;

        try
        {
            isUserAuthorized = sqlServiceDAO.verifyProjectForUser(userAccount.getGuid(), serviceGuid);

            if (DEBUG)
            {
                DEBUGGER.debug("isUserAuthorized: {}", isUserAuthorized);
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new UserControlServiceException(sqx.getMessage(), sqx);
        }

        return isUserAuthorized;
    }
}
