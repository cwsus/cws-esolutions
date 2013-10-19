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
import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.CallableStatement;

import com.cws.esolutions.core.dao.processors.interfaces.IServerDataDAO;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.dao.sysmgmt.impl
 * ServerDataDAOImpl.java
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
 * kh05451 @ Jan 8, 2013 11:16:06 AM
 *     Created.
 */
public class ServerDataDAOImpl implements IServerDataDAO
{
    @Override
    public synchronized boolean addNewServer(final List<Object> serverData) throws SQLException
    {
        final String methodName = IServerDataDAO.CNAME + "#addNewServer(final List<Object> serverData) throws SQLException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);

            for (Object str : serverData)
            {
                DEBUGGER.debug("Value: {}", str);
            }
        }

        Connection sqlConn = null;
        boolean isComplete = false;
        CallableStatement stmt = null;

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

                stmt = sqlConn.prepareCall("{CALL insertNewServer(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
                stmt.setString(1, (String) serverData.get(0)); // systemGuid
                stmt.setString(2, (String) serverData.get(1)); // systemOs
                stmt.setString(3, (String) serverData.get(2)); // systemStatus
                stmt.setString(4, (String) serverData.get(3)); // systemRegion
                stmt.setString(5, (String) serverData.get(4)); // systemType
                stmt.setString(6, (String) serverData.get(5)); // domainName
                stmt.setString(7, (String) serverData.get(6)); // cpuType
                stmt.setInt(8, (Integer) serverData.get(7)); // cpuCount
                stmt.setString(9, (String) serverData.get(8)); // serverModel
                stmt.setString(10, (String) serverData.get(9)); // serialNumber
                stmt.setInt(11, (Integer) serverData.get(10)); // installedMemory
                stmt.setString(12, (String) serverData.get(11)); // operIp
                stmt.setString(13, (String) serverData.get(12)); // operHostname
                stmt.setString(14, (String) serverData.get(13)); // mgmtIp
                stmt.setString(15, (String) serverData.get(14)); // mgmtHostname
                stmt.setString(16, (String) serverData.get(15)); // backupIp
                stmt.setString(17, (String) serverData.get(16)); // backupHostname
                stmt.setString(18, (String) serverData.get(17)); // nasIp
                stmt.setString(19, (String) serverData.get(18)); // nasHostname
                stmt.setString(20, (String) serverData.get(19)); // natAddr
                stmt.setString(21, (String) serverData.get(20)); // systemComments
                stmt.setString(22, (String) serverData.get(21)); // engineer
                stmt.setString(23, (String) serverData.get(22)); // mgrEntry
                stmt.setInt(24, (Integer) serverData.get(23)); // dmgrPort
                stmt.setString(25, (String) serverData.get(24)); // serverRack
                stmt.setString(26, (String) serverData.get(25)); // rackPosition
                stmt.setString(27, (String) serverData.get(26)); // owningDmgr

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
    public synchronized boolean removeExistingServer(final String serverGuid) throws SQLException
    {
        final String methodName = IServerDataDAO.CNAME + "#removeExistingServer(final String serverGuid) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("serverGuid: {}", serverGuid);
        }

        Connection sqlConn = null;
        boolean isComplete = false;
        CallableStatement stmt = null;

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

                stmt = sqlConn.prepareCall("{CALL removeServerFromAssets(?)}");
                stmt.setString(1, serverGuid);

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
    public synchronized List<String[]> getInstalledServers() throws SQLException
    {
        final String methodName = IServerDataDAO.CNAME + "#getInstalledServers() throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
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
            else
            {
                sqlConn.setAutoCommit(true);

                stmt = sqlConn.prepareCall("{CALL retrServerList()}");

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
                    responseData = new ArrayList<String[]>();

                    while (resultSet.next())
                    {
                        String[] serverData = new String[]
                        {
                            resultSet.getString(1), // guid
                            resultSet.getString(2), // oper hostname
                            resultSet.getString(3), // type
                        };

                        if (DEBUG)
                        {
                            for (String str : serverData)
                            {
                                DEBUGGER.debug(str);
                            }
                        }

                        responseData.add(serverData);
                    }

                    if (DEBUG)
                    {
                        for (String[] str : responseData)
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
                    throw new SQLException("No results were found");
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

        return responseData;
    }

    @Override
    public synchronized List<String> getInstalledServer(final String attribute) throws SQLException
    {
        final String methodName = IServerDataDAO.CNAME + "#getInstalledServer(final String attribute) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("attribute: {}", attribute);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        CallableStatement stmt = null;
        List<String> responseData = null;

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

                // we dont know what we have here - it could be a guid or it could be a hostname
                // most commonly it'll be a guid, but we're going to search anyway
                stmt = sqlConn.prepareCall("{ CALL retrServerData(?) }");
                stmt.setString(1, attribute);

                if (DEBUG)
                {
                    DEBUGGER.debug("stmt: {}", stmt);
                }

                resultSet = stmt.executeQuery();

                if (DEBUG)
                {
                    DEBUGGER.debug("resultSet: {}", resultSet);
                }

                if (resultSet.next())
                {
                    resultSet.first();

                    responseData = new ArrayList<String>(
                            Arrays.asList(
                                    resultSet.getString(1), // guid
                                    resultSet.getString(2), // ostype
                                    resultSet.getString(3), // status
                                    resultSet.getString(4), // region
                                    resultSet.getString(5), // type
                                    resultSet.getString(6), // domain name
                                    resultSet.getString(7), // cpu type
                                    String.valueOf(resultSet.getInt(8)), // cpu count
                                    resultSet.getString(9), // server rack
                                    resultSet.getString(10), // rack position
                                    resultSet.getString(11), // server model
                                    resultSet.getString(12), // serial number
                                    resultSet.getString(13), // installed memory
                                    resultSet.getString(14), // oper ip
                                    resultSet.getString(15), // oper hostname
                                    resultSet.getString(16), // mgmt ip
                                    resultSet.getString(17), // mgmt hostname
                                    resultSet.getString(18), // backup ip
                                    resultSet.getString(19), // backup hostname
                                    resultSet.getString(20), // nas ip
                                    resultSet.getString(21), // nas hostname
                                    resultSet.getString(22), // nat ip
                                    resultSet.getString(23), // comments
                                    resultSet.getString(24), // assigned engineer
                                    String.valueOf(resultSet.getInt(27)), // dmgrPort
                                    resultSet.getString(28), // owningDmgr
                                    resultSet.getString(29))); // mgrurl

                    if (DEBUG)
                    {
                        DEBUGGER.debug("responseData: {}", responseData);
                    }
                }
                else
                {
                    throw new SQLException("No results were found");
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

        return responseData;
    }

    @Override
    public synchronized List<String[]> getServersByAttribute(final String value) throws SQLException
    {
        final String methodName = IServerDataDAO.CNAME + "#getServersByAttribute(final String value) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
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
            else
            {
                sqlConn.setAutoCommit(true);

                stmt = sqlConn.prepareCall("{CALL getServerByAttribute(?)}");
                stmt.setString(1, value);

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
                    responseData = new ArrayList<String[]>();

                    while (resultSet.next())
                    {
                        String[] serverData = new String[]
                        {
                            resultSet.getString(1), // guid
                            resultSet.getString(2), // ostype
                            resultSet.getString(3), // status
                            resultSet.getString(4), // region
                            resultSet.getString(5), // type
                            resultSet.getString(6), // domain name
                            resultSet.getString(7), // cpu type
                            String.valueOf(resultSet.getInt(8)), // cpu count
                            resultSet.getString(9), // server rack
                            resultSet.getString(10), // rack position
                            resultSet.getString(11), // server model
                            resultSet.getString(12), // serial number
                            resultSet.getString(13), // installed memory
                            resultSet.getString(14), // oper ip
                            resultSet.getString(15), // oper hostname
                            resultSet.getString(16), // mgmt ip
                            resultSet.getString(17), // mgmt hostname
                            resultSet.getString(18), // backup ip
                            resultSet.getString(19), // backup hostname
                            resultSet.getString(20), // nas ip
                            resultSet.getString(21), // nas hostname
                            resultSet.getString(22), // nat ip
                            resultSet.getString(23), // comments
                            resultSet.getString(24), // assigned engineer
                            String.valueOf(resultSet.getInt(27)), // dmgrPort
                            resultSet.getString(28), // owningDmgr
                            resultSet.getString(29) // mgrurl
                        };

                        if (DEBUG)
                        {
                            for (String str : serverData)
                            {
                                DEBUGGER.debug(str);
                            }
                        }

                        responseData.add(serverData);
                    }

                    if (DEBUG)
                    {
                        for (String[] str : responseData)
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
                    throw new SQLException("No results were found");
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

        return responseData;
    }

    @Override
    public synchronized boolean modifyServerData(final List<String> serverData) throws SQLException
    {
        final String methodName = IServerDataDAO.CNAME + "#modifyServerData(final List<String> serverData) throws SQLException";
        
        if (DEBUG)
        {
        	DEBUGGER.debug(methodName);
            DEBUGGER.debug("request: {}", serverData);
        }
        
        return false;
    }

    @Override
    public synchronized List<String[]> getServersByAttributeWithRegion(final String attribute, final String region) throws SQLException
    {
        final String methodName = IServerDataDAO.CNAME + "#getServersByAttributeWithRegion(final String attribute, final String region) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", attribute);
            DEBUGGER.debug("Value: {}", region);
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
            else
            {
                sqlConn.setAutoCommit(true);

                stmt = sqlConn.prepareCall("{CALL getServerByAttributeWithRegion(?, ?)}");
                stmt.setString(1, attribute);
                stmt.setString(2, region);

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
                    responseData = new ArrayList<String[]>();

                    while (resultSet.next())
                    {
                        String[] serverData = new String[]
                        {
                            resultSet.getString(1), // guid
                            resultSet.getString(2), // ostype
                            resultSet.getString(3), // status
                            resultSet.getString(4), // region
                            resultSet.getString(5), // type
                            resultSet.getString(6), // domain name
                            resultSet.getString(7), // cpu type
                            String.valueOf(resultSet.getInt(8)), // cpu count
                            resultSet.getString(9), // server rack
                            resultSet.getString(10), // rack position
                            resultSet.getString(11), // server model
                            resultSet.getString(12), // serial number
                            resultSet.getString(13), // installed memory
                            resultSet.getString(14), // oper ip
                            resultSet.getString(15), // oper hostname
                            resultSet.getString(16), // mgmt ip
                            resultSet.getString(17), // mgmt hostname
                            resultSet.getString(18), // backup ip
                            resultSet.getString(19), // backup hostname
                            resultSet.getString(20), // nas ip
                            resultSet.getString(21), // nas hostname
                            resultSet.getString(22), // nat ip
                            resultSet.getString(23), // comments
                            resultSet.getString(24), // assigned engineer
                            String.valueOf(resultSet.getInt(27)), // dmgrPort
                            resultSet.getString(28), // owningDmgr
                            resultSet.getString(29) // mgrurl
                        };

                        if (DEBUG)
                        {
                            for (String str : serverData)
                            {
                                DEBUGGER.debug(str);
                            }
                        }

                        responseData.add(serverData);
                    }

                    if (DEBUG)
                    {
                        for (String[] str : responseData)
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
                    throw new SQLException("No results were found");
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

        return responseData;
    }
}
