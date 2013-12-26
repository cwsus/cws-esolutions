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
package com.cws.esolutions.security.dao.usermgmt.impl;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.security.dao.usermgmt.impl
 * File: LDAPUserManager.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.net.ConnectException;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.AddRequest;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldap.sdk.ModifyRequest;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.DeleteRequest;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.ModificationType;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.LDAPConnectionPool;

import com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager;
import com.cws.esolutions.security.dao.usermgmt.enums.SearchRequestType;
import com.cws.esolutions.security.dao.usermgmt.exception.UserManagementException;
/**
 * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager
 */
public class LDAPUserManager implements UserManager
{
    private static final String CNAME = LDAPUserManager.class.getName();

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#validateUserAccount(java.lang.String, java.lang.String)
     */
    @Override
    public synchronized void validateUserAccount(final String userId, final String userGuid) throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#validateUserAccount(final String userID, final String userGuid) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("userId: {}", userId);
            DEBUGGER.debug("userGuid: {}", userGuid);
        }

        Filter searchFilter = null;
        LDAPConnection ldapConn = null;
        SearchResult searchResult = null;
        SearchRequest searchRequest = null;
        LDAPConnectionPool ldapPool = null;

        try
        {
            ldapPool = (LDAPConnectionPool) resBean.getAuthDataSource();

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPConnectionPool: {}", ldapPool);
            }

            if (!(ldapPool.isClosed()))
            {
                ldapConn = ldapPool.getConnection();

                if (DEBUG)
                {
                    DEBUGGER.debug("LDAPConnection: {}", ldapConn);
                }

                if (ldapConn.isConnected())
                {
                    searchFilter = Filter.create("(&(objectClass=inetOrgPerson)" +
                            "(&(" + authData.getCommonName() + "=" + userGuid + ")))");

                    if (DEBUG)
                    {
                        DEBUGGER.debug("searchFilter: {}", searchFilter);
                    }

                    searchRequest = new SearchRequest(
                            authRepo.getRepositoryBaseDN(),
                            SearchScope.SUB,
                            searchFilter,
                            authData.getCommonName());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("searchRequest: {}", searchRequest);
                    }

                    searchResult = ldapConn.search(searchRequest);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("searchResult: {}", searchResult);
                    }

                    if ((searchResult.getResultCode() == ResultCode.SUCCESS) && (searchResult.getEntryCount() == 0))
                    {
                        // we should have a valid uuid now
                        searchFilter = Filter.create("(&(objectClass=inetOrgPerson)" +
                                "(&(" + authData.getUserId() + "=" + userId + ")))");

                        if (DEBUG)
                        {
                            DEBUGGER.debug("searchFilter: {}", searchFilter);
                        }

                        searchRequest = new SearchRequest(
                                authRepo.getRepositoryBaseDN(),
                                SearchScope.SUB,
                                searchFilter,
                                authData.getUserId());

                        if (DEBUG)
                        {
                            DEBUGGER.debug("searchRequest: {}", searchRequest);
                        }

                        searchResult = ldapConn.search(searchRequest);

                        if ((searchResult.getResultCode() == ResultCode.SUCCESS) && (searchResult.getEntryCount() != 0))
                        {
                            throw new UserManagementException("A user currently exists with the provided username");
                        }
                    }
                    else
                    {
                        throw new UserManagementException("A user currently exists with the provided UUID");
                    }
                }
                else
                {
                    throw new ConnectException("Failed to create LDAP connection using the specified information");
                }
            }
            else
            {
                throw new ConnectException("Failed to create LDAP connection using the specified information");
            }
        }
        catch (LDAPException lx)
        {
            ERROR_RECORDER.error(lx.getMessage(), lx);

            throw new UserManagementException(lx.getMessage(), lx);
        }
        catch (ConnectException cx)
        {
            ERROR_RECORDER.error(cx.getMessage(), cx);

            throw new UserManagementException(cx.getMessage(), cx);
        }
        finally
        {
            if ((ldapPool != null) && ((ldapConn != null) && (ldapConn.isConnected())))
            {
                ldapPool.releaseConnection(ldapConn);
            }
        }
    }

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#addUserAccount(java.lang.String, java.util.List)
     */
    @Override
    public synchronized boolean addUserAccount(final String userDN, final List<String> createRequest) throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#addUserAccount(final String userDN, final String userDN, final List<String> createRequest) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("CreateRequest: {}", createRequest);
        }

        LDAPResult ldapResult = null;
        boolean isUserCreated = false;
        LDAPConnection ldapConn = null;
        LDAPConnectionPool ldapPool = null;

        try
        {
            ldapPool = (LDAPConnectionPool) resBean.getAuthDataSource();

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPConnectionPool: {}", ldapPool);
            }

            if (!(ldapPool.isClosed()))
            {
                ldapConn = ldapPool.getConnection();

                if (DEBUG)
                {
                    DEBUGGER.debug("LDAPConnection: {}", ldapConn);
                }

                if (ldapConn.isConnected())
                {
                    // have a connection, create the user
                    List<Attribute> newAttributes = new ArrayList<>();
                    newAttributes.add(new Attribute("objectClass", authData.getObjectClass()));
                    newAttributes.add(new Attribute(authData.getUserId(), createRequest.get(0)));
                    newAttributes.add(new Attribute(authData.getUserPassword(), createRequest.get(1)));
                    newAttributes.add(new Attribute(authData.getUserRole(), createRequest.get(2)));
                    newAttributes.add(new Attribute(authData.getSurname(), createRequest.get(3)));
                    newAttributes.add(new Attribute(authData.getGivenName(), createRequest.get(4)));
                    newAttributes.add(new Attribute(authData.getEmailAddr(), createRequest.get(5)));
                    newAttributes.add(new Attribute(authData.getCommonName(), createRequest.get(6)));
                    newAttributes.add(new Attribute(authData.getDisplayName(), createRequest.get(7)));
                    newAttributes.add(new Attribute(authData.getUserType(), createRequest.get(8)));

                    AddRequest addRequest = new AddRequest(userDN, newAttributes);

                    ldapResult = ldapConn.add(addRequest);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("LDAPResult: {}", ldapResult);
                    }

                    if (ldapResult.getResultCode() == ResultCode.SUCCESS)
                    {
                        isUserCreated = true;
                    }
                    else
                    {
                        throw new UserManagementException("Failed to create the new user account. Please ensure that the data provided is valid and accurate.");
                    }
                }
                else
                {
                    throw new ConnectException("Failed to create LDAP connection using the specified information");
                }
            }
            else
            {
                throw new ConnectException("Failed to create LDAP connection using the specified information");
            }
        }
        catch (ConnectException cx)
        {
            ERROR_RECORDER.error(cx.getMessage(), cx);

            throw new UserManagementException(cx.getMessage(), cx);
        }
        catch (LDAPException lx)
        {
            ERROR_RECORDER.error(lx.getMessage(), lx);

            throw new UserManagementException(lx.getMessage(), lx);
        }
        finally
        {
            if ((ldapPool != null) && ((ldapConn != null) && (ldapConn.isConnected())))
            {
                ldapPool.releaseConnection(ldapConn);
            }
        }

        return isUserCreated;
    }

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#modifyUserInformation(java.lang.String, java.lang.String, java.util.Map)
     */
    @Override
    public synchronized boolean modifyUserInformation(final String userId, final String userGuid, final Map<String, Object> changeRequest) throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#modifyUserInformation(final String userId, final String userGuid, final Map<String, Object> changeRequest) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug(userId);
            DEBUGGER.debug(userGuid);
            DEBUGGER.debug("changeRequest: {}", changeRequest);
        }

        boolean isComplete = false;
        LDAPConnection ldapConn = null;
        LDAPConnectionPool ldapPool = null;

        try
        {
            ldapPool = (LDAPConnectionPool) resBean.getAuthDataSource();

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPConnectionPool: {}", ldapPool);
            }

            if (!(ldapPool.isClosed()))
            {
                ldapConn = ldapPool.getConnection();

                if (DEBUG)
                {
                    DEBUGGER.debug("LDAPConnection: {}", ldapConn);
                }

                if (ldapConn.isConnected())
                {
                    Filter searchFilter = Filter.create("(&(objectClass=inetOrgPerson)" +
                            "(&("  + authData.getUserId() +  "=" + userId + "))" +
                            "(&("  + authData.getCommonName() +  "=" + userGuid + ")))");

                    SearchRequest searchRequest = new SearchRequest(
						    authRepo.getRepositoryUserBase(),
                            SearchScope.SUB,
                            searchFilter,
                            authData.getCommonName(),
                            authData.getUserId(),
                            authData.getDisplayName(),
                            authData.getEmailAddr(),
                            authData.getTelephoneNumber(),
                            authData.getPagerNumber(),
                            authData.getExpiryDate(),
                            authData.getGivenName(),
                            authData.getIsSuspended(),
                            authData.getLastLogin(),
                            authData.getLockCount(),
                            authData.getOlrLocked(),
                            authData.getOlrSetupReq(),
                            authData.getSurname(),
                            authData.getTcAccepted(),
                            authData.getUserRole(),
                            authData.getSecQuestionOne(),
                            authData.getSecQuestionTwo(),
                            authData.getSecAnswerOne(),
                            authData.getSecAnswerTwo());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("searchFilter: {}", searchFilter);
                        DEBUGGER.debug("searchRequest: {}", searchRequest);
                    }

                    SearchResult searchResult = ldapConn.search(searchRequest);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("searchResult: {}", searchResult);
                    }

                    if (searchResult.getResultCode() == ResultCode.SUCCESS)
                    {
                        if (searchResult.getSearchEntries().size() == 1)
                        {
                            SearchResultEntry entry = searchResult.getSearchEntries().get(0);

                            if (DEBUG)
                            {
                                DEBUGGER.debug("SearchResultEntry: {}", entry);
                            }

                            List<Modification> modifyList = new ArrayList<>();

                            for (String key : changeRequest.keySet())
                            {
                                if (entry.getAttributeValue(key) != null)
                                {
                                    modifyList.add(new Modification(ModificationType.REPLACE, key, String.valueOf(changeRequest.get(key))));
                                }
                                else
                                {
                                    modifyList.add(new Modification(ModificationType.ADD, key, String.valueOf(changeRequest.get(key))));
                                }
                            }

                            if (DEBUG)
                            {
                                DEBUGGER.debug("modifyList: {}", modifyList);
                            }

                            LDAPResult ldapResult = ldapConn.modify(new ModifyRequest(entry.getDN(), modifyList));

                            if (DEBUG)
                            {
                                DEBUGGER.debug("LDAPResult: {}", ldapResult);
                            }

                            if (ldapResult.getResultCode() == ResultCode.SUCCESS)
                            {
                                isComplete = true;
                            }
                        }
                        else
                        {
                            throw new LDAPException(ResultCode.ASSERTION_FAILED, "Multiple user accounts were located for the provided information.");
                        }
                    }
                    else
                    {
                        throw new LDAPException(ResultCode.ASSERTION_FAILED, "No user accounts were located with the provided information.");
                    }
                }
                else
                {
                    throw new ConnectException("Failed to create LDAP connection using the specified information");
                }
            }
            else
            {
                throw new ConnectException("Failed to create LDAP connection using the specified information");
            }
        }
        catch (LDAPException lx)
        {
            ERROR_RECORDER.error(lx.getMessage(), lx);

            throw new UserManagementException(lx.getMessage(), lx);
        }
        catch (ConnectException cx)
        {
            ERROR_RECORDER.error(cx.getMessage(), cx);

            throw new UserManagementException(cx.getMessage(), cx);
        }
        finally
        {
            if ((ldapPool != null) && ((ldapConn != null) && (ldapConn.isConnected())))
            {
                ldapPool.releaseConnection(ldapConn);
            }
        }

        return isComplete;
    }

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#modifyUserSuspension(java.lang.String, java.lang.String, boolean)
     */
    @Override
    public synchronized boolean modifyUserSuspension(final String userId, final String userGuid, final boolean isSuspended) throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#modifyUserSuspension(final String userId, final String userGuid, final boolean isSuspended) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userId);
            DEBUGGER.debug("Value: {}", userGuid);
            DEBUGGER.debug("Value: {}", isSuspended); 
        }

        boolean isComplete = false;
        LDAPConnection ldapConn = null;
        LDAPConnectionPool ldapPool = null;

        try
        {
            ldapPool = (LDAPConnectionPool) resBean.getAuthDataSource();

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPConnectionPool: {}", ldapPool);
            }

            if (!(ldapPool.isClosed()))
            {
                ldapConn = ldapPool.getConnection();

                if (DEBUG)
                {
                    DEBUGGER.debug("LDAPConnection: {}", ldapConn);
                }

                if (ldapConn.isConnected())
                {
                    Filter searchFilter = Filter.create("(&(objectClass=inetOrgPerson)" +
                            "(&("  + authData.getUserId() +  "=" + userId + "))" +
                            "(&("  + authData.getCommonName() +  "=" + userGuid + ")))");

                    SearchRequest searchReq = new SearchRequest(
						    authRepo.getRepositoryUserBase(),
                            SearchScope.SUB,
                            searchFilter,
                            authData.getIsSuspended());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("searchFilter: {}", searchFilter);
                        DEBUGGER.debug("searchRequest: {}", searchReq);
                    }

                    SearchResult searchResult = ldapConn.search(searchReq);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("searchResult: {}", searchResult);
                    }

                    if ((searchResult.getResultCode() == ResultCode.SUCCESS) && (searchResult.getSearchEntries().size() == 1))
                    {
                        SearchResultEntry entry = searchResult.getSearchEntries().get(0);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("SearchResultEntry: {}", entry);
                        }

                        List<Modification> modifyList = new ArrayList<>
                        (
                            Arrays.asList
                            (
                                new Modification(ModificationType.REPLACE, authData.getIsSuspended(), String.valueOf(isSuspended))
                            )
                        );

                        if (DEBUG)
                        {
                            DEBUGGER.debug("modifyList: {}", modifyList);
                        }

                        LDAPResult ldapResult = ldapConn.modify(new ModifyRequest(entry.getDN(), modifyList));

                        if (DEBUG)
                        {
                            DEBUGGER.debug("LDAPResult: {}", ldapResult);
                        }

                        if (ldapResult.getResultCode() == ResultCode.SUCCESS)
                        {
                            isComplete = true;
                        }
                    }
                }
                else
                {
                    throw new ConnectException("Failed to create LDAP connection using the specified information");
                }
            }
            else
            {
                throw new ConnectException("Failed to create LDAP connection using the specified information");
            }
        }
        catch (LDAPException lx)
        {
            ERROR_RECORDER.error(lx.getMessage(), lx);

            throw new UserManagementException(lx.getMessage(), lx);
        }
        catch (ConnectException cx)
        {
            ERROR_RECORDER.error(cx.getMessage(), cx);

            throw new UserManagementException(cx.getMessage(), cx);
        }
        finally
        {
            if ((ldapPool != null) && ((ldapConn != null) && (ldapConn.isConnected())))
            {
                ldapPool.releaseConnection(ldapConn);
            }
        }

        return isComplete;
    }

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#unlockUserAccount(java.lang.String, java.lang.String)
     */
    @Override
    public synchronized boolean unlockUserAccount(final String userId, final String userGuid) throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#modifyUserSuspension(final String userId, final String userGuid) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userId);
            DEBUGGER.debug("Value: {}", userGuid);
        }

        boolean isComplete = false;
        LDAPConnection ldapConn = null;
        LDAPConnectionPool ldapPool = null;

        try
        {
            ldapPool = (LDAPConnectionPool) resBean.getAuthDataSource();

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPConnectionPool: {}", ldapPool);
            }

            if (!(ldapPool.isClosed()))
            {
                ldapConn = ldapPool.getConnection();

                if (DEBUG)
                {
                    DEBUGGER.debug("LDAPConnection: {}", ldapConn);
                }

                if (ldapConn.isConnected())
                {
                    Filter searchFilter = Filter.create("(&(objectClass=inetOrgPerson)" +
                            "(&("  + authData.getUserId() +  "=" + userId + "))" +
                            "(&("  + authData.getCommonName() +  "=" + userGuid + "))");

                    SearchRequest searchReq = new SearchRequest(
						    authRepo.getRepositoryUserBase(),
                            SearchScope.SUB,
                            searchFilter,
                            authData.getLockCount());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("searchFilter: {}", searchFilter);
                        DEBUGGER.debug("searchRequest: {}", searchReq);
                    }

                    SearchResult searchResult = ldapConn.search(searchReq);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("searchResult: {}", searchResult);
                    }

                    if ((searchResult.getResultCode() == ResultCode.SUCCESS) && (searchResult.getSearchEntries().size() == 1))
                    {
                        SearchResultEntry entry = searchResult.getSearchEntries().get(0);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("SearchResultEntry: {}", entry);
                        }

                        List<Modification> modifyList = new ArrayList<>
                        (
                            Arrays.asList
                            (
                                new Modification(ModificationType.REPLACE, authData.getLockCount(), "0")
                            )
                        );

                        if (DEBUG)
                        {
                            DEBUGGER.debug("modifyList: {}", modifyList);
                        }

                        LDAPResult ldapResult = ldapConn.modify(new ModifyRequest(entry.getDN(), modifyList));

                        if (DEBUG)
                        {
                            DEBUGGER.debug("LDAPResult: {}", ldapResult);
                        }

                        if (ldapResult.getResultCode() == ResultCode.SUCCESS)
                        {
                            isComplete = true;
                        }
                    }
                }
                else
                {
                    throw new ConnectException("Failed to create LDAP connection using the specified information");
                }
            }
            else
            {
                throw new ConnectException("Failed to create LDAP connection using the specified information");
            }
        }
        catch (LDAPException lx)
        {
            ERROR_RECORDER.error(lx.getMessage(), lx);

            throw new UserManagementException(lx.getMessage(), lx);
        }
        catch (ConnectException cx)
        {
            ERROR_RECORDER.error(cx.getMessage(), cx);

            throw new UserManagementException(cx.getMessage(), cx);
        }
        finally
        {
            if ((ldapPool != null) && ((ldapConn != null) && (ldapConn.isConnected())))
            {
                ldapPool.releaseConnection(ldapConn);
            }
        }

        return isComplete;
    }

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#changeUserPassword(java.lang.String, java.lang.String, java.lang.Long)
     */
    @Override
    public synchronized boolean changeUserPassword(final String userGuid, final String newPass, final Long expiry) throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#changeUserPassword(final String userGuid, final String newPass, final Long expiry) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("User GUID: {}", userGuid);
            DEBUGGER.debug("expiry: {}", expiry);
        }

        boolean isComplete = false;
        LDAPConnection ldapConn = null;
        LDAPConnectionPool ldapPool = null;

        try
        {
            ldapPool = (LDAPConnectionPool) resBean.getAuthDataSource();

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPConnectionPool: {}", ldapPool);
            }

            if (!(ldapPool.isClosed()))
            {
                ldapConn = ldapPool.getConnection();

                if (DEBUG)
                {
                    DEBUGGER.debug("LDAPConnection: {}", ldapConn);
                }

                if (ldapConn.isConnected())
                {
                    // need to get the userdn
                    Filter searchFilter = Filter.create("(&(objectClass=inetOrgPerson)" +
                                "(&(" + authData.getCommonName() + "=" + userGuid + ")))");

                    if (DEBUG)
                    {
                        DEBUGGER.debug("searchFilter: {}", searchFilter);
                    }

                    SearchRequest searchReq = new SearchRequest(
                        authRepo.getRepositoryUserBase(),
                        SearchScope.SUB,
                        searchFilter,
                        authData.getUserId(),
                        authData.getCommonName(),
                        authData.getGivenName(),
                        authData.getSurname(),
                        authData.getEmailAddr());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("searchRequest: {}", searchReq);
                    }

                    SearchResult searchResult = ldapConn.search(searchReq);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("searchResult: {}", searchResult);
                    }

                    if ((searchResult.getResultCode() == ResultCode.SUCCESS) && (searchResult.getEntryCount() == 1))
                    {
                        SearchResultEntry entry = searchResult.getSearchEntries().get(0);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("SearchResultEntry: {}", entry);
                        }

                        // perform the modification here
                        List<Modification> modifyList = new ArrayList<>(
                                Arrays.asList(
                                        new Modification(ModificationType.REPLACE, authData.getUserPassword(), newPass),
                                        new Modification(ModificationType.REPLACE, authData.getExpiryDate(), String.valueOf(expiry))));

                        LDAPResult ldapResult = ldapConn.modify(new ModifyRequest(entry.getDN(), modifyList));

                        if (DEBUG)
                        {
                            DEBUGGER.debug("LDAPResult: {}", ldapResult);
                        }

                        isComplete = (ldapResult.getResultCode() == ResultCode.SUCCESS);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("isComplete: {}", isComplete);
                        }
                    }
                    else
                    {
                        throw new LDAPException(ResultCode.NO_RESULTS_RETURNED, "Unable to locate provided user. Cannot continue.");
                    }
                }
                else
                {
                    throw new ConnectException("Failed to create LDAP connection using the specified information");
                }
            }
            else
            {
                throw new ConnectException("Failed to create LDAP connection using the specified information");
            }
        }
        catch (LDAPException lx)
        {
            ERROR_RECORDER.error(lx.getMessage(), lx);

            throw new UserManagementException(lx.getMessage(), lx);
        }
        catch (ConnectException cx)
        {
            ERROR_RECORDER.error(cx.getMessage(), cx);

            throw new UserManagementException(cx.getMessage(), cx);
        }
        finally
        {
            if ((ldapPool != null) && ((ldapConn != null) && (ldapConn.isConnected())))
            {
                ldapPool.releaseConnection(ldapConn);
            }
        }

        return isComplete;
    }

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#removeUserAccount(java.lang.String, java.lang.String)
     */
    @Override
    public synchronized boolean removeUserAccount(final String userId, final String userGuid) throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#removeUserAccount(final String userId, final String userGuid) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug(userId);
            DEBUGGER.debug(userGuid);
        }

        boolean isComplete = false;
        LDAPConnection ldapConn = null;
        LDAPConnectionPool ldapPool = null;

        try
        {
            ldapPool = (LDAPConnectionPool) resBean.getAuthDataSource();

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPConnectionPool: {}", ldapPool);
            }

            if (!(ldapPool.isClosed()))
            {
                ldapConn = ldapPool.getConnection();

                if (DEBUG)
                {
                    DEBUGGER.debug("LDAPConnection: {}", ldapConn);
                }

                if (ldapConn.isConnected())
                {
                    Filter searchFilter = Filter.create("(&(objectClass=inetOrgPerson)" +
                            "(&("  + authData.getUserId() +  "=" + userId + "))" +
                            "(&("  + authData.getCommonName() +  "=" + userGuid + "))");

                    SearchRequest searchReq = new SearchRequest(
						    authRepo.getRepositoryUserBase(),
                            SearchScope.SUB,
                            searchFilter,
                            authData.getCommonName());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("searchFilter: {}", searchFilter);
                        DEBUGGER.debug("searchRequest: {}", searchReq);
                    }

                    SearchResult searchResult = ldapConn.search(searchReq);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("searchResult: {}", searchResult);
                    }

                    if (searchResult.getResultCode() == ResultCode.SUCCESS)
                    {
                        if (searchResult.getSearchEntries().size() == 1)
                        {
                            SearchResultEntry entry = searchResult.getSearchEntries().get(0);

                            if (DEBUG)
                            {
                                DEBUGGER.debug("SearchResultEntry: {}", entry);
                            }

                            DeleteRequest deleteRequest = new DeleteRequest(entry.getDN());
                            deleteRequest.setDN(entry.getDN());

                            if (DEBUG)
                            {
                                DEBUGGER.debug("DeleteRequest: {}", deleteRequest);
                            }
                    
                            LDAPResult ldapResult = ldapConn.delete(deleteRequest);

                            if (DEBUG)
                            {
                                DEBUGGER.debug("LDAPResult: {}", ldapResult);
                            }

                            if (ldapResult.getResultCode() == ResultCode.SUCCESS)
                            {
                                isComplete = true;
                            }
                        }
                    }
                }
                else
                {
                    throw new ConnectException("Failed to create LDAP connection using the specified information");
                }
            }
            else
            {
                throw new ConnectException("Failed to create LDAP connection using the specified information");
            }
        }
        catch (LDAPException lx)
        {
            ERROR_RECORDER.error(lx.getMessage(), lx);

            throw new UserManagementException(lx.getMessage(), lx);
        }
        catch (ConnectException cx)
        {
            ERROR_RECORDER.error(cx.getMessage(), cx);

            throw new UserManagementException(cx.getMessage(), cx);
        }
        finally
        {
            if ((ldapPool != null) && ((ldapConn != null) && (ldapConn.isConnected())))
            {
                ldapPool.releaseConnection(ldapConn);
            }
        }

        return isComplete;
    }

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#searchUsers(com.cws.esolutions.security.dao.usermgmt.enums.SearchRequestType, java.lang.String)
     */
    @Override
    public synchronized List<String[]> searchUsers(final SearchRequestType searchType, final String searchData) throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#searchUsers(final SearchRequestType searchType, final String searchData) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("SearchRequestType: {}", searchType);
            DEBUGGER.debug("Search data: {}", searchData);
        }

        List<String[]> results = null;
        LDAPConnection ldapConn = null;
        LDAPConnectionPool ldapPool = null;

        try
        {
            ldapPool = (LDAPConnectionPool) resBean.getAuthDataSource();

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPConnectionPool: {}", ldapPool);
            }

            if (!(ldapPool.isClosed()))
            {
                ldapConn = ldapPool.getConnection();

                if (DEBUG)
                {
                    DEBUGGER.debug("LDAPConnection: {}", ldapConn);
                }

                if (ldapConn.isConnected())
                {
                    Filter searchFilter = null;

                    if (searchType != null)
                    {
                        switch (searchType)
                        {
                            case USERNAME:
                                searchFilter = Filter.create("(&(objectClass=inetOrgPerson)" +
                                        "(&(" + authData.getUserId() + "=" + searchData + ")))");

                                break;
                            case EMAILADDR:
                                searchFilter = Filter.create("(&(objectClass=inetOrgPerson)" +
                                        "(&(" + authData.getEmailAddr() + "=" + searchData + ")))");

                                break;
                            case GUID:
                                searchFilter = Filter.create("(&(objectClass=inetOrgPerson)" +
                                        "(&(" + authData.getCommonName() + "=" + searchData + ")))");

                                break;
                            case GIVENNAME:
                                searchFilter = Filter.create("(&(objectClass=inetOrgPerson)" +
                                        "(&(" + authData.getGivenName() + "=" + searchData + ")))");

                                break;
                            case SURNAME:
                                searchFilter = Filter.create("(&(objectClass=inetOrgPerson)" +
                                        "(&(" + authData.getSurname() + "=" + searchData + ")))");

                                break;
                            case DISPLAYNAME:
                                searchFilter = Filter.create("(&(objectClass=inetOrgPerson)" +
                                        "(&(" + authData.getDisplayName() + "=" + searchData + ")))");

                                break;
                            default:
                                throw new UserManagementException("No search type was provided.");
                        }

                        if (DEBUG)
                        {
                            DEBUGGER.debug("searchFilter: {}", searchFilter);
                        }
                    }
                    else
                    {
                        throw new UserManagementException("No valid search request type was provided. Cannot continue.");
                    }

                    SearchRequest searchReq = new SearchRequest(
                        authRepo.getRepositoryUserBase(),
                        SearchScope.SUB,
                        searchFilter,
                        authData.getCommonName(),
                        authData.getUserId());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("searchRequest: {}", searchReq);
                    }

                    SearchResult searchResult = ldapConn.search(searchReq);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("searchResult: {}", searchResult);
                    }

                    if (searchResult.getResultCode() == ResultCode.SUCCESS)
                    {
                        results = new ArrayList<>();

                        for (SearchResultEntry entry : searchResult.getSearchEntries())
                        {
                            String[] userData = new String[] {
                                    entry.getAttributeValue(authData.getCommonName()),
                                    entry.getAttributeValue(authData.getUserId())
                            };

                            if (DEBUG)
                            {
                                for (String str : userData)
                                {
                                    DEBUGGER.debug(str);
                                }
                            }

                            results.add(userData);
                        }
                    }
                    else
                    {
                        throw new UserManagementException("No users were located with the search data provided");
                    }
                }
                else
                {
                    throw new ConnectException("Failed to create LDAP connection using the specified information");
                }
            }
            else
            {
                throw new ConnectException("Failed to create LDAP connection using the specified information");
            }
        }
        catch (LDAPException lx)
        {
            ERROR_RECORDER.error(lx.getMessage(), lx);

            throw new UserManagementException(lx.getMessage(), lx);
        }
        catch (ConnectException cx)
        {
            ERROR_RECORDER.error(cx.getMessage(), cx);

            throw new UserManagementException(cx.getMessage(), cx);
        }
        finally
        {
            if ((ldapPool != null) && ((ldapConn != null) && (ldapConn.isConnected())))
            {
                ldapPool.releaseConnection(ldapConn);
            }
        }

        return results;
    }

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#loadUserAccount(java.lang.String)
     */
    @Override
    public synchronized List<Object> loadUserAccount(final String userGuid) throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#loadUserAccount(final String userGuid) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("userGuid: {}", userGuid);
        }

        LDAPConnection ldapConn = null;
        List<Object> userAccount = null;
        LDAPConnectionPool ldapPool = null;

        try
        {
            ldapPool = (LDAPConnectionPool) resBean.getAuthDataSource();

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPConnectionPool: {}", ldapPool);
            }

            if (!(ldapPool.isClosed()))
            {
                ldapConn = ldapPool.getConnection();

                if (DEBUG)
                {
                    DEBUGGER.debug("LDAPConnection: {}", ldapConn);
                }

                if (ldapConn.isConnected())
                {
                    Filter searchFilter = Filter.create("(&(objectClass=inetOrgPerson)" +
                            "(&(" + authData.getCommonName() + "=" + userGuid + ")))");

                    if (DEBUG)
                    {
                        DEBUGGER.debug("searchFilter: {}", searchFilter);
                    }

                    SearchRequest searchRequest = new SearchRequest(
                            authRepo.getRepositoryUserBase(),
                            SearchScope.SUB,
                            searchFilter,
                            authData.getCommonName(),
                            authData.getUserId(),
                            authData.getGivenName(),
                            authData.getSurname(),
                            authData.getDisplayName(),
                            authData.getEmailAddr(),
                            authData.getPagerNumber(),
                            authData.getTelephoneNumber(),
                            authData.getUserRole(),
                            authData.getLockCount(),
                            authData.getLastLogin(),
                            authData.getExpiryDate(),
                            authData.getIsSuspended(),
                            authData.getOlrSetupReq(),
                            authData.getOlrLocked(),
                            authData.getTcAccepted(),
                            authData.getUserType());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("searchRequest: {}", searchRequest);
                    }

                    SearchResult searchResult = ldapConn.search(searchRequest);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("searchResult: {}", searchResult);
                    }

                    if (searchResult.getResultCode() == ResultCode.SUCCESS)
                    {
                        if (searchResult.getEntryCount() == 1)
                        {
                            SearchResultEntry entry = searchResult.getSearchEntries().get(0);

                            if (DEBUG)
                            {
                                DEBUGGER.debug("SearchResultEntry: {}", entry);
                            }

                            // valid user, load the information
                            userAccount = new ArrayList<>();
                            userAccount.add(entry.getAttributeValue(authData.getCommonName()));
                            userAccount.add(entry.getAttributeValue(authData.getUserId()));
                            userAccount.add(entry.getAttributeValue(authData.getGivenName()));
                            userAccount.add(entry.getAttributeValue(authData.getSurname()));
                            userAccount.add(entry.getAttributeValue(authData.getDisplayName()));
                            userAccount.add(entry.getAttributeValue(authData.getEmailAddr()));
                            userAccount.add(entry.getAttributeValue(authData.getPagerNumber()));
                            userAccount.add(entry.getAttributeValue(authData.getTelephoneNumber()));
                            userAccount.add(entry.getAttributeValue(authData.getUserRole()).toUpperCase());
                            userAccount.add(entry.getAttributeValueAsInteger(authData.getLockCount()));
                            userAccount.add(entry.getAttributeValueAsLong(authData.getLastLogin()));
                            userAccount.add(entry.getAttributeValueAsLong(authData.getExpiryDate()));
                            userAccount.add(entry.getAttributeValueAsBoolean(authData.getIsSuspended()));
                            userAccount.add(entry.getAttributeValueAsBoolean(authData.getOlrSetupReq()));
                            userAccount.add(entry.getAttributeValueAsBoolean(authData.getOlrLocked()));
                            userAccount.add(entry.getAttributeValueAsBoolean(authData.getTcAccepted()));
                            userAccount.add(entry.getAttributeValue(authData.getUserType()).toUpperCase());

                            if (DEBUG)
                            {
                                DEBUGGER.debug("UserAccount: {}", userAccount);
                            }
                        }
                        else
                        {
                            throw new UserManagementException("Multiple users were located for the provided information");
                        }
                    }
                    else
                    {
                        throw new UserManagementException("Search request failed: " + searchResult.getResultCode());
                    }
                }
                else
                {
                    throw new ConnectException("Failed to create LDAP connection using the specified information");
                }
            }
            else
            {
                throw new ConnectException("Failed to create LDAP connection using the specified information");
            }
        }
        catch (LDAPException lx)
        {
            ERROR_RECORDER.error(lx.getMessage(), lx);

            throw new UserManagementException(lx.getMessage(), lx);
        }
        catch (ConnectException cx)
        {
            ERROR_RECORDER.error(cx.getMessage(), cx);

            throw new UserManagementException(cx.getMessage(), cx);
        }
        finally
        {
            if ((ldapPool != null) && ((ldapConn != null) && (ldapConn.isConnected())))
            {
                ldapPool.releaseConnection(ldapConn);
            }
        }

        return userAccount;
    }

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#listUserAccounts(java.lang.String)
     */
    @Override
    public synchronized List<String[]> listUserAccounts(final String userType) throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#listUserAccounts(final String userType) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userType);
        }

        List<String[]> results = null;
        LDAPConnection ldapConn = null;
        LDAPConnectionPool ldapPool = null;

        try
        {
            ldapPool = (LDAPConnectionPool) resBean.getAuthDataSource();

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPConnectionPool: {}", ldapPool);
            }

            if (!(ldapPool.isClosed()))
            {
                ldapConn = ldapPool.getConnection();

                if (DEBUG)
                {
                    DEBUGGER.debug("LDAPConnection: {}", ldapConn);
                }

                if (ldapConn.isConnected())
                {
                    Filter searchFilter = Filter.create("(&(objectClass=inetOrgPerson)" +
                            "(&(objectClass=" + authData.getObjectClass() + "))" +
                            "(&(" + authData.getUserType() + "=" + userType + ")))");

                    if (DEBUG)
                    {
                        DEBUGGER.debug("searchFilter: {}", searchFilter);
                    }

                    SearchRequest searchReq = new SearchRequest(
                        authRepo.getRepositoryUserBase(),
                        SearchScope.SUB,
                        searchFilter,
                        authData.getCommonName(),
                        authData.getUserId(),
                        authData.getDisplayName());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("searchRequest: {}", searchReq);
                    }

                    SearchResult searchResult = ldapConn.search(searchReq);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("searchResult: {}", searchResult);
                    }

                    if (searchResult.getResultCode() == ResultCode.SUCCESS)
                    {
                        results = new ArrayList<>();

                        for (SearchResultEntry entry : searchResult.getSearchEntries())
                        {
                            String[] userData = new String[] {
                                    entry.getAttributeValue(authData.getCommonName()),
                                    entry.getAttributeValue(authData.getUserId()),
                                    entry.getAttributeValue(authData.getDisplayName()),
                            };

                            if (DEBUG)
                            {
                                for (String str : userData)
                                {
                                    DEBUGGER.debug(str);
                                }
                            }

                            results.add(userData);
                        }
                    }
                    else
                    {
                        throw new ConnectException("No users were located with the search data provided");
                    }
                }
                else
                {
                    throw new ConnectException("Failed to create LDAP connection using the specified information");
                }
            }
            else
            {
                throw new ConnectException("Failed to create LDAP connection using the specified information");
            }
        }
        catch (LDAPException lx)
        {
            ERROR_RECORDER.error(lx.getMessage(), lx);

            throw new UserManagementException(lx.getMessage(), lx);
        }
        catch (ConnectException cx)
        {
            ERROR_RECORDER.error(cx.getMessage(), cx);

            throw new UserManagementException(cx.getMessage(), cx);
        }
        finally
        {
            if ((ldapPool != null) && ((ldapConn != null) && (ldapConn.isConnected())))
            {
                ldapPool.releaseConnection(ldapConn);
            }
        }

        return results;
    }
}
