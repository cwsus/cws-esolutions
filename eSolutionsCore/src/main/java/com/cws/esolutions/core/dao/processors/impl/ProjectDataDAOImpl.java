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

import java.util.List;
import java.util.Arrays;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;

import com.cws.esolutions.core.dao.processors.interfaces.IProjectDataDAO;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.dao.interfaces
 * File: IPackageDataDAO.java
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Jan 4, 2013 3:36:54 PM
 *     Created.
 */
/**
 * @see com.cws.esolutions.core.dao.processors.interfaces.IProjectDataDAO
 */
public class ProjectDataDAOImpl implements IProjectDataDAO
{
    /**
     * @see com.cws.esolutions.core.dao.processors.interfaces.IProjectDataDAO#addNewProject(java.util.List)
     */
    @Override
    public synchronized boolean addNewProject(final List<String> projectDetail) throws SQLException
    {
        final String methodName = IProjectDataDAO.CNAME + "#addNewProject(final List<String> projectDetail) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);

            for (String str : projectDetail)
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

            stmt = sqlConn.prepareCall("{CALL insertNewProject(?, ?, ?, ?, ?, ?, ?, ?, ?)}");
            stmt.setString(1, projectDetail.get(0)); // project guid
            stmt.setString(2, projectDetail.get(1)); // project code
            stmt.setString(3, projectDetail.get(2)); // project status
            stmt.setString(4, projectDetail.get(3)); // primary owner
            stmt.setString(5, projectDetail.get(4)); // secondary owner, could be null
            stmt.setString(6, projectDetail.get(5)); // dev email
            stmt.setString(7, projectDetail.get(6)); // prod email
            stmt.setString(8, projectDetail.get(7)); // incident q
            stmt.setString(9, projectDetail.get(8)); // change q

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
     * @see com.cws.esolutions.core.dao.processors.interfaces.IProjectDataDAO#deleteProjectData(java.lang.String)
     */
    @Override
    public synchronized boolean deleteProjectData(final String projectName) throws SQLException
    {
        final String methodName = IProjectDataDAO.CNAME + "#deleteProjectData(final String projectName) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug(projectName);
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

            stmt = sqlConn.prepareCall("{CALL removeProjectData(?)}");
            stmt.setString(1, projectName);

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
     * @see com.cws.esolutions.core.dao.processors.interfaces.IProjectDataDAO#updateProjectData(java.util.List)
     */
    @Override
    public synchronized boolean updateProjectData(final List<String> projectDetail) throws SQLException
    {
        final String methodName = IProjectDataDAO.CNAME + "#updateProjectData(final List<String> projectDetail) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);

            for (String str : projectDetail)
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

            stmt = sqlConn.prepareCall("{CALL updateProjectDetail(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
            stmt.setString(1, projectDetail.get(0)); // project guid
            stmt.setString(2, projectDetail.get(1)); // project code
            stmt.setString(3, projectDetail.get(2)); // project status
            stmt.setString(4, projectDetail.get(3)); // primary owner
            stmt.setString(5, projectDetail.get(4)); // secondary owner, could be null
            stmt.setString(6, projectDetail.get(5)); // dev email
            stmt.setString(7, projectDetail.get(6)); // prod email
            stmt.setString(8, projectDetail.get(7)); // incident q
            stmt.setString(9, projectDetail.get(8)); // change q

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
     * @see com.cws.esolutions.core.dao.processors.interfaces.IProjectDataDAO#getProjectData(java.lang.String)
     */
    @Override
    public synchronized List<String> getProjectData(final String projectGuid) throws SQLException
    {
        final String methodName = IProjectDataDAO.CNAME + "#getProjectData(final String projectGuid) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug(projectGuid);
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

            sqlConn.setAutoCommit(true);
            stmt = sqlConn.prepareCall("{CALL getProjectData(?)}");
            stmt.setString(1, projectGuid);

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
                    responseData = new ArrayList<>(
                            Arrays.asList(
                                    resultSet.getString(1), // project guid
                                    resultSet.getString(2), // project code
                                    resultSet.getString(3), // project status
                                    resultSet.getString(4), // primary owner
                                    resultSet.getString(5), // secondary owner
                                    resultSet.getString(6), // dev email
                                    resultSet.getString(7), // prod email
                                    resultSet.getString(8), // incident q
                                    resultSet.getString(9))); // chg q

                    if (DEBUG)
                    {
                        for (String str : responseData)
                        {
                            DEBUGGER.debug(str);
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
     * @see com.cws.esolutions.core.dao.processors.interfaces.IProjectDataDAO#getProjectCount()
     */
    @Override
    public synchronized int getProjectCount() throws SQLException
    {
        final String methodName = IProjectDataDAO.CNAME + "#getProjectCount() throws SQLException";

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

            stmt = sqlConn.prepareCall("{CALL getProjectCount()}");

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
     * @see com.cws.esolutions.core.dao.processors.interfaces.IProjectDataDAO#listAvailableProjects(int)
     */
    @Override
    public synchronized List<String[]> listAvailableProjects(final int startRow) throws SQLException
    {
        final String methodName = IProjectDataDAO.CNAME + "#listAvailableProjects(final int startRow) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", startRow);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        CallableStatement stmt = null;
        List<String[]> projectList = null;

        try
        {
            sqlConn = dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);

            stmt = sqlConn.prepareCall("{CALL listProjects(?)}");
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
                    projectList = new ArrayList<>();

                    while (resultSet.next())
                    {
                        String[] data = new String[] {
                                resultSet.getString(1), // project guid
                                resultSet.getString(2), // project code
                                resultSet.getString(3), // project status
                                resultSet.getString(4), // primary owner
                                resultSet.getString(5), // secondary owner
                                resultSet.getString(6), // dev email
                                resultSet.getString(7), // prod email
                                resultSet.getString(8), // incident q
                                resultSet.getString(9) // chg q
                        };

                        if (DEBUG)
                        {
                            for (String str : data)
                            {
                                DEBUGGER.debug("data: {}", str);
                            }
                        }

                        projectList.add(data);
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("projectList: {}", projectList);
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

        return projectList;
    }

    /**
     * @see com.cws.esolutions.core.dao.processors.interfaces.IProjectDataDAO#getProjectsByAttribute(java.lang.String, int)
     */
    @Override
    public synchronized List<String[]> getProjectsByAttribute(final String attribute, final int startRow) throws SQLException
    {
        final String methodName = IProjectDataDAO.CNAME + "#getProjectsByAttribute(final String attribute, final int startRow)  throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", attribute);
            DEBUGGER.debug("Value: {}", startRow);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        CallableStatement stmt = null;
        List<String[]> responseList = null;

        try
        {
            sqlConn = dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);

            stmt = sqlConn.prepareCall("{CALL getProjectByAttribute(?, ?)}");
            stmt.setString(1, attribute);
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
                    responseList = new ArrayList<>();

                    while (resultSet.next())
                    {
                        String[] data = new String[]
                        {
                                resultSet.getString(1), // project guid
                                resultSet.getString(2), // project code
                                resultSet.getString(3), // project status
                                resultSet.getString(4), // primary owner
                                resultSet.getString(5), // secondary owner
                                resultSet.getString(6), // dev email
                                resultSet.getString(7), // prod email
                                resultSet.getString(8), // incident q
                                resultSet.getString(9) // chg q
                        };

                        if (DEBUG)
                        {
                            for (String str : data)
                            {
                                DEBUGGER.debug("data: {}", str);
                            }
                        }

                        responseList.add(data);
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("responseList: {}", responseList);
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

        return responseList;
    }
}
