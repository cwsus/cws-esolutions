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
 * File: AccountChangeProcessorImpl.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Calendar;
import java.sql.SQLException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.RandomStringUtils;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.dto.UserSecurity;
import com.cws.esolutions.security.utils.PasswordUtils;
import com.cws.esolutions.security.processors.enums.SaltType;
import com.cws.esolutions.security.processors.dto.AuditEntry;
import com.cws.esolutions.security.processors.enums.AuditType;
import com.cws.esolutions.security.processors.dto.AuditRequest;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.security.dao.keymgmt.interfaces.KeyManager;
import com.cws.esolutions.security.processors.dto.AccountChangeRequest;
import com.cws.esolutions.security.processors.dto.AccountChangeResponse;
import com.cws.esolutions.security.dao.keymgmt.factory.KeyManagementFactory;
import com.cws.esolutions.security.processors.exception.AuditServiceException;
import com.cws.esolutions.security.processors.exception.AccountChangeException;
import com.cws.esolutions.security.dao.keymgmt.exception.KeyManagementException;
import com.cws.esolutions.security.processors.interfaces.IAccountChangeProcessor;
import com.cws.esolutions.security.dao.userauth.exception.AuthenticatorException;
import com.cws.esolutions.security.dao.usermgmt.exception.UserManagementException;
/**
 * @see com.cws.esolutions.security.processors.interfaces.IAccountChangeProcessor
 */
public class AccountChangeProcessorImpl implements IAccountChangeProcessor
{
    /**
     * @see com.cws.esolutions.security.processors.interfaces.IAccountChangeProcessor#changeUserEmail(com.cws.esolutions.security.processors.dto.AccountChangeRequest)
     */
    @Override
    public AccountChangeResponse changeUserEmail(final AccountChangeRequest request) throws AccountChangeException
    {
        final String methodName = IAccountChangeProcessor.CNAME + "#changeUserEmail(final AccountChangeRequest request) throws AccountChangeException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AccountChangeRequest: {}", request);
        }

        AccountChangeResponse response = new AccountChangeResponse();

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

        // ok, first things first. if this is an administrative reset, make sure the requesting user
        // is authorized to perform the action.
        if (!(StringUtils.equals(userAccount.getGuid(), requestor.getGuid())))
        {
            // requesting user is not the same as the user being reset. no authorization here,
            // no one is allowed to change user security but the owning user
            response.setRequestStatus(SecurityRequestStatus.UNAUTHORIZED);

            return response;
        }

        try
        {
            // ok, authenticate first
            String userSalt = userSec.getUserSalt(userAccount.getGuid(), SaltType.LOGON.name());

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
                                secBean.getConfigData().getSecurityConfig().getAuthAlgorithm(),
                                secBean.getConfigData().getSecurityConfig().getIterations()));

                Map<String, Object> requestMap = new HashMap<>();
                requestMap.put(authData.getEmailAddr(), userAccount.getEmailAddr());

                if (DEBUG)
                {
                    DEBUGGER.debug("Request Map: {}", requestMap);
                }

                boolean isComplete = userManager.modifyUserInformation(userAccount.getUsername(), userAccount.getGuid(), requestMap);

                if (isComplete)
                {
                    UserAccount retAccount = userAccount;
                    retAccount.setEmailAddr(userAccount.getEmailAddr());

                    response.setUserAccount(retAccount);
                    response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                }
                else
                {
                    response.setRequestStatus(SecurityRequestStatus.FAILURE);
                }
            }
            else
            {
                throw new AccountChangeException("Unable to obtain configured user salt. Cannot continue");
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);
            
            throw new AccountChangeException(sqx.getMessage(), sqx);
        }
        catch (AuthenticatorException ax)
        {
            ERROR_RECORDER.error(ax.getMessage(), ax);

            throw new AccountChangeException(ax.getMessage(), ax);
        }
        catch (UserManagementException umx)
        {
            ERROR_RECORDER.error(umx.getMessage(), umx);

            throw new AccountChangeException(umx.getMessage(), umx);
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

    /**
     * @see com.cws.esolutions.security.processors.interfaces.IAccountChangeProcessor#changeUserContact(com.cws.esolutions.security.processors.dto.AccountChangeRequest)
     */
    @Override
    public AccountChangeResponse changeUserContact(final AccountChangeRequest request) throws AccountChangeException
    {
        final String methodName = IAccountChangeProcessor.CNAME + "#changeUserContact(final AccountChangeRequest request) throws AccountChangeException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AccountChangeRequest: {}", request);
        }

        AccountChangeResponse response = new AccountChangeResponse();

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

        // ok, first things first. if this is an administrative reset, make sure the requesting user
        // is authorized to perform the action.
        if (!(StringUtils.equals(userAccount.getGuid(), requestor.getGuid())))
        {
            // requesting user is not the same as the user being reset. no authorization here,
            // no one is allowed to change user security but the owning user
            response.setRequestStatus(SecurityRequestStatus.UNAUTHORIZED);

            return response;
        }

        try
        {
            // ok, authenticate first
            String userSalt = userSec.getUserSalt(userAccount.getGuid(), SaltType.LOGON.name());

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
                                secBean.getConfigData().getSecurityConfig().getAuthAlgorithm(),
                                secBean.getConfigData().getSecurityConfig().getIterations()));

                Map<String, Object> requestMap = new HashMap<>();
                requestMap.put(authData.getTelephoneNumber(), userAccount.getTelephoneNumber());
                requestMap.put(authData.getPagerNumber(), userAccount.getPagerNumber());

                if (DEBUG)
                {
                    DEBUGGER.debug("Request Map: {}", requestMap);
                }

                boolean isComplete = userManager.modifyUserInformation(userAccount.getUsername(), userAccount.getGuid(), requestMap);

                if (isComplete)
                {
                    UserAccount retAccount = userAccount;
                    retAccount.setTelephoneNumber(userAccount.getTelephoneNumber());
                    retAccount.setPagerNumber(userAccount.getPagerNumber());

                    response.setUserAccount(retAccount);
                    response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                }
                else
                {
                    response.setRequestStatus(SecurityRequestStatus.FAILURE);
                }
            }
            else
            {
                throw new AccountChangeException("Unable to obtain configured user salt. Cannot continue");
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);
            
            throw new AccountChangeException(sqx.getMessage(), sqx);
        }
        catch (AuthenticatorException ax)
        {
            ERROR_RECORDER.error(ax.getMessage(), ax);

            throw new AccountChangeException(ax.getMessage(), ax);
        }
        catch (UserManagementException umx)
        {
            ERROR_RECORDER.error(umx.getMessage(), umx);

            throw new AccountChangeException(umx.getMessage(), umx);
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

    /**
     * @see com.cws.esolutions.security.processors.interfaces.IAccountChangeProcessor#changeUserPassword(com.cws.esolutions.security.processors.dto.AccountChangeRequest)
     */
    @Override
    public AccountChangeResponse changeUserPassword(final AccountChangeRequest request) throws AccountChangeException
    {
        final String methodName = IAccountChangeProcessor.CNAME + "#changeUserPassword(final AccountChangeRequest request) throws AccountChangeException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AccountChangeRequest: {}", request);
        }

        // List<String> authList = null;
        String currentPassword = null;
        AccountChangeResponse response = new AccountChangeResponse();

        final Calendar calendar = Calendar.getInstance();
        final RequestHostInfo reqInfo = request.getHostInfo();
        final UserAccount requestor = request.getRequestor();
        final UserAccount userAccount = request.getUserAccount();
        final UserSecurity reqSecurity = request.getUserSecurity();
        final String newUserSalt = RandomStringUtils.randomAlphanumeric(secConfig.getSaltLength());

        calendar.add(Calendar.DATE, secConfig.getPasswordExpiration());

        if (DEBUG)
        {
            DEBUGGER.debug("Calendar: {}", calendar);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount: {}", requestor);
            DEBUGGER.debug("UserAccount: {}", userAccount);
        }

        // ok, first things first. if this is an administrative reset, make sure the requesting user
        // is authorized to perform the action.
        if (!(StringUtils.equals(userAccount.getGuid(), requestor.getGuid())))
        {
            // requesting user is not the same as the user being reset. no authorization here,
            // no one is allowed to change user security but the owning user
            response.setRequestStatus(SecurityRequestStatus.UNAUTHORIZED);

            return response;
        }

        try
        {
            // otherwise, keep going
            // make sure the new password isnt the same as the existing
            if (StringUtils.equals(reqSecurity.getNewPassword(), reqSecurity.getPassword()))
            {
                throw new AccountChangeException("The new password MUST differ from the existing password.");
            }
            else if ((reqSecurity.getNewPassword().length() < secConfig.getPasswordMinLength()) // less than minimum
                    || (reqSecurity.getNewPassword().length() > secConfig.getPasswordMaxLength())) // greater than maximum
            {
                // password doesnt meet requirements, is either too short or too long
                throw new AccountChangeException("The chosen password does not meet the configured length requirements.");
            }
            else
            {
                if (!(request.isReset()))
                {
                    // ok, authenticate first
                    String userSalt = userSec.getUserSalt(userAccount.getGuid(), SaltType.LOGON.name());

                    if (StringUtils.isNotEmpty(userSalt))
                    {
                        // we aren't getting the data back here because we don't need it. if the request
                        // fails we'll get an exception and not process further. this might not be the
                        // best flow control, but it does exactly what we need where we need it.
                        authenticator.performLogon(userAccount.getGuid(), userAccount.getUsername(),
                                PasswordUtils.encryptText(reqSecurity.getPassword(), userSalt, secConfig.getAuthAlgorithm(), secConfig.getIterations()));
                    }
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
                            // make the modification in the user repository
                            userManager.changeUserPassword(userAccount.getGuid(),
                                    PasswordUtils.encryptText(reqSecurity.getNewPassword(), newUserSalt,
                                            secConfig.getAuthAlgorithm(), secConfig.getIterations()), calendar.getTimeInMillis());

                            if (DEBUG)
                            {
                                DEBUGGER.debug("isComplete: {}", isComplete);
                            }

                            if (isComplete)
                            {
                                if (userAccount.getStatus() == LoginStatus.EXPIRED)
                                {
                                    // update the account
                                    userAccount.setStatus(LoginStatus.SUCCESS);
                                }
                                else if (userAccount.getStatus() == LoginStatus.RESET)
                                {
                                    // remove the reset request
                                    boolean isRemoved = userSec.removeResetData(userAccount.getGuid(), reqSecurity.getResetRequestId());

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("isRemoved: {}", isRemoved);
                                    }

                                    if (!(isRemoved))
                                    {
                                        ERROR_RECORDER.error("Failed to remove provided reset request from datastore");
                                    }
                                }

                                response.setUserAccount(userAccount);
                                response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                            }
                            else
                            {
                                if (!(request.isReset()))
                                {
                                    // something failed. we're going to undo what we did in the user
                                    // repository, because we couldnt update the salt value. if we don't
                                    // undo it then the user will never be able to login without admin
                                    // intervention
                                    boolean isBackedOut = userManager.changeUserPassword(userAccount.getUsername(), currentPassword, userAccount.getExpiryDate());

                                    if (!(isBackedOut))
                                    {
                                        throw new AccountChangeException("Failed to modify the user account and unable to revert to existing state.");
                                    }
                                }

                                response.setRequestStatus(SecurityRequestStatus.FAILURE);
                            }
                        }
                        else
                        {
                            response.setRequestStatus(SecurityRequestStatus.FAILURE);
                        }
                    }
                    else
                    {
                        throw new AccountChangeException("Unable to obtain existing salt value from datastore. Cannot continue.");
                    }
                }
                else
                {
                    throw new AccountChangeException("Unable to generate new salt for provided user account.");
                }
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);
            
            throw new AccountChangeException(sqx.getMessage(), sqx);
        }
        catch (UserManagementException umx)
        {
            ERROR_RECORDER.error(umx.getMessage(), umx);

            throw new AccountChangeException(umx.getMessage(), umx);
        }
        catch (AuthenticatorException ax)
        {
            ERROR_RECORDER.error(ax.getMessage(), ax);

            throw new AccountChangeException(ax.getMessage(), ax);
        }
        catch (SecurityException sx)
        {
            ERROR_RECORDER.error(sx.getMessage(), sx);

            throw new AccountChangeException(sx.getMessage(), sx);
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

    /**
     * @see com.cws.esolutions.security.processors.interfaces.IAccountChangeProcessor#changeUserSecurity(com.cws.esolutions.security.processors.dto.AccountChangeRequest)
     */
    @Override
    public AccountChangeResponse changeUserSecurity(final AccountChangeRequest request) throws AccountChangeException
    {
        final String methodName = IAccountChangeProcessor.CNAME + "#changeUserSecurity(final AccountChangeRequest request) throws AccountChangeException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AccountChangeRequest: {}", request);
        }

        AccountChangeResponse response = new AccountChangeResponse();

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

        // ok, first things first. if this is an administrative reset, make sure the requesting user
        // is authorized to perform the action.
        if (!(StringUtils.equals(userAccount.getGuid(), requestor.getGuid())))
        {
            // requesting user is not the same as the user being reset. no authorization here,
            // no one is allowed to change user security but the owning user
            response.setRequestStatus(SecurityRequestStatus.UNAUTHORIZED);

            return response;
        }

        try
        {
            // otherwise, keep going
            // make sure the two questions and answers arent the same
            if ((StringUtils.equals(reqSecurity.getSecQuestionOne(), reqSecurity.getSecQuestionTwo())))
            {
                throw new AccountChangeException("The security questions must be different.");
            }
            else if ((StringUtils.equals(reqSecurity.getSecAnswerOne(), reqSecurity.getSecAnswerTwo())))
            {
                throw new AccountChangeException("The security answers must be different.");
            }
            else
            {
                // ok, authenticate first
                String userSalt = userSec.getUserSalt(userAccount.getGuid(), SaltType.LOGON.name());

                if (StringUtils.isNotEmpty(userSalt))
                {
                    // we aren't getting the data back here because we don't need it. if the request
                    // fails we'll get an exception and not process further. this might not be the
                    // best flow control, but it does exactly what we need where we need it.
                    authenticator.performLogon(userAccount.getGuid(), userAccount.getUsername(),
                            PasswordUtils.encryptText(reqSecurity.getPassword(), userSalt,
                                    secConfig.getAuthAlgorithm(), secConfig.getIterations()));

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

                            Map<String, Object> backout = new HashMap<>();
                            backout.put(authData.getSecQuestionOne(), currentSec.get(0));
                            backout.put(authData.getSecQuestionTwo(), currentSec.get(1));
                            backout.put(authData.getSecAnswerOne(), currentSec.get(2));
                            backout.put(authData.getSecAnswerTwo(), currentSec.get(3));

                            // good, move forward
                            // make the modification in the user repository
                            Map<String, Object> changeMap = new HashMap<>();
                            changeMap.put(authData.getSecQuestionOne(), reqSecurity.getSecQuestionOne());
                            changeMap.put(authData.getSecQuestionTwo(), reqSecurity.getSecQuestionTwo());
                            changeMap.put(authData.getSecAnswerOne(), PasswordUtils.encryptText(reqSecurity.getSecAnswerOne(), newUserSalt,
                                    secConfig.getAuthAlgorithm(), secConfig.getIterations()));
                            changeMap.put(authData.getSecAnswerTwo(), PasswordUtils.encryptText(reqSecurity.getSecAnswerTwo(), newUserSalt,
                                    secConfig.getAuthAlgorithm(), secConfig.getIterations()));

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
                                }
                                else
                                {
                                    // something failed. we're going to undo what we did in the user
                                    // repository, because we couldnt update the salt value. if we don't
                                    // undo it then the user will never be able to login without admin
                                    // intervention
                                    boolean backoutData = userManager.modifyUserInformation(userAccount.getUsername(), userAccount.getGuid(), backout);
                                    boolean backoutSalt = userSec.updateUserSalt(userAccount.getGuid(), existingSalt, SaltType.RESET.name());

                                    if (!(backoutData) && (!(backoutSalt)))
                                    {
                                        throw new AccountChangeException("Failed to modify the user account and unable to revert to existing state.");
                                    }

                                    response.setRequestStatus(SecurityRequestStatus.FAILURE);
                                }
                            }
                            else
                            {
                                response.setRequestStatus(SecurityRequestStatus.FAILURE);
                            }
                        }
                        else
                        {
                            ERROR_RECORDER.error("Unable to generate new salt for provided user account.");

                            response.setRequestStatus(SecurityRequestStatus.FAILURE);
                        }
                    }
                    else
                    {
                        ERROR_RECORDER.error("Unable to obtain existing salt value from datastore. Cannot continue.");

                        response.setRequestStatus(SecurityRequestStatus.FAILURE);
                    }
                }
                else
                {
                    ERROR_RECORDER.error("Unable to obtain configured user salt. Cannot continue");

                    response.setRequestStatus(SecurityRequestStatus.FAILURE);
                }
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);
            
            throw new AccountChangeException(sqx.getMessage(), sqx);
        }
        catch (UserManagementException umx)
        {
            ERROR_RECORDER.error(umx.getMessage(), umx);
            
            throw new AccountChangeException(umx.getMessage(), umx);
        }
        catch (AuthenticatorException ax)
        {
            ERROR_RECORDER.error(ax.getMessage(), ax);
            
            throw new AccountChangeException(ax.getMessage(), ax);
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

    /**
     * @see com.cws.esolutions.security.processors.interfaces.IAccountChangeProcessor#changeUserKeys(com.cws.esolutions.security.processors.dto.AccountChangeRequest)
     */
    @Override
    public AccountChangeResponse changeUserKeys(final AccountChangeRequest request) throws AccountChangeException
    {
        final String methodName = IAccountChangeProcessor.CNAME + "#changeUserKeys(final AccountChangeRequest request) throws AccountChangeException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AccountChangeRequest: {}", request);
        }

        AccountChangeResponse response = new AccountChangeResponse();

        final Calendar calendar = Calendar.getInstance();
        final RequestHostInfo reqInfo = request.getHostInfo();
        final UserAccount userAccount = request.getUserAccount();
        final UserAccount requestor = request.getRequestor();

        if (DEBUG)
        {
            DEBUGGER.debug("Calendar: {}", calendar);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("UserAccount: {}", requestor);
        }

        if (!(StringUtils.equals(userAccount.getGuid(), requestor.getGuid())))
        {
            // requesting user is not the same as the user being reset. authorize
            response.setRequestStatus(SecurityRequestStatus.UNAUTHORIZED);

            return response;
        }

        try
        {
            // get the user keypair
            KeyManager keyManager = KeyManagementFactory.getKeyManager(keyConfig.getKeyManager());

            if (DEBUG)
            {
                DEBUGGER.debug("KeyManager: {}", keyManager);
            }

            // delete the existing keys
            boolean keysRemoved = keyManager.removeKeys(userAccount.getGuid());

            if (DEBUG)
            {
                DEBUGGER.debug("keysRemoved: {}", keysRemoved);
            }

            if (keysRemoved)
            {
                // good, now re-generate
                boolean keysAdded = keyManager.createKeys(userAccount.getGuid());

                if (DEBUG)
                {
                    DEBUGGER.debug("keysAdded: {}", keysAdded);
                }

                if (keysAdded)
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
                response.setRequestStatus(SecurityRequestStatus.FAILURE);
            }
        }
        catch (KeyManagementException kmx)
        {
            ERROR_RECORDER.error(kmx.getMessage(), kmx);

            throw new AccountChangeException(kmx.getMessage(), kmx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.CHANGEKEYS);
                auditEntry.setUserAccount(userAccount);
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
