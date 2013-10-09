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
package com.cws.us.pws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Field;
import org.springframework.beans.factory.annotation.Autowired;

import com.cws.esolutions.core.Constants;
/**
 * CWSPWS_java_source
 * com.cws.us.pws
 * ApplicationConfig.java
 *
 * TODO: Add class description
 *
 * $Id: cws-codetemplates.xml 2286 2013-01-03 20:50:12Z kmhuntly@gmail.com $
 * $Author: kmhuntly@gmail.com $
 * $Date: 2013-01-03 15:50:12 -0500 (Thu, 03 Jan 2013) $
 * $Revision: 2286 $
 * @author khuntly
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * khuntly @ Apr 18, 2013 7:42:45 PM
 *     Created.
 */
public class ApplicationConfig
{
    private String methodName = null;
    @Autowired private String serviceEmail = null;
    @Autowired private String applicationName = null;

    private static final String CNAME = ApplicationConfig.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setApplicationName(final String value)
    {
        this.methodName = ApplicationConfig.CNAME + "#setApplicationName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(this.methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.applicationName = value;
    }

    public final void setServiceEmail(final String value)
    {
        this.methodName = ApplicationConfig.CNAME + "#setServiceEmail(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(this.methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serviceEmail = value;
    }

    public final String getApplicationName()
    {
        this.methodName = ApplicationConfig.CNAME + "#getApplicationName()";

        if (DEBUG)
        {
            DEBUGGER.debug(this.methodName);
            DEBUGGER.debug("Value: {}", this.applicationName);
        }

        return this.applicationName;
    }

    public final String getServiceEmail()
    {
        this.methodName = ApplicationConfig.CNAME + "#getServiceEmail()";

        if (DEBUG)
        {
            DEBUGGER.debug(this.methodName);
            DEBUGGER.debug("Value: {}", this.serviceEmail);
        }

        return this.serviceEmail;
    }

    public final String toString()
    {
        this.methodName = ApplicationConfig.CNAME + "#toString()";

        if (DEBUG)
        {
            DEBUGGER.debug(this.methodName);
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
