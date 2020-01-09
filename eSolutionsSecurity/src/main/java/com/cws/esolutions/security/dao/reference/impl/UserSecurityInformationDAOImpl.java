/*
 * Copyright (c) 2009 - 2017 CaspersBox Web Services
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
package com.cws.esolutions.security.dao.reference.impl;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.dao.reference.impl
 * File: UserSecurityInformationDAOImpl.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly   		11/23/2008 22:39:20             Created.
 */
import java.util.List;
import java.util.Arrays;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.CallableStatement;

import com.cws.esolutions.security.dao.reference.interfaces.IUserSecurityInformationDAO;
/**
 * @see com.cws.esolutions.security.dao.reference.interfaces.ISecurityReferenceDAO
 */
public class UserSecurityInformationDAOImpl implements IUserSecurityInformationDAO
{
    /**
     * @see com.cws.esolutions.security.dao.reference.interfaces.IUserSecurityInformationDAO#addOrUpdateSalt(java.lang.String, java.lang.String, java.lang.String)
     */
    public synchronized boolean addOrUpdateSalt(final String commonName, final String saltValue, final String saltType) throws SQLException
    {
        final String methodName = IUserSecurityInformationDAO.CNAME + "#addOrUpdateSalt(final String commonName, final String saltValue, final String saltType) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("commonName: {}", commonName);
            DEBUGGER.debug("saltType: {}", saltType);
        }

        Connection sqlConn = null;
        boolean isComplete = false;
        CallableStatement stmt = null;

        try
        {
            sqlConn = dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);

            stmt = sqlConn.prepareCall("{CALL addOrUpdateSalt(?, ?, ?)}");
            stmt.setString(1, commonName);
            stmt.setString(2, saltValue);
            stmt.setString(3, saltType);

            if (!(stmt.execute()))
            {
                isComplete = true;
            }
        }
        catch (SQLException sqx)
        {
            throw new SQLException(sqx.getMessage(), sqx);
        }
        finally
        {
            if (stmt != null)
            {
                stmt.close();
            }

            if ((sqlConn != null) && (!(sqlConn.isClosed())))
            {
                sqlConn.close();
            }
        }

        return isComplete;
    }

    /**
     * @see com.cws.esolutions.security.dao.reference.interfaces.IUserSecurityInformationDAO#getUserSalt(java.lang.String, java.lang.String)
     */
    public synchronized String getUserSalt(final String commonName, final String saltType) throws SQLException
    {
        final String methodName = IUserSecurityInformationDAO.CNAME + "#getUserSalt(final String commonName, final String saltType) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("commonName: {}", commonName);
        }

        String saltValue = null;
        Connection sqlConn = null;
        ResultSet resultSet = null;
        CallableStatement stmt = null;

        try
        {
            sqlConn = dataSource.getConnection();

            if (DEBUG)
            {
                DEBUGGER.debug("Connection: {}", sqlConn);
            }

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);
            stmt = sqlConn.prepareCall("{CALL retrUserSalt(?, ?)}");
            stmt.setString(1, commonName);
            stmt.setString(2, saltType);

            if (DEBUG)
            {
                DEBUGGER.debug("CallableStatement: {}", stmt);
            }

            if (stmt.execute())
            {
                resultSet = stmt.getResultSet();

                if (resultSet.next())
                {
                    resultSet.first();
                    saltValue = resultSet.getString(1);
                }
            }
        }
        catch (SQLException sqx)
        {
            throw new SQLException(sqx.getMessage(), sqx);
        }
        finally
        {
            if (resultSet != null)
            {
                resultSet.close();
            }

            if (stmt != null)
            {
                stmt.close();
            }

            if ((sqlConn != null) && (!(sqlConn.isClosed())))
            {
                sqlConn.close();
            }
        }

        return saltValue;
    }

    /**
     * @see com.cws.esolutions.security.dao.reference.interfaces.IUserSecurityInformationDAO#removeUserData(java.lang.String, java.lang.String)
     */
    public synchronized boolean removeUserData(final String commonName, final String saltType) throws SQLException
    {
        final String methodName = IUserSecurityInformationDAO.CNAME + "#removeUserData(final String commonName, final String saltType) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("commonName: {}", commonName);
            DEBUGGER.debug("saltType: {}", saltType);
        }

        Connection sqlConn = null;
        boolean isComplete = false;
        CallableStatement stmt = null;

        try
        {
            sqlConn = dataSource.getConnection();

            if (DEBUG)
            {
                DEBUGGER.debug("Connection: {}", sqlConn);
            }
            
            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);
            stmt = sqlConn.prepareCall("{CALL removeUserData(?)}");
            stmt.setString(1, commonName);

            if (DEBUG)
            {
                DEBUGGER.debug("CallableStatement: {}", stmt);
            }

            isComplete = (!(stmt.execute()));

            if (DEBUG)
            {
                DEBUGGER.debug("isComplete: {}", isComplete);
            }
        }
        catch (SQLException sqx)
        {
            throw new SQLException(sqx.getMessage(), sqx);
        }
        finally
        {
            if (stmt != null)
            {
                stmt.close();
            }

            if ((sqlConn != null) && (!(sqlConn.isClosed())))
            {
                sqlConn.close();
            }
        }

        return isComplete;
    }

    /**
     * @see com.cws.esolutions.security.dao.reference.interfaces.IUserSecurityInformationDAO#insertResetData(java.lang.String, java.lang.String, java.lang.String)
     */
    public synchronized boolean insertResetData(final String commonName, final String resetId, final String smsCode) throws SQLException
    {
        final String methodName = IUserSecurityInformationDAO.CNAME + "#insertResetData(final String commonName, final String resetId, final String smsCode) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("commonName: {}", commonName);
        }

        Connection sqlConn = null;
        boolean isComplete = false;
        CallableStatement stmt = null;

        try
        {
            sqlConn = dataSource.getConnection();


            isComplete = true;

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);
            stmt = sqlConn.prepareCall("{CALL insertResetData(?, ?, ?)}");
            stmt.setString(1, commonName);
            stmt.setString(2, resetId);
            stmt.setString(3, smsCode);

            isComplete = (!(stmt.execute()));

            if (DEBUG)
            {
                DEBUGGER.debug("isComplete: {}", isComplete);
            }
        }
        catch (SQLException sqx)
        {
            throw new SQLException(sqx.getMessage(), sqx);
        }
        finally
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

        return isComplete;
    }

    /**
     * @see com.cws.esolutions.security.dao.reference.interfaces.IUserSecurityInformationDAO#listActiveResets()
     */
    public synchronized List<String[]> listActiveResets() throws SQLException
    {
        final String methodName = IUserSecurityInformationDAO.CNAME + "#listActiveResets() throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        CallableStatement stmt = null;
        List<String[]> response = null;

        try
        {
            sqlConn = dataSource.getConnection();

            if (DEBUG)
            {
                DEBUGGER.debug("sqlConn: {}", sqlConn);
            }

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);

            stmt = sqlConn.prepareCall("{CALL listActiveResetRequests()}");

            if (DEBUG)
            {
                DEBUGGER.debug("CallableStatement: {}", stmt);
            }

            if (stmt.execute())
            {
                resultSet = stmt.getResultSet();

                if (resultSet.next())
                {
                    resultSet.beforeFirst();
                    response = new ArrayList<String[]>();

                    while (resultSet.next())
                    {
                        String[] data = new String[] { resultSet.getString(1), String.valueOf(resultSet.getTimestamp(2)) };

                        response.add(data);
                    }
                }
            }
        }
        catch (SQLException sqx)
        {
            throw new SQLException(sqx.getMessage(), sqx);
        }
        finally
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

        return response;
    }

    /**
     * @see com.cws.esolutions.security.dao.reference.interfaces.IUserSecurityInformationDAO#getResetData(java.lang.String)
     */
    public synchronized List<Object> getResetData(final String resetId) throws SQLException
    {
        final String methodName = IUserSecurityInformationDAO.CNAME + "#getResetData(final String resetId) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        List<Object> resetData = null;
        CallableStatement stmt = null;

        try
        {
            sqlConn = dataSource.getConnection();

            if (DEBUG)
            {
                DEBUGGER.debug("Connection: {}", sqlConn);
            }

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);
            stmt = sqlConn.prepareCall("{CALL getResetData(?)}");
            stmt.setString(1, resetId);

            if (stmt.execute())
            {
                resultSet = stmt.getResultSet();

                if (resultSet.next())
                {
                    resultSet.first();

                    resetData = new ArrayList<Object>(
                            Arrays.asList(
                                    resultSet.getString(1),
                                    resultSet.getTimestamp(2)));
                }
            }
        }
        catch (SQLException sqx)
        {
            throw new SQLException(sqx.getMessage(), sqx);
        }
        finally
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

        return resetData;
    }

    /**
     * @see com.cws.esolutions.security.dao.reference.interfaces.IUserSecurityInformationDAO#removeResetData(java.lang.String, java.lang.String)
     */
    public synchronized boolean removeResetData(final String commonName, final String resetId) throws SQLException
    {
        final String methodName = IUserSecurityInformationDAO.CNAME + "#removeResetData(final String commonName, final String resetId) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("commonName: {}", resetId);
        }

        Connection sqlConn = null;
        boolean isComplete = false;
        CallableStatement stmt = null;

        try
        {
            sqlConn = dataSource.getConnection();

            if (DEBUG)
            {
                DEBUGGER.debug("Connection: {}", sqlConn);
            }

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);
            stmt = sqlConn.prepareCall("{CALL removeResetData(?, ?)}");
            stmt.setString(1, commonName);
            stmt.setString(2, resetId);

            isComplete = (!(stmt.execute()));

            if (DEBUG)
            {
                DEBUGGER.debug("isComplete: {}", isComplete);
            }
        }
        catch (SQLException sqx)
        {
            throw new SQLException(sqx.getMessage(), sqx);
        }
        finally
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

        return isComplete;
    }

    /**
     * @see com.cws.esolutions.security.dao.reference.interfaces.IUserSecurityInformationDAO#verifySmsForReset(java.lang.String, java.lang.String, java.lang.String)
     */
    public synchronized boolean verifySmsForReset(final String userGuid, final String resetId, final String smsCode) throws SQLException
    {
        final String methodName = IUserSecurityInformationDAO.CNAME + "#verifySmsForReset(final String userGuid, final String resetId, final String smsCode) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("userGuid: {}", userGuid);
        }

        Connection sqlConn = null;
        boolean isVerified = false;
        CallableStatement stmt = null;

        try
        {
            sqlConn = dataSource.getConnection();

            if (DEBUG)
            {
                DEBUGGER.debug("Connection: {}", sqlConn);
            }

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);
            stmt = sqlConn.prepareCall("{CALL getCommonNameForReset(?, ?, ?)}");
            stmt.setString(1, userGuid);
            stmt.setString(2, resetId);
            stmt.setString(3, smsCode);

            isVerified = stmt.execute(); // this will return true if theres a resultset, which we expect

            if (DEBUG)
            {
                DEBUGGER.debug("isVerified: {}", isVerified);
            }
        }
        catch (SQLException sqx)
        {
            throw new SQLException(sqx.getMessage(), sqx);
        }
        finally
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

        return isVerified;
    }
}
