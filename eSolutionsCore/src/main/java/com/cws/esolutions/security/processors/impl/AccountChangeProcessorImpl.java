/**
 * Copyright (c) 2009 - 2013 By: CWS, Inc.
 * 
 * All rights reserved. These materials are confidential and
 * proprietary to CaspersBox Web Services N.A and no part of
 * these materials should be reproduced, published in any form
 * by any means, electronic or mechanical, including photocopy
 * or any information storage or retrieval system not should
 * the materials be disclosed to third parties without the
 * express written authorization of CaspersBox Web Services, N.A.
 */
package com.cws.esolutions.security.processors.impl;

import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Calendar;
import java.util.ArrayList;
import java.sql.SQLException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.RandomStringUtils;

import com.cws.esolutions.security.enums.SaltType;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.config.KeyConfig;
import com.cws.esolutions.security.dto.UserSecurity;
import com.cws.esolutions.security.utils.PasswordUtils;
import com.cws.esolutions.security.audit.dto.AuditEntry;
import com.cws.esolutions.security.audit.enums.AuditType;
import com.cws.esolutions.security.audit.dto.AuditRequest;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.keymgmt.interfaces.KeyManager;
import com.cws.esolutions.security.keymgmt.dto.KeyManagementRequest;
import com.cws.esolutions.security.keymgmt.dto.KeyManagementResponse;
import com.cws.esolutions.security.processors.dto.AccountChangeRequest;
import com.cws.esolutions.security.keymgmt.factory.KeyManagementFactory;
import com.cws.esolutions.security.processors.dto.AccountChangeResponse;
import com.cws.esolutions.security.audit.exception.AuditServiceException;
import com.cws.esolutions.security.keymgmt.exception.KeyManagementException;
import com.cws.esolutions.security.processors.exception.AccountChangeException;
import com.cws.esolutions.security.processors.interfaces.IAccountChangeProcessor;
import com.cws.esolutions.security.dao.userauth.exception.AuthenticatorException;
import com.cws.esolutions.security.dao.usermgmt.exception.UserManagementException;
/**
 * eSolutionsCore
 * com.cws.esolutions.security.processors.impl
 * AccountChangeProcessorImpl.java
 *
 * TODO: Add class description
 *
 * $Id: cws-codetemplates.xml 2286 2013-01-03 20:50:12Z kmhuntly@gmail.com $
 * $Author: kmhuntly@gmail.com $
 * $Date: 2013-01-03 15:50:12 -0500 (Thu, 03 Jan 2013) $
 * $Revision: 2286 $
 * @author khuntly
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * khuntly @ Nov 14, 2013 12:27:04 PM
 *     Created.
 */
public class AccountChangeProcessorImpl implements IAccountChangeProcessor
{
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
            response.setResponse("The requesting user was NOT authorized to perform the operation");

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
                    UserAccount retAccount = userAccount;
                    retAccount.setEmailAddr(userAccount.getEmailAddr());

                    response.setUserAccount(retAccount);
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
            response.setResponse("The requesting user was NOT authorized to perform the operation");

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
                                svcBean.getConfigData().getSecurityConfig().getAuthAlgorithm(),
                                svcBean.getConfigData().getSecurityConfig().getIterations()),
                        request.getApplicationName());

                Map<String, Object> requestMap = new HashMap<String, Object>();
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
            response.setResponse("The requesting user was NOT authorized to perform the operation");

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
                        currentPassword = PasswordUtils.encryptText(reqSecurity.getPassword(), userSalt, secConfig.getAuthAlgorithm(), secConfig.getIterations());

                        // we aren't getting the data back here because we don't need it. if the request
                        // fails we'll get an exception and not process further. this might not be the
                        // best flow control, but it does exactly what we need where we need it.
                        authenticator.performLogon(userAccount.getGuid(), userAccount.getUsername(), currentPassword, request.getApplicationName());
                    }
                }

                // ok, thats out of the way. lets keep moving.
                String newUserSalt = RandomStringUtils.randomAlphanumeric(secConfig.getSaltLength());

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

                            // make the modification in the user repository
                            isComplete = authenticator.changeUserPassword(userAccount.getGuid(),
                                    PasswordUtils.encryptText(
                                            newPassword,
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
                                if (!(request.isReset()))
                                {
                                    // something failed. we're going to undo what we did in the user
                                    // repository, because we couldnt update the salt value. if we don't
                                    // undo it then the user will never be able to login without admin
                                    // intervention
                                    boolean isBackedOut = authenticator.changeUserPassword(userAccount.getUsername(), currentPassword, userAccount.getExpiryDate());

                                    if (!(isBackedOut))
                                    {
                                        throw new AccountChangeException("Failed to modify the user account and unable to revert to existing state.");
                                    }
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
            response.setResponse("The requesting user was NOT authorized to perform the operation");

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
                    String password = PasswordUtils.encryptText(reqSecurity.getPassword(), userSalt, secConfig.getAuthAlgorithm(), secConfig.getIterations());

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

                            Map<String, Object> backout = new HashMap<String, Object>();
                            backout.put(authData.getSecQuestionOne(), currentSec.get(0));
                            backout.put(authData.getSecQuestionTwo(), currentSec.get(1));
                            backout.put(authData.getSecAnswerOne(), currentSec.get(2));
                            backout.put(authData.getSecAnswerTwo(), currentSec.get(3));

                            String secAnswerOne = PasswordUtils.encryptText(reqSecurity.getSecAnswerOne(), newUserSalt);
                            String secAnswerTwo = PasswordUtils.encryptText(reqSecurity.getSecAnswerTwo(), newUserSalt);

                            // good, move forward
                            // make the modification in the user repository
                            Map<String, Object> changeMap = new HashMap<String, Object>();
                            changeMap.put(authData.getSecQuestionOne(), reqSecurity.getSecQuestionOne());
                            changeMap.put(authData.getSecQuestionTwo(), reqSecurity.getSecQuestionTwo());
                            changeMap.put(authData.getSecAnswerOne(), secAnswerOne);
                            changeMap.put(authData.getSecAnswerTwo(), secAnswerTwo);

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

                                    if (!(backoutData) && (!(backoutSalt)))
                                    {
                                        throw new AccountChangeException("Failed to modify the user account and unable to revert to existing state.");
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
                            throw new AccountChangeException("Unable to generate new salt for provided user account.");
                        }
                    }
                    else
                    {
                        throw new AccountChangeException("Unable to obtain existing salt value from datastore. Cannot continue.");
                    }
                }
                else
                {
                    throw new AccountChangeException("Unable to obtain configured user salt. Cannot continue");
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
        final KeyConfig keyConfig = svcBean.getConfigData().getKeyConfig();

        if (DEBUG)
        {
            DEBUGGER.debug("Calendar: {}", calendar);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("UserAccount: {}", requestor);
            DEBUGGER.debug("KeyConfig: {}", keyConfig);
        }

        if (!(StringUtils.equals(userAccount.getGuid(), requestor.getGuid())))
        {
            // requesting user is not the same as the user being reset. authorize
            response.setRequestStatus(SecurityRequestStatus.UNAUTHORIZED);
            response.setResponse("The requesting user was NOT authorized to perform the operation");

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

            KeyManagementRequest keyRequest = new KeyManagementRequest();
            keyRequest.setGuid(userAccount.getGuid());
            keyRequest.setKeySize(keyConfig.getKeySize());
            keyRequest.setPubKeyField(authData.getPublicKey());
            keyRequest.setKeyAlgorithm(keyConfig.getKeyAlgorithm());
            keyRequest.setKeyDirectory(FileUtils.getFile(keyConfig.getKeyDirectory()));

            if (DEBUG)
            {
                DEBUGGER.debug("KeyManagementRequest: {}", keyRequest);
            }

            // delete the existing keys
            KeyManagementResponse deleteResponse = keyManager.removeKeys(keyRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("KeyManagementResponse: {}", deleteResponse);
            }

            if (deleteResponse.getRequestStatus() == SecurityRequestStatus.SUCCESS)
            {
                // good, now re-generate
                KeyManagementResponse createResponse = keyManager.createKeys(keyRequest);

                if (DEBUG)
                {
                    DEBUGGER.debug("KeyManagementResponse: {}", createResponse);
                }

                if (createResponse.getRequestStatus() == SecurityRequestStatus.SUCCESS)
                {
                    // get the new keypair
                    KeyManagementResponse retrResponse = keyManager.returnKeys(keyRequest);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("KeyManagementResponse: {}", retrResponse);
                    }

                    if (retrResponse.getRequestStatus() == SecurityRequestStatus.SUCCESS)
                    {
                        // all done
                        UserAccount retAccount = userAccount;
                        retAccount.setUserKeys(retrResponse.getKeyPair());

                        if (DEBUG)
                        {
                            DEBUGGER.debug("UserAccount: {}", retAccount);
                        }

                        response.setUserAccount(retAccount);
                        response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                        response.setResponse("Successfully reloaded user keys");
                    }
                    else
                    {
                        response.setRequestStatus(SecurityRequestStatus.FAILURE);
                        response.setResponse("An error occurred while retrieving the new keypair. Keys may not have generated successfully.");
                    }
                }
                else
                {
                    throw new AccountChangeException("Failed to generate new keys for the provided user.");
                }
            }
            else
            {
                throw new AccountChangeException("Failed to remove existing keypair for the provided user.");
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

    @Override
    public AccountChangeResponse createSecurityData(final AccountChangeRequest request) throws AccountChangeException
    {
        final String methodName = IAccountChangeProcessor.CNAME + "#createSecurityData(final CreateUserRequest createReq) throws AccountChangeException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AccountChangeRequest: {}", request);
        }

        AccountChangeResponse response = new AccountChangeResponse();

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
			DEBUGGER.debug("UserAccount: {}", requestor);
        }

        if (!(StringUtils.equals(userAccount.getGuid(), requestor.getGuid())))
        {
            // requesting user is not the same as the user being reset. authorize
            response.setRequestStatus(SecurityRequestStatus.UNAUTHORIZED);
            response.setResponse("The requesting user was NOT authorized to perform the operation");

            return response;
        }

        try
        {
            boolean isSaltInserted = userSec.addUserSalt(userAccount.getGuid(), newUserSalt, SaltType.RESET.name());

            if (DEBUG)
            {
                DEBUGGER.debug("isSaltInserted: {}", isSaltInserted);
            }

            if (isSaltInserted)
            {
                String secAnswerOne = PasswordUtils.encryptText(userSecurity.getSecAnswerOne(), newUserSalt, secConfig.getAuthAlgorithm(), secConfig.getIterations());
                String secAnswerTwo = PasswordUtils.encryptText(userSecurity.getSecAnswerTwo(), newUserSalt, secConfig.getAuthAlgorithm(), secConfig.getIterations());

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

                boolean isComplete = authenticator.createSecurityData(userAccount.getUsername(), userAccount.getGuid(), securityList);

                if (DEBUG)
                {
                    DEBUGGER.debug("isComplete: {}", isComplete);
                }

                if (isComplete)
                {
                    UserAccount retAccount = userAccount;
                    retAccount.setOlrSetup(false);

                    response.setUserAccount(retAccount);
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
                throw new AccountChangeException("Failed to generate salt for request. Cannot continue.");
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
}
