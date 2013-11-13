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
import com.cws.esolutions.security.SecurityConstants;
import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.config.SecurityConfig;
import com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager;
import com.cws.esolutions.security.processors.dto.AuthenticationRequest;
import com.cws.esolutions.security.dao.userauth.interfaces.Authenticator;
import com.cws.esolutions.security.processors.dto.AuthenticationResponse;
import com.cws.esolutions.security.dao.usermgmt.factory.UserManagerFactory;
import com.cws.esolutions.security.audit.processors.impl.AuditProcessorImpl;
import com.cws.esolutions.security.dao.userauth.factory.AuthenticatorFactory;
import com.cws.esolutions.security.access.control.impl.AdminControlServiceImpl;
import com.cws.esolutions.security.processors.impl.AccountControlProcessorImpl;
import com.cws.esolutions.security.dao.reference.impl.SecurityReferenceDAOImpl;
import com.cws.esolutions.security.audit.processors.interfaces.IAuditProcessor;
import com.cws.esolutions.security.processors.exception.AuthenticationException;
import com.cws.esolutions.security.dao.reference.interfaces.ISecurityReferenceDAO;
import com.cws.esolutions.security.access.control.interfaces.IAdminControlService;
import com.cws.esolutions.security.dao.reference.impl.UserSecurityInformationDAOImpl;
import com.cws.esolutions.security.dao.reference.interfaces.IUserSecurityInformationDAO;
/*
 * IAuthenticationProcessor
 * This interface allows for user authentication, and provides methods for
 * authenticating against an LDAP datastore or a SQL datastore. Two types
 * of authentication are available: standard username/password combination,
 * and two-factor authentication, such as username/password and a one-time
 * password or security image, depending on what is configured. If two-factor
 * authentication is utilized, a second client utility is required, and
 * configuration therein.
 *
 * $Id: IAuthenticationProcessor.java 2276 2013-01-03 16:32:52Z kmhuntly@gmail.com $
 * $Author: $
 * $Date: $
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * Kevin Huntly         11/23/2008 22:39:20             Created.
 * Kevin Huntly         12/05/2008 13:36:09             Added method to process change requests
 */
public interface IAuthenticationProcessor
{
    static final IAuditProcessor auditor = new AuditProcessorImpl();
    static final ISecurityReferenceDAO secRef = new SecurityReferenceDAOImpl();
    static final SecurityServiceBean svcBean = SecurityServiceBean.getInstance();
    static final IAdminControlService adminControl = new AdminControlServiceImpl();
    static final IUserSecurityInformationDAO userSec = new UserSecurityInformationDAOImpl();
    static final UserManager userManager = UserManagerFactory.getUserManager(svcBean.getConfigData().getSecurityConfig().getUserManager());

    static final String CNAME = IAuthenticationProcessor.class.getName();
    static final AuthData authData = svcBean.getConfigData().getAuthData();
    static final AuthRepo authRepo = svcBean.getConfigData().getAuthRepo();
    static final SecurityConfig secConfig = svcBean.getConfigData().getSecurityConfig();
    static final IAccountControlProcessor controlProcessor = new AccountControlProcessorImpl();
    static final Authenticator authenticator = AuthenticatorFactory.getAuthenticator(secConfig.getAuthManager());

    static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityConstants.ERROR_LOGGER + CNAME);
    static final Logger WARN_RECORDER = LoggerFactory.getLogger(SecurityConstants.WARN_LOGGER + CNAME);

    /**
     * Performs agent authentication and validation for access to application.
     * Calls AgentLogonDAO to perform database calls to verify authentication
     * and authorization to utilize service. Sets up session and entitlements
     * for application access. This method will be utilized if configured
     * for an LDAP user datastore.
     *
     * processAgentLogon
     * @throws AuthorizationException
     * @return LoginResponse
     */
    AuthenticationResponse processAgentLogon(final AuthenticationRequest request) throws AuthenticationException;

    AuthenticationResponse obtainUserSecurityConfig(final AuthenticationRequest request) throws AuthenticationException;

    AuthenticationResponse verifyUserSecurityConfig(final AuthenticationRequest request) throws AuthenticationException;
}
