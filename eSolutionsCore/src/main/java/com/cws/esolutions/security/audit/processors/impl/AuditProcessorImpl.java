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
package com.cws.esolutions.security.audit.processors.impl;

import java.util.List;
import java.util.ArrayList;
import java.sql.SQLException;

import com.cws.esolutions.security.enums.Role;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.SecurityConstants;
import com.cws.esolutions.security.audit.dto.AuditEntry;
import com.cws.esolutions.security.audit.enums.AuditType;
import com.cws.esolutions.security.audit.dto.AuditRequest;
import com.cws.esolutions.security.audit.dto.AuditResponse;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.audit.exception.AuditServiceException;
import com.cws.esolutions.security.audit.processors.interfaces.IAuditProcessor;
/**
 * SecurityService
 * com.cws.esolutions.security.audit.processors.impl
 * AuditProcessorImpl.java
 *
 *
 *
 * $Id: AuditProcessorImpl.java 2287 2013-01-03 20:52:22Z kmhuntly@gmail.com $
 * $Author: $
 * $Date: 2013-01-03 15:52:22 -0500 (Thu, 03 Jan 2013) $
 * $Revision: 2287 $
 * @author kmhuntly@gmail.com
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Oct 30, 2012 10:38:04 AM
 *     Created.
 */
public class AuditProcessorImpl implements IAuditProcessor
{
    @Override
    public void auditRequest(final AuditRequest request) throws AuditServiceException
    {
        final String methodName = IAuditProcessor.CNAME + "#auditRequest(final AuditRequest request) throws AuditServiceException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AuditRequest: {}", request);
        }

        if (request.doPerformAudit())
        {
            final AuditEntry auditEntry = request.getAuditEntry();
            final RequestHostInfo hostInfo = auditEntry.getReqInfo();
            final UserAccount userAccount = auditEntry.getUserAccount();

            if (DEBUG)
            {
                DEBUGGER.debug("AuditEntry: {}", auditEntry);
                DEBUGGER.debug("UserAccount: {}", userAccount);
                DEBUGGER.debug("RequestHostInfo: {}", hostInfo);
            }

            if (hostInfo == null)
            {
                throw new AuditServiceException("Cannot perform audit: RequestHostInfo is missing");
            }

            List<String> auditList = new ArrayList<String>();

            switch (auditEntry.getAuditType())
            {
                case LOGON:
                    auditList.add(userAccount.getSessionId());
                    auditList.add(userAccount.getUsername());
                    auditList.add(SecurityConstants.NOT_SET);
                    auditList.add(auditEntry.getAuditType().toString());
                    auditList.add(hostInfo.getHostAddress());
                    auditList.add(hostInfo.getHostName());

                    break;
                case RESETPASS:
                    auditList.add(SecurityConstants.NOT_SET);
                    auditList.add(userAccount.getUsername());
                    auditList.add(SecurityConstants.NOT_SET);
                    auditList.add(auditEntry.getAuditType().toString());
                    auditList.add(hostInfo.getHostAddress());
                    auditList.add(hostInfo.getHostName());

                    break;
                default:
                    auditList.add(userAccount.getSessionId());
                    auditList.add(userAccount.getUsername());
                    auditList.add(userAccount.getRole().toString());
                    auditList.add(auditEntry.getAuditType().toString());
                    auditList.add(hostInfo.getHostAddress());
                    auditList.add(hostInfo.getHostName());

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
            catch (SQLException sqx)
            {
                ERROR_RECORDER.error(sqx.getMessage(), sqx);

                throw new AuditServiceException(sqx.getMessage(), sqx);
            }
        }
    }

    @Override
    public AuditResponse getAuditEntries(final AuditRequest request) throws AuditServiceException
    {
        final String methodName = IAuditProcessor.CNAME + "#getAuditEntries(final AuditRequest request) throws AuditServiceException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AuditRequest: {}", request);
        }

        AuditResponse response = new AuditResponse();

        if (request.doPerformAudit())
        {
            final UserAccount user = request.getAuditEntry().getUserAccount();

            if (DEBUG)
            {
                DEBUGGER.debug("UserAccount: {}", user);
            }

            try
            {
                // capture the data for the given user
                List<String[]> dataResponse = auditDAO.getAuditInterval(user.getGuid());

                if (DEBUG)
                {
                    DEBUGGER.debug("Data: {}", dataResponse);
                }

                if (dataResponse.size() != 0)
                {
                    List<AuditEntry> auditList = new ArrayList<AuditEntry>();

                    for (String[] array : dataResponse)
                    {
                        if (DEBUG)
                        {
                            for (String str : array)
                            {
                                DEBUGGER.debug(str);
                            }
                        }

                        // capture
                        UserAccount userAccount = new UserAccount();
                        userAccount.setSessionId(array[0]);
                        userAccount.setUsername(array[1]);
                        userAccount.setRole(Role.valueOf(array[2]));

                        if (DEBUG)
                        {
                            DEBUGGER.debug("UserAccount: {}", userAccount);
                        }

                        RequestHostInfo reqInfo = new RequestHostInfo();
                        reqInfo.setHostAddress(array[6]);
                        reqInfo.setHostName(array[7]);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
                        }

                        AuditEntry auditEntry = new AuditEntry();
                        auditEntry.setAuditDate(Long.valueOf(array[4]));
                        auditEntry.setAuditType(AuditType.valueOf(array[5]));
                        auditEntry.setReqInfo(reqInfo);
                        auditEntry.setUserAccount(userAccount);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("AuditEntry: {}", auditEntry);
                        }

                        auditList.add(auditEntry);
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("AuditList: {}", auditList);
                    }

                    response.setAuditList(auditList);
                    response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                    response.setResponse("Successfully loaded audit entries for user " + request.getAuditEntry().getUserAccount().getUsername());
                }
                else
                {
                    response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                    response.setResponse("No audit entries were found for user " + request.getAuditEntry().getUserAccount().getUsername());
                }
            }
            catch (SQLException sqx)
            {
                ERROR_RECORDER.error(sqx.getMessage(), sqx);

                throw new AuditServiceException(sqx.getMessage(), sqx);
            }
        }
        else
        {
            response.setRequestStatus(SecurityRequestStatus.SUCCESS);
            response.setResponse("Auditing has been disabled in the security configuration.");
        }

        return response;
    }
}
