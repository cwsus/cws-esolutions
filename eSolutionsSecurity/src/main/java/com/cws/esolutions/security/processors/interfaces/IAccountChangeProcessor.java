/*
 * Copyright (c) 2009 - 2014 CaspersBox Web Services
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
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 * Kevin Huntly         12/05/2008 13:36:09             Added method to process change requests
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.config.xml.AuthData;
import com.cws.esolutions.security.config.xml.KeyConfig;
import com.cws.esolutions.security.SecurityServiceConstants;
import com.cws.esolutions.security.config.xml.SecurityConfig;
import com.cws.esolutions.security.processors.impl.AuditProcessorImpl;
import com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager;
import com.cws.esolutions.security.processors.dto.AccountChangeRequest;
import com.cws.esolutions.security.processors.dto.AccountChangeResponse;
import com.cws.esolutions.security.dao.userauth.interfaces.Authenticator;
import com.cws.esolutions.security.processors.interfaces.IAuditProcessor;
import com.cws.esolutions.security.dao.usermgmt.factory.UserManagerFactory;
import com.cws.esolutions.security.dao.userauth.factory.AuthenticatorFactory;
import com.cws.esolutions.security.processors.exception.AccountChangeException;
import com.cws.esolutions.security.dao.reference.impl.SecurityReferenceDAOImpl;
import com.cws.esolutions.security.dao.reference.interfaces.ISecurityReferenceDAO;
import com.cws.esolutions.security.dao.reference.impl.UserServiceInformationDAOImpl;
import com.cws.esolutions.security.dao.reference.impl.UserSecurityInformationDAOImpl;
import com.cws.esolutions.security.dao.reference.interfaces.IUserServiceInformationDAO;
import com.cws.esolutions.security.dao.reference.interfaces.IUserSecurityInformationDAO;
/**
 * Allows processing of account change requests for individual user accounts for
 * select pieces of information. This does not allow full modification of the
 * entire user account, some modifications are restricted to system adminstrators.
 * Only the owning user account can modify information for itself - methods housed
 * within this class cannot be executed by the administration team.
 *
 * @author khuntly
 * @version 1.0
 */
public interface IAccountChangeProcessor
{
    static final String CNAME = IAccountChangeProcessor.class.getName();
    static final SecurityServiceBean secBean = SecurityServiceBean.getInstance();
    static final String KEY_URI_FORMAT = "otpauth://totp/%s?secret=%s&issuer=%s&algorithm=%s"; // https://code.google.com/p/google-authenticator/wiki/KeyUriFormat

    static final IAuditProcessor auditor = new AuditProcessorImpl();
    static final AuthData authData = secBean.getConfigData().getAuthData();
    static final KeyConfig keyConfig = secBean.getConfigData().getKeyConfig();
    static final ISecurityReferenceDAO secRef = new SecurityReferenceDAOImpl();
    static final SecurityConfig secConfig = secBean.getConfigData().getSecurityConfig();
    static final IUserServiceInformationDAO userSvcs = new UserServiceInformationDAOImpl();
    static final IUserSecurityInformationDAO userSec = new UserSecurityInformationDAOImpl();
    static final UserManager userManager = UserManagerFactory.getUserManager(secConfig.getUserManager());
    static final Authenticator authenticator = AuthenticatorFactory.getAuthenticator(secConfig.getAuthManager());
    
    static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityServiceConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityServiceConstants.ERROR_LOGGER + CNAME);

    /**
     * 
     * TODO: Add in the method description/comments
     *
     * @param request
     * @return
     * @throws AccountChangeException
     */
    AccountChangeResponse enableOtpAuth(final AccountChangeRequest request) throws AccountChangeException;

    AccountChangeResponse disableOtpAuth(final AccountChangeRequest request) throws AccountChangeException;

    /**
     * 
     * TODO: Add in the method description/comments
     *
     * @param request
     * @return
     * @throws AccountChangeException
     */
    AccountChangeResponse changeUserEmail(final AccountChangeRequest request) throws AccountChangeException;

    /**
     * 
     * TODO: Add in the method description/comments
     *
     * @param request
     * @return
     * @throws AccountChangeException
     */
    AccountChangeResponse changeUserPassword(final AccountChangeRequest request) throws AccountChangeException;

    /**
     * 
     * TODO: Add in the method description/comments
     *
     * @param request
     * @return
     * @throws AccountChangeException
     */
    AccountChangeResponse changeUserSecurity(final AccountChangeRequest request) throws AccountChangeException;

    /**
     * 
     * TODO: Add in the method description/comments
     *
     * @param request
     * @return
     * @throws AccountChangeException
     */
    AccountChangeResponse changeUserContact(final AccountChangeRequest request) throws AccountChangeException;

    /**
     * 
     * TODO: Add in the method description/comments
     *
     * @param request
     * @return
     * @throws AccountChangeException
     */
    AccountChangeResponse changeUserKeys(final AccountChangeRequest request) throws AccountChangeException;
}
