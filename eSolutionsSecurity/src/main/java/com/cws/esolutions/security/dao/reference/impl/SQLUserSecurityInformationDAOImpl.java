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
 * File: SQLUserSecurityInformationDAOImpl.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.sql.Types;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;
import org.apache.commons.codec.binary.StringUtils;

import com.cws.esolutions.security.dao.usermgmt.exception.UserManagementException;
import com.cws.esolutions.security.dao.reference.interfaces.IUserSecurityInformationDAO;
/**
 * @see com.cws.esolutions.security.dao.reference.interfaces.ISecurityReferenceDAO
 */
public class SQLUserSecurityInformationDAOImpl implements IUserSecurityInformationDAO
{
    private static final String CNAME = SQLUserSecurityInformationDAOImpl.class.getName();

    /**
     * @see com.cws.esolutions.security.dao.reference.interfaces.IUserSecurityInformationDAO#addUserSalt(java.lang.String, java.lang.String, java.lang.String)
     */
    public synchronized boolean addOrUpdateUserSalt(final String commonName, final String saltValue, final String saltType) throws SQLException
    {
        final String methodName = SQLUserSecurityInformationDAOImpl.CNAME + "#addOrUpdateUserSalt(final String commonName, final String saltValue, final String saltType) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("commonName: {}", commonName);
            DEBUGGER.debug("saltType: {}", saltType);
        }

        Connection sqlConn = null;
        boolean isComplete = false;
        ResultSet resultSet = null;
        CallableStatement stmt = null;

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

            stmt = sqlConn.prepareCall("{ CALL addOrUpdateUserSalt(?, ?, ?, ?) }", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stmt.setString(1, commonName);
            stmt.setString(2, saltValue);
            stmt.setString(3, saltType);
            stmt.registerOutParameter(4, Types.INTEGER);

            stmt.execute();

            int updateCount = stmt.getInt(4);

            if (updateCount == 1)
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
        	if (!(Objects.isNull(resultSet)))
        	{
        		resultSet.close();
        	}

            if (!(Objects.isNull(stmt)))
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
        final String methodName = SQLUserSecurityInformationDAOImpl.CNAME + "#getUserSalt(final String commonName, final String saltType) throws SQLException";

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
            if (!(Objects.isNull(resultSet)))
            {
                resultSet.close();
            }

            if (!(Objects.isNull(stmt)))
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
     * @see com.cws.esolutions.security.dao.reference.interfaces.IUserSecurityInformationDAO#getUserSalt(java.lang.String, java.lang.String)
     */
    public synchronized String getUserPassword(final String commonName, final String userId) throws SQLException
    {
        final String methodName = SQLUserSecurityInformationDAOImpl.CNAME + "#getUserPassword(final String commonName, final String userId) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", commonName);
            DEBUGGER.debug("Value: {}", userId);
        }

        Connection sqlConn = null;
        String userPassword = null;
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
            stmt = sqlConn.prepareStatement("{ CALL getUserPassword(?, ?) }", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stmt.setString(1, commonName);
            stmt.setString(2, userId);

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
                    userPassword = resultSet.getString(1);
                }
            }
        }
        catch (final SQLException sqx)
        {
            throw new SQLException(sqx.getMessage(), sqx);
        }
        finally
        {
            if (!(Objects.isNull(resultSet)))
            {
                resultSet.close();
            }

            if (!(Objects.isNull(stmt)))
            {
                stmt.close();
            }

            if ((sqlConn != null) && (!(sqlConn.isClosed())))
            {
                sqlConn.close();
            }
        }

        return userPassword;
    }

    /**
     * @see com.cws.esolutions.security.dao.reference.interfaces.IUserSecurityInformationDAO#insertResetData(java.lang.String, java.lang.String, java.lang.String)
     */
    public synchronized boolean insertResetData(final String commonName, final String resetId) throws SQLException
    {
        final String methodName = SQLUserSecurityInformationDAOImpl.CNAME + "#insertResetData(final String commonName, final String resetId) throws SQLException";

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
            stmt = sqlConn.prepareStatement("{ CALL insertResetData(?, ?) }", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
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
            if (!(Objects.isNull(stmt)))
            {
                stmt.close();
            }

            if (!(Objects.isNull(sqlConn)) && (!(sqlConn.isClosed())))
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
        final String methodName = SQLUserSecurityInformationDAOImpl.CNAME + "#getResetData(final String resetId) throws SQLException";

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
            if (!(Objects.isNull(resultSet)))
            {
                resultSet.close();
            }

            if (!(Objects.isNull(stmt)))
            {
                stmt.close();
            }

            if (!(Objects.isNull(sqlConn)) && (!(sqlConn.isClosed())))
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
        final String methodName = SQLUserSecurityInformationDAOImpl.CNAME + "#removeResetData(final String commonName, final String resetId) throws SQLException";

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
            stmt = sqlConn.prepareStatement("{ CALL removeResetData(?, ?) }", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
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
            if (!(Objects.isNull(stmt)))
            {
                stmt.close();
            }

            if (!(Objects.isNull(sqlConn)) && (!(sqlConn.isClosed())))
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
        final String methodName = SQLUserSecurityInformationDAOImpl.CNAME + "#obtainSecurityQuestionList() throws SQLException";

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

            if ((Objects.isNull(sqlConn)) || (sqlConn.isClosed()))
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
            if (!(Objects.isNull(resultSet)))
            {
                resultSet.close();
            }

            if (!(Objects.isNull(stmt)))
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


    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#modifyUserPassword(java.lang.String, java.lang.String)
     */
    public synchronized boolean modifyUserPassword(final String userGuid, final String userId, final String newPass, final boolean isReset) throws UserManagementException
    {
        final String methodName = SQLUserSecurityInformationDAOImpl.CNAME + "#modifyUserPassword(final String userGuid, final String userId, final String newPass, final boolean isReset) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userGuid);
            DEBUGGER.debug("Value: {}", userId);
            DEBUGGER.debug("Value: {}", isReset);
        }

        Connection sqlConn = null;
        boolean isComplete = false;
        ResultSet resultSet = null;
        PreparedStatement stmt = null;

        if (Objects.isNull(dataSource))
        {
        	throw new UserManagementException("A datasource connection could not be obtained.");
        }

        try
        {
            sqlConn = dataSource.getConnection();

            if (DEBUG)
            {
            	DEBUGGER.debug("sqlConn: {}", sqlConn);
            }

            if ((Objects.isNull(sqlConn)) || (sqlConn.isClosed()))
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);

            // first make sure the existing password is proper
            // then make sure the new password doesnt match the existing password
            stmt = sqlConn.prepareStatement("{ CALL modifyUserPassword(?, ?, ?) }", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stmt.setString(1, userGuid);
            stmt.setString(2, newPass);
            stmt.setBoolean(3, isReset);

            if (DEBUG)
            {
                DEBUGGER.debug("PreparedStatement: {}", stmt);
            }

            stmt.executeUpdate();

            stmt.close();
            stmt = null;

            stmt = sqlConn.prepareStatement("{ CALL getUserPassword(?, ?) }", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stmt.setString(1, userGuid);
            stmt.setString(2, userId);

            if (DEBUG)
            {
            	DEBUGGER.debug("stmt: {}", stmt);
            }

            resultSet = stmt.executeQuery();

            if (!(Objects.isNull(resultSet)))
            {
            	resultSet.first();
            	String confPassword = resultSet.getString(1);

            	if (DEBUG)
            	{
            		DEBUGGER.debug("confPassword: {}", confPassword);
            	}

            	if (StringUtils.equals(confPassword, newPass))
            	{
            		isComplete = true;
            	}
            }
        }
        catch (final SQLException sqx)
        {
            throw new UserManagementException(sqx.getMessage(), sqx);
        }
        finally
        {
            try
            {
            	if (!(Objects.isNull(resultSet)))
            	{
            		resultSet.close();
            	}

                if (!(Objects.isNull(stmt)))
                {
                    stmt.close();
                }

                if (!(Objects.isNull(sqlConn)) && (!(sqlConn.isClosed())))
                {
                    sqlConn.close();
                }
            }
            catch (final SQLException sqx)
            {
                throw new UserManagementException(sqx.getMessage(), sqx);
            }
        }

        return isComplete;
    }

    /**
     * @see com.cws.esolutions.security.dao.usermgmt.interfaces.UserManager#modifyUserSecurity(java.lang.String, java.util.List)
     */
    public synchronized boolean modifyUserSecurity(final String userGuid, final List<String> values) throws UserManagementException
    {
        final String methodName = SQLUserSecurityInformationDAOImpl.CNAME + "#modifyUserSecurity(final String userGuid, final List<String> values) throws UserManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("userGuid: {}", userGuid);
        }

        Connection sqlConn = null;
        boolean isComplete = false;
        CallableStatement stmt = null;

        if (Objects.isNull(dataSource))
        {
        	throw new UserManagementException("A datasource connection could not be obtained.");
        }

        try
        {
            sqlConn = dataSource.getConnection();

            if (DEBUG)
            {
            	DEBUGGER.debug("sqlConn: {}", sqlConn);
            }

            if ((Objects.isNull(sqlConn)) || (sqlConn.isClosed()))
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);

            stmt = sqlConn.prepareCall("{ CALL addOrUpdateSecurityQuestions(?, ?, ?, ?, ?, ?) }", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stmt.setString(1, userGuid);
            stmt.setString(2, values.get(0));
            stmt.setString(3, values.get(1));
            stmt.setString(4, values.get(2));
            stmt.setString(5, values.get(3));
            stmt.registerOutParameter(6, Types.INTEGER);

            if (DEBUG)
            {
                DEBUGGER.debug("CallableStatement: {}", stmt);
            }

            stmt.execute();
            int executed = stmt.getInt(6);

            if (DEBUG)
            {
            	DEBUGGER.debug("executed: {}", executed);
            }

            if (executed == 1)
            {
            	isComplete = true;
            }
        }
        catch (final SQLException sqx)
        {
            throw new UserManagementException(sqx.getMessage(), sqx);
        }
        finally
        {
            try
            {
                if (!(Objects.isNull(stmt)))
                {
                    stmt.close();
                }

                if (!(Objects.isNull(sqlConn)) && (!(sqlConn.isClosed())))
                {
                    sqlConn.close();
                }
            }
            catch (final SQLException sqx)
            {
                throw new UserManagementException(sqx.getMessage(), sqx);
            }
        }

        return isComplete;
    }
}
