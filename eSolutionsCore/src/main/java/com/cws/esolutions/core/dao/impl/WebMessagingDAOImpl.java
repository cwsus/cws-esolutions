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
 * Package: com.cws.esolutions.security.processors.impl
 * File: FileSecurityProcessorImpl.java
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
import java.sql.Connection;
import java.util.ArrayList;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import org.apache.commons.lang3.StringUtils;

import com.cws.esolutions.core.dao.interfaces.IWebMessagingDAO;
/**
 * @see com.cws.esolutions.core.dao.interfaces.IWebMessagingDAO
 */
public class WebMessagingDAOImpl implements IWebMessagingDAO
{
    /**
     * @see com.cws.esolutions.core.dao.interfaces.IWebMessagingDAO#insertMessage(List)
     */
    public synchronized boolean insertMessage(final List<Object> messageList) throws SQLException
    {
        final String methodName = IWebMessagingDAO.CNAME + "#insertMessage(final List<Object> messageList) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("messageList: {}", messageList);
        }

        Connection sqlConn = null;
        PreparedStatement stmt = null;
        boolean isComplete = false;

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
            stmt = sqlConn.prepareStatement("{ CALL submitSvcMessage(?, ?, ?, ?, ?, ?, ?, ?) }");
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
        catch (final SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

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
     * @see com.cws.esolutions.core.dao.interfaces.IWebMessagingDAO#retrieveMessage(String)
     */
    public synchronized List<Object> retrieveMessage(final String messageId) throws SQLException
    {
        final String methodName = IWebMessagingDAO.CNAME + "#retrieveMessage(final String messageId) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug(messageId);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        PreparedStatement stmt = null;
        List<Object> svcMessage = null;

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
            stmt = sqlConn.prepareStatement("{ CALL retrServiceMessage(? }", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stmt.setString(1, messageId);

            if (DEBUG)
            {
                DEBUGGER.debug("PreparedStatement: {}", stmt);
            }

            if (stmt.execute())
            {
                resultSet = stmt.getResultSet();

                if (resultSet.next())
                {
                    resultSet.first();
                    svcMessage = new ArrayList<Object>();
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
        catch (final SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

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

        return svcMessage;
    }

    /**
     * @see com.cws.esolutions.core.dao.interfaces.IWebMessagingDAO#retrieveMessages()
     */
    public synchronized List<Object[]> retrieveMessages() throws SQLException
    {
        final String methodName = IWebMessagingDAO.CNAME + "#retrieveMessages() throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        PreparedStatement stmt = null;
        List<Object[]> response = null;

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
            stmt = sqlConn.prepareStatement("{ CALL retrServiceMessages()}", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            if (DEBUG)
            {
                DEBUGGER.debug("PreparedStatement: {}", stmt);
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
                    response = new ArrayList<Object[]>();

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
        catch (final SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

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

        return response;
    }

    /**
     * @see com.cws.esolutions.core.dao.interfaces.IWebMessagingDAO#retrieveAlertMessages()
     */
    public synchronized List<Object[]> retrieveAlertMessages() throws SQLException
    {
        final String methodName = IWebMessagingDAO.CNAME + "#retrieveAlertMessages() throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        PreparedStatement stmt = null;
        List<Object[]> response = null;

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
            stmt = sqlConn.prepareStatement("{ CALL retrAlertMessages()}", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            if (DEBUG)
            {
                DEBUGGER.debug("PreparedStatement: {}", stmt);
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
                    response = new ArrayList<Object[]>();

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
        catch (final SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

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

        return response;
    }

    /**
     * @see com.cws.esolutions.core.dao.interfaces.IWebMessagingDAO#updateMessage(String, List)
     */
    public synchronized boolean updateMessage(final String messageId, final List<Object> messageList) throws SQLException
    {
        final String methodName = IWebMessagingDAO.CNAME + "#updateMessage(final String messageId, final List<Object> messageList) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("messageId: {}", messageId);
            DEBUGGER.debug("messageList: {}", messageList);
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
            stmt = sqlConn.prepareStatement("{ CALL updateServiceMessage(?, ?, ?, ?, ?, ?, ?, ?) }");
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
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

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
     * @see com.cws.esolutions.core.dao.interfaces.IWebMessagingDAO#deleteMessage(String)
     */
    public synchronized boolean deleteMessage(final String messageId) throws SQLException
    {
        final String methodName = IWebMessagingDAO.CNAME + "#deleteMessage(final String messageId) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("messageId: {}", messageId);
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
            stmt = sqlConn.prepareStatement("{ CALL removeSvcMessage(? }");
            stmt.setString(1, messageId);

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
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

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
     * @see com.cws.esolutions.core.dao.interfaces.IWebMessagingDAO#getMessagesByAttribute(String)
     */
    public synchronized List<Object[]> getMessagesByAttribute(final String value) throws SQLException
    {
        final String methodName = IWebMessagingDAO.CNAME + "#getMessagesByAttribute(final String value) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
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

            stmt = sqlConn.prepareStatement("{ CALL getMessagesByAttribute(? }", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stmt.setString(1, sBuilder.toString().trim());

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
        catch (final SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

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
