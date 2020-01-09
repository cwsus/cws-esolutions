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
 * File: ApplicationValidator.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly   11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.ValidationUtils;

import com.cws.esolutions.web.Constants;
import com.cws.esolutions.core.processors.dto.Application;
/**
 * @author cws-khuntly
 * @version 1.0
 * @see org.springframework.validation.Validator
 */
public class ApplicationValidator implements Validator
{
    private String messageNameRequired = null;
    private String messageVersionRequired = null;
    private String messageLogsPathRequired = null;
    private String messagePlatformRequired = null;
    private String messageInstallPathRequired = null;
    private String messagePackageLocationRequired = null;
    private String messagePackageInstallerRequired = null;

    private static final String CNAME = ApplicationValidator.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setMessageNameRequired(final String value)
    {
        final String methodName = ApplicationValidator.CNAME + "#setMessageNameRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageNameRequired = value;
    }

    public final void setMessageLogsPathRequired(final String value)
    {
        final String methodName = ApplicationValidator.CNAME + "#setMessageLogsPathRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageLogsPathRequired = value;
    }

    public final void setMessageVersionRequired(final String value)
    {
        final String methodName = ApplicationValidator.CNAME + "#setMessageVersionRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageVersionRequired = value;
    }

    public final void setMessagePlatformRequired(final String value)
    {
        final String methodName = ApplicationValidator.CNAME + "#setMessagePlatformRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messagePlatformRequired = value;
    }

    public final void setMessageInstallPathRequired(final String value)
    {
        final String methodName = ApplicationValidator.CNAME + "#setMessageInstallPathRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageInstallPathRequired = value;
    }

    public final void setMessagePackageLocationRequired(final String value)
    {
        final String methodName = ApplicationValidator.CNAME + "#setMessagePackageLocationRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messagePackageLocationRequired = value;
    }

    public final void setMessagePackageInstallerRequired(final String value)
    {
        final String methodName = ApplicationValidator.CNAME + "#setMessagePackageInstallerRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messagePackageInstallerRequired = value;
    }

    public final boolean supports(final Class<?> value)
    {
        final String methodName = ApplicationValidator.CNAME + "#supports(final Class<?> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        final boolean isSupported = Application.class.isAssignableFrom(value);

        if (DEBUG)
        {
            DEBUGGER.debug("isSupported: {}", isSupported);
        }

        return isSupported;
    }

    public final void validate(final Object target, final Errors errors)
    {
        final String methodName = ApplicationValidator.CNAME + "#validate(final Object target, final Errors errors)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Object: {}", target);
            DEBUGGER.debug("Errors: {}", errors);
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", this.messageNameRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "version", this.messageVersionRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "installPath", this.messageInstallPathRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "logsDirectory", this.messageLogsPathRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "packageLocation", this.messagePackageLocationRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "packageInstaller", this.messagePackageInstallerRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "platforms", this.messagePlatformRequired);

        Application request = (Application) target;

        if (DEBUG)
        {
            DEBUGGER.debug("ApplicationRequest: {}", request);
        }

        if (request.getVersion() == 0.0)
        {
            errors.reject("version", this.messageVersionRequired);
        }
    }
}
