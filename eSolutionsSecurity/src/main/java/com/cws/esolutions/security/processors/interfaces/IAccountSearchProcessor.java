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
package com.cws.esolutions.security.processors.interfaces;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.processors.interfaces
 * File: IAccountChangeProcessor.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 * cws-khuntly           12/05/2008 13:36:09             Added method to process change requests
 */
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.config.xml.SystemConfig;
import com.cws.esolutions.security.SecurityServiceConstants;
import com.cws.esolutions.security.config.xml.SecurityConfig;
import com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager;
import com.cws.esolutions.security.processors.dto.AccountSearchRequest;
import com.cws.esolutions.security.processors.dto.AccountSearchResponse;
import com.cws.esolutions.security.dao.usermgmt.factory.UserManagerFactory;
import com.cws.esolutions.security.processors.exception.AccountChangeException;
import com.cws.esolutions.security.processors.exception.AccountSearchException;
import com.cws.esolutions.security.processors.impl.AuditProcessorImpl;
import com.cws.esolutions.security.services.impl.AccessControlServiceImpl;
import com.cws.esolutions.security.services.interfaces.IAccessControlService;
/**
 * API allowing processing of account change requests for individual user accounts for
 * select pieces of information. This does not allow full modification of the
 * entire user account, some modifications are restricted to system adminstrators.
 * Only the owning user account can modify information for itself - methods housed
 * within this class cannot be executed by the administration team.
 *
 * @author cws-khuntly
 * @version 1.0
 */
public interface IAccountSearchProcessor
{
	static final String CNAME = IAccountSearchProcessor.class.getName();
    static final SecurityServiceBean secBean = SecurityServiceBean.getInstance();
    static final SystemConfig sysConfig = secBean.getConfigData().getSystemConfig();
    static final IAuditProcessor auditor = (IAuditProcessor) new AuditProcessorImpl();
    static final SecurityConfig secConfig = secBean.getConfigData().getSecurityConfig();
    static final IAccessControlService accessControl = (IAccessControlService) new AccessControlServiceImpl();
    static final UserManager userManager = (UserManager) UserManagerFactory.getUserManager(secConfig.getUserManager());

    static final Logger ERROR_RECORDER = LogManager.getLogger(SecurityServiceConstants.ERROR_LOGGER + IAccountSearchProcessor.CNAME);
    static final Logger DEBUGGER = LogManager.getLogger(SecurityServiceConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    /**
     * Allows a provided user to change the email address associated with their account. When performed,
     * a confirmation email is sent to both the new AND the old email addresses to ensure that the request
     * was indeed performed by the user and that it is a valid change.
     *
     * @param request - The {@link com.cws.esolutions.security.processors.dto.AccountChangeRequest}
     * which contains the necessary information to complete the request
     * @return {@link com.cws.esolutions.security.processors.dto.AccountChangeResponse} containing
     * response information regarding the request status
     * @throws AccountChangeException {@link com.cws.esolutions.security.processors.exception.AccountChangeException} if an exception occurs during processing
     */
    AccountSearchResponse findUserAccount(final AccountSearchRequest request) throws AccountSearchException;

    /**
     * Allows a provided user to change the email address associated with their account. When performed,
     * a confirmation email is sent to both the new AND the old email addresses to ensure that the request
     * was indeed performed by the user and that it is a valid change.
     *
     * @param request - The {@link com.cws.esolutions.security.processors.dto.AccountChangeRequest}
     * which contains the necessary information to complete the request
     * @return {@link com.cws.esolutions.security.processors.dto.AccountChangeResponse} containing
     * response information regarding the request status
     * @throws AccountChangeException {@link com.cws.esolutions.security.processors.exception.AccountChangeException} if an exception occurs during processing
     */
    AccountSearchResponse searchAccounts(final AccountSearchRequest request) throws AccountSearchException;
}
