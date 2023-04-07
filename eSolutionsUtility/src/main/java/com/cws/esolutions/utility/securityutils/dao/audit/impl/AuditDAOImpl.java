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
package com.cws.esolutions.utility.securityutils.dao.audit.impl;
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
import java.util.Objects;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.Connection;
import javax.naming.Context;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.cws.esolutions.utility.UtilityConstants;
import com.cws.esolutions.utility.securityutils.dao.audit.interfaces.IAuditDAO;
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
        PreparedStatement stmt = null;

        try
        {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup(UtilityConstants.DS_CONTEXT);
            DataSource dataSource = (DataSource) envContext.lookup(IAuditDAO.INIT_AUDITDS_MANAGER);

            if (DEBUG)
            {
            	DEBUGGER.debug("Context: {}", initContext);
            	DEBUGGER.debug("Context: {}", envContext);
            	DEBUGGER.debug("DataSource: {}", dataSource);
            }

            if (Objects.isNull(dataSource))
            {
            	throw new SQLException("A datasource connection could not be obtained.");
            }

            sqlConn = dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain audit datasource connection");
            }

            sqlConn.setAutoCommit(true);

            stmt = sqlConn.prepareStatement("{ CALL insertAuditEntry(?, ?, ?, ?, ?, ?, ?, ?, ?) }", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stmt.setString(1, auditRequest.get(0)); // session id
            stmt.setString(2, auditRequest.get(1)); // username
            stmt.setString(3, auditRequest.get(2)); // guid
            stmt.setString(4, auditRequest.get(3)); // user role
            stmt.setString(5, auditRequest.get(4)); // applid
            stmt.setString(6, auditRequest.get(5)); // applname
            stmt.setString(7, auditRequest.get(6)); // user action
            stmt.setString(8, auditRequest.get(7)); // srcaddr
            stmt.setString(9, auditRequest.get(8)); // srcaddr
            
            if (DEBUG)
            {
                DEBUGGER.debug("PreparedStatement: {}", stmt);
            }

            stmt.execute();
        }
        catch (final SQLException sqx)
        {
            throw new SQLException(sqx.getMessage(), sqx);
        }
        catch (NamingException nx)
        {
        	throw new SQLException(nx.getMessage(), nx);
		}
        finally
        {
            try
            {
                if (!(Objects.isNull(stmt)))
                {
                    stmt.close();
                }

                if (!(Objects.isNull(sqlConn)) && (!(sqlConn.isClosed())))
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
    public synchronized List<Object> getAuditInterval(final String guid, final int startRow, final DataSource dataSource) throws SQLException
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
        PreparedStatement stmt = null;
        List<Object> responseList = null;

        if (Objects.isNull(dataSource))
        {
        	throw new SQLException("A datasource connection could not be obtained.");
        }

        try
        {
            sqlConn = dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain audit datasource connection");
            }

            sqlConn.setAutoCommit(true);
            stmt = sqlConn.prepareStatement("{ CALL getAuditInterval(?, ?) }", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stmt.setString(1, guid);
            stmt.setInt(2, startRow);

            if (DEBUG)
            {
                DEBUGGER.debug("PreparedStatement: {}", stmt);
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
                    responseList = new ArrayList<Object>();

                    while (resultSet.next())
                    {
                        Object[] data = new Object[]
                        {
                            resultSet.getString(1), // sessid
                            resultSet.getString(2), // username
                            resultSet.getString(3), // cn
                            resultSet.getString(4), // role
                            resultSet.getString(5), // applid
                            resultSet.getString(6), // applname
                            resultSet.getTimestamp(7), // req timestamp
                            resultSet.getString(8), // req action
                            resultSet.getString(9), // src addr
                            resultSet.getString(10) // src host
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
                if (!(Objects.isNull(resultSet)))
                {
                    resultSet.close();
                }

                if (!(Objects.isNull(stmt)))
                {
                    stmt.close();
                }

                if (!(Objects.isNull(sqlConn)) && (!(sqlConn.isClosed())))
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
