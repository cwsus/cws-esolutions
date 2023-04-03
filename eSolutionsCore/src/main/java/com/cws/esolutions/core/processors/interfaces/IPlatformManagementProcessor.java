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
package com.cws.esolutions.core.processors.interfaces;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.processors.interfaces
 * File: IPlatformManagementProcessor.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.cws.esolutions.core.CoreServicesConstants;
import com.cws.esolutions.core.dao.impl.PlatformDataDAOImpl;
import com.cws.esolutions.core.dao.interfaces.IPlatformDataDAO;
import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.config.xml.SecurityConfig;
import com.cws.esolutions.security.processors.impl.AuditProcessorImpl;
import com.cws.esolutions.core.processors.dto.PlatformManagementRequest;
import com.cws.esolutions.core.processors.dto.PlatformManagementResponse;
import com.cws.esolutions.security.processors.interfaces.IAuditProcessor;
import com.cws.esolutions.security.services.impl.AccessControlServiceImpl;
import com.cws.esolutions.security.services.interfaces.IAccessControlService;
import com.cws.esolutions.core.processors.exception.PlatformManagementException;
/**
 * API allowing service management functionality.
 *
 * @author cws-khuntly
 * @version 1.0
 */
public interface IPlatformManagementProcessor
{
    static final String CNAME = IPlatformManagementProcessor.class.getName();
    static final SecurityServiceBean secBean = SecurityServiceBean.getInstance();
    static final SecurityConfig secConfig = secBean.getConfigData().getSecurityConfig();
    static final IAuditProcessor auditor = (IAuditProcessor) new AuditProcessorImpl();
    static final IPlatformDataDAO serviceDao = (IPlatformDataDAO) new PlatformDataDAOImpl();
    static final IAccessControlService accessControl = (IAccessControlService) new AccessControlServiceImpl();

    static final Logger DEBUGGER = LogManager.getLogger(CoreServicesConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger ERROR_RECORDER = LogManager.getLogger(CoreServicesConstants.ERROR_LOGGER + CNAME);

    /**
     * Adds the provided service from the service datastore.
     *
     * @param request The {@link com.cws.esolutions.core.processors.dto.PlatformManagementRequest}
     * containing the necessary information to process the request.
     * @return {@link com.cws.esolutions.core.processors.dto.PlatformManagementResponse} containing
     * the response information for the given request
     * @throws PlatformManagementException {@link com.cws.esolutions.core.processors.exception.PlatformManagementException}
     * if an exception occurs during processing
     */
    PlatformManagementResponse addNewPlatform(final PlatformManagementRequest request) throws PlatformManagementException;

    /**
     * Updates information for a provided service within the service datastore.
     *
     * @param request The {@link com.cws.esolutions.core.processors.dto.PlatformManagementRequest}
     * containing the necessary information to process the request.
     * @return {@link com.cws.esolutions.core.processors.dto.PlatformManagementResponse} containing
     * the response information for the given request
     * @throws PlatformManagementException {@link com.cws.esolutions.core.processors.exception.PlatformManagementException}
     * if an exception occurs during processing
     */
    PlatformManagementResponse updatePlatformData(final PlatformManagementRequest request) throws PlatformManagementException;

    /**
     * Removes the provided service from the service datastore by marking it as
     * "decommissioned"
     *
     * @param request The {@link com.cws.esolutions.core.processors.dto.PlatformManagementRequest}
     * containing the necessary information to process the request.
     * @return {@link com.cws.esolutions.core.processors.dto.PlatformManagementResponse} containing
     * the response information for the given request
     * @throws PlatformManagementException {@link com.cws.esolutions.core.processors.exception.PlatformManagementException}
     * if an exception occurs during processing
     */
    PlatformManagementResponse removePlatformData(final PlatformManagementRequest request) throws PlatformManagementException;

    /**
     * Lists all services housed within the service datastore to the requestor for
     * further processing
     *
     * @param request The {@link com.cws.esolutions.core.processors.dto.PlatformManagementRequest}
     * containing the necessary information to process the request.
     * @return {@link com.cws.esolutions.core.processors.dto.PlatformManagementResponse} containing
     * the response information for the given request
     * @throws PlatformManagementException {@link com.cws.esolutions.core.processors.exception.PlatformManagementException}
     * if an exception occurs during processing
     */
    PlatformManagementResponse listPlatforms(final PlatformManagementRequest request) throws PlatformManagementException;

    /**
     * Locates a service (or list of services) for the given search attributes and returns to the
     * requestor for further processing
     *
     * @param request The {@link com.cws.esolutions.core.processors.dto.PlatformManagementRequest}
     * containing the necessary information to process the request.
     * @return {@link com.cws.esolutions.core.processors.dto.PlatformManagementResponse} containing
     * the response information for the given request
     * @throws PlatformManagementException {@link com.cws.esolutions.core.processors.exception.PlatformManagementException}
     * if an exception occurs during processing
     */
    PlatformManagementResponse getPlatformByAttribute(final PlatformManagementRequest request) throws PlatformManagementException;

    /**
     * Obtains service information for a provide service and returns to the requestor for further
     * processing as necessary
     *
     * @param request The {@link com.cws.esolutions.core.processors.dto.PlatformManagementRequest}
     * containing the necessary information to process the request.
     * @return {@link com.cws.esolutions.core.processors.dto.PlatformManagementResponse} containing
     * the response information for the given request
     * @throws PlatformManagementException {@link com.cws.esolutions.core.processors.exception.PlatformManagementException}
     * if an exception occurs during processing
     */
    PlatformManagementResponse getPlatformData(final PlatformManagementRequest request) throws PlatformManagementException;
}
