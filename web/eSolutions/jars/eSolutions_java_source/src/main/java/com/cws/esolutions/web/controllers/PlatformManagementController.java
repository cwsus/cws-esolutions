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
 * File: ServerManagementController.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.util.List;
import java.util.Enumeration;
import org.springframework.ui.Model;
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
import com.cws.esolutions.web.ApplicationServiceBean;
import com.cws.esolutions.core.processors.dto.Platform;
import com.cws.esolutions.web.validators.PlatformValidator;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.dto.PlatformManagementRequest;
import com.cws.esolutions.core.processors.dto.PlatformManagementResponse;
import com.cws.esolutions.core.processors.impl.PlatformManagementProcessorImpl;
import com.cws.esolutions.core.processors.exception.PlatformManagementException;
import com.cws.esolutions.core.processors.interfaces.IPlatformManagementProcessor;
/**
 * @author cws-khuntly
 * @version 1.0
 * @see org.springframework.stereotype.Controller
 */
@Controller
@RequestMapping("platform-management")
public class PlatformManagementController
{
    private int recordsPerPage = 20; // default to 20
    private String serviceId = null;
    private String serviceName = null;
    private String defaultPage = null;
    private String addPlatformPage = null;
    private String viewPlatformPage = null;
    private String viewPlatformList = null;
    private String addPlatformRedirect = null;
    private PlatformValidator validator = null;
    private String messageNoPlatformFound = null;
    private String messagePlatformAddSuccess = null;
    private String messagePlatformAddFailure = null;
    private ApplicationServiceBean appConfig = null;

    private static final String CNAME = PlatformManagementController.class.getName();

    private static final Logger DEBUGGER = LogManager.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LogManager.getLogger(Constants.ERROR_LOGGER + CNAME);

    public final void setAppConfig(final ApplicationServiceBean value)
    {
        final String methodName = PlatformManagementController.CNAME + "#setAppConfig(final ApplicationServiceBean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.appConfig = value;
    }

    public final void setValidator(final PlatformValidator value)
    {
        final String methodName = PlatformManagementController.CNAME + "#setValidator(final PlatformValidator value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.validator = value;
    }

    public final void setServiceName(final String value)
    {
        final String methodName = PlatformManagementController.CNAME + "#setServiceName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serviceName = value;
    }

    public final void setServiceId(final String value)
    {
        final String methodName = PlatformManagementController.CNAME + "#setServiceId(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serviceId = value;
    }

    public final void setDefaultPage(final String value)
    {
        final String methodName = PlatformManagementController.CNAME + "#setDefaultPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.defaultPage = value;
    }

    public final void setAddPlatformPage(final String value)
    {
        final String methodName = PlatformManagementController.CNAME + "#setAddPlatformPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.addPlatformPage = value;
    }

    public final void setViewPlatformPage(final String value)
    {
        final String methodName = PlatformManagementController.CNAME + "#setViewPlatformPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.viewPlatformPage = value;
    }

    public final void setViewPlatformList(final String value)
    {
        final String methodName = PlatformManagementController.CNAME + "#setViewPlatformList(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.viewPlatformList = value;
    }

    public final void setMessagePlatformAddSuccess(final String value)
    {
        final String methodName = PlatformManagementController.CNAME + "#setMessagePlatformAddSuccess(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messagePlatformAddSuccess = value;
    }

    public final void setMessageNoPlatformFound(final String value)
    {
        final String methodName = PlatformManagementController.CNAME + "#setMessageNoPlatformFound(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageNoPlatformFound = value;
    }

    public final void setAddPlatformRedirect(final String value)
    {
        final String methodName = PlatformManagementController.CNAME + "#setAddPlatformRedirect(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.addPlatformRedirect = value;
    }

    public final void setMessagePlatformAddFailure(final String value)
    {
        final String methodName = PlatformManagementController.CNAME + "#setMessagePlatformAddFailure(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messagePlatformAddFailure = value;
    }

    public final void setRecordsPerPage(final int value)
    {
        final String methodName = PlatformManagementController.CNAME + "#setRecordsPerPage(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.recordsPerPage = value;
    }

    @RequestMapping(value = "default", method = RequestMethod.GET)
    public final ModelAndView showDefaultPage(final Model model)
    {
        final String methodName = PlatformManagementController.CNAME + "#showDefaultPage(final Model model)";

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

        mView.addObject(Constants.COMMAND, new Platform());
        mView.setViewName(this.defaultPage);

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "search/terms/{terms}/type/{type}/page/{page}", method = RequestMethod.GET)
    public final ModelAndView showSearchPage(@PathVariable("terms") final String terms, @PathVariable("type") final String type, @PathVariable("page") final int page, final Model model)
    {
        final String methodName = PlatformManagementController.CNAME + "#showSearchPage(@PathVariable(\"terms\") final String terms, @PathVariable(\"type\") final String type, @PathVariable(\"page\") final int page, final Model model)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", terms);
            DEBUGGER.debug("Value: {}", type);
            DEBUGGER.debug("Value: {}", page);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IPlatformManagementProcessor processor = (IPlatformManagementProcessor) new PlatformManagementProcessorImpl();

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
            RequestHostInfo reqInfo = new RequestHostInfo();
            reqInfo.setHostName(hRequest.getRemoteHost());
            reqInfo.setHostAddress(hRequest.getRemoteAddr());

            if (DEBUG)
            {
                DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            }

            Platform platform = new Platform();
            platform.setPlatformName(terms);

            if (DEBUG)
            {
                DEBUGGER.debug("Platform: {}", platform);
            }

            PlatformManagementRequest svcRequest = new PlatformManagementRequest();
            svcRequest.setApplicationId(this.appConfig.getApplicationId());
            svcRequest.setApplicationName(this.appConfig.getApplicationName());
            svcRequest.setRequestInfo(reqInfo);
            svcRequest.setPlatform(platform);
            svcRequest.setServiceId(this.serviceId);
            svcRequest.setStartPage(page);
            svcRequest.setUserAccount(userAccount);

            if (DEBUG)
            {
                DEBUGGER.debug("PlatformManagementRequest: {}", svcRequest);
            }

            PlatformManagementResponse svcResponse = processor.getPlatformByAttribute(svcRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("PlatformManagementResponse: {}", svcResponse);
            }

            switch (svcResponse.getRequestStatus())
            {
				case EXCEPTION:
					mView.setViewName(this.appConfig.getErrorResponsePage());

					break;
				case FAILURE:
                    mView.addObject(Constants.ERROR_RESPONSE, this.appConfig.getMessageNoSearchResults());
                    mView.addObject(Constants.COMMAND, new Platform());
                    mView.setViewName(this.defaultPage);

					break;
				case SUCCESS:
                    mView.addObject("pages", (int) Math.ceil(svcResponse.getEntryCount() * 1.0 / this.recordsPerPage));
                    mView.addObject("page", page);
                    mView.addObject("searchTerms", terms);
                    mView.addObject("searchType", type);
                    mView.addObject(Constants.SEARCH_RESULTS, svcResponse.getPlatformList());
                    mView.addObject(Constants.COMMAND, new Platform());
                    mView.setViewName(this.defaultPage);

					break;
				case UNAUTHORIZED:
					mView.setViewName(this.appConfig.getUnauthorizedPage());

					break;
				default:
                    mView.addObject(Constants.ERROR_RESPONSE, this.appConfig.getMessageNoSearchResults());
                    mView.addObject(Constants.COMMAND, new Platform());
                    mView.setViewName(this.defaultPage);

					break;
            }
        }
        catch (final PlatformManagementException smx)
        {
            ERROR_RECORDER.error(smx.getMessage(), smx);

            mView.setViewName(this.appConfig.getErrorResponsePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "list-platforms", method = RequestMethod.GET)
    public final ModelAndView showPlatformList(final Model model)
    {
        final String methodName = PlatformManagementController.CNAME + "#showPlatformList(final String model)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IPlatformManagementProcessor processor = (IPlatformManagementProcessor) new PlatformManagementProcessorImpl();

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
            RequestHostInfo reqInfo = new RequestHostInfo();
            reqInfo.setHostName(hRequest.getRemoteHost());
            reqInfo.setHostAddress(hRequest.getRemoteAddr());

            if (DEBUG)
            {
                DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            }

            PlatformManagementRequest svcRequest = new PlatformManagementRequest();
            svcRequest.setRequestInfo(reqInfo);
            svcRequest.setUserAccount(userAccount);
            svcRequest.setServiceId(this.serviceId);
            svcRequest.setApplicationId(this.appConfig.getApplicationId());
            svcRequest.setApplicationName(this.appConfig.getApplicationName());

            if (DEBUG)
            {
                DEBUGGER.debug("PlatformManagementRequest: {}", svcRequest);
            }

            PlatformManagementResponse svcResponse = processor.listPlatforms(svcRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("PlatformManagementResponse: {}", svcResponse);
            }

            switch (svcResponse.getRequestStatus())
            {
				case EXCEPTION:
					mView.setViewName(this.appConfig.getErrorResponsePage());

					break;
				case FAILURE:
                    mView.addObject(Constants.ERROR_RESPONSE, this.appConfig.getMessageNoSearchResults());
                    mView.setViewName(this.addPlatformRedirect);

					break;
				case SUCCESS:
                    List<Platform> platformList = svcResponse.getPlatformList();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("platformList: {}", platformList);
                    }

                    mView.addObject("pages", (int) Math.ceil(svcResponse.getEntryCount() * 1.0 / this.recordsPerPage));
                    mView.addObject("page", 1);
                    mView.addObject("platformList", platformList);

                    mView.setViewName(this.viewPlatformList);

					break;
				case UNAUTHORIZED:
					mView.setViewName(this.appConfig.getUnauthorizedPage());

					break;
				default:
                    mView.addObject(Constants.ERROR_RESPONSE, this.appConfig.getMessageNoSearchResults());
                    mView.setViewName(this.addPlatformRedirect);

					break;
            }
        }
        catch (final PlatformManagementException smx)
        {
            ERROR_RECORDER.error(smx.getMessage(), smx);

            mView.setViewName(this.appConfig.getErrorResponsePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }
    
        return mView;
    }

    @RequestMapping(value = "list-platforms/page/{page}", method = RequestMethod.GET)
    public final ModelAndView showPlatformList(@PathVariable("page") final int page, final Model model)
    {
        final String methodName = PlatformManagementController.CNAME + "#showPlatformList(@PathVariable(\"page\") final int page, final Model model)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Page: {}", page);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IPlatformManagementProcessor processor = (IPlatformManagementProcessor) new PlatformManagementProcessorImpl();

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
            RequestHostInfo reqInfo = new RequestHostInfo();
            reqInfo.setHostName(hRequest.getRemoteHost());
            reqInfo.setHostAddress(hRequest.getRemoteAddr());

            if (DEBUG)
            {
                DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            }

            PlatformManagementRequest svcRequest = new PlatformManagementRequest();
            svcRequest.setRequestInfo(reqInfo);
            svcRequest.setUserAccount(userAccount);
            svcRequest.setServiceId(this.serviceId);
            svcRequest.setApplicationId(this.appConfig.getApplicationId());
            svcRequest.setApplicationName(this.appConfig.getApplicationName());
            svcRequest.setStartPage((page - 1) * this.recordsPerPage);

            if (DEBUG)
            {
                DEBUGGER.debug("PlatformManagementRequest: {}", svcRequest);
            }

            PlatformManagementResponse svcResponse = processor.listPlatforms(svcRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("PlatformManagementResponse: {}", svcResponse);
            }

            switch (svcResponse.getRequestStatus())
            {
				case EXCEPTION:
					mView.setViewName(this.appConfig.getErrorResponsePage());

					break;
				case FAILURE:
                    mView.addObject(Constants.ERROR_RESPONSE, this.appConfig.getErrorResponsePage());
                    mView.setViewName(this.defaultPage);

					break;
				case SUCCESS:
                    mView.addObject("pages", (int) Math.ceil(svcResponse.getEntryCount() * 1.0 / this.recordsPerPage));
                    mView.addObject("page", page);
                    mView.addObject("platformList", svcResponse.getPlatformList());

					break;
				case UNAUTHORIZED:
					break;
				default:
                    mView.addObject(Constants.ERROR_RESPONSE, this.appConfig.getMessageNoSearchResults());
                    mView.setViewName(this.defaultPage);

					break;
            }
        }
        catch (final PlatformManagementException smx)
        {
            ERROR_RECORDER.error(smx.getMessage(), smx);

            mView.setViewName(this.appConfig.getErrorResponsePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "platform/{guid}", method = RequestMethod.GET)
    public final ModelAndView showPlatform(@PathVariable("guid") final String guid, final Model model)
    {
        final String methodName = PlatformManagementController.CNAME + "#showPlatform(@PathVariable(\"guid\") final String guid, final Model model)";

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
        final IPlatformManagementProcessor platformMgr = (IPlatformManagementProcessor) new PlatformManagementProcessorImpl();

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
            RequestHostInfo reqInfo = new RequestHostInfo();
            reqInfo.setHostName(hRequest.getRemoteHost());
            reqInfo.setHostAddress(hRequest.getRemoteAddr());

            if (DEBUG)
            {
                DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            }

            Platform reqPlatform = new Platform();
            reqPlatform.setPlatformGuid(guid);

            if (DEBUG)
            {
                DEBUGGER.debug("Platform: {}", reqPlatform);
            }

            // get a list of available servers
            PlatformManagementRequest svcRequest = new PlatformManagementRequest();
            svcRequest.setRequestInfo(reqInfo);
            svcRequest.setUserAccount(userAccount);
            svcRequest.setServiceId(this.serviceId);
            svcRequest.setPlatform(reqPlatform);
            svcRequest.setApplicationId(this.appConfig.getApplicationId());
            svcRequest.setApplicationName(this.appConfig.getApplicationName());

            if (DEBUG)
            {
                DEBUGGER.debug("PlatformManagementRequest: {}", svcRequest);
            }

            PlatformManagementResponse svcResponse = platformMgr.getPlatformData(svcRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("PlatformManagementResponse: {}", svcResponse);
            }

            switch (svcResponse.getRequestStatus())
            {
				case EXCEPTION:
                    mView.setViewName(this.appConfig.getErrorResponsePage());

					break;
				case FAILURE:
                    mView.addObject(Constants.ERROR_RESPONSE, this.appConfig.getMessageNoSearchResults());
                    mView.setViewName(this.defaultPage);

					break;
				case SUCCESS:
                    Platform resPlatform = svcResponse.getPlatform();
                    
                    if (DEBUG)
                    {
                        DEBUGGER.debug("Service: {}", resPlatform);
                    }
    
                    mView.addObject("platform", resPlatform);
                    mView.setViewName(this.viewPlatformPage);

					break;
				case UNAUTHORIZED:
					mView.setViewName(this.appConfig.getUnauthorizedPage());

					break;
				default:
                    mView.addObject(Constants.ERROR_RESPONSE, this.appConfig.getMessageNoSearchResults());
                    mView.setViewName(this.defaultPage);

					break;
            
            }
        }
        catch (final PlatformManagementException smx)
        {
            ERROR_RECORDER.error(smx.getMessage(), smx);

            mView.setViewName(this.appConfig.getErrorResponsePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "add-platform", method = RequestMethod.GET)
    public final ModelAndView showAddPlatform(final Model model)
    {
        final String methodName = PlatformManagementController.CNAME + "#showAddPlatform(final Model model)";

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

    	mView.addObject(Constants.COMMAND, new Platform());
    	mView.setViewName(this.addPlatformPage);

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "search", method = RequestMethod.POST)
    public final ModelAndView submitPlatformSearch(@ModelAttribute("platform") final Platform platform, final BindingResult bindResult, final Model model)
    {
        final String methodName = PlatformManagementController.CNAME + "#submitPlatformSearch(@ModelAttribute(\"platform\") final Platform platform, final BindingResult bindResult, final Model model)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Platform: {}", platform);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IPlatformManagementProcessor processor = (IPlatformManagementProcessor) new PlatformManagementProcessorImpl();

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
            RequestHostInfo reqInfo = new RequestHostInfo();
            reqInfo.setHostName(hRequest.getRemoteHost());
            reqInfo.setHostAddress(hRequest.getRemoteAddr());

            if (DEBUG)
            {
                DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            }

            PlatformManagementRequest svcRequest = new PlatformManagementRequest();
            svcRequest.setApplicationId(this.appConfig.getApplicationId());
            svcRequest.setApplicationName(this.appConfig.getApplicationName());
            svcRequest.setRequestInfo(reqInfo);
            svcRequest.setPlatform(platform);
            svcRequest.setServiceId(this.serviceId);
            svcRequest.setUserAccount(userAccount);

            if (DEBUG)
            {
                DEBUGGER.debug("PlatformManagementRequest: {}", svcRequest);
            }

            PlatformManagementResponse svcResponse = processor.getPlatformByAttribute(svcRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("PlatformManagementResponse: {}", svcResponse);
            }

            switch (svcResponse.getRequestStatus())
            {
				case EXCEPTION:
					mView.setViewName(this.appConfig.getErrorResponsePage());

					break;
				case FAILURE:
                    mView.addObject(Constants.ERROR_RESPONSE, this.appConfig.getMessageNoSearchResults());
                    mView.addObject(Constants.COMMAND, new Platform());
                    mView.setViewName(this.defaultPage);

					break;
				case SUCCESS:
                    mView.addObject("pages", (int) Math.ceil(svcResponse.getEntryCount() * 1.0 / this.recordsPerPage));
                    mView.addObject("page", 1);
                    mView.addObject("searchTerms", platform.getPlatformName());
                    mView.addObject(Constants.SEARCH_RESULTS, svcResponse.getPlatformList());
                    mView.addObject(Constants.COMMAND, new Platform());
                    mView.setViewName(this.defaultPage);

					break;
				case UNAUTHORIZED:
					mView.setViewName(this.appConfig.getUnauthorizedPage());

					break;
				default:
                    mView.addObject(Constants.ERROR_RESPONSE, this.messageNoPlatformFound);
                    mView.addObject(Constants.COMMAND, new Platform());
                    mView.setViewName(this.defaultPage);

					break;
            }
        }
        catch (final PlatformManagementException smx)
        {
            ERROR_RECORDER.error(smx.getMessage(), smx);

            mView.setViewName(this.appConfig.getErrorResponsePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/add-platform", method = RequestMethod.POST)
    public final ModelAndView addNewPlatform(@ModelAttribute("request") final Platform platform, final BindingResult bindResult)
    {
        final String methodName = PlatformManagementController.CNAME + "#addNewPlatform(@ModelAttribute(\"request\") final Platform platform, final BindingResult bindResult)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Platform: {}", platform);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IPlatformManagementProcessor processor = (IPlatformManagementProcessor) new PlatformManagementProcessorImpl();

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

        this.validator.validate(platform, bindResult);

        if (bindResult.hasErrors())
        {
            // validation failed
            ERROR_RECORDER.error("Errors: {}", bindResult.getAllErrors());

            mView.addObject(Constants.ERROR_MESSAGE, this.appConfig.getMessageValidationFailed());
            mView.addObject(Constants.BIND_RESULT, bindResult.getAllErrors());
            mView.addObject(Constants.COMMAND, new Platform());
            mView.setViewName(this.addPlatformPage);

            return mView;
        }

        try
        {
            RequestHostInfo reqInfo = new RequestHostInfo();
            reqInfo.setHostName(hRequest.getRemoteHost());
            reqInfo.setHostAddress(hRequest.getRemoteAddr());

            if (DEBUG)
            {
                DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            }

            PlatformManagementRequest request = new PlatformManagementRequest();
            request.setApplicationId(this.appConfig.getApplicationId());
            request.setApplicationName(this.appConfig.getApplicationName());
            request.setPlatform(platform);
            request.setRequestInfo(reqInfo);
            request.setServiceId(this.serviceId);
            request.setUserAccount(userAccount);

            if (DEBUG)
            {
            	DEBUGGER.debug("PlatformManagementRequest: {}", request);
            }

            PlatformManagementResponse response = processor.addNewPlatform(request);

            if (DEBUG)
            {
            	DEBUGGER.debug("PlatformManagementResponse: {}", response);
            }

            switch (response.getRequestStatus())
            {
				case EXCEPTION:
					mView.setViewName(this.appConfig.getErrorResponsePage());

					break;
				case FAILURE:
					mView.addObject(Constants.ERROR_MESSAGE, this.messagePlatformAddFailure);
					mView.setViewName(this.addPlatformPage);

					break;
				case SUCCESS:
					mView.addObject(Constants.RESPONSE_MESSAGE, this.messagePlatformAddSuccess);
					mView.setViewName(this.addPlatformPage);

					break;
				case UNAUTHORIZED:
					mView.setViewName(this.appConfig.getUnauthorizedPage());

					break;
				default:
					// we dont know what happened tbh
					mView.addObject(Constants.ERROR_MESSAGE, this.messagePlatformAddFailure);
					mView.setViewName(this.addPlatformPage);

					break;
            }
        }
        catch (PlatformManagementException pmx)
        {
        	ERROR_RECORDER.error(pmx.getMessage(), pmx);

        	mView.setViewName(this.appConfig.getErrorResponsePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }
}
