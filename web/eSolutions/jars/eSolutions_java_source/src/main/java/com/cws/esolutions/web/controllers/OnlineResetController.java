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
 * File: LoginController.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.util.Date;
import org.slf4j.Logger;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Enumeration;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.cws.esolutions.web.Constants;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.web.ApplicationServiceBean;
import com.cws.esolutions.core.utils.dto.EmailMessage;
import com.cws.esolutions.web.validators.OnlineResetValidator;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.core.processors.dto.MessagingRequest;
import com.cws.esolutions.core.processors.dto.MessagingResponse;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.security.processors.dto.AccountChangeData;
import com.cws.esolutions.security.processors.enums.ResetRequestType;
import com.cws.esolutions.security.processors.dto.AuthenticationData;
import com.cws.esolutions.security.processors.dto.AccountResetRequest;
import com.cws.esolutions.security.processors.dto.AccountResetResponse;
import com.cws.esolutions.security.processors.dto.AccountControlRequest;
import com.cws.esolutions.security.processors.dto.AccountControlResponse;
import com.cws.esolutions.core.processors.interfaces.IWebMessagingProcessor;
import com.cws.esolutions.security.processors.impl.AccountResetProcessorImpl;
import com.cws.esolutions.core.processors.impl.ServiceMessagingProcessorImpl;
import com.cws.esolutions.core.processors.exception.MessagingServiceException;
import com.cws.esolutions.security.processors.exception.AccountResetException;
import com.cws.esolutions.security.processors.impl.AccountControlProcessorImpl;
import com.cws.esolutions.security.processors.exception.AccountControlException;
import com.cws.esolutions.security.processors.interfaces.IAccountResetProcessor;
import com.cws.esolutions.security.processors.interfaces.IAccountControlProcessor;
/**
 * @author cws-khuntly
 * @version 1.0
 * @see org.springframework.stereotype.Controller
 */
@Controller
@RequestMapping("/online-reset")
public class OnlineResetController
{
    private String resetURL = null;
    private String submitAnswersPage = null;
    private JavaMailSender mailSender = null;
    private String submitUsernamePage = null;
    private String submitEmailAddrPage = null;
    private String messageRequestFailure = null;
    private String messageRequestComplete = null;
    private OnlineResetValidator validator = null;
    private ApplicationServiceBean appConfig = null;

    private SimpleMailMessage forgotUsernameEmail = null;
    private SimpleMailMessage forgotPasswordEmail = null;

    private static final String RESET_KEY_ID = "resetKey";
    private static final String CNAME = OnlineResetController.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CNAME);

    public final void setMailSender(final JavaMailSender value)
    {
        final String methodName = OnlineResetController.CNAME + "#setMailSender(final JavaMailSender value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.mailSender = value;
    }

    public final void setResetURL(final String value)
    {
        final String methodName = OnlineResetController.CNAME + "#setResetURL(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.resetURL = value;
    }

    public final void setMessageRequestComplete(final String value)
    {
        final String methodName = OnlineResetController.CNAME + "#setMessageRequestComplete(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageRequestComplete = value;
    }

    public final void setMessageRequestFailure(final String value)
    {
        final String methodName = OnlineResetController.CNAME + "#setMessageRequestFailure(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageRequestFailure = value;
    }

    public final void setSubmitAnswersPage(final String value)
    {
        final String methodName = OnlineResetController.CNAME + "#setSubmitAnswersPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.submitAnswersPage = value;
    }

    public final void setSubmitUsernamePage(final String value)
    {
        final String methodName = OnlineResetController.CNAME + "#setSubmitUsernamePage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.submitUsernamePage = value;
    }

    public final void setSubmitEmailAddrPage(final String value)
    {
        final String methodName = OnlineResetController.CNAME + "#setSubmitEmailAddrPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.submitEmailAddrPage = value;
    }

    public final void setAppConfig(final ApplicationServiceBean value)
    {
        final String methodName = OnlineResetController.CNAME + "#setAppConfig(final ApplicationServiceBean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.appConfig = value;
    }

    public final void setValidator(final OnlineResetValidator value)
    {
        final String methodName = OnlineResetController.CNAME + "#setValidator(final OnlineResetValidator value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.validator = value;
    }

    public final void setForgotUsernameEmail(final SimpleMailMessage value)
    {
        final String methodName = OnlineResetController.CNAME + "#setForgotUsernameEmail(final SimpleMailMessage value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.forgotUsernameEmail = value;
    }

    public final void setForgotPasswordEmail(final SimpleMailMessage value)
    {
        final String methodName = OnlineResetController.CNAME + "#setForgotPasswordEmail(final SimpleMailMessage value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.forgotPasswordEmail = value;
    }

    @RequestMapping(value = "/forgot-username", method = RequestMethod.GET)
    public final String showForgotUsername(final Model model)
    {
        final String methodName = OnlineResetController.CNAME + "#showForgotUsername()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Model: {}", model);
        }

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final IWebMessagingProcessor svcMessage = new ServiceMessagingProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());

            DEBUGGER.debug("Dumping session content:");
            Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String element = sessionEnumeration.nextElement();
                Object value = hSession.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String element = paramsEnumeration.nextElement();
                Object value = hRequest.getParameter(element);

                DEBUGGER.debug("Parameter: {}; Value: {}", element, value);
            }
        }

        try
        {
            MessagingResponse messageResponse = svcMessage.showAlertMessages(new MessagingRequest());

            if (DEBUG)
            {
                DEBUGGER.debug("MessagingResponse: {}", messageResponse);
            }

            if (messageResponse.getRequestStatus() == CoreServicesStatus.SUCCESS)
            {
            	model.addAttribute("alertMessages", messageResponse.getSvcMessages());
            }
        }
        catch (final MessagingServiceException msx)
        {
            // don't do anything with it
        }

        model.addAttribute("resetType", ResetRequestType.USERNAME);
        model.addAttribute(Constants.COMMAND, new AccountChangeData());

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", model);
        }

        return this.submitEmailAddrPage;
    }

    @RequestMapping(value = "/forgot-password", method = RequestMethod.GET)
    public final String showForgottenPassword(final Model model)
    {
        final String methodName = OnlineResetController.CNAME + "#showForgottenPassword()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Model: {}", model);
        }

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());

            DEBUGGER.debug("Dumping session content:");
            Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String element = sessionEnumeration.nextElement();
                Object value = hSession.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String element = paramsEnumeration.nextElement();
                Object value = hRequest.getParameter(element);

                DEBUGGER.debug("Parameter: {}; Value: {}", element, value);
            }
        }

        model.addAttribute("resetType", ResetRequestType.PASSWORD);
        model.addAttribute(Constants.COMMAND, new AccountChangeData());

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", model);
        }

        return this.submitUsernamePage;
    }

    @RequestMapping(value = "/forgot-password/{resetId}", method = RequestMethod.GET)
    public final String showPasswordChange(@PathVariable(value = "resetId") final String resetId, final Model model)
    {
        final String methodName = OnlineResetController.CNAME + "#showPasswordChange(@PathVariable(value = \"resetId\") final String resetId, final Model model)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("resetId: {}", resetId);
        }

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());

            DEBUGGER.debug("Dumping session content:");
            Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String element = sessionEnumeration.nextElement();
                Object value = hSession.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String element = paramsEnumeration.nextElement();
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

            AccountResetRequest resetReq = new AccountResetRequest();
            resetReq.setHostInfo(reqInfo);
            resetReq.setApplicationId(this.appConfig.getApplicationId());
            resetReq.setApplicationName(this.appConfig.getApplicationName());

            IAccountResetProcessor resetProcessor = (IAccountResetProcessor) new AccountResetProcessorImpl();
            AccountResetResponse resetRes = resetProcessor.verifyResetRequest(resetReq);

            if (DEBUG)
            {
                DEBUGGER.debug("AccountResetResponse: {}", resetRes);
            }

            if (resetRes.getRequestStatus() == SecurityRequestStatus.SUCCESS)
            {
                UserAccount userAccount = resetRes.getUserAccount();

                if (DEBUG)
                {
                    DEBUGGER.debug("UserAccount: {}", userAccount);
                }

                if (userAccount.isSuspended())
                {
                    // this account is suspended, we cant work on it
                    hSession.invalidate();

                    model.addAttribute(Constants.ERROR_MESSAGE, this.appConfig.getMessageUserNotLoggedIn());
                    return this.appConfig.getLogonRedirect();
                }
                else
                {
                    // add in the session id
                    hSession.setAttribute(Constants.USER_ACCOUNT, userAccount);

                    model.addAttribute(OnlineResetController.RESET_KEY_ID, resetId);
                    return this.appConfig.getLogonRedirect();
                }
            }
            else
            {
                // user not logged in, redirect
                hSession.invalidate();

                return this.appConfig.getLogonRedirect();
            }
        }
        catch (final AccountResetException arx)
        {
            ERROR_RECORDER.error(arx.getMessage(), arx);

            return this.appConfig.getErrorResponsePage();
        }
    }

    @RequestMapping(value = "/cancel", method = RequestMethod.GET)
    public final String doCancelRequest(final Model model)
    {
        final String methodName = OnlineResetController.CNAME + "#doCancelRequest()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());

            DEBUGGER.debug("Dumping session content:");
            Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String element = sessionEnumeration.nextElement();
                Object value = hSession.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String element = paramsEnumeration.nextElement();
                Object value = hRequest.getParameter(element);

                DEBUGGER.debug("Parameter: {}; Value: {}", element, value);
            }
        }

        hSession.invalidate(); // clear the http session

        model.addAttribute(Constants.RESPONSE_MESSAGE, this.appConfig.getMessageRequestCanceled());

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", model);
        }

        return this.appConfig.getLogonRedirect();
    }

    @RequestMapping(value = "/forgot-username", method = RequestMethod.POST)
    public final String submitForgottenUsername(@ModelAttribute("request") final AccountChangeData request, final BindingResult bindResult, final Model model)
    {
        final String methodName = OnlineResetController.CNAME + "#submitForgottenUsername(@ModelAttribute(\"UserChangeRequest\") final UserChangeRequest request, final BindingResult bindResult, final Model model)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("UserChangeRequest: {}", request);
            DEBUGGER.debug("BindingResult: {}", bindResult);
            DEBUGGER.debug("Model: {}", model);
        }

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final IAccountControlProcessor acctController = new AccountControlProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());

            DEBUGGER.debug("Dumping session content:");
            Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String element = sessionEnumeration.nextElement();
                Object value = hSession.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String element = paramsEnumeration.nextElement();
                Object value = hRequest.getParameter(element);

                DEBUGGER.debug("Parameter: {}; Value: {}", element, value);
            }
        }

        // validate
        this.validator.validate(request, bindResult);

        if (bindResult.hasErrors())
        {
            // validation failed
            ERROR_RECORDER.error("Errors: {}", bindResult.getAllErrors());

            model.addAttribute(Constants.ERROR_MESSAGE, this.appConfig.getMessageValidationFailed());
            model.addAttribute(Constants.BIND_RESULT, bindResult.getAllErrors());
            model.addAttribute(Constants.COMMAND, new AccountChangeData());

            return this.submitUsernamePage;
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

            UserAccount reqAccount = new UserAccount();
            reqAccount.setEmailAddr(request.getEmailAddr());

            if (DEBUG)
            {
                DEBUGGER.debug("UserAccount: {}", reqAccount);
            }

            AccountControlRequest controlReq = new AccountControlRequest();
            controlReq.setHostInfo(reqInfo);
            controlReq.setUserAccount(reqAccount);
            controlReq.setApplicationId(this.appConfig.getApplicationId());
            controlReq.setApplicationName(this.appConfig.getApplicationName());

            if (DEBUG)
            {
                DEBUGGER.debug("AccountControlRequest: {}", request);
            }

            AccountControlResponse response = acctController.searchAccounts(controlReq);

            if (DEBUG)
            {
                DEBUGGER.debug("AccountControlResponse: {}", response);
            }

            if (response.getRequestStatus() == SecurityRequestStatus.SUCCESS)
            {
                // this will return a single user account
                UserAccount userAccount = response.getUserList().get(0);

                if (DEBUG)
                {
                    DEBUGGER.debug("UserAccount: {}", userAccount);
                }

                try
                {
                    EmailMessage message = new EmailMessage();
                    message.setIsAlert(false);
                    message.setMessageSubject(this.forgotUsernameEmail.getSubject());
                    message.setMessageTo(new ArrayList<String>(
                            Arrays.asList(
                                    String.format(this.forgotUsernameEmail.getTo()[0], userAccount.getEmailAddr()))));
                    message.setEmailAddr(new ArrayList<String>(
                            Arrays.asList(
                                    String.format(this.forgotUsernameEmail.getTo()[0], this.appConfig.getEmailAddress()))));
                    message.setMessageBody(String.format(this.forgotUsernameEmail.getText(),
                            userAccount.getGivenName(),
                            new Date(System.currentTimeMillis()),
                            reqInfo.getHostName(),
                            userAccount.getUsername()));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("EmailMessage: {}", message);
                    }

                    // TODO
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
                	autoResponse.setReplyTo(this.appConfig.getEmailAddress());
                	autoResponse.setTo(message.getEmailAddr().get(0));
                	autoResponse.setSubject(message.getMessageSubject());
                	autoResponse.setText(message.getMessageBody());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("EmailMessage: {}", autoResponse);
                    }

                    mailSender.send(autoResponse);
                }
                catch (final MailException mx)
                {
                    ERROR_RECORDER.error(mx.getMessage(), mx);

                    model.addAttribute(Constants.ERROR_MESSAGE, this.appConfig.getMessageEmailSendFailed());
                }

                model.addAttribute(Constants.MESSAGE_RESPONSE, this.messageRequestComplete);
                return this.appConfig.getLogonRedirect();
            }
            else
            {
                return this.appConfig.getErrorResponsePage();
            }
        }
        catch (final AccountControlException acx)
        {
            ERROR_RECORDER.error(acx.getMessage(), acx);

            return this.appConfig.getErrorResponsePage();
        }
    }

    @RequestMapping(value = "/forgot-password", method = RequestMethod.POST)
    public final String submitUsername(@ModelAttribute("request") final AccountChangeData request, final BindingResult bindResult, final Model model)
    {
        final String methodName = OnlineResetController.CNAME + "#submitUsername(@ModelAttribute(\"UserChangeRequest\") final UserChangeRequest request, final BindingResult bindResult, final Model model)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("UserChangeRequest: {}", request);
            DEBUGGER.debug("BindingResult: {}", bindResult);
            DEBUGGER.debug("Model: {}", model);
        }

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final IAccountResetProcessor processor = new AccountResetProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());

            DEBUGGER.debug("Dumping session content:");
            Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String element = sessionEnumeration.nextElement();
                Object value = hSession.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String element = paramsEnumeration.nextElement();
                Object value = hRequest.getParameter(element);

                DEBUGGER.debug("Parameter: {}; Value: {}", element, value);
            }
        }

        this.validator.validate(request, bindResult);

        if (bindResult.hasErrors())
        {
            // validation failed
            ERROR_RECORDER.error("Errors: {}", bindResult.getAllErrors());

            model.addAttribute(Constants.ERROR_MESSAGE, this.appConfig.getMessageValidationFailed());
            model.addAttribute(Constants.BIND_RESULT, bindResult.getAllErrors());
            model.addAttribute(Constants.COMMAND, new AccountChangeData());

            return this.submitUsernamePage;
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

            UserAccount reqAccount = new UserAccount();
            reqAccount.setUsername(request.getUsername());

            if (DEBUG)
            {
                DEBUGGER.debug("UserAccount: {}", reqAccount);
            }

            AccountResetRequest resetReq = new AccountResetRequest();
            resetReq.setApplicationId(this.appConfig.getApplicationId());
            resetReq.setApplicationName(this.appConfig.getApplicationName());
            resetReq.setHostInfo(reqInfo);
            resetReq.setUserAccount(reqAccount);

            if (DEBUG)
            {
                DEBUGGER.debug("AuthenticationRequest: {}", resetReq);
            }

            AccountResetResponse response = processor.obtainUserSecurityConfig(resetReq);

            if (DEBUG)
            {
                DEBUGGER.debug("AccountResetResponse: {}", response);
            }

            if (response.getRequestStatus() == SecurityRequestStatus.SUCCESS)
            {
                UserAccount resAccount = response.getUserAccount();

                if ((resAccount.isSuspended()) || (resAccount.isOlrLocked()))
                {
                    return this.appConfig.getUnauthorizedPage();
                }

                AuthenticationData userSec = response.getUserSecurity();

                if (DEBUG)
                {
                    DEBUGGER.debug("AuthenticationData: {}", userSec);
                }

                AccountChangeData changeReq = new AccountChangeData();
                changeReq.setSecQuestionOne(userSec.getSecQuestionOne());
                changeReq.setSecQuestionTwo(userSec.getSecQuestionTwo());

                if (DEBUG)
                {
                    DEBUGGER.debug("UserChangeRequest: {}", changeReq);
                }

                // xlnt. set the user
                hSession.setAttribute(Constants.USER_ACCOUNT, resAccount);

                model.addAttribute("resetType", ResetRequestType.QUESTIONS);
                model.addAttribute(Constants.COMMAND, changeReq);

                return this.submitAnswersPage;
            }
            else
            {
                return this.appConfig.getErrorResponsePage();
            }
        }
        catch (final AccountResetException arx)
        {
            ERROR_RECORDER.error(arx.getMessage(), arx);

            return this.appConfig.getErrorResponsePage();
        }
    }

    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    public final String submitSecurityResponse(@ModelAttribute("request") final AccountChangeData request, final BindingResult bindResult, final Model model)
    {
        final String methodName = OnlineResetController.CNAME + "#submitSecurityResponse(@ModelAttribute(\"request\") final UserChangeRequest request, final BindingResult bindResult, final Model model)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("UserChangeRequest: {}", request);
            DEBUGGER.debug("BindingResult: {}", bindResult);
            DEBUGGER.debug("Model: {}", model);
        }

        boolean resetError = false;

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IAccountResetProcessor processor = new AccountResetProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String element = sessionEnumeration.nextElement();
                Object value = hSession.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String element = paramsEnumeration.nextElement();
                Object value = hRequest.getParameter(element);

                DEBUGGER.debug("Parameter: {}; Value: {}", element, value);
            }
        }

        this.validator.validate(request, bindResult);

        if (bindResult.hasErrors())
        {
            // validation failed
            ERROR_RECORDER.error("Errors: {}", bindResult.getAllErrors());

            model.addAttribute(Constants.ERROR_MESSAGE, this.appConfig.getMessageValidationFailed());
            model.addAttribute(Constants.BIND_RESULT, bindResult.getAllErrors());
            model.addAttribute(Constants.COMMAND, request);

            return this.submitAnswersPage;
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

            AuthenticationData userSecurity = new AuthenticationData();
            userSecurity.setSecAnswerOne(request.getSecAnswerOne());
            userSecurity.setSecAnswerTwo(request.getSecAnswerTwo());

            if (DEBUG)
            {
                DEBUGGER.debug("AuthenticationData: {}", userSecurity);
            }

            AccountResetRequest resRequest = new AccountResetRequest();
            resRequest.setApplicationId(this.appConfig.getApplicationId());
            resRequest.setApplicationName(this.appConfig.getApplicationName());
            resRequest.setHostInfo(reqInfo);
            resRequest.setUserAccount(userAccount);
            resRequest.setCount(request.getCount());
            resRequest.setUserSecurity(userSecurity);

            if (DEBUG)
            {
                DEBUGGER.debug("AccountResetRequest: {}", resRequest);
            }

            AccountResetResponse response = processor.verifyUserSecurityConfig(resRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("AccountResetResponse: {}", response);
            }

            if (response.getRequestStatus() == SecurityRequestStatus.SUCCESS)
            {
                // ok, good - the user successfully passed this validation
                // kick off the reset workflow
                AccountResetRequest resetReq = new AccountResetRequest();
                resetReq.setHostInfo(reqInfo);
                resetReq.setUserAccount(userAccount);
                resetReq.setApplicationId(this.appConfig.getApplicationId());
                resetReq.setApplicationName(this.appConfig.getApplicationName());

                if (DEBUG)
                {
                    DEBUGGER.debug("AccountResetRequest: {}", resetReq);
                }

                AccountResetResponse resetRes = processor.resetUserPassword(resetReq);

                if (DEBUG)
                {
                    DEBUGGER.debug("AccountResetResponse: {}", resetRes);
                }

                if (resetRes.getRequestStatus() == SecurityRequestStatus.SUCCESS)
                {
                    // good, send email
                    UserAccount responseAccount = resetRes.getUserAccount();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("UserAccount: {}", responseAccount);
                    }

                    String emailId = RandomStringUtils.randomAlphanumeric(16);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Message ID: {}", emailId);
                    }

                    StringBuilder targetURL = new StringBuilder()
                        .append(hRequest.getScheme() + "://" + hRequest.getServerName())
                        .append((hRequest.getServerPort() == 443) ? null : ":" + hRequest.getServerPort())
                        .append(hRequest.getContextPath() + this.resetURL + resetRes.getResetId());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("targetURL: {}", targetURL);
                    }
                        
                    try
                    {
                    	SimpleMailMessage emailMessage = new SimpleMailMessage();
                    	emailMessage.setTo(this.forgotPasswordEmail.getTo());
                    	emailMessage.setSubject(this.forgotPasswordEmail.getSubject());
                    	emailMessage.setFrom(this.appConfig.getEmailAddress());
                    	emailMessage.setText(String.format(this.forgotPasswordEmail.getText(),
                    			userAccount.getGivenName(),
                                new Date(System.currentTimeMillis()),
                                reqInfo.getHostName(),
                                targetURL.toString(),
                                8, 128));

                    	if (DEBUG)
                    	{
                    		DEBUGGER.debug("SimpleMailMessage: {}", emailMessage);
                    	}

                    	mailSender.send(emailMessage);
                    }
                    catch (final MailException mx)
                    {
                        ERROR_RECORDER.error(mx.getMessage(), mx);

                        model.addAttribute(Constants.ERROR_MESSAGE, this.appConfig.getMessageEmailSendFailed());
                    }

                    if (this.appConfig.getIsSmsEnabled())
                    {
                        // send an sms code
                        try
                        {
                        	SimpleMailMessage emailMessage = new SimpleMailMessage();
                        	emailMessage.setTo(userAccount.getPagerNumber());
                        	emailMessage.setText(resetRes.getSmsCode());

                        	if (DEBUG)
                        	{
                        		DEBUGGER.debug("SimpleMailMessage: {}", emailMessage);
                        	}

                        	mailSender.send(emailMessage);
                        }
                        catch (final MailException mx)
                        {
                            ERROR_RECORDER.error(mx.getMessage(), mx);

                            model.addAttribute(Constants.ERROR_MESSAGE, this.appConfig.getMessageEmailSendFailed());
                        }
                    }
                }
                else
                {
                    // some failure occurred
                    return this.appConfig.getErrorResponsePage();
                }
            }
            else
            {
                // user not logged in, redirect
                request.setCount(response.getCount());

                if (DEBUG)
                {
                    DEBUGGER.debug("UserChangeRequest: {}", request);
                }

                resetError = true;
                model.addAttribute(Constants.ERROR_RESPONSE, this.messageRequestFailure);
                model.addAttribute(Constants.COMMAND, request);

                return this.submitAnswersPage;
            }
        }
        catch (final AccountResetException arx)
        {
            ERROR_RECORDER.error(arx.getMessage(), arx);

            return this.appConfig.getErrorResponsePage();
        }
        finally
        {
            if (!(resetError))
            {
                // invalidate the session at this point
                hSession.removeAttribute(Constants.USER_ACCOUNT);
                hSession.invalidate();

                hRequest.getSession().removeAttribute(Constants.USER_ACCOUNT);
                hRequest.getSession().invalidate();

                model.addAttribute(Constants.RESPONSE_MESSAGE, this.messageRequestComplete);

                return this.appConfig.getLogonRedirect();
            }
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", model);
        }

        return this.appConfig.getLogonRedirect();
    }
}
