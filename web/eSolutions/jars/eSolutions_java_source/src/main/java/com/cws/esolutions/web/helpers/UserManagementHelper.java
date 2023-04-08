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
 *
 */
package com.cws.esolutions.web.helpers;
/*
 * Project: eSolutions_java_source
 * Package: com.cws.esolutions.web.controllers
 * File: LoginController.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.util.List;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.cws.esolutions.web.Constants;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.security.processors.dto.AccountControlRequest;
import com.cws.esolutions.security.processors.dto.AccountControlResponse;
import com.cws.esolutions.security.processors.impl.AccountControlProcessorImpl;
import com.cws.esolutions.security.processors.exception.AccountControlException;
import com.cws.esolutions.security.processors.interfaces.IAccountControlProcessor;
/**
 * @author cws-khuntly
 * @version 1.0
 */
public class UserManagementHelper
{
	private static final String CNAME = UserManagementHelper.class.getName();

    private static final Logger DEBUGGER = LogManager.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LogManager.getLogger(Constants.ERROR_LOGGER + CNAME);

	public static final UserAccount rebuildAccount(final RequestHostInfo reqInfo, final UserAccount requestingAccount, final String accountGuid, final List<String> callingAppData)
	{
        final String methodName = UserManagementHelper.CNAME + "#rebuildAccount(final RequestHostInfo reqInfo, final UserAccount requestingAccount, final String accountGuid, final List<String> callingAppData)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", reqInfo);
            DEBUGGER.debug("Value: {}", requestingAccount);
            DEBUGGER.debug("Value: {}", accountGuid);
            DEBUGGER.debug("Value: {}", callingAppData);
        }

        UserAccount returnedAccount = null;

        final IAccountControlProcessor processor = (IAccountControlProcessor) new AccountControlProcessorImpl();

        try
        {
        	UserAccount searchAccount = new UserAccount();
        	searchAccount.setGuid(accountGuid);

        	if (DEBUG)
        	{
        		DEBUGGER.debug("UserAccount: {}", searchAccount);
        	}

        	AccountControlRequest returnAccount = new AccountControlRequest();
            returnAccount.setHostInfo(reqInfo);
            returnAccount.setUserAccount(searchAccount);
	        returnAccount.setRequestor(requestingAccount);
	        returnAccount.setApplicationId(callingAppData.get(0));
	        returnAccount.setApplicationName(callingAppData.get(1));
	
	        if (DEBUG)
	        {
	            DEBUGGER.debug("AccountControlRequest: {}", returnAccount);
	        }
	
	        AccountControlResponse getReturnedAccount = processor.loadUserAccount(returnAccount);
	
	        if (DEBUG)
	        {
	            DEBUGGER.debug("AccountControlResponse: {}", getReturnedAccount);
	        }

	        switch (getReturnedAccount.getRequestStatus())
	        {
	            case SUCCESS:
	            	returnedAccount = getReturnedAccount.getUserAccount();

	            	if (DEBUG)
	            	{
	            		DEBUGGER.debug("UserAccount: {}", returnedAccount);
	            	}

	                break;
	            default:
	                break;
	        }
        }
        catch (final AccountControlException acx)
        {
            ERROR_RECORDER.error(acx.getMessage(), acx);
        }

        return returnedAccount;
	}
}
