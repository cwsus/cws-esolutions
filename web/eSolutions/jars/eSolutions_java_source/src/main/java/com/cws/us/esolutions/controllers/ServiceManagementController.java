/**
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
package com.cws.us.esolutions.controllers;

import java.util.Map;
import java.util.List;
import org.slf4j.Logger;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Enumeration;
import org.slf4j.LoggerFactory;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.cws.us.esolutions.Constants;
import com.cws.us.esolutions.dto.PlatformRequest;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.us.esolutions.ApplicationServiceBean;
import com.cws.esolutions.core.processors.dto.Server;
import com.cws.esolutions.core.processors.dto.Project;
import com.cws.esolutions.core.processors.dto.Platform;
import com.cws.us.esolutions.validators.PlatformValidator;
import com.cws.esolutions.core.processors.dto.SearchResult;
import com.cws.esolutions.core.processors.enums.ServerType;
import com.cws.esolutions.core.processors.dto.SearchRequest;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.dto.SearchResponse;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.core.processors.impl.SearchProcessorImpl;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.core.processors.interfaces.ISearchProcessor;
import com.cws.esolutions.core.processors.dto.ServerManagementRequest;
import com.cws.esolutions.core.processors.dto.ServerManagementResponse;
import com.cws.esolutions.core.processors.dto.ProjectManagementRequest;
import com.cws.esolutions.core.processors.dto.PlatformManagementRequest;
import com.cws.esolutions.core.processors.dto.ProjectManagementResponse;
import com.cws.esolutions.core.processors.dto.PlatformManagementResponse;
import com.cws.esolutions.core.processors.exception.SearchRequestException;
import com.cws.esolutions.core.processors.impl.ServerManagementProcessorImpl;
import com.cws.esolutions.core.processors.impl.ProjectManagementProcessorImpl;
import com.cws.esolutions.core.processors.exception.ServerManagementException;
import com.cws.esolutions.core.processors.exception.ProjectManagementException;
import com.cws.esolutions.core.processors.impl.PlatformManagementProcessorImpl;
import com.cws.esolutions.core.processors.exception.PlatformManagementException;
import com.cws.esolutions.core.processors.interfaces.IServerManagementProcessor;
import com.cws.esolutions.core.processors.interfaces.IProjectManagementProcessor;
import com.cws.esolutions.core.processors.interfaces.IPlatformManagementProcessor;
/**
 * eSolutions_java_source
 * com.cws.us.esolutions.controllers
 * ServiceManagementController.java
 *
 * $Id$
 * $Author$
 * $Date$
 * $Revision$
 * @author kh05451
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Jan 16, 2013 11:53:26 AM
 *     Created.
 */
@Controller
@RequestMapping("/service-management")
public class ServiceManagementController
{
    private String systemMgmt = null;
    private String projectMgmt = null;
    private String serviceName = null;
    private String defaultPage = null;
    private String platformMgmt = null;
    private String addProjectPage = null;
    private String selectDmgrPage = null;
    private String addPlatformPage = null;
    private String viewProjectPage = null;
    private String viewPlatformPage = null;
    private String viewProjectsList = null;
    private String viewPlatformList = null;
    private ApplicationServiceBean appConfig = null;
    private PlatformValidator platformValidator = null;

    private static final String CNAME = ServiceManagementController.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CNAME);

    public final void setPlatformValidator(final PlatformValidator value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setPlatformValidator(final PlatformValidator value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.platformValidator = value;
    }

    public final void setServiceName(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setServiceName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serviceName = value;
    }

    public final void setDefaultPage(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setDefaultPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.defaultPage = value;
    }

    public final void setAddProjectPage(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setAddProjectPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.addProjectPage = value;
    }

    public final void setSelectDmgrPage(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setSelectDmgrPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.selectDmgrPage = value;
    }

    public final void setAddPlatformPage(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setAddPlatformPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.addPlatformPage = value;
    }

    public final void setViewProjectPage(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setViewProjectPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.viewProjectPage = value;
    }

    public final void setViewPlatformPage(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setViewPlatformPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.viewPlatformPage = value;
    }

    public final void setViewProjectsList(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setViewProjectsList(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.viewProjectsList = value;
    }

    public final void setViewPlatformList(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setViewPlatformList(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.viewPlatformList = value;
    }

    public final void setAppConfig(final ApplicationServiceBean value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setAppConfig(final CoreServiceBean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.appConfig = value;
    }

    public final void setProjectMgmt(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setProjectMgmt(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.projectMgmt = value;
    }

    public final void setPlatformMgmt(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setPlatformMgmt(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.platformMgmt = value;
    }

    public final void setSystemMgmt(final String value)
    {
        final String methodName = ServiceManagementController.CNAME + "#setSystemMgmt(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.systemMgmt = value;
    }

    @RequestMapping(value = "/default", method = RequestMethod.GET)
    public ModelAndView showDefaultPage()
    {
        final String methodName = ServiceManagementController.CNAME + "#showDefaultPage()";

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
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }
        }

        if (userAccount.getStatus() == LoginStatus.EXPIRED)
        {
            // redirect to password page
            mView = new ModelAndView(new RedirectView());
            mView.setViewName(appConfig.getExpiredRedirect());
            mView.addObject(Constants.ERROR_MESSAGE, Constants.PASSWORD_EXPIRED);

            if (DEBUG)
            {
                DEBUGGER.debug("ModelAndView: {}", mView);
            }

            return mView;
        }

        if (appConfig.getServices().get(this.serviceName))
        {
            mView.setViewName(this.defaultPage);
        }
        else
        {
            mView.setViewName(appConfig.getUnavailablePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        // in here, we're going to get all the messages to display and such
        return mView;
    }

    @RequestMapping(value = "/project/{projectName}", method = RequestMethod.GET)
    public ModelAndView showProject(@PathVariable("projectName") final String projectName)
    {
        final String methodName = ServiceManagementController.CNAME + "#showProject(@PathVariable(\"projectName\") final String projectName)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("projectName: {}", projectName);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IProjectManagementProcessor projectMgr = new ProjectManagementProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }
        }

        if (userAccount.getStatus() == LoginStatus.EXPIRED)
        {
            // redirect to password page
            mView = new ModelAndView(new RedirectView());
            mView.setViewName(appConfig.getExpiredRedirect());
            mView.addObject(Constants.ERROR_MESSAGE, Constants.PASSWORD_EXPIRED);

            if (DEBUG)
            {
                DEBUGGER.debug("ModelAndView: {}", mView);
            }

            return mView;
        }

        if (appConfig.getServices().get(this.serviceName))
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

                Project reqProject = new Project();
                reqProject.setProjectGuid(projectName);

                if (DEBUG)
                {
                    DEBUGGER.debug("Project: {}", reqProject);
                }

                // get a list of available servers
                ProjectManagementRequest request = new ProjectManagementRequest();
                request.setRequestInfo(reqInfo);
                request.setUserAccount(userAccount);
                request.setServiceId(this.projectMgmt);
                request.setProject(reqProject);

                if (DEBUG)
                {
                    DEBUGGER.debug("ProjectManagementRequest: {}", request);
                }

                ProjectManagementResponse response = projectMgr.getProjectData(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("ProjectManagementResponse: {}", response);
                }

                if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    Project resProject = response.getProject();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Project: {}", resProject);
                    }

                    mView.addObject("project", resProject);
                    mView.setViewName(this.viewProjectPage);
                }
                else
                {
                    mView.addObject(Constants.ERROR_MESSAGE, response.getResponse());
                    mView.setViewName(this.defaultPage);
                }
            }
            catch (ProjectManagementException pmx)
            {
                ERROR_RECORDER.error(pmx.getMessage(), pmx);

                mView.setViewName(appConfig.getErrorResponsePage());
            }
        }
        else
        {
            mView.setViewName(appConfig.getUnavailablePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/platform/{platformName}", method = RequestMethod.GET)
    public ModelAndView showPlatform(@PathVariable("platformName") final String platformName)
    {
        final String methodName = ServiceManagementController.CNAME + "#showPlatform(@PathVariable(\"platformName\") final String platformName)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("platformName: {}", platformName);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IPlatformManagementProcessor platformMgr = new PlatformManagementProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }
        }

        if (userAccount.getStatus() == LoginStatus.EXPIRED)
        {
            // redirect to password page
            mView = new ModelAndView(new RedirectView());
            mView.setViewName(appConfig.getExpiredRedirect());
            mView.addObject(Constants.ERROR_MESSAGE, Constants.PASSWORD_EXPIRED);

            if (DEBUG)
            {
                DEBUGGER.debug("ModelAndView: {}", mView);
            }

            return mView;
        }

        if (appConfig.getServices().get(this.serviceName))
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

                Platform reqPlatform = new Platform();
                reqPlatform.setPlatformGuid(platformName);

                if (DEBUG)
                {
                    DEBUGGER.debug("Platform: {}", reqPlatform);
                }

                // get a list of available servers
                PlatformManagementRequest request = new PlatformManagementRequest();
                request.setRequestInfo(reqInfo);
                request.setUserAccount(userAccount);
                request.setServiceId(this.projectMgmt);
                request.setPlatform(reqPlatform);

                if (DEBUG)
                {
                    DEBUGGER.debug("PlatformManagementRequest: {}", request);
                }

                PlatformManagementResponse response = platformMgr.getPlatformData(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("PlatformManagementResponse: {}", response);
                }

                if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    Platform resPlatform = response.getPlatformData();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Platform: {}", resPlatform);
                    }

                    mView.addObject("platform", resPlatform);
                    mView.setViewName(this.viewPlatformPage);
                }
                else
                {
                    mView.addObject(Constants.ERROR_MESSAGE, response.getResponse());
                    mView.setViewName(this.defaultPage);
                }
            }
            catch (PlatformManagementException pmx)
            {
                ERROR_RECORDER.error(pmx.getMessage(), pmx);

                mView.setViewName(appConfig.getErrorResponsePage());
            }
        }
        else
        {
            mView.setViewName(appConfig.getUnavailablePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/add-project", method = RequestMethod.GET)
    public ModelAndView showAddProject()
    {
        final String methodName = ServiceManagementController.CNAME + "#showAddProject()";

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
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }
        }

        if (userAccount.getStatus() == LoginStatus.EXPIRED)
        {
            // redirect to password page
            mView = new ModelAndView(new RedirectView());
            mView.setViewName(appConfig.getExpiredRedirect());
            mView.addObject(Constants.ERROR_MESSAGE, Constants.PASSWORD_EXPIRED);

            if (DEBUG)
            {
                DEBUGGER.debug("ModelAndView: {}", mView);
            }

            return mView;
        }

        if (appConfig.getServices().get(this.serviceName))
        {
            mView.addObject("command", new Project());
            mView.setViewName(this.addProjectPage);
        }
        else
        {
            mView.setViewName(appConfig.getUnavailablePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/add-platform", method = RequestMethod.GET)
    public ModelAndView showAddPlatform()
    {
        final String methodName = ServiceManagementController.CNAME + "#showAddPlatform()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        ModelAndView mView = new ModelAndView();

        final IServerManagementProcessor serverMgr = new ServerManagementProcessorImpl();
        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);

        if (DEBUG)
        {
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }
        }

        if (userAccount.getStatus() == LoginStatus.EXPIRED)
        {
            // redirect to password page
            mView = new ModelAndView(new RedirectView());
            mView.setViewName(appConfig.getExpiredRedirect());
            mView.addObject(Constants.ERROR_MESSAGE, Constants.PASSWORD_EXPIRED);

            if (DEBUG)
            {
                DEBUGGER.debug("ModelAndView: {}", mView);
            }

            return mView;
        }

        if (appConfig.getServices().get(this.serviceName))
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

                Server dmgrServer = new Server();
                dmgrServer.setServerType(ServerType.DMGRSERVER);

                if (DEBUG)
                {
                    DEBUGGER.debug("Server: {}", dmgrServer);
                }

                ServerManagementRequest dmgrRequest = new ServerManagementRequest();
                dmgrRequest.setRequestInfo(reqInfo);
                dmgrRequest.setServiceId(this.systemMgmt);
                dmgrRequest.setTargetServer(dmgrServer);
                dmgrRequest.setUserAccount(userAccount);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServerManagementRequest: {}", dmgrRequest);
                }

                ServerManagementResponse dmgrResponse = serverMgr.listServersByType(dmgrRequest);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServerManagementResponse: {}", dmgrResponse);
                }

                if (dmgrResponse.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    List<Server> listing = dmgrResponse.getServerList();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("listing: {}", listing);
                    }

                    Map<String, String> dmgrList = new HashMap<String, String>();

                    for (Server server : listing)
                    {
                        if (DEBUG)
                        {
                            DEBUGGER.debug("Server: {}", server);
                        }

                        dmgrList.put(server.getServerGuid(), server.getOperHostName());
                    }

                    mView.addObject("dmgrList", dmgrList);
                }

                mView.addObject("command", new PlatformRequest());
                mView.setViewName(this.selectDmgrPage);
            }
            catch (ServerManagementException smx)
            {
                ERROR_RECORDER.error(smx.getMessage(), smx);

                mView.setViewName(appConfig.getErrorResponsePage());
            }
        }
        else
        {
            mView.setViewName(appConfig.getUnavailablePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/list-platforms", method = RequestMethod.GET)
    public ModelAndView showPlatformList()
    {
        final String methodName = ServiceManagementController.CNAME + "#showPlatformList()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IPlatformManagementProcessor platformMgr = new PlatformManagementProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }
        }

        if (userAccount.getStatus() == LoginStatus.EXPIRED)
        {
            // redirect to password page
            mView = new ModelAndView(new RedirectView());
            mView.setViewName(appConfig.getExpiredRedirect());
            mView.addObject(Constants.ERROR_MESSAGE, Constants.PASSWORD_EXPIRED);

            if (DEBUG)
            {
                DEBUGGER.debug("ModelAndView: {}", mView);
            }

            return mView;
        }

        if (appConfig.getServices().get(this.serviceName))
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

                PlatformManagementRequest request = new PlatformManagementRequest();
                request.setRequestInfo(reqInfo);
                request.setUserAccount(userAccount);
                request.setServiceId(this.platformMgmt);

                if (DEBUG)
                {
                    DEBUGGER.debug("PlatformManagementRequest: {}", request);
                }

                PlatformManagementResponse response = platformMgr.listPlatforms(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("PlatformManagementResponse: {}", response);
                }

                if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    List<Platform> platformList = response.getPlatformList();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("platformList: {}", platformList);
                    }

                    mView.addObject("platformList", platformList);
                    mView.setViewName(this.viewPlatformList);
                }
                else
                {
                    mView.addObject(Constants.ERROR_MESSAGE, response.getResponse());
                    mView.setViewName(this.defaultPage);
                }
            }
            catch (PlatformManagementException pmx)
            {
                ERROR_RECORDER.error(pmx.getMessage(), pmx);

                mView.setViewName(appConfig.getErrorResponsePage());
            }
        }
        else
        {
            mView.setViewName(appConfig.getUnavailablePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/list-projects", method = RequestMethod.GET)
    public ModelAndView showProjectList()
    {
        final String methodName = ServiceManagementController.CNAME + "#showProjectList()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IProjectManagementProcessor projectMgr = new ProjectManagementProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }
        }

        if (userAccount.getStatus() == LoginStatus.EXPIRED)
        {
            // redirect to password page
            mView = new ModelAndView(new RedirectView());
            mView.setViewName(appConfig.getExpiredRedirect());
            mView.addObject(Constants.ERROR_MESSAGE, Constants.PASSWORD_EXPIRED);

            if (DEBUG)
            {
                DEBUGGER.debug("ModelAndView: {}", mView);
            }

            return mView;
        }

        if (appConfig.getServices().get(this.serviceName))
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

                ProjectManagementRequest request = new ProjectManagementRequest();
                request.setRequestInfo(reqInfo);
                request.setUserAccount(userAccount);
                request.setServiceId(this.platformMgmt);

                if (DEBUG)
                {
                    DEBUGGER.debug("ProjectManagementRequest: {}", request);
                }

                ProjectManagementResponse response = projectMgr.listProjects(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("ProjectManagementResponse: {}", response);
                }

                if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    List<Project> projectList = response.getProjectList();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("projectList: {}", projectList);
                    }

                    mView.addObject("projectList", projectList);
                    mView.setViewName(this.viewProjectsList);
                }
                else
                {
                    mView.addObject(Constants.ERROR_MESSAGE, response.getResponse());
                    mView.setViewName(this.defaultPage);
                }
            }
            catch (ProjectManagementException pmx)
            {
                ERROR_RECORDER.error(pmx.getMessage(), pmx);

                mView.setViewName(appConfig.getErrorResponsePage());
            }
        }
        else
        {
            mView.setViewName(appConfig.getUnavailablePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/add-platform", method = RequestMethod.POST)
    public ModelAndView doAddPlatform(@ModelAttribute("request") final PlatformRequest request, final BindingResult bindResult)
    {
        final String methodName = ServiceManagementController.CNAME + "#doAddPlatform(@ModelAttribute(\"request\") final PlatformRequest request, final BindingResult bindResult)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Project: {}", request);
            DEBUGGER.debug("BindingResult: {}", bindResult);
        }

        ModelAndView mView = new ModelAndView();

        final ISearchProcessor search = new SearchProcessorImpl();
        final IServerManagementProcessor serverMgr = new ServerManagementProcessorImpl();
        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);

        if (DEBUG)
        {
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }
        }

        if (userAccount.getStatus() == LoginStatus.EXPIRED)
        {
            // redirect to password page
            mView = new ModelAndView(new RedirectView());
            mView.setViewName(appConfig.getExpiredRedirect());
            mView.addObject(Constants.ERROR_MESSAGE, Constants.PASSWORD_EXPIRED);

            if (DEBUG)
            {
                DEBUGGER.debug("ModelAndView: {}", mView);
            }

            return mView;
        }

        if (appConfig.getServices().get(this.serviceName))
        {
            platformValidator.validate(request, bindResult);

            if (bindResult.hasErrors())
            {
                // something was missing from the request
                mView.addObject("message", "invalid request data"); // TODO: this should be an actual message
                mView.addObject("command", new PlatformRequest());
                mView.setViewName(this.selectDmgrPage);
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

                    Server requestedDmgr = new Server();
                    String dmgrName = request.getDmgrName();
                    String dmgrGuid = request.getPlatformDmgr();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("dmgrGuid: {}", dmgrGuid);
                        DEBUGGER.debug("dmgrName: {}", dmgrName);
                    }

                    if ((StringUtils.isNotEmpty(dmgrGuid)) || (StringUtils.isNotEmpty(dmgrName)))
                    {
                        if (StringUtils.isNotEmpty(dmgrGuid))
                        {
                            requestedDmgr.setServerGuid(request.getPlatformDmgr());
                        }
                        else if (StringUtils.isNotEmpty(dmgrName))
                        {
                            SearchRequest dmgrSearch = new SearchRequest();
                            dmgrSearch.setRequestInfo(reqInfo);
                            dmgrSearch.setUserAccount(userAccount);
                            dmgrSearch.setSearchTerms(request.getDmgrName());

                            if (DEBUG)
                            {
                                DEBUGGER.debug("SearchRequest: {}", dmgrSearch);
                            }

                            SearchResponse dmgrSearchRes = search.doServerSearch(dmgrSearch);

                            if (DEBUG)
                            {
                                DEBUGGER.debug("SearchResponse: {}", dmgrSearchRes);
                            }

                            if (dmgrSearchRes.getRequestStatus() == CoreServicesStatus.SUCCESS)
                            {
                                List<SearchResult> dmgrSearchResults = dmgrSearchRes.getResults();

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("dmgrSearchResults: {}", dmgrSearchRes);
                                }

                                if ((dmgrSearchResults != null) && (dmgrSearchResults.size() != 0))
                                {
                                    for (SearchResult searchResult : dmgrSearchResults)
                                    {
                                        if (DEBUG)
                                        {
                                            DEBUGGER.debug("SearchResult: {}", searchResult);
                                        }

                                        if (StringUtils.equals(searchResult.getTitle(), dmgrName))
                                        {
                                            requestedDmgr.setServerGuid(searchResult.getPath());
                                            requestedDmgr.setOperHostName(searchResult.getTitle());

                                            if (DEBUG)
                                            {
                                                DEBUGGER.debug("Server: {}", requestedDmgr);
                                            }

                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        else
                        {
                            mView.addObject(Constants.ERROR_MESSAGE, "system.mgmt.no.dmgr");
                            mView.addObject("command", new PlatformRequest());
                            mView.setViewName(this.selectDmgrPage);

                            if (DEBUG)
                            {
                                DEBUGGER.debug("ModelAndView: {}", mView);
                            }

                            return mView;
                        }

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Server: {}", requestedDmgr);
                        }

                        ServerManagementRequest dmgrRequest = new ServerManagementRequest();
                        dmgrRequest.setRequestInfo(reqInfo);
                        dmgrRequest.setUserAccount(userAccount);
                        dmgrRequest.setServiceId(this.systemMgmt);
                        dmgrRequest.setTargetServer(requestedDmgr);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("ServerManagementRequest: {}", dmgrRequest);
                        }

                        ServerManagementResponse dmgrResponse = serverMgr.getServerData(dmgrRequest);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("ServerManagementResponse: {}", dmgrResponse);
                        }

                        if (dmgrResponse.getRequestStatus() == CoreServicesStatus.SUCCESS)
                        {
                            Server dmgrResServer = dmgrResponse.getServer();

                            if (DEBUG)
                            {
                                DEBUGGER.debug("Server: {}", dmgrResServer);
                            }

                            if ((request.getAppServers() == null) || (request.getAppServers().size() == 0))
                            {
                                SearchRequest appServerSearch = new SearchRequest();
                                appServerSearch.setRequestInfo(reqInfo);
                                appServerSearch.setUserAccount(userAccount);
                                appServerSearch.setSearchTerms(dmgrResServer.getServerGuid());

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("SearchRequest: {}", appServerSearch);
                                }

                                SearchResponse appServerResponse = search.doServerSearch(appServerSearch);

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("SearchResponse: {}", appServerResponse);
                                }

                                if (appServerResponse.getRequestStatus() == CoreServicesStatus.SUCCESS)
                                {
                                    List<SearchResult> searchList = appServerResponse.getResults();
                                    Map<String, String> appServerList = new HashMap<String, String>();

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("searchList: {}", searchList);
                                    }

                                    for (SearchResult result : searchList)
                                    {
                                        if (DEBUG)
                                        {
                                            DEBUGGER.debug("SearchResult: {}", result);
                                        }

                                        if (!(StringUtils.equals(request.getPlatformDmgr(), result.getPath())))
                                        {
                                            appServerList.put(result.getPath(), result.getTitle());
                                        }
                                    }

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("appServerList: {}", appServerList);
                                    }

                                    mView.addObject("appServerList", appServerList);
                                }
                            }

                            if ((request.getWebServers() == null) || (request.getWebServers().size() == 0))
                            {
                                Server webServerSearch = new Server();
                                webServerSearch.setServerType(ServerType.WEBSERVER);

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("Server: {}", webServerSearch);
                                }

                                ServerManagementRequest webRequest = new ServerManagementRequest();
                                webRequest.setRequestInfo(reqInfo);
                                webRequest.setUserAccount(userAccount);
                                webRequest.setServiceId(this.systemMgmt);
                                webRequest.setTargetServer(webServerSearch);

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("ServerManagementRequest: {}", webRequest);
                                }

                                ServerManagementResponse webResponse = serverMgr.listServersByType(webRequest);

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("ServerManagementResponse: {}", webResponse);
                                }

                                if (webResponse.getRequestStatus() == CoreServicesStatus.SUCCESS)
                                {
                                    List<Server> webServers = webResponse.getServerList();
                                    Map<String, String> webServerList = new HashMap<String, String>();

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("webServers: {}", webServers);
                                    }

                                    for (Server server : webServers)
                                    {
                                        if (DEBUG)
                                        {
                                            DEBUGGER.debug("Server: {}", server);
                                        }

                                        if (server.getServerRegion() == dmgrResServer.getServerRegion())
                                        {
                                            webServerList.put(server.getServerGuid(), server.getOperHostName());
                                        }
                                    }

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("webServerList: {}", webServerList);
                                    }

                                    mView.addObject("webServerList", webServerList);
                                }
                            }

                            request.setPlatformDmgr(request.getPlatformDmgr());
                            request.setDmgrName(dmgrResServer.getOperHostName());
                            request.setPlatformRegion(dmgrResServer.getServerRegion());

                            if (DEBUG)
                            {
                                DEBUGGER.debug("PlatformRequest: {}", request);
                            }

                            mView.addObject("command", request);
                            mView.setViewName(this.addPlatformPage);
                        }
                        else
                        {
                            // no valid dmgr
                            mView.addObject(Constants.ERROR_MESSAGE, "system.mgmt.no.dmgr");
                            mView.addObject("command", new PlatformRequest());
                            mView.setViewName(this.selectDmgrPage);
                        }
                    }
                    else
                    {
                        // no dmgr
                        mView.addObject(Constants.ERROR_MESSAGE, "system.mgmt.no.dmgr");
                        mView.addObject("command", new PlatformRequest());
                        mView.setViewName(this.selectDmgrPage);
                    }
                }
                catch (ServerManagementException smx)
                {
                    ERROR_RECORDER.error(smx.getMessage(), smx);

                    mView.setViewName(appConfig.getErrorResponsePage());
                }
                catch (SearchRequestException srx)
                {
                    ERROR_RECORDER.error(srx.getMessage(), srx);

                    mView.setViewName(appConfig.getErrorResponsePage());
                }
            }
        }
        else
        {
            mView.setViewName(appConfig.getUnavailablePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/submit-platform", method = RequestMethod.POST)
    public ModelAndView submitNewPlatform(@ModelAttribute("request") final PlatformRequest request, final BindingResult bindResult)
    {
        final String methodName = ServiceManagementController.CNAME + "#submitNewPlatform(@ModelAttribute(\"request\") final PlatformRequest request, final BindingResult bindResult)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Project: {}", request);
            DEBUGGER.debug("BindingResult: {}", bindResult);
        }

        ModelAndView mView = new ModelAndView();

        final ISearchProcessor search = new SearchProcessorImpl();
        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IPlatformManagementProcessor platformMgr = new PlatformManagementProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }
        }

        if (userAccount.getStatus() == LoginStatus.EXPIRED)
        {
            // redirect to password page
            mView = new ModelAndView(new RedirectView());
            mView.setViewName(appConfig.getExpiredRedirect());
            mView.addObject(Constants.ERROR_MESSAGE, Constants.PASSWORD_EXPIRED);

            if (DEBUG)
            {
                DEBUGGER.debug("ModelAndView: {}", mView);
            }

            return mView;
        }

        if (appConfig.getServices().get(this.serviceName))
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

                Server requestedDmgr = new Server();
                String dmgrName = request.getDmgrName();
                String dmgrGuid = request.getPlatformDmgr();

                if (DEBUG)
                {
                    DEBUGGER.debug("dmgrGuid: {}", dmgrGuid);
                    DEBUGGER.debug("dmgrName: {}", dmgrName);
                }

                if (StringUtils.isNotEmpty(dmgrName))
                {
                    SearchRequest dmgrSearch = new SearchRequest();
                    dmgrSearch.setRequestInfo(reqInfo);
                    dmgrSearch.setUserAccount(userAccount);
                    dmgrSearch.setSearchTerms(request.getDmgrName());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("SearchRequest: {}", dmgrSearch);
                    }

                    SearchResponse dmgrSearchRes = search.doServerSearch(dmgrSearch);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("SearchResponse: {}", dmgrSearchRes);
                    }

                    if (dmgrSearchRes.getRequestStatus() == CoreServicesStatus.SUCCESS)
                    {
                        List<SearchResult> dmgrSearchResults = dmgrSearchRes.getResults();

                        if (DEBUG)
                        {
                            DEBUGGER.debug("dmgrSearchResults: {}", dmgrSearchRes);
                        }

                        if ((dmgrSearchResults != null) && (dmgrSearchResults.size() != 0))
                        {
                            for (SearchResult searchResult : dmgrSearchResults)
                            {
                                if (DEBUG)
                                {
                                    DEBUGGER.debug("SearchResult: {}", searchResult);
                                }

                                if (StringUtils.equals(searchResult.getTitle(), dmgrName))
                                {
                                    requestedDmgr.setServerGuid(searchResult.getPath());
                                    requestedDmgr.setOperHostName(searchResult.getTitle());

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("Server: {}", requestedDmgr);
                                    }

                                    break;
                                }
                            }

                            Platform platform = new Platform();
                            platform.setPlatformName(request.getPlatformName());
                            platform.setPlatformRegion(request.getPlatformRegion());
                            platform.setStatus(request.getStatus());
                            platform.setPlatformDmgr(requestedDmgr);
                            platform.setDescription(request.getDescription());

                            if (DEBUG)
                            {
                                DEBUGGER.debug("Platform: {}", platform);
                            }

                            if ((request.getWebServers() != null) && (request.getWebServers().size() != 0))
                            {
                                List<Server> webServers = new ArrayList<Server>();

                                for (String guid : request.getWebServers())
                                {
                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("guid: {}", guid);
                                    }

                                    Server server = new Server();
                                    server.setServerGuid(guid);

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("Server: {}", server);
                                    }

                                    webServers.add(server);
                                }

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("webServers: {}", webServers);
                                }

                                platform.setWebServers(webServers);
                            }

                            if (DEBUG)
                            {
                                DEBUGGER.debug("Platform: {}", platform);
                            }

                            if ((request.getAppServers() != null) && (request.getAppServers().size() != 0))
                            {
                                List<Server> appServers = new ArrayList<Server>();

                                for (String guid : request.getAppServers())
                                {
                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("guid: {}", guid);
                                    }

                                    Server server = new Server();
                                    server.setServerGuid(guid);

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("Server: {}", server);
                                    }

                                    appServers.add(server);
                                }

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("appServers: {}", appServers);
                                }

                                platform.setAppServers(appServers);
                            }

                            if (DEBUG)
                            {
                                DEBUGGER.debug("Platform: {}", platform);
                            }

                            PlatformManagementRequest platformRequest = new PlatformManagementRequest();
                            platformRequest.setRequestInfo(reqInfo);
                            platformRequest.setUserAccount(userAccount);
                            platformRequest.setServiceId(this.platformMgmt);
                            platformRequest.setPlatform(platform);

                            if (DEBUG)
                            {
                                DEBUGGER.debug("PlatformManagementRequest: {}", platformRequest);
                            }

                            PlatformManagementResponse platformResponse = platformMgr.addNewPlatform(platformRequest);

                            if (DEBUG)
                            {
                                DEBUGGER.debug("PlatformManagementResponse: {}", platformResponse);
                            }

                            if (platformResponse.getRequestStatus() == CoreServicesStatus.SUCCESS)
                            {
                                mView.addObject("command", new PlatformRequest());
                                mView.addObject(Constants.RESPONSE_MESSAGE, platformResponse.getResponse());
                                mView.setViewName(this.selectDmgrPage);
                            }
                            else
                            {
                                mView.addObject("command", new PlatformRequest());
                                mView.addObject(Constants.ERROR_MESSAGE, platformResponse.getResponse());
                                mView.setViewName(this.selectDmgrPage);
                            }
                        }
                        else
                        {
                            mView.addObject(Constants.ERROR_MESSAGE, "system.mgmt.no.dmgr");
                            mView.addObject("command", new PlatformRequest());
                            mView.setViewName(this.selectDmgrPage);
                        }
                    }
                    else
                    {
                        mView.addObject(Constants.ERROR_MESSAGE, "system.mgmt.no.dmgr");
                        mView.addObject("command", new PlatformRequest());
                        mView.setViewName(this.selectDmgrPage);
                    }
                }
                else
                {
                    // no dmgr
                    mView.addObject(Constants.ERROR_MESSAGE, "system.mgmt.no.dmgr");
                    mView.addObject("command", new PlatformRequest());
                    mView.setViewName(this.selectDmgrPage);
                }
            }
            catch (SearchRequestException srx)
            {
                ERROR_RECORDER.error(srx.getMessage(), srx);

                mView.setViewName(appConfig.getErrorResponsePage());
            }
            catch (PlatformManagementException pmx)
            {
                ERROR_RECORDER.error(pmx.getMessage(), pmx);

                mView.setViewName(appConfig.getErrorResponsePage());
            }
        }
        else
        {
            mView.setViewName(appConfig.getUnavailablePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/add-project", method = RequestMethod.POST)
    public ModelAndView doAddProject(@ModelAttribute("request") final Project request, final BindingResult bindResult)
    {
        final String methodName = ServiceManagementController.CNAME + "#doAddProject(@ModelAttribute(\"request\") final Project request, final BindingResult bindResult)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Project: {}", request);
            DEBUGGER.debug("BindingResult: {}", bindResult);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IProjectManagementProcessor projectMgr = new ProjectManagementProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }
        }

        if (userAccount.getStatus() == LoginStatus.EXPIRED)
        {
            // redirect to password page
            mView = new ModelAndView(new RedirectView());
            mView.setViewName(appConfig.getExpiredRedirect());
            mView.addObject(Constants.ERROR_MESSAGE, Constants.PASSWORD_EXPIRED);

            if (DEBUG)
            {
                DEBUGGER.debug("ModelAndView: {}", mView);
            }

            return mView;
        }

        if (appConfig.getServices().get(this.serviceName))
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

                ProjectManagementRequest projectRequest = new ProjectManagementRequest();
                projectRequest.setRequestInfo(reqInfo);
                projectRequest.setUserAccount(userAccount);
                projectRequest.setServiceId(this.projectMgmt);
                projectRequest.setProject(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("ProjectManagementRequest: {}", projectRequest);
                }

                ProjectManagementResponse projectResponse = projectMgr.addNewProject(projectRequest);

                if (DEBUG)
                {
                    DEBUGGER.debug("ProjectManagementResponse: {}", projectResponse);
                }

                if (projectResponse.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    mView.addObject(Constants.RESPONSE_MESSAGE, projectResponse.getResponse());
                }
                else
                {
                    mView.addObject(Constants.ERROR_MESSAGE, projectResponse.getResponse());
                }

                mView.addObject("command", new Project());
                mView.setViewName(this.addProjectPage);
            }
            catch (ProjectManagementException pmx)
            {
                ERROR_RECORDER.error(pmx.getMessage(), pmx);

                mView.setViewName(appConfig.getErrorResponsePage());
            }
        }
        else
        {
            mView.setViewName(appConfig.getUnavailablePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }
}

