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
 * File: CommonController.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
import java.util.Objects;
import java.util.Enumeration;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.validation.BindingResult;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.cws.esolutions.web.Constants;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.web.ApplicationServiceBean;
import com.cws.esolutions.core.utils.dto.EmailMessage;
import com.cws.esolutions.web.validators.EmailMessageValidator;
/**
 * @author cws-khuntly
 * @version 1.0
 * @see org.springframework.stereotype.Controller
 */
@Controller
@RequestMapping("common")
public class CommonController
{
    private String homePage = null;
    private JavaMailSender mailSender = null;
    private ApplicationServiceBean appConfig = null;
    private SimpleMailMessage contactResponseEmail = null;

    private static final String CNAME = CommonController.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CNAME);

    public final void setAppConfig(final ApplicationServiceBean value)
    {
        final String methodName = CommonController.CNAME + "#setAppConfig(final ApplicationServiceBean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.appConfig = value;
    }

    public final void setMailSender(final JavaMailSender value)
    {
        final String methodName = CommonController.CNAME + "#setMailSender(final JavaMailSender value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.mailSender = value;
    }

    public final void setHomePage(final String value)
    {
        final String methodName = CommonController.CNAME + "#setHomePage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.homePage = value;
    }

    public final void setContactResponseEmail(final SimpleMailMessage value)
    {
        final String methodName = CommonController.CNAME + "#setContactResponseEmail(final SimpleMailMessage value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.contactResponseEmail = value;
    }

    @RequestMapping(value = "default", method = RequestMethod.GET)
    public final String showDefaultPage(final Model model)
    {
        final String methodName = CommonController.CNAME + "#showDefaultPage(final Model model)";

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

    @RequestMapping(value = "contact", method = RequestMethod.GET)
    public final String showContactPage(final Model model)
    {
        final String methodName = CommonController.CNAME + "#showContactPage(final Model model)";

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

        model.addAttribute("serviceEmail", this.appConfig.getEmailAddress());
        model.addAttribute(Constants.COMMAND, new EmailMessage());

        return this.appConfig.getContactAdminsPage();
    }

    @RequestMapping(value = "contact", method = RequestMethod.POST)
    public final String doSubmitMessage(@ModelAttribute("message") final EmailMessage message, final Model model, final BindingResult bindResult)
    {
        final String methodName = CommonController.CNAME + "#doSubmitMessage(@ModelAttribute(\"message\") final EmailMessage message, final Model model, final BindingResult bindResult)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("EmailMessage: {}", message);
            DEBUGGER.debug("BindingResult: {}", bindResult);
        }

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final EmailMessageValidator validator = this.appConfig.getMessageValidator();
        final String emailId = RandomStringUtils.randomAlphanumeric(16);

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("EmailMessageValidator: {}", validator);
            DEBUGGER.debug("emailId: {}", emailId);

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

        validator.validate(message, bindResult);

        if (bindResult.hasErrors())
        {
            // validation failed
            ERROR_RECORDER.error("Errors: {}", bindResult.getAllErrors());

            model.addAttribute("serviceEmail", this.appConfig.getEmailAddress());
            model.addAttribute(Constants.ERROR_MESSAGE, this.appConfig.getMessageValidationFailed());
            model.addAttribute(Constants.BIND_RESULT, bindResult.getAllErrors());
            model.addAttribute(Constants.COMMAND, new EmailMessage());

            return this.appConfig.getContactAdminsPage();
        }

        try
        {
        	SimpleMailMessage emailMessage = new SimpleMailMessage();
        	emailMessage.setTo(message.getEmailAddr().get(0));
        	emailMessage.setSubject(message.getMessageSubject());
        	emailMessage.setText(message.getMessageBody());

        	if (DEBUG)
        	{
        		DEBUGGER.debug("SimpleMailMessage: {}", emailMessage);
        	}

        	mailSender.send(emailMessage);

        	SimpleMailMessage autoResponse = new SimpleMailMessage();
        	autoResponse.setReplyTo(this.contactResponseEmail.getFrom());
        	autoResponse.setTo(this.contactResponseEmail.getTo()[0]);
        	autoResponse.setSubject(this.contactResponseEmail.getSubject());
        	autoResponse.setText(message.getMessageBody());

            if (DEBUG)
            {
                DEBUGGER.debug("EmailMessage: {}", autoResponse);
            }

            mailSender.send(autoResponse);

            model.addAttribute("serviceEmail", this.appConfig.getEmailAddress());
            model.addAttribute(Constants.COMMAND, new EmailMessage());
            model.addAttribute(Constants.RESPONSE_MESSAGE, this.appConfig.getMessageEmailSentSuccess());
            
            return this.appConfig.getContactAdminsRedirect();
        }
        catch (final MailException mx)
        {
            ERROR_RECORDER.error(mx.getMessage(), mx);

            model.addAttribute("serviceEmail", this.appConfig.getEmailAddress());
            model.addAttribute(Constants.COMMAND, new EmailMessage());
            model.addAttribute(Constants.ERROR_MESSAGE, this.appConfig.getMessageRequestProcessingFailure());

            return this.appConfig.getContactAdminsPage();
        }
    }
}
