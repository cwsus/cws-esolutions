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
 * cws-khuntly   11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Enumeration;
import org.slf4j.LoggerFactory;
import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.cws.esolutions.web.Constants;
import com.cws.esolutions.core.utils.EmailUtils;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.web.ApplicationServiceBean;
import com.cws.esolutions.core.utils.dto.EmailMessage;
import com.cws.esolutions.web.validators.EmailMessageValidator;
import com.cws.esolutions.core.processors.dto.MessagingRequest;
import com.cws.esolutions.core.config.xml.CoreConfigurationData;
import com.cws.esolutions.core.processors.dto.MessagingResponse;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.core.processors.exception.MessagingServiceException;
import com.cws.esolutions.core.processors.impl.ServiceMessagingProcessorImpl;
/**
 * @author cws-khuntly
 * @version 1.0
 * @see org.springframework.stereotype.Controller
 */
@Controller
@RequestMapping("/common")
public class CommonController
{
    private String homePage = null;
    private String serviceId = null;
    private CoreConfigurationData coreConfig = null;
    private ApplicationServiceBean appConfig = null;
    private SimpleMailMessage contactResponseEmail = null;
    private ServiceMessagingProcessorImpl processor = null;

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

    public final void setCoreConfig(final CoreConfigurationData value)
    {
        final String methodName = CommonController.CNAME + "#setCoreConfig(final CoreConfigurationData value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.coreConfig = value;
    }

    public final void setProcessor(final ServiceMessagingProcessorImpl value)
    {
        final String methodName = CommonController.CNAME + "#setProcessor(final ServiceMessagingProcessorImpl value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.processor = value;
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

    public final void setServiceId(final String value)
    {
        final String methodName = CommonController.CNAME + "#setServiceId(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serviceId = value;
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

    @RequestMapping(value = "/default", method = RequestMethod.GET)
    public final ModelAndView showDefaultPage()
    {
        final String methodName = CommonController.CNAME + "#showDefaultPage()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        ModelAndView mView = new ModelAndView();
        mView.setViewName(this.homePage);

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

            MessagingRequest mRequest = new MessagingRequest();
            mRequest.setRequestInfo(reqInfo);
            mRequest.setUserAccount(userAccount);
            mRequest.setServiceId(this.serviceId);
            mRequest.setApplicationId(this.appConfig.getApplicationId());
            mRequest.setApplicationName(this.appConfig.getApplicationName());

            if (DEBUG)
            {
                DEBUGGER.debug("MessagingRequest: {}", mRequest);
            }

            MessagingResponse mResponse = this.processor.showMessages(mRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("MessagingResponse: {}", mResponse);
            }

            if (mResponse.getRequestStatus() == CoreServicesStatus.SUCCESS)
            {
                mView.addObject("dateFormat", this.appConfig.getDateFormat());
                mView.addObject("messageList", mResponse.getSvcMessages());
            }

            MessagingResponse messageResponse = this.processor.showAlertMessages(new MessagingRequest());

            if (DEBUG)
            {
                DEBUGGER.debug("MessagingResponse: {}", messageResponse);
            }

            if (messageResponse.getRequestStatus() == CoreServicesStatus.SUCCESS)
            {
                mView.addObject("alertMessages", messageResponse.getSvcMessages());
            }
        }
        catch (MessagingServiceException msx)
        {
            // log it, but dont redirect or anything
            ERROR_RECORDER.error(msx.getMessage(), msx);
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        // in here, we're going to get all the messages to display and such
        return mView;
    }

    @RequestMapping(value = "/unauthorized", method = RequestMethod.GET)
    public final ModelAndView showUnauthorizedPage()
    {
        final String methodName = CommonController.CNAME + "#showUnauthorizedPage()";

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

        mView.setViewName(this.appConfig.getUnauthorizedPage());

        return mView;
    }

    @RequestMapping(value = "/contact", method = RequestMethod.GET)
    public final ModelAndView showContactPage()
    {
        final String methodName = CommonController.CNAME + "#showContactPage()";

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

        mView.addObject("serviceEmail", this.coreConfig.getAppConfig().getEmailAliasId());
        mView.addObject(Constants.COMMAND, new EmailMessage());
        mView.setViewName(this.appConfig.getContactAdminsPage());

        return mView;
    }

    @RequestMapping(value = "/contact", method = RequestMethod.POST)
    public final ModelAndView doSubmitMessage(@ModelAttribute("message") final EmailMessage message, final BindingResult bindResult)
    {
        final String methodName = CommonController.CNAME + "#doSubmitMessage(@ModelAttribute(\"message\") final EmailMessage message, final BindingResult bindResult)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("EmailMessage: {}", message);
            DEBUGGER.debug("BindingResult: {}", bindResult);
        }

        ModelAndView mView = new ModelAndView();

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

            mView.addObject(Constants.ERROR_MESSAGE, this.appConfig.getMessageValidationFailed());
            mView.addObject(Constants.BIND_RESULT, bindResult.getAllErrors());
            mView.addObject(Constants.COMMAND, message);
            mView.setViewName(this.appConfig.getContactAdminsPage());

            return mView;
        }

        try
        {
            EmailUtils.sendEmailMessage(this.coreConfig.getMailConfig(), message, true);

            EmailMessage autoResponse = new EmailMessage();
            autoResponse.setIsAlert(false);
            autoResponse.setMessageSubject(this.contactResponseEmail.getSubject());
            autoResponse.setMessageTo(new ArrayList<String>(Arrays.asList(String.format(this.contactResponseEmail.getTo()[0], message.getEmailAddr().get(0)))));
            autoResponse.setEmailAddr(new ArrayList<String>(Arrays.asList(String.format(this.contactResponseEmail.getFrom()))));
            autoResponse.setMessageBody(String.format(
                this.contactResponseEmail.getText(),
                message.getEmailAddr(),
                message.getMessageBody()));

            if (DEBUG)
            {
                DEBUGGER.debug("EmailMessage: {}", autoResponse);
            }

            EmailUtils.sendEmailMessage(this.coreConfig.getMailConfig(), autoResponse, true);

            mView = new ModelAndView(new RedirectView());
            mView.setViewName(this.appConfig.getContactAdminsRedirect());
            mView.addObject(Constants.RESPONSE_MESSAGE, this.appConfig.getMessageEmailSentSuccess());
        }
        catch (MessagingException mx)
        {
            ERROR_RECORDER.error(mx.getMessage(), mx);

            mView = new ModelAndView(new RedirectView());
            mView.setViewName(this.appConfig.getContactAdminsPage());
            mView.addObject(Constants.ERROR_MESSAGE, this.appConfig.getMessageRequestProcessingFailure());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }
}
