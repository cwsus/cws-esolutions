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
import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.Calendar;
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

import com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager;
import com.cws.esolutions.security.dao.usermgmt.exception.UserManagementException;
/**
 * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager
 */
public class LDAPUserManager implements UserManager
{
    private Properties connProps = null;

    private static final String BASE_DN = "repositoryBaseDN";
    private static final String BASE_OBJECT = "baseObjectClass";
    private static final String USER_BASE = "repositoryUserBase";
    private static final String ROLE_BASE = "repositoryRoleBase";
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

            Filter searchFilter = Filter.create("(&(objectClass=" + this.connProps.getProperty(LDAPUserManager.BASE_OBJECT) + ")" +
                    "(&(cn=" + userGuid + "))" +
					"(|(uid=" + userId + ")))");

            if (DEBUG)
            {
                DEBUGGER.debug("Filter: {}", searchFilter);
            }

            SearchRequest searchRequest = new SearchRequest(
                    this.connProps.getProperty(this.connProps.getProperty(LDAPUserManager.BASE_DN)),
                    SearchScope.SUB,
                    searchFilter,
                    "cn");

            if (DEBUG)
            {
                DEBUGGER.debug("SearchRequest: {}", searchRequest);
            }

            searchResult = ldapConn.search(searchRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("searchResult: {}", searchResult);
            }

            if ((searchResult.getResultCode() != ResultCode.SUCCESS) || (searchResult.getEntryCount() == 0))
            {
                reurn true;
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
                .append(this.connProps.getProperty(LDAPUserManager.USER_BASE));

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
                    new Attribute("objectClass", this.connProps.getProperty(LDAPUserManager.BASE_OBJECT)),
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
                        .append(this.connProps.getProperty(LDAPUserManager.ROLE_BASE));

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
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#modifyUserSuspension(java.lang.String, boolean, java.lang.String)
     */
    @Override
    public synchronized boolean modifyUserSuspension(final String userId, final boolean isSuspended, final String attributeName) throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#modifyUserSuspension(final String userDN, final boolean isSuspended, final String attributeName) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userId);
            DEBUGGER.debug("Value: {}", isSuspended);
            DEBUGGER.debug("Value: {}", attributeName);
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
                    new Modification(ModificationType.REPLACE, authRepo.getIsSuspended(), String.valueOf(isSuspended))
                )
            );

            if (DEBUG)
            {
                DEBUGGER.debug("modifyList: {}", modifyList);
            }

            LDAPResult ldapResult = ldapConn.modify(new ModifyRequest(new StringBuilder()
                .append("uid" + "=" + userId + ",")
                .append(this.connProps.getProperty(LDAPUserManager.USER_BASE)).toString(), modifyList));

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
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#unlockUserAccount(java.lang.String, java.lang.String)
     */
    @Override
    public synchronized void lockUserAccount(final String userId, final String attributeName) throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#lockUserAccount(final String userId) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userId);
            DEBUGGER.debug("Value: {}", attributeName);
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
                    new Modification(ModificationType.REPLACE, authRepo.getLockCount(), "0")
                )
            );

            if (DEBUG)
            {
                DEBUGGER.debug("modifyList: {}", modifyList);
            }

            LDAPResult ldapResult = ldapConn.modify(new ModifyRequest(new StringBuilder()
                .append("uid" + "=" + userId + ",")
                .append(this.connProps.getProperty(LDAPUserManager.USER_BASE)).toString(), modifyList));

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
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#unlockUserAccount(java.lang.String, java.lang.String)
     */
    @Override
    public synchronized boolean unlockUserAccount(final String userId, final String attributeName) throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#modifyUserSuspension(final String userId) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userId);
            DEBUGGER.debug("Value: {}", attributeName);
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
                    new Modification(ModificationType.REPLACE, authRepo.getLockCount(), "0")));

            if (DEBUG)
            {
                DEBUGGER.debug("modifyList: {}", modifyList);
            }

            LDAPResult ldapResult = ldapConn.modify(new ModifyRequest(new StringBuilder()
                .append("uid" + "=" + userId + ",")
                .append(this.connProps.getProperty(LDAPUserManager.USER_BASE)).toString(), modifyList));

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
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#changeUserPassword(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public synchronized boolean changeUserPassword(final String userId, final String newPass, final String attributeName) throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#changeUserPassword(final String userId, final String newPass) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userId);
            DEBUGGER.debug("Value: {}", attributeName);
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
                    new Modification(ModificationType.REPLACE, authRepo.getUserPassword(), newPass),
                    new Modification(ModificationType.REPLACE, authRepo.getExpiryDate(), String.valueOf(cal.getTime()))));

            LDAPResult ldapResult = ldapConn.modify(new ModifyRequest(new StringBuilder()
                .append("uid" + "=" + userId + ",")
                .append(this.connProps.getProperty(LDAPUserManager.USER_BASE)).toString(), modifyList));

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
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#addOtpSecret(java.lang.String, java.lang.String)
     */
    @Override
    public synchronized boolean addOtpSecret(final String userId, final String secret, final String attributeName) throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#addOtpSecret(final String userId, final String secret) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("User GUID: {}", userId);
            DEBUGGER.debug("Value: {}", attributeName);
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

            LDAPResult ldapResult = null;

            try
            {
                // assume add
                List<Modification> modifyList = new ArrayList<>(
                        Arrays.asList(
						    new Modification(ModificationType.ADD, authRepo.getSecret(), secret)));

                ldapResult = ldapConn.modify(new ModifyRequest(new StringBuilder()
                    .append("uid" + "=" + userId + ",")
                    .append(this.connProps.getProperty(LDAPUserManager.USER_BASE)).toString(), modifyList));
            }
            catch (LDAPException lx)
            {
                // ok, maybe mod ?
                List<Modification> modifyList = new ArrayList<>(
                        Arrays.asList(
                                new Modification(ModificationType.REPLACE, attributeName, secret)));

                ldapResult = ldapConn.modify(new ModifyRequest(new StringBuilder()
                    .append("uid" + "=" + userId + ",")
                    .append(this.connProps.getProperty(LDAPUserManager.USER_BASE)).toString(), modifyList));
            }

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
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#removeOtpSecret(java.lang.String, java.lang.String)
     */
    @Override
    public synchronized boolean removeOtpSecret(final String userId, final String attributeName) throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#removeOtpSecret(final String userId, final String value) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("userId: {}", userId);
            DEBUGGER.debug("Value: {}", attributeName);
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
					    new Modification(ModificationType.DELETE, authRepo.getSecret())));

            LDAPResult ldapResult = ldapConn.modify(new ModifyRequest(new StringBuilder()
                .append("uid" + "=" + userId + ",")
                .append(this.connProps.getProperty(LDAPUserManager.USER_BASE)).toString(), modifyList));

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
                .append("uid" + "=" + userId + ",")
                .append(this.connProps.getProperty(LDAPUserManager.USER_BASE)).toString());

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
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#searchUsers(java.lang.String, java.lang.String)
     */
    @Override
    public synchronized List<String[]> searchUsers(final String attribute, final String searchData) throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#searchUsers(final String attribute, final String searchData) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", attribute);
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
            Filter searchFilter = Filter.create("(&(objectClass=" + this.connProps.getProperty(LDAPUserManager.BASE_OBJECT) + ")" +
                "(&(" + authRepo.getCommonName() + "=" + searchData +"))" +
                "(|(" + authRepo.getUserId() + "=" + searchData +"))" +
                "(|(" + authRepo.getEmailAddr() + "=" + searchData +"))" +
                "(|(" + authRepo.getGivenName() + "=" + searchData +"))" +
                "(|(" + authRepo.getSurname() + "=" + searchData +"))" +
                "(|(" + authRepo.getDisplayName() + "=" + searchData +")))");

            if (DEBUG)
            {
                DEBUGGER.debug("searchFilter: {}", searchFilter);
            }

            SearchRequest searchReq = new SearchRequest(
                this.connProps.getProperty(LDAPUserManager.USER_BASE),
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
                results = new ArrayList<String[]>();

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
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#loadUserAccount(java.lang.String, java.util.List)
     */
    @Override
    public synchronized List<Object> loadUserAccount(final String userGuid, final List<String> attributes) throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#loadUserAccount(final String userGuid, final List<String> attributes) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userGuid);
            DEBUGGER.debug("Value: {}", attributes);
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

            Filter searchFilter = Filter.create("(&(objectClass=" + this.connProps.getProperty(LDAPUserManager.BASE_OBJECT) + ")" +
                    "(&(cn=" + userGuid + ")))");

            if (DEBUG)
            {
                DEBUGGER.debug("searchFilter: {}", searchFilter);
            }

            SearchRequest searchRequest = new SearchRequest(
                    this.connProps.getProperty(LDAPUserManager.USER_BASE),
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
                        this.connProps.getProperty(LDAPUserManager.ROLE_BASE),
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

            SearchRequest searchReq = new SearchRequest(
                this.connProps.getProperty(LDAPUserManager.USER_BASE),
                SearchScope.SUB,
                Filter.create("(&(objectClass=" + this.connProps.getProperty(LDAPUserManager.BASE_OBJECT) + "))"));

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
    public synchronized boolean modifyUserEmail(final String userId, final String value, final String attributeName) throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#modifyUserEmail(final String userId, final String value) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("userId: {}", userId);
            DEBUGGER.debug("userGuid: {}", value);
            DEBUGGER.debug("Value: {}", attributeName);
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
					    new Modification(ModificationType.REPLACE, authRepo.getEmailAddr(), value)));

            LDAPResult ldapResult = ldapConn.modify(new ModifyRequest(new StringBuilder()
                .append("uid" + "=" + userId + ",")
                .append(this.connProps.getProperty(LDAPUserManager.USER_BASE)).toString(), modifyList));

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
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#modifyUserContact(java.lang.String, java.util.String, java.lang.String)
     */
    @Override
    public synchronized boolean modifyUserContact(final String userId, final String value, final List<String> values) throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#modifyUserContact(final String userId, final String value, final String attribute) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userId);
            DEBUGGER.debug("Value: {}", value);
            DEBUGGER.debug("Value: {}", attribute);
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
				    	new Modification(ModificationType.REPLACE, authRepo.getTelephoneNumber(), value.get(0))),
			        	new Modification(ModificationType.REPLACE, authRepo.getPagerNumber(), value.get(1))));

            LDAPResult ldapResult = ldapConn.modify(new ModifyRequest(new StringBuilder()
                .append("uid" + "=" + userId + ",")
                .append(this.connProps.getProperty(LDAPUserManager.USER_BASE)).toString(), modifyList));

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
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#modifyUserRole(java.lang.String, java.lang.Object[])
     */
    @Override
    public synchronized boolean modifyUserRole(final String userId, final Object[] value) throws UserManagementException
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
            .append("uid" + "=" + userId + ",")
            .append(this.connProps.getProperty(LDAPUserManager.USER_BASE));

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
                    .append(this.connProps.getProperty(LDAPUserManager.ROLE_BASE));

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
    public synchronized boolean lockOnlineReset(final String userId, final String attributeName) throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#lockOnlineReset(final String userId) throws UserManagementException";

        if (DEBUG)
        {
            
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("userId: {}", userId);
            DEBUGGER.debug("Value: {}", attributeName);
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
				    	new Modification(ModificationType.REPLACE, authRepo.getIsOlrLocked(), String.valueOf(true))));

            LDAPResult ldapResult = ldapConn.modify(new ModifyRequest(new StringBuilder()
                .append("uid" + "=" + userId + ",")
                .append(this.connProps.getProperty(LDAPUserManager.USER_BASE)).toString(), modifyList));

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
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#clearLockCount(java.lang.String, java.lang.String)
     */
    @Override
    public synchronized boolean clearLockCount(final String userId, final String attribute) throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#clearLockCount(final String userId, final String attribute) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("userId: {}", userId);
            DEBUGGER.debug("Value: {}", attribute);
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
				    	new Modification(ModificationType.REPLACE, authRepo.getLockCount(), "0")));

            LDAPResult ldapResult = ldapConn.modify(new ModifyRequest(new StringBuilder()
                .append("uid" + "=" + userId + ",")
                .append(this.connProps.getProperty(LDAPUserManager.USER_BASE)).toString(), modifyList));

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
    public synchronized boolean changeUserSecurity(final String userId, final List<String> values) throws UserManagementException
    {
        final String methodName = LDAPUserManager.CNAME + "#changeUserSecurity(final String userId, final Map<String, String> values) throws UserManagementException";

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
                    new Modification(ModificationType.REPLACE, authRepo.getSecQuestionOne(), values.get(0)),
                    new Modification(ModificationType.REPLACE, authRepo.getSecQuestionTwo(), values.get(1)),
                    new Modification(ModificationType.REPLACE, authRepo.getSecAnswerOne(), values.get(2)),
                    new Modification(ModificationType.REPLACE, authRepo.getSecO\AnswerTwo(), values.get(3))));

            if (DEBUG)
            {
                DEBUGGER.debug("modifyList: {}", modifyList);
            }

            LDAPResult ldapResult = ldapConn.modify(new ModifyRequest(new StringBuilder()
                .append("uid" + "=" + userId + ",")
                .append(this.connProps.getProperty(LDAPUserManager.USER_BASE)).toString(), modifyList));

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
