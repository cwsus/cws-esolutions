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

import com.cws.us.esolutions.Constants;
import com.cws.us.esolutions.dto.ProjectRequest;
import com.cws.us.esolutions.dto.PlatformRequest;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.us.esolutions.ApplicationServiceBean;
import com.cws.esolutions.core.processors.dto.Server;
import com.cws.esolutions.core.processors.dto.Project;
import com.cws.esolutions.core.processors.dto.Platform;
import com.cws.esolutions.core.processors.dto.DataCenter;
import com.cws.us.esolutions.validators.PlatformValidator;
import com.cws.us.esolutions.validators.ProjectValidator;
import com.cws.esolutions.core.processors.enums.ServerType;
import com.cws.us.esolutions.validators.DatacenterValidator;
import com.cws.esolutions.core.processors.dto.SearchRequest;
import com.cws.esolutions.core.processors.dto.SearchResponse;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.enums.ServiceStatus;
import com.cws.us.esolutions.validators.SearchRequestValidator;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.processors.enums.ControlType;
import com.cws.esolutions.core.processors.enums.SearchRequestType;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.core.processors.impl.SearchProcessorImpl;
import com.cws.esolutions.core.processors.interfaces.ISearchProcessor;
import com.cws.esolutions.core.processors.dto.ServerManagementRequest;
import com.cws.esolutions.core.processors.dto.ServerManagementResponse;
import com.cws.esolutions.core.processors.dto.ProjectManagementRequest;
import com.cws.esolutions.core.processors.dto.PlatformManagementRequest;
import com.cws.esolutions.core.processors.dto.ProjectManagementResponse;
import com.cws.esolutions.security.processors.dto.AccountControlRequest;
import com.cws.esolutions.security.processors.dto.AccountControlResponse;
import com.cws.esolutions.core.processors.dto.PlatformManagementResponse;
import com.cws.esolutions.core.processors.dto.DatacenterManagementRequest;
import com.cws.esolutions.core.processors.dto.DatacenterManagementResponse;
import com.cws.esolutions.core.processors.exception.SearchRequestException;
import com.cws.esolutions.core.processors.impl.ServerManagementProcessorImpl;
import com.cws.esolutions.core.processors.impl.ProjectManagementProcessorImpl;
import com.cws.esolutions.core.processors.exception.ServerManagementException;
import com.cws.esolutions.security.processors.impl.AccountControlProcessorImpl;
import com.cws.esolutions.core.processors.exception.ProjectManagementException;
import com.cws.esolutions.core.processors.impl.PlatformManagementProcessorImpl;
import com.cws.esolutions.security.processors.exception.AccountControlException;
import com.cws.esolutions.core.processors.exception.PlatformManagementException;
import com.cws.esolutions.core.processors.interfaces.IServerManagementProcessor;
import com.cws.esolutions.core.processors.impl.DatacenterManagementProcessorImpl;
import com.cws.esolutions.core.processors.interfaces.IProjectManagementProcessor;
import com.cws.esolutions.security.processors.interfaces.IAccountControlProcessor;
import com.cws.esolutions.core.processors.interfaces.IPlatformManagementProcessor;
import com.cws.esolutions.core.processors.exception.DatacenterManagementException;
import com.cws.esolutions.core.processors.interfaces.IDatacenterManagementProcessor;
/**
 * eSolutions_java_source
 * com.cws.us.esolutions.controllers
 * ServiceManagementController.java
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
@RequestMapping("/service-management")
public class ServiceManagementController
{
    private int recordsPerPage = 20; // default to 20
    private String dcService = null;
    private String systemMgmt = null;
    private String projectMgmt = null;
    private String serviceName = null;
    private String defaultPage = null;
    private String platformMgmt = null;
    private String addProjectPage = null;
    private String selectDmgrPage = null;
    private String addPlatformPage = null;
    private String viewProjectPage = null;
    private String viewPlatformPage = null;
    private String viewProjectsList = null;
    private String viewPlatformList = null;
    private String addDatacenterPage = null;
    private String addServerRedirect = null;
    private String messageNoDmgrFound = null;
    private String viewDatacenterPage = null;
    private String addProjectRedirect = null;
    private String viewDatacentersPage = null;
    private String addPlatformRedirect = null;
    private String messageNoDatacenters = null;
    private String addDatacenterRedirect = null;
    private ApplicationServiceBean appConfig = null;
    private ProjectValidator projectValidator = null;
    private PlatformValidator platformValidator = null;
    private SearchRequestValidator searchValidator = null;
    private String messageProjectSuccessfullyAdded = null;
    private String messagePlatformSuccessfullyAdded = null;
    private DatacenterValidator datacenterValidator = null;
    private String messageDatacenterSuccessfullyAdded = null;

    private static final String CNAME = ServiceManagementController.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CNAME);

    public final void setAppConfig(final ApplicationServiceBean value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setAppConfig(final CoreServiceBean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.appConfig = value;
    }

    public final void setPlatformValidator(final PlatformValidator value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setPlatformValidator(final PlatformValidator value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.platformValidator = value;
    }

    public final void setProjectValidator(final ProjectValidator value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setProjectValidator(final ProjectValidator value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.projectValidator = value;
    }

    public final void setDatacenterValidator(final DatacenterValidator value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setDatacenterValidator(final DatacenterValidator value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.datacenterValidator = value;
    }

    public final void setSearchValidator(final SearchRequestValidator value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setSearchValidator(final ServerValidator value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.searchValidator = value;
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

    public final void setDcService(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setDcService(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.dcService = value;
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

    public final void setAddProjectRedirect(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setAddProjectRedirect(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.addProjectRedirect = value;
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

    public final void setAddProjectPage(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setAddProjectPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.addProjectPage = value;
    }

    public final void setSelectDmgrPage(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setSelectDmgrPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.selectDmgrPage = value;
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

    public final void setViewProjectPage(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setViewProjectPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.viewProjectPage = value;
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

    public final void setViewProjectsList(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setViewProjectsList(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.viewProjectsList = value;
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

    public final void setProjectMgmt(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setProjectMgmt(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.projectMgmt = value;
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

    public final void setSystemMgmt(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setSystemMgmt(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.systemMgmt = value;
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

    public final void setMessageDatacenterSuccessfullyAdded(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setMessageDatacenterSuccessfullyAdded(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageDatacenterSuccessfullyAdded = value;
    }

    public final void setMessageNoDmgrFound(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setMessageNoDmgrFound(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageNoDmgrFound = value;
    }

    public final void setMessagePlatformSuccessfullyAdded(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setMessagePlatformSuccessfullyAdded(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messagePlatformSuccessfullyAdded = value;
    }

    public final void setMessageProjectSuccessfullyAdded(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setMessageProjectSuccessfullyAdded(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageProjectSuccessfullyAdded = value;
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

    public final void setAddServerRedirect(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setAddServerRedirect(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.addServerRedirect = value;
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
            @SuppressWarnings("unchecked") Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked") Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }

            DEBUGGER.debug("Dumping request parameters:");
            @SuppressWarnings("unchecked") Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String requestElement = paramsEnumeration.nextElement();
                Object requestValue = hRequest.getParameter(requestElement);

                DEBUGGER.debug("Parameter: " + requestElement + "; Value: " + requestValue);
            }
        }

        if (this.appConfig.getServices().get(this.serviceName))
        {
            mView.addObject("searchTypes", SearchRequestType.values());
            mView.addObject("command", new SearchRequest());
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
        final ISearchProcessor searchProcessor = new SearchProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            @SuppressWarnings("unchecked") Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked") Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }

            DEBUGGER.debug("Dumping request parameters:");
            @SuppressWarnings("unchecked") Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String requestElement = paramsEnumeration.nextElement();
                Object requestValue = hRequest.getParameter(requestElement);

                DEBUGGER.debug("Parameter: " + requestElement + "; Value: " + requestValue);
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

                SearchRequest request = new SearchRequest();
                request.setSearchTerms(terms);
                request.setStartRow(page);
                request.setSearchType(SearchRequestType.valueOf(type));

                if (DEBUG)
                {
                    DEBUGGER.debug("SearchRequest: {}", request);
                }

                SearchResponse searchRes = searchProcessor.doServiceSearch(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("SearchResponse: {}", searchRes);
                }

                if (searchRes.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    mView.addObject("pages", (int) Math.ceil(searchRes.getEntryCount() * 1.0 / this.recordsPerPage));
                    mView.addObject("page", page);
                    mView.addObject("searchTerms", terms);
                    mView.addObject("searchType", type);
                    mView.addObject(Constants.SEARCH_RESULTS, searchRes.getResults());
                    mView.addObject("command", new SearchRequest());
                    mView.setViewName(this.defaultPage);
                }
                else if (searchRes.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                {
                    mView.setViewName(this.appConfig.getUnauthorizedPage());
                }
                else
                {
                    mView.addObject(Constants.ERROR_RESPONSE, searchRes.getResponse());
                    mView.addObject("command", new SearchRequest());
                    mView.setViewName(this.defaultPage);
                }
            }
            catch (SearchRequestException srx)
            {
                ERROR_RECORDER.error(srx.getMessage(), srx);

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
        final IPlatformManagementProcessor platformMgr = new PlatformManagementProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            @SuppressWarnings("unchecked") Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked") Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }

            DEBUGGER.debug("Dumping request parameters:");
            @SuppressWarnings("unchecked") Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String requestElement = paramsEnumeration.nextElement();
                Object requestValue = hRequest.getParameter(requestElement);

                DEBUGGER.debug("Parameter: " + requestElement + "; Value: " + requestValue);
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

                PlatformManagementRequest request = new PlatformManagementRequest();
                request.setRequestInfo(reqInfo);
                request.setUserAccount(userAccount);
                request.setServiceId(this.platformMgmt);
                request.setApplicationId(this.appConfig.getApplicationId());
                request.setApplicationName(this.appConfig.getApplicationName());

                if (DEBUG)
                {
                    DEBUGGER.debug("PlatformManagementRequest: {}", request);
                }

                PlatformManagementResponse response = platformMgr.listPlatforms(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("PlatformManagementResponse: {}", response);
                }

                if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    List<Platform> platformList = response.getPlatformList();

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
                    mView.addObject(Constants.ERROR_RESPONSE, response.getResponse());
                    mView.setViewName(this.addPlatformRedirect);
                }
            }
            catch (PlatformManagementException pmx)
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
        final IPlatformManagementProcessor platformMgr = new PlatformManagementProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            @SuppressWarnings("unchecked") Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked") Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }

            DEBUGGER.debug("Dumping request parameters:");
            @SuppressWarnings("unchecked") Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String requestElement = paramsEnumeration.nextElement();
                Object requestValue = hRequest.getParameter(requestElement);

                DEBUGGER.debug("Parameter: " + requestElement + "; Value: " + requestValue);
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

                PlatformManagementRequest request = new PlatformManagementRequest();
                request.setRequestInfo(reqInfo);
                request.setUserAccount(userAccount);
                request.setServiceId(this.platformMgmt);
                request.setApplicationId(this.appConfig.getApplicationId());
                request.setApplicationName(this.appConfig.getApplicationName());
                request.setStartPage((page - 1) * this.recordsPerPage);

                if (DEBUG)
                {
                    DEBUGGER.debug("PlatformManagementRequest: {}", request);
                }

                PlatformManagementResponse response = platformMgr.listPlatforms(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("PlatformManagementResponse: {}", response);
                }

                if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    List<Platform> platformList = response.getPlatformList();

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
                    mView.addObject(Constants.ERROR_RESPONSE, response.getResponse());
                    mView.setViewName(this.defaultPage);
                }
            }
            catch (PlatformManagementException pmx)
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

    @RequestMapping(value = "/list-projects", method = RequestMethod.GET)
    public final ModelAndView showProjectList()
    {
        final String methodName = ServiceManagementController.CNAME + "#showProjectList()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IProjectManagementProcessor projectMgr = new ProjectManagementProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            @SuppressWarnings("unchecked") Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked") Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }

            DEBUGGER.debug("Dumping request parameters:");
            @SuppressWarnings("unchecked") Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String requestElement = paramsEnumeration.nextElement();
                Object requestValue = hRequest.getParameter(requestElement);

                DEBUGGER.debug("Parameter: " + requestElement + "; Value: " + requestValue);
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

                ProjectManagementRequest request = new ProjectManagementRequest();
                request.setRequestInfo(reqInfo);
                request.setUserAccount(userAccount);
                request.setServiceId(this.platformMgmt);
                request.setApplicationId(this.appConfig.getApplicationId());
                request.setApplicationName(this.appConfig.getApplicationName());

                if (DEBUG)
                {
                    DEBUGGER.debug("ProjectManagementRequest: {}", request);
                }

                ProjectManagementResponse response = projectMgr.listProjects(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("ProjectManagementResponse: {}", response);
                }

                if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    List<Project> projectList = response.getProjectList();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("projectList: {}", projectList);
                    }

                    mView.addObject("pages", (int) Math.ceil(response.getEntryCount() * 1.0 / this.recordsPerPage));
                    mView.addObject("page", 1);
                    mView.addObject("projectList", projectList);
                    mView.setViewName(this.viewProjectsList);
                }
                else if (response.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                {
                    mView.setViewName(this.appConfig.getUnauthorizedPage());
                }
                else
                {
                    mView = new ModelAndView(new RedirectView());
                    mView.addObject(Constants.ERROR_RESPONSE, response.getResponse());
                    mView.setViewName(this.addProjectRedirect);
                }
            }
            catch (ProjectManagementException pmx)
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

    @RequestMapping(value = "/list-projects/page/{page}", method = RequestMethod.GET)
    public final ModelAndView showProjectList(@PathVariable("page") final int page)
    {
        final String methodName = ServiceManagementController.CNAME + "#showProjectList(@PathVariable(\"page\") final int page)";

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
        final IProjectManagementProcessor projectMgr = new ProjectManagementProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            @SuppressWarnings("unchecked") Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked") Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }

            DEBUGGER.debug("Dumping request parameters:");
            @SuppressWarnings("unchecked") Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String requestElement = paramsEnumeration.nextElement();
                Object requestValue = hRequest.getParameter(requestElement);

                DEBUGGER.debug("Parameter: " + requestElement + "; Value: " + requestValue);
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

                ProjectManagementRequest request = new ProjectManagementRequest();
                request.setRequestInfo(reqInfo);
                request.setUserAccount(userAccount);
                request.setServiceId(this.platformMgmt);
                request.setApplicationId(this.appConfig.getApplicationId());
                request.setApplicationName(this.appConfig.getApplicationName());
                request.setStartPage((page - 1) * this.recordsPerPage);

                if (DEBUG)
                {
                    DEBUGGER.debug("ProjectManagementRequest: {}", request);
                }

                ProjectManagementResponse response = projectMgr.listProjects(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("ProjectManagementResponse: {}", response);
                }

                if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    List<Project> projectList = response.getProjectList();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("projectList: {}", projectList);
                    }

                    mView.addObject("pages", (int) Math.ceil(response.getEntryCount() * 1.0 / this.recordsPerPage));
                    mView.addObject("page", page);
                    mView.addObject("projectList", projectList);
                    mView.setViewName(this.viewProjectsList);
                }
                else if (response.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                {
                    mView.setViewName(this.appConfig.getUnauthorizedPage());
                }
                else
                {
                    mView.addObject(Constants.ERROR_RESPONSE, response.getResponse());
                    mView.setViewName(this.defaultPage);
                }
            }
            catch (ProjectManagementException pmx)
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
        final IDatacenterManagementProcessor processor = new DatacenterManagementProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            @SuppressWarnings("unchecked") Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked") Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }

            DEBUGGER.debug("Dumping request parameters:");
            @SuppressWarnings("unchecked") Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String requestElement = paramsEnumeration.nextElement();
                Object requestValue = hRequest.getParameter(requestElement);

                DEBUGGER.debug("Parameter: " + requestElement + "; Value: " + requestValue);
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

                DatacenterManagementRequest request = new DatacenterManagementRequest();
                request.setRequestInfo(reqInfo);
                request.setUserAccount(userAccount);
                request.setServiceId(this.dcService);
                request.setApplicationId(this.appConfig.getApplicationId());
                request.setApplicationName(this.appConfig.getApplicationName());

                if (DEBUG)
                {
                    DEBUGGER.debug("DatacenterManagementRequest: {}", request);
                }

                DatacenterManagementResponse response = processor.listDatacenters(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("DatacenterManagementResponse: {}", response);
                }

                if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    List<DataCenter> datacenterList = response.getDatacenterList();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("List<DataCenter>: {}", datacenterList);
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
                        mView.addObject("command", new DataCenter());
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
                    mView.addObject(Constants.ERROR_RESPONSE, response.getResponse());
                    mView.setViewName(this.addDatacenterRedirect);
                }
            }
            catch (DatacenterManagementException dmx)
            {
                ERROR_RECORDER.error(dmx.getMessage(), dmx);

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
        final IDatacenterManagementProcessor processor = new DatacenterManagementProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            @SuppressWarnings("unchecked") Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked") Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }

            DEBUGGER.debug("Dumping request parameters:");
            @SuppressWarnings("unchecked") Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String requestElement = paramsEnumeration.nextElement();
                Object requestValue = hRequest.getParameter(requestElement);

                DEBUGGER.debug("Parameter: " + requestElement + "; Value: " + requestValue);
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

                DatacenterManagementRequest request = new DatacenterManagementRequest();
                request.setRequestInfo(reqInfo);
                request.setUserAccount(userAccount);
                request.setServiceId(this.dcService);
                request.setApplicationId(this.appConfig.getApplicationId());
                request.setApplicationName(this.appConfig.getApplicationName());
                request.setStartPage((page - 1) * this.recordsPerPage);

                if (DEBUG)
                {
                    DEBUGGER.debug("DatacenterManagementRequest: {}", request);
                }

                DatacenterManagementResponse response = processor.listDatacenters(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("DatacenterManagementResponse: {}", response);
                }

                if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    List<DataCenter> datacenterList = response.getDatacenterList();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("List<DataCenter>: {}", datacenterList);
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
                    mView.addObject(Constants.ERROR_RESPONSE, response.getResponse());
                    mView.setViewName(this.defaultPage);
                }
            }
            catch (DatacenterManagementException dmx)
            {
                ERROR_RECORDER.error(dmx.getMessage(), dmx);

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

    @RequestMapping(value = "/project/{projectName}", method = RequestMethod.GET)
    public final ModelAndView showProject(@PathVariable("projectName") final String projectName)
    {
        final String methodName = ServiceManagementController.CNAME + "#showProject(@PathVariable(\"projectName\") final String projectName)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("projectName: {}", projectName);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IProjectManagementProcessor projectMgr = new ProjectManagementProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            @SuppressWarnings("unchecked") Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked") Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }

            DEBUGGER.debug("Dumping request parameters:");
            @SuppressWarnings("unchecked") Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String requestElement = paramsEnumeration.nextElement();
                Object requestValue = hRequest.getParameter(requestElement);

                DEBUGGER.debug("Parameter: " + requestElement + "; Value: " + requestValue);
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

                Project reqProject = new Project();
                reqProject.setProjectGuid(projectName);

                if (DEBUG)
                {
                    DEBUGGER.debug("Project: {}", reqProject);
                }

                // get a list of available servers
                ProjectManagementRequest request = new ProjectManagementRequest();
                request.setRequestInfo(reqInfo);
                request.setUserAccount(userAccount);
                request.setServiceId(this.projectMgmt);
                request.setProject(reqProject);
                request.setApplicationId(this.appConfig.getApplicationId());
                request.setApplicationName(this.appConfig.getApplicationName());

                if (DEBUG)
                {
                    DEBUGGER.debug("ProjectManagementRequest: {}", request);
                }

                ProjectManagementResponse response = projectMgr.getProjectData(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("ProjectManagementResponse: {}", response);
                }

                if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    Project resProject = response.getProject();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Project: {}", resProject);
                    }

                    mView.addObject("project", resProject);
                    mView.setViewName(this.viewProjectPage);
                }
                else if (response.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                {
                    mView.setViewName(this.appConfig.getUnauthorizedPage());
                }
                else
                {
                    mView.addObject(Constants.ERROR_RESPONSE, response.getResponse());
                    mView.setViewName(this.defaultPage);
                }
            }
            catch (ProjectManagementException pmx)
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

    @RequestMapping(value = "/platform/{platformName}", method = RequestMethod.GET)
    public final ModelAndView showPlatform(@PathVariable("platformName") final String platformName)
    {
        final String methodName = ServiceManagementController.CNAME + "#showPlatform(@PathVariable(\"platformName\") final String platformName)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("platformName: {}", platformName);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IPlatformManagementProcessor platformMgr = new PlatformManagementProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            @SuppressWarnings("unchecked") Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked") Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }

            DEBUGGER.debug("Dumping request parameters:");
            @SuppressWarnings("unchecked") Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String requestElement = paramsEnumeration.nextElement();
                Object requestValue = hRequest.getParameter(requestElement);

                DEBUGGER.debug("Parameter: " + requestElement + "; Value: " + requestValue);
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

                Platform reqPlatform = new Platform();
                reqPlatform.setPlatformGuid(platformName);

                if (DEBUG)
                {
                    DEBUGGER.debug("Platform: {}", reqPlatform);
                }

                // get a list of available servers
                PlatformManagementRequest request = new PlatformManagementRequest();
                request.setRequestInfo(reqInfo);
                request.setUserAccount(userAccount);
                request.setServiceId(this.projectMgmt);
                request.setPlatform(reqPlatform);
                request.setApplicationId(this.appConfig.getApplicationId());
                request.setApplicationName(this.appConfig.getApplicationName());

                if (DEBUG)
                {
                    DEBUGGER.debug("PlatformManagementRequest: {}", request);
                }

                PlatformManagementResponse response = platformMgr.getPlatformData(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("PlatformManagementResponse: {}", response);
                }

                if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    Platform resPlatform = response.getPlatformData();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Platform: {}", resPlatform);
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
                    mView.addObject(Constants.ERROR_RESPONSE, response.getResponse());
                    mView.setViewName(this.defaultPage);
                }
            }
            catch (PlatformManagementException pmx)
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
        final IDatacenterManagementProcessor processor = new DatacenterManagementProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            @SuppressWarnings("unchecked") Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked") Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }

            DEBUGGER.debug("Dumping request parameters:");
            @SuppressWarnings("unchecked") Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String requestElement = paramsEnumeration.nextElement();
                Object requestValue = hRequest.getParameter(requestElement);

                DEBUGGER.debug("Parameter: " + requestElement + "; Value: " + requestValue);
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

                DataCenter reqDatacenter = new DataCenter();
                reqDatacenter.setDatacenterGuid(datacenter);

                if (DEBUG)
                {
                    DEBUGGER.debug("Datacenter: {}", reqDatacenter);
                }

                // get a list of available servers
                DatacenterManagementRequest request = new DatacenterManagementRequest();
                request.setRequestInfo(reqInfo);
                request.setUserAccount(userAccount);
                request.setServiceId(this.dcService);
                request.setDataCenter(reqDatacenter);
                request.setApplicationId(this.appConfig.getApplicationId());
                request.setApplicationName(this.appConfig.getApplicationName());

                if (DEBUG)
                {
                    DEBUGGER.debug("DatacenterManagementRequest: {}", request);
                }

                DatacenterManagementResponse response = processor.getDatacenter(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("DatacenterManagementResponse: {}", response);
                }

                if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    DataCenter resDatacenter = response.getDataCenter();

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
                    mView.addObject(Constants.ERROR_RESPONSE, response.getResponse());
                    mView.setViewName(this.defaultPage);
                }
            }
            catch (DatacenterManagementException dmx)
            {
                ERROR_RECORDER.error(dmx.getMessage(), dmx);

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

    @RequestMapping(value = "/add-project", method = RequestMethod.GET)
    public final ModelAndView showAddProject()
    {
        final String methodName = ServiceManagementController.CNAME + "#showAddProject()";

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
            @SuppressWarnings("unchecked") Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked") Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }

            DEBUGGER.debug("Dumping request parameters:");
            @SuppressWarnings("unchecked") Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String requestElement = paramsEnumeration.nextElement();
                Object requestValue = hRequest.getParameter(requestElement);

                DEBUGGER.debug("Parameter: " + requestElement + "; Value: " + requestValue);
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

                // search accounts
                AccountControlRequest request = new AccountControlRequest();
                request.setHostInfo(reqInfo);
                request.setApplicationId(this.appConfig.getApplicationName());
                request.setControlType(ControlType.SERVICES);
                request.setRequestor(userAccount);
                request.setIsLogonRequest(false);
                request.setApplicationId(this.appConfig.getApplicationId());
                request.setApplicationName(this.appConfig.getApplicationName());

                if (DEBUG)
                {
                    DEBUGGER.debug("AccountControlRequest: {}", request);
                }

                IAccountControlProcessor processor = new AccountControlProcessorImpl();
                AccountControlResponse response = processor.listUserAccounts(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("AccountControlResponse: {}", response);
                }

                if (response.getRequestStatus() == SecurityRequestStatus.SUCCESS)
                {
                    if ((response.getUserList() != null) && (response.getUserList().size() != 0))
                    {
                        List<UserAccount> userList = response.getUserList();

                        if (DEBUG)
                        {
                            DEBUGGER.debug("List<UserAccount> {}", userList);
                        }

                        mView.addObject("userList", userList);
                        mView.addObject("statusList", ServiceStatus.values());
                        mView.addObject("command", new ProjectRequest());
                        mView.setViewName(this.addProjectPage);
                    }
                    else
                    {
                        mView.addObject("command", new UserAccount());
                        mView.addObject(Constants.ERROR_RESPONSE, response.getResponse());
                    }
                }
                else if (response.getRequestStatus() == SecurityRequestStatus.UNAUTHORIZED)
                {
                    mView.setViewName(this.appConfig.getUnauthorizedPage());
                }
                else
                {
                    mView.addObject("command", new UserAccount());
                    mView.addObject(Constants.ERROR_RESPONSE, response.getResponse());
                }
            }
            catch (AccountControlException acx)
            {
                ERROR_RECORDER.error(acx.getMessage(), acx);
    
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
            @SuppressWarnings("unchecked") Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked") Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }

            DEBUGGER.debug("Dumping request parameters:");
            @SuppressWarnings("unchecked") Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String requestElement = paramsEnumeration.nextElement();
                Object requestValue = hRequest.getParameter(requestElement);

                DEBUGGER.debug("Parameter: " + requestElement + "; Value: " + requestValue);
            }
        }

        if (this.appConfig.getServices().get(this.serviceName))
        {
            mView.addObject("statusList", ServiceStatus.values());
            mView.addObject("command", new DataCenter());
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

        final IServerManagementProcessor serverMgr = new ServerManagementProcessorImpl();
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
            @SuppressWarnings("unchecked") Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked") Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }

            DEBUGGER.debug("Dumping request parameters:");
            @SuppressWarnings("unchecked") Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String requestElement = paramsEnumeration.nextElement();
                Object requestValue = hRequest.getParameter(requestElement);

                DEBUGGER.debug("Parameter: " + requestElement + "; Value: " + requestValue);
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

                Server dmgrServer = new Server();
                dmgrServer.setServerType(ServerType.DMGRSERVER);

                if (DEBUG)
                {
                    DEBUGGER.debug("Server: {}", dmgrServer);
                }

                ServerManagementRequest dmgrRequest = new ServerManagementRequest();
                dmgrRequest.setRequestInfo(reqInfo);
                dmgrRequest.setServiceId(this.systemMgmt);
                dmgrRequest.setTargetServer(dmgrServer);
                dmgrRequest.setUserAccount(userAccount);
                dmgrRequest.setApplicationId(this.appConfig.getApplicationId());
                dmgrRequest.setApplicationName(this.appConfig.getApplicationName());

                if (DEBUG)
                {
                    DEBUGGER.debug("ServerManagementRequest: {}", dmgrRequest);
                }

                ServerManagementResponse dmgrResponse = serverMgr.listServersByType(dmgrRequest);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServerManagementResponse: {}", dmgrResponse);
                }

                if (dmgrResponse.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    List<Server> listing = dmgrResponse.getServerList();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("listing: {}", listing);
                    }

                    mView.addObject("command", new Server());
                    mView.addObject("dmgrList", listing);
                    mView.setViewName(this.selectDmgrPage);
                }
                else if (dmgrResponse.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                {
                    mView.setViewName(this.appConfig.getUnauthorizedPage());
                }
                else
                {
                    // no dmgrs were found. redirect over to the add server page
                    mView = new ModelAndView(new RedirectView());
                    mView.addObject(Constants.ERROR_MESSAGE, this.messageNoDmgrFound);
                    mView.setViewName(this.addServerRedirect);
                }
            }
            catch (ServerManagementException smx)
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

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public final ModelAndView submitServiceSearch(@ModelAttribute("request") final SearchRequest request, final BindingResult bindResult)
    {
        final String methodName = ServiceManagementController.CNAME + "#submitServiceSearch(@ModelAttribute(\"request\") final SearchRequest request, final BindingResult bindResult)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("SearchRequest: {}", request);
            DEBUGGER.debug("BindingResult: {}", bindResult);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final ISearchProcessor searchProcessor = new SearchProcessorImpl();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            @SuppressWarnings("unchecked") Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked") Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }

            DEBUGGER.debug("Dumping request parameters:");
            @SuppressWarnings("unchecked") Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String requestElement = paramsEnumeration.nextElement();
                Object requestValue = hRequest.getParameter(requestElement);

                DEBUGGER.debug("Parameter: " + requestElement + "; Value: " + requestValue);
            }
        }

        if (this.appConfig.getServices().get(this.serviceName))
        {
            this.searchValidator.validate(request, bindResult);

            if (bindResult.hasErrors())
            {
                // validation failed
                ERROR_RECORDER.error("Errors: {}", bindResult.getAllErrors());

                mView.addObject(Constants.ERROR_MESSAGE, this.appConfig.getMessageValidationFailed());
                mView.addObject("searchTypes", SearchRequestType.values());
                mView.addObject("command", new SearchRequest());
                mView.setViewName(this.defaultPage);

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

                SearchResponse searchRes = searchProcessor.doApplicationSearch(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("SearchResponse: {}", searchRes);
                }

                if (searchRes.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    mView.addObject("pages", (int) Math.ceil(searchRes.getEntryCount() * 1.0 / this.recordsPerPage));
                    mView.addObject("page", 1);
                    mView.addObject("searchTerms", request.getSearchTerms());
                    mView.addObject("searchType", request.getSearchType());
                    mView.addObject(Constants.SEARCH_RESULTS, searchRes.getResults());
                    mView.addObject("searchTypes", SearchRequestType.values());
                    mView.addObject("command", new SearchRequest());
                    mView.setViewName(this.defaultPage);
                }
                else if (searchRes.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                {
                    mView.setViewName(this.appConfig.getUnauthorizedPage());
                }
                else
                {
                    mView.addObject(Constants.ERROR_RESPONSE, searchRes.getResponse());
                    mView.addObject("searchTypes", SearchRequestType.values());
                    mView.addObject("command", new SearchRequest());
                    mView.setViewName(this.defaultPage);
                }
            }
            catch (SearchRequestException srx)
            {
                ERROR_RECORDER.error(srx.getMessage(), srx);

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

    @RequestMapping(value = "/validate-dmgr", method = RequestMethod.POST)
    public final ModelAndView validateSelectedManager(@ModelAttribute("request") final Server request, final BindingResult bindResult)
    {
        final String methodName = ServiceManagementController.CNAME + "#validateSelectedManager(@ModelAttribute(\"request\") final Server request, final BindingResult bindResult)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Server: {}", request);
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
            @SuppressWarnings("unchecked") Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked") Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }

            DEBUGGER.debug("Dumping request parameters:");
            @SuppressWarnings("unchecked") Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String requestElement = paramsEnumeration.nextElement();
                Object requestValue = hRequest.getParameter(requestElement);

                DEBUGGER.debug("Parameter: " + requestElement + "; Value: " + requestValue);
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

                ServerManagementRequest dmgrServerRequest = new ServerManagementRequest();
                dmgrServerRequest.setTargetServer(request);
                dmgrServerRequest.setRequestInfo(reqInfo);
                dmgrServerRequest.setUserAccount(userAccount);
                dmgrServerRequest.setServiceId(this.systemMgmt);
                dmgrServerRequest.setApplicationId(this.appConfig.getApplicationId());
                dmgrServerRequest.setApplicationName(this.appConfig.getApplicationName());

                if (DEBUG)
                {
                    DEBUGGER.debug("ServerManagementRequest: {}", dmgrServerRequest);
                }

                ServerManagementResponse dmgrServerResponse = processor.getServerData(dmgrServerRequest);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServerManagementResponse: {}", dmgrServerResponse);
                }

                if (dmgrServerResponse.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    Server targetDmgr = dmgrServerResponse.getServer();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Server: {}", targetDmgr);
                    }

                    PlatformRequest platformRequest = new PlatformRequest();
                    platformRequest.setPlatformDmgr(targetDmgr.getServerGuid());
                    platformRequest.setPlatformDmgrName(targetDmgr.getOperHostName());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("PlatformRequest: {}", platformRequest);
                    }

                    ServerManagementRequest appServerRequest = new ServerManagementRequest();
                    appServerRequest.setTargetServer(targetDmgr);
                    appServerRequest.setRequestInfo(reqInfo);
                    appServerRequest.setUserAccount(userAccount);
                    appServerRequest.setServiceId(this.systemMgmt);
                    appServerRequest.setApplicationId(this.appConfig.getApplicationId());
                    appServerRequest.setApplicationName(this.appConfig.getApplicationName());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("ServerManagementRequest: {}", appServerRequest);
                    }

                    // get the list of appservers associated with this dmgr
                    ServerManagementResponse appServerResponse = processor.listServersByDmgr(appServerRequest);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("ServerManagementResponse: {}", appServerResponse);
                    }

                    if (appServerResponse.getRequestStatus() == CoreServicesStatus.SUCCESS)
                    {
                        // xlnt
                        List<Server> appServerList = appServerResponse.getServerList();

                        if (DEBUG)
                        {
                            DEBUGGER.debug("List<Server>: {}", appServerList);
                        }

                        mView.addObject("appServerList", appServerList);
                    }
                    else if (appServerResponse.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                    {
                        mView.setViewName(this.appConfig.getUnauthorizedPage());

                        return mView;
                    }

                    // get the list of webservers associated with this dmgr
                    Server webserver = new Server();
                    webserver.setServerType(ServerType.WEBSERVER);
                    webserver.setDatacenter(targetDmgr.getDatacenter());
                    webserver.setNetworkPartition(targetDmgr.getNetworkPartition());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Server: {}", webserver);
                    }

                    ServerManagementRequest webRequest = new ServerManagementRequest();
                    webRequest.setTargetServer(webserver);
                    webRequest.setRequestInfo(reqInfo);
                    webRequest.setUserAccount(userAccount);
                    webRequest.setServiceId(this.systemMgmt);
                    webRequest.setApplicationId(this.appConfig.getApplicationId());
                    webRequest.setApplicationName(this.appConfig.getApplicationName());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("ServerManagementRequest: {}", webRequest);
                    }

                    ServerManagementResponse webResponse = processor.listServersByType(webRequest);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("ServerManagementResponse: {}", webResponse);
                    }

                    if (webResponse.getRequestStatus() == CoreServicesStatus.SUCCESS)
                    {
                        // xlnt
                        List<Server> webServerList = webResponse.getServerList();

                        if (DEBUG)
                        {
                            DEBUGGER.debug("List<Server>: {}", webServerList);
                        }

                        mView.addObject("webServerList", webServerList);
                    }
                    else if (webResponse.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                    {
                        mView.setViewName(this.appConfig.getUnauthorizedPage());

                        return mView;
                    }

                    mView.addObject("statusList", ServiceStatus.values());
                    mView.addObject("command", platformRequest);
                    mView.setViewName(this.addPlatformPage);
                }
                else if (dmgrServerResponse.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                {
                    mView.setViewName(this.appConfig.getUnauthorizedPage());
                }
                else
                {
                    mView.addObject(Constants.ERROR_MESSAGE, this.messageNoDmgrFound);
                    mView.setViewName(this.appConfig.getErrorResponsePage());
                }
            }
            catch (ServerManagementException smx)
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
        final IServerManagementProcessor serverMgr = new ServerManagementProcessorImpl();
        final IPlatformManagementProcessor processor = new PlatformManagementProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            @SuppressWarnings("unchecked") Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked") Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }

            DEBUGGER.debug("Dumping request parameters:");
            @SuppressWarnings("unchecked") Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String requestElement = paramsEnumeration.nextElement();
                Object requestValue = hRequest.getParameter(requestElement);

                DEBUGGER.debug("Parameter: " + requestElement + "; Value: " + requestValue);
            }
        }

        if (this.appConfig.getServices().get(this.serviceName))
        {
            try
            {
                this.platformValidator.validate(request, bindResult);

                if (bindResult.hasErrors())
                {
                    RequestHostInfo reqInfo = new RequestHostInfo();
                    reqInfo.setHostName(hRequest.getRemoteHost());
                    reqInfo.setHostAddress(hRequest.getRemoteAddr());
                    reqInfo.setSessionId(hSession.getId());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
                    }

                    Server dmgrServer = new Server();
                    dmgrServer.setServerGuid(request.getPlatformDmgr());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Server: {}", dmgrServer);
                    }

                    ServerManagementRequest dmgrServerRequest = new ServerManagementRequest();
                    dmgrServerRequest.setTargetServer(dmgrServer);
                    dmgrServerRequest.setRequestInfo(reqInfo);
                    dmgrServerRequest.setUserAccount(userAccount);
                    dmgrServerRequest.setServiceId(this.systemMgmt);
                    dmgrServerRequest.setApplicationId(this.appConfig.getApplicationId());
                    dmgrServerRequest.setApplicationName(this.appConfig.getApplicationName());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("ServerManagementRequest: {}", dmgrServerRequest);
                    }

                    ServerManagementResponse dmgrServerResponse = serverMgr.getServerData(dmgrServerRequest);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("ServerManagementResponse: {}", dmgrServerResponse);
                    }

                    if (dmgrServerResponse.getRequestStatus() == CoreServicesStatus.SUCCESS)
                    {
                        Server targetDmgr = dmgrServerResponse.getServer();

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Server: {}", targetDmgr);
                        }

                        PlatformRequest platformRequest = new PlatformRequest();
                        platformRequest.setPlatformDmgr(targetDmgr.getServerGuid());
                        platformRequest.setPlatformDmgrName(targetDmgr.getOperHostName());

                        if (DEBUG)
                        {
                            DEBUGGER.debug("PlatformRequest: {}", platformRequest);
                        }

                        ServerManagementRequest appServerRequest = new ServerManagementRequest();
                        appServerRequest.setTargetServer(targetDmgr);
                        appServerRequest.setRequestInfo(reqInfo);
                        appServerRequest.setUserAccount(userAccount);
                        appServerRequest.setServiceId(this.systemMgmt);
                        appServerRequest.setApplicationId(this.appConfig.getApplicationId());
                        appServerRequest.setApplicationName(this.appConfig.getApplicationName());

                        if (DEBUG)
                        {
                            DEBUGGER.debug("ServerManagementRequest: {}", appServerRequest);
                        }

                        // get the list of appservers associated with this dmgr
                        ServerManagementResponse appServerResponse = serverMgr.listServersByType(appServerRequest);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("ServerManagementResponse: {}", appServerResponse);
                        }

                        if (appServerResponse.getRequestStatus() == CoreServicesStatus.SUCCESS)
                        {
                            // xlnt
                            List<Server> appServerList = appServerResponse.getServerList();

                            if (DEBUG)
                            {
                                DEBUGGER.debug("List<Server>: {}", appServerList);
                            }

                            mView.addObject("appServerList", appServerList);
                        }
                        else if (appServerResponse.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                        {
                            mView.setViewName(this.appConfig.getUnauthorizedPage());

                            return mView;
                        }

                        // get the list of webservers associated with this dmgr
                        Server webserver = new Server();
                        webserver.setServerType(ServerType.WEBSERVER);
                        webserver.setDatacenter(targetDmgr.getDatacenter());
                        webserver.setNetworkPartition(targetDmgr.getNetworkPartition());

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Server: {}", webserver);
                        }

                        ServerManagementRequest webRequest = new ServerManagementRequest();
                        webRequest.setTargetServer(webserver);
                        webRequest.setRequestInfo(reqInfo);
                        webRequest.setUserAccount(userAccount);
                        webRequest.setServiceId(this.systemMgmt);
                        webRequest.setApplicationId(this.appConfig.getApplicationId());
                        webRequest.setApplicationName(this.appConfig.getApplicationName());

                        if (DEBUG)
                        {
                            DEBUGGER.debug("ServerManagementRequest: {}", webRequest);
                        }

                        ServerManagementResponse webResponse = serverMgr.listServersByType(webRequest);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("ServerManagementResponse: {}", webResponse);
                        }

                        if (webResponse.getRequestStatus() == CoreServicesStatus.SUCCESS)
                        {
                            // xlnt
                            List<Server> webServerList = webResponse.getServerList();

                            if (DEBUG)
                            {
                                DEBUGGER.debug("List<Server>: {}", webServerList);
                            }

                            mView.addObject("webServerList", webServerList);
                        }
                        else if (webResponse.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                        {
                            mView.setViewName(this.appConfig.getUnauthorizedPage());

                            return mView;
                        }

                        // something was missing from the request
                        mView.addObject(Constants.ERROR_MESSAGE, this.appConfig.getMessageValidationFailed());
                        mView.addObject("statusList", ServiceStatus.values());
                        mView.addObject("command", request);
                        mView.setViewName(this.addPlatformPage);

                        return mView;
                    }
                    else if (dmgrServerResponse.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                    {
                        mView.setViewName(this.appConfig.getUnauthorizedPage());

                        return mView;
                    }
                }

                RequestHostInfo reqInfo = new RequestHostInfo();
                reqInfo.setHostName(hRequest.getRemoteHost());
                reqInfo.setHostAddress(hRequest.getRemoteAddr());
                reqInfo.setSessionId(hSession.getId());

                if (DEBUG)
                {
                    DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
                }

                Server dmgrServer = new Server();
                dmgrServer.setServerGuid(request.getPlatformDmgr());

                if (DEBUG)
                {
                    DEBUGGER.debug("Server: {}", dmgrServer);
                }

                ServerManagementRequest dmgrRequest = new ServerManagementRequest();
                dmgrRequest.setRequestInfo(reqInfo);
                dmgrRequest.setServiceId(this.systemMgmt);
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
                    Server targetDmgr = dmgrResponse.getServer();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Server: {}", targetDmgr);
                    }

                    List<Server> appServers = new ArrayList<>();

                    // get the servers
                    for (String guid : request.getAppServers())
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

                    List<Server> webServers = new ArrayList<>();

                    // get the servers
                    for (String guid : request.getWebServers())
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

                        webServers.add(server);
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("List<Server>: {}", webServers);
                    }

                    Platform platform = new Platform();
                    platform.setAppServers(appServers);
                    platform.setDescription(request.getDescription());
                    platform.setPlatformDmgr(targetDmgr);
                    platform.setPlatformName(request.getPlatformName());
                    platform.setPlatformRegion(targetDmgr.getServerRegion());
                    platform.setStatus(request.getStatus());
                    platform.setWebServers(webServers);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Platform: {}", platform);
                    }

                    PlatformManagementRequest platformRequest = new PlatformManagementRequest();
                    platformRequest.setPlatform(platform);
                    platformRequest.setRequestInfo(reqInfo);
                    platformRequest.setServiceId(this.platformMgmt);
                    platformRequest.setUserAccount(userAccount);
                    platformRequest.setApplicationId(this.appConfig.getApplicationId());
                    platformRequest.setApplicationName(this.appConfig.getApplicationName());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("PlatformManagementRequest: {}", platformRequest);
                    }

                    PlatformManagementResponse platformResponse = processor.addNewPlatform(platformRequest);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("PlatformManagementResponse: {}", platformResponse);
                    }

                    if (platformResponse.getRequestStatus() == CoreServicesStatus.SUCCESS)
                    {
                        mView.addObject(Constants.RESPONSE_MESSAGE, this.messagePlatformSuccessfullyAdded);
                    }
                    else
                    {
                        mView.addObject(Constants.ERROR_RESPONSE, platformResponse.getResponse());
                    }
                }
                else if (dmgrResponse.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                {
                    mView.setViewName(this.appConfig.getUnauthorizedPage());

                    return mView;
                }
                else
                {
                    // selected dmgr was invalid
                    mView.addObject(Constants.ERROR_MESSAGE, this.messageNoDmgrFound);
                }

                mView = new ModelAndView(new RedirectView());
                mView.setViewName(this.selectDmgrPage);
            }
            catch (ServerManagementException smx)
            {
                ERROR_RECORDER.error(smx.getMessage(), smx);

                mView.setViewName(this.appConfig.getErrorResponsePage());
            }
            catch (PlatformManagementException pmx)
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
    public final ModelAndView doAddDatacenter(@ModelAttribute("request") final DataCenter request, final BindingResult bindResult)
    {
        final String methodName = ServiceManagementController.CNAME + "#doAddDatacenter(@ModelAttribute(\"request\") final DataCenter request, final BindingResult bindResult)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", request);
            DEBUGGER.debug("Value: {}", bindResult);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IDatacenterManagementProcessor processor = new DatacenterManagementProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            @SuppressWarnings("unchecked") Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked") Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }

            DEBUGGER.debug("Dumping request parameters:");
            @SuppressWarnings("unchecked") Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String requestElement = paramsEnumeration.nextElement();
                Object requestValue = hRequest.getParameter(requestElement);

                DEBUGGER.debug("Parameter: " + requestElement + "; Value: " + requestValue);
            }
        }

        if (this.appConfig.getServices().get(this.serviceName))
        {
            this.datacenterValidator.validate(request, bindResult);

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

                DatacenterManagementRequest dcRequest = new DatacenterManagementRequest();
                dcRequest.setDataCenter(request);
                dcRequest.setRequestInfo(reqInfo);
                dcRequest.setServiceId(this.dcService);
                dcRequest.setUserAccount(userAccount);
                dcRequest.setApplicationId(this.appConfig.getApplicationId());
                dcRequest.setApplicationName(this.appConfig.getApplicationName());

                if (DEBUG)
                {
                    DEBUGGER.debug("DatacenterManagementRequest: {}", dcRequest);
                }

                DatacenterManagementResponse dcResponse = processor.addNewDatacenter(dcRequest);

                if (DEBUG)
                {
                    DEBUGGER.debug("DatacenterManagementResponse: {}", dcResponse);
                }

                if (dcResponse.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    // return to add dc page
                    mView.addObject(Constants.RESPONSE_MESSAGE, this.messageDatacenterSuccessfullyAdded);
                    mView.addObject("command", new DataCenter());
                    mView.setViewName(this.addDatacenterPage);
                }
                else if (dcResponse.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                {
                    mView.setViewName(this.appConfig.getUnauthorizedPage());
                }
                else
                {
                    mView.addObject(Constants.ERROR_RESPONSE, dcResponse.getResponse());
                    mView.addObject("command", request);
                    mView.setViewName(this.addDatacenterPage);
                }
            }
            catch (DatacenterManagementException dmx)
            {
                ERROR_RECORDER.error(dmx.getMessage(), dmx);

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

    @RequestMapping(value = "/add-project", method = RequestMethod.POST)
    public final ModelAndView doAddProject(@ModelAttribute("request") final ProjectRequest request, final BindingResult bindResult)
    {
        final String methodName = ServiceManagementController.CNAME + "#doAddProject(@ModelAttribute(\"request\") final ProjectRequest request, final BindingResult bindResult)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ProjectRequest: {}", request);
            DEBUGGER.debug("BindingResult: {}", bindResult);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IProjectManagementProcessor projectMgr = new ProjectManagementProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            @SuppressWarnings("unchecked") Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked") Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }

            DEBUGGER.debug("Dumping request parameters:");
            @SuppressWarnings("unchecked") Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String requestElement = paramsEnumeration.nextElement();
                Object requestValue = hRequest.getParameter(requestElement);

                DEBUGGER.debug("Parameter: " + requestElement + "; Value: " + requestValue);
            }
        }

        if (this.appConfig.getServices().get(this.serviceName))
        {
            this.projectValidator.validate(request, bindResult);

            if (bindResult.hasErrors())
            {
                try
                {
                    RequestHostInfo reqInfo = new RequestHostInfo();
                    reqInfo.setHostAddress(hRequest.getRemoteAddr());
                    reqInfo.setHostName(hRequest.getRemoteHost());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
                    }

                    // search accounts
                    AccountControlRequest accRequest = new AccountControlRequest();
                    accRequest.setHostInfo(reqInfo);
                    accRequest.setApplicationId(this.appConfig.getApplicationName());
                    accRequest.setControlType(ControlType.SERVICES);
                    accRequest.setRequestor(userAccount);
                    accRequest.setIsLogonRequest(false);
                    accRequest.setApplicationId(this.appConfig.getApplicationId());
                    accRequest.setApplicationName(this.appConfig.getApplicationName());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("AccountControlRequest: {}", request);
                    }

                    IAccountControlProcessor processor = new AccountControlProcessorImpl();
                    AccountControlResponse response = processor.searchAccounts(accRequest);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("AccountControlResponse: {}", response);
                    }

                    if (response.getRequestStatus() == SecurityRequestStatus.SUCCESS)
                    {
                        if ((response.getUserList() != null) && (response.getUserList().size() != 0))
                        {
                            List<UserAccount> userList = response.getUserList();

                            if (DEBUG)
                            {
                                DEBUGGER.debug("List<UserAccount> {}", userList);
                            }

                            mView.addObject("userList", userList);
                            mView.addObject("statusList", ServiceStatus.values());
                            mView.addObject("command", new ProjectRequest());
                            mView.addObject(Constants.ERROR_MESSAGE, this.appConfig.getMessageValidationFailed());
                            mView.setViewName(this.addProjectPage);
                        }
                        else
                        {
                            mView.addObject(Constants.ERROR_RESPONSE, response.getResponse());
                            mView.setViewName(this.defaultPage);
                        }
                    }
                    else if (response.getRequestStatus() == SecurityRequestStatus.UNAUTHORIZED)
                    {
                        mView.setViewName(this.appConfig.getUnauthorizedPage());
                    }
                    else
                    {
                        mView.addObject(Constants.ERROR_RESPONSE, response.getResponse());
                        mView.setViewName(this.defaultPage);
                    }
                }
                catch (AccountControlException acx)
                {
                    ERROR_RECORDER.error(acx.getMessage(), acx);
        
                    mView.setViewName(this.appConfig.getErrorResponsePage());
                }
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

                UserAccount primaryContact = new UserAccount();
                primaryContact.setGuid(request.getPrimaryContact());

                if (DEBUG)
                {
                    DEBUGGER.debug("UserAccount: {}", primaryContact);
                }

                UserAccount secondaryContact = new UserAccount();
                secondaryContact.setGuid(request.getPrimaryContact());

                if (DEBUG)
                {
                    DEBUGGER.debug("UserAccount: {}", secondaryContact);
                }

                Project reqProject = new Project();
                reqProject.setProjectCode(request.getProjectCode());
                reqProject.setChangeQueue(request.getChangeQueue());
                reqProject.setDevEmail(request.getDevEmail());
                reqProject.setProdEmail(request.getProdEmail());
                reqProject.setIncidentQueue(request.getIncidentQueue());
                reqProject.setPrimaryContact(primaryContact);
                reqProject.setSecondaryContact(secondaryContact);
                reqProject.setProjectStatus(request.getProjectStatus());

                if (DEBUG)
                {
                    DEBUGGER.debug("Project: {}", reqProject);
                }

                ProjectManagementRequest projectRequest = new ProjectManagementRequest();
                projectRequest.setRequestInfo(reqInfo);
                projectRequest.setUserAccount(userAccount);
                projectRequest.setServiceId(this.projectMgmt);
                projectRequest.setProject(reqProject);
                projectRequest.setApplicationId(this.appConfig.getApplicationId());
                projectRequest.setApplicationName(this.appConfig.getApplicationName());

                if (DEBUG)
                {
                    DEBUGGER.debug("ProjectManagementRequest: {}", projectRequest);
                }

                ProjectManagementResponse projectResponse = projectMgr.addNewProject(projectRequest);

                if (DEBUG)
                {
                    DEBUGGER.debug("ProjectManagementResponse: {}", projectResponse);
                }

                if (projectResponse.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    mView.addObject(Constants.RESPONSE_MESSAGE, this.messageProjectSuccessfullyAdded);
                }
                else if (projectResponse.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                {
                    mView.setViewName(this.appConfig.getUnauthorizedPage());
                }
                else
                {
                    mView.addObject(Constants.ERROR_RESPONSE, projectResponse.getResponse());
                }

                mView.addObject("command", new Project());
                mView.setViewName(this.addProjectPage);
            }
            catch (ProjectManagementException pmx)
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
}
