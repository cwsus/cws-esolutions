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
import java.sql.Connection;
import java.util.ArrayList;
import java.sql.SQLException;
import java.sql.CallableStatement;
import java.sql.ResultSetMetaData;

import com.cws.esolutions.security.dao.reference.interfaces.ISecurityReferenceDAO;
/*
 * IUserSQLServiceInformationDAO
 * API to retrieve and return user service information
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * Kevin Huntly         11/23/2008 22:39:20             Created.
 */
public class SecurityReferenceDAOImpl implements ISecurityReferenceDAO
{
    @Override
    public synchronized List<String> obtainApprovedServers() throws SQLException
    {
        final String methodName = ISecurityReferenceDAO.CNAME + "#obtainApprovedServers() throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        CallableStatement stmt = null;
        List<String> securityList = null;

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
                stmt = sqlConn.prepareCall("{CALL retrApprovedServers()}");

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
                    else
                    {
                        throw new SQLException("No approved servers were found.");
                    }
                }
                else
                {
                    throw new SQLException("Unable to obtain security role information.");
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

            if ((sqlConn != null) && (!(sqlConn.isClosed())))
            {
                sqlConn.close();
            }
        }

        return securityList;
    }

    @Override
    public synchronized List<String> obtainSecurityQuestionList() throws SQLException
    {
        final String methodName = ISecurityReferenceDAO.CNAME + "#obtainSecurityQuestionList() throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        CallableStatement stmt = null;
        List<String> questionList = null;

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
                stmt = sqlConn.prepareCall("{CALL retrieve_user_questions()}");

                if (DEBUG)
                {
                    DEBUGGER.debug(stmt.toString());
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
                    else
                    {
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

            if ((sqlConn != null) && (!(sqlConn.isClosed())))
            {
                sqlConn.close();
            }
        }

        return questionList;
    }
}
