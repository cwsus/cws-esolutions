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
import com.cws.us.esolutions.dto.PlatformRequest;
/**
 * eSolutions_java_source
 * com.cws.us.esolutions.validators
 * PlatformValidator.java
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
public class PlatformValidator implements Validator
{
    private String messagePlatformNameRequired = null;
    private String messagePlatformDmgrRequired = null;
    private String messagePlatformRegionRequired = null;
    private String messagePlatformStatusRequired = null;
    private String messagePlatformWebServersRequired = null;
    private String messagePlatformAppServersRequired = null;

    private static final String CNAME = PlatformValidator.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setMessagePlatformDmgrRequired(final String value)
    {
        final String methodName = PlatformValidator.CNAME + "#setMessagePlatformDmgrRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messagePlatformDmgrRequired = value;
    }

    public final void setMessagePlatformNameRequired(final String value)
    {
        final String methodName = PlatformValidator.CNAME + "#setMessagePlatformNameRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messagePlatformNameRequired = value;
    }

    public final void setMessagePlatformStatusRequired(final String value)
    {
        final String methodName = PlatformValidator.CNAME + "#setMessagePlatformStatusRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messagePlatformStatusRequired = value;
    }

    public final void setMessagePlatformAppServersRequired(final String value)
    {
        final String methodName = PlatformValidator.CNAME + "#setMessagePlatformAppServersRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messagePlatformAppServersRequired = value;
    }

    public final void setMessagePlatformWebServersRequired(final String value)
    {
        final String methodName = PlatformValidator.CNAME + "#setMessagePlatformWebServersRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messagePlatformWebServersRequired = value;
    }

    public final void setMessagePlatformRegionRequired(final String value)
    {
        final String methodName = PlatformValidator.CNAME + "#setMessagePlatformRegionRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messagePlatformRegionRequired = value;
    }

    @Override
    public final boolean supports(final Class<?> value)
    {
        final String methodName = PlatformValidator.CNAME + "#supports(final Class<?> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: ", value);
        }

        final boolean isSupported = PlatformRequest.class.isAssignableFrom(value);

        if (DEBUG)
        {
            DEBUGGER.debug("isSupported: {}", isSupported);
        }

        return isSupported;
    }

    @Override
    public final void validate(final Object target, final Errors errors)
    {
        final String methodName = PlatformValidator.CNAME + "#validate(final Object target, final Errors errors)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Object: {}", target);
            DEBUGGER.debug("Errors: {}", errors);
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dmgrName", this.messagePlatformDmgrRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "platformName", this.messagePlatformNameRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "platformDmgr", this.messagePlatformDmgrRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "status", this.messagePlatformStatusRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "appServers", this.messagePlatformAppServersRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "webServers", this.messagePlatformWebServersRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "platformRegion", this.messagePlatformRegionRequired);
    }
}
