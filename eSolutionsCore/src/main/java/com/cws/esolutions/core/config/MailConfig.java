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
package com.cws.esolutions.core.config;

import org.slf4j.Logger;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Properties;
import org.slf4j.LoggerFactory;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.cws.esolutions.core.Constants;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.config
 * ApplicationConfig.java
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
@XmlType(name = "mail-config")
@XmlAccessorType(XmlAccessType.NONE)
public final class MailConfig implements Serializable
{
    private String mailFrom = null;
    private String propertyFile = null;
    private Properties mailProps = null;
    private String dataSourceName = null;

    private static final String CNAME = MailConfig.class.getName();
    private static final long serialVersionUID = -3187516318848375651L;
    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setMailFrom(final String value)
    {
        final String methodName = MailConfig.CNAME + "#setMailFrom(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.mailFrom = value;
    }

    public final void setPropertyFile(final String value)
    {
        final String methodName = MailConfig.CNAME + "#setPropertyFile(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.propertyFile = value;
    }

    public final void setDataSourceName(final String value)
    {
        final String methodName = MailConfig.CNAME + "#setDataSourceName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.dataSourceName = value;
    }

    @XmlElement(name = "mailFrom")
    public final String getMailFrom()
    {
        final String methodName = MailConfig.CNAME + "#getMailFrom()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.mailFrom);
        }

        return this.mailFrom;
    }

    @XmlElement(name = "propertyFile")
    public final Properties getMailProps()
    {
        final String methodName = MailConfig.CNAME + "#getMailProps()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.propertyFile);
        }

        try
        {
            mailProps = new Properties();
            mailProps.load(MailConfig.class.getClassLoader().getResourceAsStream(this.propertyFile));
        }
        catch (IOException iox)
        {
            // do nothing
        }

        return this.mailProps;
    }

    @XmlElement(name = "dataSourceName")
    public final String getDataSourceName()
    {
        final String methodName = MailConfig.CNAME + "#getDataSourceName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.dataSourceName);
        }

        return this.dataSourceName;
    }

    public final String toString()
    {
        final String methodName = MailConfig.CNAME + "#toString()";

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
