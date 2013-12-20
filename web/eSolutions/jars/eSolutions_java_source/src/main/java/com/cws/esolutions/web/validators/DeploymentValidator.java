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

import com.cws.esolutions.web.Constants;
import com.cws.esolutions.web.ApplicationServiceBean;
import com.cws.esolutions.core.processors.dto.Application;
/*
 * Project: eSolutions_java_source
 * Package: com.cws.esolutions.web.validators
 * File: DeploymentValidator.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
@Component
public class DeploymentValidator implements Validator
{
    private String messageBinaryInvalid = null;
    private ApplicationServiceBean appConfig = null;
    private String messageBinaryInvalidForType = null;
    private String messageDeploymentTypeRequired = null;
    private String messageDeploymentFilesRequired = null;
    private String messageApplicationVersionRequired = null;

    private static final String CNAME = DeploymentValidator.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setAppConfig(final ApplicationServiceBean value)
    {
        final String methodName = DeploymentValidator.CNAME + "#setAppConfig(final ApplicationServiceBean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.appConfig = value;
    }

    public final void setMessageBinaryInvalid(final String value)
    {
        final String methodName = DeploymentValidator.CNAME + "#setMessageBinaryInvalid(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageBinaryInvalid = value;
    }

    public final void setMessageBinaryInvalidForType(final String value)
    {
        final String methodName = DeploymentValidator.CNAME + "#setMessageBinaryInvalidForType(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageBinaryInvalidForType = value;
    }

    public final void setMessageDeploymentFilesRequired(final String value)
    {
        final String methodName = DeploymentValidator.CNAME + "#setMessageDeploymentFilesRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageDeploymentFilesRequired = value;
    }

    public final void setMessageDeploymentTypeRequired(final String value)
    {
        final String methodName = DeploymentValidator.CNAME + "#setMessageDeploymentTypeRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageDeploymentTypeRequired = value;
    }

    public final void setMessageApplicationVersionRequired(final String value)
    {
        final String methodName = DeploymentValidator.CNAME + "#setMessageApplicationVersionRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageApplicationVersionRequired = value;
    }

    @Override
    public final boolean supports(final Class<?> value)
    {
        final String methodName = DeploymentValidator.CNAME + "#supports(final Class<?> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: ", value);
        }

        final boolean isSupported = Application.class.isAssignableFrom(value);

        if (DEBUG)
        {
            DEBUGGER.debug("isSupported: {}", isSupported);
        }

        return isSupported;
    }

    @Override
    public final void validate(final Object target, final Errors errors)
    {
        final String methodName = DeploymentValidator.CNAME + "#validate(final Object target, final Errors errors)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Object: {}", target);
            DEBUGGER.debug("Errors: {}", errors);
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "version", this.messageApplicationVersionRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "deploymentType", this.messageDeploymentTypeRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "applicationBinary", this.messageDeploymentFilesRequired);

        final Application request = (Application) target;

        if (DEBUG)
        {
            DEBUGGER.debug("ApplicationRequest: {}", request);
        }

        if (request.getApplicationBinary() != null)
        {
            MultipartFile binary = (MultipartFile) request.getApplicationBinary();

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
                    for (String allowed : this.appConfig.getAllowedAppFileExtensions())
                    {
                        if (!(StringUtils.equals(fileExt, allowed)))
                        {
                            errors.reject("applicationBinary", this.messageBinaryInvalid);
                        }

                        break;
                    }

                    break;
                case WEB:
                    for (String allowed : this.appConfig.getAllowedWebFileExtensions())
                    {
                        if (!(StringUtils.equals(fileExt, allowed)))
                        {
                            errors.reject("applicationBinary", this.messageBinaryInvalid);
                        }

                        break;
                    }

                    break;
                default:
                    errors.reject("applicationBinary", this.messageBinaryInvalidForType);

                    break;
            }
        }
    }
}
