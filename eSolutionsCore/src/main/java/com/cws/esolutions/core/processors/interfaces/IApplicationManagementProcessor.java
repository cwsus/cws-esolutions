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
 * cws-khuntly   		11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ResourceBundle;

import com.cws.esolutions.core.CoreServiceBean;
import com.cws.esolutions.core.CoreServiceConstants;
import com.cws.esolutions.core.config.xml.AgentConfig;
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
    static final IAuditProcessor auditor = new AuditProcessorImpl();
    static final CoreServiceBean appBean = CoreServiceBean.getInstance();
    static final IServiceDataDAO serviceDao = new ServiceDataDAOImpl();
    static final IApplicationDataDAO appDAO = new ApplicationDataDAOImpl();
    static final String CNAME = IApplicationManagementProcessor.class.getName();
    static final AgentConfig agentConfig = appBean.getConfigData().getAgentConfig();
    static final IAccessControlService accessControl = new AccessControlServiceImpl();
    static final ApplicationConfig appConfig = appBean.getConfigData().getAppConfig();

    // NLS
    static final ResourceBundle bundle = ResourceBundle.getBundle("eSolutionsCore/nls/processors/ApplicationManagementProcessorImpl");
    static final String MESSAGE_INVALID_PLATFORM = "invalid.platform.provided";
    static final String MESSAGE_NO_PLATFORM_PROVIDED = "no.platform.provided";
    static final String MESSAGE_NO_APPLICATIONS_FOUND = "no.applications.found";
    static final String MESSAGE_NO_APPLICATION_DATA_FOUND = "no.application.data.found";
    static final String MESSAGE_MQ_AGENT_FAILED = "agent.message.send.failed";
    static final String MESSAGE_AGENT_REQUEST_FAILED = "message.agent.request.failed";
    static final String MESSAGE_APPLICATION_EXISTS = "message.application.exists";
    static final String MESSAGE_ADD_APPLICATION_FAILED = "message.add.application.failed";
    static final String MESSAGE_UPDATE_APPLICATION_FAILED = "message.update.application.failed";
    static final String MESSAGE_DELETE_APPLICATION_FAILED = "message.delete.application.failed";
    static final String MESSAGE_LIST_APPLICATIONS_FAILED = "message.list.applications.failed";

    static final Logger DEBUGGER = LoggerFactory.getLogger(CoreServiceConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(CoreServiceConstants.ERROR_LOGGER + CNAME);

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
