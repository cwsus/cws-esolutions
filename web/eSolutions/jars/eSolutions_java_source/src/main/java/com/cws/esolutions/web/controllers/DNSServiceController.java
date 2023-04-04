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
 * File: DNSServiceController.java
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.cws.esolutions.web.Constants;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.web.ApplicationServiceBean;
import com.cws.esolutions.core.processors.dto.DNSRecord;
import com.cws.esolutions.core.processors.enums.DNSRecordType;
import com.cws.esolutions.core.processors.enums.DNSRequestType;
import com.cws.esolutions.core.processors.dto.DNSServiceRequest;
import com.cws.esolutions.core.processors.dto.DNSServiceResponse;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.exception.DNSServiceException;
import com.cws.esolutions.core.processors.dto.ApplicationEnablementRequest;
import com.cws.esolutions.core.processors.impl.DNSServiceRequestProcessorImpl;
import com.cws.esolutions.core.processors.interfaces.IDNSServiceRequestProcessor;
/**
 * @author cws-khuntly
 * @version 1.0
 * @see org.springframework.stereotype.Controller
 */
@Controller
@RequestMapping("dns-service")
public class DNSServiceController
{
    private String serviceId = null;
    private String lookupPage = null;
    private String serviceName = null;
    private String serviceHost = null;
    private String[] searchSuffix = null;
    private List<String> serviceTypes = null;
    private String messageNoSearchResults = null;
    private ApplicationServiceBean appConfig = null;

    private static final String CNAME = DNSServiceController.class.getName();

    private static final Logger DEBUGGER = LogManager.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LogManager.getLogger(Constants.ERROR_LOGGER + CNAME);

    public final void setLookupPage(final String value)
    {
        final String methodName = DNSServiceController.CNAME + "#setLookupPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.lookupPage = value;
    }

    public final void setServiceName(final String value)
    {
        final String methodName = DNSServiceController.CNAME + "#setServiceName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serviceName = value;
    }

    public final void setAppConfig(final ApplicationServiceBean value)
    {
        final String methodName = DNSServiceController.CNAME + "#setAppConfig(final ApplicationServiceBean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.appConfig = value;
    }

    public final void setServiceId(final String value)
    {
        final String methodName = DNSServiceController.CNAME + "#setServiceId(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serviceId = value;
    }

    public final void setServiceHost(final String value)
    {
        final String methodName = DNSServiceController.CNAME + "#setServiceHost(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serviceHost = value;
    }

    public final void setSearchSuffix(final String[] value)
    {
        final String methodName = DNSServiceController.CNAME + "#setSearchSuffix(final String[] value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);

            if (value != null)
            {
                for (String str : value)
                {
                    DEBUGGER.debug("Value: {}", str);
                }
            }
        }

        this.searchSuffix = value;
    }

    public final void setServiceTypes(final List<String> value)
    {
        final String methodName = DNSServiceController.CNAME + "#setServiceTypes(final List<String> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serviceTypes = value;
    }

    public final void setMessageNoSearchResults(final String value)
    {
    	final String methodName = CNAME + "#setMessageNoSearchResults(final String value)";

    	if (DEBUG)
    	{
    		DEBUGGER.debug(methodName);
    		DEBUGGER.debug("Value: {}", value);
    	}

    	this.messageNoSearchResults = value;
    }

    @RequestMapping(value = {"default", "lookup", "search"}, method = RequestMethod.GET)
    public final ModelAndView showDefaultPage(final Model model)
    {
        final String methodName = DNSServiceController.CNAME + "#showLookupPage(final Model model)";

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

        mView.addObject("serviceTypes", this.serviceTypes);
        mView.addObject(Constants.COMMAND, new DNSRecord());
        mView.setViewName(this.lookupPage);

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "search", method = RequestMethod.POST)
    public final ModelAndView submitLookup(@ModelAttribute("entry") final DNSRecord request, final BindingResult bindResult, final Model model)
    {
        final String methodName = DNSServiceController.CNAME + "#submitLookup(@ModelAttribute(\"entry\") final DNSRecord request, final BindingResult bindResult)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("DNSRecord: {}", request);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IDNSServiceRequestProcessor processor = (IDNSServiceRequestProcessor) new DNSServiceRequestProcessorImpl();

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

        ApplicationEnablementRequest enableRequest = new ApplicationEnablementRequest();
        enableRequest.setApplicationId(this.appConfig.getApplicationId());
        enableRequest.setApplicationName(this.appConfig.getApplicationName());
        enableRequest.setServiceGuid(this.serviceId);
        enableRequest.setServiceName(this.serviceName);

        if (DEBUG)
        {
            DEBUGGER.debug("ApplicationEnablementRequest: {}", enableRequest);
        }

        try
        {
            // ensure authenticated access
            RequestHostInfo reqInfo = new RequestHostInfo();
            reqInfo.setHostAddress(hRequest.getRemoteAddr());
            reqInfo.setHostName(hRequest.getRemoteHost());

            if (DEBUG)
            {
                DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            }

            DNSRecord record = new DNSRecord();
            record.setRecordName(request.getRecordName());
            record.setRecordType(DNSRecordType.valueOf(request.getRecordType().toString()));

            if (DEBUG)
            {
                DEBUGGER.debug("DNSRecord: {}", record);
            }

            DNSServiceRequest dnsRequest = new DNSServiceRequest();
            dnsRequest.setRecord(record);
            dnsRequest.setRequestInfo(reqInfo);
            dnsRequest.setUserAccount(userAccount);
            dnsRequest.setServiceId(this.serviceId);
            dnsRequest.setSearchURL(this.serviceHost);
            dnsRequest.setSearchPath(this.searchSuffix);
            dnsRequest.setRequestType(DNSRequestType.LOOKUP);
            dnsRequest.setApplicationId(this.appConfig.getApplicationId());
            dnsRequest.setApplicationName(this.appConfig.getApplicationName());

            if (DEBUG)
            {
                DEBUGGER.debug("DNSServiceRequest: {}", dnsRequest);
            }

            DNSServiceResponse response = processor.performLookup(dnsRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("DNSServiceResponse: {}", response);
            }

            switch (response.getRequestStatus())
            {
                case SUCCESS:
                    if ((response.getDnsRecords() != null) && (response.getDnsRecords().size() != 0))
                    {
                        // multiple records were returned
                        mView.addObject("dnsEntries", response.getDnsRecords());
                    }
                    else if (response.getDnsRecord() != null)
                    {
                        mView.addObject("dnsEntry", response.getDnsRecord());
                    }
                    else
                    {
                        mView.addObject(Constants.ERROR_RESPONSE, this.messageNoSearchResults);
                    }

                    mView.addObject("serviceTypes", this.serviceTypes);
                    mView.addObject(Constants.COMMAND, new DNSRecord());
                    mView.setViewName(this.lookupPage);

                    break;
                case FAILURE:
                    mView.setViewName(this.appConfig.getErrorResponsePage());

                    break;
                case EXCEPTION:
                    mView.setViewName(this.appConfig.getErrorResponsePage());

                    break;
                case UNAUTHORIZED:
                    mView.setViewName(this.appConfig.getUnauthorizedPage());

                    break;
                default:
            }
        }
        catch (final DNSServiceException dsx)
        {
            ERROR_RECORDER.error(dsx.getMessage(), dsx);

            mView.setViewName(this.appConfig.getErrorResponsePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }
}
