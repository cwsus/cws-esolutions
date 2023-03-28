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
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import com.cws.esolutions.security.dao.reference.interfaces.IUserSecurityInformationDAO;
/**
 * @see com.cws.esolutions.security.dao.reference.interfaces.ISecurityReferenceDAO
 */
public class UserSecurityInformationDAOImpl implements IUserSecurityInformationDAO
{
    private static final String CNAME = UserSecurityInformationDAOImpl.class.getName();

    /**
     * @see com.cws.esolutions.security.dao.reference.interfaces.IUserSecurityInformationDAO#addUserSalt(java.lang.String, java.lang.String, java.lang.String)
     */
    public synchronized boolean addUserSalt(final String commonName, final String saltValue, final String saltType) throws SQLException
    {
        final String methodName = UserSecurityInformationDAOImpl.CNAME + "#addUserSalt(final String commonName, final String saltValue, final String saltType) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("commonName: {}", commonName);
            DEBUGGER.debug("saltType: {}", saltType);
        }

        Connection sqlConn = null;
        boolean isComplete = false;
        PreparedStatement stmt = null;

        if (Objects.isNull(dataSource))
        {
        	throw new SQLException("A datasource connection could not be obtained.");
        }

        try
        {
            sqlConn = dataSource.getConnection();

            if ((Objects.isNull(sqlConn)) || (sqlConn.isClosed()))
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);

            stmt = sqlConn.prepareStatement("{ CALL addUserSalt(?, ?, ?) }");
            stmt.setString(1, commonName);
            stmt.setString(2, saltValue);
            stmt.setString(3, saltType);

            if (!(stmt.execute()))
            {
                isComplete = true;
            }
        }
        catch (final SQLException sqx)
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
    public synchronized boolean updateUserSalt(final String commonName, final String saltValue, final String saltType) throws SQLException
    {
        final String methodName = UserSecurityInformationDAOImpl.CNAME + "#updateUserSalt(final String commonName, final String saltValue, final String saltType) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("commonName: {}", commonName);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        boolean isComplete = false;
        PreparedStatement stmt = null;

        if (Objects.isNull(dataSource))
        {
        	throw new SQLException("A datasource connection could not be obtained.");
        }

        try
        {
            sqlConn = dataSource.getConnection();

            if (DEBUG)
            {
                DEBUGGER.debug("Connection: {}", sqlConn);
            }

            if ((Objects.isNull(sqlConn)) || (sqlConn.isClosed()))
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);

            stmt = sqlConn.prepareStatement("{ CALL updateUserSalt(?, ?, ?) }");
            stmt.setString(1, commonName);
            stmt.setString(2, saltValue);
            stmt.setString(3, saltType);

            if (!(stmt.execute()))
            {
                isComplete = true;
            }
        }
        catch (final SQLException sqx)
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

        return isComplete;
    }

    /**
     * @see com.cws.esolutions.security.dao.reference.interfaces.IUserSecurityInformationDAO#getUserSalt(java.lang.String, java.lang.String)
     */
    public synchronized String getUserSalt(final String commonName, final String saltType) throws SQLException
    {
        final String methodName = UserSecurityInformationDAOImpl.CNAME + "#getUserSalt(final String commonName, final String saltType) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("commonName: {}", commonName);
        }

        String saltValue = null;
        Connection sqlConn = null;
        ResultSet resultSet = null;
        PreparedStatement stmt = null;

        if (Objects.isNull(dataSource))
        {
        	throw new SQLException("A datasource connection could not be obtained.");
        }

        try
        {
            sqlConn = dataSource.getConnection();

            if (DEBUG)
            {
                DEBUGGER.debug("Connection: {}", sqlConn);
            }

            if ((Objects.isNull(sqlConn)) || (sqlConn.isClosed()))
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);
            stmt = sqlConn.prepareStatement("{ CALL getUserSalt(?, ?) }", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stmt.setString(1, commonName);
            stmt.setString(2, saltType);

            if (DEBUG)
            {
                DEBUGGER.debug("PreparedStatement: {}", stmt);
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
        catch (final SQLException sqx)
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
     * @see com.cws.esolutions.security.dao.reference.interfaces.IUserSecurityInformationDAO#insertResetData(java.lang.String, java.lang.String, java.lang.String)
     */
    public synchronized boolean insertResetData(final String commonName, final String resetId) throws SQLException
    {
        final String methodName = UserSecurityInformationDAOImpl.CNAME + "#insertResetData(final String commonName, final String resetId) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("commonName: {}", commonName);
        }

        Connection sqlConn = null;
        boolean isComplete = false;
        PreparedStatement stmt = null;

        if (Objects.isNull(dataSource))
        {
        	throw new SQLException("A datasource connection could not be obtained.");
        }

        try
        {
            sqlConn = dataSource.getConnection();

            if ((Objects.isNull(sqlConn)) || (sqlConn.isClosed()))
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);
            stmt = sqlConn.prepareStatement("{ CALL insertResetData(?, ?) }");
            stmt.setString(1, commonName);
            stmt.setString(2, resetId);

            isComplete = (!(stmt.execute()));

            if (DEBUG)
            {
                DEBUGGER.debug("isComplete: {}", isComplete);
            }
        }
        catch (final SQLException sqx)
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
     * @see com.cws.esolutions.security.dao.reference.interfaces.IUserSecurityInformationDAO#getResetData(java.lang.String)
     */
    public synchronized List<Object> getResetData(final String resetId) throws SQLException
    {
        final String methodName = UserSecurityInformationDAOImpl.CNAME + "#getResetData(final String resetId) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        List<Object> resetData = null;
        PreparedStatement stmt = null;

        if (Objects.isNull(dataSource))
        {
        	throw new SQLException("A datasource connection could not be obtained.");
        }

        try
        {
            sqlConn = dataSource.getConnection();

            if (DEBUG)
            {
                DEBUGGER.debug("Connection: {}", sqlConn);
            }

            if ((Objects.isNull(sqlConn)) || (sqlConn.isClosed()))
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);
            stmt = sqlConn.prepareStatement("{ CALL getResetData(?) }", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
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
        catch (final SQLException sqx)
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
        final String methodName = UserSecurityInformationDAOImpl.CNAME + "#removeResetData(final String commonName, final String resetId) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("commonName: {}", resetId);
        }

        Connection sqlConn = null;
        boolean isComplete = false;
        PreparedStatement stmt = null;

        if (Objects.isNull(dataSource))
        {
        	throw new SQLException("A datasource connection could not be obtained.");
        }

        try
        {
            sqlConn = dataSource.getConnection();

            if (DEBUG)
            {
                DEBUGGER.debug("Connection: {}", sqlConn);
            }

            if ((Objects.isNull(sqlConn)) || (sqlConn.isClosed()))
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);
            stmt = sqlConn.prepareStatement("{ CALL removeResetData(?, ?) }");
            stmt.setString(1, commonName);
            stmt.setString(2, resetId);

            isComplete = (!(stmt.execute()));

            if (DEBUG)
            {
                DEBUGGER.debug("isComplete: {}", isComplete);
            }
        }
        catch (final SQLException sqx)
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
     * @see com.cws.esolutions.security.dao.reference.interfaces.IUserSecurityInformationDAO#obtainSecurityQuestionList()
     */
    public synchronized HashMap<Integer, String> obtainSecurityQuestionList() throws SQLException
    {
        final String methodName = UserSecurityInformationDAOImpl.CNAME + "#obtainSecurityQuestionList() throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        PreparedStatement stmt = null;
        HashMap<Integer, String> questionMap = null;

        if (Objects.isNull(dataSource))
        {
        	throw new SQLException("A datasource connection could not be obtained.");
        }

        try
        {
            sqlConn = dataSource.getConnection();

            if ((sqlConn == null) || (sqlConn.isClosed()))
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);
            stmt = sqlConn.prepareStatement("{ CALL retrSecurityQuestions() }", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            if (DEBUG)
            {
                DEBUGGER.debug("PreparedStatement: {}", stmt);
            }

            if (stmt.execute())
            {
                resultSet = stmt.getResultSet();
                resultSet.last();
                int iRowCount = resultSet.getRow();
                resultSet.beforeFirst();

                if (DEBUG)
                {
                	DEBUGGER.debug("iRowCount: {}", iRowCount);
                }

                if (iRowCount == 0)
                {
                    throw new SQLException("No security questions are currently configured.");
                }

                if (resultSet.next())
                {
                    questionMap = new HashMap<Integer, String>();

                    while (resultSet.next())
                    {
                    	questionMap.put(resultSet.getInt(1), resultSet.getString(2));

                        if (DEBUG)
                        {
                            DEBUGGER.debug("questionMap: {}", questionMap);
                        }
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Map<Integer, String>: {}", questionMap);
                    }
                }
                else
                {
                	throw new SQLException("No security questions are currently configured.");
                }
            }
        }
        catch (final SQLException sqx)
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

        return questionMap;
    }
}
