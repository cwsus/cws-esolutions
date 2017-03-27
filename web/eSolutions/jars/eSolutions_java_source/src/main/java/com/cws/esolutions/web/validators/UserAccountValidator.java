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
 * File: UserAccountValidator.java
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
import com.cws.esolutions.security.dto.UserAccount;
/**
 * @author khuntly
 * @version 1.0
 * @see org.springframework.validation.Validator
 */
public class UserAccountValidator implements Validator
{
    private String messageUserRoleRequired = null;
    private String messageUsernameRequired = null;
    private String messageEmailAddrRequired = null;
    private String messageGivenNameRequired = null;
    private String messageUserSurnameRequired = null;
    private String messageSuspensionFlagRequired = null;

    private static final String CNAME = UserAccountValidator.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setMessageUserRoleRequired(final String value)
    {
        final String methodName = UserAccountValidator.CNAME + "#setMessageUserRoleRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageUserRoleRequired = value;
    }

    public final void setMessageUsernameRequired(final String value)
    {
        final String methodName = UserAccountValidator.CNAME + "#setMessageUsernameRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageUsernameRequired = value;
    }

    public final void setMessageEmailAddrRequired(final String value)
    {
        final String methodName = UserAccountValidator.CNAME + "#setMessageEmailAddrRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageEmailAddrRequired = value;
    }

    public final void setMessageGivenNameRequired(final String value)
    {
        final String methodName = UserAccountValidator.CNAME + "#setMessageGivenNameRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageGivenNameRequired = value;
    }

    public final void setMessageUserSurnameRequired(final String value)
    {
        final String methodName = UserAccountValidator.CNAME + "#setMessageUserSurnameRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageUserSurnameRequired = value;
    }

    public final void setMessageSuspensionFlagRequired(final String value)
    {
        final String methodName = UserAccountValidator.CNAME + "#setMessageSuspensionFlagRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageSuspensionFlagRequired = value;
    }

    public final boolean supports(final Class<?> value)
    {
        final String methodName = UserAccountValidator.CNAME + "#supports(final Class<?> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        final boolean isSupported = UserAccount.class.isAssignableFrom(value);

        if (DEBUG)
        {
            DEBUGGER.debug("isSupported: {}", isSupported);
        }

        return isSupported;
    }

    public final void validate(final Object target, final Errors errors)
    {
        final String methodName = UserAccountValidator.CNAME + "#validate(final Object target, final Errors errors)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Object: {}", target);
            DEBUGGER.debug("Errors: {}", errors);
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "role", this.messageUserRoleRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "surname", this.messageUserSurnameRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", this.messageUsernameRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "emailAddr", this.messageEmailAddrRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "givenName", this.messageGivenNameRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "suspended", this.messageSuspensionFlagRequired);
    }
}
