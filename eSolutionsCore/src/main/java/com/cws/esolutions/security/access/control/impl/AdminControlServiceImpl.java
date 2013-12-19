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
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.security.access.control.impl
 * File: AdminControlServiceImpl.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import org.apache.commons.lang.StringUtils;

import com.cws.esolutions.security.enums.Role;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.access.control.enums.AdminControlType;
import com.cws.esolutions.security.access.control.interfaces.IAdminControlService;
/**
 * @see com.cws.esolutions.security.access.control.interfaces.IAdminControlService
 */
public class AdminControlServiceImpl implements IAdminControlService
{
    /**
     * @see com.cws.esolutions.security.access.control.interfaces.IAdminControlService#adminControlService(com.cws.esolutions.security.dto.UserAccount)
     */
    @Override
    public boolean adminControlService(final UserAccount userAccount)
    {
        final String methodName = IAdminControlService.CNAME + "#adminControlService(final UserAccount userAccount) throws AdminControlServiceException";

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
     * @see com.cws.esolutions.security.access.control.interfaces.IAdminControlService#adminControlService(com.cws.esolutions.security.dto.UserAccount, com.cws.esolutions.security.access.control.enums.AdminControlType)
     */
    @Override
    public boolean adminControlService(final UserAccount userAccount, final AdminControlType controlType)
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
