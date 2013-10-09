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
package com.cws.esolutions.security.processors.interfaces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.security.config.AuthData;
import com.cws.esolutions.security.config.AuthRepo;
import com.cws.esolutions.security.config.KeyConfig;
import com.cws.esolutions.security.SecurityConstants;
import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.config.SecurityConfig;
import com.cws.esolutions.security.keymgmt.interfaces.KeyManager;
import com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager;
import com.cws.esolutions.security.keymgmt.factory.KeyManagementFactory;
import com.cws.esolutions.security.processors.dto.AccountControlRequest;
import com.cws.esolutions.security.processors.dto.AccountControlResponse;
import com.cws.esolutions.security.dao.userauth.interfaces.Authenticator;
import com.cws.esolutions.security.dao.usermgmt.factory.UserManagerFactory;
import com.cws.esolutions.security.audit.processors.impl.AuditProcessorImpl;
import com.cws.esolutions.security.dao.userauth.factory.AuthenticatorFactory;
import com.cws.esolutions.security.access.control.impl.UserControlServiceImpl;
import com.cws.esolutions.security.access.control.impl.AdminControlServiceImpl;
import com.cws.esolutions.security.dao.reference.impl.SecurityReferenceDAOImpl;
import com.cws.esolutions.security.audit.processors.interfaces.IAuditProcessor;
import com.cws.esolutions.security.processors.exception.AccountControlException;
import com.cws.esolutions.security.access.control.interfaces.IUserControlService;
import com.cws.esolutions.security.access.control.interfaces.IAdminControlService;
import com.cws.esolutions.security.dao.reference.interfaces.ISecurityReferenceDAO;
import com.cws.esolutions.security.dao.reference.impl.UserServiceInformationDAOImpl;
import com.cws.esolutions.security.dao.reference.impl.UserSecurityInformationDAOImpl;
import com.cws.esolutions.security.dao.reference.interfaces.IUserServiceInformationDAO;
import com.cws.esolutions.security.dao.reference.interfaces.IUserSecurityInformationDAO;
/**
 * SecurityService
 * com.cws.esolutions.security.processors.interfaces
 * IAccountControlProcessor.java
 *
 * $Id: IAccountControlProcessor.java 2287 2013-01-03 20:52:22Z kmhuntly@gmail.com $
 * $Author: $
 * $Date: 2013-01-03 15:52:22 -0500 (Thu, 03 Jan 2013) $
 * $Revision: 2287 $
 * @author kmhuntly@gmail.com
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * khuntly @ Nov 23, 2008 22:39:20
 *     Created.
 */
public interface IAccountControlProcessor
{
    static final String CNAME = IAccountControlProcessor.class.getName();
    static final SecurityServiceBean svcBean = SecurityServiceBean.getInstance();

    static final IAuditProcessor auditor = new AuditProcessorImpl();
    static final AuthRepo authRepo = svcBean.getConfigData().getAuthRepo();
    static final AuthData authData = svcBean.getConfigData().getAuthData();
    static final KeyConfig keyConfig = svcBean.getConfigData().getKeyConfig();
    static final ISecurityReferenceDAO secRef = new SecurityReferenceDAOImpl();
    static final IUserControlService userControl = new UserControlServiceImpl();
    static final IAdminControlService adminControl = new AdminControlServiceImpl();
    static final SecurityConfig secConfig = svcBean.getConfigData().getSecurityConfig();
    static final IUserServiceInformationDAO userSvcs = new UserServiceInformationDAOImpl();
    static final IUserSecurityInformationDAO userSec = new UserSecurityInformationDAOImpl();
    static final KeyManager keyManager = KeyManagementFactory.getKeyManager(keyConfig.getKeyManager());
    static final UserManager userManager = UserManagerFactory.getUserManager(secConfig.getUserManager());
    static final Authenticator authenticator = AuthenticatorFactory.getAuthenticator(secConfig.getAuthManager());
    
    static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger WARN_RECORDER = LoggerFactory.getLogger(SecurityConstants.WARN_LOGGER + CNAME);
    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityConstants.ERROR_LOGGER + CNAME);

    /**
     * Creates a new user account based on the information provided. Inserts
     * new user into authorization database and sends email to user notifying
     * of account info. This method is utilized with an LDAP user datastore.
     *
     * @param Map<String, String>
     * @param DirContext
     * @param List<String>
     * @throws NamingException
     * @throws InvalidAgentException
     */
    AccountControlResponse createNewUser(final AccountControlRequest request) throws AccountControlException;

    AccountControlResponse createSecurityData(final AccountControlRequest request) throws AccountControlException;

    /**
     * Modify a provided user account, capable of modifying most attributes.
     *
     * @param request - The user account to modify, as well as the modified data
     * @return <code>AccountControlResponse</code> containing the result data
     * @throws AccountControlException if an exception occurs during account modification
     */
    // AccountControlResponse modifyAccount(final AccountControlRequest request) throws AccountControlException;

    AccountControlResponse searchAccounts(final AccountControlRequest request) throws AccountControlException;

    AccountControlResponse loadUserAccount(final AccountControlRequest request) throws AccountControlException;

    AccountControlResponse changeUserEmail(final AccountControlRequest request) throws AccountControlException;

    AccountControlResponse changeUserPassword(final AccountControlRequest request) throws AccountControlException;

    AccountControlResponse changeUserSecurity(final AccountControlRequest request) throws AccountControlException;

    AccountControlResponse removeUserAccount(final AccountControlRequest request) throws AccountControlException;

    AccountControlResponse modifyUserSuspension(final AccountControlRequest request) throws AccountControlException;
}
