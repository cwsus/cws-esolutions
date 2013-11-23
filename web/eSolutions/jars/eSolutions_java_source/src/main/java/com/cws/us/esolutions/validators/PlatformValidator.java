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
import org.slf4j.Logger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.cws.us.esolutions.Constants;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.us.esolutions.ApplicationServiceBean;
import com.cws.esolutions.core.processors.dto.Server;
import com.cws.esolutions.core.processors.dto.Platform;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.core.processors.dto.ServerManagementRequest;
import com.cws.esolutions.core.processors.dto.ServerManagementResponse;
import com.cws.esolutions.core.processors.impl.ServerManagementProcessorImpl;
import com.cws.esolutions.core.processors.exception.ServerManagementException;
import com.cws.esolutions.core.processors.interfaces.IServerManagementProcessor;
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
    private String serviceId = null;
    private ApplicationServiceBean appConfig = null;
    private String messageWebServersRequired = null;
    private String messageAppServersRequired = null;
    private String messageDeploymentManagerRequired = null;

    private static final String CNAME = PlatformValidator.class.getName();
    private static final Collection<String> ignoreList = Collections.unmodifiableList(
            Arrays.asList(
                    "CNAME",
                    "DEBUGGER",
                    "DEBUG",
                    "ERROR_RECORDER",
                    "serialVersionUID"));

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public final void setAppConfig(final ApplicationServiceBean value)
    {
        final String methodName = PlatformValidator.CNAME + "#setAppConfig(final ApplicationServiceBean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.appConfig = value;
    }

    public final void setServiceId(final String value)
    {
        final String methodName = PlatformValidator.CNAME + "#setServiceId(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serviceId = value;
    }

    public final void setMessageWebServersRequired(final String value)
    {
        final String methodName = PlatformValidator.CNAME + "#setMessageWebServersRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageWebServersRequired = value;
    }

    public final void setMessageAppServersRequired(final String value)
    {
        final String methodName = PlatformValidator.CNAME + "#setMessageAppServersRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageAppServersRequired = value;
    }

    public final void setMessageDeploymentManagerRequired(final String value)
    {
        final String methodName = PlatformValidator.CNAME + "#setMessageDeploymentManagerRequired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageDeploymentManagerRequired = value;
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

        final boolean isSupported = List.class.isAssignableFrom(value);

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

        List<Object> requestList = (List<Object>) target;

        final IServerManagementProcessor processor = new ServerManagementProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("List<Object>: {}", requestList);
        }

        final Platform platform = (Platform) requestList.get(0);
        final RequestHostInfo hostInfo = (RequestHostInfo) requestList.get(1);
        final UserAccount userAccount = (UserAccount) requestList.get(2);

        if (DEBUG)
        {
            DEBUGGER.debug("Platform: {}", platform);
            DEBUGGER.debug("RequestHostInfo: {}", hostInfo);
            DEBUGGER.debug("UserAccount: {}", userAccount);
        }

        for (Field field : platform.getClass().getDeclaredFields())
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
                        if (field.get(platform) == null)
                        {
                            if (DEBUG)
                            {
                                DEBUGGER.debug("Rejecting value for " + field.getName());
                            }

                            errors.reject(field.getName(), appConfig.getMessageValidationFailed());
                        }
                    }
                    catch (IllegalAccessException iax)
                    {
                        // nothing
                    }
                }
            }
        }

        try
        {
            ServerManagementRequest dmgrRequest = new ServerManagementRequest();
            dmgrRequest.setRequestInfo(hostInfo);
            dmgrRequest.setServiceId(this.serviceId);
            dmgrRequest.setTargetServer(platform.getPlatformDmgr());
            dmgrRequest.setUserAccount(userAccount);
            dmgrRequest.setApplicationId(appConfig.getApplicationId());
            dmgrRequest.setApplicationName(appConfig.getApplicationName());

            if (DEBUG)
            {
                DEBUGGER.debug("ServerManagementRequest: {}", dmgrRequest);
            }

            ServerManagementResponse dmgrResponse = processor.getServerData(dmgrRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("ServerManagementResponse: {}", dmgrResponse);
            }

            if (dmgrResponse.getRequestStatus() != CoreServicesStatus.SUCCESS)
            {
                errors.reject(this.messageDeploymentManagerRequired);
            }

            for (Server appServer : platform.getAppServers())
            {
                if (DEBUG)
                {
                    DEBUGGER.debug("Server: {}", appServer);
                }

                ServerManagementRequest serverReq = new ServerManagementRequest();
                serverReq.setRequestInfo(hostInfo);
                serverReq.setServiceId(this.serviceId);
                serverReq.setTargetServer(appServer);
                serverReq.setUserAccount(userAccount);
                serverReq.setApplicationId(appConfig.getApplicationId());
                serverReq.setApplicationName(appConfig.getApplicationName());

                if (DEBUG)
                {
                    DEBUGGER.debug("ServerManagementRequest: {}", serverReq);
                }

                ServerManagementResponse serverRes = processor.getServerData(serverReq);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServerManagementResponse: {}", serverRes);
                }

                if (serverRes.getRequestStatus() != CoreServicesStatus.SUCCESS)
                {
                    errors.reject(this.messageAppServersRequired);
                }
            }

            for (Server webServer : platform.getWebServers())
            {
                if (DEBUG)
                {
                    DEBUGGER.debug("Server: {}", webServer);
                }

                ServerManagementRequest serverReq = new ServerManagementRequest();
                serverReq.setRequestInfo(hostInfo);
                serverReq.setServiceId(this.serviceId);
                serverReq.setTargetServer(webServer);
                serverReq.setUserAccount(userAccount);
                serverReq.setApplicationId(appConfig.getApplicationId());
                serverReq.setApplicationName(appConfig.getApplicationName());

                if (DEBUG)
                {
                    DEBUGGER.debug("ServerManagementRequest: {}", serverReq);
                }

                ServerManagementResponse serverRes = processor.getServerData(serverReq);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServerManagementResponse: {}", serverRes);
                }

                if (serverRes.getRequestStatus() != CoreServicesStatus.SUCCESS)
                {
                    errors.reject(this.messageWebServersRequired);
                }
            }
        }
        catch (ServerManagementException smx)
        {
            errors.reject(appConfig.getMessageValidationFailed());
        }
    }
}
