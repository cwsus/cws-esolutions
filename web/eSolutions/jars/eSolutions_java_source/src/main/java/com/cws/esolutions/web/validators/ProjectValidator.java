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
import com.cws.esolutions.web.dto.ProjectRequest;
/*
 * Project: eSolutions_java_source
 * Package: com.cws.esolutions.web.validators
 * File: ProjectValidator.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
public class ProjectValidator implements Validator
{
    private String messageDevEmailRequired = null;
    private String messagePrdEmailRequired = null;
    private String messageProjectNameRequired = null;
    private String messageChangeQueueRequired = null;
    private String messageProjectStatusRequired = null;
    private String messageIncidentQueueRequired = null;
    private String messagePrimaryContactRequired = null;

    private static final String CNAME = ProjectValidator.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setMessageDevEmailRequired(final String value)
    {
        final String methodName = ProjectValidator.CNAME + "#setMessageDevEmailRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageDevEmailRequired = value;
    }

    public final void setMessagePrdEmailRequired(final String value)
    {
        final String methodName = ProjectValidator.CNAME + "#setMessagePrdEmailRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messagePrdEmailRequired = value;
    }

    public final void setMessageProjectNameRequired(final String value)
    {
        final String methodName = ProjectValidator.CNAME + "#setMessageProjectNameRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageProjectNameRequired = value;
    }

    public final void setMessageChangeQueueRequired(final String value)
    {
        final String methodName = ProjectValidator.CNAME + "#setMessageChangeQueueRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageChangeQueueRequired = value;
    }

    public final void setMessageProjectStatusRequired(final String value)
    {
        final String methodName = ProjectValidator.CNAME + "#setMessageProjectStatusRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageProjectStatusRequired = value;
    }

    public final void setMessageIncidentQueueRequired(final String value)
    {
        final String methodName = ProjectValidator.CNAME + "#setMessageIncidentQueueRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageIncidentQueueRequired = value;
    }

    public final void setMessagePrimaryContactRequired(final String value)
    {
        final String methodName = ProjectValidator.CNAME + "#setMessagePrimaryContactRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messagePrimaryContactRequired = value;
    }

    @Override
    public final boolean supports(final Class<?> value)
    {
        final String methodName = ProjectValidator.CNAME + "#supports(final Class<?> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: ", value);
        }

        final boolean isSupported = ProjectRequest.class.isAssignableFrom(value);

        if (DEBUG)
        {
            DEBUGGER.debug("isSupported: {}", isSupported);
        }

        return isSupported;
    }

    @Override
    public final void validate(final Object target, final Errors errors)
    {
        final String methodName = ProjectValidator.CNAME + "#validate(final Object target, final Errors errors)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Object: {}", target);
            DEBUGGER.debug("Errors: {}", errors);
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "devEmail", this.messageDevEmailRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "prodEmail", this.messagePrdEmailRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "projectName", this.messageProjectNameRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "changeQueue", this.messageChangeQueueRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "incidentQueue", this.messageProjectStatusRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "primaryContact", this.messageIncidentQueueRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "secondaryContact", this.messagePrimaryContactRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "projectStatus", this.messageProjectStatusRequired);
    }
}
