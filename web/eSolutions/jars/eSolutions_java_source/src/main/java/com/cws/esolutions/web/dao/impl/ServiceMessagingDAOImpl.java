/*
 * Copyright (c) 2009 - 2014 CaspersBox Web Services
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
package com.cws.esolutions.web.dao.impl;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.security.processors.impl
 * File: FileSecurityProcessorImpl.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.List;
import java.sql.ResultSet;
import java.sql.Connection;
import java.util.ArrayList;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.CallableStatement;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cws.esolutions.web.dao.interfaces.IMessagingDAO;

public class ServiceMessagingDAOImpl implements IMessagingDAO
{
    @Autowired private DataSource dataSource = null;

    public final void setDataSource(final DataSource value)
    {
        final String methodName = IMessagingDAO.CNAME + "#setDataSource(final DataSource value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.dataSource = value;
    }

    @Override
    public synchronized boolean insertMessage(final List<Object> messageList) throws SQLException
    {
        final String methodName = IMessagingDAO.CNAME + "#insertMessage(final List<Object> messageList) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("messageList: {}", messageList);
        }

        Connection sqlConn = null;
        CallableStatement stmt = null;
        boolean isComplete = false;

        try
        {
            sqlConn = this.dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);
            stmt = sqlConn.prepareCall("{CALL submitSvcMessage(?, ?, ?, ?, ?, ?, ?, ?)}");
            stmt.setString(1, (String) messageList.get(0)); // message id
            stmt.setString(2, (String) messageList.get(1)); // message title
            stmt.setString(3, (String) messageList.get(2)); // message text
            stmt.setString(4, (String) messageList.get(3)); // author email
            stmt.setBoolean(5, (Boolean) messageList.get(4)); // is active
            stmt.setBoolean(6, (Boolean) messageList.get(5)); // is alert
            stmt.setBoolean(7, (Boolean) messageList.get(6)); // does expire
            stmt.setLong(8, (messageList.get(7) == null) ? 0 : (Long) messageList.get(7)); // expiry date

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
    public synchronized List<Object> retrieveMessage(final String messageId) throws SQLException
    {
        final String methodName = IMessagingDAO.CNAME + "#retrieveMessage(final String messageId) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug(messageId);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        CallableStatement stmt = null;
        List<Object> svcMessage = null;

        try
        {
            sqlConn = this.dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);
            stmt = sqlConn.prepareCall("{CALL retrServiceMessage(?)}");
            stmt.setString(1, messageId);

            if (DEBUG)
            {
                DEBUGGER.debug("CallableStatement: {}", stmt);
            }

            if (stmt.execute())
            {
                resultSet = stmt.getResultSet();

                if (resultSet.next())
                {
                    resultSet.first();
                    svcMessage = new ArrayList<>();
                    svcMessage.add(resultSet.getString(1)); // svc_message_id
                    svcMessage.add(resultSet.getString(2)); // svc_message_title
                    svcMessage.add(resultSet.getString(3)); // svc_message_txt
                    svcMessage.add(resultSet.getString(4)); // svc_message_author
                    svcMessage.add(resultSet.getTimestamp(5)); // svc_message_submitdate
                    svcMessage.add(resultSet.getBoolean(6)); // svc_message_active
                    svcMessage.add(resultSet.getBoolean(7)); // svc_message_alert
                    svcMessage.add(resultSet.getBoolean(8)); // svc_message_expires
                    svcMessage.add(resultSet.getTimestamp(9)); // svc_message_expirydate
                    svcMessage.add(resultSet.getTimestamp(10)); // svc_message_modifiedon
                    svcMessage.add(resultSet.getString(11)); // svc_message_modifiedby

                    if (DEBUG)
                    {
                        DEBUGGER.debug("svcMessage: {}", svcMessage);
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

        return svcMessage;
    }

    @Override
    public synchronized List<Object[]> retrieveMessages() throws SQLException
    {
        final String methodName = IMessagingDAO.CNAME + "#retrieveMessages() throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        CallableStatement stmt = null;
        List<Object[]> response = null;

        try
        {
            sqlConn = this.dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }
            
            sqlConn.setAutoCommit(true);
            stmt = sqlConn.prepareCall("{CALL retrServiceMessages()}");

            if (DEBUG)
            {
                DEBUGGER.debug("CallableStatement: {}", stmt);
            }

            if (stmt.execute())
            {
                resultSet = stmt.getResultSet();

                if (DEBUG)
                {
                    DEBUGGER.debug("ResultSet: {}", resultSet);
                }

                if (resultSet.next())
                {
                    resultSet.beforeFirst();
                    response = new ArrayList<>();

                    while (resultSet.next())
                    {
                        Object[] data = new Object[]
                        {
                                resultSet.getString(1), // svc_message_id
                                resultSet.getString(2), // svc_message_title
                                resultSet.getString(3), // svc_message_txt
                                resultSet.getString(4), // svc_message_author
                                resultSet.getTimestamp(5), // svc_message_submitdate
                                resultSet.getBoolean(6), // svc_message_active
                                resultSet.getBoolean(7), // svc_message_alert
                                resultSet.getBoolean(8), // svc_message_expires
                                resultSet.getTimestamp(9), // svc_message_expirydate
                                resultSet.getTimestamp(10), // svc_message_modifiedon
                                resultSet.getString(11) // svc_message_modifiedby
                        };

                        if (DEBUG)
                        {
                            DEBUGGER.debug("data: {}", data);
                        }

                        response.add(data);
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
    public synchronized List<Object[]> retrieveAlertMessages() throws SQLException
    {
        final String methodName = IMessagingDAO.CNAME + "#retrieveAlertMessages() throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        CallableStatement stmt = null;
        List<Object[]> response = null;

        try
        {
            sqlConn = this.dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }
            
            sqlConn.setAutoCommit(true);
            stmt = sqlConn.prepareCall("{CALL retrAlertMessages()}");

            if (DEBUG)
            {
                DEBUGGER.debug("CallableStatement: {}", stmt);
            }

            if (stmt.execute())
            {
                resultSet = stmt.getResultSet();

                if (DEBUG)
                {
                    DEBUGGER.debug("ResultSet: {}", resultSet);
                }

                if (resultSet.next())
                {
                    resultSet.beforeFirst();
                    response = new ArrayList<>();

                    while (resultSet.next())
                    {
                        Object[] data = new Object[]
                        {
                                resultSet.getString(1), // svc_message_id
                                resultSet.getString(2), // svc_message_title
                                resultSet.getString(3), // svc_message_txt
                                resultSet.getString(4), // svc_message_author
                                resultSet.getTimestamp(5), // svc_message_submitdate
                                resultSet.getBoolean(6), // svc_message_active
                                resultSet.getBoolean(7), // svc_message_alert
                                resultSet.getBoolean(8), // svc_message_expires
                                resultSet.getTimestamp(9), // svc_message_expirydate
                                resultSet.getTimestamp(10), // svc_message_modifiedon
                                resultSet.getString(11) // svc_message_modifiedby
                        };

                        if (DEBUG)
                        {
                            DEBUGGER.debug("data: {}", data);
                        }

                        response.add(data);
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
    public synchronized boolean updateMessage(final String messageId, final List<Object> messageList) throws SQLException
    {
        final String methodName = IMessagingDAO.CNAME + "#updateMessage(final String messageId, final List<Object> messageList) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("messageId: {}", messageId);
            DEBUGGER.debug("messageList: {}", messageList);
        }

        Connection sqlConn = null;
        boolean isComplete = false;
        CallableStatement stmt = null;

        try
        {
            sqlConn = this.dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);
            stmt = sqlConn.prepareCall("{CALL updateServiceMessage(?, ?, ?, ?, ?, ?, ?, ?)}");
            stmt.setString(1, messageId); // messageId
            stmt.setString(2, (String) messageList.get(0)); // messageTitle
            stmt.setString(3, (String) messageList.get(1)); // messageText
            stmt.setBoolean(4, (Boolean) messageList.get(2)); // active
            stmt.setBoolean(5, (Boolean) messageList.get(3)); // alert
            stmt.setBoolean(6, (Boolean) messageList.get(4)); // expiry
            stmt.setLong(7, (messageList.get(5) == null) ? 0 : (Long) messageList.get(5)); // expiry date
            stmt.setString(8, (String) messageList.get(6)); // modifyAuthor

            if (DEBUG)
            {
                DEBUGGER.debug("CallableStatement: {}", stmt);
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
    public synchronized boolean deleteMessage(final String messageId) throws SQLException
    {
        final String methodName = IMessagingDAO.CNAME + "#deleteMessage(final String messageId) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("messageId: {}", messageId);
        }

        Connection sqlConn = null;
        boolean isComplete = false;
        CallableStatement stmt = null;

        try
        {
            sqlConn = this.dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);
            stmt = sqlConn.prepareCall("{CALL removeSvcMessage(?)}");
            stmt.setString(1, messageId);

            if (DEBUG)
            {
                DEBUGGER.debug("CallableStatement: {}", stmt);
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
    public synchronized List<Object[]> getMessagesByAttribute(final String value) throws SQLException
    {
        final String methodName = IMessagingDAO.CNAME + "#getMessagesByAttribute(final String value) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        CallableStatement stmt = null;
        List<Object[]> responseData = null;

        try
        {
            sqlConn = this.dataSource.getConnection();

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

            stmt = sqlConn.prepareCall("{CALL getMessagesByAttribute(?)}");
            stmt.setString(1, sBuilder.toString().trim());

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
                    responseData = new ArrayList<>();

                    while (resultSet.next())
                    {
                        Object[] messageData = new Object[]
                        {
                                resultSet.getString(1), // svc_message_id
                                resultSet.getString(2), // svc_message_title
                                resultSet.getString(3), // svc_message_txt
                                resultSet.getString(4), // svc_message_author
                                resultSet.getTimestamp(5), // svc_message_submitdate
                                resultSet.getBoolean(6), // svc_message_active
                                resultSet.getBoolean(7), // svc_message_alert
                                resultSet.getBoolean(8), // svc_message_expires
                                resultSet.getTimestamp(9), // svc_message_expirydate
                                resultSet.getTimestamp(10), // svc_message_modifiedon
                                resultSet.getString(11) // svc_message_modifiedby
                        };

                        if (DEBUG)
                        {
                            for (Object obj : messageData)
                            {
                                DEBUGGER.debug("Value: {}", obj);
                            }
                        }

                        responseData.add(messageData);
                    }

                    if (DEBUG)
                    {
                        for (Object[] str : responseData)
                        {
                            for (Object obj : str)
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
