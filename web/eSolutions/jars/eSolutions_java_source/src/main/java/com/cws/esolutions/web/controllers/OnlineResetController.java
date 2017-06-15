/*
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
 * cws-khuntly   11/23/2008 22:39:20             Created.
 */
import java.util.Date;
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
import org.springframework.web.bind.annotation.PathVariable;
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
import com.cws.esolutions.web.validators.OnlineResetValidator;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.core.processors.dto.MessagingRequest;
import com.cws.esolutions.core.processors.dto.MessagingResponse;
import com.cws.esolutions.core.config.xml.CoreConfigurationData;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.security.processors.dto.AccountChangeData;
import com.cws.esolutions.security.processors.enums.ResetRequestType;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.security.processors.dto.AuthenticationData;
import com.cws.esolutions.security.processors.dto.AccountResetRequest;
import com.cws.esolutions.security.processors.dto.AccountResetResponse;
import com.cws.esolutions.security.processors.dto.AccountControlRequest;
import com.cws.esolutions.security.config.xml.SecurityConfigurationData;
import com.cws.esolutions.security.processors.dto.AccountControlResponse;
import com.cws.esolutions.security.processors.impl.AccountResetProcessorImpl;
import com.cws.esolutions.core.processors.exception.MessagingServiceException;
import com.cws.esolutions.core.processors.impl.ServiceMessagingProcessorImpl;
import com.cws.esolutions.core.processors.interfaces.IWebMessagingProcessor;
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
    private boolean allowUserReset = true;
    private String submitAnswersPage = null;
    private String submitUsernamePage = null;
    private String submitEmailAddrPage = null;
    private String messageRequestFailure = null;
    private String messageRequestComplete = null;
    private OnlineResetValidator validator = null;
    private ApplicationServiceBean appConfig = null;
    private CoreConfigurationData coreConfig = null;
    private SecurityConfigurationData secConfig = null;
    private SimpleMailMessage forgotUsernameEmail = null;
    private SimpleMailMessage forgotPasswordEmail = null;

    private static final String RESET_KEY_ID = "resetKey";
    private static final String CNAME = OnlineResetController.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CNAME);

    public final void setCoreConfig(final CoreConfigurationData value)
    {
        final String methodName = OnlineResetController.CNAME + "#setCoreConfig(final CoreConfigurationData value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.coreConfig = value;
    }

    public final void setSecConfig(final SecurityConfigurationData value)
    {
        final String methodName = OnlineResetController.CNAME + "#setSecConfig(final SecurityConfigurationData value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.secConfig = value;
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

    public final void setAllowUserReset(final boolean value)
    {
        final String methodName = OnlineResetController.CNAME + "#setAllowUserReset(final boolean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.allowUserReset = value;
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
    public final ModelAndView showForgotUsername()
    {
        final String methodName = OnlineResetController.CNAME + "#showForgotUsername()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        ModelAndView mView = new ModelAndView();

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
            @SuppressWarnings("unchecked") Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String element = sessionEnumeration.nextElement();
                Object value = hSession.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked") Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            @SuppressWarnings("unchecked") Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

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
                mView.addObject("alertMessages", messageResponse.getSvcMessages());
            }
        }
        catch (MessagingServiceException msx)
        {
            // don't do anything with it
        }

        mView.addObject("resetType", ResetRequestType.USERNAME);
        mView.addObject(Constants.COMMAND, new AccountChangeData());
        mView.setViewName(this.submitEmailAddrPage);

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/forgot-password", method = RequestMethod.GET)
    public final ModelAndView showForgottenPassword()
    {
        final String methodName = OnlineResetController.CNAME + "#showForgottenPassword()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        ModelAndView mView = new ModelAndView();
        mView.addObject(Constants.ALLOW_RESET, this.allowUserReset);

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
            @SuppressWarnings("unchecked") Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String element = sessionEnumeration.nextElement();
                Object value = hSession.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked") Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            @SuppressWarnings("unchecked") Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

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
                mView.addObject("alertMessages", messageResponse.getSvcMessages());
            }
        }
        catch (MessagingServiceException msx)
        {
            // don't do anything with it
        }

        mView.addObject("resetType", ResetRequestType.PASSWORD);
        mView.addObject(Constants.COMMAND, new AccountChangeData());
        mView.setViewName(this.submitUsernamePage);

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/forgot-password/{resetId}", method = RequestMethod.GET)
    public final ModelAndView showPasswordChange(@PathVariable(value = "resetId") final String resetId)
    {
        final String methodName = OnlineResetController.CNAME + "#showPasswordChange()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("resetId: {}", resetId);
        }

        ModelAndView mView = new ModelAndView(new RedirectView());

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
            @SuppressWarnings("unchecked") Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String element = sessionEnumeration.nextElement();
                Object value = hSession.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked") Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            @SuppressWarnings("unchecked") Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

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

            IAccountResetProcessor resetProcessor = new AccountResetProcessorImpl();
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

                    mView.addObject(Constants.ERROR_MESSAGE, this.appConfig.getMessageUserNotLoggedIn());
                    mView.setViewName(this.appConfig.getLogonRedirect());
                }
                else
                {
                    // add in the session id
                    hSession.setAttribute(Constants.USER_ACCOUNT, userAccount);

                    mView.addObject(OnlineResetController.RESET_KEY_ID, resetId);
                    mView.setViewName(this.appConfig.getExpiredRedirect());
                }
            }
            else
            {
                // user not logged in, redirect
                hSession.invalidate();

                mView.setViewName(this.appConfig.getLogonRedirect());
            }
        }
        catch (AccountResetException arx)
        {
            ERROR_RECORDER.error(arx.getMessage(), arx);

            mView.setViewName(this.appConfig.getErrorResponsePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/cancel", method = RequestMethod.GET)
    public final ModelAndView doCancelRequest()
    {
        final String methodName = OnlineResetController.CNAME + "#doCancelRequest()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        ModelAndView mView = new ModelAndView();

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
            @SuppressWarnings("unchecked") Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String element = sessionEnumeration.nextElement();
                Object value = hSession.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked") Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            @SuppressWarnings("unchecked") Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String element = paramsEnumeration.nextElement();
                Object value = hRequest.getParameter(element);

                DEBUGGER.debug("Parameter: {}; Value: {}", element, value);
            }
        }

        hSession.invalidate(); // clear the http session

        mView = new ModelAndView(new RedirectView());
        mView.addObject(Constants.RESPONSE_MESSAGE, this.appConfig.getMessageRequestCanceled());
        mView.setViewName(this.appConfig.getLogonRedirect());

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/forgot-username", method = RequestMethod.POST)
    public final ModelAndView submitForgottenUsername(@ModelAttribute("request") final AccountChangeData request, final BindingResult bindResult)
    {
        final String methodName = OnlineResetController.CNAME + "#submitForgottenUsername(@ModelAttribute(\"UserChangeRequest\") final UserChangeRequest request, final BindingResult bindResult)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("UserChangeRequest: {}", request);
            DEBUGGER.debug("BindingResult: {}", bindResult);
        }

        ModelAndView mView = new ModelAndView();

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
            @SuppressWarnings("unchecked") Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String element = sessionEnumeration.nextElement();
                Object value = hSession.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked") Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            @SuppressWarnings("unchecked") Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

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

            mView.addObject(Constants.ERROR_MESSAGE, this.appConfig.getMessageValidationFailed());
            mView.addObject(Constants.BIND_RESULT, bindResult.getAllErrors());
            mView.addObject(Constants.COMMAND, new AccountChangeData());
            mView.setViewName(this.submitUsernamePage);

            return mView;
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

                    EmailUtils.sendEmailMessage(this.coreConfig.getMailConfig(), message, true);
                }
                catch (MessagingException mx)
                {
                    ERROR_RECORDER.error(mx.getMessage(), mx);

                    mView.addObject(Constants.ERROR_MESSAGE, this.appConfig.getMessageEmailSendFailed());
                }

                mView = new ModelAndView(new RedirectView());
                mView.addObject(Constants.MESSAGE_RESPONSE, this.messageRequestComplete);
                mView.setViewName(this.appConfig.getLogonRedirect());
            }
            else
            {
                mView.setViewName(this.appConfig.getErrorResponsePage());
            }
        }
        catch (AccountControlException acx)
        {
            ERROR_RECORDER.error(acx.getMessage(), acx);

            mView = new ModelAndView(new RedirectView());
            mView.setViewName(this.appConfig.getErrorResponsePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/forgot-password", method = RequestMethod.POST)
    public final ModelAndView submitUsername(@ModelAttribute("request") final AccountChangeData request, final BindingResult bindResult)
    {
        final String methodName = OnlineResetController.CNAME + "#submitUsername(@ModelAttribute(\"UserChangeRequest\") final UserChangeRequest request, final BindingResult bindResult)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("UserChangeRequest: {}", request);
            DEBUGGER.debug("BindingResult: {}", bindResult);
        }

        ModelAndView mView = new ModelAndView();

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
            @SuppressWarnings("unchecked") Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String element = sessionEnumeration.nextElement();
                Object value = hSession.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked") Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            @SuppressWarnings("unchecked") Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

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

            mView.addObject(Constants.ERROR_MESSAGE, this.appConfig.getMessageValidationFailed());
            mView.addObject(Constants.BIND_RESULT, bindResult.getAllErrors());
            mView.addObject(Constants.COMMAND, new AccountChangeData());
            mView.setViewName(this.submitUsernamePage);

            return mView;
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
                    mView = new ModelAndView(new RedirectView());
                    mView.setViewName(this.appConfig.getUnauthorizedPage());

                    return mView;
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

                mView.addObject("resetType", ResetRequestType.QUESTIONS);
                mView.addObject(Constants.COMMAND, changeReq);
                mView.setViewName(this.submitAnswersPage);
            }
            else
            {
                mView.setViewName(this.appConfig.getErrorResponsePage());
            }
        }
        catch (AccountResetException arx)
        {
            ERROR_RECORDER.error(arx.getMessage(), arx);

            mView = new ModelAndView(new RedirectView());
            mView.setViewName(this.appConfig.getErrorResponsePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    public final ModelAndView submitSecurityResponse(@ModelAttribute("request") final AccountChangeData request, final BindingResult bindResult)
    {
        final String methodName = OnlineResetController.CNAME + "#submitSecurityResponse(@ModelAttribute(\"request\") final UserChangeRequest request, final BindingResult bindResult)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("UserChangeRequest: {}", request);
            DEBUGGER.debug("BindingResult: {}", bindResult);
        }

        boolean resetError = false;
        ModelAndView mView = new ModelAndView();

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
            @SuppressWarnings("unchecked") Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String element = sessionEnumeration.nextElement();
                Object value = hSession.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked") Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            @SuppressWarnings("unchecked") Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

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

            mView.addObject(Constants.ERROR_MESSAGE, this.appConfig.getMessageValidationFailed());
            mView.addObject(Constants.BIND_RESULT, bindResult.getAllErrors());
            mView.addObject(Constants.COMMAND, request);
            mView.setViewName(this.submitAnswersPage);

            return mView;
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
                        EmailMessage message = new EmailMessage();
                        message.setIsAlert(false);
                        message.setMessageSubject(this.forgotPasswordEmail.getSubject());
                        message.setMessageTo(new ArrayList<String>(
                                Arrays.asList(
                                        String.format(this.forgotPasswordEmail.getTo()[0], userAccount.getEmailAddr()))));
                        message.setEmailAddr(new ArrayList<String>(
                                Arrays.asList(
                                        String.format(this.forgotPasswordEmail.getTo()[0], this.appConfig.getEmailAddress()))));
                        message.setMessageBody(String.format(this.forgotPasswordEmail.getText(),
                            responseAccount.getGivenName(),
                            new Date(System.currentTimeMillis()),
                            reqInfo.getHostName(),
                            targetURL.toString(),
                            this.secConfig.getSecurityConfig().getPasswordMinLength(),
                            this.secConfig.getSecurityConfig().getPasswordMaxLength()));

                        if (DEBUG)
                        {
                            DEBUGGER.debug("EmailMessage: {}", message);
                        }

                        EmailUtils.sendEmailMessage(this.coreConfig.getMailConfig(), message, true);
                    }
                    catch (MessagingException mx)
                    {
                        ERROR_RECORDER.error(mx.getMessage(), mx);

                        mView.addObject(Constants.ERROR_MESSAGE, this.appConfig.getMessageEmailSendFailed());
                    }

                    if (this.secConfig.getSecurityConfig().getSmsResetEnabled())
                    {
                        // send an sms code
                        EmailMessage smsMessage = new EmailMessage();
                        smsMessage.setIsAlert(true); // set this to alert so it shows as high priority
                        smsMessage.setMessageBody(resetRes.getSmsCode());
                        smsMessage.setMessageTo(new ArrayList<String>(Arrays.asList(responseAccount.getPagerNumber())));
                        smsMessage.setEmailAddr(new ArrayList<String>(Arrays.asList(this.appConfig.getEmailAddress())));

                        if (DEBUG)
                        {
                            DEBUGGER.debug("EmailMessage: {}", smsMessage);
                        }

                        try
                        {
                            EmailUtils.sendEmailMessage(this.coreConfig.getMailConfig(), smsMessage, true);
                        }
                        catch (MessagingException mx)
                        {
                            ERROR_RECORDER.error(mx.getMessage(), mx);

                            mView.addObject(Constants.ERROR_MESSAGE, this.appConfig.getMessageEmailSendFailed());
                        }
                    }
                }
                else
                {
                    // some failure occurred
                    mView.setViewName(this.appConfig.getErrorResponsePage());
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
                mView.addObject(Constants.ERROR_RESPONSE, this.messageRequestFailure);
                mView.addObject(Constants.COMMAND, request);
                mView.setViewName(this.submitAnswersPage);
            }
        }
        catch (AccountResetException arx)
        {
            ERROR_RECORDER.error(arx.getMessage(), arx);

            mView.setViewName(this.appConfig.getErrorResponsePage());
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

                mView.addObject(Constants.RESPONSE_MESSAGE, this.messageRequestComplete);
                mView.setViewName(this.appConfig.getLogonRedirect());
            }
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }
}
