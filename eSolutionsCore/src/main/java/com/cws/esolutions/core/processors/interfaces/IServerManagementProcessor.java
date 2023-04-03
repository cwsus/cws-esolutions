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
 * File: IServerManagementProcessor.java
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
import com.cws.esolutions.core.dao.impl.ServerDataDAOImpl;
import com.cws.esolutions.core.dao.interfaces.IServerDataDAO;
import com.cws.esolutions.core.processors.dto.ServerManagementRequest;
import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.config.xml.SecurityConfig;
import com.cws.esolutions.security.processors.impl.AuditProcessorImpl;
import com.cws.esolutions.core.processors.dto.ServerManagementResponse;
import com.cws.esolutions.security.processors.interfaces.IAuditProcessor;
import com.cws.esolutions.security.services.impl.AccessControlServiceImpl;
import com.cws.esolutions.security.services.interfaces.IAccessControlService;
import com.cws.esolutions.core.processors.exception.ServerManagementException;
/**
 * API allowing server management functionality within the service datastore
 *
 * @author cws-khuntly
 * @version 1.0
 */
public interface IServerManagementProcessor
{
	static final String CNAME = IServerManagementProcessor.class.getName();
    static final IServerDataDAO serverDAO = (IServerDataDAO) new ServerDataDAOImpl();
    static final IAuditProcessor auditor = (IAuditProcessor) new AuditProcessorImpl();
    static final SecurityServiceBean secBean = SecurityServiceBean.getInstance();
    static final SecurityConfig secConfig = secBean.getConfigData().getSecurityConfig();
    static final IAccessControlService accessControl = (IAccessControlService) new AccessControlServiceImpl();

    static final Logger DEBUGGER = LogManager.getLogger(CoreServicesConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger ERROR_RECORDER = LogManager.getLogger(CoreServicesConstants.ERROR_LOGGER + CNAME);

    /**
     * Adds the provided server to the service datastore.
     *
     * @param request The {@link com.cws.esolutions.core.processors.dto.ServerManagementRequest}
     * containing the necessary information to process the request.
     * @return {@link com.cws.esolutions.core.processors.dto.ServerManagementResponse} containing
     * the response information for the given request
     * @throws ServerManagementException {@link com.cws.esolutions.core.processors.exception.ServerManagementException}
     * if an exception occurs during processing
     */
    ServerManagementResponse addNewServer(final ServerManagementRequest request) throws ServerManagementException;

    /**
     * Updates the provided server to the service datastore.
     *
     * @param request The {@link com.cws.esolutions.core.processors.dto.ServerManagementRequest}
     * containing the necessary information to process the request.
     * @return {@link com.cws.esolutions.core.processors.dto.ServerManagementResponse} containing
     * the response information for the given request
     * @throws ServerManagementException {@link com.cws.esolutions.core.processors.exception.ServerManagementException}
     * if an exception occurs during processing
     */
    ServerManagementResponse updateServerData(final ServerManagementRequest request) throws ServerManagementException;

    /**
     * Removes the provided server to the service datastore by marking it as "decommissioned"
     *
     * @param request The {@link com.cws.esolutions.core.processors.dto.ServerManagementRequest}
     * containing the necessary information to process the request.
     * @return {@link com.cws.esolutions.core.processors.dto.ServerManagementResponse} containing
     * the response information for the given request
     * @throws ServerManagementException {@link com.cws.esolutions.core.processors.exception.ServerManagementException}
     * if an exception occurs during processing
     */
    ServerManagementResponse removeServerData(final ServerManagementRequest request) throws ServerManagementException;

    /**
     * Lists all servers housed within the service datastore and returns to the requestor
     * for further processing
     *
     * @param request The {@link com.cws.esolutions.core.processors.dto.ServerManagementRequest}
     * containing the necessary information to process the request.
     * @return {@link com.cws.esolutions.core.processors.dto.ServerManagementResponse} containing
     * the response information for the given request
     * @throws ServerManagementException {@link com.cws.esolutions.core.processors.exception.ServerManagementException}
     * if an exception occurs during processing
     */
    ServerManagementResponse listServers(final ServerManagementRequest request) throws ServerManagementException;

    /**
     * Lists all servers with the provided attribute housed within the service datastore and returns to the requestor
     * for further processing
     *
     * @param request The {@link com.cws.esolutions.core.processors.dto.ServerManagementRequest}
     * containing the necessary information to process the request.
     * @return {@link com.cws.esolutions.core.processors.dto.ServerManagementResponse} containing
     * the response information for the given request
     * @throws ServerManagementException {@link com.cws.esolutions.core.processors.exception.ServerManagementException}
     * if an exception occurs during processing
     */
    ServerManagementResponse listServersByAttribute(final ServerManagementRequest request) throws ServerManagementException;

    /**
     * Returns detailed information for the provided server and returns to the requestor for further
     * processing
     *
     * @param request The {@link com.cws.esolutions.core.processors.dto.ServerManagementRequest}
     * containing the necessary information to process the request.
     * @return {@link com.cws.esolutions.core.processors.dto.ServerManagementResponse} containing
     * the response information for the given request
     * @throws ServerManagementException {@link com.cws.esolutions.core.processors.exception.ServerManagementException}
     * if an exception occurs during processing
     */
    ServerManagementResponse getServerData(final ServerManagementRequest request) throws ServerManagementException;
}
