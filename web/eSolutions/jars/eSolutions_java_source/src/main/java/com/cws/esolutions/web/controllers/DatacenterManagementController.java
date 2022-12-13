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
 * File: DataCenterManagement.java
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
import org.springframework.mail.MailSender;
import javax.servlet.http.HttpServletRequest;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.validation.BindingResult;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.cws.esolutions.web.Constants;
import com.cws.esolutions.web.model.SearchRequest;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.web.ApplicationServiceBean;
import com.cws.esolutions.core.processors.dto.Service;
import com.cws.esolutions.core.processors.dto.ServiceManagementRequest;
import com.cws.esolutions.core.processors.dto.ServiceManagementResponse;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.core.processors.enums.ServiceType;
import com.cws.esolutions.core.processors.exception.ServiceManagementException;
import com.cws.esolutions.core.processors.impl.ServiceManagementProcessorImpl;
import com.cws.esolutions.core.processors.interfaces.IServiceManagementProcessor;
import com.cws.esolutions.core.utils.dto.EmailMessage;
import com.cws.esolutions.web.validators.EmailMessageValidator;
/**
 * @author cws-khuntly
 * @version 1.0
 * @see org.springframework.stereotype.Controller
 */
@Controller
@RequestMapping("datacenter-management")
public class DatacenterManagementController
{
    private String homePage = null;
    private String serviceName = null;
    private ApplicationServiceBean appConfig = null;

    private static final String CNAME = DatacenterManagementController.class.getName();

    private static final Logger DEBUGGER = LogManager.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LogManager.getLogger(Constants.ERROR_LOGGER + CNAME);

    public final void setAppConfig(final ApplicationServiceBean value)
    {
        final String methodName = DatacenterManagementController.CNAME + "#setAppConfig(final ApplicationServiceBean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.appConfig = value;
    }

    public final void setHomePage(final String value)
    {
        final String methodName = DatacenterManagementController.CNAME + "#setHomePage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.homePage = value;
    }

    @RequestMapping(value = "add-datacenter", method = RequestMethod.GET)
    public final String showAddDatacenter(final Model model)
    {
        final String methodName = DatacenterManagementController.CNAME + "#showAddDatacenter(final Model model)";

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

        return this.homePage;
    }

    /*
    @RequestMapping(value = "default", method = RequestMethod.GET)
    public final String showDefaultPage(final Model model)
    {
        final String methodName = DatacenterManagementController.CNAME + "#showDefaultPage(final Model model)";

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

                DEBUGGER.debug("Session Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<?> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = (String) requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Request Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<?> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String element = (String) paramsEnumeration.nextElement();
                Object value = hRequest.getParameter(element);

                DEBUGGER.debug("Request Parameter: {}; Value: {}", element, value);
            }
        }

        if (Objects.isNull(hSession.getAttribute(Constants.USER_ACCOUNT)))
        {
        	return this.appConfig.getLogonRedirect();
        }

        // in here, we're going to get all the messages to display and such
        return this.homePage;
    }

    @RequestMapping(value = "list-datacenters", method = RequestMethod.GET)
    public final String showDatacenterList(final Model model)
    {
        final String methodName = DatacenterManagementController.CNAME + "#showDatacenterList(final Model model)";

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
    */
}