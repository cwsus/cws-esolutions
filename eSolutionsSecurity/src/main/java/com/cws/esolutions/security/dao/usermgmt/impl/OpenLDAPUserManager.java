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
package com.cws.esolutions.security.dao.usermgmt.impl;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.dao.usermgmt.impl
 * File: OpenLDAPUserManager.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly           11/23/2008 22:39:20             Created.
 */
import java.util.Date;
import java.util.List;
import java.util.Arrays;
import java.util.Calendar;
import java.util.ArrayList;
import java.net.ConnectException;
import org.apache.commons.lang3.StringUtils;
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
public class OpenLDAPUserManager implements UserManager
{
    private static final String CNAME = OpenLDAPUserManager.class.getName();

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#validateUserAccount(java.lang.String, java.lang.String)
     */
    public synchronized boolean validateUserAccount(final String userId, final String userGuid) throws UserManagementException
    {
        final String methodName = OpenLDAPUserManager.CNAME + "#validateUserAccount(final String userID, final String userGuid) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userId);
            DEBUGGER.debug("Value: {}", userGuid);
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

            Filter searchFilter = Filter.create("(&(objectClass=" + repoConfig.getBaseObject() + ")" +
                    "(|(cn=" + userGuid + ")" +
                    "(uid=" + userId + ")))");

            if (DEBUG)
            {
                DEBUGGER.debug("Filter: {}", searchFilter);
            }

            SearchRequest searchRequest = new SearchRequest(
                    repoConfig.getRepositoryBaseDN(),
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
        catch (final LDAPException lx)
        {
            throw new UserManagementException(lx.getMessage(), lx);
        }
        catch (final ConnectException cx)
        {
            throw new UserManagementException(cx.getMessage(), cx);
        }
        finally
        {
            if ((ldapPool != null) && ((ldapConn != null) && (ldapConn.isConnected())))
            {
                ldapConn.close();
                ldapPool.releaseConnection(ldapConn);
            }
        }

        return isValid;
    }

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#addUserAccount(java.util.List, java.util.List)
     */
    public synchronized boolean addUserAccount(final List<Object> userAccount, final List<String> roles) throws UserManagementException
    {
        final String methodName = OpenLDAPUserManager.CNAME + "#addUserAccount(final List<String> userAccount, final List<String> roles) throws UserManagementException";

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
                .append(userAttributes.getUserId() + "=" + userAccount.get(0) + ",")
                .append(repoConfig.getRepositoryUserBase() + "," + repoConfig.getRepositoryBaseDN());

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
            List<Attribute> newAttributes = new ArrayList<Attribute>(
                Arrays.asList(
                    new Attribute("objectClass", repoConfig.getBaseObject()),
                    new Attribute(userAttributes.getCommonName(), (String) userAccount.get(0)),
                    new Attribute(userAttributes.getUserId(),  (String) userAccount.get(1)),
                    new Attribute(userAttributes.getEmailAddr(),  (String) userAccount.get(2)),
                    new Attribute(userAttributes.getGivenName(),  (String) userAccount.get(3)),
                    new Attribute(userAttributes.getSurname(),  (String) userAccount.get(4)),
                    new Attribute(userAttributes.getDisplayName(),  (String) userAccount.get(3) + " " +  (String) userAccount.get(4)),
                    new Attribute(securityAttributes.getIsSuspended(),  (String) userAccount.get(5)),
                    new Attribute(securityAttributes.getLockCount(), "0"),
                    new Attribute(securityAttributes.getExpiryDate(), new Date().toString())));

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
                        .append(repoConfig.getRepositoryRoleBase());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("StringBuilder: {}", roleDN);
                    }

                    AddRequest addToGroup = new AddRequest(roleDN.toString(),
                        new ArrayList<Attribute>(
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
                        throw new UserManagementException("Failed to add user to group: " +  role);
                    }
                }

                return true;
            }
        }
        catch (final ConnectException cx)
        {
            throw new UserManagementException(cx.getMessage(), cx);
        }
        catch (final LDAPException lx)
        {
            throw new UserManagementException(lx.getMessage(), lx);
        }
        finally
        {
            if ((ldapPool != null) && ((ldapConn != null) && (ldapConn.isConnected())))
            {
                ldapConn.close();
                ldapPool.releaseConnection(ldapConn);
            }
        }

        return isUserCreated;
    }

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#removeUserAccount(java.lang.String)
     */
    public synchronized boolean removeUserAccount(final String userId) throws UserManagementException
    {
        final String methodName = OpenLDAPUserManager.CNAME + "#removeUserAccount(final String userId) throws UserManagementException";

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
                .append(userAttributes.getUserId() + "=" + userId + ",")
                .append(repoConfig.getRepositoryUserBase() + "," + repoConfig.getRepositoryBaseDN()).toString());

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
        catch (final LDAPException lx)
        {
            throw new UserManagementException(lx.getMessage(), lx);
        }
        catch (final ConnectException cx)
        {
            throw new UserManagementException(cx.getMessage(), cx);
        }
        finally
        {
            if ((ldapPool != null) && ((ldapConn != null) && (ldapConn.isConnected())))
            {
                ldapConn.close();
                ldapPool.releaseConnection(ldapConn);
            }
        }

        return isComplete;
    }

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#searchUsers(java.lang.String)
     */
    public synchronized List<String[]> searchUsers(final String searchData) throws UserManagementException
    {
        final String methodName = OpenLDAPUserManager.CNAME + "#searchUsers(final String searchData) throws UserManagementException";

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

            Filter searchFilter = Filter.create("(&(objectClass=" + repoConfig.getBaseObject() + ")" +
                "(|(" + userAttributes.getCommonName() + "=" + searchData +")" +
                "(" + userAttributes.getUserId() + "=" + searchData +")" +
                "(" + userAttributes.getEmailAddr() + "=" + searchData +")" +
                "(" + userAttributes.getGivenName() + "=" + searchData +")" +
                "(" + userAttributes.getSurname() + "=" + searchData +")" +
                "(" + userAttributes.getDisplayName() + "=" + searchData +")))");

            if (DEBUG)
            {
                DEBUGGER.debug("searchFilter: {}", searchFilter);
            }

            SearchRequest searchReq = new SearchRequest(
                    repoConfig.getRepositoryUserBase() + "," + repoConfig.getRepositoryBaseDN(),
                    SearchScope.SUB,
                    searchFilter,
                    userAttributes.getCommonName(),
                    userAttributes.getUserId());

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
                results = new ArrayList<String[]>();

                for (SearchResultEntry entry : searchResult.getSearchEntries())
                {
                    String[] userData = new String[] {
                            entry.getAttributeValue(userAttributes.getCommonName()),
                            entry.getAttributeValue(userAttributes.getUserId())
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
        catch (final LDAPException lx)
        {
            throw new UserManagementException(lx.getMessage(), lx);
        }
        catch (final ConnectException cx)
        {
            throw new UserManagementException(cx.getMessage(), cx);
        }
        finally
        {
            if ((ldapPool != null) && ((ldapConn != null) && (ldapConn.isConnected())))
            {
                ldapConn.close();
                ldapPool.releaseConnection(ldapConn);
            }
        }

        return results;
    }

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#loadUserAccount(java.lang.String)
     */
    public synchronized List<Object> loadUserAccount(final String userGuid) throws UserManagementException
    {
        final String methodName = OpenLDAPUserManager.CNAME + "#loadUserAccount(final String userGuid) throws UserManagementException";

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

            Filter searchFilter = Filter.create("(&(objectClass=" + repoConfig.getBaseObject() + ")" +
                    "(&(" + userAttributes.getCommonName() + "=" + userGuid + ")))");

            if (DEBUG)
            {
                DEBUGGER.debug("searchFilter: {}", searchFilter);
            }

            SearchRequest searchRequest = new SearchRequest(
                    repoConfig.getRepositoryUserBase() + "," + repoConfig.getRepositoryBaseDN(),
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

                    userAccount = new ArrayList<Object>();

                    for (String returningAttribute : userAttributes.getReturningAttributes())
                    {
                        if (DEBUG)
                        {
                            DEBUGGER.debug("returningAttribute: {}", returningAttribute);
                        }

                        if (entry.hasAttribute(returningAttribute))
                        {
                            userAccount.add(entry.getAttributeValue(returningAttribute));
                        }
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("userAccount: {}", userAccount);
                    }

                    if (StringUtils.isNotBlank(userAttributes.getMemberOf()))
                    {
                        Filter roleFilter = Filter.create("(&(objectClass=groupOfUniqueNames)" +
                                "(&(uniqueMember=" + entry.getDN() + ")))");
    
                        if (DEBUG)
                        {
                            DEBUGGER.debug("SearchFilter: {}", roleFilter);
                        }
    
                        SearchRequest roleSearch = new SearchRequest(
                                repoConfig.getRepositoryRoleBase(),
                            SearchScope.SUB,
                            roleFilter,
                            userAttributes.getCommonName());
    
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
                            List<String> roles = new ArrayList<String>();
    
                            for (SearchResultEntry role : roleResult.getSearchEntries())
                            {
                                if (DEBUG)
                                {
                                    DEBUGGER.debug("SearchResultEntry: {}", role);
                                }
    
                                roles.add(role.getAttributeValue(userAttributes.getCommonName()));
                            }
    
                            userAccount.add(roles);
                        }
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
        catch (final LDAPException lx)
        {
            throw new UserManagementException(lx.getMessage(), lx);
        }
        catch (final ConnectException cx)
        {
            throw new UserManagementException(cx.getMessage(), cx);
        }
        finally
        {
            if ((ldapPool != null) && ((ldapConn != null) && (ldapConn.isConnected())))
            {
                ldapConn.close();
                ldapPool.releaseConnection(ldapConn);
            }
        }

        return userAccount;
    }

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#getUserByEmailAddress(java.lang.String)
     */
    public synchronized List<String[]> getUserByEmailAddress(final String value) throws UserManagementException
    {
        final String methodName = OpenLDAPUserManager.CNAME + "#getUserByEmailAddress(final String value) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        LDAPConnection ldapConn = null;
        List<String[]> userAccount = null;
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

            Filter searchFilter = Filter.create("(&(objectClass=" + repoConfig.getBaseObject() + ")" +
                    "(&(" + userAttributes.getEmailAddr() + "=" + value + ")))");

            if (DEBUG)
            {
                DEBUGGER.debug("searchFilter: {}", searchFilter);
            }

            SearchRequest searchRequest = new SearchRequest(
                    repoConfig.getRepositoryUserBase() + "," + repoConfig.getRepositoryBaseDN(),
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

                    String[] returnedData = new String[] { entry.getAttribute(userAttributes.getCommonName()).toString(),
                    		entry.getAttribute(userAttributes.getUserId()).toString(), entry.getAttribute(userAttributes.getEmailAddr()).toString() };

                    if (DEBUG)
                    {
                    	DEBUGGER.debug("returnedData {}", (Object[]) returnedData);

                    	for (String val : returnedData)
                    	{
                    		DEBUGGER.debug("Entry: {}", val);
                    	}
                    }

                    userAccount = new ArrayList<String[]>();
                    userAccount.add(returnedData);

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
        catch (final LDAPException lx)
        {
            throw new UserManagementException(lx.getMessage(), lx);
        }
        catch (final ConnectException cx)
        {
            throw new UserManagementException(cx.getMessage(), cx);
        }
        finally
        {
            if ((ldapPool != null) && ((ldapConn != null) && (ldapConn.isConnected())))
            {
                ldapConn.close();
                ldapPool.releaseConnection(ldapConn);
            }
        }

        return userAccount;
    }

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#getUserByEmailAddress(java.lang.String)
     */
    public synchronized String getUserByUsername(final String value) throws UserManagementException
    {
        final String methodName = OpenLDAPUserManager.CNAME + "#getUserByEmailAddress(final String value) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        String userAccount = null;
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

            Filter searchFilter = Filter.create("(&(objectClass=" + repoConfig.getBaseObject() + ")" +
                    "(&(" + userAttributes.getUserId() + "=" + value + ")))");

            if (DEBUG)
            {
                DEBUGGER.debug("searchFilter: {}", searchFilter);
            }

            SearchRequest searchRequest = new SearchRequest(
                    repoConfig.getRepositoryUserBase() + "," + repoConfig.getRepositoryBaseDN(),
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

                    userAccount = entry.getAttribute(userAttributes.getCommonName()).toString();

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
        catch (final LDAPException lx)
        {
            throw new UserManagementException(lx.getMessage(), lx);
        }
        catch (final ConnectException cx)
        {
            throw new UserManagementException(cx.getMessage(), cx);
        }
        finally
        {
            if ((ldapPool != null) && ((ldapConn != null) && (ldapConn.isConnected())))
            {
                ldapConn.close();
                ldapPool.releaseConnection(ldapConn);
            }
        }

        return userAccount;
    }

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#listUserAccounts()
     */
    public synchronized List<String[]> listUserAccounts() throws UserManagementException
    {
        final String methodName = OpenLDAPUserManager.CNAME + "#listUserAccounts() throws UserManagementException";

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
                    repoConfig.getRepositoryUserBase() + "," + repoConfig.getRepositoryBaseDN(),
                SearchScope.SUB,
                Filter.create("(&(objectClass=" + repoConfig.getBaseObject() + "))"));

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
                results = new ArrayList<String[]>();

                for (SearchResultEntry entry : searchResult.getSearchEntries())
                {
                    String[] userData = new String[] {
                            entry.getAttributeValue(userAttributes.getCommonName()),
                            entry.getAttributeValue(userAttributes.getUserId())
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
        catch (final LDAPException lx)
        {
            throw new UserManagementException(lx.getMessage(), lx);
        }
        catch (final ConnectException cx)
        {
            throw new UserManagementException(cx.getMessage(), cx);
        }
        finally
        {
            if ((ldapPool != null) && ((ldapConn != null) && (ldapConn.isConnected())))
            {
                ldapConn.close();
                ldapPool.releaseConnection(ldapConn);
            }
        }

        return results;
    }

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#modifyUserEmail(java.lang.String, java.lang.String)
     */
    public synchronized boolean modifyUserEmail(final String userId, final String value) throws UserManagementException
    {
        final String methodName = OpenLDAPUserManager.CNAME + "#modifyUserEmail(final String userId, final String value) throws UserManagementException";

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

            List<Modification> modifyList = new ArrayList<Modification>(
                    Arrays.asList(
                        new Modification(ModificationType.REPLACE, userAttributes.getEmailAddr(), value)));

            LDAPResult ldapResult = ldapConn.modify(new ModifyRequest(new StringBuilder()
                .append(userAttributes.getUserId() + "=" + userId + ",")
                .append(repoConfig.getRepositoryUserBase() + "," + repoConfig.getRepositoryBaseDN()).toString(), modifyList));

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
        catch (final LDAPException lx)
        {
            throw new UserManagementException(lx.getMessage(), lx);
        }
        catch (final ConnectException cx)
        {
            throw new UserManagementException(cx.getMessage(), cx);
        }
        finally
        {
            if ((ldapPool != null) && ((ldapConn != null) && (ldapConn.isConnected())))
            {
                ldapConn.close();
                ldapPool.releaseConnection(ldapConn);
            }
        }

        return isComplete;
    }

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#modifyUserContact(java.lang.String, java.util.List)
     */
    public synchronized boolean modifyUserContact(final String userId, final List<String> values) throws UserManagementException
    {
        final String methodName = OpenLDAPUserManager.CNAME + "#modifyUserContact(final String userId, final String value, final String attribute) throws UserManagementException";

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

            List<Modification> modifyList = new ArrayList<Modification>(
                    Arrays.asList(
                        new Modification(ModificationType.REPLACE, userAttributes.getTelephoneNumber(), values.get(0))));

            LDAPResult ldapResult = ldapConn.modify(new ModifyRequest(new StringBuilder()
                .append(userAttributes.getUserId() + "=" + userId + ",")
                .append(repoConfig.getRepositoryUserBase() + "," + repoConfig.getRepositoryBaseDN()).toString(), modifyList));

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
        catch (final LDAPException lx)
        {
            throw new UserManagementException(lx.getMessage(), lx);
        }
        catch (final ConnectException cx)
        {
            throw new UserManagementException(cx.getMessage(), cx);
        }
        finally
        {
            if ((ldapPool != null) && ((ldapConn != null) && (ldapConn.isConnected())))
            {
                ldapConn.close();
                ldapPool.releaseConnection(ldapConn);
            }
        }

        return isComplete;
    }
    
    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#modifyUserSuspension(java.lang.String, boolean)
     */
    public synchronized boolean modifyUserSuspension(final String userId, final boolean isSuspended) throws UserManagementException
    {
        final String methodName = OpenLDAPUserManager.CNAME + "#modifyUserSuspension(final String userDN, final boolean isSuspended) throws UserManagementException";

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

            List<Modification> modifyList = new ArrayList<Modification>
            (
                Arrays.asList
                (
                    new Modification(ModificationType.REPLACE, securityAttributes.getIsSuspended(), String.valueOf(isSuspended))
                )
            );

            if (DEBUG)
            {
                DEBUGGER.debug("modifyList: {}", modifyList);
            }

            LDAPResult ldapResult = ldapConn.modify(new ModifyRequest(new StringBuilder()
                .append(userAttributes.getUserId() + "=" + userId + ",")
                .append(repoConfig.getRepositoryUserBase() + "," + repoConfig.getRepositoryBaseDN()).toString(), modifyList));

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPResult: {}", ldapResult);
            }

            if (ldapResult.getResultCode() == ResultCode.SUCCESS)
            {
                isComplete = true;
            }
        }
        catch (final LDAPException lx)
        {
            throw new UserManagementException(lx.getMessage(), lx);
        }
        catch (final ConnectException cx)
        {
            throw new UserManagementException(cx.getMessage(), cx);
        }
        finally
        {
            if ((ldapPool != null) && ((ldapConn != null) && (ldapConn.isConnected())))
            {
                ldapConn.close();
                ldapPool.releaseConnection(ldapConn);
            }
        }

        return isComplete;
    }

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#modifyUserGroups(java.lang.String, java.lang.Object[])
     */
    public synchronized boolean modifyUserGroups(final String userId, final Object[] value) throws UserManagementException
    {
        final String methodName = OpenLDAPUserManager.CNAME + "#modifyUserGroups(final String userId, final Object[] value) throws UserManagementException";

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
            .append(userAttributes.getUserId() + "=" + userId + ",")
            .append(repoConfig.getRepositoryUserBase() + "," + repoConfig.getRepositoryBaseDN());

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
                    .append(userAttributes.getCommonName() + "=" + (String) group)
                    .append(repoConfig.getRepositoryRoleBase());

                if (DEBUG)
                {
                    DEBUGGER.debug("StringBuilder: {}", roleDN);
                }

                AddRequest addRequest = new AddRequest(roleDN.toString(),
                    new ArrayList<Attribute>(
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
        catch (final LDAPException lx)
        {
            throw new UserManagementException(lx.getMessage(), lx);
        }
        catch (final ConnectException cx)
        {
            throw new UserManagementException(cx.getMessage(), cx);
        }
        finally
        {
            if ((ldapPool != null) && ((ldapConn != null) && (ldapConn.isConnected())))
            {
                ldapConn.close();
                ldapPool.releaseConnection(ldapConn);
            }
        }

        return isComplete;
    }

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#modifyOlrLock(java.lang.String, boolean)
     */
    public synchronized boolean modifyOlrLock(final String userId, final boolean isLocked) throws UserManagementException
    {
        final String methodName = OpenLDAPUserManager.CNAME + "#modifyOlrLock(final String userId, final boolean isLocked) throws UserManagementException";

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

            List<Modification> modifyList = new ArrayList<Modification>(
                    Arrays.asList(
                        new Modification(ModificationType.REPLACE, securityAttributes.getOlrLocked(), String.valueOf(isLocked))));

            LDAPResult ldapResult = ldapConn.modify(new ModifyRequest(new StringBuilder()
                .append(userAttributes.getUserId() + "=" + userId + ",")
                .append(repoConfig.getRepositoryUserBase() + "," + repoConfig.getRepositoryBaseDN()).toString(), modifyList));

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
        catch (final LDAPException lx)
        {
            throw new UserManagementException(lx.getMessage(), lx);
        }
        catch (final ConnectException cx)
        {
            throw new UserManagementException(cx.getMessage(), cx);
        }
        finally
        {
            if ((ldapPool != null) && ((ldapConn != null) && (ldapConn.isConnected())))
            {
                ldapConn.close();
                ldapPool.releaseConnection(ldapConn);
            }
        }

        return isComplete;
    }

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#modifyUserLock(java.lang.String, boolean, int)
     */
    public synchronized boolean modifyUserLock(final String userId, final boolean isLocked, final int increment) throws UserManagementException
    {
        final String methodName = OpenLDAPUserManager.CNAME + "#modifyUserLock(final String userId, final boolean isLocked, final int increment) throws UserManagementException";

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

            List<Modification> modifyList = new ArrayList<Modification>();

            if (isLocked)
            {
                modifyList.add(new Modification(ModificationType.REPLACE, securityAttributes.getLockCount(), "3"));
            }
            else
            {
                modifyList.add(new Modification(ModificationType.REPLACE, securityAttributes.getLockCount(), String.valueOf(increment)));
            }

            LDAPResult ldapResult = ldapConn.modify(new ModifyRequest(new StringBuilder()
                .append(userAttributes.getUserId() + "=" + userId + ",")
                .append(repoConfig.getRepositoryUserBase() + "," + repoConfig.getRepositoryBaseDN()).toString(), modifyList));

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
        catch (final LDAPException lx)
        {
            throw new UserManagementException(lx.getMessage(), lx);
        }
        catch (final ConnectException cx)
        {
            throw new UserManagementException(cx.getMessage(), cx);
        }
        finally
        {
            if ((ldapPool != null) && ((ldapConn != null) && (ldapConn.isConnected())))
            {
                ldapConn.close();
                ldapPool.releaseConnection(ldapConn);
            }
        }

        return isComplete;
    }

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#modifyUserPassword(java.lang.String, java.lang.String)
     */
    public synchronized boolean modifyUserPassword(final String userId, final String newPass) throws UserManagementException
    {
        final String methodName = OpenLDAPUserManager.CNAME + "#modifyUserPassword(final String userId, final String newPass) throws UserManagementException";

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

            List<Modification> modifyList = new ArrayList<Modification>(
                Arrays.asList(
                    new Modification(ModificationType.REPLACE, securityAttributes.getUserPassword(), newPass),
                    new Modification(ModificationType.REPLACE, securityAttributes.getExpiryDate(), String.valueOf(cal.getTime()))));

            LDAPResult ldapResult = ldapConn.modify(new ModifyRequest(new StringBuilder()
                .append(userAttributes.getUserId() + "=" + userId + ",")
                .append(repoConfig.getRepositoryUserBase() + "," + repoConfig.getRepositoryBaseDN()).toString(), modifyList));

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
        catch (final LDAPException lx)
        {
            throw new UserManagementException(lx.getMessage(), lx);
        }
        catch (final ConnectException cx)
        {
            throw new UserManagementException(cx.getMessage(), cx);
        }
        finally
        {
            if ((ldapPool != null) && ((ldapConn != null) && (ldapConn.isConnected())))
            {
                ldapConn.close();
                ldapPool.releaseConnection(ldapConn);
            }
        }

        return isComplete;
    }

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#modifyOtpSecret(java.lang.String, boolean, java.lang.String)
     */
    public synchronized boolean modifyOtpSecret(final String userId, final boolean addSecret, final String secret) throws UserManagementException
    {
        final String methodName = OpenLDAPUserManager.CNAME + "#modifyOtpSecret(final String userId, final boolean addSecret, final String secret) throws UserManagementException";

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

            List<Modification> modifyList = new ArrayList<Modification>();

            if (addSecret)
            {
                modifyList.add(new Modification(ModificationType.REPLACE, securityAttributes.getSecret(), secret));
            }
            else
            {
                modifyList.add(new Modification(ModificationType.DELETE, securityAttributes.getSecret()));
            }

            if (DEBUG)
            {
                DEBUGGER.debug("List<Modification>: {}", modifyList);
            }

            LDAPResult ldapResult = ldapConn.modify(new ModifyRequest(new StringBuilder()
                .append(userAttributes.getUserId() + "=" + userId + ",")
                .append(repoConfig.getRepositoryUserBase() + "," + repoConfig.getRepositoryBaseDN()).toString(), modifyList));

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
        catch (final LDAPException lx)
        {
            throw new UserManagementException(lx.getMessage(), lx);
        }
        catch (final ConnectException cx)
        {
            throw new UserManagementException(cx.getMessage(), cx);
        }
        finally
        {
            if ((ldapPool != null) && ((ldapConn != null) && (ldapConn.isConnected())))
            {
                ldapConn.close();
                ldapPool.releaseConnection(ldapConn);
            }
        }

        return isComplete;
    }

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#modifyUserSecurity(java.lang.String, java.util.List)
     */
    public synchronized boolean modifyUserSecurity(final String userId, final List<String> values) throws UserManagementException
    {
        final String methodName = OpenLDAPUserManager.CNAME + "#modifyUserSecurity(final String userId, final Map<String, String> values) throws UserManagementException";

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

            List<Modification> modifyList = new ArrayList<Modification>(
                Arrays.asList(
                    new Modification(ModificationType.REPLACE, securityAttributes.getSecQuestionOne(), values.get(0)),
                    new Modification(ModificationType.REPLACE, securityAttributes.getSecQuestionTwo(), values.get(1)),
                    new Modification(ModificationType.REPLACE, securityAttributes.getSecAnswerOne(), values.get(2)),
                    new Modification(ModificationType.REPLACE, securityAttributes.getSecAnswerTwo(), values.get(3))));

            if (DEBUG)
            {
                DEBUGGER.debug("modifyList: {}", modifyList);
            }

            LDAPResult ldapResult = ldapConn.modify(new ModifyRequest(new StringBuilder()
                .append(userAttributes.getUserId() + "=" + userId + ",")
                .append(repoConfig.getRepositoryUserBase() + "," + repoConfig.getRepositoryBaseDN()).toString(), modifyList));

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPResult: {}", ldapResult);
            }

            if (ldapResult.getResultCode() == ResultCode.SUCCESS)
            {
                isComplete = true;
            }
        }
        catch (final LDAPException lx)
        {
            throw new UserManagementException(lx.getMessage(), lx);
        }
        catch (final ConnectException cx)
        {
            throw new UserManagementException(cx.getMessage(), cx);
        }
        finally
        {
            if ((ldapPool != null) && ((ldapConn != null) && (ldapConn.isConnected())))
            {
                ldapConn.close();
                ldapPool.releaseConnection(ldapConn);
            }
        }

        return isComplete;
    }

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#performSuccessfulLogin(java.lang.String, java.lang.String, java.lang.int, java.lang.Long)
     */
    public boolean performSuccessfulLogin(final String userId, final String guid, final int lockCount, final Long timestamp) throws UserManagementException
    {
        final String methodName = OpenLDAPUserManager.CNAME + "#performSuccessfulLogin(final String userId, final String guid, final int lockCount, final Long timestamp) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userId);
            DEBUGGER.debug("Value: {}", guid);
            DEBUGGER.debug("Value: {}", timestamp);
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

            List<Modification> modifyList = new ArrayList<Modification>(
                Arrays.asList(
                    new Modification(ModificationType.REPLACE, securityAttributes.getLastLogin(), String.valueOf(timestamp)),
                    new Modification(ModificationType.REPLACE, securityAttributes.getLockCount(), String.valueOf(0))));

            if (DEBUG)
            {
                DEBUGGER.debug("modifyList: {}", modifyList);
            }

            LDAPResult ldapResult = ldapConn.modify(new ModifyRequest(new StringBuilder()
                .append(userAttributes.getUserId() + "=" + userId + ",")
                .append(userAttributes.getUserGuid() + "=" + guid + ",")
                .append(repoConfig.getRepositoryUserBase() + "," + repoConfig.getRepositoryBaseDN()).toString(), modifyList));

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPResult: {}", ldapResult);
            }

            if (ldapResult.getResultCode() == ResultCode.SUCCESS)
            {
                isComplete = true;
            }
        }
        catch (final LDAPException lx)
        {
            throw new UserManagementException(lx.getMessage(), lx);
        }
        catch (final ConnectException cx)
        {
            throw new UserManagementException(cx.getMessage(), cx);
        }
        finally
        {
            if ((ldapPool != null) && ((ldapConn != null) && (ldapConn.isConnected())))
            {
                ldapConn.close();
                ldapPool.releaseConnection(ldapConn);
            }
        }

        return isComplete;
    }
}
