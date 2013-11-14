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
package com.cws.esolutions.security.dao.reference.impl;

import java.util.List;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import com.cws.esolutions.security.dao.reference.interfaces.IUserServiceInformationDAO;
/*
 * UserSQLServiceInformationDAOImpl
 * Obtains and returns the user service list for the provided
 * user information. This information can then be shared across
 * to the calling application for consumption.
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * Kevin Huntly         01/18/2010 10:05:24             Created.
 */
public class UserServiceInformationDAOImpl implements IUserServiceInformationDAO
{
    @Override
    public synchronized boolean addProjectIdForUser(final String commonName, final String projectGuid) throws SQLException
    {
        final String methodName = IUserServiceInformationDAO.CNAME + "#addProjectIdForUser(final String commonName, final String projectGuid) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug(commonName);
            DEBUGGER.debug(projectGuid);
        }

        Connection sqlConn = null;
        boolean isComplete = false;
        PreparedStatement stmt = null;

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
            else
            {
                sqlConn.setAutoCommit(true);
                stmt = sqlConn.prepareCall("{CALL addProjectToUser(?, ?)}");
                stmt.setString(1, commonName);
                stmt.setString(2, projectGuid);

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

            throw new SQLException(sqx.getMessage());
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

    @Override
    public synchronized boolean removeProjectIdForUser(final String commonName, final String projectGuid) throws SQLException
    {
        final String methodName = IUserServiceInformationDAO.CNAME + "#removeProjectIdForUser(final String commonName, final String projectGuid) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug(commonName);
            DEBUGGER.debug(projectGuid);
        }

        Connection sqlConn = null;
        PreparedStatement stmt = null;
        boolean isComplete = false;

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
            else
            {
                sqlConn.setAutoCommit(true);
                stmt = sqlConn.prepareCall("{CALL removeProjectFromUser(?, ?)}");
                stmt.setString(1, commonName);
                stmt.setString(2, projectGuid);

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

            throw new SQLException(sqx.getMessage());
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

    @Override
    public synchronized boolean verifyProjectForUser(final String commonName, final String projectGuid) throws SQLException
    {
        final String methodName = IUserServiceInformationDAO.CNAME + "#verifyProjectForUser(final String commonName, final String projectGuid) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug(commonName);
            DEBUGGER.debug(projectGuid);
        }

        Connection sqlConn = null;
        boolean isComplete = false;
        PreparedStatement stmt = null;

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
            else
            {
                sqlConn.setAutoCommit(true);
                stmt = sqlConn.prepareCall("{CALL isUserAuthorizedForProject(?, ?)}");
                stmt.setString(1, commonName);
                stmt.setString(2, projectGuid);

                if (DEBUG)
                {
                    DEBUGGER.debug(stmt.toString());
                }

                isComplete = stmt.execute();

                if (DEBUG)
                {
                    DEBUGGER.debug("isComplete: {}", isComplete);
                }
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new SQLException(sqx.getMessage());
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

    @Override
    public synchronized List<String> returnUserAuthorizedProjects(final String commonName) throws SQLException
    {
        final String methodName = IUserServiceInformationDAO.CNAME + "#returnUserAuthorizedProjects(final String commonName) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug(commonName);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        PreparedStatement stmt = null;
        List<String> projectList = null;

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
            else
            {
                sqlConn.setAutoCommit(true);
                stmt = sqlConn.prepareCall("{CALL retrieveAuthorizedProjectsForUser(?)}");
                stmt.setString(1, commonName);

                if (DEBUG)
                {
                    DEBUGGER.debug(stmt.toString());
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
                        projectList = new ArrayList<String>();

                        while (resultSet.next())
                        {
                            projectList.add(resultSet.getString(1));
                        }

                        if (DEBUG)
                        {
                            DEBUGGER.debug("List<String>: {}", projectList);
                        }
                    }
                    else
                    {
                        throw new SQLException("The provided user does not currently have any services assigned.");
                    }
                }
                else
                {
                    throw new SQLException("The provided user does not currently have any services assigned.");
                }
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new SQLException(sqx.getMessage());
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

        return projectList;
    }

    @Override
    public synchronized boolean addServiceToUser(final String commonName, final String serviceGuid) throws SQLException
    {
        final String methodName = IUserServiceInformationDAO.CNAME + "#addServiceToUser(final String commonName, final String serviceGuid) throws SQLException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("commonName: {}", commonName);
            DEBUGGER.debug("serviceGuid: {}", serviceGuid);
        }

        Connection sqlConn = null;
        boolean isComplete = false;
        PreparedStatement stmt = null;

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
            else
            {
                sqlConn.setAutoCommit(true);
                stmt = sqlConn.prepareCall("{CALL addServiceToUser(?, ?)}");
                stmt.setString(1, commonName);
                stmt.setString(2, serviceGuid);

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

            throw new SQLException(sqx.getMessage());
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

    @Override
    public synchronized boolean removeServiceFromUser(final String commonName, final String serviceGuid) throws SQLException
    {
        final String methodName = IUserServiceInformationDAO.CNAME + "#removeServiceFromUser(final String commonName, final String serviceGuid) throws SQLException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("commonName: {}", commonName);
            DEBUGGER.debug("serviceGuid: {}", serviceGuid);
        }

        Connection sqlConn = null;
        boolean isComplete = false;
        PreparedStatement stmt = null;

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
            else
            {
                sqlConn.setAutoCommit(true);
                stmt = sqlConn.prepareCall("{CALL removeServiceFromUser(?, ?)}");
                stmt.setString(1, commonName);
                stmt.setString(2, serviceGuid);

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

            throw new SQLException(sqx.getMessage());
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

    @Override
    public synchronized boolean verifyServiceForUser(final String commonName, final String serviceGuid) throws SQLException
    {
        final String methodName = IUserServiceInformationDAO.CNAME + "#verifyServiceForUser(final String commonName, final String serviceGuid) throws SQLException";
        
        if (DEBUG)
        {
        	DEBUGGER.debug(methodName);
            DEBUGGER.debug("commonName: {}", commonName);
            DEBUGGER.debug("serviceGuid: {}", serviceGuid);
        }
        
        Connection sqlConn = null;
        boolean isComplete = false;
        PreparedStatement stmt = null;

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
            else
            {
                sqlConn.setAutoCommit(true);
                stmt = sqlConn.prepareCall("{CALL verifySvcForUser(?, ?)}");
                stmt.setString(1, commonName);
                stmt.setString(2, serviceGuid);

                if (DEBUG)
                {
                    DEBUGGER.debug(stmt.toString());
                }

                ResultSet resultSet = stmt.executeQuery();

                if (DEBUG)
                {
                    DEBUGGER.debug("ResultSet: {}", resultSet);
                }

                if (resultSet.next())
                {
                    resultSet.first();

                    isComplete = resultSet.getBoolean(1);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("isComplete: {}", isComplete);
                    }
                }
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new SQLException(sqx.getMessage());
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

    @Override
    public synchronized List<String> listServicesForUser(final String commonName) throws SQLException
    {
        final String methodName = IUserServiceInformationDAO.CNAME + "#listServicesForUser(final String commonName) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug(commonName);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        PreparedStatement stmt = null;
        List<String> serviceList = null;

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
            else
            {
                sqlConn.setAutoCommit(true);
                stmt = sqlConn.prepareCall("{CALL listServicesForUser(?)}");
                stmt.setString(1, commonName);

                if (DEBUG)
                {
                    DEBUGGER.debug(stmt.toString());
                }

                resultSet = stmt.getResultSet();

                if (DEBUG)
                {
                    DEBUGGER.debug("ResultSet: {}", resultSet);
                }

                if (resultSet.next())
                {
                    resultSet.first();
                    serviceList = new ArrayList<String>();

                    while (resultSet.next())
                    {
                        serviceList.add(resultSet.getString(1));
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("List<String>: {}", serviceList);
                    }
                }
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new SQLException(sqx.getMessage());
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
