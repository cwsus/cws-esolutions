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
import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.core.config.xml.ApplicationConfig;
import com.cws.esolutions.core.processors.dto.MessagingRequest;
import com.cws.esolutions.core.processors.dto.MessagingResponse;
import com.cws.esolutions.security.audit.processors.impl.AuditProcessorImpl;
import com.cws.esolutions.core.processors.exception.MessagingServiceException;
import com.cws.esolutions.security.access.control.impl.UserControlServiceImpl;
import com.cws.esolutions.security.audit.processors.interfaces.IAuditProcessor;
import com.cws.esolutions.security.access.control.impl.AdminControlServiceImpl;
import com.cws.esolutions.security.access.control.interfaces.IUserControlService;
import com.cws.esolutions.security.access.control.interfaces.IAdminControlService;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.dao.interfaces
 * File: IPackageDataDAO.java
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Jan 4, 2013 3:36:54 PM
 *     Created.
 */
public interface IMessagingProcessor
{
    static final IAuditProcessor auditor = new AuditProcessorImpl();
    static final IUserControlService userControl = new UserControlServiceImpl();
    static final IAdminControlService adminControl = new AdminControlServiceImpl();
    static final CoreServiceBean appBean = CoreServiceBean.getInstance();
    static final SecurityServiceBean secBean = SecurityServiceBean.getInstance();
    static final ApplicationConfig appConfig = appBean.getConfigData().getAppConfig();
    static final List<String> serviceAccount = secBean.getConfigData().getSecurityConfig().getServiceAccount();

    static final String dateFormat = appConfig.getDateFormat();
    static final String CNAME = IMessagingProcessor.class.getName();

    static final Logger WARN_RECORDER = LoggerFactory.getLogger(Constants.WARN_LOGGER + CNAME);
    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CNAME);
    static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    MessagingResponse addNewMessage(final MessagingRequest request) throws MessagingServiceException;

    MessagingResponse updateExistingMessage(final MessagingRequest request) throws MessagingServiceException;

    MessagingResponse showAlertMessages(final MessagingRequest request) throws MessagingServiceException;

    MessagingResponse showMessages(final MessagingRequest request) throws MessagingServiceException;

    MessagingResponse showMessage(final MessagingRequest request) throws MessagingServiceException;
}
