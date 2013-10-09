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
package com.cws.esolutions.security.dao.audit.impl;

import java.util.List;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.CallableStatement;

import com.cws.esolutions.security.dao.audit.interfaces.IAuditDAO;
/*
 * IAgentAuditDAO
 * Data access class interface API for agent audit events
 *
 * $Id: AuditDAOImpl.java 2276 2013-01-03 16:32:52Z kmhuntly@gmail.com $
 * $Author: $
 * $Date: $
 *
 * History
 *
 * Author                       Date                            Comments
 * ----------------------------------------------------------------------------
 * Kevin Huntly                 11/23/2008 22:39:20             Created.
 */
public class AuditDAOImpl implements IAuditDAO
{
    @Override
    public synchronized void auditRequestedOperation(final List<String> auditRequest) throws SQLException
    {
        final String methodName = IAuditDAO.CNAME + "#auditRequestedOperation(final List<String> auditRequest) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AuditRequest: {}", auditRequest);
        }

        Connection sqlConn = null;
        CallableStatement stmt = null;

        try
        {
            sqlConn = dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain audit datasource connection");
            }
            else
            {
                sqlConn.setAutoCommit(true);
                stmt = sqlConn.prepareCall("{CALL insertAuditEntry(?, ?, ?, ?, ?, ?, ?)}");
                stmt.setString(1, auditRequest.get(0)); // usersessid
                stmt.setString(2, auditRequest.get(1)); // username
                stmt.setString(3, auditRequest.get(2)); // userrole
                stmt.setLong(4, System.currentTimeMillis()); // reqtime
                stmt.setString(5, auditRequest.get(3)); // useraction
                stmt.setString(6, auditRequest.get(4)); // srcaddr
                stmt.setString(7, auditRequest.get(5)); // srchost

                if (DEBUG)
                {
                    DEBUGGER.debug(stmt.toString());
                }

                stmt.execute();
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
    }

    @Override
    public synchronized List<String[]> getAuditInterval(final String username) throws SQLException
    {
        final String methodName = IAuditDAO.CNAME + "#getAuditInterval(final String username) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Username: {}", username);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        CallableStatement stmt = null;
        List<String[]> responseList = null;

        try
        {
            sqlConn = dataSource.getConnection();

            sqlConn = dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain audit datasource connection");
            }
            else
            {
                sqlConn.setAutoCommit(true);
                stmt = sqlConn.prepareCall("{CALL retrAuditInterval(?)}");
                stmt.setString(1, username);

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
                        String[] data = new String[] {
                                resultSet.getString(1),
                                resultSet.getString(2),
                                resultSet.getString(3),
                                resultSet.getString(4),
                                String.valueOf(resultSet.getLong(5)),
                                resultSet.getString(6),
                                resultSet.getString(7),
                                resultSet.getString(8)
                        };

                        if (DEBUG)
                        {
                            for (String str : data)
                            {
                                DEBUGGER.debug(str);
                            }
                        }

                        responseList.add(data);
                    }

                    if (DEBUG)
                    {
                        for (String[] str : responseList)
                        {
                            for (String str1 : str)
                            {
                                DEBUGGER.debug(str1);
                            }
                        }
                    }
                }
                else
                {
                    throw new SQLException("No audit entries were located for the provided user.");
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

        return responseList;
    }
}
