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
import com.cws.esolutions.core.dao.impl.DatacenterDataDAOImpl;
import com.cws.esolutions.core.dao.interfaces.IDatacenterDataDAO;
import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.config.xml.SecurityConfig;
import com.cws.esolutions.security.processors.impl.AuditProcessorImpl;
import com.cws.esolutions.security.processors.interfaces.IAuditProcessor;
import com.cws.esolutions.security.services.impl.AccessControlServiceImpl;
import com.cws.esolutions.core.processors.dto.DatacenterManagementRequest;
import com.cws.esolutions.core.processors.dto.DatacenterManagementResponse;
import com.cws.esolutions.security.services.interfaces.IAccessControlService;
import com.cws.esolutions.core.processors.exception.DatacenterManagementException;
/**
 * API allowing service management functionality.
 *
 * @author cws-khuntly
 * @version 1.0
 */
public interface IDatacenterManagementProcessor
{
    static final String CNAME = IDatacenterManagementProcessor.class.getName();
    static final IAuditProcessor auditor = (IAuditProcessor) new AuditProcessorImpl();
    static final SecurityServiceBean secBean = SecurityServiceBean.getInstance();
    static final SecurityConfig secConfig = secBean.getConfigData().getSecurityConfig();
    static final IDatacenterDataDAO datacenterDao = (IDatacenterDataDAO) new DatacenterDataDAOImpl();
    static final IAccessControlService accessControl = (IAccessControlService) new AccessControlServiceImpl();

    static final Logger DEBUGGER = LogManager.getLogger(CoreServicesConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger ERROR_RECORDER = LogManager.getLogger(CoreServicesConstants.ERROR_LOGGER + CNAME);

    /**
     * Adds the provided service from the service datastore.
     *
     * @param request The {@link com.cws.esolutions.core.processors.dto.DatacenterManagementRequest}
     * containing the necessary information to process the request.
     * @return {@link com.cws.esolutions.core.processors.dto.DatacenterManagementResponse} containing
     * the response information for the given request
     * @throws DatacenterManagementException {@link com.cws.esolutions.core.processors.exception.DatacenterManagementException}
     * if an exception occurs during processing
     */
    DatacenterManagementResponse addNewDatacenter(final DatacenterManagementRequest request) throws DatacenterManagementException;

    /**
     * Updates information for a provided service within the service datastore.
     *
     * @param request The {@link com.cws.esolutions.core.processors.dto.DatacenterManagementRequest}
     * containing the necessary information to process the request.
     * @return {@link com.cws.esolutions.core.processors.dto.DatacenterManagementResponse} containing
     * the response information for the given request
     * @throws DatacenterManagementException {@link com.cws.esolutions.core.processors.exception.DatacenterManagementException}
     * if an exception occurs during processing
     */
    DatacenterManagementResponse updateDatacenter(final DatacenterManagementRequest request) throws DatacenterManagementException;

    /**
     * Removes the provided service from the service datastore by marking it as
     * "decommissioned"
     *
     * @param request The {@link com.cws.esolutions.core.processors.dto.DatacenterManagementRequest}
     * containing the necessary information to process the request.
     * @return {@link com.cws.esolutions.core.processors.dto.DatacenterManagementResponse} containing
     * the response information for the given request
     * @throws DatacenterManagementException {@link com.cws.esolutions.core.processors.exception.DatacenterManagementException}
     * if an exception occurs during processing
     */
    DatacenterManagementResponse removeDatacenter(final DatacenterManagementRequest request) throws DatacenterManagementException;

    /**
     * Lists all services housed within the service datastore to the requestor for
     * further processing
     *
     * @param request The {@link com.cws.esolutions.core.processors.dto.DatacenterManagementRequest}
     * containing the necessary information to process the request.
     * @return {@link com.cws.esolutions.core.processors.dto.DatacenterManagementResponse} containing
     * the response information for the given request
     * @throws DatacenterManagementException {@link com.cws.esolutions.core.processors.exception.DatacenterManagementException}
     * if an exception occurs during processing
     */
    DatacenterManagementResponse listDatacenters(final DatacenterManagementRequest request) throws DatacenterManagementException;

    /**
     * Locates a service (or list of services) for the given search attributes and returns to the
     * requestor for further processing
     *
     * @param request The {@link com.cws.esolutions.core.processors.dto.DatacenterManagementRequest}
     * containing the necessary information to process the request.
     * @return {@link com.cws.esolutions.core.processors.dto.DatacenterManagementResponse} containing
     * the response information for the given request
     * @throws DatacenterManagementException {@link com.cws.esolutions.core.processors.exception.DatacenterManagementException}
     * if an exception occurs during processing
     */
    DatacenterManagementResponse getDatacenterByAttribute(final DatacenterManagementRequest request) throws DatacenterManagementException;

    /**
     * Obtains service information for a provide service and returns to the requestor for further
     * processing as necessary
     *
     * @param request The {@link com.cws.esolutions.core.processors.dto.DatacenterManagementRequest}
     * containing the necessary information to process the request.
     * @return {@link com.cws.esolutions.core.processors.dto.DatacenterManagementResponse} containing
     * the response information for the given request
     * @throws DatacenterManagementException {@link com.cws.esolutions.core.processors.exception.DatacenterManagementException}
     * if an exception occurs during processing
     */
    DatacenterManagementResponse getDatacenterData(final DatacenterManagementRequest request) throws DatacenterManagementException;
}
