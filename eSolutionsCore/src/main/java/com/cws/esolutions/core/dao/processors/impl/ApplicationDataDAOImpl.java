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
import java.util.Arrays;
import java.util.List;
import java.sql.ResultSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.sql.SQLException;
import java.sql.CallableStatement;

import com.cws.esolutions.core.dao.processors.interfaces.IApplicationDataDAO;
/**
 * @see com.cws.esolutions.core.dao.processors.interfaces.IApplicationDataDAO
 */
public class ApplicationDataDAOImpl implements IApplicationDataDAO
{
    /**
     * @see com.cws.esolutions.core.dao.processors.interfaces.IApplicationDataDAO#addNewApplication(java.util.List)
     */
    @Override
    public synchronized boolean addNewApplication(final List<String> value) throws SQLException
    {
        final String methodName = IApplicationDataDAO.CNAME + "#addNewApplication(final List<String> value) throws SQLException";
        
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
            stmt = sqlConn.prepareCall("{CALL insertNewApplication(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
            stmt.setString(1, value.get(0)); // appGuid
            stmt.setString(2, value.get(1)); // appName
            stmt.setString(3, value.get(2)); // appVersion
            stmt.setString(4, value.get(3)); // basePath
            stmt.setString(5, value.get(4)); // scm path
            stmt.setString(6, value.get(5)); // clusterName
            stmt.setString(7, value.get(6)); // jvmName
            stmt.setString(8, value.get(8)); // installPath
            stmt.setString(9, value.get(8)); // logsDir
            stmt.setString(10, value.get(9)); // pidDir
            stmt.setString(11, value.get(10)); // projectGuid
            stmt.setString(12, value.get(11)); // platformGuid

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
     * @see com.cws.esolutions.core.dao.processors.interfaces.IApplicationDataDAO#updateApplication(java.util.List)
     */
    @Override
    public synchronized boolean updateApplication(final List<String> value) throws SQLException
    {
        final String methodName = IApplicationDataDAO.CNAME + "#updateApplication(final List<String> value) throws SQLException";
        
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
            stmt = sqlConn.prepareCall("{CALL updateApplicationData(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
            stmt.setString(1, value.get(0)); // appGuid
            stmt.setString(2, value.get(1)); // appName
            stmt.setString(3, value.get(2)); // appVersion
            stmt.setString(4, value.get(3)); // basePath
            stmt.setString(5, value.get(4)); // scm path
            stmt.setString(6, value.get(5)); // clusterName
            stmt.setString(7, value.get(6)); // jvmName
            stmt.setString(8, value.get(8)); // installPath
            stmt.setString(9, value.get(8)); // logsDir
            stmt.setString(10, value.get(9)); // pidDir
            stmt.setString(11, value.get(10)); // projectGuid
            stmt.setString(12, value.get(11)); // platformGuid

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
     * @see com.cws.esolutions.core.dao.processors.interfaces.IApplicationDataDAO#deleteApplication(java.lang.String)
     */
    @Override
    public synchronized boolean deleteApplication(final String value) throws SQLException
    {
        final String methodName = IApplicationDataDAO.CNAME + "#deleteApplication(final String value) throws SQLException";
        
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
            stmt = sqlConn.prepareCall("{CALL removeApplicationData(?)}");
            stmt.setString(1, value); // systemGuid

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
     * @see com.cws.esolutions.core.dao.processors.interfaces.IApplicationDataDAO#getApplicationCount()
     */
    @Override
    public synchronized int getApplicationCount() throws SQLException
    {
        final String methodName = IApplicationDataDAO.CNAME + "#getApplicationCount() throws SQLException";

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
            stmt = sqlConn.prepareCall("{CALL getApplicationCount()}");

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
     * @see com.cws.esolutions.core.dao.processors.interfaces.IApplicationDataDAO#listInstalledApplications(int)
     */
    @Override
    public synchronized List<String[]> listInstalledApplications(final int startRow) throws SQLException
    {
        final String methodName = IApplicationDataDAO.CNAME + "#listInstalledApplications(final int startRow) throws SQLException";

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

            stmt = sqlConn.prepareCall("{CALL listApplications(?)}");
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
                            resultSet.getString(1), // T1.APPLICATION_GUID
                            resultSet.getString(2), // T1.APPLICATION_NAME
                            resultSet.getString(3), // T2.PROJECT_GUID
                            resultSet.getString(4), // T2.PROJECT_NAME
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
     * @see com.cws.esolutions.core.dao.processors.interfaces.IApplicationDataDAO#getApplicationData(java.lang.String)
     */
    @Override
    public synchronized List<String> getApplicationData(final String value) throws SQLException
    {
        final String methodName = IApplicationDataDAO.CNAME + "#getInstalledServer(final String value) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("value: {}", value);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        CallableStatement stmt = null;
        List<String> responseData = null;

        try
        {
            sqlConn = dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);
            stmt = sqlConn.prepareCall("{CALL getApplicationData(?)}");
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

                    responseData = new ArrayList<>
                    (
                        Arrays.asList
                        (
                            resultSet.getString(1), // T1.APPLICATION_GUID
                            resultSet.getString(2), // T1.APPLICATION_NAME
                            resultSet.getString(3), // T1.APPLICATION_VERSION
                            resultSet.getString(4), // T1.BASE_PATH
                            resultSet.getString(5), // T1.SCM_PATH
                            resultSet.getString(6), // T1.CLUSTER_NAME
                            resultSet.getString(7), // T1.JVM_NAME
                            resultSet.getString(8), // T1.INSTALL_PATH
                            resultSet.getString(9), // T1.LOGS_DIRECTORY
                            resultSet.getString(10), // T1.PID_DIRECTORY
                            resultSet.getString(11), // T1.PLATFORM_GUID
                            resultSet.getString(12), // T2.PROJECT_GUID
                            resultSet.getString(13) // T2.PROJECT_NAME
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
     * @see com.cws.esolutions.core.dao.processors.interfaces.IApplicationDataDAO#getApplicationsByAttribute(java.lang.String, int)
     */
    @Override
    public synchronized List<String[]> getApplicationsByAttribute(final String value, final int startRow) throws SQLException
    {
        final String methodName = IApplicationDataDAO.CNAME + "#getApplicationsByAttribute(final String value, final int startRow) throws SQLException";

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
                            resultSet.getString(1), // T1.APPLICATION_GUID
                            resultSet.getString(2), // T1.APPLICATION_NAME
                            resultSet.getString(3), // T2.PROJECT_GUID
                            resultSet.getString(4), // T2.PROJECT_NAME
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
