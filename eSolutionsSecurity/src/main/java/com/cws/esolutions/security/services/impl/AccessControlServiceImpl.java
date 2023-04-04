/*
 * Copyright (c) 2009 - 2020 CaspersBox Web Services
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
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.services.impl
 * File: AccessControlServiceImpl.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.util.List;
import java.sql.SQLException;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.services.dto.AccessControlServiceRequest;
import com.cws.esolutions.security.services.dto.AccessControlServiceResponse;
import com.cws.esolutions.security.services.interfaces.IAccessControlService;
import com.cws.esolutions.security.services.exception.AccessControlServiceException;
/**
 * @see com.cws.esolutions.security.services.interfaces.IAccessControlService
 */
public class AccessControlServiceImpl implements IAccessControlService
{
    private static final String CNAME = AccessControlServiceImpl.class.getName();

    /**
     * @see com.cws.esolutions.security.services.interfaces.IAccessControlService#isUserAuthorized(AccessControlServiceRequest) throws AccessControlServiceException
     */
    public AccessControlServiceResponse isUserAuthorized(final AccessControlServiceRequest request) throws AccessControlServiceException
    {
        final String methodName = AccessControlServiceImpl.CNAME + "#isUserAuthorized(final AccessControlServiceRequest request) throws AccessControlServiceException";

        AccessControlServiceResponse response = new AccessControlServiceResponse();

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AccessControlServiceRequest: {}", request);
        }

        final UserAccount userAccount = request.getUserAccount();

        if (DEBUG)
        {
        	DEBUGGER.debug("UserAccount: {}", userAccount);
        }

    	switch (userAccount.getUserRole())
    	{
        	case SITE_ADMIN:
        		response.setIsUserAuthorized(Boolean.TRUE);

        		break;
        	default:
                try
                {
                	List<String> groupList = userSec.getUserGroups(userAccount.getGuid(), userAccount.getUsername());

                	if (DEBUG)
                	{
                		DEBUGGER.debug("groupList: {}", groupList);
                	}

                	for (String group : groupList)
                	{
                		if (DEBUG)
                		{
                			DEBUGGER.debug("group: {}", group);
                		}
                	}
                }
                catch (SQLException sqx)
                {
                	ERROR_RECORDER.error(sqx.getMessage(), sqx);
                }

				break;
        }

        return response;
    }
}
