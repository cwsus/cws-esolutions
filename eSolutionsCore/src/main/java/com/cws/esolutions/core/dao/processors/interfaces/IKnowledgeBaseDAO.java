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
package com.cws.esolutions.core.dao.processors.interfaces;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.dao.processors.interfaces
 * File: IKnowledgeBaseDAO.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.List;
import org.slf4j.Logger;
import javax.sql.DataSource;
import java.sql.SQLException;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.core.Constants;
import com.cws.esolutions.core.CoreServiceBean;
import com.cws.esolutions.core.controllers.ResourceControllerBean;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 */
public interface IKnowledgeBaseDAO
{
    static final CoreServiceBean appBean = CoreServiceBean.getInstance();
    static final ResourceControllerBean resBean = appBean.getResourceBean();
    static final DataSource dataSource = resBean.getDataSource().get("ApplicationDataSource");

    static final String CNAME = IKnowledgeBaseDAO.class.getName();

    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CNAME);
    static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    /**
     *
     * @param articleDetail
     * @return boolean
     * @throws SQLException
     */
    boolean doCreateArticle(final List<String> articleDetail) throws SQLException;

    /**
     * Provides an interface to apply updates to an existing knowledgebase
     * article.
     *
     * @param articleDetail
     * @return boolean
     * @throws SQLException
     */
    boolean doUpdateArticle(final List<String> articleDetail) throws SQLException;

    boolean updateArticleStatus(final String articleId, final String modifiedBy, final String status) throws SQLException;

    int getArticleCount(final String type) throws SQLException;

    List<Object[]> listTopArticles() throws SQLException;

    /**
    *
    * @param articleId
    * @param isApproval
    * @return List<String>
    * @throws SQLException
    */
   List<Object> retrieveArticle(final String articleId, final boolean isApproval) throws SQLException;

    /**
     *
     * @param author
     * @return List<String>
     * @throws SQLException
     */
    List<Object[]> searchPendingArticles(final String author, final int startRow) throws SQLException;

    List<Object[]> getArticlesByAttribute(final String attribute, final int startRow) throws SQLException;
}
