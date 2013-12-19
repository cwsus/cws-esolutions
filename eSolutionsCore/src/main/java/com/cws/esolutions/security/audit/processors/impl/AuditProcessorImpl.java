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
package com.cws.esolutions.security.audit.processors.impl;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.security.audit.processors.impl
 * File: AuditProcessorImpl.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.Date;
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
 * @see com.cws.esolutions.security.audit.processors.interfaces.IAuditProcessor
 */
public class AuditProcessorImpl implements IAuditProcessor
{
    /**
     * @see com.cws.esolutions.security.audit.processors.interfaces.IAuditProcessor#auditRequest(com.cws.esolutions.security.audit.dto.AuditRequest)
     */
    @Override
    public void auditRequest(final AuditRequest request) throws AuditServiceException
    {
        final String methodName = IAuditProcessor.CNAME + "#auditRequest(final AuditRequest request) throws AuditServiceException";

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

            List<String> auditList = new ArrayList<>();

            switch (auditEntry.getAuditType())
            {
                case LOGON:
                    auditList.add(hostInfo.getSessionId()); // usr_audit_sessionid
                    auditList.add(userAccount.getUsername()); // usr_audit_userid
                    auditList.add(SecurityConstants.NOT_SET); // usr_audit_userguid
                    auditList.add(SecurityConstants.NOT_SET); // usr_audit_role
                    auditList.add(auditEntry.getApplicationId()); // usr_audit_applid
                    auditList.add(auditEntry.getApplicationName()); // usr_audit_applname
                    auditList.add(auditEntry.getAuditType().toString()); // usr_audit_action
                    auditList.add(hostInfo.getHostAddress()); // usr_audit_srcaddr
                    auditList.add(hostInfo.getHostName()); // usr_audit_srchost

                    break;
                default:
                    auditList.add(hostInfo.getSessionId()); // usr_audit_sessionid
                    auditList.add(userAccount.getUsername()); // usr_audit_userid
                    auditList.add(userAccount.getGuid()); // usr_audit_userguid
                    auditList.add(userAccount.getRole().name()); // usr_audit_role
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
            catch (SQLException sqx)
            {
                ERROR_RECORDER.error(sqx.getMessage(), sqx);

                throw new AuditServiceException(sqx.getMessage(), sqx);
            }
        }
    }

    /**
     * @see com.cws.esolutions.security.audit.processors.interfaces.IAuditProcessor#getAuditEntries(com.cws.esolutions.security.audit.dto.AuditRequest)
     */
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

        final AuditEntry auditEntry = request.getAuditEntry();

        if (DEBUG)
        {
            DEBUGGER.debug("AuditEntry: {}", auditEntry);
        }

        if (secConfig.getPerformAudit())
        {
            final UserAccount user = auditEntry.getUserAccount();

            if (DEBUG)
            {
                DEBUGGER.debug("UserAccount: {}", user);
            }

            try
            {
                // capture the data for the given user
                int rowCount = auditDAO.getAuditCount(user.getGuid());

                if (DEBUG)
                {
                    DEBUGGER.debug("rowCount: {}", rowCount);
                }

                List<String[]> dataResponse = auditDAO.getAuditInterval(user.getGuid(), request.getStartRow());

                if (DEBUG)
                {
                    DEBUGGER.debug("Data: {}", dataResponse);
                }

                if ((dataResponse != null) && (dataResponse.size() != 0))
                {
                    List<AuditEntry> auditList = new ArrayList<>();

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
                        userAccount.setUsername(array[1]); // usr_audit_userid
                        userAccount.setGuid(array[2]); // usr_audit_userguid
                        userAccount.setRole(Role.valueOf(array[3])); // usr_audit_role

                        if (DEBUG)
                        {
                            DEBUGGER.debug("UserAccount: {}", userAccount);
                        }

                        RequestHostInfo reqInfo = new RequestHostInfo();
                        reqInfo.setHostAddress(array[8]);
                        reqInfo.setHostName(array[9]);
                        reqInfo.setSessionId(array[0]);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
                        }

                        AuditEntry resEntry = new AuditEntry();
                        resEntry.setApplicationId(array[4]); // usr_audit_applid
                        resEntry.setApplicationName(array[5]); // usr_audit_applname
                        resEntry.setAuditDate(new Date(Long.valueOf(array[6]))); // usr_audit_timestamp
                        resEntry.setAuditType(AuditType.valueOf(array[7])); // usr_audit_action
                        resEntry.setHostInfo(reqInfo);
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

                    response.setEntryCount(rowCount);
                    response.setAuditList(auditList);
                    response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                }
                else
                {
                    response.setRequestStatus(SecurityRequestStatus.SUCCESS);
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
            response.setRequestStatus(SecurityRequestStatus.DISABLED);
        }

        return response;
    }
}
