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
import com.cws.us.esolutions.dto.OnlineResetRequest;
/**
 * eSolutions_java_source
 * com.cws.us.esolutions.validators
 * OnlineResetValidator.java
 *
 * $Id: $
 * $Author: $
 * $Date: $
 * $Revision: $
 * @author khuntly
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * khuntly @ Apr 18, 2013 6:01:12 PM
 *     Created.
 */
public class OnlineResetValidator implements Validator
{
    private static final String CNAME = OnlineResetValidator.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    @Override
    public boolean supports(final Class<?> clazz)
    {
        final String methodName = OnlineResetValidator.CNAME + "#supports(final Class clazz)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Class: {}", clazz);
        }

        boolean isSupported = OnlineResetRequest.class.isAssignableFrom(clazz);

        if (DEBUG)
        {
            DEBUGGER.debug("isSupported: {}", isSupported);
        }

        return isSupported;
    }

    @Override
    public void validate(final Object target, final Errors errors)
    {
        final String methodName = OnlineResetValidator.CNAME + "#validate(final <Class> request)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("errors: {}", errors);
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "secAnswerOne", "error.answer.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "secAnswerTwo", "error.answer.empty");
    }
}
