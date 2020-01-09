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
package com.cws.esolutions.security.dao.reference.interfaces;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.dao.reference.interfaces
 * File: IUserSecurityInformationDAO.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly   		11/23/2008 22:39:20             Created.
 */
import java.util.List;
import org.slf4j.Logger;
import javax.sql.DataSource;
import java.sql.SQLException;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.SecurityServiceConstants;
/**
 * API allowing data access for user security information, such as salt
 * or reset data.
 *
 * @author cws-khuntly
 * @version 1.0
 */
public interface IUserSecurityInformationDAO
{
    static final SecurityServiceBean svcBean = SecurityServiceBean.getInstance();

    static final String CNAME = IUserSecurityInformationDAO.class.getName();
    static final DataSource dataSource = svcBean.getDataSources().get(SecurityServiceConstants.INIT_SECURITYDS_MANAGER);

    static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityServiceConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    /**
     * Inserts a salt value for the provided user into the security information datastore.
     * The salt value is used during the authentication process, in conjunction with the
     * user's specified password, to perform authentication
     *
     * @param commonName - The commonName associated with the user (also known as GUID)
     * @param saltValue - The salt value generated for the provided user
     * @param saltType - The provided salt type - logon or reset
     * @return <code>true</code> if successful, <code>false</code> otherwise
     * @throws SQLException {@link java.sql.SQLException} if an exception occurs during processing
     */
    boolean addOrUpdateSalt(final String commonName, final String saltValue, final String saltType) throws SQLException;

    /**
     * Removes the salt value for the provided user. This only happens during account
     * removal, as without the salt value the user cannot complete an authentication
     * request.
     *
     * @param commonName - The commonName associated with the user (also known as GUID)
     * @param saltType - The salt type to remove
     * @return <code>true</code> if successful, <code>false</code> otherwise
     * @throws SQLException {@link java.sql.SQLException} if an exception occurs during processing
     */
    boolean removeUserData(final String commonName, final String saltType) throws SQLException;

    /**
     * Returns the salt value associated with the given user account to process an
     * authentication request.
     *
     * @param commonName - The commonName associated with the user (also known as GUID)
     * @param saltType - The provided salt type - logon or reset
     * @return String - The salt value for the configured user account
     * @throws SQLException {@link java.sql.SQLException} if an exception occurs during processing
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
     * @throws SQLException {@link java.sql.SQLException} if an exception occurs during processing
     */
    boolean insertResetData(final String commonName, final String resetId, final String smsCode) throws SQLException;

    /**
     * Lists reset requests housed in the security datastore.
     *
     * @return A <code>List</code> of all associated reset requests housed.
     * @throws SQLException {@link java.sql.SQLException} if an exception occurs during processing
     */
    List<String[]> listActiveResets() throws SQLException;

    /**
     * Returns the salt value associated with the given user account to process an
     * authentication request.
     *
     * @param resetId - The reset request identifier provided to the user
     * @return The commonName (GUID) associated with the reset request identifier
     * @throws SQLException {@link java.sql.SQLException} if an exception occurs during processing
     */
    List<Object> getResetData(final String resetId) throws SQLException;

    /**
     * Returns the salt value associated with the given user account to process an
     * authentication request.
     *
     * @param commonName - The commonName associated with the user (also known as GUID)
     * @param resetId - The reset request identifier provided to the user
     * @return <code>true</code> if the removal was successful, <code>false</code> otherwise
     * @throws SQLException {@link java.sql.SQLException} if an exception occurs during processing
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
     * @throws SQLException {@link java.sql.SQLException} if an exception occurs during processing
     */
    boolean verifySmsForReset(final String commonName, final String resetId, final String smsCode) throws SQLException;
}
