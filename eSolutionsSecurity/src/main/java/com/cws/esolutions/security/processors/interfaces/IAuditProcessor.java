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
package com.cws.esolutions.security.processors.interfaces;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.processors.interfaces
 * File: IAuditProcessor.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.SecurityServiceConstants;
import com.cws.esolutions.security.config.xml.SecurityConfig;
import com.cws.esolutions.security.dao.audit.impl.AuditDAOImpl;
import com.cws.esolutions.security.processors.dto.AuditRequest;
import com.cws.esolutions.security.processors.dto.AuditResponse;
import com.cws.esolutions.security.dao.audit.interfaces.IAuditDAO;
import com.cws.esolutions.security.services.impl.AccessControlServiceImpl;
import com.cws.esolutions.security.services.interfaces.IAccessControlService;
import com.cws.esolutions.security.processors.exception.AuditServiceException;
/**
 * API allowing audit processing, if enabled.
 *
 * @author cws-khuntly
 * @version 1.0
 */
public interface IAuditProcessor
{
    static final IAuditDAO auditDAO = (IAuditDAO) new AuditDAOImpl();
    static final SecurityServiceBean svcBean = SecurityServiceBean.getInstance();
    static final SecurityConfig secConfig = svcBean.getConfigData().getSecurityConfig();
    static final IAccessControlService accessControl = (IAccessControlService) new AccessControlServiceImpl();

    static final String SERVICE_ID = "360144AC-7234-406A-B152-08CD080459A6";

    static final Logger ERROR_RECORDER = LogManager.getLogger(SecurityServiceConstants.ERROR_LOGGER);
    static final Logger AUDIT_RECORDER = LogManager.getLogger(SecurityServiceConstants.AUDIT_LOGGER);
    static final Logger DEBUGGER = LogManager.getLogger(SecurityServiceConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    /**
     * Audits the performed request, if enabled.
     *
     * @param request - The {@link com.cws.esolutions.security.processors.dto.AuditRequest}
     * which contains the necessary information to complete the request
     * @throws AuditServiceException {@link com.cws.esolutions.security.processors.exception.AuditServiceException} if an exception occurs during processing
     */
    void auditRequest(final AuditRequest request) throws AuditServiceException;

    /**
     * Provides a list of audit history for the provided user account, if enabled.
     *
     * @param request - The {@link com.cws.esolutions.security.processors.dto.AuditRequest}
     * which contains the necessary information to complete the request
     * @return {@link com.cws.esolutions.security.processors.dto.AuditResponse} containing
     * response information regarding the request status
     * @throws AuditServiceException {@link com.cws.esolutions.security.processors.exception.AuditServiceException} if an exception occurs during processing
     */
    AuditResponse getAuditEntries(final AuditRequest request) throws AuditServiceException;
}
