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
package com.cws.esolutions.utility.services.impl;
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
import org.apache.commons.lang3.StringUtils;

import com.cws.esolutions.utility.services.dto.AccessControlServiceRequest;
import com.cws.esolutions.utility.services.dto.AccessControlServiceResponse;
import com.cws.esolutions.utility.services.interfaces.IAccessControlService;
import com.cws.esolutions.utility.services.exception.AccessControlServiceException;
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

        boolean isAuthorized = false;
        AccessControlServiceResponse response = new AccessControlServiceResponse();

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AccessControlServiceRequest: {}", request);
        }

        List<String> accountData = request.getUserAccount();

        if (DEBUG)
        {
        	DEBUGGER.debug("UserAccount: {}", accountData);
        }

        isAuthorized = StringUtils.equals(accountData.get(0), "SITE_ADMIN");
        response.setIsUserAuthorized(isAuthorized);

        return response;
    }
}
