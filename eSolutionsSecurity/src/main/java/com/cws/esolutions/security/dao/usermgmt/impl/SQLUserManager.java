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
 * File: SQLUserManager.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.List;
import java.util.Map;
import java.sql.ResultSet;
import java.sql.Connection;
import java.util.ArrayList;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.CallableStatement;

import com.cws.esolutions.security.SecurityServiceConstants;
import com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager;
import com.cws.esolutions.security.dao.usermgmt.exception.UserManagementException;
/**
 * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager
 */
public class SQLUserManager implements UserManager
{
    private static final String CNAME = SQLUserManager.class.getName();
    static final DataSource dataSource = svcBean.getDataSources().get(SecurityServiceConstants.INIT_SECURITYDS_MANAGER);

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
            DEBUGGER.debug("Value: {}", userId);
            DEBUGGER.debug("Value: {}", userGuid);
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

            stmt = sqlConn.prepareCall("{ CALL getUserByAttribute(?, ?) }");
            stmt.setString(1, userId);
            stmt.setInt(2, 0);

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
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#addUserAccount(java.util.Map, java.util.List)
     */
    @Override
    public synchronized boolean addUserAccount(final Map<String, String> userAccount, final List<String> roles) throws UserManagementException
    {
        final String methodName = SQLUserManager.CNAME + "#addUserAccount(final List<String> createRequest, final List<String> roles) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userAccount);
            DEBUGGER.debug("Value: {}", roles);
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

            for (int x = 1; x < userAccount.size(); x++)
            {
                if (DEBUG)
                {
                    DEBUGGER.debug("Value: {}", userAccount.get(x));
                }

                stmt.setString(x, userAccount.get(x));
            }

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
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#addOtpSecret(java.lang.String, java.lang.String)
     */
    @Override
    public synchronized boolean addOtpSecret(final String userId, final String secret, final String attributeName) throws UserManagementException
    {
        final String methodName = SQLUserManager.CNAME + "#addOtpSecret(final String userId, final String secret) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("userId: {}", userId);
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
            stmt = sqlConn.prepareCall("{ CALL addUserSecret(?, ?) }");
            stmt.setString(1, userId);
            stmt.setString(2, secret);

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
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#removeOtpSecret(java.lang.String, java.lang.String)
     */
    @Override
    public synchronized boolean removeOtpSecret(final String userId, final String attributeName) throws UserManagementException
    {
        final String methodName = SQLUserManager.CNAME + "#removeOtpSecret(final String userId) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("userId: {}", userId);
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
            stmt = sqlConn.prepareCall("{ CALL removeUserSecret(?) }");
            stmt.setString(1, userId);

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
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#changeUserPassword(java.lang.String, java.lang.String, java.lang.int)
     */
    @Override
    public synchronized boolean changeUserPassword(final String userId, final String newPass, final String attributeName) throws UserManagementException
    {
        final String methodName = SQLUserManager.CNAME + "#changeUserPassword(final String userId, final String newPass) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("userId: {}", userId);
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
            stmt = sqlConn.prepareCall("{ CALL updateUserPassword(?, ?) }");
            stmt.setString(1, userId);
            stmt.setString(3, newPass);

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
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#removeUserAccount(java.lang.String)
     */
    @Override
    public synchronized boolean removeUserAccount(final String userId) throws UserManagementException
    {
        final String methodName = SQLUserManager.CNAME + "#removeUserAccount(final String userId) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("userId: {}", userId);
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
            stmt.setString(1, userId);

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
                            resultSet.getString("cn"),
                            resultSet.getString("uid")
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
    public synchronized List<String[]> searchUsers(final String attribute, final String searchData) throws UserManagementException
    {
        final String methodName = SQLUserManager.CNAME + "#searchUsers(final SearchRequestType searchType, final String searchData) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", searchData);
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

            stmt = sqlConn.prepareCall("{ CALL getUserByAttribute(?, ?) }");
            stmt.setString(1, searchData);
            stmt.setInt(2, 0);

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
                            resultSet.getString("cn"),
                            resultSet.getString("uid")
                        };

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Data: {}", (Object) userData);
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
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#loadUserAccount(java.lang.String, java.util.List)
     */
    @Override
    public synchronized List<Object> loadUserAccount(final String guid, final List<String> attributes) throws UserManagementException
    {
        final String methodName = SQLUserManager.CNAME + "#loadUserAccount(final String guid, final List<String> attributes) throws UserManagementException";
        
        if(DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", guid);
            DEBUGGER.debug("Value: {}", attributes);
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

                    for (String attribute : attributes)
                    {
                        if (DEBUG)
                        {
                            DEBUGGER.debug("Attribute: {}", attribute);
                        }

                        userAccount.add(resultSet.getString(attribute));
                    }

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
    public synchronized boolean modifyUserSuspension(final String userId, final boolean isSuspended, final String attributeName) throws UserManagementException
    {
        final String methodName = SQLUserManager.CNAME + "#modifyUserSuspension(final String userId, final boolean isSuspended) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userId);
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
            stmt.setString(1, userId);
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
    public synchronized void lockUserAccount(final String userId, final String attributeName) throws UserManagementException
    {
        final String methodName = SQLUserManager.CNAME + "#unlockUserAccount(final String userId) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userId);
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
            stmt.setString(1, userId);

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
    public synchronized boolean unlockUserAccount(final String userId, final String attributeName) throws UserManagementException
    {
        final String methodName = SQLUserManager.CNAME + "#unlockUserAccount(final Stirng userId, final String userId) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userId);
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
            stmt.setString(1, userId);

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
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#modifyUserEmail(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public synchronized boolean modifyUserEmail(final String userId, final String value, final String attributeName) throws UserManagementException
    {
        final String methodName = SQLUserManager.CNAME + "#modifyUserEmail(final String userId, final String value) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("userId: {}", userId);
            DEBUGGER.debug("userId: {}", value);
        }
        
        return false;
    }

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#modifyUserContact(java.lang.String, java.lang.String, java.util.List)
     */
    @Override
    public synchronized boolean modifyUserContact(final String userId, final String value, final String attribute) throws UserManagementException
    {
        final String methodName = SQLUserManager.CNAME + "#modifyUserContact(final String userId, final List<String> value) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("userId: {}", userId);
            DEBUGGER.debug("List<String>: {}", value);
        }
        
        return false;
    }

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#modifyUserRole(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public synchronized boolean modifyUserRole(final String userId, final Object[] value) throws UserManagementException
    {
        final String methodName = SQLUserManager.CNAME + "#modifyUserRole(final String userId, final Object[] value) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("userId: {}", userId);
            DEBUGGER.debug("List<String>: {}", value);
        }
        
        return false;
    }

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#lockOnlineReset(java.lang.String, java.lang.String)
     */
    @Override
    public synchronized boolean lockOnlineReset(final String userId, final String attributeName) throws UserManagementException
    {
        final String methodName = SQLUserManager.CNAME + "#lockOnlineReset(final String userId, final String value) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("userId: {}", userId);
        }
        
        return false;
    }

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#clearLockCount(java.lang.String, java.lang.String)
     */
    @Override
    public synchronized boolean clearLockCount(final String userId, final String attributeName) throws UserManagementException
    {
        final String methodName = SQLUserManager.CNAME + "#clearLockCount(final String userId, final String value) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("userId: {}", userId);
        }
        
        return false;
    }

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#changeUserSecurity(java.lang.String, java.util.List)
     */
    @Override
    public synchronized boolean changeUserSecurity(final String userId, final Map<String, String> values) throws UserManagementException
    {
        final String methodName = SQLUserManager.CNAME + "#changeUserSecurity(final String userId, final List<String> values) throws UserManagementException";
        
        if (DEBUG)
        {
        	DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: ", userId);
        }
        
        return false;
    }
}
