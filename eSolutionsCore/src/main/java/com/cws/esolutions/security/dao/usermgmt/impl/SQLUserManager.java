/**
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

import java.util.Map;
import java.util.List;
import java.sql.ResultSet;
import java.sql.Connection;
import java.util.ArrayList;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import org.apache.commons.lang.StringUtils;

import com.cws.esolutions.security.SecurityConstants;
import com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager;
import com.cws.esolutions.security.dao.usermgmt.enums.SearchRequestType;
import com.cws.esolutions.security.dao.usermgmt.exception.UserManagementException;
/**
 * SecurityService
 * com.cws.esolutions.security.usermgmt.impl
 * SQLUserManager.java
 *
 * $Id: $
 * $Author: $
 * $Date: $
 * $Revision: $
 * @author kmhuntly@gmail.com
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Nov 6, 2012 4:00:38 PM
 *     Created.
 */
public class SQLUserManager implements UserManager
{
    private static final String CNAME = SQLUserManager.class.getName();
    private static final DataSource dataSource = resBean.getDataSource().get(SecurityConstants.INIT_SECURITYDS_MANAGER);

    @Override
    public synchronized void validateUserAccount(final String userId, final String userGuid) throws UserManagementException
    {
        final String methodName = SQLUserManager.CNAME + "#validateUserAccount(final String userId, final String userGuid) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("userId: {}", userId);
            DEBUGGER.debug("userGuid: {}", userGuid);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        PreparedStatement stmt = null;

        try
        {
            sqlConn = SQLUserManager.dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }
            else
            {
                if ((StringUtils.isNotEmpty(userId)) && (StringUtils.isNotEmpty(userGuid)))
                {
                    sqlConn.setAutoCommit(true);

                    stmt = sqlConn.prepareCall("{ CALL getUserByAttribute(?) }");

                    if (DEBUG)
                    {
                        DEBUGGER.debug("stmt: {}", stmt);
                    }

                    if (stmt != null)
                    {
                        stmt.setString(1, userGuid);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("stmt: {}", stmt);
                        }

                        resultSet = stmt.executeQuery();

                        if (DEBUG)
                        {
                            DEBUGGER.debug("ResultSet: {}", resultSet);
                        }

                        if (resultSet.next())
                        {
                            throw new UserManagementException("A user currently exists with the provided UUID");
                        }

                        if (resultSet != null)
                        {
                            resultSet.close();
                            resultSet = null;
                        }

                        if (stmt != null)
                        {
                            stmt.close();
                            stmt = null;
                        }

                        stmt = sqlConn.prepareCall("{ CALL getUserByAttribute(?) }");

                        if (DEBUG)
                        {
                            DEBUGGER.debug("stmt: {}", stmt);
                        }

                        if (stmt != null)
                        {
                            stmt.setString(1, userId);

                            if (DEBUG)
                            {
                                DEBUGGER.debug("stmt: {}", stmt);
                            }

                            resultSet = stmt.executeQuery();

                            if (DEBUG)
                            {
                                DEBUGGER.debug("ResultSet: {}", resultSet);
                            }

                            if (resultSet.next())
                            {
                                throw new UserManagementException("A user currently exists with the provided username");
                            }
                        }
                        else
                        {
                            throw new SQLException("Failed to create callable statement against connection");
                        }
                    }
                    else
                    {
                        throw new SQLException("Failed to create callable statement against connection");
                    }
                }
                else
                {
                    throw new NullPointerException("Either provided userid or user guid is blank. Cannot complete verification");
                }
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new UserManagementException(sqx.getMessage(), sqx);
        }
        finally
        {
            try
            {
                if (resultSet != null)
                {
                    resultSet.close();
                }

                if (stmt != null)
                {
                    stmt.close();
                }

                if (!(sqlConn == null) && (!(sqlConn.isClosed())))
                {
                    sqlConn.close();
                }
            }
            catch (SQLException sqx)
            {
                ERROR_RECORDER.error(sqx.getMessage(), sqx);
            }
        }
    }

    @Override
    public synchronized boolean addUserAccount(final String userDN, final List<String> createRequest, final String groupName) throws UserManagementException
    {
        final String methodName = SQLUserManager.CNAME + "#addUserAccount(final String userDN, final List<String> createRequest, final String groupName) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userDN);
            DEBUGGER.debug("Value: {}", createRequest);
            DEBUGGER.debug("Value: {}", groupName);
        }

        Connection sqlConn = null;
        boolean isComplete = false;
        PreparedStatement stmt = null;

        try
        {
            sqlConn = SQLUserManager.dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }
            else
            {
                sqlConn.setAutoCommit(true);

                stmt = sqlConn.prepareCall("{ CALL addUserAccount(?, ?, ?, ?, ?, ?, ?, ?) }");
                stmt.setString(1, (String) createRequest.get(0));
                stmt.setString(2, (String) createRequest.get(1));
                stmt.setString(3, (String) createRequest.get(2));
                stmt.setString(4, (String) createRequest.get(3));
                stmt.setString(5, (String) createRequest.get(4));
                stmt.setString(6, (String) createRequest.get(5));
                stmt.setString(7, (String) createRequest.get(6));
                stmt.setString(8, (String) createRequest.get(7));

                if (DEBUG)
                {
                    DEBUGGER.debug(stmt.toString());
                }

                if (!(stmt.execute()))
                {
                    isComplete = true;
                }
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new UserManagementException(sqx.getMessage(), sqx);
        }
        finally
        {
            try
            {
                if (stmt != null)
                {
                    stmt.close();
                }

                if (!(sqlConn == null) && (!(sqlConn.isClosed())))
                {
                    sqlConn.close();
                }
            }
            catch (SQLException sqx)
            {
                ERROR_RECORDER.error(sqx.getMessage(), sqx);
            }
        }

        return isComplete;
    }

    @Override
    public synchronized boolean modifyUserInformation(final String userId, final String userGuid, Map<String, Object> changeRequest) throws UserManagementException
    {
        final String methodName = SQLUserManager.CNAME + "#modifyUserInformation(final String userId, final String userGuid, Map<String, Object> changeRequest) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userId);
            DEBUGGER.debug("Value: {}", userGuid);
            DEBUGGER.debug("Value: {}", changeRequest);
        }

        Connection sqlConn = null;
        boolean isComplete = false;
        PreparedStatement stmt = null;

        try
        {
            sqlConn = SQLUserManager.dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }
            else
            {
                int x = 1;
                sqlConn.setAutoCommit(true);
                StringBuilder sBuilder = new StringBuilder()
                    .append("UPDATE usr_lgn \n")
                    .append("SET \n");

                for (String key : changeRequest.keySet())
                {
                    sBuilder.append(key + " = " + changeRequest.get(key) + " ");

                    if (x != changeRequest.size())
                    {
                        sBuilder.append(", ");
                    }

                    x++;
                }

                sBuilder.append("WHERE uid = '" + userId + "' \n");
                sBuilder.append("AND cn = '" + userGuid + "'");

                if (DEBUG)
                {
                    DEBUGGER.debug(sBuilder.toString());
                }

                x = sqlConn.createStatement().executeUpdate(sBuilder.toString());

                if (x == 1)
                {
                    isComplete = true;
                }
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new UserManagementException(sqx.getMessage(), sqx);
        }
        finally
        {
            try
            {
                if (stmt != null)
                {
                    stmt.close();
                }

                if (!(sqlConn == null) && (!(sqlConn.isClosed())))
                {
                    sqlConn.close();
                }
            }
            catch (SQLException sqx)
            {
                ERROR_RECORDER.error(sqx.getMessage(), sqx);
            }
        }

        return isComplete;
    }

    @Override
    public synchronized boolean removeUserAccount(final String userId, final String userGuid) throws UserManagementException
    {
        final String methodName = SQLUserManager.CNAME + "#removeUserAccount(final String userId, final String userGuid) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("userId: {}", userId);
            DEBUGGER.debug("userGuid: {}", userGuid);
        }

        Connection sqlConn = null;
        boolean isComplete = false;
        PreparedStatement stmt = null;

        try
        {
            sqlConn = SQLUserManager.dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }
            else
            {
                sqlConn.setAutoCommit(true);

                stmt = sqlConn.prepareCall("{ CALL removeUserAccount(?) }");
                stmt.setString(1, userGuid);

                if (DEBUG)
                {
                    DEBUGGER.debug(stmt.toString());
                }

                if (!(stmt.execute()))
                {
                    isComplete = true;
                }
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new UserManagementException(sqx.getMessage(), sqx);
        }
        finally
        {
            try
            {
                if (stmt != null)
                {
                    stmt.close();
                }

                if (!(sqlConn == null) && (!(sqlConn.isClosed())))
                {
                    sqlConn.close();
                }
            }
            catch (SQLException sqx)
            {
                ERROR_RECORDER.error(sqx.getMessage(), sqx);
            }
        }

        return isComplete;
    }

    @Override
    public synchronized List<String[]> listUserAccounts() throws UserManagementException
    {
        final String methodName = SQLUserManager.CNAME + "#listUserAccounts() throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        PreparedStatement stmt = null;
        List<String[]> results = null;

        try
        {
            sqlConn = SQLUserManager.dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }
            else
            {
                sqlConn.setAutoCommit(true);

                stmt = sqlConn.prepareCall("{ CALL listUserAccounts() }");

                if (DEBUG)
                {
                    DEBUGGER.debug(stmt.toString());
                }

                resultSet = stmt.executeQuery();

                if (resultSet.next())
                {
                    resultSet.beforeFirst();
                    results = new ArrayList<String[]>();

                    while (resultSet.next())
                    {
                        String[] userData = new String[] {
                            resultSet.getString(authData.getCommonName()),
                            resultSet.getString(authData.getUserId()),
                            resultSet.getString(authData.getGivenName()),
                            resultSet.getString(authData.getSurname()),
                            resultSet.getString(authData.getDisplayName()),
                            resultSet.getString(authData.getEmailAddr()),
                            resultSet.getString(authData.getUserRole()),
                            resultSet.getString(authData.getLockCount()),
                            resultSet.getString(authData.getLastLogin()),
                            resultSet.getString(authData.getExpiryDate()),
                            resultSet.getString(authData.getIsSuspended()),
                            resultSet.getString(authData.getOlrSetupReq()),
                            resultSet.getString(authData.getOlrLocked()),
                            resultSet.getString(authData.getTcAccepted()),
                            resultSet.getString(authData.getPublicKey()),
                        };

                        if (DEBUG)
                        {
                            if (userData != null)
                            {
                                for (String str : userData)
                                {
                                    DEBUGGER.debug(str);
                                }
                            }
                        }

                        results.add(userData);
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("List: {}", results);
                    }
                }
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new UserManagementException(sqx.getMessage(), sqx);
        }
        finally
        {
            try
            {
                if (resultSet != null)
                {
                    resultSet.close();
                }
            
                if (stmt != null)
                {
                    stmt.close();
                }

                if (!(sqlConn == null) && (!(sqlConn.isClosed())))
                {
                    sqlConn.close();
                }
            }
            catch (SQLException sqx)
            {
                ERROR_RECORDER.error(sqx.getMessage(), sqx);
            }
        }

        return results;
    }

    @Override
    public synchronized List<String[]> searchUsers(final SearchRequestType searchType, final String searchData) throws UserManagementException
    {
        final String methodName = SQLUserManager.CNAME + "#searchUsers(final SearchRequestType searchType, final String searchData) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("SearchRequestType: {}", searchType);
            DEBUGGER.debug("Search data: {}", searchData);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        PreparedStatement stmt = null;
        List<String[]> results = null;

        try
        {
            sqlConn = SQLUserManager.dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }
            else
            {
                sqlConn.setAutoCommit(true);

                stmt = sqlConn.prepareCall("{ CALL getUserByAttribute(?) }");
                stmt.setString(1, searchData);

                if (DEBUG)
                {
                    DEBUGGER.debug(stmt.toString());
                }

                resultSet = stmt.executeQuery();

                if (resultSet.next())
                {
                    resultSet.beforeFirst();
                    results = new ArrayList<String[]>();

                    while (resultSet.next())
                    {
                        String[] userData = new String[] {
                            resultSet.getString(authData.getCommonName()),
                            resultSet.getString(authData.getUserId()),
                            resultSet.getString(authData.getGivenName()),
                            resultSet.getString(authData.getSurname()),
                            resultSet.getString(authData.getDisplayName()),
                            resultSet.getString(authData.getEmailAddr()),
                            resultSet.getString(authData.getUserRole()),
                            resultSet.getString(authData.getLockCount()),
                            resultSet.getString(authData.getLastLogin()),
                            resultSet.getString(authData.getExpiryDate()),
                            resultSet.getString(authData.getIsSuspended()),
                            resultSet.getString(authData.getOlrSetupReq()),
                            resultSet.getString(authData.getOlrLocked()),
                            resultSet.getString(authData.getTcAccepted()),
                            resultSet.getString(authData.getPublicKey()),
                        };

                        if (DEBUG)
                        {
                            if (userData != null)
                            {
                                for (String str : userData)
                                {
                                    DEBUGGER.debug(str);
                                }
                            }
                        }

                        results.add(userData);
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("List: {}", results);
                    }
                }
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new UserManagementException(sqx.getMessage(), sqx);
        }
        finally
        {
            try
            {
                if (resultSet != null)
                {
                    resultSet.close();
                }
            
                if (stmt != null)
                {
                    stmt.close();
                }

                if (!(sqlConn == null) && (!(sqlConn.isClosed())))
                {
                    sqlConn.close();
                }
            }
            catch (SQLException sqx)
            {
                ERROR_RECORDER.error(sqx.getMessage(), sqx);
            }
        }

        return results;
    }

    @Override
    public synchronized List<Object> loadUserAccount(final String guid) throws UserManagementException
    {
        final String methodName = SQLUserManager.CNAME + "#loadUserAccount(final String guid) throws UserManagementException";
        
        if(DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("String: {}", guid);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        PreparedStatement stmt = null;
        List<Object> userAccount = null;

        try
        {
            sqlConn = SQLUserManager.dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }
            else
            {
                sqlConn.setAutoCommit(true);

                stmt = sqlConn.prepareCall("{ CALL loadUserAccount(?) }");

                if (stmt != null)
                {
                    stmt.setString(1, guid); // common name

                    if (DEBUG)
                    {
                        DEBUGGER.debug("stmt: {}", stmt);
                    }

                    resultSet = stmt.executeQuery();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("ResultSet: {}", resultSet);
                    }

                    if (resultSet.next())
                    {
                        resultSet.last();
                        int x = resultSet.getRow();

                        if (DEBUG)
                        {
                            DEBUGGER.debug("x: {}", x);
                        }

                        if ((x == 0) || (x > 1))
                        {
                            throw new UserManagementException("No user account was located for the provided data.");
                        }
                        else
                        {
                            resultSet.first();

                            userAccount = new ArrayList<Object>();
                            userAccount.add(resultSet.getString(authData.getCommonName()));
                            userAccount.add(resultSet.getString(authData.getUserId()));
                            userAccount.add(resultSet.getString(authData.getGivenName()));
                            userAccount.add(resultSet.getString(authData.getSurname()));
                            userAccount.add(resultSet.getString(authData.getDisplayName()));
                            userAccount.add(resultSet.getString(authData.getEmailAddr()));
                            userAccount.add(resultSet.getString(authData.getUserRole()).toUpperCase());
                            userAccount.add(resultSet.getInt(authData.getLockCount()));
                            userAccount.add(resultSet.getLong(authData.getLastLogin()));
                            userAccount.add(resultSet.getLong(authData.getExpiryDate()));
                            userAccount.add(resultSet.getBoolean(authData.getIsSuspended()));
                            userAccount.add(resultSet.getBoolean(authData.getOlrSetupReq()));
                            userAccount.add(resultSet.getBoolean(authData.getOlrLocked()));
                            userAccount.add(resultSet.getBoolean(authData.getTcAccepted()));
                            userAccount.add(resultSet.getBytes(authData.getPublicKey()));

                            if (DEBUG)
                            {
                                DEBUGGER.debug("UserAccount: {}", userAccount);
                            }
                        }
                    }
                    else
                    {
                        throw new UserManagementException("No users were located with the provided information");
                    }
                }
                else
                {
                    throw new SQLException("Unable to create prepared statement against SQL connection");
                }
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new UserManagementException(sqx.getMessage(), sqx);
        }
        finally
        {
            try
            {
                if (resultSet != null)
                {
                    resultSet.close();
                }
            
                if (stmt != null)
                {
                    stmt.close();
                }

                if (!(sqlConn == null) && (!(sqlConn.isClosed())))
                {
                    sqlConn.close();
                }
            }
            catch (SQLException sqx)
            {
                ERROR_RECORDER.error(sqx.getMessage(), sqx);
            }
        }

        return userAccount;
    }

    @Override
    public synchronized boolean modifyUserSuspension(final String userId, final String userGuid, final boolean isSuspended) throws UserManagementException
    {
        final String methodName = SQLUserManager.CNAME + "#modifyUserSuspension(final String userId, final String userGuid, final boolean isSuspended) throws UserManagementException";

        if (DEBUG)
        {
        	DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userId);
            DEBUGGER.debug("Value: {}", userGuid);
            DEBUGGER.debug("Value: {}", isSuspended);
        }

        Connection sqlConn = null;
        boolean isComplete = false;
        PreparedStatement stmt = null;

        try
        {
            sqlConn = SQLUserManager.dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }
            else
            {
                sqlConn.setAutoCommit(true);

                stmt = sqlConn.prepareCall("{ CALL modifyUserSuspension(?, ?) }");
                stmt.setString(1, userGuid);
                stmt.setBoolean(2, isSuspended);

                if (DEBUG)
                {
                    DEBUGGER.debug(stmt.toString());
                }

                int x = stmt.executeUpdate();

                if (DEBUG)
                {
                    DEBUGGER.debug("Update: {}", x);
                }

                if (x == 1)
                {
                    isComplete = true;
                }
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new UserManagementException(sqx.getMessage(), sqx);
        }
        finally
        {
            try
            {
                if (stmt != null)
                {
                    stmt.close();
                }

                if (!(sqlConn == null) && (!(sqlConn.isClosed())))
                {
                    sqlConn.close();
                }
            }
            catch (SQLException sqx)
            {
                ERROR_RECORDER.error(sqx.getMessage(), sqx);
            }
        }

        return isComplete;
    }

    @Override
    public synchronized boolean unlockUserAccount(final String userId, final String userGuid) throws UserManagementException
    {
        final String methodName = SQLUserManager.CNAME + "#unlockUserAccount(final Stirng userId, final String userGuid) throws UserManagementException";

        if (DEBUG)
        {
        	DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userId);
            DEBUGGER.debug("Value: {}", userGuid);
        }

        Connection sqlConn = null;
        boolean isComplete = false;
        PreparedStatement stmt = null;

        try
        {
            sqlConn = SQLUserManager.dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }
            else
            {
                sqlConn.setAutoCommit(true);

                stmt = sqlConn.prepareCall("{ CALL unlockUserAccount(?) }");
                stmt.setString(1, userGuid);

                if (DEBUG)
                {
                    DEBUGGER.debug(stmt.toString());
                }

                int x = stmt.executeUpdate();

                if (DEBUG)
                {
                    DEBUGGER.debug("Update: {}", x);
                }

                if (x == 1)
                {
                    isComplete = true;
                }
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new UserManagementException(sqx.getMessage(), sqx);
        }
        finally
        {
            try
            {
                if (stmt != null)
                {
                    stmt.close();
                }

                if (!(sqlConn == null) && (!(sqlConn.isClosed())))
                {
                    sqlConn.close();
                }
            }
            catch (SQLException sqx)
            {
                ERROR_RECORDER.error(sqx.getMessage(), sqx);
            }
        }

        return isComplete;
    }
}
