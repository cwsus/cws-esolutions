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

import java.util.List;
import java.util.Arrays;
import org.slf4j.Logger;
import java.util.Collection;
import java.util.Collections;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.ValidationUtils;

import com.cws.us.esolutions.Constants;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.us.esolutions.ApplicationServiceBean;
import com.cws.esolutions.core.processors.dto.Platform;
import com.cws.esolutions.core.processors.dto.Application;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.core.processors.dto.ProjectManagementRequest;
import com.cws.esolutions.core.processors.dto.ProjectManagementResponse;
import com.cws.esolutions.core.processors.dto.PlatformManagementRequest;
import com.cws.esolutions.core.processors.dto.PlatformManagementResponse;
import com.cws.esolutions.core.processors.impl.ProjectManagementProcessorImpl;
import com.cws.esolutions.core.processors.impl.PlatformManagementProcessorImpl;
import com.cws.esolutions.core.processors.exception.ProjectManagementException;
import com.cws.esolutions.core.processors.exception.PlatformManagementException;
import com.cws.esolutions.core.processors.interfaces.IProjectManagementProcessor;
import com.cws.esolutions.core.processors.interfaces.IPlatformManagementProcessor;
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
    private String platformService = null;
    private String projectService = null;
    private String messageScmPathRequired = null;
    private ApplicationServiceBean appConfig = null;
    private String messageApplicationVersionRequired = null;
    private String messageApplicationProjectRequired = null;
    private String messageApplicationPlatformRequired = null;

    private static final String CNAME = ApplicationValidator.class.getName();
    private static final Collection<String> ignoreList = Collections.unmodifiableList(
            Arrays.asList(
                    "CNAME",
                    "DEBUGGER",
                    "DEBUG",
                    "ERROR_RECORDER",
                    "serialVersionUID",
                    "deploymentType",
                    "managementType",
                    "applicationBinary",
                    "scmPath",
                    "applicationGuid"));

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setAppConfig(final ApplicationServiceBean value)
    {
        final String methodName = ApplicationValidator.CNAME + "#setAppConfig(final ApplicationServiceBean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.appConfig = value;
    }

    public final void setPlatformService(final String value)
    {
        final String methodName = ApplicationValidator.CNAME + "#setPlatformService(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.platformService = value;
    }

    public final void setProjectService(final String value)
    {
        final String methodName = ApplicationValidator.CNAME + "#setProjectService(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.projectService = value;
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

        final boolean isSupported = List.class.isAssignableFrom(target);

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

        final List<Object> requestList = (List<Object>) target;
        final IProjectManagementProcessor projectProcessor = new ProjectManagementProcessorImpl();
        final IPlatformManagementProcessor platformProcessor = new PlatformManagementProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("List<Object>: {}", requestList);
        }

        final Application application = (Application) requestList.get(0);
        final RequestHostInfo reqInfo = (RequestHostInfo) requestList.get(1);
        final UserAccount userAccount = (UserAccount) requestList.get(2);

        for (Field field : application.getClass().getDeclaredFields())
        {
            field.setAccessible(true);

            if (DEBUG)
            {
                DEBUGGER.debug("field: {}", field);
            }

            for (String str : ignoreList)
            {
                if (DEBUG)
                {
                    DEBUGGER.debug("String: {}", str);
                }

                if (!(StringUtils.equals(str, field.getName())))
                {
                    try
                    {
                        if (field.get(application) == null)
                        {
                            if (DEBUG)
                            {
                                DEBUGGER.debug("Rejecting value for " + field.getName());
                            }

                            errors.reject(field.getName(), appConfig.getMessageValidationFailed());

                            return;
                        }
                    }
                    catch (IllegalAccessException iax)
                    {
                        // nothing
                    }
                }
            }
        }

        for (Platform platform : application.getApplicationPlatforms())
        {
            if (DEBUG)
            {
                DEBUGGER.debug("Platform: {}", platform);
            }

            PlatformManagementRequest request = new PlatformManagementRequest();
            request.setApplicationId(appConfig.getApplicationId());
            request.setApplicationName(appConfig.getApplicationName());
            request.setPlatform(platform);
            request.setRequestInfo(reqInfo);
            request.setServiceId(this.platformService);
            request.setUserAccount(userAccount);

            if (DEBUG)
            {
                DEBUGGER.debug("PlatformManagementRequest: {}", request);
            }

            try
            {
                PlatformManagementResponse response = platformProcessor.getPlatformData(request);

                if (response.getRequestStatus() != CoreServicesStatus.SUCCESS)
                {
                    errors.reject(this.messageApplicationPlatformRequired);

                    return;
                }
            }
            catch (PlatformManagementException pmx)
            {
                errors.reject(this.messageApplicationPlatformRequired);

                return;
            }
        }

        ProjectManagementRequest projectReq = new ProjectManagementRequest();
        projectReq.setApplicationId(appConfig.getApplicationId());
        projectReq.setApplicationName(appConfig.getApplicationName());
        projectReq.setProject(application.getApplicationProject());
        projectReq.setRequestInfo(reqInfo);
        projectReq.setServiceId(this.projectService);
        projectReq.setUserAccount(userAccount);

        if (DEBUG)
        {
            DEBUGGER.debug("ProjectManagementRequest: {}", projectReq);
        }

        try
        {
            ProjectManagementResponse projectRes = projectProcessor.getProjectData(projectReq);

            if (DEBUG)
            {
                DEBUGGER.debug("ProjectManagementResponse: {}", projectRes);
            }

            if (projectRes.getRequestStatus() != CoreServicesStatus.SUCCESS)
            {
                errors.reject(this.messageApplicationProjectRequired);
            }
        }
        catch (ProjectManagementException pmx)
        {
            errors.reject(this.messageApplicationProjectRequired);
        }

        if (StringUtils.equals(application.getApplicationVersion(), "0.0"))
        {
            errors.reject("version", this.messageApplicationVersionRequired);
        }

        if ((application.isScmEnabled()) && (StringUtils.isEmpty(application.getScmPath()))) 
        {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "scmPath", this.messageScmPathRequired);
        }
    }
}
