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
package com.cws.esolutions.core.dao.processors.impl;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.dao.interfaces
 * File: IPackageDataDAO.java
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
import java.sql.SQLException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;

import com.cws.esolutions.core.dao.processors.interfaces.IPlatformDataDAO;
/**
 * @see com.cws.esolutions.core.dao.processors.interfaces.IPlatformDataDAO
 */
public class PlatformDataDAOImpl implements IPlatformDataDAO
{
    /**
     * @see com.cws.esolutions.core.dao.processors.interfaces.IPlatformDataDAO#addNewPlatform(java.util.List)
     */
    @Override
    public synchronized boolean addNewPlatform(final List<String> platformData) throws SQLException
    {
        final String methodName = IPlatformDataDAO.CNAME + "#addNewPlatform(final List<String> platformData) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);

            for (String str : platformData)
            {
                DEBUGGER.debug(str);
            }
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

            stmt = sqlConn.prepareCall("{CALL addNewPlatform(?, ?, ?, ?, ?, ?, ?, ?)}");
            stmt.setString(1, platformData.get(0)); // platform guid
            stmt.setString(2, platformData.get(1)); // platform name
            stmt.setString(3, platformData.get(2)); // platform region
            stmt.setString(4, platformData.get(3)); // platform dmgr (single, could be null)
            stmt.setString(5, platformData.get(4)); // platform appservers (list, could be null)
            stmt.setString(6, platformData.get(5)); // platform webservers (list, could be null)
            stmt.setString(7, platformData.get(6)); // status
            stmt.setString(8, platformData.get(7)); // platform description

            if (DEBUG)
            {
                DEBUGGER.debug(stmt.toString());
            }

            isComplete = (!(stmt.execute()));

            if (DEBUG)
            {
                DEBUGGER.debug("isComplete: {}", isComplete);
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

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
     * @see com.cws.esolutions.core.dao.processors.interfaces.IPlatformDataDAO#deletePlatform(java.lang.String)
     */
    @Override
    public synchronized boolean deletePlatform(final String platformGuid) throws SQLException
    {
        final String methodName = IPlatformDataDAO.CNAME + "#deletePlatform(final String platformGuid) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug(platformGuid);
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

            stmt = sqlConn.prepareCall("{CALL removePlatformData(?)}");
            stmt.setString(1, platformGuid);

            if (DEBUG)
            {
                DEBUGGER.debug(stmt.toString());
            }

            isComplete = (!(stmt.execute()));

            if (DEBUG)
            {
                DEBUGGER.debug("isComplete: {}", isComplete);
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

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
     * @see com.cws.esolutions.core.dao.processors.interfaces.IPlatformDataDAO#updatePlatformData(java.util.List)
     */
    @Override
    public synchronized boolean updatePlatformData(final List<String> platformData) throws SQLException
    {
        final String methodName = IPlatformDataDAO.CNAME + "#updatePlatformData(final List<String> platformData) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);

            for (String str : platformData)
            {
                DEBUGGER.debug(str);
            }
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

            stmt = sqlConn.prepareCall("{CALL updatePlatformDetail(?, ?, ?, ?, ?, ?, ?, ?)}");
            stmt.setString(1, platformData.get(0)); // platform guid
            stmt.setString(2, platformData.get(1)); // platform name
            stmt.setString(3, platformData.get(2)); // platform region
            stmt.setString(4, platformData.get(3)); // platform dmgr (single, could be null)
            stmt.setString(5, platformData.get(4)); // platform appservers (list, could be null)
            stmt.setString(6, platformData.get(5)); // platform webservers (list, could be null)
            stmt.setString(7, platformData.get(6)); // status
            stmt.setString(8, platformData.get(7)); // platform description

            if (DEBUG)
            {
                DEBUGGER.debug(stmt.toString());
            }

            isComplete = (!(stmt.execute()));

            if (DEBUG)
            {
                DEBUGGER.debug("isComplete: {}", isComplete);
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

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
     * @see com.cws.esolutions.core.dao.processors.interfaces.IPlatformDataDAO#getPlatformData(java.lang.String)
     */
    @Override
    public synchronized List<Object> getPlatformData(final String platformGuid) throws SQLException
    {
        final String methodName = IPlatformDataDAO.CNAME + "#getPlatformData(final String platformGuid) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug(platformGuid);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        PreparedStatement stmt = null;
        List<Object> responseData = null;

        try
        {
            sqlConn = dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("SQL Connection is not an instance of Connection or is null");
            }

            sqlConn.setAutoCommit(true);
            stmt = sqlConn.prepareCall("{CALL getPlatformData(?)}");
            stmt.setString(1, platformGuid);

            if (DEBUG)
            {
                DEBUGGER.debug(stmt.toString());
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
                    responseData = new ArrayList<Object>(
                            Arrays.asList(
                                    resultSet.getString(1), // T1.PLATFORM_GUID
                                    resultSet.getString(2), // T1.PLATFORM_NAME
                                    resultSet.getString(3), // T1.PLATFORM_REGION
                                    resultSet.getString(4), // T1.PLATFORM_APPSERVERS
                                    resultSet.getString(5), // T1.PLATFORM_WEBSERVERS
                                    resultSet.getString(6), // T1.PLATFORM_DESC
                                    resultSet.getString(7), // T2.SYSTEM_GUID
                                    resultSet.getString(8), // T2.SYSTEM_OSTYPE
                                    resultSet.getString(9), // T2.SYSTEM_STATUS
                                    resultSet.getString(10), // T2.NETWORK_PARTITION
                                    resultSet.getString(11), // T2.DOMAIN_NAME
                                    resultSet.getString(12), // T2.CPU_TYPE
                                    resultSet.getInt(13), // T2.CPU_COUNT
                                    resultSet.getString(14), // T2.SERVER_RACK
                                    resultSet.getString(15), // T2.RACK_POSITION
                                    resultSet.getString(16), // T2.SERVER_MODEL
                                    resultSet.getString(17), // T2.SERIAL_NUMBER
                                    resultSet.getInt(18), // T2.INSTALLED_MEMORY
                                    resultSet.getString(19), // T2.OPER_IP
                                    resultSet.getString(20), // T2.OPER_HOSTNAME
                                    resultSet.getString(21), // T2.MGMT_IP
                                    resultSet.getString(22), // T2.MGMT_HOSTNAME
                                    resultSet.getString(23), // T2.BKUP_IP
                                    resultSet.getString(24), // T2.BKUP_HOSTNAME
                                    resultSet.getString(25), // T2.NAS_IP
                                    resultSet.getString(26), // T2.NAS_HOSTNAME
                                    resultSet.getString(27), // T2.NAT_ADDR
                                    resultSet.getString(28), // T2.COMMENTS
                                    resultSet.getString(29), // T2.ASSIGNED_ENGINEER
                                    resultSet.getInt(30), // T2.DMGR_PORT
                                    resultSet.getString(31), // T2.MGR_ENTRY
                                    resultSet.getString(32), // T3.DATACENTER_GUID
                                    resultSet.getString(33), // T3.DATACENTER_NAME
                                    resultSet.getString(34), // T3.DATACENTER_STATUS
                                    resultSet.getString(35))); // T3.DATACENTER_DESC

                    if (DEBUG)
                    {
                        for (Object obj : responseData)
                        {
                            DEBUGGER.debug("Value: {}", obj);
                        }
                    }
                }
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

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

            if ((!(sqlConn == null) && (!(sqlConn.isClosed()))))
            {
                sqlConn.close();
            }
        }

        return responseData;
    }

    /**
     * @see com.cws.esolutions.core.dao.processors.interfaces.IPlatformDataDAO#getPlatformCount()
     */
    @Override
    public synchronized int getPlatformCount() throws SQLException
    {
        final String methodName = IPlatformDataDAO.CNAME + "#getPlatformCount() throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        int count = 0;
        Connection sqlConn = null;
        ResultSet resultSet = null;
        CallableStatement stmt = null;

        try
        {
            sqlConn = dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);
            stmt = sqlConn.prepareCall("{ CALL getPlatformCount() }");

            if (DEBUG)
            {
                DEBUGGER.debug("stmt: {}", stmt);
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

                    count = resultSet.getInt(1);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("count: {}", count);
                    }
                }
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new SQLException(sqx.getMessage(), sqx);
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

        return count;
    }

    /**
     * @see com.cws.esolutions.core.dao.processors.interfaces.IPlatformDataDAO#listAvailablePlatforms(int)
     */
    @Override
    public synchronized List<String[]> listAvailablePlatforms(final int startRow) throws SQLException
    {
        final String methodName = IPlatformDataDAO.CNAME + "#listAvailablePlatforms(final int startRow) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", startRow);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        CallableStatement stmt = null;
        List<String[]> platformList = null;

        try
        {
            sqlConn = dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);

            stmt = sqlConn.prepareCall("{CALL listPlatforms(?)}");
            stmt.setInt(1, startRow);

            if (DEBUG)
            {
                DEBUGGER.debug(stmt.toString());
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
                    platformList = new ArrayList<>();

                    while (resultSet.next())
                    {
                        String[] data = new String[]
                        {
                            resultSet.getString(1), // PLATFORM_GUID
                            resultSet.getString(2) // PLATFORM_NAME
                        };

                        if (DEBUG)
                        {
                            for (String str : data)
                            {
                                DEBUGGER.debug("data: {}", str);
                            }
                        }

                        platformList.add(data);
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("platformList: {}", platformList);
                    }
                }
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

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

        return platformList;
    }

    /**
     * @see com.cws.esolutions.core.dao.processors.interfaces.IPlatformDataDAO#listPlatformsByAttribute(java.lang.String, int)
     */
    @Override
    public synchronized List<String[]> listPlatformsByAttribute(final String value, final int startRow) throws SQLException
    {
        final String methodName = IPlatformDataDAO.CNAME + "#listPlatformsByAttribute(final String value, final int startRow) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
            DEBUGGER.debug("Value: {}", startRow);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        CallableStatement stmt = null;
        List<String[]> platformList = null;

        try
        {
            sqlConn = dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);

            stmt = sqlConn.prepareCall("{CALL getPlatformByAttribute(?, ?)}");
            stmt.setString(1, value);
            stmt.setInt(2, startRow);

            if (DEBUG)
            {
                DEBUGGER.debug(stmt.toString());
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
                    platformList = new ArrayList<>();

                    while (resultSet.next())
                    {
                        String[] data = new String[]
                        {
                            resultSet.getString(1), // PLATFORM_GUID
                            resultSet.getString(2) // PLATFORM_NAME
                        };

                        if (DEBUG)
                        {
                            for (String str : data)
                            {
                                DEBUGGER.debug("data: {}", str);
                            }
                        }

                        platformList.add(data);
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("platformList: {}", platformList);
                    }
                }
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

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

        return platformList;
    }
}
