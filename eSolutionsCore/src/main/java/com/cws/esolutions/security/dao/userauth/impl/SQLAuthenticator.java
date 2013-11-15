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
package com.cws.esolutions.security.dao.userauth.impl;

import java.util.List;
import java.util.Arrays;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.Connection;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;

import com.cws.esolutions.security.SecurityConstants;
import com.cws.esolutions.security.dao.userauth.interfaces.Authenticator;
import com.cws.esolutions.security.dao.userauth.exception.AuthenticatorException;
/*
 * UserSQLAuthenticationDAOImpl
 * Data access class for agent authentication events
 *
 * History
 *
 * Author                       Date                            Comments
 * ----------------------------------------------------------------------------
 * Kevin Huntly                 11/23/2008 22:39:20             Created.
 */
public class SQLAuthenticator implements Authenticator
{
    private static final String CNAME = SQLAuthenticator.class.getName();
    private static final DataSource dataSource = resBean.getDataSource().get(SecurityConstants.INIT_SECURITYDS_MANAGER);

    @Override
    public synchronized List<Object> performLogon(final String guid, final String username, final String password, final String groupName) throws AuthenticatorException
    {
        final String methodName = SQLAuthenticator.CNAME + "#performLogon(final String user, final String password, final String groupName) throws AuthenticatorException";
        
        if(DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("String: {}", guid);
            DEBUGGER.debug("String: {}", username);
            DEBUGGER.debug("String: {}", password);
            DEBUGGER.debug("String: {}", groupName);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        CallableStatement stmt = null;
        List<Object> userAccount = null;

        try
        {
            sqlConn = SQLAuthenticator.dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }
            else
            {
                sqlConn.setAutoCommit(true);

                stmt = sqlConn.prepareCall("{CALL performAuthentication(?, ?, ?)}");
                stmt.setString(1, guid); // common name
                stmt.setString(2, username); // username
                stmt.setString(2, password); // password

                if (DEBUG)
                {
                    DEBUGGER.debug(stmt.toString());
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
                            throw new AuthenticatorException("No user account was located for the provided data.");
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
                            userAccount.add(resultSet.getString(authData.getPagerNumber()));
                            userAccount.add(resultSet.getString(authData.getTelephoneNumber()));
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

                            stmt.close();
                            stmt = null;
                            stmt = sqlConn.prepareCall("{ CALL loginSuccess(?, ?) }");
                            stmt.setString(1, guid); // common name
                            stmt.setString(2, password); // username

                            if (DEBUG)
                            {
                                DEBUGGER.debug("stmt: {}", stmt);
                            }

                            int y = stmt.executeUpdate();

                            if (DEBUG)
                            {
                                DEBUGGER.debug("Result: {}", y);
                            }

                            if (y != 1)
                            {
                                ERROR_RECORDER.error("Failed to update last logon and authentication count.");
                            }
                        }
                    }
                    else
                    {
                        throw new AuthenticatorException("No user account was located for the provided data.");
                    }
                }
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new AuthenticatorException(sqx.getMessage(), sqx);
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
    public synchronized boolean changeUserPassword(final String userGuid, final String newPass, final Long expiry) throws AuthenticatorException
    {
        final String methodName = SQLAuthenticator.CNAME + "#changeUserPassword(final String userGuid, final String newPass, final Long expiry) throws AuthenticatorException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("userGuid: {}", userGuid);
            DEBUGGER.debug("newPass: {}", newPass);
            DEBUGGER.debug("expiry: {}", expiry);
        }

        Connection sqlConn = null;
        boolean isComplete = false;
        PreparedStatement stmt = null;

        try
        {
            sqlConn = SQLAuthenticator.dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }
            else
            {
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
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new AuthenticatorException(sqx.getMessage(), sqx);
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
    public synchronized void lockUserAccount(final String userId, final int currentCount) throws AuthenticatorException
    {
        final String methodName = SQLAuthenticator.CNAME + "#boolean lockUserAccount(final String userId, final int currentCount) throws AuthenticatorException";
        
        if(DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("LoginType: {}", userId);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        CallableStatement stmt = null;

        try
        {
            sqlConn = SQLAuthenticator.dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }
            else
            {
                sqlConn.setAutoCommit(true);
                stmt = sqlConn.prepareCall("{CALL lockUserAcct(?, ?)}");
                stmt.setString(1, userId);
                stmt.setInt(2, currentCount + 1);

                if (DEBUG)
                {
                    DEBUGGER.debug("stmt: {}", stmt.toString());
                }

                if (!(stmt.execute()))
                {
                    ERROR_RECORDER.error("Failed to increment user lock count.");
                }
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new AuthenticatorException(sqx.getMessage(), sqx);
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
    public synchronized boolean createSecurityData(final String userId, final String userGuid, final List<String> request) throws AuthenticatorException
    {
        final String methodName = SQLAuthenticator.CNAME + "#createSecurityData(final String userId, final String userGuid, final List<String> request) throws AuthenticatorException";

        if(DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userId);
            DEBUGGER.debug("Value: {}", userGuid);
            DEBUGGER.debug("Value: {}", request);
        }

        Connection sqlConn = null;
        boolean isComplete = false;
        ResultSet resultSet = null;
        CallableStatement stmt = null;

        try
        {
            sqlConn = SQLAuthenticator.dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }
            else
            {
                sqlConn.setAutoCommit(true);

                stmt = sqlConn.prepareCall("{ CALL addOrUpdateSecurityQuestions(?, ?, ?, ?, ?, ?, ? }");
                stmt.setString(1, userGuid); // guid
                stmt.setString(2, userId); // password
                stmt.setString(4, request.get(3)); // secques 1
                stmt.setString(5, request.get(4)); // secques 2
                stmt.setString(6, request.get(5)); // secques 1
                stmt.setString(7, request.get(6)); // secques 2

                if (DEBUG)
                {
                    DEBUGGER.debug("Statement: {}", stmt.toString());
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

            throw new AuthenticatorException(sqx.getMessage(), sqx);
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

        return isComplete;
    }

    @Override
    public synchronized boolean verifySecurityData(final List<String> request) throws AuthenticatorException
    {
        final String methodName = SQLAuthenticator.CNAME + "#verifySecurityData(final List<String> request) throws AuthenticatorException";

        if(DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Request: {}", request);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        boolean isAuthorized = false;
        CallableStatement stmt = null;

        try
        {
            sqlConn = SQLAuthenticator.dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }
            else
            {
                sqlConn.setAutoCommit(true);

                stmt = sqlConn.prepareCall("{CALL verifySecurityQuestions(?, ?, ?, ?)}");
                stmt.setString(1, request.get(0)); // guid
                stmt.setString(2, request.get(1)); // username
                stmt.setString(3, request.get(2)); // secans 1
                stmt.setString(4, request.get(3)); // secans 2

                if (DEBUG)
                {
                    DEBUGGER.debug("Statement: {}", stmt.toString());
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
                        resultSet.first();
                        isAuthorized = resultSet.getBoolean(1);
                    }
                }
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new AuthenticatorException(sqx.getMessage(), sqx);
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

        return isAuthorized;
    }

    @Override
    public synchronized List<String> obtainSecurityData(final String userName, final String userGuid) throws AuthenticatorException
    {
        final String methodName = SQLAuthenticator.CNAME + "#obtainSecurityData(final String userName, final String userGuid) throws AuthenticatorException";
        
        if(DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userName);
            DEBUGGER.debug("Value: {}", userGuid);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        CallableStatement stmt = null;
        List<String> userSecurity = null;

        try
        {
            sqlConn = SQLAuthenticator.dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }
            else
            {
                sqlConn.setAutoCommit(true);

                stmt = sqlConn.prepareCall("{CALL getUserByAttribute(?)}");
                stmt.setString(1, userGuid); // guid

                if (DEBUG)
                {
                    DEBUGGER.debug(stmt.toString());
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
                        resultSet.first();

                        userSecurity = new ArrayList<String>(
                            Arrays.asList(
                                resultSet.getString(authData.getSecQuestionOne()),
                                resultSet.getString(authData.getSecQuestionTwo())));

                        if (DEBUG)
                        {
                            DEBUGGER.debug("userSecurity: {}", userSecurity);
                        }
                    }
                    else
                    {
                        throw new AuthenticatorException("No user account was located for the provided data.");
                    }
                }
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new AuthenticatorException(sqx.getMessage(), sqx);
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

        return userSecurity;
    }
}
