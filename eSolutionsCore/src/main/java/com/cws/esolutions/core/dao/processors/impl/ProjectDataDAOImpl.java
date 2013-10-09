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

import com.cws.esolutions.core.dao.processors.interfaces.IProjectDataDAO;
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
public class ProjectDataDAOImpl implements IProjectDataDAO
{
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
            else
            {
                sqlConn.setAutoCommit(true);

                stmt = sqlConn.prepareCall("{CALL insertNewProject(?, ?, ?, ?, ?, ?, ?, ?)}");
                stmt.setString(1, projectDetail.get(0)); // project guid
                stmt.setString(2, projectDetail.get(1)); // project code
                stmt.setString(3, projectDetail.get(2)); // project status
                stmt.setString(4, projectDetail.get(3)); // primary owner
                stmt.setString(5, projectDetail.get(4)); // secondary owner, could be null
                stmt.setString(6, projectDetail.get(5)); // contact email
                stmt.setString(7, projectDetail.get(6)); // incident q
                stmt.setString(8, projectDetail.get(7)); // change q

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
            else
            {
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
            else
            {
                sqlConn.setAutoCommit(true);

                stmt = sqlConn.prepareCall("{CALL updateProjectDetail(?, ?, ?, ?, ?, ?, ?, ?)}");
                stmt.setString(1, projectDetail.get(0)); // project guid
                stmt.setString(2, projectDetail.get(1)); // project code
                stmt.setString(3, projectDetail.get(2)); // project status
                stmt.setString(4, projectDetail.get(3)); // primary owner
                stmt.setString(5, projectDetail.get(4)); // secondary owner, could be null
                stmt.setString(6, projectDetail.get(5)); // contact email
                stmt.setString(7, projectDetail.get(6)); // incident q
                stmt.setString(8, projectDetail.get(7)); // change q

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
            else
            {
                sqlConn.setAutoCommit(true);
                stmt = sqlConn.prepareCall("{CALL getProjectData(?)}");
                stmt.setString(1, projectGuid);

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
                                resultSet.getString(1), // project guid
                                resultSet.getString(2), // project code
                                resultSet.getString(3), // project status
                                resultSet.getString(4), // primary owner
                                resultSet.getString(5), // secondary owner
                                resultSet.getString(6), // contact email
                                resultSet.getString(7), // incident q
                                resultSet.getString(8))); // chg q

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
    public synchronized List<String[]> listAvailableProjects() throws SQLException
    {
        final String methodName = IProjectDataDAO.CNAME + "#listAvailableProjects() throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
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
            else
            {
                sqlConn.setAutoCommit(true);

                stmt = sqlConn.prepareCall("{CALL listProjects()}");

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
                    projectList = new ArrayList<String[]>();

                    while (resultSet.next())
                    {
                        String[] data = new String[] {
                            resultSet.getString(1), // project guid
                            resultSet.getString(2), // project code
                            resultSet.getString(3), // project status
                            resultSet.getString(4), // primary owner
                            resultSet.getString(5), // secondary owner
                            resultSet.getString(6), // contact email
                            resultSet.getString(7), // incident q
                            resultSet.getString(8) // chg q
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

    @Override
    public synchronized List<String[]> getProjectsByAttribute(final String attribute) throws SQLException
    {
        final String methodName = IProjectDataDAO.CNAME + "#getProjectsByAttribute(final String attribute)  throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
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
            else
            {
                sqlConn.setAutoCommit(true);

                stmt = sqlConn.prepareCall("{CALL getProjectByAttribute(?)}");
                stmt.setString(1, attribute);

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
                    responseList = new ArrayList<String[]>();

                    while (resultSet.next())
                    {
                        String[] data = new String[]
                        {
                            resultSet.getString(1), // project guid
                            resultSet.getString(2), // project code
                            resultSet.getString(3), // project region
                            resultSet.getString(4), // primary owner
                            resultSet.getString(5), // secondary owner
                            resultSet.getString(6), // contact email
                            resultSet.getString(7), // incident q
                            resultSet.getString(8) // chg q
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
                else
                {
                    throw new SQLException("No results were found");
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
