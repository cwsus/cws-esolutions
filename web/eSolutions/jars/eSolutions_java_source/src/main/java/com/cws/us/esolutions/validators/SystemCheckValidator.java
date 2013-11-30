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
package com.cws.us.esolutions.validators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.ValidationUtils;

import com.cws.us.esolutions.Constants;
import com.cws.esolutions.core.processors.dto.SystemCheckRequest;
/**
 * eSolutions_java_source
 * com.cws.us.esolutions.validators
 * ServerValidator.java
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
 * 35033355 @ May 16, 2013 8:40:08 AM
 *     Created.
 */
public class SystemCheckValidator implements Validator
{
    private String messageTargetNameRequired = null;
    private String messageTargetPortRequired = null;

    private static final String CNAME = SystemCheckValidator.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setMessageTargetNameRequired(final String value)
    {
        final String methodName = SystemCheckValidator.CNAME + "#setMessageTargetNameRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageTargetNameRequired = value;
    }

    public final void setMessageTargetPortRequired(final String value)
    {
        final String methodName = SystemCheckValidator.CNAME + "#setMessageTargetPortRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageTargetPortRequired = value;
    }

    @Override
    public final boolean supports(final Class<?> value)
    {
        final String methodName = SystemCheckValidator.CNAME + "#supports(final Class<?> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: ", value);
        }

        final boolean isSupported = SystemCheckRequest.class.isAssignableFrom(value);

        if (DEBUG)
        {
            DEBUGGER.debug("isSupported: {}", isSupported);
        }

        return isSupported;
    }

    @Override
    public final void validate(final Object target, final Errors errors)
    {
        final String methodName = SystemCheckValidator.CNAME + "#validate(final Object target, final Errors errors)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Object: {}", target);
            DEBUGGER.debug("Errors: {}", errors);
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "targetServer", this.messageTargetNameRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "targetPort", this.messageTargetPortRequired);
    }
}
