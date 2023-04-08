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
package com.cws.esolutions.core.processors.impl;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.processors.exception
 * File: ApplicationManagementException.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.sql.SQLException;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.core.enums.CoreServicesStatus;
import com.cws.esolutions.core.processors.dto.ApplicationEnablementRequest;
import com.cws.esolutions.core.processors.dto.ApplicationEnablementResponse;
import com.cws.esolutions.core.processors.exception.ApplicationEnablementException;
import com.cws.esolutions.core.processors.interfaces.IApplicationEnablementProcessor;
/**
 * @see com.cws.esolutions.core.exception.CoreServicesException
 */
public class ApplicationEnablementProcessorImpl implements IApplicationEnablementProcessor
{
	private static final String CNAME = ApplicationEnablementProcessorImpl.class.getName();

	public ApplicationEnablementResponse isServiceEnabled(final ApplicationEnablementRequest request) throws ApplicationEnablementException
	{
        final String methodName = ApplicationEnablementProcessorImpl.CNAME + "#listApplications(final ApplicationEnablementRequest request) throws ApplicationEnablementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ApplicationEnablementRequest: {}", request);
        }

        ApplicationEnablementResponse response = new ApplicationEnablementResponse();

        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            boolean isEnabled = appDAO.isServiceEnabled(request.getRequestURI());

            if (DEBUG)
            {
            	DEBUGGER.debug("isEnabled: {}", isEnabled);
            }

            response.setIsEnabled(isEnabled);
            response.setRequestStatus(CoreServicesStatus.SUCCESS);
        }
        catch (final SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new ApplicationEnablementException(sqx.getMessage(), sqx);
        }
        
        return response;
	}
}
