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
package com.cws.esolutions.core.utils;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.utils
 * File: SQLUtils.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly           11/23/2008 22:39:20             Created.
 */
import java.util.Map;
import org.slf4j.Logger;
import java.sql.ResultSet;
import java.sql.Connection;
import javax.sql.DataSource;
import java.sql.SQLException;
import org.slf4j.LoggerFactory;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;

import com.cws.esolutions.core.CoreServiceBean;
import com.cws.esolutions.core.CoreServiceConstants;
import com.cws.esolutions.core.utils.exception.UtilityException;
/**
 * @author cws-khuntly
 * @version 1.0
 */
public class SQLUtils
{
    private static final String CNAME = SQLUtils.class.getName();
    private static final CoreServiceBean appBean = CoreServiceBean.getInstance();
    private static final DataSource dataSource = appBean.getDataSources().get("ApplicationDataSource");

    private static final Logger DEBUGGER = LoggerFactory.getLogger(CoreServiceConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(CoreServiceConstants.ERROR_LOGGER);

    /**
     * Run a provided query against the configured datasource and return the resultset for
     * processing by the requestor.
     *
     * @param query - The query to execute against the database.
     * @return A {@link java.sql.ResultSet} containing the returned data
     * @throws UtilityException {@link com.cws.esolutions.core.utils.exception.UtilityException} if an exception occurs
     * during processing
     */
    public static final ResultSet runQuery(final String query) throws UtilityException
    {
        final String methodName = SQLUtils.CNAME + "#runQuery(final String query) throws UtilityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", query);
        }

        ResultSet rs = null;
        Connection sqlConn = null;
        ResultSet resultSet = null;
        PreparedStatement stmt = null;

        try
        {
            sqlConn = SQLUtils.dataSource.getConnection();

            if (DEBUG)
            {
                DEBUGGER.debug("Connection: {}", sqlConn);
            }

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain connection to datasource. Cannot continue.");
            }

            sqlConn.setAutoCommit(true);
            stmt = sqlConn.prepareStatement(query);

            if (DEBUG)
            {
                DEBUGGER.debug("stmt: {}", stmt);
            }

            if (stmt.execute())
            {
                rs = stmt.getResultSet();

                if (DEBUG)
                {
                    DEBUGGER.debug("ResultSet: {}", rs);
                }

                if (!(rs.next()))
                {
                    throw new SQLException("No data was obtained for the provided query.");
                }

                resultSet = rs;
            }
        }
        catch (SQLException sqx)
        {
            throw new UtilityException(sqx.getMessage(), sqx);
        }
        finally
        {
            try
            {
                if (rs != null)
                {
                    rs.close();
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
            catch (SQLException sqx)
            {
                throw new UtilityException(sqx.getMessage(), sqx);
            }
        }
        
        return resultSet;
    }

    /**
     * Run a provided query against the configured datasource and return the resultset for
     * processing by the requestor.
     *
     * @param query - The query to execute against the database.
     * @param params - The query parameters for the statement call
     * @return A {@link java.sql.ResultSet} containing the returned data
     * @throws UtilityException {@link com.cws.esolutions.core.utils.exception.UtilityException} if an exception occurs
     * during processing
     */
    public static final ResultSet runQuery(final String query, final Map<Integer, Object> params) throws UtilityException
    {
        final String methodName = SQLUtils.CNAME + "#runQuery(final String query, final Map<Integer, Object> params) throws UtilityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", query);
            DEBUGGER.debug("Value: {}", params);
        }

        ResultSet rs = null;
        Connection sqlConn = null;
        ResultSet resultSet = null;
        CallableStatement stmt = null;

        try
        {
            sqlConn = SQLUtils.dataSource.getConnection();

            if (DEBUG)
            {
                DEBUGGER.debug("Connection: {}", sqlConn);
            }

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain connection to datasource. Cannot continue.");
            }

            sqlConn.setAutoCommit(true);
            stmt = sqlConn.prepareCall(query);

            if (!(params.isEmpty()))
            {
                for (Integer key : params.keySet())
                {
                    if (DEBUG)
                    {
                        DEBUGGER.debug("Key: {}, Value: {}", key, params.get(key));
                    }

                    stmt.setObject(key, params.get(key));
                }
            }

            if (DEBUG)
            {
                DEBUGGER.debug("stmt: {}", stmt);
            }

            if (stmt.execute())
            {
                rs = stmt.getResultSet();

                if (DEBUG)
                {
                    DEBUGGER.debug("ResultSet: {}", rs);
                }

                if (!(rs.next()))
                {
                    throw new SQLException("No data was obtained for the provided query.");
                }

                resultSet = rs;
            }
        }
        catch (SQLException sqx)
        {
            throw new UtilityException(sqx.getMessage(), sqx);
        }
        finally
        {
            try
            {
                if (rs != null)
                {
                    rs.close();
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
            catch (SQLException sqx)
            {
                ERROR_RECORDER.error(sqx.getMessage(), sqx);
            }
        }
        
        return resultSet;
    }

    /**
     * Performs an insert, update or delete against the configured datasource. This method does not
     * return any data as no data is returned for successful inserts/updates/deletes (though deletes
     * will return the row count deleted)
     *
     * @param query - The query to execute against the database.
     * @throws UtilityException {@link com.cws.esolutions.core.utils.exception.UtilityException} if an exception occurs
     * during processing
     */
    public static final void addOrDeleteData(final String query) throws UtilityException
    {
        final String methodName = SQLUtils.CNAME + "#addOrDeleteData(final String query) throws UtilityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", query);
        }

        Connection sqlConn = null;
        PreparedStatement stmt = null;

        try
        {
            sqlConn = SQLUtils.dataSource.getConnection();

            if (DEBUG)
            {
                DEBUGGER.debug("Connection: {}", sqlConn);
            }

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain connection to datasource. Cannot continue.");
            }

            sqlConn.setAutoCommit(true);
            stmt = sqlConn.prepareStatement(query);

            if (DEBUG)
            {
                DEBUGGER.debug("stmt: {}", stmt);
            }

            if (stmt.executeUpdate() != 0)
            {
                throw new SQLException("An error occured while performing the requested operation.");
            }
        }
        catch (SQLException sqx)
        {
            throw new UtilityException(sqx.getMessage(), sqx);
        }
        finally
        {
            try
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
            catch (SQLException sqx)
            {
                throw new UtilityException(sqx.getMessage(), sqx);
            }
        }
    }

    /**
     * Performs an insert, update or delete against the configured datasource. This method does not
     * return any data as no data is returned for successful inserts/updates/deletes (though deletes
     * will return the row count deleted)
     *
     * @param query - The query to execute against the database.
     * @param params - A list of parameters to be applied to the statement call
     * @throws UtilityException {@link com.cws.esolutions.core.utils.exception.UtilityException} if an exception occurs
     * during processing
     */
    public static final void addOrDeleteData(final String query, final Map<Integer, Object> params) throws UtilityException
    {
        final String methodName = SQLUtils.CNAME + "#addOrDeleteData(final String query, final Map<Integer, Object> params) throws UtilityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", query);
            DEBUGGER.debug("Value: {}", params);
        }

        Connection sqlConn = null;
        CallableStatement stmt = null;

        try
        {
            sqlConn = SQLUtils.dataSource.getConnection();

            if (DEBUG)
            {
                DEBUGGER.debug("Connection: {}", sqlConn);
            }

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain connection to datasource. Cannot continue.");
            }

            sqlConn.setAutoCommit(true);
            stmt = sqlConn.prepareCall(query);

            if (!(params.isEmpty()))
            {
                for (Integer key : params.keySet())
                {
                    if (DEBUG)
                    {
                        DEBUGGER.debug("Key: {}, Value: {}", key, params.get(key));
                    }

                    stmt.setObject(key, params.get(key));
                }
            }

            if (DEBUG)
            {
                DEBUGGER.debug("stmt: {}", stmt);
            }

            if (stmt.executeUpdate() != 0)
            {
                throw new SQLException("An error occured while performing the requested operation.");
            }
        }
        catch (SQLException sqx)
        {
            throw new UtilityException(sqx.getMessage(), sqx);
        }
        finally
        {
            try
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
            catch (SQLException sqx)
            {
                throw new UtilityException(sqx.getMessage(), sqx);
            }
        }
    }
}
