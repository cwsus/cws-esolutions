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
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import com.cws.esolutions.core.dao.processors.interfaces.IKnowledgeBaseDAO;
import com.cws.esolutions.core.dao.processors.interfaces.IServerDataDAO;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.dao.impl
 * KnowledgeBaseDAOImpl.java
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
 * kh05451 @ Oct 29, 2012 9:44:46 AM
 *     Created.
 */
public class KnowledgeBaseDAOImpl implements IKnowledgeBaseDAO
{
    @Override
    public synchronized boolean updateArticleStatus(final String articleId, final String modifiedBy, final String status) throws SQLException
    {
        final String methodName = IKnowledgeBaseDAO.CNAME + "#updateArticleStatus(final String articleId, final String modifiedBy, final String status) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Article: {}", articleId);
            DEBUGGER.debug("modifiedBy: {}", modifiedBy);
            DEBUGGER.debug("status: {}", status);
        }

        Connection sqlConn = null;
        boolean isComplete = false;
        PreparedStatement stmt = null;

        try
        {
            sqlConn = dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);
            stmt = sqlConn.prepareCall("{CALL updateArticleStatus(?, ?, ?)}");
            stmt.setString(1, articleId);
            stmt.setString(2, modifiedBy);
            stmt.setString(3, status);

            if (DEBUG)
            {
                DEBUGGER.debug("stmt: {}", stmt);
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
    public synchronized boolean doCreateArticle(final List<String> articleDetail) throws SQLException
    {
        final String methodName = IKnowledgeBaseDAO.CNAME + "#doCreateArticle(final List<String> articleDetail) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Article detail: {}", articleDetail);
            DEBUGGER.debug("Article size: {}", articleDetail.size());
        }

        Connection sqlConn = null;
        boolean isComplete = false;
        PreparedStatement stmt = null;

        try
        {
            sqlConn = dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);
            stmt = sqlConn.prepareCall("{CALL addNewArticle(?, ?, ?, ?, ?, ?, ?)}");
            stmt.setString(1, articleDetail.get(0)); // article id
            stmt.setString(2, articleDetail.get(1)); // author
            stmt.setString(3, articleDetail.get(2)); // keywords
            stmt.setString(4, articleDetail.get(3)); // title
            stmt.setString(5, articleDetail.get(4)); // symptoms
            stmt.setString(6, articleDetail.get(5)); // cause
            stmt.setString(7, articleDetail.get(6)); // resolutions

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

    @Override
    public synchronized boolean doUpdateArticle(final List<String> articleDetail) throws SQLException
    {
        final String methodName = IKnowledgeBaseDAO.CNAME + "#doUpdateArticle(final List<Object> articleDetail) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Article detail: {}", articleDetail);
            DEBUGGER.debug("Article size: {}", articleDetail.size());
        }

        Connection sqlConn = null;
        boolean isComplete = false;
        PreparedStatement stmt = null;

        try
        {
            sqlConn = dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);
 
            stmt = sqlConn.prepareCall("{CALL updateArticle(?, ?, ?, ?, ?, ?, ?)}");
            stmt.setString(1, articleDetail.get(0)); // article id
            stmt.setString(2, articleDetail.get(1)); // keywords
            stmt.setString(3, articleDetail.get(2)); // title
            stmt.setString(4, articleDetail.get(3)); // symptoms
            stmt.setString(5, articleDetail.get(4)); // cause
            stmt.setString(6, articleDetail.get(5)); // resolution
            stmt.setString(7, articleDetail.get(6)); // modified by

            if (DEBUG)
            {
                DEBUGGER.debug("Statement: {}", stmt);
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
    public synchronized int getArticleCount(final String type) throws SQLException
    {
        final String methodName = IServerDataDAO.CNAME + "#getArticleCount(final String type) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", type);
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
            stmt = sqlConn.prepareCall("{ CALL getArticleCount(?) }");
            stmt.setString(1, type);

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

    @Override
    public synchronized List<Object> retrieveArticle(final String articleId, final boolean isApproval) throws SQLException
    {
        final String methodName = IKnowledgeBaseDAO.CNAME + "#retrieveArticle(final String articleId, final boolean isApproval) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Article ID: {}", articleId);
            DEBUGGER.debug("isApproval: {}", isApproval);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        PreparedStatement stmt = null;
        List<Object> articleData = null;

        try
        {
            sqlConn = dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);

            stmt = sqlConn.prepareCall("{CALL retrArticle(?, ?)}");
            stmt.setString(1, articleId);
            stmt.setBoolean(2, isApproval);

            if (DEBUG)
            {
                DEBUGGER.debug(stmt.toString());
            }

            if (stmt.execute())
            {
                resultSet = stmt.getResultSet();

                if (resultSet.next())
                {
                    resultSet.first();

                    articleData = new ArrayList<Object>(
                            Arrays.asList(
                                    resultSet.getInt(1), // kbase_page_hits (TINYINT)
                                    resultSet.getString(2), // kbase_article_id (VARCHAR)
                                    resultSet.getLong(3), // kbase_article_createdate (BIGINT)
                                    resultSet.getString(4), // kbase_article_author (VARCHAR)
                                    resultSet.getString(5), // kbase_article_keywords (VARCHAR)
                                    resultSet.getString(6), // kbase_article_title (VARCHAR)
                                    resultSet.getString(7), // kbase_article_symptoms (VARCHAR)
                                    resultSet.getString(8), // kbase_article_cause (VARCHAR)
                                    resultSet.getString(9), // kbase_article_resolution (TEXT)
                                    resultSet.getString(10), // kbase_article_status (VARCHAR)
                                    resultSet.getString(11), // kbase_article_reviewedby (VARCHAR)
                                    resultSet.getLong(12), // kbase_article_revieweddate (BIGINT)
                                    resultSet.getLong(13), // kbase_article_modifieddate (BIGINT)
                                    resultSet.getString(14))); // kbase_article_modifiedby (VARCHAR)

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Article data: {}", articleData);
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

        return articleData;
    }

    @Override
    public synchronized List<Object[]> searchPendingArticles(final String author, final int startRow) throws SQLException
    {
        final String methodName = IKnowledgeBaseDAO.CNAME + "#searchPendingArticles(final String author, final int startRow) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Author: {}", author);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        PreparedStatement stmt = null;
        List<Object[]> articleData = null;

        try
        {
            sqlConn = dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);

            stmt = sqlConn.prepareCall("{CALL retrPendingArticles(?, ?)}");
            stmt.setString(1, author);
            stmt.setInt(2, startRow);

            if (stmt.execute())
            {
                resultSet = stmt.getResultSet();

                if (resultSet.next())
                {
                    resultSet.beforeFirst();

                    articleData = new ArrayList<>();

                    while (resultSet.next())
                    {
                        Object[] data = new Object[]
                        {
                                resultSet.getInt(1), // kbase_page_hits (TINYINT)
                                resultSet.getString(2), // kbase_article_id (VARCHAR)
                                resultSet.getLong(3), // kbase_article_createdate (BIGINT)
                                resultSet.getString(4), // kbase_article_author (VARCHAR)
                                resultSet.getString(5), // kbase_article_keywords (VARCHAR)
                                resultSet.getString(6), // kbase_article_title (VARCHAR)
                                resultSet.getString(7), // kbase_article_symptoms (VARCHAR)
                                resultSet.getString(8), // kbase_article_cause (VARCHAR)
                                resultSet.getString(9), // kbase_article_resolution (TEXT)
                                resultSet.getString(10), // kbase_article_status (VARCHAR)
                                resultSet.getString(11), // kbase_article_reviewedby (VARCHAR)
                                resultSet.getLong(12), // kbase_article_revieweddate (BIGINT)
                                resultSet.getLong(13), // kbase_article_modifieddate (BIGINT)
                                resultSet.getString(14) // kbase_article_modifiedby (VARCHAR)
                        };

                        if (DEBUG)
                        {
                            for (Object obj : data)
                            {
                                DEBUGGER.debug("Value: {}", obj);
                            }
                        }

                        articleData.add(data);
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("articleData: {}", articleData);
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

        return articleData;
    }

    @Override
    public synchronized List<Object[]> listTopArticles() throws SQLException
    {
        final String methodName = IKnowledgeBaseDAO.CNAME + "#listTopArticles() throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        PreparedStatement stmt = null;
        List<Object[]> responseList = null;

        try
        {
            sqlConn = dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);

            stmt = sqlConn.prepareCall("{CALL retrTopArticles()}");

            if (DEBUG)
            {
                DEBUGGER.debug(stmt.toString());
            }

            if (stmt.execute())
            {
                resultSet = stmt.getResultSet();

                if (resultSet.next())
                {
                    resultSet.beforeFirst();

                    responseList = new ArrayList<>();

                    while (resultSet.next())
                    {
                        Object[] articleData = new Object[]
                        {
                                resultSet.getInt(1), // kbase_page_hits (TINYINT)
                                resultSet.getString(2), // kbase_article_id (VARCHAR)
                                resultSet.getLong(3), // kbase_article_createdate (BIGINT)
                                resultSet.getString(4), // kbase_article_author (VARCHAR)
                                resultSet.getString(5), // kbase_article_keywords (VARCHAR)
                                resultSet.getString(6), // kbase_article_title (VARCHAR)
                                resultSet.getString(7), // kbase_article_symptoms (VARCHAR)
                                resultSet.getString(8), // kbase_article_cause (VARCHAR)
                                resultSet.getString(9), // kbase_article_resolution (TEXT)
                                resultSet.getString(10), // kbase_article_status (VARCHAR)
                                resultSet.getString(11), // kbase_article_reviewedby (VARCHAR)
                                resultSet.getLong(12), // kbase_article_revieweddate (BIGINT)
                                resultSet.getLong(13), // kbase_article_modifieddate (BIGINT)
                                resultSet.getString(14) // kbase_article_modifiedby (VARCHAR)
                        };

                        if (DEBUG)
                        {
                            for (Object obj : articleData)
                            {
                                DEBUGGER.debug("Value: {}", obj);
                            }
                        }

                        responseList.add(articleData);
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Response: {}", responseList);
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

        return responseList;
    }

    @Override
    public synchronized List<Object[]> getArticlesByAttribute(final String attribute, final int startRow) throws SQLException
    {
        final String methodName = IKnowledgeBaseDAO.CNAME + "#getArticlesByAttribute(final String attribute, final int startRow) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", attribute);
            DEBUGGER.debug("Value: {}", startRow);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        PreparedStatement stmt = null;
        List<Object[]> responseList = null;

        try
        {
            sqlConn = dataSource.getConnection();

            if (sqlConn.isClosed())
            {
                throw new SQLException("Unable to obtain application datasource connection");
            }

            sqlConn.setAutoCommit(true);

            stmt = sqlConn.prepareCall("{CALL getArticleByAttribute(?, ?)}");
            stmt.setString(1, attribute);
            stmt.setInt(2, startRow);

            if (DEBUG)
            {
                DEBUGGER.debug(stmt.toString());
            }

            if (stmt.execute())
            {
                resultSet = stmt.getResultSet();

                if (resultSet.next())
                {
                    resultSet.beforeFirst();

                    responseList = new ArrayList<>();

                    while (resultSet.next())
                    {
                        Object[] articleData = new Object[]
                        {
                                resultSet.getInt(1), // kbase_page_hits (TINYINT)
                                resultSet.getString(2), // kbase_article_id (VARCHAR)
                                resultSet.getLong(3), // kbase_article_createdate (BIGINT)
                                resultSet.getString(4), // kbase_article_author (VARCHAR)
                                resultSet.getString(5), // kbase_article_keywords (VARCHAR)
                                resultSet.getString(6), // kbase_article_title (VARCHAR)
                                resultSet.getString(7), // kbase_article_symptoms (VARCHAR)
                                resultSet.getString(8), // kbase_article_cause (VARCHAR)
                                resultSet.getString(9), // kbase_article_resolution (TEXT)
                                resultSet.getString(10), // kbase_article_status (VARCHAR)
                                resultSet.getString(11), // kbase_article_reviewedby (VARCHAR)
                                resultSet.getLong(12), // kbase_article_revieweddate (BIGINT)
                                resultSet.getLong(13), // kbase_article_modifieddate (BIGINT)
                                resultSet.getString(14) // kbase_article_modifiedby (VARCHAR)
                        };

                        if (DEBUG)
                        {
                            for (Object obj : articleData)
                            {
                                DEBUGGER.debug("Value: {}", obj);
                            }
                        }

                        responseList.add(articleData);
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Response: {}", responseList);
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

        return responseList;
    }
}
