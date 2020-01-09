/*
 * Copyright (c) 2009 - 2020 CaspersBox Web Services
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
 *
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
 * cws-khuntly   11/23/2008 22:39:20             Created.
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
 * @author cws-khuntly
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
