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
package com.cws.us.esolutions.controllers;

import java.util.Date;
import org.slf4j.Logger;
import java.io.IOException;
import java.util.Enumeration;
import java.text.MessageFormat;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.IOUtils;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.cws.us.esolutions.Constants;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.us.esolutions.ApplicationServiceBean;
import com.cws.esolutions.core.processors.dto.Server;
import com.cws.us.esolutions.validators.SystemCheckValidator;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.enums.ServiceCheckType;
import com.cws.esolutions.core.processors.dto.SystemCheckRequest;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.core.processors.dto.ServerManagementRequest;
import com.cws.esolutions.core.processors.dto.ServerManagementResponse;
import com.cws.esolutions.core.processors.impl.ServerManagementProcessorImpl;
import com.cws.esolutions.core.processors.exception.ServerManagementException;
import com.cws.esolutions.core.processors.interfaces.IServerManagementProcessor;
/**
 * eSolutions_java_source
 * com.cws.us.esolutions.controllers
 * SystemCheckController.java
 *
 * $Id$
 * $Author$
 * $Date$
 * $Revision$
 * @author kh05451
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Jan 16, 2013 11:53:26 AM
 *     Created.
 */
@Controller
@RequestMapping("/system-check")
public class SystemCheckController
{
    private String serviceId = null;
    private String serviceName = null;
    private String netstatPage = null;
    private String messageSource = null;
    private String errorResponse = null;
    private String remoteDatePage = null;
    private String testTelnetPage = null;
    private String listProcessesPage = null;
    private SystemCheckValidator validator = null;
    private ApplicationServiceBean appConfig = null;

    private static final String CNAME = SystemCheckController.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CNAME);

    public final void setServiceId(final String value)
    {
        final String methodName = SystemCheckController.CNAME + "#setServiceId(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serviceId = value;
    }

    public final void setServiceName(final String value)
    {
        final String methodName = SystemCheckController.CNAME + "#setServiceName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serviceName = value;
    }

    public final void setValidator(final SystemCheckValidator value)
    {
        final String methodName = SystemCheckController.CNAME + "#setValidator(final SystemCheckValidator value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.validator = value;
    }

    public final void setErrorResponse(final String value)
    {
        final String methodName = SystemCheckController.CNAME + "#setErrorResponse(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.errorResponse = value;
    }

    public final void setRemoteDatePage(final String value)
    {
        final String methodName = SystemCheckController.CNAME + "#setRemoteDatePage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.remoteDatePage = value;
    }

    public final void setTestTelnetPage(final String value)
    {
        final String methodName = SystemCheckController.CNAME + "#setTestTelnetPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.testTelnetPage = value;
    }

    public final void setNetstatPage(final String value)
    {
        final String methodName = SystemCheckController.CNAME + "#setNetstatPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.netstatPage = value;
    }

    public final void setListProcessesPage(final String value)
    {
        final String methodName = SystemCheckController.CNAME + "#setListProcessesPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.listProcessesPage = value;
    }

    public final void setAppConfig(final ApplicationServiceBean value)
    {
        final String methodName = SystemCheckController.CNAME + "#setAppConfig(final CoreServiceBean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.appConfig = value;
    }

    public final void setMessageSource(final String value)
    {
        final String methodName = SystemCheckController.CNAME + "#setMessageSource(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageSource = value;
    }

    @RequestMapping(value = "/remote-date/server/{value}", method = RequestMethod.GET)
    public final ModelAndView showRemoteDate(@PathVariable("value") final String value)
    {
        final String methodName = SystemCheckController.CNAME + "#showRemoteDate(@PathVariable(\"value\") final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IServerManagementProcessor processor = new ServerManagementProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String requestElement = paramsEnumeration.nextElement();
                Object requestValue = hRequest.getParameter(requestElement);

                DEBUGGER.debug("Parameter: " + requestElement + "; Value: " + requestValue);
            }
        }

        if (appConfig.getServices().get(this.serviceName))
        {
            try
            {
                RequestHostInfo reqInfo = new RequestHostInfo();
                reqInfo.setHostName(hRequest.getRemoteHost());
                reqInfo.setHostAddress(hRequest.getRemoteAddr());
                reqInfo.setSessionId(hSession.getId());

                if (DEBUG)
                {
                    DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
                }

                Server server = new Server();
                server.setServerGuid(value);

                if (DEBUG)
                {
                    DEBUGGER.debug("Server: {}", server);
                }

                // a source server is *required*
                ServerManagementRequest request = new ServerManagementRequest();
                request.setRequestInfo(reqInfo);
                request.setUserAccount(userAccount);
                request.setServiceId(this.serviceId);
                request.setApplicationId(appConfig.getApplicationId());
                request.setApplicationName(appConfig.getApplicationName());
                request.setTargetServer(server);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServerManagementRequest: {}", request);
                }

                ServerManagementResponse response = processor.getServerData(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServerManagementResponse: {}", response);
                }

                if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    Server resServer = response.getServer();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Server: {}", resServer);
                    }

                    mView.addObject("server", resServer);
                    mView.addObject("command", new SystemCheckRequest());
                    mView.setViewName(this.remoteDatePage);
                }
                else if (response.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                {
                    mView.setViewName(appConfig.getUnauthorizedPage());
                }
                else
                {
                    mView.addObject(Constants.ERROR_RESPONSE, response.getResponse());
                    mView.setViewName(appConfig.getErrorResponsePage());
                }
            }
            catch (ServerManagementException smx)
            {
                ERROR_RECORDER.error(smx.getMessage(), smx);

                mView.setViewName(appConfig.getErrorResponsePage());
            }
        }
        else
        {
            mView.setViewName(appConfig.getUnavailablePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/telnet/server/{value}", method = RequestMethod.GET)
    public final ModelAndView showTestTelnet(@PathVariable("value") final String value)
    {
        final String methodName = SystemCheckController.CNAME + "#showTestTelnet(@PathVariable(\"value\") final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IServerManagementProcessor processor = new ServerManagementProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String requestElement = paramsEnumeration.nextElement();
                Object requestValue = hRequest.getParameter(requestElement);

                DEBUGGER.debug("Parameter: " + requestElement + "; Value: " + requestValue);
            }
        }

        if (appConfig.getServices().get(this.serviceName))
        {
            RequestHostInfo reqInfo = new RequestHostInfo();
            reqInfo.setHostName(hRequest.getRemoteHost());
            reqInfo.setHostAddress(hRequest.getRemoteAddr());
            reqInfo.setSessionId(hSession.getId());

            if (DEBUG)
            {
                DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            }

            try
            {
                Server server = new Server();
                server.setServerGuid(value);

                if (DEBUG)
                {
                    DEBUGGER.debug("Server: {}", server);
                }

                // a source server is *required*
                ServerManagementRequest request = new ServerManagementRequest();
                request.setRequestInfo(reqInfo);
                request.setUserAccount(userAccount);
                request.setServiceId(this.serviceId);
                request.setApplicationId(appConfig.getApplicationId());
                request.setApplicationName(appConfig.getApplicationName());
                request.setTargetServer(server);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServerManagementRequest: {}", request);
                }

                ServerManagementResponse response = processor.getServerData(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServerManagementResponse: {}", response);
                }

                if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    Server resServer = response.getServer();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Server: {}", resServer);
                    }

                    mView.addObject("server", resServer);
                    mView.addObject("command", new SystemCheckRequest());
                    mView.setViewName(this.testTelnetPage);
                }
                else if (response.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                {
                    mView.setViewName(appConfig.getUnauthorizedPage());
                }
                else
                {
                    mView.addObject(Constants.ERROR_RESPONSE, response.getResponse());
                    mView.setViewName(appConfig.getErrorResponsePage());
                }
            }
            catch (ServerManagementException smx)
            {
                ERROR_RECORDER.error(smx.getMessage(), smx);

                mView.setViewName(appConfig.getErrorResponsePage());
            }
        }
        else
        {
            mView.setViewName(appConfig.getUnavailablePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/netstat/server/{value}", method = RequestMethod.GET)
    public final ModelAndView showNetstat(@PathVariable("value") final String value)
    {
        final String methodName = SystemCheckController.CNAME + "#showNetstat(@PathVariable(\"value\") final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IServerManagementProcessor processor = new ServerManagementProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String requestElement = paramsEnumeration.nextElement();
                Object requestValue = hRequest.getParameter(requestElement);

                DEBUGGER.debug("Parameter: " + requestElement + "; Value: " + requestValue);
            }
        }

        if (appConfig.getServices().get(this.serviceName))
        {
            try
            {
                RequestHostInfo reqInfo = new RequestHostInfo();
                reqInfo.setHostName(hRequest.getRemoteHost());
                reqInfo.setHostAddress(hRequest.getRemoteAddr());
                reqInfo.setSessionId(hSession.getId());

                if (DEBUG)
                {
                    DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
                }

                Server server = new Server();
                server.setServerGuid(value);

                if (DEBUG)
                {
                    DEBUGGER.debug("Server: {}", server);
                }

                // a source server is *required*
                ServerManagementRequest request = new ServerManagementRequest();
                request.setRequestInfo(reqInfo);
                request.setUserAccount(userAccount);
                request.setServiceId(this.serviceId);
                request.setApplicationId(appConfig.getApplicationId());
                request.setApplicationName(appConfig.getApplicationName());
                request.setTargetServer(server);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServerManagementRequest: {}", request);
                }

                ServerManagementResponse response = processor.getServerData(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServerManagementResponse: {}", response);
                }

                if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    Server resServer = response.getServer();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Server: {}", resServer);
                    }

                    mView.addObject("server", resServer);
                    mView.addObject("command", new SystemCheckRequest());
                    mView.setViewName(this.netstatPage);
                }
                else if (response.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                {
                    mView.setViewName(appConfig.getUnauthorizedPage());
                }
                else
                {
                    mView.addObject(Constants.ERROR_RESPONSE, response.getResponse());
                    mView.setViewName(appConfig.getErrorResponsePage());
                }
            }
            catch (ServerManagementException smx)
            {
                ERROR_RECORDER.error(smx.getMessage(), smx);

                mView.setViewName(appConfig.getErrorResponsePage());
            }
        }
        else
        {
            mView.setViewName(appConfig.getUnavailablePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/list-processes/server/{value}", method = RequestMethod.GET)
    public final ModelAndView showListProcesses(@PathVariable("value") final String value)
    {
        final String methodName = SystemCheckController.CNAME + "#showListProcesses(@PathVariable(\"value\") final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IServerManagementProcessor processor = new ServerManagementProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String requestElement = paramsEnumeration.nextElement();
                Object requestValue = hRequest.getParameter(requestElement);

                DEBUGGER.debug("Parameter: " + requestElement + "; Value: " + requestValue);
            }
        }

        if (appConfig.getServices().get(this.serviceName))
        {
            try
            {
                RequestHostInfo reqInfo = new RequestHostInfo();
                reqInfo.setHostName(hRequest.getRemoteHost());
                reqInfo.setHostAddress(hRequest.getRemoteAddr());
                reqInfo.setSessionId(hSession.getId());

                if (DEBUG)
                {
                    DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
                }

                Server server = new Server();
                server.setServerGuid(value);

                if (DEBUG)
                {
                    DEBUGGER.debug("Server: {}", server);
                }

                // a source server is *required*
                ServerManagementRequest request = new ServerManagementRequest();
                request.setRequestInfo(reqInfo);
                request.setUserAccount(userAccount);
                request.setServiceId(this.serviceId);
                request.setApplicationId(appConfig.getApplicationId());
                request.setApplicationName(appConfig.getApplicationName());
                request.setTargetServer(server);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServerManagementRequest: {}", request);
                }

                ServerManagementResponse response = processor.getServerData(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServerManagementResponse: {}", response);
                }

                if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    Server resServer = response.getServer();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Server: {}", resServer);
                    }

                    mView.addObject("server", resServer);
                    mView.addObject("command", new SystemCheckRequest());
                    mView.setViewName(this.listProcessesPage);
                }
                else if (response.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                {
                    mView.setViewName(appConfig.getUnauthorizedPage());
                }
                else
                {
                    mView.addObject(Constants.ERROR_RESPONSE, response.getResponse());
                    mView.setViewName(appConfig.getErrorResponsePage());
                }
            }
            catch (ServerManagementException smx)
            {
                ERROR_RECORDER.error(smx.getMessage(), smx);

                mView.setViewName(appConfig.getErrorResponsePage());
            }
        }
        else
        {
            mView.setViewName(appConfig.getUnavailablePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/telnet", method = RequestMethod.POST)
    public final ModelAndView runTelnetTest(@ModelAttribute("request") final SystemCheckRequest request, final BindingResult bindResult)
    {
        final String methodName = SystemCheckController.CNAME + "#runTelnetTest(@ModelAttribute(\"request\") final SystemCheckRequest request, final BindingResult bindResult)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("SystemCheckRequest: {}", request);
            DEBUGGER.debug("BindingResult: {}", bindResult);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IServerManagementProcessor processor = new ServerManagementProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String requestElement = paramsEnumeration.nextElement();
                Object requestValue = hRequest.getParameter(requestElement);

                DEBUGGER.debug("Parameter: " + requestElement + "; Value: " + requestValue);
            }
        }

        validator.validate(request, bindResult);

        if (DEBUG)
        {
            DEBUGGER.debug("BindingResult: {}", bindResult);
        }

        if (bindResult.hasErrors())
        {
            mView.addObject(Constants.RESPONSE_MESSAGE, appConfig.getMessageValidationFailed());
            mView.addObject("command", new SystemCheckRequest());
            mView.addObject("server", request.getSourceServer());

            mView.setViewName(this.testTelnetPage);
            return mView;
        }

        if (appConfig.getServices().get(this.serviceName))
        {
            RequestHostInfo reqInfo = new RequestHostInfo();
            reqInfo.setHostName(hRequest.getRemoteHost());
            reqInfo.setHostAddress(hRequest.getRemoteAddr());
            reqInfo.setSessionId(hSession.getId());

            if (DEBUG)
            {
                DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            }

            try
            {
                Server source = new Server();
                source.setServerGuid(request.getSourceServer());

                if (DEBUG)
                {
                    DEBUGGER.debug("Server: {}", source);
                }

                Server target = new Server();
                target.setOperHostName(request.getTargetServer());

                if (DEBUG)
                {
                    DEBUGGER.debug("Server: {}", target);
                }

                ServerManagementRequest serverReq = new ServerManagementRequest();
                serverReq.setServiceId(this.serviceId);
                serverReq.setSourceServer(source);
                serverReq.setTargetServer(target);
                serverReq.setRequestType(ServiceCheckType.TELNET);
                serverReq.setPortNumber(request.getTargetPort());
                serverReq.setRequestInfo(reqInfo);
                serverReq.setUserAccount(userAccount);
                serverReq.setApplicationId(appConfig.getApplicationId());
                serverReq.setApplicationName(appConfig.getApplicationName());

                if (DEBUG)
                {
                    DEBUGGER.debug("ServerManagementRequest: {}", serverReq);
                }

                ServerManagementResponse response = processor.runTelnetCheck(serverReq);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServerManagementResponse: {}", response);
                }

                if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    // all set
                    mView.addObject(Constants.MESSAGE_RESPONSE, response.getResponse());
                }
                else if (response.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                {
                    mView.setViewName(appConfig.getUnauthorizedPage());

                    return mView;
                }
                else
                {
                    // nooo
                    mView.addObject(Constants.ERROR_RESPONSE, response.getResponse());
                }

                // regardless of what happens we still allow the user to
                // make the request
                mView.addObject("command", new SystemCheckRequest());
                mView.setViewName(this.testTelnetPage);
            }
            catch (ServerManagementException smx)
            {
                ERROR_RECORDER.error(smx.getMessage(), smx);

                mView.addObject(Constants.ERROR_MESSAGE, smx.getMessage());
                mView.addObject("command", new SystemCheckRequest());
                mView.setViewName(this.testTelnetPage);
            }
        }
        else
        {
            mView.setViewName(appConfig.getUnavailablePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/remote-date", method = RequestMethod.POST)
    public final ModelAndView runRemoteDate(@ModelAttribute("request") final SystemCheckRequest request, final BindingResult binding)
    {
        final String methodName = SystemCheckController.CNAME + "#runRemoteDate(@ModelAttribute(\"request\") final SystemCheckRequest request, final BindingResult binding)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("SystemCheckRequest: {}", request);
            DEBUGGER.debug("BindingResult: {}", binding);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IServerManagementProcessor processor = new ServerManagementProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String requestElement = paramsEnumeration.nextElement();
                Object requestValue = hRequest.getParameter(requestElement);

                DEBUGGER.debug("Parameter: " + requestElement + "; Value: " + requestValue);
            }
        }

        if (appConfig.getServices().get(this.serviceName))
        {
            RequestHostInfo reqInfo = new RequestHostInfo();
            reqInfo.setHostName(hRequest.getRemoteHost());
            reqInfo.setHostAddress(hRequest.getRemoteAddr());
            reqInfo.setSessionId(hSession.getId());

            if (DEBUG)
            {
                DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            }

            try
            {
                Server server = new Server();
                server.setServerGuid(request.getSourceServer());

                if (DEBUG)
                {
                    DEBUGGER.debug("Server: {}", server);
                }

                ServerManagementRequest serverReq = new ServerManagementRequest();
                serverReq.setServiceId(this.serviceId);
                serverReq.setTargetServer(server);
                serverReq.setRequestType(ServiceCheckType.REMOTEDATE);
                serverReq.setRequestInfo(reqInfo);
                serverReq.setUserAccount(userAccount);
                serverReq.setApplicationId(appConfig.getApplicationId());
                serverReq.setApplicationName(appConfig.getApplicationName());

                if (DEBUG)
                {
                    DEBUGGER.debug("ServerManagementRequest: {}", serverReq);
                }

                ServerManagementResponse response = processor.runRemoteDateCheck(serverReq);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServerManagementResponse: {}", response);
                }

                mView.addObject("command", new SystemCheckRequest());

                if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    // all set
                    Date date = new Date((Long) response.getResponseObject());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Date: {}", date);
                    }

                    // remotedate.request.return.message = The current time on {0} is {1}.
                    String responseMessage = MessageFormat.format(IOUtils.toString(
                            this.getClass().getClassLoader().getResourceAsStream(this.messageSource)), new Object[]
                    {
                        request.getSourceServer(),
                        date
                    });

                    if (DEBUG)
                    {
                        DEBUGGER.debug("responseMessage: {}", responseMessage);
                    }

                    mView.addObject(Constants.MESSAGE_RESPONSE, responseMessage);
                }
                else if (response.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                {
                    mView.setViewName(appConfig.getUnauthorizedPage());

                    return mView;
                }
                else
                {
                    // nooo
                    mView.addObject(Constants.ERROR_RESPONSE, response.getResponse());
                }

                // regardless of what happens we still allow the user to
                // make the request
                mView.addObject("command", new SystemCheckRequest());
                mView.setViewName(this.remoteDatePage);
            }
            catch (IOException iox)
            {
                ERROR_RECORDER.error(iox.getMessage(), iox);

                mView.addObject(Constants.ERROR_MESSAGE, this.errorResponse);
                mView.addObject("command", new SystemCheckRequest());
                mView.setViewName(this.remoteDatePage);
            }
            catch (ServerManagementException smx)
            {
                ERROR_RECORDER.error(smx.getMessage(), smx);

                mView.addObject(Constants.ERROR_MESSAGE, smx.getMessage());
                mView.addObject("command", new SystemCheckRequest());
                mView.setViewName(this.remoteDatePage);
            }
        }
        else
        {
            mView.setViewName(appConfig.getUnavailablePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/netstat", method = RequestMethod.POST)
    public final ModelAndView runNetstat(@ModelAttribute("request") final SystemCheckRequest request, final BindingResult bindResult)
    {
        final String methodName = SystemCheckController.CNAME + "#runNetstat(@ModelAttribute(\"request\") final SystemCheckRequest request, final BindingResult bindResult)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("SystemCheckRequest: {}", request);
            DEBUGGER.debug("BindingResult: {}", bindResult);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IServerManagementProcessor processor = new ServerManagementProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String requestElement = paramsEnumeration.nextElement();
                Object requestValue = hRequest.getParameter(requestElement);

                DEBUGGER.debug("Parameter: " + requestElement + "; Value: " + requestValue);
            }
        }

        validator.validate(request, bindResult);

        if (DEBUG)
        {
            DEBUGGER.debug("BindingResult: {}", bindResult);
        }

        if (bindResult.hasErrors())
        {
            mView.addObject(Constants.RESPONSE_MESSAGE, appConfig.getMessageValidationFailed());
            mView.addObject("command", new SystemCheckRequest());
            mView.addObject("server", request.getSourceServer());

            mView.setViewName(this.netstatPage);
            return mView;
        }

        if (appConfig.getServices().get(this.serviceName))
        {
            RequestHostInfo reqInfo = new RequestHostInfo();
            reqInfo.setHostName(hRequest.getRemoteHost());
            reqInfo.setHostAddress(hRequest.getRemoteAddr());
            reqInfo.setSessionId(hSession.getId());

            if (DEBUG)
            {
                DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            }

            try
            {
                Server source = new Server();
                source.setServerGuid(request.getSourceServer());

                if (DEBUG)
                {
                    DEBUGGER.debug("Server: {}", source);
                }

                ServerManagementRequest serverReq = new ServerManagementRequest();
                serverReq.setServiceId(this.serviceId);
                serverReq.setSourceServer(source);
                serverReq.setRequestType(ServiceCheckType.NETSTAT);
                serverReq.setPortNumber(request.getTargetPort());
                serverReq.setRequestInfo(reqInfo);
                serverReq.setUserAccount(userAccount);
                serverReq.setApplicationId(appConfig.getApplicationId());
                serverReq.setApplicationName(appConfig.getApplicationName());

                if (DEBUG)
                {
                    DEBUGGER.debug("ServerManagementRequest: {}", serverReq);
                }

                ServerManagementResponse response = processor.runNetstatCheck(serverReq);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServerManagementResponse: {}", response);
                }

                if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    // all set
                    mView.addObject(Constants.MESSAGE_RESPONSE, response.getResponse());
                }
                else if (response.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                {
                    mView.setViewName(appConfig.getUnauthorizedPage());

                    return mView;
                }
                else
                {
                    // nooo
                    mView.addObject(Constants.ERROR_RESPONSE, response.getResponse());
                }

                // regardless of what happens we still allow the user to
                // make the request
                mView.addObject("command", new SystemCheckRequest());
                mView.setViewName(this.netstatPage);
            }
            catch (ServerManagementException smx)
            {
                ERROR_RECORDER.error(smx.getMessage(), smx);

                mView.addObject(Constants.ERROR_MESSAGE, smx.getMessage());
                mView.addObject("command", new SystemCheckRequest());
                mView.setViewName(this.netstatPage);
            }
        }
        else
        {
            mView.setViewName(appConfig.getUnavailablePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/list-processes", method = RequestMethod.POST)
    public final ModelAndView runProcessList(@ModelAttribute("request") final SystemCheckRequest request, final BindingResult bindResult)
    {
        final String methodName = SystemCheckController.CNAME + "#runProcessList(@ModelAttribute(\"request\") final SystemCheckRequest request, final BindingResult bindResult)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("SystemCheckRequest: {}", request);
            DEBUGGER.debug("BindingResult: {}", bindResult);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IServerManagementProcessor processor = new ServerManagementProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String requestElement = paramsEnumeration.nextElement();
                Object requestValue = hRequest.getParameter(requestElement);

                DEBUGGER.debug("Parameter: " + requestElement + "; Value: " + requestValue);
            }
        }

        validator.validate(request, bindResult);

        if (DEBUG)
        {
            DEBUGGER.debug("BindingResult: {}", bindResult);
        }

        if (bindResult.hasErrors())
        {
            mView.addObject(Constants.RESPONSE_MESSAGE, appConfig.getMessageValidationFailed());
            mView.addObject("command", new SystemCheckRequest());
            mView.addObject("server", request.getSourceServer());

            mView.setViewName(this.listProcessesPage);
            return mView;
        }

        if (appConfig.getServices().get(this.serviceName))
        {
            RequestHostInfo reqInfo = new RequestHostInfo();
            reqInfo.setHostName(hRequest.getRemoteHost());
            reqInfo.setHostAddress(hRequest.getRemoteAddr());
            reqInfo.setSessionId(hSession.getId());

            if (DEBUG)
            {
                DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            }

            try
            {
                Server source = new Server();
                source.setServerGuid(request.getSourceServer());

                if (DEBUG)
                {
                    DEBUGGER.debug("Server: {}", source);
                }

                ServerManagementRequest serverReq = new ServerManagementRequest();
                serverReq.setServiceId(this.serviceId);
                serverReq.setSourceServer(source);
                serverReq.setRequestType(ServiceCheckType.NETSTAT);
                serverReq.setPortNumber(request.getTargetPort());
                serverReq.setRequestInfo(reqInfo);
                serverReq.setUserAccount(userAccount);
                serverReq.setApplicationId(appConfig.getApplicationId());
                serverReq.setApplicationName(appConfig.getApplicationName());

                if (DEBUG)
                {
                    DEBUGGER.debug("ServerManagementRequest: {}", serverReq);
                }

                ServerManagementResponse response = processor.runProcessListCheck(serverReq);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServerManagementResponse: {}", response);
                }

                if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    // all set
                    mView.addObject(Constants.MESSAGE_RESPONSE, response.getResponse());
                }
                else if (response.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                {
                    mView.setViewName(appConfig.getUnauthorizedPage());

                    return mView;
                }
                else
                {
                    // nooo
                    mView.addObject(Constants.ERROR_RESPONSE, response.getResponse());
                }

                // regardless of what happens we still allow the user to
                // make the request
                mView.addObject("command", new SystemCheckRequest());
                mView.setViewName(this.listProcessesPage);
            }
            catch (ServerManagementException smx)
            {
                ERROR_RECORDER.error(smx.getMessage(), smx);

                mView.addObject(Constants.ERROR_MESSAGE, smx.getMessage());
                mView.addObject("command", new SystemCheckRequest());
                mView.setViewName(this.listProcessesPage);
            }
        }
        else
        {
            mView.setViewName(appConfig.getUnavailablePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }
}
