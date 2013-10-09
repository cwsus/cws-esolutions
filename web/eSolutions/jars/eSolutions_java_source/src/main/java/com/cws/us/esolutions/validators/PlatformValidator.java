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
    private static final String CNAME = PlatformValidator.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    @Override
    public boolean supports(final Class<?> target)
    {
        final String methodName = PlatformValidator.CNAME + "#supports(final Class<?> target)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Class: ", target);
        }

        return PlatformRequest.class.isAssignableFrom(target);
    }

    @Override
    public void validate(final Object target, final Errors errors)
    {
        final String methodName = PlatformValidator.CNAME + "#validate(final Object target, final Errors errors)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Object: {}", target);
            DEBUGGER.debug("Errors: {}", errors);
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dmgrName", "platform.dmgr.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "platformName", "platform.name.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "platformDmgr", "platform.dmgr.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "status", "platform.status.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "appServers", "platform.appservers.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "webServers", "platform.webservers.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "platformRegion", "platform.region.required");
    }
}
