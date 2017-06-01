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
/*
 * Project: eSolutions_java_source
 * Package: com.cws.esolutions.web.validators
 * File: TelephoneValidator.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.List;
import org.slf4j.Logger;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.regex.Pattern;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.ValidationUtils;

import com.cws.esolutions.security.processors.dto.AccountChangeData;
import com.cws.esolutions.web.Constants;
/**
 * @author khuntly
 * @version 1.0
 * @see org.springframework.validation.Validator
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

    public final boolean supports(final Class<?> value)
    {
        final String methodName = TelephoneValidator.CNAME + "#supports(final Class<?> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        final boolean isSupported = AccountChangeData.class.isAssignableFrom(value);

        if (DEBUG)
        {
            DEBUGGER.debug("isSupported: {}", isSupported);
        }

        return isSupported;
    }

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

        AccountChangeData request = (AccountChangeData) target;

        if (DEBUG)
        {
            DEBUGGER.debug("UserChangeRequest: {}", request);
        }

        boolean patternMatch = false;
        final List<Pattern> patternList = new ArrayList<Pattern>(
                Arrays.asList(
                        Pattern.compile("\\d{10}"),
                        Pattern.compile("\\d{3}-\\d{3}-\\d{4}"),
                        Pattern.compile("\\d{3}.\\d{3}.\\d{4}"),
                        Pattern.compile("\\d{3}/\\d{3}.\\d{4}"),
                        Pattern.compile("\\d{3}/\\d{3}-\\d{4}")));

        if (DEBUG)
        {
            DEBUGGER.debug("List<Pattern>: {}", patternList);
        }

        for (Pattern pattern : patternList)
        {
            if (DEBUG)
            {
                DEBUGGER.debug("Pattern: {}", pattern);
            }

            if (pattern.matcher(request.getTelNumber()).matches())
            {
                patternMatch = true;

                break;
            }
        }

        for (Pattern pattern : patternList)
        {
            if (DEBUG)
            {
                DEBUGGER.debug("Pattern: {}", pattern);
            }

            if (pattern.matcher(request.getPagerNumber()).matches())
            {
                patternMatch = true;

                break;
            }
        }

        if (!(patternMatch))
        {
            errors.reject("telNumber", this.messageNumberInvalid);
            errors.reject("pagerNumber", this.messageNumberInvalid);
        }
    }
}
