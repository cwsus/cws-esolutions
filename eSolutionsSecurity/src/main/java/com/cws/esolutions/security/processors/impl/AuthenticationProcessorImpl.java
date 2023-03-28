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
import java.util.Date;
import java.util.List;
import java.util.Arrays;
import java.sql.Timestamp;
import java.security.KeyPair;
import java.sql.SQLException;
import org.apache.commons.lang3.StringUtils;

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
import com.cws.esolutions.security.processors.interfaces.IAuthenticationProcessor;
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

        boolean isValid = false;
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
        	String userInfo = userManager.getUserByUsername(authUser.getUsername());

            if (DEBUG)
            {
                DEBUGGER.debug("userInfo: {}", userInfo);
            }

            if (StringUtils.isEmpty(userInfo))
            {
            	throw new AuthenticationException("Unable to locate an account for the given information. Cannot continue");
            }

            
            String userSalt = userSec.getUserSalt(userInfo, SaltType.LOGON.name()); // user salt as obtained from the database

            if (StringUtils.isEmpty(userSalt))
            {
                throw new AuthenticationException("Unable to obtain configured user security information. Cannot continue");
            }

        	char[] decrypted = PasswordUtils.decryptText(authenticator.performLogon(userInfo), authSec.getPassword(), userSalt.getBytes(),
        			secConfig.getSecretKeyAlgorithm(), secConfig.getIterations(), secConfig.getKeyLength(),
            		secConfig.getEncryptionAlgorithm(), secConfig.getEncryptionInstance(), sysConfig.getEncoding()).toCharArray();

        	if (!(Arrays.equals(decrypted, authSec.getPassword())))
        	{
        		throw new AuthenticationException("Authentication failure");
        	}

            // load the user account here
            List<Object> authObject = userManager.loadUserAccount(userInfo);

            if (DEBUG)
            {
            	DEBUGGER.debug("authObject: {}", authObject);
            }

            if ((Integer) authObject.get(7) >= secConfig.getMaxAttempts())
            {
                // user locked
                response.setRequestStatus(SecurityRequestStatus.FAILURE);

                return response;
            }

            userAccount = new UserAccount();
            userAccount.setGuid((String) authObject.get(0)); // CN
            userAccount.setUsername((String) authObject.get(1)); // UID
            userAccount.setGivenName((String) authObject.get(2)); // GIVENNAME
            userAccount.setSurname((String) authObject.get(3)); // sn
            userAccount.setDisplayName((String) authObject.get(4)); // displayName
            userAccount.setEmailAddr((String) authObject.get(5)); // email
            userAccount.setUserRole(SecurityUserRole.valueOf((String) authObject.get(6))); //cwsrole
            userAccount.setFailedCount(Integer.parseInt(authObject.get(7).toString())); // cwsfailedpwdcount            
            userAccount.setLastLogin((Timestamp) authObject.get(8)); // cwslastlogin
            userAccount.setExpiryDate((Timestamp) authObject.get(9)); // cwsexpirydate
            userAccount.setSuspended(Boolean.valueOf(authObject.get(10).toString())); // cwsissuspended
            userAccount.setOlrSetup(Boolean.valueOf(authObject.get(11).toString())); // cwsisolrsetup
            userAccount.setOlrLocked(Boolean.valueOf(authObject.get(12).toString())); // cwsisolrlocked
            userAccount.setAccepted(Boolean.valueOf(authObject.get(13).toString())); // cwsistcaccepted
            userAccount.setUserKeys((KeyPair) authObject.get(14)); // cwspublickey
            userAccount.setTelephoneNumber((String) authObject.get(15)); // telephoneNumber
            userAccount.setPagerNumber((String) authObject.get(16)); // pager

            if (DEBUG)
            {
                DEBUGGER.debug("UserAccount: {}", userAccount);
            }

            // get otp salt if available
            String returnedSalt = userSec.getUserSalt(userAccount.getGuid(), SaltType.OTP.toString());

            // if the user has enabled otp auth, do it here
            // TODO
            if (StringUtils.isNotEmpty(returnedSalt))
            {
                userAccount.setStatus(LoginStatus.CONTINUE);

                response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                response.setUserAccount(userAccount);

                return response;
            }

            // have a user account, run with it
            if ((userAccount.getExpiryDate().before(new Date(System.currentTimeMillis())) || (userAccount.getExpiryDate().equals(new Date(System.currentTimeMillis())))))
            {
                userAccount.setStatus(LoginStatus.EXPIRED);

                response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                response.setUserAccount(userAccount);
            }
            else
            {
                authenticator.performSuccessfulLogin(userAccount.getUsername(), userAccount.getGuid(), userAccount.getFailedCount(), System.currentTimeMillis());

                userAccount.setLastLogin(new Timestamp(System.currentTimeMillis()));
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
        catch (final SecurityServiceException ssx)
        {
            ERROR_RECORDER.error(ssx.getMessage(), ssx);

            throw new AuthenticationException(ssx.getMessage(), ssx);
        }
        catch (final SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new AuthenticationException(sqx.getMessage(), sqx);
        }
        catch (final SecurityException sx)
        {
            ERROR_RECORDER.error(sx.getMessage(), sx);
        }
        finally
        {
        	if ((secConfig.getPerformAudit()) && (isValid))
        	{
	            // audit if a valid account. if not valid we cant audit much,
        		// but we should try anyway. not sure how thats going to work
	            try
	            {
	                AuditEntry auditEntry = new AuditEntry();
	                auditEntry.setHostInfo(reqInfo);
	                auditEntry.setAuditType(AuditType.LOGON);
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
}
