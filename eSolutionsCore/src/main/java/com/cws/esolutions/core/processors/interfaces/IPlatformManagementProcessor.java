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
 * File: IPlatformManagementProcessor.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.core.Constants;
import com.cws.esolutions.core.CoreServiceBean;
import com.cws.esolutions.core.config.xml.SSHConfig;
import com.cws.esolutions.core.config.xml.ScriptConfig;
import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.core.dao.impl.ServerDataDAOImpl;
import com.cws.esolutions.core.config.xml.ApplicationConfig;
import com.cws.esolutions.core.dao.impl.PlatformDataDAOImpl;
import com.cws.esolutions.core.dao.interfaces.IServerDataDAO;
import com.cws.esolutions.core.dao.impl.DatacenterDataDAOImpl;
import com.cws.esolutions.core.dao.interfaces.IPlatformDataDAO;
import com.cws.esolutions.core.dao.interfaces.IDatacenterDataDAO;
import com.cws.esolutions.security.processors.impl.AuditProcessorImpl;
import com.cws.esolutions.core.processors.dto.PlatformManagementRequest;
import com.cws.esolutions.core.processors.dto.PlatformManagementResponse;
import com.cws.esolutions.security.processors.interfaces.IAuditProcessor;
import com.cws.esolutions.security.services.impl.AccessControlServiceImpl;
import com.cws.esolutions.security.services.interfaces.IAccessControlService;
import com.cws.esolutions.core.processors.exception.PlatformManagementException;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 */
public interface IPlatformManagementProcessor
{
    static final IAuditProcessor auditor = new AuditProcessorImpl();
    static final IServerDataDAO serverDao = new ServerDataDAOImpl();
    static final CoreServiceBean appBean = CoreServiceBean.getInstance();
    static final IPlatformDataDAO platformDao = new PlatformDataDAOImpl();
    static final IDatacenterDataDAO datactrDAO = new DatacenterDataDAOImpl();
    static final String CNAME = IPlatformManagementProcessor.class.getName();
    static final SecurityServiceBean secBean = SecurityServiceBean.getInstance();
    static final IAccessControlService accessControl = new AccessControlServiceImpl();
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
