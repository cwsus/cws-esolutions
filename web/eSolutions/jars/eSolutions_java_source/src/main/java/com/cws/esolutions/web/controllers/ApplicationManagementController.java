/*
 * Copyright (c) 2009 - 2020 CaspersBox Web Services
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.cws.esolutions.web.controllers;
/*
 * Project: eSolutions_java_source
 * Package: com.cws.esolutions.web.controllers
 * File: ApplicationManagementController.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Enumeration;
import org.springframework.ui.Model;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.cws.esolutions.web.Constants;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.web.ApplicationServiceBean;
import com.cws.esolutions.core.processors.dto.Platform;
import com.cws.esolutions.core.enums.CoreServicesStatus;
import com.cws.esolutions.core.processors.dto.Application;
import com.cws.esolutions.web.validators.ApplicationValidator;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.dto.PlatformManagementRequest;
import com.cws.esolutions.core.processors.dto.PlatformManagementResponse;
import com.cws.esolutions.core.processors.dto.ApplicationManagementRequest;
import com.cws.esolutions.core.processors.dto.ApplicationManagementResponse;
import com.cws.esolutions.core.processors.impl.PlatformManagementProcessorImpl;
import com.cws.esolutions.core.processors.exception.PlatformManagementException;
import com.cws.esolutions.core.processors.interfaces.IPlatformManagementProcessor;
import com.cws.esolutions.core.processors.impl.ApplicationManagementProcessorImpl;
import com.cws.esolutions.core.processors.exception.ApplicationManagementException;
import com.cws.esolutions.core.processors.interfaces.IApplicationManagementProcessor;
/**
 * @author cws-khuntly
 * @version 1.0
 * @see org.springframework.stereotype.Controller
 */
@Controller
@RequestMapping("/application-management")
public class ApplicationManagementController
{
    private String serviceId = null;
    private int recordsPerPage = 20; // default to 20
    private String addAppPage = null;
    private String defaultPage = null;
    private String viewAppPage = null;
    private String viewFilePage = null;
    private String platformMgmt = null;
    private String messageNoFileData = null;
    private String retrieveFilesPage = null;
    private String addPlatformRedirect = null;
    private String viewApplicationsPage = null;
    private String addApplicationRedirect = null;
    private String messageApplicationAdded = null;
    private ApplicationValidator validator = null;
    private ApplicationServiceBean appConfig = null;
    private String messageNoPlatformAssigned = null;
    private String messageApplicationRetired = null;
    private String messageNoApplicationsFound = null;

    private static final String CNAME = ApplicationManagementController.class.getName();

    private static final Logger DEBUGGER = LogManager.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LogManager.getLogger(Constants.ERROR_LOGGER + CNAME);

    public final void setValidator(final ApplicationValidator value)
    {
        final String methodName = ApplicationManagementController.CNAME + "#setValidator(final ApplicationValidator value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.validator = value;
    }

    public final void setAppConfig(final ApplicationServiceBean value)
    {
        final String methodName = ApplicationManagementController.CNAME + "#setAppConfig(final ApplicationServiceBean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.appConfig = value;
    }

    public final void setAddAppPage(final String value)
    {
        final String methodName = ApplicationManagementController.CNAME + "#setAddAppPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.addAppPage = value;
    }

    public final void setRecordsPerPage(final int value)
    {
        final String methodName = ApplicationManagementController.CNAME + "#setRecordsPerPage(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.recordsPerPage = value;
    }

    public final void setDefaultPage(final String value)
    {
        final String methodName = ApplicationManagementController.CNAME + "#setDefaultPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.defaultPage = value;
    }

    public final void setViewAppPage(final String value)
    {
        final String methodName = ApplicationManagementController.CNAME + "#setViewAppPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.viewAppPage = value;
    }

    public final void setViewFilePage(final String value)
    {
        final String methodName = ApplicationManagementController.CNAME + "#setViewFilePage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.viewFilePage = value;
    }

    public final void setRetrieveFilesPage(final String value)
    {
        final String methodName = ApplicationManagementController.CNAME + "#setRetrieveFilesPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.retrieveFilesPage = value;
    }

    public final void setServiceId(final String value)
    {
        final String methodName = ApplicationManagementController.CNAME + "#setServiceId(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serviceId = value;
    }

    public final void setAddPlatformRedirect(final String value)
    {
        final String methodName = ApplicationManagementController.CNAME + "#setAddPlatformRedirect(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.addPlatformRedirect = value;
    }

    public final void setViewApplicationsPage(final String value)
    {
        final String methodName = ApplicationManagementController.CNAME + "#setViewApplicationsPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.viewApplicationsPage = value;
    }

    public final void setMessageNoApplicationsFound(final String value)
    {
        final String methodName = ApplicationManagementController.CNAME + "#setMessageNoApplicationsFound(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageNoApplicationsFound = value;
    }

    public final void setMessageNoFileData(final String value)
    {
        final String methodName = ApplicationManagementController.CNAME + "#setMessageNoFileData(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageNoFileData = value;
    }

    public final void setMessageNoPlatformAssigned(final String value)
    {
        final String methodName = ApplicationManagementController.CNAME + "#setMessageNoPlatformAssigned(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageNoPlatformAssigned = value;
    }

    public final void setMessageApplicationAdded(final String value)
    {
        final String methodName = ApplicationManagementController.CNAME + "#setMessageApplicationAdded(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageApplicationAdded = value;
    }

    public final void setMessageApplicationRetired(final String value)
    {
        final String methodName = ApplicationManagementController.CNAME + "#setMessageApplicationRetired(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageApplicationRetired = value;
    }

    public final void setAddApplicationRedirect(final String value)
    {
        final String methodName = ApplicationManagementController.CNAME + "#setAddApplicationRedirect(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.addApplicationRedirect = value;
    }

    @RequestMapping(value = "default", method = RequestMethod.GET)
    public final ModelAndView showDefaultPage(final Model model)
    {
        final String methodName = ApplicationManagementController.CNAME + "#showDefaultPage(final Model model)";

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

        mView.addObject(Constants.COMMAND, new Application());
		mView.setViewName(this.defaultPage);

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "search/terms/{terms}/page/{page}", method = RequestMethod.GET)
    public final ModelAndView showSearchPage(@PathVariable("terms") final String terms, @PathVariable("page") final int page, final Model model)
    {
        final String methodName = ApplicationManagementController.CNAME + "#showSearchPage(@PathVariable(\"terms\") final String terms, @PathVariable(\"page\") final int page, final Model model)";

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
        final IApplicationManagementProcessor appProcessor = (IApplicationManagementProcessor) new ApplicationManagementProcessorImpl();

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

		try
        {
            RequestHostInfo reqInfo = new RequestHostInfo();
            reqInfo.setHostName(hRequest.getRemoteHost());
            reqInfo.setHostAddress(hRequest.getRemoteAddr());

            if (DEBUG)
            {
                DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            }

            Application application = new Application();
            application.setName(terms);

            if (DEBUG)
            {
                DEBUGGER.debug("Application: {}", application);
            }

            ApplicationManagementRequest appRequest = new ApplicationManagementRequest();
            appRequest.setApplication(application);
            appRequest.setApplicationId(this.appConfig.getApplicationId());
            appRequest.setApplicationName(this.appConfig.getApplicationName());
            appRequest.setRequestInfo(reqInfo);
            appRequest.setServiceId(this.serviceId);
            appRequest.setUserAccount(userAccount);

            if (DEBUG)
            {
                DEBUGGER.debug("ApplicationManagementRequest: {}", appRequest);
            }

            ApplicationManagementResponse appResponse = appProcessor.listApplications(appRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("ApplicationManagementResponse: {}", appResponse);
            }

            switch (appResponse.getRequestStatus())
            {
				case EXCEPTION:
					mView.setViewName(this.appConfig.getErrorResponsePage());

					break;
				case FAILURE:
	            	mView.addObject(Constants.ERROR_RESPONSE, this.appConfig.getMessageNoSearchResults());
	            	mView.addObject(Constants.COMMAND, new Application());
	            	mView.setViewName(this.defaultPage);

					break;
				case SUCCESS:
	                mView.addObject("pages", (int) Math.ceil(appResponse.getEntryCount() * 1.0 / this.recordsPerPage));
	                mView.addObject("page", page);
	                mView.addObject("searchTerms", terms);
	                mView.addObject(Constants.SEARCH_RESULTS, appResponse.getApplicationList());
	                mView.addObject(Constants.COMMAND, new Application());
	                mView.setViewName(this.defaultPage);

					break;
				case UNAUTHORIZED:
					mView.setViewName(this.appConfig.getUnauthorizedPage());

					break;
				default:
	            	mView.addObject(Constants.ERROR_RESPONSE, this.appConfig.getMessageNoSearchResults());
	            	mView.addObject(Constants.COMMAND, new Application());
	            	mView.setViewName(this.defaultPage);

					break;
            }
        }
        catch (final ApplicationManagementException amx)
        {
            ERROR_RECORDER.error(amx.getMessage(), amx);

            mView.setViewName(this.appConfig.getErrorResponsePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "list-applications", method = RequestMethod.GET)
    public final ModelAndView listInstalledApplications(final Model model)
    {
        final String methodName = ApplicationManagementController.CNAME + "#listInstalledApplications(final Model model)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IApplicationManagementProcessor appMgr = (IApplicationManagementProcessor) new ApplicationManagementProcessorImpl();

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

        try
        {
            RequestHostInfo reqInfo = new RequestHostInfo();
            reqInfo.setHostName(hRequest.getRemoteHost());
            reqInfo.setHostAddress(hRequest.getRemoteAddr());

            if (DEBUG)
            {
                DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            }

            ApplicationManagementRequest appRequest = new ApplicationManagementRequest();
            appRequest.setRequestInfo(reqInfo);
            appRequest.setServiceId(this.serviceId);
            appRequest.setUserAccount(userAccount);
            appRequest.setApplicationId(this.appConfig.getApplicationId());
            appRequest.setApplicationName(this.appConfig.getApplicationName());

            if (DEBUG)
            {
                DEBUGGER.debug("ApplicationManagementRequest: {}", appRequest);
            }

            ApplicationManagementResponse appResponse = appMgr.listApplications(appRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("ApplicationManagementResponse: {}", appResponse);
            }

            switch (appResponse.getRequestStatus())
            {
			case EXCEPTION:
				mView.setViewName(this.appConfig.getErrorResponsePage());

				break;
			case FAILURE:
                mView.addObject(Constants.ERROR_RESPONSE, this.appConfig.getMessageNoSearchResults());
                mView.setViewName(this.addApplicationRedirect);

				break;
			case SUCCESS:
                List<Application> applicationList = appResponse.getApplicationList();

                if (DEBUG)
                {
                    DEBUGGER.debug("List<Application>: {}", applicationList);
                }

                if ((applicationList != null) && (applicationList.size() != 0))
                {
                    mView.addObject("applicationList", applicationList);
                    mView.setViewName(this.viewApplicationsPage);
                }
                else
                {
                    mView.addObject(Constants.ERROR_MESSAGE, this.messageNoApplicationsFound);
                    mView.setViewName(this.defaultPage);
                }

				break;
			case UNAUTHORIZED:
				mView.setViewName(this.appConfig.getUnauthorizedPage());

				break;
			default:
                mView.addObject(Constants.ERROR_RESPONSE, this.appConfig.getMessageNoSearchResults());
                mView.setViewName(this.addApplicationRedirect);

				break;
            
            }
        }
        catch (final ApplicationManagementException amx)
        {
            ERROR_RECORDER.error(amx.getMessage(), amx);

            mView.setViewName(this.appConfig.getErrorResponsePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "application/{request}", method = RequestMethod.GET)
    public final ModelAndView showApplication(@PathVariable("request") final String request, final Model model)
    {
        final String methodName = ApplicationManagementController.CNAME + "#showApplication(@PathVariable(\"request\") final String request, final Model model)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("request: {}", request);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IApplicationManagementProcessor appMgr = (IApplicationManagementProcessor) new ApplicationManagementProcessorImpl();

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

        try
        {
            RequestHostInfo reqInfo = new RequestHostInfo();
            reqInfo.setHostName(hRequest.getRemoteHost());
            reqInfo.setHostAddress(hRequest.getRemoteAddr());

            if (DEBUG)
            {
                DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            }

            Application reqApplication = new Application();
            reqApplication.setGuid(request);

            if (DEBUG)
            {
                DEBUGGER.debug("Application: {}", reqApplication);
            }

            // get a list of available servers
            ApplicationManagementRequest appRequest = new ApplicationManagementRequest();
            appRequest.setRequestInfo(reqInfo);
            appRequest.setUserAccount(userAccount);
            appRequest.setServiceId(this.serviceId);
            appRequest.setApplication(reqApplication);
            appRequest.setApplicationId(this.appConfig.getApplicationId());
            appRequest.setApplicationName(this.appConfig.getApplicationName());

            if (DEBUG)
            {
                DEBUGGER.debug("ApplicationManagementRequest: {}", appRequest);
            }

            ApplicationManagementResponse appResponse = appMgr.getApplicationData(appRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("ApplicationManagementResponse: {}", appResponse);
            }

            if (appResponse.getRequestStatus() == CoreServicesStatus.SUCCESS)
            {
                Application resApplication = appResponse.getApplication();

                if (DEBUG)
                {
                    DEBUGGER.debug("Application: {}", resApplication);
                }

                mView.addObject("application", resApplication);
                mView.setViewName(this.viewAppPage);
            }
            else if (appResponse.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
            {
                mView.setViewName(this.appConfig.getUnauthorizedPage());
            }
            else
            {
                mView.addObject(Constants.ERROR_RESPONSE, this.appConfig.getMessageNoSearchResults());
                mView.setViewName(this.defaultPage);
            }
        }
        catch (final ApplicationManagementException amx)
        {
            ERROR_RECORDER.error(amx.getMessage(), amx);

            mView.setViewName(this.appConfig.getErrorResponsePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "add-application", method = RequestMethod.GET)
    public ModelAndView showAddApplication(final Model model)
    {
        final String methodName = ApplicationManagementController.CNAME + "#showAddApplication(final Model model)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IPlatformManagementProcessor platformProcessor = (IPlatformManagementProcessor) new PlatformManagementProcessorImpl();

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

        RequestHostInfo reqInfo = new RequestHostInfo();
        reqInfo.setHostName(hRequest.getRemoteHost());
        reqInfo.setHostAddress(hRequest.getRemoteAddr());

        if (DEBUG)
        {
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        PlatformManagementRequest platformReq = new PlatformManagementRequest();
        platformReq.setRequestInfo(reqInfo);
        platformReq.setServiceId(this.platformMgmt);
        platformReq.setUserAccount(userAccount);
        platformReq.setApplicationId(this.appConfig.getApplicationId());
        platformReq.setApplicationName(this.appConfig.getApplicationName());

        if (DEBUG)
        {
            DEBUGGER.debug("PlatformManagementRequest: {}", platformReq);
        }

        try
        {
            PlatformManagementResponse platformResponse = platformProcessor.listPlatforms(platformReq);

            if (DEBUG)
            {
                DEBUGGER.debug("PlatformManagementResponse: {}", platformResponse);
            }

            switch (platformResponse.getRequestStatus())
            {
				case EXCEPTION:
					mView.setViewName(this.appConfig.getErrorResponsePage());

					break;
				case FAILURE:
					mView.setViewName(this.addPlatformRedirect);
					mView.addObject(Constants.COMMAND, new Application());

					break;
				case SUCCESS:
                    List<Platform> platformList = platformResponse.getPlatformList();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("platformList: {}", platformList);
                    }

                    if ((platformList != null) && (platformList.size() != 0))
                    {
                        Map<String, String> platformListing = new HashMap<String, String>();

                        for (Platform resPlatform : platformList)
                        {
                            if (DEBUG)
                            {
                                DEBUGGER.debug("Service: {}", resPlatform);
                            }

                            platformListing.put(resPlatform.getPlatformGuid(), resPlatform.getPlatformName());
                        }

                        if (DEBUG)
                        {
                            DEBUGGER.debug("platformListing: {}", platformListing);
                        }

                        mView.addObject("platformListing", platformListing);
                        mView.addObject(Constants.COMMAND, new Application());
                        mView.setViewName(this.addAppPage);
                    }

					break;
				case UNAUTHORIZED:
					mView.setViewName(this.appConfig.getUnauthorizedPage());

					break;
				default:
					mView.setViewName(this.addPlatformRedirect);
					mView.addObject(Constants.COMMAND, new Application());

					break;
            }
        }
        catch (final PlatformManagementException smx)
        {
            ERROR_RECORDER.error(smx.getMessage(), smx);

            mView.setViewName(this.appConfig.getErrorResponsePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "retire-application/application/{application}", method = RequestMethod.GET)
    public final ModelAndView showRetireApplication(@PathVariable("application") final String application, final Model model)
    {
        final String methodName = ApplicationManagementController.CNAME + "#showRetireApplication(@PathVariable(\"application\") final String application, final Model model)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", application);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IApplicationManagementProcessor appMgr = (IApplicationManagementProcessor) new ApplicationManagementProcessorImpl();

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

        try
        {
            RequestHostInfo reqInfo = new RequestHostInfo();
            reqInfo.setHostName(hRequest.getRemoteHost());
            reqInfo.setHostAddress(hRequest.getRemoteAddr());

            if (DEBUG)
            {
                DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            }

            Application reqApplication = new Application();
            reqApplication.setGuid(application);

            if (DEBUG)
            {
                DEBUGGER.debug("Application: {}", reqApplication);
            }

            // get a list of available servers
            ApplicationManagementRequest appRequest = new ApplicationManagementRequest();
            appRequest.setRequestInfo(reqInfo);
            appRequest.setUserAccount(userAccount);
            appRequest.setServiceId(this.serviceId);
            appRequest.setApplication(reqApplication);
            appRequest.setApplicationId(this.appConfig.getApplicationId());
            appRequest.setApplicationName(this.appConfig.getApplicationName());

            if (DEBUG)
            {
                DEBUGGER.debug("ApplicationManagementRequest: {}", appRequest);
            }

            ApplicationManagementResponse appRes = appMgr.deleteApplicationData(appRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("ApplicationManagementResponse: {}", appRes);
            }

            switch (appRes.getRequestStatus())
            {
				case EXCEPTION:
					mView.setViewName(this.appConfig.getErrorResponsePage());

					break;
				case FAILURE:
					mView.setViewName(this.defaultPage);

					break;
				case SUCCESS:
                    mView.addObject(Constants.RESPONSE_MESSAGE, this.messageApplicationRetired);
                    mView.setViewName(this.defaultPage);

					break;
				case UNAUTHORIZED:
					mView.setViewName(this.appConfig.getUnauthorizedPage());

					break;
				default:
					mView.setViewName(this.defaultPage);

					break;
            }
        }
        catch (final ApplicationManagementException amx)
        {
            ERROR_RECORDER.error(amx.getMessage(), amx);

            mView.setViewName(this.appConfig.getErrorResponsePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "search", method = RequestMethod.POST)
    public final ModelAndView submitApplicationSearch(@ModelAttribute("application") final Application application, final BindingResult bindResult, final Model model)
    {
        final String methodName = ApplicationManagementController.CNAME + "#submitApplicationSearch(@ModelAttribute(\"application\") final Application application, final BindingResult bindResult, final Model model)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("SearchRequest: {}", application);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IApplicationManagementProcessor processor = (IApplicationManagementProcessor) new ApplicationManagementProcessorImpl();

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

        try
        {
            RequestHostInfo reqInfo = new RequestHostInfo();
            reqInfo.setHostName(hRequest.getRemoteHost());
            reqInfo.setHostAddress(hRequest.getRemoteAddr());

            if (DEBUG)
            {
                DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            }

            ApplicationManagementRequest appRequest = new ApplicationManagementRequest();
            appRequest.setApplication(application);
            appRequest.setApplicationId(this.appConfig.getApplicationId());
            appRequest.setApplicationName(this.appConfig.getApplicationName());
            appRequest.setRequestInfo(reqInfo);
            appRequest.setServiceId(this.serviceId);
            appRequest.setUserAccount(userAccount);

            if (DEBUG)
            {
                DEBUGGER.debug("ApplicationManagementRequest: {}", appRequest);
            }

            ApplicationManagementResponse appResponse = processor.listApplications(appRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("ApplicationManagementResponse: {}", appResponse);
            }

            switch (appResponse.getRequestStatus())
            {
				case EXCEPTION:
					mView.setViewName(this.appConfig.getErrorResponsePage());

					break;
				case FAILURE:
                    mView.addObject(Constants.ERROR_RESPONSE, this.appConfig.getMessageNoSearchResults());
                    mView.addObject(Constants.COMMAND, new Application());
                    mView.setViewName(this.defaultPage);

					break;
				case SUCCESS:
                    mView.addObject("pages", (int) Math.ceil(appResponse.getEntryCount() * 1.0 / this.recordsPerPage));
                    mView.addObject("page", 1);
                    mView.addObject(Constants.SEARCH_RESULTS, appResponse.getApplicationList());
                    mView.setViewName(this.viewApplicationsPage);

					break;
				case UNAUTHORIZED:
					mView.setViewName(this.appConfig.getUnauthorizedPage());

					break;
				default:
                    mView.addObject(Constants.ERROR_RESPONSE, this.appConfig.getMessageNoSearchResults());
                    mView.addObject(Constants.COMMAND, new Application());
                    mView.setViewName(this.defaultPage);

					break;
            }
        }
        catch (final ApplicationManagementException amx)
        {
            ERROR_RECORDER.error(amx.getMessage(), amx);

            mView.setViewName(this.appConfig.getErrorResponsePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "add-application", method = RequestMethod.POST)
    public final ModelAndView doAddApplication(@ModelAttribute("request") final Application request, final BindingResult bindResult, final Model model)
    {
        final String methodName = ApplicationManagementController.CNAME + "#doAddApplication(@ModelAttribute(\"request\") final Application request, final BindingResult bindResult, final Model model)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", request);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IApplicationManagementProcessor processor = (IApplicationManagementProcessor) new ApplicationManagementProcessorImpl();

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

        this.validator.validate(request, bindResult);

        if (bindResult.hasErrors())
        {
            // validation failed
            ERROR_RECORDER.error("Errors: {}", bindResult.getAllErrors());

            mView.addObject(Constants.ERROR_MESSAGE, this.appConfig.getMessageValidationFailed());
            mView.addObject(Constants.BIND_RESULT, bindResult.getAllErrors());
            mView.addObject(Constants.COMMAND, new Application());
            mView.setViewName(this.addAppPage);
        }
        else
        {
            try
            {
                RequestHostInfo reqInfo = new RequestHostInfo();
                reqInfo.setHostName(hRequest.getRemoteHost());
                reqInfo.setHostAddress(hRequest.getRemoteAddr());

                if (DEBUG)
                {
                    DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
                }

                Platform platform = new Platform();
                platform.setPlatformGuid(request.getPlatformGuid());

                if (DEBUG)
                {
                	DEBUGGER.debug("Platform: {}", platform);
                }

                request.setPlatform(platform);

                if (DEBUG)
                {
                	DEBUGGER.debug("requestApp: {}", request);
                }

                ApplicationManagementRequest appRequest = new ApplicationManagementRequest();
                appRequest.setApplication(request);
                appRequest.setServiceId(this.serviceId);
                appRequest.setRequestInfo(reqInfo);
                appRequest.setUserAccount(userAccount);
                appRequest.setApplicationId(this.appConfig.getApplicationId());
                appRequest.setApplicationName(this.appConfig.getApplicationName());

                if (DEBUG)
                {
                    DEBUGGER.debug("ApplicationManagementRequest: {}", request);
                }

                ApplicationManagementResponse response = processor.addNewApplication(appRequest);

                if (DEBUG)
                {
                    DEBUGGER.debug("ApplicationManagementResponse: {}", response);
                }

                if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    // app added
                    mView.addObject(Constants.RESPONSE_MESSAGE, this.messageApplicationAdded);
                    mView.setViewName(this.addApplicationRedirect);
                }
                else if (response.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                {
                    mView.setViewName(this.appConfig.getUnauthorizedPage());
                }
                else
                {
                    mView.setViewName(this.appConfig.getErrorResponsePage());
                }
            }
            catch (final ApplicationManagementException amx)
            {
                ERROR_RECORDER.error(amx.getMessage(), amx);

                mView.setViewName(this.appConfig.getErrorResponsePage());
            }
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }
}
