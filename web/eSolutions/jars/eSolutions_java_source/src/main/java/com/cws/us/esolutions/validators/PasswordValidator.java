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
import java.util.regex.Pattern;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.ValidationUtils;

import com.cws.us.esolutions.Constants;
import com.cws.us.esolutions.dto.UserChangeRequest;
import com.cws.esolutions.security.config.SecurityConfig;
/**
 * eSolutions_java_source
 * com.cws.us.esolutions.validators
 * PasswordValidator.java
 *
 * $Id: $
 * $Author: $
 * $Date: $
 * $Revision: $
 * @author khuntly
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * khuntly @ Apr 18, 2013 6:01:12 PM
 *     Created.
 */
public class PasswordValidator implements Validator
{
    private SecurityConfig secConfig = null;
    private String messagePasswordMatch = null;
    private String messageNewPasswordRequired = null;
    private String messageConfirmPasswordRequired = null;
    private String messageCurrentPasswordRequired = null;
    private String messagePasswordFailedValidation = null;

    private static final String CNAME = PasswordValidator.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setSecConfig(final SecurityConfig value)
    {
        final String methodName = PasswordValidator.CNAME + "#setSecConfig(final SecurityConfig value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.secConfig = value;
    }

    public final void setMessageCurrentPasswordRequired(final String value)
    {
        final String methodName = PasswordValidator.CNAME + "#setMessageCurrentPasswordRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageCurrentPasswordRequired = value;
    }

    public final void setMessageNewPasswordRequired(final String value)
    {
        final String methodName = PasswordValidator.CNAME + "#setMessageNewPasswordRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageNewPasswordRequired = value;
    }

    public final void setMessageConfirmPasswordRequired(final String value)
    {
        final String methodName = PasswordValidator.CNAME + "#setMessageConfirmPasswordRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageConfirmPasswordRequired = value;
    }

    public final void setMessagePasswordMatch(final String value)
    {
        final String methodName = PasswordValidator.CNAME + "#setMessagePasswordMatch(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messagePasswordMatch = value;
    }

    public final void setMessagePasswordFailedValidation(final String value)
    {
        final String methodName = PasswordValidator.CNAME + "#setMessagePasswordFailedValidation(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messagePasswordFailedValidation = value;
    }

    @Override
    public final boolean supports(final Class<?> value)
    {
        final String methodName = PasswordValidator.CNAME + "#supports(final Class<?> value)";

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
        final String methodName = PasswordValidator.CNAME + "#validate(final <Class> request)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("errors: {}", errors);
        }

        final UserChangeRequest changeReq = (UserChangeRequest) target;
        final String newPassword = changeReq.getNewPassword();
        final String existingPassword = changeReq.getCurrentPassword();
        final int minLength = (this.secConfig.getPasswordMinLength() >= 8) ? this.secConfig.getPasswordMinLength() : 8;
        final int maxLength = (this.secConfig.getPasswordMaxLength() <= 128) ? this.secConfig.getPasswordMaxLength() : 128;
        final Pattern pattern = Pattern.compile("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[~`!@#\\$%\\^\\&\\*()\\_\\-\\+\\=\\{\\}\\[\\]\\/|'\";:.,<>?]).{" + minLength + "," + maxLength + "})");

        if (DEBUG)
        {
            DEBUGGER.debug("UserChangeRequest: {}", changeReq);
            DEBUGGER.debug("minLength: {}", minLength);
            DEBUGGER.debug("maxLength: {}", maxLength);
            DEBUGGER.debug("pattern: {}", pattern);
        }

        if (!(changeReq.isReset()))
        {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "currentPassword", this.messageCurrentPasswordRequired);
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "newPassword", this.messageNewPasswordRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmPassword", this.messageConfirmPasswordRequired);

        if (!(changeReq.isReset()) && (StringUtils.equals(existingPassword, newPassword)))
        {
            errors.reject("currentPassword", this.messagePasswordMatch);
        }
        else if (!(pattern.matcher(newPassword).matches()))
        {
            errors.reject("newPassword", this.messagePasswordFailedValidation);
        }
    }
}
