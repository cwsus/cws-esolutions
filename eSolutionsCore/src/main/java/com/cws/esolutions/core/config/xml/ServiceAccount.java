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
package com.cws.esolutions.core.config.xml;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.config.xml
 * File: DataSourceManager.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.lang.reflect.Field;
import org.apache.logging.log4j.Logger;
import jakarta.xml.bind.annotation.XmlType;
import org.apache.logging.log4j.LogManager;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

import com.cws.esolutions.core.CoreServicesConstants;
/**
 * @author cws-khuntly
 * @version 1.0
 * @see java.io.Serializable
 */
@XmlType(name = "serviceAccount")
@XmlAccessorType(XmlAccessType.NONE)
public final class ServiceAccount
{
    private String accountName = null;
    private String accountGuid = null;
    private String accountRole = null;

    private static final String CNAME = ServiceAccount.class.getName();

    private static final Logger DEBUGGER = LogManager.getLogger(CoreServicesConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setAccountName(final String value)
    {
        final String methodName = ServiceAccount.CNAME + "#setAccountName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.accountName = value;
    }

    public final void setAccountGuid(final String value)
    {
        final String methodName = ServiceAccount.CNAME + "#setAccountGuid(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.accountGuid = value;
    }

    public final void setAccountRole(final String value)
    {
        final String methodName = ServiceAccount.CNAME + "#setAccountRole(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.accountRole = value;
    }

    @XmlElement(name = "accountName")
    public final String getAccountName()
    {
        final String methodName = ServiceAccount.CNAME + "#getAccountName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("accountName: {}", this.accountName);
        }

        return this.accountName;
    }

    @XmlElement(name = "accountGuid")
    public final String getAccountGuid()
    {
        final String methodName = ServiceAccount.CNAME + "#getAccountGuid()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.accountGuid);
        }

        return this.accountGuid;
    }

    @XmlElement(name = "accountRole")
    public final String getAccountRole()
    {
        final String methodName = ServiceAccount.CNAME + "#getAccountRole()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.accountRole);
        }

        return this.accountRole;
    }

    @Override
    public final String toString()
    {
    	StringBuilder sBuilder = new StringBuilder()
            .append("[" + this.getClass().getName() + "]" + CoreServicesConstants.LINE_BREAK + "{" + CoreServicesConstants.LINE_BREAK);

        for (Field field : this.getClass().getDeclaredFields())
        {
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
