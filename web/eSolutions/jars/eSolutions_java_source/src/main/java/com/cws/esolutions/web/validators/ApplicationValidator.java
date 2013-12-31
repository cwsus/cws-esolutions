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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.ValidationUtils;

import com.cws.esolutions.web.Constants;
import com.cws.esolutions.web.dto.ApplicationRequest;
/*
 * Project: eSolutions_java_source
 * Package: com.cws.esolutions.web.validators
 * File: ApplicationValidator.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
public class ApplicationValidator implements Validator
{
    private String messagePackageLocationRequired = null;
    private String messageApplicationNameRequired = null;
    private String messagePackageInstallerRequired = null;
    private String messageApplicationVersionRequired = null;
    private String messageApplicationLogsPathRequired = null;
    private String messageApplicationPlatformRequired = null;
    private String messageApplicationInstallPathRequired = null;

    private static final String CNAME = ApplicationValidator.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setMessageApplicationNameRequired(final String value)
    {
        final String methodName = ApplicationValidator.CNAME + "#setMessageApplicationNameRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageApplicationNameRequired = value;
    }

    public final void setMessageApplicationLogsPathRequired(final String value)
    {
        final String methodName = ApplicationValidator.CNAME + "#setMessageApplicationLogsPathRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageApplicationLogsPathRequired = value;
    }

    public final void setMessageApplicationVersionRequired(final String value)
    {
        final String methodName = ApplicationValidator.CNAME + "#setMessageApplicationVersionRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageApplicationVersionRequired = value;
    }

    public final void setMessageApplicationPlatformRequired(final String value)
    {
        final String methodName = ApplicationValidator.CNAME + "#setMessageApplicationPlatformRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageApplicationPlatformRequired = value;
    }

    public final void setMessageApplicationInstallPathRequired(final String value)
    {
        final String methodName = ApplicationValidator.CNAME + "#setMessageApplicationInstallPathRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageApplicationInstallPathRequired = value;
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

    @Override
    public final boolean supports(final Class<?> target)
    {
        final String methodName = ApplicationValidator.CNAME + "#supports(final Class<?> target)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Class: ", target);
        }

        final boolean isSupported = ApplicationRequest.class.isAssignableFrom(target);

        if (DEBUG)
        {
            DEBUGGER.debug("isSupported: {}", isSupported);
        }

        return isSupported;
    }

    @Override
    public final void validate(final Object target, final Errors errors)
    {
        final String methodName = ApplicationValidator.CNAME + "#validate(final Object target, final Errors errors)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Object: {}", target);
            DEBUGGER.debug("Errors: {}", errors);
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", this.messageApplicationNameRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "version", this.messageApplicationVersionRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "installPath", this.messageApplicationInstallPathRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "logsDirectory", this.messageApplicationLogsPathRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "packageLocation", this.messagePackageLocationRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "packageInstaller", this.messagePackageInstallerRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "platforms", this.messageApplicationPlatformRequired);

        ApplicationRequest request = (ApplicationRequest) target;

        if (DEBUG)
        {
            DEBUGGER.debug("ApplicationRequest: {}", request);
        }

        if (request.getVersion() == 0.0)
        {
            errors.reject("version", this.messageApplicationVersionRequired);
        }
    }
}