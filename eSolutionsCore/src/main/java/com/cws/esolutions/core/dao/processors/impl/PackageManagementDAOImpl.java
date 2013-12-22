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
 * File: PackageManagementDAOImpl.java
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
import java.sql.Connection;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.CallableStatement;

import com.cws.esolutions.core.dao.processors.interfaces.IPackageManagementDAO;
/**
 * @see com.cws.esolutions.core.dao.processors.interfaces.IPackageManagementDAO
 */
public class PackageManagementDAOImpl implements IPackageManagementDAO
{
    /**
     * @see com.cws.esolutions.core.dao.processors.interfaces.IPackageManagementDAO#addNewPackage(java.util.List)
     */
    @Override
    public synchronized boolean addNewPackage(final List<Object> value) throws SQLException
    {
        final String methodName = IPackageManagementDAO.CNAME + "#addNewPackage(final List<Object> value) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);

            for (String str : value)
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
            stmt = sqlConn.prepareCall("{CALL insertNewPackage(?, ?, ?, ?, ?)}");
            stmt.setString(1, (String) value.get(0)); // PACKAGE_GUID
            stmt.setString(2, (String) value.get(1)); // PACKAGE_NAME
            stmt.setBigDecimal(3, (BigDecimal) value.get(2)); // PACKAGE_VERSION
            stmt.setString(4, (String) value.get(3)); // PACKAGE_LOCATION
            stmt.setString(5, (String) value.get(4)); // PACKAGE_INSTALLER

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
     * @see com.cws.esolutions.core.dao.processors.interfaces.IPackageManagementDAO#updatePackage(java.util.List)
     */
    @Override
    public synchronized boolean updatePackage(final List<Object> value) throws SQLException
    {
        final String methodName = IPackageManagementDAO.CNAME + "#updatePackage(final List<Object> value) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);

            for (String str : value)
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
            stmt = sqlConn.prepareCall("{CALL updatePackageData(?, ?, ?, ?, ?)}");
            stmt.setString(1, (String) value.get(0)); // PACKAGE_GUID
            stmt.setString(2, (String) value.get(1)); // PACKAGE_NAME
            stmt.setBigDecimal(3, (BigDecimal) value.get(2)); // PACKAGE_VERSION
            stmt.setString(4, (String) value.get(3)); // PACKAGE_LOCATION
            stmt.setString(5, (String) value.get(4)); // PACKAGE_INSTALLER

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
     * @see com.cws.esolutions.core.dao.processors.interfaces.IPackageManagementDAO#deletePackage(java.lang.String)
     */
    @Override
    public synchronized boolean deletePackage(final String value) throws SQLException
    {
        final String methodName = IPackageManagementDAO.CNAME + "#deletePackage(final String value) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
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
            stmt = sqlConn.prepareCall("{CALL removePackageData(?)}");
            stmt.setString(1, value); // PACKAGE_GUID

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
     * @see com.cws.esolutions.core.dao.processors.interfaces.IPackageManagementDAO#getPackageCount()
     */
    @Override
    public synchronized int getPackageCount() throws SQLException
    {
        final String methodName = IPackageManagementDAO.CNAME + "#getPackageCount() throws SQLException";

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
            stmt = sqlConn.prepareCall("{CALL getPackageCount()}");

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

        return count;
    }

    /**
     * @see com.cws.esolutions.core.dao.processors.interfaces.IPackageManagementDAO#listInstalledPackages(int)
     */
    @Override
    public synchronized List<String[]> listInstalledPackages(final int startRow) throws SQLException
    {
        final String methodName = IPackageManagementDAO.CNAME + "#listInstalledPackages(final int startRow) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", startRow);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        CallableStatement stmt = null;
        List<String[]> responseData = null;

        try
        {
            sqlConn = dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);

            stmt = sqlConn.prepareCall("{CALL listPackages(?)}");
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
                    responseData = new ArrayList<>();

                    while (resultSet.next())
                    {
                        String[] data = new String[]
                        {
                            resultSet.getString(1), // PACKAGE_GUID
                            resultSet.getString(2) // PACKAGE_NAME
                        };

                        if (DEBUG)
                        {
                            for (String str : data)
                            {
                                DEBUGGER.debug(str);
                            }
                        }

                        responseData.add(data);
                    }

                    if (DEBUG)
                    {
                        for (String[] str : responseData)
                        {
                            for (String str1 : str)
                            {
                                DEBUGGER.debug(str1);
                            }
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

            if ((sqlConn != null) && (!(sqlConn.isClosed())))
            {
                sqlConn.close();
            }
        }

        return responseData;
    }

    /**
     * @see com.cws.esolutions.core.dao.processors.interfaces.IPackageManagementDAO#getPackageData(java.lang.String)
     */
    @Override
    public synchronized List<Object> getPackageData(final String value) throws SQLException
    {
        final String methodName = IPackageManagementDAO.CNAME + "#getPackageData(final String value) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("value: {}", value);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        CallableStatement stmt = null;
        List<Object> responseData = null;

        try
        {
            sqlConn = dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);
            stmt = sqlConn.prepareCall("{CALL getPackageData(?)}");
            stmt.setString(1, value);

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
                    resultSet.first();

                    responseData = new ArrayList<Object>
                    (
                        Arrays.asList
                        (
                            resultSet.getString(1), // PACKAGE_GUID
                            resultSet.getString(2), // PACKAGE_NAME
                            resultSet.getBigDecimal(3), // PACKAGE_VERSION
                            resultSet.getString(4), // PACKAGE_LOCATION
                            resultSet.getString(5) // PACKAGE_INSTALLER
                        )
                    );

                    if (DEBUG)
                    {
                        for (String data : responseData)
                        {
                            DEBUGGER.debug("data: {}", data);
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

            if ((sqlConn != null) && (!(sqlConn.isClosed())))
            {
                sqlConn.close();
            }
        }

        return responseData;
    }

    /**
     * @see com.cws.esolutions.core.dao.processors.interfaces.IPackageManagementDAO#getPackagesByAttribute(java.lang.String, int)
     */
    @Override
    public synchronized List<String[]> getPackagesByAttribute(final String value, final int startRow) throws SQLException
    {
        final String methodName = IPackageManagementDAO.CNAME + "#getPackagesByAttribute(final String value, final int startRow) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
            DEBUGGER.debug("Value: {}", startRow);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        CallableStatement stmt = null;
        List<String[]> responseData = null;

        try
        {
            sqlConn = dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);
            stmt = sqlConn.prepareCall("{CALL getApplicationByAttribute(?, ?)}");
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
                    responseData = new ArrayList<>();

                    while (resultSet.next())
                    {
                        String[] data = new String[]
                        {
                            resultSet.getString(1), // PACKAGE_GUID
                            resultSet.getString(2) // PACKAGE_NAME
                        };

                        if (DEBUG)
                        {
                            for (String str : data)
                            {
                                DEBUGGER.debug(str);
                            }
                        }

                        responseData.add(data);
                    }

                    if (DEBUG)
                    {
                        for (String[] str : responseData)
                        {
                            for (String str1 : str)
                            {
                                DEBUGGER.debug(str1);
                            }
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

            if ((sqlConn != null) && (!(sqlConn.isClosed())))
            {
                sqlConn.close();
            }
        }

        return responseData;
    }
}
