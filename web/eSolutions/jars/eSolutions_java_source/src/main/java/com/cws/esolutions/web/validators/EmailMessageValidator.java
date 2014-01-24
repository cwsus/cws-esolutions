/*
 * Copyright (c) 2009 - 2014 CaspersBox Web Services
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
import java.util.regex.Pattern;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.ValidationUtils;

import com.cws.esolutions.web.Constants;
import com.cws.esolutions.core.utils.dto.EmailMessage;
/*
 * Project: eSolutions_java_source
 * Package: com.cws.esolutions.web.validators
 * File: EmailMessageValidator.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
public class EmailMessageValidator implements Validator
{
    private String messageBodyRequired = null;
    private String messageFromRequired = null;
    private String messageSubjectRequired = null;

    private static final String CNAME = EmailMessageValidator.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setMessageBodyRequired(final String value)
    {
        final String methodName = EmailMessageValidator.CNAME + "#setMessageBodyRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageBodyRequired = value;
    }

    public final void setMessageFromRequired(final String value)
    {
        final String methodName = EmailMessageValidator.CNAME + "#setMessageFromRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageFromRequired = value;
    }

    public final void setMessageSubjectRequired(final String value)
    {
        final String methodName = EmailMessageValidator.CNAME + "#setMessageSubjectRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageSubjectRequired = value;
    }

    @Override
    public final boolean supports(final Class<?> value)
    {
        final String methodName = EmailMessageValidator.CNAME + "#supports(final Class<?> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        final boolean isSupported = EmailMessage.class.isAssignableFrom(value);

        if (DEBUG)
        {
            DEBUGGER.debug("isSupported: {}", isSupported);
        }

        return isSupported;
    }

    @Override
    public final void validate(final Object target, final Errors errors)
    {
        final String methodName = EmailMessageValidator.CNAME + "#validate(final Object target, final Errors errors)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Object: {}", target);
            DEBUGGER.debug("Errors: {}", errors);
        }

        final EmailMessage message = (EmailMessage) target;
		final Pattern pattern = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");

        if (DEBUG)
        {
            DEBUGGER.debug("EmailMessage: {}", message);
            DEBUGGER.debug("Pattern: {}", pattern);
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "messageBody", this.messageBodyRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "emailAddr", this.messageFromRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "messageSubject", this.messageSubjectRequired);

        if (!(pattern.matcher(message.getEmailAddr().get(0)).matches()))
        {
            errors.reject("emailAddr", this.messageFromRequired);
        }
    }
}
