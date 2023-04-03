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
 * File: SystemCheckController.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.util.Enumeration;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.cws.esolutions.web.Constants;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.core.processors.dto.Server;
import com.cws.esolutions.web.ApplicationServiceBean;
import com.cws.esolutions.core.enums.CoreServicesStatus;
import com.cws.esolutions.web.validators.SystemCheckValidator;
import com.cws.esolutions.core.processors.dto.SystemCheckRequest;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.dto.SystemCheckResponse;
import com.cws.esolutions.core.processors.impl.SystemCheckProcessorImpl;
import com.cws.esolutions.core.processors.exception.SystemCheckException;
import com.cws.esolutions.core.processors.interfaces.ISystemCheckProcessor;
import com.cws.esolutions.core.processors.dto.ApplicationEnablementRequest;
import com.cws.esolutions.core.processors.dto.ApplicationEnablementResponse;
import com.cws.esolutions.core.processors.impl.ApplicationEnablementProcessorImpl;
import com.cws.esolutions.core.processors.exception.ApplicationEnablementException;
import com.cws.esolutions.core.processors.interfaces.IApplicationEnablementProcessor;
/**
 * @author cws-khuntly
 * @version 1.0
 * @see org.springframework.stereotype.Controller
 */
@Controller
@RequestMapping("system-check")
public class SystemCheckController
{
    private String serviceId = null;
    private String serviceName = null;
    private String netstatPage = null;
    private String remoteDatePage = null;
    private String testTelnetPage = null;
    private String listProcessesPage = null;
    private SystemCheckValidator validator = null;
    private ApplicationServiceBean appConfig = null;

    private static final String CNAME = SystemCheckController.class.getName();

    private static final Logger DEBUGGER = LogManager.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LogManager.getLogger(Constants.ERROR_LOGGER + CNAME);

    public final void setServiceId(final String value)
    {
        final String methodName = SystemCheckController.CNAME + "#setServiceId(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serviceId = value;
    }

    public final void setServiceName(final String value)
    {
        final String methodName = SystemCheckController.CNAME + "#setServiceName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serviceName = value;
    }

    public final void setValidator(final SystemCheckValidator value)
    {
        final String methodName = SystemCheckController.CNAME + "#setValidator(final SystemCheckValidator value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.validator = value;
    }

    public final void setRemoteDatePage(final String value)
    {
        final String methodName = SystemCheckController.CNAME + "#setRemoteDatePage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.remoteDatePage = value;
    }

    public final void setTestTelnetPage(final String value)
    {
        final String methodName = SystemCheckController.CNAME + "#setTestTelnetPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.testTelnetPage = value;
    }

    public final void setNetstatPage(final String value)
    {
        final String methodName = SystemCheckController.CNAME + "#setNetstatPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.netstatPage = value;
    }

    public final void setListProcessesPage(final String value)
    {
        final String methodName = SystemCheckController.CNAME + "#setListProcessesPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.listProcessesPage = value;
    }

    public final void setAppConfig(final ApplicationServiceBean value)
    {
        final String methodName = SystemCheckController.CNAME + "#setAppConfig(final ApplicationServiceBean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.appConfig = value;
    }

    @RequestMapping(value = "/remote-date/server/{guid}", method = RequestMethod.GET)
    public final ModelAndView showRemoteDate(@PathVariable("guid") final String guid)
    {
        final String methodName = SystemCheckController.CNAME + "#showRemoteDate(@PathVariable(\"guid\") final String guid)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", guid);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final ISystemCheckProcessor processor = (ISystemCheckProcessor) new SystemCheckProcessorImpl();
        final IApplicationEnablementProcessor enabler = (IApplicationEnablementProcessor) new ApplicationEnablementProcessorImpl();

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

        ApplicationEnablementRequest enableRequest = new ApplicationEnablementRequest();
        enableRequest.setApplicationId(this.appConfig.getApplicationId());
        enableRequest.setApplicationName(this.appConfig.getApplicationName());
        enableRequest.setServiceGuid(this.serviceId);
        enableRequest.setServiceName(this.serviceName);

        if (DEBUG)
        {
            DEBUGGER.debug("ApplicationEnablementRequest: {}", enableRequest);
        }

        try
        {
            ApplicationEnablementResponse enableResponse = enabler.isServiceEnabled(enableRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("ApplicationEnablementResponse: {}", enableResponse);
            }

            switch (enableResponse.getRequestStatus())
            {
                case EXCEPTION:
                    mView.setViewName(this.appConfig.getErrorResponsePage());

                    break;
                case FAILURE:
                    mView.setViewName(this.appConfig.getErrorResponsePage());

                    break;
                case SUCCESS:
                    try
                    {
                        RequestHostInfo reqInfo = new RequestHostInfo();
                        reqInfo.setHostName(hRequest.getRemoteHost());
                        reqInfo.setHostAddress(hRequest.getRemoteAddr());

                        if (DEBUG)
                        {
                            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
                        }

                        Server server = new Server();
                        server.setServerGuid(guid);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Server: {}", server);
                        }

                        // a source server is *required*
                        SystemCheckRequest request = new SystemCheckRequest();
                        request.setRequestInfo(reqInfo);
                        request.setUserAccount(userAccount);
                        request.setServiceId(this.serviceId);
                        request.setApplicationId(this.appConfig.getApplicationId());
                        request.setApplicationName(this.appConfig.getApplicationName());
                        request.setTargetServer(server);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("SystemCheckRequest: {}", request);
                        }

                        SystemCheckResponse response = processor.runRemoteDateCheck(request);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("SystemCheckResponse: {}", response);
                        }

                        if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                        {
                            mView.addObject("server", "server"); // TODO
                            mView.addObject(Constants.COMMAND, new SystemCheckRequest());
                            mView.setViewName(this.remoteDatePage);
                        }
                        else if (response.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                        {
                            mView.setViewName(this.appConfig.getUnauthorizedPage());
                        }
                        else
                        {
                            mView.setViewName(this.appConfig.getErrorResponsePage());
                        }
                    }
                    catch (final SystemCheckException scx)
                    {
                        ERROR_RECORDER.error(scx.getMessage(), scx);

                        mView.setViewName(this.appConfig.getErrorResponsePage());
                    }

                    break;
                case UNAUTHORIZED:
                    mView.setViewName(this.appConfig.getUnauthorizedPage());

                    break;
                default:
                    mView.setViewName(this.appConfig.getUnavailablePage());

                    break;
            }
        }
        catch (final ApplicationEnablementException aex)
        {
            ERROR_RECORDER.error(aex.getMessage(), aex);

            mView.setViewName(this.appConfig.getErrorResponsePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/telnet/server/{guid}", method = RequestMethod.GET)
    public final ModelAndView showTestTelnet(@PathVariable("guid") final String guid)
    {
        final String methodName = SystemCheckController.CNAME + "#showTestTelnet(@PathVariable(\"guid\") final String guid)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", guid);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IApplicationEnablementProcessor enabler = (IApplicationEnablementProcessor) new ApplicationEnablementProcessorImpl();

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

        ApplicationEnablementRequest enableRequest = new ApplicationEnablementRequest();
        enableRequest.setApplicationId(this.appConfig.getApplicationId());
        enableRequest.setApplicationName(this.appConfig.getApplicationName());
        enableRequest.setServiceGuid(this.serviceId);
        enableRequest.setServiceName(this.serviceName);

        if (DEBUG)
        {
            DEBUGGER.debug("ApplicationEnablementRequest: {}", enableRequest);
        }

        try
        {
            ApplicationEnablementResponse enableResponse = enabler.isServiceEnabled(enableRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("ApplicationEnablementResponse: {}", enableResponse);
            }

            switch (enableResponse.getRequestStatus())
            {
                case EXCEPTION:
                    mView.setViewName(this.appConfig.getErrorResponsePage());

                    break;
                case FAILURE:
                    mView.setViewName(this.appConfig.getErrorResponsePage());

                    break;
                case SUCCESS:
                    RequestHostInfo reqInfo = new RequestHostInfo();
                    reqInfo.setHostName(hRequest.getRemoteHost());
                    reqInfo.setHostAddress(hRequest.getRemoteAddr());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
                    }

                    Server server = new Server();
                    server.setServerGuid(guid);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Server: {}", server);
                    }

                    mView.addObject("server", server);
                    mView.addObject(Constants.COMMAND, new SystemCheckRequest());
                    mView.setViewName(this.testTelnetPage);

                    break;
                case UNAUTHORIZED:
                    mView.setViewName(this.appConfig.getUnauthorizedPage());

                    break;
                default:
                    mView.setViewName(this.appConfig.getUnavailablePage());

                    break;
            }
        }
        catch (final ApplicationEnablementException aex)
        {
            ERROR_RECORDER.error(aex.getMessage(), aex);

            mView.setViewName(this.appConfig.getErrorResponsePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/netstat/server/{guid}", method = RequestMethod.GET)
    public final ModelAndView showNetstat(@PathVariable("guid") final String guid)
    {
        final String methodName = SystemCheckController.CNAME + "#showNetstat(@PathVariable(\"guid\") final String guid)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", guid);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IApplicationEnablementProcessor enabler = (IApplicationEnablementProcessor) new ApplicationEnablementProcessorImpl();

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

        ApplicationEnablementRequest enableRequest = new ApplicationEnablementRequest();
        enableRequest.setApplicationId(this.appConfig.getApplicationId());
        enableRequest.setApplicationName(this.appConfig.getApplicationName());
        enableRequest.setServiceGuid(this.serviceId);
        enableRequest.setServiceName(this.serviceName);

        if (DEBUG)
        {
            DEBUGGER.debug("ApplicationEnablementRequest: {}", enableRequest);
        }

        try
        {
            ApplicationEnablementResponse enableResponse = enabler.isServiceEnabled(enableRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("ApplicationEnablementResponse: {}", enableResponse);
            }

            switch (enableResponse.getRequestStatus())
            {
                case EXCEPTION:
                    mView.setViewName(this.appConfig.getErrorResponsePage());

                    break;
                case FAILURE:
                    mView.setViewName(this.appConfig.getErrorResponsePage());

                    break;
                case SUCCESS:
                    Server server = new Server();
                    server.setServerGuid(guid);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Server: {}", server);
                    }

                    mView.addObject("server", server);
                    mView.addObject(Constants.COMMAND, new SystemCheckRequest());
                    mView.setViewName(this.netstatPage);

                    break;
                case UNAUTHORIZED:
                    mView.setViewName(this.appConfig.getUnauthorizedPage());

                    break;
                default:
                    mView.setViewName(this.appConfig.getUnavailablePage());

                    break;
            }
        }
        catch (final ApplicationEnablementException aex)
        {
            ERROR_RECORDER.error(aex.getMessage(), aex);

            mView.setViewName(this.appConfig.getErrorResponsePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/list-processes/server/{guid}", method = RequestMethod.GET)
    public final ModelAndView showListProcesses(@PathVariable("guid") final String guid)
    {
        final String methodName = SystemCheckController.CNAME + "#showListProcesses(@PathVariable(\"guid\") final String guid)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", guid);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IApplicationEnablementProcessor enabler = (IApplicationEnablementProcessor) new ApplicationEnablementProcessorImpl();

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

        ApplicationEnablementRequest enableRequest = new ApplicationEnablementRequest();
        enableRequest.setApplicationId(this.appConfig.getApplicationId());
        enableRequest.setApplicationName(this.appConfig.getApplicationName());
        enableRequest.setServiceGuid(this.serviceId);
        enableRequest.setServiceName(this.serviceName);

        if (DEBUG)
        {
            DEBUGGER.debug("ApplicationEnablementRequest: {}", enableRequest);
        }

        try
        {
            ApplicationEnablementResponse enableResponse = enabler.isServiceEnabled(enableRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("ApplicationEnablementResponse: {}", enableResponse);
            }

            switch (enableResponse.getRequestStatus())
            {
                case EXCEPTION:
                    mView.setViewName(this.appConfig.getErrorResponsePage());

                    break;
                case FAILURE:
                    mView.setViewName(this.appConfig.getErrorResponsePage());

                    break;
                case SUCCESS:
                    Server server = new Server();
                    server.setServerGuid(guid);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Server: {}", server);
                    }

                    mView.addObject("server", server);
                    mView.setViewName(this.listProcessesPage);

                    break;
                case UNAUTHORIZED:
                    mView.setViewName(this.appConfig.getUnauthorizedPage());

                    break;
                default:
                    mView.setViewName(this.appConfig.getUnavailablePage());

                    break;
            }
        }
        catch (final ApplicationEnablementException aex)
        {
            ERROR_RECORDER.error(aex.getMessage(), aex);

            mView.setViewName(this.appConfig.getErrorResponsePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/telnet", method = RequestMethod.POST)
    public final ModelAndView runTelnetTest(@ModelAttribute("request") final SystemCheckRequest request, final BindingResult bindResult)
    {
        final String methodName = SystemCheckController.CNAME + "#runTelnetTest(@ModelAttribute(\"request\") final SystemCheckRequest request, final BindingResult bindResult)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("SystemCheckRequest: {}", request);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final ISystemCheckProcessor processor = (ISystemCheckProcessor) new SystemCheckProcessorImpl();
        final IApplicationEnablementProcessor enabler = (IApplicationEnablementProcessor) new ApplicationEnablementProcessorImpl();

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

        ApplicationEnablementRequest enableRequest = new ApplicationEnablementRequest();
        enableRequest.setApplicationId(this.appConfig.getApplicationId());
        enableRequest.setApplicationName(this.appConfig.getApplicationName());
        enableRequest.setServiceGuid(this.serviceId);
        enableRequest.setServiceName(this.serviceName);

        if (DEBUG)
        {
            DEBUGGER.debug("ApplicationEnablementRequest: {}", enableRequest);
        }

        try
        {
            ApplicationEnablementResponse enableResponse = enabler.isServiceEnabled(enableRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("ApplicationEnablementResponse: {}", enableResponse);
            }

            switch (enableResponse.getRequestStatus())
            {
                case EXCEPTION:
                    mView.setViewName(this.appConfig.getErrorResponsePage());

                    break;
                case FAILURE:
                    mView.setViewName(this.appConfig.getErrorResponsePage());

                    break;
                case SUCCESS:
                    this.validator.validate(request, bindResult);

                    if (bindResult.hasErrors())
                    {
                        // validation failed
                        ERROR_RECORDER.error("Errors: {}", bindResult.getAllErrors());

                        mView.addObject(Constants.RESPONSE_MESSAGE, this.appConfig.getMessageValidationFailed());
                        mView.addObject(Constants.BIND_RESULT, bindResult.getAllErrors());
                        mView.addObject(Constants.COMMAND, new SystemCheckRequest());
                        mView.addObject("server", request.getSourceServer());
                        mView.setViewName(this.testTelnetPage);

                        return mView;
                    }

                    RequestHostInfo reqInfo = new RequestHostInfo();
                    reqInfo.setHostName(hRequest.getRemoteHost());
                    reqInfo.setHostAddress(hRequest.getRemoteAddr());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
                    }

                    try
                    {
                        SystemCheckResponse response = processor.runTelnetCheck(request);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("SystemCheckResponse: {}", response);
                        }

                        if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                        {
                            // all set
                            mView.addObject(Constants.MESSAGE_RESPONSE, response.getResponse());
                        }
                        else if (response.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                        {
                            mView.setViewName(this.appConfig.getUnauthorizedPage());
                        }
                        else
                        {
                            mView.setViewName(this.appConfig.getErrorResponsePage());
                        }
                    }
                    catch (final SystemCheckException scx)
                    {
                        ERROR_RECORDER.error(scx.getMessage(), scx);

                        mView.setViewName(this.appConfig.getErrorResponsePage());
                    }

                    break;
                case UNAUTHORIZED:
                    mView.setViewName(this.appConfig.getUnauthorizedPage());

                    break;
                default:
                    mView.setViewName(this.appConfig.getUnavailablePage());

                    break;
            }
        }
        catch (final ApplicationEnablementException aex)
        {
            ERROR_RECORDER.error(aex.getMessage(), aex);

            mView.setViewName(this.appConfig.getErrorResponsePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/netstat", method = RequestMethod.POST)
    public final ModelAndView runNetstat(@ModelAttribute("request") final SystemCheckRequest request, final BindingResult bindResult)
    {
        final String methodName = SystemCheckController.CNAME + "#runNetstat(@ModelAttribute(\"request\") final SystemCheckRequest request, final BindingResult bindResult)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("SystemCheckRequest: {}", request);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final ISystemCheckProcessor processor = (ISystemCheckProcessor) new SystemCheckProcessorImpl();
        final IApplicationEnablementProcessor enabler = (IApplicationEnablementProcessor) new ApplicationEnablementProcessorImpl();

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

        ApplicationEnablementRequest enableRequest = new ApplicationEnablementRequest();
        enableRequest.setApplicationId(this.appConfig.getApplicationId());
        enableRequest.setApplicationName(this.appConfig.getApplicationName());
        enableRequest.setServiceGuid(this.serviceId);
        enableRequest.setServiceName(this.serviceName);

        if (DEBUG)
        {
            DEBUGGER.debug("ApplicationEnablementRequest: {}", enableRequest);
        }

        try
        {
            ApplicationEnablementResponse enableResponse = enabler.isServiceEnabled(enableRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("ApplicationEnablementResponse: {}", enableResponse);
            }

            switch (enableResponse.getRequestStatus())
            {
                case EXCEPTION:
                    mView.setViewName(this.appConfig.getErrorResponsePage());

                    break;
                case FAILURE:
                    mView.setViewName(this.appConfig.getErrorResponsePage());

                    break;
                case SUCCESS:
                    this.validator.validate(request, bindResult);
            
                    if (bindResult.hasErrors())
                    {
                        // validation failed
                        ERROR_RECORDER.error("Errors: {}", bindResult.getAllErrors());
            
                        mView.addObject(Constants.RESPONSE_MESSAGE, this.appConfig.getMessageValidationFailed());
                        mView.addObject(Constants.BIND_RESULT, bindResult.getAllErrors());
                        mView.addObject(Constants.COMMAND, new SystemCheckRequest());
                        mView.addObject("server", request.getSourceServer());
                        mView.setViewName(this.netstatPage);
            
                        return mView;
                    }
            
                    RequestHostInfo reqInfo = new RequestHostInfo();
                    reqInfo.setHostName(hRequest.getRemoteHost());
                    reqInfo.setHostAddress(hRequest.getRemoteAddr());
            
                    if (DEBUG)
                    {
                        DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
                    }
            
                    try
                    {
                        SystemCheckResponse response = processor.runNetstatCheck(request);
            
                        if (DEBUG)
                        {
                            DEBUGGER.debug("SystemCheckResponse: {}", response);
                        }
            
                        if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                        {
                            // all set
                            mView.addObject(Constants.MESSAGE_RESPONSE, response.getResponse());
                        }
                        else if (response.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                        {
                            mView.setViewName(this.appConfig.getUnauthorizedPage());
                        }
                        else if (response.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                        {
                            mView.setViewName(this.appConfig.getUnauthorizedPage());
                        }
                        else
                        {
                            mView.setViewName(this.appConfig.getErrorResponsePage());
                        }
                    }
                    catch (final SystemCheckException scx)
                    {
                        ERROR_RECORDER.error(scx.getMessage(), scx);
            
                        mView.setViewName(this.appConfig.getErrorResponsePage());
                    }

                    break;
                case UNAUTHORIZED:
                    mView.setViewName(this.appConfig.getUnauthorizedPage());

                    break;
                default:
                    mView.setViewName(this.appConfig.getUnavailablePage());

                    break;
            }
        }
        catch (final ApplicationEnablementException aex)
        {
            ERROR_RECORDER.error(aex.getMessage(), aex);

            mView.setViewName(this.appConfig.getErrorResponsePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/list-processes", method = RequestMethod.POST)
    public final ModelAndView runProcessList(@ModelAttribute("request") final SystemCheckRequest request, final BindingResult bindResult)
    {
        final String methodName = SystemCheckController.CNAME + "#runProcessList(@ModelAttribute(\"request\") final SystemCheckRequest request, final BindingResult bindResult)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("SystemCheckRequest: {}", request);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final ISystemCheckProcessor processor = (ISystemCheckProcessor) new SystemCheckProcessorImpl();
        final IApplicationEnablementProcessor enabler = (IApplicationEnablementProcessor) new ApplicationEnablementProcessorImpl();

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

        ApplicationEnablementRequest enableRequest = new ApplicationEnablementRequest();
        enableRequest.setApplicationId(this.appConfig.getApplicationId());
        enableRequest.setApplicationName(this.appConfig.getApplicationName());
        enableRequest.setServiceGuid(this.serviceId);
        enableRequest.setServiceName(this.serviceName);

        if (DEBUG)
        {
            DEBUGGER.debug("ApplicationEnablementRequest: {}", enableRequest);
        }

        try
        {
            ApplicationEnablementResponse enableResponse = enabler.isServiceEnabled(enableRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("ApplicationEnablementResponse: {}", enableResponse);
            }

            switch (enableResponse.getRequestStatus())
            {
                case EXCEPTION:
                    mView.setViewName(this.appConfig.getErrorResponsePage());

                    break;
                case FAILURE:
                    mView.setViewName(this.appConfig.getErrorResponsePage());

                    break;
                case SUCCESS:
                    this.validator.validate(request, bindResult);
            
                    if (bindResult.hasErrors())
                    {
                        // validation failed
                        ERROR_RECORDER.error("Errors: {}", bindResult.getAllErrors());
            
                        mView.addObject(Constants.RESPONSE_MESSAGE, this.appConfig.getMessageValidationFailed());
                        mView.addObject(Constants.BIND_RESULT, bindResult.getAllErrors());
                        mView.addObject(Constants.COMMAND, new SystemCheckRequest());
                        mView.addObject("server", request.getSourceServer());
                        mView.setViewName(this.listProcessesPage);
            
                        return mView;
                    }
            
                    RequestHostInfo reqInfo = new RequestHostInfo();
                    reqInfo.setHostName(hRequest.getRemoteHost());
                    reqInfo.setHostAddress(hRequest.getRemoteAddr());
            
                    if (DEBUG)
                    {
                        DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
                    }
            
                    try
                    {
                        SystemCheckResponse response = processor.runProcessListCheck(request);
            
                        if (DEBUG)
                        {
                            DEBUGGER.debug("SystemCheckResponse: {}", response);
                        }
            
                        if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                        {
                            // all set
                            mView.addObject("responseData", response.getResponseObject());
                            mView.addObject(Constants.MESSAGE_RESPONSE, response.getResponse());
                        }
                        else if (response.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                        {
                            mView.setViewName(this.appConfig.getUnauthorizedPage());
                        }
                        else if (response.getRequestStatus() == CoreServicesStatus.UNAUTHORIZED)
                        {
                            mView.setViewName(this.appConfig.getUnauthorizedPage());
                        }
                        else
                        {
                            mView.setViewName(this.appConfig.getErrorResponsePage());
                        }
                    }
                    catch (final SystemCheckException scx)
                    {
                        ERROR_RECORDER.error(scx.getMessage(), scx);
            
                        mView.setViewName(this.appConfig.getErrorResponsePage());
                    }

                    break;
                case UNAUTHORIZED:
                    mView.setViewName(this.appConfig.getUnauthorizedPage());

                    break;
                default:
                    mView.setViewName(this.appConfig.getUnavailablePage());

                    break;
            }
        }
        catch (final ApplicationEnablementException aex)
        {
            ERROR_RECORDER.error(aex.getMessage(), aex);

            mView.setViewName(this.appConfig.getErrorResponsePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }
}
