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

import java.util.List;
import org.slf4j.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.core.Constants;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.core.processors.enums.ServiceStatus;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.processors.dto
 * Package.java
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
 * kh05451 @ Jan 2, 2013 9:32:14 AM
 *     Created.
 */
public class Project implements Serializable
{
    private String devEmail = null;
    private String prodEmail = null;
    private String projectGuid = null;
    private String projectName = null;
    private String changeQueue = null;
    private String incidentQueue = null;
    private UserAccount primaryContact = null;
    private ServiceStatus projectStatus = null;
    private UserAccount secondaryContact = null;
    private List<Application> applicationList = null;

    private static final String CNAME = Project.class.getName();
    private static final long serialVersionUID = -4095595278052824141L;

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER);

    public final void setProjectGuid(final String value)
    {
        final String methodName = Project.CNAME + "#setProjectGuid(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.projectGuid = value;
    }

    public final void setProjectCode(final String value)
    {
        final String methodName = Project.CNAME + "#setProjectCode(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.projectCode = value;
    }

    public final void setChangeQueue(final String value)
    {
        final String methodName = Project.CNAME + "#setChangeQueue(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.changeQueue = value;
    }

    public final void setDevEmail(final String value)
    {
        final String methodName = Project.CNAME + "#setDevEmail(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.devEmail = value;
    }

    public final void setProdEmail(final String value)
    {
        final String methodName = Project.CNAME + "#setProdEmail(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.prodEmail = value;
    }

    public final void setIncidentQueue(final String value)
    {
        final String methodName = Project.CNAME + "#setIncidentQueue(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.incidentQueue = value;
    }

    public final void setPrimaryContact(final UserAccount value)
    {
        final String methodName = Project.CNAME + "#setPrimaryContact(final UserAccount value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.primaryContact = value;
    }

    public final void setSecondaryContact(final UserAccount value)
    {
        final String methodName = Project.CNAME + "#setSecondaryContact(final UserAccount value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.secondaryContact = value;
    }

    public final void setApplicationList(final List<Application> value)
    {
        final String methodName = Project.CNAME + "#setApplicationList(final List<Application> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.applicationList = value;
    }

    public final void setProjectStatus(final ServiceStatus value)
    {
        final String methodName = Project.CNAME + "#setProjectStatus(final ServiceStatus value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.projectStatus = value;
    }

    public final String getProjectGuid()
    {
        final String methodName = Project.CNAME + "#getProjectGuid()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.projectGuid);
        }

        return this.projectGuid;
    }

    public final String getProjectCode()
    {
        final String methodName = Project.CNAME + "#getProjectCode()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.projectCode);
        }

        return this.projectCode;
    }

    public final String getChangeQueue()
    {
        final String methodName = Project.CNAME + "#getChangeQueue()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.changeQueue);
        }

        return this.changeQueue;
    }

    public final String getDevEmail()
    {
        final String methodName = Project.CNAME + "#getDevEmail()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.devEmail);
        }

        return this.devEmail;
    }

    public final String getProdEmail()
    {
        final String methodName = Project.CNAME + "#getProdEmail()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.prodEmail);
        }

        return this.prodEmail;
    }

    public final String getIncidentQueue()
    {
        final String methodName = Project.CNAME + "#getIncidentQueue()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.incidentQueue);
        }

        return this.incidentQueue;
    }

    public final UserAccount getPrimaryContact()
    {
        final String methodName = Project.CNAME + "#getPrimaryContact()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.primaryContact);
        }

        return this.primaryContact;
    }

    public final UserAccount getSecondaryContact()
    {
        final String methodName = Project.CNAME + "#getSecondaryContact()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.secondaryContact);
        }

        return this.secondaryContact;
    }

    public final List<Application> getApplicationList()
    {
        final String methodName = Project.CNAME + "#getApplicationList()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.applicationList);
        }

        return this.applicationList;
    }

    public final ServiceStatus getProjectStatus()
    {
        final String methodName = Project.CNAME + "#getProjectStatus()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.projectStatus);
        }

        return this.projectStatus;
    }

    @Override
    public final String toString()
    {
        final String methodName = Project.CNAME + "#toString()";

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
