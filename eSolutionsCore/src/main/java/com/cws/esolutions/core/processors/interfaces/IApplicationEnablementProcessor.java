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
import com.cws.esolutions.core.config.xml.ApplicationConfig;
import com.cws.esolutions.core.dao.impl.ApplicationEnablementDAOImpl;
import com.cws.esolutions.core.dao.interfaces.IApplicationEnablementDAO;
import com.cws.esolutions.core.processors.dto.ApplicationEnablementRequest;
import com.cws.esolutions.core.processors.dto.ApplicationEnablementResponse;
import com.cws.esolutions.core.processors.exception.ApplicationEnablementException;
import com.cws.esolutions.utility.securityutils.processors.impl.AuditProcessorImpl;
import com.cws.esolutions.utility.securityutils.processors.interfaces.IAuditProcessor;
/**
 * API allowing application management functionality
 *
 * @author cws-khuntly
 * @version 1.0
 */
public interface IApplicationEnablementProcessor
{
    static final CoreServicesBean appBean = CoreServicesBean.getInstance();
    static final ApplicationConfig appConfig = appBean.getConfigData().getAppConfig();
    static final IAuditProcessor auditor = (IAuditProcessor) new AuditProcessorImpl();
    static final IApplicationEnablementDAO appDAO = (IApplicationEnablementDAO) new ApplicationEnablementDAOImpl();

    static final Logger DEBUGGER = LogManager.getLogger(CoreServicesConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger ERROR_RECORDER = LogManager.getLogger(CoreServicesConstants.ERROR_LOGGER);

    ApplicationEnablementResponse isServiceEnabled(final ApplicationEnablementRequest request) throws ApplicationEnablementException;
}