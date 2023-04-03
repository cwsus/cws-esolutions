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
 * File: IPackageDataDAO.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.util.List;
import java.util.Arrays;
import java.util.Objects;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import org.apache.commons.lang3.StringUtils;

import com.cws.esolutions.core.dao.interfaces.IServerDataDAO;
/**
 * @see com.cws.esolutions.core.dao.interfaces.IServerDataDAO
 */
public class ServerDataDAOImpl implements IServerDataDAO
{
    /**
     * @see com.cws.esolutions.core.dao.interfaces.IServerDataDAO#addServer(java.util.List)
     */
    public synchronized boolean addServer(final List<Object> serverData) throws SQLException
    {
        final String methodName = IServerDataDAO.CNAME + "#addServer(final List<Object> serverData) throws SQLException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("platformData: {}", serverData);
        }

        Connection sqlConn = null;
        boolean isComplete = false;
        PreparedStatement stmt = null;

        if (Objects.isNull(dataSource))
        {
        	throw new SQLException("A datasource connection could not be obtained.");
        }

        try
        {
            sqlConn = dataSource.getConnection();

            if ((Objects.isNull(sqlConn)) || (sqlConn.isClosed()))
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);

            stmt = sqlConn.prepareStatement("{ CALL insertNewServer(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }",
            		ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
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
                DEBUGGER.debug("PreparedStatement: {}", stmt);
            }

            isComplete = (!(stmt.execute()));

            if (DEBUG)
            {
                DEBUGGER.debug("isComplete: {}", isComplete);
            }
        }
        catch (final SQLException sqx)
        {
            throw new SQLException(sqx.getMessage(), sqx);
        }
        finally
        {
            if (!(Objects.isNull(stmt)))
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
     * @see com.cws.esolutions.core.dao.interfaces.IServerDataDAO#updateServer(java.lang.String, java.util.List)
     */
    public synchronized boolean updateServer(final String serverGuid, final List<Object> serverData) throws SQLException
    {
        final String methodName = IServerDataDAO.CNAME + "#updateServer(final String serverGuid, final List<Object> serverData) throws SQLException";
        
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
        PreparedStatement stmt = null;

        if (Objects.isNull(dataSource))
        {
        	throw new SQLException("A datasource connection could not be obtained.");
        }

        try
        {
            sqlConn = dataSource.getConnection();

            if ((Objects.isNull(sqlConn)) || (sqlConn.isClosed()))
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }
 
            sqlConn.setAutoCommit(true);

            stmt = sqlConn.prepareStatement("{ CALL updateServerData(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }",
            		ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
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
                DEBUGGER.debug("PreparedStatement: {}", stmt);
            }

            isComplete = (!(stmt.execute()));

            if (DEBUG)
            {
                DEBUGGER.debug("isComplete: {}", isComplete);
            }
        }
        catch (final SQLException sqx)
        {
            throw new SQLException(sqx.getMessage(), sqx);
        }
        finally
        {
            if (!(Objects.isNull(stmt)))
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
     * @see com.cws.esolutions.core.dao.interfaces.IServerDataDAO#removeServer(java.lang.String)
     */
    public synchronized boolean removeServer(final String serverGuid) throws SQLException
    {
        final String methodName = IServerDataDAO.CNAME + "#removeServer(final String serverGuid) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("serverGuid: {}", serverGuid);
        }

        Connection sqlConn = null;
        boolean isComplete = false;
        PreparedStatement stmt = null;

        if (Objects.isNull(dataSource))
        {
        	throw new SQLException("A datasource connection could not be obtained.");
        }

        try
        {
            sqlConn = dataSource.getConnection();

            if ((Objects.isNull(sqlConn)) || (sqlConn.isClosed()))
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);

            stmt = sqlConn.prepareStatement("{ CALL removeServerFromAssets(?) }", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stmt.setString(1, serverGuid);

            if (DEBUG)
            {
                DEBUGGER.debug("PreparedStatement: {}", stmt);
            }

            isComplete = (!(stmt.execute()));

            if (DEBUG)
            {
                DEBUGGER.debug("isComplete: {}", isComplete);
            }
        }
        catch (final SQLException sqx)
        {
            throw new SQLException(sqx.getMessage(), sqx);
        }
        finally
        {
            if (!(Objects.isNull(stmt)))
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
     * @see com.cws.esolutions.core.dao.interfaces.IServerDataDAO#listServers(int)
     */
    public synchronized List<String[]> listServers(final int startRow) throws SQLException
    {
        final String methodName = IServerDataDAO.CNAME + "#listServers(final int startRow) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", startRow);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        PreparedStatement stmt = null;
        List<String[]> responseData = null;

        if (Objects.isNull(dataSource))
        {
        	throw new SQLException("A datasource connection could not be obtained.");
        }

        try
        {
            sqlConn = dataSource.getConnection();

            if ((Objects.isNull(sqlConn)) || (sqlConn.isClosed()))
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);

            stmt = sqlConn.prepareStatement("{ CALL retrServerList(?) }", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stmt.setInt(1, startRow);

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
                    responseData = new ArrayList<String[]>();

                    while (resultSet.next())
                    {
                        String[] serverData = new String[]
                        {
                                resultSet.getString(1), // SYSTEM_GUID
                                resultSet.getString(2) // NAME
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
        catch (final SQLException sqx)
        {
            throw new SQLException(sqx.getMessage(), sqx);
        }
        finally
        {
            if (!(Objects.isNull(resultSet)))
            {
                resultSet.close();
            }

            if (!(Objects.isNull(stmt)))
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
     * @see com.cws.esolutions.core.dao.interfaces.IServerDataDAO#getServersByAttribute(java.lang.String, int)
     */
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
        PreparedStatement stmt = null;
        List<Object[]> responseData = null;

        if (Objects.isNull(dataSource))
        {
        	throw new SQLException("A datasource connection could not be obtained.");
        }

        try
        {
            sqlConn = dataSource.getConnection();

            if ((Objects.isNull(sqlConn)) || (sqlConn.isClosed()))
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

            stmt = sqlConn.prepareStatement("{ CALL getServerByAttribute(?, ?) }", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stmt.setString(1, sBuilder.toString().trim());
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
                    responseData = new ArrayList<Object[]>();

                    while (resultSet.next())
                    {
                        Object[] data = new Object[]
                        {
                            resultSet.getString(1), // GUID
                            resultSet.getString(2), // OPER_HOSTNAME
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
        catch (final SQLException sqx)
        {
            throw new SQLException(sqx.getMessage(), sqx);
        }
        finally
        {
            if (!(Objects.isNull(resultSet)))
            {
                resultSet.close();
            }

            if (!(Objects.isNull(stmt)))
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
     * @see com.cws.esolutions.core.dao.interfaces.IServerDataDAO#getServer(java.lang.String)
     */
    public synchronized List<Object> getServer(final String attribute) throws SQLException
    {
        final String methodName = IServerDataDAO.CNAME + "#getServer(final String attribute) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("attribute: {}", attribute);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        PreparedStatement stmt = null;
        List<Object> responseData = null;

        if (Objects.isNull(dataSource))
        {
        	throw new SQLException("A datasource connection could not be obtained.");
        }

        try
        {
            sqlConn = dataSource.getConnection();

            if ((Objects.isNull(sqlConn)) || (sqlConn.isClosed()))
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);

            // we dont know what we have here - it could be a guid or it could be a hostname
            // most commonly it'll be a guid, but we're going to search anyway
            stmt = sqlConn.prepareStatement("{ CALL retrServerData(?) }", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stmt.setString(1, attribute);

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
                    resultSet.first();

                    responseData = new ArrayList<Object>(
                            Arrays.asList(
                                    resultSet.getString(1), // T1.SYSTEM_GUID
                                    resultSet.getString(2), // T1.SYSTEM_OSTYPE
                                    resultSet.getString(3), // T1.SYSTEM_STATUS
                                    resultSet.getString(4), // T1.SYSTEM_REGION
                                    resultSet.getString(5), // T1.NETWORK_PARTITION
                                    resultSet.getString(6), // T1.SYSTEM_TYPE
                                    resultSet.getString(7), // T1.DOMAIN_NAME
                                    resultSet.getString(8), // T1.CPU_TYPE
                                    resultSet.getInt(9), // T1.CPU_COUNT
                                    resultSet.getString(10), // T1.SERVER_RACK
                                    resultSet.getString(11), // T1.RACK_POSITION
                                    resultSet.getString(12), // T1.SERVER_MODEL
                                    resultSet.getString(13), // T1.SERIAL_NUMBER
                                    resultSet.getInt(14), // T1.INSTALLED_MEMORY
                                    resultSet.getString(15), // T1.OPER_IP
                                    resultSet.getString(16), // T1.OPER_HOSTNAME
                                    resultSet.getString(17), // T1.MGMT_IP
                                    resultSet.getString(18), // T1.MGMT_HOSTNAME
                                    resultSet.getString(19), // T1.BKUP_IP
                                    resultSet.getString(20), // T1.BKUP_HOSTNAME
                                    resultSet.getString(21), // T1.NAS_IP
                                    resultSet.getString(22), // T1.NAS_HOSTNAME
                                    resultSet.getString(23), // T1.NAT_ADDR
                                    resultSet.getString(24), // T1.COMMENTS
                                    resultSet.getString(25), // T1.ASSIGNED_ENGINEER
                                    resultSet.getTimestamp(26), // T1.ADD_DATE
                                    resultSet.getTimestamp(27), // T1.DELETE_DATE
                                    resultSet.getInt(28), // T1.DMGR_PORT
                                    resultSet.getString(29), // T1.OWNING_DMGR
                                    resultSet.getString(30), // T1.MGR_ENTRY
                                    resultSet.getString(31), // T2.GUID
                                    resultSet.getString(32))); // T2.NAME

                    if (DEBUG)
                    {
                        DEBUGGER.debug("responseData: {}", responseData);
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
            if (!(Objects.isNull(resultSet)))
            {
                resultSet.close();
            }

            if (!(Objects.isNull(stmt)))
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
