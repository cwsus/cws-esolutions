/**
 * Copyright (c) 2009 - 2013 By: CWS, Inc.
 * 
 * All rights reserved. These materials are confidential and
 * proprietary to CaspersBox Web Services N.A and no part of
 * these materials should be reproduced, published in any form
 * by any means, electronic or mechanical, including photocopy
 * or any information storage or retrieval system not should
 * the materials be disclosed to third parties without the
 * express written authorization of CaspersBox Web Services, N.A.
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
import com.cws.esolutions.security.processors.dto.AccountChangeRequest;
import com.cws.esolutions.security.keymgmt.factory.KeyManagementFactory;
import com.cws.esolutions.security.processors.dto.AccountChangeResponse;
import com.cws.esolutions.security.dao.userauth.interfaces.Authenticator;
import com.cws.esolutions.security.dao.usermgmt.factory.UserManagerFactory;
import com.cws.esolutions.security.audit.processors.impl.AuditProcessorImpl;
import com.cws.esolutions.security.dao.userauth.factory.AuthenticatorFactory;
import com.cws.esolutions.security.access.control.impl.UserControlServiceImpl;
import com.cws.esolutions.security.processors.exception.AccountChangeException;
import com.cws.esolutions.security.access.control.impl.AdminControlServiceImpl;
import com.cws.esolutions.security.audit.processors.interfaces.IAuditProcessor;
import com.cws.esolutions.security.dao.reference.impl.SecurityReferenceDAOImpl;
import com.cws.esolutions.security.access.control.interfaces.IUserControlService;
import com.cws.esolutions.security.access.control.interfaces.IAdminControlService;
import com.cws.esolutions.security.dao.reference.interfaces.ISecurityReferenceDAO;
import com.cws.esolutions.security.dao.reference.impl.UserServiceInformationDAOImpl;
import com.cws.esolutions.security.dao.reference.impl.UserSecurityInformationDAOImpl;
import com.cws.esolutions.security.dao.reference.interfaces.IUserServiceInformationDAO;
import com.cws.esolutions.security.dao.reference.interfaces.IUserSecurityInformationDAO;
/**
 * eSolutionsCore
 * com.cws.esolutions.security.processors.interfaces
 * IAccountChangeProcessor.java
 *
 * $Id: $
 * $Author: $
 * $Date: $
 * $Revision: $
 * @author khuntly
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * khuntly @ Nov 14, 2013 12:15:35 PM
 *     Created.
 */
public interface IAccountChangeProcessor
{
    static final String CNAME = IAccountChangeProcessor.class.getName();
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

    AccountChangeResponse createSecurityData(final AccountChangeRequest request) throws AccountChangeException;

    AccountChangeResponse changeUserEmail(final AccountChangeRequest request) throws AccountChangeException;

    AccountChangeResponse changeUserPassword(final AccountChangeRequest request) throws AccountChangeException;

    AccountChangeResponse changeUserSecurity(final AccountChangeRequest request) throws AccountChangeException;

    AccountChangeResponse changeUserKeys(final AccountChangeRequest request) throws AccountChangeException;

    AccountChangeResponse changeUserContact(final AccountChangeRequest request) throws AccountChangeException;
}
