/*
 * Copyright (c) 2009 - 2017 CaspersBox Web Services
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
/*
 * Project: eSolutions_java_source
 * Package: com.cws.esolutions.web.validators
 * File: SystemCheckValidator.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.ValidationUtils;

import com.cws.esolutions.web.Constants;
import com.cws.esolutions.core.processors.dto.SystemCheckRequest;
/**
 * @author khuntly
 * @version 1.0
 * @see org.springframework.validation.Validator
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

    public final boolean supports(final Class<?> value)
    {
        final String methodName = SystemCheckValidator.CNAME + "#supports(final Class<?> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        final boolean isSupported = SystemCheckRequest.class.isAssignableFrom(value);

        if (DEBUG)
        {
            DEBUGGER.debug("isSupported: {}", isSupported);
        }

        return isSupported;
    }

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
