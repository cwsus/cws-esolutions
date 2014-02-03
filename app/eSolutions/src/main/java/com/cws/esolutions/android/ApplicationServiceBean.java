/*
 * Copyright (c) 2009 - 2014 CaspersBox Web Services
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
package com.cws.esolutions.android;
/*
 * Project: eSolutions
 * Package: com.cws.esolutions.android
 * File: ApplicationServiceBean.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.Map;
import org.slf4j.Logger;
import java.util.Properties;
import javax.sql.DataSource;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.security.processors.dto.RequestHostInfo;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 */
public class ApplicationServiceBean
{
    private RequestHostInfo reqInfo = null;
    private Properties secProperties = null;
    private Properties authProperties = null;
    private Properties coreProperties = null;
    private Map<String, DataSource> dataSources = null;

    private static ApplicationServiceBean instance = null;

    private static final String CNAME = ApplicationServiceBean.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + ApplicationServiceBean.class.getSimpleName());

    /**
     * Returns a static instance of this bean
     *
     * @return ApplicationServiceBean
     */
    public static final ApplicationServiceBean getInstance()
    {
        final String method = CNAME + "#ApplicationServiceBean()";

        if (DEBUG)
        {
            DEBUGGER.debug(method);
            DEBUGGER.debug("instance: {}", ApplicationServiceBean.instance);
        }

        if (ApplicationServiceBean.instance == null)
        {
            ApplicationServiceBean.instance = new ApplicationServiceBean();
        }

        return ApplicationServiceBean.instance;
    }

    public final void setDataSources(final Map<String, DataSource> value)
    {
        final String methodName = ApplicationServiceBean.CNAME + "#setDataSources(final Map<String, DataSource> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.dataSources = value;
    }

    /**
     * Sets a static copy of the Application configuration as defined in the
     * configuration xml files.
     *
     * @param value - The system-provided hostname that the service is running
     * on
     */
    public final void setReqInfo(final RequestHostInfo value)
    {
        final String methodName = ApplicationServiceBean.CNAME + "#setReqInfo(final RequestHostInfo value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.reqInfo = value;
    }

    public final void setSecProperties(final Properties value)
    {
        final String methodName = ApplicationServiceBean.CNAME + "#setSecProperties(final Properties value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.secProperties = value;
    }

    public final void setAuthProperties(final Properties value)
    {
        final String methodName = ApplicationServiceBean.CNAME + "#setAuthProperties(final Properties value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.authProperties = value;
    }

    public final void setCoreProperties(final Properties value)
    {
        final String methodName = ApplicationServiceBean.CNAME + "#setCoreProperties(final Properties value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.coreProperties = value;
    }

    public final Map<String, DataSource> getDataSources()
    {
        final String methodName = ApplicationServiceBean.CNAME + "#getDataSources()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.dataSources);
        }

        return this.dataSources;
    }

    /**
     * Returns the string representation of the system-provided hostname.
     *
     * @return String - The system-provided hostname
     */
    public final RequestHostInfo getReqInfo()
    {
        final String methodName = ApplicationServiceBean.CNAME + "#getReqInfo()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.reqInfo);
        }

        return this.reqInfo;
    }

    public final Properties getSecProperties()
    {
        final String methodName = ApplicationServiceBean.CNAME + "#getSecProperties()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.secProperties);
        }

        return this.secProperties;
    }

    public final Properties getAuthProperties()
    {
        final String methodName = ApplicationServiceBean.CNAME + "#getAuthProperties()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.authProperties);
        }

        return this.authProperties;
    }

    public final Properties getCoreProperties()
    {
        final String methodName = ApplicationServiceBean.CNAME + "#getCoreProperties()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.coreProperties);
        }

        return this.coreProperties;
    }

    @Override
    public final String toString()
    {
        final String methodName = ApplicationServiceBean.CNAME + "#toString()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        final StringBuilder sBuilder = new StringBuilder()
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
                (!(field.getName().equals("instance"))) &&
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

