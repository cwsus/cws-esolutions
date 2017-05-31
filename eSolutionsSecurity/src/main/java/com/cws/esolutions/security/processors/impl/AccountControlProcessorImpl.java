/*
 * Copyright (c) 2009 - 2017 CaspersBox Web Services
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
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.processors.impl
 * File: AccountControlProcessorImpl.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.Arrays;
import java.util.Calendar;
import java.util.ArrayList;
import java.sql.SQLException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.RandomStringUtils;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.utils.PasswordUtils;
import com.cws.esolutions.security.SecurityServiceConstants;
import com.cws.esolutions.security.processors.dto.AuditEntry;
import com.cws.esolutions.security.processors.enums.SaltType;
import com.cws.esolutions.security.processors.enums.AuditType;
import com.cws.esolutions.security.processors.dto.AuditRequest;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.security.processors.dto.AuthenticationData;
import com.cws.esolutions.security.processors.dto.AccountControlRequest;
import com.cws.esolutions.security.processors.dto.AccountControlResponse;
import com.cws.esolutions.security.processors.exception.AuditServiceException;
import com.cws.esolutions.security.processors.exception.AccountControlException;
import com.cws.esolutions.security.dao.usermgmt.exception.UserManagementException;
import com.cws.esolutions.security.processors.interfaces.IAccountControlProcessor;
import com.cws.esolutions.security.services.dto.AccessControlServiceRequest;
import com.cws.esolutions.security.services.dto.AccessControlServiceResponse;
import com.cws.esolutions.security.services.exception.AccessControlServiceException;
/**
 * @see com.cws.esolutions.security.processors.interfaces.IFileSecurityProcessor
 */
public class AccountControlProcessorImpl implements IAccountControlProcessor
{
    /**
     * @see com.cws.esolutions.security.processors.interfaces.IAccountControlProcessor#createNewUser(com.cws.esolutions.security.processors.dto.AccountControlRequest)
     */
    public AccountControlResponse createNewUser(final AccountControlRequest request) throws AccountControlException
    {
        final String methodName = IAccountControlProcessor.CNAME + "#createNewUser(final CreateUserRequest createReq) throws AccountControlException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AccountControlRequest: {}", request);
        }

        AccountControlResponse response = new AccountControlResponse();

        final RequestHostInfo reqInfo = request.getHostInfo();
        final UserAccount reqAccount = request.getRequestor();
        final UserAccount userAccount = request.getUserAccount();
        final AuthenticationData userSecurity = request.getUserSecurity();
        final String newUserSalt = RandomStringUtils.randomAlphanumeric(secConfig.getSaltLength());

        if (DEBUG)
        {
            DEBUGGER.debug("Requestor: {}", reqAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("AuthenticationData: {}", userSecurity);
        }

        try
        {
            // this will require admin and service authorization
        	AccessControlServiceRequest accessRequest = new AccessControlServiceRequest();
        	accessRequest.setUserAccount(userAccount);
        	accessRequest.setServiceGuid(request.getServiceId());

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
                    auditEntry.setAuditType(AuditType.CREATEUSER);
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
                catch (AuditServiceException asx)
                {
                    ERROR_RECORDER.error(asx.getMessage(), asx);
                }

                return response;
            }

            String userGuid = UUID.randomUUID().toString();

            if (DEBUG)
            {
                DEBUGGER.debug("Value: {}", userGuid);
            }

            int x = 0;

            while (true)
            {
                if (x == 10)
                {
                    throw new AccountControlException("Failed to generate a unique user GUID");
                }

                try
                {
                    userManager.validateUserAccount(userAccount.getUsername(), userGuid);

                    break;
                }
                catch (UserManagementException umx)
                {
                    ERROR_RECORDER.error(umx.getMessage(), umx);

                    if (!(StringUtils.contains(umx.getMessage(), "UUID")))
                    {
                        response.setRequestStatus(SecurityRequestStatus.FAILURE);

                        return response;
                    }

                    userGuid = UUID.randomUUID().toString();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Value: {}", userGuid);
                    }

                    x++;

                    continue;
                }
            }

            // insert the user salt
            boolean isSaltInserted = userSec.addOrUpdateSalt(userGuid, newUserSalt, SaltType.LOGON.name());

            if (DEBUG)
            {
                DEBUGGER.debug("isSaltInserted: {}", isSaltInserted);
            }

            if (isSaltInserted)
            {
                String newPassword = PasswordUtils.encryptText(RandomStringUtils.randomAlphanumeric(secConfig.getPasswordMaxLength()), newUserSalt,
                        secConfig.getAuthAlgorithm(), secConfig.getIterations(),
                        secBean.getConfigData().getSystemConfig().getEncoding());

                List<String> accountData = new ArrayList<String>(
                    Arrays.asList(
                            userGuid,
                            userAccount.getUsername(),
                            newPassword,
                            String.valueOf(userAccount.isSuspended()),
                            userAccount.getSurname(),
                            userAccount.getGivenName(),
                            userAccount.getGivenName() + " " + userAccount.getSurname(), 
                            userAccount.getEmailAddr()));

                if (DEBUG)
                {
                    DEBUGGER.debug("accountData: {}", accountData);
                }

                boolean isUserCreated = userManager.addUserAccount(accountData,
                        new ArrayList<String>(Arrays.asList(Arrays.toString(userAccount.getGroups()))));

                if (DEBUG)
                {
                    DEBUGGER.debug("isUserCreated: {}", isUserCreated);
                }

                if (isUserCreated)
                {
                    response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                }
                else
                {
                    // failed to add the user to the repository
                    ERROR_RECORDER.error("Failed to add user to the userAccount repository");

                    response.setRequestStatus(SecurityRequestStatus.FAILURE);
                }
            }
            else
            {
                // failed to insert salt
                ERROR_RECORDER.error("Failed to provision new user: failed to insert the generated salt value");

                response.setRequestStatus(SecurityRequestStatus.FAILURE);
            }
        }
        catch (AccountControlException acx)
        {
            ERROR_RECORDER.error(acx.getMessage(), acx);

            throw new AccountControlException(acx.getMessage(), acx);
        }
        catch (AccessControlServiceException acx)
        {
            ERROR_RECORDER.error(acx.getMessage(), acx);

            throw new AccountControlException(acx.getMessage(), acx);
        }
        catch (UserManagementException umx)
        {
            ERROR_RECORDER.error(umx.getMessage(), umx);

            throw new AccountControlException(umx.getMessage(), umx);
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new AccountControlException(sqx.getMessage(), sqx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.CREATEUSER);
                auditEntry.setUserAccount(reqAccount);
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
            catch (AuditServiceException asx)
            {
                ERROR_RECORDER.error(asx.getMessage(), asx);
            }
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.security.processors.interfaces.IAccountControlProcessor#removeUserAccount(com.cws.esolutions.security.processors.dto.AccountControlRequest)
     */
    public AccountControlResponse removeUserAccount(final AccountControlRequest request) throws AccountControlException
    {
        final String methodName = IAccountControlProcessor.CNAME + "#removeUserAccount(final AccountControlRequest) throws AccountControlException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AccountControlRequest: {}", request);
        }

        AccountControlResponse response = new AccountControlResponse();

        final RequestHostInfo reqInfo = request.getHostInfo();
        final UserAccount reqAccount = request.getRequestor();
        final UserAccount userAccount = request.getUserAccount();

        if (DEBUG)
        {
            DEBUGGER.debug("UserAccount reqUser: {}", reqAccount);
            DEBUGGER.debug("UserAccount userAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            // this will require admin and service authorization
        	AccessControlServiceRequest accessRequest = new AccessControlServiceRequest();
        	accessRequest.setUserAccount(userAccount);
        	accessRequest.setServiceGuid(request.getServiceId());

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
                    auditEntry.setAuditType(AuditType.DELETEUSER);
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
                catch (AuditServiceException asx)
                {
                    ERROR_RECORDER.error(asx.getMessage(), asx);
                }

                return response;
            }

            // delete userAccount
            boolean isComplete = userManager.removeUserAccount(userAccount.getGuid());

            if (DEBUG)
            {
                DEBUGGER.debug("isComplete: {}", isComplete);
            }

            if (isComplete)
            {
                response.setRequestStatus(SecurityRequestStatus.SUCCESS);
            }
            else
            {
                response.setRequestStatus(SecurityRequestStatus.FAILURE);
            }
        }
        catch (AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);

            throw new AccountControlException(acsx.getMessage(), acsx);
        }
        catch (UserManagementException umx)
        {
            ERROR_RECORDER.error(umx.getMessage(), umx);

            throw new AccountControlException(umx.getMessage(), umx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.DELETEUSER);
                auditEntry.setUserAccount(reqAccount);
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
            catch (AuditServiceException asx)
            {
                ERROR_RECORDER.error(asx.getMessage(), asx);
            }
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.security.processors.interfaces.IAccountControlProcessor#modifyUserSuspension(com.cws.esolutions.security.processors.dto.AccountControlRequest)
     */
    public AccountControlResponse modifyUserSuspension(final AccountControlRequest request) throws AccountControlException
    {
        final String methodName = IAccountControlProcessor.CNAME + "#modifyUserSuspension(final AccountControlRequest) throws AccountControlException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AccountControlRequest: {}", request);
        }

        AccountControlResponse response = new AccountControlResponse();

        final RequestHostInfo reqInfo = request.getHostInfo();
        final UserAccount reqAccount = request.getRequestor();
        final UserAccount userAccount = request.getUserAccount();

        if (DEBUG)
        {
            DEBUGGER.debug("UserAccount reqUser: {}", reqAccount);
            DEBUGGER.debug("UserAccount userAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            // this will require admin and service authorization
        	AccessControlServiceRequest accessRequest = new AccessControlServiceRequest();
        	accessRequest.setUserAccount(userAccount);
        	accessRequest.setServiceGuid(request.getServiceId());

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
                    auditEntry.setAuditType(AuditType.SUSPENDUSER);
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
                catch (AuditServiceException asx)
                {
                    ERROR_RECORDER.error(asx.getMessage(), asx);
                }

                return response;
            }

            // we will only have a guid here - so we need to load the user
            List<Object> userData = userManager.loadUserAccount(userAccount.getGuid());

            if (DEBUG)
            {
                DEBUGGER.debug("List<Object>: {}", userData);
            }

            if ((userData != null) && (userData.size() != 0))
            {
                boolean isComplete = userManager.modifyUserSuspension((String) userData.get(1), userAccount.isSuspended());

                if (DEBUG)
                {
                    DEBUGGER.debug("isComplete: {}", isComplete);
                }

                if (isComplete)
                {
                    response.setUserAccount(userAccount);
                    response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                }
                else
                {
                    response.setRequestStatus(SecurityRequestStatus.FAILURE);
                }
            }
            else
            {
                ERROR_RECORDER.error("Failed to locate user for given GUID");

                response.setRequestStatus(SecurityRequestStatus.FAILURE);
            }
        }
        catch (AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);

            throw new AccountControlException(acsx.getMessage(), acsx);
        }
        catch (UserManagementException umx)
        {
            ERROR_RECORDER.error(umx.getMessage(), umx);

            throw new AccountControlException(umx.getMessage(), umx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.SUSPENDUSER);
                auditEntry.setUserAccount(reqAccount);
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
            catch (AuditServiceException asx)
            {
                ERROR_RECORDER.error(asx.getMessage(), asx);
            }
        }


        return response;
    }

    /**
     * @see com.cws.esolutions.security.processors.interfaces.IAccountControlProcessor#modifyUserRole(com.cws.esolutions.security.processors.dto.AccountControlRequest)
     */
    public AccountControlResponse modifyUserRole(final AccountControlRequest request) throws AccountControlException
    {
        final String methodName = IAccountControlProcessor.CNAME + "#modifyUserRole(final AccountControlRequest) throws AccountControlException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AccountControlRequest: {}", request);
        }

        AccountControlResponse response = new AccountControlResponse();

        final RequestHostInfo reqInfo = request.getHostInfo();
        final UserAccount reqAccount = request.getRequestor();
        final UserAccount userAccount = request.getUserAccount();

        if (DEBUG)
        {
            DEBUGGER.debug("UserAccount reqUser: {}", reqAccount);
            DEBUGGER.debug("UserAccount userAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            // this will require admin and service authorization
        	AccessControlServiceRequest accessRequest = new AccessControlServiceRequest();
        	accessRequest.setUserAccount(userAccount);
        	accessRequest.setServiceGuid(request.getServiceId());

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
                    auditEntry.setAuditType(AuditType.MODIFYUSER);
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
                catch (AuditServiceException asx)
                {
                    ERROR_RECORDER.error(asx.getMessage(), asx);
                }

                return response;
            }

            // we will only have a guid here - so we need to load the user
            List<Object> userData = userManager.loadUserAccount(userAccount.getGuid());

            if (DEBUG)
            {
                DEBUGGER.debug("List<Object>: {}", userData);
            }

            if ((userData != null) && (userData.size() != 0))
            {
                boolean isComplete = userManager.modifyUserGroups((String) userData.get(0), userAccount.getGroups());

                if (DEBUG)
                {
                    DEBUGGER.debug("isComplete: {}", isComplete);
                }

                if (isComplete)
                {
                    List<Object> resData = userManager.loadUserAccount((String) userData.get(0));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("List<Object>: {}", resData);
                    }

                    if ((resData != null) && (!(resData.isEmpty())))
                    {
                        UserAccount resAccount = new UserAccount();
                        resAccount.setGuid((String) userData.get(0));
                        resAccount.setUsername((String) userData.get(1));
                        resAccount.setGivenName((String) userData.get(2));
                        resAccount.setSurname((String) userData.get(3));
                        resAccount.setDisplayName((String) userData.get(4));
                        resAccount.setEmailAddr((String) userData.get(5));
                        resAccount.setPagerNumber((userData.get(6) == null) ? SecurityServiceConstants.NOT_SET : (String) userData.get(6));
                        resAccount.setTelephoneNumber((userData.get(7) == null) ? SecurityServiceConstants.NOT_SET : (String) userData.get(7));
                        resAccount.setFailedCount(((userData.get(9) == null) ? 0 : (Integer) userData.get(9)));
                        resAccount.setLastLogin(((userData.get(10) == null) ? new Date(1L) : new Date((Long) userData.get(10))));
                        resAccount.setExpiryDate(((userData.get(11) == null) ? new Date(System.currentTimeMillis()) : (Date) userData.get(11)));
                        resAccount.setSuspended(((userData.get(12) == null) ? Boolean.FALSE : (Boolean) userData.get(12)));
                        resAccount.setOlrSetup(((userData.get(13) == null) ? Boolean.FALSE : (Boolean) userData.get(13)));
                        resAccount.setOlrLocked(((userData.get(14) == null) ? Boolean.FALSE : (Boolean) userData.get(14)));

                        if (DEBUG)
                        {
                            DEBUGGER.debug("UserAccount: {}", resAccount);
                        }

                        response.setUserAccount(resAccount);
                    }
                    else
                    {
                        // if we have an issue re-loading the userAccount
                        // just put the existing one in. its something.
                        response.setUserAccount(userAccount);
                    }

                    response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                }
                else
                {
                    response.setRequestStatus(SecurityRequestStatus.FAILURE);
                }
            }
            else
            {
                ERROR_RECORDER.error("Failed to locate user for given GUID");

                response.setRequestStatus(SecurityRequestStatus.FAILURE);
            }
        }
        catch (AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);

            throw new AccountControlException(acsx.getMessage(), acsx);
        }
        catch (UserManagementException umx)
        {
            ERROR_RECORDER.error(umx.getMessage(), umx);

            throw new AccountControlException(umx.getMessage(), umx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.MODIFYUSER);
                auditEntry.setUserAccount(reqAccount);
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
            catch (AuditServiceException asx)
            {
                ERROR_RECORDER.error(asx.getMessage(), asx);
            }
        }


        return response;
    }

    /**
     * @see com.cws.esolutions.security.processors.interfaces.IAccountControlProcessor#modifyUserPassword(com.cws.esolutions.security.processors.dto.AccountControlRequest)
     */
    public AccountControlResponse modifyUserPassword(final AccountControlRequest request) throws AccountControlException
    {
        final String methodName = IAccountControlProcessor.CNAME + "#modifyUserPassword(final AccountControlRequest request) throws AccountControlException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AccountControlRequest: {}", request);
        }

        AccountControlResponse response = new AccountControlResponse();

        final Calendar calendar = Calendar.getInstance();
        final RequestHostInfo reqInfo = request.getHostInfo();
        final UserAccount reqAccount = request.getRequestor();
        final UserAccount userAccount = request.getUserAccount();

        calendar.add(Calendar.DATE, secConfig.getPasswordExpiration());

        if (DEBUG)
        {
            DEBUGGER.debug("Calendar: {}", calendar);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount: {}", reqAccount);
            DEBUGGER.debug("UserAccount: {}", userAccount);
        }

        try
        {
            // this will require admin and service authorization
        	AccessControlServiceRequest accessRequest = new AccessControlServiceRequest();
        	accessRequest.setUserAccount(userAccount);
        	accessRequest.setServiceGuid(request.getServiceId());

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
                    auditEntry.setAuditType(AuditType.CHANGEPASS);
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
                catch (AuditServiceException asx)
                {
                    ERROR_RECORDER.error(asx.getMessage(), asx);
                }

                return response;
            }

            // this is a reset request, so we need to do a few things
            // 1, we need to generate a unique id that we can email off
            // to the user, that we can then look up to confirm
            // then once we have that we can actually do the reset
            // first, change the existing password
            // 128 character values - its possible that the reset is
            // coming as a result of a possible compromise
            String tmpPassword = PasswordUtils.encryptText(RandomStringUtils.randomAlphanumeric(secConfig.getPasswordMaxLength()),
                    RandomStringUtils.randomAlphanumeric(secConfig.getSaltLength()),
                    secConfig.getAuthAlgorithm(), secConfig.getIterations(),
                    secBean.getConfigData().getSystemConfig().getEncoding());
            String tmpSalt = RandomStringUtils.randomAlphanumeric(secConfig.getSaltLength());

            if ((StringUtils.isNotEmpty(tmpPassword)) && (StringUtils.isNotEmpty(tmpSalt)))
            {
                // update the authentication datastore with the new password
                // we never show the user the password, we're only doing this
                // to prevent unauthorized access (or further unauthorized access)
                // we get a return code back but we aren't going to use it really
                boolean isComplete = userManager.modifyUserPassword(userAccount.getGuid(), tmpPassword);

                if (DEBUG)
                {
                    DEBUGGER.debug("isComplete: {}", isComplete);
                }

                // now generate a temporary id to stuff into the database
                // this will effectively replace the current salt value
                String resetId = RandomStringUtils.randomAlphanumeric(secConfig.getResetIdLength());
                String resetSms = RandomStringUtils.randomAlphanumeric(secConfig.getSmsCodeLength());

                if ((StringUtils.isNotEmpty(resetId)) && (StringUtils.isNotEmpty(resetSms)))
                {
                    isComplete = userSec.insertResetData(userAccount.getGuid(), resetId, ((secConfig.getSmsResetEnabled()) ? resetSms : null));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("isComplete: {}", isComplete);
                    }

                    if (isComplete)
                    {
                        response.setResetId(resetId);
                        response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                    }
                    else
                    {
                        ERROR_RECORDER.error("Unable to insert password identifier into database. Cannot continue.");

                        response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                    }
                }
                else
                {
                    ERROR_RECORDER.error("Unable to generate a unique identifier. Cannot continue.");

                    response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                }
            }
            else
            {
                ERROR_RECORDER.error("Failed to generate a temporary password. Cannot continue.");

                response.setRequestStatus(SecurityRequestStatus.SUCCESS);
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new AccountControlException(sqx.getMessage(), sqx);
        }
        catch (AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);

            throw new AccountControlException(acsx.getMessage(), acsx);
        }
        catch (UserManagementException umx)
        {
            ERROR_RECORDER.error(umx.getMessage(), umx);

            throw new AccountControlException(umx.getMessage(), umx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.RESETPASS);
                auditEntry.setUserAccount(reqAccount);
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
            catch (AuditServiceException asx)
            {
                ERROR_RECORDER.error(asx.getMessage(), asx);
            }
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.security.processors.interfaces.IAccountControlProcessor#modifyUserLockout(com.cws.esolutions.security.processors.dto.AccountControlRequest)
     */
    public AccountControlResponse modifyUserLockout(final AccountControlRequest request) throws AccountControlException
    {
        final String methodName = IAccountControlProcessor.CNAME + "#modifyUserLockout(final AccountControlRequest) throws AccountControlException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AccountControlRequest: {}", request);
        }

        AccountControlResponse response = new AccountControlResponse();

        final RequestHostInfo reqInfo = request.getHostInfo();
        final UserAccount reqAccount = request.getRequestor();
        final UserAccount userAccount = request.getUserAccount();

        if (DEBUG)
        {
            DEBUGGER.debug("UserAccount reqUser: {}", reqAccount);
            DEBUGGER.debug("UserAccount userAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            // this will require admin and service authorization
        	AccessControlServiceRequest accessRequest = new AccessControlServiceRequest();
        	accessRequest.setUserAccount(userAccount);
        	accessRequest.setServiceGuid(request.getServiceId());

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
                    auditEntry.setAuditType(AuditType.LOCKUSER);
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
                catch (AuditServiceException asx)
                {
                    ERROR_RECORDER.error(asx.getMessage(), asx);
                }

                return response;
            }

            // we will only have a guid here - so we need to load the user
            List<Object> userData = userManager.loadUserAccount(userAccount.getGuid());

            if (DEBUG)
            {
                DEBUGGER.debug("List<Object>: {}", userData);
            }

            if ((userData != null) && (userData.size() != 0))
            {
                boolean isComplete = userManager.modifyUserLock((String) userData.get(0), true, userAccount.getFailedCount());

                if (DEBUG)
                {
                    DEBUGGER.debug("isComplete: {}", isComplete);
                }

                if (isComplete)
                {
                    response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                }
            }
            else
            {
                ERROR_RECORDER.error("Failed to locate user for given GUID");

                response.setRequestStatus(SecurityRequestStatus.FAILURE);
            }
        }
        catch (AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);

            throw new AccountControlException(acsx.getMessage(), acsx);
        }
        catch (UserManagementException umx)
        {
            ERROR_RECORDER.error(umx.getMessage(), umx);

            throw new AccountControlException(umx.getMessage(), umx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.LOCKUSER);
                auditEntry.setUserAccount(reqAccount);
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
            catch (AuditServiceException asx)
            {
                ERROR_RECORDER.error(asx.getMessage(), asx);
            }
        }


        return response;
    }

    /**
     * @see com.cws.esolutions.security.processors.interfaces.IAccountControlProcessor#searchAccounts(com.cws.esolutions.security.processors.dto.AccountControlRequest)
     */
    public AccountControlResponse searchAccounts(final AccountControlRequest request) throws AccountControlException
    {
        final String methodName = IAccountControlProcessor.CNAME + "#searchAccounts(final AccountControlRequest request) throws AccountControlException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AccountControlRequest: {}", request);
        }

        AccountControlResponse response = new AccountControlResponse();

        final RequestHostInfo reqInfo = request.getHostInfo();
        final UserAccount reqAccount = request.getRequestor();
        final UserAccount userAccount = request.getUserAccount();

        if (DEBUG)
        {
            DEBUGGER.debug("UserAccount: {}", reqAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount: {}", userAccount);
        }

        try
        {
            // this will require admin and service authorization
        	AccessControlServiceRequest accessRequest = new AccessControlServiceRequest();
        	accessRequest.setUserAccount(userAccount);
        	accessRequest.setServiceGuid(request.getServiceId());

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
                catch (AuditServiceException asx)
                {
                    ERROR_RECORDER.error(asx.getMessage(), asx);
                }

                return response;
            }

            List<String[]> userList = userManager.searchUsers(userAccount.getEmailAddr());

            if (DEBUG)
            {
                DEBUGGER.debug("userList: {}", userList);
            }

            if ((userList != null) && (userList.size() != 0))
            {
                List<UserAccount> userAccounts = new ArrayList<UserAccount>();

                for (Object[] userData : userList)
                {
                    if (!(StringUtils.equals(reqAccount.getGuid(), (String) userData[0])))
                    {
                        UserAccount userInfo = new UserAccount();
                        userInfo.setGuid((String) userData[0]);
                        userInfo.setUsername((String) userData[1]);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("UserAccount: {}", userInfo);
                        }

                        userAccounts.add(userInfo);
                    }
                }

                if (DEBUG)
                {
                    DEBUGGER.debug("userAccounts: {}", userAccounts);
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
                response.setRequestStatus(SecurityRequestStatus.FAILURE);
            }
        }
        catch (UserManagementException umx)
        {
            ERROR_RECORDER.error(umx.getMessage(), umx);

            throw new AccountControlException(umx.getMessage(), umx);
        }
        catch (AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);

            throw new AccountControlException(acsx.getMessage(), acsx);
        }
        finally
        {
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.SEARCHACCOUNTS);
                auditEntry.setUserAccount(reqAccount);
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
            catch (AuditServiceException asx)
            {
                ERROR_RECORDER.error(asx.getMessage(), asx);
            }
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.security.processors.interfaces.IAccountControlProcessor#loadUserAccount(com.cws.esolutions.security.processors.dto.AccountControlRequest)
     */
    public AccountControlResponse loadUserAccount(final AccountControlRequest request) throws AccountControlException
    {
        final String methodName = IAccountControlProcessor.CNAME + "#loadUserAccount(final AccountControlRequest request) throws AccountControlException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AccountControlRequest: {}", request);
        }

        AccountControlResponse response = new AccountControlResponse();

        final RequestHostInfo reqInfo = request.getHostInfo();
        final UserAccount reqAccount = request.getRequestor();
        final UserAccount userAccount = request.getUserAccount();

        if (DEBUG)
        {
            DEBUGGER.debug("UserAccount: {}", reqAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount: {}", userAccount);
        }

        try
        {
            // this will require admin and service authorization
        	AccessControlServiceRequest accessRequest = new AccessControlServiceRequest();
        	accessRequest.setUserAccount(userAccount);
        	accessRequest.setServiceGuid(request.getServiceId());

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
                    auditEntry.setAuditType(AuditType.LOADACCOUNT);
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
                catch (AuditServiceException asx)
                {
                    ERROR_RECORDER.error(asx.getMessage(), asx);
                }

                return response;
            }

            List<Object> userData = userManager.loadUserAccount(userAccount.getGuid());

            if (DEBUG)
            {
                DEBUGGER.debug("List<Object>: {}", userData);
            }

            if ((userData != null) && (!(userData.isEmpty())))
            {
                UserAccount loadAccount = new UserAccount();
                loadAccount.setGuid((String) userData.get(0));
                loadAccount.setUsername((String) userData.get(1));
                loadAccount.setSurname((String) userData.get(2));
                loadAccount.setGivenName((String) userData.get(3));
                loadAccount.setEmailAddr((String) userData.get(4));
                loadAccount.setDisplayName((String) userData.get(5));
                loadAccount.setTelephoneNumber((userData.get(6) == null) ? SecurityServiceConstants.NOT_SET : (String) userData.get(6));

                if (userData.size() > 7)
                {
                    loadAccount.setManagerGuid((String) userData.get(7));
                    loadAccount.setManagerName((String) userData.get(8));
                }
                if (DEBUG)
                {
                    DEBUGGER.debug("UserAccount: {}", loadAccount);
                }

                response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                response.setUserAccount(loadAccount);
            }
            else
            {
                response.setRequestStatus(SecurityRequestStatus.FAILURE);
            }

            if (DEBUG)
            {
                DEBUGGER.debug("AccountControlResponse: {}", response);
            }
        }
        catch (UserManagementException umx)
        {
            ERROR_RECORDER.error(umx.getMessage(), umx);

            throw new AccountControlException(umx.getMessage(), umx);
        }
        catch (AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);

            throw new AccountControlException(acsx.getMessage(), acsx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.LOADACCOUNT);
                auditEntry.setUserAccount(reqAccount);
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
            catch (AuditServiceException asx)
            {
                ERROR_RECORDER.error(asx.getMessage(), asx);
            }
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.security.processors.interfaces.IAccountControlProcessor#listUserAccounts(com.cws.esolutions.security.processors.dto.AccountControlRequest)
     */
    public AccountControlResponse listUserAccounts(final AccountControlRequest request) throws AccountControlException
    {
        final String methodName = IAccountControlProcessor.CNAME + "#listUserAccounts(final AccountControlRequest request) throws AccountControlException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AccountControlRequest: {}", request);
        }

        AccountControlResponse response = new AccountControlResponse();

        final RequestHostInfo reqInfo = request.getHostInfo();
        final UserAccount reqAccount = request.getRequestor();
        final UserAccount userAccount = request.getUserAccount();

        if (DEBUG)
        {
            DEBUGGER.debug("UserAccount: {}", reqAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount: {}", userAccount);
        }

        try
        {
            // this will require admin and service authorization
        	AccessControlServiceRequest accessRequest = new AccessControlServiceRequest();
        	accessRequest.setUserAccount(userAccount);
        	accessRequest.setServiceGuid(request.getServiceId());

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
                    auditEntry.setAuditType(AuditType.LISTUSERS);
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
                catch (AuditServiceException asx)
                {
                    ERROR_RECORDER.error(asx.getMessage(), asx);
                }

                return response;
            }

            List<String[]> userList = userManager.listUserAccounts();

            if (DEBUG)
            {
                DEBUGGER.debug("userList: {}", userList);
            }

            if ((userList != null) && (userList.size() != 0))
            {
                List<UserAccount> userAccounts = new ArrayList<UserAccount>();

                for (String[] userData : userList)
                {
                    if (!(StringUtils.equals(reqAccount.getGuid(), userData[0])))
                    {
                        UserAccount userInfo = new UserAccount();
                        userInfo.setGuid(userData[0]);
                        userInfo.setUsername(userData[1]);
                        userInfo.setDisplayName(userData[2]);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("UserAccount: {}", userInfo);
                        }

                        userAccounts.add(userInfo);
                    }
                }

                if (DEBUG)
                {
                    DEBUGGER.debug("userAccounts: {}", userAccounts);
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
                response.setRequestStatus(SecurityRequestStatus.FAILURE);
            }
        }
        catch (UserManagementException umx)
        {
            ERROR_RECORDER.error(umx.getMessage(), umx);

            throw new AccountControlException(umx.getMessage(), umx);
        }
        catch (AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);

            throw new AccountControlException(acsx.getMessage(), acsx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.LISTUSERS);
                auditEntry.setUserAccount(reqAccount);
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
            catch (AuditServiceException asx)
            {
                ERROR_RECORDER.error(asx.getMessage(), asx);
            }
        }

        return response;
    }
}
