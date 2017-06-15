/*
 * Copyright (c) 2009 - 2017 CaspersBox Web Services
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
package com.cws.esolutions.agent.config.xml;
/*
 * Project: eSolutionsAgent
 * Package: com.cws.esolutions.agent.config.xml
 * File: ScriptConfig.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly   11/23/2008 22:39:20             Created.
 */
import java.io.File;
import java.util.Map;
import org.slf4j.Logger;
import java.util.HashMap;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.cws.esolutions.agent.AgentConstants;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author cws-khuntly
 * @version 1.0
 */
@XmlType(name = "script-config")
@XmlAccessorType(XmlAccessType.NONE)
public final class ScriptConfig implements Serializable
{
    private int scriptTimeout = 0;
    private String logsDirectory = null;
    private Map<String, File> scripts = null;
    
    private static final String CNAME = ScriptConfig.class.getName();
    private static final long serialVersionUID = -190318672398494206L;

    private static final Logger DEBUGGER = LoggerFactory.getLogger(AgentConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(AgentConstants.ERROR_LOGGER);

    public final void setLogsDirectory(final String value)
    {
        final String methodName = ScriptConfig.CNAME + "#setLogsDirectory(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.logsDirectory = value;
    }

    public final void setScriptTimeout(final int value)
    {
        final String methodName = ScriptConfig.CNAME + "#setScriptTimeout(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.scriptTimeout = value;
    }

    public final void setScripts(final HashMap<String, File> value)
    {
        final String methodName = ScriptConfig.CNAME + "#setScripts(final HashMap<String, File> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.scripts = value;
    }

    @XmlElement(name = "scriptTimeout")
    public final int getScriptTimeout()
    {
        final String methodName = ScriptConfig.CNAME + "#getScriptTimeout()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.scriptTimeout);
        }

        return this.scriptTimeout;
    }

    @XmlElement(name = "scripts")
    public final Map<String, File> getScripts()
    {
        final String methodName = ScriptConfig.CNAME + "#getScripts()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.scripts);
        }

        return this.scripts;
    }

    @XmlElement(name = "logsDirectory")
    public final String getLogsDirectory()
    {
        final String methodName = ScriptConfig.CNAME + "#getLogsDirectory()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.logsDirectory);
        }

        return this.logsDirectory;
    }

    @Override
    public final String toString()
    {
        final String methodName = ScriptConfig.CNAME + "#toString()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        StringBuilder sBuilder = new StringBuilder()
            .append("[" + this.getClass().getName() + "]" + AgentConstants.LINE_BREAK + "{" + AgentConstants.LINE_BREAK);

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
                        sBuilder.append("\t" + field.getName() + " --> " + field.get(this) + AgentConstants.LINE_BREAK);
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
