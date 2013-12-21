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
package com.cws.esolutions.security.processors.impl;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.security.processors.impl
 * File: AccountControlProcessorImpl.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.Map;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Calendar;
import java.util.ArrayList;
import java.sql.SQLException;
import java.lang.reflect.Field;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.RandomStringUtils;

import com.cws.esolutions.security.enums.Role;
import com.cws.esolutions.security.enums.SaltType;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.dto.UserSecurity;
import com.cws.esolutions.security.SecurityConstants;
import com.cws.esolutions.security.utils.PasswordUtils;
import com.cws.esolutions.security.audit.dto.AuditEntry;
import com.cws.esolutions.security.audit.enums.AuditType;
import com.cws.esolutions.security.audit.dto.AuditRequest;
import com.cws.esolutions.security.audit.dto.AuditResponse;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.processors.enums.ControlType;
import com.cws.esolutions.security.dao.usermgmt.enums.SearchRequestType;
import com.cws.esolutions.security.processors.dto.AccountControlRequest;
import com.cws.esolutions.security.audit.exception.AuditServiceException;
import com.cws.esolutions.security.processors.dto.AccountControlResponse;
import com.cws.esolutions.security.access.control.enums.AdminControlType;
import com.cws.esolutions.security.keymgmt.exception.KeyManagementException;
import com.cws.esolutions.security.processors.exception.AccountControlException;
import com.cws.esolutions.security.dao.usermgmt.exception.UserManagementException;
import com.cws.esolutions.security.processors.interfaces.IAccountControlProcessor;
import com.cws.esolutions.security.access.control.exception.AdminControlServiceException;
/**
 * @see com.cws.esolutions.security.processors.interfaces.IFileSecurityProcessor
 */
public class AccountControlProcessorImpl implements IAccountControlProcessor
{
    /**
     * @see com.cws.esolutions.security.processors.interfaces.IAccountControlProcessor#createNewUser(com.cws.esolutions.security.processors.dto.AccountControlRequest)
     */
    @Override
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
        final UserSecurity userSecurity = request.getUserSecurity();
        final String newUserSalt = RandomStringUtils.randomAlphanumeric(secConfig.getSaltLength());
        final StringBuilder userDN = new StringBuilder()
            .append(authData.getUserId() + "=" + userAccount.getUsername() + ",")
            .append(authRepo.getRepositoryUserBase());

        if (DEBUG)
        {
            DEBUGGER.debug("Requestor: {}", reqAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("UserSecurity: {}", userSecurity);
            DEBUGGER.debug("userDN: {}", userDN);
        }

        try
        {
            boolean isAuthorized = adminControl.adminControlService(reqAccount, AdminControlType.USER_ADMIN);

            if (DEBUG)
            {
                DEBUGGER.debug("isAuthorized: {}", isAuthorized);
            }

            if (isAuthorized)
            {
                String userGuid = (StringUtils.isNotEmpty(userAccount.getGuid())) ? userAccount.getGuid() : UUID.randomUUID().toString();

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
                boolean isSaltInserted = userSec.addUserSalt(userGuid, newUserSalt, SaltType.LOGON.name());

                if (DEBUG)
                {
                    DEBUGGER.debug("isSaltInserted: {}", isSaltInserted);
                }

                if (isSaltInserted)
                {
                    String newPassword = PasswordUtils.encryptText(RandomStringUtils.randomAlphanumeric(secConfig.getPasswordMaxLength()), newUserSalt,
                            secConfig.getAuthAlgorithm(), secConfig.getIterations());

                    List<String> accountData = new ArrayList<>(
                        Arrays.asList(
                                userAccount.getUsername(),
                                newPassword,
                                userAccount.getRole().name(),
                                userAccount.getSurname(),
                                userAccount.getGivenName(),
                                userAccount.getEmailAddr(),
                                userGuid,
                                userAccount.getDisplayName()));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("accountData: {}", accountData);
                    }

                    boolean isUserCreated = userManager.addUserAccount(userDN.toString(), accountData);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("isUserCreated: {}", isUserCreated);
                    }

                    if (isUserCreated)
                    {
                        // generate a key for the user
                        try
                        {
                            keyManager.createKeys(userGuid);
                        }
                        catch (KeyManagementException kmx)
                        {
                            ERROR_RECORDER.error(kmx.getMessage(), kmx);
                        }

                        response.setRequestStatus(SecurityRequestStatus.SUCCESS);

                        // assign services (if any)
                        if ((request.getServicesList() != null) && (request.getServicesList().size() != 0))
                        {
                            try
                            {
                                for (String svcId : request.getServicesList())
                                {
                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("Service: {}", svcId);
                                    }

                                    boolean isServiceAdded = userSvcs.addServiceToUser(userAccount.getGuid(), svcId);

                                    if (!(isServiceAdded))
                                    {
                                        throw new SQLException("Failed to provision service " + svcId + " for the provided user");
                                    }
                                }
                            }
                            catch (SQLException sqx)
                            {
                                ERROR_RECORDER.error(sqx.getMessage(), sqx);
                            }
                        }
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
            else
            {
                response.setRequestStatus(SecurityRequestStatus.UNAUTHORIZED);
            }
        }
        catch (AccountControlException acx)
        {
            ERROR_RECORDER.error(acx.getMessage(), acx);

            throw new AccountControlException(acx.getMessage(), acx);
        }
        catch (AdminControlServiceException acx)
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
    @Override
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
            boolean isAuthorized = adminControl.adminControlService(reqAccount, AdminControlType.USER_ADMIN);

            if (DEBUG)
            {
                DEBUGGER.debug("isAuthorized: {}", isAuthorized);
            }

            if (isAuthorized)
            {
                
                // delete userAccount
                boolean isComplete = userManager.removeUserAccount(userAccount.getUsername(), userAccount.getGuid());

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
        }
        catch (AdminControlServiceException acsx)
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
    @Override
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
            boolean isAuthorized = adminControl.adminControlService(reqAccount, AdminControlType.USER_ADMIN);

            if (DEBUG)
            {
                DEBUGGER.debug("isAuthorized: {}", isAuthorized);
            }

            if (isAuthorized)
            {
                // we will only have a guid here - so we need to load the user
                List<Object> userData = userManager.loadUserAccount(userAccount.getGuid());

                if (DEBUG)
                {
                    DEBUGGER.debug("List<Object>: {}", userData);
                }

                if ((userData != null) && (userData.size() != 0))
                {
                    boolean isComplete = userManager.modifyUserSuspension((String) userData.get(1), (String) userData.get(0), userAccount.isSuspended());

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
            else
            {
                response.setRequestStatus(SecurityRequestStatus.UNAUTHORIZED);
            }
        }
        catch (AdminControlServiceException acsx)
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
    @Override
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
            boolean isAuthorized = adminControl.adminControlService(reqAccount, AdminControlType.USER_ADMIN);

            if (DEBUG)
            {
                DEBUGGER.debug("isAuthorized: {}", isAuthorized);
            }

            if (isAuthorized)
            {
                // we will only have a guid here - so we need to load the user
                List<Object> userData = userManager.loadUserAccount(userAccount.getGuid());

                if (DEBUG)
                {
                    DEBUGGER.debug("List<Object>: {}", userData);
                }

                if ((userData != null) && (userData.size() != 0))
                {
                    Map<String, Object> requestMap = new HashMap<String, Object>()
                    {
                        private static final long serialVersionUID = -4501815670500496164L;

                        {
                            put(authData.getLockCount(), userAccount.getFailedCount());
                        }
                    };

                    if (DEBUG)
                    {
                        DEBUGGER.debug("requestMap: {}", requestMap);
                    }

                    boolean isComplete = userManager.modifyUserInformation((String) userData.get(1), (String) userData.get(0), requestMap);

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
                else
                {
                    ERROR_RECORDER.error("Failed to locate user for given GUID");

                    response.setRequestStatus(SecurityRequestStatus.FAILURE);
                }
            }
            else
            {
                response.setRequestStatus(SecurityRequestStatus.UNAUTHORIZED);
            }
        }
        catch (AdminControlServiceException acsx)
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
    @Override
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
            boolean isAuthorized = adminControl.adminControlService(reqAccount, AdminControlType.USER_ADMIN);

            if (DEBUG)
            {
                DEBUGGER.debug("isAuthorized: {}", isAuthorized);
            }

            if (isAuthorized)
            {
                // we will only have a guid here - so we need to load the user
                List<Object> userData = userManager.loadUserAccount(userAccount.getGuid());

                if (DEBUG)
                {
                    DEBUGGER.debug("List<Object>: {}", userData);
                }

                if ((userData != null) && (userData.size() != 0))
                {
                    Map<String, Object> requestMap = new HashMap<String, Object>()
                    {
                        private static final long serialVersionUID = -4501815670500496164L;

                        {
                            put(authData.getUserRole(), userAccount.getRole());
                        }
                    };

                    if (DEBUG)
                    {
                        DEBUGGER.debug("requestMap: {}", requestMap);
                    }

                    boolean isComplete = userManager.modifyUserInformation((String) userData.get(1), (String) userData.get(0), requestMap);

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
                            resAccount.setPagerNumber((userData.get(6) == null) ? SecurityConstants.NOT_SET : (String) userData.get(6));
                            resAccount.setTelephoneNumber((userData.get(7) == null) ? SecurityConstants.NOT_SET : (String) userData.get(7));
                            resAccount.setRole(Role.valueOf((String) userData.get(8)));
                            resAccount.setFailedCount(((userData.get(9) == null) ? 0 : (Integer) userData.get(9)));
                            resAccount.setLastLogin(((userData.get(10) == null) ? new Date(1L) : new Date((Long) userData.get(10))));
                            resAccount.setExpiryDate(((userData.get(11) == null) ? 1L : (Long) userData.get(11)));
                            resAccount.setSuspended(((userData.get(12) == null) ? Boolean.FALSE : (Boolean) userData.get(12)));
                            resAccount.setOlrSetup(((userData.get(13) == null) ? Boolean.FALSE : (Boolean) userData.get(13)));
                            resAccount.setOlrLocked(((userData.get(14) == null) ? Boolean.FALSE : (Boolean) userData.get(14)));
                            resAccount.setTcAccepted(((userData.get(15) == null) ? Boolean.FALSE : (Boolean) userData.get(15)));

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
            else
            {
                response.setRequestStatus(SecurityRequestStatus.UNAUTHORIZED);
            }
        }
        catch (AdminControlServiceException acsx)
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
    @Override
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
            boolean isAuthorized = adminControl.adminControlService(reqAccount, AdminControlType.USER_ADMIN);

            if (DEBUG)
            {
                DEBUGGER.debug("isAuthorized: {}", isAuthorized);
            }

            if (isAuthorized)
            {
                // this is a reset request, so we need to do a few things
                // 1, we need to generate a unique id that we can email off
                // to the user, that we can then look up to confirm
                // then once we have that we can actually do the reset
                // first, change the existing password
                // 128 character values - its possible that the reset is
                // coming as a result of a possible compromise
                String tmpPassword = PasswordUtils.encryptText(RandomStringUtils.randomAlphanumeric(secConfig.getPasswordMaxLength()), RandomStringUtils.randomAlphanumeric(secConfig.getSaltLength()),
                        secConfig.getAuthAlgorithm(), secConfig.getIterations());
                String tmpSalt = RandomStringUtils.randomAlphanumeric(secConfig.getSaltLength());

                if ((StringUtils.isNotEmpty(tmpPassword)) && (StringUtils.isNotEmpty(tmpSalt)))
                {
                    // update the authentication datastore with the new password
                    // we never show the user the password, we're only doing this
                    // to prevent unauthorized access (or further unauthorized access)
                    // we get a return code back but we aren't going to use it really
                    boolean isComplete = userManager.changeUserPassword(userAccount.getGuid(), tmpPassword, System.currentTimeMillis());

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
            else
            {
                response.setRequestStatus(SecurityRequestStatus.UNAUTHORIZED);
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new AccountControlException(sqx.getMessage(), sqx);
        }
        catch (AdminControlServiceException acsx)
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
    @Override
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
        final SearchRequestType searchType = request.getSearchType();

        if (DEBUG)
        {
            DEBUGGER.debug("UserAccount: {}", reqAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("SearchRequestType: {}", searchType);
        }

        try
        {
            boolean isUserAuthorized = false;

            if (searchType != SearchRequestType.FORGOTUID)
            {
                isUserAuthorized = adminControl.adminControlService(reqAccount, AdminControlType.USER_ADMIN);

                if (DEBUG)
                {
                    DEBUGGER.debug("isUserAuthorized: {}", isUserAuthorized);
                }
            }
            else
            {
                isUserAuthorized = true;
            }

            if (isUserAuthorized)
            {
                List<String[]> userList = null;

                switch (searchType)
                {
                    case FORGOTUID:
                        userList = userManager.searchUsers(SearchRequestType.EMAILADDR, userAccount.getEmailAddr());

                        break;
                    default:
                        String searchData = null;
                        SearchRequestType requestType = null;

                        for (Field field : userAccount.getClass().getDeclaredFields())
                        {
                            field.setAccessible(true); // private fields, make them accessible

                            if (DEBUG)
                            {
                                DEBUGGER.debug("field: {}", field);
                            }

                            if (!(field.getName().equals("methodName")) &&
                                    (!(field.getName().equals("CNAME"))) &&
                                    (!(field.getName().equals("DEBUGGER"))) &&
                                    (!(field.getName().equals("DEBUG"))) &&
                                    (!(field.getName().equals("ERROR_RECORDER"))) &&
                                    (!(field.getName().equals("serialVersionUID"))))
                            {
                                try
                                {
                                    if (field.get(userAccount) != null)
                                    {
                                        if (DEBUG)
                                        {
                                            DEBUGGER.debug("Value: {}", field.get(userAccount));
                                        }

                                        searchData = (String) field.get(userAccount);
                                        requestType = SearchRequestType.valueOf(field.getName().toUpperCase());

                                        if (DEBUG)
                                        {
                                            DEBUGGER.debug("requestType: {}", requestType);
                                            DEBUGGER.debug("searchData: {}", searchData);
                                        }

                                        break;
                                    }
                                }
                                catch (IllegalAccessException iax)
                                {
                                    ERROR_RECORDER.error(iax.getMessage(), iax);
                                }
                            }
                        }

                        userList = userManager.searchUsers(requestType, searchData);

                        break;
                }

                if (DEBUG)
                {
                    DEBUGGER.debug("userList: {}", userList);
                }

                if ((userList != null) && (userList.size() != 0))
                {
                    if ((searchType == SearchRequestType.FORGOTUID) && (userList.size() != 1))
                    {
                        response.setRequestStatus(SecurityRequestStatus.FAILURE);
                    }
                    else
                    {
                        List<UserAccount> userAccounts = new ArrayList<>();

                        for (String[] userData : userList)
                        {
                            if (!(StringUtils.equals(reqAccount.getGuid(), userData[0])))
                            {
                                UserAccount userInfo = new UserAccount();
                                userInfo.setGuid(userData[0]);
                                userInfo.setUsername(userData[1]);
                                userInfo.setGivenName(userData[2]);
                                userInfo.setSurname(userData[3]);
                                userInfo.setEmailAddr(userData[5]);
                                userInfo.setDisplayName(userData[4]);

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
                }
                else
                {
                    response.setRequestStatus(SecurityRequestStatus.FAILURE);
                }
            }
            else
            {
                response.setRequestStatus(SecurityRequestStatus.UNAUTHORIZED);
            }
        }
        catch (UserManagementException umx)
        {
            ERROR_RECORDER.error(umx.getMessage(), umx);

            throw new AccountControlException(umx.getMessage(), umx);
        }
        catch (AdminControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);

            throw new AccountControlException(acsx.getMessage(), acsx);
        }
        finally
        {
            if (searchType != SearchRequestType.FORGOTUID)
            {
                // audit
                try
                {
                    AuditEntry auditEntry = new AuditEntry();
                    auditEntry.setHostInfo(reqInfo);
                    auditEntry.setAuditType(AuditType.SEARCHACCOUNTS);
                    auditEntry.setUserAccount(reqAccount);
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
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.security.processors.interfaces.IAccountControlProcessor#loadUserAccount(com.cws.esolutions.security.processors.dto.AccountControlRequest)
     */
    @Override
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
        final SearchRequestType searchType = request.getSearchType();

        if (DEBUG)
        {
            DEBUGGER.debug("UserAccount: {}", reqAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("SearchRequestType: {}", searchType);
        }

        try
        {
            boolean isUserAuthorized = false;

            if (searchType != SearchRequestType.FORGOTUID)
            {
                isUserAuthorized = adminControl.adminControlService(reqAccount, AdminControlType.USER_ADMIN);

                if (DEBUG)
                {
                    DEBUGGER.debug("isUserAuthorized: {}", isUserAuthorized);
                }
            }
            else
            {
                isUserAuthorized = true;
            }

            if (isUserAuthorized)
            {
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
                    loadAccount.setGivenName((String) userData.get(2));
                    loadAccount.setSurname((String) userData.get(3));
                    loadAccount.setDisplayName((String) userData.get(4));
                    loadAccount.setEmailAddr((String) userData.get(5));
                    loadAccount.setPagerNumber((userData.get(6) == null) ? SecurityConstants.NOT_SET : (String) userData.get(6));
                    loadAccount.setTelephoneNumber((userData.get(7) == null) ? SecurityConstants.NOT_SET : (String) userData.get(7));
                    loadAccount.setRole(Role.valueOf((String) userData.get(8)));
                    loadAccount.setFailedCount(((userData.get(9) == null) ? 0 : (Integer) userData.get(9)));
                    loadAccount.setLastLogin(((userData.get(10) == null) ? new Date(1L) : new Date((Long) userData.get(10))));
                    loadAccount.setExpiryDate(((userData.get(11) == null) ? 1L : (Long) userData.get(11)));
                    loadAccount.setSuspended(((userData.get(12) == null) ? Boolean.FALSE : (Boolean) userData.get(12)));
                    loadAccount.setOlrSetup(((userData.get(13) == null) ? Boolean.FALSE : (Boolean) userData.get(13)));
                    loadAccount.setOlrLocked(((userData.get(14) == null) ? Boolean.FALSE : (Boolean) userData.get(14)));
                    loadAccount.setTcAccepted(((userData.get(15) == null) ? Boolean.FALSE : (Boolean) userData.get(15)));

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
            }
            else
            {
                response.setRequestStatus(SecurityRequestStatus.UNAUTHORIZED);
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
        catch (AdminControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);

            throw new AccountControlException(acsx.getMessage(), acsx);
        }
        finally
        {
            if (searchType != SearchRequestType.FORGOTUID)
            {
                // audit
                try
                {
                    AuditEntry auditEntry = new AuditEntry();
                    auditEntry.setHostInfo(reqInfo);
                    auditEntry.setAuditType(AuditType.LOADACCOUNT);
                    auditEntry.setUserAccount(reqAccount);
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
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.security.processors.interfaces.IAccountControlProcessor#loadUserAudit(com.cws.esolutions.security.processors.dto.AccountControlRequest)
     */
    @Override
    public AccountControlResponse loadUserAudit(final AccountControlRequest request) throws AccountControlException
    {
        final String methodName = IAccountControlProcessor.CNAME + "#loadUserAuditTrail(final AccountControlRequest request) throws AccountControlException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AuditRequest: {}", request);
        }

        AccountControlResponse response = new AccountControlResponse();

        final RequestHostInfo reqInfo = request.getHostInfo();
        final UserAccount reqAccount = request.getRequestor();
        final UserAccount userAccount = request.getUserAccount();
        final SearchRequestType searchType = request.getSearchType();

        if (DEBUG)
        {
            DEBUGGER.debug("UserAccount: {}", reqAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("SearchRequestType: {}", searchType);
        }

        try
        {
            boolean isUserAuthorized = adminControl.adminControlService(reqAccount, AdminControlType.USER_ADMIN);

            if (DEBUG)
            {
                DEBUGGER.debug("isUserAuthorized: {}", isUserAuthorized);
            }

            if (isUserAuthorized)
            {
                // get the user info
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

                    if (DEBUG)
                    {
                        DEBUGGER.debug("UserAccount: {}", loadAccount);
                    }

                    AuditEntry auditEntry = new AuditEntry();
                    auditEntry.setUserAccount(loadAccount);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("AuditEntry: {}", auditEntry);
                    }

                    AuditRequest auditRequest = new AuditRequest();
                    auditRequest.setAuditEntry(auditEntry);
                    auditRequest.setStartRow(request.getStartPage());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("AuditRequest: {}", auditRequest);
                    }

                    AuditResponse auditResponse = auditor.getAuditEntries(auditRequest);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("AuditResponse: {}", auditResponse);
                    }

                    if (auditResponse.getRequestStatus() == SecurityRequestStatus.SUCCESS)
                    {
                        List<AuditEntry> auditEntries = auditResponse.getAuditList();

                        if (DEBUG)
                        {
                            DEBUGGER.debug("List<AuditEntry>: {}", auditEntries);
                        }

                        if ((auditEntries != null) && (auditEntries.size() != 0))
                        {
                            // add to the response
                            response.setUserAccount(loadAccount);
                            response.setEntryCount(auditResponse.getEntryCount());
                            response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                            response.setAuditEntries(auditEntries);
                        }
                        else
                        {
                            response.setRequestStatus(SecurityRequestStatus.FAILURE);
                        }
                    }
                }
                else
                {
                    response.setRequestStatus(SecurityRequestStatus.FAILURE);
                }
            }
            else
            {
                response.setRequestStatus(SecurityRequestStatus.UNAUTHORIZED);
            }

            if (DEBUG)
            {
                DEBUGGER.debug("AuditResponse: {}", response);
            }
        }
        catch (AdminControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);

            throw new AccountControlException(acsx.getMessage(), acsx);
        }
        catch (AuditServiceException asx)
        {
            ERROR_RECORDER.error(asx.getMessage(), asx);

            throw new AccountControlException(asx.getMessage(), asx);
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
                auditEntry.setAuditType(AuditType.SHOWAUDIT);
                auditEntry.setUserAccount(reqAccount);
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
    @Override
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
            boolean isUserAuthorized = false;

            if (request.getControlType() != ControlType.SERVICES)
            {
                isUserAuthorized = adminControl.adminControlService(reqAccount, AdminControlType.USER_ADMIN);

                if (DEBUG)
                {
                    DEBUGGER.debug("isUserAuthorized: {}", isUserAuthorized);
                }
            }
            else
            {
                isUserAuthorized = true;
            }

            if (isUserAuthorized)
            {
                List<String[]> userList = userManager.listUserAccounts(request.getUserType().name());

                if (DEBUG)
                {
                    DEBUGGER.debug("userList: {}", userList);
                }

                if ((userList != null) && (userList.size() != 0))
                {
                    List<UserAccount> userAccounts = new ArrayList<>();

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
            else
            {
                response.setRequestStatus(SecurityRequestStatus.UNAUTHORIZED);
            }
        }
        catch (UserManagementException umx)
        {
            ERROR_RECORDER.error(umx.getMessage(), umx);

            throw new AccountControlException(umx.getMessage(), umx);
        }
        catch (AdminControlServiceException acsx)
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
                auditEntry.setAuditType(AuditType.SEARCHACCOUNTS);
                auditEntry.setUserAccount(reqAccount);
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
