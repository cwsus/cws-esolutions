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
package com.cws.esolutions.security;

import org.slf4j.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Modifier;

import com.cws.esolutions.core.controllers.ResourceControllerBean;
import com.cws.esolutions.security.config.SecurityServiceConfiguration;
/*
 * SecurityServiceInitBean
 * SecurityServiceInitializerServlet for application. Currently loads logging
 *
 * History
 *
 * Author               Date                           Comments
 * ----------------------------------------------------------------------------
 * Kevin Huntly         11/23/2008 22:39:20            Created.
 */
public class SecurityServiceBean implements Serializable
{
    private ResourceControllerBean resourceBean = null;
    private SecurityServiceConfiguration configData = null;

    private static SecurityServiceBean instance = null;

    private static final long serialVersionUID = -2801382473833987974L;
    private static final String CNAME = SecurityServiceBean.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityConstants.ERROR_LOGGER);

    /**
     * Returns a static instance of this bean
     *
     * @return SecurityServiceInitBean
     */
    public static final SecurityServiceBean getInstance()
    {
        final String method = CNAME + "#getInstance()";

        if (DEBUG)
        {
            DEBUGGER.debug(method);
            DEBUGGER.debug("instance: {}", SecurityServiceBean.instance);
        }

        if (SecurityServiceBean.instance == null)
        {
            SecurityServiceBean.instance = new SecurityServiceBean();
        }

        return SecurityServiceBean.instance;
    }

    public final void setConfigData(final SecurityServiceConfiguration value)
    {
        final String methodName = SecurityServiceBean.CNAME + "#setConfigData(final SecurityServiceConfiguration value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.configData = value;
    }

    public final void setResourceBean(final ResourceControllerBean value)
    {
        final String methodName = SecurityServiceBean.CNAME + "#setResourceBean(final ResourceControllerBean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.resourceBean = value;
    }

    public final SecurityServiceConfiguration getConfigData()
    {
        final String methodName = SecurityServiceBean.CNAME + "#getConfigData()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.configData);
        }

        return this.configData;
    }

    public final ResourceControllerBean getResourceBean()
    {
        final String methodName = SecurityServiceBean.CNAME + "#getResourceBean()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.resourceBean);
        }

        return this.resourceBean;
    }

    public final String toString()
    {
        final String methodName = SecurityServiceBean.CNAME + "#toString()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        StringBuilder sBuilder = new StringBuilder()
            .append("[" + this.getClass().getName() + "]" + SecurityConstants.LINE_BREAK + "{" + SecurityConstants.LINE_BREAK);

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
                        sBuilder.append("\t" + field.getName() + " --> " + field.get(this) + SecurityConstants.LINE_BREAK);
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
