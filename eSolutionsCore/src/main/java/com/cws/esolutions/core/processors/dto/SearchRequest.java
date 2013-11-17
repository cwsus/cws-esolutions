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
import org.slf4j.LoggerFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.cws.esolutions.core.Constants;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.processors.dto
 * UserSearchRequest.java
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
 * kh05451 @ Oct 30, 2012 2:45:21 PM
 *     Created.
 */
public class SearchRequest implements Serializable
{
    private int startPage = 0;
    private int searchLimit = 0;
    private String searchTerms = null;
    private UserAccount userAccount = null;
    private RequestHostInfo requestInfo = null;

    private static final long serialVersionUID = 3827869327665501686L;
    private static final String CNAME = SearchRequest.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER);

    public final void setUserAccount(final UserAccount value)
    {
        final String methodName = SearchRequest.CNAME + "#setUserAccount(final UserAccount value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.userAccount = value;
    }

    public final void setRequestInfo(final RequestHostInfo value)
    {
        final String methodName = SearchRequest.CNAME + "#setRequestInfo(final RequestHostInfo value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.requestInfo = value;
    }

    public final void setStartPage(final int value)
    {
        final String methodName = SearchRequest.CNAME + "#setStartPage(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.startPage = value;
    }

    public final void setSearchTerms(final String value)
    {
        final String methodName = SearchRequest.CNAME + "#setSearchTerms(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug(value);
        }

        this.searchTerms = value;
    }

    public final void setSearchLimit(final int value)
    {
        final String methodName = SearchRequest.CNAME + "#setSearchLimit(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.searchLimit = value;
    }

    public final UserAccount getUserAccount()
    {
        final String methodName = SearchRequest.CNAME + "#getUserAccount()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.userAccount);
        }

        return this.userAccount;
    }

    public final RequestHostInfo getRequestInfo()
    {
        final String methodName = SearchRequest.CNAME + "#getRequestInfo()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.requestInfo);
        }

        return this.requestInfo;
    }

    public final int getStartPage()
    {
        final String methodName = SearchRequest.CNAME + "#getStartPage()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.startPage);
        }

        return this.startPage;
    }

    public final String getSearchTerms()
    {
        final String methodName = SearchRequest.CNAME + "#getSearchTerms()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug(this.searchTerms);
        }

        return this.searchTerms;
    }

    public final int getSearchLimit()
    {
        final String methodName = SearchRequest.CNAME + "#getSearchLimit()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.searchLimit);
        }

        return this.searchLimit;
    }

    @Override
    public final String toString()
    {
        final String methodName = SearchRequest.CNAME + "#toString()";

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
