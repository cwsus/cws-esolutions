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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Arrays;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.io.FileInputStream;
import java.net.ConnectException;
import java.io.FileNotFoundException;

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
import com.cws.esolutions.security.SecurityServiceConstants;
import com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager;
import com.cws.esolutions.security.dao.usermgmt.enums.SearchRequestType;
import com.cws.esolutions.security.dao.usermgmt.exception.UserManagementException;
/**
 * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager
 */
public class LDAPUserManager implements UserManager
{
    private Properties connProps = null;

    private static final String CNAME = LDAPUserManager.class.getName();

    public LDAPUserManager() throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#LDAPUserManager()#Constructor throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        try
        {
            this.connProps = new Properties();
            this.connProps.load(new FileInputStream(secConfig.getAuthConfig()));

            if (DEBUG)
            {
                DEBUGGER.debug("Properties: {}", this.connProps);
            }
        }
        catch (FileNotFoundException fnfx)
        {
            ERROR_RECORDER.error(fnfx.getMessage(), fnfx);

            throw new UserManagementException(fnfx.getMessage(), fnfx);
        }
        catch (IOException iox)
        {
            ERROR_RECORDER.error(iox.getMessage(), iox);

            throw new UserManagementException(iox.getMessage(), iox);
        }
    }

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

            searchFilter = Filter.create("(&(objectClass=" + authData.getObjectClass() + ")" +
                    "(&(" + authData.getCommonName() + "=" + userGuid + ")))");

            if (DEBUG)
            {
                DEBUGGER.debug("searchFilter: {}", searchFilter);
            }

            searchRequest = new SearchRequest(
                    this.connProps.getProperty(SecurityServiceConstants.BASE_DN),
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
                searchFilter = Filter.create("(&(objectClass=" + authData.getObjectClass() + ")" +
                        "(&(" + authData.getUserId() + "=" + userId + ")))");

                if (DEBUG)
                {
                    DEBUGGER.debug("searchFilter: {}", searchFilter);
                }

                searchRequest = new SearchRequest(
                        this.connProps.getProperty(SecurityServiceConstants.BASE_DN),
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
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#addUserAccount(java.lang.List, java.util.List)
     */
    @Override
    public synchronized boolean addUserAccount(final List<String> createRequest, final List<String> roles) throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#addUserAccount(final List<String> createRequest, final List<String> roles) throws UserManagementException";

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
            final StringBuilder userDN = new StringBuilder()
                .append(authData.getUserId() + "=" + createRequest.get(0) + ",")
                .append(this.connProps.getProperty(SecurityServiceConstants.USER_BASE));

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
            List<Attribute> newAttributes = new ArrayList<>();
            newAttributes.add(new Attribute("objectClass", authData.getObjectClass()));
            newAttributes.add(new Attribute(authData.getUserId(), createRequest.get(0)));
            newAttributes.add(new Attribute(authData.getUserPassword(), createRequest.get(1)));
            newAttributes.add(new Attribute(authData.getSurname(), createRequest.get(3)));
            newAttributes.add(new Attribute(authData.getGivenName(), createRequest.get(4)));
            newAttributes.add(new Attribute(authData.getEmailAddr(), createRequest.get(5)));
            newAttributes.add(new Attribute(authData.getCommonName(), createRequest.get(6)));
            newAttributes.add(new Attribute(authData.getDisplayName(), createRequest.get(7)));

            AddRequest addRequest = new AddRequest(userDN.toString(), newAttributes);

            ldapResult = ldapConn.add(addRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPResult: {}", ldapResult);
            }

            if (ldapResult.getResultCode() == ResultCode.SUCCESS)
            {
                isUserCreated = true;
            }

            // add user to groups now
            for (String role : roles)
            {
                if (DEBUG)
                {
                    DEBUGGER.debug("String: {}", role);
                }

                StringBuilder roleDN = new StringBuilder()
                    .append("cn=" + role)
                    .append(this.connProps.getProperty(SecurityServiceConstants.ROLE_BASE));

                if (DEBUG)
                {
                    DEBUGGER.debug("StringBuilder: {}", roleDN);
                }

                addRequest = new AddRequest(roleDN.toString(),
                    new ArrayList<>(
                        Arrays.asList(
                            new Attribute("objectClass", "uniqueMember"),
                            new Attribute("uniqueMember", userDN.toString()))));

                if (DEBUG)
                {
                    DEBUGGER.debug("AddRequest: {}", addRequest);
                }

                ldapResult = ldapConn.add(addRequest);

                if (ldapResult.getResultCode() != ResultCode.SUCCESS)
                {
                    ERROR_RECORDER.error("Failed to add user to group: {}", role);
                }
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
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#modifyUserSuspension(java.lang.String, boolean)
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
                .append(authData.getUserId() + "=" + userId + ",")
                .append(this.connProps.getProperty(SecurityServiceConstants.USER_BASE)).toString(), modifyList));

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
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#unlockUserAccount(java.lang.String, java.lang.String, java.lang.Boolean)
     */
    @Override
    public synchronized void lockUserAccount(final String userId) throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#lockUserAccount(final String userId) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userId);
        }

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
                    new Modification(ModificationType.REPLACE, authData.getLockCount(), "0")
                )
            );

            if (DEBUG)
            {
                DEBUGGER.debug("modifyList: {}", modifyList);
            }

            LDAPResult ldapResult = ldapConn.modify(new ModifyRequest(new StringBuilder()
                .append(authData.getUserId() + "=" + userId + ",")
                .append(this.connProps.getProperty(SecurityServiceConstants.USER_BASE)).toString(), modifyList));

            if (DEBUG)
            {
                DEBUGGER.debug("LDAPResult: {}", ldapResult);
            }

            if (ldapResult.getResultCode() != ResultCode.SUCCESS)
            {
                ERROR_RECORDER.error("Failed to modify the requested lock count");
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
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#unlockUserAccount(java.lang.String)
     */
    @Override
    public synchronized boolean unlockUserAccount(final String userId) throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#modifyUserSuspension(final String userId) throws UserManagementException";

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
                    new Modification(ModificationType.REPLACE, authData.getLockCount(), "0")));

            if (DEBUG)
            {
                DEBUGGER.debug("modifyList: {}", modifyList);
            }

            LDAPResult ldapResult = ldapConn.modify(new ModifyRequest(new StringBuilder()
                .append(authData.getUserId() + "=" + userId + ",")
                .append(this.connProps.getProperty(SecurityServiceConstants.USER_BASE)).toString(), modifyList));

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
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#changeUserPassword(java.lang.String, java.lang.String, java.lang.int)
     */
    @Override
    public synchronized boolean changeUserPassword(final String userId, final String newPass) throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#changeUserPassword(final String userId, final String newPass) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("User GUID: {}", userId);
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
                .append(authData.getUserId() + "=" + userId + ",")
                .append(this.connProps.getProperty(SecurityServiceConstants.USER_BASE)).toString(), modifyList));

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
                .append(authData.getUserId() + "=" + userId + ",")
                .append(this.connProps.getProperty(SecurityServiceConstants.USER_BASE)).toString());

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
    public synchronized List<Object[]> searchUsers(final SearchRequestType searchType, final String searchData) throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#searchUsers(final SearchRequestType searchType, final String searchData) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("SearchRequestType: {}", searchType);
            DEBUGGER.debug("Search data: {}", searchData);
        }

        List<Object[]> results = null;
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
            Filter searchFilter = null;

            if (searchType != null)
            {
                switch (searchType)
                {
                    case USERNAME:
                        searchFilter = Filter.create("(&(objectClass=" + authData.getObjectClass() + ")" +
                                "(&(" + authData.getUserId() + "=" + searchData + ")))");

                        break;
                    case EMAILADDR:
                        searchFilter = Filter.create("(&(objectClass=" + authData.getObjectClass() + ")" +
                                "(&(" + authData.getEmailAddr() + "=" + searchData + ")))");

                        break;
                    case GUID:
                        searchFilter = Filter.create("(&(objectClass=" + authData.getObjectClass() + ")" +
                                "(&(" + authData.getCommonName() + "=" + searchData + ")))");

                        break;
                    case GIVENNAME:
                        searchFilter = Filter.create("(&(objectClass=" + authData.getObjectClass() + ")" +
                                "(&(" + authData.getGivenName() + "=" + searchData + ")))");

                        break;
                    case SURNAME:
                        searchFilter = Filter.create("(&(objectClass=" + authData.getObjectClass() + ")" +
                                "(&(" + authData.getSurname() + "=" + searchData + ")))");

                        break;
                    case DISPLAYNAME:
                        searchFilter = Filter.create("(&(objectClass=" + authData.getObjectClass() + ")" +
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
                this.connProps.getProperty(SecurityServiceConstants.USER_BASE),
                SearchScope.SUB,
                searchFilter,
                authData.getCommonName(),
                authData.getUserId(),
                authData.getLockCount());

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
                results = new ArrayList<Object[]>();

                for (SearchResultEntry entry : searchResult.getSearchEntries())
                {
                    Object[] userData = new Object[] {
                            entry.getAttributeValue(authData.getCommonName()),
                            entry.getAttributeValue(authData.getUserId()),
                            entry.getAttributeValueAsInteger(authData.getLockCount())
                    };

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Data: {}", userData);
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

            Filter searchFilter = Filter.create("(&(objectClass=" + authData.getObjectClass() + ")" +
                    "(&(" + authData.getCommonName() + "=" + userGuid + ")))");

            if (DEBUG)
            {
                DEBUGGER.debug("searchFilter: {}", searchFilter);
            }

            SearchRequest searchRequest = new SearchRequest(
                    this.connProps.getProperty(SecurityServiceConstants.USER_BASE),
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
                    authData.getLastLogin(),
                    authData.getExpiryDate(),
                    authData.getIsSuspended(),
                    authData.getOlrSetupReq(),
                    authData.getOlrLocked());

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
                    userAccount.add(entry.getAttributeValueAsInteger(authData.getLockCount()));
                    userAccount.add(entry.getAttributeValueAsLong(authData.getLastLogin()));
                    userAccount.add(entry.getAttributeValueAsLong(authData.getExpiryDate()));
                    userAccount.add(entry.getAttributeValueAsBoolean(authData.getIsSuspended()));
                    userAccount.add(entry.getAttributeValueAsBoolean(authData.getOlrSetupReq()));
                    userAccount.add(entry.getAttributeValueAsBoolean(authData.getOlrLocked()));

                    Filter roleFilter = Filter.create("(&(objectClass=groupOfUniqueNames)" +
                            "(&(uniqueMember=" + entry.getDN() + ")))");

                    if (DEBUG)
                    {
                        DEBUGGER.debug("SearchFilter: {}", roleFilter);
                    }

                    SearchRequest roleSearch = new SearchRequest(
                        this.connProps.getProperty(SecurityServiceConstants.ROLE_BASE),
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

            Filter searchFilter = Filter.create("(&(objectClass=" + authData.getObjectClass() + ")" +
                    "(&(objectClass=" + authData.getObjectClass() + ")))");

            if (DEBUG)
            {
                DEBUGGER.debug("searchFilter: {}", searchFilter);
            }

            SearchRequest searchReq = new SearchRequest(
                this.connProps.getProperty(SecurityServiceConstants.USER_BASE),
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
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#modifyUserEmail(java.lang.String, java.lang.String)
     */
    @Override
    public boolean modifyUserEmail(final String userId, final String value) throws UserManagementException
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
                .append(authData.getUserId() + "=" + userId + ",")
                .append(this.connProps.getProperty(SecurityServiceConstants.USER_BASE)).toString(), modifyList));

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
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#modifyUserContact(java.lang.String, java.util.List)
     */
    @Override
    public boolean modifyUserContact(final String userId, final List<String> value) throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#modifyUserContact(final String userId, final List<String> value) throws UserManagementException";

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
                            new Modification(ModificationType.REPLACE, authData.getTelephoneNumber(), value.get(0)),
                            new Modification(ModificationType.REPLACE, authData.getPagerNumber(), value.get(1))));

            LDAPResult ldapResult = ldapConn.modify(new ModifyRequest(new StringBuilder()
                .append(authData.getUserId() + "=" + userId + ",")
                .append(this.connProps.getProperty(SecurityServiceConstants.USER_BASE)).toString(), modifyList));

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
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#modifyUserRole(java.lang.String, java.util.List)
     */
    @Override
    public boolean modifyUserRole(final String userId, final Object[] value) throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#modifyUserRole(final String userId, final Object[] value) throws UserManagementException";

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
            .append(authData.getUserId() + "=" + userId + ",")
            .append(this.connProps.getProperty(SecurityServiceConstants.USER_BASE));

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
                    .append(this.connProps.getProperty(SecurityServiceConstants.ROLE_BASE));

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
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#lockOnlineReset(java.lang.String, java.lang.String)
     */
    @Override
    public boolean lockOnlineReset(final String userId) throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#lockOnlineReset(final String userId) throws UserManagementException";

        if (DEBUG)
        {
            
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("userId: {}", userId);
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
                            new Modification(ModificationType.REPLACE, authData.getOlrLocked(), String.valueOf(true))));

            LDAPResult ldapResult = ldapConn.modify(new ModifyRequest(new StringBuilder()
                .append(authData.getUserId() + "=" + userId + ",")
                .append(this.connProps.getProperty(SecurityServiceConstants.USER_BASE)).toString(), modifyList));

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
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#clearLockCount(java.lang.String)
     */
    @Override
    public boolean clearLockCount(final String userId) throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#clearLockCount(final String userId) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("userId: {}", userId);
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
                            new Modification(ModificationType.REPLACE, authData.getLockCount(), "0"),
                            new Modification(ModificationType.REPLACE, authData.getLastLogin(), String.valueOf(new Date()))));

            LDAPResult ldapResult = ldapConn.modify(new ModifyRequest(new StringBuilder()
                .append(authData.getUserId() + "=" + userId + ",")
                .append(this.connProps.getProperty(SecurityServiceConstants.USER_BASE)).toString(), modifyList));

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
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#changeUserSecurity(java.lang.String, java.util.List)
     */
    @Override
    public boolean changeUserSecurity(final String userId, final List<String> values) throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#changeUserSecurity(final String userId, final List<String> values) throws UserManagementException";

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
                    new Modification(ModificationType.REPLACE, authData.getSecQuestionOne(), values.get(1)),
                    new Modification(ModificationType.REPLACE, authData.getSecAnswerOne(), values.get(2)),
                    new Modification(ModificationType.REPLACE, authData.getSecAnswerOne(), values.get(3))));

            if (DEBUG)
            {
                DEBUGGER.debug("modifyList: {}", modifyList);
            }

            LDAPResult ldapResult = ldapConn.modify(new ModifyRequest(new StringBuilder()
                .append(authData.getUserId() + "=" + userId + ",")
                .append(this.connProps.getProperty(SecurityServiceConstants.USER_BASE)).toString(), modifyList));

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
}
