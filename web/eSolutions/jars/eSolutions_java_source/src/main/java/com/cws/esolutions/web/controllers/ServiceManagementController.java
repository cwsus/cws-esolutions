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
 * File: ServiceManagementController.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.util.List;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.cws.esolutions.web.Constants;
import com.cws.esolutions.web.model.SearchRequest;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.web.ApplicationServiceBean;
import com.cws.esolutions.core.processors.dto.Service;
import com.cws.esolutions.core.enums.CoreServicesStatus;
import com.cws.esolutions.web.validators.ServiceValidator;
import com.cws.esolutions.core.processors.enums.ServiceType;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.dto.ServerManagementRequest;
import com.cws.esolutions.core.processors.dto.ServerManagementResponse;
import com.cws.esolutions.core.processors.dto.ServiceManagementRequest;
import com.cws.esolutions.core.processors.dto.ServiceManagementResponse;
import com.cws.esolutions.core.processors.dto.ApplicationEnablementRequest;
import com.cws.esolutions.core.processors.dto.ApplicationEnablementResponse;
import com.cws.esolutions.core.processors.impl.ServerManagementProcessorImpl;
import com.cws.esolutions.core.processors.impl.ServiceManagementProcessorImpl;
import com.cws.esolutions.core.processors.exception.ServerManagementException;
import com.cws.esolutions.core.processors.exception.ServiceManagementException;
import com.cws.esolutions.core.processors.interfaces.IServerManagementProcessor;
import com.cws.esolutions.core.processors.interfaces.IServiceManagementProcessor;
import com.cws.esolutions.core.processors.impl.ApplicationEnablementProcessorImpl;
import com.cws.esolutions.core.processors.exception.ApplicationEnablementException;
import com.cws.esolutions.core.processors.interfaces.IApplicationEnablementProcessor;

/**
 * @author cws-khuntly
 * @version 1.0
 * @see org.springframework.stereotype.Controller
 */
@Controller
@RequestMapping("service-management")
public class ServiceManagementController
{
    private int recordsPerPage = 20; // default to 20
    private String serviceId = null;
    private String serviceName = null;
    private String defaultPage = null;
    private String addServicePage = null;
    private String viewServicePage = null;
    private String viewServiceList = null;
    private String addServiceRedirect = null;
    private ServiceValidator validator = null;
    private String messageNoServicesFound = null;
    private String messageServiceAddSuccess = null;
    private String messageServiceAddFailure = null;
    private ApplicationServiceBean appConfig = null;

    private static final String CNAME = ServiceManagementController.class.getName();

    private static final Logger DEBUGGER = LogManager.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LogManager.getLogger(Constants.ERROR_LOGGER + CNAME);

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

    public final void setValidator(final ServiceValidator value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setValidator(final ServiceValidator value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.validator = value;
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

    public final void setAddServicePage(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setAddServicePage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.addServicePage = value;
    }

    public final void setViewServicePage(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setViewServicePage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.viewServicePage = value;
    }

    public final void setViewServiceList(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setViewServiceList(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.viewServiceList = value;
    }

    public final void setMessageServiceAddSuccess(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setMessageServiceAddSuccess(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageServiceAddSuccess = value;
    }

    public final void setMessageNoServicesFound(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setMessageNoServicesFound(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageNoServicesFound = value;
    }

    public final void setAddServiceRedirect(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setAddServiceRedirect(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.addServiceRedirect = value;
    }

    public final void setMessageServiceAddFailure(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setMessageServiceAddFailure(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageServiceAddFailure = value;
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

    @RequestMapping(value = "default", method = RequestMethod.GET)
    public final ModelAndView showDefaultPage(final Model model)
    {
        final String methodName = ServiceManagementController.CNAME + "#showDefaultPage(final Model model)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IApplicationEnablementProcessor enabler = (IApplicationEnablementProcessor) new ApplicationEnablementProcessorImpl();

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

        ApplicationEnablementRequest request = new ApplicationEnablementRequest();
        request.setApplicationId(this.appConfig.getApplicationId());
        request.setApplicationName(this.appConfig.getApplicationName());
        request.setServiceGuid(this.serviceId);
        request.setServiceName(this.serviceName);

        if (DEBUG)
        {
            DEBUGGER.debug("ApplicationEnablementRequest: {}", request);
        }

        try
        {
            ApplicationEnablementResponse response = enabler.isServiceEnabled(request);

            if (DEBUG)
            {
                DEBUGGER.debug("ApplicationEnablementResponse: {}", response);
            }

            switch (response.getRequestStatus())
            {
                case EXCEPTION:
                    mView.setViewName(this.appConfig.getErrorResponsePage());

                    break;
                case FAILURE:
                    mView.setViewName(this.appConfig.getErrorResponsePage());

                    break;
                case SUCCESS:
                    mView.addObject(Constants.COMMAND, new SearchRequest());
                    mView.setViewName(this.defaultPage);

                    break;
                case UNAUTHORIZED:
                    mView.setViewName(this.appConfig.getUnauthorizedPage());

                    break;
                default:
                    mView.setViewName(this.appConfig.getUnavailablePage());

                    break;
            }
        }
        catch (final ApplicationEnablementException aex)
        {
            ERROR_RECORDER.error(aex.getMessage(), aex);

            mView.setViewName(this.appConfig.getErrorResponsePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "search/terms/{terms}/type/{type}/page/{page}", method = RequestMethod.GET)
    public final ModelAndView showSearchPage(@PathVariable("terms") final String terms, @PathVariable("type") final String type, @PathVariable("page") final int page, final Model model)
    {
        final String methodName = ServiceManagementController.CNAME + "#showSearchPage(@PathVariable(\"terms\") final String terms, @PathVariable(\"type\") final String type, @PathVariable(\"page\") final int page, final Model model)";

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
        final IServiceManagementProcessor processor = (IServiceManagementProcessor) new ServiceManagementProcessorImpl();
        final IApplicationEnablementProcessor enabler = (IApplicationEnablementProcessor) new ApplicationEnablementProcessorImpl();

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

        ApplicationEnablementRequest request = new ApplicationEnablementRequest();
        request.setApplicationId(this.appConfig.getApplicationId());
        request.setApplicationName(this.appConfig.getApplicationName());
        request.setServiceGuid(this.serviceId);
        request.setServiceName(this.serviceName);

        if (DEBUG)
        {
            DEBUGGER.debug("ApplicationEnablementRequest: {}", request);
        }

        try
        {
            ApplicationEnablementResponse response = enabler.isServiceEnabled(request);

            if (DEBUG)
            {
                DEBUGGER.debug("ApplicationEnablementResponse: {}", response);
            }

            switch (response.getRequestStatus())
            {
                case EXCEPTION:
                    mView.setViewName(this.appConfig.getErrorResponsePage());

                    break;
                case FAILURE:
                    mView.setViewName(this.appConfig.getErrorResponsePage());

                    break;
                case SUCCESS:
                    try
                    {
                        RequestHostInfo reqInfo = new RequestHostInfo();
                        reqInfo.setHostName(hRequest.getRemoteHost());
                        reqInfo.setHostAddress(hRequest.getRemoteAddr());
            
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
            
                        ServiceManagementRequest svcRequest = new ServiceManagementRequest();
                        svcRequest.setApplicationId(this.appConfig.getApplicationId());
                        svcRequest.setApplicationName(this.appConfig.getApplicationName());
                        svcRequest.setRequestInfo(reqInfo);
                        svcRequest.setService(service);
                        svcRequest.setServiceId(this.serviceId);
                        svcRequest.setStartPage(page);
                        svcRequest.setUserAccount(userAccount);
            
                        if (DEBUG)
                        {
                            DEBUGGER.debug("ServiceManagementRequest: {}", svcRequest);
                        }
            
                        ServiceManagementResponse svcResponse = processor.getServiceByAttribute(svcRequest);
            
                        if (DEBUG)
                        {
                            DEBUGGER.debug("ServiceManagementResponse: {}", svcResponse);
                        }
            
                        if (svcResponse.getRequestStatus() == CoreServicesStatus.SUCCESS)
                        {
                            mView.addObject("pages", (int) Math.ceil(svcResponse.getEntryCount() * 1.0 / this.recordsPerPage));
                            mView.addObject("page", page);
                            mView.addObject("searchTerms", terms);
                            mView.addObject("searchType", type);
                            mView.addObject(Constants.SEARCH_RESULTS, svcResponse.getServiceList());
                            mView.addObject(Constants.COMMAND, new Service());

                            mView.setViewName(this.defaultPage);
                        }
                        else if (response.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                        {
                            mView.setViewName(this.appConfig.getUnauthorizedPage());
                        }
                        else
                        {
                            mView.addObject(Constants.ERROR_RESPONSE, this.appConfig.getMessageNoSearchResults());
                            mView.addObject(Constants.COMMAND, new Service());

                            mView.setViewName(this.defaultPage);
                        }
                    }
                    catch (final ServiceManagementException smx)
                    {
                        ERROR_RECORDER.error(smx.getMessage(), smx);
            
                        mView.setViewName(this.appConfig.getErrorResponsePage());
                    }

                    break;
                case UNAUTHORIZED:
                    mView.setViewName(this.appConfig.getUnauthorizedPage());

                    break;
                default:
                    mView.setViewName(this.appConfig.getUnavailablePage());

                    break;
            }
        }
        catch (final ApplicationEnablementException aex)
        {
            ERROR_RECORDER.error(aex.getMessage(), aex);

            mView.setViewName(this.appConfig.getErrorResponsePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "list-platforms", method = RequestMethod.GET)
    public final ModelAndView showPlatformList(final Model model)
    {
        final String methodName = ServiceManagementController.CNAME + "#showPlatformList(final String model)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IServiceManagementProcessor processor = (IServiceManagementProcessor) new ServiceManagementProcessorImpl();
        final IApplicationEnablementProcessor enabler = (IApplicationEnablementProcessor) new ApplicationEnablementProcessorImpl();

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

        ApplicationEnablementRequest request = new ApplicationEnablementRequest();
        request.setApplicationId(this.appConfig.getApplicationId());
        request.setApplicationName(this.appConfig.getApplicationName());
        request.setServiceGuid(this.serviceId);
        request.setServiceName(this.serviceName);

        if (DEBUG)
        {
            DEBUGGER.debug("ApplicationEnablementRequest: {}", request);
        }

        try
        {
            ApplicationEnablementResponse response = enabler.isServiceEnabled(request);

            if (DEBUG)
            {
                DEBUGGER.debug("ApplicationEnablementResponse: {}", response);
            }

            switch (response.getRequestStatus())
            {
                case EXCEPTION:
                    mView.setViewName(this.appConfig.getErrorResponsePage());

                    break;
                case FAILURE:
                    mView.setViewName(this.appConfig.getErrorResponsePage());

                    break;
                case SUCCESS:
                    try
                    {
                        RequestHostInfo reqInfo = new RequestHostInfo();
                        reqInfo.setHostName(hRequest.getRemoteHost());
                        reqInfo.setHostAddress(hRequest.getRemoteAddr());

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

                        ServiceManagementRequest svcRequest = new ServiceManagementRequest();
                        svcRequest.setRequestInfo(reqInfo);
                        svcRequest.setUserAccount(userAccount);
                        svcRequest.setServiceId(this.serviceId);
                        svcRequest.setApplicationId(this.appConfig.getApplicationId());
                        svcRequest.setApplicationName(this.appConfig.getApplicationName());
                        svcRequest.setService(service);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("ServiceManagementRequest: {}", svcRequest);
                        }

                        ServiceManagementResponse svcResponse = processor.listServicesByType(svcRequest);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("ServiceManagementResponse: {}", svcResponse);
                        }

                        if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                        {
                            List<Service> platformList = svcResponse.getServiceList();

                            if (DEBUG)
                            {
                                DEBUGGER.debug("platformList: {}", platformList);
                            }

                            mView.addObject("pages", (int) Math.ceil(svcResponse.getEntryCount() * 1.0 / this.recordsPerPage));
                            mView.addObject("page", 1);
                            mView.addObject("platformList", platformList);

                            mView.setViewName(this.viewServiceList);
                        }
                        else if (response.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                        {
                            mView.setViewName(this.appConfig.getUnauthorizedPage());
                        }
                        else
                        {
                            mView.addObject(Constants.ERROR_RESPONSE, this.appConfig.getMessageNoSearchResults());
                            mView.setViewName(this.addServiceRedirect);
                        }
                    }
                    catch (final ServiceManagementException smx)
                    {
                        ERROR_RECORDER.error(smx.getMessage(), smx);

                        mView.setViewName(this.appConfig.getErrorResponsePage());
                    }

                    break;
                case UNAUTHORIZED:
                    mView.setViewName(this.appConfig.getUnauthorizedPage());

                    break;
                default:
                    mView.setViewName(this.appConfig.getUnavailablePage());

                    break;
            }
        }
        catch (final ApplicationEnablementException aex)
        {
            ERROR_RECORDER.error(aex.getMessage(), aex);

            mView.setViewName(this.appConfig.getErrorResponsePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }
    
        return mView;
    }

    @RequestMapping(value = "list-platforms/page/{page}", method = RequestMethod.GET)
    public final ModelAndView showPlatformList(@PathVariable("page") final int page, final Model model)
    {
        final String methodName = ServiceManagementController.CNAME + "#showPlatformList(@PathVariable(\"page\") final int page, final Model model)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Page: {}", page);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IServiceManagementProcessor processor = (IServiceManagementProcessor) new ServiceManagementProcessorImpl();
        final IApplicationEnablementProcessor enabler = (IApplicationEnablementProcessor) new ApplicationEnablementProcessorImpl();

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

        ApplicationEnablementRequest request = new ApplicationEnablementRequest();
        request.setApplicationId(this.appConfig.getApplicationId());
        request.setApplicationName(this.appConfig.getApplicationName());
        request.setServiceGuid(this.serviceId);
        request.setServiceName(this.serviceName);

        if (DEBUG)
        {
            DEBUGGER.debug("ApplicationEnablementRequest: {}", request);
        }

        try
        {
            ApplicationEnablementResponse response = enabler.isServiceEnabled(request);

            if (DEBUG)
            {
                DEBUGGER.debug("ApplicationEnablementResponse: {}", response);
            }

            switch (response.getRequestStatus())
            {
                case EXCEPTION:
                    mView.setViewName(this.appConfig.getErrorResponsePage());

                    break;
                case FAILURE:
                    mView.setViewName(this.appConfig.getErrorResponsePage());

                    break;
                case SUCCESS:
                    try
                    {
                        RequestHostInfo reqInfo = new RequestHostInfo();
                        reqInfo.setHostName(hRequest.getRemoteHost());
                        reqInfo.setHostAddress(hRequest.getRemoteAddr());
            
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
            
                        ServiceManagementRequest svcRequest = new ServiceManagementRequest();
                        svcRequest.setRequestInfo(reqInfo);
                        svcRequest.setUserAccount(userAccount);
                        svcRequest.setServiceId(this.serviceId);
                        svcRequest.setApplicationId(this.appConfig.getApplicationId());
                        svcRequest.setApplicationName(this.appConfig.getApplicationName());
                        svcRequest.setStartPage((page - 1) * this.recordsPerPage);
                        svcRequest.setService(service);
            
                        if (DEBUG)
                        {
                            DEBUGGER.debug("ServiceManagementRequest: {}", svcRequest);
                        }
            
                        ServiceManagementResponse svcResponse = processor.listServicesByType(svcRequest);
            
                        if (DEBUG)
                        {
                            DEBUGGER.debug("ServiceManagementResponse: {}", svcResponse);
                        }
            
                        if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                        {
                            List<Service> platformList = svcResponse.getServiceList();
            
                            if (DEBUG)
                            {
                                DEBUGGER.debug("platformList: {}", platformList);
                            }
            
                            mView.addObject("pages", (int) Math.ceil(svcResponse.getEntryCount() * 1.0 / this.recordsPerPage));
                            mView.addObject("page", page);
                            mView.addObject("platformList", platformList);

                            mView.setViewName(this.viewServiceList);
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
                    catch (final ServiceManagementException smx)
                    {
                        ERROR_RECORDER.error(smx.getMessage(), smx);
            
                        mView.setViewName(this.appConfig.getErrorResponsePage());
                    }

                    break;
                case UNAUTHORIZED:
                    mView.setViewName(this.appConfig.getUnauthorizedPage());

                    break;
                default:
                    mView.setViewName(this.appConfig.getUnavailablePage());

                    break;
            }
        }
        catch (final ApplicationEnablementException aex)
        {
            ERROR_RECORDER.error(aex.getMessage(), aex);

            mView.setViewName(this.appConfig.getErrorResponsePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "service/{guid}", method = RequestMethod.GET)
    public final ModelAndView showService(@PathVariable("guid") final String guid, final Model model)
    {
        final String methodName = ServiceManagementController.CNAME + "#showService(@PathVariable(\"guid\") final String guid, final Model model)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", guid);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IServiceManagementProcessor platformMgr = (IServiceManagementProcessor) new ServiceManagementProcessorImpl();
        final IApplicationEnablementProcessor enabler = (IApplicationEnablementProcessor) new ApplicationEnablementProcessorImpl();

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

        ApplicationEnablementRequest request = new ApplicationEnablementRequest();
        request.setApplicationId(this.appConfig.getApplicationId());
        request.setApplicationName(this.appConfig.getApplicationName());
        request.setServiceGuid(this.serviceId);
        request.setServiceName(this.serviceName);

        if (DEBUG)
        {
            DEBUGGER.debug("ApplicationEnablementRequest: {}", request);
        }

        try
        {
            ApplicationEnablementResponse response = enabler.isServiceEnabled(request);

            if (DEBUG)
            {
                DEBUGGER.debug("ApplicationEnablementResponse: {}", response);
            }

            switch (response.getRequestStatus())
            {
                case EXCEPTION:
                    mView.setViewName(this.appConfig.getErrorResponsePage());

                    break;
                case FAILURE:
                    mView.setViewName(this.appConfig.getErrorResponsePage());

                    break;
                case SUCCESS:
                    try
                    {
                        RequestHostInfo reqInfo = new RequestHostInfo();
                        reqInfo.setHostName(hRequest.getRemoteHost());
                        reqInfo.setHostAddress(hRequest.getRemoteAddr());
            
                        if (DEBUG)
                        {
                            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
                        }
            
                        Service reqService = new Service();
                        reqService.setGuid(guid);
            
                        if (DEBUG)
                        {
                            DEBUGGER.debug("Service: {}", reqService);
                        }
            
                        // get a list of available servers
                        ServiceManagementRequest svcRequest = new ServiceManagementRequest();
                        svcRequest.setRequestInfo(reqInfo);
                        svcRequest.setUserAccount(userAccount);
                        svcRequest.setServiceId(this.serviceId);
                        svcRequest.setService(reqService);
                        svcRequest.setApplicationId(this.appConfig.getApplicationId());
                        svcRequest.setApplicationName(this.appConfig.getApplicationName());
            
                        if (DEBUG)
                        {
                            DEBUGGER.debug("ServiceManagementRequest: {}", request);
                        }
            
                        ServiceManagementResponse svcResponse = platformMgr.getServiceData(svcRequest);
            
                        if (DEBUG)
                        {
                            DEBUGGER.debug("ServiceManagementResponse: {}", svcResponse);
                        }
            
                        if (svcResponse.getRequestStatus() == CoreServicesStatus.SUCCESS)
                        {
                            Service resPlatform = svcResponse.getService();
            
                            if (DEBUG)
                            {
                                DEBUGGER.debug("Service: {}", resPlatform);
                            }
            
                            mView.addObject("platform", resPlatform);
                            mView.setViewName(this.viewServicePage);
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
                    catch (final ServiceManagementException smx)
                    {
                        ERROR_RECORDER.error(smx.getMessage(), smx);
            
                        mView.setViewName(this.appConfig.getErrorResponsePage());
                    }

                    break;
                case UNAUTHORIZED:
                    mView.setViewName(this.appConfig.getUnauthorizedPage());

                    break;
                default:
                    mView.setViewName(this.appConfig.getUnavailablePage());

                    break;
            }
        }
        catch (final ApplicationEnablementException aex)
        {
            ERROR_RECORDER.error(aex.getMessage(), aex);

            mView.setViewName(this.appConfig.getErrorResponsePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "add-service", method = RequestMethod.GET)
    public final ModelAndView showAddService(final Model model)
    {
        final String methodName = ServiceManagementController.CNAME + "#showAddService(final Model model)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IServerManagementProcessor processor = (IServerManagementProcessor) new ServerManagementProcessorImpl();
        final IApplicationEnablementProcessor enabler = (IApplicationEnablementProcessor) new ApplicationEnablementProcessorImpl();

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

        ApplicationEnablementRequest request = new ApplicationEnablementRequest();
        request.setApplicationId(this.appConfig.getApplicationId());
        request.setApplicationName(this.appConfig.getApplicationName());
        request.setServiceGuid(this.serviceId);
        request.setServiceName(this.serviceName);

        if (DEBUG)
        {
            DEBUGGER.debug("ApplicationEnablementRequest: {}", request);
        }

        try
        {
            ApplicationEnablementResponse response = enabler.isServiceEnabled(request);

            if (DEBUG)
            {
                DEBUGGER.debug("ApplicationEnablementResponse: {}", response);
            }

            switch (response.getRequestStatus())
            {
                case EXCEPTION:
                    mView.setViewName(this.appConfig.getErrorResponsePage());

                    break;
                case FAILURE:
                    mView.setViewName(this.appConfig.getErrorResponsePage());

                    break;
                case SUCCESS:
                    // TODO: build in getting the list of servers here
                    try
                    {
                        RequestHostInfo reqInfo = new RequestHostInfo();
                        reqInfo.setHostName(hRequest.getRemoteHost());
                        reqInfo.setHostAddress(hRequest.getRemoteAddr());
            
                        if (DEBUG)
                        {
                            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
                        }
                        
                        ServerManagementRequest svcRequest = new ServerManagementRequest();
                        svcRequest.setApplicationId(this.appConfig.getApplicationId());
                        svcRequest.setApplicationName(this.appConfig.getApplicationName());
                        svcRequest.setRequestInfo(reqInfo);
                        svcRequest.setUserAccount(userAccount);
                        svcRequest.setStartPage(0);
            
                        if (DEBUG)
                        {
                            DEBUGGER.debug("ServerManagementRequest: {}", svcRequest);
                        }
            
                        ServerManagementResponse svcResponse = processor.listServers(svcRequest);
            
                        if (DEBUG)
                        {
                            DEBUGGER.debug("ServerManagementResponse: {}", svcResponse);
                        }
            
                        switch (svcResponse.getRequestStatus())
                        {
                            case EXCEPTION:
                                ERROR_RECORDER.error("An error occurred while obtaining the list of installed servers. Please try again later.");
            
                                mView.addObject(Constants.ERROR_MESSAGE, "theme.system.service.failure");
            
                                break;
                            case FAILURE:
                                ERROR_RECORDER.error("A system failure occurred while obtaining the list of installed servers. Please try again later.");
            
                                mView.addObject(Constants.ERROR_MESSAGE, "theme.system.service.failure");
            
                                break;
                            case SUCCESS:
                            	mView.addObject(Constants.COMMAND, new Service());
                                mView.addObject("serverList", svcResponse.getServerList());
            
                                break;
                            case UNAUTHORIZED:
                                mView.setViewName(this.appConfig.getUnauthorizedPage());
        
                                break;
                        }
                    }
                    catch (ServerManagementException smx)
                    {
                        ERROR_RECORDER.error(smx.getMessage(), smx);
            
                        mView.setViewName(this.appConfig.getErrorResponsePage());
                    }

                    break;
                case UNAUTHORIZED:
                    mView.setViewName(this.appConfig.getUnauthorizedPage());

                    break;
                default:
                    mView.setViewName(this.appConfig.getUnavailablePage());

                    break;
            }
        }
        catch (final ApplicationEnablementException aex)
        {
            ERROR_RECORDER.error(aex.getMessage(), aex);

            mView.setViewName(this.appConfig.getErrorResponsePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "search", method = RequestMethod.POST)
    public final ModelAndView submitServiceSearch(@ModelAttribute("service") final Service service, final BindingResult bindResult, final Model model)
    {
        final String methodName = ServiceManagementController.CNAME + "#submitServiceSearch(@ModelAttribute(\"service\") final Service service, final BindingResult bindResult, final Model model)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("SearchRequest: {}", service);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IServiceManagementProcessor processor = (IServiceManagementProcessor) new ServiceManagementProcessorImpl();
        final IApplicationEnablementProcessor enabler = (IApplicationEnablementProcessor) new ApplicationEnablementProcessorImpl();

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

        ApplicationEnablementRequest request = new ApplicationEnablementRequest();
        request.setApplicationId(this.appConfig.getApplicationId());
        request.setApplicationName(this.appConfig.getApplicationName());
        request.setServiceGuid(this.serviceId);
        request.setServiceName(this.serviceName);

        if (DEBUG)
        {
            DEBUGGER.debug("ApplicationEnablementRequest: {}", request);
        }

        try
        {
            ApplicationEnablementResponse response = enabler.isServiceEnabled(request);

            if (DEBUG)
            {
                DEBUGGER.debug("ApplicationEnablementResponse: {}", response);
            }

            switch (response.getRequestStatus())
            {
                case EXCEPTION:
                    mView.setViewName(this.appConfig.getErrorResponsePage());

                    break;
                case FAILURE:
                    mView.setViewName(this.appConfig.getErrorResponsePage());

                    break;
                case SUCCESS:
                    // TODO: build in getting the list of servers here
                    try
                    {
                        RequestHostInfo reqInfo = new RequestHostInfo();
                        reqInfo.setHostName(hRequest.getRemoteHost());
                        reqInfo.setHostAddress(hRequest.getRemoteAddr());
            
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
            
                        ServiceManagementRequest svcRequest = new ServiceManagementRequest();
                        svcRequest.setApplicationId(this.appConfig.getApplicationId());
                        svcRequest.setApplicationName(this.appConfig.getApplicationName());
                        svcRequest.setRequestInfo(reqInfo);
                        svcRequest.setService(reqService);
                        svcRequest.setServiceId(this.serviceId);
                        svcRequest.setUserAccount(userAccount);
            
                        if (DEBUG)
                        {
                            DEBUGGER.debug("ServiceManagementRequest: {}", request);
                        }
            
                        ServiceManagementResponse svcResponse = processor.getServiceByAttribute(svcRequest);
            
                        if (DEBUG)
                        {
                            DEBUGGER.debug("ServiceManagementResponse: {}", svcResponse);
                        }
            
                        if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                        {
                            mView.addObject("pages", (int) Math.ceil(svcResponse.getEntryCount() * 1.0 / this.recordsPerPage));
                            mView.addObject("page", 1);
                            mView.addObject("searchTerms", service.getName());
                            mView.addObject(Constants.SEARCH_RESULTS, svcResponse.getServiceList());
                            mView.addObject(Constants.COMMAND, new Service());

                            mView.setViewName(this.defaultPage);
                        }
                        else if (response.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                        {
                            mView.setViewName(this.appConfig.getUnauthorizedPage());
                        }
                        else
                        {
                            mView.addObject(Constants.ERROR_RESPONSE, this.appConfig.getMessageNoSearchResults());
                            mView.addObject(Constants.COMMAND, new Service());
                            mView.setViewName(this.defaultPage);
                        }
                    }
                    catch (final ServiceManagementException smx)
                    {
                        ERROR_RECORDER.error(smx.getMessage(), smx);
            
                        mView.setViewName(this.appConfig.getErrorResponsePage());
                    }

                    break;
                case UNAUTHORIZED:
                    mView.setViewName(this.appConfig.getUnauthorizedPage());

                    break;
                default:
                    mView.setViewName(this.appConfig.getUnavailablePage());

                    break;
            }
        }
        catch (final ApplicationEnablementException aex)
        {
            ERROR_RECORDER.error(aex.getMessage(), aex);

            mView.setViewName(this.appConfig.getErrorResponsePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }
}
