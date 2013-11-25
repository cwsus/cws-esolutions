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
import java.util.Vector;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import com.cws.esolutions.core.dao.processors.interfaces.IDNSServiceDAO;
/**
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.dao.processors.impl
 * File: DNSServiceDAOImpl.java
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
 * 35033355 @ Jul 19, 2013 10:40:24 AM
 *     Created.
 */
public class DNSServiceDAOImpl implements IDNSServiceDAO
{
    @Override
    public synchronized List<Vector<String>> getServiceData(final String serviceName) throws SQLException
    {
        final String methodName = IDNSServiceDAO.CNAME + "#getServiceData(final String serviceName) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", serviceName);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        PreparedStatement stmt = null;
        List<Vector<String>> response = null;

        try
        {
            sqlConn = dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);

            stmt = sqlConn.prepareCall("{CALL getRecordByAttribute(?)}");
            stmt.setString(1, serviceName);

            if (DEBUG)
            {
                DEBUGGER.debug(stmt.toString());
            }

            if (stmt.execute())
            {
                resultSet = stmt.getResultSet();

                if (resultSet.next())
                {
                    resultSet.beforeFirst();
                    response = new ArrayList<>();

                    while (resultSet.next())
                    {
                        Vector<String> vector = new Vector<>(
                                Arrays.asList(
                                        resultSet.getString(1), // PROJECT_CODE
                                        resultSet.getString(2), // ZONE_FILE
                                        String.valueOf(resultSet.getBoolean(3)), // APEX_RECORD
                                        resultSet.getString(4), // RR_ORIGIN
                                        String.valueOf(resultSet.getInt(5)), // RR_TIMETOLIVE
                                        resultSet.getString(6), // RR_HOSTNAME
                                        resultSet.getString(7), // RR_OWNER
                                        resultSet.getString(8), // RR_HOSTMASTER
                                        String.valueOf(resultSet.getString(9)), // RR_SERIAL
                                        String.valueOf(resultSet.getString(10)), // RR_REFRESH
                                        String.valueOf(resultSet.getString(11)), // RR_RETRY
                                        String.valueOf(resultSet.getString(12)), // RR_EXPIRY
                                        String.valueOf(resultSet.getString(13)), // RR_CACHETIME
                                        resultSet.getString(14), // RR_CLASS
                                        resultSet.getString(15), // RR_TYPE
                                        String.valueOf(resultSet.getString(16)), // RR_PORT
                                        String.valueOf(resultSet.getString(17)), // RR_WEIGHT
                                        resultSet.getString(18), // RR_SERVICE
                                        resultSet.getString(19), // RR_PROTOCOL
                                        String.valueOf(resultSet.getString(20)), // RR_PRIORITY
                                        resultSet.getString(21), // RR_TARGET
                                        resultSet.getString(22), // SECONDARY_TARGET
                                        resultSet.getString(23))); // TERTIARY_TARGET

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Vector: {}", vector);
                        }

                        response.add(vector);
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("response: {}", response);
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

        return response;
    }

    @Override
    public synchronized boolean addNewService(final List<String> service, final boolean isApex) throws SQLException
    {
        final String methodName = IDNSServiceDAO.CNAME + "#addNewService(final List<String> service, final boolean isApex) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", service);
            DEBUGGER.debug("Value: {}", isApex);
        }

        Connection sqlConn = null;
        boolean isComplete = false;
        PreparedStatement stmt = null;

        try
        {
            sqlConn = dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);

            if (isApex)
            {
                stmt = sqlConn.prepareCall("{ CALL insertApex(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }");
                stmt.setString(1, service.get(0)); // PROJECT_CODE
                stmt.setString(2, service.get(1)); // ZONE_FILE
                stmt.setString(3, service.get(2)); // RR_ORIGIN
                stmt.setInt(4, Integer.parseInt(service.get(3))); // RR_TIMETOLIVE
                stmt.setString(5, service.get(4)); // RR_HOSTNAME
                stmt.setString(6, service.get(5)); // RR_OWNER
                stmt.setString(7, service.get(6)); // RR_HOSTMASTER
                stmt.setInt(8, Integer.parseInt(service.get(7))); // RR_SERIAL
                stmt.setInt(9, Integer.parseInt(service.get(8))); // RR_REFRESH
                stmt.setInt(10, Integer.parseInt(service.get(9))); // RR_RETRY
                stmt.setInt(11, Integer.parseInt(service.get(10))); // RR_EXPIRY
                stmt.setInt(12, Integer.parseInt(service.get(11))); // RR_CACHETIME

                if (DEBUG)
                {
                    DEBUGGER.debug("stmt: {}", stmt);
                }
            }
            else
            {
                stmt = sqlConn.prepareCall("{ CALL insertRecord(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }");
                stmt.setString(1, service.get(0)); // PROJECT_CODE
                stmt.setString(2, service.get(1)); // ZONE_FILE
                stmt.setString(3, service.get(2)); // RR_ORIGIN
                stmt.setString(4, service.get(3)); // RR_HOSTNAME
                stmt.setString(5, service.get(4)); // RR_CLASS
                stmt.setString(6, service.get(5)); // RR_TYPE
                stmt.setInt(7, Integer.parseInt(service.get(6))); // RR_PORT
                stmt.setInt(8, Integer.parseInt(service.get(7))); // RR_WEIGHT
                stmt.setString(9, service.get(8)); // RR_SERVICE
                stmt.setString(10, service.get(9)); // RR_PROTOCOL
                stmt.setInt(11, Integer.parseInt(service.get(10))); // RR_PRIORITY
                stmt.setString(12, service.get(11)); // RR_TARGET
                stmt.setString(13, service.get(12));  // SECONDARY_TARGET
                stmt.setString(14, service.get(13)); // TERTIARY_TARGET
            }

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

    @Override
    public synchronized boolean removeService(final String serviceName) throws SQLException
    {
        final String methodName = IDNSServiceDAO.CNAME + "#removeService(final String serviceName) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: ", serviceName);
        }

        return false;
    }
}
