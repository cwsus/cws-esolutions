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
 * File: VirtualServiceManager.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
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

    static final Logger DEBUGGER = LoggerFactory.getLogger(CoreServiceConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(CoreServiceConstants.ERROR_LOGGER + CNAME);

    /**
     * 
     * TODO: Add in the method description/comments
     *
     * @param request The request information to add the new application
     * @return <code>ApplicationManagementResponse</code> containing the response information
     * @throws ApplicationManagementException
     */
    ApplicationManagementResponse addNewApplication(final ApplicationManagementRequest request) throws ApplicationManagementException;

    /**
     * 
     * TODO: Add in the method description/comments
     *
     * @param request
     * @return
     * @throws ApplicationManagementException
     */
    ApplicationManagementResponse updateApplicationData(final ApplicationManagementRequest request) throws ApplicationManagementException;

    /**
     * 
     * TODO: Add in the method description/comments
     *
     * @param request
     * @return
     * @throws ApplicationManagementException
     */
    ApplicationManagementResponse deleteApplicationData(final ApplicationManagementRequest request) throws ApplicationManagementException;

    /**
     * 
     * TODO: Add in the method description/comments
     *
     * @param request
     * @return
     * @throws ApplicationManagementException
     */
    ApplicationManagementResponse listApplications(final ApplicationManagementRequest request) throws ApplicationManagementException;

    /**
     * 
     * TODO: Add in the method description/comments
     *
     * @param request
     * @return
     * @throws ApplicationManagementException
     */
    ApplicationManagementResponse listApplicationsByAttribute(final ApplicationManagementRequest request) throws ApplicationManagementException;

    /**
     * 
     * TODO: Add in the method description/comments
     *
     * @param request
     * @return
     * @throws ApplicationManagementException
     */
    ApplicationManagementResponse getApplicationData(final ApplicationManagementRequest request) throws ApplicationManagementException;

    /**
     * 
     * TODO: Add in the method description/comments
     *
     * @param request
     * @return
     * @throws ApplicationManagementException
     */
    ApplicationManagementResponse applicationFileRequest(final ApplicationManagementRequest request) throws ApplicationManagementException;

    /**
     * 
     * TODO: Add in the method description/comments
     *
     * @param request
     * @return
     * @throws ApplicationManagementException
     */
    ApplicationManagementResponse deployApplication(final ApplicationManagementRequest request) throws ApplicationManagementException;
}
