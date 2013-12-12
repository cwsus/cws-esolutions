/*
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
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.dao.interfaces
 * File: IPackageDataDAO.java
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Jan 4, 2013 3:36:54 PM
 *     Created.
 */
/**
 * @see com.cws.esolutions.core.dao.processors.interfaces.IServerDataDAO
 */
public class ServerDataDAOImpl implements IServerDataDAO
{
    /**
     * @see com.cws.esolutions.core.dao.processors.interfaces.IServerDataDAO#addNewServer(java.util.List)
     */
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

    /**
     * @see com.cws.esolutions.core.dao.processors.interfaces.IServerDataDAO#removeExistingServer(java.lang.String)
     */
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

    /**
     * @see com.cws.esolutions.core.dao.processors.interfaces.IServerDataDAO#getInstalledServers(int)
     */
    @Override
    public synchronized List<Object[]> getInstalledServers(final int startRow) throws SQLException
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
        List<Object[]> responseData = null;

        try
        {
            sqlConn = dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);

            stmt = sqlConn.prepareCall("{CALL retrServerList(?)}");
            stmt.setInt(1, startRow);

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
                    responseData = new ArrayList<Object[]>();

                    while (resultSet.next())
                    {
                        Object[] serverData = new Object[]
                        {
                                resultSet.getString(1), // SYSTEM_GUID
                                resultSet.getString(2), // SYSTEM_OSTYPE
                                resultSet.getString(3), // SYSTEM_STATUS
                                resultSet.getString(4), // SYSTEM_REGION
                                resultSet.getString(5), // NETWORK_PARTITION
                                resultSet.getString(6), // DATACENTER_GUID
                                resultSet.getString(7), // SYSTEM_TYPE
                                resultSet.getString(8), // DOMAIN_NAME
                                resultSet.getString(9), // CPU_TYPE
                                resultSet.getInt(10), // CPU_COUNT
                                resultSet.getString(11), // SERVER_RACK
                                resultSet.getString(12), // RACK_POSITION
                                resultSet.getString(13), // SERVER_MODEL
                                resultSet.getString(14), // SERIAL_NUMBER
                                resultSet.getInt(15), // INSTALLED_MEMORY
                                resultSet.getString(16), // OPER_IP
                                resultSet.getString(17), // OPER_HOSTNAME
                                resultSet.getString(18), // MGMT_IP
                                resultSet.getString(19), // MGMT_HOSTNAME
                                resultSet.getString(20), // BKUP_IP
                                resultSet.getString(21), // BKUP_HOSTNAME
                                resultSet.getString(22), // NAS_IP
                                resultSet.getString(23), // NAS_HOSTNAME
                                resultSet.getString(24), // NAT_ADDR
                                resultSet.getString(25), // COMMENTS
                                resultSet.getString(26), // ASSIGNED_ENGINEER
                                resultSet.getTimestamp(27), // ADD_DATE
                                resultSet.getTimestamp(28), // DELETE_DATE
                                resultSet.getInt(29), // DMGR_PORT
                                resultSet.getString(30), // OWNING_DMGR
                                resultSet.getString(31) // MGR_ENTRY
                        };

                        if (DEBUG)
                        {
                            for (Object obj : serverData)
                            {
                                DEBUGGER.debug("Value: {}", obj);
                            }
                        }

                        responseData.add(serverData);
                    }

                    if (DEBUG)
                    {
                        for (Object[] objArr : responseData)
                        {
                            for (Object obj : objArr)
                            {
                                DEBUGGER.debug("Value: {}", obj);
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
     * @see com.cws.esolutions.core.dao.processors.interfaces.IServerDataDAO#getServersForDmgr(java.lang.String)
     */
    @Override
    public synchronized List<Object[]> getServersForDmgr(final String dmgr) throws SQLException
    {
        final String methodName = IServerDataDAO.CNAME + "#getServersForDmgr(final String dmgr) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", dmgr);
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

            stmt = sqlConn.prepareCall("{CALL retrServersForDmgr(?)}");
            stmt.setString(1, dmgr);

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
                    responseData = new ArrayList<Object[]>();

                    while (resultSet.next())
                    {
                        Object[] serverData = new Object[]
                        {
                                resultSet.getString(1), // SYSTEM_GUID
                                resultSet.getString(2), // SYSTEM_OSTYPE
                                resultSet.getString(3), // SYSTEM_STATUS
                                resultSet.getString(4), // SYSTEM_REGION
                                resultSet.getString(5), // NETWORK_PARTITION
                                resultSet.getString(6), // DATACENTER_GUID
                                resultSet.getString(7), // SYSTEM_TYPE
                                resultSet.getString(8), // DOMAIN_NAME
                                resultSet.getString(9), // CPU_TYPE
                                resultSet.getInt(10), // CPU_COUNT
                                resultSet.getString(11), // SERVER_RACK
                                resultSet.getString(12), // RACK_POSITION
                                resultSet.getString(13), // SERVER_MODEL
                                resultSet.getString(14), // SERIAL_NUMBER
                                resultSet.getInt(15), // INSTALLED_MEMORY
                                resultSet.getString(16), // OPER_IP
                                resultSet.getString(17), // OPER_HOSTNAME
                                resultSet.getString(18), // MGMT_IP
                                resultSet.getString(19), // MGMT_HOSTNAME
                                resultSet.getString(20), // BKUP_IP
                                resultSet.getString(21), // BKUP_HOSTNAME
                                resultSet.getString(22), // NAS_IP
                                resultSet.getString(23), // NAS_HOSTNAME
                                resultSet.getString(24), // NAT_ADDR
                                resultSet.getString(25), // COMMENTS
                                resultSet.getString(26), // ASSIGNED_ENGINEER
                                resultSet.getTimestamp(27), // ADD_DATE
                                resultSet.getTimestamp(28), // DELETE_DATE
                                resultSet.getString(29), // OWNING_DMGR
                        };

                        if (DEBUG)
                        {
                            for (Object obj : serverData)
                            {
                                DEBUGGER.debug("Value: {}", obj);
                            }
                        }

                        responseData.add(serverData);
                    }

                    if (DEBUG)
                    {
                        for (Object[] objArr : responseData)
                        {
                            for (Object obj : objArr)
                            {
                                DEBUGGER.debug("Value: {}", obj);
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
     * @see com.cws.esolutions.core.dao.processors.interfaces.IServerDataDAO#getInstalledServer(java.lang.String)
     */
    @Override
    public synchronized List<Object> getInstalledServer(final String attribute) throws SQLException
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
        List<Object> responseData = null;

        try
        {
            sqlConn = dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);

            // we dont know what we have here - it could be a guid or it could be a hostname
            // most commonly it'll be a guid, but we're going to search anyway
            stmt = sqlConn.prepareCall("{ CALL retrServerData(?) }");
            stmt.setString(1, attribute);

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

                    responseData = new ArrayList<Object>(
                            Arrays.asList(
                                    resultSet.getString(1), // SYSTEM_GUID
                                    resultSet.getString(2), // SYSTEM_OSTYPE
                                    resultSet.getString(3), // SYSTEM_STATUS
                                    resultSet.getString(4), // SYSTEM_REGION
                                    resultSet.getString(5), // NETWORK_PARTITION
                                    resultSet.getString(6), // DATACENTER_GUID
                                    resultSet.getString(7), // SYSTEM_TYPE
                                    resultSet.getString(8), // DOMAIN_NAME
                                    resultSet.getString(9), // CPU_TYPE
                                    resultSet.getInt(10), // CPU_COUNT
                                    resultSet.getString(11), // SERVER_RACK
                                    resultSet.getString(12), // RACK_POSITION
                                    resultSet.getString(13), // SERVER_MODEL
                                    resultSet.getString(14), // SERIAL_NUMBER
                                    resultSet.getInt(15), // INSTALLED_MEMORY
                                    resultSet.getString(16), // OPER_IP
                                    resultSet.getString(17), // OPER_HOSTNAME
                                    resultSet.getString(18), // MGMT_IP
                                    resultSet.getString(19), // MGMT_HOSTNAME
                                    resultSet.getString(20), // BKUP_IP
                                    resultSet.getString(21), // BKUP_HOSTNAME
                                    resultSet.getString(22), // NAS_IP
                                    resultSet.getString(23), // NAS_HOSTNAME
                                    resultSet.getString(24), // NAT_ADDR
                                    resultSet.getString(25), // COMMENTS
                                    resultSet.getString(26), // ASSIGNED_ENGINEER
                                    resultSet.getTimestamp(27), // ADD_DATE
                                    resultSet.getTimestamp(28), // DELETE_DATE
                                    resultSet.getInt(29), // DMGR_PORT
                                    resultSet.getString(30), // OWNING_DMGR
                                    resultSet.getString(31))); // MGR_ENTRY

                    if (DEBUG)
                    {
                        DEBUGGER.debug("responseData: {}", responseData);
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

        return responseData;
    }

    /**
     * @see com.cws.esolutions.core.dao.processors.interfaces.IServerDataDAO#getServerCount()
     */
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

            sqlConn.setAutoCommit(true);
            stmt = sqlConn.prepareCall("{ CALL getServerCount() }");

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

    /**
     * @see com.cws.esolutions.core.dao.processors.interfaces.IServerDataDAO#validateServerHostName(java.lang.String)
     */
    @Override
    public synchronized int validateServerHostName(final String hostName) throws SQLException
    {
        final String methodName = IServerDataDAO.CNAME + "#validateServerHostName(final String hostName) throws SQLException";

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

            sqlConn.setAutoCommit(true);
            stmt = sqlConn.prepareCall("{ CALL validateServerHostName(?) }");
            stmt.setString(1, hostName);

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

    /**
     * @see com.cws.esolutions.core.dao.processors.interfaces.IServerDataDAO#getServersByAttribute(java.lang.String, int)
     */
    @Override
    public synchronized List<Object[]> getServersByAttribute(final String value, final int startRow) throws SQLException
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
        List<Object[]> responseData = null;

        try
        {
            sqlConn = dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);

            stmt = sqlConn.prepareCall("{CALL getServerByAttribute(?, ?)}");
            stmt.setString(1, value);
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
                    responseData = new ArrayList<Object[]>();

                    while (resultSet.next())
                    {
                        Object[] serverData = new Object[]
                        {
                                resultSet.getString(1), // SYSTEM_GUID
                                resultSet.getString(2), // SYSTEM_OSTYPE
                                resultSet.getString(3), // SYSTEM_STATUS
                                resultSet.getString(4), // SYSTEM_REGION
                                resultSet.getString(5), // NETWORK_PARTITION
                                resultSet.getString(6), // DATACENTER_GUID
                                resultSet.getString(7), // SYSTEM_TYPE
                                resultSet.getString(8), // DOMAIN_NAME
                                resultSet.getString(9), // CPU_TYPE
                                resultSet.getInt(10), // CPU_COUNT
                                resultSet.getString(11), // SERVER_RACK
                                resultSet.getString(12), // RACK_POSITION
                                resultSet.getString(13), // SERVER_MODEL
                                resultSet.getString(14), // SERIAL_NUMBER
                                resultSet.getInt(15), // INSTALLED_MEMORY
                                resultSet.getString(16), // OPER_IP
                                resultSet.getString(17), // OPER_HOSTNAME
                                resultSet.getString(18), // MGMT_IP
                                resultSet.getString(19), // MGMT_HOSTNAME
                                resultSet.getString(20), // BKUP_IP
                                resultSet.getString(21), // BKUP_HOSTNAME
                                resultSet.getString(22), // NAS_IP
                                resultSet.getString(23), // NAS_HOSTNAME
                                resultSet.getString(24), // NAT_ADDR
                                resultSet.getString(25), // COMMENTS
                                resultSet.getString(26), // ASSIGNED_ENGINEER
                                resultSet.getTimestamp(27), // ADD_DATE
                                resultSet.getTimestamp(28), // DELETE_DATE
                                resultSet.getInt(29), // DMGR_PORT
                                resultSet.getString(30), // OWNING_DMGR
                                resultSet.getString(31) // MGR_ENTRY
                        };

                        if (DEBUG)
                        {
                            for (Object obj : serverData)
                            {
                                DEBUGGER.debug("Value: {}", obj);
                            }
                        }

                        responseData.add(serverData);
                    }

                    if (DEBUG)
                    {
                        for (Object[] objArr : responseData)
                        {
                            for (Object obj : objArr)
                            {
                                DEBUGGER.debug("Value: {}", obj);
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
     * @see com.cws.esolutions.core.dao.processors.interfaces.IServerDataDAO#modifyServerData(java.lang.String, java.util.List)
     */
    @Override
    public synchronized boolean modifyServerData(final String serverGuid, final List<Object> serverData) throws SQLException
    {
        final String methodName = IServerDataDAO.CNAME + "#modifyServerData(final String serverGuid, final List<Object> serverData) throws SQLException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", serverGuid);

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
 
            sqlConn.setAutoCommit(true);

            stmt = sqlConn.prepareCall("{CALL updateServerData(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
            stmt.setString(1, serverGuid); // systemGuid
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

    /**
     * @see com.cws.esolutions.core.dao.processors.interfaces.IServerDataDAO#getServersByAttributeWithRegion(java.lang.String, java.lang.String, int)
     */
    @Override
    public synchronized List<Object[]> getServersByAttributeWithRegion(final String attribute, final String region, final int startRow) throws SQLException
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
        List<Object[]> responseData = null;

        try
        {
            sqlConn = dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);

            stmt = sqlConn.prepareCall("{CALL getServerByAttributeWithRegion(?, ?, ?)}");
            stmt.setString(1, attribute);
            stmt.setString(2, region);
            stmt.setInt(3, startRow);

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
                    responseData = new ArrayList<Object[]>();

                    while (resultSet.next())
                    {
                        Object[] serverData = new Object[]
                        {
                                resultSet.getString(1), // SYSTEM_GUID
                                resultSet.getString(2), // SYSTEM_OSTYPE
                                resultSet.getString(3), // SYSTEM_STATUS
                                resultSet.getString(4), // SYSTEM_REGION
                                resultSet.getString(5), // NETWORK_PARTITION
                                resultSet.getString(6), // DATACENTER_GUID
                                resultSet.getString(7), // SYSTEM_TYPE
                                resultSet.getString(8), // DOMAIN_NAME
                                resultSet.getString(9), // CPU_TYPE
                                resultSet.getInt(10), // CPU_COUNT
                                resultSet.getString(11), // SERVER_RACK
                                resultSet.getString(12), // RACK_POSITION
                                resultSet.getString(13), // SERVER_MODEL
                                resultSet.getString(14), // SERIAL_NUMBER
                                resultSet.getInt(15), // INSTALLED_MEMORY
                                resultSet.getString(16), // OPER_IP
                                resultSet.getString(17), // OPER_HOSTNAME
                                resultSet.getString(18), // MGMT_IP
                                resultSet.getString(19), // MGMT_HOSTNAME
                                resultSet.getString(20), // BKUP_IP
                                resultSet.getString(21), // BKUP_HOSTNAME
                                resultSet.getString(22), // NAS_IP
                                resultSet.getString(23), // NAS_HOSTNAME
                                resultSet.getString(24), // NAT_ADDR
                                resultSet.getString(25), // COMMENTS
                                resultSet.getString(26), // ASSIGNED_ENGINEER
                                resultSet.getTimestamp(27), // ADD_DATE
                                resultSet.getTimestamp(28), // DELETE_DATE
                                resultSet.getInt(29), // DMGR_PORT
                                resultSet.getString(30), // OWNING_DMGR
                                resultSet.getString(31) // MGR_ENTRY
                        };

                        if (DEBUG)
                        {
                            for (Object obj : serverData)
                            {
                                DEBUGGER.debug("Value: {}", obj);
                            }
                        }

                        responseData.add(serverData);
                    }

                    if (DEBUG)
                    {
                        for (Object[] objArr : responseData)
                        {
                            for (Object obj : objArr)
                            {
                                DEBUGGER.debug("Value: {}", obj);
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
