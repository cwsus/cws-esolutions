/*
 * Copyright (c) 2009 - 2020 CaspersBox Web Services
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
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.dao.audit.impl
 * File: AuditDAOImpl.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.util.List;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.CallableStatement;

import com.cws.esolutions.security.dao.audit.interfaces.IAuditDAO;
/**
 * @see com.cws.esolutions.security.dao.audit.interfaces.IAuditDAO
 */
public class AuditDAOImpl implements IAuditDAO
{
    private static final String CNAME = AuditDAOImpl.class.getName();

    /**
     * @see com.cws.esolutions.security.dao.audit.interfaces.IAuditDAO#auditRequestedOperation(java.util.List)
     */
    public synchronized void auditRequestedOperation(final List<String> auditRequest) throws SQLException
    {
        final String methodName = AuditDAOImpl.CNAME + "#auditRequestedOperation(final List<String> auditRequest) throws SQLException";

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

            if ((sqlConn == null) || (sqlConn.isClosed()))
            {
                throw new SQLException("Unable to obtain audit datasource connection");
            }

            sqlConn.setAutoCommit(true);

            stmt = sqlConn.prepareCall("{CALL insertAuditEntry(?, ?, ?, ?, ?, ?, ?, ?)}");
            stmt.setString(1, auditRequest.get(0)); // username
            stmt.setString(2, auditRequest.get(0)); // userguid
            stmt.setString(3, auditRequest.get(0)); // userrole
            stmt.setString(4, auditRequest.get(0)); // applid
            stmt.setString(5, auditRequest.get(0)); // applname
            stmt.setString(6, auditRequest.get(0)); // useraction
            stmt.setString(7, auditRequest.get(0)); // srcaddr
            stmt.setString(8, auditRequest.get(0)); // srchost

            if (DEBUG)
            {
                DEBUGGER.debug("CallableStatement: {}", stmt);
            }

            stmt.execute();
        }
        catch (final SQLException sqx)
        {
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
            catch (final SQLException sqx)
            {
                throw new SQLException(sqx.getMessage(), sqx);
            }
        }
    }

    /**
     * @see com.cws.esolutions.security.dao.audit.interfaces.IAuditDAO#getAuditInterval(String, int)
     */
    public synchronized List<Object> getAuditInterval(final String guid, final int startRow) throws SQLException
    {
        final String methodName = AuditDAOImpl.CNAME + "#getAuditInterval(final String guid, final int startRow) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", guid);
            DEBUGGER.debug("Value: {}", startRow);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        CallableStatement stmt = null;
        List<Object> responseList = null;

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
                DEBUGGER.debug("CallableStatement: {}", stmt);
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
                    int count = resultSet.getInt(1);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("count: {}", count);
                    }

                    resultSet.beforeFirst();
                    responseList = new ArrayList<Object>();
                    responseList.add(count);

                    while (resultSet.next())
                    {
                        Object[] data = new Object[]
                        {
                            resultSet.getString(3), // USERNAME
                            resultSet.getString(4), // CN
                            resultSet.getString(5), // APPLICATION_ID
                            resultSet.getString(6), // APPLICATION_NAME
                            resultSet.getTimestamp(7), // REQUEST_TIMESTAMP
                            resultSet.getString(8), // ACTION
                            resultSet.getString(9), // SOURCE_ADDRESS
                            resultSet.getString(10) // SOURCE_HOSTNAME
                        };

                        if (DEBUG)
                        {
                            for (Object obj : data)
                            {
                                DEBUGGER.debug("Value: {}", obj);
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
        catch (final SQLException sqx)
        {
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
            catch (final SQLException sqx)
            {
                throw new SQLException(sqx.getMessage(), sqx);
            }
        }

        return responseList;
    }
}
