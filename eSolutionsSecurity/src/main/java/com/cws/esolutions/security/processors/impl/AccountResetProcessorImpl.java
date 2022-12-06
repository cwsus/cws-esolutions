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
 * File: AccountResetProcessorImpl.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.util.Date;
import java.util.List;
import java.util.Arrays;
import java.util.Calendar;
import java.util.ArrayList;
import java.sql.SQLException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.RandomStringUtils;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.utils.PasswordUtils;
import com.cws.esolutions.security.processors.enums.SaltType;
import com.cws.esolutions.security.processors.dto.AuditEntry;
import com.cws.esolutions.security.processors.enums.AuditType;
import com.cws.esolutions.security.processors.dto.AuditRequest;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.security.processors.dto.AuthenticationData;
import com.cws.esolutions.security.exception.SecurityServiceException;
import com.cws.esolutions.security.processors.dto.AccountResetRequest;
import com.cws.esolutions.security.processors.dto.AccountResetResponse;
import com.cws.esolutions.security.processors.exception.AuditServiceException;
import com.cws.esolutions.security.processors.exception.AccountResetException;
import com.cws.esolutions.security.processors.interfaces.IAccountResetProcessor;
import com.cws.esolutions.security.dao.userauth.exception.AuthenticatorException;
import com.cws.esolutions.security.dao.usermgmt.exception.UserManagementException;
/**
 * @see com.cws.esolutions.security.processors.interfaces.IAccountResetProcessor
 */
public class AccountResetProcessorImpl implements IAccountResetProcessor
{
    private static final String CNAME = AccountResetProcessorImpl.class.getName();

    /**
     * @see com.cws.esolutions.security.processors.interfaces.IAccountResetProcessor#findUserAccount(com.cws.esolutions.security.processors.dto.AccountResetRequest)
     */
    public AccountResetResponse findUserAccount(final AccountResetRequest request) throws AccountResetException
    {
        final String methodName = AccountResetProcessorImpl.CNAME + "#findUserAccount(final AccountResetRequest request) throws AccountResetException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AccountResetRequest: {}", request);
        }

        AccountResetResponse response = new AccountResetResponse();

        final RequestHostInfo reqInfo = request.getHostInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            List<String[]> userList = userManager.searchUsers(request.getSearchData());

            if (DEBUG)
            {
                DEBUGGER.debug("List<String[]>: {}", userList);
            }

            if ((userList == null) || (userList.size() == 0))
            {
                response.setRequestStatus(SecurityRequestStatus.FAILURE);

                return response;
            }

            UserAccount userInfo = new UserAccount();
            userInfo.setGuid(userList.get(0)[0]);
            userInfo.setUsername(userList.get(0)[1]);

            if (DEBUG)
            {
                DEBUGGER.debug("UserAccount: {}", userInfo);
            }

            response.setRequestStatus(SecurityRequestStatus.SUCCESS);
            response.setUserAccount(userInfo);
        }
        catch (final UserManagementException umx)
        {
            ERROR_RECORDER.error(umx.getMessage(), umx);

            throw new AccountResetException(umx.getMessage(), umx);
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
     * @see com.cws.esolutions.security.processors.interfaces.IAccountResetProcessor#obtainUserSecurityConfig(com.cws.esolutions.security.processors.dto.AccountResetRequest)
     */
    public AccountResetResponse obtainUserSecurityConfig(final AccountResetRequest request) throws AccountResetException
    {
        final String methodName = AccountResetProcessorImpl.CNAME + "#obtainUserSecurityConfig(final AccountResetRequest request) throws AccountResetException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AccountResetRequest: {}", request);
        }

        AccountResetResponse response = new AccountResetResponse();

        final RequestHostInfo reqInfo = request.getHostInfo();
        final UserAccount userAccount = request.getUserAccount();

        if (DEBUG)
        {
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount: {}", userAccount);
        }

        try
        {
            List<String> securityData = authenticator.obtainSecurityData(userAccount.getUsername(), userAccount.getGuid());

            if (DEBUG)
            {
                DEBUGGER.debug("List<String>: {}", securityData);
            }

            if ((securityData == null) || (securityData.isEmpty()))
            {
                response.setRequestStatus(SecurityRequestStatus.FAILURE);
            }

            if (securityData != null)
            {
                if (StringUtils.isNotBlank(securityData.get(0)))
                {
                    UserAccount resAccount = new UserAccount();
                    resAccount.setGuid(securityData.get(0));
                    resAccount.setUsername(securityData.get(1));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("UserAccount: {}", resAccount);
                    }

                    AuthenticationData userSecurity = new AuthenticationData();
                    userSecurity.setSecQuestionOne(securityData.get(0));
                    userSecurity.setSecQuestionTwo(securityData.get(1));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("AuthenticationData: {}", userSecurity);
                    }

                    response.setUserAccount(resAccount);
                    response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                    response.setUserSecurity(userSecurity);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("AccountResetResponse: {}", response);
                    }
                }
            }
        }
        catch (final AuthenticatorException ax)
        {
            ERROR_RECORDER.error(ax.getMessage(), ax);

            throw new AccountResetException(ax.getMessage(), ax);
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
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.security.processors.interfaces.IAccountResetProcessor#verifyUserSecurityConfig(com.cws.esolutions.security.processors.dto.AccountResetRequest)
     */
    public AccountResetResponse verifyUserSecurityConfig(final AccountResetRequest request) throws AccountResetException
    {
        final String methodName = AccountResetProcessorImpl.CNAME + "#verifyUserSecurityConfig(final AccountResetRequest request) throws AccountResetException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AccountResetRequest: {}", request);
        }

        AccountResetResponse authResponse = new AccountResetResponse();

        final RequestHostInfo reqInfo = request.getHostInfo();
        final UserAccount userAccount = request.getUserAccount();
        final AuthenticationData userSecurity = request.getUserSecurity();

        if (DEBUG)
        {
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount: {}", userAccount);
        }

        try
        {
            final String userSalt = userSec.getUserSalt(userAccount.getGuid(), SaltType.RESET.name());

            if (StringUtils.isNotEmpty(userSalt))
            {
                boolean isVerified = authenticator.verifySecurityData(userAccount.getUsername(), userAccount.getGuid(),
                    new ArrayList<String>(
                        Arrays.asList(
                            PasswordUtils.encryptText(userSecurity.getSecAnswerOne(), userSalt,
                                secConfig.getMessageDigest(), secConfig.getIterations(),
                                secBean.getConfigData().getSystemConfig().getEncoding()),
                            PasswordUtils.encryptText(userSecurity.getSecAnswerTwo(), userSalt,
                                secConfig.getMessageDigest(), secConfig.getIterations(),
                                secBean.getConfigData().getSystemConfig().getEncoding()))));

                if (DEBUG)
                {
                    DEBUGGER.debug("isVerified: {}", isVerified);
                }

                if (isVerified)
                {
                    authResponse.setRequestStatus(SecurityRequestStatus.SUCCESS);
                }
                else
                {
                    if (request.getCount() >= 3)
                    {
                        try
                        {
                            userManager.modifyOlrLock(userAccount.getUsername(), true);
                        }
                        catch (final UserManagementException umx)
                        {
                            ERROR_RECORDER.error(umx.getMessage(), umx);
                        }
                    }

                    authResponse.setCount(request.getCount() + 1);
                    authResponse.setRequestStatus(SecurityRequestStatus.FAILURE);
                }
            }
            else
            {
                throw new AccountResetException("Unable to obtain user salt value. Cannot continue.");
            }
        }
        catch (final SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new AccountResetException(sqx.getMessage(), sqx);
        }
        catch (final AuthenticatorException ax)
        {
            ERROR_RECORDER.error(ax.getMessage(), ax);

            throw new AccountResetException(ax.getMessage(), ax);
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
	                auditEntry.setAuditType(AuditType.VERIFYSECURITY);
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
        }

        return authResponse;
    }

    /**
     * @see com.cws.esolutions.security.processors.interfaces.IAccountResetProcessor#resetUserPassword(com.cws.esolutions.security.processors.dto.AccountResetRequest)
     */
    public AccountResetResponse resetUserPassword(final AccountResetRequest request) throws AccountResetException
    {
        final String methodName = AccountResetProcessorImpl.CNAME + "#resetUserPassword(final AccountResetRequest request) throws AccountResetException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AccountResetRequest: {}", request);
        }

        AccountResetResponse response = new AccountResetResponse();

        final Calendar calendar = Calendar.getInstance();
        final RequestHostInfo reqInfo = request.getHostInfo();
        final UserAccount userAccount = request.getUserAccount();

        calendar.add(Calendar.DATE, secConfig.getPasswordExpiration());

        if (DEBUG)
        {
            DEBUGGER.debug("Calendar: {}", calendar);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount: {}", userAccount);
        }

        try
        {
            String resetId = RandomStringUtils.randomAlphanumeric(secConfig.getResetIdLength());
            String smsReset = RandomStringUtils.randomAlphanumeric(secConfig.getSmsCodeLength());

            if (StringUtils.isNotEmpty(resetId))
            {
                boolean isComplete = userSec.insertResetData(userAccount.getGuid(), resetId, ((secConfig.getSmsResetEnabled()) ? smsReset : null));

                if (DEBUG)
                {
                    DEBUGGER.debug("isComplete: {}", isComplete);
                }

                if (isComplete)
                {
                    // load the user account for the email response
                    List<Object> userData = userManager.loadUserAccount(userAccount.getGuid());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("UserData: {}", userData);
                    }

                    if ((userData != null) && (!(userData.isEmpty())))
                    {
                        UserAccount responseAccount = new UserAccount();
                        responseAccount.setGuid((String) userData.get(0));
                        responseAccount.setUsername((String) userData.get(1));
                        responseAccount.setGivenName((String) userData.get(6));
                        responseAccount.setSurname((String) userData.get(7));
                        responseAccount.setDisplayName((String) userData.get(8));
                        responseAccount.setEmailAddr((String) userData.get(9));
                        responseAccount.setPagerNumber((String) userData.get(10));
                        responseAccount.setTelephoneNumber((String) userData.get(11));

                        if (DEBUG)
                        {
                            DEBUGGER.debug("UserAccount: {}", responseAccount);
                        }

                        response.setResetId(resetId);
                        response.setSmsCode(((secConfig.getSmsResetEnabled()) ? smsReset : null));
                        response.setUserAccount(responseAccount);
                        response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                    }
                    else
                    {
                        ERROR_RECORDER.error("Failed to locate user account in authentication repository. Cannot continue.");

                        response.setRequestStatus(SecurityRequestStatus.FAILURE);
                    }
                }
                else
                {
                    ERROR_RECORDER.error("Unable to insert password identifier into database. Cannot continue.");

                    response.setRequestStatus(SecurityRequestStatus.FAILURE);
                }
            }
            else
            {
                ERROR_RECORDER.error("Unable to generate a unique identifier. Cannot continue.");

                response.setRequestStatus(SecurityRequestStatus.FAILURE);
            }
        }
        catch (final SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new AccountResetException(sqx.getMessage(), sqx);
        }
        catch (final UserManagementException umx)
        {
            ERROR_RECORDER.error(umx.getMessage(), umx);

            throw new AccountResetException(umx.getMessage(), umx);
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
	                auditEntry.setAuditType(AuditType.RESETPASS);
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
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.security.processors.interfaces.IAccountResetProcessor#verifyResetRequest(com.cws.esolutions.security.processors.dto.AccountResetRequest)
     */
    public AccountResetResponse verifyResetRequest(final AccountResetRequest request) throws AccountResetException
    {
        final String methodName = AccountResetProcessorImpl.CNAME + "#verifyResetRequest(final AccountResetRequest request) throws AccountResetException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AccountResetRequest: {}", request);
        }

        AccountResetResponse response = new AccountResetResponse();

        final Calendar cal = Calendar.getInstance();
        final RequestHostInfo reqInfo = request.getHostInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("Calendar: {}", cal);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            cal.add(Calendar.MINUTE, secConfig.getResetTimeout());

            if (DEBUG)
            {
                DEBUGGER.debug("Reset expiry: {}", cal.getTimeInMillis());
            }

            // the request id should be in here, so lets make sure it exists
            List<Object> resetData = userSec.getResetData(request.getResetRequestId());

            final String commonName = (String) resetData.get(0);
            final Date resetTimestamp = (Date) resetData.get(1);

            if (DEBUG)
            {
                DEBUGGER.debug("String: {}", commonName);
                DEBUGGER.debug("Date: {}", resetTimestamp);
            }

            // make sure the timestamp is appropriate
            if (resetTimestamp.after(cal.getTime()))
            {
                response.setRequestStatus(SecurityRequestStatus.FAILURE);
            }

            // good, now we have something we can look for
            List<Object> userList = userManager.loadUserAccount(commonName);

            if (DEBUG)
            {
                DEBUGGER.debug("userList: {}", userList);
            }

            // we expect back only one
            if ((userList == null) || (userList.size() == 0))
            {
                throw new AccountResetException("Unable to load user account information. Cannot continue.");
            }

            UserAccount userAccount = new UserAccount();
            userAccount.setStatus(LoginStatus.RESET);
            userAccount.setGuid((String) userList.get(0));
            userAccount.setUsername((String) userList.get(1));
            userAccount.setSurname((String) userList.get(5));
            userAccount.setGivenName((String) userList.get(6));
            userAccount.setDisplayName((String) userList.get(7));
            userAccount.setEmailAddr((String) userList.get(8));

            if (DEBUG)
            {
                DEBUGGER.debug("UserAccount: {}", userAccount);
            }

            // remove the reset request
            boolean isRemoved = userSec.removeResetData(userAccount.getGuid(), request.getResetRequestId());

            if (DEBUG)
            {
                DEBUGGER.debug("isRemoved: {}", isRemoved);
            }

            if (!(isRemoved))
            {
                ERROR_RECORDER.error("Failed to remove provided reset request from datastore");
            }

            response.setRequestStatus(SecurityRequestStatus.SUCCESS);
            response.setUserAccount(userAccount);

            if (DEBUG)
            {
                DEBUGGER.debug("AccountResetResponse: {}", response);
            }
        }
        catch (final SecurityServiceException ssx)
        {
            ERROR_RECORDER.error(ssx.getMessage(), ssx);

            throw new AccountResetException(ssx.getMessage(), ssx);
        }
        catch (final SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new AccountResetException(sqx.getMessage(), sqx);
        }

        return response;
    }
}
