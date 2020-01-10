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
 * File: IAuthenticationProcessor.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 * cws-khuntly          12/05/2008 13:36:09             Added method to process change requests
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.SecurityServiceConstants;
import com.cws.esolutions.security.config.xml.SecurityConfig;
import com.cws.esolutions.security.processors.impl.AuditProcessorImpl;
import com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager;
import com.cws.esolutions.security.processors.dto.AuthenticationRequest;
import com.cws.esolutions.security.dao.userauth.interfaces.Authenticator;
import com.cws.esolutions.security.processors.dto.AuthenticationResponse;
import com.cws.esolutions.security.processors.interfaces.IAuditProcessor;
import com.cws.esolutions.security.services.impl.AccessControlServiceImpl;
import com.cws.esolutions.security.dao.usermgmt.factory.UserManagerFactory;
import com.cws.esolutions.security.dao.userauth.factory.AuthenticatorFactory;
import com.cws.esolutions.security.services.interfaces.IAccessControlService;
import com.cws.esolutions.security.processors.impl.AccountControlProcessorImpl;
import com.cws.esolutions.security.dao.reference.impl.SecurityReferenceDAOImpl;
import com.cws.esolutions.security.processors.exception.AuthenticationException;
import com.cws.esolutions.security.dao.reference.interfaces.ISecurityReferenceDAO;
import com.cws.esolutions.security.dao.reference.impl.UserSecurityInformationDAOImpl;
import com.cws.esolutions.security.dao.reference.interfaces.IUserSecurityInformationDAO;
/**
 * API allowing user authentication request processing.
 *
 * @author cws-khuntly
 * @version 1.0
 */
public interface IAuthenticationProcessor
{
    static final String ATTRIBUTE_UID = "uid";
    static final String ATTRIBUTE_GUID = "cn";
    static final IAuditProcessor auditor = new AuditProcessorImpl();
    static final ISecurityReferenceDAO secRef = new SecurityReferenceDAOImpl();
    static final SecurityServiceBean secBean = SecurityServiceBean.getInstance();
    static final IAccessControlService accessControl = new AccessControlServiceImpl();
    static final SecurityConfig secConfig = secBean.getConfigData().getSecurityConfig();
    static final IAccountControlProcessor controlProcessor = new AccountControlProcessorImpl();
    static final IUserSecurityInformationDAO userSec = new UserSecurityInformationDAOImpl();
    static final Authenticator authenticator = AuthenticatorFactory.getAuthenticator(secConfig.getAuthManager());
    static final UserManager userManager = UserManagerFactory.getUserManager(secBean.getConfigData().getSecurityConfig().getUserManager());

    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityServiceConstants.ERROR_LOGGER);
    static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityServiceConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    /**
     * Performs agent authentication and validation for access to application.
     * Calls AgentLogonDAO to perform database calls to verify authentication
     * and authorization to utilize service. Sets up session and entitlements
     * for application access. This method will be utilized if configured
     * for an LDAP user datastore.
     *
     * @param request The {@link com.cws.esolutions.security.processors.dto.AuthenticationRequest}
     * containing the necessary authentication information to process the request.
     * @return {@link com.cws.esolutions.security.processors.dto.AuthenticationResponse}
     * containing the response for the provided request.
     * @throws AuthenticationException {@link com.cws.esolutions.security.processors.exception.AuthenticationException} if an exception occurs during processing
     */
    AuthenticationResponse processAgentLogon(final AuthenticationRequest request) throws AuthenticationException;

    /**
     * Performs agent authentication and validation for access to application.
     * Calls AgentLogonDAO to perform database calls to verify authentication
     * and authorization to utilize service. Sets up session and entitlements
     * for application access. This method will be utilized if configured
     * for an LDAP user datastore. This method provides OTP authentication processing
     * on top of the normal username/password process.
     *
     * @param request The {@link com.cws.esolutions.security.processors.dto.AuthenticationRequest}
     * containing the necessary authentication information to process the request.
     * @return {@link com.cws.esolutions.security.processors.dto.AuthenticationResponse}
     * containing the response for the provided request.
     * @throws AuthenticationException {@link com.cws.esolutions.security.processors.exception.AuthenticationException} if an exception occurs during processing
     */
    AuthenticationResponse processOtpLogon(final AuthenticationRequest request) throws AuthenticationException;
}
