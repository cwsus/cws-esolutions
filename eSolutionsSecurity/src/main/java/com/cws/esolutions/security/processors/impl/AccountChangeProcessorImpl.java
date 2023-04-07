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
import java.util.Date;
import java.util.Arrays;
import java.util.Objects;
import java.util.Calendar;
import java.util.ArrayList;
import java.security.KeyPair;
import java.sql.SQLException;
import org.apache.commons.lang3.StringUtils;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.config.xml.KeyConfig;
import com.cws.esolutions.security.processors.enums.SaltType;
import com.cws.esolutions.utility.securityutils.PasswordUtils;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.security.processors.dto.AccountChangeData;
import com.cws.esolutions.security.dao.keymgmt.interfaces.KeyManager;
import com.cws.esolutions.security.processors.dto.AuthenticationData;
import com.cws.esolutions.security.processors.dto.AccountChangeRequest;
import com.cws.esolutions.security.processors.dto.AccountChangeResponse;
import com.cws.esolutions.utility.securityutils.processors.dto.AuditEntry;
import com.cws.esolutions.utility.securityutils.processors.enums.AuditType;
import com.cws.esolutions.utility.securityutils.processors.dto.AuditRequest;
import com.cws.esolutions.security.dao.keymgmt.factory.KeyManagementFactory;
import com.cws.esolutions.utility.securityutils.processors.dto.RequestHostInfo;
import com.cws.esolutions.security.processors.exception.AccountChangeException;
import com.cws.esolutions.security.dao.keymgmt.exception.KeyManagementException;
import com.cws.esolutions.security.processors.interfaces.IAccountChangeProcessor;
import com.cws.esolutions.security.dao.usermgmt.exception.UserManagementException;
import com.cws.esolutions.utility.securityutils.processors.exception.AuditServiceException;
/**
 * @see com.cws.esolutions.security.processors.interfaces.IAccountChangeProcessor
 */
public class AccountChangeProcessorImpl implements IAccountChangeProcessor
{
    private static final String CNAME = AccountChangeProcessorImpl.class.getName();

    /**
     * @see com.cws.esolutions.security.processors.interfaces.IAccountChangeProcessor#changeUserEmail(com.cws.esolutions.security.processors.dto.AccountChangeRequest)
     */
    public AccountChangeResponse changeUserEmail(final AccountChangeRequest request) throws AccountChangeException
    {
        final String methodName = AccountChangeProcessorImpl.CNAME + "#changeUserEmail(final AccountChangeRequest request) throws AccountChangeException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AccountChangeRequest: {}", request);
        }

        AccountChangeResponse response = new AccountChangeResponse();

        final RequestHostInfo reqInfo = request.getHostInfo();
        final UserAccount requestor = request.getRequestor();
        final UserAccount userAccount = request.getUserAccount();

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
            boolean isComplete = userManager.modifyUserEmail(userAccount.getGuid(), userAccount.getEmailAddr());

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
        catch (final UserManagementException umx)
        {
            ERROR_RECORDER.error(umx.getMessage(), umx);

            throw new AccountChangeException(umx.getMessage(), umx);
        }
        catch (final SecurityException sx)
        {
            ERROR_RECORDER.error(sx.getMessage(), sx);

            throw new AccountChangeException(sx.getMessage(), sx);
        }
        finally
        {
            // audit
            if (secConfig.getPerformAudit())
            {
                // audit if a valid account. if not valid we cant audit much,
                // but we should try anyway. not sure how thats going to work
                try
                {
                    AuditEntry auditEntry = new AuditEntry();
                    auditEntry.setAuditType(AuditType.CHANGEEMAIL);
                    auditEntry.setAuditDate(new Date(System.currentTimeMillis()));
                    auditEntry.setSessionId(requestor.getSessionId());
                    auditEntry.setUserGuid(requestor.getGuid());
                    auditEntry.setUserName(requestor.getUsername());
                    auditEntry.setUserRole(requestor.getUserRole().toString());
                    auditEntry.setAuthorized(Boolean.TRUE);
                    auditEntry.setApplicationId(request.getApplicationId());
                    auditEntry.setApplicationName(request.getApplicationName());
    
                    if (DEBUG)
                    {
                        DEBUGGER.debug("AuditEntry: {}", auditEntry);
                    }
    
                    AuditRequest auditRequest = new AuditRequest();
                    auditRequest.setAuditEntry(auditEntry);
                    auditRequest.setHostInfo(reqInfo);
    
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
     * @see com.cws.esolutions.security.processors.interfaces.IAccountChangeProcessor#changeUserContact(com.cws.esolutions.security.processors.dto.AccountChangeRequest)
     */
    public AccountChangeResponse changeUserContact(final AccountChangeRequest request) throws AccountChangeException
    {
        final String methodName = AccountChangeProcessorImpl.CNAME + "#changeUserContact(final AccountChangeRequest request) throws AccountChangeException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AccountChangeRequest: {}", request);
        }

        AccountChangeResponse response = new AccountChangeResponse();

        final RequestHostInfo reqInfo = request.getHostInfo();
        final UserAccount requestor = request.getRequestor();
        final UserAccount userAccount = request.getUserAccount();

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
        	boolean isComplete = userManager.modifyUserContact(userAccount.getGuid(),
                    new ArrayList<String>(
                            Arrays.asList(
                                    userAccount.getTelephoneNumber(),
                                    userAccount.getPagerNumber())));

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
        catch (final UserManagementException umx)
        {
            ERROR_RECORDER.error(umx.getMessage(), umx);

            throw new AccountChangeException(umx.getMessage(), umx);
        }
        catch (final SecurityException sx)
        {
            ERROR_RECORDER.error(sx.getMessage(), sx);

            throw new AccountChangeException(sx.getMessage(), sx);
        }
        finally
        {
            // audit
            if (secConfig.getPerformAudit())
            {
                // audit if a valid account. if not valid we cant audit much,
                // but we should try anyway. not sure how thats going to work
                try
                {
                    AuditEntry auditEntry = new AuditEntry();
                    auditEntry.setAuditType(AuditType.CHANGECONTACT);
                    auditEntry.setAuditDate(new Date(System.currentTimeMillis()));
                    auditEntry.setSessionId(requestor.getSessionId());
                    auditEntry.setUserGuid(requestor.getGuid());
                    auditEntry.setUserName(requestor.getUsername());
                    auditEntry.setUserRole(requestor.getUserRole().toString());
                    auditEntry.setAuthorized(Boolean.TRUE);
                    auditEntry.setApplicationId(request.getApplicationId());
                    auditEntry.setApplicationName(request.getApplicationName());
    
                    if (DEBUG)
                    {
                        DEBUGGER.debug("AuditEntry: {}", auditEntry);
                    }
    
                    AuditRequest auditRequest = new AuditRequest();
                    auditRequest.setAuditEntry(auditEntry);
                    auditRequest.setHostInfo(reqInfo);
    
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
     * @see com.cws.esolutions.security.processors.interfaces.IAccountChangeProcessor#changeUserPassword(com.cws.esolutions.security.processors.dto.AccountChangeRequest)
     */
    public AccountChangeResponse changeUserPassword(final AccountChangeRequest request) throws AccountChangeException
    {
        final String methodName = AccountChangeProcessorImpl.CNAME + "#changeUserPassword(final AccountChangeRequest request) throws AccountChangeException";

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
        final AuthenticationData reqSecurity = request.getUserSecurity();
        final boolean isReset = request.getChangeData().getIsReset();

        calendar.add(Calendar.DATE, secConfig.getPasswordExpiration());

        if (DEBUG)
        {
            DEBUGGER.debug("Calendar: {}", calendar);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount: {}", requestor);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("AuthenticationData: {}", reqSecurity);
            DEBUGGER.debug("boolean: {}", isReset);
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
            if (Arrays.equals(reqSecurity.getNewPassword(), reqSecurity.getPassword()))
            {
                throw new AccountChangeException("The new password MUST differ from the existing password.");
            }
            else if ((reqSecurity.getNewPassword().length < secConfig.getPasswordMinLength()) // less than minimum
                    || (reqSecurity.getNewPassword().length > secConfig.getPasswordMaxLength())) // greater than maximum
            {
                // password doesnt meet requirements, is either too short or too long
                throw new AccountChangeException("The chosen password does not meet the configured length requirements.");
            }
            else
            {
                String newSalt = PasswordUtils.returnGeneratedSalt(secConfig.getRandomGenerator(), secConfig.getSaltLength());

                if (DEBUG)
                {
                	DEBUGGER.debug("newSalt: {}", newSalt);
                }

                if (StringUtils.isNotBlank(newSalt))
                {
                    // good, move forward
                    // put the new salt in the database
                    boolean isComplete = userSec.addOrUpdateUserSalt(userAccount.getGuid(), newSalt, SaltType.LOGON.name());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("isComplete: {}", isComplete);
                    }

                    if (isComplete)
                    {
                        // make the modification in the user repository
                        isComplete = userSec.modifyUserPassword(userAccount.getGuid(), userAccount.getUsername(),
                                PasswordUtils.encryptText(reqSecurity.getNewPassword(), newSalt,
                                		secConfig.getSecretKeyAlgorithm(), secConfig.getIterations(), secConfig.getKeyLength(),
                                		sysConfig.getEncoding()), false);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("isComplete: {}", isComplete);
                        }

                        if (isComplete)
                        {
                            userAccount.setStatus(LoginStatus.SUCCESS);

                            if (DEBUG)
                            {
                            	DEBUGGER.debug("userAccount: {}", userAccount);
                            }

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
                        response.setRequestStatus(SecurityRequestStatus.FAILURE);
                    }
                }
                else
                {
                    throw new AccountChangeException("Unable to generate new salt for provided user account.");
                }
            }
        }
        catch (final SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new AccountChangeException(sqx.getMessage(), sqx);
        }
        catch (final UserManagementException umx)
        {
            ERROR_RECORDER.error(umx.getMessage(), umx);

            throw new AccountChangeException(umx.getMessage(), umx);
        }
        catch (final SecurityException sx)
        {
            ERROR_RECORDER.error(sx.getMessage(), sx);

            throw new AccountChangeException(sx.getMessage(), sx);
        }
        finally
        {
        	if (!(isReset))
        	{
	            // audit
	            if (secConfig.getPerformAudit())
	            {
	                // audit if a valid account. if not valid we cant audit much,
	                // but we should try anyway. not sure how thats going to work
	                try
	                {
	                    AuditEntry auditEntry = new AuditEntry();
	                    auditEntry.setAuditType(AuditType.CHANGEPASS);
	                    auditEntry.setAuditDate(new Date(System.currentTimeMillis()));
	                    auditEntry.setSessionId(requestor.getSessionId());
	                    auditEntry.setUserGuid(requestor.getGuid());
	                    auditEntry.setUserName(requestor.getUsername());
	                    auditEntry.setUserRole(requestor.getUserRole().toString());
	                    auditEntry.setAuthorized(Boolean.TRUE);
	                    auditEntry.setApplicationId(request.getApplicationId());
	                    auditEntry.setApplicationName(request.getApplicationName());
    
	                    if (DEBUG)
	                    {
	                        DEBUGGER.debug("AuditEntry: {}", auditEntry);
	                    }
    
	                    AuditRequest auditRequest = new AuditRequest();
	                    auditRequest.setAuditEntry(auditEntry);
	                    auditRequest.setHostInfo(reqInfo);
    
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
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.security.processors.interfaces.IAccountChangeProcessor#changeUserSecurity(com.cws.esolutions.security.processors.dto.AccountChangeRequest)
     */
    public AccountChangeResponse changeUserSecurity(final AccountChangeRequest request) throws AccountChangeException
    {
        final String methodName = AccountChangeProcessorImpl.CNAME + "#changeUserSecurity(final AccountChangeRequest request) throws AccountChangeException";

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
        final AuthenticationData reqSecurity = request.getUserSecurity();
        final AccountChangeData changeData = request.getChangeData();

        if (DEBUG)
        {
            DEBUGGER.debug("Calendar: {}", calendar);
			DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
			DEBUGGER.debug("UserAccount: {}", requestor);
			DEBUGGER.debug("UserAccount: {}", userAccount);
			DEBUGGER.debug("AuthenticationData: {}", reqSecurity);
			DEBUGGER.debug("AccountChangeData: {}", changeData);
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
        	// make sure the two questions and answers arent the same
            if ((StringUtils.equals(changeData.getSecQuestionOne(), changeData.getSecQuestionTwo())))
            {
                throw new AccountChangeException("The security questions must be different.");
            }
            else if ((Arrays.equals(changeData.getSecAnswerOne(), changeData.getSecAnswerTwo())))
            {
                throw new AccountChangeException("The security answers must be different.");
            }
            else
            {
                String newSalt = PasswordUtils.returnGeneratedSalt(secConfig.getRandomGenerator(), secConfig.getSaltLength());

                if (StringUtils.isBlank(newSalt))
                {
                	throw new AccountChangeException("Failed to generate a salt value for the security operation.");
                }

                // good, move forward
                // make the modification in the user repository
                boolean isComplete = userSec.modifyUserSecurity(userAccount.getGuid(), 
                        new ArrayList<String>(
                            Arrays.asList(
                            		changeData.getSecQuestionOne(),
                            		changeData.getSecQuestionTwo(),
                            		PasswordUtils.encryptText(changeData.getSecAnswerOne(), newSalt,
                            				secConfig.getSecretKeyAlgorithm(),
                            				secConfig.getIterations(), secConfig.getKeyLength(),
                            				sysConfig.getEncoding()),
                            		PasswordUtils.encryptText(changeData.getSecAnswerTwo(), newSalt,
                            				secConfig.getSecretKeyAlgorithm(),
                            				secConfig.getIterations(), secConfig.getKeyLength(),
                            				sysConfig.getEncoding()))));

                if (DEBUG)
                {
                    DEBUGGER.debug("isComplete: {}", isComplete);
                }

                if (isComplete)
                {
                    // now update the salt
                    isComplete = userSec.addOrUpdateUserSalt(userAccount.getGuid(), newSalt, SaltType.RESET.name());

                    if (isComplete)
                    {
                        response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                    }
                    else
                    {
                        ERROR_RECORDER.error("Failed to add the new user salt information to the database.");

                        response.setRequestStatus(SecurityRequestStatus.FAILURE);
                    }
                }
                else
                {
                    ERROR_RECORDER.error("Failed to modify the account in the user repository.");
                	
                    response.setRequestStatus(SecurityRequestStatus.FAILURE);
                }
            }
        }
        catch (final SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new AccountChangeException(sqx.getMessage(), sqx);
        }
        catch (final UserManagementException umx)
        {
            ERROR_RECORDER.error(umx.getMessage(), umx);

            throw new AccountChangeException(umx.getMessage(), umx);
        }
        catch (final SecurityException sx)
        {
            ERROR_RECORDER.error(sx.getMessage(), sx);

            throw new AccountChangeException(sx.getMessage(), sx);
        }
        finally
        {
            // audit
            if (secConfig.getPerformAudit())
            {
                // audit if a valid account. if not valid we cant audit much,
                // but we should try anyway. not sure how thats going to work
                try
                {
                    AuditEntry auditEntry = new AuditEntry();
                    auditEntry.setAuditType(AuditType.CHANGESECURITY);
                    auditEntry.setAuditDate(new Date(System.currentTimeMillis()));
                    auditEntry.setSessionId(requestor.getSessionId());
                    auditEntry.setUserGuid(requestor.getGuid());
                    auditEntry.setUserName(requestor.getUsername());
                    auditEntry.setUserRole(requestor.getUserRole().toString());
                    auditEntry.setAuthorized(Boolean.TRUE);
                    auditEntry.setApplicationId(request.getApplicationId());
                    auditEntry.setApplicationName(request.getApplicationName());
    
                    if (DEBUG)
                    {
                        DEBUGGER.debug("AuditEntry: {}", auditEntry);
                    }
    
                    AuditRequest auditRequest = new AuditRequest();
                    auditRequest.setAuditEntry(auditEntry);
                    auditRequest.setHostInfo(reqInfo);
    
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
     * @see com.cws.esolutions.security.processors.interfaces.IAccountChangeProcessor#changeUserKeys(com.cws.esolutions.security.processors.dto.AccountChangeRequest)
     */
    public AccountChangeResponse changeUserKeys(final AccountChangeRequest request) throws AccountChangeException
    {
        final String methodName = AccountChangeProcessorImpl.CNAME + "#changeUserKeys(final AccountChangeRequest request) throws AccountChangeException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AccountChangeRequest: {}", request);
        }

        AccountChangeResponse response = new AccountChangeResponse();

        final RequestHostInfo reqInfo = request.getHostInfo();
        final UserAccount userAccount = request.getUserAccount();
        final UserAccount requestor = request.getRequestor();
        final KeyConfig keyConfig = secBean.getConfigData().getKeyConfig();
        final KeyManager keyManager = KeyManagementFactory.getKeyManager(keyConfig.getKeyManager());

        if (DEBUG)
        {
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("UserAccount: {}", requestor);
            DEBUGGER.debug("KeyConfig: {}", keyConfig);
            DEBUGGER.debug("KeyManager: {}", keyManager);
        }

        if (!(StringUtils.equals(userAccount.getGuid(), requestor.getGuid())))
        {
            // requesting user is not the same as the user being reset. authorize
            response.setRequestStatus(SecurityRequestStatus.UNAUTHORIZED);

            return response;
        }

        try
        {
        	KeyPair keyPair = null;

        	// delete the existing keys
        	try
        	{
        		keyPair = keyManager.returnKeys(userAccount.getGuid());
        	}
        	catch (KeyManagementException kmx) {} // do nothing with it, we dont care

        	if (DEBUG)
        	{
        		DEBUGGER.debug("KeyPair: {}", keyPair);
        	}

        	if (!(Objects.isNull(keyPair)))
        	{
        		boolean keysRemoved = keyManager.removeKeys(userAccount.getGuid());

	            if (DEBUG)
	            {
	                DEBUGGER.debug("keysRemoved: {}", keysRemoved);
	            }
        	}

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
        catch (final KeyManagementException kmx)
        {
            ERROR_RECORDER.error(kmx.getMessage(), kmx);

            throw new AccountChangeException(kmx.getMessage(), kmx);
        }
        finally
        {
            // audit
            if (secConfig.getPerformAudit())
            {
                // audit if a valid account. if not valid we cant audit much,
                // but we should try anyway. not sure how thats going to work
                try
                {
                    AuditEntry auditEntry = new AuditEntry();
                    auditEntry.setAuditType(AuditType.CHANGEKEYS);
                    auditEntry.setAuditDate(new Date(System.currentTimeMillis()));
                    auditEntry.setSessionId(requestor.getSessionId());
                    auditEntry.setUserGuid(requestor.getGuid());
                    auditEntry.setUserName(requestor.getUsername());
                    auditEntry.setUserRole(requestor.getUserRole().toString());
                    auditEntry.setAuthorized(Boolean.TRUE);
                    auditEntry.setApplicationId(request.getApplicationId());
                    auditEntry.setApplicationName(request.getApplicationName());
    
                    if (DEBUG)
                    {
                        DEBUGGER.debug("AuditEntry: {}", auditEntry);
                    }
    
                    AuditRequest auditRequest = new AuditRequest();
                    auditRequest.setAuditEntry(auditEntry);
                    auditRequest.setHostInfo(reqInfo);
    
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
}
