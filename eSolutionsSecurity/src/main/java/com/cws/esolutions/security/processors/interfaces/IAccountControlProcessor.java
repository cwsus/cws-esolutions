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
 * File: IAccountControlProcessor.java
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
import com.cws.esolutions.security.processors.dto.AccountControlRequest;
import com.cws.esolutions.security.dao.userauth.interfaces.Authenticator;
import com.cws.esolutions.security.processors.dto.AccountControlResponse;
import com.cws.esolutions.utility.services.impl.AccessControlServiceImpl;
import com.cws.esolutions.security.dao.usermgmt.factory.UserManagerFactory;
import com.cws.esolutions.utility.services.interfaces.IAccessControlService;
import com.cws.esolutions.security.dao.userauth.factory.AuthenticatorFactory;
import com.cws.esolutions.security.processors.exception.AccountControlException;
import com.cws.esolutions.utility.securityutils.processors.impl.AuditProcessorImpl;
import com.cws.esolutions.utility.securityutils.processors.interfaces.IAuditProcessor;
import com.cws.esolutions.security.dao.reference.impl.SQLUserSecurityInformationDAOImpl;
import com.cws.esolutions.security.dao.reference.interfaces.IUserSecurityInformationDAO;
/**
 * API allowing administrative action against user account data
 * housed within the authentication datastore.
 *
 * @author cws-khuntly
 * @version 1.0
 */
public interface IAccountControlProcessor
{
	static final String CNAME = IAccountControlProcessor.class.getName();
    static final SecurityServiceBean secBean = SecurityServiceBean.getInstance();
    static final SystemConfig sysConfig = secBean.getConfigData().getSystemConfig();
    static final IAuditProcessor auditor = (IAuditProcessor) new AuditProcessorImpl();
    static final SecurityConfig secConfig = secBean.getConfigData().getSecurityConfig();
    static final IAccessControlService accessControl = (IAccessControlService) new AccessControlServiceImpl();
    static final UserManager userManager = (UserManager) UserManagerFactory.getUserManager(secConfig.getUserManager());
    static final IUserSecurityInformationDAO userSec = (IUserSecurityInformationDAO) new SQLUserSecurityInformationDAOImpl();
    static final Authenticator authenticator = (Authenticator) AuthenticatorFactory.getAuthenticator(secConfig.getAuthManager());
    
    static final Logger ERROR_RECORDER = LogManager.getLogger(SecurityServiceConstants.ERROR_LOGGER + IAccountControlProcessor.CNAME);
    static final Logger DEBUGGER = LogManager.getLogger(SecurityServiceConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    /**
     * Allows an administrator to create a new user with the provided account information.
     *
     * @param request - The {@link com.cws.esolutions.security.processors.dto.AccountControlRequest}
     * which contains the necessary information to complete the request
     * @return {@link com.cws.esolutions.security.processors.dto.AccountControlResponse} containing
     * response information regarding the request status
     * @throws AccountControlException {@link com.cws.esolutions.security.processors.exception.AccountControlException} if an exception occurs during processing
     */
    AccountControlResponse createNewUser(final AccountControlRequest request) throws AccountControlException;

    /**
     * Returns detailed information regarding a provided user account for review, modification or removal.
     *
     * @param request - The {@link com.cws.esolutions.security.processors.dto.AccountControlRequest}
     * which contains the necessary information to complete the request
     * @return {@link com.cws.esolutions.security.processors.dto.AccountControlResponse} containing
     * response information regarding the request status
     * @throws AccountControlException {@link com.cws.esolutions.security.processors.exception.AccountControlException} if an exception occurs during processing
     */
    AccountControlResponse loadUserAccount(final AccountControlRequest request) throws AccountControlException;

    /**
     * Removes a specified user account from the authentication datastore.
     *
     * @param request - The {@link com.cws.esolutions.security.processors.dto.AccountControlRequest}
     * which contains the necessary information to complete the request
     * @return {@link com.cws.esolutions.security.processors.dto.AccountControlResponse} containing
     * response information regarding the request status
     * @throws AccountControlException {@link com.cws.esolutions.security.processors.exception.AccountControlException} if an exception occurs during processing
     */
    AccountControlResponse removeUserAccount(final AccountControlRequest request) throws AccountControlException;

    /**
     * Allows modification of a user's suspension flag indicator, to either suspend or unsuspend
     * the provided account.
     *
     * @param request - The {@link com.cws.esolutions.security.processors.dto.AccountControlRequest}
     * which contains the necessary information to complete the request
     * @return {@link com.cws.esolutions.security.processors.dto.AccountControlResponse} containing
     * response information regarding the request status
     * @throws AccountControlException {@link com.cws.esolutions.security.processors.exception.AccountControlException} if an exception occurs during processing
     */
    AccountControlResponse modifyUserSuspension(final AccountControlRequest request) throws AccountControlException;

    /**
     * Allows modification of the provided user account's role within the application, to either add
     * or remove access as provided by the selected role.
     *
     * @param request - The {@link com.cws.esolutions.security.processors.dto.AccountControlRequest}
     * which contains the necessary information to complete the request
     * @return {@link com.cws.esolutions.security.processors.dto.AccountControlResponse} containing
     * response information regarding the request status
     * @throws AccountControlException {@link com.cws.esolutions.security.processors.exception.AccountControlException} if an exception occurs during processing
     */
    AccountControlResponse modifyUserRole(final AccountControlRequest request) throws AccountControlException;

    /**
     * Allows modification of the provided user's password in the event it has been fully forgotten and
     * the Online Reset process cannot be used.
     *
     * @param request - The {@link com.cws.esolutions.security.processors.dto.AccountControlRequest}
     * which contains the necessary information to complete the request
     * @return {@link com.cws.esolutions.security.processors.dto.AccountControlResponse} containing
     * response information regarding the request status
     * @throws AccountControlException {@link com.cws.esolutions.security.processors.exception.AccountControlException} if an exception occurs during processing
     */
    AccountControlResponse modifyUserPassword(final AccountControlRequest request) throws AccountControlException;

    /**
     * Allows modification of a user's lockout status, to either lock or unlock access to the application
     * as necessary.
     *
     * @param request - The {@link com.cws.esolutions.security.processors.dto.AccountControlRequest}
     * which contains the necessary information to complete the request
     * @return {@link com.cws.esolutions.security.processors.dto.AccountControlResponse} containing
     * response information regarding the request status
     * @throws AccountControlException {@link com.cws.esolutions.security.processors.exception.AccountControlException} if an exception occurs during processing
     */
    AccountControlResponse modifyUserLockout(final AccountControlRequest request) throws AccountControlException;

    /**
     * Provides a list of user accounts housed within the authentication datastore.
     *
     * @param request - The {@link com.cws.esolutions.security.processors.dto.AccountControlRequest}
     * which contains the necessary information to complete the request
     * @return {@link com.cws.esolutions.security.processors.dto.AccountControlResponse} containing
     * response information regarding the request status
     * @throws AccountControlException {@link com.cws.esolutions.security.processors.exception.AccountControlException} if an exception occurs during processing
     */
    AccountControlResponse listUserAccounts(final AccountControlRequest request) throws AccountControlException;
}
