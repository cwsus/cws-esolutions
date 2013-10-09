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

import org.apache.commons.lang.StringUtils;

import com.cws.esolutions.security.enums.Role;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.access.control.enums.AdminControlType;
import com.cws.esolutions.security.access.control.interfaces.IAdminControlService;
import com.cws.esolutions.security.access.control.exception.AdminControlServiceException;
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
public class AdminControlServiceImpl implements IAdminControlService
{
    /**
     * Determines if the requested user has the proper level of authority to
     * access the requested resource. This method needs a little work - its
     * long-term goal is to allow both a servlet-based method as well as a
     * portlet service. It should also query an applicable user datastore,
     * in the event the session data may have been tampered.
     *
     * @param userName
     * @param userGuid
     * @return boolean
     * @throws AuthorizationException
     * @author khuntly
     */
    @Override
    public boolean adminControlService(final UserAccount userAccount) throws AdminControlServiceException
    {
        final String methodName = IAdminControlService.CNAME + "#adminControlService(final UserAccount userAccount) throws AdminControlServiceException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("UserAccount", userAccount);
        }

        boolean isAuthorized = false;

        if (StringUtils.isNotEmpty(userAccount.getGuid()))
        {
            if ((userAccount.getRole() == Role.ADMIN) || (userAccount.getRole() == Role.SITEADMIN))
            {
                isAuthorized = true;
            }
        }

        return isAuthorized;
    }

    @Override
    public boolean adminControlService(final UserAccount userAccount, final AdminControlType controlType) throws AdminControlServiceException
    {
        final String methodName = IAdminControlService.CNAME + "#adminControlService(final UserAccount userAccount, final AdminControlType controlType) throws AdminControlServiceException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("UserAccount", userAccount);
            DEBUGGER.debug("AdminControlType", controlType);
        }

        boolean isAuthorized = false;

        if (userAccount.getRole() == Role.SITEADMIN)
        {
            isAuthorized = true;
        }
        else
        {
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
        }

        return isAuthorized;
    }
}
