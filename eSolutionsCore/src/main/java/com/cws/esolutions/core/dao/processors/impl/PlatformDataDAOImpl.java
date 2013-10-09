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
package com.cws.esolutions.core.dao.processors.impl;

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
 * eSolutionsCore
 * com.cws.esolutions.core.dao.sysmgmt.impl
 * ProjectDataImpl.java
 *
 * $Id: $
 * $Author: $
 * $Date: $
 * $Revision: $
 * @author kmhuntly@gmail.com
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Jan 8, 2013 11:17:08 AM
 *     Created.
 */
public class PlatformDataDAOImpl implements IPlatformDataDAO
{
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
            else
            {
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
            else
            {
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
            else
            {
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

    @Override
    public synchronized List<String> getPlatformData(final String platformGuid) throws SQLException
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
        List<String> responseData = null;

        try
        {
            sqlConn = dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("SQL Connection is not an instance of Connection or is null");
            }
            else
            {
                sqlConn.setAutoCommit(true);
                stmt = sqlConn.prepareCall("{CALL getPlatformData(?)}");
                stmt.setString(1, platformGuid);

                if (DEBUG)
                {
                    DEBUGGER.debug(stmt.toString());
                }

                resultSet = stmt.executeQuery();

                if (DEBUG)
                {
                    DEBUGGER.debug("resultSet: {}", resultSet);
                }

                if (resultSet.next())
                {
                    responseData = new ArrayList<String>(
                            Arrays.asList(
                                    resultSet.getString(1), // guid
                                    resultSet.getString(2), // name
                                    resultSet.getString(3), // region
                                    resultSet.getString(4), // dmgr
                                    resultSet.getString(5), // appserver list
                                    resultSet.getString(6), // webserver list
                                    resultSet.getString(7))); // description

                    if (DEBUG)
                    {
                        for (String str : responseData)
                        {
                            DEBUGGER.debug(str);
                        }
                    }
                }
                else
                {
                    throw new SQLException("No results were found for the provided project code and region");
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

    @Override
    public synchronized List<String[]> listAvailablePlatforms() throws SQLException
    {
        final String methodName = IPlatformDataDAO.CNAME + "#listAvailablePlatforms() throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
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
            else
            {
                sqlConn.setAutoCommit(true);

                stmt = sqlConn.prepareCall("{CALL listPlatforms()}");

                if (DEBUG)
                {
                    DEBUGGER.debug(stmt.toString());
                }

                resultSet = stmt.executeQuery();

                if (DEBUG)
                {
                    DEBUGGER.debug("resultSet: {}", resultSet);
                }

                if (resultSet.next())
                {
                    resultSet.beforeFirst();
                    platformList = new ArrayList<String[]>();

                    while (resultSet.next())
                    {
                        String[] data = new String[] {
                                resultSet.getString(1), // guid
                                resultSet.getString(2), // name
                                resultSet.getString(3), // region
                                resultSet.getString(4), // dmgr
                                resultSet.getString(5), // appserver list
                                resultSet.getString(6), // webserver list
                                resultSet.getString(7) // description
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

    @Override
    public synchronized List<String[]> listPlatformsByAttribute(final String value) throws SQLException
    {
        final String methodName = IPlatformDataDAO.CNAME + "#listPlatformsByAttribute(final String value) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
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
            else
            {
                sqlConn.setAutoCommit(true);

                stmt = sqlConn.prepareCall("{CALL getPlatformByAttribute(?)}");
                stmt.setString(1, value);

                if (DEBUG)
                {
                    DEBUGGER.debug(stmt.toString());
                }

                resultSet = stmt.executeQuery();

                if (DEBUG)
                {
                    DEBUGGER.debug("resultSet: {}", resultSet);
                }

                if (resultSet.next())
                {
                    resultSet.beforeFirst();
                    platformList = new ArrayList<String[]>();

                    while (resultSet.next())
                    {
                        String[] data = new String[] {
                            resultSet.getString(1), // guid
                            resultSet.getString(2), // name
                            resultSet.getString(3), // region
                            resultSet.getString(4), // dmgr
                            resultSet.getString(5), // appserver list
                            resultSet.getString(6), // webserver list
                            resultSet.getString(7) // description
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
