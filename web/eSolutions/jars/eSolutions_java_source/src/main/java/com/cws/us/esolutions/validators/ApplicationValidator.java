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
import com.cws.us.esolutions.dto.ApplicationRequest;
/**
 * eSolutions_java_source
 * com.cws.us.esolutions.validators
 * ApplicationValidator.java
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
 * 35033355 @ May 16, 2013 8:40:08 AM
 *     Created.
 */
public class ApplicationValidator implements Validator
{
    private String messageApplicationNameRequired = null;
    private String messageApplicationClusterRequired = null;
    private String messageApplicationVersionRequired = null;
    private String messageApplicationProjectRequired = null;
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

    public final void setMessageApplicationProjectRequired(final String value)
    {
        final String methodName = ApplicationValidator.CNAME + "#setMessageApplicationProjectRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageApplicationProjectRequired = value;
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

    public final void setMessageApplicationClusterRequired(final String value)
    {
        final String methodName = ApplicationValidator.CNAME + "#setMessageApplicationClusterRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageApplicationClusterRequired = value;
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

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "applicationName", this.messageApplicationNameRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "applicationProject", this.messageApplicationProjectRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "applicationPlatform", this.messageApplicationPlatformRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "applicationVersion", this.messageApplicationVersionRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "applicationCluster", this.messageApplicationClusterRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "applicationLogsPath", this.messageApplicationLogsPathRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "applicationInstallPath", this.messageApplicationInstallPathRequired);
    }
}
