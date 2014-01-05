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
package com.cws.esolutions.security.processors.interfaces;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.security.processors.interfaces
 * File: IAccountChangeProcessor.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 * Kevin Huntly         12/05/2008 13:36:09             Added method to process change requests
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.config.xml.AuthData;
import com.cws.esolutions.security.config.xml.AuthRepo;
import com.cws.esolutions.security.config.xml.KeyConfig;
import com.cws.esolutions.security.SecurityServiceConstants;
import com.cws.esolutions.security.config.xml.SecurityConfig;
import com.cws.esolutions.security.dao.keymgmt.interfaces.KeyManager;
import com.cws.esolutions.security.processors.impl.AuditProcessorImpl;
import com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager;
import com.cws.esolutions.security.processors.dto.AccountChangeRequest;
import com.cws.esolutions.security.processors.dto.AccountChangeResponse;
import com.cws.esolutions.security.dao.userauth.interfaces.Authenticator;
import com.cws.esolutions.security.processors.interfaces.IAuditProcessor;
import com.cws.esolutions.security.dao.usermgmt.factory.UserManagerFactory;
import com.cws.esolutions.security.dao.keymgmt.factory.KeyManagementFactory;
import com.cws.esolutions.security.dao.userauth.factory.AuthenticatorFactory;
import com.cws.esolutions.security.processors.exception.AccountChangeException;
import com.cws.esolutions.security.dao.reference.impl.SecurityReferenceDAOImpl;
import com.cws.esolutions.security.dao.reference.interfaces.ISecurityReferenceDAO;
import com.cws.esolutions.security.dao.reference.impl.UserServiceInformationDAOImpl;
import com.cws.esolutions.security.dao.reference.impl.UserSecurityInformationDAOImpl;
import com.cws.esolutions.security.dao.reference.interfaces.IUserServiceInformationDAO;
import com.cws.esolutions.security.dao.reference.interfaces.IUserSecurityInformationDAO;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 */
public interface IAccountChangeProcessor
{
    static final String CNAME = IAccountChangeProcessor.class.getName();
    static final SecurityServiceBean secBean = SecurityServiceBean.getInstance();

    static final IAuditProcessor auditor = new AuditProcessorImpl();
    static final AuthRepo authRepo = secBean.getConfigData().getAuthRepo();
    static final AuthData authData = secBean.getConfigData().getAuthData();
    static final KeyConfig keyConfig = secBean.getConfigData().getKeyConfig();
    static final ISecurityReferenceDAO secRef = new SecurityReferenceDAOImpl();
    static final SecurityConfig secConfig = secBean.getConfigData().getSecurityConfig();
    static final IUserServiceInformationDAO userSvcs = new UserServiceInformationDAOImpl();
    static final IUserSecurityInformationDAO userSec = new UserSecurityInformationDAOImpl();
    static final KeyManager keyManager = KeyManagementFactory.getKeyManager(keyConfig.getKeyManager());
    static final UserManager userManager = UserManagerFactory.getUserManager(secConfig.getUserManager());
    static final Authenticator authenticator = AuthenticatorFactory.getAuthenticator(secConfig.getAuthManager());
    
    static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityServiceConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger WARN_RECORDER = LoggerFactory.getLogger(SecurityServiceConstants.WARN_LOGGER + CNAME);
    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityServiceConstants.ERROR_LOGGER + CNAME);

    AccountChangeResponse changeUserEmail(final AccountChangeRequest request) throws AccountChangeException;

    AccountChangeResponse changeUserPassword(final AccountChangeRequest request) throws AccountChangeException;

    AccountChangeResponse changeUserSecurity(final AccountChangeRequest request) throws AccountChangeException;

    AccountChangeResponse changeUserKeys(final AccountChangeRequest request) throws AccountChangeException;

    AccountChangeResponse changeUserContact(final AccountChangeRequest request) throws AccountChangeException;
}
