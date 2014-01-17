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
package com.cws.esolutions.web.controllers;

import java.util.List;
import org.slf4j.Logger;
import java.util.Enumeration;
import org.slf4j.LoggerFactory;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.cws.esolutions.web.Constants;
import com.cws.esolutions.web.dto.ServerRequest;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.web.ApplicationServiceBean;
import com.cws.esolutions.core.processors.dto.Server;
import com.cws.esolutions.core.processors.dto.Service;
import com.cws.esolutions.web.validators.ServerValidator;
import com.cws.esolutions.core.processors.enums.ServerType;
import com.cws.esolutions.core.processors.enums.ServerStatus;
import com.cws.esolutions.core.processors.enums.ServiceRegion;
import com.cws.esolutions.core.processors.dto.SystemCheckRequest;
import com.cws.esolutions.core.processors.enums.NetworkPartition;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.core.processors.dto.ServerManagementRequest;
import com.cws.esolutions.core.processors.dto.ServerManagementResponse;
import com.cws.esolutions.core.processors.dto.ServiceManagementRequest;
import com.cws.esolutions.core.processors.dto.ServiceManagementResponse;
import com.cws.esolutions.core.processors.impl.ServerManagementProcessorImpl;
import com.cws.esolutions.core.processors.impl.ServiceManagementProcessorImpl;
import com.cws.esolutions.core.processors.exception.ServerManagementException;
import com.cws.esolutions.core.processors.exception.ServiceManagementException;
import com.cws.esolutions.core.processors.interfaces.IServerManagementProcessor;
import com.cws.esolutions.core.processors.interfaces.IServiceManagementProcessor;
/*
 * Project: eSolutions_java_source
 * Package: com.cws.esolutions.web.controllers
 * File: SystemManagementController.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
@Controller
@RequestMapping("/system-management")
public class SystemManagementController
{
    private int recordsPerPage = 20;
    private String dcService = null;
    private String serviceName = null;
    private String defaultPage = null;
    private String systemService = null;
    private String addServerPage = null;
    private String viewServerPage = null;
    private String adminConsolePage = null;
    private String addServerRedirect = null;
    private String messageNoDmgrsFound = null;
    private String addDatacenterRedirect = null;
    private List<String> availableDomains = null;
    private String messageAddServerSuccess = null;
    private ServerValidator serverValidator = null;
    private ApplicationServiceBean appConfig = null;

    private static final String CNAME = SystemManagementController.class.getName();
    private static final String ADD_SERVER_REDIRECT = "redirect:/ui/system-management/add-server";

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CNAME);

    public final void setSystemService(final String value)
    {
        final String methodName = SystemManagementController.CNAME + "#setSystemService(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.systemService = value;
    }

    public final void setServiceName(final String value)
    {
        final String methodName = SystemManagementController.CNAME + "#setServiceName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serviceName = value;
    }

    public final void setDefaultPage(final String value)
    {
        final String methodName = SystemManagementController.CNAME + "#setDefaultPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.defaultPage = value;
    }

    public final void setAddServerPage(final String value)
    {
        final String methodName = SystemManagementController.CNAME + "#setAddServerPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.addServerPage = value;
    }

    public final void setViewServerPage(final String value)
    {
        final String methodName = SystemManagementController.CNAME + "#setViewServerPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.viewServerPage = value;
    }

    public final void setMessageNoDmgrsFound(final String value)
    {
        final String methodName = SystemManagementController.CNAME + "#setMessageNoDmgrsFound(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageNoDmgrsFound = value;
    }

    public final void setAddServerRedirect(final String value)
    {
        final String methodName = SystemManagementController.CNAME + "#setAddServerRedirect(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.addServerRedirect = value;
    }

    public final void setAdminConsolePage(final String value)
    {
        final String methodName = SystemManagementController.CNAME + "#setAdminConsolePage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.adminConsolePage = value;
    }

    public final void setAppConfig(final ApplicationServiceBean value)
    {
        final String methodName = SystemManagementController.CNAME + "#setAppConfig(final ApplicationServiceBean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.appConfig = value;
    }

    public final void setDcService(final String value)
    {
        final String methodName = SystemManagementController.CNAME + "#setDcService(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.dcService = value;
    }

    public final void setRecordsPerPage(final int value)
    {
        final String methodName = SystemManagementController.CNAME + "#setRecordsPerPage(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.recordsPerPage = value;
    }

    public final void setServerValidator(final ServerValidator value)
    {
        final String methodName = SystemManagementController.CNAME + "#setServerValidator(final ServerValidator value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serverValidator = value;
    }

    public final void setAvailableDomains(final List<String> value)
    {
        final String methodName = SystemManagementController.CNAME + "#setAvailableDomains(final List<String> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.availableDomains = value;
    }

    public final void setAddDatacenterRedirect(final String value)
    {
        final String methodName = SystemManagementController.CNAME + "#setAddDatacenterRedirect(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.addDatacenterRedirect = value;
    }

    public final void setMessageAddServerSuccess(final String value)
    {
        final String methodName = SystemManagementController.CNAME + "#setMessageAddServerSuccess(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageAddServerSuccess = value;
    }

    @RequestMapping(value = "/default", method = RequestMethod.GET)
    public final ModelAndView showDefaultPage()
    {
        final String methodName = SystemManagementController.CNAME + "#showDefaultPage()";

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
            Enumeration<?> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String element = (String) sessionEnumeration.nextElement();
                Object value = hSession.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<?> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = (String) requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<?> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String element = (String) paramsEnumeration.nextElement();
                Object value = hRequest.getParameter(element);

                DEBUGGER.debug("Parameter: {}; Value: {}", element, value);
            }
        }

        if (this.appConfig.getServices().get(this.serviceName))
        {
            mView.addObject("command", new Server());
            mView.setViewName(this.defaultPage);
        }
        else
        {
            mView.setViewName(this.appConfig.getUnavailablePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/search/terms/{terms}/page/{page}", method = RequestMethod.GET)
    public final ModelAndView showSearchPage(@PathVariable("terms") final String terms, @PathVariable("page") final int page)
    {
        final String methodName = SystemManagementController.CNAME + "#showSearchPage(@PathVariable(\"terms\") final String terms, @PathVariable(\"page\") final int page)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", terms);
            DEBUGGER.debug("Value: {}", page);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        IServerManagementProcessor processor = new ServerManagementProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            Enumeration<?> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String element = (String) sessionEnumeration.nextElement();
                Object value = hSession.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<?> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = (String) requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<?> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String element = (String) paramsEnumeration.nextElement();
                Object value = hRequest.getParameter(element);

                DEBUGGER.debug("Parameter: {}; Value: {}", element, value);
            }
        }

        if (this.appConfig.getServices().get(this.serviceName))
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

                ServerManagementRequest request = new ServerManagementRequest();
                request.setApplicationId(this.appConfig.getApplicationId());
                request.setApplicationName(this.appConfig.getApplicationName());
                request.setAttribute(terms);
                request.setRequestInfo(reqInfo);
                request.setServiceId(this.systemService);
                request.setUserAccount(userAccount);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServerManagementRequest: {}", request);
                }

                ServerManagementResponse response = processor.listServersByAttribute(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServerManagementResponse: {}", response);
                }

                if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    mView.addObject("pages", (int) Math.ceil(response.getEntryCount() * 1.0 / this.recordsPerPage));
                    mView.addObject("page", page);
                    mView.addObject("searchTerms", terms);
                    mView.addObject(Constants.SEARCH_RESULTS, response.getServerList());
                    mView.addObject("command", new Server());
                    mView.setViewName(this.defaultPage);
                }
                else if (response.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                {
                    mView.setViewName(this.appConfig.getUnauthorizedPage());

                    return mView;
                }
                else
                {
                    mView.setViewName(this.appConfig.getErrorResponsePage());

                    return mView;
                }
            }
            catch (ServerManagementException smx)
            {
                ERROR_RECORDER.error(smx.getMessage(), smx);

                mView.setViewName(this.appConfig.getErrorResponsePage());

                return mView;
            }
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/service-consoles", method = RequestMethod.GET)
    public final ModelAndView showAdminConsoles()
    {
        final String methodName = SystemManagementController.CNAME + "#showAdminConsoles()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IServerManagementProcessor serverMgr = new ServerManagementProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            Enumeration<?> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String element = (String) sessionEnumeration.nextElement();
                Object value = hSession.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<?> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = (String) requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<?> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String element = (String) paramsEnumeration.nextElement();
                Object value = hRequest.getParameter(element);

                DEBUGGER.debug("Parameter: {}; Value: {}", element, value);
            }
        }

        if (this.appConfig.getServices().get(this.serviceName))
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

                ServerManagementRequest serviceReq = new ServerManagementRequest();
                serviceReq.setRequestInfo(reqInfo);
                serviceReq.setUserAccount(userAccount);
                serviceReq.setServiceId(this.systemService);
                serviceReq.setApplicationId(this.appConfig.getApplicationId());
                serviceReq.setApplicationName(this.appConfig.getApplicationName());
                serviceReq.setAttribute(ServerType.DMGRSERVER.name());

                if (DEBUG)
                {
                    DEBUGGER.debug("ServerManagementRequest: {}", serviceReq);
                }

                ServerManagementResponse response = serverMgr.listServersByAttribute(serviceReq);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServerManagementResponse: {}", response);
                }

                if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    List<Server> serverList = response.getServerList();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("serverList: {}", serverList);
                    }

                    if ((serverList != null) && (serverList.size() != 0))
                    {
                        mView.addObject("serverList", serverList);
                        mView.setViewName(this.adminConsolePage);
                    }
                    else
                    {
                        mView = new ModelAndView(new RedirectView());
                        mView.addObject(Constants.MESSAGE_RESPONSE, this.messageNoDmgrsFound);
                        mView.setViewName(this.addServerRedirect);
                    }
                }
                else if (response.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                {
                    mView.setViewName(this.appConfig.getUnauthorizedPage());

                    return mView;
                }
                else
                {
                    mView.setViewName(this.appConfig.getErrorResponsePage());

                    return mView;
                }
            }
            catch (ServerManagementException smx)
            {
                ERROR_RECORDER.error(smx.getMessage(), smx);

                mView.setViewName(this.appConfig.getErrorResponsePage());

                return mView;
            }
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/server/{serverGuid}", method = RequestMethod.GET)
    public final ModelAndView showServerDetail(@PathVariable("serverGuid") final String serverGuid)
    {
        final String methodName = SystemManagementController.CNAME + "#showServerDetail(@PathVariable(\"serverGuid\") final String serverGuid)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("serverName: {}", serverGuid);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IServerManagementProcessor serverMgr = new ServerManagementProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            Enumeration<?> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String element = (String) sessionEnumeration.nextElement();
                Object value = hSession.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<?> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = (String) requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<?> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String element = (String) paramsEnumeration.nextElement();
                Object value = hRequest.getParameter(element);

                DEBUGGER.debug("Parameter: {}; Value: {}", element, value);
            }
        }

        if (this.appConfig.getServices().get(this.serviceName))
        {
            try
            {
                RequestHostInfo reqInfo = new RequestHostInfo();
                reqInfo.setHostAddress(hRequest.getRemoteAddr());
                reqInfo.setHostName(hRequest.getRemoteHost());
                reqInfo.setSessionId(hSession.getId());

                if (DEBUG)
                {
                    DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
                }

                Server target = new Server();
                target.setServerGuid(serverGuid);

                if (DEBUG)
                {
                    DEBUGGER.debug("Server: {}", target);
                }

                ServerManagementRequest guidRequest = new ServerManagementRequest();
                guidRequest.setRequestInfo(reqInfo);
                guidRequest.setUserAccount(userAccount);
                guidRequest.setServiceId(this.systemService);
                guidRequest.setTargetServer(target);
                guidRequest.setApplicationId(this.appConfig.getApplicationId());
                guidRequest.setApplicationName(this.appConfig.getApplicationName());

                if (DEBUG)
                {
                    DEBUGGER.debug("ServerManagementRequest: {}", guidRequest);
                }

                ServerManagementResponse guidResponse = serverMgr.getServerData(guidRequest);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServerManagementResponse: {}", guidResponse);
                }

                if (guidResponse.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    // yay
                    mView.addObject("server", guidResponse.getServer());
                    mView.setViewName(this.viewServerPage);
                }
                else if (guidResponse.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                {
                    mView.setViewName(this.appConfig.getUnauthorizedPage());
                }
                else
                {
                    ServerManagementRequest hostRequest = new ServerManagementRequest();
                    hostRequest.setRequestInfo(reqInfo);
                    hostRequest.setUserAccount(userAccount);
                    hostRequest.setServiceId(this.systemService);
                    hostRequest.setTargetServer(target);
                    hostRequest.setApplicationId(this.appConfig.getApplicationId());
                    hostRequest.setApplicationName(this.appConfig.getApplicationName());
                    hostRequest.setAttribute(serverGuid);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("ServerManagementRequest: {}", hostRequest);
                    }

                    ServerManagementResponse hostResponse = serverMgr.listServersByAttribute(hostRequest);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("ServerManagementResponse: {}", hostResponse);
                    }

                    if (hostResponse.getRequestStatus() == CoreServicesStatus.SUCCESS)
                    {
                        // yay
                        mView.addObject("statusList", ServerStatus.values());
                        mView.addObject("server", hostResponse.getServer());
                        mView.setViewName(this.viewServerPage);
                    }
                    else if (hostResponse.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                    {
                        mView.setViewName(this.appConfig.getUnauthorizedPage());

                        return mView;
                    }
                    else
                    {
                        mView.setViewName(this.appConfig.getErrorResponsePage());

                        return mView;
                    }
                }
            }
            catch (ServerManagementException smx)
            {
                ERROR_RECORDER.error(smx.getMessage(), smx);

                mView.setViewName(this.appConfig.getErrorResponsePage());

                return mView;
            }
        }
        else
        {
            mView.setViewName(this.appConfig.getUnavailablePage());

            return mView;
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/server-control", method = RequestMethod.GET)
    public final ModelAndView showServerControl()
    {
        final String methodName = SystemManagementController.CNAME + "#showServerControl()";

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
            Enumeration<?> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String element = (String) sessionEnumeration.nextElement();
                Object value = hSession.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<?> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = (String) requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<?> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String element = (String) paramsEnumeration.nextElement();
                Object value = hRequest.getParameter(element);

                DEBUGGER.debug("Parameter: {}; Value: {}", element, value);
            }
        }

        if (this.appConfig.getServices().get(this.serviceName))
        {
            // TODO
            mView.setViewName(this.defaultPage);
        }
        else
        {
            mView.setViewName(this.appConfig.getUnavailablePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/add-server", method = RequestMethod.GET)
    public final ModelAndView showAddNewServer()
    {
        final String methodName = SystemManagementController.CNAME + "#showAddNewServer()";

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
        final IServiceManagementProcessor svcProcessor = new ServiceManagementProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            Enumeration<?> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String element = (String) sessionEnumeration.nextElement();
                Object value = hSession.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<?> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = (String) requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<?> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String element = (String) paramsEnumeration.nextElement();
                Object value = hRequest.getParameter(element);

                DEBUGGER.debug("Parameter: {}; Value: {}", element, value);
            }
        }

        if (this.appConfig.getServices().get(this.serviceName))
        {
            RequestHostInfo reqInfo = new RequestHostInfo();
            reqInfo.setHostName(hRequest.getRemoteHost());
            reqInfo.setHostAddress(hRequest.getRemoteAddr());
            reqInfo.setSessionId(hSession.getId());
    
            if (DEBUG)
            {
                DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            }

            ServerManagementRequest dmgrRequest = new ServerManagementRequest();
            dmgrRequest.setRequestInfo(reqInfo);
            dmgrRequest.setUserAccount(userAccount);
            dmgrRequest.setServiceId(this.systemService);
            dmgrRequest.setApplicationId(this.appConfig.getApplicationId());
            dmgrRequest.setApplicationName(this.appConfig.getApplicationName());
            dmgrRequest.setAttribute(ServerType.DMGRSERVER.name());

            try
            {
                ServerManagementResponse dmgrResponse = processor.listServersByAttribute(dmgrRequest);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServerManagementResponse: {}", dmgrResponse);
                }

                if (dmgrResponse.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    List<Server> dmgrServers = dmgrResponse.getServerList();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("List<Server>: {}", dmgrServers);
                    }

                    mView.addObject("dmgrServers", dmgrServers);
                }
                else if (dmgrResponse.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                {
                    mView.setViewName(this.appConfig.getUnauthorizedPage());

                    return mView;
                }
            }
            catch (ServerManagementException smx)
            {
                ERROR_RECORDER.error(smx.getMessage(), smx);
            }

            // list datacenters
            ServiceManagementRequest dcRequest = new ServiceManagementRequest();
            dcRequest.setRequestInfo(reqInfo);
            dcRequest.setServiceId(this.dcService);
            dcRequest.setUserAccount(userAccount);
            dcRequest.setApplicationId(this.appConfig.getApplicationId());
            dcRequest.setApplicationName(this.appConfig.getApplicationName());

            if (DEBUG)
            {
                DEBUGGER.debug("DatacenterManagementRequest: {}", dcRequest);
            }

            try
            {
                ServiceManagementResponse dcResponse = svcProcessor.listServices(dcRequest);

                if (DEBUG)
                {
                    DEBUGGER.debug("DatacenterManagementResponse: {}", dcResponse);
                }

                if (dcResponse.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    List<Service> services = dcResponse.getServiceList();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("List<Service>: {}", services);
                    }

                    mView.addObject("datacenters", services);
                }
                else if (dcResponse.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                {
                    mView.setViewName(this.appConfig.getUnauthorizedPage());

                    return mView;
                }
                else
                {
                    // redirect to add datacenter
                    mView = new ModelAndView(new RedirectView());
                    mView.setViewName(this.addDatacenterRedirect);

                    return mView;
                }
            }
            catch (ServiceManagementException smx)
            {
                ERROR_RECORDER.error(smx.getMessage(), smx);

                // redirect to add datacenter
                mView = new ModelAndView(new RedirectView());
                mView.setViewName(this.addDatacenterRedirect);

                return mView;
            }

            mView.addObject("domainList", this.availableDomains);
            mView.addObject("serverTypes", ServerType.values());
            mView.addObject("serverStatuses", ServerStatus.values());
            mView.addObject("serverRegions", ServiceRegion.values());
            mView.addObject("networkPartitions", NetworkPartition.values());
            mView.addObject("command", new ServerRequest());
            mView.setViewName(this.addServerPage);
        }
        else
        {
            mView.setViewName(this.appConfig.getUnavailablePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/add-server", method = RequestMethod.POST)
    public final ModelAndView addNewServer(@ModelAttribute("request") final ServerRequest request, final BindingResult binding)
    {
        final String methodName = SystemManagementController.CNAME + "#addNewServer(@ModelAttribute(\"request\") final ServerRequest request, final BindingResult binding)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ServerRequest: {}", request);
            DEBUGGER.debug("BindingResult: {}", binding);
        }

        ModelAndView mView = new ModelAndView(new RedirectView());

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IServerManagementProcessor serverMgr = new ServerManagementProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            Enumeration<?> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String element = (String) sessionEnumeration.nextElement();
                Object value = hSession.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<?> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = (String) requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<?> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String element = (String) paramsEnumeration.nextElement();
                Object value = hRequest.getParameter(element);

                DEBUGGER.debug("Parameter: {}; Value: {}", element, value);
            }
        }

        if (this.appConfig.getServices().get(this.serviceName))
        {
            this.serverValidator.validate(request, binding);

            if (binding.hasErrors())
            {
                ERROR_RECORDER.error("Request failed validation: {}", binding.getAllErrors());

                // send back to page
                mView.addObject(Constants.ERROR_MESSAGE, this.appConfig.getMessageValidationFailed());
                mView.setViewName(this.addServerPage);

                return mView;
            }

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
                server.setVirtualId(request.getVirtualId());
                server.setOsName(request.getOsName());
                server.setOperIpAddress(request.getOperIpAddress());
                server.setOperHostName(request.getOperHostName());
                server.setMgmtIpAddress(request.getMgmtIpAddress());
                server.setMgmtHostName(request.getMgmtHostName());
                server.setBkIpAddress(request.getBkIpAddress());
                server.setBkHostName(request.getBkHostName());
                server.setNasIpAddress(request.getNasIpAddress());
                server.setNasHostName(request.getNasHostName());
                server.setNatAddress(request.getNatAddress());
                server.setServerStatus(request.getServerStatus());
                server.setServerType(request.getServerType());
                server.setServerComments(request.getServerComments());
                server.setCpuType(request.getCpuType());
                server.setCpuCount(request.getCpuCount());
                server.setServerRack(request.getServerRack());
                server.setServerModel(request.getServerModel());
                server.setRackPosition(request.getRackPosition());
                server.setSerialNumber(request.getSerialNumber());
                server.setInstalledMemory(request.getInstalledMemory());

                // figure out the type
                switch (request.getServerType())
                {
                    case APPSERVER:
                        // if its an application server, theres no location configured
                        // because its driven by the owning dmgr
                        Server dmgrServer = new Server();
                        dmgrServer.setServerGuid(request.getOwningDmgr());

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Server: {}", dmgrServer);
                        }

                        // find out what datacenter/partition/etc
                        ServerManagementRequest dmgrRequest = new ServerManagementRequest();
                        dmgrRequest.setRequestInfo(reqInfo);
                        dmgrRequest.setServiceId(this.systemService);
                        dmgrRequest.setTargetServer(dmgrServer);
                        dmgrRequest.setUserAccount(userAccount);
                        dmgrRequest.setApplicationId(this.appConfig.getApplicationId());
                        dmgrRequest.setApplicationName(this.appConfig.getApplicationName());

                        if (DEBUG)
                        {
                            DEBUGGER.debug("ServerManagementRequest: {}", dmgrRequest);
                        }

                        ServerManagementResponse dmgrResponse = serverMgr.getServerData(dmgrRequest);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("ServerManagementResponse: {}", dmgrResponse);
                        }

                        if (dmgrResponse.getRequestStatus() == CoreServicesStatus.SUCCESS)
                        {
                            Server dmgr = dmgrResponse.getServer();

                            if (DEBUG)
                            {
                                DEBUGGER.debug("Server: {}", dmgr);
                            }

                            // excellent
                            server.setOwningDmgr(dmgrServer);
                            server.setService(dmgr.getService());
                            server.setServerRegion(dmgr.getServerRegion());
                            server.setDomainName(dmgr.getDomainName());
                            server.setNetworkPartition(dmgr.getNetworkPartition());

                            break;
                        }
                        else if (dmgrResponse.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                        {
                            mView.setViewName(this.appConfig.getUnauthorizedPage());

                            return mView;
                        }
                        else
                        {
                            // no dmgr information found for the request
                            mView.addObject(Constants.ERROR_RESPONSE, this.messageNoDmgrsFound);
                            mView.setViewName(SystemManagementController.ADD_SERVER_REDIRECT);

                            return mView;
                        }
                    default:
                        Service datacenter = new Service();
                        datacenter.setGuid(request.getDatacenter());

                        if (DEBUG)
                        {
                            DEBUGGER.debug("DataCenter: {}", datacenter);
                        }

                        server.setServerRegion(request.getServerRegion());
                        server.setDomainName(request.getDomainName());
                        server.setDmgrPort(request.getDmgrPort());
                        server.setMgrUrl(request.getMgrUrl());
                        server.setNetworkPartition(request.getNetworkPartition());

                        break;
                }

                if (DEBUG)
                {
                    DEBUGGER.debug("Server: {}", server);
                }

                ServerManagementRequest serverReq = new ServerManagementRequest();
                serverReq.setRequestInfo(reqInfo);
                serverReq.setUserAccount(userAccount);
                serverReq.setServiceId(this.systemService);
                serverReq.setTargetServer(server);
                serverReq.setApplicationId(this.appConfig.getApplicationId());
                serverReq.setApplicationName(this.appConfig.getApplicationName());

                if (DEBUG)
                {
                    DEBUGGER.debug("ServerManagementRequest: {}", request);
                }

                ServerManagementResponse response = serverMgr.addNewServer(serverReq);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServerManagementResponse: {}", response);
                }

                if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    // all set
                    // at this point we should be kicking off a request to install the agent
                    /*ServerManagementRequest installRequest = new ServerManagementRequest();
                    installRequest.setInstallAgent(true);
                    installRequest.setRequestInfo(reqInfo);
                    installRequest.setUserAccount(userAccount);
                    installRequest.setTargetServer(request);
                    installRequest.setServiceId(this.systemService);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("ServerManagementRequest: {}", installRequest);
                    }

                    serverMgr.installSoftwarePackage(installRequest);*/ // we are NOT waiting for a response here

                    mView.addObject(Constants.RESPONSE_MESSAGE, this.messageAddServerSuccess);
                    mView.setViewName(SystemManagementController.ADD_SERVER_REDIRECT);
                }
                else if (response.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                {
                    mView.setViewName(this.appConfig.getUnauthorizedPage());

                    return mView;
                }
                else
                {
                    mView.setViewName(this.appConfig.getErrorResponsePage());

                    return mView;
                }
            }
            catch (ServerManagementException smx)
            {
                ERROR_RECORDER.error(smx.getMessage(), smx);

                mView.setViewName(this.appConfig.getErrorResponsePage());

                return mView;
            }
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/server-control", method = RequestMethod.POST)
    public final ModelAndView runServerControlOperation(@ModelAttribute("request") final SystemCheckRequest request, final BindingResult binding)
    {
        final String methodName = SystemManagementController.CNAME + "#runServerControlOperation(@ModelAttribute(\"request\") final SystemCheckRequest request, final BindingResult binding)";

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

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            Enumeration<?> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String element = (String) sessionEnumeration.nextElement();
                Object value = hSession.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<?> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = (String) requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<?> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String element = (String) paramsEnumeration.nextElement();
                Object value = hRequest.getParameter(element);

                DEBUGGER.debug("Parameter: {}; Value: {}", element, value);
            }
        }

        if (this.appConfig.getServices().get(this.serviceName))
        {
            RequestHostInfo reqInfo = new RequestHostInfo();
            reqInfo.setHostName(hRequest.getRemoteHost());
            reqInfo.setHostAddress(hRequest.getRemoteAddr());
            reqInfo.setSessionId(hSession.getId());

            if (DEBUG)
            {
                DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            }
        }
        else
        {
            mView.setViewName(this.appConfig.getUnavailablePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public final ModelAndView doServerSearch(@ModelAttribute("request") final Server server, final BindingResult bindResult)
    {
        final String methodName = SystemManagementController.CNAME + "#doServerSearch(@ModelAttribute(\"request\") final Server server, final BindingResult bindResult)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", server);
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
            Enumeration<?> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String element = (String) sessionEnumeration.nextElement();
                Object value = hSession.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<?> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = (String) requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<?> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String element = (String) paramsEnumeration.nextElement();
                Object value = hRequest.getParameter(element);

                DEBUGGER.debug("Parameter: {}; Value: {}", element, value);
            }
        }

        if (this.appConfig.getServices().get(this.serviceName))
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

                ServerManagementRequest request = new ServerManagementRequest();
                request.setApplicationId(this.appConfig.getApplicationId());
                request.setApplicationName(this.appConfig.getApplicationName());
                request.setAttribute(server.getOperHostName());
                request.setRequestInfo(reqInfo);
                request.setServiceId(this.systemService);
                request.setUserAccount(userAccount);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServerManagementRequest: {}", request);
                }

                ServerManagementResponse response = processor.listServersByAttribute(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServerManagementResponse: {}", response);
                }

                if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    mView.addObject("pages", (int) Math.ceil(response.getEntryCount() * 1.0 / this.recordsPerPage));
                    mView.addObject("page", 1);
                    mView.addObject("searchTerms", server.getOperHostName());
                    mView.addObject(Constants.SEARCH_RESULTS, response.getServerList());
                    mView.addObject("command", new Server());
                    mView.setViewName(this.defaultPage);
                }
                else if (response.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                {
                    mView.setViewName(this.appConfig.getUnauthorizedPage());

                    return mView;
                }
                else
                {
                    mView.setViewName(this.appConfig.getErrorResponsePage());

                    return mView;
                }
            }
            catch (ServerManagementException smx)
            {
                ERROR_RECORDER.error(smx.getMessage(), smx);

                mView.setViewName(this.appConfig.getErrorResponsePage());

                return mView;
            }
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }
}
