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
 * File: AuthenticationProcessorImpl.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.util.List;
import java.util.Date;
import java.sql.SQLException;
import com.unboundid.ldap.sdk.ResultCode;
import org.apache.commons.lang.StringUtils;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.utils.PasswordUtils;
import com.cws.esolutions.security.enums.SecurityUserRole;
import com.cws.esolutions.security.processors.enums.SaltType;
import com.cws.esolutions.security.processors.dto.AuditEntry;
import com.cws.esolutions.security.processors.enums.AuditType;
import com.cws.esolutions.security.processors.dto.AuditRequest;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.security.processors.dto.AuthenticationData;
import com.cws.esolutions.security.exception.SecurityServiceException;
import com.cws.esolutions.security.processors.dto.AuthenticationRequest;
import com.cws.esolutions.security.processors.dto.AuthenticationResponse;
import com.cws.esolutions.security.processors.exception.AuditServiceException;
import com.cws.esolutions.security.processors.exception.AuthenticationException;
import com.cws.esolutions.security.dao.userauth.exception.AuthenticatorException;
import com.cws.esolutions.security.processors.interfaces.IAuthenticationProcessor;
import com.cws.esolutions.security.dao.usermgmt.exception.UserManagementException;
/**
 * @see com.cws.esolutions.security.processors.interfaces.IAuthenticationProcessor
 */
public class AuthenticationProcessorImpl implements IAuthenticationProcessor
{
    private static final String CNAME = AuthenticationProcessorImpl.class.getName();

    /**
     * @see com.cws.esolutions.security.processors.interfaces.IAuthenticationProcessor#processAgentLogon(com.cws.esolutions.security.processors.dto.AuthenticationRequest)
     */
    public AuthenticationResponse processAgentLogon(final AuthenticationRequest request) throws AuthenticationException
    {
        final String methodName = AuthenticationProcessorImpl.CNAME + "#processAgentLogon(final AuthenticationRequest request) throws AuthenticationException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AuthenticationRequest: {}", request);
        }

        UserAccount userAccount = null;
        AuthenticationResponse response = new AuthenticationResponse();

        final RequestHostInfo reqInfo = request.getHostInfo();
        final UserAccount authUser = request.getUserAccount();
        final AuthenticationData authSec = request.getUserSecurity();

        if (DEBUG)
        {
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount: {}", authUser);
        }

        try
        {
            List<String[]> userInfo = userManager.searchUsers(authUser.getUsername());

            if (DEBUG)
            {
                DEBUGGER.debug("List<String[]>: {}", userInfo);
            }

            if (userInfo.size() != 1)
            {
                response.setRequestStatus(SecurityRequestStatus.FAILURE);

                return response;
            }

            if (DEBUG)
            {
            	for (int x = 0; x < userInfo.size(); x++)
            	{
            		DEBUGGER.debug("UserInfo: {}", (Object) userInfo.get(x));
            	}
            }

            String userSalt = userSec.getUserSalt(userInfo.get(0)[0], SaltType.LOGON.name());

            if (StringUtils.isEmpty(userSalt))
            {
                throw new AuthenticationException("Unable to obtain configured user salt. Cannot continue");
            }

            List<Object> authObject = authenticator.performLogon(userInfo.get(0)[0], authUser.getUsername(),
            		userSalt, authSec.getPassword());

            if (DEBUG)
            {
                DEBUGGER.debug("List<Object>: {}", authObject);
            }

            if (authObject.size() == 0)
            {
                throw new AuthenticationException("Authentication processing failed. Cannot continue.");
            }

            if (DEBUG)
            {
            	for (Object entry : authObject)
            	{
            		DEBUGGER.debug("Entry: {}", entry);
            	}
            }

            if ((Integer) authObject.get(6) >= secConfig.getMaxAttempts())
            {
                // user locked
                response.setRequestStatus(SecurityRequestStatus.FAILURE);

                return response;
            }

            // if the user has enabled otp auth, do it here
            // TODO
            if (StringUtils.isNotEmpty((String) authObject.get(2)))
            {
                userAccount = new UserAccount();
                userAccount.setGuid((String) authObject.get(0));
                userAccount.setUsername((String) authObject.get(1));
                userAccount.setStatus(LoginStatus.CONTINUE);

                response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                response.setUserAccount(userAccount);

                return response;
            }

            userAccount = new UserAccount();
            userAccount.setGuid((String) authObject.get(0)); // CN
            userAccount.setUsername((String) authObject.get(1)); // UID
            userAccount.setGivenName((String) authObject.get(2)); // SN
            userAccount.setSurname((String) authObject.get(3)); // givenName
            userAccount.setDisplayName((String) authObject.get(4)); // displayName
            userAccount.setUserRole(SecurityUserRole.valueOf((String) authObject.get(5))); //cwsrole
            userAccount.setFailedCount((int) authObject.get(6)); // cwsfailedpwdcount
            userAccount.setLastLogin((long) authObject.get(7)); // cwslastlogin
            userAccount.setExpiryDate((long) authObject.get(8)); // cwsexpirydate
            userAccount.setSuspended((boolean) authObject.get(9)); // cwsissuspended
            userAccount.setOlrSetup((boolean) authObject.get(10)); // cwsisolrsetup
            userAccount.setOlrLocked((boolean) authObject.get(11)); // cwsisolrlocked
            userAccount.setAccepted((boolean) authObject.get(12)); // cwsistcaccepted
            userAccount.setTelephoneNumber((String) authObject.get(13)); // telephoneNumber
            userAccount.setPagerNumber((String) authObject.get(14)); // pager

            if (DEBUG)
            {
                DEBUGGER.debug("UserAccount: {}", userAccount);
            }

            // have a user account, run with it
            if ((userAccount.getExpiryDate() > System.currentTimeMillis()) || (userAccount.getExpiryDate() == System.currentTimeMillis()))
            {
                userAccount.setStatus(LoginStatus.EXPIRED);

                response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                response.setUserAccount(userAccount);
            }
            else
            {
                userManager.performSuccessfulLogin(userAccount.getUsername(), userAccount.getGuid(), userAccount.getFailedCount(), System.currentTimeMillis());

                userAccount.setLastLogin(System.currentTimeMillis());
                userAccount.setStatus(LoginStatus.SUCCESS);

                response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                response.setUserAccount(userAccount);
            }

            if (DEBUG)
            {
                DEBUGGER.debug("UserAccount: {}", userAccount);
                DEBUGGER.debug("AuthenticationResponse: {}", response);
            }
        }
        catch (AuthenticatorException ax)
        {
            ERROR_RECORDER.error(ax.getMessage(), ax);

            try
            {
                if (ax.getResultCode() == ResultCode.INVALID_CREDENTIALS)
                {
                    // failed authentication, update counter
                    // find out if this is a valid user...
                    List<String[]> userList = userManager.searchUsers(authUser.getUsername());

                    // only do the work if the userlist is equal to 1.
                    // if there were 150 users found then we dont want
                    // to shoot them all
                    if ((userList != null) && (userList.size() == 1))
                    {
                        // do it
                        userManager.modifyUserLock(userList.get(0)[0], false, request.getCount() + 1);
                    }
                }
            }
            catch (UserManagementException umx)
            {
                ERROR_RECORDER.error(umx.getMessage(), umx);
            }

            response.setCount(request.getCount() + 1);
            response.setRequestStatus(SecurityRequestStatus.FAILURE);
        }
        catch (SecurityServiceException ssx)
        {
            ERROR_RECORDER.error(ssx.getMessage(), ssx);

            throw new AuthenticationException(ssx.getMessage(), ssx);
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new AuthenticationException(sqx.getMessage(), sqx);
        }
        catch (SecurityException sx)
        {
            ERROR_RECORDER.error(sx.getMessage(), sx);
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
	                auditEntry.setAuditType(AuditType.LOGON);
	                auditEntry.setUserAccount(authUser);
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
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.security.processors.interfaces.IAuthenticationProcessor#processOtpLogon(com.cws.esolutions.security.processors.dto.AuthenticationRequest)
     */
    public AuthenticationResponse processOtpLogon(final AuthenticationRequest request) throws AuthenticationException
    {
        final String methodName = AuthenticationProcessorImpl.CNAME + "#processOtpLogon(final AuthenticationRequest request) throws AuthenticationException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AuthenticationRequest: {}", request);
        }

        UserAccount userAccount = null;
        AuthenticationResponse response = new AuthenticationResponse();

        final RequestHostInfo reqInfo = request.getHostInfo();
        final UserAccount authUser = request.getUserAccount();
        final AuthenticationData authSec = request.getUserSecurity();

        if (DEBUG)
        {
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount: {}", authUser);
        }

        try
        {
            String otpSalt = userSec.getUserSalt(authUser.getGuid(), SaltType.OTP.name());
            String otpSecret = authenticator.obtainOtpSecret(authUser.getUsername(), authUser.getGuid());

            // if the user has enabled otp auth, do it here
            if ((StringUtils.isEmpty(otpSalt)) || (StringUtils.isEmpty(otpSecret)))
            {
                throw new AuthenticationException("Unable to obtain security information. Cannot continue.");
            }

            boolean isAuthorized = PasswordUtils.validateOtpValue(secConfig.getOtpVariance(), secConfig.getOtpAlgorithm(),
                    PasswordUtils.decryptText(otpSecret, otpSalt,
                            secBean.getConfigData().getSecurityConfig().getSecretAlgorithm(),
                            secBean.getConfigData().getSecurityConfig().getIterations(),
                            secBean.getConfigData().getSecurityConfig().getKeyBits(),
                            secBean.getConfigData().getSecurityConfig().getEncryptionAlgorithm(),
                            secBean.getConfigData().getSecurityConfig().getEncryptionInstance(),
                            secBean.getConfigData().getSystemConfig().getEncoding()),
                    secBean.getConfigData().getSecurityConfig().getEncryptionInstance(),
                    authSec.getOtpValue());

            if (DEBUG)
            {
                DEBUGGER.debug("isAuthorized: {}", isAuthorized);
            }

            if (!(isAuthorized))
            {
                response.setRequestStatus(SecurityRequestStatus.FAILURE);
                response.setCount(request.getCount() + 1);

                return response;
            }

            List<Object> userData = userManager.loadUserAccount(authUser.getGuid());

            userAccount = new UserAccount();
            userAccount.setGuid((String) userData.get(0));
            userAccount.setUsername((String) userData.get(1));
            userAccount.setGivenName((String) userData.get(2));
            userAccount.setSurname((String) userData.get(3));
            userAccount.setDisplayName((String) userData.get(4));
            userAccount.setEmailAddr((String) userData.get(5));
            userAccount.setPagerNumber((String) userData.get(6));
            userAccount.setTelephoneNumber((String) userData.get(7));
            userAccount.setFailedCount((Integer) userData.get(8));
            userAccount.setLastLogin((long) userData.get(9));
            userAccount.setExpiryDate((long) userData.get(10));
            userAccount.setSuspended((Boolean) userData.get(11));
            userAccount.setOlrSetup((Boolean) userData.get(12));
            userAccount.setOlrLocked((Boolean) userData.get(13));
            userAccount.setGroups(StringUtils.split((String) userData.get(15), ","));

            if (DEBUG)
            {
                DEBUGGER.debug("UserAccount: {}", userAccount);
            }

            // have a user account, run with it
            if (userAccount.getExpiryDate() < new Date(System.currentTimeMillis()).getTime()
                    || userAccount.getExpiryDate() == new Date(System.currentTimeMillis()).getTime())
            {
                userAccount.setStatus(LoginStatus.EXPIRED);

                response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                response.setUserAccount(userAccount);
            }
            else
            {
                userAccount.setStatus(LoginStatus.SUCCESS);

                response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                response.setUserAccount(userAccount);
            }

            if (DEBUG)
            {
                DEBUGGER.debug("AuthenticationResponse: {}", response);
            }
        }
        catch (SecurityServiceException ssx)
        {
            ERROR_RECORDER.error(ssx.getMessage(), ssx);

            throw new AuthenticationException(ssx.getMessage(), ssx);
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new AuthenticationException(sqx.getMessage(), sqx);
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
	                auditEntry.setAuditType(AuditType.LOGON);
	                auditEntry.setUserAccount(authUser);
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
        }

        return response;
    }
}
