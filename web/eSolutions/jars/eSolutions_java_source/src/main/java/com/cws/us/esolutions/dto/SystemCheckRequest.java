/**
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
package com.cws.us.esolutions.dto;

import org.slf4j.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;

import com.cws.us.esolutions.Constants;
/**
 * eSolutions_java_source
 * com.cws.us.esolutions.dto
 * SystemCheckRequest.java
 *
 * $Id: $
 * $Author: $
 * $Date: $
 * $Revision: $
 * @author 35033355
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * 35033355 @ May 16, 2013 11:07:14 AM
 *     Created.
 */
public class SystemCheckRequest implements Serializable
{
    private int targetPort = 0;
    private String sourceServer = null;
    private String targetServer = null;

    private static final long serialVersionUID = -46841443676631031L;
    private static final String CNAME = SystemCheckRequest.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setTargetPort(final int value)
    {
        final String methodName = SystemCheckRequest.CNAME + "#setTargetPort(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.targetPort = value;
    }

    public final void setSourceServer(final String value)
    {
        final String methodName = SystemCheckRequest.CNAME + "#setSourceServer(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.sourceServer = value;
    }

    public final void setTargetServer(final String value)
    {
        final String methodName = SystemCheckRequest.CNAME + "#setTargetServer(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.targetServer = value;
    }

    public final int getTargetPort()
    {
        final String methodName = SystemCheckRequest.CNAME + "#getTargetPort()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.targetPort);
        }

        return this.targetPort;
    }

    public final String getSourceServer()
    {
        final String methodName = SystemCheckRequest.CNAME + "#getSourceServer()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.sourceServer);
        }

        return this.sourceServer;
    }

    public final String getTargetServer()
    {
        final String methodName = SystemCheckRequest.CNAME + "#getTargetServer()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.targetServer);
        }

        return this.targetServer;
    }

    @Override
    public final String toString()
    {
        final String methodName = SystemCheckRequest.CNAME + "#toString()";

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
