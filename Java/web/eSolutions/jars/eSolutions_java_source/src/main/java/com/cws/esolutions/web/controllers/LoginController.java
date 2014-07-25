/*
 * Copyright (c) 2009 - 2014 CaspersBox Web Services
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
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
import java.util.Enumeration;
import org.slf4j.LoggerFactory;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
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

import com.cws.esolutions.web.Constants;
import com.cws.esolutions.web.dto.LoginRequest;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.web.ApplicationServiceBean;
import com.cws.esolutions.web.validators.LoginValidator;
import com.cws.esolutions.core.processors.dto.MessagingRequest;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.core.processors.dto.MessagingResponse;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.security.processors.dto.AuthenticationData;
import com.cws.esolutions.security.processors.dto.AuthenticationRequest;
import com.cws.esolutions.security.processors.dto.AuthenticationResponse;
import com.cws.esolutions.web.processors.impl.ServiceMessagingProcessorImpl;
import com.cws.esolutions.core.processors.exception.MessagingServiceException;
import com.cws.esolutions.security.processors.impl.AuthenticationProcessorImpl;
import com.cws.esolutions.security.processors.exception.AuthenticationException;
import com.cws.esolutions.security.processors.interfaces.IAuthenticationProcessor;
/**
 * @author khuntly
 * @version 1.0
 * @see org.springframework.stereotype.Controller
 */
@Controller
@RequestMapping("/login")
public class LoginController
{
    private String loginPage = null;
    private String otpLoginPage = null;
    private boolean allowUserReset = true;
    private LoginValidator validator = null;
    private String logoffCompleteString = null;
    private String messageSubmissionFailed = null;
    private ApplicationServiceBean appConfig = null;
    private ServiceMessagingProcessorImpl messaging = null;

    private static final String CNAME = LoginController.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CNAME);

    public final void setMessaging(final ServiceMessagingProcessorImpl value)
    {
        final String methodName = LoginController.CNAME + "#setMessaging(final ServiceMessagingProcessorImpl value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messaging = value;
    }

    public final void setLoginPage(final String value)
    {
        final String methodName = LoginController.CNAME + "#setLoginPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.loginPage = value;
    }

    public final void setOtpLoginPage(final String value)
    {
        final String methodName = LoginController.CNAME + "#setOtpLoginPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.otpLoginPage = value;
    }

    public final void setLogoffCompleteString(final String value)
    {
        final String methodName = LoginController.CNAME + "#setLogoffCompleteString(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.logoffCompleteString = value;
    }

    public final void setValidator(final LoginValidator value)
    {
        final String methodName = LoginController.CNAME + "#setValidator(final LoginValidator value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.validator = value;
    }

    public final void setAppConfig(final ApplicationServiceBean value)
    {
        final String methodName = LoginController.CNAME + "#setAppConfig(final ApplicationServiceBean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.appConfig = value;
    }

    public final void setAllowUserReset(final boolean value)
    {
        final String methodName = LoginController.CNAME + "#setAllowUserReset(final boolean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.allowUserReset = value;
    }

    public final void setMessageSubmissionFailed(final String value)
    {
        final String methodName = LoginController.CNAME + "#setMessageSubmissionFailed(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageSubmissionFailed = value;
    }

    @RequestMapping(value = "/default", method = RequestMethod.GET)
    public final ModelAndView showDefaultPage()
    {
        final String methodName = LoginController.CNAME + "#showDefaultPage()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        ModelAndView mView = new ModelAndView();
        mView.addObject(Constants.ALLOW_RESET, this.allowUserReset);

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

        Enumeration<?> sessionEnumeration = hSession.getAttributeNames();

        while (sessionEnumeration.hasMoreElements())
        {
            String element = (String) sessionEnumeration.nextElement();

            if (DEBUG)
            {
                DEBUGGER.debug("element: {}", element);
            }

            Object value = hSession.getAttribute(element);

            if (DEBUG)
            {
                DEBUGGER.debug("value: {}", value);
            }

            if (value instanceof UserAccount)
            {
                UserAccount sessionAccount = (UserAccount) value;

                if (DEBUG)
                {
                    DEBUGGER.debug("UserAccount: {}", sessionAccount);
                }

                if (sessionAccount.getStatus() == null)
                {
                    hSession.invalidate();

                    break;
                }

                switch (sessionAccount.getStatus())
                {
                    case SUCCESS:
                        mView = new ModelAndView(new RedirectView());
                        mView.setViewName(this.appConfig.getHomeRedirect());

                        return mView;
                    case EXPIRED:
                        hSession.invalidate();
                        mView.addObject(Constants.RESPONSE_MESSAGE, this.appConfig.getMessagePasswordExpired());

                        return mView;
                    default:
                        hSession.invalidate();

                        break;
                }
            }
        }

        mView.setViewName(this.loginPage);
        mView.addObject("command", new LoginRequest());

        try
        {
            MessagingResponse messageResponse = this.messaging.showAlertMessages(new MessagingRequest());

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

        if (StringUtils.isNotBlank(hRequest.getParameter("vpath")))
        {
            mView.addObject("redirectPath", hRequest.getParameter("vpath"));
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public final ModelAndView performLogout()
    {
        final String methodName = LoginController.CNAME + "#performLogout()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
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

        hSession.removeAttribute(Constants.USER_ACCOUNT);
        hSession.invalidate();

        mView.addObject(Constants.RESPONSE_MESSAGE, this.logoffCompleteString);
        mView.setViewName(this.appConfig.getLogonRedirect());

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    // combined logon
    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    public final ModelAndView doCombinedLogin(@ModelAttribute("loginRequest") final LoginRequest loginRequest, final BindingResult bindResult)
    {
        final String methodName = LoginController.CNAME + "#doCombinedLogin(@ModelAttribute(\"loginRequest\") final LoginRequest loginRequest, final BindingResult bindResult)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("LoginRequest: {}", loginRequest);
            DEBUGGER.debug("BindingResult: {}", bindResult);
        }

        ModelAndView mView = new ModelAndView();
        mView.addObject(Constants.ALLOW_RESET, this.allowUserReset);

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final IAuthenticationProcessor authProcessor = new AuthenticationProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());

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

        this.validator.validate(loginRequest, bindResult);

        if (bindResult.hasErrors())
        {
            mView.addObject("errors", bindResult.getAllErrors());
            mView.addObject("command", new LoginRequest());
            mView.setViewName(this.loginPage);

            return mView;
        }

        try
        {
            // validate
            RequestHostInfo reqInfo = new RequestHostInfo();
            reqInfo.setHostAddress(hRequest.getRemoteHost());
            reqInfo.setHostName(hRequest.getRemoteAddr());

            if (DEBUG)
            {
                DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            }

            UserAccount reqUser = new UserAccount();
            reqUser.setUsername(loginRequest.getLoginUser());

            if (DEBUG)
            {
                DEBUGGER.debug("UserAccount: {}", reqUser);
            }

            AuthenticationData reqSecurity = new AuthenticationData();
            reqSecurity.setPassword(loginRequest.getLoginPass());

            if (DEBUG)
            {
                DEBUGGER.debug("AuthenticationData: {}", reqSecurity);
            }

            AuthenticationRequest authRequest = new AuthenticationRequest();
            authRequest.setHostInfo(reqInfo);
            authRequest.setUserAccount(reqUser);
            authRequest.setUserSecurity(reqSecurity);
            authRequest.setApplicationId(this.appConfig.getApplicationId());
            authRequest.setApplicationName(this.appConfig.getApplicationName());

            if (DEBUG)
            {
                DEBUGGER.debug("AuthenticationRequest: {}", authRequest);
            }

            AuthenticationResponse authResponse = authProcessor.processAgentLogon(authRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("AuthenticationResponse: {}", authResponse);
            }

            if (authResponse.getRequestStatus() == SecurityRequestStatus.SUCCESS)
            {
                UserAccount userAccount = authResponse.getUserAccount();

                if (DEBUG)
                {
                    DEBUGGER.debug("UserAccount: {}", userAccount);
                }

                switch (userAccount.getStatus())
                {
                    case SUCCESS:
                        hSession.setAttribute(Constants.USER_ACCOUNT, userAccount);

                        mView = new ModelAndView(new RedirectView());

                        if (StringUtils.isNotBlank(hRequest.getParameter("vpath")))
                        {
                            mView.setViewName("redirect:" + hRequest.getParameter("vpath"));
                        }
                        else
                        {
                            mView.setViewName(this.appConfig.getHomeRedirect());
                        }

                        if (DEBUG)
                        {
                            DEBUGGER.debug("ModelAndView: {}", mView);
                        }

                        return mView;
                    case EXPIRED:
                        // password expired - redirect to change password page
                        hSession.invalidate();

                        mView.addObject(Constants.RESPONSE_MESSAGE, this.appConfig.getMessagePasswordExpired());
                        mView.addObject("command", new LoginRequest());
                        mView.setViewName(this.loginPage);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("ModelAndView: {}", mView);
                        }

                        return mView;
                    default:
                        // no dice (but its also an unspecified failure)
                        ERROR_RECORDER.error("An unspecified error occurred during authentication.");

                        mView.addObject(Constants.ERROR_MESSAGE, this.appConfig.getMessageRequestProcessingFailure());
                        mView.addObject("command", new LoginRequest());
                        mView.setViewName(this.loginPage);

                        break;
                }
            }
            else
            {
                mView.addObject(Constants.ERROR_MESSAGE, this.appConfig.getMessageRequestProcessingFailure());
                mView.addObject("command", new LoginRequest());
                mView.setViewName(this.loginPage);
            }
        }
        catch (AuthenticationException ax)
        {
            ERROR_RECORDER.error(ax.getMessage(), ax);

            mView.addObject("command", new LoginRequest());
            mView.setViewName(this.loginPage);
            mView.addObject(Constants.ERROR_MESSAGE, this.appConfig.getMessageRequestProcessingFailure());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    // otp logon
    @RequestMapping(value = "/otp", method = RequestMethod.POST)
    public final ModelAndView submitOtpLogin(@ModelAttribute("security") final AuthenticationData security, final BindingResult bindResult)
    {
        final String methodName = LoginController.CNAME + "#submitOtpLogin(@ModelAttribute(\"security\") final AuthenticationData security, final BindingResult bindResult)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AuthenticationData: {}", security);
            DEBUGGER.debug("BindingResult: {}", bindResult);
        }

        ModelAndView mView = new ModelAndView();
        mView.addObject(Constants.ALLOW_RESET, this.allowUserReset);

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final IAuthenticationProcessor authProcessor = new AuthenticationProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());

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
            // validate
            RequestHostInfo reqInfo = new RequestHostInfo();
            reqInfo.setHostAddress(hRequest.getRemoteHost());
            reqInfo.setHostName(hRequest.getRemoteAddr());

            if (DEBUG)
            {
                DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            }

            AuthenticationRequest authRequest = new AuthenticationRequest();
            authRequest.setHostInfo(reqInfo);
            authRequest.setUserAccount((UserAccount) hRequest.getSession().getAttribute(Constants.USER_ACCOUNT));
            authRequest.setUserSecurity(security);
            authRequest.setApplicationId(this.appConfig.getApplicationId());
            authRequest.setApplicationName(this.appConfig.getApplicationName());

            if (DEBUG)
            {
                DEBUGGER.debug("AuthenticationRequest: {}", authRequest);
            }

            AuthenticationResponse authResponse = authProcessor.processAgentLogon(authRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("AuthenticationResponse: {}", authResponse);
            }

            if (authResponse.getRequestStatus() == SecurityRequestStatus.SUCCESS)
            {
                UserAccount userAccount = authResponse.getUserAccount();

                if (DEBUG)
                {
                    DEBUGGER.debug("UserAccount: {}", userAccount);
                }

                switch (userAccount.getStatus())
                {
                    case SUCCESS:
                        // username validated
                        // check logon type
                        hSession.setAttribute(Constants.USER_ACCOUNT, userAccount);

                        if (StringUtils.isNotBlank(hRequest.getParameter("vpath")))
                        {
                            mView = new ModelAndView(new RedirectView("redirect:" + hRequest.getParameter("vpath"), false));
                        }
                        else
                        {
                            mView = new ModelAndView(new RedirectView(this.appConfig.getHomeRedirect(), true));
                        }

                        return mView;
                    case EXPIRED:
                        // password expired - redirect to change password page
                        hSession.invalidate();

                        mView.addObject(Constants.RESPONSE_MESSAGE, this.appConfig.getMessagePasswordExpired());
                        mView.addObject("command", new LoginRequest());
                        mView.setViewName(this.loginPage);

                        return mView;
                    default:
                        mView.addObject("command", new AuthenticationData());
                        mView.setViewName(this.otpLoginPage);
                        mView.addObject(Constants.ERROR_RESPONSE, this.messageSubmissionFailed);

                        break;
                }
            }
            else
            {
                mView.addObject("command", new AuthenticationData());
                mView.setViewName(this.otpLoginPage);
                mView.addObject(Constants.ERROR_RESPONSE, this.messageSubmissionFailed);
            }
        }
        catch (AuthenticationException ax)
        {
            ERROR_RECORDER.error(ax.getMessage(), ax);

            mView.addObject("command", new AuthenticationData());
            mView.setViewName(this.otpLoginPage);
            mView.addObject(Constants.ERROR_RESPONSE, this.appConfig.getMessageRequestProcessingFailure());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }
}
