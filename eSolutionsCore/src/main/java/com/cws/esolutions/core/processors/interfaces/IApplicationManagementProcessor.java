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
 * File: VirtualServiceManager.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.cws.esolutions.core.CoreServicesBean;
import com.cws.esolutions.core.CoreServicesConstants;
import com.cws.esolutions.core.dao.impl.ServiceDataDAOImpl;
import com.cws.esolutions.core.config.xml.ApplicationConfig;
import com.cws.esolutions.core.dao.interfaces.IServiceDataDAO;
import com.cws.esolutions.core.dao.impl.ApplicationDataDAOImpl;
import com.cws.esolutions.core.dao.interfaces.IApplicationDataDAO;
import com.cws.esolutions.security.processors.impl.AuditProcessorImpl;
import com.cws.esolutions.security.processors.interfaces.IAuditProcessor;
import com.cws.esolutions.security.services.impl.AccessControlServiceImpl;
import com.cws.esolutions.core.processors.dto.ApplicationManagementRequest;
import com.cws.esolutions.core.processors.dto.ApplicationManagementResponse;
import com.cws.esolutions.security.services.interfaces.IAccessControlService;
import com.cws.esolutions.core.processors.exception.ApplicationManagementException;
/**
 * API allowing application management functionality
 *
 * @author cws-khuntly
 * @version 1.0
 */
public interface IApplicationManagementProcessor
{
    static final CoreServicesBean appBean = CoreServicesBean.getInstance();
    static final ApplicationConfig appConfig = appBean.getConfigData().getAppConfig();
    static final IAuditProcessor auditor = (IAuditProcessor) new AuditProcessorImpl();
    static final IServiceDataDAO serviceDao = (IServiceDataDAO) new ServiceDataDAOImpl();
    static final IApplicationDataDAO appDAO = (IApplicationDataDAO) new ApplicationDataDAOImpl();
    static final IAccessControlService accessControl = (IAccessControlService) new AccessControlServiceImpl();

    static final Logger DEBUGGER = LogManager.getLogger(CoreServicesConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger ERROR_RECORDER = LogManager.getLogger(CoreServicesConstants.ERROR_LOGGER);

    /**
     * Allows addition of a new application to the service datastore
     *
     * @param request - The {@link com.cws.esolutions.core.processors.dto.ApplicationManagementRequest}
     * housing the necessary data to process
     * @return The {@link com.cws.esolutions.core.processors.dto.ApplicationManagementResponse} containing the response information, or error code
     * @throws ApplicationManagementException {@link com.cws.esolutions.core.processors.exception.ApplicationManagementException} if an error occurs during processing
     */
    ApplicationManagementResponse addNewApplication(final ApplicationManagementRequest request) throws ApplicationManagementException;

    /**
     * Allows updates to aa application in the service datastore
     *
     * @param request - The {@link com.cws.esolutions.core.processors.dto.ApplicationManagementRequest}
     * housing the necessary data to process
     * @return The {@link com.cws.esolutions.core.processors.dto.ApplicationManagementResponse} containing the response information, or error code
     * @throws ApplicationManagementException {@link com.cws.esolutions.core.processors.exception.ApplicationManagementException} if an error occurs during processing
     */
    ApplicationManagementResponse updateApplicationData(final ApplicationManagementRequest request) throws ApplicationManagementException;

    /**
     * Allows removal of an application in the service datastore
     *
     * @param request - The {@link com.cws.esolutions.core.processors.dto.ApplicationManagementRequest}
     * housing the necessary data to process
     * @return The {@link com.cws.esolutions.core.processors.dto.ApplicationManagementResponse} containing the response information, or error code
     * @throws ApplicationManagementException {@link com.cws.esolutions.core.processors.exception.ApplicationManagementException} if an error occurs during processing
     */
    ApplicationManagementResponse deleteApplicationData(final ApplicationManagementRequest request) throws ApplicationManagementException;

    /**
     * Lists all applications housed within the service datastore
     *
     * @param request - The {@link com.cws.esolutions.core.processors.dto.ApplicationManagementRequest}
     * housing the necessary data to process
     * @return The {@link com.cws.esolutions.core.processors.dto.ApplicationManagementResponse} containing the response information, or error code
     * @throws ApplicationManagementException {@link com.cws.esolutions.core.processors.exception.ApplicationManagementException} if an error occurs during processing
     */
    ApplicationManagementResponse listApplications(final ApplicationManagementRequest request) throws ApplicationManagementException;

    /**
     * Lists all applications with the provided attributes housed within the service datastore
     *
     * @param request - The {@link com.cws.esolutions.core.processors.dto.ApplicationManagementRequest}
     * housing the necessary data to process
     * @return The {@link com.cws.esolutions.core.processors.dto.ApplicationManagementResponse} containing the response information, or error code
     * @throws ApplicationManagementException {@link com.cws.esolutions.core.processors.exception.ApplicationManagementException} if an error occurs during processing
     */
    ApplicationManagementResponse listApplicationsByAttribute(final ApplicationManagementRequest request) throws ApplicationManagementException;

    /**
     * Obtains detailed information regarding a provided application housed within the service datastore
     *
     * @param request - The {@link com.cws.esolutions.core.processors.dto.ApplicationManagementRequest}
     * housing the necessary data to process
     * @return The {@link com.cws.esolutions.core.processors.dto.ApplicationManagementResponse} containing the response information, or error code
     * @throws ApplicationManagementException {@link com.cws.esolutions.core.processors.exception.ApplicationManagementException} if an error occurs during processing
     */
    ApplicationManagementResponse getApplicationData(final ApplicationManagementRequest request) throws ApplicationManagementException;

    /**
     * Obtains file information from a remote node housing a given application and returns
     * to the requestor for further processing
     *
     * @param request - The {@link com.cws.esolutions.core.processors.dto.ApplicationManagementRequest}
     * housing the necessary data to process
     * @return The {@link com.cws.esolutions.core.processors.dto.ApplicationManagementResponse} containing the response information, or error code
     * @throws ApplicationManagementException {@link com.cws.esolutions.core.processors.exception.ApplicationManagementException} if an error occurs during processing
     */
    ApplicationManagementResponse applicationFileRequest(final ApplicationManagementRequest request) throws ApplicationManagementException;

    /**
     * Allows deployment of application-related files to target remote nodes
     *
     * @param request - The {@link com.cws.esolutions.core.processors.dto.ApplicationManagementRequest}
     * housing the necessary data to process
     * @return The {@link com.cws.esolutions.core.processors.dto.ApplicationManagementResponse} containing the response information, or error code
     * @throws ApplicationManagementException {@link com.cws.esolutions.core.processors.exception.ApplicationManagementException} if an error occurs during processing
     */
    ApplicationManagementResponse deployApplication(final ApplicationManagementRequest request) throws ApplicationManagementException;
}
