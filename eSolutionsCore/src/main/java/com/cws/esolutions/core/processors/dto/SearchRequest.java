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

import com.cws.esolutions.core.Constants;
import com.cws.esolutions.core.processors.enums.SearchRequestType;
/**
 * SearchRequest object to perform various types of searches.
 *
 * @author kmhuntly@gmail.com
 * @version 1.0
 */
/*
 * eSolutionsCore
 * com.cws.esolutions.core.processors.dto
 * SearchRequest.java
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
    private int startRow = 0;
    private int searchLimit = 0;
    private String searchTerms = null;
    private SearchRequestType searchType = null;

    private static final long serialVersionUID = 3827869327665501686L;
    private static final String CNAME = SearchRequest.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER);

    public final void setStartRow(final int value)
    {
        final String methodName = SearchRequest.CNAME + "#setStartRow(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.startRow = value;
    }

    public final void setSearchTerms(final String value)
    {
        final String methodName = SearchRequest.CNAME + "#setSearchTerms(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.searchTerms = value;
    }

    public final void setSearchType(final SearchRequestType value)
    {
        final String methodName = SearchRequest.CNAME + "#setSearchTerms(final SearchRequestType value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.searchType = value;
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

    public final int getStartRow()
    {
        final String methodName = SearchRequest.CNAME + "#getStartRow()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.startRow);
        }

        return this.startRow;
    }

    public final String getSearchTerms()
    {
        final String methodName = SearchRequest.CNAME + "#getSearchTerms()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.searchTerms);
        }

        return this.searchTerms;
    }

    public final SearchRequestType getSearchType()
    {
        final String methodName = SearchRequest.CNAME + "#getSearchType()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.searchType);
        }

        return this.searchType;
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

            if (!(field.getName().equals("methodName")) &&
                    (!(field.getName().equals("CNAME"))) &&
                    (!(field.getName().equals("DEBUGGER"))) &&
                    (!(field.getName().equals("DEBUG"))) &&
                    (!(field.getName().equals("ERROR_RECORDER"))) &&
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
