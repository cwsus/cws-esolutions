/*
 * Copyright (c) 2009 - 2014 CaspersBox Web Services
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
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 */
public interface IAuditProcessor
{
    static final IAuditDAO auditDAO = new AuditDAOImpl();
    static final SecurityServiceBean svcBean = SecurityServiceBean.getInstance();
    static final IAccessControlService accessControl = new AccessControlServiceImpl();
    static final SecurityConfig secConfig = svcBean.getConfigData().getSecurityConfig();

    static final String CNAME = IAuditProcessor.class.getName();
    static final String SERVICE_ID = "360144AC-7234-406A-B152-08CD080459A6";

    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityServiceConstants.ERROR_LOGGER + CNAME);
    static final Logger AUDIT_RECORDER = LoggerFactory.getLogger(SecurityServiceConstants.AUDIT_LOGGER + CNAME);
    static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityServiceConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    void auditRequest(final AuditRequest request) throws AuditServiceException;

    AuditResponse getAuditEntries(final AuditRequest request) throws AuditServiceException;
}
