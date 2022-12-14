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
 * File: ServiceMessagingController.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.util.Enumeration;
import org.springframework.ui.Model;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.cws.esolutions.web.Constants;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.web.ApplicationServiceBean;
import com.cws.esolutions.core.processors.dto.ServiceMessage;
import com.cws.esolutions.core.processors.dto.MessagingRequest;
import com.cws.esolutions.core.processors.dto.MessagingResponse;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.impl.ServiceMessagingProcessorImpl;
import com.cws.esolutions.core.processors.exception.MessagingServiceException;
/**
 * @author cws-khuntly
 * @version 1.0
 * @see org.springframework.stereotype.Controller
 */
@Controller
@RequestMapping("service-messaging")
public class ServiceMessagingController
{
    private String serviceId = null;
    private String createMessageRedirect = null;
    private String addServiceMessagePage = null;
    private String viewServiceMessagesPage = null;
    private String messageSuccessfullyAdded = null;
    private ApplicationServiceBean appConfig = null;
    private String messageSuccessfullyUpdated = null;
    private ServiceMessagingProcessorImpl processor = null;

    private static final String CNAME = ServiceMessagingController.class.getName();

    private static final Logger DEBUGGER = LogManager.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LogManager.getLogger(Constants.ERROR_LOGGER + CNAME);

    public final void setAppConfig(final ApplicationServiceBean value)
    {
        final String methodName = ServiceMessagingController.CNAME + "#setAppConfig(final ApplicationServiceBean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.appConfig = value;
    }

    public final void setServiceId(final String value)
    {
        final String methodName = ServiceMessagingController.CNAME + "#setServiceId(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serviceId = value;
    }

    public final void setProcessor(final ServiceMessagingProcessorImpl value)
    {
        final String methodName = ServiceMessagingController.CNAME + "#setProcessor(final ServiceMessagingProcessorImpl value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.processor = value;
    }

    public final void setAddServiceMessagePage(final String value)
    {
        final String methodName = ServiceMessagingController.CNAME + "#setAddServiceMessagePage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.addServiceMessagePage = value;
    }

    public final void setViewServiceMessagesPage(final String value)
    {
        final String methodName = ServiceMessagingController.CNAME + "#setViewServiceMessagesPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.viewServiceMessagesPage = value;
    }

    public final void setCreateMessageRedirect(final String value)
    {
        final String methodName = ServiceMessagingController.CNAME + "#setCreateMessageRedirect(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.createMessageRedirect = value;
    }

    public final void setMessageSuccessfullyAdded(final String value)
    {
        final String methodName = ServiceMessagingController.CNAME + "#setMessageSuccessfullyAdded(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageSuccessfullyAdded = value;
    }

    public final void setMessageSuccessfullyUpdated(final String value)
    {
        final String methodName = ServiceMessagingController.CNAME + "#setMessageSuccessfullyUpdated(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageSuccessfullyUpdated = value;
    }

    @RequestMapping(value = "default", method = RequestMethod.GET)
    public final String showDefaultPage(final Model model)
    {
        final String methodName = ServiceMessagingController.CNAME + "#showDefaultPage(final Model model)";

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

        String responsePage = null;

        try
        {
            RequestHostInfo reqInfo = new RequestHostInfo();
            reqInfo.setHostName(hRequest.getRemoteHost());
            reqInfo.setHostAddress(hRequest.getRemoteAddr());

            if (DEBUG)
            {
                DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            }

            MessagingRequest request = new MessagingRequest();
            request.setRequestInfo(reqInfo);
            request.setServiceId(this.serviceId);
            request.setUserAccount(userAccount);
            request.setApplicationId(this.appConfig.getApplicationId());
            request.setApplicationName(this.appConfig.getApplicationName());

            if (DEBUG)
            {
                DEBUGGER.debug("MessagingRequest: {}", request);
            }

            MessagingResponse response = this.processor.showMessages(request);

            if (DEBUG)
            {
                DEBUGGER.debug("MessagingResponse: {}", response);
            }

            switch (response.getRequestStatus())
            {
            	case SUCCESS:
            		model.addAttribute("dateFormat", this.appConfig.getDateFormat());
            		model.addAttribute("messageList", response.getSvcMessages());

            		responsePage = this.viewServiceMessagesPage;

            		break;
            	case UNAUTHORIZED:
            		responsePage = this.appConfig.getUnauthorizedPage();

            		break;
            	default:
            		responsePage = this.createMessageRedirect; // try it ?

            		break;
            }

        }
        catch (final MessagingServiceException msx)
        {
            ERROR_RECORDER.error(msx.getMessage(), msx);

            return this.appConfig.getErrorResponsePage();
        }

        return responsePage;
    }

    @RequestMapping(value = "add-message", method = RequestMethod.GET)
    public final String showAddMessage(final Model model)
    {
        final String methodName = ServiceMessagingController.CNAME + "#showAddMessage(final Model model)";

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

        model.addAttribute(Constants.COMMAND, new ServiceMessage());

        return this.addServiceMessagePage;
    }

    @RequestMapping(value = "edit-message/message/{messageId}", method = RequestMethod.GET)
    public final String showEditMessage(@PathVariable(value = "messageId") final String messageId, final Model model)
    {
        final String methodName = ServiceMessagingController.CNAME + "#showEditMessage(@PathVariable(value = \"messageId\", final Model model) final String messageId)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("messageId: {}", messageId);
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

        String responsePage = null;

        try
        {
            RequestHostInfo reqInfo = new RequestHostInfo();
            reqInfo.setHostName(hRequest.getRemoteHost());
            reqInfo.setHostAddress(hRequest.getRemoteAddr());

            if (DEBUG)
            {
                DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            }

            ServiceMessage message = new ServiceMessage();
            message.setMessageId(messageId);

            if (DEBUG)
            {
                DEBUGGER.debug("ServiceMessage: {}", message);
            }

            MessagingRequest request = new MessagingRequest();
            request.setRequestInfo(reqInfo);
            request.setServiceId(this.serviceId);
            request.setUserAccount(userAccount);
            request.setSvcMessage(message);
            request.setApplicationId(this.appConfig.getApplicationId());
            request.setApplicationName(this.appConfig.getApplicationName());

            if (DEBUG)
            {
                DEBUGGER.debug("MessagingRequest: {}", request);
            }

            MessagingResponse response = this.processor.showMessage(request);

            if (DEBUG)
            {
                DEBUGGER.debug("MessagingResponse: {}", response);
            }

            switch (response.getRequestStatus())
            {
				case EXCEPTION:
					model.addAttribute(Constants.ERROR_RESPONSE, "An error occurred while performing the requested operation.");
					responsePage = this.appConfig.getErrorResponsePage();

					break;
				case FAILURE:
					model.addAttribute(Constants.ERROR_RESPONSE, "An error occurred while performing the requested operation.");
					responsePage = this.appConfig.getErrorResponsePage();

					break;
				case SUCCESS:
	                ServiceMessage responseMessage = response.getSvcMessage();

	                if (DEBUG)
	                {
	                    DEBUGGER.debug("ServiceMessage: {}", responseMessage);
	                }

	                model.addAttribute(Constants.COMMAND, responseMessage);

	                responsePage = this.addServiceMessagePage;

	                break;
				case UNAUTHORIZED:
					responsePage = this.appConfig.getUnauthorizedPage();

					break;
				default:
					break;
            }
        }
        catch (final MessagingServiceException msx)
        {
            ERROR_RECORDER.error(msx.getMessage(), msx);

            responsePage = this.appConfig.getErrorResponsePage();
        }

        return responsePage;
    }

    @RequestMapping(value = "submit-message", method = RequestMethod.POST)
    public final String doAddOrModifyServiceMessage(@ModelAttribute("message") final ServiceMessage message, final Model model, final BindingResult bindResult)
    {
        final String methodName = ServiceMessagingController.CNAME + "#doAddOrModifyServiceMessage(@ModelAttribute(\"message\") final ServiceMessage message, final Model model, final BindingResult bindResult";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ServiceMessage: {}", message);
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

        String responsePage = null;

        // validate here
        try
        {
            RequestHostInfo reqInfo = new RequestHostInfo();
            reqInfo.setHostName(hRequest.getRemoteHost());
            reqInfo.setHostAddress(hRequest.getRemoteAddr());

            if (DEBUG)
            {
                DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            }

            MessagingRequest request = new MessagingRequest();
            request.setRequestInfo(reqInfo);
            request.setServiceId(this.serviceId);
            request.setUserAccount(userAccount);
            request.setSvcMessage(message);
            request.setApplicationId(this.appConfig.getApplicationId());
            request.setApplicationName(this.appConfig.getApplicationName());

            if (DEBUG)
            {
                DEBUGGER.debug("MessagingRequest: {}", request);
            }

            MessagingResponse response = (message.getIsNewMessage()) ? this.processor.addNewMessage(request) : this.processor.updateExistingMessage(request);

            if (DEBUG)
            {
                DEBUGGER.debug("MessagingResponse: {}", response);
            }

            switch (response.getRequestStatus())
            {
				case EXCEPTION:
					responsePage = this.appConfig.getErrorResponsePage();
		
					break;
				case FAILURE:
					responsePage = this.appConfig.getErrorResponsePage();
		
					break;
				case SUCCESS:
		            if (message.getIsNewMessage())
		            {
		                model.addAttribute(Constants.RESPONSE_MESSAGE, this.messageSuccessfullyAdded);
		            }
		            else
		            {
		                model.addAttribute(Constants.RESPONSE_MESSAGE, this.messageSuccessfullyUpdated);
		            }
		
					break;
				case UNAUTHORIZED:
					responsePage = this.appConfig.getUnauthorizedPage();

					break;
				default:
					model.addAttribute(Constants.ERROR_RESPONSE, "An error occurred while performing the requested operation.");
					responsePage = this.addServiceMessagePage;

					break;
            }
        }
        catch (final MessagingServiceException msx)
        {
            ERROR_RECORDER.error(msx.getMessage(), msx);

            responsePage = this.appConfig.getErrorResponsePage();
        }

        model.addAttribute(Constants.COMMAND, new ServiceMessage());

        return responsePage;
    }
}
