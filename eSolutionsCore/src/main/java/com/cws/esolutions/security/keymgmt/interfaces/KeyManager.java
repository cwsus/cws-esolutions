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
package com.cws.esolutions.security.keymgmt.interfaces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.security.config.AuthRepo;
import com.cws.esolutions.security.config.AuthData;
import com.cws.esolutions.security.SecurityConstants;
import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.core.controllers.ResourceControllerBean;
import com.cws.esolutions.security.keymgmt.dto.KeyManagementRequest;
import com.cws.esolutions.security.keymgmt.dto.KeyManagementResponse;
import com.cws.esolutions.security.keymgmt.exception.KeyManagementException;
import com.cws.esolutions.security.dao.reference.impl.SecurityReferenceDAOImpl;
import com.cws.esolutions.security.dao.reference.interfaces.ISecurityReferenceDAO;
import com.cws.esolutions.security.dao.reference.impl.UserServiceInformationDAOImpl;
import com.cws.esolutions.security.dao.reference.interfaces.IUserServiceInformationDAO;
/**
 * SecurityService
 * com.cws.esolutions.security.dao.ldap
 * Authenticator.java
 *
 * $Id: $
 * $Author: $
 * $Date: $
 * $Revision: $
 * @author kmhuntly@gmail.com
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Oct 26, 2012 4:34:11 PM
 *     Created.
 */
public interface KeyManager
{
    static final ISecurityReferenceDAO secRef = new SecurityReferenceDAOImpl();
    static final SecurityServiceBean svcBean = SecurityServiceBean.getInstance();
    static final ResourceControllerBean resBean = svcBean.getResourceBean();
    static final AuthRepo authRepo = svcBean.getConfigData().getAuthRepo();
    static final AuthData authData = svcBean.getConfigData().getAuthData();

    static final IUserServiceInformationDAO userSvcs = new UserServiceInformationDAOImpl();
    
    static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityConstants.ERROR_LOGGER + KeyManager.class.getName());

    KeyManagementResponse returnKeys(final KeyManagementRequest request) throws KeyManagementException;

    KeyManagementResponse createKeys(final KeyManagementRequest request) throws KeyManagementException;

    KeyManagementResponse removeKeys(final KeyManagementRequest request) throws KeyManagementException;
}
