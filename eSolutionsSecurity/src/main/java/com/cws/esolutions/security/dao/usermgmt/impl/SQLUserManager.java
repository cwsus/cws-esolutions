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
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.dao.usermgmt.impl
 * File: SQLUserManager.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.Map;
import java.util.List;
import java.sql.ResultSet;
import java.sql.Connection;
import java.util.ArrayList;

import javax.sql.DataSource;

import java.sql.SQLException;
import java.sql.CallableStatement;

import com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager;
import com.cws.esolutions.security.dao.usermgmt.enums.SearchRequestType;
import com.cws.esolutions.security.dao.usermgmt.exception.UserManagementException;
/**
 * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager
 */
public class SQLUserManager implements UserManager
{
    private static final String CNAME = SQLUserManager.class.getName();
    static final DataSource dataSource = svcBean.getDataSources().get("SecurityDataSource");

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#validateUserAccount(java.lang.String, java.lang.String)
     */
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
        CallableStatement stmt = null;

        try
        {
            sqlConn = SQLUserManager.dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);

            stmt = sqlConn.prepareCall("{ CALL getUserByAttribute(?) }");
            stmt.setString(1, userGuid);

            if (DEBUG)
            {
                DEBUGGER.debug("stmt: {}", stmt);
            }

            if (stmt.execute())
            {
                resultSet = stmt.executeQuery();

                if (DEBUG)
                {
                    DEBUGGER.debug("ResultSet: {}", resultSet);
                }

                if (resultSet.next())
                {
                    throw new UserManagementException("A user currently exists with the provided UUID");
                }

                resultSet.close();
                resultSet = null;

                stmt.close();
                stmt = null;

                stmt = sqlConn.prepareCall("{ CALL getUserByAttribute(?) }");
                stmt.setString(1, userId);

                if (DEBUG)
                {
                    DEBUGGER.debug("stmt: {}", stmt);
                }

                if (stmt.execute())
                {
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

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#addUserAccount(java.lang.List, java.util.List)
     */
    @Override
    public synchronized boolean addUserAccount(final List<String> createRequest, final List<String> roles) throws UserManagementException
    {
        final String methodName = SQLUserManager.CNAME + "#addUserAccount(final List<String> createRequest, final List<String> roles) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", createRequest);
        }

        Connection sqlConn = null;
        boolean isComplete = false;
        CallableStatement stmt = null;

        try
        {
            sqlConn = SQLUserManager.dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);

            stmt = sqlConn.prepareCall("{ CALL addUserAccount(?, ?, ?, ?, ?, ?, ?, ?) }");
            stmt.setString(1, createRequest.get(0));
            stmt.setString(2, createRequest.get(1));
            stmt.setString(3, createRequest.get(2));
            stmt.setString(4, createRequest.get(3));
            stmt.setString(5, createRequest.get(4));
            stmt.setString(6, createRequest.get(5));
            stmt.setString(7, createRequest.get(6));
            stmt.setString(8, createRequest.get(7));

            if (DEBUG)
            {
                DEBUGGER.debug(stmt.toString());
            }

            if (!(stmt.execute()))
            {
                isComplete = true;
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

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#modifyUserInformation(java.lang.String, java.lang.String, java.util.Map)
     */
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
        CallableStatement stmt = null;

        try
        {
            sqlConn = SQLUserManager.dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

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

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#changeUserPassword(java.lang.String, java.lang.String, java.lang.Long)
     */
    @Override
    public synchronized boolean changeUserPassword(final String userGuid, final String newPass, final Long expiry) throws UserManagementException
    {
        final String methodName = SQLUserManager.CNAME + "#changeUserPassword(final String userGuid, final String newPass, final Long expiry) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("userGuid: {}", userGuid);
            DEBUGGER.debug("newPass: {}", newPass);
            DEBUGGER.debug("expiry: {}", expiry);
        }

        Connection sqlConn = null;
        boolean isComplete = false;
        CallableStatement stmt = null;

        try
        {
            sqlConn = SQLUserManager.dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);

            // first make sure the existing password is proper
            // then make sure the new password doesnt match the existing password
            stmt = sqlConn.prepareCall("{ CALL updateUserPassword(?, ?, ?) }");
            stmt.setString(1, userGuid);
            stmt.setString(2, newPass);
            stmt.setLong(3, expiry);

            if (DEBUG)
            {
                DEBUGGER.debug(stmt.toString());
            }

            if (stmt.executeUpdate() == 1)
            {
                isComplete = true;
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

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#removeUserAccount(java.lang.String, java.lang.String)
     */
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
        CallableStatement stmt = null;

        try
        {
            sqlConn = SQLUserManager.dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

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

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#listUserAccounts()
     */
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
        CallableStatement stmt = null;
        List<String[]> results = null;

        try
        {
            sqlConn = SQLUserManager.dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);

            stmt = sqlConn.prepareCall("{ CALL listUserAccounts() }");

            if (DEBUG)
            {
                DEBUGGER.debug(stmt.toString());
            }

            if (stmt.execute())
            {
                resultSet = stmt.getResultSet();

                if (resultSet.next())
                {
                    resultSet.beforeFirst();
                    results = new ArrayList<>();

                    while (resultSet.next())
                    {
                        String[] userData = new String[]
                        {
                                resultSet.getString(authData.getCommonName()),
                                resultSet.getString(authData.getUserId()),
                                resultSet.getString(authData.getGivenName()),
                                resultSet.getString(authData.getSurname()),
                                resultSet.getString(authData.getDisplayName()),
                                resultSet.getString(authData.getEmailAddr()),
                                resultSet.getString(authData.getLockCount()),
                                resultSet.getString(authData.getLastLogin()),
                                resultSet.getString(authData.getExpiryDate()),
                                resultSet.getString(authData.getIsSuspended()),
                                resultSet.getString(authData.getOlrSetupReq()),
                                resultSet.getString(authData.getOlrLocked()),
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

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#searchUsers(com.cws.esolutions.security.dao.usermgmt.enums.SearchRequestType, java.lang.String)
     */
    @Override
    public synchronized List<Object[]> searchUsers(final SearchRequestType searchType, final String searchData) throws UserManagementException
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
        CallableStatement stmt = null;
        List<Object[]> results = null;

        try
        {
            sqlConn = SQLUserManager.dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);

            stmt = sqlConn.prepareCall("{ CALL getUserByAttribute(?) }");
            stmt.setString(1, searchData);

            if (DEBUG)
            {
                DEBUGGER.debug(stmt.toString());
            }

            if (stmt.execute())
            {
                resultSet = stmt.getResultSet();

                if (resultSet.next())
                {
                    resultSet.beforeFirst();
                    results = new ArrayList<>();

                    while (resultSet.next())
                    {
                        Object[] userData = new Object[]
                        {
                                resultSet.getString(authData.getCommonName()),
                                resultSet.getString(authData.getUserId()),
                                resultSet.getInt(authData.getLockCount())
                        };

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Data: {}", userData);
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

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#loadUserAccount(java.lang.String)
     */
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
        CallableStatement stmt = null;
        List<Object> userAccount = null;

        try
        {
            sqlConn = SQLUserManager.dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);

            stmt = sqlConn.prepareCall("{ CALL loadUserAccount(?) }");
            stmt.setString(1, guid); // common name

            if (DEBUG)
            {
                DEBUGGER.debug("CallableStatement: {}", stmt);
            }

            if (stmt.execute())
            {
                resultSet = stmt.getResultSet();

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

                    resultSet.first();

                    userAccount = new ArrayList<>();
                    userAccount.add(resultSet.getString(authData.getCommonName()));
                    userAccount.add(resultSet.getString(authData.getUserId()));
                    userAccount.add(resultSet.getString(authData.getGivenName()));
                    userAccount.add(resultSet.getString(authData.getSurname()));
                    userAccount.add(resultSet.getString(authData.getDisplayName()));
                    userAccount.add(resultSet.getString(authData.getEmailAddr()));
                    userAccount.add(resultSet.getString(authData.getPagerNumber()));
                    userAccount.add(resultSet.getString(authData.getTelephoneNumber()));
                    userAccount.add(resultSet.getInt(authData.getLockCount()));
                    userAccount.add(resultSet.getLong(authData.getLastLogin()));
                    userAccount.add(resultSet.getLong(authData.getExpiryDate()));
                    userAccount.add(resultSet.getBoolean(authData.getIsSuspended()));
                    userAccount.add(resultSet.getBoolean(authData.getOlrSetupReq()));
                    userAccount.add(resultSet.getBoolean(authData.getOlrLocked()));

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

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#modifyUserSuspension(java.lang.String, java.lang.String, boolean)
     */
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
        CallableStatement stmt = null;

        try
        {
            sqlConn = SQLUserManager.dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

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

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#lockUserAccount(java.lang.String, java.lang.String, java.lang.Boolean)
     */
    @Override
    public synchronized void lockUserAccount(final String userId, final String userGuid) throws UserManagementException
    {
        final String methodName = SQLUserManager.CNAME + "#unlockUserAccount(final Stirng userId, final String userGuid) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userId);
            DEBUGGER.debug("Value: {}", userGuid);
        }

        Connection sqlConn = null;
        CallableStatement stmt = null;

        try
        {
            sqlConn = SQLUserManager.dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);

            stmt = sqlConn.prepareCall("{ CALL lockUserAccount(?) }");
            stmt.setString(1, userGuid);

            if (DEBUG)
            {
                DEBUGGER.debug(stmt.toString());
            }

            stmt.executeUpdate();
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
    }

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#unlockUserAccount(java.lang.String, java.lang.String)
     */
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
        CallableStatement stmt = null;

        try
        {
            sqlConn = SQLUserManager.dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

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
