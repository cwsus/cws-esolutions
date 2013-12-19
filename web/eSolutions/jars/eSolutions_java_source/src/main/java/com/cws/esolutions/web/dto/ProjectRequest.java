/*
 * Copyright (c) 2009 - 2013 By: CWS, Inc.
 * 
 * All rights reserved. These materials are confidential and
 * proprietary to CaspersBox Web Services N.A and no part of
 * these materials should be reproduced, published in any form
 * by any means, electronic or mechanical, including photocopy
 * or any information storage or retrieval system not should
 * the materials be disclosed to third parties without the
 * express written authorization of CaspersBox Web Services, N.A.
 */
package com.cws.esolutions.web.dto;

import org.slf4j.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.core.Constants;
import com.cws.esolutions.core.processors.enums.ServiceStatus;
/*
 * Project: eSolutions_java_source
 * Package: com.cws.esolutions.web.dto
 * File: ProjectRequest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
public class ProjectRequest implements Serializable
{
    private String devEmail = null;
    private String prodEmail = null;
    private String projectName = null;
    private String changeQueue = null;
    private String incidentQueue = null;
    private String primaryContact = null;
    private String secondaryContact = null;
    private ServiceStatus projectStatus = null;

    private static final long serialVersionUID = -7694489390149040635L;
    private static final String CNAME = ProjectRequest.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER);

    public final void setDevEmail(final String value)
    {
        final String methodName = ProjectRequest.CNAME + "#setDevEmail(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.devEmail = value;
    }

    public final void setProdEmail(final String value)
    {
        final String methodName = ProjectRequest.CNAME + "#setProdEmail(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.prodEmail = value;
    }

    public final void setProjectName(final String value)
    {
        final String methodName = ProjectRequest.CNAME + "#setProjectName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.projectName = value;
    }

    public final void setChangeQueue(final String value)
    {
        final String methodName = ProjectRequest.CNAME + "#setChangeQueue(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.changeQueue = value;
    }

    public final void setIncidentQueue(final String value)
    {
        final String methodName = ProjectRequest.CNAME + "#setIncidentQueue(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.incidentQueue = value;
    }

    public final void setPrimaryContact(final String value)
    {
        final String methodName = ProjectRequest.CNAME + "#setPrimaryContact(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.primaryContact = value;
    }

    public final void setSecondaryContact(final String value)
    {
        final String methodName = ProjectRequest.CNAME + "#setSecondaryContact(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.secondaryContact = value;
    }

    public final void setProjectStatus(final ServiceStatus value)
    {
        final String methodName = ProjectRequest.CNAME + "#setProjectStatus(final ServiceStatus value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.projectStatus = value;
    }

    public final String getDevEmail()
    {
        final String methodName = ProjectRequest.CNAME + "#getDevEmail()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.devEmail);
        }

        return this.devEmail;
    }

    public final String getProdEmail()
    {
        final String methodName = ProjectRequest.CNAME + "#getProdEmail()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.prodEmail);
        }

        return this.prodEmail;
    }

    public final String getProjectName()
    {
        final String methodName = ProjectRequest.CNAME + "#getProjectName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.projectName);
        }

        return this.projectName;
    }

    public final String getChangeQueue()
    {
        final String methodName = ProjectRequest.CNAME + "#getChangeQueue()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.changeQueue);
        }

        return this.changeQueue;
    }

    public final String getIncidentQueue()
    {
        final String methodName = ProjectRequest.CNAME + "#getIncidentQueue()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.incidentQueue);
        }

        return this.incidentQueue;
    }

    public final String getPrimaryContact()
    {
        final String methodName = ProjectRequest.CNAME + "#getPrimaryContact()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.primaryContact);
        }

        return this.primaryContact;
    }

    public final String getSecondaryContact()
    {
        final String methodName = ProjectRequest.CNAME + "#getSecondaryContact()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.secondaryContact);
        }

        return this.secondaryContact;
    }

    public final ServiceStatus getProjectStatus()
    {
        final String methodName = ProjectRequest.CNAME + "#getProjectStatus()";

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
        final String methodName = ProjectRequest.CNAME + "#toString()";

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
