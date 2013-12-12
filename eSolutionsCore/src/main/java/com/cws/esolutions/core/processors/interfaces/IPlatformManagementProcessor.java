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

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.core.Constants;
import com.cws.esolutions.core.CoreServiceBean;
import com.cws.esolutions.core.config.SSHConfig;
import com.cws.esolutions.core.config.ScriptConfig;
import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.core.config.ApplicationConfig;
import com.cws.esolutions.core.dao.processors.impl.ServerDataDAOImpl;
import com.cws.esolutions.core.dao.processors.impl.PlatformDataDAOImpl;
import com.cws.esolutions.core.processors.dto.PlatformManagementRequest;
import com.cws.esolutions.core.dao.processors.interfaces.IServerDataDAO;
import com.cws.esolutions.core.processors.dto.PlatformManagementResponse;
import com.cws.esolutions.core.dao.processors.impl.DatacenterDataDAOImpl;
import com.cws.esolutions.core.dao.processors.interfaces.IPlatformDataDAO;
import com.cws.esolutions.core.dao.processors.interfaces.IDatacenterDataDAO;
import com.cws.esolutions.security.audit.processors.impl.AuditProcessorImpl;
import com.cws.esolutions.security.access.control.impl.UserControlServiceImpl;
import com.cws.esolutions.security.access.control.impl.AdminControlServiceImpl;
import com.cws.esolutions.security.audit.processors.interfaces.IAuditProcessor;
import com.cws.esolutions.core.processors.exception.PlatformManagementException;
import com.cws.esolutions.security.access.control.interfaces.IUserControlService;
import com.cws.esolutions.security.access.control.interfaces.IAdminControlService;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.security.audit.processors.interfaces
 * File: IAuditProcessor.java
 *
 * History
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * Kevin Huntly         11/23/2008 22:39:20             Created.
 * Kevin Huntly         12/05/2008 13:36:09             Added method to process change requests
 */
public interface IPlatformManagementProcessor
{
    static final IAuditProcessor auditor = new AuditProcessorImpl();
    static final IServerDataDAO serverDao = new ServerDataDAOImpl();
    static final CoreServiceBean appBean = CoreServiceBean.getInstance();
    static final IPlatformDataDAO platformDao = new PlatformDataDAOImpl();
    static final IDatacenterDataDAO datactrDAO = new DatacenterDataDAOImpl();
    static final String CNAME = IPlatformManagementProcessor.class.getName();
    static final IUserControlService userControl = new UserControlServiceImpl();
    static final SecurityServiceBean secBean = SecurityServiceBean.getInstance();
    static final IAdminControlService adminControl = new AdminControlServiceImpl();
    static final List<String> serviceAccount = secBean.getConfigData().getSecurityConfig().getServiceAccount();

    static final SSHConfig sshConfig = appBean.getConfigData().getSshConfig();
    static final ApplicationConfig appConfig = appBean.getConfigData().getAppConfig();
    static final ScriptConfig scriptConfig = appBean.getConfigData().getScriptConfig();

    static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CNAME);
    static final Logger WARN_RECORDER = LoggerFactory.getLogger(Constants.WARN_LOGGER + CNAME);

    PlatformManagementResponse addNewPlatform(final PlatformManagementRequest request) throws PlatformManagementException;

    PlatformManagementResponse updatePlatformData(final PlatformManagementRequest request) throws PlatformManagementException;

    PlatformManagementResponse listPlatforms(final PlatformManagementRequest request) throws PlatformManagementException;

    PlatformManagementResponse listPlatformsByAttribute(final PlatformManagementRequest request) throws PlatformManagementException;

    PlatformManagementResponse getPlatformData(final PlatformManagementRequest request) throws PlatformManagementException;
}
