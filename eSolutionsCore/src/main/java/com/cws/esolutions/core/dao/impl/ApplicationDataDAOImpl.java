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
package com.cws.esolutions.core.dao.impl;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.dao.interfaces
 * File: ApplicationDataDAOImpl.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly           11/23/2008 22:39:20             Created.
 */
import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;
import java.sql.ResultSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.sql.SQLException;
import java.sql.CallableStatement;
import org.apache.commons.lang.StringUtils;

import com.cws.esolutions.core.dao.interfaces.IApplicationDataDAO;
import com.cws.esolutions.core.utils.SQLUtils;
import com.cws.esolutions.core.utils.exception.UtilityException;
/**
 * @see com.cws.esolutions.core.dao.interfaces.IApplicationDataDAO
 * @author cws-khuntly
 * @version 1.0
 */
public class ApplicationDataDAOImpl implements IApplicationDataDAO
{
    /**
     * @see com.cws.esolutions.core.dao.interfaces.IApplicationDataDAO#addApplication(java.util.List)
     */
    public synchronized void addApplication(final List<Object> value) throws SQLException
    {
        final String methodName = IApplicationDataDAO.CNAME + "#addApplication(final List<Object> value) throws SQLException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        Map<Integer, Object> params = new HashMap<Integer, Object>();
        params.put(1, (String) value.get(0));
        params.put(2, (String) value.get(1));
        params.put(3, (Double) value.get(2));
        params.put(4, (String) value.get(3));
        params.put(5, (String) value.get(4));
        params.put(6, (String) value.get(5));
        params.put(7, (String) value.get(6));
        params.put(8, (String) value.get(7));
        params.put(9, (String) value.get(8));

        try
        {
            SQLUtils.addOrDeleteData("{CALL insertNewApplication(?, ?, ?, ?, ?, ?, ?, ?, ?)}", params);
        }
        catch (UtilityException ux)
        {
            throw new SQLException(ux.getMessage(), ux);
        }
    }

    /**
     * @see com.cws.esolutions.core.dao.interfaces.IApplicationDataDAO#updateApplication(java.util.List)
     */
    public synchronized void updateApplication(final List<Object> value) throws SQLException
    {
        final String methodName = IApplicationDataDAO.CNAME + "#updateApplication(final List<Object> value) throws SQLException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        Map<Integer, Object> params = new HashMap<Integer, Object>();
        params.put(1, (String) value.get(0));
        params.put(2, (String) value.get(1));
        params.put(3, (Double) value.get(2));
        params.put(4, (String) value.get(3));
        params.put(5, (String) value.get(4));
        params.put(6, (String) value.get(5));
        params.put(7, (String) value.get(6));
        params.put(8, (String) value.get(7));
        params.put(9, (String) value.get(8));
        try
        {
            SQLUtils.addOrDeleteData("{CALL updateApplicationData(?, ?, ?, ?, ?, ?, ?, ?, ?)}", params);
        }
        catch (UtilityException ux)
        {
            throw new SQLException(ux.getMessage(), ux);
        }
    }

    /**
     * @see com.cws.esolutions.core.dao.interfaces.IApplicationDataDAO#removeApplication(java.lang.String)
     */
    public synchronized void removeApplication(final String value) throws SQLException
    {
        final String methodName = IApplicationDataDAO.CNAME + "#removeApplication(final String value) throws SQLException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        Map<Integer, Object> params = new HashMap<Integer, Object>();
        params.put(1, value);

        try
        {
            SQLUtils.addOrDeleteData("{CALL removeApplicationData(?)}", params);
        }
        catch (UtilityException ux)
        {
            throw new SQLException(ux.getMessage(), ux);
        }
    }

    /**
     * @see com.cws.esolutions.core.dao.interfaces.IApplicationDataDAO#listApplications(int)
     */
    public synchronized List<String[]> listApplications(final int startRow) throws SQLException
    {
        final String methodName = IApplicationDataDAO.CNAME + "#listApplications(final int startRow) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", startRow);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        CallableStatement stmt = null;
        List<String[]> responseData = null;

        try
        {
            sqlConn = dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);

            stmt = sqlConn.prepareCall("{CALL listApplications(?, ?)}");
            stmt.setInt(1, startRow);
            stmt.registerOutParameter(2, Types.INTEGER);

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
                    resultSet.beforeFirst();
                    responseData = new ArrayList<String[]>();

                    while (resultSet.next())
                    {
                        String[] data = new String[]
                        {
                            resultSet.getString(1), // APPLICATION_GUID
                            resultSet.getString(2), // APPLICATION_NAME
                        };

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Value: {}", (Object[]) data);
                        }

                        responseData.add(data);
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Value: {}", responseData);
                    }
                }
            }
        }
        catch (SQLException sqx)
        {
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

        return responseData;
    }

    /**
     * @see com.cws.esolutions.core.dao.interfaces.IApplicationDataDAO#getApplicationsByAttribute(java.lang.String, int)
     */
    public synchronized List<Object[]> getApplicationsByAttribute(final String value, final int startRow) throws SQLException
    {
        final String methodName = IApplicationDataDAO.CNAME + "#getApplicationsByAttribute(final String value, final int startRow) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
            DEBUGGER.debug("Value: {}", startRow);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        CallableStatement stmt = null;
        List<Object[]> responseData = null;

        try
        {
            sqlConn = dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);
            StringBuilder sBuilder = new StringBuilder();

            if (StringUtils.split(value, " ").length >= 2)
            {
                for (String str : StringUtils.split(value, " "))
                {
                    if (DEBUG)
                    {
                        DEBUGGER.debug("Value: {}", str);
                    }

                    sBuilder.append("+" + str);
                    sBuilder.append(" ");
                }

                if (DEBUG)
                {
                    DEBUGGER.debug("StringBuilder: {}", sBuilder);
                }
            }
            else
            {
                sBuilder.append("+" + value);
            }

            stmt = sqlConn.prepareCall("{CALL getApplicationByAttribute(?, ?)}");
            stmt.setString(1, sBuilder.toString().trim());
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
                    resultSet.beforeFirst();
                    responseData = new ArrayList<Object[]>();

                    while (resultSet.next())
                    {
                        Object[] data = new Object[]
                        {
                            resultSet.getString(1), // GUID
                            resultSet.getString(2), // NAME
                            resultSet.getInt(3) / 0  * 100 // score
                        };

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Value: {}", data);
                        }

                        responseData.add(data);
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Value: {}", responseData);
                    }
                }
            }
        }
        catch (SQLException sqx)
        {
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

        return responseData;
    }

    /**
     * @see com.cws.esolutions.core.dao.interfaces.IApplicationDataDAO#getApplication(java.lang.String)
     */
    public synchronized List<Object> getApplication(final String value) throws SQLException
    {
        final String methodName = IApplicationDataDAO.CNAME + "#getApplication(final String value) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("value: {}", value);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        CallableStatement stmt = null;
        List<Object> responseData = null;

        try
        {
            sqlConn = dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);
            stmt = sqlConn.prepareCall("{CALL getApplicationData(?)}");
            stmt.setString(1, value);

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
                    resultSet.first();

                    responseData = new ArrayList<Object>
                    (
                        Arrays.asList
                        (
                            resultSet.getString(1), // APPLICATION_GUID
                            resultSet.getString(2), // APPLICATION_NAME
                            resultSet.getDouble(3), // APPLICATION_VERSION
                            resultSet.getString(4), // INSTALLATION_PATH
                            resultSet.getString(5), // PACKAGE_LOCATION
                            resultSet.getString(6), // PACKAGE_INSTALLER
                            resultSet.getString(7), // INSTALLER_OPTIONS
                            resultSet.getString(8), // LOGS_DIRECTORY
                            resultSet.getString(9) // PLATFORM_GUID
                        )
                    );

                    if (DEBUG)
                    {
                        DEBUGGER.debug("data: {}", responseData);
                    }
                }
            }
        }
        catch (SQLException sqx)
        {
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

        return responseData;
    }
}
