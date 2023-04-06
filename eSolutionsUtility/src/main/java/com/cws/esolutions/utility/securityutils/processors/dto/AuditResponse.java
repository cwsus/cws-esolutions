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
package com.cws.esolutions.utility.securityutils.processors.dto;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.processors.dto
 * File: AuditResponse.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.util.List;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.cws.esolutions.utility.UtilityConstants;
import com.cws.esolutions.utility.securityutils.processors.enums.AuditRequestStatus;
/**
 * @author cws-khuntly
 * @version 1.0
 * @see java.io.Serializable
 */
public class AuditResponse implements Serializable
{
	private int entryCount = 0;
    private List<AuditEntry> auditList = null;
    private AuditRequestStatus requestStatus = null;

    private static final String CNAME = AuditResponse.class.getName();
    private static final long serialVersionUID = -6944324117201453121L;

    private static final Logger DEBUGGER = LogManager.getLogger(UtilityConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setRequestStatus(final AuditRequestStatus value)
    {
        final String methodName = AuditResponse.CNAME + "#setRequestStatus(final v value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.requestStatus = value;
    }

    public final void setEntryCount(final int value)
    {
        final String methodName = AuditResponse.CNAME + "#setEntryCount(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.entryCount = value;
    }

    public final void setAuditList(final List<AuditEntry> value)
    {
        final String methodName = AuditResponse.CNAME + "#setAuditList(final List<AuditEntry value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.auditList = value;
    }

    public final AuditRequestStatus getRequestStatus()
    {
        final String methodName = AuditResponse.CNAME + "#getRequestStatus()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.requestStatus);
        }

        return this.requestStatus;
    }

    public final int getEntryCount()
    {
        final String methodName = AuditResponse.CNAME + "#getEntryCount()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.entryCount);
        }

        return this.entryCount;
    }

    public final List<AuditEntry> getAuditList()
    {
        final String methodName = AuditResponse.CNAME + "#getAuditList()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.auditList);
        }

        return this.auditList;
    }

    @Override
    public final String toString()
    {
        StringBuilder sBuilder = new StringBuilder()
            .append("[" + this.getClass().getName() + "]" + UtilityConstants.LINE_BREAK + "{" + UtilityConstants.LINE_BREAK);

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
                        sBuilder.append("\t" + field.getName() + " --> " + field.get(this) + UtilityConstants.LINE_BREAK);
                    }
                }
                catch (final IllegalAccessException iax) {}
            }
        }

        sBuilder.append('}');

        return sBuilder.toString();
    }
}
