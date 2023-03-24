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
import java.util.Objects;
import java.util.Enumeration;
import org.springframework.ui.Model;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import javax.servlet.http.HttpServletRequest;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.validation.BindingResult;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.cws.esolutions.web.Constants;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.web.ApplicationServiceBean;
import com.cws.esolutions.web.model.UserChangeRequest;
import com.cws.esolutions.web.validators.OnlineResetValidator;
import com.cws.esolutions.web.validators.PasswordValidator;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.security.processors.dto.AccountChangeData;
import com.cws.esolutions.security.processors.enums.ResetRequestType;
import com.cws.esolutions.security.processors.dto.AuthenticationData;
import com.cws.esolutions.security.processors.dto.AccountResetRequest;
import com.cws.esolutions.security.processors.dto.AccountResetResponse;
import com.cws.esolutions.security.processors.dto.AccountChangeRequest;
import com.cws.esolutions.security.processors.dto.AccountChangeResponse;
import com.cws.esolutions.security.processors.impl.AccountResetProcessorImpl;
import com.cws.esolutions.security.processors.impl.AccountChangeProcessorImpl;
import com.cws.esolutions.security.processors.exception.AccountResetException;
import com.cws.esolutions.security.processors.exception.AccountChangeException;
import com.cws.esolutions.security.processors.interfaces.IAccountResetProcessor;
import com.cws.esolutions.security.processors.interfaces.IAccountChangeProcessor;
/**
 * @author cws-khuntly
 * @version 1.0
 * @see org.springframework.stereotype.Controller
 */
@Controller
@RequestMapping("online-reset")
public class OnlineResetController
{
    private String resetURL = null;
    private String submitAnswersPage = null;
    private JavaMailSender mailSender = null;
    private String submitUsernamePage = null;
    private String submitEmailAddrPage = null;
    private String messageRequestFailure = null;
    private String messageNoAccountFound = null;
    private String submitNewPasswordPage = null;
    private String messageAccountDisabled = null;
    private String messageRequestComplete = null;
    private OnlineResetValidator validator = null;
    private ApplicationServiceBean appConfig = null;
    private PasswordValidator passwordValidator = null;

    private SimpleMailMessage forgotUsernameEmail = null;
    private SimpleMailMessage forgotPasswordEmail = null;

    private static final String CNAME = OnlineResetController.class.getName();

    private static final Logger DEBUGGER = LogManager.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LogManager.getLogger(Constants.ERROR_LOGGER + CNAME);

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

    public final void setMessageNoAccountFound(final String value)
    {
        final String methodName = OnlineResetController.CNAME + "#setMessageNoAccountFound(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageNoAccountFound = value;
    }

    public final void setMessageAccountDisabled(final String value)
    {
        final String methodName = OnlineResetController.CNAME + "#setMessageAccountDisabled(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageAccountDisabled = value;
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

    public final void setSubmitNewPasswordPage(final String value)
    {
        final String methodName = OnlineResetController.CNAME + "#setSubmitNewPasswordPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.submitNewPasswordPage = value;
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

    public final void setPasswordValidator(final PasswordValidator value)
    {
        final String methodName = OnlineResetController.CNAME + "#setPasswordValidator(final OnlineResetValidator value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.passwordValidator = value;
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

    @RequestMapping(value = "forgot-username", method = RequestMethod.GET)
    public final String showForgotUsername(final Model model)
    {
        final String methodName = OnlineResetController.CNAME + "#showForgotUsername()";

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

        model.addAttribute("resetType", ResetRequestType.USERNAME);
        model.addAttribute(Constants.COMMAND, new AccountChangeData());

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", model);
        }

        return this.submitEmailAddrPage;
    }

    @RequestMapping(value = "forgot-password", method = RequestMethod.GET)
    public final String showForgottenPassword(final Model model)
    {
        final String methodName = OnlineResetController.CNAME + "#showForgottenPassword()";

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

        model.addAttribute("resetType", ResetRequestType.PASSWORD);
        model.addAttribute(Constants.COMMAND, new AccountChangeData());

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", model);
        }

        return this.submitUsernamePage;
    }

    @RequestMapping(value = "forgot-password/{resetId}", method = RequestMethod.GET)
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
        final IAccountResetProcessor processor = (IAccountResetProcessor) new AccountResetProcessorImpl();

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
            resetReq.setResetRequestId(resetId);

            if (DEBUG)
            {
            	DEBUGGER.debug("AccountResetRequest: {}", resetReq);
            }

            AccountResetResponse resetRes = processor.verifyResetRequest(resetReq);

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
                	model.addAttribute("resetGuid", resetRes.getUserAccount().getGuid());
                	model.addAttribute("resetUsername", resetRes.getUserAccount().getUsername());
                	model.addAttribute(Constants.COMMAND, new UserChangeRequest());

                    return this.submitNewPasswordPage;
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

    @RequestMapping(value = "cancel", method = RequestMethod.GET)
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

    @RequestMapping(value = "forgot-username", method = RequestMethod.POST)
    public final String submitForgottenUsername(@ModelAttribute("request") final AccountChangeData request, final BindingResult bindResult, final Model model)
    {
        final String methodName = OnlineResetController.CNAME + "#submitForgottenUsername(@ModelAttribute(\"UserChangeRequest\") final UserChangeRequest request, final BindingResult bindResult, final Model model)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("UserChangeRequest: {}", request);
        }

        String responsePage = null;

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final IAccountResetProcessor processor = (IAccountResetProcessor) new AccountResetProcessorImpl();

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

            AccountResetRequest resetRequest = new AccountResetRequest();
            resetRequest.setApplicationId(this.appConfig.getApplicationId());
            resetRequest.setApplicationName(this.appConfig.getApplicationName());
            resetRequest.setHostInfo(reqInfo);
            resetRequest.setSearchData(request.getEmailAddr());

            if (DEBUG)
            {
                DEBUGGER.debug("AccountResetRequest: {}", request);
            }

            AccountResetResponse response = processor.findUserAccount(resetRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("AccountResetResponse: {}", response);
            }

            switch (response.getRequestStatus())
            {
				case DISABLED:
            		model.addAttribute(Constants.ERROR_MESSAGE, this.messageAccountDisabled);

            		responsePage = this.appConfig.getLogonRedirect();

            		break;
				case FAILURE:
            		model.addAttribute(Constants.ERROR_MESSAGE, this.appConfig.getMessageNoSearchResults());

            		responsePage = this.appConfig.getErrorResponsePage();

            		break;
				case SUCCESS:
	                // this will return a single user account
					if (Objects.isNull(response.getUserAccount()))
					{
	            		model.addAttribute(Constants.ERROR_MESSAGE, this.messageNoAccountFound);

	            		responsePage = this.appConfig.getLogonRedirect();

	            		break;
					}
					else
					{
						UserAccount userAccount = response.getUserAccount();

		                if (DEBUG)
		                {
		                    DEBUGGER.debug("UserAccount: {}", userAccount);
		                }

		                try
		                {
		                	// TODO some shit here
		                	SimpleMailMessage emailMessage = this.forgotPasswordEmail;
		                	emailMessage.setTo(response.getUserAccount().getEmailAddr());
		                	emailMessage.setText(String.format(this.forgotUsernameEmail.getText(),
		                            userAccount.getGivenName(),
		                            new Date(System.currentTimeMillis()),
		                            reqInfo.getHostName(),
		                            userAccount.getUsername()));

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

	                model.addAttribute(Constants.MESSAGE_RESPONSE, this.messageRequestComplete);

	                responsePage = this.appConfig.getLogonRedirect();

	                break;
				case UNAUTHORIZED:
					responsePage = this.appConfig.getUnauthorizedPage();

					break;
            }
        }
        catch (final AccountResetException arx)
        {
            ERROR_RECORDER.error(arx.getMessage(), arx);

            responsePage = this.appConfig.getErrorResponsePage();
        }

        return responsePage;
    }

    @RequestMapping(value = "forgot-password", method = RequestMethod.POST)
    public final String submitUsername(@ModelAttribute("request") final AccountChangeData request, final BindingResult bindResult, final Model model)
    {
        final String methodName = OnlineResetController.CNAME + "#submitUsername(@ModelAttribute(\"UserChangeRequest\") final UserChangeRequest request, final BindingResult bindResult, final Model model)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AccountChangeData: {}", request);
        }

        String responsePage = null;

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final IAccountResetProcessor processor = (IAccountResetProcessor) new AccountResetProcessorImpl();

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

            AccountResetRequest resetRequest = new AccountResetRequest();
            resetRequest.setApplicationId(this.appConfig.getApplicationId());
            resetRequest.setApplicationName(this.appConfig.getApplicationName());
            resetRequest.setHostInfo(reqInfo);
            resetRequest.setSearchData(request.getUsername());

            if (DEBUG)
            {
                DEBUGGER.debug("AccountResetRequest: {}", request);
            }

            AccountResetResponse response = processor.findUserAccount(resetRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("AccountResetResponse: {}", response);
            }

            switch (response.getRequestStatus())
            {
				case DISABLED:
            		model.addAttribute(Constants.ERROR_MESSAGE, this.messageAccountDisabled);

            		responsePage = this.appConfig.getLogonRedirect();

            		break;
				case FAILURE:
            		model.addAttribute(Constants.ERROR_MESSAGE, this.appConfig.getMessageNoSearchResults());

            		return this.appConfig.getErrorResponsePage();
				case SUCCESS:
	                // this will return a single user account
					if (Objects.isNull(response.getUserAccount()))
					{
	            		model.addAttribute(Constants.ERROR_MESSAGE, this.messageNoAccountFound);

	            		responsePage = this.appConfig.getLogonRedirect();

	            		break;
					}
					else
					{
			            AccountResetRequest resetReq = new AccountResetRequest();
			            resetReq.setApplicationId(this.appConfig.getApplicationId());
			            resetReq.setApplicationName(this.appConfig.getApplicationName());
			            resetReq.setHostInfo(reqInfo);
			            resetReq.setUserAccount(response.getUserAccount());

			            if (DEBUG)
			            {
			                DEBUGGER.debug("AccountResetRequest: {}", resetReq);
			            }

			            AccountResetResponse resetResponse = processor.obtainUserSecurityConfig(resetReq);

			            if (DEBUG)
			            {
			                DEBUGGER.debug("AccountResetResponse: {}", resetResponse);
			            }

			            switch (resetResponse.getRequestStatus())
			            {
							case DISABLED:
								model.addAttribute(Constants.ERROR_MESSAGE, this.messageAccountDisabled);
								responsePage = this.appConfig.getErrorResponsePage();

								break;
							case FAILURE:
								model.addAttribute(Constants.ERROR_MESSAGE, this.messageRequestFailure);
				            	responsePage = this.appConfig.getErrorResponsePage();

								break;
							case SUCCESS:
				                UserAccount resAccount = resetResponse.getUserAccount();

				                if (DEBUG)
				                {
				                	DEBUGGER.debug("UserAccount resAccount: {}", resAccount);
				                }
	
				                if ((resAccount.isSuspended()) || (resAccount.isOlrLocked()))
				                {
				                    return this.appConfig.getUnauthorizedPage();
				                }

				                AuthenticationData secResponse = resetResponse.getUserSecurity();

				                AccountChangeData changeReq = new AccountChangeData();
				                changeReq.setSecQuestionOne(secResponse.getSecQuestionOne());
				                changeReq.setSecQuestionTwo(secResponse.getSecQuestionTwo());
				                changeReq.setGuid(resAccount.getGuid());
				                changeReq.setUsername(resAccount.getUsername());
				                changeReq.setResetType(ResetRequestType.QUESTIONS);

				                if (DEBUG)
				                {
				                    DEBUGGER.debug("UserChangeRequest: {}", changeReq);
				                }

				                model.addAttribute(Constants.COMMAND, changeReq);
	
				                responsePage = this.submitAnswersPage;
	
								break;
							case UNAUTHORIZED:
								responsePage = this.appConfig.getUnauthorizedPage();
	
								break;
							default:
								model.addAttribute(Constants.ERROR_MESSAGE, this.appConfig.getMessageRequestProcessingFailure());
								responsePage = this.appConfig.getErrorResponsePage();
	
								break;
			            }

			            break;
					}
				case UNAUTHORIZED:
					responsePage = this.appConfig.getUnauthorizedPage();

					break;
            }
        }
        catch (final AccountResetException arx)
        {
            ERROR_RECORDER.error(arx.getMessage(), arx);

            responsePage = this.appConfig.getErrorResponsePage();
        }

        return responsePage;
    }

    @RequestMapping(value = "submit", method = RequestMethod.POST)
    public final String submitSecurityResponse(@ModelAttribute("request") final AccountChangeData request, final BindingResult bindResult, final Model model)
    {
        final String methodName = OnlineResetController.CNAME + "#submitSecurityResponse(@ModelAttribute(\"request\") final UserChangeRequest request, final BindingResult bindResult, final Model model)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AccountChangeData: {}", request);
        }

        String responsePage = null;

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final IAccountResetProcessor processor = (IAccountResetProcessor) new AccountResetProcessorImpl();

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

            UserAccount userAccount = new UserAccount();
            userAccount.setGuid(request.getGuid());
            userAccount.setUsername(request.getUsername());

            if (DEBUG)
            {
                DEBUGGER.debug("UserAccount: {}", userAccount);
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

            AccountResetResponse resResponse = processor.verifyUserSecurityConfig(resRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("AccountResetResponse: {}", resResponse);
            }

            switch (resResponse.getRequestStatus())
            {
				case DISABLED:
	                model.addAttribute(Constants.ERROR_RESPONSE, this.messageAccountDisabled);
	                model.addAttribute(Constants.COMMAND, request);

	                responsePage = this.submitAnswersPage;

	                break;
				case FAILURE:
	                model.addAttribute(Constants.ERROR_RESPONSE, this.messageRequestFailure);
	                model.addAttribute(Constants.COMMAND, request);

	                responsePage = this.submitAnswersPage;

	                break;
				case SUCCESS:
					// ok, good - the user successfully passed this validation
	                // kick off the reset workflow
	                AccountResetRequest resetReq = new AccountResetRequest();
	                resetReq.setHostInfo(reqInfo);
	                resetReq.setUserAccount(resResponse.getUserAccount());
	                resetReq.setApplicationId(this.appConfig.getApplicationId());
	                resetReq.setApplicationName(this.appConfig.getApplicationName());

	                if (DEBUG)
	                {
	                    DEBUGGER.debug("AccountResetRequest: {}", resetReq);
	                }

	                AccountResetResponse resetRes = processor.insertResetRequest(resetReq);

	                if (DEBUG)
	                {
	                    DEBUGGER.debug("AccountResetResponse: {}", resetRes);
	                }

	                switch (resetRes.getRequestStatus())
	                {
						case DISABLED:
		                	model.addAttribute(Constants.ERROR_MESSAGE, this.messageAccountDisabled);

		                	responsePage = this.submitAnswersPage;

							break;
						case FAILURE:
		                	model.addAttribute(Constants.ERROR_MESSAGE, this.messageRequestFailure);

		                	responsePage = this.submitAnswersPage;

							break;
						case SUCCESS:
		                    // good, send email
		                    UserAccount responseAccount = resetRes.getUserAccount();

		                    if (DEBUG)
		                    {
		                        DEBUGGER.debug("UserAccount: {}", responseAccount);
		                    }

		                    StringBuilder targetURL = new StringBuilder()
		                        .append(hRequest.getScheme() + "://" + hRequest.getServerName())
		                        .append(hRequest.getContextPath() + this.resetURL + resetRes.getResetId());

		                    if (DEBUG)
		                    {
		                        DEBUGGER.debug("targetURL: {}", targetURL);
		                    }
		                        
		                    try
		                    {
		                    	SimpleMailMessage emailMessage = this.forgotPasswordEmail;
		                    	emailMessage.setTo(responseAccount.getEmailAddr());
		                    	emailMessage.setText(String.format(
		                    			this.forgotPasswordEmail.getText(),
		                    				responseAccount.getGivenName(),
		                    				new Date(System.currentTimeMillis()),
		                    				reqInfo.getHostName(),
		                    				targetURL.toString()));

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

		                    model.addAttribute(Constants.MESSAGE_RESPONSE, this.messageRequestComplete);

		                    responsePage = this.appConfig.getLogonRedirect();

		                    break;
						case UNAUTHORIZED:
							responsePage = this.appConfig.getUnauthorizedPage();

							break;
						default:
		                	model.addAttribute(Constants.ERROR_MESSAGE, this.messageRequestFailure);

		                	responsePage = this.appConfig.getErrorResponsePage();
							break;
	                }

					break;
				case UNAUTHORIZED:
					responsePage = this.appConfig.getUnauthorizedPage();

					break;
				default:
                	model.addAttribute(Constants.ERROR_MESSAGE, this.messageRequestFailure);

                	responsePage = this.appConfig.getErrorResponsePage();

                	break;
            }
        }
        catch (final AccountResetException arx)
        {
            ERROR_RECORDER.error(arx.getMessage(), arx);

            responsePage = this.appConfig.getErrorResponsePage();
        }
        finally
        {
            // invalidate the session at this point
            hSession.removeAttribute(Constants.USER_ACCOUNT);
            hSession.invalidate();

            hRequest.getSession().removeAttribute(Constants.USER_ACCOUNT);
            hRequest.getSession().invalidate();
        }

        return responsePage;
    }

    @RequestMapping(value = "forgot-password/change-password", method = RequestMethod.POST)
    public final String submitPasswordChange(@ModelAttribute("request") final AccountChangeData request, final BindingResult bindResult, final Model model)
    {
        final String methodName = OnlineResetController.CNAME + "#submitSecurityResponse(@ModelAttribute(\"request\") final UserChangeRequest request, final BindingResult bindResult, final Model model)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AccountChangeData: {}", request);
        }

        String responsePage = null;

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final IAccountChangeProcessor processor = (IAccountChangeProcessor) new AccountChangeProcessorImpl();

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

        this.passwordValidator.validate(request, bindResult);

        if (bindResult.hasErrors())
        {
            // validation failed
            ERROR_RECORDER.error("Errors: {}", bindResult.getAllErrors());

            model.addAttribute(Constants.ERROR_MESSAGE, this.appConfig.getMessageValidationFailed());
            model.addAttribute(Constants.BIND_RESULT, bindResult.getAllErrors());
            model.addAttribute(Constants.COMMAND, request);

            return this.submitNewPasswordPage;
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
            reqAccount.setGuid(request.getGuid());
            reqAccount.setUsername(request.getUsername());

            if (DEBUG)
            {
                DEBUGGER.debug("UserAccount reqAccount: {}", reqAccount);
            }

            UserAccount userAccount = new UserAccount();
            userAccount.setGuid(request.getGuid());
            userAccount.setUsername(request.getUsername());

            if (DEBUG)
            {
                DEBUGGER.debug("UserAccount userAccount: {}", userAccount);
            }

            AuthenticationData userSecurity = new AuthenticationData();
            userSecurity.setNewPassword(request.getConfirmPassword());

            if (DEBUG)
            {
                DEBUGGER.debug("AuthenticationData: {}", userSecurity);
            }

            AccountChangeRequest changeReq = new AccountChangeRequest();
            changeReq.setApplicationId(this.appConfig.getApplicationId());
            changeReq.setApplicationName(this.appConfig.getApplicationName());
            changeReq.setHostInfo(reqInfo);
            changeReq.setIsReset(true);
            changeReq.setUserAccount(userAccount);
            changeReq.setUserSecurity(userSecurity);
            changeReq.setRequestor(reqAccount);

            if (DEBUG)
            {
                DEBUGGER.debug("AccountChangeRequest: {}", changeReq);
            }

            AccountChangeResponse resResponse = processor.changeUserPassword(changeReq);

            if (DEBUG)
            {
                DEBUGGER.debug("AccountChangeResponse: {}", resResponse);
            }

            switch (resResponse.getRequestStatus())
            {
                case DISABLED:
                    model.addAttribute(Constants.ERROR_RESPONSE, this.messageAccountDisabled);
                    model.addAttribute(Constants.COMMAND, request);

                    responsePage = this.submitAnswersPage;

                    break;
                case FAILURE:
                    model.addAttribute(Constants.ERROR_RESPONSE, this.messageRequestFailure);
                    model.addAttribute(Constants.COMMAND, request);

                    responsePage = this.submitAnswersPage;

                    break;
                case SUCCESS:
                    // at this point the user password has been changed and we can complete a login
                	UserAccount account = resResponse.getUserAccount();

                	if (DEBUG)
                	{
                		DEBUGGER.debug("UserAccount: {}", account);
                	}

                	responsePage = this.appConfig.getHomePage();

                    break;
                case UNAUTHORIZED:
                    responsePage = this.appConfig.getUnauthorizedPage();

                    break;
                default:
                    model.addAttribute(Constants.ERROR_MESSAGE, this.messageRequestFailure);

                    responsePage = this.appConfig.getErrorResponsePage();

                    break;
            }
        }
        catch (final AccountChangeException acx)
        {
            ERROR_RECORDER.error(acx.getMessage(), acx);

            responsePage = this.appConfig.getErrorResponsePage();
        }
        finally
        {
            // invalidate the session at this point
            hSession.removeAttribute(Constants.USER_ACCOUNT);
            hSession.invalidate();

            hRequest.getSession().removeAttribute(Constants.USER_ACCOUNT);
            hRequest.getSession().invalidate();
        }

        return responsePage;
    }
}
