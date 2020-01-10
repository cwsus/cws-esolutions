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
 * cws-khuntly           11/23/2008 22:39:20             Created.
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
    /**
     * @see com.cws.esolutions.security.services.interfaces.IAccessControlService#isUserAuthorized(AccessControlServiceRequest) throws AccessControlServiceException
     */
    public AccessControlServiceResponse isUserAuthorized(final AccessControlServiceRequest request) throws AccessControlServiceException
    {
        final String methodName = IAccessControlService.CNAME + "#isUserAuthorized(final AccessControlServiceRequest request) throws AccessControlServiceException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AccessControlServiceRequest: {}", request);
        }

        final UserAccount userAccount = request.getUserAccount();
        final String userServiceId = request.getServiceGuid();

        AccessControlServiceResponse response = new AccessControlServiceResponse();

        if (secConfig.getEnableSecurity())
        {
            for (String group : userAccount.getGroups())
            {
                if (DEBUG)
                {
                    DEBUGGER.debug("UserGroup: {}", group);
                }
    
                try
                {
                    List<String> services = ref.listServicesForGroup(group);
    
                    if (DEBUG)
                    {
                        DEBUGGER.debug("List<String>: {}", services);
                    }
    
                    if (services.contains(userServiceId))
                    {
                        response.setIsUserAuthorized(Boolean.TRUE);
                    }
                }
                catch (SQLException sqx)
                {
                    ERROR_RECORDER.error(sqx.getMessage(), sqx);

                    response.setIsUserAuthorized(Boolean.FALSE);
                }
            }
        }
        else
        {
            response.setIsUserAuthorized(Boolean.TRUE);
        }

        return response;
    }
}
