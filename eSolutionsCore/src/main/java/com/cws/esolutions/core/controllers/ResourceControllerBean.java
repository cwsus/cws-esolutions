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
package com.cws.esolutions.core.controllers;

import java.util.Map;
import org.slf4j.Logger;
import javax.sql.DataSource;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Modifier;

import com.cws.esolutions.core.Constants;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.controllers
 * ResourceControllerBean.java
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
 * kh05451 @ Nov 23, 2012 8:21:09 AM
 *     Created.
 */
public class ResourceControllerBean implements Serializable
{
    private Object authDataSource = null;
    private Map<String, DataSource> dataSource = null;

    private static final long serialVersionUID = -3232269466364712542L;
    private static final String CNAME = ResourceControllerBean.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER);

    /**
     * Returns a static instance of this bean
     * 
     * @return SecurityServiceInitBean
     */
    public static final ResourceControllerBean getInstance()
    {
        final String method = ResourceControllerBean.CNAME + "#getInstance()";

        if (DEBUG)
        {
            DEBUGGER.debug(method);
        }

        return new ResourceControllerBean();
    }

    public final void setAuthDataSource(final Object value)
    {
        final String methodName = ResourceControllerBean.CNAME + "#setAuthDataSource(final Object value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.authDataSource = value;
    }

    public final void setDataSource(final Map<String, DataSource> value)
    {
        final String methodName = ResourceControllerBean.CNAME + "#setDataSource(final Map<String, DataSource> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.dataSource = value;
    }

    public final Object getAuthDataSource()
    {
        final String methodName = ResourceControllerBean.CNAME + "#getAuthDataSource()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.authDataSource);
        }

        return this.authDataSource;
    }

    public final Map<String, DataSource> getDataSource()
    {
        final String methodName = ResourceControllerBean.CNAME + "#getDataSource()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("this.dataSource: {}", this.dataSource);
        }

        return this.dataSource;
    }

    public final String toString()
    {
        final String methodName = ResourceControllerBean.CNAME + "#toString()";

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
