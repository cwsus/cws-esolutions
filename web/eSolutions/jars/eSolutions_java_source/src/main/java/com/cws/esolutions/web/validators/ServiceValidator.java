/*
 * Copyright (c) 2009 - 2014 CaspersBox Web Services
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
import com.cws.esolutions.core.processors.dto.Service;
import com.cws.esolutions.core.processors.enums.ServiceType;
/*
 * Project: eSolutions_java_source
 * Package: com.cws.esolutions.web.validators
 * File: ServiceValidator.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
public class ServiceValidator implements Validator
{
    private String messageNameRequired = null;
    private String messageTypeRequired = null;
    private String messageStatusRequired = null;
    private String messageRegionRequired = null;
    private String messageServersRequired = null;
    private String messagePartitionRequired = null;
    private String messageDescriptionRequired = null;

    private static final String CNAME = ServiceValidator.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setMessageNameRequired(final String value)
    {
        final String methodName = ServiceValidator.CNAME + "#setMessageNameRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageNameRequired = value;
    }

    public final void setMessageStatusRequired(final String value)
    {
        final String methodName = ServiceValidator.CNAME + "#setMessageStatusRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageStatusRequired = value;
    }

    public final void setMessageDescriptionRequired(final String value)
    {
        final String methodName = ServiceValidator.CNAME + "#setMessageDescriptionRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageDescriptionRequired = value;
    }

    public final void setMessageRegionRequired(final String value)
    {
        final String methodName = ServiceValidator.CNAME + "#setMessageRegionRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageRegionRequired = value;
    }

    public final void setMessageServersRequired(final String value)
    {
        final String methodName = ServiceValidator.CNAME + "#setMessageServersRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageServersRequired = value;
    }

    public final void setMessagePartitionRequired(final String value)
    {
        final String methodName = ServiceValidator.CNAME + "#setMessagePartitionRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messagePartitionRequired = value;
    }

    public final void setMessageTypeRequired(final String value)
    {
        final String methodName = ServiceValidator.CNAME + "#setMessageTypeRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageTypeRequired = value;
    }

    @Override
    public final boolean supports(final Class<?> value)
    {
        final String methodName = ServiceValidator.CNAME + "#supports(final Class<?> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        final boolean isSupported = Service.class.isAssignableFrom(value);

        if (DEBUG)
        {
            DEBUGGER.debug("isSupported: {}", value);
        }

        return isSupported;
    }

    @Override
    public final void validate(final Object target, final Errors errors)
    {
        final String methodName = ServiceValidator.CNAME + "#validate(final Object target, final Errors errors)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Object: {}", target);
            DEBUGGER.debug("Errors: {}", errors);
        }

        final Service service = (Service) target;

        if (DEBUG)
        {
            DEBUGGER.debug("Service: {}", service);
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", this.messageNameRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "type", this.messageTypeRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", this.messageDescriptionRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "status", this.messageStatusRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "region", this.messageRegionRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "partition", this.messagePartitionRequired);

        if ((service.getType() == ServiceType.PLATFORM) && ((service.getServers() == null) || (service.getServers().size() == 0)))
        {
            errors.rejectValue("servers", this.messageServersRequired);
        }
    }
}
