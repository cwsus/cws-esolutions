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

import java.util.List;
import org.slf4j.Logger;
import java.io.Serializable;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.cws.esolutions.core.Constants;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.processors.dto
 * KnowledgeBaseResponse.java
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
 * kh05451 @ Oct 30, 2012 12:34:49 PM
 *     Created.
 */
public class KnowledgeBaseResponse implements Serializable
{
    private Article article = null;
    private String response = null;
    private List<Article> articleList = null;
    private CoreServicesStatus requestStatus = null;

    private static final long serialVersionUID = -4112583385542471257L;
    private static final String CNAME = KnowledgeBaseResponse.class.getName();
    
    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER);

    public final void setRequestStatus(final CoreServicesStatus value)
    {
        final String methodName = KnowledgeBaseResponse.CNAME + "#setRequestStatus(final CoreServicesStatus value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.requestStatus = value;
    }

    public final void setResponse(final String value)
    {
        final String methodName = KnowledgeBaseResponse.CNAME + "#setResponse(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.response = value;
    }

    public final void setArticle(final Article value)
    {
        final String methodName = KnowledgeBaseResponse.CNAME + "#setArticle(final Article value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.article = value;
    }

    public final void setArticleList(final List<Article> value)
    {
        final String methodName = KnowledgeBaseResponse.CNAME + "#setArticle(final List<Article> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.articleList = value;
    }

    public final CoreServicesStatus getRequestStatus()
    {
        final String methodName = KnowledgeBaseResponse.CNAME + "#getRequestStatus()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.requestStatus);
        }

        return this.requestStatus;
    }

    public final String getResponse()
    {
        final String methodName = KnowledgeBaseResponse.CNAME + "#getResponse()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.response);
        }

        return this.response;
    }

    public final Article getArticle()
    {
        final String methodName = KnowledgeBaseResponse.CNAME + "#getArticle()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.article);
        }

        return this.article;
    }

    public final List<Article> getArticleList()
    {
        final String methodName = KnowledgeBaseResponse.CNAME + "#getArticleList()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.articleList);
        }

        return this.articleList;
    }

    @Override
    public final String toString()
    {
        final String methodName = KnowledgeBaseResponse.CNAME + "#toString()";

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

            if (field.getModifiers() != Modifier.STATIC)
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
                    ERROR_RECORDER.error(iax.getMessage(), iax);
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
