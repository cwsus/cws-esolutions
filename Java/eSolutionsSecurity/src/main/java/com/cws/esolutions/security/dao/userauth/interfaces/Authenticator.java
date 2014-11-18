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
package com.cws.esolutions.security.dao.userauth.interfaces;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.dao.userauth.interfaces
 * File: Authenticator.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.SecurityServiceConstants;
import com.cws.esolutions.security.config.xml.SecurityConfig;
import com.cws.esolutions.security.config.xml.UserReturningAttributes;
import com.cws.esolutions.security.config.xml.RepositoryConfig;
import com.cws.esolutions.security.config.xml.SecurityReturningAttributes;
import com.cws.esolutions.security.dao.userauth.exception.AuthenticatorException;
/**
 * API allowing user authentication tasks. Used in conjunction with the
 * {@link com.cws.esolutions.security.dao.userauth.factory.AuthenticatorFactory}
 * to provide functionality for LDAP and SQL datastores.
 *
 * @author khuntly
 * @version 1.0
 */
public interface Authenticator
{
    static final SecurityServiceBean svcBean = SecurityServiceBean.getInstance();
    static final SecurityConfig secConfig = svcBean.getConfigData().getSecurityConfig();
    static final RepositoryConfig repoConfig = svcBean.getConfigData().getRepoConfig();
    static final UserReturningAttributes userAttributes = repoConfig.getUserAttributes();
    static final SecurityReturningAttributes securityAttributes = repoConfig.getSecurityAttributes();

    static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityServiceConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityServiceConstants.ERROR_LOGGER + Authenticator.class.getName());

    /**
     * Processes an agent logon request via an LDAP user datastore. If the
     * information provided matches an existing record, the user is
     * considered authenticated successfully and further processing
     * is performed to determine if that user is required to modify
     * their password or setup online reset questions. If yes, the
     * necessary flags are sent back to the frontend for further
     * handling.
     *
     * @param userId - the username to validate data against
     * @param password - the password to validate data against
     * @return List - The account information for the authenticated user
     * @throws AuthenticatorException {@link com.cws.esolutions.security.dao.userauth.exception.AuthenticatorException} if an exception occurs during processing
     */
    List<Object> performLogon(final String userId, final String password) throws AuthenticatorException;

    /**
     * Processes an agent logon request via an LDAP user datastore. If the
     * information provided matches an existing record, the user is
     * considered authenticated successfully and further processing
     * is performed to determine if that user is required to modify
     * their password or setup online reset questions. If yes, the
     * necessary flags are sent back to the frontend for further
     * handling.
     *
     * @param userId - the username to validate data against
     * @param guid - the GUID to validate data against
     * @return List - The security data housed for the given user
     * @throws AuthenticatorException {@link com.cws.esolutions.security.dao.userauth.exception.AuthenticatorException} if an exception occurs during processing
     */
    List<String> obtainSecurityData(final String userId, final String guid) throws AuthenticatorException;

    /**
     * Processes an agent logon request via an LDAP user datastore. If the
     * information provided matches an existing record, the user is
     * considered authenticated successfully and further processing
     * is performed to determine if that user is required to modify
     * their password or setup online reset questions. If yes, the
     * necessary flags are sent back to the frontend for further
     * handling.
     *
     * @param userId - the username to validate data against
     * @param guid - the GUID to validate data against
     * @return boolean - <code>true</code> if verified, <code>false</code> otherwise
     * @throws AuthenticatorException {@link com.cws.esolutions.security.dao.userauth.exception.AuthenticatorException} if an exception occurs during processing
     */
    String obtainOtpSecret(final String userId, final String guid) throws AuthenticatorException;

    /**
     * Processes authentication for the selected security question and user. If successful,
     * a true response is returned back to the frontend signalling that further
     * authentication processing, if required, can take place.
     *
     * @param userId - the username to validate data against
     * @param guid - the GUID to validate data against
     * @param values - the security information to validate for the given username
     * @return boolean - <code>true</code> if verified, <code>false</code> otherwise
     * @throws AuthenticatorException {@link com.cws.esolutions.security.dao.userauth.exception.AuthenticatorException} if an exception occurs during processing
     */
    boolean verifySecurityData(final String userId, final String guid, List<String> values) throws AuthenticatorException;
}
