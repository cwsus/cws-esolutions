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
import java.util.Objects;
import java.util.Enumeration;
import org.springframework.ui.Model;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
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
import com.cws.esolutions.web.validators.ServiceValidator;
import com.cws.esolutions.core.processors.enums.ServiceType;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.core.processors.dto.ServiceManagementRequest;
import com.cws.esolutions.core.processors.dto.ServiceManagementResponse;
import com.cws.esolutions.core.processors.impl.ServiceManagementProcessorImpl;
import com.cws.esolutions.core.processors.exception.ServiceManagementException;
import com.cws.esolutions.core.processors.interfaces.IServiceManagementProcessor;
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
    public final String showDefaultPage(final Model model)
    {
        final String methodName = ServiceManagementController.CNAME + "#showDefaultPage(final Model model)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

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

        if (Objects.isNull(hSession.getAttribute(Constants.USER_ACCOUNT)))
        {
        	return this.appConfig.getLogonRedirect();
        }

        if (!(this.appConfig.getServices().get(this.serviceName)))
        {
            return this.appConfig.getUnavailablePage();
        }

        model.addAttribute(Constants.COMMAND, new SearchRequest());

        return this.defaultPage;
    }

    @RequestMapping(value = "search/terms/{terms}/type/{type}/page/{page}", method = RequestMethod.GET)
    public final String showSearchPage(@PathVariable("terms") final String terms, @PathVariable("type") final String type, @PathVariable("page") final int page, final Model model)
    {
        final String methodName = ServiceManagementController.CNAME + "#showSearchPage(@PathVariable(\"terms\") final String terms, @PathVariable(\"type\") final String type, @PathVariable(\"page\") final int page, final Model model)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", terms);
            DEBUGGER.debug("Value: {}", type);
            DEBUGGER.debug("Value: {}", page);
        }

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IServiceManagementProcessor processor = (IServiceManagementProcessor) new ServiceManagementProcessorImpl();

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

        if (Objects.isNull(hSession.getAttribute(Constants.USER_ACCOUNT)))
        {
        	return this.appConfig.getLogonRedirect();
        }

        if (!(this.appConfig.getServices().get(this.serviceName)))
        {
            return this.appConfig.getUnavailablePage();
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
                model.addAttribute("pages", (int) Math.ceil(response.getEntryCount() * 1.0 / this.recordsPerPage));
                model.addAttribute("page", page);
                model.addAttribute("searchTerms", terms);
                model.addAttribute("searchType", type);
                model.addAttribute(Constants.SEARCH_RESULTS, response.getServiceList());
                model.addAttribute(Constants.COMMAND, new Service());

                return this.defaultPage;
            }
            else if (response.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
            {
                return this.appConfig.getUnauthorizedPage();
            }
            else
            {
                model.addAttribute(Constants.ERROR_RESPONSE, this.appConfig.getMessageNoSearchResults());
                model.addAttribute(Constants.COMMAND, new Service());

                return this.defaultPage;
            }
        }
        catch (final ServiceManagementException smx)
        {
            ERROR_RECORDER.error(smx.getMessage(), smx);

            return this.appConfig.getErrorResponsePage();
        }
    }

    @RequestMapping(value = "list-platforms", method = RequestMethod.GET)
    public final String showPlatformList(final Model model)
    {
        final String methodName = ServiceManagementController.CNAME + "#showPlatformList(final String model)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IServiceManagementProcessor processor = (IServiceManagementProcessor) new ServiceManagementProcessorImpl();

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

        if (Objects.isNull(hSession.getAttribute(Constants.USER_ACCOUNT)))
        {
        	return this.appConfig.getLogonRedirect();
        }

        if (!(this.appConfig.getServices().get(this.serviceName)))
        {
            return this.appConfig.getUnavailablePage();
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

            Service service = new Service();
            service.setType(ServiceType.PLATFORM);

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
                List<Service> platformList = response.getServiceList();

                if (DEBUG)
                {
                    DEBUGGER.debug("platformList: {}", platformList);
                }

                model.addAttribute("pages", (int) Math.ceil(response.getEntryCount() * 1.0 / this.recordsPerPage));
                model.addAttribute("page", 1);
                model.addAttribute("platformList", platformList);

                return this.viewServiceList;
            }
            else if (response.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
            {
                return this.appConfig.getUnauthorizedPage();
            }
            else
            {
                model.addAttribute(Constants.ERROR_RESPONSE, this.appConfig.getMessageNoSearchResults());

                return this.addServiceRedirect;
            }
        }
        catch (final ServiceManagementException pmx)
        {
            ERROR_RECORDER.error(pmx.getMessage(), pmx);

            return this.appConfig.getErrorResponsePage();
        }
    }

    @RequestMapping(value = "list-platforms/page/{page}", method = RequestMethod.GET)
    public final String showPlatformList(@PathVariable("page") final int page, final Model model)
    {
        final String methodName = ServiceManagementController.CNAME + "#showPlatformList(@PathVariable(\"page\") final int page, final Model model)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Page: {}", page);
        }

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IServiceManagementProcessor processor = (IServiceManagementProcessor) new ServiceManagementProcessorImpl();

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

        if (Objects.isNull(hSession.getAttribute(Constants.USER_ACCOUNT)))
        {
        	return this.appConfig.getLogonRedirect();
        }

        if (!(this.appConfig.getServices().get(this.serviceName)))
        {
            return this.appConfig.getUnavailablePage();
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

            Service service = new Service();
            service.setType(ServiceType.PLATFORM);

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
                List<Service> platformList = response.getServiceList();

                if (DEBUG)
                {
                    DEBUGGER.debug("platformList: {}", platformList);
                }

                model.addAttribute("pages", (int) Math.ceil(response.getEntryCount() * 1.0 / this.recordsPerPage));
                model.addAttribute("page", page);
                model.addAttribute("platformList", platformList);

                return this.viewServiceList;
            }
            else if (response.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
            {
                return this.appConfig.getUnauthorizedPage();
            }
            else
            {
                model.addAttribute(Constants.ERROR_RESPONSE, this.appConfig.getMessageNoSearchResults());

                return this.defaultPage;
            }
        }
        catch (final ServiceManagementException pmx)
        {
            ERROR_RECORDER.error(pmx.getMessage(), pmx);

            return this.appConfig.getErrorResponsePage();
        }
    }

    @RequestMapping(value = "list-datacenters", method = RequestMethod.GET)
    public final String showDatacenterList(final Model model)
    {
        final String methodName = ServiceManagementController.CNAME + "#showDatacenterList(final Model model)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IServiceManagementProcessor processor = (IServiceManagementProcessor) new ServiceManagementProcessorImpl();

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

        if (Objects.isNull(hSession.getAttribute(Constants.USER_ACCOUNT)))
        {
        	return this.appConfig.getLogonRedirect();
        }

        if (!(this.appConfig.getServices().get(this.serviceName)))
        {
            return this.appConfig.getUnavailablePage();
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
                    model.addAttribute("pages", (int) Math.ceil(response.getEntryCount() * 1.0 / this.recordsPerPage));
                    model.addAttribute("page", 1);
                    model.addAttribute("datacenterList", datacenterList);

                    return this.viewServiceList;
                }
                else
                {
                    model.addAttribute(Constants.MESSAGE_RESPONSE, this.messageNoServicesFound);
                    model.addAttribute(Constants.COMMAND, new Service());

                    return this.addServiceRedirect;
                }
            }
            else if (response.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
            {
                return this.appConfig.getUnauthorizedPage();
            }
            else
            {
                model.addAttribute(Constants.ERROR_RESPONSE, this.appConfig.getMessageNoSearchResults());

                return this.addServiceRedirect;
            }
        }
        catch (final ServiceManagementException smx)
        {
            ERROR_RECORDER.error(smx.getMessage(), smx);

            return this.appConfig.getErrorResponsePage();
        }
    }

    @RequestMapping(value = "list-datacenters/page/{page}", method = RequestMethod.GET)
    public final String showDataCenterList(@PathVariable("page") final int page, final Model model)
    {
        final String methodName = ServiceManagementController.CNAME + "#showDatacenterList(@PathVariable(\"page\") final int page, final Model model)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", page);
        }

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IServiceManagementProcessor processor = (IServiceManagementProcessor) new ServiceManagementProcessorImpl();

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

        if (Objects.isNull(hSession.getAttribute(Constants.USER_ACCOUNT)))
        {
        	return this.appConfig.getLogonRedirect();
        }

        if (!(this.appConfig.getServices().get(this.serviceName)))
        {
            return this.appConfig.getUnavailablePage();
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

                model.addAttribute("pages", (int) Math.ceil(response.getEntryCount() * 1.0 / this.recordsPerPage));
                model.addAttribute("page", page);
                model.addAttribute("datacenterList", datacenterList);

                return this.viewServiceList;
            }
            else if (response.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
            {
                return this.appConfig.getUnauthorizedPage();
            }
            else
            {
            	model.addAttribute(Constants.ERROR_RESPONSE, this.appConfig.getMessageNoSearchResults());

                return this.defaultPage;
            }
        }
        catch (final ServiceManagementException smx)
        {
            ERROR_RECORDER.error(smx.getMessage(), smx);

            return this.appConfig.getErrorResponsePage();
        }
    }

    @RequestMapping(value = "service/{guid}", method = RequestMethod.GET)
    public final String showService(@PathVariable("guid") final String guid, final Model model)
    {
        final String methodName = ServiceManagementController.CNAME + "#showService(@PathVariable(\"guid\") final String guid, final Model model)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", guid);
        }

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IServiceManagementProcessor platformMgr = (IServiceManagementProcessor) new ServiceManagementProcessorImpl();

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

        if (Objects.isNull(hSession.getAttribute(Constants.USER_ACCOUNT)))
        {
        	return this.appConfig.getLogonRedirect();
        }

        if (!(this.appConfig.getServices().get(this.serviceName)))
        {
            return this.appConfig.getUnavailablePage();
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

            Service reqService = new Service();
            reqService.setGuid(guid);

            if (DEBUG)
            {
                DEBUGGER.debug("Service: {}", reqService);
            }

            // get a list of available servers
            ServiceManagementRequest request = new ServiceManagementRequest();
            request.setRequestInfo(reqInfo);
            request.setUserAccount(userAccount);
            request.setServiceId(this.serviceId);
            request.setService(reqService);
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

                model.addAttribute("platform", resPlatform);

                return this.viewServicePage;
            }
            else if (response.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
            {
                return this.appConfig.getUnauthorizedPage();
            }
            else
            {
                model.addAttribute(Constants.ERROR_RESPONSE, this.appConfig.getMessageNoSearchResults());

                return this.defaultPage;
            }
        }
        catch (final ServiceManagementException pmx)
        {
            ERROR_RECORDER.error(pmx.getMessage(), pmx);

            return this.appConfig.getErrorResponsePage();
        }
    }


    @RequestMapping(value = "add-service", method = RequestMethod.GET)
    public final String showAddService(final Model model)
    {
        final String methodName = ServiceManagementController.CNAME + "#showAddService(final Model model)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

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

        if (Objects.isNull(hSession.getAttribute(Constants.USER_ACCOUNT)))
        {
        	return this.appConfig.getLogonRedirect();
        }

        if (!(this.appConfig.getServices().get(this.serviceName)))
        {
            return this.appConfig.getUnavailablePage();
        }

        model.addAttribute(Constants.COMMAND, new Service());

        return this.addServicePage;
    }

    @RequestMapping(value = "search", method = RequestMethod.POST)
    public final String submitServiceSearch(@ModelAttribute("service") final Service service, final BindingResult bindResult, final Model model)
    {
        final String methodName = ServiceManagementController.CNAME + "#submitServiceSearch(@ModelAttribute(\"service\") final Service service, final BindingResult bindResult, final Model model)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("SearchRequest: {}", service);
        }

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IServiceManagementProcessor processor = (IServiceManagementProcessor) new ServiceManagementProcessorImpl();

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

        if (Objects.isNull(hSession.getAttribute(Constants.USER_ACCOUNT)))
        {
        	return this.appConfig.getLogonRedirect();
        }

        if (!(this.appConfig.getServices().get(this.serviceName)))
        {
            return this.appConfig.getUnavailablePage();
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
                model.addAttribute("pages", (int) Math.ceil(response.getEntryCount() * 1.0 / this.recordsPerPage));
                model.addAttribute("page", 1);
                model.addAttribute("searchTerms", service.getName());
                model.addAttribute(Constants.SEARCH_RESULTS, response.getServiceList());
                model.addAttribute(Constants.COMMAND, new Service());

                return this.defaultPage;
            }
            else if (response.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
            {
                return this.appConfig.getUnauthorizedPage();
            }
            else
            {
            	model.addAttribute(Constants.ERROR_RESPONSE, this.appConfig.getMessageNoSearchResults());
            	model.addAttribute(Constants.COMMAND, new Service());

                return this.defaultPage;
            }
        }
        catch (final ServiceManagementException smx)
        {
            ERROR_RECORDER.error(smx.getMessage(), smx);

            return this.appConfig.getErrorResponsePage();
        }
    }

    @RequestMapping(value = "add-service", method = RequestMethod.POST)
    public final String doAddDatacenter(@ModelAttribute("service") final Service service, final BindingResult bindResult, final Model model)
    {
        final String methodName = ServiceManagementController.CNAME + "#doAddDatacenter(@ModelAttribute(\"service\") final Service service, final BindingResult bindResult, final Model model)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", service);
        }

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

        if (Objects.isNull(hSession.getAttribute(Constants.USER_ACCOUNT)))
        {
        	return this.appConfig.getLogonRedirect();
        }

        if (!(this.appConfig.getServices().get(this.serviceName)))
        {
            return this.appConfig.getUnavailablePage();
        }

        this.validator.validate(service, bindResult);

        if (bindResult.hasErrors())
        {
            // validation failed
            ERROR_RECORDER.error("Errors: {}", bindResult.getAllErrors());

            model.addAttribute(Constants.ERROR_MESSAGE, this.appConfig.getMessageValidationFailed());
            model.addAttribute(Constants.BIND_RESULT, bindResult.getAllErrors());
            model.addAttribute(Constants.COMMAND, new Service());

            return this.addServicePage;
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
            	model.addAttribute(Constants.RESPONSE_MESSAGE, this.messageServiceAddSuccess);
            	model.addAttribute(Constants.COMMAND, new Service());

            	return this.addServicePage;
            }
            else if (dcResponse.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
            {
                return this.appConfig.getUnauthorizedPage();
            }
            else
            {
            	model.addAttribute(Constants.ERROR_RESPONSE, this.messageServiceAddFailure);
            	model.addAttribute(Constants.COMMAND, service);

            	return this.addServicePage;
            }
        }
        catch (final ServiceManagementException smx)
        {
            ERROR_RECORDER.error(smx.getMessage(), smx);

            return this.appConfig.getErrorResponsePage();
        }
    }
}
