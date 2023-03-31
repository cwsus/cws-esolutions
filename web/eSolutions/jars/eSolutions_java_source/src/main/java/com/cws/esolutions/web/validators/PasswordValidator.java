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
 * File: PasswordValidator.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.ValidationUtils;

import com.cws.esolutions.web.Constants;
import com.cws.esolutions.security.processors.dto.AccountChangeData;
/**
 * @author cws-khuntly
 * @version 1.0
 * @see org.springframework.validation.Validator
 */
public class PasswordValidator implements Validator
{
	private int passwordMinLength = 8; // default
	private int passwordMaxLength = 128; // default
    private String messagePasswordMatch = null;
    private String messageNewPasswordRequired = null;
    private String messageConfirmPasswordRequired = null;
    private String messageCurrentPasswordRequired = null;
    private String messagePasswordFailedValidation = null;
    private String messagePasswordLengthCheckFailed = null;

    private static final String CNAME = PasswordValidator.class.getName();

    private static final Logger DEBUGGER = LogManager.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setPasswordMinLength(final int value)
    {
        final String methodName = PasswordValidator.CNAME + "#setPasswordMinLength(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.passwordMinLength = value;
    }

    public final void setPasswordMaxLength(final int value)
    {
        final String methodName = PasswordValidator.CNAME + "#setPasswordMaxLength(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.passwordMaxLength = value;
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

    public final void setMessagePasswordLengthCheckFailed(final String value)
    {
        final String methodName = PasswordValidator.CNAME + "#setMessagePasswordLengthCheckFailed(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Class: {}", value);
        }

    	this.messagePasswordLengthCheckFailed = value;
    }

    public final boolean supports(final Class<?> value)
    {
        final String methodName = PasswordValidator.CNAME + "#supports(final Class<?> value)";

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
        final String methodName = PasswordValidator.CNAME + "#validate(final <Class> request)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("target: {}", target);
            DEBUGGER.debug("errors: {}", errors);
        }

        final AccountChangeData changeReq = (AccountChangeData) target;
        final String newPassword = changeReq.getNewPassword();
        final String existingPassword = changeReq.getCurrentPassword();
        final int minLength = this.passwordMinLength;
        final int maxLength = this.passwordMaxLength;

        if (DEBUG)
        {
            DEBUGGER.debug("UserChangeRequest: {}", changeReq);
            DEBUGGER.debug("minLength: {}", minLength);
            DEBUGGER.debug("maxLength: {}", maxLength);
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
        else if (!(StringUtils.isAlphanumeric(newPassword)))
        {
        	errors.reject("currentPassword", this.messagePasswordFailedValidation);
        }
        else if ((newPassword.length() < minLength) || (newPassword.length() > maxLength))
        {
        	errors.reject("currentPassword", this.messagePasswordLengthCheckFailed);
        }
    }
}
