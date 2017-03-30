/*
 * Copyright (c) 2009 - 2017 CaspersBox Web Services
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
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.io.File;
import java.util.Map;
import java.util.List;
import org.slf4j.Logger;
import java.sql.ResultSet;
import java.io.IOException;
import java.sql.Connection;
import javax.sql.DataSource;
import java.sql.SQLException;
import org.slf4j.LoggerFactory;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.cws.esolutions.core.CoreServiceBean;
import com.cws.esolutions.core.CoreServiceConstants;
import com.cws.esolutions.core.utils.enums.LoadType;
import com.cws.esolutions.core.utils.exception.UtilityException;
/**
 * @author khuntly
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
                throw new UtilityException(sqx.getMessage(), sqx);
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

            if (!(stmt.execute()))
            {
                throw new SQLException("An error occurred while performing the requested operation.");
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

            if (!(stmt.execute()))
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
     * A clone of the Oracle "sqlloader" command. At its core, sqlloader performs a line by line
     * insert statement of the data provided into a given database. This method performs the
     * same, given the input file, table name, and column names, with the associated delimiter
     * for the data.
     *
     * @param inFile - The full path to the file that contains the data to insert
     * @param tableName - The table to insert the data into
     * @param columnNames - The column names that the data should be imported into
     * @param delimiter - The file delimiter
     * @param loadType - The load type to perform, as available from Oracle
     * @throws UtilityException {@link com.cws.esolutions.core.utils.exception.UtilityException} if an exception occurs
     * during processing
     */
    public static final void sqlldr(final String inFile, final String tableName, final List<String> columnNames, final String delimiter, final LoadType loadType) throws UtilityException
    {
        final String methodName = SQLUtils.CNAME + "#sqlldr(final String inFile, final String tableName, final List<String> columnNames, final String delimiter, final LoadType loadType) throws UtilityException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", inFile);
            DEBUGGER.debug("Value: {}", tableName);
            DEBUGGER.debug("Value: {}", columnNames);
            DEBUGGER.debug("Value: {}", delimiter);
            DEBUGGER.debug("Value: {}", loadType);
        }

        Connection sqlConn = null;
        CallableStatement stmt = null;

        try
        {
            File inputFile = FileUtils.getFile(inFile);

            if (DEBUG)
            {
                DEBUGGER.debug("File: {}", inputFile);
            }

            if (!(inputFile.canRead()))
            {
                throw new IOException("Unable to load provided file. Cannot continue.");
            }

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

            switch (loadType)
            {
                case TRUNCATE:
                    sqlConn.prepareStatement("TRUNCATE TABLE " + tableName + ";").execute();

                    break;
                case REPLACE:
                    ResultSet rs = sqlConn.prepareStatement("SELECT * FROM " + tableName + ";").executeQuery();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("ResultSet: {}", rs);
                    }

                    if (rs.next())
                    {
                        rs.beforeFirst();

                        while (rs.next())
                        {
                            rs.deleteRow();
                        }
                    }

                    break;
                default:
                    break;
            }

            for (String line : FileUtils.readLines(inputFile, "UTF-8"))
            {
                if (DEBUG)
                {
                    DEBUGGER.debug("Value: {}", line);
                }

                // if the line has ' in it, escape it please
                line = StringUtils.replace(line, "'", "\\'");

                if (DEBUG)
                {
                    DEBUGGER.debug("Value: {}", line);
                }

                StringBuilder sBuilder = new StringBuilder()
                    .append("INSERT INTO " + tableName + " \n")
                    .append("(");

                for (int x = 0; x < columnNames.size(); x++)
                {
                    if (DEBUG)
                    {
                        DEBUGGER.debug("Value: {}", columnNames.get(x));
                    }

                    if (x == (columnNames.size() - 1))
                    {
                        sBuilder.append(columnNames.get(x) + ")\n");
                        sBuilder.append("VALUES\n");

                        break;
                    }

                    sBuilder.append(columnNames.get(x) + delimiter);
                }

                sBuilder.append("(");
                String[] entryData = StringUtils.split(line, delimiter);

                for (int y = 0; y < entryData.length; y++)
                {
                    if (DEBUG)
                    {
                        DEBUGGER.debug("Value: {}", entryData[y]);
                    }

                    if (y == (entryData.length - 1))
                    {
                        sBuilder.append(entryData[y] + ");\n");

                        break;
                    }

                    sBuilder.append("'" + entryData[y] + "'" + delimiter + " ");
                }

                if (DEBUG)
                {
                    DEBUGGER.debug("Value: {}, String: {}", sBuilder, sBuilder.toString());
                }

                if (!(sqlConn.prepareStatement(sBuilder.toString()).execute()))
                {
                    ERROR_RECORDER.error("Unable to insert entry {}: ", line);
                }
            }
        }
        catch (IOException iox)
        {
            throw new UtilityException(iox.getMessage(), iox);
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
