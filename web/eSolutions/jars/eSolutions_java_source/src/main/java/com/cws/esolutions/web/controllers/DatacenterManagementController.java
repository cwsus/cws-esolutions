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
 * File: DataCenterManagement.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.util.List;
import java.util.Objects;
import java.util.Enumeration;
import org.springframework.ui.Model;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import javax.servlet.http.HttpServletRequest;import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.cws.esolutions.web.Constants;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.web.ApplicationServiceBean;
import com.cws.esolutions.core.processors.dto.Datacenter;
import com.cws.esolutions.web.validators.DatacenterValidator;
import com.cws.esolutions.core.processors.enums.ServiceStatus;
import com.cws.esolutions.core.processors.dto.DatacenterManagementRequest;
import com.cws.esolutions.core.processors.dto.DatacenterManagementResponse;
import com.cws.esolutions.core.processors.impl.DatacenterManagementProcessorImpl;
import com.cws.esolutions.core.processors.exception.DatacenterManagementException;
import com.cws.esolutions.core.processors.interfaces.IDatacenterManagementProcessor;
/**
 * @author cws-khuntly
 * @version 1.0
 * @see org.springframework.stereotype.Controller
 */
@Controller
@RequestMapping("datacenter-management")
public class DatacenterManagementController
{
    private String homePage = null;
    private String serviceId = null;
    private int recordsPerPage = 20;
    private String addDatacenterPage = null;
    private String viewDatacenterPage = null;
    private String listDatacentersPage = null;
    private DatacenterValidator validator = null;
    private String messageDatacenterAddFailure = null;
    private String messageDatacenterAddSuccess = null;

    private String messageNoSearchResults = null;
    private ApplicationServiceBean appConfig = null;

    private static final String CNAME = DatacenterManagementController.class.getName();

    private static final Logger DEBUGGER = LogManager.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LogManager.getLogger(Constants.ERROR_LOGGER + CNAME);

    public final void setAppConfig(final ApplicationServiceBean value)
    {
        final String methodName = DatacenterManagementController.CNAME + "#setAppConfig(final ApplicationServiceBean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.appConfig = value;
    }

    public final void setServiceId(final String value)
    {
        final String methodName = DatacenterManagementController.CNAME + "#setServiceId(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serviceId = value;
    }

    public final void setHomePage(final String value)
    {
        final String methodName = DatacenterManagementController.CNAME + "#setHomePage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.homePage = value;
    }

    public final void setAddDatacenterPage(final String value)
    {
        final String methodName = DatacenterManagementController.CNAME + "#setAddDatacenterPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.addDatacenterPage = value;
    }

    public final void setViewDatacenterPage(final String value)
    {
        final String methodName = DatacenterManagementController.CNAME + "#setViewDatacenterPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.viewDatacenterPage = value;
    }

    public final void setListDatacentersPage(final String value)
    {
        final String methodName = DatacenterManagementController.CNAME + "#setListDatacentersPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.listDatacentersPage = value;
    }

    public final void setMessageNoSearchResults(final String value)
    {
        final String methodName = DatacenterManagementController.CNAME + "#setMessageNoSearchResults(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageNoSearchResults = value;
    }

    public final void setMessageDatacenterAddSuccess(final String value)
    {
        final String methodName = DatacenterManagementController.CNAME + "#setMessageDatacenterAddSuccess(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageDatacenterAddSuccess = value;
    }

    public final void setMessageDatacenterAddFailure(final String value)
    {
        final String methodName = DatacenterManagementController.CNAME + "#setMessageDatacenterAddFailure(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageDatacenterAddFailure = value;
    }

    public final void setValidator(final DatacenterValidator value)
    {
        final String methodName = DatacenterManagementController.CNAME + "#setValidator(final DatacenterValidator value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.validator = value;
    }

    @RequestMapping(value = "default", method = RequestMethod.GET)
    public final ModelAndView showDefaultPage(final Model model)
    {
        final String methodName = DatacenterManagementController.CNAME + "#showDefaultPage(final Model model)";

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

                DEBUGGER.debug("Session Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<?> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = (String) requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Request Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<?> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String element = (String) paramsEnumeration.nextElement();
                Object value = hRequest.getParameter(element);

                DEBUGGER.debug("Request Parameter: {}; Value: {}", element, value);
            }
        }

        mView.addObject(Constants.COMMAND, new Datacenter());
        mView.setViewName(this.homePage);

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "add-datacenter", method = RequestMethod.GET)
    public final ModelAndView showAddDatacenter(final Model model)
    {
        final String methodName = DatacenterManagementController.CNAME + "#showAddDatacenter(final Model model)";

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

        model.addAttribute("statusList", ServiceStatus.values());
        model.addAttribute(Constants.COMMAND, new Datacenter());
        mView.setViewName(this.addDatacenterPage);

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "list-datacenters", method = RequestMethod.GET)
    public final ModelAndView showDatacenterList(final Model model)
    {
        final String methodName = DatacenterManagementController.CNAME + "#showDatacenterList(final Model model)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IDatacenterManagementProcessor processor = (IDatacenterManagementProcessor) new DatacenterManagementProcessorImpl();

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

            DatacenterManagementRequest mgmtRequest = new DatacenterManagementRequest();
            mgmtRequest.setApplicationId(this.appConfig.getApplicationId());
            mgmtRequest.setApplicationName(this.appConfig.getApplicationName());
            mgmtRequest.setRequestInfo(reqInfo);
            mgmtRequest.setServiceId(this.serviceId);
            mgmtRequest.setStartPage(0);
            mgmtRequest.setUserAccount(userAccount);

            if (DEBUG)
            {
                DEBUGGER.debug("DatacenterManagementRequest: {}", mgmtRequest);
            }

            DatacenterManagementResponse mgmtResponse = processor.listDatacenters(mgmtRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("DatacenterManagementResponse: {}", mgmtResponse);
            }

            switch (mgmtResponse.getRequestStatus())
            {
				case EXCEPTION:
					mView.setViewName(this.appConfig.getErrorResponsePage());

					break;
				case FAILURE:
                    mView.addObject(Constants.ERROR_RESPONSE, this.appConfig.getMessageNoSearchResults());
                    mView.addObject(Constants.COMMAND, new Datacenter());
                    mView.setViewName(this.addDatacenterPage);

					break;
				case SUCCESS:
                    List<Datacenter> datacenterList = mgmtResponse.getDatacenterList();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("List<Datacenter>: {}", datacenterList);
                    }

                    if ((datacenterList != null) && (datacenterList.size() != 0))
                    {
                        mView.addObject("pages", (int) Math.ceil(mgmtResponse.getEntryCount() * 1.0 / this.recordsPerPage));
                        mView.addObject("page", 1);
                        mView.addObject("datacenterList", datacenterList);
                        mView.setViewName(this.listDatacentersPage);
                    }
                    else
                    {
                        mView.addObject(Constants.MESSAGE_RESPONSE, this.messageNoSearchResults);
                        mView.addObject(Constants.COMMAND, new Datacenter());
                        mView.setViewName(this.addDatacenterPage);
                    }

					break;
				case UNAUTHORIZED:
					mView.setViewName(this.appConfig.getUnauthorizedPage());

					break;
				default:
					mView.addObject(Constants.ERROR_RESPONSE, this.appConfig.getMessageRequestProcessingFailure());
                    mView.addObject(Constants.COMMAND, new Datacenter());
                    mView.setViewName(this.addDatacenterPage);

					break;
            }
        }
        catch (final DatacenterManagementException dmx)
        {
            ERROR_RECORDER.error(dmx.getMessage(), dmx);

            mView.setViewName(this.appConfig.getErrorResponsePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "datacenter/{guid}", method = RequestMethod.GET)
    public final ModelAndView showSearchPage(@PathVariable("guid") final String guid, final Model model)
    {
        final String methodName = DatacenterManagementController.CNAME + "#showSearchPage(@PathVariable(\"guid\") final String guid, final Model model)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("@PathVariable(\"guid\"): ", guid);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IDatacenterManagementProcessor processor = (IDatacenterManagementProcessor) new DatacenterManagementProcessorImpl();

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

            Datacenter datacenter = new Datacenter();
            datacenter.setGuid(guid);

            if (DEBUG)
            {
                DEBUGGER.debug("Datacenter: {}", datacenter);
            }

            DatacenterManagementRequest mgmtRequest = new DatacenterManagementRequest();
            mgmtRequest.setApplicationId(this.appConfig.getApplicationId());
            mgmtRequest.setApplicationName(this.appConfig.getApplicationName());
            mgmtRequest.setRequestInfo(reqInfo);
            mgmtRequest.setServiceId(this.serviceId);
            mgmtRequest.setStartPage(0);
            mgmtRequest.setUserAccount(userAccount);
            mgmtRequest.setDatacenter(datacenter);

            if (DEBUG)
            {
                DEBUGGER.debug("DatacenterManagementRequest: {}", mgmtRequest);
            }

            DatacenterManagementResponse mgmtResponse = processor.getDatacenterData(mgmtRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("DatacenterManagementRequest: {}", mgmtResponse);
            }

            switch (mgmtResponse.getRequestStatus())
            {
				case EXCEPTION:
					mView.setViewName(this.appConfig.getErrorResponsePage());

					break;
				case FAILURE:
					mView.setViewName(this.appConfig.getErrorResponsePage());

					break;
				case SUCCESS:
                    Datacenter dcResponse = mgmtResponse.getDatacenter();
                    
                    if (DEBUG)
                    {
                        DEBUGGER.debug("Datacenter: {}", dcResponse);
                    }
    
                    if (Objects.isNull(dcResponse))
                    {
                        mView.addObject(Constants.MESSAGE_RESPONSE, this.messageNoSearchResults);
                        mView.addObject(Constants.COMMAND, new Datacenter());
                        mView.setViewName(this.addDatacenterPage);
                    }
    
                    mView.addObject("datacenter", dcResponse);
                    mView.setViewName(this.viewDatacenterPage);

					break;
				case UNAUTHORIZED:
					mView.setViewName(this.appConfig.getUnauthorizedPage());

					break;
				default:
					mView.addObject(Constants.ERROR_RESPONSE, this.appConfig.getMessageRequestProcessingFailure());
                    model.addAttribute(Constants.COMMAND, new Datacenter());
                    mView.setViewName(this.addDatacenterPage);

					break;
            }
        }
        catch (final DatacenterManagementException dmx)
        {
            ERROR_RECORDER.error(dmx.getMessage(), dmx);

            mView.setViewName(this.appConfig.getErrorResponsePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "add-datacenter", method = RequestMethod.POST)
    public final ModelAndView doAddDatacenter(@ModelAttribute("datacenter") final Datacenter datacenter, final BindingResult bindResult, final Model model)
    {
        final String methodName = DatacenterManagementController.CNAME + "#doAddDatacenter(@ModelAttribute(\"datacenter\") final Datacenter datacenter, final BindingResult bindResult, final Model model)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", datacenter);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IDatacenterManagementProcessor processor = (IDatacenterManagementProcessor) new DatacenterManagementProcessorImpl();

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

        this.validator.validate(datacenter, bindResult);
        
        if (bindResult.hasErrors())
        {
            // validation failed
            ERROR_RECORDER.error("Errors: {}", bindResult.getAllErrors());

            mView.addObject(Constants.ERROR_MESSAGE, this.appConfig.getMessageValidationFailed());
            mView.addObject(Constants.BIND_RESULT, bindResult.getAllErrors());
            mView.addObject(Constants.COMMAND, new Datacenter());
            mView.setViewName(this.addDatacenterPage);
        }
        else
        {
            try
            {
                RequestHostInfo reqInfo = new RequestHostInfo();
                reqInfo.setHostName(hRequest.getRemoteHost());
                reqInfo.setHostAddress(hRequest.getRemoteAddr());
    
                if (DEBUG)
                {
                    DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
                }
    
                DatacenterManagementRequest mgmtRequest = new DatacenterManagementRequest();
                mgmtRequest.setDatacenter(datacenter);
                mgmtRequest.setRequestInfo(reqInfo);
                mgmtRequest.setServiceId(this.serviceId);
                mgmtRequest.setUserAccount(userAccount);
                mgmtRequest.setApplicationId(this.appConfig.getApplicationId());
                mgmtRequest.setApplicationName(this.appConfig.getApplicationName());
    
                if (DEBUG)
                {
                    DEBUGGER.debug("DatacenterManagementRequest: {}", mgmtRequest);
                }
    
                DatacenterManagementResponse mgmtResponse = processor.addNewDatacenter(mgmtRequest);
    
                if (DEBUG)
                {
                    DEBUGGER.debug("DatacenterManagementResponse: {}", mgmtResponse);
                }

                switch (mgmtResponse.getRequestStatus())
                {
					case EXCEPTION:
						mView.setViewName(this.appConfig.getErrorResponsePage());

						break;
					case FAILURE:
                        mView.addObject(Constants.ERROR_RESPONSE, this.messageDatacenterAddFailure);
                        mView.addObject(Constants.COMMAND, new Datacenter());
                        mView.setViewName(this.addDatacenterPage);

						break;
					case SUCCESS:
                        mView.addObject(Constants.RESPONSE_MESSAGE, this.messageDatacenterAddSuccess);
                        mView.addObject(Constants.COMMAND, new Datacenter());
                        mView.setViewName(this.addDatacenterPage);

						break;
					case UNAUTHORIZED:
						mView.setViewName(this.appConfig.getUnauthorizedPage());

						break;
					default:
						mView.addObject(Constants.ERROR_RESPONSE, this.appConfig.getMessageRequestProcessingFailure());
                        mView.addObject(Constants.COMMAND, new Datacenter());
                        mView.setViewName(this.addDatacenterPage);

						break;
                }
            }
            catch (final DatacenterManagementException dmx)
            {
                ERROR_RECORDER.error(dmx.getMessage(), dmx);
    
                mView.setViewName(this.appConfig.getErrorResponsePage());
            }
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }
}
