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
package com.cws.esolutions.security.dao.reference.interfaces;

import java.util.List;
import org.slf4j.Logger;
import javax.sql.DataSource;
import java.sql.SQLException;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.security.SecurityConstants;
import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.core.controllers.ResourceControllerBean;
/**
 * eSolutionsCore
 * com.cws.esolutions.security.dao.reference.interfaces
 * IUserSecurityInformationDAO.java
 *
 *
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
 * khuntly @ May 1, 2013 6:41:46 AM
 *     Created.
 */
public interface IUserSecurityInformationDAO
{
    static final SecurityServiceBean svcBean = SecurityServiceBean.getInstance();
    static final ResourceControllerBean resBean = svcBean.getResourceBean();

    static final String CNAME = IUserServiceInformationDAO.class.getName();
    static final DataSource dataSource = resBean.getDataSource().get(SecurityConstants.INIT_SECURITYDS_MANAGER);

    static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityConstants.ERROR_LOGGER + CNAME);

    /**
     * Inserts a salt value for the provided user into the security information datastore.
     * The salt value is used during the authentication process, in conjunction with the
     * user's specified password, to perform authentication
     *
     * @param commonName - The commonName associated with the user (also known as GUID)
     * @param saltValue - The salt value generated for the provided user
     * @param saltType - The provided salt type - logon or reset
     * @return <code>true</code> if successful, <code>false</code> otherwise
     * @throws SQLException if an exception occurs during insertion process
     */
    boolean addUserSalt(final String commonName, final String saltValue, final String saltType) throws SQLException;

    /**
     * Updates the configured salt value for the configured user. This happens during a
     * user-initiated password change or reset, and also during an administrator-initiated
     * password reset.
     *
     * @param commonName - The commonName associated with the user (also known as GUID)
     * @param saltValue - The salt value generated for the provided user
     * @param saltType - The provided salt type - logon or reset
     * @return <code>true</code> if successful, <code>false</code> otherwise
     * @throws SQLException if an exception occurs during insertion process
     */
    boolean updateUserSalt(final String commonName, final String saltValue, final String saltType) throws SQLException;

    /**
     * Removes the salt value for the provided user. This only happens during account
     * removal, as without the salt value the user cannot complete an authentication
     * request.
     *
     * @param commonName - The commonName associated with the user (also known as GUID)
     * @return <code>true</code> if successful, <code>false</code> otherwise
     * @throws SQLException if an exception occurs during insertion process
     */
    boolean removeUserData(final String commonName) throws SQLException;

    /**
     * Returns the salt value associated with the given user account to process an
     * authentication request.
     *
     * @param commonName - The commonName associated with the user (also known as GUID)
     * @param saltType - The provided salt type - logon or reset
     * @return String - The salt value for the configured user account
     * @throws SQLException if an exception occurs during insertion process
     */
    String getUserSalt(final String commonName, final String saltType) throws SQLException;

    /**
     * Adds a reset request into the security datastore. This information is added on
     * the request of a user or an administrator during the password reset process. A
     * scheduled event within the database clears out requests older than 30 minutes from
     * the time they were submitted.
     *
     * @param commonName - The commonName associated with the user (also known as GUID)
     * @param resetId - The reset request identifier provided to the user
     * @param smsCode - The SMS code sent to the user (if any)
     * @return <code>true</code> if the insertion was successful, <code>false</code> otherwise
     * @throws SQLException if an exception occurs during insertion process
     */
    boolean insertResetData(final String commonName, final String resetId, final String smsCode) throws SQLException;

    List<String[]> listActiveResets() throws SQLException;

    /**
     * Returns the salt value associated with the given user account to process an
     * authentication request.
     *
     * @param resetId - The reset request identifier provided to the user
     * @return The commonName (GUID) associated with the reset request identifier
     * @throws SQLException if an exception occurs during insertion process
     */
    List<String> getResetData(final String resetId) throws SQLException;

    /**
     * Returns the salt value associated with the given user account to process an
     * authentication request.
     *
     * @param commonName - The commonName associated with the user (also known as GUID)
     * @param resetId - The reset request identifier provided to the user
     * @return <code>true</code> if the removal was successful, <code>false</code> otherwise
     * @throws SQLException if an exception occurs during insertion process
     */
    boolean removeResetData(final String commonName, final String resetId) throws SQLException;

    /**
     * Returns the salt value associated with the given user account to process an
     * authentication request.
     *
     * @param commonName - The commonName (GUID) associated with the reset request
     * @param resetId - The reset request identifier provided to the user
     * @param smsCode - The SMS code provided to the user associated with the reset request
     * @return <code>true</code> if successful, <code>false</code> otherwise
     * @throws SQLException if an exception occurs during insertion process
     */
    boolean verifySmsForReset(final String commonName, final String resetId, final String smsCode) throws SQLException;

    Object getAuthenticationData(final String commonName, final String dataType) throws SQLException;
}
