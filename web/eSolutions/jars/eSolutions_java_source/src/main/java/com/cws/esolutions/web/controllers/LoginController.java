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
import org.slf4j.Logger;
import java.util.Enumeration;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.cws.esolutions.web.Constants;
import com.cws.esolutions.web.model.LoginRequest;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.web.ApplicationServiceBean;
import com.cws.esolutions.web.validators.LoginValidator;
import com.cws.esolutions.security.enums.SecurityUserRole;
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
@RequestMapping("auth")
public class LoginController
{
    private String loginPage = null;
    private String otpLoginPage = null;
    private boolean allowUserReset = true;
    private LoginValidator validator = null;
    private String logoffCompleteString = null;
    private ApplicationServiceBean appConfig = null;

    private static final String CNAME = LoginController.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CNAME);

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

    @RequestMapping(value = {"default", "login"}, method = RequestMethod.GET)
    public final String showDefaultPage(final Model model)
    {
    	final String methodName = LoginController.CNAME + "#showLoginPage(final Model model)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        model.addAttribute(Constants.ALLOW_RESET, this.allowUserReset);

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
                    	break;
                    case EXPIRED:
                        hSession.invalidate();

                        model.addAttribute(Constants.RESPONSE_MESSAGE, this.appConfig.getMessagePasswordExpired());

                        break;
                    default:
                        hSession.invalidate();

                        break;
                }
            }
        }

        model.addAttribute(this.allowUserReset);
        model.addAttribute(Constants.COMMAND, new LoginRequest());

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", model);
        }

        return this.loginPage;
    }

    @RequestMapping(value = "logout", method = RequestMethod.GET)
    public final String performLogout(final Model model)
    {
        final String methodName = LoginController.CNAME + "#performLogout(final Model model)";

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

        model.addAttribute(Constants.RESPONSE_MESSAGE, this.logoffCompleteString);
        model.addAttribute(Constants.COMMAND, new LoginRequest());

        if (DEBUG)
        {
            DEBUGGER.debug("Model: {}", model);
        }

        return this.loginPage;
    }

    // combined logon
    @RequestMapping(value = "submit", method = RequestMethod.POST)
    public final String doCombinedLogin(@ModelAttribute("LoginRequest") final LoginRequest loginRequest, final BindingResult bindResult, final Model model, final RedirectAttributes redirectAttributes)
    {
        final String methodName = LoginController.CNAME + "#doCombinedLogin(@ModelAttribute(\"AuthenticationData\") final LoginRequest loginRequest, final BindingResult bindResult, final Model model, final RedirectAttributes redirectAttributes)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("LoginRequest: {}", loginRequest);
            DEBUGGER.debug("BindingResult: {}", bindResult);
            DEBUGGER.debug("Model: {}", model);
            DEBUGGER.debug("RedirectAttributes: {}", redirectAttributes);
        }

        model.addAttribute(Constants.ALLOW_RESET, this.allowUserReset);
        redirectAttributes.addFlashAttribute(Constants.ALLOW_RESET, this.allowUserReset);

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final IAuthenticationProcessor authProcessor = (IAuthenticationProcessor) new AuthenticationProcessorImpl();

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

            model.addAttribute(Constants.ERROR_MESSAGE, this.appConfig.getMessageValidationFailed());
            model.addAttribute(Constants.BIND_RESULT, bindResult.getAllErrors());
            model.addAttribute(Constants.COMMAND, new LoginRequest());

            return this.loginPage;
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

            switch (authResponse.getRequestStatus())
            {
            	case SUCCESS:
            		UserAccount userAccount = authResponse.getUserAccount();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("UserAccount: {}", userAccount);
                    }

                    if (userAccount.getUserRole() == SecurityUserRole.NONE)
                    {
                    	hSession.invalidate();

                        model.addAttribute(Constants.ERROR_MESSAGE, this.appConfig.getMessageAccountNotAuthorized());
                        model.addAttribute(Constants.COMMAND, new LoginRequest());

                        return this.loginPage;
                    }

                    switch (userAccount.getStatus())
                    {
                        case SUCCESS:
                        	hSession.setAttribute(Constants.USER_ACCOUNT, userAccount);

                            if (StringUtils.isNotBlank(hRequest.getParameter("vpath")))
                            {
                                return "redirect:/" + hRequest.getParameter("vpath");
                            }
                            else
                            {
                                return this.appConfig.getHomePage();
                            }
                        case EXPIRED:
                            // password expired - redirect to change password page
                            hSession.invalidate();

                            model.addAttribute(Constants.RESPONSE_MESSAGE, this.appConfig.getMessagePasswordExpired());
                            model.addAttribute(Constants.COMMAND, new LoginRequest());

                            return this.loginPage;
                        default:
                            // no dice (but its also an unspecified failure)
                            ERROR_RECORDER.error("An unspecified error occurred during authentication.");

                            model.addAttribute(Constants.ERROR_MESSAGE, this.appConfig.getMessageRequestProcessingFailure());
                            model.addAttribute(Constants.COMMAND, new LoginRequest());

                            return this.loginPage;
                    }
            	case FAILURE:
            		hSession.invalidate();

                    ERROR_RECORDER.error("An error occurred while processing the logon request.");

                    model.addAttribute(Constants.ERROR_MESSAGE, this.appConfig.getMessageRequestProcessingFailure());
                    model.addAttribute(Constants.COMMAND, new LoginRequest());

                    return this.loginPage;
            	case UNAUTHORIZED:
            		hSession.invalidate();

                    ERROR_RECORDER.error("The requested account is unauthorized for this application.");

                    model.addAttribute(Constants.ERROR_MESSAGE, this.appConfig.getMessageAccountNotAuthorized());
                    model.addAttribute(Constants.COMMAND, new LoginRequest());

                    return this.loginPage;
            	case DISABLED:
            		hSession.invalidate();

                    ERROR_RECORDER.error("The requested account is is disabled.");

                    model.addAttribute(Constants.ERROR_MESSAGE, this.appConfig.getMessageAccountNotAuthorized());
                    model.addAttribute(Constants.COMMAND, new LoginRequest());

                    return this.loginPage;
            	default:
            		hSession.invalidate();

            		ERROR_RECORDER.error("An unknown error occurred while authenticating.");

                	model.addAttribute(Constants.ERROR_MESSAGE, this.appConfig.getMessageRequestProcessingFailure());
                	model.addAttribute(Constants.COMMAND, new LoginRequest());

                	return this.loginPage;
            }
        }
        catch (final AuthenticationException ax)
        {
        	hSession.invalidate();

            ERROR_RECORDER.error(ax.getMessage(), ax);

            model.addAttribute(Constants.COMMAND, new LoginRequest());
            model.addAttribute(Constants.ERROR_MESSAGE, this.appConfig.getMessageRequestProcessingFailure());

            return this.loginPage;
        }
    }

    // otp logon
    @RequestMapping(value = "otp", method = RequestMethod.POST)
    public final String submitOtpLogin(@ModelAttribute("security") final AuthenticationData security, final Model model, final BindingResult bindResult)
    {
        final String methodName = LoginController.CNAME + "#submitOtpLogin(@ModelAttribute(\"security\") final AuthenticationData security, final Model model, final BindingResult bindResult)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AuthenticationData: {}", security);
            DEBUGGER.debug("BindingResult: {}", bindResult);
        }

        model.addAttribute(Constants.ALLOW_RESET, this.allowUserReset);

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

            switch (authResponse.getRequestStatus())
            {
            	case SUCCESS:
            		UserAccount userAccount = authResponse.getUserAccount();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("UserAccount: {}", userAccount);
                    }

                    if (userAccount.getUserRole() == SecurityUserRole.NONE)
                    {
                    	hSession.invalidate();

                        model.addAttribute(Constants.ERROR_MESSAGE, this.appConfig.getMessageAccountNotAuthorized());
                        model.addAttribute(Constants.COMMAND, new LoginRequest());

                        return this.loginPage;
                    }

                    switch (userAccount.getStatus())
                    {
                        case SUCCESS:
                        	hSession.setAttribute(Constants.USER_ACCOUNT, userAccount);

                            if (StringUtils.isNotBlank(hRequest.getParameter("vpath")))
                            {
                                return "redirect:/" + hRequest.getParameter("vpath");
                            }
                            else
                            {
                                return this.appConfig.getHomePage();
                            }
                        case EXPIRED:
                            // password expired - redirect to change password page
                            hSession.invalidate();

                            model.addAttribute(Constants.RESPONSE_MESSAGE, this.appConfig.getMessagePasswordExpired());
                            model.addAttribute(Constants.COMMAND, new LoginRequest());

                            return this.loginPage;
                        default:
                            // no dice (but its also an unspecified failure)
                            ERROR_RECORDER.error("An unspecified error occurred during authentication.");

                            model.addAttribute(Constants.ERROR_MESSAGE, this.appConfig.getMessageRequestProcessingFailure());
                            model.addAttribute(Constants.COMMAND, new LoginRequest());

                            return this.otpLoginPage;
                    }
            	case FAILURE:
            		hSession.invalidate();

                    ERROR_RECORDER.error("An error occurred while processing the logon request.");

                    model.addAttribute(Constants.ERROR_MESSAGE, this.appConfig.getMessageRequestProcessingFailure());
                    model.addAttribute(Constants.COMMAND, new LoginRequest());

                    return this.loginPage;
            	case UNAUTHORIZED:
            		hSession.invalidate();

                    ERROR_RECORDER.error("The requested account is unauthorized for this application.");

                    model.addAttribute(Constants.ERROR_MESSAGE, this.appConfig.getMessageAccountNotAuthorized());
                    model.addAttribute(Constants.COMMAND, new LoginRequest());

                    return this.loginPage;
            	case DISABLED:
            		hSession.invalidate();

                    ERROR_RECORDER.error("The requested account is is disabled.");

                    model.addAttribute(Constants.ERROR_MESSAGE, this.appConfig.getMessageAccountNotAuthorized());
                    model.addAttribute(Constants.COMMAND, new LoginRequest());

                    return this.loginPage;
            	default:
            		hSession.invalidate();

            		ERROR_RECORDER.error("An unknown error occurred while authenticating.");

                	model.addAttribute(Constants.ERROR_MESSAGE, this.appConfig.getMessageRequestProcessingFailure());
                	model.addAttribute(Constants.COMMAND, new LoginRequest());

                	return this.loginPage;
            }
        }
        catch (final AuthenticationException ax)
        {
        	hSession.invalidate();

            ERROR_RECORDER.error(ax.getMessage(), ax);

            model.addAttribute(Constants.COMMAND, new LoginRequest());
            model.addAttribute(Constants.ERROR_MESSAGE, this.appConfig.getMessageRequestProcessingFailure());

            return this.loginPage;
        }
    }
}
