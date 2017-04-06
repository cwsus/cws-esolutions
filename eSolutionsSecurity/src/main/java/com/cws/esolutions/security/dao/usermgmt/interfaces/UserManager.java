/*
 * Copyright (c) 2009 - 2017 CaspersBox Web Services
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
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.SecurityServiceConstants;
import com.cws.esolutions.security.config.xml.SecurityConfig;
import com.cws.esolutions.security.config.xml.UserReturningAttributes;
import com.cws.esolutions.security.config.xml.RepositoryConfig;
import com.cws.esolutions.security.config.xml.SecurityReturningAttributes;
import com.cws.esolutions.security.dao.usermgmt.exception.UserManagementException;
/**
 * API allowing user user management tasks. Used in conjunction with the
 * {@link com.cws.esolutions.security.dao.usermgmt.factory.UserManagerFactory}
 * to provide functionality for LDAP and SQL datastores.
 *
 * @author khuntly
 * @version 1.0
 */
public interface UserManager
{
    static final SecurityServiceBean svcBean = SecurityServiceBean.getInstance();
    static final SecurityConfig secConfig = svcBean.getConfigData().getSecurityConfig();
    static final RepositoryConfig repoConfig = svcBean.getConfigData().getRepoConfig();
    static final UserReturningAttributes userAttributes = repoConfig.getUserAttributes();
    static final SecurityReturningAttributes securityAttributes = repoConfig.getSecurityAttributes();

    static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityServiceConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    /**
     * Validates new user uniqueness by ensuring that the provided GUID and username
     * are not already in use within the authentication datastore. If either are in
     * use, a <code>UserManagementException</code> is thrown, and the requestor may
     * either re-submit using new information automatically, or fail outright.
     *
     * @param userId - The chosen username for the new account
     * @param userGuid - The generated UUID for the new user account
     * @return <code>true</code> if the account already exists, <code>false</code> otherwise
     * @throws UserManagementException if an exception occurs during processing
     */
    boolean validateUserAccount(final String userId, final String userGuid) throws UserManagementException;

    /**
     * Adds a new user to the authentication system. This method is utilized
     * for an LDAP user datastore, and does NOT insert security reset
     * credentials - these will be configured by the user on their first
     * logon.
     *
     * @param userAccount - An <code>ArrayList</code> containing the actual user information,
     * such as username, first name, etc.
     * @param roles - An <code>ArrayList</code> containing the actual user information,
     * @return boolean - <code>true</code> if user creation was successful, <code>false</code> otherwise
     * @throws UserManagementException if an exception occurs during processing
     */
    boolean addUserAccount(final List<String> userAccount, final List<String> roles) throws UserManagementException;

    /**
     * Removes a provided user account from the authentication datastore. This
     * method fully deletes - the account will become unrecoverable, and if
     * re-created it will NOT have the same security information (such as UUID)
     *
     * @param userId - The username to perform the modification against
     * @return <code>true</code> if the process completes, <code>false</code> otherwise
     * @throws UserManagementException if an exception occurs during processing
     */
    boolean removeUserAccount(final String userId) throws UserManagementException;

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
     * @param searchData - The search string to utilize within the <code>Filter</code>
     * that correlates to the provided <code>SearchRequestType</code>
     * @return List - An <code>ArrayList</code> containing a string array of
     * all possible responses
     * @throws UserManagementException if an exception occurs during processing
     */
    List<String[]> searchUsers(final String searchData) throws UserManagementException;

    /**
     * Loads and returns data for a provided user account. Search is performed using the user's
     * GUID (Globally Unique IDentifier). If no user, or more than one user is returned for the
     * provided information, an <code>UserManagementException</code> is thrown and returned to
     * the requestor.
     *
     * @param userId - The Globally Unique IDentifier of the desired user
     * @return <code>ArrayList</code> - The associated user account data
     * @throws UserManagementException if an exception occurs during processing
     */
    List<Object> loadUserAccount(final String userId) throws UserManagementException;

    /**
     * Returns a list of ALL user accounts stored in the authentication datastore. This is
     * ONLY to be used with the reapers - currently, the {@link com.cws.esolutions.security.quartz.IdleAccountLocker}
     * and {@link com.cws.esolutions.security.quartz.PasswordExpirationNotifier}.
     *
     * <strong>THIS SHOULD NOT BE USED IN ANY OTHER CLASSES UNLESS ABSOLUTELY NECESSARY.</strong>
     *
     * @return <code>ArrayList</code> - A list of all user accounts currently housed in the repository
     * @throws UserManagementException if an exception occurs during processing
     */
    List<String[]> listUserAccounts() throws UserManagementException;

    /**
     * Allows an authenticated user or administrator to modify the email address of a provided
     * account by updating the data within the configured authorization datastore.
     *
     * @param userId - The username to perform the modification against
     * @param value - The new email address to apply
     * @return <code>true</code> if the process completes, <code>false</code> otherwise
     * @throws UserManagementException if an exception occurs during processing
     */
    boolean modifyUserEmail(final String userId, final String value) throws UserManagementException;

    /**
     * Allows an authenticated user or administrator to modify the contact information of a provided
     * account by updating the data within the configured authorization datastore.
     *
     * @param userId - The username to perform the modification against
     * @param value - The list of modification items to make
     * @return <code>true</code> if the process completes, <code>false</code> otherwise
     * @throws UserManagementException if an exception occurs during processing
     */
    boolean modifyUserContact(final String userId, final List<String> value) throws UserManagementException;

    /**
     * Suspends or unsuspends a provided user account. This could be utilized
     * for various different reasons, including temporarily suspending an
     * account during extended periods of leave or investigations.
     *
     * @param userId - The username to perform the modification against
     * @param isSuspended - <code>true</code> to suspend the account, <code>false</code> to unsuspend
     * @return <code>true</code> if the process completes, <code>false</code> otherwise
     * @throws UserManagementException if an exception occurs during processing
     */
    boolean modifyUserSuspension(final String userId, final boolean isSuspended) throws UserManagementException;

    /**
     * 
     * Allows an administrator to modify the groups the specified account is associated with by adding or
     * removing the account from the provided group names.
     *
     * @param userId - The username to perform the modification against
     * @param value - The group associated to add to or remove from
     * @return <code>true</code> if the process completes, <code>false</code> otherwise
     * @throws UserManagementException if an exception occurs during processing
     */
    boolean modifyUserGroups(final String userId, final Object[] value) throws UserManagementException;

    /**
     * Allows an administrator to either lock or unlock a user account's ability to perform an
     * Online Reset request, if enabled.
     *
     * @param userId - The username to perform the modification against
     * @param isLocked - <code>true</code> if lock, <code>false</code> if unlock
     * @return <code>true</code> if the process completes, <code>false</code> otherwise
     * @throws UserManagementException if an exception occurs during processing
     */
    boolean modifyOlrLock(final String userId, final boolean isLocked) throws UserManagementException;

    /**
     * Allows an administrator to lock or unlock a user account as desired.
     *
     * @param userId - The username to perform the modification against
     * @param isLocked - <code>true</code> if locked, <code>false</code> if unlocked
     * @param increment - The count to increment by
     * @return <code>true</code> if the process completes, <code>false</code> otherwise
     * @throws UserManagementException if an exception occurs during processing
     */
    boolean modifyUserLock(final String userId, final boolean isLocked, final int increment) throws UserManagementException;

    /**
     * Allows administrators and users alike to modify their password, either via an Online Reset
     * request or via accounting screens upon request.
     *
     * @param userId - The username to perform the modification against
     * @param newPass - The new password associated for the user account
     * @return <code>true</code> if the process completes, <code>false</code> otherwise
     * @throws UserManagementException if an exception occurs during processing
     */
    boolean modifyUserPassword(final String userId, final String newPass) throws UserManagementException;

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

    /**
     * Allows a user account to modify their OTP secret, if the account has been set up with OTP
     * authentication.
     *
     * @param userId - The username to perform the modification against
     * @param addSecret - <code>true</code> to add a new secret, <code>false</code> to replace an existing secret
     * @param secret - The secret itself
     * @return <code>true</code> if the process completes, <code>false</code> otherwise
     * @throws UserManagementException if an exception occurs during processing
     */
    boolean modifyOtpSecret(final String userId, final boolean addSecret, final String secret) throws UserManagementException;
}
