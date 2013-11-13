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
import java.util.regex.Pattern;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.ValidationUtils;

import com.cws.us.esolutions.Constants;
import com.cws.us.esolutions.dto.UserChangeRequest;
/**
 * eSolutions_java_source
 * com.cws.us.esolutions.validators
 * ApplicationValidator.java
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
public class TelephoneValidator implements Validator
{
    private String messageNumberInvalid = null;
    private String messagePasswordRequired = null;

    private static final String CNAME = TelephoneValidator.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setMessageNumberInvalid(final String value)
    {
        final String methodName = TelephoneValidator.CNAME + "#setMessageNumberInvalid(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageNumberInvalid = value;
    }

    public final void setMessagePasswordRequired(final String value)
    {
        final String methodName = TelephoneValidator.CNAME + "#setMessagePasswordRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messagePasswordRequired = value;
    }

    @Override
    public final boolean supports(final Class<?> target)
    {
        final String methodName = TelephoneValidator.CNAME + "#supports(final Class<?> target)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Class: ", target);
        }

        final boolean isSupported = UserChangeRequest.class.isAssignableFrom(target);

        if (DEBUG)
        {
            DEBUGGER.debug("isSupported: {}", isSupported);
        }

        return isSupported;
    }

    @Override
    public final void validate(final Object target, final Errors errors)
    {
        final String methodName = TelephoneValidator.CNAME + "#validate(final Object target, final Errors errors)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Object: {}", target);
            DEBUGGER.debug("Errors: {}", errors);
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "telNumber", this.messageNumberInvalid);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "pagerNumber", this.messageNumberInvalid);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "currentPassword", this.messagePasswordRequired);

        UserChangeRequest request = (UserChangeRequest) target;

        if (DEBUG)
        {
            DEBUGGER.debug("UserChangeRequest: {}", request);
        }

        final Pattern pattern = Pattern.compile("\\d{3}-\\d{7}");

        if (DEBUG)
        {
            DEBUGGER.debug("Pattern: {}", pattern);
        }

        if (!(pattern.matcher(request.getTelNumber()).matches()))
        {
            errors.reject("telNumber", this.messageNumberInvalid);
        }
        else if (!(pattern.matcher(request.getPagerNumber()).matches()))
        {
            errors.reject("pagerNumber", this.messageNumberInvalid);
        }
    }
}
