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
import com.cws.esolutions.core.processors.dto.EmailMessage;
/**
 * eSolutions_java_source
 * com.cws.us.esolutions.validators
 * EmailMessageValidator.java
 *
 * TODO: Add class description
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
 * 35033355 @ Nov 18, 2013 2:45:18 PM
 *     Created.
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
    public final boolean supports(final Class<?> target)
    {
        final String methodName = EmailMessageValidator.CNAME + "#supports(final Class<?> target)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Class: ", target);
        }

        final boolean isSupported = EmailMessage.class.isAssignableFrom(target);

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

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "messageBody", this.messageBodyRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "messageFrom", this.messageFromRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "messageSubject", this.messageSubjectRequired);
    }
}