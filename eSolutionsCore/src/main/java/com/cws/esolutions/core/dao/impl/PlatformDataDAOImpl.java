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
package com.cws.esolutions.core.dao.impl;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.dao.interfaces
 * File: IPackageDataDAO.java
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
import java.util.Objects;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;

import com.cws.esolutions.core.dao.interfaces.IPlatformDataDAO;
/**
 * @see com.cws.esolutions.core.dao.interfaces.IPlatformDataDAO
 */
public class PlatformDataDAOImpl implements IPlatformDataDAO
{
    /**
     * @see com.cws.esolutions.core.dao.interfaces.IPlatformDataDAO#addServer(java.util.List)
     */
    public synchronized boolean addPlatform(final List<String> platformData) throws SQLException
    {
        final String methodName = IPlatformDataDAO.CNAME + "#addPlatform(final List<String> platformData) throws SQLException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", platformData);
        }

        Connection sqlConn = null;
        boolean isComplete = false;
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

            stmt = sqlConn.prepareCall("{ CALL addNewPlatform(?, ?, ?, ?, ?) }", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            stmt.setString(1, platformData.get(0)); // guid
            stmt.setString(2, platformData.get(1)); // name
            stmt.setString(3, platformData.get(2)); // status
            stmt.setString(4, platformData.get(3)); // description
            stmt.registerOutParameter(5, Types.INTEGER);

            if (DEBUG)
            {
                DEBUGGER.debug("PreparedStatement: {}", stmt);
            }

            stmt.execute();
            int updateCount = stmt.getInt(5);

            if (DEBUG)
            {
            	DEBUGGER.debug("updateCount: {}", updateCount);
            }

            if (updateCount == 1)
            {
            	isComplete = true;
            }

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

            if ((sqlConn != null) && (!(sqlConn.isClosed())))
            {
                sqlConn.close();
            }
        }

        return isComplete;
    }

    /**
     * @see com.cws.esolutions.core.dao.interfaces.IPlatformDataDAO#updateServer(java.lang.String, java.util.List)
     */
    public synchronized boolean updatePlatform(final String platformGuid, final List<String> platformData) throws SQLException
    {
        final String methodName = IPlatformDataDAO.CNAME + "#updatePlatform(final String platformGuid, final List<String> platformData) throws SQLException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", platformGuid);
            DEBUGGER.debug("Value: {}", platformData);
        }

        Connection sqlConn = null;
        boolean isComplete = false;
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

            stmt = sqlConn.prepareCall("{ CALL updatePlatformData(?, ?, ?, ?, ?) }", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stmt.setString(1, platformGuid); // systemGuid
            stmt.setString(2, platformData.get(0)); // platformName
            stmt.setString(3, platformData.get(1)); // platformstatus
            stmt.setString(4, platformData.get(2)); // description
            stmt.registerOutParameter(5, Types.INTEGER);

            if (DEBUG)
            {
                DEBUGGER.debug("PreparedStatement: {}", stmt);
            }

            stmt.execute();
            int updateCount = stmt.getInt(5);

            if (DEBUG)
            {
            	DEBUGGER.debug("updateCount: {}", updateCount);
            }

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
     * @see com.cws.esolutions.core.dao.interfaces.IPlatformDataDAO#listServers(int)
     */
    public synchronized List<String[]> listPlatforms(final int startRow) throws SQLException
    {
        final String methodName = IPlatformDataDAO.CNAME + "#listPlatforms(final int startRow) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", startRow);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        PreparedStatement stmt = null;
        List<String[]> responseData = null;

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

            stmt = sqlConn.prepareStatement("{ CALL listPlatforms(?) }", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stmt.setInt(1, startRow);

            if (DEBUG)
            {
                DEBUGGER.debug("PreparedStatement: {}", stmt);
            }

            if (stmt.execute())
            {
                resultSet = stmt.getResultSet();

                if (DEBUG)
                {
                    DEBUGGER.debug("resultSet: {}", resultSet);
                }

                if (resultSet.next())
                {
                    resultSet.beforeFirst();
                    responseData = new ArrayList<String[]>();

                    while (resultSet.next())
                    {
                        String[] serverData = new String[]
                        {
                                resultSet.getString(1), // GUID
                                resultSet.getString(2) // NAME
                        };

                        if (DEBUG)
                        {
                            DEBUGGER.debug("serverData: {}", (Object) serverData);
                        }

                        responseData.add(serverData);
                    }

                    if (DEBUG)
                    {
                        for (Object[] objArr : responseData)
                        {
                            for (Object obj : objArr)
                            {
                                DEBUGGER.debug("Value: {}", obj);
                            }
                        }
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

        return responseData;
    }

    /**
     * @see com.cws.esolutions.core.dao.interfaces.IPlatformDataDAO#getServersByAttribute(java.lang.String, int)
     */
    public synchronized List<Object[]> getPlatformsByAttribute(final String value, final int startRow) throws SQLException
    {
        final String methodName = IPlatformDataDAO.CNAME + "#getPlatformsByAttribute(final String value, final int startRow) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
            DEBUGGER.debug("Value: {}", startRow);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        PreparedStatement stmt = null;
        List<Object[]> responseData = null;

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

            stmt = sqlConn.prepareStatement("{ CALL getPlatformByAttribute(?, ?) }", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stmt.setString(1, value);
            stmt.setInt(2, startRow);

            if (DEBUG)
            {
                DEBUGGER.debug("PreparedStatement: {}", stmt);
            }

            if (stmt.execute())
            {
                resultSet = stmt.getResultSet();

                if (DEBUG)
                {
                    DEBUGGER.debug("resultSet: {}", resultSet);
                }

                if (resultSet.next())
                {
                    resultSet.beforeFirst();
                    responseData = new ArrayList<Object[]>();

                    while (resultSet.next())
                    {
                        Object[] data = new Object[]
                        {
                            resultSet.getString(1), // GUID
                            resultSet.getString(2) // OPER_HOSTNAME
                        };

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Value: {}", data);
                        }

                        responseData.add(data);
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Value: {}", responseData);
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

        return responseData;
    }

    /**
     * @see com.cws.esolutions.core.dao.interfaces.IPlatformDataDAO#getServer(java.lang.String)
     */
    public synchronized List<String> getPlatform(final String attribute) throws SQLException
    {
        final String methodName = IPlatformDataDAO.CNAME + "#getPlatform(final String attribute) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("attribute: {}", attribute);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        PreparedStatement stmt = null;
        List<String> responseData = null;

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

            // we dont know what we have here - it could be a guid or it could be a hostname
            // most commonly it'll be a guid, but we're going to search anyway
            stmt = sqlConn.prepareStatement("{ CALL getPlatformData(?) }", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stmt.setString(1, attribute);

            if (DEBUG)
            {
                DEBUGGER.debug("PreparedStatement: {}", stmt);
            }

            if (stmt.execute())
            {
                resultSet = stmt.getResultSet();

                if (DEBUG)
                {
                    DEBUGGER.debug("resultSet: {}", resultSet);
                }

                if (resultSet.next())
                {
                    resultSet.first();

                    responseData = new ArrayList<String>(
                            Arrays.asList(
                                    resultSet.getString(1), // GUID
                                    resultSet.getString(2), // PLATFORMNAME
                                    resultSet.getString(3), // PLATFORMSTATUS
                                    resultSet.getString(4)));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("responseData: {}", responseData);
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

        return responseData;
    }

    /**
     * @see com.cws.esolutions.core.dao.interfaces.IPlatformDataDAO#removeServer(java.lang.String)
     */
    public synchronized boolean removePlatform(final String platformGuid) throws SQLException
    {
        final String methodName = IPlatformDataDAO.CNAME + "#removePlatform(final String platformGuid) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", platformGuid);
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

            stmt = sqlConn.prepareStatement("{ CALL removePlatformData(?) }", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stmt.setString(1, platformGuid);

            if (DEBUG)
            {
                DEBUGGER.debug("PreparedStatement: {}", stmt);
            }

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

            if ((sqlConn != null) && (!(sqlConn.isClosed())))
            {
                sqlConn.close();
            }
        }

        return isComplete;
    }
}
