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
 * cws-khuntly   11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
import java.util.regex.Pattern;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.ValidationUtils;

import com.cws.esolutions.web.Constants;
import com.cws.esolutions.security.config.xml.SecurityConfigurationData;
import com.cws.esolutions.security.processors.dto.AccountChangeData;
/**
 * @author cws-khuntly
 * @version 1.0
 * @see org.springframework.validation.Validator
 */
public class PasswordValidator implements Validator
{
    private String messagePasswordMatch = null;
    private String messageNewPasswordRequired = null;
    private SecurityConfigurationData secConfig = null;
    private String messageConfirmPasswordRequired = null;
    private String messageCurrentPasswordRequired = null;
    private String messagePasswordFailedValidation = null;

    private static final String CNAME = PasswordValidator.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setSecConfig(final SecurityConfigurationData value)
    {
        final String methodName = PasswordValidator.CNAME + "#setSecConfig(final SecurityConfigurationData value)";

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
            DEBUGGER.debug("errors: {}", errors);
        }

        final AccountChangeData changeReq = (AccountChangeData) target;
        final String newPassword = changeReq.getNewPassword();
        final String existingPassword = changeReq.getCurrentPassword();
        final int minLength = (this.secConfig.getSecurityConfig().getPasswordMinLength() >= 8) ? this.secConfig.getSecurityConfig().getPasswordMinLength() : 8;
        final int maxLength = (this.secConfig.getSecurityConfig().getPasswordMaxLength() <= 128) ? this.secConfig.getSecurityConfig().getPasswordMaxLength() : 128;
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
