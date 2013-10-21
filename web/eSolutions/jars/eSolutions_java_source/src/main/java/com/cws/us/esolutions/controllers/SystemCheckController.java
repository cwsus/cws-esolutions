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

import java.util.Map;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import java.util.HashMap;
import java.io.IOException;
import java.util.Enumeration;
import java.text.MessageFormat;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.IOUtils;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.cws.us.esolutions.Constants;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.us.esolutions.dto.ManagementRequest;
import com.cws.us.esolutions.ApplicationServiceBean;
import com.cws.esolutions.core.processors.dto.Server;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.agent.processors.enums.SystemCheckType;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.core.processors.dto.ServerManagementRequest;
import com.cws.esolutions.core.processors.dto.ServerManagementResponse;
import com.cws.esolutions.core.processors.impl.ServerManagementProcessorImpl;
import com.cws.esolutions.security.access.control.impl.UserControlServiceImpl;
import com.cws.esolutions.core.processors.exception.ServerManagementException;
import com.cws.esolutions.core.processors.interfaces.IServerManagementProcessor;
import com.cws.esolutions.security.access.control.interfaces.IUserControlService;
import com.cws.esolutions.security.access.control.exception.UserControlServiceException;
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
    private String defaultPage = null;
    private String messageSource = null;
    private String errorResponse = null;
    private String remoteDatePage = null;
    private String testTelnetPage = null;
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

    public final void setDefaultPage(final String value)
    {
        final String methodName = SystemCheckController.CNAME + "#setDefaultPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.defaultPage = value;
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

    @RequestMapping(value = "/default", method = RequestMethod.GET)
    public final ModelAndView showDefaultPage()
    {
        final String methodName = SystemCheckController.CNAME + "#showDefaultPage()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);

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

        if (userAccount.getStatus() == LoginStatus.EXPIRED)
        {
            // redirect to password page
            mView = new ModelAndView(new RedirectView());
            mView.setViewName(appConfig.getExpiredRedirect());
            mView.addObject(Constants.ERROR_MESSAGE, Constants.PASSWORD_EXPIRED);

            if (DEBUG)
            {
                DEBUGGER.debug("ModelAndView: {}", mView);
            }

            return mView;
        }

        if (appConfig.getServices().get(this.serviceName))
        {
            try
            {
                IUserControlService control = new UserControlServiceImpl();

                boolean isUserAuthorized = control.isUserAuthorizedForService(userAccount.getGuid(), this.serviceId);

                if (DEBUG)
                {
                    DEBUGGER.debug("isUserAuthorized: {}", isUserAuthorized);
                }

                if (isUserAuthorized)
                {
                    mView.setViewName(this.defaultPage);
                }
                else
                {
                    mView.setViewName(appConfig.getUnauthorizedPage());
                }
            }
            catch (UserControlServiceException ucsx)
            {
                ERROR_RECORDER.error(ucsx.getMessage(), ucsx);

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

    @RequestMapping(value = "/remote-date", method = RequestMethod.GET)
    public final ModelAndView showRemoteDate()
    {
        final String methodName = SystemCheckController.CNAME + "#showRemoteDate()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
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

        if (userAccount.getStatus() == LoginStatus.EXPIRED)
        {
            // redirect to password page
            mView = new ModelAndView(new RedirectView());
            mView.setViewName(appConfig.getExpiredRedirect());
            mView.addObject(Constants.ERROR_MESSAGE, Constants.PASSWORD_EXPIRED);

            if (DEBUG)
            {
                DEBUGGER.debug("ModelAndView: {}", mView);
            }

            return mView;
        }

        if (appConfig.getServices().get(this.serviceName))
        {
            try
            {
                IUserControlService control = new UserControlServiceImpl();

                boolean isUserAuthorized = control.isUserAuthorizedForService(userAccount.getGuid(), this.serviceId);

                if (DEBUG)
                {
                    DEBUGGER.debug("isUserAuthorized: {}", isUserAuthorized);
                }

                if (isUserAuthorized)
                {
                    RequestHostInfo reqInfo = new RequestHostInfo();
                    reqInfo.setHostName(hRequest.getRemoteHost());
                    reqInfo.setHostAddress(hRequest.getRemoteAddr());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
                    }

                    // a source server is *required*
                    ServerManagementRequest request = new ServerManagementRequest();
                    request.setRequestInfo(reqInfo);
                    request.setUserAccount(userAccount);
                    request.setServiceId(this.serviceId);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("ServerManagementRequest: {}", request);
                    }

                    ServerManagementResponse response = processor.listServers(request);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("ServerManagementResponse: {}", response);
                    }

                    if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                    {
                        List<Server> servers = response.getServerList();

                        if (DEBUG)
                        {
                            DEBUGGER.debug("servers: {}", servers);
                        }

                        Map<String, String> serverMap = new HashMap<String, String>();

                        for (Server server : servers)
                        {
                            if (DEBUG)
                            {
                                DEBUGGER.debug("Server: {}", server);
                            }

                            serverMap.put(server.getServerGuid(), server.getOperHostName());
                        }

                        mView.addObject("serverList", serverMap);
                        mView.addObject("command", new ManagementRequest());
                        mView.setViewName(this.remoteDatePage);
                    }
                    else
                    {
                        mView.addObject(Constants.ERROR_MESSAGE, response.getResponse());
                    }
                }
                else
                {
                    mView.setViewName(appConfig.getUnauthorizedPage());
                }
            }
            catch (ServerManagementException smx)
            {
                ERROR_RECORDER.error(smx.getMessage(), smx);

                mView.setViewName(appConfig.getErrorResponsePage());
            }
            catch (UserControlServiceException ucsx)
            {
                ERROR_RECORDER.error(ucsx.getMessage(), ucsx);

                mView.setViewName(appConfig.getUnauthorizedPage());
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

    @RequestMapping(value = "/telnet", method = RequestMethod.GET)
    public final ModelAndView showTestTelnet()
    {
        final String methodName = SystemCheckController.CNAME + "#showTestTelnet()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
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

        if (userAccount.getStatus() == LoginStatus.EXPIRED)
        {
            // redirect to password page
            mView = new ModelAndView(new RedirectView());
            mView.setViewName(appConfig.getExpiredRedirect());
            mView.addObject(Constants.ERROR_MESSAGE, Constants.PASSWORD_EXPIRED);

            if (DEBUG)
            {
                DEBUGGER.debug("ModelAndView: {}", mView);
            }

            return mView;
        }

        if (appConfig.getServices().get(this.serviceName))
        {
            RequestHostInfo reqInfo = new RequestHostInfo();
            reqInfo.setHostName(hRequest.getRemoteHost());
            reqInfo.setHostAddress(hRequest.getRemoteAddr());

            if (DEBUG)
            {
                DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            }

            try
            {
                // a source server is *required*
                ServerManagementRequest request = new ServerManagementRequest();
                request.setRequestInfo(reqInfo);
                request.setUserAccount(userAccount);
                request.setServiceId(this.serviceId);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServerManagementRequest: {}", request);
                }

                ServerManagementResponse response = processor.listServers(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServerManagementResponse: {}", response);
                }

                if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    List<Server> servers = response.getServerList();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("servers: {}", servers);
                    }

                    Map<String, String> serverMap = new HashMap<String, String>();

                    for (Server server : servers)
                    {
                        if (DEBUG)
                        {
                            DEBUGGER.debug("Server: {}", server);
                        }

                        serverMap.put(server.getServerGuid(), server.getOperHostName());
                    }

                    mView.addObject("serverList", serverMap);
                    mView.addObject("command", new ManagementRequest());
                    mView.setViewName(this.testTelnetPage);
                }
                else
                {
                    mView.addObject(Constants.ERROR_MESSAGE, response.getResponse());
                    mView.setViewName(this.defaultPage);
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
    public final ModelAndView runTelnetTest(@ModelAttribute("request") final ManagementRequest request, final BindingResult binding)
    {
        final String methodName = SystemCheckController.CNAME + "#runTelnetTest(@ModelAttribute(\"request\") final ManagementRequest request, final BindingResult binding)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ManagementRequest: {}", request);
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

        if (userAccount.getStatus() == LoginStatus.EXPIRED)
        {
            // redirect to password page
            mView = new ModelAndView(new RedirectView());
            mView.setViewName(appConfig.getExpiredRedirect());
            mView.addObject(Constants.ERROR_MESSAGE, Constants.PASSWORD_EXPIRED);

            if (DEBUG)
            {
                DEBUGGER.debug("ModelAndView: {}", mView);
            }

            return mView;
        }

        if (appConfig.getServices().get(this.serviceName))
        {
            RequestHostInfo reqInfo = new RequestHostInfo();
            reqInfo.setHostName(hRequest.getRemoteHost());
            reqInfo.setHostAddress(hRequest.getRemoteAddr());

            if (DEBUG)
            {
                DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            }

            try
            {
                Server targetServer = new Server();
                targetServer.setOperHostName(request.getTargetServer());

                if (DEBUG)
                {
                    DEBUGGER.debug("Server: {}", targetServer);
                }

                ServerManagementRequest serverReq = new ServerManagementRequest();
                serverReq.setServiceId(this.serviceName);
                serverReq.setTargetServer(targetServer);
                serverReq.setRequestType(SystemCheckType.TELNET);
                serverReq.setPortNumber(request.getTargetPort());
                serverReq.setRequestInfo(reqInfo);
                serverReq.setUserAccount(userAccount);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServerManagementRequest: {}", serverReq);
                }

                // TODO: this needs to change
                if ((StringUtils.isNotEmpty(request.getSourceServer()) || (StringUtils.equals(request.getSourceServer(), appConfig.getHostname()))))
                {
                    Server sourceServer = new Server();
                    sourceServer.setOperHostName(request.getSourceServer());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("SourceServer: {}", sourceServer);
                    }

                    serverReq.setSourceServer(sourceServer);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("ServerManagementRequest: {}", serverReq);
                    }
                }

                ServerManagementResponse response = processor.runTelnetCheck(serverReq);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServerManagementResponse: {}", response);
                }

                if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    // all set
                    mView.addObject(Constants.RESPONSE_MESSAGE, response.getResponse());
                }
                else
                {
                    // nooo
                    mView.addObject(Constants.ERROR_MESSAGE, response.getResponse());
                }

                // regardless of what happens we still allow the user to
                // make the request
                mView.addObject("command", new ManagementRequest());
                mView.setViewName(this.testTelnetPage);
            }
            catch (ServerManagementException smx)
            {
                ERROR_RECORDER.error(smx.getMessage(), smx);

                mView.addObject(Constants.ERROR_MESSAGE, this.errorResponse);
                mView.addObject("command", new ManagementRequest());
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
    public final ModelAndView runRemoteDate(@ModelAttribute("request") final ManagementRequest request, final BindingResult binding)
    {
        final String methodName = SystemCheckController.CNAME + "#runRemoteDate(@ModelAttribute(\"request\") final ManagementRequest request, final BindingResult binding)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ManagementRequest: {}", request);
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

        if (userAccount.getStatus() == LoginStatus.EXPIRED)
        {
            // redirect to password page
            mView = new ModelAndView(new RedirectView());
            mView.setViewName(appConfig.getExpiredRedirect());
            mView.addObject(Constants.ERROR_MESSAGE, Constants.PASSWORD_EXPIRED);

            if (DEBUG)
            {
                DEBUGGER.debug("ModelAndView: {}", mView);
            }

            return mView;
        }

        if (appConfig.getServices().get(this.serviceName))
        {
            RequestHostInfo reqInfo = new RequestHostInfo();
            reqInfo.setHostName(hRequest.getRemoteHost());
            reqInfo.setHostAddress(hRequest.getRemoteAddr());

            if (DEBUG)
            {
                DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            }

            try
            {
                // a source server is *required*
                Server sourceServer = new Server();
                sourceServer.setOperHostName(request.getSourceServer());

                if (DEBUG)
                {
                    DEBUGGER.debug("Server: {}", sourceServer);
                }

                Server targetServer = new Server();
                targetServer.setOperHostName(request.getTargetServer());

                if (DEBUG)
                {
                    DEBUGGER.debug("Server: {}", targetServer);
                }

                ServerManagementRequest serverReq = new ServerManagementRequest();
                serverReq.setServiceId(this.serviceName);
                serverReq.setTargetServer(targetServer);
                serverReq.setRequestType(SystemCheckType.REMOTEDATE);
                serverReq.setRequestInfo(reqInfo);
                serverReq.setUserAccount(userAccount);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServerManagementRequest: {}", serverReq);
                }

                ServerManagementResponse response = processor.runRemoteDateCheck(serverReq);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServerManagementResponse: {}", response);
                }

                mView.addObject("command", new ManagementRequest());

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

                    mView.addObject(Constants.RESPONSE_MESSAGE, responseMessage);
                }
                else
                {
                    // nooo
                    mView.addObject(Constants.ERROR_MESSAGE, response.getResponse());
                }

                // regardless of what happens we still allow the user to
                // make the request
                mView.addObject("command", new ManagementRequest());
                mView.setViewName(this.remoteDatePage);
            }
            catch (IOException iox)
            {
                ERROR_RECORDER.error(iox.getMessage(), iox);

                mView.addObject(Constants.ERROR_MESSAGE, this.errorResponse);
                mView.addObject("command", new ManagementRequest());
                mView.setViewName(this.remoteDatePage);
            }
            catch (ServerManagementException smx)
            {
                ERROR_RECORDER.error(smx.getMessage(), smx);

                mView.addObject(Constants.ERROR_MESSAGE, this.errorResponse);
                mView.addObject("command", new ManagementRequest());
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
}
