/**
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.core.Constants;
import com.cws.esolutions.core.CoreServiceBean;
import com.cws.esolutions.core.config.ApplicationConfig;
import com.cws.esolutions.core.dao.processors.impl.DatacenterDataDAOImpl;
import com.cws.esolutions.core.processors.dto.DatacenterManagementRequest;
import com.cws.esolutions.core.processors.dto.DatacenterManagementResponse;
import com.cws.esolutions.core.dao.processors.interfaces.IDatacenterDataDAO;
import com.cws.esolutions.security.access.control.impl.UserControlServiceImpl;
import com.cws.esolutions.security.access.control.impl.AdminControlServiceImpl;
import com.cws.esolutions.security.access.control.interfaces.IUserControlService;
import com.cws.esolutions.core.processors.exception.DatacenterManagementException;
import com.cws.esolutions.security.access.control.interfaces.IAdminControlService;
import com.cws.esolutions.security.audit.processors.impl.AuditProcessorImpl;
import com.cws.esolutions.security.audit.processors.interfaces.IAuditProcessor;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.processors.interfaces
 * IServerManagementProcessor.java
 *
 * $Id: $
 * $Author: $
 * $Date: $
 * $Revision: $
 * @author kmhuntly@gmail.com
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Oct 29, 2012 9:44:46 AM
 *     Created.
 */
public interface IDatacenterManagementProcessor
{
    static final IAuditProcessor auditor = new AuditProcessorImpl();
    static final IUserControlService userControl = new UserControlServiceImpl();
    static final IAdminControlService adminControl = new AdminControlServiceImpl();
    static final CoreServiceBean appBean = CoreServiceBean.getInstance();

    static final IDatacenterDataDAO datactrDAO = new DatacenterDataDAOImpl();
    static final ApplicationConfig appConfig = appBean.getConfigData().getAppConfig();

    static final String CNAME = IDatacenterManagementProcessor.class.getName();

    static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CNAME);
    static final Logger WARN_RECORDER = LoggerFactory.getLogger(Constants.WARN_LOGGER + CNAME);

    DatacenterManagementResponse addNewDatacenter(final DatacenterManagementRequest request) throws DatacenterManagementException;

    DatacenterManagementResponse listDatacenters(final DatacenterManagementRequest request) throws DatacenterManagementException;

    DatacenterManagementResponse getDatacenter(final DatacenterManagementRequest request) throws DatacenterManagementException;
}
