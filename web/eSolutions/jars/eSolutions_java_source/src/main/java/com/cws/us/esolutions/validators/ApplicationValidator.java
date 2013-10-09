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
    private static final String CNAME = ApplicationValidator.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    @Override
    public boolean supports(final Class<?> target)
    {
        final String methodName = ApplicationValidator.CNAME + "#supports(final Class<?> target)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Class: ", target);
        }

        return ApplicationRequest.class.isAssignableFrom(target);
    }

    @Override
    public void validate(final Object target, final Errors errors)
    {
        final String methodName = ApplicationValidator.CNAME + "#validate(final Object target, final Errors errors)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Object: {}", target);
            DEBUGGER.debug("Errors: {}", errors);
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "applicationName", "app.mgmt.app.name.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "applicationProject", "app.mgmt.app.project.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "applicationPlatform", "app.mgmt.app.platform.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "applicationVersion", "app.mgmt.app.version.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "applicationCluster", "app.mgmt.app.cluster.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "applicationLogsPath", "app.mgmt.app.logs.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "applicationInstallPath", "app.mgmt.app.install.required");
    }
}
