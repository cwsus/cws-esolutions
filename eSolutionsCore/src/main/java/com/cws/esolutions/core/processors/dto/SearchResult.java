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
import java.lang.reflect.Modifier;

import com.cws.esolutions.core.Constants;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.processors.dto
 * SearchResult.java
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
 * kh05451 @ Oct 31, 2012 2:50:28 PM
 *     Created.
 */
public class SearchResult implements Serializable
{
    private int entryCount = 0;
    private String path = null;
    private String title = null;

    private static final String CNAME = SearchResult.class.getName();
    private static final long serialVersionUID = 3347199205587437064L;

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER);

    public final void setEntryCount(final int value)
    {
        final String methodName = SearchResult.CNAME + "#setEntryCount(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.entryCount = value;
    }

    public final void setPath(final String value)
    {
        final String methodName = SearchResult.CNAME + "#setPath(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.path = value;
    }

    public final void setTitle(final String value)
    {
        final String methodName = SearchResult.CNAME + "#setTitle(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.title = value;
    }

    public final int getEntryCount()
    {
        final String methodName = SearchResult.CNAME + "#getEntryCount()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.entryCount);
        }

        return this.entryCount;
    }

    public final String getPath()
    {
        final String methodName = SearchResult.CNAME + "#getPath()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.path);
        }

        return this.path;
    }

    public final String getTitle()
    {
        final String methodName = SearchResult.CNAME + "#getTitle()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.title);
        }

        return this.title;
    }

    @Override
    public final String toString()
    {
        final String methodName = SearchResult.CNAME + "#toString()";

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
