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
 * File: AuditProcessorImpl.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.ArrayList;
import java.net.InetAddress;
import java.sql.SQLException;
import java.net.UnknownHostException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.utils.PasswordUtils;
import com.cws.esolutions.security.enums.SecurityUserRole;
import com.cws.esolutions.security.processors.dto.AuditEntry;
import com.cws.esolutions.security.processors.enums.SaltType;
import com.cws.esolutions.security.processors.enums.AuditType;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.processors.dto.AuditRequest;
import com.cws.esolutions.security.processors.dto.AuditResponse;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.security.processors.interfaces.IAuditProcessor;
import com.cws.esolutions.security.services.dto.AccessControlServiceRequest;
import com.cws.esolutions.security.services.dto.AccessControlServiceResponse;
import com.cws.esolutions.security.processors.exception.AuditServiceException;
import com.cws.esolutions.security.dao.userauth.exception.AuthenticatorException;
import com.cws.esolutions.security.services.exception.AccessControlServiceException;
/**
 * @see com.cws.esolutions.security.processors.interfaces.IAuditProcessor
 */
public class AuditProcessorImpl implements IAuditProcessor
{
    private static final String CNAME = AuditProcessorImpl.class.getName();

    /**
     * @see com.cws.esolutions.security.processors.interfaces.IAuditProcessor#auditRequest(com.cws.esolutions.security.processors.dto.AuditRequest)
     */
    public void auditRequest(final AuditRequest request) throws AuditServiceException
    {
        final String methodName = AuditProcessorImpl.CNAME + "#auditRequest(final AuditRequest request) throws AuditServiceException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AuditRequest: {}", request);
        }

        final AuditEntry auditEntry = request.getAuditEntry();
        final RequestHostInfo hostInfo = auditEntry.getHostInfo();
        final UserAccount userAccount = auditEntry.getUserAccount();

        if (DEBUG)
        {
            DEBUGGER.debug("AuditEntry: {}", auditEntry);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", hostInfo);
        }

        if (secConfig.getPerformAudit())
        {
            if ((Objects.isNull(hostInfo)) || (Objects.isNull(userAccount)))
            {
            	ERROR_RECORDER.error("Cannot perform audit: RequestHostInfo / UserAccount is missing");

            	return;
            }

            try
            {
	            List<String> auditList = new ArrayList<String>();
	            auditList.add((StringUtils.isEmpty(userAccount.getSessionId())) ? RandomStringUtils.randomAlphanumeric(128) : userAccount.getSessionId()); // sessionid
	            auditList.add((StringUtils.isEmpty(userAccount.getUsername())) ? "WEBUSR" : userAccount.getUsername()); // username
	            auditList.add((StringUtils.isEmpty(userAccount.getGuid())) ? "918671b2-662e-4499-9fd3-1e4e88e0fba2" : userAccount.getGuid()); // userguid
	            auditList.add((Objects.isNull(userAccount.getUserRole())) ? SecurityUserRole.NONE.toString() : userAccount.getUserRole().toString()); // userrole
	            auditList.add((StringUtils.isEmpty(auditEntry.getApplicationId())) ? "SecurityServicesDefault" : auditEntry.getApplicationId()); // applid
	            auditList.add((StringUtils.isEmpty(auditEntry.getApplicationName())) ? "SecurityServicesDefault" : auditEntry.getApplicationName()); // applname
	            auditList.add((StringUtils.isEmpty(auditEntry.getAuditType().toString())) ? AuditType.NONE.toString() : auditEntry.getAuditType().toString()); // useraction
	            auditList.add((StringUtils.isEmpty(hostInfo.getHostAddress())) ? InetAddress.getLocalHost().getHostAddress() : hostInfo.getHostAddress()); // srcaddr
	            auditList.add((StringUtils.isEmpty(hostInfo.getHostName())) ? InetAddress.getLocalHost().getHostName() : hostInfo.getHostName()); // srchost

	            if (DEBUG)
	            {
	                DEBUGGER.debug("auditList: {}", auditList);

	                for (String str : auditList)
	                {
	                	DEBUGGER.debug(str);
	                }
	            }

                // log it ..
                AUDIT_RECORDER.info("AUDIT: User: " + userAccount + ", Requested Action: " + auditEntry.getAuditType() + ", Host: " + hostInfo);

                // .. and stuff in in the db
                auditDAO.auditRequestedOperation(auditList);
            }
            catch (final SQLException sqx)
            {
                ERROR_RECORDER.error(sqx.getMessage(), sqx);
            }
            catch (UnknownHostException uhx)
            {
            	ERROR_RECORDER.error(uhx.getMessage(), uhx);
            }
        }
    }

    /**
     * @see com.cws.esolutions.security.processors.interfaces.IAuditProcessor#getAuditEntries(com.cws.esolutions.security.processors.dto.AuditRequest)
     */
    public AuditResponse getAuditEntries(final AuditRequest request) throws AuditServiceException
    {
        final String methodName = AuditProcessorImpl.CNAME + "#getAuditEntries(final AuditRequest request) throws AuditServiceException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AuditRequest: {}", request);
        }

        AuditResponse response = new AuditResponse();

        final UserAccount reqAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getHostInfo();
        final AuditEntry auditEntry = request.getAuditEntry();
        final UserAccount targetAccount = request.getUserAccount();

        if (DEBUG)
        {
        	DEBUGGER.debug("UserAccount: {}", reqAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("AuditEntry: {}", auditEntry);
            DEBUGGER.debug("UserAccount: {}", targetAccount);
        }

        try
        {
            String tokenSalt = userSec.getUserSalt(reqAccount.getGuid(), SaltType.AUTHTOKEN.toString());
            String authToken = PasswordUtils.encryptText(reqAccount.getGuid().toCharArray(), tokenSalt,
                    secConfig.getSecretKeyAlgorithm(),
                    secConfig.getIterations(), secConfig.getKeyLength(),
                    sysConfig.getEncoding());

            if (DEBUG)
            {
                DEBUGGER.debug("tokenSalt: {}", tokenSalt);
                DEBUGGER.debug("authToken: {}", authToken);
            }

            boolean isAuthenticated = authenticator.validateAuthToken(reqAccount.getGuid(), reqAccount.getUsername(), reqAccount.getAuthToken());

            if (!(isAuthenticated))
            {
                throw new AuditServiceException("An invalid authentication token was presented.");
            }

            // this will require admin and service authorization
            AccessControlServiceRequest accessRequest = new AccessControlServiceRequest();
            accessRequest.setUserAccount(reqAccount);

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
                    AuditEntry setAuditEntry = new AuditEntry();
                    setAuditEntry.setHostInfo(reqInfo);
                    setAuditEntry.setAuditType(AuditType.GETAUDITENTRIES);
                    setAuditEntry.setUserAccount(reqAccount);
                    auditEntry.setAuthorized(Boolean.FALSE);
                    setAuditEntry.setApplicationId(request.getApplicationId());
                    setAuditEntry.setApplicationName(request.getApplicationName());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("AuditEntry: {}", setAuditEntry);
                    }

                    AuditRequest auditRequest = new AuditRequest();
                    auditRequest.setAuditEntry(setAuditEntry);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("AuditRequest: {}", auditRequest);
                    }

                    auditRequest(auditRequest);
                }
                catch (final AuditServiceException asx)
                {
                    ERROR_RECORDER.error(asx.getMessage(), asx);
                }

                return response;
            }

            if (secConfig.getPerformAudit())
            {
                final UserAccount user = auditEntry.getUserAccount();

                if (DEBUG)
                {
                    DEBUGGER.debug("UserAccount: {}", user);
                }

                List<Object> dataResponse = auditDAO.getAuditInterval(user.getGuid(), request.getStartRow());

                if (DEBUG)
                {
                    DEBUGGER.debug("Data: {}", dataResponse);
                }

                if ((dataResponse != null) && (dataResponse.size() != 0))
                {
                    int count = (Integer) dataResponse.get(0);
                    List<AuditEntry> auditList = new ArrayList<AuditEntry>();

                    for (int x = 1; dataResponse.size() != x; x++)
                    {
                        if (DEBUG)
                        {
                            DEBUGGER.debug("Value: {}", dataResponse.get(x));
                        }

                        Object[] array = (Object[]) dataResponse.get(x);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Object[]: {}", array);
                        }

                        RequestHostInfo hostInfo = new RequestHostInfo();
                        hostInfo.setHostAddress((String) array[7]); // resultSet.getString(9), // SOURCE_ADDRESS
                        hostInfo.setHostName((String) array[8]); // resultSet.getString(10) // SOURCE_HOSTNAME

                        if (DEBUG)
                        {
                            DEBUGGER.debug("RequestHostInfo: {}", hostInfo);
                        }

                        // capture
                        UserAccount resAccount = new UserAccount();
                        resAccount.setUsername((String) array[1]); // resultSet.getString(3), // USERNAME
                        resAccount.setGuid((String) array[2]); // resultSet.getString(4), // CN

                        if (DEBUG)
                        {
                            DEBUGGER.debug("UserAccount: {}", resAccount);
                        }

                        AuditEntry resEntry = new AuditEntry();
                        resEntry.setApplicationId((String) (array[3])); // resultSet.getString(5), // APPLICATION_ID
                        resEntry.setApplicationName((String) array[4]); // resultSet.getString(6), // APPLICATION_NAME
                        resEntry.setAuditDate((Date) array[5]); // resultSet.getTimestamp(7), // REQUEST_TIMESTAMP
                        resEntry.setAuditType(AuditType.valueOf((String) array[6])); // resultSet.getString(8), // ACTION
                        resEntry.setHostInfo(hostInfo);
                        resEntry.setUserAccount(resAccount);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("AuditEntry: {}", resEntry);
                        }

                        auditList.add(resEntry);
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("AuditList: {}", auditList);
                    }

                    response.setEntryCount(count);
                    response.setAuditList(auditList);
                    response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                }
                else
                {
                    response.setRequestStatus(SecurityRequestStatus.FAILURE);
                }
            }
            else
            {
                response.setRequestStatus(SecurityRequestStatus.SUCCESS);
            }
        }
        catch (final SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new AuditServiceException(sqx.getMessage(), sqx);
        }
        catch (final AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);

            throw new AuditServiceException(acsx.getMessage(), acsx);
        }
        catch (final AuthenticatorException ax)
        {
            ERROR_RECORDER.error(ax.getMessage(), ax);

            throw new AuditServiceException(ax.getMessage(), ax);
        }
        finally
        {
        	if (secConfig.getPerformAudit())
        	{
	            // audit
	            try
	            {
	                AuditEntry entry = new AuditEntry();
	                auditEntry.setHostInfo(reqInfo);
	                auditEntry.setAuditType(AuditType.SHOWAUDIT);
	                auditEntry.setAuthorized(Boolean.TRUE);
	                auditEntry.setUserAccount(request.getUserAccount());
	                auditEntry.setApplicationId(request.getApplicationId());
	                auditEntry.setApplicationName(request.getApplicationName());
	
	                if (DEBUG)
	                {
	                    DEBUGGER.debug("AuditEntry: {}", auditEntry);
	                }
	
	                AuditRequest auditRequest = new AuditRequest();
	                auditRequest.setAuditEntry(entry);
	
	                if (DEBUG)
	                {
	                    DEBUGGER.debug("AuditRequest: {}", auditRequest);
	                }
	
	                auditRequest(auditRequest);
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
