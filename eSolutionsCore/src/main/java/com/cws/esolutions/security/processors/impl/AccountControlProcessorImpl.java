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
package com.cws.esolutions.security.processors.impl;

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

import javax.mail.MessagingException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.RandomStringUtils;

import com.cws.esolutions.security.enums.Role;
import com.cws.esolutions.core.utils.EmailUtils;
import com.cws.esolutions.security.enums.SaltType;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.dto.UserSecurity;
import com.cws.esolutions.security.utils.PasswordUtils;
import com.cws.esolutions.security.audit.dto.AuditEntry;
import com.cws.esolutions.security.audit.enums.AuditType;
import com.cws.esolutions.security.audit.dto.AuditRequest;
import com.cws.esolutions.security.audit.dto.AuditResponse;
import com.cws.esolutions.core.processors.dto.EmailMessage;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.keymgmt.dto.KeyManagementRequest;
import com.cws.esolutions.security.keymgmt.dto.KeyManagementResponse;
import com.cws.esolutions.security.dao.usermgmt.enums.SearchRequestType;
import com.cws.esolutions.security.processors.dto.AccountControlRequest;
import com.cws.esolutions.security.audit.exception.AuditServiceException;
import com.cws.esolutions.security.processors.dto.AccountControlResponse;
import com.cws.esolutions.security.access.control.enums.AdminControlType;
import com.cws.esolutions.security.keymgmt.exception.KeyManagementException;
import com.cws.esolutions.security.processors.exception.AccountControlException;
import com.cws.esolutions.security.dao.userauth.exception.AuthenticatorException;
import com.cws.esolutions.security.dao.usermgmt.exception.UserManagementException;
import com.cws.esolutions.security.processors.interfaces.IAccountControlProcessor;
import com.cws.esolutions.security.access.control.exception.AdminControlServiceException;
/**
 * SecurityService
 * com.cws.esolutions.security.processors.impl
 * AccountControlProcessorImpl.java
 *
 * $Id: $
 * $Author: $
 * $Date: $
 * $Revision: $
 * @author kmhuntly@gmail.com
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Oct 29, 2012 9:44:46 AM
 *     Created.
 */
public class AccountControlProcessorImpl implements IAccountControlProcessor
{
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

        final UserAccount requestor = request.getRequestor();
        final RequestHostInfo reqInfo = request.getHostInfo();
        final UserAccount userAccount = request.getUserAccount();
        final UserSecurity userSecurity = request.getUserSecurity();
        final String newUserSalt = RandomStringUtils.randomAlphanumeric(secConfig.getSaltLength());
        final StringBuilder userDN = new StringBuilder()
            .append(authData.getUserId() + "=" + userAccount.getUsername() + ",")
            .append(authRepo.getRepositoryUserBase());

        if (DEBUG)
        {
            DEBUGGER.debug("Requestor: {}", requestor);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("UserSecurity: {}", userSecurity);
            DEBUGGER.debug("newUserSalt: {}", newUserSalt);
            DEBUGGER.debug("userDN: {}", userDN);
        }

        try
        {
            boolean isAuthorized = adminControl.adminControlService(requestor, AdminControlType.USER_ADMIN);

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

                        if (StringUtils.contains(umx.getMessage(), "UUID"))
                        {
                            userGuid = UUID.randomUUID().toString();

                            if (DEBUG)
                            {
                                DEBUGGER.debug("Value: {}", userGuid);
                            }

                            x++;

                            continue;
                        }
                        else
                        {
                            response.setRequestStatus(SecurityRequestStatus.FAILURE);
                            response.setResponse("A user already exists with the provided username.");

                            return response;
                        }
                    }
                }

                // insert the user salt
                boolean isSaltInserted = userSec.addUserSalt(userAccount.getGuid(), newUserSalt, SaltType.LOGON.name());

                if (DEBUG)
                {
                    DEBUGGER.debug("isSaltInserted: {}", isSaltInserted);
                }

                if (isSaltInserted)
                {
                    String newPassword = PasswordUtils.encryptText(RandomStringUtils.randomAlphanumeric(secConfig.getPasswordMaxLength()), newUserSalt,
                            secConfig.getAuthAlgorithm(), secConfig.getIterations());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Value: {}", newPassword);
                    }

                    List<String> accountData = new ArrayList<String>(
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

                    boolean isUserCreated = userManager.addUserAccount(userDN.toString(), accountData, request.getApplicationName());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("isUserCreated: {}", isUserCreated);
                    }

                    if (isUserCreated)
                    {
                        // generate a key for the user
                        KeyManagementRequest keyRequest = new KeyManagementRequest();
                        keyRequest.setKeyAlgorithm(keyConfig.getKeyAlgorithm());
                        keyRequest.setGuid(userGuid);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("KeyManagementRequest: {}", keyRequest);
                        }

                        KeyManagementResponse keyResponse = keyManager.createKeys(keyRequest);

                        if (keyResponse.getRequestStatus() == SecurityRequestStatus.SUCCESS)
                        {
                            response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                            response.setResponse("New user successfully created");

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

                                    response.setResponse("Successfully added the new user account, but failed to provision services. Please review application logs.");
                                }
                            }
                        }
                        else
                        {
                            // failed to generate user keypair
                            userManager.removeUserAccount(userAccount.getUsername(), userGuid);
                            userSec.removeUserData(userGuid);

                            throw new AccountControlException("Failed to provision new user account: failed to generate keys for the provided user");
                        }
                    }
                    else
                    {
                        // failed to add the user to the repository
                        throw new AccountControlException("Failed to add user to the account repository");
                    }
                }
                else
                {
                    // failed to insert salt
                    throw new AccountControlException("Failed to provision new user account: failed to insert the generated salt value");
                }
            }
            else
            {
                throw new AdminControlServiceException("Failed to provision new user account: Requesting user is NOT authorized to perform the operation");
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
        catch (KeyManagementException kmx)
        {
            ERROR_RECORDER.error(kmx.getMessage(), kmx);
            
            throw new AccountControlException(kmx.getMessage(), kmx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.CREATEUSER);
                auditEntry.setUserAccount(requestor);
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

        final UserAccount requestor = request.getRequestor();
        final UserAccount account = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getHostInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("UserAccount reqUser: {}", requestor);
            DEBUGGER.debug("UserAccount account: {}", account);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            boolean isAuthorized = adminControl.adminControlService(requestor, AdminControlType.USER_ADMIN);

            if (DEBUG)
            {
                DEBUGGER.debug("isAuthorized: {}", isAuthorized);
            }

            if (isAuthorized)
            {
                
                // delete account
                boolean isComplete = userManager.removeUserAccount(account.getUsername(), account.getGuid());

                if (DEBUG)
                {
                    DEBUGGER.debug("isComplete: {}", isComplete);
                }

                if (isComplete)
                {
                    response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                    response.setResponse("User account was successfully deleted");
                }
                else
                {
                    response.setRequestStatus(SecurityRequestStatus.FAILURE);
                    response.setResponse("Failed to delete user account");
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
                auditEntry.setUserAccount(requestor);
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

    @Override
    public AccountControlResponse createSecurityData(final AccountControlRequest request) throws AccountControlException
    {
        final String methodName = IAccountControlProcessor.CNAME + "#createSecurityData(final CreateUserRequest createReq) throws AccountControlException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AccountControlRequest: {}", request);
        }

        AccountControlResponse response = new AccountControlResponse();

        final UserAccount requestor = request.getRequestor();
        final RequestHostInfo reqInfo = request.getHostInfo();
        final UserAccount userAccount = request.getUserAccount();
        final UserSecurity userSecurity = request.getUserSecurity();
        final String newUserSalt = RandomStringUtils.randomAlphanumeric(secConfig.getSaltLength());

        if (DEBUG)
        {
            DEBUGGER.debug("Requestor: {}", requestor);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("UserSecurity: {}", userSecurity);
        }

        try
        {
            String resetSalt = RandomStringUtils.randomAlphanumeric(secConfig.getSaltLength());

            if (DEBUG)
            {
                DEBUGGER.debug("resetSalt: {}", resetSalt);
            }

            boolean isSaltInserted = userSec.addUserSalt(userAccount.getGuid(), newUserSalt, SaltType.RESET.name());

            if (DEBUG)
            {
                DEBUGGER.debug("isSaltInserted: {}", isSaltInserted);
            }

            if (isSaltInserted)
            {
                String secAnswerOne = PasswordUtils.encryptText(userSecurity.getSecAnswerOne(), newUserSalt, secConfig.getAuthAlgorithm(), secConfig.getIterations());
                String secAnswerTwo = PasswordUtils.encryptText(userSecurity.getSecAnswerTwo(), newUserSalt, secConfig.getAuthAlgorithm(), secConfig.getIterations());

                if (DEBUG)
                {
                    DEBUGGER.debug("Value: {}", secAnswerOne);
                    DEBUGGER.debug("Value: {}", secAnswerTwo);
                }

                List<String> securityList = new ArrayList<String>
                (
                    Arrays.asList
                    (
                        userSecurity.getPassword(),
                        userSecurity.getSecQuestionOne(),
                        userSecurity.getSecQuestionTwo(),
                        secAnswerOne,
                        secAnswerTwo
                    )
                );

                if (DEBUG)
                {
                    DEBUGGER.debug("securityList: {}", securityList);
                }

                boolean isComplete = authenticator.createSecurityData(userAccount.getUsername(), userAccount.getGuid(), securityList);

                if (DEBUG)
                {
                    DEBUGGER.debug("isComplete: {}", isComplete);
                }

                if (isComplete)
                {
                    response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                    response.setResponse("Successfully added security questions/answers for user.");
                }
                else
                {
                    response.setRequestStatus(SecurityRequestStatus.FAILURE);
                    response.setResponse("Failed to ad security questions/answers for user.");
                }
            }
            else
            {
                throw new AccountControlException("Failed to generate salt for request. Cannot continue.");
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new AccountControlException(sqx.getMessage(), sqx);
        }
        catch (AuthenticatorException ax)
        {
            ERROR_RECORDER.error(ax.getMessage(), ax);
            
            throw new AccountControlException(ax.getMessage(), ax);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.ADDSECURITY);
                auditEntry.setUserAccount(requestor);
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

        final UserAccount requestor = request.getRequestor();
        final UserAccount account = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getHostInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("UserAccount reqUser: {}", requestor);
            DEBUGGER.debug("UserAccount account: {}", account);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            boolean isAuthorized = adminControl.adminControlService(requestor, AdminControlType.USER_ADMIN);

            if (DEBUG)
            {
                DEBUGGER.debug("isAuthorized: {}", isAuthorized);
            }

            if (isAuthorized)
            {
                // we will only have a guid here - so we need to load the user account
                List<Object> userData = userManager.loadUserAccount(account.getGuid());

                if (DEBUG)
                {
                    DEBUGGER.debug("List<Object>: {}", userData);
                }

                if ((userData != null) && (userData.size() != 0))
                {
                    boolean isComplete = userManager.modifyUserSuspension((String) userData.get(1), account.getGuid(), account.isSuspended());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("isComplete: {}", isComplete);
                    }

                    if (isComplete)
                    {
                        response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                        response.setResponse("Successfully performed suspension modification.");
                    }
                    else
                    {
                        response.setRequestStatus(SecurityRequestStatus.FAILURE);
                        response.setResponse("Failed to perform suspension modification for requested user.");
                    }
                }
                else
                {
                    throw new AccountControlException("Failed to locate user account for given GUID");
                }
            }
            else
            {
                throw new AccountControlException("The requesting user was not authorized to perform the operation.");
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
                auditEntry.setUserAccount(requestor);
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

        final UserAccount requestor = request.getRequestor();
        final UserAccount account = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getHostInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("UserAccount reqUser: {}", requestor);
            DEBUGGER.debug("UserAccount account: {}", account);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            boolean isAuthorized = adminControl.adminControlService(requestor, AdminControlType.USER_ADMIN);

            if (DEBUG)
            {
                DEBUGGER.debug("isAuthorized: {}", isAuthorized);
            }

            if (isAuthorized)
            {
                // we will only have a guid here - so we need to load the user account
                List<Object> userData = userManager.loadUserAccount(account.getGuid());

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
                            put(authData.getLockCount(), account.getFailedCount());
                        }
                    };

                    if (DEBUG)
                    {
                        DEBUGGER.debug("requestMap: {}", requestMap);
                    }

                    boolean isComplete = userManager.modifyUserInformation(account.getUsername(), account.getGuid(), requestMap);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("isComplete: {}", isComplete);
                    }

                    if (isComplete)
                    {
                        response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                        response.setResponse("Successfully performed suspension modification.");
                    }
                    else
                    {
                        response.setRequestStatus(SecurityRequestStatus.FAILURE);
                        response.setResponse("Failed to perform suspension modification for requested user.");
                    }
                }
                else
                {
                    throw new AccountControlException("Failed to locate user account for given GUID");
                }
            }
            else
            {
                throw new AccountControlException("The requesting user was not authorized to perform the operation.");
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
                auditEntry.setUserAccount(requestor);
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

    @Override
    public AccountControlResponse changeUserEmail(final AccountControlRequest request) throws AccountControlException
    {
        final String methodName = IAccountControlProcessor.CNAME + "#changeUserEmail(final AccountControlRequest request) throws AccountControlException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AccountControlRequest: {}", request);
        }

        AccountControlResponse response = new AccountControlResponse();

        final RequestHostInfo reqInfo = request.getHostInfo();
        final UserAccount requestor = request.getRequestor();
        final UserAccount userAccount = request.getUserAccount();
        final UserSecurity reqSecurity = request.getUserSecurity();

        if (DEBUG)
        {
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount: {}", requestor);
            DEBUGGER.debug("UserAccount: {}", userAccount);
        }

        try
        {
            // ok, first things first. if this is an administrative reset, make sure the requesting user
            // is authorized to perform the action. if this is a user initiated change, make sure the
            // new password isnt the same as the existing
            if (!(StringUtils.equals(userAccount.getGuid(), requestor.getGuid())))
            {
                // requesting user is not the same as the user being reset. authorize
                boolean isAdminAuthorized = adminControl.adminControlService(requestor, AdminControlType.USER_ADMIN);

                if (DEBUG)
                {
                    DEBUGGER.debug("isAdminAuthorized: {}", isAdminAuthorized);
                }

                if (!(isAdminAuthorized))
                {
                    // nope !
                    throw new AccountControlException("Unauthorized modification request attempted by " + requestor.getUsername() + " for user " + userAccount.getUsername());
                }
            }

            // ok, authenticate first
            String userSalt = userSec.getUserSalt(userAccount.getGuid(), SaltType.LOGON.name());

            if (DEBUG)
            {
                DEBUGGER.debug("userSalt: {}", userSalt);
            }

            if (StringUtils.isNotEmpty(userSalt))
            {
                // we aren't getting the data back here because we don't need it. if the request
                // fails we'll get an exception and not process further. this might not be the
                // best flow control, but it does exactly what we need where we need it.
                authenticator.performLogon(userAccount.getGuid(),
                        userAccount.getUsername(),
                        PasswordUtils.encryptText(
                                reqSecurity.getPassword(),
                                userSalt,
                                svcBean.getConfigData().getSecurityConfig().getAuthAlgorithm(),
                                svcBean.getConfigData().getSecurityConfig().getIterations()),
                        request.getApplicationName());

                Map<String, Object> requestMap = new HashMap<String, Object>();
                requestMap.put(authData.getEmailAddr(), userAccount.getEmailAddr());

                if (DEBUG)
                {
                    DEBUGGER.debug("Request Map: {}", requestMap);
                }

                boolean isComplete = userManager.modifyUserInformation(userAccount.getUsername(), userAccount.getGuid(), requestMap);

                if (isComplete)
                {
                    // TODO
                    // send an email out to the user
                    EmailMessage email = new EmailMessage();
                    email.setIsAlert(false);
                    email.setMessageFrom(new ArrayList<String>(Arrays.asList(svcBean.getConfigData().getEmailAddr())));
                    email.setMessageTo(new ArrayList<String>(Arrays.asList(userAccount.getEmailAddr())));
                    email.setMessageSubject("some subject");
                    email.setMessageBody("some body");

                    if (DEBUG)
                    {
                        DEBUGGER.debug("EmailMessage: {}", email);
                    }

                    try
                    {
                        EmailUtils.sendEmailMessage(email);
                    }
                    catch (MessagingException mx)
                    {
                        ERROR_RECORDER.error(mx.getMessage(), mx);
                    }

                    response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                    response.setResponse("Modification request successfully performed");
                }
                else
                {
                    response.setRequestStatus(SecurityRequestStatus.FAILURE);
                    response.setResponse("Modification request was not completed successfully");
                }
            }
            else
            {
                throw new AccountControlException("Unable to obtain configured user salt. Cannot continue");
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
        catch (AuthenticatorException ax)
        {
            ERROR_RECORDER.error(ax.getMessage(), ax);

            throw new AccountControlException(ax.getMessage(), ax);
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
                auditEntry.setUserAccount(requestor);
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

    @Override
    public AccountControlResponse changeUserPassword(final AccountControlRequest request) throws AccountControlException
    {
        final String methodName = IAccountControlProcessor.CNAME + "#changeUserPassword(final AccountControlRequest request) throws AccountControlException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AccountControlRequest: {}", request);
        }

        // List<String> authList = null;
        AccountControlResponse response = new AccountControlResponse();

        final Calendar calendar = Calendar.getInstance();
        final RequestHostInfo reqInfo = request.getHostInfo();
        final UserAccount requestor = request.getRequestor();
        final UserAccount userAccount = request.getUserAccount();
        final UserSecurity reqSecurity = request.getUserSecurity();

        calendar.add(Calendar.DATE, secConfig.getPasswordExpiration());

        if (DEBUG)
        {
            DEBUGGER.debug("Calendar: {}", calendar);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount: {}", requestor);
            DEBUGGER.debug("UserAccount: {}", userAccount);
        }

        try
        {
            // ok, first things first. if this is an administrative reset, make sure the requesting user
            // is authorized to perform the action. if this is a user initiated change, make sure the
            // new password isnt the same as the existing
            if (!(StringUtils.equals(userAccount.getGuid(), requestor.getGuid())))
            {
                // requesting user is not the same as the user being reset. authorize
                boolean isAdminAuthorized = adminControl.adminControlService(requestor, AdminControlType.USER_ADMIN);

                if (DEBUG)
                {
                    DEBUGGER.debug("isAdminAuthorized: {}", isAdminAuthorized);
                }

                if (!(isAdminAuthorized))
                {
                    // nope !
                    throw new AccountControlException("Unauthorized modification request attempted by " + requestor.getUsername() + " for user " + userAccount.getUsername());
                }
            }

            // otherwise, keep going
            // make sure the new password isnt the same as the existing
            if (StringUtils.equals(reqSecurity.getNewPassword(), reqSecurity.getPassword()))
            {
                throw new AccountControlException("The new password MUST differ from the existing password.");
            }
            else if ((reqSecurity.getNewPassword().length() < secConfig.getPasswordMinLength()) // less than minimum
                    || (reqSecurity.getNewPassword().length() > secConfig.getPasswordMaxLength())) // greater than maximum
            {
                // password doesnt meet requirements, is either too short or too long
                throw new AccountControlException("The chosen password does not meet the configured length requirements.");
            }
            else
            {
                // ok, authenticate first
                String userSalt = userSec.getUserSalt(userAccount.getGuid(), SaltType.LOGON.name());

                if (DEBUG)
                {
                    DEBUGGER.debug("userSalt: {}", userSalt);
                }

                if (StringUtils.isNotEmpty(userSalt))
                {
                    String currentPassword = PasswordUtils.encryptText(reqSecurity.getPassword(), userSalt, secConfig.getAuthAlgorithm(), secConfig.getIterations());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Value: {}", currentPassword);
                    }

                    // we aren't getting the data back here because we don't need it. if the request
                    // fails we'll get an exception and not process further. this might not be the
                    // best flow control, but it does exactly what we need where we need it.
                    authenticator.performLogon(userAccount.getGuid(), userAccount.getUsername(), currentPassword, request.getApplicationName());

                    // ok, thats out of the way. lets keep moving.
                    String newUserSalt = RandomStringUtils.randomAlphanumeric(secConfig.getSaltLength());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Value: {}", newUserSalt);
                    }

                    if (StringUtils.isNotEmpty(newUserSalt))
                    {
                        // get rollback information in case something breaks...
                        // we already have the existing expiry and password, all we really need to get here is the salt.
                        String existingSalt = userSec.getUserSalt(userAccount.getGuid(), SaltType.LOGON.name());

                        if (StringUtils.isNotEmpty(existingSalt))
                        {
                            // good, move forward
                            // put the new salt in the database
                            boolean isComplete = userSec.updateUserSalt(userAccount.getGuid(), newUserSalt, SaltType.LOGON.name());

                            if (DEBUG)
                            {
                                DEBUGGER.debug("isComplete: {}", isComplete);
                            }

                            if (isComplete)
                            {
                                // good
                                String newPassword = PasswordUtils.encryptText(reqSecurity.getNewPassword(), newUserSalt, secConfig.getAuthAlgorithm(), secConfig.getIterations());

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("Value: {}", newPassword);
                                }

                                // make the modification in the user repository
                                isComplete = authenticator.changeUserPassword(userAccount.getGuid(),
                                        PasswordUtils.encryptText(
                                                reqSecurity.getNewPassword(),
                                                newUserSalt,
                                                svcBean.getConfigData().getSecurityConfig().getAuthAlgorithm(),
                                                svcBean.getConfigData().getSecurityConfig().getIterations()),
                                        calendar.getTimeInMillis());

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("isComplete: {}", isComplete);
                                }

                                if (isComplete)
                                {
                                    response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                                    response.setResponse("Successfully changed password.");
                                }
                                else
                                {
                                    // something failed. we're going to undo what we did in the user
                                    // repository, because we couldnt update the salt value. if we don't
                                    // undo it then the user will never be able to login without admin
                                    // intervention
                                    boolean isBackedOut = authenticator.changeUserPassword(userAccount.getUsername(), currentPassword, userAccount.getExpiryDate());

                                    if (!(isBackedOut))
                                    {
                                        throw new AccountControlException("Failed to modify the user account and unable to revert to existing state.");
                                    }

                                    response.setRequestStatus(SecurityRequestStatus.FAILURE);
                                    response.setResponse("Failed to change the password associated with the user account");
                                }
                            }
                            else
                            {
                                response.setRequestStatus(SecurityRequestStatus.FAILURE);
                                response.setResponse("Failed to change the password associated with the user account");
                            }
                        }
                        else
                        {
                            throw new AccountControlException("Unable to obtain existing salt value from datastore. Cannot continue.");
                        }
                    }
                    else
                    {
                        throw new AccountControlException("Unable to generate new salt for provided user account.");
                    }
                }
                else
                {
                    throw new AccountControlException("Unable to obtain configured user salt. Cannot continue");
                }
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
        catch (AuthenticatorException ax)
        {
            ERROR_RECORDER.error(ax.getMessage(), ax);

            throw new AccountControlException(ax.getMessage(), ax);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.CHANGEPASS);
                auditEntry.setUserAccount(requestor);
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

    @Override
    public AccountControlResponse changeUserSecurity(final AccountControlRequest request) throws AccountControlException
    {
        final String methodName = IAccountControlProcessor.CNAME + "#changeUserSecurity(final AccountControlRequest request) throws AccountControlException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AccountControlRequest: {}", request);
        }

        AccountControlResponse response = new AccountControlResponse();

        final Calendar calendar = Calendar.getInstance();
        final RequestHostInfo reqInfo = request.getHostInfo();
        final UserAccount requestor = request.getRequestor();
        final UserAccount userAccount = request.getUserAccount();
        final UserSecurity reqSecurity = request.getUserSecurity();

        if (DEBUG)
        {
            DEBUGGER.debug("Calendar: {}", calendar);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount: {}", requestor);
            DEBUGGER.debug("UserAccount: {}", userAccount);
        }

        try
        {
            // ok, first things first. if this is an administrative reset, make sure the requesting user
            // is authorized to perform the action. if this is a user initiated change, make sure the
            // new password isnt the same as the existing
            if (!(StringUtils.equals(userAccount.getGuid(), requestor.getGuid())))
            {
                // requesting user is not the same as the user being reset. no authorization here,
                // no one is allowed to change user security but the owning user
                throw new AccountControlException("Only the owning user can change security data.");
            }

            // otherwise, keep going
            // make sure the two questions and answers arent the same
            if ((StringUtils.equals(reqSecurity.getSecQuestionOne(), reqSecurity.getSecQuestionTwo())))
            {
                throw new AccountControlException("The security questions must be different.");
            }
            else if ((StringUtils.equals(reqSecurity.getSecAnswerOne(), reqSecurity.getSecAnswerTwo())))
            {
                throw new AccountControlException("The security answers must be different.");
            }
            else
            {
                // ok, authenticate first
                String userSalt = userSec.getUserSalt(userAccount.getGuid(), SaltType.LOGON.name());

                if (DEBUG)
                {
                    DEBUGGER.debug("userSalt: {}", userSalt);
                }

                if (StringUtils.isNotEmpty(userSalt))
                {
                    String password = PasswordUtils.encryptText(reqSecurity.getPassword(), userSalt, secConfig.getAuthAlgorithm(), secConfig.getIterations());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Value: {}", password);
                    }

                    // we aren't getting the data back here because we don't need it. if the request
                    // fails we'll get an exception and not process further. this might not be the
                    // best flow control, but it does exactly what we need where we need it.
                    authenticator.performLogon(userAccount.getGuid(), userAccount.getUsername(), password, request.getApplicationName());

                    // ok, thats out of the way. lets keep moving.
                    String newUserSalt = RandomStringUtils.randomAlphanumeric(secConfig.getSaltLength());

                    if (StringUtils.isNotEmpty(newUserSalt))
                    {
                        // get rollback information in case something breaks...
                        // we already have the existing expiry and password, all we really need to get here is the salt.
                        String existingSalt = userSec.getUserSalt(userAccount.getGuid(), SaltType.RESET.name());

                        if (StringUtils.isNotEmpty(existingSalt))
                        {
                            // make the backout
                            List<String> currentSec = authenticator.obtainSecurityData(userAccount.getUsername(), userAccount.getGuid());

                            if (DEBUG)
                            {
                                DEBUGGER.debug("currentSec: {}", currentSec);
                            }

                            Map<String, Object> backout = new HashMap<String, Object>();
                            backout.put(authData.getSecQuestionOne(), currentSec.get(0));
                            backout.put(authData.getSecQuestionTwo(), currentSec.get(1));
                            backout.put(authData.getSecAnswerOne(), currentSec.get(2));
                            backout.put(authData.getSecAnswerTwo(), currentSec.get(3));

                            if (DEBUG)
                            {
                                DEBUGGER.debug("backout: {}", backout);
                            }

                            String secAnswerOne = PasswordUtils.encryptText(reqSecurity.getSecAnswerOne(), newUserSalt, secConfig.getAuthAlgorithm(), secConfig.getIterations());
                            String secAnswerTwo = PasswordUtils.encryptText(reqSecurity.getSecAnswerTwo(), newUserSalt, secConfig.getAuthAlgorithm(), secConfig.getIterations());

                            if (DEBUG)
                            {
                                DEBUGGER.debug("Value: {}", secAnswerOne);
                                DEBUGGER.debug("Value: {}", secAnswerTwo);
                            }

                            // good, move forward
                            // make the modification in the user repository
                            Map<String, Object> changeMap = new HashMap<String, Object>();
                            changeMap.put(authData.getSecQuestionOne(), reqSecurity.getSecQuestionOne());
                            changeMap.put(authData.getSecQuestionTwo(), reqSecurity.getSecQuestionTwo());
                            changeMap.put(authData.getSecAnswerOne(), secAnswerOne);
                            changeMap.put(authData.getSecAnswerTwo(), secAnswerTwo);

                            if (DEBUG)
                            {
                                DEBUGGER.debug("changeMap: {}", changeMap);
                            }

                            boolean isComplete = userManager.modifyUserInformation(userAccount.getUsername(), userAccount.getGuid(), changeMap);

                            if (DEBUG)
                            {
                                DEBUGGER.debug("isComplete: {}", isComplete);
                            }

                            if (isComplete)
                            {
                                // now update the salt
                                isComplete = userSec.updateUserSalt(userAccount.getGuid(), newUserSalt, SaltType.RESET.name());

                                if (isComplete)
                                {
                                    response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                                    response.setResponse("Successfully changed security information.");
                                }
                                else
                                {
                                    // something failed. we're going to undo what we did in the user
                                    // repository, because we couldnt update the salt value. if we don't
                                    // undo it then the user will never be able to login without admin
                                    // intervention
                                    boolean backoutData = userManager.modifyUserInformation(userAccount.getUsername(), userAccount.getGuid(), backout);
                                    boolean backoutSalt = userSec.updateUserSalt(userAccount.getGuid(), existingSalt, SaltType.RESET.name());

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("backoutData: {}", backoutData);
                                        DEBUGGER.debug("backoutSalt: {}", backoutSalt);
                                    }

                                    if (!(backoutData) && (!(backoutSalt)))
                                    {
                                        throw new AccountControlException("Failed to modify the user account and unable to revert to existing state.");
                                    }

                                    response.setRequestStatus(SecurityRequestStatus.FAILURE);
                                    response.setResponse("Failed to change the password associated with the user account");
                                }
                            }
                            else
                            {
                                response.setRequestStatus(SecurityRequestStatus.FAILURE);
                                response.setResponse("Failed to change the password associated with the user account");
                            }
                        }
                        else
                        {
                            throw new AccountControlException("Unable to generate new salt for provided user account.");
                        }
                    }
                    else
                    {
                        throw new AccountControlException("Unable to obtain existing salt value from datastore. Cannot continue.");
                    }
                }
                else
                {
                    throw new AccountControlException("Unable to obtain configured user salt. Cannot continue");
                }
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);
            
            throw new AccountControlException(sqx.getMessage(), sqx);
        }
        catch (UserManagementException umx)
        {
            ERROR_RECORDER.error(umx.getMessage(), umx);
            
            throw new AccountControlException(umx.getMessage(), umx);
        }
        catch (AuthenticatorException ax)
        {
            ERROR_RECORDER.error(ax.getMessage(), ax);
            
            throw new AccountControlException(ax.getMessage(), ax);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.ADDSECURITY);
                auditEntry.setUserAccount(requestor);
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

        final UserAccount requestor = request.getRequestor();
        final RequestHostInfo reqInfo = request.getHostInfo();
        final UserAccount searchUser = request.getUserAccount();
        final SearchRequestType searchType = request.getSearchType();

        if (DEBUG)
        {
            DEBUGGER.debug("UserAccount: {}", requestor);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount: {}", searchUser);
            DEBUGGER.debug("SearchRequestType: {}", searchType);
        }

        try
        {
            boolean isUserAuthorized = false;

            if (searchType != SearchRequestType.FORGOTUID)
            {
                isUserAuthorized = adminControl.adminControlService(requestor, AdminControlType.USER_ADMIN);

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
                        userList = userManager.searchUsers(SearchRequestType.EMAILADDR, searchUser.getEmailAddr());

                        break;
                    default:
                        String searchData = null;
                        SearchRequestType requestType = null;

                        for (Field field : searchUser.getClass().getDeclaredFields())
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
                                    (!(field.getName().equals("serialVersionUID"))))
                            {
                                try
                                {
                                    if (field.get(searchUser) != null)
                                    {
                                        if (DEBUG)
                                        {
                                            DEBUGGER.debug("Value: {}", field.get(searchUser));
                                        }

                                        searchData = (String) field.get(searchUser);
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
                                    // don't do anything with it
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
                        response.setResponse("Multiple accounts were located with the provided information.");
                    }
                    else
                    {
                        List<UserAccount> userAccounts = new ArrayList<UserAccount>();

                        for (String[] userData : userList)
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

                        if (DEBUG)
                        {
                            DEBUGGER.debug("userAccounts: {}", userAccounts);
                        }

                        response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                        response.setResponse("Successfully loaded matching accounts");
                        response.setUserList(userAccounts);
                    }
                }
                else
                {
                    response.setRequestStatus(SecurityRequestStatus.FAILURE);
                    response.setResponse("No accounts were found with the provided data");
                }
            }
            else
            {
                response.setRequestStatus(SecurityRequestStatus.FAILURE);
                response.setResponse("Requesting user account was NOT authorized to perform the operation");
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
                    auditEntry.setUserAccount(requestor);
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

        final UserAccount requestor = request.getRequestor();
        final RequestHostInfo reqInfo = request.getHostInfo();
        final UserAccount searchUser = request.getUserAccount();
        final SearchRequestType searchType = request.getSearchType();

        if (DEBUG)
        {
            DEBUGGER.debug("UserAccount: {}", requestor);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount: {}", searchUser);
            DEBUGGER.debug("SearchRequestType: {}", searchType);
        }

        try
        {
            boolean isUserAuthorized = false;

            if (searchType != SearchRequestType.FORGOTUID)
            {
                isUserAuthorized = adminControl.adminControlService(requestor, AdminControlType.USER_ADMIN);

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
                List<Object> userData = userManager.loadUserAccount(searchUser.getGuid());

                if (DEBUG)
                {
                    DEBUGGER.debug("List<Object>: {}", userData);
                }

                if ((userData != null) && (!(userData.isEmpty())))
                {
                    UserAccount userAccount = new UserAccount();
                    userAccount.setGuid((String) userData.get(0));
                    userAccount.setUsername((String) userData.get(1));
                    userAccount.setGivenName((String) userData.get(2));
                    userAccount.setSurname((String) userData.get(3));
                    userAccount.setDisplayName((String) userData.get(4));
                    userAccount.setEmailAddr((String) userData.get(5));
                    userAccount.setRole(Role.valueOf((String) userData.get(6)));
                    userAccount.setFailedCount((Integer) userData.get(7));
                    userAccount.setLastLogin(new Date((Long) userData.get(8)));
                    userAccount.setExpiryDate((Long) userData.get(9));
                    userAccount.setSuspended((Boolean) userData.get(10));
                    userAccount.setOlrSetup((Boolean) userData.get(11));
                    userAccount.setOlrLocked((Boolean) userData.get(12));
                    userAccount.setTcAccepted((Boolean) userData.get(13));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("UserAccount: {}", userAccount);
                    }

                    response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                    response.setResponse("Successfully loaded user account");
                    response.setUserAccount(userAccount);
                }
                else
                {
                    response.setRequestStatus(SecurityRequestStatus.FAILURE);
                    response.setResponse("No accounts were found with the provided data");
                }
            }
            else
            {
                response.setRequestStatus(SecurityRequestStatus.FAILURE);
                response.setResponse("Requesting user account was NOT authorized to perform the operation");
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
                    auditEntry.setUserAccount(requestor);
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

        final UserAccount requestor = request.getRequestor();
        final RequestHostInfo reqInfo = request.getHostInfo();
        final UserAccount searchUser = request.getUserAccount();
        final SearchRequestType searchType = request.getSearchType();

        if (DEBUG)
        {
            DEBUGGER.debug("UserAccount: {}", requestor);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount: {}", searchUser);
            DEBUGGER.debug("SearchRequestType: {}", searchType);
        }

        try
        {
            boolean isUserAuthorized = adminControl.adminControlService(requestor, AdminControlType.USER_ADMIN);

            if (DEBUG)
            {
                DEBUGGER.debug("isUserAuthorized: {}", isUserAuthorized);
            }

            if (isUserAuthorized)
            {
                // get the user info
                UserAccount userAccount = null;
                List<Object> userData = userManager.loadUserAccount(searchUser.getGuid());

                if (DEBUG)
                {
                    DEBUGGER.debug("List<Object>: {}", userData);
                }

                if ((userData != null) && (!(userData.isEmpty())))
                {
                    userAccount = new UserAccount();
                    userAccount.setGuid((String) userData.get(0));
                    userAccount.setUsername((String) userData.get(1));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("UserAccount: {}", userAccount);
                    }
                }

                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setUserAccount(searchUser);

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
                        response.setUserAccount(userAccount);
                        response.setEntryCount(auditResponse.getEntryCount());
                        response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                        response.setResponse("Successfully loaded audit trail");
                        response.setAuditEntries(auditEntries);
                    }
                    else
                    {
                        response.setRequestStatus(SecurityRequestStatus.FAILURE);
                        response.setResponse("No audit history was located for the provided user.");
                    }
                }
            }
            else
            {
                response.setRequestStatus(SecurityRequestStatus.FAILURE);
                response.setResponse("Requesting user account was NOT authorized to perform the operation");
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
                auditEntry.setUserAccount(requestor);
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
