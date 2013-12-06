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
import com.cws.esolutions.security.SecurityConstants;
/**
 * eSolutions_java_source
 * com.cws.us.pws.processors.dto
 * LoginRequest.java
 *
 * $Id: $
 * $Author: $
 * $Date: $
 * $Revision: $
 * @author khuntly
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * khuntly @ Apr 28, 2013 3:02:35 PM
 *     Created.
 */
public class LoginRequest implements Serializable
{
    private String loginUser = null;
    private String loginPass = null;
    private String otpValue = null;

    private static final String CNAME = LoginRequest.class.getName();
    private static final long serialVersionUID = -7043606830867233708L;

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER);

    public final void setLoginUser(final String value)
    {
        final String methodName = LoginRequest.CNAME + "#setLoginUser(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.loginUser = value;
    }

    public final void setLoginPass(final String value)
    {
        final String methodName = LoginRequest.CNAME + "#setLoginPass(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        this.loginPass = value;
    }

    public final void setOtpValue(final String value)
    {
        final String methodName = LoginRequest.CNAME + "#setOtpValue(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        this.otpValue = value;
    }

    public final String getLoginUser()
    {
        final String methodName = LoginRequest.CNAME + "#getLoginUser";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.loginUser);
        }

        return this.loginUser;
    }

    public final String getLoginPass()
    {
        final String methodName = LoginRequest.CNAME + "#getLoginPass";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        return this.loginPass;
    }

    public final String getOtpValue()
    {
        final String methodName = LoginRequest.CNAME + "#getOtpValue";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        return this.otpValue;
    }

    @Override
    public final String toString()
    {
        final String methodName = LoginRequest.CNAME + "#toString()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        StringBuilder sBuilder = new StringBuilder()
            .append("[" + this.getClass().getName() + "]" + SecurityConstants.LINE_BREAK + "{" + SecurityConstants.LINE_BREAK);

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
                    (!(field.getName().equals("loginPass"))) &&
                    (!(field.getName().equals("otpValue"))) &&
                    (!(field.getName().equals("serialVersionUID"))))
            {
                try
                {
                    if (field.get(this) != null)
                    {
                        sBuilder.append("\t" + field.getName() + " --> " + field.get(this) + SecurityConstants.LINE_BREAK);
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
