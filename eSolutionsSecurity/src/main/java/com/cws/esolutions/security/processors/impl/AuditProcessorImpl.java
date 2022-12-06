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
import java.util.ArrayList;
import java.sql.SQLException;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.SecurityServiceConstants;
import com.cws.esolutions.security.processors.dto.AuditEntry;
import com.cws.esolutions.security.processors.enums.AuditType;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.processors.dto.AuditRequest;
import com.cws.esolutions.security.processors.dto.AuditResponse;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.security.processors.interfaces.IAuditProcessor;
import com.cws.esolutions.security.processors.exception.AuditServiceException;
import com.cws.esolutions.security.services.dto.AccessControlServiceRequest;
import com.cws.esolutions.security.services.dto.AccessControlServiceResponse;
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
            if ((hostInfo == null) || (userAccount == null))
            {
                throw new AuditServiceException("Cannot perform audit: RequestHostInfo / UserAccount is missing");
            }

            List<String> auditList = new ArrayList<String>();

            switch (auditEntry.getAuditType())
            {
                case LOGON:
                    auditList.add(userAccount.getUsername()); // usr_audit_userid
                    auditList.add(SecurityServiceConstants.NOT_SET); // usr_audit_userguid
                    auditList.add(auditEntry.getApplicationId()); // usr_audit_applid
                    auditList.add(auditEntry.getApplicationName()); // usr_audit_applname
                    auditList.add(auditEntry.getAuditType().toString()); // usr_audit_action
                    auditList.add(hostInfo.getHostAddress()); // usr_audit_srcaddr
                    auditList.add(hostInfo.getHostName()); // usr_audit_srchost

                    break;
                default:
                    auditList.add(userAccount.getUsername()); // usr_audit_userid
                    auditList.add(userAccount.getGuid()); // usr_audit_userguid
                    auditList.add(auditEntry.getApplicationId()); // usr_audit_applid
                    auditList.add(auditEntry.getApplicationName()); // usr_audit_applname
                    auditList.add(auditEntry.getAuditType().toString()); // usr_audit_action
                    auditList.add(hostInfo.getHostAddress()); // usr_audit_srcaddr
                    auditList.add(hostInfo.getHostName()); // usr_audit_srchost

                    break;
            }

            if (DEBUG)
            {
                DEBUGGER.debug("auditList: {}", auditList);

                for (String str : auditList)
                {
                    DEBUGGER.debug(str);
                }
            }

            try
            {
                // log it ..
                AUDIT_RECORDER.info("AUDIT: User: " + userAccount + ", Requested Action: " + auditEntry.getAuditType() + ", Host: " + hostInfo);

                // .. and stuff in in the db
                auditDAO.auditRequestedOperation(auditList);
            }
            catch (final SQLException sqx)
            {
                ERROR_RECORDER.error(sqx.getMessage(), sqx);

                throw new AuditServiceException(sqx.getMessage(), sqx);
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

        final RequestHostInfo reqInfo = request.getHostInfo();
        final AuditEntry auditEntry = request.getAuditEntry();
        final UserAccount reqAccount = request.getUserAccount();

        if (DEBUG)
        {
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("AuditEntry: {}", auditEntry);
            DEBUGGER.debug("UserAccount: {}", reqAccount);
        }

        try
        {
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
                        UserAccount userAccount = new UserAccount();
                        userAccount.setUsername((String) array[1]); // resultSet.getString(3), // USERNAME
                        userAccount.setGuid((String) array[2]); // resultSet.getString(4), // CN

                        if (DEBUG)
                        {
                            DEBUGGER.debug("UserAccount: {}", userAccount);
                        }

                        AuditEntry resEntry = new AuditEntry();
                        resEntry.setApplicationId((String) (array[3])); // resultSet.getString(5), // APPLICATION_ID
                        resEntry.setApplicationName((String) array[4]); // resultSet.getString(6), // APPLICATION_NAME
                        resEntry.setAuditDate((Date) array[5]); // resultSet.getTimestamp(7), // REQUEST_TIMESTAMP
                        resEntry.setAuditType(AuditType.valueOf((String) array[6])); // resultSet.getString(8), // ACTION
                        resEntry.setHostInfo(hostInfo);
                        resEntry.setUserAccount(userAccount);

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
