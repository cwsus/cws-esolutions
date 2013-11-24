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
import org.apache.commons.lang.StringUtils;
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
    private String messageJvmNameRequired = null;
    private String messageScmPathRequired = null;
    private String messageBasePathRequired = null;
    private String messagePidDirectoryRequired = null;
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

    public final void setMessageJvmNameRequired(final String value)
    {
        final String methodName = ApplicationValidator.CNAME + "#setMessageJvmNameRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageJvmNameRequired = value;
    }

    public final void setMessageBasePathRequired(final String value)
    {
        final String methodName = ApplicationValidator.CNAME + "#setMessageBasePathRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageBasePathRequired = value;
    }

    public final void setMessagePidDirectoryRequired(final String value)
    {
        final String methodName = ApplicationValidator.CNAME + "#setMessagePidDirectoryRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messagePidDirectoryRequired = value;
    }

    public final void setMessageScmPathRequired(final String value)
    {
        final String methodName = ApplicationValidator.CNAME + "#setMessageScmPathRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageScmPathRequired = value;
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

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "jvmName", this.messageJvmNameRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "project", this.messageApplicationProjectRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "basePath", this.messageBasePathRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "platform", this.messageApplicationPlatformRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "version", this.messageApplicationVersionRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "logsPath", this.messageApplicationLogsPathRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "clusterName", this.messageApplicationClusterRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "installPath", this.messageApplicationInstallPathRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "pidDirectory", this.messagePidDirectoryRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "applicationName", this.messageApplicationNameRequired);
        
        ApplicationRequest request = (ApplicationRequest) target;

        if (DEBUG)
        {
            DEBUGGER.debug("ApplicationRequest: {}", request);
        }

        if (StringUtils.equals(request.getVersion(), "0.0"))
        {
            errors.reject("version", this.messageApplicationVersionRequired);
        }

        if ((request.isScmEnabled()) && (StringUtils.isEmpty(request.getScmPath())))
        {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "scmPath", this.messageScmPathRequired);
        }
    }
}