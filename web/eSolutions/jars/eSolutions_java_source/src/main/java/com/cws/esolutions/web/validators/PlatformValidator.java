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
package com.cws.esolutions.web.validators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.ValidationUtils;

import com.cws.esolutions.web.Constants;
import com.cws.esolutions.web.dto.PlatformRequest;
/*
 * Project: eSolutions_java_source
 * Package: com.cws.esolutions.web.validators
 * File: PlatformValidator.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
public class PlatformValidator implements Validator
{
    private String messagePlatformNameRequired = null;
    private String messagePlatformStatusRequired = null;
    private String messagePlatformServersRequired = null;

    private static final String CNAME = PlatformValidator.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setMessagePlatformNameRequired(final String value)
    {
        final String methodName = PlatformValidator.CNAME + "#setMessagePlatformNameRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messagePlatformNameRequired = value;
    }

    public final void setMessagePlatformStatusRequired(final String value)
    {
        final String methodName = PlatformValidator.CNAME + "#setMessagePlatformStatusRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messagePlatformStatusRequired = value;
    }

    public final void setMessagePlatformServersRequired(final String value)
    {
        final String methodName = PlatformValidator.CNAME + "#setMessagePlatformServersRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messagePlatformServersRequired = value;
    }

    @Override
    public final boolean supports(final Class<?> value)
    {
        final String methodName = PlatformValidator.CNAME + "#supports(final Class<?> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: ", value);
        }

        final boolean isSupported = PlatformRequest.class.isAssignableFrom(value);

        if (DEBUG)
        {
            DEBUGGER.debug("isSupported: {}", isSupported);
        }

        return isSupported;
    }

    @Override
    public final void validate(final Object target, final Errors errors)
    {
        final String methodName = PlatformValidator.CNAME + "#validate(final Object target, final Errors errors)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Object: {}", target);
            DEBUGGER.debug("Errors: {}", errors);
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "platformName", this.messagePlatformNameRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "status", this.messagePlatformStatusRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "platformServers", this.messagePlatformServersRequired);
    }
}