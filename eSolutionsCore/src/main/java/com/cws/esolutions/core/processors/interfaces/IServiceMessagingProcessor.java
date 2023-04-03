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
 * File: IMessagingProcessor.java
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
import com.cws.esolutions.core.config.xml.ServiceAccount;
import com.cws.esolutions.core.dao.impl.WebMessagingDAOImpl;
import com.cws.esolutions.core.config.xml.ApplicationConfig;
import com.cws.esolutions.core.dao.interfaces.IWebMessagingDAO;
import com.cws.esolutions.core.processors.dto.MessagingRequest;
import com.cws.esolutions.core.processors.dto.MessagingResponse;
import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.config.xml.SecurityConfig;
import com.cws.esolutions.security.processors.impl.AuditProcessorImpl;
import com.cws.esolutions.security.processors.interfaces.IAuditProcessor;
import com.cws.esolutions.security.services.impl.AccessControlServiceImpl;
import com.cws.esolutions.security.services.interfaces.IAccessControlService;
import com.cws.esolutions.core.processors.exception.MessagingServiceException;
/**
 * @author cws-khuntly
 * @version 1.0
 */
public interface IServiceMessagingProcessor
{
    static final CoreServicesBean appBean = CoreServicesBean.getInstance();
    static final ApplicationConfig appConfig = appBean.getConfigData().getAppConfig();
    static final ServiceAccount serviceAccount = appBean.getConfigData().getAppConfig().getServiceAccount().get(0);
    static final IWebMessagingDAO webMessengerDAO = (IWebMessagingDAO)new WebMessagingDAOImpl();
    static final IAuditProcessor auditor = (IAuditProcessor) new AuditProcessorImpl();
    static final IAccessControlService accessControl = (IAccessControlService) new AccessControlServiceImpl();
    static final SecurityServiceBean secBean = SecurityServiceBean.getInstance();
    static final SecurityConfig secConfig = secBean.getConfigData().getSecurityConfig();

    static final String dateFormat = appConfig.getDateFormat();
    static final String CNAME = IServiceMessagingProcessor.class.getName();

    static final Logger ERROR_RECORDER = LogManager.getLogger(CoreServicesConstants.ERROR_LOGGER + CNAME);
    static final Logger DEBUGGER = LogManager.getLogger(CoreServicesConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    MessagingResponse addNewMessage(final MessagingRequest request) throws MessagingServiceException;

    MessagingResponse updateExistingMessage(final MessagingRequest request) throws MessagingServiceException;

    MessagingResponse showAlertMessages(final MessagingRequest request) throws MessagingServiceException;

    MessagingResponse showMessages(final MessagingRequest request) throws MessagingServiceException;

    MessagingResponse showMessage(final MessagingRequest request) throws MessagingServiceException;
}
