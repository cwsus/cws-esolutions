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
package com.cws.esolutions.security.dao.userauth.interfaces;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.security.SecurityConstants;
import com.cws.esolutions.security.config.xml.AuthData;
import com.cws.esolutions.security.config.xml.AuthRepo;
import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.core.controllers.ResourceControllerBean;
import com.cws.esolutions.security.dao.userauth.exception.AuthenticatorException;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.security.audit.processors.interfaces
 * File: IAuditProcessor.java
 *
 * History
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * Kevin Huntly         11/23/2008 22:39:20             Created.
 * Kevin Huntly         12/05/2008 13:36:09             Added method to process change requests
 */
public interface Authenticator
{
    static final SecurityServiceBean svcBean = SecurityServiceBean.getInstance();
    static final ResourceControllerBean resBean = svcBean.getResourceBean();
    static final AuthRepo authRepo = svcBean.getConfigData().getAuthRepo();
    static final AuthData authData = svcBean.getConfigData().getAuthData();

    static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityConstants.ERROR_LOGGER + Authenticator.class.getName());

    /**
     * Processes an agent logon request via an LDAP user datastore. If the
     * information provided matches an existing record, the user is
     * considered authenticated successfully and further processing
     * is performed to determine if that user is required to modify
     * their password or setup online reset questions. If yes, the
     * necessary flags are sent back to the frontend for further
     * handling.
     *
     * executeAgentLogon
     * @param guid
     * @param username
     * @param password
     * @param groupName
     * @return List<Object>
     * @throws AuthenticatorException
     */
    List<Object> performLogon(final String guid, final String username, final String password, final String groupName) throws AuthenticatorException;

    void lockUserAccount(final String userId, final int currentCount) throws AuthenticatorException;

    List<String> obtainSecurityData(final String userId, final String userGuid) throws AuthenticatorException;

    /**
     * Processes authentication for the selected security question and user. If successful,
     * a true response is returned back to the frontend signalling that further
     * authentication processing, if required, can take place.
     *
     * @param request
     * @return boolean
     * @throws AuthenticatorException
     */
    boolean verifySecurityData(final List<String> request) throws AuthenticatorException;
}
