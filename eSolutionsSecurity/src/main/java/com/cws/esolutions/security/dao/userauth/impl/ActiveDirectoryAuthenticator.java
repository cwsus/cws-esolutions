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
package com.cws.esolutions.security.dao.userauth.impl;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.dao.userauth.impl
 * File: OpenLDAPAuthenticator.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.net.ConnectException;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.BindRequest;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SimpleBindRequest;
import com.unboundid.ldap.sdk.LDAPConnectionPool;

import com.cws.esolutions.security.dao.userauth.interfaces.Authenticator;
import com.cws.esolutions.security.dao.userauth.exception.AuthenticatorException;
/**
 * @see com.cws.esolutions.security.dao.userauth.interfaces.Authenticator
 */
public class ActiveDirectoryAuthenticator implements Authenticator
{
    private static final String CNAME = ActiveDirectoryAuthenticator.class.getName();

    /**
     * @see com.cws.esolutions.security.dao.userauth.interfaces.Authenticator#performLogon(java.lang.String, java.lang.String)
     */
    public synchronized boolean performLogon(final String userGuid, final String username, final String password) throws AuthenticatorException
    {
        final String methodName = ActiveDirectoryAuthenticator.CNAME + "#performLogon(final String userGuid, final String username, final String password) throws AuthenticatorException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("String: {}", userGuid);
            DEBUGGER.debug("String: {}", username);
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
                "(&(" + userAttributes.getUserId() + "=" + username + ")))");

            if (DEBUG)
            {
                DEBUGGER.debug("SearchFilter: {}", searchFilter);
            }

            SearchRequest searchRequest = new SearchRequest(
                repoConfig.getRepositoryUserBase() + "," + repoConfig.getRepositoryBaseDN(),
                SearchScope.SUB,
                searchFilter);

            if (DEBUG)
            {
                DEBUGGER.debug("SearchRequest: {}", searchRequest);
            }

            SearchResult searchResult = ldapConn.search(searchRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("SearchResult: {}", searchResult);
            }

            if ((searchResult.getResultCode() != ResultCode.SUCCESS) || (searchResult.getEntryCount() != 1))
            {
                throw new AuthenticatorException("No user was found for the provided user information");
            }

            SearchResultEntry entry = searchResult.getSearchEntries().get(0);

            if (DEBUG)
            {
                DEBUGGER.debug("SearchResultEntry: {}", entry);
            }

            BindRequest bindRequest = new SimpleBindRequest(entry.getDN(), password);

            if (DEBUG)
            {
                DEBUGGER.debug("BindRequest: {}", bindRequest);
            }

            isValid = true;
        }
        catch (LDAPException lx)
        {
            throw new AuthenticatorException(lx.getResultCode(), lx.getMessage(), lx);
        }
        catch (ConnectException cx)
        {
            throw new AuthenticatorException(cx.getMessage(), cx);
        }
        finally
        {
            if ((ldapPool != null) && (!(ldapPool.isClosed())))
            {
                ldapConn.close();
                ldapPool.releaseConnection(ldapConn);
            }
        }

        return isValid;
    }

    /**
     * @see com.cws.esolutions.security.dao.userauth.interfaces.Authenticator#obtainSecurityData(java.lang.String, java.lang.String)
     */
    public synchronized List<String> obtainSecurityData(final String userId, final String userGuid) throws AuthenticatorException
    {
        final String methodName = ActiveDirectoryAuthenticator.CNAME + "#obtainSecurityData(final String userId, final String userGuid) throws AuthenticatorException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userId);
            DEBUGGER.debug("Value: {}", userGuid);
        }

        LDAPConnection ldapConn = null;
        List<String> userSecurity = null;
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
                "(&(" + userAttributes.getCommonName() + "=" + userGuid + "))" +
                "(&(" + userAttributes.getUserId() + "=" + userId + ")))");

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

            if ((searchResult.getResultCode() != ResultCode.SUCCESS) || (searchResult.getSearchEntries().size() != 1))
            {
                throw new AuthenticatorException("No user was found for the provided user information");
            }

            SearchResultEntry entry = searchResult.getSearchEntries().get(0);

            userSecurity = new ArrayList<String>(
                Arrays.asList(
                    entry.getAttributeValue(securityAttributes.getSecQuestionOne()),
                    entry.getAttributeValue(securityAttributes.getSecQuestionTwo())));

            if (DEBUG)
            {
                DEBUGGER.debug("List<String>: {}", userSecurity);
            }
        }
        catch (LDAPException lx)
        {
            throw new AuthenticatorException(lx.getMessage(), lx);
        }
        catch (ConnectException cx)
        {
            throw new AuthenticatorException(cx.getMessage(), cx);
        }
        finally
        {
            if ((ldapPool != null) && (!(ldapPool.isClosed())))
            {
                ldapConn.close();
                ldapPool.releaseConnection(ldapConn);
            }
        }

        return userSecurity;
    }

    /**
     * @see com.cws.esolutions.security.dao.userauth.interfaces.Authenticator#obtainOtpSecret(java.lang.String, java.lang.String)
     */
    public synchronized String obtainOtpSecret(final String userId, final String userGuid) throws AuthenticatorException
    {
        final String methodName = ActiveDirectoryAuthenticator.CNAME + "#obtainOtpSecret(final String userId, final String userGuid) throws AuthenticatorException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userId);
            DEBUGGER.debug("Value: {}", userGuid);
        }

        String otpSecret = null;
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
                    "(&(" + userAttributes.getCommonName() + "=" + userGuid + "))" +
                    "(&(" + userAttributes.getUserId() + "=" + userId + ")))");

            if (DEBUG)
            {
                DEBUGGER.debug("searchFilter: {}", searchFilter);
            }

            SearchRequest searchRequest = new SearchRequest(
                repoConfig.getRepositoryUserBase() + "," + repoConfig.getRepositoryBaseDN(),
                SearchScope.SUB,
                searchFilter,
                securityAttributes.getSecret());

            if (DEBUG)
            {
                DEBUGGER.debug("searchRequest: {}", searchRequest);
            }

            SearchResult searchResult = ldapConn.search(searchRequest);

            if ((searchResult.getResultCode() != ResultCode.SUCCESS) || (searchResult.getSearchEntries().size() != 1))
            {
                throw new AuthenticatorException("No user was found for the provided user information");
            }

            otpSecret = searchResult.getSearchEntries().get(0).getAttributeValue(securityAttributes.getSecret());
        }
        catch (LDAPException lx)
        {
            throw new AuthenticatorException(lx.getMessage(), lx);
        }
        catch (ConnectException cx)
        {
            throw new AuthenticatorException(cx.getMessage(), cx);
        }
        finally
        {
            if ((ldapPool != null) && (!(ldapPool.isClosed())))
            {
                ldapConn.close();
                ldapPool.releaseConnection(ldapConn);
            }
        }

        return otpSecret;
    }

    /**
     * @see com.cws.esolutions.security.dao.userauth.interfaces.Authenticator#verifySecurityData(java.lang.String, java.lang.String, java.util.List)
     */
    public synchronized boolean verifySecurityData(final String userId, final String userGuid, List<String> values) throws AuthenticatorException
    {
        final String methodName = ActiveDirectoryAuthenticator.CNAME + "#verifySecurityData(final String userId, final String userGuid, List<String> values) throws AuthenticatorException";

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

            // validate the question
            Filter searchFilter = Filter.create("(&(objectClass=" + repoConfig.getBaseObject() + ")" +
                "(&(" + userAttributes.getCommonName() + "=" + userGuid + "))" +
                "(&(" + userAttributes.getUserId() + "=" + userId + "))" +
                "(&(" + securityAttributes.getSecAnswerOne() + "=" + values.get(0) + "))" +
                "(&(" + securityAttributes.getSecAnswerTwo() + "=" + values.get(1) + ")))");

            SearchRequest searchReq = new SearchRequest(
                repoConfig.getRepositoryUserBase() + "," + repoConfig.getRepositoryBaseDN(),
                SearchScope.SUB,
                searchFilter);

            if (DEBUG)
            {
                DEBUGGER.debug("SearchRequest: {}", searchReq);
            }

            SearchResult searchResult = ldapConn.search(searchReq);

            if (DEBUG)
            {
                DEBUGGER.debug("searchResult: {}", searchResult);
            }

            if ((searchResult.getResultCode() == ResultCode.SUCCESS) && (searchResult.getEntryCount() == 1))
            {
                return true;
            }
        }
        catch (LDAPException lx)
        {
            throw new AuthenticatorException(lx.getMessage(), lx);
        }
        catch (ConnectException cx)
        {
            throw new AuthenticatorException(cx.getMessage(), cx);
        }
        finally
        {
            if ((ldapPool != null) && (!(ldapPool.isClosed())))
            {
                ldapConn.close();
                ldapPool.releaseConnection(ldapConn);
            }
        }

        return isComplete;
    }
}
