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
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.util.List;
import java.util.HashMap;
import javax.sql.DataSource;
import java.sql.SQLException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.SecurityServiceConstants;
import com.cws.esolutions.security.dao.usermgmt.exception.UserManagementException;
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
    static final DataSource dataSource = (DataSource) svcBean.getAuthDataSource();

    static final Logger DEBUGGER = LogManager.getLogger(SecurityServiceConstants.DEBUGGER);
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
    boolean addOrUpdateUserSalt(final String commonName, final String saltValue, final String saltType) throws SQLException;

    /**
     * Processes authentication for the selected security question and user. If successful,
     * a true response is returned back to the frontend signalling that further
     * authentication processing, if required, can take place.
     *
     * @return List - A list of all approved servers within the authorization datastore
     * @throws SQLException {@link java.sql.SQLException} if an exception occurs during processing
     */
    HashMap<Integer, String> obtainSecurityQuestionList() throws SQLException;

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
     * Returns the salt value associated with the given user account to process an
     * authentication request.
     *
     * @param commonName - The commonName associated with the user (also known as GUID)
     * @return String - The salt value for the configured user account
     * @throws SQLException {@link java.sql.SQLException} if an exception occurs during processing
     */
    String getUserPassword(final String commonName, final String userId) throws SQLException;

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
    boolean insertResetData(final String commonName, final String resetId) throws SQLException;

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
     * Allows administrators and users alike to modify their password, either via an Online Reset
     * request or via accounting screens upon request.
     *
     * @param userGuid - The username to perform the modification against
     * @param userId - The username to perform the modification against
     * @param newPass - The new password associated for the user account
     * @param isReset
     * @return <code>true</code> if the process completes, <code>false</code> otherwise
     * @throws UserManagementException if an exception occurs during processing
     */
    boolean modifyUserPassword(final String userGuid, final String userId, final String newPass, final boolean isReset) throws UserManagementException;

    /**
     * 
     * Allows users to modify their password, either via an Online Reset
     * request or via accounting screens upon request.
     *
     * @param userId - The username to perform the modification against
     * @param values - The values associated for the new security questions/answers
     * @return <code>true</code> if the process completes, <code>false</code> otherwise
     * @throws UserManagementException if an exception occurs during processing
     */
    boolean modifyUserSecurity(final String userId, final List<String> values) throws UserManagementException;
}
