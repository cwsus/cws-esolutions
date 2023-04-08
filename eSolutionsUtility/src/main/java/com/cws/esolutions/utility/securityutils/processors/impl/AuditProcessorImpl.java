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
package com.cws.esolutions.utility.securityutils.processors.impl;
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
import java.util.Arrays;
import java.util.Objects;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.net.InetAddress;
import java.sql.SQLException;
import java.net.UnknownHostException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;

import com.cws.esolutions.utility.securityutils.processors.dto.AuditEntry;
import com.cws.esolutions.utility.securityutils.processors.dto.AuditRequest;
import com.cws.esolutions.utility.securityutils.processors.dto.AuditResponse;
import com.cws.esolutions.utility.securityutils.processors.enums.AuditRequestStatus;
import com.cws.esolutions.utility.securityutils.processors.enums.AuditType;
import com.cws.esolutions.utility.securityutils.processors.interfaces.IAuditProcessor;
import com.cws.esolutions.utility.securityutils.processors.exception.AuditServiceException;
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
        final List<String> reqInfo = request.getHostInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("AuditEntry: {}", auditEntry);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            List<String> auditList = new ArrayList<String>();
            auditList.add((StringUtils.isEmpty(auditEntry.getSessionId())) ? RandomStringUtils.randomAlphanumeric(128) : auditEntry.getSessionId()); // sessionid
            auditList.add((StringUtils.isEmpty(auditEntry.getUserName())) ? "WEBUSR" : auditEntry.getUserName()); // username
            auditList.add((StringUtils.isEmpty(auditEntry.getUserGuid())) ? "918671b2-662e-4499-9fd3-1e4e88e0fba2" : auditEntry.getUserGuid()); // userguid
            auditList.add((Objects.isNull(auditEntry.getUserRole())) ? "WEBROLE" : auditEntry.getUserRole()); // userrole
            auditList.add((StringUtils.isEmpty(auditEntry.getApplicationId())) ? "SecurityServicesDefault" : auditEntry.getApplicationId()); // applid
            auditList.add((StringUtils.isEmpty(auditEntry.getApplicationName())) ? "SecurityServicesDefault" : auditEntry.getApplicationName()); // applname
            auditList.add((StringUtils.isEmpty(auditEntry.getAuditType().toString())) ? "NONE" : auditEntry.getAuditType().toString()); // useraction
            auditList.add((StringUtils.isEmpty(reqInfo.get(0))) ? InetAddress.getLocalHost().getHostAddress() : reqInfo.get(0)); // srcaddr
            auditList.add((StringUtils.isEmpty(reqInfo.get(1))) ? InetAddress.getLocalHost().getHostName() : reqInfo.get(1)); // srchost

            if (DEBUG)
            {
                DEBUGGER.debug("auditList: {}", auditList);
            }

            // log it ..
            AUDIT_RECORDER.info("AUDIT: User: " + auditEntry.getUserName() + ", Requested Action: " + auditEntry.getAuditType() + ", Host: " + reqInfo);

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

        final AuditEntry auditEntry = request.getAuditEntry();

        if (DEBUG)
        {
            DEBUGGER.debug("AuditEntry: {}", auditEntry);
        }

        try
        {
        	List<Object> dataResponse = auditDAO.getAuditInterval(auditEntry.getUserGuid(), request.getStartRow());

            if (DEBUG)
            {
                DEBUGGER.debug("Data: {}", dataResponse);
            }

            if ((dataResponse != null) && (dataResponse.size() != 0))
            {
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

                    List<String> hostInfo = new ArrayList<String>(
                    		Arrays.asList(
                    				(String) array[8],
                    				(String) array[9]));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("RequestHostInfo: {}", hostInfo);
                    }

                    // capture
                    List<String> userAccount = new ArrayList<String>(Arrays.asList((String) array[1], (String) array[2])); // resultSet.getString(4), // CN

                    if (DEBUG)
                    {
                        DEBUGGER.debug("UserAccount: {}", userAccount);
                    }

                    AuditEntry resEntry = new AuditEntry();
                    resEntry.setSessionId((String) array[0]);
                    resEntry.setUserName((String) array[1]);
                    resEntry.setUserGuid((String) array[2]);
                    resEntry.setUserRole((String) array[3]);
                    resEntry.setApplicationId((String) array[4]);
                    resEntry.setApplicationName((String) array[5]);                    
                    resEntry.setAuditDate(new Date(((Timestamp) array[6]).getTime()));
                    resEntry.setAuditType(AuditType.valueOf((String) array[7]));

                    resEntry.setHostInfo(hostInfo);
                    resEntry.setAccountInfo(userAccount);

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

                response.setEntryCount(dataResponse.size());
                response.setAuditList(auditList);
                response.setRequestStatus(AuditRequestStatus.SUCCESS);
            }
            else
            {
                response.setRequestStatus(AuditRequestStatus.SUCCESS);
            }
        }
        catch (final SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new AuditServiceException(sqx.getMessage(), sqx);
        }

        return response;
    }
}
