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
package com.cws.esolutions.core.processors.dto;

import org.slf4j.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.core.Constants;
import com.cws.esolutions.core.processors.enums.ArticleStatus;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.processors.dto
 * Article.java
 *
 * $Id: $
 * $Author: $
 * $String: $
 * $Revision: $
 * @author kmhuntly@gmail.com
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Oct 30, 2012 2:51:43 PM
 *     Created.
 */
public class Article implements Serializable
{
    private int pageHits = 0;
    private String title = null;
    private String cause = null;
    private String author = null;
    private String keywords = null;
    private String symptoms = null;
    private String articleId = null;
    private String createDate = null;
    private String reviewedOn = null;
    private String modifiedOn = null;
    private String modifiedBy = null;
    private String reviewedBy = null;
    private String resolution = null;
    private String authorEmail = null;
    private ArticleStatus articleStatus = null;

    private static final long serialVersionUID = 8298281387755676485L;
    private static final String CNAME = KnowledgeBaseResponse.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setPageHits(final int value)
    {
        final String methodName = Article.CNAME + "#setPageHits(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.pageHits = value;
    }

    public final void setArticleId(final String value)
    {
        final String methodName = Article.CNAME + "#setArticleId(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.articleId = value;
    }

    public final void setCreateDate(final String value)
    {
        final String methodName = Article.CNAME + "#setCreateDate(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.createDate = value;
    }

    public final void setAuthor(final String value)
    {
        final String methodName = Article.CNAME + "#setAuthor(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.author = value;
    }

    public final void setKeywords(final String value)
    {
        final String methodName = Article.CNAME + "#setKeywords(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.keywords = value;
    }

    public final void setTitle(final String value)
    {
        final String methodName = Article.CNAME + "#setTitle(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.title = value;
    }

    public final void setSymptoms(final String value)
    {
        final String methodName = Article.CNAME + "#setSymptoms(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.symptoms = value;
    }

    public final void setCause(final String value)
    {
        final String methodName = Article.CNAME + "#setCause(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.cause = value;
    }

    public final void setAuthorEmail(final String value)
    {
        final String methodName = Article.CNAME + "#setAuthorEmail(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.authorEmail = value;
    }

    public final void setResolution(final String value)
    {
        final String methodName = Article.CNAME + "#setResolution(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.resolution = value;
    }

    public final void setArticleStatus(final ArticleStatus value)
    {
        final String methodName = Article.CNAME + "#setArticleStatus(final ArticleStatus value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ArticleStatus: {}", value);
        }

        this.articleStatus = value;
    }

    public final void setReviewedBy(final String value)
    {
        final String methodName = Article.CNAME + "#setReviewedBy(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.reviewedBy = value;
    }

    public final void setReviewedOn(final String value)
    {
        final String methodName = Article.CNAME + "#setReviewedOn(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.reviewedOn = value;
    }

    public final void setModifiedBy(final String value)
    {
        final String methodName = Article.CNAME + "#setModifiedBy(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.modifiedBy = value;
    }

    public final void setModifiedOn(final String value)
    {
        final String methodName = Article.CNAME + "#setModifiedOn(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.modifiedOn = value;
    }

    public final int getPageHits()
    {
        final String methodName = Article.CNAME + "#getPageHits()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.pageHits);
        }

        return this.pageHits;
    }

    public final String getArticleId()
    {
        final String methodName = Article.CNAME + "#getArticleId()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.articleId);
        }

        return this.articleId;
    }

    public final String getCreateDate()
    {
        final String methodName = Article.CNAME + "#getCreateDate()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.createDate);
        }

        return this.createDate;
    }

    public final String getAuthor()
    {
        final String methodName = Article.CNAME + "#getAuthor()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.author);
        }

        return this.author;
    }

    public final String getKeywords()
    {
        final String methodName = Article.CNAME + "#getKeywords()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.keywords);
        }

        return this.keywords;
    }

    public final String getTitle()
    {
        final String methodName = Article.CNAME + "#getTitle()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.title);
        }

        return this.title;
    }

    public final String getSymptoms()
    {
        final String methodName = Article.CNAME + "#getSymptoms()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.symptoms);
        }

        return this.symptoms;
    }

    public final String getCause()
    {
        final String methodName = Article.CNAME + "#getCause()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.cause);
        }

        return this.cause;
    }

    public final String getAuthorEmail()
    {
        final String methodName = Article.CNAME + "#getAuthorEmail()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.authorEmail);
        }

        return this.authorEmail;
    }

    public final String getResolution()
    {
        final String methodName = Article.CNAME + "#getResolution()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.resolution);
        }

        return this.resolution;
    }

    public final ArticleStatus getArticleStatus()
    {
        final String methodName = Article.CNAME + "#getArticleStatus()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.articleStatus);
        }

        return this.articleStatus;
    }

    public final String getReviewedBy()
    {
        final String methodName = Article.CNAME + "#getReviewedBy()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.reviewedBy);
        }

        return this.reviewedBy;
    }

    public final String getReviewedOn()
    {
        final String methodName = Article.CNAME + "#getReviewedOn()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.reviewedOn);
        }

        return this.reviewedOn;
    }

    public final String getModifiedBy()
    {
        final String methodName = Article.CNAME + "#getModifiedBy()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.modifiedBy);
        }

        return this.modifiedBy;
    }

    public final String getModifiedOn()
    {
        final String methodName = Article.CNAME + "#getModifiedOn()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.modifiedOn);
        }

        return this.modifiedOn;
    }

    @Override
    public final String toString()
    {
        final String methodName = Article.CNAME + "#toString()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        StringBuilder sBuilder = new StringBuilder()
            .append("[" + this.getClass().getName() + "]" + Constants.LINE_BREAK + "{" + Constants.LINE_BREAK);

        for (Field field : this.getClass().getDeclaredFields())
        {
            if (DEBUG)
            {
                DEBUGGER.debug("field: {}", field);
            }

            if (!(field.getName().equals("methodName")) &&
                    (!(field.getName().equals("CNAME"))) &&
                    (!(field.getName().equals("DEBUGGER"))) &&
                    (!(field.getName().equals("DEBUG"))) &&
                    (!(field.getName().equals("serialVersionUID"))))
            {
                try
                {
                    if (field.get(this) != null)
                    {
                        sBuilder.append("\t" + field.getName() + " --> " + field.get(this) + Constants.LINE_BREAK);
                    }
                }
                catch (IllegalAccessException iax)
                {
                    // don't do anything with it
                }
            }
        }

        sBuilder.append('}');

        if (DEBUG)
        {
            DEBUGGER.debug("sBuilder: {}", sBuilder);
        }

        return sBuilder.toString();
    }
}
