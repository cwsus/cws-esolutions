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
 * File: SecurityReferenceDAOImpl.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.util.Map;
import java.util.List;
import java.util.Objects;
import java.util.HashMap;
import java.sql.ResultSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.sql.SQLException;
import java.sql.CallableStatement;
import java.sql.ResultSetMetaData;
import org.apache.commons.lang3.StringUtils;

import com.cws.esolutions.security.dao.reference.interfaces.ISecurityReferenceDAO;
/**
 * @see com.cws.esolutions.security.dao.reference.interfaces.ISecurityReferenceDAO
 */
public class SecurityReferenceDAOImpl implements ISecurityReferenceDAO
{
    private static final String CNAME = SecurityReferenceDAOImpl.class.getName();

    /**
     * @see com.cws.esolutions.security.dao.reference.interfaces.ISecurityReferenceDAO#obtainApprovedServers()
     */
    public synchronized List<String> obtainApprovedServers() throws SQLException
    {
        final String methodName = SecurityReferenceDAOImpl.CNAME + "#obtainApprovedServers() throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        CallableStatement stmt = null;
        List<String> securityList = null;

        if (Objects.isNull(dataSource))
        {
        	throw new SQLException("A datasource connection could not be obtained.");
        }

        try
        {
            sqlConn = dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);
            stmt = sqlConn.prepareCall("{CALL retrApprovedServers()}", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

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
                    resultSet.beforeFirst();

                    securityList = new ArrayList<String>();

                    while (resultSet.next())
                    {
                        if (DEBUG)
                        {
                            DEBUGGER.debug(resultSet.getString(1));
                        }

                        // check if column is null
                        securityList.add(resultSet.getString(1));
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("securityList: {}", securityList);
                    }
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

        return securityList;
    }

    /**
     * @see com.cws.esolutions.security.dao.reference.interfaces.ISecurityReferenceDAO#obtainSecurityQuestionList()
     */
    public synchronized List<String> obtainSecurityQuestionList() throws SQLException
    {
        final String methodName = SecurityReferenceDAOImpl.CNAME + "#obtainSecurityQuestionList() throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        CallableStatement stmt = null;
        List<String> questionList = null;

        if (Objects.isNull(dataSource))
        {
        	throw new SQLException("A datasource connection could not be obtained.");
        }

        try
        {
            sqlConn = dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);
            stmt = sqlConn.prepareCall("{CALL retrieve_user_questions()}", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            if (DEBUG)
            {
                DEBUGGER.debug("CallableStatement: {}", stmt);
            }

            if (stmt.execute())
            {
                resultSet = stmt.getResultSet();
                resultSet.last();
                int iRowCount = resultSet.getRow();

                if (iRowCount == 0)
                {
                    throw new SQLException("No security questions are currently configured.");
                }

                resultSet.first();
                ResultSetMetaData resultData = resultSet.getMetaData();

                int iColumns = resultData.getColumnCount();

                questionList = new ArrayList<String>();

                for (int x = 1; x < iColumns + 1; x++)
                {
                    if (DEBUG)
                    {
                        DEBUGGER.debug("resultSet.getObject: {}", resultSet.getObject(resultData.getColumnName(x)));
                    }

                    // check if column is null
                    resultSet.getObject(resultData.getColumnName(x));

                    // if the column was null, insert n/a, otherwise, insert the column's contents
                    questionList.add((String) (resultSet.wasNull() ? "N/A" : resultSet.getObject(resultData.getColumnName(x))));
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

        return questionList;
    }

    /**
     * @see com.cws.esolutions.security.dao.reference.interfaces.ISecurityReferenceDAO#listAvailableServices()
     */
    public synchronized Map<String, String> listAvailableServices() throws SQLException
    {
        final String methodName = SecurityReferenceDAOImpl.CNAME + "#listAvailableServices() throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        CallableStatement stmt = null;
        Map<String, String> serviceMap = null;

        if (Objects.isNull(dataSource))
        {
        	throw new SQLException("A datasource connection could not be obtained.");
        }

        try
        {
            sqlConn = dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);
            stmt = sqlConn.prepareCall("{CALL retrAvailableServices()}", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

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
                    resultSet.beforeFirst();
                    serviceMap = new HashMap<String, String>();

                    while (resultSet.next())
                    {
                        serviceMap.put(resultSet.getString(1), resultSet.getString(2));
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Map<String, String>: {}", serviceMap);
                    }
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

        return serviceMap;
    }

    /**
     * @see com.cws.esolutions.security.dao.reference.interfaces.ISecurityReferenceDAO#listServicesForGroup(java.lang.String)
     */
    public synchronized List<String> listServicesForGroup(final String groupName) throws SQLException
    {
        final String methodName = SecurityReferenceDAOImpl.CNAME + "#listServicesForGroup(final String groupName) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", groupName);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        CallableStatement stmt = null;
        List<String> serviceList = null;

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

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);
            stmt = sqlConn.prepareCall("{CALL listServicesForGroup(?)}", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stmt.setString(1, groupName);

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
                    resultSet.first();
                    serviceList = new ArrayList<String>();

                    for (String service : StringUtils.split(resultSet.getString(1), ",")) // single row response
                    {
                        if (DEBUG)
                        {
                            DEBUGGER.debug("Service: {}", service);
                        }

                        serviceList.add(service);
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("List<String>: {}", serviceList);
                    }
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

        return serviceList;
    }
}
