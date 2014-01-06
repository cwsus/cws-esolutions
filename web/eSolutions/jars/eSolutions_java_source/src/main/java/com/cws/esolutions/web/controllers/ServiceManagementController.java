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
import java.util.ArrayList;
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
import com.cws.esolutions.web.dto.PlatformRequest;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.core.processors.dto.Server;
import com.cws.esolutions.web.ApplicationServiceBean;
import com.cws.esolutions.core.processors.dto.Service;
import com.cws.esolutions.web.validators.ServiceValidator;
import com.cws.esolutions.core.processors.enums.ServiceType;
import com.cws.esolutions.core.processors.enums.ServiceStatus;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.core.processors.dto.ServiceManagementRequest;
import com.cws.esolutions.core.processors.dto.ServiceManagementResponse;
import com.cws.esolutions.core.processors.impl.ServiceManagementProcessorImpl;
import com.cws.esolutions.core.processors.exception.ServiceManagementException;
import com.cws.esolutions.core.processors.interfaces.IServiceManagementProcessor;
/*
 * Project: eSolutions_java_source
 * Package: com.cws.esolutions.web.controllers
 * File: ServiceManagementController.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
@Controller
@RequestMapping("/service-management")
public class ServiceManagementController
{
    private int recordsPerPage = 20; // default to 20
    private String serviceId = null;
    private String projectMgmt = null;
    private String serviceName = null;
    private String defaultPage = null;
    private String platformMgmt = null;
    private String addPlatformPage = null;
    private String viewPlatformPage = null;
    private String viewPlatformList = null;
    private String addDatacenterPage = null;
    private String viewDatacenterPage = null;
    private String viewDatacentersPage = null;
    private String addPlatformRedirect = null;
    private String messageNoDatacenters = null;
    private String addDatacenterRedirect = null;
    private String messagePlatformAddSuccess = null;
    private String messagePlatformAddFailure = null;
    private ApplicationServiceBean appConfig = null;
    private ServiceValidator serviceValidator = null;
    private String messageDatacenterAddSuccess = null;
    private String messageDatacenterAddFailure = null;
    
    private static final String CNAME = ServiceManagementController.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CNAME);

    public final void setAppConfig(final ApplicationServiceBean value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setAppConfig(final ApplicationServiceBean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.appConfig = value;
    }

    public final void setServiceValidator(final ServiceValidator value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setServiceValidator(final ServiceValidator value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serviceValidator = value;
    }

    public final void setServiceName(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setServiceName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serviceName = value;
    }

    public final void setServiceId(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setServiceId(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serviceId = value;
    }

    public final void setAddPlatformRedirect(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setAddPlatformRedirect(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.addPlatformRedirect = value;
    }

    public final void setAddDatacenterRedirect(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setAddDatacenterRedirect(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.addDatacenterRedirect = value;
    }

    public final void setDefaultPage(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setDefaultPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.defaultPage = value;
    }

    public final void setAddPlatformPage(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setAddPlatformPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.addPlatformPage = value;
    }

    public final void setViewPlatformPage(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setViewPlatformPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.viewPlatformPage = value;
    }

    public final void setViewDatacentersPage(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setViewDatacentersPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.viewDatacentersPage = value;
    }

    public final void setViewDatacenterPage(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setViewDatacenterPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.viewDatacenterPage = value;
    }

    public final void setViewPlatformList(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setViewPlatformList(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.viewPlatformList = value;
    }

    public final void setAddDatacenterPage(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setAddDatacenterPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.addDatacenterPage = value;
    }

    public final void setPlatformMgmt(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setPlatformMgmt(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.platformMgmt = value;
    }

    public final void setRecordsPerPage(final int value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setRecordsPerPage(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.recordsPerPage = value;
    }

    public final void setMessageNoDatacenters(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setMessageNoDatacenters(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageNoDatacenters = value;
    }

    public final void setMessagePlatformAddSuccess(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setMessagePlatformAddSuccess(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messagePlatformAddSuccess = value;
    }

    public final void setMessagePlatformAddFailure(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setMessagePlatformAddFailure(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messagePlatformAddFailure = value;
    }

    public final void setMessageDatacenterAddSuccess(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setMessageDatacenterAddSuccess(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageDatacenterAddSuccess = value;
    }

    public final void setMessageDatacenterAddFailure(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setMessageDatacenterAddFailure(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageDatacenterAddFailure = value;
    }

    @RequestMapping(value = "/default", method = RequestMethod.GET)
    public final ModelAndView showDefaultPage()
    {
        final String methodName = ServiceManagementController.CNAME + "#showDefaultPage()";

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
                String element = sessionEnumeration.nextElement();
                Object value = hSession.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String element = paramsEnumeration.nextElement();
                Object value = hRequest.getParameter(element);

                DEBUGGER.debug("Parameter: {}; Value: {}", element, value);
            }
        }

        if (this.appConfig.getServices().get(this.serviceName))
        {
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

    @RequestMapping(value = "/search/terms/{terms}/type/{type}/page/{page}", method = RequestMethod.GET)
    public final ModelAndView showSearchPage(@PathVariable("terms") final String terms, @PathVariable("type") final String type, @PathVariable("page") final int page)
    {
        final String methodName = ServiceManagementController.CNAME + "#showSearchPage(@PathVariable(\"terms\") final String terms, @PathVariable(\"type\") final String type, @PathVariable(\"page\") final int page)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", terms);
            DEBUGGER.debug("Value: {}", type);
            DEBUGGER.debug("Value: {}", page);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IServiceManagementProcessor processor = new ServiceManagementProcessorImpl();

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
                String element = sessionEnumeration.nextElement();
                Object value = hSession.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String element = paramsEnumeration.nextElement();
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

                Service service = new Service();
                service.setName(terms);

                if (DEBUG)
                {
                    DEBUGGER.debug("Service: {}", service);
                }

                ServiceManagementRequest request = new ServiceManagementRequest();
                request.setApplicationId(this.appConfig.getApplicationId());
                request.setApplicationName(this.appConfig.getApplicationName());
                request.setRequestInfo(reqInfo);
                request.setService(service);
                request.setServiceId(this.serviceId);
                request.setStartPage(page);
                request.setUserAccount(userAccount);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServiceManagementRequest: {}", request);
                }

                ServiceManagementResponse response = processor.getServiceByAttribute(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServiceManagementResponse: {}", response);
                }

                if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    mView.addObject("pages", (int) Math.ceil(response.getEntryCount() * 1.0 / this.recordsPerPage));
                    mView.addObject("page", page);
                    mView.addObject("searchTerms", terms);
                    mView.addObject("searchType", type);
                    mView.addObject(Constants.SEARCH_RESULTS, response.getServiceList());
                    mView.addObject("command", new Service());
                    mView.setViewName(this.defaultPage);
                }
                else if (response.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                {
                    mView.setViewName(this.appConfig.getUnauthorizedPage());
                }
                else
                {
                    mView.addObject(Constants.ERROR_RESPONSE, this.appConfig.getMessageNoSearchResults());
                    mView.addObject("command", new Service());
                    mView.setViewName(this.defaultPage);
                }
            }
            catch (ServiceManagementException smx)
            {
                ERROR_RECORDER.error(smx.getMessage(), smx);

                mView.setViewName(this.appConfig.getErrorResponsePage());
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

    @RequestMapping(value = "/list-platforms", method = RequestMethod.GET)
    public final ModelAndView showPlatformList()
    {
        final String methodName = ServiceManagementController.CNAME + "#showPlatformList()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IServiceManagementProcessor processor = new ServiceManagementProcessorImpl();

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
                String element = sessionEnumeration.nextElement();
                Object value = hSession.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String element = paramsEnumeration.nextElement();
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

                Service service = new Service();
                service.setType(ServiceType.PLATFORM);

                if (DEBUG)
                {
                    DEBUGGER.debug("Service: {}", service);
                }

                ServiceManagementRequest request = new ServiceManagementRequest();
                request.setRequestInfo(reqInfo);
                request.setUserAccount(userAccount);
                request.setServiceId(this.platformMgmt);
                request.setApplicationId(this.appConfig.getApplicationId());
                request.setApplicationName(this.appConfig.getApplicationName());
                request.setService(service);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServiceManagementRequest: {}", request);
                }

                ServiceManagementResponse response = processor.listServicesByType(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServiceManagementResponse: {}", response);
                }

                if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    List<Service> platformList = response.getServiceList();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("platformList: {}", platformList);
                    }

                    mView.addObject("pages", (int) Math.ceil(response.getEntryCount() * 1.0 / this.recordsPerPage));
                    mView.addObject("page", 1);
                    mView.addObject("platformList", platformList);
                    mView.setViewName(this.viewPlatformList);
                }
                else if (response.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                {
                    mView.setViewName(this.appConfig.getUnauthorizedPage());
                }
                else
                {
                    mView = new ModelAndView(new RedirectView());
                    mView.addObject(Constants.ERROR_RESPONSE, this.appConfig.getMessageNoSearchResults());
                    mView.setViewName(this.addPlatformRedirect);
                }
            }
            catch (ServiceManagementException pmx)
            {
                ERROR_RECORDER.error(pmx.getMessage(), pmx);

                mView.setViewName(this.appConfig.getErrorResponsePage());
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

    @RequestMapping(value = "/list-platforms/page/{page}", method = RequestMethod.GET)
    public final ModelAndView showPlatformList(@PathVariable("page") final int page)
    {
        final String methodName = ServiceManagementController.CNAME + "#showPlatformList(@PathVariable(\"page\") final int page)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IServiceManagementProcessor processor = new ServiceManagementProcessorImpl();

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
                String element = sessionEnumeration.nextElement();
                Object value = hSession.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String element = paramsEnumeration.nextElement();
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

                Service service = new Service();
                service.setType(ServiceType.PLATFORM);

                if (DEBUG)
                {
                    DEBUGGER.debug("Service: {}", service);
                }

                ServiceManagementRequest request = new ServiceManagementRequest();
                request.setRequestInfo(reqInfo);
                request.setUserAccount(userAccount);
                request.setServiceId(this.platformMgmt);
                request.setApplicationId(this.appConfig.getApplicationId());
                request.setApplicationName(this.appConfig.getApplicationName());
                request.setStartPage((page - 1) * this.recordsPerPage);
                request.setService(service);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServiceManagementRequest: {}", request);
                }

                ServiceManagementResponse response = processor.listServicesByType(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServiceManagementResponse: {}", response);
                }

                if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    List<Service> platformList = response.getServiceList();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("platformList: {}", platformList);
                    }

                    mView.addObject("pages", (int) Math.ceil(response.getEntryCount() * 1.0 / this.recordsPerPage));
                    mView.addObject("page", page);
                    mView.addObject("platformList", platformList);
                    mView.setViewName(this.viewPlatformList);
                }
                else if (response.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                {
                    mView.setViewName(this.appConfig.getUnauthorizedPage());
                }
                else
                {
                    mView.addObject(Constants.ERROR_RESPONSE, this.appConfig.getMessageNoSearchResults());
                    mView.setViewName(this.defaultPage);
                }
            }
            catch (ServiceManagementException pmx)
            {
                ERROR_RECORDER.error(pmx.getMessage(), pmx);

                mView.setViewName(this.appConfig.getErrorResponsePage());
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

    @RequestMapping(value = "/list-datacenters", method = RequestMethod.GET)
    public final ModelAndView showDatacenterList()
    {
        final String methodName = ServiceManagementController.CNAME + "#showDatacenterList()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IServiceManagementProcessor processor = new ServiceManagementProcessorImpl();

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
                String element = sessionEnumeration.nextElement();
                Object value = hSession.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String element = paramsEnumeration.nextElement();
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

                Service service = new Service();
                service.setType(ServiceType.DATACENTER);

                if (DEBUG)
                {
                    DEBUGGER.debug("Service: {}", service);
                }

                ServiceManagementRequest request = new ServiceManagementRequest();
                request.setRequestInfo(reqInfo);
                request.setUserAccount(userAccount);
                request.setServiceId(this.serviceId);
                request.setApplicationId(this.appConfig.getApplicationId());
                request.setApplicationName(this.appConfig.getApplicationName());
                request.setService(service);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServiceManagementRequest: {}", request);
                }

                ServiceManagementResponse response = processor.listServicesByType(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServiceManagementResponse: {}", response);
                }

                if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    List<Service> datacenterList = response.getServiceList();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("List<Service>: {}", datacenterList);
                    }

                    if ((datacenterList != null) && (datacenterList.size() != 0))
                    {
                        mView.addObject("pages", (int) Math.ceil(response.getEntryCount() * 1.0 / this.recordsPerPage));
                        mView.addObject("page", 1);
                        mView.addObject("datacenterList", datacenterList);
                        mView.setViewName(this.viewDatacentersPage);
                    }
                    else
                    {
                        mView.addObject(Constants.MESSAGE_RESPONSE, this.messageNoDatacenters);
                        mView.addObject("command", new Service());
                        mView.setViewName(this.addDatacenterPage);
                    }
                }
                else if (response.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                {
                    mView.setViewName(this.appConfig.getUnauthorizedPage());
                }
                else
                {
                    mView = new ModelAndView(new RedirectView());
                    mView.addObject(Constants.ERROR_RESPONSE, this.appConfig.getMessageNoSearchResults());
                    mView.setViewName(this.addDatacenterRedirect);
                }
            }
            catch (ServiceManagementException smx)
            {
                ERROR_RECORDER.error(smx.getMessage(), smx);

                mView.setViewName(this.appConfig.getErrorResponsePage());
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

    @RequestMapping(value = "/list-datacenters/page/{page}", method = RequestMethod.GET)
    public final ModelAndView showDataCenterList(@PathVariable("page") final int page)
    {
        final String methodName = ServiceManagementController.CNAME + "#showDatacenterList(@PathVariable(\"page\") final int page)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", page);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IServiceManagementProcessor processor = new ServiceManagementProcessorImpl();

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
                String element = sessionEnumeration.nextElement();
                Object value = hSession.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String element = paramsEnumeration.nextElement();
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

                Service service = new Service();
                service.setType(ServiceType.DATACENTER);

                if (DEBUG)
                {
                    DEBUGGER.debug("Service: {}", service);
                }

                ServiceManagementRequest request = new ServiceManagementRequest();
                request.setRequestInfo(reqInfo);
                request.setUserAccount(userAccount);
                request.setServiceId(this.serviceId);
                request.setApplicationId(this.appConfig.getApplicationId());
                request.setApplicationName(this.appConfig.getApplicationName());
                request.setStartPage((page - 1) * this.recordsPerPage);
                request.setService(service);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServiceManagementRequest: {}", request);
                }

                ServiceManagementResponse response = processor.listServicesByType(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServiceManagementResponse: {}", response);
                }

                if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    List<Service> datacenterList = response.getServiceList();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("List<Service>: {}", datacenterList);
                    }

                    mView.addObject("pages", (int) Math.ceil(response.getEntryCount() * 1.0 / this.recordsPerPage));
                    mView.addObject("page", page);
                    mView.addObject("datacenterList", datacenterList);
                    mView.setViewName(this.viewDatacentersPage);
                }
                else if (response.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                {
                    mView.setViewName(this.appConfig.getUnauthorizedPage());
                }
                else
                {
                    mView.addObject(Constants.ERROR_RESPONSE, this.appConfig.getMessageNoSearchResults());
                    mView.setViewName(this.defaultPage);
                }
            }
            catch (ServiceManagementException smx)
            {
                ERROR_RECORDER.error(smx.getMessage(), smx);

                mView.setViewName(this.appConfig.getErrorResponsePage());
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

    @RequestMapping(value = "/platform/{platformGuid}", method = RequestMethod.GET)
    public final ModelAndView showPlatform(@PathVariable("platformGuid") final String platformGuid)
    {
        final String methodName = ServiceManagementController.CNAME + "#showPlatform(@PathVariable(\"platformGuid\") final String platformGuid)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", platformGuid);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IServiceManagementProcessor platformMgr = new ServiceManagementProcessorImpl();

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
                String element = sessionEnumeration.nextElement();
                Object value = hSession.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String element = paramsEnumeration.nextElement();
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

                Service reqPlatform = new Service();
                reqPlatform.setGuid(platformGuid);

                if (DEBUG)
                {
                    DEBUGGER.debug("Service: {}", reqPlatform);
                }

                // get a list of available servers
                ServiceManagementRequest request = new ServiceManagementRequest();
                request.setRequestInfo(reqInfo);
                request.setUserAccount(userAccount);
                request.setServiceId(this.projectMgmt);
                request.setService(reqPlatform);
                request.setApplicationId(this.appConfig.getApplicationId());
                request.setApplicationName(this.appConfig.getApplicationName());

                if (DEBUG)
                {
                    DEBUGGER.debug("ServiceManagementRequest: {}", request);
                }

                ServiceManagementResponse response = platformMgr.getServiceData(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServiceManagementResponse: {}", response);
                }

                if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    Service resPlatform = response.getService();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Service: {}", resPlatform);
                    }

                    mView.addObject("platform", resPlatform);
                    mView.setViewName(this.viewPlatformPage);
                }
                else if (response.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                {
                    mView.setViewName(this.appConfig.getUnauthorizedPage());
                }
                else
                {
                    mView.addObject(Constants.ERROR_RESPONSE, this.appConfig.getMessageNoSearchResults());
                    mView.setViewName(this.defaultPage);
                }
            }
            catch (ServiceManagementException pmx)
            {
                ERROR_RECORDER.error(pmx.getMessage(), pmx);

                mView.setViewName(this.appConfig.getErrorResponsePage());
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

    @RequestMapping(value = "/datacenter/{datacenter}", method = RequestMethod.GET)
    public final ModelAndView showDatacenter(@PathVariable("datacenter") final String datacenter)
    {
        final String methodName = ServiceManagementController.CNAME + "#showPlatform(@PathVariable(\"datacenter\") final String datacenter)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", datacenter);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IServiceManagementProcessor processor = new ServiceManagementProcessorImpl();

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
                String element = sessionEnumeration.nextElement();
                Object value = hSession.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String element = paramsEnumeration.nextElement();
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

                Service reqDatacenter = new Service();
                reqDatacenter.setGuid(datacenter);

                if (DEBUG)
                {
                    DEBUGGER.debug("Datacenter: {}", reqDatacenter);
                }

                // get a list of available servers
                ServiceManagementRequest request = new ServiceManagementRequest();
                request.setRequestInfo(reqInfo);
                request.setUserAccount(userAccount);
                request.setServiceId(this.serviceId);
                request.setService(reqDatacenter);
                request.setApplicationId(this.appConfig.getApplicationId());
                request.setApplicationName(this.appConfig.getApplicationName());

                if (DEBUG)
                {
                    DEBUGGER.debug("ServiceManagementRequest: {}", request);
                }

                ServiceManagementResponse response = processor.getServiceData(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServiceManagementResponse: {}", response);
                }

                if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    Service resDatacenter = response.getService();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("DataCenter: {}", resDatacenter);
                    }

                    mView.addObject("datacenter", resDatacenter);
                    mView.setViewName(this.viewDatacenterPage);
                }
                else if (response.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                {
                    mView.setViewName(this.appConfig.getUnauthorizedPage());
                }
                else
                {
                    mView.addObject(Constants.ERROR_RESPONSE, this.appConfig.getMessageNoSearchResults());
                    mView.setViewName(this.defaultPage);
                }
            }
            catch (ServiceManagementException smx)
            {
                ERROR_RECORDER.error(smx.getMessage(), smx);

                mView.setViewName(this.appConfig.getErrorResponsePage());
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

    @RequestMapping(value = "/add-datacenter", method = RequestMethod.GET)
    public final ModelAndView showAddDatacenter()
    {
        final String methodName = ServiceManagementController.CNAME + "#showAddDatacenter()";

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
                String element = sessionEnumeration.nextElement();
                Object value = hSession.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String element = paramsEnumeration.nextElement();
                Object value = hRequest.getParameter(element);

                DEBUGGER.debug("Parameter: {}; Value: {}", element, value);
            }
        }

        if (this.appConfig.getServices().get(this.serviceName))
        {
            mView.addObject("statusList", ServiceStatus.values());
            mView.addObject("command", new Service());
            mView.setViewName(this.addDatacenterPage);
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

    @RequestMapping(value = "/add-platform", method = RequestMethod.GET)
    public final ModelAndView showAddPlatform()
    {
        final String methodName = ServiceManagementController.CNAME + "#showAddPlatform()";

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
                String element = sessionEnumeration.nextElement();
                Object value = hSession.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String element = paramsEnumeration.nextElement();
                Object value = hRequest.getParameter(element);

                DEBUGGER.debug("Parameter: {}; Value: {}", element, value);
            }
        }

        if (this.appConfig.getServices().get(this.serviceName))
        {
            mView.addObject(new PlatformRequest());
            mView.setViewName(this.addPlatformPage);
        }
        else
        {
            mView = new ModelAndView(new RedirectView());
            mView.setViewName(this.appConfig.getUnavailablePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public final ModelAndView submitServiceSearch(@ModelAttribute("service") final Service service, final BindingResult bindResult)
    {
        final String methodName = ServiceManagementController.CNAME + "#submitServiceSearch(@ModelAttribute(\"service\") final Service service, final BindingResult bindResult)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("SearchRequest: {}", service);
            DEBUGGER.debug("BindingResult: {}", bindResult);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IServiceManagementProcessor processor = new ServiceManagementProcessorImpl();

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
                String element = sessionEnumeration.nextElement();
                Object value = hSession.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String element = paramsEnumeration.nextElement();
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

                Service reqService = new Service();
                reqService.setName(service.getName());

                if (DEBUG)
                {
                    DEBUGGER.debug("Service: {}", reqService);
                }

                ServiceManagementRequest request = new ServiceManagementRequest();
                request.setApplicationId(this.appConfig.getApplicationId());
                request.setApplicationName(this.appConfig.getApplicationName());
                request.setRequestInfo(reqInfo);
                request.setService(reqService);
                request.setServiceId(this.serviceId);
                request.setUserAccount(userAccount);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServiceManagementRequest: {}", request);
                }

                ServiceManagementResponse response = processor.getServiceByAttribute(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServiceManagementResponse: {}", response);
                }

                if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    mView.addObject("pages", (int) Math.ceil(response.getEntryCount() * 1.0 / this.recordsPerPage));
                    mView.addObject("page", 1);
                    mView.addObject("searchTerms", service.getName());
                    mView.addObject(Constants.SEARCH_RESULTS, response.getServiceList());
                    mView.addObject("command", new Service());
                    mView.setViewName(this.defaultPage);
                }
                else if (response.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                {
                    mView.setViewName(this.appConfig.getUnauthorizedPage());
                }
                else
                {
                    mView.addObject(Constants.ERROR_RESPONSE, this.appConfig.getMessageNoSearchResults());
                    mView.addObject("command", new Service());
                    mView.setViewName(this.defaultPage);
                }
            }
            catch (ServiceManagementException smx)
            {
                ERROR_RECORDER.error(smx.getMessage(), smx);

                mView.setViewName(this.appConfig.getErrorResponsePage());
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

    @RequestMapping(value = "/submit-platform", method = RequestMethod.POST)
    public final ModelAndView doAddPlatform(@ModelAttribute("request") final PlatformRequest request, final BindingResult bindResult)
    {
        final String methodName = ServiceManagementController.CNAME + "#doAddPlatform(@ModelAttribute(\"request\") final PlatformRequest request, final BindingResult bindResult)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("PlatformRequest: {}", request);
            DEBUGGER.debug("BindingResult: {}", bindResult);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IServiceManagementProcessor processor = new ServiceManagementProcessorImpl();

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
                String element = sessionEnumeration.nextElement();
                Object value = hSession.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String element = paramsEnumeration.nextElement();
                Object value = hRequest.getParameter(element);

                DEBUGGER.debug("Parameter: {}; Value: {}", element, value);
            }
        }

        if (this.appConfig.getServices().get(this.serviceName))
        {
            try
            {
                this.serviceValidator.validate(request, bindResult);

                if (bindResult.hasErrors())
                {
                    // something was missing from the request
                    mView.addObject(Constants.ERROR_MESSAGE, this.appConfig.getMessageValidationFailed());
                    mView.addObject("statusList", ServiceStatus.values());
                    mView.addObject("command", request);
                    mView.setViewName(this.addPlatformPage);

                    return mView;
                }

                RequestHostInfo reqInfo = new RequestHostInfo();
                reqInfo.setHostName(hRequest.getRemoteHost());
                reqInfo.setHostAddress(hRequest.getRemoteAddr());
                reqInfo.setSessionId(hSession.getId());

                if (DEBUG)
                {
                    DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
                }

                List<Server> appServers = new ArrayList<>();

                // get the servers
                for (String guid : request.getPlatformServers())
                {
                    if (DEBUG)
                    {
                        DEBUGGER.debug("String: {}", guid);
                    }

                    Server server = new Server();
                    server.setServerGuid(guid);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Server: {}", server);
                    }

                    appServers.add(server);
                }

                if (DEBUG)
                {
                    DEBUGGER.debug("List<Server>: {}", appServers);
                }

                Service platform = new Service();
                platform.setServers(appServers);
                platform.setDescription(request.getDescription());
                platform.setName(request.getPlatformName());
                platform.setRegion(request.getRegion());
                platform.setStatus(request.getStatus());

                if (DEBUG)
                {
                    DEBUGGER.debug("Service: {}", platform);
                }

                ServiceManagementRequest platformRequest = new ServiceManagementRequest();
                platformRequest.setService(platform);
                platformRequest.setRequestInfo(reqInfo);
                platformRequest.setServiceId(this.platformMgmt);
                platformRequest.setUserAccount(userAccount);
                platformRequest.setApplicationId(this.appConfig.getApplicationId());
                platformRequest.setApplicationName(this.appConfig.getApplicationName());

                if (DEBUG)
                {
                    DEBUGGER.debug("ServiceManagementRequest: {}", platformRequest);
                }

                ServiceManagementResponse platformResponse = processor.addNewService(platformRequest);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServiceManagementResponse: {}", platformResponse);
                }

                if (platformResponse.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    mView.addObject(Constants.RESPONSE_MESSAGE, this.messagePlatformAddSuccess);
                }
                else if (platformResponse.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                {
                    mView.setViewName(this.appConfig.getUnauthorizedPage());

                    return mView;
                }
                else
                {
                    mView.addObject(Constants.ERROR_RESPONSE, this.messagePlatformAddFailure);
                }

                mView = new ModelAndView(new RedirectView());
                mView.setViewName(this.addPlatformRedirect);
            }
            catch (ServiceManagementException pmx)
            {
                ERROR_RECORDER.error(pmx.getMessage(), pmx);

                mView.setViewName(this.appConfig.getErrorResponsePage());
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

    @RequestMapping(value = "/submit-datacenter", method = RequestMethod.POST)
    public final ModelAndView doAddDatacenter(@ModelAttribute("service") final Service service, final BindingResult bindResult)
    {
        final String methodName = ServiceManagementController.CNAME + "#doAddDatacenter(@ModelAttribute(\"service\") final Service service, final BindingResult bindResult)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", service);
            DEBUGGER.debug("Value: {}", bindResult);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IServiceManagementProcessor processor = new ServiceManagementProcessorImpl();

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
                String element = sessionEnumeration.nextElement();
                Object value = hSession.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String element = paramsEnumeration.nextElement();
                Object value = hRequest.getParameter(element);

                DEBUGGER.debug("Parameter: {}; Value: {}", element, value);
            }
        }

        if (this.appConfig.getServices().get(this.serviceName))
        {
            this.serviceValidator.validate(service, bindResult);

            if (bindResult.hasErrors())
            {
                // go back
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

                ServiceManagementRequest dcRequest = new ServiceManagementRequest();
                dcRequest.setService(service);
                dcRequest.setRequestInfo(reqInfo);
                dcRequest.setServiceId(this.serviceId);
                dcRequest.setUserAccount(userAccount);
                dcRequest.setApplicationId(this.appConfig.getApplicationId());
                dcRequest.setApplicationName(this.appConfig.getApplicationName());

                if (DEBUG)
                {
                    DEBUGGER.debug("ServiceManagementRequest: {}", dcRequest);
                }

                ServiceManagementResponse dcResponse = processor.addNewService(dcRequest);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServiceManagementResponse: {}", dcResponse);
                }

                if (dcResponse.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    // return to add dc page
                    mView.addObject(Constants.RESPONSE_MESSAGE, this.messageDatacenterAddSuccess);
                    mView.addObject("command", new Service());
                    mView.setViewName(this.addDatacenterPage);
                }
                else if (dcResponse.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                {
                    mView.setViewName(this.appConfig.getUnauthorizedPage());
                }
                else
                {
                    mView.addObject(Constants.ERROR_RESPONSE, this.messageDatacenterAddFailure);
                    mView.addObject("command", service);
                    mView.setViewName(this.addDatacenterPage);
                }
            }
            catch (ServiceManagementException smx)
            {
                ERROR_RECORDER.error(smx.getMessage(), smx);

                mView.setViewName(this.appConfig.getErrorResponsePage());
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
}
