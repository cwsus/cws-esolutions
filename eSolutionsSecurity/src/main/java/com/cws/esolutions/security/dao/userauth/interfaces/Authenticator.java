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
 * cws-khuntly           11/23/2008 22:39:20             Created.
 */
import java.util.List;
import java.util.HashMap;
import javax.sql.DataSource;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.config.xml.SystemConfig;
import com.cws.esolutions.security.SecurityServiceConstants;
import com.cws.esolutions.security.config.xml.SecurityConfig;
import com.cws.esolutions.security.dao.userauth.exception.AuthenticatorException;
import com.cws.esolutions.security.dao.usermgmt.exception.UserManagementException;
/**
 * @author cws-khuntly
 * @version 1.0
 */
public interface Authenticator
{
    static final SecurityServiceBean svcBean = SecurityServiceBean.getInstance();
    static final SecurityConfig secConfig = svcBean.getConfigData().getSecurityConfig();
    static final SystemConfig systemConfig = svcBean.getConfigData().getSystemConfig();
    static final DataSource dataSource = svcBean.getDataSources().get("SecurityDataSource");

    static final Logger DEBUGGER = LogManager.getLogger(SecurityServiceConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    /**
     * 
     * @param userGuid
     * @param userName
     * @param password
     * @return
     * @throws AuthenticatorException
     */
    boolean performLogon(final String userGuid, final String userName, final String password) throws AuthenticatorException;

    /**
     * 
     * @param userGuid
     * @param userName
     * @param questionMap
     * @return
     */
    boolean verifySecurityData(final String userGuid, final String userName, final HashMap<String, String> questionMap) throws AuthenticatorException;

    /**
     * Allows an administrator to lock or unlock a user account as desired.
     *
     * @param userId - The username to perform the modification against
     * @param guid - The Globally Unique Identifier for the account in the repository
     * @return <code>true</code> if the process completes, <code>false</code> otherwise
     * @throws UserManagementException if an exception occurs during processing
     */
    boolean performSuccessfulLogin(final String userId, final String guid, final String authToken) throws AuthenticatorException;

    /**
     * 
     * @param userGuid
     * @param userName
     * @param authToken
     * @throws AuthenticatorException
     */
    void performLogoff(final String userGuid, final String userName, final String authToken) throws AuthenticatorException;

    /**
     * Processes an agent logon request via an LDAP user datastore. If the
     * information provided matches an existing record, the user is
     * considered authenticated successfully and further processing
     * is performed to determine if that user is required to modify
     * their password or setup online reset questions. If yes, the
     * necessary flags are sent back to the frontend for further
     * handling.
     *
     * @param guid - The user's UUID
     * @param userId
     * @param authToken
     * @return String - The account information for the authenticated user
     * @throws AuthenticatorException {@link com.cws.esolutions.security.dao.userauth.exception.AuthenticatorException} if an exception occurs during processing
     */
    boolean validateAuthToken(final String guid, final String userId, final String authToken) throws AuthenticatorException;

    /**
     * Processes an agent logon request via an LDAP user datastore. If the
     * information provided matches an existing record, the user is
     * considered authenticated successfully and further processing
     * is performed to determine if that user is required to modify
     * their password or setup online reset questions. If yes, the
     * necessary flags are sent back to the frontend for further
     * handling.
     *
     * @param guid - The user's UUID
     * @return String - The account information for the authenticated user
     * @throws AuthenticatorException {@link com.cws.esolutions.security.dao.userauth.exception.AuthenticatorException} if an exception occurs during processing
     */
    List<Boolean> getOlrStatus(final String guid, final String userId) throws AuthenticatorException;
}
