/*
 * Copyright (c) 2009 - 2013 By: CWS, Inc.
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
 * File: SetupController.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly   		11/23/2008 22:39:20             Created.
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
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.web.ApplicationServiceBean;
import com.cws.esolutions.web.validators.LoginValidator;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.security.processors.dto.AuthenticationData;
import com.cws.esolutions.security.processors.dto.AuthenticationRequest;
import com.cws.esolutions.security.processors.dto.AuthenticationResponse;
import com.cws.esolutions.security.processors.impl.AuthenticationProcessorImpl;
import com.cws.esolutions.security.processors.exception.AuthenticationException;
import com.cws.esolutions.security.processors.interfaces.IAuthenticationProcessor;
/**
 * @author cws-khuntly
 * @version 1.0
 * @see org.springframework.stereotype.Controller
 */
@Controller
@RequestMapping("/setup")
public class SetupController
{
    private String setupPage = null;
    private LoginValidator validator = null;
    private ApplicationServiceBean appConfig = null;

    private static final String CNAME = SetupController.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CNAME);

    public final void setSetupPage(final String value)
    {
        final String methodName = SetupController.CNAME + "#setSetupPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.setupPage = value;
    }

    public final void setValidator(final LoginValidator value)
    {
        final String methodName = SetupController.CNAME + "#setValidator(final LoginValidator value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.validator = value;
    }

    public final void setAppConfig(final ApplicationServiceBean value)
    {
        final String methodName = SetupController.CNAME + "#setAppConfig(final ApplicationServiceBean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.appConfig = value;
    }

    @RequestMapping(value = "/default", method = RequestMethod.GET)
    public final ModelAndView showDefaultPage()
    {
        final String methodName = SetupController.CNAME + "#showDefaultPage()";

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

        mView.setViewName(this.setupPage);
        mView.addObject(Constants.COMMAND, new AuthenticationRequest());

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

    // combined logon
    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    public final ModelAndView doCompleteSetup(@ModelAttribute("AuthenticationData") final AuthenticationData loginRequest, final BindingResult bindResult)
    {
        final String methodName = SetupController.CNAME + "#doCompleteSetup(@ModelAttribute(\"AuthenticationData\") final AuthenticationData loginRequest, final BindingResult bindResult)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("LoginRequest: {}", loginRequest);
            DEBUGGER.debug("BindingResult: {}", bindResult);
        }

        ModelAndView mView = new ModelAndView();

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
            // validation failed
            ERROR_RECORDER.error("Errors: {}", bindResult.getAllErrors());

            mView.addObject(Constants.ERROR_MESSAGE, this.appConfig.getMessageValidationFailed());
            mView.addObject(Constants.BIND_RESULT, bindResult.getAllErrors());
            mView.addObject(Constants.COMMAND, new AuthenticationRequest());
            mView.setViewName(this.setupPage);

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
            reqUser.setUsername(loginRequest.getUsername());

            if (DEBUG)
            {
                DEBUGGER.debug("UserAccount: {}", reqUser);
            }

            AuthenticationData reqSecurity = new AuthenticationData();
            reqSecurity.setPassword(loginRequest.getPassword());

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

                        break;
                    case EXPIRED:
                        // password expired - redirect to change password page
                        hSession.invalidate();

                        mView.addObject(Constants.RESPONSE_MESSAGE, this.appConfig.getMessagePasswordExpired());
                        mView.addObject(Constants.COMMAND, new AuthenticationRequest());
                        mView.setViewName(this.setupPage);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("ModelAndView: {}", mView);
                        }

                        break;
                    default:
                        // no dice (but its also an unspecified failure)
                        ERROR_RECORDER.error("An unspecified error occurred during authentication.");

                        mView.addObject(Constants.ERROR_MESSAGE, this.appConfig.getMessageRequestProcessingFailure());
                        mView.addObject(Constants.COMMAND, new AuthenticationRequest());
                        mView.setViewName(this.setupPage);

                        break;
                }
            }
            else
            {
                mView.addObject(Constants.ERROR_MESSAGE, this.appConfig.getMessageRequestProcessingFailure());
                mView.addObject(Constants.COMMAND, new AuthenticationRequest());
                mView.setViewName(this.setupPage);
            }
        }
        catch (AuthenticationException ax)
        {
            ERROR_RECORDER.error(ax.getMessage(), ax);

            mView.addObject(Constants.COMMAND, new AuthenticationRequest());
            mView.setViewName(this.setupPage);
            mView.addObject(Constants.ERROR_MESSAGE, this.appConfig.getMessageRequestProcessingFailure());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }
}
