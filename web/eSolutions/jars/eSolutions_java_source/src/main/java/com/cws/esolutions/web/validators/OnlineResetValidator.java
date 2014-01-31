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
 * File: OnlineResetValidator.java
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
import com.cws.esolutions.web.dto.UserChangeRequest;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
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

    @Override
    public final boolean supports(final Class<?> value)
    {
        final String methodName = OnlineResetValidator.CNAME + "#supports(final Class<?> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Class: {}", value);
        }

        final boolean isSupported = UserChangeRequest.class.isAssignableFrom(value);

        if (DEBUG)
        {
            DEBUGGER.debug("isSupported: {}", isSupported);
        }

        return isSupported;
    }

    @Override
    public final void validate(final Object target, final Errors errors)
    {
        final String methodName = OnlineResetValidator.CNAME + "#validate(final Object target, final Errors errors)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("errors: {}", errors);
        }

        UserChangeRequest request = (UserChangeRequest) target;

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
