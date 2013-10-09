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
package com.cws.us.pws.controllers;

import java.util.Arrays;
import org.slf4j.Logger;
import java.util.ArrayList;
import java.util.Enumeration;
import org.slf4j.LoggerFactory;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.cws.us.pws.Constants;
import com.cws.us.pws.ApplicationConfig;
import com.cws.us.pws.validators.ContactValidator;
import com.cws.esolutions.core.processors.dto.EmailMessage;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.dto.MessagingRequest;
import com.cws.esolutions.core.processors.dto.MessagingResponse;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.core.processors.interfaces.IMessagingProcessor;
import com.cws.esolutions.core.processors.impl.EmailMessagingProcessorImpl;
import com.cws.esolutions.core.processors.exception.MessagingServiceException;
/**
 * CWSPWS_java_source
 * com.cws.us.pws.controllers
 * HomeController.java
 *
 * This is a VERY basic controller because it really doesn't need to do
 * anything. It loads the home page and that's pretty much it - nothing
 * fancy.
 *
 * $Id$
 * $Author$
 * $Date$
 * $Revision$
 * @author 35033355
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * 35033355 @ Apr 10, 2013 12:59:40 PM
 *     Created.
 */
@Controller
@RequestMapping("/contact")
public class ContactController
{
    private String methodName = null;
    private ContactValidator validator = null;
    private ApplicationConfig appConfig = null;

    private static final String CNAME = ContactController.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER);

    public final void setAppConfig(final ApplicationConfig value)
    {
        this.methodName = ContactController.CNAME + "#setAppConfig(final ApplicationConfig value)";

        if (DEBUG)
        {
            DEBUGGER.debug(this.methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.appConfig = value;
    }

    public final void setValidator(final ContactValidator value)
    {
        this.methodName = ContactController.CNAME + "#setValidator(final ContactValidator value)";

        if (DEBUG)
        {
            DEBUGGER.debug(this.methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.validator = value;
    }

    @RequestMapping(value = "/default.htm", method = RequestMethod.GET)
    public ModelAndView showDefaultPage()
    {
        this.methodName = ContactController.CNAME + "#showDefaultPage()";

        if (DEBUG)
        {
            DEBUGGER.debug(this.methodName);
        }

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();

        if (DEBUG)
        {
            DEBUGGER.debug("HttpSession: {}", hSession);

            DEBUGGER.debug("Dumping session content:");
            @SuppressWarnings("unchecked")
            Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked")
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }
        }

        return new ModelAndView("addContact", "command", new EmailMessage());
    }

    @RequestMapping(value = "/contact.htm", method = RequestMethod.GET)
    public ModelAndView showMessagingPage()
    {
        this.methodName = ContactController.CNAME + "#showMessagingPage()";

        if (DEBUG)
        {
            DEBUGGER.debug(this.methodName);
        }

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();

        if (DEBUG)
        {
            DEBUGGER.debug("HttpSession: {}", hSession);

            DEBUGGER.debug("Dumping session content:");
            @SuppressWarnings("unchecked")
            Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked")
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }
        }

        return new ModelAndView("addContact", "command", new EmailMessage());
    }

    @RequestMapping(value = "/contact.htm", method = RequestMethod.POST)
    public ModelAndView sendMessage(@ModelAttribute("message") final EmailMessage message, final BindingResult bindResult)
    {
        this.methodName = ContactController.CNAME + "#sendMessage(@ModelAttribute(\"message\") final EmailMessage message, final BindingResult bindResult)";

        if (DEBUG)
        {
            DEBUGGER.debug(this.methodName);
            DEBUGGER.debug("MessagingRequest: {}", message);
            DEBUGGER.debug("BindingResult: {}", bindResult);
        }

        String viewName = null;

        final IMessagingProcessor msgProcessor = new EmailMessagingProcessorImpl();
        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();

        if (DEBUG)
        {
            DEBUGGER.debug("HttpSession: {}", hSession);

            DEBUGGER.debug("Dumping session content:");
            @SuppressWarnings("unchecked")
            Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked")
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }
        }

        try
        {
            // validate
            validator.validate(message, bindResult);

            if (bindResult.hasErrors())
            {
                // errors occurred during validation
                ERROR_RECORDER.error("Form failed field validation");

                viewName = "addContact";
            }
            else
            {
                final RequestHostInfo reqInfo = new RequestHostInfo();
                reqInfo.setHostAddress(hRequest.getRemoteAddr());
                reqInfo.setHostName(hRequest.getRemoteHost());

                if (DEBUG)
                {
                    DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
                }

                // add in a message id
                message.setMessageId(RandomStringUtils.randomAlphanumeric(12));
                message.setMessageFrom(new ArrayList<String>(Arrays.asList(appConfig.getServiceEmail())));

                if (DEBUG)
                {
                    DEBUGGER.debug("Message: {}", message);
                }

                MessagingRequest request = new MessagingRequest();
                request.setEmailMessage(message);
                request.setRequestInfo(reqInfo);
                request.setWebRequest(true);

                if (DEBUG)
                {
                    DEBUGGER.debug("MessagingRequest: {}", request);
                }

                MessagingResponse response = msgProcessor.addNewMessage(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("MessagingResponse: {}", response);
                }

                if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    // good, present the data
                    viewName = "contactResponse";
                }
                else
                {
                    viewName = "errorResponse";
                }
            }
        }
        catch (MessagingServiceException msx)
        {
            ERROR_RECORDER.error(msx.getMessage(), msx);

            viewName = "errorResponse";
        }

        if (DEBUG)
        {
            DEBUGGER.debug("viewName: {}", viewName);
        }

        return new ModelAndView(viewName);
    }
}
