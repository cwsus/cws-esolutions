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
package com.cws.esolutions.security.processors.impl;
import java.util.ArrayList;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.processors.impl
 * File: AccountChangeProcessorImpl.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.processors.dto.AuditEntry;
import com.cws.esolutions.security.processors.enums.AuditType;
import com.cws.esolutions.security.processors.dto.AuditRequest;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.security.processors.dto.AccountSearchRequest;
import com.cws.esolutions.security.processors.dto.AccountSearchResponse;
import com.cws.esolutions.security.services.dto.AccessControlServiceRequest;
import com.cws.esolutions.security.services.dto.AccessControlServiceResponse;
import com.cws.esolutions.security.processors.exception.AuditServiceException;
import com.cws.esolutions.security.processors.exception.AccountSearchException;
import com.cws.esolutions.security.processors.interfaces.IAccountSearchProcessor;
import com.cws.esolutions.security.dao.usermgmt.exception.UserManagementException;
import com.cws.esolutions.security.services.exception.AccessControlServiceException;
/**
 * @see com.cws.esolutions.security.processors.interfaces.IAccountChangeProcessor
 */
public class AccountSearchProcessorImpl implements IAccountSearchProcessor
{
    /**
     * @see com.cws.esolutions.security.processors.interfaces.IAccountResetProcessor#findUserAccount(com.cws.esolutions.security.processors.dto.AccountResetRequest)
     */
    public AccountSearchResponse findUserAccount(final AccountSearchRequest request) throws AccountSearchException
    {
        final String methodName = AccountSearchProcessorImpl.CNAME + "#findUserAccount(final AccountSearchRequest request) throws AccountSearchException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AccountResetRequest: {}", request);
        }

        List<UserAccount> responseData = null;
        AccountSearchResponse response = new AccountSearchResponse();

        final RequestHostInfo reqInfo = request.getHostInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
        	List<String[]> userList = userManager.searchUsers(request.getSearchTerms());

            if (DEBUG)
            {
                DEBUGGER.debug("List<String[]>: {}", userList);
            }

            if ((Objects.isNull(userList)) || (userList.size() == 0))
            {
                response.setRequestStatus(SecurityRequestStatus.FAILURE);

                return response;
            }

            responseData = new ArrayList<UserAccount>();

            for (String[] data : userList)
            {
            	if (DEBUG)
            	{
            		DEBUGGER.debug("data: {}", (Object) data);
            	}

            	UserAccount foundAccount = new UserAccount();

            	if (data.length > 1)
            	{
                	foundAccount.setGuid(data[0]);
                	foundAccount.setUsername(data[1]);
            	}
            	else
            	{
                	foundAccount.setGuid(data[0]);
            	}

                if (DEBUG)
                {
                    DEBUGGER.debug("foundAccount: {}", foundAccount);
                }

                responseData.add(foundAccount);
            }

            response.setRequestStatus(SecurityRequestStatus.SUCCESS);
            response.setUserList(responseData);
        }
        catch (final UserManagementException umx)
        {
            ERROR_RECORDER.error(umx.getMessage(), umx);

            throw new AccountSearchException(umx.getMessage(), umx);
        }
        finally
        {
        	if (secConfig.getPerformAudit())
        	{
	            // audit
	            try
	            {
	                AuditEntry auditEntry = new AuditEntry();
	                auditEntry.setHostInfo(reqInfo);
	                auditEntry.setAuditType(AuditType.LOADSECURITY);
	                auditEntry.setUserAccount(request.getUserAccount());
	                auditEntry.setAuthorized(Boolean.TRUE);
	                auditEntry.setApplicationId(request.getApplicationId());
	                auditEntry.setApplicationName(request.getApplicationName());
	
	                if (DEBUG)
	                {
	                    DEBUGGER.debug("AuditEntry: {}", auditEntry);
	                }
	
	                AuditRequest auditRequest = new AuditRequest();
	                auditRequest.setAuditEntry(auditEntry);
	
	                if (DEBUG)
	                {
	                    DEBUGGER.debug("AuditRequest: {}", auditRequest);
	                }
	
	                auditor.auditRequest(auditRequest);
	            }
	            catch (final AuditServiceException asx)
	            {
	                ERROR_RECORDER.error(asx.getMessage(), asx);
	            }
        	}
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.security.processors.interfaces.IAccountControlProcessor#searchAccounts(com.cws.esolutions.security.processors.dto.AccountControlRequest)
     */
    public AccountSearchResponse searchAccounts(final AccountSearchRequest request) throws AccountSearchException
    {
        final String methodName = AccountSearchProcessorImpl.CNAME + "#searchAccounts(final AccountSearchRequest request) throws AccountSearchException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AccountControlRequest: {}", request);
        }

        List<UserAccount> userAccounts = new ArrayList<UserAccount>();
        AccountSearchResponse response = new AccountSearchResponse();

        final RequestHostInfo reqInfo = request.getHostInfo();
        final UserAccount userAccount = request.getUserAccount();

        if (DEBUG)
        {
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount: {}", userAccount);
        }

        try
        {
            // this will require admin and service authorization
            AccessControlServiceRequest accessRequest = new AccessControlServiceRequest();
            accessRequest.setUserAccount(userAccount);

            if (DEBUG)
            {
                DEBUGGER.debug("AccessControlServiceRequest: {}", accessRequest);
            }

            AccessControlServiceResponse accessResponse = accessControl.isUserAuthorized(accessRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("AccessControlServiceResponse accessResponse: {}", accessResponse);
            }

            if (!(accessResponse.getIsUserAuthorized()))
            {
                // unauthorized
                response.setRequestStatus(SecurityRequestStatus.UNAUTHORIZED);

                // audit
                try
                {
                    AuditEntry auditEntry = new AuditEntry();
                    auditEntry.setHostInfo(reqInfo);
                    auditEntry.setAuditType(AuditType.SEARCHACCOUNTS);
                    auditEntry.setUserAccount(userAccount);
                    auditEntry.setAuthorized(Boolean.FALSE);
                    auditEntry.setApplicationId(request.getApplicationId());
                    auditEntry.setApplicationName(request.getApplicationName());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("AuditEntry: {}", auditEntry);
                    }

                    AuditRequest auditRequest = new AuditRequest();
                    auditRequest.setAuditEntry(auditEntry);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("AuditRequest: {}", auditRequest);
                    }

                    auditor.auditRequest(auditRequest);
                }
                catch (final AuditServiceException asx)
                {
                    ERROR_RECORDER.error(asx.getMessage(), asx);
                }

                return response;
            }

            List<String[]> userList = userManager.searchUsers(request.getSearchTerms());

	        if (DEBUG)
	        {
	        	DEBUGGER.debug("userList: {}", userList);
	        }

            if ((userList != null) && (userList.size() != 0))
            {
                for (Object[] userData : userList)
                {
                	if (DEBUG)
                	{
                		DEBUGGER.debug("userData: {}", userData);
                	}

                	if (StringUtils.equals(userAccount.getGuid(), (String) userData[0]))
                	{
                		continue;
                	}

            		UserAccount userInfo = new UserAccount();
            		userInfo.setGuid((String) userData[0]);
            		userInfo.setUsername((String) userData[1]);

            		if (DEBUG)
            		{
            			DEBUGGER.debug("UserAccount: {}", userInfo);
            		}

            		userAccounts.add(userInfo);

                    if (DEBUG)
                    {
                    	DEBUGGER.debug("userAccounts: {}", userAccounts);
                    }
                }

                if (userAccounts.size() == 0)
                {
                	response.setRequestStatus(SecurityRequestStatus.FAILURE);
                }
                else
                {
                	response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                    response.setUserList(userAccounts);
                }
            }
            else
            {
            	throw new AccountSearchException("Failed to load account for the given information.");
            }
        }
        catch (final UserManagementException umx)
        {
            ERROR_RECORDER.error(umx.getMessage(), umx);

            throw new AccountSearchException(umx.getMessage(), umx);
        }
        catch (final AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);

            throw new AccountSearchException(acsx.getMessage(), acsx);
        }
        finally
        {
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.SEARCHACCOUNTS);
                auditEntry.setUserAccount(userAccount);
                auditEntry.setAuthorized(Boolean.TRUE);
                auditEntry.setApplicationId(request.getApplicationId());
                auditEntry.setApplicationName(request.getApplicationName());

                if (DEBUG)
                {
                    DEBUGGER.debug("AuditEntry: {}", auditEntry);
                }

                AuditRequest auditRequest = new AuditRequest();
                auditRequest.setAuditEntry(auditEntry);

                if (DEBUG)
                {
                    DEBUGGER.debug("AuditRequest: {}", auditRequest);
                }

                auditor.auditRequest(auditRequest);
            }
            catch (final AuditServiceException asx)
            {
                ERROR_RECORDER.error(asx.getMessage(), asx);
            }
        }

        return response;
    }
}
