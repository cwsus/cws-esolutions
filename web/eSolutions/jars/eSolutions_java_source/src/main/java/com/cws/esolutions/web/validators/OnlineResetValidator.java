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
 * File: OnlineResetValidator.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
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
public class OnlineResetValidator implements Validator
{
    private String messageAnswerRequired = null;
    private String messageUsernameRequired = null;
    private String messageEmailAddressRequired = null;

    private static final String CNAME = OnlineResetValidator.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setMessageAnswerRequired(final String value)
    {
        final String methodName = OnlineResetValidator.CNAME + "#setMessageAnswerRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageAnswerRequired = value;
    }

    public final void setMessageUsernameRequired(final String value)
    {
        final String methodName = OnlineResetValidator.CNAME + "#setMessageUsernameRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageUsernameRequired = value;
    }

    public final void setMessageEmailAddressRequired(final String value)
    {
        final String methodName = OnlineResetValidator.CNAME + "#setMessageEmailAddressRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageEmailAddressRequired = value;
    }

    public final boolean supports(final Class<?> value)
    {
        final String methodName = OnlineResetValidator.CNAME + "#supports(final Class<?> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Class: {}", value);
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
        final String methodName = OnlineResetValidator.CNAME + "#validate(final Object target, final Errors errors)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("errors: {}", errors);
        }

        AccountChangeData request = (AccountChangeData) target;

        if (DEBUG)
        {
            DEBUGGER.debug("UserChangeRequest: {}", request);
        }

        switch (request.getResetType())
        {
            case USERNAME:
                ValidationUtils.rejectIfEmptyOrWhitespace(errors, "emailAddr", this.messageEmailAddressRequired);

                break;
            case PASSWORD:
                ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", this.messageUsernameRequired);

                break;
            case QUESTIONS:
                ValidationUtils.rejectIfEmptyOrWhitespace(errors, "secAnswerOne", this.messageAnswerRequired);
                ValidationUtils.rejectIfEmptyOrWhitespace(errors, "secAnswerTwo", this.messageAnswerRequired);

                break;
            default:
                break;
        }

        if (DEBUG)
        {
            DEBUGGER.debug("Errors: {}", errors);
        }
    }
}
