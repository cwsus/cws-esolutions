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
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.core.CoreServiceConstants;
import com.cws.esolutions.core.dao.impl.ServerDataDAOImpl;
import com.cws.esolutions.core.dao.impl.ServiceDataDAOImpl;
import com.cws.esolutions.core.dao.interfaces.IServerDataDAO;
import com.cws.esolutions.core.dao.interfaces.IServiceDataDAO;
import com.cws.esolutions.security.processors.impl.AuditProcessorImpl;
import com.cws.esolutions.core.processors.dto.ServiceManagementRequest;
import com.cws.esolutions.core.processors.dto.ServiceManagementResponse;
import com.cws.esolutions.security.processors.interfaces.IAuditProcessor;
import com.cws.esolutions.security.services.impl.AccessControlServiceImpl;
import com.cws.esolutions.security.services.interfaces.IAccessControlService;
import com.cws.esolutions.core.processors.exception.ServiceManagementException;
/**
 * API allowing service management functionality.
 *
 * @author khuntly
 * @version 1.0
 */
public interface IServiceManagementProcessor
{
    static final IAuditProcessor auditor = new AuditProcessorImpl();
    static final IServerDataDAO serverDao = new ServerDataDAOImpl();
    static final IServiceDataDAO serviceDao = new ServiceDataDAOImpl();
    static final String CNAME = IServiceManagementProcessor.class.getName();
    static final IAccessControlService accessControl = new AccessControlServiceImpl();

    static final Logger DEBUGGER = LoggerFactory.getLogger(CoreServiceConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(CoreServiceConstants.ERROR_LOGGER + CNAME);

    /**
     * Adds the provided service from the service datastore.
     *
     * @param request The {@link com.cws.esolutions.core.processors.dto.ServiceManagementRequest}
     * containing the necessary information to process the request.
     * @return {@link com.cws.esolutions.core.processors.dto.ServiceManagementResponse} containing
     * the response information for the given request
     * @throws ServiceManagementException {@link com.cws.esolutions.core.processors.exception.ServiceManagementException}
     * if an exception occurs during processing
     */
    ServiceManagementResponse addNewService(final ServiceManagementRequest request) throws ServiceManagementException;

    /**
     * Updates information for a provided service within the service datastore.
     *
     * @param request The {@link com.cws.esolutions.core.processors.dto.ServiceManagementRequest}
     * containing the necessary information to process the request.
     * @return {@link com.cws.esolutions.core.processors.dto.ServiceManagementResponse} containing
     * the response information for the given request
     * @throws ServiceManagementException {@link com.cws.esolutions.core.processors.exception.ServiceManagementException}
     * if an exception occurs during processing
     */
    ServiceManagementResponse updateServiceData(final ServiceManagementRequest request) throws ServiceManagementException;

    /**
     * Removes the provided service from the service datastore by marking it as
     * "decommissioned"
     *
     * @param request The {@link com.cws.esolutions.core.processors.dto.ServiceManagementRequest}
     * containing the necessary information to process the request.
     * @return {@link com.cws.esolutions.core.processors.dto.ServiceManagementResponse} containing
     * the response information for the given request
     * @throws ServiceManagementException {@link com.cws.esolutions.core.processors.exception.ServiceManagementException}
     * if an exception occurs during processing
     */
    ServiceManagementResponse removeServiceData(final ServiceManagementRequest request) throws ServiceManagementException;

    /**
     * Lists all services housed within the service datastore to the requestor for
     * further processing
     *
     * @param request The {@link com.cws.esolutions.core.processors.dto.ServiceManagementRequest}
     * containing the necessary information to process the request.
     * @return {@link com.cws.esolutions.core.processors.dto.ServiceManagementResponse} containing
     * the response information for the given request
     * @throws ServiceManagementException {@link com.cws.esolutions.core.processors.exception.ServiceManagementException}
     * if an exception occurs during processing
     */
    ServiceManagementResponse listServices(final ServiceManagementRequest request) throws ServiceManagementException;

    /**
     * Locates a service (or list of services) for the given type attributes and returns to the
     * requestor for further processing
     *
     * @param request The {@link com.cws.esolutions.core.processors.dto.ServiceManagementRequest}
     * containing the necessary information to process the request.
     * @return {@link com.cws.esolutions.core.processors.dto.ServiceManagementResponse} containing
     * the response information for the given request
     * @throws ServiceManagementException {@link com.cws.esolutions.core.processors.exception.ServiceManagementException}
     * if an exception occurs during processing
     */
    ServiceManagementResponse listServicesByType(final ServiceManagementRequest request) throws ServiceManagementException;

    /**
     * Locates a service (or list of services) for the given search attributes and returns to the
     * requestor for further processing
     *
     * @param request The {@link com.cws.esolutions.core.processors.dto.ServiceManagementRequest}
     * containing the necessary information to process the request.
     * @return {@link com.cws.esolutions.core.processors.dto.ServiceManagementResponse} containing
     * the response information for the given request
     * @throws ServiceManagementException {@link com.cws.esolutions.core.processors.exception.ServiceManagementException}
     * if an exception occurs during processing
     */
    ServiceManagementResponse getServiceByAttribute(final ServiceManagementRequest request) throws ServiceManagementException;

    /**
     * Obtains service information for a provide service and returns to the requestor for further
     * processing as necessary
     *
     * @param request The {@link com.cws.esolutions.core.processors.dto.ServiceManagementRequest}
     * containing the necessary information to process the request.
     * @return {@link com.cws.esolutions.core.processors.dto.ServiceManagementResponse} containing
     * the response information for the given request
     * @throws ServiceManagementException {@link com.cws.esolutions.core.processors.exception.ServiceManagementException}
     * if an exception occurs during processing
     */
    ServiceManagementResponse getServiceData(final ServiceManagementRequest request) throws ServiceManagementException;
}
