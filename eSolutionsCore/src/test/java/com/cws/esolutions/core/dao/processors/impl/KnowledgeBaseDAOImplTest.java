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
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.dao.processors.impl
 * File: KnowledgeBaseDAOImplTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.List;
import org.junit.Test;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Before;
import java.util.ArrayList;
import java.sql.SQLException;
import org.apache.commons.lang.RandomStringUtils;

import com.cws.esolutions.core.processors.enums.ArticleStatus;
import com.cws.esolutions.core.listeners.CoreServiceInitializer;
import com.cws.esolutions.core.dao.processors.interfaces.IKnowledgeBaseDAO;

public class KnowledgeBaseDAOImplTest
{
    private static final IKnowledgeBaseDAO dao = new KnowledgeBaseDAOImpl();

    @Before
    public void setUp()
    {
        try
        {
            CoreServiceInitializer.initializeService("eSolutionsCore/config/ServiceConfig.xml", "logging/logging.xml");
        }
        catch (Exception ex)
        {
            Assert.fail(ex.getMessage());

            System.exit(-1);
        }
    }

    @Test
    public void testDoCreateArticle()
    {
        List<String> list = new ArrayList<>(
                Arrays.asList(
                        "KB" + RandomStringUtils.randomNumeric(8),
                        "khuntly",
                        "kmhuntly@gmail.com",
                        "test testy testerson",
                        "Test Article",
                        "Test symptoms",
                        "Test cause",
                        "This is a test article. This is a test resolution"));

        try
        {
            Assert.assertTrue(KnowledgeBaseDAOImplTest.dao.doCreateArticle(list));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public void testUpdateArticleStatusAsRejected()
    {
        try
        {
            Assert.assertTrue(KnowledgeBaseDAOImplTest.dao.updateArticleStatus("KB22208793", "khuntly", ArticleStatus.REJECTED.name()));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public void testUpdateArticleStatusAsDeleted()
    {
        try
        {
            Assert.assertTrue(KnowledgeBaseDAOImplTest.dao.updateArticleStatus("KB22208793", "khuntly", ArticleStatus.DELETED.name()));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public void testUpdateArticleStatusAsApproved()
    {
        try
        {
            Assert.assertTrue(KnowledgeBaseDAOImplTest.dao.updateArticleStatus("KB22208793", "khuntly", ArticleStatus.APPROVED.name()));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public void testDoUpdateArticle()
    {
        List<String> list = new ArrayList<>(
                Arrays.asList(
                        "KB22208793",
                        "test testy testerson updated",
                        "Update Test Article",
                        "Update Test symptoms",
                        "Test cause",
                        "This is a test article. This is a test resolution",
                        "khuntly"));

        try
        {
            Assert.assertTrue(KnowledgeBaseDAOImplTest.dao.doUpdateArticle(list));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public void testRetrieveArticleAsNoPending()
    {
        try
        {
            Assert.assertNotNull(KnowledgeBaseDAOImplTest.dao.retrieveArticle("KB22208793", false));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public void testRetrieveArticleAsPending()
    {
        try
        {
            Assert.assertNotNull(KnowledgeBaseDAOImplTest.dao.retrieveArticle("KB22208793", true));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public void testSearchPendingArticles()
    {
        try
        {
            Assert.assertNotNull(KnowledgeBaseDAOImplTest.dao.searchPendingArticles("khuntly", 0));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }

    @Test
    public void testGetArticlesByAttribute()
    {
        try
        {
            Assert.assertNotNull(KnowledgeBaseDAOImplTest.dao.getArticlesByAttribute("test", 0));
        }
        catch (SQLException sqx)
        {
            Assert.fail(sqx.getMessage());
        }
    }
}
