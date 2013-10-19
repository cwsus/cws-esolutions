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

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.multipart.MultipartFile;

import com.cws.us.esolutions.Constants;
import com.cws.us.esolutions.ApplicationServiceBean;
import com.cws.us.esolutions.dto.ApplicationRequest;
/**
 * eSolutions_java_source
 * com.cws.us.esolutions.validators
 * DeploymentValidator.java
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
@Component
public class DeploymentValidator implements Validator
{
    private static final String CNAME = DeploymentValidator.class.getName();

    private ApplicationServiceBean appConfig = null;

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setAppConfig(final ApplicationServiceBean value)
    {
        final String methodName = DeploymentValidator.CNAME + "#setAppConfig(final CoreServiceBean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.appConfig = value;
    }

    @Override
    public boolean supports(final Class<?> target)
    {
        final String methodName = DeploymentValidator.CNAME + "#supports(final Class<?> target)";

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
        final String methodName = DeploymentValidator.CNAME + "#validate(final Object target, final Errors errors)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Object: {}", target);
            DEBUGGER.debug("Errors: {}", errors);
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "version", "app.mgmt.app.version.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "deploymentType", "app.mgmt.app.deployment.type.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "applicationBinary", "app.mgmt.app.deployment.files.required");

        final ApplicationRequest request = (ApplicationRequest) target;

        if (DEBUG)
        {
            DEBUGGER.debug("ApplicationRequest: {}", request);
        }

        if (request.getApplicationBinary() != null)
        {
            MultipartFile binary = request.getApplicationBinary();

            if (DEBUG)
            {
                DEBUGGER.debug("MultipartFile: {}", binary);
            }

            final File uploadedFile = FileUtils.getFile(binary.getOriginalFilename());

            if (DEBUG)
            {
                DEBUGGER.debug("File: {}", uploadedFile);
            }

            final String fileExt = StringUtils.substring(uploadedFile.getName(), uploadedFile.getName().lastIndexOf("."));

            if (DEBUG)
            {
                DEBUGGER.debug("fileExt: {}", fileExt);
            }

            switch (request.getDeploymentType())
            {
                case APP:
                    for (String allowed : appConfig.getAllowedAppFileExtensions())
                    {
                        if (!(StringUtils.equals(fileExt, allowed)))
                        {
                            errors.reject("applicationBinary", "app.mgmt.binary.invalid.type");
                        }

                        break;
                    }

                    break;
                case WEB:
                    for (String allowed : appConfig.getAllowedWebFileExtensions())
                    {
                        if (!(StringUtils.equals(fileExt, allowed)))
                        {
                            errors.reject("applicationBinary", "app.mgmt.binary.invalid.type");
                        }

                        break;
                    }

                    break;
                default:
                    errors.reject("applicationBinary", "app.mgmt.binary.not.valid.for.type");

                    break;
            }
        }
    }
}
