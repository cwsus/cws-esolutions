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
package com.cws.esolutions.security.dao.userauth.impl;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.dao.userauth.impl
 * File: SQLAuthenticator.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.List;
import java.util.Arrays;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.Connection;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.CallableStatement;

import com.cws.esolutions.security.dao.userauth.interfaces.Authenticator;
import com.cws.esolutions.security.dao.userauth.exception.AuthenticatorException;
/**
 * @see com.cws.esolutions.security.dao.userauth.interfaces.Authenticator
 */
public class SQLAuthenticator implements Authenticator
{
    private static final String CNAME = SQLAuthenticator.class.getName();
    private static final DataSource dataSource = (DataSource) svcBean.getAuthDataSource();

    /**
     * @see com.cws.esolutions.security.dao.userauth.interfaces.Authenticator#performLogon(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public synchronized List<Object> performLogon(final String username, final String password) throws AuthenticatorException
    {
        final String methodName = SQLAuthenticator.CNAME + "#performLogon(final String user, final String password) throws AuthenticatorException";
        
        if(DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("String: {}", username);
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

            sqlConn.setAutoCommit(true);

            stmt = sqlConn.prepareCall("{CALL performAuthentication(?, ?)}");
            stmt.setString(1, username); // username
            stmt.setString(2, password); // password

            if (DEBUG)
            {
                DEBUGGER.debug("CallableStatement: {}", stmt);
            }

            if (!(stmt.execute()))
            {
                throw new AuthenticatorException("No user was found for the provided user information");
            }

            resultSet = stmt.getResultSet();

            if (DEBUG)
            {
                DEBUGGER.debug("ResultSet: {}", resultSet);
            }

            if (resultSet.next())
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

    /**
     * @see com.cws.esolutions.security.dao.userauth.interfaces.Authenticator#verifySecurityData(java.util.List)
     */
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

    /**
     * @see com.cws.esolutions.security.dao.userauth.interfaces.Authenticator#obtainSecurityData(java.lang.String, java.lang.String)
     */
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

                    userSecurity = new ArrayList<>(
                            Arrays.asList(
                                    resultSet.getString(authData.getSecQuestionOne()),
                                    resultSet.getString(authData.getSecQuestionTwo())));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("userSecurity: {}", userSecurity);
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
