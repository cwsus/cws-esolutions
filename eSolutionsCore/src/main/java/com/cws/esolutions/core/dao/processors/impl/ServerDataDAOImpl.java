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

                stmt = sqlConn.prepareCall("{CALL insertNewServer(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
                stmt.setString(1, (String) serverData.get(0)); // systemGuid
                stmt.setString(2, (String) serverData.get(1)); // systemOs
                stmt.setString(3, (String) serverData.get(2)); // systemStatus
                stmt.setString(4, (String) serverData.get(3)); // systemRegion
                stmt.setString(5, (String) serverData.get(4)); // networkPartiton
                stmt.setString(6, (String) serverData.get(5)); // datacenter
                stmt.setString(7, (String) serverData.get(6)); // systemType
                stmt.setString(8, (String) serverData.get(7)); // domainName
                stmt.setString(9, (String) serverData.get(8)); // cpuType
                stmt.setInt(10, (Integer) serverData.get(9)); // cpuCount
                stmt.setString(11, (String) serverData.get(10)); // serverModel
                stmt.setString(12, (String) serverData.get(11)); // serialNumber
                stmt.setInt(13, (Integer) serverData.get(12)); // installedMemory
                stmt.setString(14, (String) serverData.get(13)); // operIp
                stmt.setString(15, (String) serverData.get(14)); // operHostname
                stmt.setString(16, (String) serverData.get(15)); // mgmtIp
                stmt.setString(17, (String) serverData.get(16)); // mgmtHostname
                stmt.setString(18, (String) serverData.get(17)); // backupIp
                stmt.setString(19, (String) serverData.get(18)); // backupHostname
                stmt.setString(20, (String) serverData.get(19)); // nasIp
                stmt.setString(21, (String) serverData.get(20)); // nasHostname
                stmt.setString(22, (String) serverData.get(21)); // natAddr
                stmt.setString(23, (String) serverData.get(22)); // systemComments
                stmt.setString(24, (String) serverData.get(23)); // engineer
                stmt.setString(25, (String) serverData.get(24)); // mgrEntry
                stmt.setInt(26, (Integer) serverData.get(25)); // dmgrPort
                stmt.setString(27, (String) serverData.get(26)); // serverRack
                stmt.setString(28, (String) serverData.get(27)); // rackPosition
                stmt.setString(29, (String) serverData.get(28)); // owningDmgr

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
    public synchronized List<String[]> getInstalledServers(final int startRow) throws SQLException
    {
        final String methodName = IServerDataDAO.CNAME + "#getInstalledServers(final int startRow) throws SQLException";

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
            else
            {
                sqlConn.setAutoCommit(true);

                stmt = sqlConn.prepareCall("{CALL retrServerList(?)}");
                stmt.setInt(1, startRow);

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
                                resultSet.getString(5), // partition
                                resultSet.getString(6), // datacenter
                                resultSet.getString(7), // type
                                resultSet.getString(8), // domain name
                                resultSet.getString(9), // cpu type
                                String.valueOf(resultSet.getInt(10)), // cpu count
                                resultSet.getString(11), // server rack
                                resultSet.getString(12), // rack position
                                resultSet.getString(13), // server model
                                resultSet.getString(14), // serial number
                                resultSet.getString(15), // installed memory
                                resultSet.getString(16), // oper ip
                                resultSet.getString(17), // oper hostname
                                resultSet.getString(18), // mgmt ip
                                resultSet.getString(19), // mgmt hostname
                                resultSet.getString(20), // backup ip
                                resultSet.getString(21), // backup hostname
                                resultSet.getString(22), // nas ip
                                resultSet.getString(23), // nas hostname
                                resultSet.getString(24), // nat ip
                                resultSet.getString(25), // comments
                                resultSet.getString(26), // assigned engineer
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
                                    resultSet.getString(5), // partition
                                    resultSet.getString(6), // datacenter
                                    resultSet.getString(7), // type
                                    resultSet.getString(8), // domain name
                                    resultSet.getString(9), // cpu type
                                    String.valueOf(resultSet.getInt(10)), // cpu count
                                    resultSet.getString(11), // server rack
                                    resultSet.getString(12), // rack position
                                    resultSet.getString(13), // server model
                                    resultSet.getString(14), // serial number
                                    resultSet.getString(15), // installed memory
                                    resultSet.getString(16), // oper ip
                                    resultSet.getString(17), // oper hostname
                                    resultSet.getString(18), // mgmt ip
                                    resultSet.getString(19), // mgmt hostname
                                    resultSet.getString(20), // backup ip
                                    resultSet.getString(21), // backup hostname
                                    resultSet.getString(22), // nas ip
                                    resultSet.getString(23), // nas hostname
                                    resultSet.getString(24), // nat ip
                                    resultSet.getString(25), // comments
                                    resultSet.getString(26), // assigned engineer
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
    public synchronized int getServerCount() throws SQLException
    {
        final String methodName = IServerDataDAO.CNAME + "#getServerCount() throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
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
                throw new SQLException("Unable to obtain application datasource connection");
            }
            else
            {
                sqlConn.setAutoCommit(true);
                stmt = sqlConn.prepareCall("{ CALL getServerCount() }");

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

                    count = resultSet.getInt(1);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("count: {}", count);
                    }
                }
                else
                {
                    throw new SQLException("No server entries were located.");
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
    public synchronized List<String[]> getServersByAttribute(final String value, final int startRow) throws SQLException
    {
        final String methodName = IServerDataDAO.CNAME + "#getServersByAttribute(final String value, final int startRow) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
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
            else
            {
                sqlConn.setAutoCommit(true);

                stmt = sqlConn.prepareCall("{CALL getServerByAttribute(?, ?)}");
                stmt.setString(1, value);
                stmt.setInt(2, startRow);

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
                                resultSet.getString(5), // partition
                                resultSet.getString(6), // datacenter
                                resultSet.getString(7), // type
                                resultSet.getString(8), // domain name
                                resultSet.getString(9), // cpu type
                                String.valueOf(resultSet.getInt(10)), // cpu count
                                resultSet.getString(11), // server rack
                                resultSet.getString(12), // rack position
                                resultSet.getString(13), // server model
                                resultSet.getString(14), // serial number
                                resultSet.getString(15), // installed memory
                                resultSet.getString(16), // oper ip
                                resultSet.getString(17), // oper hostname
                                resultSet.getString(18), // mgmt ip
                                resultSet.getString(19), // mgmt hostname
                                resultSet.getString(20), // backup ip
                                resultSet.getString(21), // backup hostname
                                resultSet.getString(22), // nas ip
                                resultSet.getString(23), // nas hostname
                                resultSet.getString(24), // nat ip
                                resultSet.getString(25), // comments
                                resultSet.getString(26), // assigned engineer
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
    public synchronized List<String[]> getServersByAttributeWithRegion(final String attribute, final String region, final int startRow) throws SQLException
    {
        final String methodName = IServerDataDAO.CNAME + "#getServersByAttributeWithRegion(final String attribute, final String region, final int startRow) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", attribute);
            DEBUGGER.debug("Value: {}", region);
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
            else
            {
                sqlConn.setAutoCommit(true);

                stmt = sqlConn.prepareCall("{CALL getServerByAttributeWithRegion(?, ?, ?)}");
                stmt.setString(1, attribute);
                stmt.setString(2, region);
                stmt.setInt(3, startRow);

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
                                resultSet.getString(5), // partition
                                resultSet.getString(6), // datacenter
                                resultSet.getString(7), // type
                                resultSet.getString(8), // domain name
                                resultSet.getString(9), // cpu type
                                String.valueOf(resultSet.getInt(10)), // cpu count
                                resultSet.getString(11), // server rack
                                resultSet.getString(12), // rack position
                                resultSet.getString(13), // server model
                                resultSet.getString(14), // serial number
                                resultSet.getString(15), // installed memory
                                resultSet.getString(16), // oper ip
                                resultSet.getString(17), // oper hostname
                                resultSet.getString(18), // mgmt ip
                                resultSet.getString(19), // mgmt hostname
                                resultSet.getString(20), // backup ip
                                resultSet.getString(21), // backup hostname
                                resultSet.getString(22), // nas ip
                                resultSet.getString(23), // nas hostname
                                resultSet.getString(24), // nat ip
                                resultSet.getString(25), // comments
                                resultSet.getString(26), // assigned engineer
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
