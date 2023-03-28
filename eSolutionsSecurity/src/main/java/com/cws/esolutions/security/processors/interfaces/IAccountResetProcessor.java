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
 * File: IAccountResetProcessor.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.SecurityServiceConstants;
import com.cws.esolutions.security.config.xml.SecurityConfig;
import com.cws.esolutions.security.processors.impl.AuditProcessorImpl;
import com.cws.esolutions.security.processors.dto.AccountResetRequest;
import com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager;
import com.cws.esolutions.security.processors.dto.AccountResetResponse;
import com.cws.esolutions.security.dao.userauth.interfaces.Authenticator;
import com.cws.esolutions.security.services.impl.AccessControlServiceImpl;
import com.cws.esolutions.security.dao.usermgmt.factory.UserManagerFactory;
import com.cws.esolutions.security.services.interfaces.IAccessControlService;
import com.cws.esolutions.security.dao.userauth.factory.AuthenticatorFactory;
import com.cws.esolutions.security.processors.exception.AccountResetException;
import com.cws.esolutions.security.dao.reference.impl.UserSecurityInformationDAOImpl;
import com.cws.esolutions.security.dao.reference.interfaces.IUserSecurityInformationDAO;
/**
 * API allowing account reset processing, if enabled.
 *
 * @author cws-khuntly
 * @version 1.0
 */
public interface IAccountResetProcessor
{
	static final String CNAME = IAccountResetProcessor.class.getName();
    static final IAuditProcessor auditor = (IAuditProcessor) new AuditProcessorImpl();
    static final SecurityServiceBean secBean = SecurityServiceBean.getInstance();
    static final SecurityConfig secConfig = secBean.getConfigData().getSecurityConfig();
    static final IAccessControlService accessControl = (IAccessControlService) new AccessControlServiceImpl();
    static final IUserSecurityInformationDAO userSec = (IUserSecurityInformationDAO) new UserSecurityInformationDAOImpl();
    static final Authenticator authenticator = (Authenticator) AuthenticatorFactory.getAuthenticator(secConfig.getAuthManager());
    static final UserManager userManager = (UserManager) UserManagerFactory.getUserManager(secBean.getConfigData().getSecurityConfig().getUserManager());

    static final Logger ERROR_RECORDER = LogManager.getLogger(SecurityServiceConstants.ERROR_LOGGER + IAccountResetProcessor.CNAME);
    static final Logger DEBUGGER = LogManager.getLogger(SecurityServiceConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    /**
     * Obtains and returns the associated security information for the selected user account
     *
     * @param request - The {@link com.cws.esolutions.security.processors.dto.AccountResetRequest}
     * which contains the necessary information to complete the request
     * @return {@link com.cws.esolutions.security.processors.dto.AccountResetResponse} containing
     * response information regarding the request status
     * @throws AccountResetException {@link com.cws.esolutions.security.processors.exception.AccountResetException} if an exception occurs during processing
     */
    AccountResetResponse obtainUserSecurityConfig(final AccountResetRequest request) throws AccountResetException;

    /**
     * Verifies that the provided security information is accurate prior to performing a requested
     * password reset. This is performed during reset authentication.
     *
     * @param request - The {@link com.cws.esolutions.security.processors.dto.AccountResetRequest}
     * which contains the necessary information to complete the request
     * @return {@link com.cws.esolutions.security.processors.dto.AccountResetResponse} containing
     * response information regarding the request status
     * @throws AccountResetException {@link com.cws.esolutions.security.processors.exception.AccountResetException} if an exception occurs during processing
     */
    AccountResetResponse verifyUserSecurityConfig(final AccountResetRequest request) throws AccountResetException;

    /**
     * Verifies that the provided security information is accurate prior to performing a requested
     * password reset. This is performed upon submission of a reset request and the notification
     * email has been sent with the unique identifier for continuation.
     *
     * @param request - The {@link com.cws.esolutions.security.processors.dto.AccountResetRequest}
     * which contains the necessary information to complete the request
     * @return {@link com.cws.esolutions.security.processors.dto.AccountResetResponse} containing
     * response information regarding the request status
     * @throws AccountResetException {@link com.cws.esolutions.security.processors.exception.AccountResetException} if an exception occurs during processing
     */
    AccountResetResponse verifyResetRequest(final AccountResetRequest request) throws AccountResetException;

    /**
     * Resets a user account password upon successful completion of the Online Reset process.
     *
     * @param request - The {@link com.cws.esolutions.security.processors.dto.AccountResetRequest}
     * which contains the necessary information to complete the request
     * @return {@link com.cws.esolutions.security.processors.dto.AccountResetResponse} containing
     * response information regarding the request status
     * @throws AccountResetException {@link com.cws.esolutions.security.processors.exception.AccountResetException} if an exception occurs during processing
     */
    AccountResetResponse insertResetRequest(final AccountResetRequest request) throws AccountResetException;
}
