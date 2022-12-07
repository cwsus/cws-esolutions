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
import org.slf4j.Logger;
import java.util.Enumeration;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.cws.esolutions.web.Constants;
import com.cws.esolutions.web.model.SearchRequest;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.web.ApplicationServiceBean;
import com.cws.esolutions.core.processors.dto.DNSRecord;
import com.cws.esolutions.core.processors.enums.DNSRecordType;
import com.cws.esolutions.core.processors.enums.DNSRequestType;
import com.cws.esolutions.core.processors.dto.DNSServiceRequest;
import com.cws.esolutions.core.processors.dto.DNSServiceResponse;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.exception.DNSServiceException;
import com.cws.esolutions.core.processors.impl.DNSServiceRequestProcessorImpl;
import com.cws.esolutions.core.processors.interfaces.IDNSServiceRequestProcessor;
/**
 * @author cws-khuntly
 * @version 1.0
 * @see org.springframework.stereotype.Controller
 */
@Controller
@RequestMapping("/dns-service")
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

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CNAME);

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

    @RequestMapping(value = {"/default", "/lookup"}, method = RequestMethod.GET)
    public final String showDefaultPage(final Model model)
    {
        final String methodName = DNSServiceController.CNAME + "#showLookupPage(final Model model)";

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

        if (!(this.appConfig.getServices().get(this.serviceName)))
        {
            return this.appConfig.getUnavailablePage();
        }

        model.addAttribute("serviceTypes", this.serviceTypes);
        model.addAttribute(Constants.COMMAND, new SearchRequest());

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", model);
        }

        return this.lookupPage;
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public final String submitLookup(@ModelAttribute("entry") final SearchRequest request, final BindingResult bindResult, final Model model)
    {
        final String methodName = DNSServiceController.CNAME + "#submitLookup(@ModelAttribute(\"entry\") final SearchRequest request, final BindingResult bindResult)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("DNSRecord: {}", request);
            DEBUGGER.debug("BindingResult: {}", bindResult);
        }

        String responsePage = null;

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IDNSServiceRequestProcessor dnsProcessor = new DNSServiceRequestProcessorImpl();

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

        if (!(this.appConfig.getServices().get(this.serviceName)))
        {
            return this.appConfig.getUnavailablePage();
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
            record.setRecordName(request.getSearchTerms());
            record.setRecordType(DNSRecordType.valueOf(request.getSearchExtras()));

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

            DNSServiceResponse response = dnsProcessor.performLookup(dnsRequest);

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
	                    model.addAttribute("dnsEntries", response.getDnsRecords());
	                }
	                else if (response.getDnsRecord() != null)
	                {
	                	model.addAttribute("dnsEntry", response.getDnsRecord());
	                }
	                else
	                {
	                	model.addAttribute(Constants.ERROR_RESPONSE, this.messageNoSearchResults);
	                }

	                model.addAttribute("serviceTypes", this.serviceTypes);
	                model.addAttribute(Constants.COMMAND, new SearchRequest());

	                responsePage = this.lookupPage;

	                break;
	            case FAILURE:
	            	responsePage = this.appConfig.getErrorResponsePage();

	            	break;
	            case EXCEPTION:
	            	responsePage = this.appConfig.getErrorResponsePage();

	            	break;
	            case UNAUTHORIZED:
	            	responsePage = this.appConfig.getUnauthorizedPage();

	            	break;
            	default:
            }
        }
        catch (final DNSServiceException dsx)
        {
            ERROR_RECORDER.error(dsx.getMessage(), dsx);

            return this.appConfig.getErrorResponsePage();
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", model);
        }
 
        return responsePage;
    }
}
