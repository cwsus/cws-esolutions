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
package com.cws.esolutions.security.dao.usermgmt.interfaces;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.dao.usermgmt.interfaces
 * File: UserManager.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.Map;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.security.config.xml.AuthData;
import com.cws.esolutions.security.config.xml.AuthRepo;
import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.SecurityServiceConstants;
import com.cws.esolutions.security.dao.usermgmt.enums.SearchRequestType;
import com.cws.esolutions.security.dao.usermgmt.exception.UserManagementException;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 */
public interface UserManager
{
    static final SecurityServiceBean svcBean = SecurityServiceBean.getInstance();
    static final AuthRepo authRepo = svcBean.getConfigData().getAuthRepo();
    static final AuthData authData = svcBean.getConfigData().getAuthData();

    static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityServiceConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityServiceConstants.ERROR_LOGGER + UserManager.class.getName());

    /**
     * Validates new user uniqueness by ensuring that the provided GUID and username
     * are not already in use within the authentication datastore. If either are in
     * use, a <code>UserManagementException</code> is thrown, and the requestor may
     * either re-submit using new information automatically, or fail outright.
     *
     * @param userId - The chosen username for the new account
     * @param userGuid - The generated UUID for the new user account
     * @throws UserManagementException if any errors occur during the process
     * or if any one of the provided information already exists in the datastore
     */
    void validateUserAccount(final String userId, final String userGuid) throws UserManagementException;

    /**
     * Adds a new user to the authentication system. This method is utilized
     * for an LDAP user datastore, and does NOT insert security reset
     * credentials - these will be configured by the user on their first
     * logon.
     *
     * @param createRequest - An <code>ArrayList<String></code> containing the actual user information,
     * such as username, first name, etc.
     * @param roles - An <code>ArrayList<String></code> containing the actual user information,
     * @return boolean - <code>true</code> if user creation was successful, <code>false</code> otherwise
     * @throws UserManagementException if an error occurs during processing
     */
    boolean addUserAccount(final List<String> createRequest, final List<String> roles) throws UserManagementException;

    /**
     * Adds a new user to the authentication system. This method is utilized
     * for an LDAP user datastore, and does NOT insert security reset
     * credentials - these will be configured by the user on their first
     * logon.
     *
     * @param userId
     * @param userGuid
     * @param changeRequest
     * @return boolean
     * @throws NamingException
     * @throws InvalidAgentException
     */
    boolean modifyUserInformation(final String userId, final String userGuid, final Map<String, Object> changeRequest) throws UserManagementException;

    /**
     * Suspends or unsuspends a provided user account. This could be utilized
     * for various different reasons, including temporarily suspending an
     * account during extended periods of leave or investigations.
     *
     * @param userId - The username to perform the modification against
     * @param userGuid - The UUID of the user to perform the modification against
     * @param isSuspended - <code>true</code> to suspend the account, <code>false</code> to unsuspend
     * @return boolean
     * @throws UserManagementException if an error occurs during processing
     */
    boolean modifyUserSuspension(final String userId, final String userGuid, final boolean isSuspended) throws UserManagementException;

    /**
     * Locks a provided user account by either incrementing the existing lock count
     * by 1 or by setting the value to the configured lockout value.
     *
     * @param userId - The username to perform the modification against
     * @param userGuid - The UUID of the user to perform the modification against
     * @return boolean
     * @throws UserManagementException if an error occurs during processing
     */
    void lockUserAccount(final String userId, final String userGuid) throws UserManagementException;

    /**
     * Unlocks a provided user account by setting the lock count to 0.
     *
     * @param userId - The username to perform the modification against
     * @param userGuid - The UUID of the user to perform the modification against
     * @return boolean
     * @throws UserManagementException if an error occurs during processing
     */
    boolean unlockUserAccount(final String userId, final String userGuid) throws UserManagementException;

    /**
     * Removes a provided user account from the authentication datastore. This
     * method fully deletes - the account will become unrecoverable, and if
     * re-created it will NOT have the same security information (such as UUID)
     *
     * @param userId - The username to perform the modification against
     * @param userGuid - The UUID of the user to perform the modification against
     * @return boolean
     * @throws UserManagementException if an error occurs during processing
     */
    boolean removeUserAccount(final String userId, final String userGuid) throws UserManagementException;

    boolean changeUserPassword(final String userDN, final String newPass, final Long expiry) throws UserManagementException;

    /**
     * Searches for user accounts given provided search data.
     * The following <code>SearchRequestType</code>s are available:
     * <code>USERNAME</code> - A basic username search. If a wildcard
     * is utilized, all matching accounts are returned, otherwise one
     * or zero accounts will be returned, depending on result.
     * <code>EMAILADDR</code> - An email address search. Response follows
     * that of <code>USERNAME</code> search.
     * <code>GUID</code> - A UUID search. 1 or zero accounts should be
     * returned with this type of search.
     * <code>FIRSTNAME</code> - Search based on first name. Multiple accounts
     * may be returned as a result of this search, with or without wildcarding.
     * <code>LASTNAME</code> - Search based on last name. Multiple accounts
     * may be returned as a result of this search, with or without wildcarding.
     *
     * @param searchType - The <code>SearchRequestType</code> to utilize for this
     * this search. This determines which <code>Filter</code> to use.
     * @param searchData - The search string to utilize within the <code>Filter</code>
     * that correlates to the provided <code>SearchRequestType</code>
     * @return List<String[]> - An <code>ArrayList</code> containing a string array of
     * all possible responses
     * @throws UserManagementException if an error occurs during processing or if an invalid
     * <code>SearchRequestType</code> value is provided.
     */
    List<Object[]> searchUsers(final SearchRequestType searchType, final String searchData) throws UserManagementException;

    /**
     * Loads and returns data for a provided user account. Search is performed using the user's
     * GUID (Globally Unique IDentifier). If no user, or more than one user is returned for the
     * provided information, an <code>UserManagementException</code> is thrown and returned to
     * the requestor.
     *
     * @param userGuid - The Globally Unique IDentifier of the desired user
     * @return List<Object> - The associated user account data
     * @throws UserManagementException if an error occurs during the search process
     */
    List<Object> loadUserAccount(final String userGuid) throws UserManagementException;

    /**
     * Returns a list of ALL user accounts stored in the authentication datastore. This is
     * ONLY to be used with the reapers - currently, the <link>com.cws.esolutions.security.quartz.IdleAccountLocker</link>
     * and <link>com.cws.esolutions.security.quartz.PasswordExpirationNotifier</link>.
     *
     * <strong>THIS SHOULD NOT BE USED IN ANY OTHER CLASSES UNLESS ABSOLUTELY NECESSARY.</strong>
     *
     * @return List<String[]> - A list of all user accounts currently housed in the repository
     * @throws UserManagementException if an error occurs during processing
     */
    List<String[]> listUserAccounts() throws UserManagementException;
}
