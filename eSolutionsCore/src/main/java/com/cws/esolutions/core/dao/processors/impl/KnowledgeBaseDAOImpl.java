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
import java.sql.PreparedStatement;

import com.cws.esolutions.core.dao.processors.interfaces.IKnowledgeBaseDAO;
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
            else
            {
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
            else
            {
                sqlConn.setAutoCommit(true);
                stmt = sqlConn.prepareCall("{CALL addNewArticle(?, ?, ?, ?, ?, ?, ?, ?)}");
                stmt.setString(1, articleDetail.get(0)); // article id
                stmt.setString(2, articleDetail.get(1)); // author
                stmt.setString(3, articleDetail.get(2)); // author email
                stmt.setString(4, articleDetail.get(3)); // keywords
                stmt.setString(5, articleDetail.get(4)); // title
                stmt.setString(6, articleDetail.get(5)); // symptoms
                stmt.setString(7, articleDetail.get(6)); // cause
                stmt.setString(8, articleDetail.get(7)); // resolutions

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

            throw new SQLException(sqx.getMessage());
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
            else
            {
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
    public synchronized List<String> retrieveArticle(final String articleId, final boolean isApproval) throws SQLException
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
        List<String> articleData = null;

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

                stmt = sqlConn.prepareCall("{CALL retrArticle(?, ?)}");
                stmt.setString(1, articleId);
                stmt.setBoolean(2, isApproval);

                if (DEBUG)
                {
                    DEBUGGER.debug(stmt.toString());
                }

                resultSet = stmt.executeQuery();

                if (resultSet.next())
                {
                    resultSet.first();
                    articleData = new ArrayList<String>(
                            Arrays.asList(
                                    resultSet.getString(1),
                                    resultSet.getString(2),
                                    String.valueOf(resultSet.getString(3)),
                                    resultSet.getString(4),
                                    resultSet.getString(5),
                                    resultSet.getString(6),
                                    resultSet.getString(7),
                                    resultSet.getString(8),
                                    resultSet.getString(9),
                                    resultSet.getString(10),
                                    resultSet.getString(11),
                                    String.valueOf(resultSet.getString(12)),
                                    String.valueOf(resultSet.getString(13)),
                                    resultSet.getString(14),
                                    resultSet.getString(15),
                                    resultSet.getString(15)));
                    
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
    public synchronized List<String[]> searchPendingArticles(final String author) throws SQLException
    {
        final String methodName = IKnowledgeBaseDAO.CNAME + "#searchPendingArticles(final String author) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Author: {}", author);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        PreparedStatement stmt = null;
        List<String[]> articleData = null;

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

                stmt = sqlConn.prepareCall("{CALL retrPendingArticles(?)}");
                stmt.setString(1, author);

                resultSet = stmt.executeQuery();

                if (resultSet.next())
                {
                    resultSet.beforeFirst();
                    articleData = new ArrayList<String[]>();

                    while (resultSet.next())
                    {
                        String[] data = new String[]
                        {
                                resultSet.getString(1),
                                resultSet.getString(2),
                                String.valueOf(resultSet.getString(3)),
                                resultSet.getString(4),
                                resultSet.getString(5),
                                resultSet.getString(6),
                                resultSet.getString(7),
                                resultSet.getString(8),
                                resultSet.getString(9),
                                resultSet.getString(10),
                                resultSet.getString(11),
                                String.valueOf(resultSet.getString(12)),
                                String.valueOf(resultSet.getString(13)),
                                resultSet.getString(14),
                                resultSet.getString(15),
                                resultSet.getString(15)
                        };

                        if (DEBUG)
                        {
                            for (String str : data)
                            {
                                DEBUGGER.debug("data: {}", str);
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
    public synchronized List<String[]> getArticlesByAttribute(final String attribute) throws SQLException
    {
        final String methodName = IKnowledgeBaseDAO.CNAME + "#getArticlesByAttribute(final String attribute) throws SQLException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Attribute: {}", attribute);
        }

        Connection sqlConn = null;
        ResultSet resultSet = null;
        PreparedStatement stmt = null;
        List<String[]> responseList = null;

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

                stmt = sqlConn.prepareCall("{CALL getArticleByAttribute(?)}");
                stmt.setString(1, attribute);

                if (DEBUG)
                {
                    DEBUGGER.debug(stmt.toString());
                }

                resultSet = stmt.executeQuery();

                if (resultSet.next())
                {
                    resultSet.beforeFirst();
                    responseList = new ArrayList<String[]>();

                    while (resultSet.next())
                    {
                        String[] articleData = new String[]
                        {
                            resultSet.getString(1), // kbase_page_hits
                            resultSet.getString(2), // kbase_article_id
                            resultSet.getString(3), // kbase_article_createdate
                            resultSet.getString(4), // kbase_article_author
                            resultSet.getString(5), // kbase_article_keywords
                            resultSet.getString(6), // kbase_article_title
                            resultSet.getString(7), // kbase_article_symptoms
                            resultSet.getString(8), // kbase_article_cause
                            resultSet.getString(9), // kbase_article_resolution
                            resultSet.getString(10), // kbase_article_status
                            resultSet.getString(11), // kbase_article_reviewedby
                            resultSet.getString(12), // kbase_article_revieweddate
                            resultSet.getString(13), // kbase_article_modifieddate
                            resultSet.getString(14), // kbase_article_modifiedby
                            resultSet.getString(15), // kbase_article_author_email
                        };

                        if (DEBUG)
                        {
                            for (String str : articleData)
                            {
                                DEBUGGER.debug(str);
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
