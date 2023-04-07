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
import java.util.List;
import java.util.Objects;
import java.util.ArrayList;
import java.net.InetAddress;
import java.sql.SQLException;
import java.net.UnknownHostException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;

import com.cws.esolutions.utility.securityutils.processors.dto.AuditEntry;
import com.cws.esolutions.utility.securityutils.processors.dto.AuditRequest;
import com.cws.esolutions.utility.securityutils.processors.dto.RequestHostInfo;
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
        final RequestHostInfo reqInfo = request.getHostInfo();

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
            auditList.add((StringUtils.isEmpty(reqInfo.getHostAddress())) ? InetAddress.getLocalHost().getHostAddress() : reqInfo.getHostAddress()); // srcaddr
            auditList.add((StringUtils.isEmpty(reqInfo.getHostName())) ? InetAddress.getLocalHost().getHostName() : reqInfo.getHostName()); // srchost

            if (DEBUG)
            {
                DEBUGGER.debug("auditList: {}", auditList);

                for (String str : auditList)
                {
                	DEBUGGER.debug(str);
                }
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
}
