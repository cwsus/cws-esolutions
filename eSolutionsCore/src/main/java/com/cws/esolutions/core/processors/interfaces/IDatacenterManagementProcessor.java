/*
 * Copyright (c) 2009 - 2013 By: CWS, Inc.
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

import com.cws.esolutions.core.Constants;
import com.cws.esolutions.core.CoreServiceBean;
import com.cws.esolutions.core.config.xml.ApplicationConfig;
import com.cws.esolutions.security.processors.impl.AuditProcessorImpl;
import com.cws.esolutions.core.dao.processors.impl.DatacenterDataDAOImpl;
import com.cws.esolutions.security.processors.interfaces.IAuditProcessor;
import com.cws.esolutions.core.processors.dto.DatacenterManagementRequest;
import com.cws.esolutions.security.services.impl.AccessControlServiceImpl;
import com.cws.esolutions.core.processors.dto.DatacenterManagementResponse;
import com.cws.esolutions.core.dao.processors.interfaces.IDatacenterDataDAO;
import com.cws.esolutions.security.services.interfaces.IAccessControlService;
import com.cws.esolutions.core.processors.exception.DatacenterManagementException;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 */
public interface IDatacenterManagementProcessor
{
    static final IAuditProcessor auditor = new AuditProcessorImpl();
    static final CoreServiceBean appBean = CoreServiceBean.getInstance();
    static final IDatacenterDataDAO datactrDAO = new DatacenterDataDAOImpl();
    static final ApplicationConfig appConfig = appBean.getConfigData().getAppConfig();
    static final IAccessControlService accessControl = new AccessControlServiceImpl();

    static final String CNAME = IDatacenterManagementProcessor.class.getName();

    static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CNAME);
    static final Logger WARN_RECORDER = LoggerFactory.getLogger(Constants.WARN_LOGGER + CNAME);

    DatacenterManagementResponse addNewDatacenter(final DatacenterManagementRequest request) throws DatacenterManagementException;

    DatacenterManagementResponse updateDatacenter(final DatacenterManagementRequest request) throws DatacenterManagementException;

    DatacenterManagementResponse listDatacenters(final DatacenterManagementRequest request) throws DatacenterManagementException;

    DatacenterManagementResponse getDatacenter(final DatacenterManagementRequest request) throws DatacenterManagementException;
}
