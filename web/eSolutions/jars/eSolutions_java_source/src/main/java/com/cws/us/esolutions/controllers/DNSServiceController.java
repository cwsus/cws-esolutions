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

import org.slf4j.Logger;
import java.util.Enumeration;
import org.slf4j.LoggerFactory;
import javax.servlet.http.HttpSession;
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
import com.cws.us.esolutions.ApplicationServiceBean;
import com.cws.esolutions.core.processors.dto.DNSRecord;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.enums.DNSRequestType;
import com.cws.esolutions.core.processors.dto.DNSServiceRequest;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.core.processors.dto.DNSServiceResponse;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.core.processors.exception.DNSServiceException;
import com.cws.esolutions.core.processors.impl.DNSServiceRequestProcessorImpl;
import com.cws.esolutions.core.processors.interfaces.IDNSServiceRequestProcessor;
/**
 * eSolutions_java_source
 * com.cws.us.esolutions.controllers
 * DNSServiceController.java
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
@RequestMapping("/dns-service")
public class DNSServiceController
{
    private String serviceId = null;
    private String lookupPage = null;
    private String serviceName = null;
    private String defaultPage = null;
    private String serviceHost = null;
    private String[] searchSuffix = null;
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

    public final void setDefaultPage(final String value)
    {
        final String methodName = DNSServiceController.CNAME + "#setDefaultPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.defaultPage = value;
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

    @RequestMapping(value = "/default", method = RequestMethod.GET)
    public ModelAndView showDefaultPage()
    {
        final String methodName = DNSServiceController.CNAME + "#showDefaultPage()";

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
            DEBUGGER.debug("HttpSession: {}", hSession);
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
            mView = new ModelAndView();
            mView.setViewName(this.defaultPage);
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

    @RequestMapping(value = "/lookup", method = RequestMethod.GET)
    public ModelAndView showLookup()
    {
        final String methodName = DNSServiceController.CNAME + "#showLookup()";

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
            DEBUGGER.debug("HttpSession: {}", hSession);
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
        }

        if (userAccount.getStatus() == LoginStatus.EXPIRED)
        {
            // redirect to password page
            mView = new ModelAndView(new RedirectView());
            mView.setViewName(appConfig.getExpiredRedirect());
            mView.addObject(Constants.ERROR_MESSAGE, Constants.PASSWORD_EXPIRED);
            return mView;
        }

        if (appConfig.getServices().get(this.serviceName))
        {
            mView.addObject("command", new DNSRecord());
            mView.setViewName(this.lookupPage);
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

    @RequestMapping(value = "/lookup", method = RequestMethod.POST)
    public ModelAndView showLookup(@ModelAttribute("entry") final DNSRecord record, final BindingResult bindResult)
    {
        final String methodName = DNSServiceController.CNAME + "#showLookup(@ModelAttribute(\"entry\") final DNSRecord record, final BindingResult bindResult)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("DNSRecord: {}", record);
            DEBUGGER.debug("BindingResult: {}", bindResult);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IDNSServiceRequestProcessor dnsProcessor = new DNSServiceRequestProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("HttpSession: {}", hSession);
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
        }

        if (userAccount.getStatus() == LoginStatus.EXPIRED)
        {
            // redirect to password page
            mView = new ModelAndView(new RedirectView());
            mView.setViewName(appConfig.getExpiredRedirect());
            mView.addObject(Constants.ERROR_MESSAGE, Constants.PASSWORD_EXPIRED);
            return mView;
        }

        if (appConfig.getServices().get(this.serviceName))
        {
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

                DNSServiceRequest request = new DNSServiceRequest();
                request.setRecord(record);
                request.setRequestInfo(reqInfo);
                request.setUserAccount(userAccount);
                request.setServiceId(this.serviceId);
                request.setSearchURL(this.serviceHost);
                request.setSearchPath(this.searchSuffix);
                request.setRequestType(DNSRequestType.LOOKUP);

                if (DEBUG)
                {
                    DEBUGGER.debug("DNSServiceRequest: {}", request);
                }

                DNSServiceResponse response = dnsProcessor.performLookup(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("DNSServiceResponse: {}", response);
                }

                if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
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
                        mView.addObject(Constants.ERROR_MESSAGE, response.getResponse());
                    }
                }
                else
                {
                    mView.addObject(Constants.ERROR_MESSAGE, response.getResponse());
                }

                mView.addObject("command", new DNSRecord());
                mView.setViewName(this.lookupPage);
            }
            catch (DNSServiceException dsx)
            {
                ERROR_RECORDER.error(dsx.getMessage(), dsx);

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
}
