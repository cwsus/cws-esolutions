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
package com.cws.esolutions.security.dao.userauth.impl;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.dao.userauth.impl
 * File: LDAPAuthenticator.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.util.Properties;
import java.io.FileInputStream;
import java.net.ConnectException;
import java.io.FileNotFoundException;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.BindRequest;
import com.unboundid.ldap.sdk.SearchScope;
import org.apache.commons.lang.StringUtils;
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
public class LDAPAuthenticator implements Authenticator
{
    private Properties connProps = null;

    private static final String BASE_DN = "repositoryBaseDN";
    private static final String BASE_OBJECT = "baseObjectClass";
    private static final String USER_BASE = "repositoryUserBase";
    private static final String ROLE_BASE = "repositoryRoleBase";
    private static final String CNAME = LDAPAuthenticator.class.getName();

    public LDAPAuthenticator() throws AuthenticatorException
    {
        final String methodName = LDAPAuthenticator.CNAME + "#LDAPAuthenticator()#Constructor throws AuthenticatorException";

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

            throw new AuthenticatorException(fnfx.getMessage(), fnfx);
        }
        catch (IOException iox)
        {
            ERROR_RECORDER.error(iox.getMessage(), iox);

            throw new AuthenticatorException(iox.getMessage(), iox);
        }
    }

    /**
     * @see com.cws.esolutions.security.dao.userauth.interfaces.Authenticator#performLogon(java.lang.String, java.lang.String, java.util.List)
     */
    @Override
    public synchronized List<Object> performLogon(final String username, final String password, final List<String> attributes) throws AuthenticatorException
    {
        final String methodName = LDAPAuthenticator.CNAME + "#performLogon(final String username, final String password, final List<String> attributes) throws AuthenticatorException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("String: {}", username);
            DEBUGGER.debug("List<String>: {}", attributes);
        }

        LDAPConnection ldapConn = null;
        LDAPConnectionPool ldapPool = null;
        List<Object> userAccount = new ArrayList<Object>();

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

            Filter searchFilter = Filter.create("(&(objectClass=" + this.connProps.getProperty(LDAPAuthenticator.BASE_OBJECT) + ")" +
                "(&(uid=" + username+ ")))");

            if (DEBUG)
            {
                DEBUGGER.debug("SearchFilter: {}", searchFilter);
            }

            SearchRequest searchRequest = new SearchRequest(
                this.connProps.getProperty(LDAPAuthenticator.USER_BASE),
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

            BindResult bindResult = ldapConn.bind(bindRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("BindResult: {}", bindResult);
            }

            for (String attribute : attributes)
            {
                if (DEBUG)
                {
                    DEBUGGER.debug("Attribute: {}", attribute);
                }

                userAccount.add(entry.getAttributeValue(attribute));
            }

            Filter roleFilter = Filter.create("(&(objectClass=groupOfUniqueNames)" +
                    "(&(uniqueMember=" + entry.getDN() + ")))");

            if (DEBUG)
            {
                DEBUGGER.debug("SearchFilter: {}", roleFilter);
            }

            SearchRequest roleSearch = new SearchRequest(
                this.connProps.getProperty(LDAPAuthenticator.ROLE_BASE),
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
                StringBuilder sBuilder = new StringBuilder();

                for (int x = 0; x < roleResult.getSearchEntries().size(); x++)
                {
                    SearchResultEntry role = roleResult.getSearchEntries().get(x);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("SearchResultEntry: {}", role);
                    }

                    if (x == roleResult.getSearchEntries().size())
                    {
                        sBuilder.append(role);
                    }

                    sBuilder.append(role + ", ");

                    if (DEBUG)
                    {
                        DEBUGGER.debug("sBuilder: {}", sBuilder.toString());
                    }
                }

                userAccount.add(sBuilder.toString());
            }

            if (DEBUG)
            {
                DEBUGGER.debug("UserAccount: {}", userAccount);
            }
        }
        catch (LDAPException lx)
        {
            ERROR_RECORDER.error(lx.getMessage(), lx);

            throw new AuthenticatorException(lx.getResultCode(), lx.getMessage(), lx);
        }
        catch (ConnectException cx)
        {
            ERROR_RECORDER.error(cx.getMessage(), cx);

            throw new AuthenticatorException(cx.getMessage(), cx);
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
     * @see com.cws.esolutions.security.dao.userauth.interfaces.Authenticator#obtainSecurityData(java.lang.String, java.lang.String, java.util.List)
     */
    @Override
    public synchronized List<String> obtainSecurityData(final String userId, final String userGuid, final List<String> attributes) throws AuthenticatorException
    {
        final String methodName = LDAPAuthenticator.CNAME + "#obtainSecurityData(final String userId, final String userGuid, final List<String> attributes) throws AuthenticatorException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userId);
            DEBUGGER.debug("Value: {}", userGuid);
            DEBUGGER.debug("Value: {}", attributes);
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

            Filter searchFilter = Filter.create("(&(objectClass=" + this.connProps.getProperty(LDAPAuthenticator.BASE_OBJECT) + ")" +
                "(&(cn=" + userId + "))" +
                "(&(uid=" + userGuid + ")))");

            if (DEBUG)
            {
                DEBUGGER.debug("searchFilter: {}", searchFilter);
            }

            SearchRequest searchRequest = new SearchRequest(
                this.connProps.getProperty(LDAPAuthenticator.BASE_DN),
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

            userSecurity = new ArrayList<>();
            for (String attribute : attributes)
            {
                if (DEBUG)
                {
                    DEBUGGER.debug("Attribute: {}", attribute);
                }

                userSecurity.add(entry.getAttributeValue(attribute));
            }
        }
        catch (LDAPException lx)
        {
            ERROR_RECORDER.error(lx.getMessage(), lx);

            throw new AuthenticatorException(lx.getMessage(), lx);
        }
        catch (ConnectException cx)
        {
            ERROR_RECORDER.error(cx.getMessage(), cx);

            throw new AuthenticatorException(cx.getMessage(), cx);
        }
        finally
        {
            if ((ldapPool != null) && ((ldapConn != null) && (ldapConn.isConnected())))
            {
                ldapPool.releaseConnection(ldapConn);
            }
        }

        return userSecurity;
    }

    /**
     * @see com.cws.esolutions.security.dao.userauth.interfaces.Authenticator#obtainOtpSecret(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public synchronized String obtainOtpSecret(final String userId, final String userGuid, final String attribute) throws AuthenticatorException
    {
        final String methodName = LDAPAuthenticator.CNAME + "#obtainOtpSecret(final String userId, final String userGuid, final String attribute) throws AuthenticatorException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userId);
            DEBUGGER.debug("Value: {}", userGuid);
            DEBUGGER.debug("Value: {}", attribute);
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

            Filter searchFilter = Filter.create("(&(objectClass=" + this.connProps.getProperty(LDAPAuthenticator.BASE_OBJECT) + ")" +
                "(&(uid=" + userId + "))" +
                "(&(cn=" + userGuid + ")))");

            if (DEBUG)
            {
                DEBUGGER.debug("searchFilter: {}", searchFilter);
            }

            SearchRequest searchRequest = new SearchRequest(
                this.connProps.getProperty(LDAPAuthenticator.BASE_DN),
                SearchScope.SUB,
                searchFilter,
                attribute);

            if (DEBUG)
            {
                DEBUGGER.debug("searchRequest: {}", searchRequest);
            }

            SearchResult searchResult = ldapConn.search(searchRequest);

            if ((searchResult.getResultCode() != ResultCode.SUCCESS) || (searchResult.getSearchEntries().size() != 1))
            {
                throw new AuthenticatorException("No user was found for the provided user information");
            }

            otpSecret = searchResult.getSearchEntries().get(0).getAttributeValue(attribute);
        }
        catch (LDAPException lx)
        {
            ERROR_RECORDER.error(lx.getMessage(), lx);

            throw new AuthenticatorException(lx.getMessage(), lx);
        }
        catch (ConnectException cx)
        {
            ERROR_RECORDER.error(cx.getMessage(), cx);

            throw new AuthenticatorException(cx.getMessage(), cx);
        }
        finally
        {
            if ((ldapPool != null) && ((ldapConn != null) && (ldapConn.isConnected())))
            {
                ldapPool.releaseConnection(ldapConn);
            }
        }

        return otpSecret;
    }

    /**
     * @see com.cws.esolutions.security.dao.userauth.interfaces.Authenticator#verifySecurityData(java.lang.String, java.lang.String, java.util.Map)
     */
    @Override
    public synchronized boolean verifySecurityData(final String userId, final String userGuid, final Map<String, String> attributes) throws AuthenticatorException
    {
        final String methodName = LDAPAuthenticator.CNAME + "#verifySecurityData(final String userId, final String userGuid, final Map<String, String> attributes) throws AuthenticatorException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userId);
            DEBUGGER.debug("Value: {}", userGuid);
            DEBUGGER.debug("Value: {}", attributes);
        }

        boolean isAuthorized = false;
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
            Filter searchFilter = Filter.create("(&(objectClass=" + this.connProps.getProperty(LDAPAuthenticator.BASE_OBJECT) + ")" +
                "(&(cn=" + userGuid + "))" +
                "(&(uid=" + userId + ")))");

            SearchRequest searchReq = new SearchRequest(
                this.connProps.getProperty(LDAPAuthenticator.BASE_DN),
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

            if ((searchResult.getResultCode() != ResultCode.SUCCESS) || (searchResult.getEntryCount() != 1))
            {
                throw new AuthenticatorException("No user was found for the provided user information");
            }

            SearchResultEntry entry = searchResult.getSearchEntries().get(0);

            if (DEBUG)
            {
                DEBUGGER.debug("SearchResultEntry: {}", entry);
            }

            for (String key : attributes.keySet())
            {
                if (DEBUG)
                {
                    DEBUGGER.debug("Key: {}", key);
                }

                if (StringUtils.equals(entry.getAttributeValue(key), attributes.get(key)))
                {
                    break;
                }

                isAuthorized = true;
            }
        }
        catch (LDAPException lx)
        {
            ERROR_RECORDER.error(lx.getMessage(), lx);

            throw new AuthenticatorException(lx.getMessage(), lx);
        }
        catch (ConnectException cx)
        {
            ERROR_RECORDER.error(cx.getMessage(), cx);

            throw new AuthenticatorException(cx.getMessage(), cx);
        }
        finally
        {
            if ((ldapPool != null) && (!(ldapPool.isClosed())))
            {
                ldapPool.releaseConnection(ldapConn);
            }
        }

        return isAuthorized;
    }
}
