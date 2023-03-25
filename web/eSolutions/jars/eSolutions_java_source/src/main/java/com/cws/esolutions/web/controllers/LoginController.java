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
import java.util.Objects;
import java.util.Enumeration;
import org.springframework.ui.Model;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.commons.lang3.StringUtils;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
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

    private static final Logger DEBUGGER = LogManager.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LogManager.getLogger(Constants.ERROR_LOGGER + CNAME);

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

    @RequestMapping(value = {"default", "login", "submit"}, method = RequestMethod.GET)
    public final ModelAndView showDefaultPage(final Model model)
    {
    	final String methodName = LoginController.CNAME + "#showLoginPage(final Model model)";

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
                    	break;
                    case EXPIRED:
                        hSession.invalidate();

                        mView.addObject(Constants.RESPONSE_MESSAGE, this.appConfig.getMessagePasswordExpired());

                        break;
                    default:
                        hSession.invalidate();

                        break;
                }
            }
        }

        if (!(Objects.isNull(hSession.getAttribute(Constants.USER_ACCOUNT))))
        {
        	mView.setViewName(this.appConfig.getHomePage());
        }
        else
        {
        	mView.addObject(this.allowUserReset);
        	mView.addObject(Constants.COMMAND, new LoginRequest());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "logout", method = RequestMethod.GET)
    public final ModelAndView performLogout(final Model model)
    {
        final String methodName = LoginController.CNAME + "#performLogout(final Model model)";

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

        hSession.removeAttribute(Constants.USER_ACCOUNT);
        hSession.invalidate();

        mView.addObject(Constants.ALLOW_RESET, this.allowUserReset);
        mView.addObject(Constants.RESPONSE_MESSAGE, this.logoffCompleteString);
        mView.addObject(Constants.COMMAND, new LoginRequest());
        mView.setViewName(this.loginPage);

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    // combined logon
    @RequestMapping(value = "submit", method = RequestMethod.POST)
    public final ModelAndView doCombinedLogin(@ModelAttribute("LoginRequest") final LoginRequest loginRequest, final BindingResult bindResult, final Model model, final RedirectAttributes redirectAttributes)
    {
        final String methodName = LoginController.CNAME + "#doCombinedLogin(@ModelAttribute(\"AuthenticationData\") final LoginRequest loginRequest, final BindingResult bindResult, final Model model, final RedirectAttributes redirectAttributes)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("LoginRequest: {}", loginRequest);
        }

        ModelAndView mView = new ModelAndView();
        mView.addObject(Constants.ALLOW_RESET, this.allowUserReset);
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

            mView.setViewName(this.loginPage);
            mView.addObject(Constants.ERROR_MESSAGE, this.appConfig.getMessageValidationFailed());
            mView.addObject(Constants.BIND_RESULT, bindResult.getAllErrors());
            mView.addObject(Constants.COMMAND, new LoginRequest());
        }
        else
        {
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
	
	                    	mView.setViewName(this.loginPage);
	                        mView.addObject(Constants.ERROR_MESSAGE, this.appConfig.getMessageAccountNotAuthorized());
	                        mView.addObject(Constants.COMMAND, new LoginRequest());
	
	                        break;
	                    }
	
	                    switch (userAccount.getStatus())
	                    {
	                        case SUCCESS:
	                        	hSession.setAttribute(Constants.USER_ACCOUNT, userAccount);

	                        	mView.setViewName(this.appConfig.getHomePage());
	
	                        	break;
	                        case EXPIRED:
	                            // password expired - redirect to change password page
	                        	hSession.setAttribute(Constants.USER_ACCOUNT, userAccount);

	                        	ModelAndView redirectView = new ModelAndView();
	                        	redirectAttributes.addFlashAttribute("userAccount", userAccount);

	                        	redirectView.addObject(Constants.RESPONSE_MESSAGE, this.appConfig.getMessagePasswordExpired());
	                        	redirectView.setView(new RedirectView("/esolutions/ui/user-account/password"));

	                        	if (DEBUG)
	                        	{
	                        		DEBUGGER.debug("ModelAndView: {}", redirectView);
	                        	}

	                            return redirectView;
	                        default:
	                            // no dice (but its also an unspecified failure)
	                            ERROR_RECORDER.error("An unspecified error occurred during authentication.");

	                            mView.addObject(Constants.ERROR_MESSAGE, this.appConfig.getMessageRequestProcessingFailure());
	                            mView.addObject(Constants.COMMAND, new LoginRequest());
	                            mView.setViewName(this.loginPage);
	
	                            break;
	                    }

	                    break;
	            	case FAILURE:
	            		hSession.invalidate();
	
	                    ERROR_RECORDER.error("An error occurred while processing the logon request.");
	
	                    mView.addObject(Constants.ERROR_MESSAGE, this.appConfig.getMessageRequestProcessingFailure());
	                    mView.addObject(Constants.COMMAND, new LoginRequest());
	                    mView.setViewName(this.loginPage);
	
	                    break;
	            	case UNAUTHORIZED:
	            		hSession.invalidate();
	
	                    ERROR_RECORDER.error("The requested account is unauthorized for this application.");
	
	                    mView.addObject(Constants.ERROR_MESSAGE, this.appConfig.getMessageAccountNotAuthorized());
	                    mView.addObject(Constants.COMMAND, new LoginRequest());
	                    mView.setViewName(this.loginPage);
	
	                    break;
	            	case DISABLED:
	            		hSession.invalidate();
	
	                    ERROR_RECORDER.error("The requested account is is disabled.");
	
	                    mView.addObject(Constants.ERROR_MESSAGE, this.appConfig.getMessageAccountNotAuthorized());
	                    mView.addObject(Constants.COMMAND, new LoginRequest());
	                    mView.setViewName(this.loginPage);
	
	                    break;
	            	default:
	            		hSession.invalidate();
	
	            		ERROR_RECORDER.error("An unknown error occurred while authenticating.");
	
	                	mView.addObject(Constants.ERROR_MESSAGE, this.appConfig.getMessageRequestProcessingFailure());
	                	mView.addObject(Constants.COMMAND, new LoginRequest());
	                	mView.setViewName(this.loginPage);
	                	
	                	break;
	            }
	        }
	        catch (final AuthenticationException ax)
	        {
	        	hSession.invalidate();
	
	            ERROR_RECORDER.error(ax.getMessage(), ax);
	
	            mView.addObject(Constants.COMMAND, new LoginRequest());
	            mView.addObject(Constants.ERROR_MESSAGE, this.appConfig.getMessageRequestProcessingFailure());
	            mView.setViewName(this.loginPage);
	        }
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    // otp logon
    @RequestMapping(value = "otp", method = RequestMethod.POST)
    public final ModelAndView submitOtpLogin(@ModelAttribute("security") final AuthenticationData security, final Model model, final BindingResult bindResult)
    {
        final String methodName = LoginController.CNAME + "#submitOtpLogin(@ModelAttribute(\"security\") final AuthenticationData security, final Model model, final BindingResult bindResult)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AuthenticationData: {}", security);
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

                        mView.addObject(Constants.ERROR_MESSAGE, this.appConfig.getMessageAccountNotAuthorized());
                        mView.addObject(Constants.COMMAND, new LoginRequest());

                        mView.setViewName(this.loginPage);

                        break;
                    }

                    switch (userAccount.getStatus())
                    {
                        case SUCCESS:
                        	hSession.setAttribute(Constants.USER_ACCOUNT, userAccount);

                            if (StringUtils.isNotBlank(hRequest.getParameter("vpath")))
                            {
                            	return new ModelAndView((String) hRequest.getAttribute("vpath"));
                            }
                            else
                            {
                            	mView.setViewName(this.appConfig.getHomePage());
                            }

                            break;
                        case EXPIRED:
                            // password expired - redirect to change password page
                            hSession.invalidate();

                            mView.addObject(Constants.RESPONSE_MESSAGE, this.appConfig.getMessagePasswordExpired());
                            mView.addObject(Constants.COMMAND, new LoginRequest());
                            mView.setViewName(this.loginPage);

                            break;
                        default:
                            // no dice (but its also an unspecified failure)
                            ERROR_RECORDER.error("An unspecified error occurred during authentication.");

                            mView.addObject(Constants.ERROR_MESSAGE, this.appConfig.getMessageRequestProcessingFailure());
                            mView.addObject(Constants.COMMAND, new LoginRequest());
                            mView.setViewName(this.otpLoginPage);

                            break;
                    }
            	case FAILURE:
            		hSession.invalidate();

                    ERROR_RECORDER.error("An error occurred while processing the logon request.");

                    mView.addObject(Constants.ERROR_MESSAGE, this.appConfig.getMessageRequestProcessingFailure());
                    mView.addObject(Constants.COMMAND, new LoginRequest());
                    mView.setViewName(this.loginPage);

                    break;
            	case UNAUTHORIZED:
            		hSession.invalidate();

                    ERROR_RECORDER.error("The requested account is unauthorized for this application.");

                    mView.addObject(Constants.ERROR_MESSAGE, this.appConfig.getMessageAccountNotAuthorized());
                    mView.addObject(Constants.COMMAND, new LoginRequest());
                    mView.setViewName(this.loginPage);

                    break;
            	case DISABLED:
            		hSession.invalidate();

                    ERROR_RECORDER.error("The requested account is is disabled.");

                    mView.addObject(Constants.ERROR_MESSAGE, this.appConfig.getMessageAccountNotAuthorized());
                    mView.addObject(Constants.COMMAND, new LoginRequest());

                    mView.setViewName(this.loginPage);

                    break;
            	default:
            		hSession.invalidate();

            		ERROR_RECORDER.error("An unknown error occurred while authenticating.");

            		mView.addObject(Constants.ERROR_MESSAGE, this.appConfig.getMessageRequestProcessingFailure());
            		mView.addObject(Constants.COMMAND, new LoginRequest());
                	mView.setViewName(this.loginPage);

                	break;
            }
        }
        catch (final AuthenticationException ax)
        {
        	hSession.invalidate();

            ERROR_RECORDER.error(ax.getMessage(), ax);

            mView.addObject(Constants.COMMAND, new LoginRequest());
            mView.addObject(Constants.ERROR_MESSAGE, this.appConfig.getMessageRequestProcessingFailure());
            mView.setViewName(this.loginPage);
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }
}
