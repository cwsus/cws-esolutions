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
 * File: WebMessageValidator.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.util.regex.Pattern;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.validation.ValidationUtils;

import com.cws.esolutions.web.Constants;
/**
 * @author cws-khuntly
 * @version 1.0
 * @see org.springframework.validation.Validator
 */
public class WebMessageValidator implements Validator
{
    private String messageBodyRequired = null;
    private String messageFromRequired = null;
    private String messageSubjectRequired = null;

    private static final String CNAME = WebMessageValidator.class.getName();

    private static final Logger DEBUGGER = LogManager.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setMessageBodyRequired(final String value)
    {
        final String methodName = WebMessageValidator.CNAME + "#setMessageBodyRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageBodyRequired = value;
    }

    public final void setMessageFromRequired(final String value)
    {
        final String methodName = WebMessageValidator.CNAME + "#setMessageFromRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageFromRequired = value;
    }

    public final void setMessageSubjectRequired(final String value)
    {
        final String methodName = WebMessageValidator.CNAME + "#setMessageSubjectRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageSubjectRequired = value;
    }

    public final boolean supports(final Class<?> value)
    {
        final String methodName = WebMessageValidator.CNAME + "#supports(final Class<?> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        final boolean isSupported = SimpleMailMessage.class.isAssignableFrom(value);

        if (DEBUG)
        {
            DEBUGGER.debug("isSupported: {}", isSupported);
        }

        return isSupported;
    }

    public final void validate(final Object target, final Errors errors)
    {
        final String methodName = WebMessageValidator.CNAME + "#validate(final Object target, final Errors errors)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Object: {}", target);
            DEBUGGER.debug("Errors: {}", errors);
        }

        final SimpleMailMessage message = (SimpleMailMessage) target;
        final Pattern pattern = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");

        if (DEBUG)
        {
            DEBUGGER.debug("EmailMessage: {}", message);
            DEBUGGER.debug("Pattern: {}", pattern);
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "messageBody", this.messageBodyRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "emailAddr", this.messageFromRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "messageSubject", this.messageSubjectRequired);

        if (!(pattern.matcher(message.getTo().toString()).matches()))
        {
            errors.reject("emailAddr", this.messageFromRequired);
        }
    }
}
