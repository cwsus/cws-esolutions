/*
 * Copyright (c) 2009 - 2020 CaspersBox Web Services
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
package com.cws.esolutions.core;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core
 * File: CoreServicesBean.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.util.Map;
import javax.sql.DataSource;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.cws.esolutions.core.config.xml.CoreConfigurationData;
/**
 * @author cws-khuntly
 * @version 1.0
 */
public class CoreServicesBean implements Serializable
{
    private String osType = null;
    private String hostName = null;
    private CoreConfigurationData configData = null;
    private Map<String, DataSource> dataSources = null;

    private static CoreServicesBean instance = null;

    private static final long serialVersionUID = -246438369655482396L;
    private static final String CNAME = CoreServicesBean.class.getName();

    private static final Logger DEBUGGER = LogManager.getLogger(CoreServicesConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    /**
     * Returns a static instance of this bean
     *
     * @return InitializerBean
     */
    public static final CoreServicesBean getInstance()
    {
        final String method = CNAME + "#getInstance()";

        if (DEBUG)
        {
            DEBUGGER.debug(method);
            DEBUGGER.debug("instance: {}", CoreServicesBean.instance);
        }

        if (CoreServicesBean.instance == null)
        {
            CoreServicesBean.instance = new CoreServicesBean();
        }

        return CoreServicesBean.instance;
    }

    /**
     * Sets a static copy of the Application configuration as defined in the
     * configuration xml files.
     *
     * @param value - The complete copy of application configuration information
     */
    public final void setConfigData(final CoreConfigurationData value)
    {
        final String methodName = CoreServicesBean.CNAME + "#setConfigData(final CoreConfigurationData value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.configData = value;
    }

    public final void setDataSources(final Map<String, DataSource> value)
    {
        final String methodName = CoreServicesBean.CNAME + "#setDataSources(final Map<String, DataSource> value)";

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
     * @param value - The system-provided Operating System name that the service
     * is running on
     */
    public final void setOsType(final String value)
    {
        final String methodName = CoreServicesBean.CNAME + "#setOsType(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.osType = value;
    }

    /**
     * Sets a static copy of the Application configuration as defined in the
     * configuration xml files.
     *
     * @param value - The system-provided hostname that the service is running
     * on
     */
    public final void setHostName(final String value)
    {
        final String methodName = CoreServicesBean.CNAME + "#setHostName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.hostName = value;
    }

    /**
     * Returns a static copy of the Application configuration as defined in the
     * configuration xml files.
     *
     * @return CoreConfigurationData - A complete copy of the application
     * configuration data.
     */
    public final CoreConfigurationData getConfigData()
    {
        final String methodName = CoreServicesBean.CNAME + "#getConfigData()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.configData);
        }

        return this.configData;
    }

    public final Map<String, DataSource> getDataSources()
    {
        final String methodName = CoreServicesBean.CNAME + "#getDataSources()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.dataSources);
        }

        return this.dataSources;
    }

    /**
     * Returns the string representation of the system-provided Operating
     * System name.
     *
     * @return String - The string representation of the system-provided
     * Operating System name.
     */
    public final String getOsType()
    {
        final String methodName = CoreServicesBean.CNAME + "#getOsType()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.osType);
        }

        return this.osType;
    }

    /**
     * Returns the string representation of the system-provided hostname.
     *
     * @return String - The system-provided hostname
     */
    public final String getHostName()
    {
        final String methodName = CoreServicesBean.CNAME + "#getHostName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.hostName);
        }

        return this.hostName;
    }

    @Override
    public final String toString()
    {
        final StringBuilder sBuilder = new StringBuilder()
            .append("[" + this.getClass().getName() + "]" + CoreServicesConstants.LINE_BREAK + "{" + CoreServicesConstants.LINE_BREAK);

        for (Field field : this.getClass().getDeclaredFields())
        {
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
                        sBuilder.append("\t" + field.getName() + " --> " + field.get(this) + CoreServicesConstants.LINE_BREAK);
                    }
                }
                catch (final IllegalAccessException iax) {}
            }
        }

        sBuilder.append('}');

        return sBuilder.toString();
    }
}
