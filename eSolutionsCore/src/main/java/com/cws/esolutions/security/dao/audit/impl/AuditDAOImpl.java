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

            sqlConn.setAutoCommit(true);
            stmt = sqlConn.prepareCall("{CALL insertAuditEntry(?, ?, ?, ?, ?, ?, ?, ?, ?)}");
            stmt.setString(1, auditRequest.get(0)); // usr_audit_sessionid
            stmt.setString(2, auditRequest.get(1)); // usr_audit_userid
            stmt.setString(3, auditRequest.get(2)); // usr_audit_userguid
            stmt.setString(4, auditRequest.get(3)); // usr_audit_role
            stmt.setString(5, auditRequest.get(4)); // usr_audit_applid
            stmt.setString(6, auditRequest.get(5)); // usr_audit_applname
            stmt.setString(7, auditRequest.get(6)); // usr_audit_action
            stmt.setString(8, auditRequest.get(7)); // usr_audit_srcaddr
            stmt.setString(9, auditRequest.get(8)); // usr_audit_srchost

            if (DEBUG)
            {
                DEBUGGER.debug(stmt.toString());
            }

            stmt.execute();
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
    public synchronized int getAuditCount(final String guid) throws SQLException
    {
        final String methodName = IAuditDAO.CNAME + "#getAuditCount(final String guid) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", guid);
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
                throw new SQLException("Unable to obtain audit datasource connection");
            }

            sqlConn.setAutoCommit(true);
            stmt = sqlConn.prepareCall("{ CALL getAuditCount(?) }");
            stmt.setString(1, guid);

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

    @Override
    public synchronized List<String[]> getAuditInterval(final String guid, final int startRow) throws SQLException
    {
        final String methodName = IAuditDAO.CNAME + "#getAuditInterval(final String guid, final int startRow) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", guid);
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
                throw new SQLException("Unable to obtain audit datasource connection");
            }

            sqlConn.setAutoCommit(true);
            stmt = sqlConn.prepareCall("{CALL getAuditInterval(?, ?)}");
            stmt.setString(1, guid);
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
                                resultSet.getString(1), // usr_audit_sessionid
                                resultSet.getString(2), // usr_audit_userid
                                resultSet.getString(3), // usr_audit_userguid
                                resultSet.getString(4), // usr_audit_role
                                resultSet.getString(5), // usr_audit_applid
                                resultSet.getString(6), // usr_audit_applname
                                String.valueOf(resultSet.getLong(7)), // usr_audit_timestamp
                                resultSet.getString(8), // usr_audit_action
                                resultSet.getString(9), // usr_audit_srcaddr
                                resultSet.getString(10) // usr_audit_srchost
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
