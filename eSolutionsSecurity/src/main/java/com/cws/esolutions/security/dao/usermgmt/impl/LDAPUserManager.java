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
package com.cws.esolutions.security.dao.usermgmt.impl;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.dao.usermgmt.impl
 * File: LDAPUserManager.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.Date;
import java.util.List;
import java.util.Arrays;
import java.util.Calendar;
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
    public synchronized boolean validateUserAccount(final String userId, final String userGuid) throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#validateUserAccount(final String userID, final String userGuid) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("userId: {}", userId);
            DEBUGGER.debug("userGuid: {}", userGuid);
        }

        boolean isValid = false;
        LDAPConnection ldapConn = null;
        LDAPConnectionPool ldapPool = null;

        try
        {
            ldapPool = (LDAPConnectionPool) svcBean.getAuthDataSource();

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPConnectionPool: {}", ldapPool);
            }

            if (ldapPool.isClosed())
            {
                throw new ConnectException("Failed to create LDAP connection using the specified information");
            }

            ldapConn = ldapPool.getConnection();

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPConnection: {}", ldapConn);
            }

            if (!(ldapConn.isConnected()))
            {
                throw new ConnectException("Failed to create LDAP connection using the specified information");
            }

            Filter searchFilter = Filter.create("(&(objectClass=" + authData.getBaseObject() + ")" +
                    "(|(cn=" + userGuid + ")" +
                    "(uid=" + userId + ")))");

            if (DEBUG)
            {
                DEBUGGER.debug("Filter: {}", searchFilter);
            }

            SearchRequest searchRequest = new SearchRequest(
                    authData.getRepositoryBaseDN(),
                    SearchScope.SUB,
                    searchFilter,
                    "cn");

            if (DEBUG)
            {
                DEBUGGER.debug("SearchRequest: {}", searchRequest);
            }

            SearchResult searchResult = ldapConn.search(searchRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("searchResult: {}", searchResult);
            }

            if ((searchResult.getResultCode() != ResultCode.SUCCESS) || (searchResult.getEntryCount() == 0))
            {
                return true;
            }
        }
        catch (LDAPException lx)
        {
            throw new UserManagementException(lx.getMessage(), lx);
        }
        catch (ConnectException cx)
        {
            throw new UserManagementException(cx.getMessage(), cx);
        }
        finally
        {
            if ((ldapPool != null) && ((ldapConn != null) && (ldapConn.isConnected())))
            {
                ldapPool.releaseConnection(ldapConn);
            }
        }

        return isValid;
    }

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#addUserAccount(java.util.Map, java.util.List)
     */
    @Override
    public synchronized boolean addUserAccount(final List<String> userAccount, final List<String> roles) throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#addUserAccount(final Map<String, String> userAccount, final List<String> roles) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userAccount);
            DEBUGGER.debug("Value: {}", roles);
        }

        boolean isUserCreated = false;
        LDAPConnection ldapConn = null;
        LDAPConnectionPool ldapPool = null;

        try
        {
            final StringBuilder userDN = new StringBuilder()
                .append("uid" + "=" + userAccount.get(0) + ",")
                .append(authData.getRepositoryUserBase());

            if (DEBUG)
            {
                DEBUGGER.debug("StringBuilder: {}", userDN);
            }

            ldapPool = (LDAPConnectionPool) svcBean.getAuthDataSource();

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPConnectionPool: {}", ldapPool);
            }

            if (ldapPool.isClosed())
            {
                throw new ConnectException("Failed to create LDAP connection using the specified information");
            }

            ldapConn = ldapPool.getConnection();

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPConnection: {}", ldapConn);
            }

            if (!(ldapConn.isConnected()))
            {
                throw new ConnectException("Failed to create LDAP connection using the specified information");
            }

            // have a connection, create the user
            List<Attribute> newAttributes = new ArrayList<>(
                Arrays.asList(
                    new Attribute("objectClass", authData.getBaseObject()),
                    new Attribute(authData.getCommonName(), userAccount.get(0)),
                    new Attribute(authData.getUserId(), userAccount.get(1)),
                    new Attribute(authData.getEmailAddr(), userAccount.get(2)),
                    new Attribute(authData.getGivenName(), userAccount.get(3)),
                    new Attribute(authData.getSurname(), userAccount.get(4)),
                    new Attribute(authData.getDisplayName(), userAccount.get(3) + " " + userAccount.get(4)),
                    new Attribute(authData.getIsSuspended(), userAccount.get(5)),
                    new Attribute(authData.getLockCount(), "0"),
                    new Attribute(authData.getExpiryDate(), new Date().toString())));

            if (DEBUG)
            {
                DEBUGGER.debug("List<Attribute>: {}", newAttributes);
            }

            AddRequest addRequest = new AddRequest(userDN.toString(), newAttributes);

            LDAPResult ldapResult = ldapConn.add(addRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPResult: {}", ldapResult);
            }

            if (ldapResult.getResultCode() == ResultCode.SUCCESS)
            {
                for (String role : roles)
                {
                    if (DEBUG)
                    {
                        DEBUGGER.debug("String: {}", role);
                    }

                    StringBuilder roleDN = new StringBuilder()
                        .append("cn=" + role)
                        .append(authData.getRepositoryRoleBase());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("StringBuilder: {}", roleDN);
                    }

                    AddRequest addToGroup = new AddRequest(roleDN.toString(),
                        new ArrayList<>(
                            Arrays.asList(
                                new Attribute("objectClass", "uniqueMember"),
                                new Attribute("uniqueMember", userDN.toString()))));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("AddRequest: {}", addToGroup);
                    }

                    LDAPResult addToGroupResult = ldapConn.add(addToGroup);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("LDAPResult: {}", addToGroupResult);
                    }

                    if (addToGroupResult.getResultCode() != ResultCode.SUCCESS)
                    {
                        ERROR_RECORDER.error("Failed to add user to group: {}", role);
                    }
                }

                return true;
            }
        }
        catch (ConnectException cx)
        {
            throw new UserManagementException(cx.getMessage(), cx);
        }
        catch (LDAPException lx)
        {
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
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#removeUserAccount(java.lang.String)
     */
    @Override
    public synchronized boolean removeUserAccount(final String userId) throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#removeUserAccount(final String userId) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug(userId);
        }

        boolean isComplete = false;
        LDAPConnection ldapConn = null;
        LDAPConnectionPool ldapPool = null;

        try
        {
            ldapPool = (LDAPConnectionPool) svcBean.getAuthDataSource();

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPConnectionPool: {}", ldapPool);
            }

            if (ldapPool.isClosed())
            {
                throw new ConnectException("Failed to create LDAP connection using the specified information");
            }

            ldapConn = ldapPool.getConnection();

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPConnection: {}", ldapConn);
            }

            if (!(ldapConn.isConnected()))
            {
                throw new ConnectException("Failed to create LDAP connection using the specified information");
            }

            DeleteRequest deleteRequest = new DeleteRequest(new StringBuilder()
                .append("uid" + "=" + userId + ",")
                .append(authData.getRepositoryUserBase()).toString());

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
        catch (LDAPException lx)
        {
            throw new UserManagementException(lx.getMessage(), lx);
        }
        catch (ConnectException cx)
        {
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
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#searchUsers(java.lang.String, java.lang.String)
     */
    @Override
    public synchronized List<String[]> searchUsers(final String searchData) throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#searchUsers(final String searchData) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", searchData);
        }

        List<String[]> results = null;
        LDAPConnection ldapConn = null;
        LDAPConnectionPool ldapPool = null;

        try
        {
            ldapPool = (LDAPConnectionPool) svcBean.getAuthDataSource();

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPConnectionPool: {}", ldapPool);
            }

            if (ldapPool.isClosed())
            {
                throw new ConnectException("Failed to create LDAP connection using the specified information");
            }

            ldapConn = ldapPool.getConnection();

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPConnection: {}", ldapConn);
            }

            if (!(ldapConn.isConnected()))
            {
                throw new ConnectException("Failed to create LDAP connection using the specified information");
            }
            Filter searchFilter = Filter.create("(&(objectClass=" + authData.getBaseObject() + ")" +
                "(|(" + authData.getCommonName() + "=" + searchData +")" +
                "(" + authData.getUserId() + "=" + searchData +")" +
                "(" + authData.getEmailAddr() + "=" + searchData +")" +
                "(" + authData.getGivenName() + "=" + searchData +")" +
                "(" + authData.getSurname() + "=" + searchData +")" +
                "(" + authData.getDisplayName() + "=" + searchData +")))");

            if (DEBUG)
            {
                DEBUGGER.debug("searchFilter: {}", searchFilter);
            }

            SearchRequest searchReq = new SearchRequest(
                authData.getRepositoryUserBase(),
                SearchScope.SUB,
                searchFilter,
                "cn",
                "uid");

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
                            entry.getAttributeValue("cn"),
                            entry.getAttributeValue("uid")
                    };

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Data: {}", (Object) userData);
                    }

                    results.add(userData);
                }
            }
            else
            {
                throw new UserManagementException("No users were located with the search data provided");
            }
        }
        catch (LDAPException lx)
        {
            throw new UserManagementException(lx.getMessage(), lx);
        }
        catch (ConnectException cx)
        {
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
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#loadUserAccount(java.lang.String, java.util.List)
     */
    @Override
    public synchronized List<Object> loadUserAccount(final String userGuid) throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#loadUserAccount(final String userGuid) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userGuid);
        }

        LDAPConnection ldapConn = null;
        List<Object> userAccount = null;
        LDAPConnectionPool ldapPool = null;

        try
        {
            ldapPool = (LDAPConnectionPool) svcBean.getAuthDataSource();

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPConnectionPool: {}", ldapPool);
            }

            if (ldapPool.isClosed())
            {
                throw new ConnectException("Failed to create LDAP connection using the specified information");
            }

            ldapConn = ldapPool.getConnection();

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPConnection: {}", ldapConn);
            }

            if (!(ldapConn.isConnected()))
            {
                throw new ConnectException("Failed to create LDAP connection using the specified information");
            }

            Filter searchFilter = Filter.create("(&(objectClass=" + authData.getBaseObject() + ")" +
                    "(&(cn=" + userGuid + ")))");

            if (DEBUG)
            {
                DEBUGGER.debug("searchFilter: {}", searchFilter);
            }

            SearchRequest searchRequest = new SearchRequest(
                    authData.getRepositoryUserBase(),
                    SearchScope.SUB,
                    searchFilter);

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
                    for (String attribute : authData.getEntries())
                    {
                        if (DEBUG)
                        {
                            DEBUGGER.debug("Attribute: {}", attribute);
                        }

                        userAccount.add(entry.getAttributeValue((attribute)));
                    }

                    Filter roleFilter = Filter.create("(&(objectClass=groupOfUniqueNames)" +
                            "(&(uniqueMember=" + entry.getDN() + ")))");

                    if (DEBUG)
                    {
                        DEBUGGER.debug("SearchFilter: {}", roleFilter);
                    }

                    SearchRequest roleSearch = new SearchRequest(
                        authData.getRepositoryRoleBase(),
                        SearchScope.SUB,
                        roleFilter,
                        "cn");

                    if (DEBUG)
                    {
                        DEBUGGER.debug("SearchRequest: {}", roleSearch);
                    }

                    SearchResult roleResult = ldapConn.search(roleSearch);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("searchResult: {}", roleResult);
                    }

                    if ((roleResult.getResultCode() == ResultCode.SUCCESS) && (roleResult.getEntryCount() != 0))
                    {
                        List<String> roles = new ArrayList<>();

                        for (SearchResultEntry role : roleResult.getSearchEntries())
                        {
                            if (DEBUG)
                            {
                                DEBUGGER.debug("SearchResultEntry: {}", role);
                            }

                            roles.add(role.getAttributeValue("cn"));
                        }

                        userAccount.add(roles);
                    }

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
        catch (LDAPException lx)
        {
            throw new UserManagementException(lx.getMessage(), lx);
        }
        catch (ConnectException cx)
        {
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
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#listUserAccounts()
     */
    @Override
    public synchronized List<String[]> listUserAccounts() throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#listUserAccounts() throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        List<String[]> results = null;
        LDAPConnection ldapConn = null;
        LDAPConnectionPool ldapPool = null;

        try
        {
            ldapPool = (LDAPConnectionPool) svcBean.getAuthDataSource();

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPConnectionPool: {}", ldapPool);
            }

            if (ldapPool.isClosed())
            {
                throw new ConnectException("Failed to create LDAP connection using the specified information");
            }

            ldapConn = ldapPool.getConnection();

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPConnection: {}", ldapConn);
            }

            if (!(ldapConn.isConnected()))
            {
                throw new ConnectException("Failed to create LDAP connection using the specified information");
            }

            SearchRequest searchReq = new SearchRequest(
                authData.getRepositoryUserBase(),
                SearchScope.SUB,
                Filter.create("(&(objectClass=" + authData.getBaseObject() + "))"));

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
                            entry.getAttributeValue("cn"),
                            entry.getAttributeValue("uid")
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
        catch (LDAPException lx)
        {
            throw new UserManagementException(lx.getMessage(), lx);
        }
        catch (ConnectException cx)
        {
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
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#modifyUserEmail(java.lang.String, java.lang.String)
     */
    @Override
    public synchronized boolean modifyUserEmail(final String userId, final String value) throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#modifyUserEmail(final String userId, final String value) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("userId: {}", userId);
            DEBUGGER.debug("userGuid: {}", value);
        }
        
        boolean isComplete = false;
        LDAPConnection ldapConn = null;
        LDAPConnectionPool ldapPool = null;

        try
        {
            ldapPool = (LDAPConnectionPool) svcBean.getAuthDataSource();

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPConnectionPool: {}", ldapPool);
            }

            if (ldapPool.isClosed())
            {
                throw new ConnectException("Failed to create LDAP connection using the specified information");
            }

            ldapConn = ldapPool.getConnection();

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPConnection: {}", ldapConn);
            }

            if (!(ldapConn.isConnected()))
            {
                throw new ConnectException("Failed to create LDAP connection using the specified information");
            }

            List<Modification> modifyList = new ArrayList<>(
                    Arrays.asList(
                        new Modification(ModificationType.REPLACE, authData.getEmailAddr(), value)));

            LDAPResult ldapResult = ldapConn.modify(new ModifyRequest(new StringBuilder()
                .append("uid" + "=" + userId + ",")
                .append(authData.getRepositoryUserBase()).toString(), modifyList));

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
        catch (LDAPException lx)
        {
            throw new UserManagementException(lx.getMessage(), lx);
        }
        catch (ConnectException cx)
        {
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
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#modifyUserContact(java.lang.String, java.util.String, java.lang.String)
     */
    @Override
    public synchronized boolean modifyUserContact(final String userId, final List<String> values) throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#modifyUserContact(final String userId, final String value, final String attribute) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userId);
            DEBUGGER.debug("Value: {}", values);
        }
        
        boolean isComplete = false;
        LDAPConnection ldapConn = null;
        LDAPConnectionPool ldapPool = null;

        try
        {
            ldapPool = (LDAPConnectionPool) svcBean.getAuthDataSource();

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPConnectionPool: {}", ldapPool);
            }

            if (ldapPool.isClosed())
            {
                throw new ConnectException("Failed to create LDAP connection using the specified information");
            }

            ldapConn = ldapPool.getConnection();

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPConnection: {}", ldapConn);
            }

            if (!(ldapConn.isConnected()))
            {
                throw new ConnectException("Failed to create LDAP connection using the specified information");
            }

            List<Modification> modifyList = new ArrayList<>(
                    Arrays.asList(
                        new Modification(ModificationType.REPLACE, authData.getTelephoneNumber(), values.get(0)),
                        new Modification(ModificationType.REPLACE, authData.getPagerNumber(), values.get(1))));

            LDAPResult ldapResult = ldapConn.modify(new ModifyRequest(new StringBuilder()
                .append("uid" + "=" + userId + ",")
                .append(authData.getRepositoryUserBase()).toString(), modifyList));

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
        catch (LDAPException lx)
        {
            throw new UserManagementException(lx.getMessage(), lx);
        }
        catch (ConnectException cx)
        {
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
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#modifyUserSuspension(java.lang.String, boolean, java.lang.String)
     */
    @Override
    public synchronized boolean modifyUserSuspension(final String userId, final boolean isSuspended) throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#modifyUserSuspension(final String userDN, final boolean isSuspended) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userId);
            DEBUGGER.debug("Value: {}", isSuspended);
        }

        boolean isComplete = false;
        LDAPConnection ldapConn = null;
        LDAPConnectionPool ldapPool = null;

        try
        {
            ldapPool = (LDAPConnectionPool) svcBean.getAuthDataSource();

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPConnectionPool: {}", ldapPool);
            }

            if (ldapPool.isClosed())
            {
                throw new ConnectException("Failed to create LDAP connection using the specified information");
            }

            ldapConn = ldapPool.getConnection();

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPConnection: {}", ldapConn);
            }

            if (!(ldapConn.isConnected()))
            {
                throw new ConnectException("Failed to create LDAP connection using the specified information");
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

            LDAPResult ldapResult = ldapConn.modify(new ModifyRequest(new StringBuilder()
                .append("uid" + "=" + userId + ",")
                .append(authData.getRepositoryUserBase()).toString(), modifyList));

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPResult: {}", ldapResult);
            }

            if (ldapResult.getResultCode() == ResultCode.SUCCESS)
            {
                isComplete = true;
            }
        }
        catch (LDAPException lx)
        {
            throw new UserManagementException(lx.getMessage(), lx);
        }
        catch (ConnectException cx)
        {
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
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#modifyUserGroups(java.lang.String, java.lang.Object[])
     */
    @Override
    public synchronized boolean modifyUserGroups(final String userId, final Object[] value) throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#modifyUserGroups(final String userId, final Object[] value) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("userId: {}", userId);
            DEBUGGER.debug("userGuid: {}", value);
        }

        boolean isComplete = false;
        LDAPConnection ldapConn = null;
        LDAPConnectionPool ldapPool = null;

        final StringBuilder userDN = new StringBuilder()
            .append("uid" + "=" + userId + ",")
            .append(authData.getRepositoryUserBase());

        try
        {
            ldapPool = (LDAPConnectionPool) svcBean.getAuthDataSource();

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPConnectionPool: {}", ldapPool);
            }

            if (ldapPool.isClosed())
            {
                throw new ConnectException("Failed to create LDAP connection using the specified information");
            }

            ldapConn = ldapPool.getConnection();

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPConnection: {}", ldapConn);
            }

            if (!(ldapConn.isConnected()))
            {
                throw new ConnectException("Failed to create LDAP connection using the specified information");
            }

            for (Object group : value)
            {
                if (DEBUG)
                {
                    DEBUGGER.debug("Group: {}", group);
                }

                StringBuilder roleDN = new StringBuilder()
                    .append("cn=" + (String) group)
                    .append(authData.getRepositoryRoleBase());

                if (DEBUG)
                {
                    DEBUGGER.debug("StringBuilder: {}", roleDN);
                }

                AddRequest addRequest = new AddRequest(roleDN.toString(),
                    new ArrayList<>(
                        Arrays.asList(
                            new Attribute("objectClass", "uniqueMember"),
                            new Attribute("uniqueMember", userDN.toString()))));

                if (DEBUG)
                {
                    DEBUGGER.debug("AddRequest: {}", addRequest);
                }

                LDAPResult ldapResult = ldapConn.add(addRequest);

                if (ldapResult.getResultCode() == ResultCode.SUCCESS)
                {
                    isComplete = true;
                }
            }
        }
        catch (LDAPException lx)
        {
            throw new UserManagementException(lx.getMessage(), lx);
        }
        catch (ConnectException cx)
        {
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
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#lockOnlineReset(java.lang.String, java.lang.String)
     */
    @Override
    public synchronized boolean modifyOlrLock(final String userId, final boolean isLocked) throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#modifyOlrLock(final String userId, final boolean isLocked) throws UserManagementException";

        if (DEBUG)
        {
            
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("userId: {}", userId);
            DEBUGGER.debug("Value: {}", isLocked);
        }
        
        boolean isComplete = false;
        LDAPConnection ldapConn = null;
        LDAPConnectionPool ldapPool = null;

        try
        {
            ldapPool = (LDAPConnectionPool) svcBean.getAuthDataSource();

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPConnectionPool: {}", ldapPool);
            }

            if (ldapPool.isClosed())
            {
                throw new ConnectException("Failed to create LDAP connection using the specified information");
            }

            ldapConn = ldapPool.getConnection();

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPConnection: {}", ldapConn);
            }

            if (!(ldapConn.isConnected()))
            {
                throw new ConnectException("Failed to create LDAP connection using the specified information");
            }

            List<Modification> modifyList = new ArrayList<>(
                    Arrays.asList(
                        new Modification(ModificationType.REPLACE, authData.getOlrLocked(), String.valueOf(isLocked))));

            LDAPResult ldapResult = ldapConn.modify(new ModifyRequest(new StringBuilder()
                .append("uid" + "=" + userId + ",")
                .append(authData.getRepositoryUserBase()).toString(), modifyList));

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
        catch (LDAPException lx)
        {
            throw new UserManagementException(lx.getMessage(), lx);
        }
        catch (ConnectException cx)
        {
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
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#clearLockCount(java.lang.String, java.lang.String)
     */
    @Override
    public synchronized boolean modifyUserLock(final String userId, final boolean isLocked, final int increment) throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#modifyUserLock(final String userId, final boolean isLocked, final int increment) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("userId: {}", userId);
            DEBUGGER.debug("Value: {}", isLocked);
            DEBUGGER.debug("Value: {}", increment);
        }
        
        boolean isComplete = false;
        LDAPConnection ldapConn = null;
        LDAPConnectionPool ldapPool = null;

        try
        {
            ldapPool = (LDAPConnectionPool) svcBean.getAuthDataSource();

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPConnectionPool: {}", ldapPool);
            }

            if (ldapPool.isClosed())
            {
                throw new ConnectException("Failed to create LDAP connection using the specified information");
            }

            ldapConn = ldapPool.getConnection();

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPConnection: {}", ldapConn);
            }

            if (!(ldapConn.isConnected()))
            {
                throw new ConnectException("Failed to create LDAP connection using the specified information");
            }

            List<Modification> modifyList = new ArrayList<>();

            if (isLocked)
            {
                modifyList.add(new Modification(ModificationType.REPLACE, authData.getLockCount(), "3"));
            }
            else
            {
                modifyList.add(new Modification(ModificationType.REPLACE, authData.getLockCount(), String.valueOf(increment)));
            }

            LDAPResult ldapResult = ldapConn.modify(new ModifyRequest(new StringBuilder()
                .append("uid" + "=" + userId + ",")
                .append(authData.getRepositoryUserBase()).toString(), modifyList));

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
        catch (LDAPException lx)
        {
            throw new UserManagementException(lx.getMessage(), lx);
        }
        catch (ConnectException cx)
        {
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
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#modifyUserPassword(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public synchronized boolean modifyUserPassword(final String userId, final String newPass) throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#modifyUserPassword(final String userId, final String newPass) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userId);
        }

        boolean isComplete = false;
        LDAPConnection ldapConn = null;
        LDAPConnectionPool ldapPool = null;

        try
        {
            ldapPool = (LDAPConnectionPool) svcBean.getAuthDataSource();

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPConnectionPool: {}", ldapPool);
            }

            if (ldapPool.isClosed())
            {
                throw new ConnectException("Failed to create LDAP connection using the specified information");
            }

            ldapConn = ldapPool.getConnection();

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPConnection: {}", ldapConn);
            }

            if (!(ldapConn.isConnected()))
            {
                throw new ConnectException("Failed to create LDAP connection using the specified information");
            }

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, secConfig.getPasswordExpiration());

            List<Modification> modifyList = new ArrayList<>(
                Arrays.asList(
                    new Modification(ModificationType.REPLACE, authData.getUserPassword(), newPass),
                    new Modification(ModificationType.REPLACE, authData.getExpiryDate(), String.valueOf(cal.getTime()))));

            LDAPResult ldapResult = ldapConn.modify(new ModifyRequest(new StringBuilder()
                .append("uid" + "=" + userId + ",")
                .append(authData.getRepositoryUserBase()).toString(), modifyList));

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
        catch (LDAPException lx)
        {
            throw new UserManagementException(lx.getMessage(), lx);
        }
        catch (ConnectException cx)
        {
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
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#modifyOtpSecret(java.lang.String, java.lang.String)
     */
    @Override
    public synchronized boolean modifyOtpSecret(final String userId, final boolean addSecret, final String secret) throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#modifyOtpSecret(final String userId, final boolean addSecret, final String secret) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userId);
            DEBUGGER.debug("Value: {}", addSecret);
        }

        boolean isComplete = false;
        LDAPConnection ldapConn = null;
        LDAPConnectionPool ldapPool = null;

        try
        {
            ldapPool = (LDAPConnectionPool) svcBean.getAuthDataSource();

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPConnectionPool: {}", ldapPool);
            }

            if (ldapPool.isClosed())
            {
                throw new ConnectException("Failed to create LDAP connection using the specified information");
            }

            ldapConn = ldapPool.getConnection();

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPConnection: {}", ldapConn);
            }

            if (!(ldapConn.isConnected()))
            {
                throw new ConnectException("Failed to create LDAP connection using the specified information");
            }

            List<Modification> modifyList = new ArrayList<>();

            if (addSecret)
            {
                modifyList.add(new Modification(ModificationType.REPLACE, authData.getSecret(), secret));
            }
            else
            {
                modifyList.add(new Modification(ModificationType.DELETE, authData.getSecret()));
            }

            if (DEBUG)
            {
                DEBUGGER.debug("List<Modification>: {}", modifyList);
            }

            LDAPResult ldapResult = ldapConn.modify(new ModifyRequest(new StringBuilder()
                .append("uid" + "=" + userId + ",")
                .append(authData.getRepositoryUserBase()).toString(), modifyList));

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
        catch (LDAPException lx)
        {
            throw new UserManagementException(lx.getMessage(), lx);
        }
        catch (ConnectException cx)
        {
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
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#modifyUserSecurity(java.lang.String, java.util.List)
     */
    @Override
    public synchronized boolean modifyUserSecurity(final String userId, final List<String> values) throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#modifyUserSecurity(final String userId, final Map<String, String> values) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userId);
        }

        boolean isComplete = false;
        LDAPConnection ldapConn = null;
        LDAPConnectionPool ldapPool = null;

        try
        {
            ldapPool = (LDAPConnectionPool) svcBean.getAuthDataSource();

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPConnectionPool: {}", ldapPool);
            }

            if (ldapPool.isClosed())
            {
                throw new ConnectException("Failed to create LDAP connection using the specified information");
            }

            ldapConn = ldapPool.getConnection();

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPConnection: {}", ldapConn);
            }

            if (!(ldapConn.isConnected()))
            {
                throw new ConnectException("Failed to create LDAP connection using the specified information");
            }

            List<Modification> modifyList = new ArrayList<>(
                Arrays.asList(
                    new Modification(ModificationType.REPLACE, authData.getSecQuestionOne(), values.get(0)),
                    new Modification(ModificationType.REPLACE, authData.getSecQuestionTwo(), values.get(1)),
                    new Modification(ModificationType.REPLACE, authData.getSecAnswerOne(), values.get(2)),
                    new Modification(ModificationType.REPLACE, authData.getSecAnswerTwo(), values.get(3))));

            if (DEBUG)
            {
                DEBUGGER.debug("modifyList: {}", modifyList);
            }

            LDAPResult ldapResult = ldapConn.modify(new ModifyRequest(new StringBuilder()
                .append("uid" + "=" + userId + ",")
                .append(authData.getRepositoryUserBase()).toString(), modifyList));

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPResult: {}", ldapResult);
            }

            if (ldapResult.getResultCode() == ResultCode.SUCCESS)
            {
                isComplete = true;
            }
        }
        catch (LDAPException lx)
        {
            throw new UserManagementException(lx.getMessage(), lx);
        }
        catch (ConnectException cx)
        {
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
}
