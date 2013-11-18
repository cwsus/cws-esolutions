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

import java.util.Date;
import java.util.List;
import java.util.Arrays;
import org.slf4j.Logger;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import org.slf4j.LoggerFactory;
import java.text.MessageFormat;
import org.apache.commons.io.IOUtils;
import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.cws.us.esolutions.Constants;
import com.cws.esolutions.security.enums.Role;
import com.cws.esolutions.core.utils.EmailUtils;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.dto.UserSecurity;
import com.cws.us.esolutions.ApplicationServiceBean;
import com.cws.esolutions.security.audit.dto.AuditEntry;
import com.cws.esolutions.security.config.SecurityConfig;
import com.cws.esolutions.core.processors.dto.EmailMessage;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.us.esolutions.validators.UserAccountValidator;
import com.cws.esolutions.core.controllers.ResourceController;
import com.cws.esolutions.core.exception.CoreServiceException;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.processors.enums.ControlType;
import com.cws.esolutions.security.processors.enums.ModificationType;
import com.cws.esolutions.security.processors.dto.AccountResetRequest;
import com.cws.esolutions.security.processors.dto.AccountResetResponse;
import com.cws.esolutions.security.processors.dto.AccountControlRequest;
import com.cws.esolutions.security.dao.usermgmt.enums.SearchRequestType;
import com.cws.esolutions.security.processors.dto.AccountControlResponse;
import com.cws.esolutions.security.processors.impl.AccountResetProcessorImpl;
import com.cws.esolutions.security.processors.exception.AccountResetException;
import com.cws.esolutions.security.processors.impl.AccountControlProcessorImpl;
import com.cws.esolutions.security.processors.exception.AccountControlException;
import com.cws.esolutions.security.processors.interfaces.IAccountResetProcessor;
import com.cws.esolutions.security.processors.interfaces.IAccountControlProcessor;
/**
 * eSolutions_java_source
 * com.cws.us.esolutions.controllers
 * UserManagementController.java
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
@RequestMapping("/user-management")
public class UserManagementController
{
    private String resetURL = null;
    private int recordsPerPage = 20; // default to 20
    private String serviceId = null;
    private String serviceName = null;
    private String viewUserPage = null;
    private String messageSource = null;
    private String viewAuditPage = null;
    private String userResetEmail = null;
    private String createUserPage = null;
    private String searchUsersPage = null;
    private SecurityConfig secConfig = null;
    private String messageNoUsersFound = null;
    private String messageResetComplete = null;
    private String passwordResetSubject = null;
    private String messageAccountCreated = null;
    private String messageAccountSuspended = null;
    private UserAccountValidator validator = null;
    private ApplicationServiceBean appConfig = null;
    private String messageRoleChangedSuccessfully = null;

    private static final String CNAME = UserManagementController.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CNAME);

    public final void setServiceId(final String value)
    {
        final String methodName = UserManagementController.CNAME + "#setServiceId(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serviceId = value;
    }

    public final void setValidator(final UserAccountValidator value)
    {
        final String methodName = UserManagementController.CNAME + "#setValidator(final UserAccountValidator value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.validator = value;
    }

    public final void setServiceName(final String value)
    {
        final String methodName = UserManagementController.CNAME + "#setServiceName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serviceName = value;
    }

    public final void setRecordsPerPage(final int value)
    {
        final String methodName = UserManagementController.CNAME + "#setRecordsPerPage(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.recordsPerPage = value;
    }

    public final void setViewUserPage(final String value)
    {
        final String methodName = UserManagementController.CNAME + "#setViewUserPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.viewUserPage = value;
    }

    public final void setViewAuditPage(final String value)
    {
        final String methodName = UserManagementController.CNAME + "#setViewAuditPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.viewAuditPage = value;
    }

    public final void setCreateUserPage(final String value)
    {
        final String methodName = UserManagementController.CNAME + "#setCreateUserPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.createUserPage = value;
    }

    public final void setSearchUsersPage(final String value)
    {
        final String methodName = UserManagementController.CNAME + "#setSearchUsersPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.searchUsersPage = value;
    }

    public final void setMessageSource(final String value)
    {
        final String methodName = UserManagementController.CNAME + "#setMessageSource(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageSource = value;
    }

    public final void setMessageNoUsersFound(final String value)
    {
        final String methodName = UserManagementController.CNAME + "#setMessageNoUsersFound(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageNoUsersFound = value;
    }

    public final void setMessageAccountCreated(final String value)
    {
        final String methodName = UserManagementController.CNAME + "#setMessageAccountCreated(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageAccountCreated = value;
    }

    public final void setMessageResetComplete(final String value)
    {
        final String methodName = UserManagementController.CNAME + "#setMessageResetComplete(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageResetComplete = value;
    }

    public final void setMessageAccountSuspended(final String value)
    {
        final String methodName = UserManagementController.CNAME + "#setMessageAccountSuspended(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageAccountSuspended = value;
    }

    public final void setMessageRoleChangedSuccessfully(final String value)
    {
        final String methodName = UserManagementController.CNAME + "#setMessageRoleChangedSuccessfully(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageRoleChangedSuccessfully = value;
    }

    public final void setAppConfig(final ApplicationServiceBean value)
    {
        final String methodName = UserManagementController.CNAME + "#setAppConfig(final CoreServiceBean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.appConfig = value;
    }

    public final void setSecConfig(final SecurityConfig value)
    {
        final String methodName = UserManagementController.CNAME + "#setSecConfig(final SecurityConfig value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.secConfig = value;
    }

    public final void setResetURL(final String value)
    {
        final String methodName = UserManagementController.CNAME + "#setResetURL(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.resetURL = value;
    }

    public final void setUserResetEmail(final String value)
    {
        final String methodName = UserManagementController.CNAME + "#setUserResetEmail(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.userResetEmail = value;
    }

    public final void setPasswordResetSubject(final String value)
    {
        final String methodName = UserManagementController.CNAME + "#setPasswordResetSubject(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.passwordResetSubject = value;
    }

    @RequestMapping(value = "/default", method = RequestMethod.GET)
    public final ModelAndView showDefaultPage()
    {
        final String methodName = UserManagementController.CNAME + "#showDefaultPage()";

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
            @SuppressWarnings("unchecked") Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked") Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }

            DEBUGGER.debug("Dumping request parameters:");
            @SuppressWarnings("unchecked") Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String requestElement = paramsEnumeration.nextElement();
                Object requestValue = hRequest.getParameter(requestElement);

                DEBUGGER.debug("Parameter: " + requestElement + "; Value: " + requestValue);
            }
        }

        if (appConfig.getServices().get(this.serviceName))
        {
            mView.addObject("command", new UserAccount());
            mView.setViewName(this.searchUsersPage);
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

    @RequestMapping(value = "/add-user", method = RequestMethod.GET)
    public final ModelAndView showAddUser()
    {
        final String methodName = UserManagementController.CNAME + "#showAddUser()";

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
            @SuppressWarnings("unchecked") Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked") Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }

            DEBUGGER.debug("Dumping request parameters:");
            @SuppressWarnings("unchecked") Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String requestElement = paramsEnumeration.nextElement();
                Object requestValue = hRequest.getParameter(requestElement);

                DEBUGGER.debug("Parameter: " + requestElement + "; Value: " + requestValue);
            }
        }

        if (appConfig.getServices().get(this.serviceName))
        {
            mView.addObject("roles", Role.values());
            mView.addObject("command", new UserAccount());
            mView.setViewName(this.createUserPage); 
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

    @RequestMapping(value = "/view/account/{userGuid}", method = RequestMethod.GET)
    public final ModelAndView showAccountData(@PathVariable("userGuid") final String userGuid)
    {
        final String methodName = UserManagementController.CNAME + "#showAccountData(@PathVariable(\"userGuid\") final String userGuid)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("userGuid: {}", userGuid);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IAccountControlProcessor acctController = new AccountControlProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            @SuppressWarnings("unchecked") Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked") Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }

            DEBUGGER.debug("Dumping request parameters:");
            @SuppressWarnings("unchecked") Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String requestElement = paramsEnumeration.nextElement();
                Object requestValue = hRequest.getParameter(requestElement);

                DEBUGGER.debug("Parameter: " + requestElement + "; Value: " + requestValue);
            }
        }

        if (appConfig.getServices().get(this.serviceName))
        {
            try
            {
                // ensure authenticated access
                RequestHostInfo reqInfo = new RequestHostInfo();
                reqInfo.setHostAddress(hRequest.getRemoteAddr());
                reqInfo.setHostName(hRequest.getRemoteHost());
                reqInfo.setSessionId(hSession.getId());

                if (DEBUG)
                {
                    DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
                }

                UserAccount searchAccount = new UserAccount();
                searchAccount.setGuid(userGuid);

                if (DEBUG)
                {
                    DEBUGGER.debug("UserAccount: {}", searchAccount);
                }

                AccountControlRequest request = new AccountControlRequest();
                request.setHostInfo(reqInfo);
                request.setUserAccount(searchAccount);
                request.setApplicationId(appConfig.getApplicationId());
                request.setRequestor(userAccount);
                request.setServiceId(this.serviceId);
                request.setApplicationId(appConfig.getApplicationId());
                request.setApplicationName(appConfig.getApplicationName());

                if (DEBUG)
                {
                    DEBUGGER.debug("AccountControlRequest: {}", request);
                }

                AccountControlResponse response = acctController.loadUserAccount(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("AccountControlResponse: {}", response);
                }

                if (response.getRequestStatus() == SecurityRequestStatus.SUCCESS)
                {
                    if (response.getUserAccount() != null)
                    {
                        mView.addObject("userRoles", Role.values());
                        mView.addObject("userAccount", response.getUserAccount());
                        mView.setViewName(this.viewUserPage);
                    }
                    else
                    {
                        mView.addObject(Constants.ERROR_MESSAGE, this.messageNoUsersFound);
                        mView.setViewName(this.searchUsersPage);
                    }
                }
                else if (response.getRequestStatus() == SecurityRequestStatus.UNAUTHORIZED)
                {
                    mView.setViewName(appConfig.getUnauthorizedPage());
                }
                else
                {
                    mView.addObject(Constants.ERROR_RESPONSE, response.getResponse());
                    mView.setViewName(this.searchUsersPage);
                }
            }
            catch (AccountControlException acx)
            {
                ERROR_RECORDER.error(acx.getMessage(), acx);

                mView.setViewName(appConfig.getErrorResponsePage());
            }
        }
        else
        {
            mView.setViewName(appConfig.getUnavailablePage());
        }

        return mView;
    }

    @RequestMapping(value = "/audit/account/{userGuid}", method = RequestMethod.GET)
    public final ModelAndView showAuditData(@PathVariable("userGuid") final String userGuid)
    {
        final String methodName = UserManagementController.CNAME + "#showAuditData(@PathVariable(\"userGuid\") final String userGuid)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userGuid);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IAccountControlProcessor acctController = new AccountControlProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            @SuppressWarnings("unchecked") Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked") Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }

            DEBUGGER.debug("Dumping request parameters:");
            @SuppressWarnings("unchecked") Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String requestElement = paramsEnumeration.nextElement();
                Object requestValue = hRequest.getParameter(requestElement);

                DEBUGGER.debug("Parameter: " + requestElement + "; Value: " + requestValue);
            }
        }

        if (appConfig.getServices().get(this.serviceName))
        {
            try
            {
                RequestHostInfo reqInfo = new RequestHostInfo();
                reqInfo.setHostAddress(hRequest.getRemoteAddr());
                reqInfo.setHostName(hRequest.getRemoteHost());
                reqInfo.setSessionId(hSession.getId());

                if (DEBUG)
                {
                    DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
                }

                UserAccount searchAccount = new UserAccount();
                searchAccount.setGuid(userGuid);

                if (DEBUG)
                {
                    DEBUGGER.debug("UserAccount: {}", searchAccount);
                }

                AccountControlRequest request = new AccountControlRequest();
                request.setHostInfo(reqInfo);
                request.setUserAccount(searchAccount);
                request.setApplicationId(appConfig.getApplicationId());
                request.setRequestor(userAccount);
                request.setServiceId(this.serviceId);
                request.setApplicationId(appConfig.getApplicationId());
                request.setApplicationName(appConfig.getApplicationName());

                if (DEBUG)
                {
                    DEBUGGER.debug("AccountControlRequest: {}", request);
                }

                AccountControlResponse response = acctController.loadUserAudit(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("AccountControlResponse: {}", response);
                }

                if (response.getRequestStatus() == SecurityRequestStatus.SUCCESS)
                {
                    List<AuditEntry> auditEntries = response.getAuditEntries();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("List<AuditEntry>: {}", auditEntries);
                    }

                    if ((auditEntries != null) && (auditEntries.size() != 0))
                    {
                        mView.addObject("pages", (int) Math.ceil(response.getEntryCount() * 1.0 / recordsPerPage));
                        mView.addObject("page", 1);
                        mView.addObject("auditEntries", auditEntries);
                        mView.addObject("userAccount", searchAccount);
                        mView.setViewName(this.viewAuditPage);
                    }
                    else
                    {
                        mView.addObject(Constants.ERROR_RESPONSE, response.getResponse());
                        mView.setViewName(this.viewAuditPage);
                    }
                }
                else if (response.getRequestStatus() == SecurityRequestStatus.UNAUTHORIZED)
                {
                    mView.setViewName(appConfig.getUnauthorizedPage());
                }
                else
                {
                    mView.addObject(Constants.ERROR_RESPONSE, response.getResponse());
                    mView.setViewName(this.viewAuditPage);
                }
            }
            catch (AccountControlException acx)
            {
                ERROR_RECORDER.error(acx.getMessage(), acx);

                mView.setViewName(appConfig.getErrorResponsePage());
            }
        }
        else
        {
            mView.setViewName(appConfig.getUnavailablePage());
        }

        return mView;
    }

    @RequestMapping(value = "/audit/account/{userGuid}/page/{page}", method = RequestMethod.GET)
    public final ModelAndView showAuditData(@PathVariable("userGuid") final String userGuid, @PathVariable("page") final int page)
    {
        final String methodName = UserManagementController.CNAME + "#showAuditData(@PathVariable(\"userGuid\") final String userGuid, @PathVariable(\"page\") final int page)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userGuid);
            DEBUGGER.debug("Value: {}", page);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IAccountControlProcessor acctController = new AccountControlProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            @SuppressWarnings("unchecked") Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked") Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }

            DEBUGGER.debug("Dumping request parameters:");
            @SuppressWarnings("unchecked") Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String requestElement = paramsEnumeration.nextElement();
                Object requestValue = hRequest.getParameter(requestElement);

                DEBUGGER.debug("Parameter: " + requestElement + "; Value: " + requestValue);
            }
        }

        if (appConfig.getServices().get(this.serviceName))
        {
            try
            {
                RequestHostInfo reqInfo = new RequestHostInfo();
                reqInfo.setHostAddress(hRequest.getRemoteAddr());
                reqInfo.setHostName(hRequest.getRemoteHost());
                reqInfo.setSessionId(hSession.getId());

                if (DEBUG)
                {
                    DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
                }

                UserAccount searchAccount = new UserAccount();
                searchAccount.setGuid(userGuid);

                if (DEBUG)
                {
                    DEBUGGER.debug("UserAccount: {}", searchAccount);
                }

                AccountControlRequest request = new AccountControlRequest();
                request.setHostInfo(reqInfo);
                request.setUserAccount(searchAccount);
                request.setApplicationId(appConfig.getApplicationId());
                request.setRequestor(userAccount);
                request.setServiceId(this.serviceId);
                request.setApplicationId(appConfig.getApplicationId());
                request.setApplicationName(appConfig.getApplicationName());
                request.setStartPage((page-1) * recordsPerPage);

                if (DEBUG)
                {
                    DEBUGGER.debug("AccountControlRequest: {}", request);
                }

                AccountControlResponse response = acctController.loadUserAudit(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("AccountControlResponse: {}", response);
                }

                if (response.getRequestStatus() == SecurityRequestStatus.SUCCESS)
                {
                    List<AuditEntry> auditEntries = response.getAuditEntries();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("List<AuditEntry>: {}", auditEntries);
                    }

                    if ((auditEntries != null) && (auditEntries.size() != 0))
                    {
                        mView.addObject("pages", (int) Math.ceil(response.getEntryCount() * 1.0 / recordsPerPage));
                        mView.addObject("page", page);
                        mView.addObject("auditEntries", auditEntries);
                        mView.addObject("userAccount", searchAccount);
                        mView.setViewName(this.viewAuditPage);
                    }
                    else
                    {
                        mView.addObject(Constants.ERROR_RESPONSE, response.getResponse());
                        mView.setViewName(this.viewAuditPage);
                    }
                }
                else if (response.getRequestStatus() == SecurityRequestStatus.UNAUTHORIZED)
                {
                    mView.setViewName(appConfig.getUnauthorizedPage());
                }
                else
                {
                    mView.addObject(Constants.ERROR_RESPONSE, response.getResponse());
                    mView.setViewName(this.searchUsersPage);
                }
            }
            catch (AccountControlException acx)
            {
                ERROR_RECORDER.error(acx.getMessage(), acx);

                mView.setViewName(appConfig.getErrorResponsePage());
            }
        }
        else
        {
            mView.setViewName(appConfig.getUnavailablePage());
        }

        return mView;
    }

    @RequestMapping(value = "/lock/account/{userGuid}", method = RequestMethod.GET)
    public final ModelAndView lockUserAccount(@PathVariable("userGuid") final String userGuid)
    {
        final String methodName = UserManagementController.CNAME + "#lockUserAccount(@PathVariable(\"userGuid\") final String userGuid)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userGuid);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IAccountControlProcessor acctController = new AccountControlProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            @SuppressWarnings("unchecked") Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked") Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }

            DEBUGGER.debug("Dumping request parameters:");
            @SuppressWarnings("unchecked") Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String requestElement = paramsEnumeration.nextElement();
                Object requestValue = hRequest.getParameter(requestElement);

                DEBUGGER.debug("Parameter: " + requestElement + "; Value: " + requestValue);
            }
        }

        if (appConfig.getServices().get(this.serviceName))
        {
            try
            {
                RequestHostInfo reqInfo = new RequestHostInfo();
                reqInfo.setHostAddress(hRequest.getRemoteAddr());
                reqInfo.setHostName(hRequest.getRemoteHost());
                reqInfo.setSessionId(hSession.getId());

                if (DEBUG)
                {
                    DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
                }

                UserAccount account = new UserAccount();
                account.setGuid(userGuid);
                account.setFailedCount(3);

                if (DEBUG)
                {
                    DEBUGGER.debug("UserAccount: {}", account);
                }

                AccountControlRequest request = new AccountControlRequest();
                request.setHostInfo(reqInfo);
                request.setUserAccount(account);
                request.setApplicationName(appConfig.getApplicationName());
                request.setApplicationId(appConfig.getApplicationId());
                request.setControlType(ControlType.SUSPEND);
                request.setModType(ModificationType.NONE);
                request.setRequestor(userAccount);
                request.setIsLogonRequest(false);
                request.setServiceId(this.serviceId);

                if (DEBUG)
                {
                    DEBUGGER.debug("AccountControlRequest: {}", request);
                }

                AccountControlResponse response = acctController.modifyUserLockout(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("AccountControlResponse: {}", response);
                }

                if (response.getRequestStatus() == SecurityRequestStatus.SUCCESS)
                {
                    mView.addObject(Constants.MESSAGE_RESPONSE, response.getResponse());
                    mView.setViewName(this.searchUsersPage);
                }
                else if (response.getRequestStatus() == SecurityRequestStatus.UNAUTHORIZED)
                {
                    mView.setViewName(appConfig.getUnauthorizedPage());
                }
                else
                {
                    mView.addObject(Constants.ERROR_RESPONSE, response.getResponse());
                    mView.setViewName(this.searchUsersPage);
                }
            }
            catch (AccountControlException acx)
            {
                ERROR_RECORDER.error(acx.getMessage(), acx);

                mView.setViewName(appConfig.getErrorResponsePage());
            }
        }
        else
        {
            mView.setViewName(appConfig.getUnavailablePage());
        }

        return mView;
    }

    @RequestMapping(value = "/unlock/account/{userGuid}", method = RequestMethod.GET)
    public final ModelAndView unlockUserAccount(@PathVariable("userGuid") final String userGuid)
    {
        final String methodName = UserManagementController.CNAME + "#unlockUserAccount(@PathVariable(\"userGuid\") final String userGuid)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userGuid);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IAccountControlProcessor acctController = new AccountControlProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            @SuppressWarnings("unchecked") Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked") Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }

            DEBUGGER.debug("Dumping request parameters:");
            @SuppressWarnings("unchecked") Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String requestElement = paramsEnumeration.nextElement();
                Object requestValue = hRequest.getParameter(requestElement);

                DEBUGGER.debug("Parameter: " + requestElement + "; Value: " + requestValue);
            }
        }

        if (appConfig.getServices().get(this.serviceName))
        {
            try
            {
                RequestHostInfo reqInfo = new RequestHostInfo();
                reqInfo.setHostAddress(hRequest.getRemoteAddr());
                reqInfo.setHostName(hRequest.getRemoteHost());
                reqInfo.setSessionId(hSession.getId());

                if (DEBUG)
                {
                    DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
                }

                UserAccount account = new UserAccount();
                account.setGuid(userGuid);
                account.setFailedCount(0);

                if (DEBUG)
                {
                    DEBUGGER.debug("UserAccount: {}", account);
                }

                AccountControlRequest request = new AccountControlRequest();
                request.setHostInfo(reqInfo);
                request.setUserAccount(account);
                request.setApplicationName(appConfig.getApplicationName());
                request.setApplicationId(appConfig.getApplicationId());
                request.setControlType(ControlType.SUSPEND);
                request.setModType(ModificationType.NONE);
                request.setRequestor(userAccount);
                request.setIsLogonRequest(false);
                request.setServiceId(this.serviceId);

                if (DEBUG)
                {
                    DEBUGGER.debug("AccountControlRequest: {}", request);
                }

                AccountControlResponse response = acctController.modifyUserLockout(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("AccountControlResponse: {}", response);
                }

                if (response.getRequestStatus() == SecurityRequestStatus.SUCCESS)
                {
                    mView.addObject(Constants.MESSAGE_RESPONSE, response.getResponse());
                    mView.setViewName(this.searchUsersPage);
                }
                else if (response.getRequestStatus() == SecurityRequestStatus.UNAUTHORIZED)
                {
                    mView.setViewName(appConfig.getUnauthorizedPage());
                }
                else
                {
                    mView.addObject(Constants.ERROR_RESPONSE, response.getResponse());
                    mView.setViewName(this.searchUsersPage);
                }
            }
            catch (AccountControlException acx)
            {
                ERROR_RECORDER.error(acx.getMessage(), acx);

                mView.setViewName(appConfig.getErrorResponsePage());
            }
        }
        else
        {
            mView.setViewName(appConfig.getUnavailablePage());
        }

        return mView;
    }

    @RequestMapping(value = "/suspend/account/{userGuid}", method = RequestMethod.GET)
    public final ModelAndView suspendUserAccount(@PathVariable("userGuid") final String userGuid)
    {
        final String methodName = UserManagementController.CNAME + "#suspendUserAccount(@PathVariable(\"userGuid\") final String userGuid)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userGuid);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IAccountControlProcessor acctController = new AccountControlProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            @SuppressWarnings("unchecked") Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked") Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }

            DEBUGGER.debug("Dumping request parameters:");
            @SuppressWarnings("unchecked") Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String requestElement = paramsEnumeration.nextElement();
                Object requestValue = hRequest.getParameter(requestElement);

                DEBUGGER.debug("Parameter: " + requestElement + "; Value: " + requestValue);
            }
        }

        if (appConfig.getServices().get(this.serviceName))
        {
            try
            {
                RequestHostInfo reqInfo = new RequestHostInfo();
                reqInfo.setHostAddress(hRequest.getRemoteAddr());
                reqInfo.setHostName(hRequest.getRemoteHost());
                reqInfo.setSessionId(hSession.getId());

                if (DEBUG)
                {
                    DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
                }

                UserAccount account = new UserAccount();
                account.setGuid(userGuid);
                account.setSuspended(true);

                if (DEBUG)
                {
                    DEBUGGER.debug("UserAccount: {}", account);
                }

                AccountControlRequest request = new AccountControlRequest();
                request.setHostInfo(reqInfo);
                request.setUserAccount(account);
                request.setApplicationName(appConfig.getApplicationName());
                request.setApplicationId(appConfig.getApplicationId());
                request.setControlType(ControlType.SUSPEND);
                request.setModType(ModificationType.NONE);
                request.setRequestor(userAccount);
                request.setIsLogonRequest(false);
                request.setServiceId(this.serviceId);

                if (DEBUG)
                {
                    DEBUGGER.debug("AccountControlRequest: {}", request);
                }

                AccountControlResponse response = acctController.modifyUserSuspension(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("AccountControlResponse: {}", response);
                }

                if (response.getRequestStatus() == SecurityRequestStatus.SUCCESS)
                {
                    mView.addObject(Constants.MESSAGE_RESPONSE, response.getResponse());
                    mView.setViewName(this.searchUsersPage);
                }
                else if (response.getRequestStatus() == SecurityRequestStatus.UNAUTHORIZED)
                {
                    mView.setViewName(appConfig.getUnauthorizedPage());
                }
                else
                {
                    mView.addObject(Constants.ERROR_RESPONSE, response.getResponse());
                    mView.setViewName(this.searchUsersPage);
                }
            }
            catch (AccountControlException acx)
            {
                ERROR_RECORDER.error(acx.getMessage(), acx);

                mView.setViewName(appConfig.getErrorResponsePage());
            }
        }
        else
        {
            mView.setViewName(appConfig.getUnavailablePage());
        }

        return mView;
    }

    @RequestMapping(value = "/unsuspend/account/{userGuid}", method = RequestMethod.GET)
    public final ModelAndView unsuspendUserAccount(@PathVariable("userGuid") final String userGuid)
    {
        final String methodName = UserManagementController.CNAME + "#unsuspendUserAccount(@PathVariable(\"userGuid\") final String userGuid)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userGuid);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IAccountControlProcessor acctController = new AccountControlProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            @SuppressWarnings("unchecked") Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked") Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }

            DEBUGGER.debug("Dumping request parameters:");
            @SuppressWarnings("unchecked") Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String requestElement = paramsEnumeration.nextElement();
                Object requestValue = hRequest.getParameter(requestElement);

                DEBUGGER.debug("Parameter: " + requestElement + "; Value: " + requestValue);
            }
        }

        if (appConfig.getServices().get(this.serviceName))
        {
            try
            {
                RequestHostInfo reqInfo = new RequestHostInfo();
                reqInfo.setHostAddress(hRequest.getRemoteAddr());
                reqInfo.setHostName(hRequest.getRemoteHost());
                reqInfo.setSessionId(hSession.getId());

                if (DEBUG)
                {
                    DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
                }

                UserAccount account = new UserAccount();
                account.setGuid(userGuid);
                account.setSuspended(false);

                if (DEBUG)
                {
                    DEBUGGER.debug("UserAccount: {}", account);
                }

                AccountControlRequest request = new AccountControlRequest();
                request.setHostInfo(reqInfo);
                request.setUserAccount(account);
                request.setApplicationName(appConfig.getApplicationName());
                request.setApplicationId(appConfig.getApplicationId());
                request.setControlType(ControlType.SUSPEND);
                request.setModType(ModificationType.NONE);
                request.setRequestor(userAccount);
                request.setIsLogonRequest(false);
                request.setServiceId(this.serviceId);

                if (DEBUG)
                {
                    DEBUGGER.debug("AccountControlRequest: {}", request);
                }

                AccountControlResponse response = acctController.modifyUserSuspension(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("AccountControlResponse: {}", response);
                }

                if (response.getRequestStatus() == SecurityRequestStatus.SUCCESS)
                {
                    mView.addObject(Constants.MESSAGE_RESPONSE, response.getResponse());
                    mView.setViewName(this.searchUsersPage);
                }
                else if (response.getRequestStatus() == SecurityRequestStatus.UNAUTHORIZED)
                {
                    mView.setViewName(appConfig.getUnauthorizedPage());
                }
                else
                {
                    mView.addObject(Constants.ERROR_RESPONSE, response.getResponse());
                    mView.setViewName(this.searchUsersPage);
                }
            }
            catch (AccountControlException acx)
            {
                ERROR_RECORDER.error(acx.getMessage(), acx);

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

    @RequestMapping(value = "/reset/account/{userGuid}", method = RequestMethod.GET)
    public final ModelAndView resetUserAccount(@PathVariable("userGuid") final String userGuid)
    {
        final String methodName = UserManagementController.CNAME + "#resetUserAccount(@PathVariable(\"userGuid\") final String userGuid)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userGuid);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IAccountResetProcessor processor = new AccountResetProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            @SuppressWarnings("unchecked") Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked") Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }

            DEBUGGER.debug("Dumping request parameters:");
            @SuppressWarnings("unchecked") Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String requestElement = paramsEnumeration.nextElement();
                Object requestValue = hRequest.getParameter(requestElement);

                DEBUGGER.debug("Parameter: " + requestElement + "; Value: " + requestValue);
            }
        }

        if (appConfig.getServices().get(this.serviceName))
        {
            try
            {
                RequestHostInfo reqInfo = new RequestHostInfo();
                reqInfo.setHostAddress(hRequest.getRemoteAddr());
                reqInfo.setHostName(hRequest.getRemoteHost());
                reqInfo.setSessionId(hSession.getId());

                if (DEBUG)
                {
                    DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
                }

                UserAccount account = new UserAccount();
                account.setGuid(userGuid);
                account.setSuspended(false);

                if (DEBUG)
                {
                    DEBUGGER.debug("UserAccount: {}", account);
                }

                AccountResetRequest request = new AccountResetRequest();
                request.setHostInfo(reqInfo);
                request.setUserAccount(account);
                request.setApplicationName(appConfig.getApplicationName());
                request.setApplicationId(appConfig.getApplicationId());
                request.setAlgorithm(secConfig.getAuthAlgorithm());
                request.setRequestor(userAccount);

                if (DEBUG)
                {
                    DEBUGGER.debug("AccountResetRequest: {}", request);
                }

                AccountResetResponse response = processor.resetUserPassword(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("AccountResetResponse: {}", response);
                }

                if (response.getRequestStatus() == SecurityRequestStatus.SUCCESS)
                {
                    // good, send email
                    UserAccount responseAccount = response.getUserAccount();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("UserAccount: {}", responseAccount);
                    }

                    String emailId = RandomStringUtils.randomAlphanumeric(16);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Message ID: {}", emailId);
                    }

                    StringBuilder targetURL = new StringBuilder()
                        .append(hRequest.getScheme() + "://" + hRequest.getServerName())
                        .append((hRequest.getServerPort() == 443) ? null : ":" + hRequest.getServerPort())
                        .append(hRequest.getContextPath() + this.resetURL + response.getResetId());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("targetURL: {}", targetURL);
                    }
                            
                    String emailBody = MessageFormat.format(IOUtils.toString(
                            this.getClass().getClassLoader().getResourceAsStream(this.userResetEmail)), new Object[]
                        {
                            responseAccount.getGivenName(),
                            new Date(System.currentTimeMillis()),
                            reqInfo.getHostName(),
                            targetURL.toString(),
                            secConfig.getPasswordMinLength(),
                            secConfig.getPasswordMaxLength()
                        });

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Email body: {}", emailBody);
                    }

                    // good, now generate an email with the information
                    EmailMessage emailMessage = new EmailMessage();
                    emailMessage.setIsAlert(true); // set this to alert so it shows as high priority
                    emailMessage.setMessageBody(emailBody);
                    emailMessage.setMessageId(RandomStringUtils.randomAlphanumeric(16));
                    emailMessage.setMessageSubject("[ " + emailId + " ] - " + ResourceController.returnSystemPropertyValue(this.messageSource,
                            this.passwordResetSubject, this.getClass().getClassLoader()));
                    emailMessage.setMessageFrom(new ArrayList<String>(Arrays.asList(appConfig.getSecEmailAddr())));
                    emailMessage.setMessageTo(new ArrayList<String>(Arrays.asList(responseAccount.getEmailAddr())));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("EmailMessage: {}", emailMessage);
                    }

                    EmailUtils.sendEmailMessage(emailMessage);

                    mView.addObject(Constants.RESPONSE_MESSAGE, this.messageResetComplete);
                    mView.addObject(Constants.USER_ACCOUNT, response.getUserAccount());
                    mView.setViewName(this.viewUserPage);
                }
                else if (response.getRequestStatus() == SecurityRequestStatus.UNAUTHORIZED)
                {
                    mView.setViewName(appConfig.getUnauthorizedPage());
                }
                else
                {
                    // some failure occurred
                    ERROR_RECORDER.error(response.getResponse());

                    mView.addObject(Constants.ERROR_RESPONSE, response.getResponse());
                    mView.addObject("userAccount", response.getUserAccount());
                    mView.setViewName(this.viewUserPage);
                }
            }
            catch (AccountResetException arx)
            {
                ERROR_RECORDER.error(arx.getMessage(), arx);

                mView.setViewName(appConfig.getErrorResponsePage());
            }
            catch (IOException iox)
            {
                ERROR_RECORDER.error(iox.getMessage(), iox);

                mView.setViewName(appConfig.getUnauthorizedPage());
            }
            catch (CoreServiceException csx)
            {
                ERROR_RECORDER.error(csx.getMessage(), csx);

                mView.setViewName(appConfig.getUnauthorizedPage());
            }
            catch (MessagingException mx)
            {
                ERROR_RECORDER.error(mx.getMessage(), mx);

                mView.setViewName(appConfig.getUnauthorizedPage());
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

    @RequestMapping(value = "/change-role/account/{userGuid}/role/{role}", method = RequestMethod.GET)
    public final ModelAndView changeUserRole(@PathVariable("userGuid") final String userGuid, @PathVariable("role") final String role)
    {
        final String methodName = UserManagementController.CNAME + "#changeUserRole(@PathVariable(\"userGuid\") final String userGuid, @PathVariable(\"role\") final String role)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", userGuid);
            DEBUGGER.debug("Value: {}", role);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IAccountControlProcessor acctController = new AccountControlProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            @SuppressWarnings("unchecked") Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked") Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }

            DEBUGGER.debug("Dumping request parameters:");
            @SuppressWarnings("unchecked") Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String requestElement = paramsEnumeration.nextElement();
                Object requestValue = hRequest.getParameter(requestElement);

                DEBUGGER.debug("Parameter: " + requestElement + "; Value: " + requestValue);
            }
        }

        if (appConfig.getServices().get(this.serviceName))
        {
            try
            {
                RequestHostInfo reqInfo = new RequestHostInfo();
                reqInfo.setHostAddress(hRequest.getRemoteAddr());
                reqInfo.setHostName(hRequest.getRemoteHost());
                reqInfo.setSessionId(hSession.getId());

                if (DEBUG)
                {
                    DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
                }

                UserAccount account = new UserAccount();
                account.setGuid(userGuid);
                account.setRole(Role.valueOf(role));

                if (DEBUG)
                {
                    DEBUGGER.debug("UserAccount: {}", account);
                }

                AccountControlRequest request = new AccountControlRequest();
                request.setHostInfo(reqInfo);
                request.setUserAccount(account);
                request.setApplicationId(appConfig.getApplicationId());
                request.setRequestor(userAccount);
                request.setServiceId(this.serviceId);
                request.setApplicationId(appConfig.getApplicationId());
                request.setApplicationName(appConfig.getApplicationName());
                request.setModType(ModificationType.ROLE);
                request.setControlType(ControlType.MODIFY);

                if (DEBUG)
                {
                    DEBUGGER.debug("AccountControlRequest: {}", request);
                }

                AccountControlResponse response = acctController.modifyUserRole(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("AccountControlResponse: {}", response);
                }

                if (response.getRequestStatus() == SecurityRequestStatus.SUCCESS)
                {
                    mView.addObject("userRoles", Role.values());
                    mView.addObject("userAccount", response.getUserAccount());
                    mView.addObject(Constants.RESPONSE_MESSAGE, this.messageRoleChangedSuccessfully);
                    mView.setViewName(this.viewUserPage);
                }
                else if (response.getRequestStatus() == SecurityRequestStatus.UNAUTHORIZED)
                {
                    mView.setViewName(appConfig.getUnauthorizedPage());
                }
                else
                {
                    mView.addObject(Constants.ERROR_RESPONSE, response.getResponse());
                    mView.setViewName(this.searchUsersPage);
                }
            }
            catch (AccountControlException acx)
            {
                ERROR_RECORDER.error(acx.getMessage(), acx);

                mView.setViewName(appConfig.getErrorResponsePage());
            }
        }
        else
        {
            mView.setViewName(appConfig.getUnavailablePage());
        }

        return mView;
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public final ModelAndView doSearchUsers(@ModelAttribute("user") final UserAccount user, final BindingResult bindResult)
    {
        final String methodName = UserManagementController.CNAME + "#doSearchUsers(@ModelAttribute(\"user\") final UserAccount user, final BindingResult bindResult)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", user);
            DEBUGGER.debug("Value: {}", bindResult);
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
            @SuppressWarnings("unchecked") Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked") Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }

            DEBUGGER.debug("Dumping request parameters:");
            @SuppressWarnings("unchecked") Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String requestElement = paramsEnumeration.nextElement();
                Object requestValue = hRequest.getParameter(requestElement);

                DEBUGGER.debug("Parameter: " + requestElement + "; Value: " + requestValue);
            }
        }

        if (appConfig.getServices().get(this.serviceName))
        {
            try
            {
                RequestHostInfo hostInfo = new RequestHostInfo();
                hostInfo.setHostAddress(hRequest.getRemoteAddr());
                hostInfo.setHostName(hRequest.getRemoteHost());

                if (DEBUG)
                {
                    DEBUGGER.debug("RequestHostInfo: {}", hostInfo);
                }

                UserAccount searchAccount = new UserAccount();
                searchAccount.setGuid((StringUtils.isNotEmpty(user.getGuid())) ? user.getGuid() : null);
                searchAccount.setDisplayName((StringUtils.isNotEmpty(user.getDisplayName())) ? user.getDisplayName() : null);
                searchAccount.setEmailAddr((StringUtils.isNotEmpty(user.getEmailAddr())) ? user.getEmailAddr() : null);
                searchAccount.setUsername((StringUtils.isNotEmpty(user.getUsername())) ? user.getUsername() : null);

                if (DEBUG)
                {
                    DEBUGGER.debug("UserAccount: {}", searchAccount);
                }

                // search accounts
                AccountControlRequest request = new AccountControlRequest();
                request.setHostInfo(hostInfo);
                request.setUserAccount(searchAccount);
                request.setApplicationId(appConfig.getApplicationName());
                request.setControlType(ControlType.LOOKUP);
                request.setModType(ModificationType.NONE);
                request.setSearchType(SearchRequestType.ALL);
                request.setRequestor(userAccount);
                request.setIsLogonRequest(false);
                request.setServiceId(this.serviceId);
                request.setApplicationId(appConfig.getApplicationId());
                request.setApplicationName(appConfig.getApplicationName());

                if (DEBUG)
                {
                    DEBUGGER.debug("AccountControlRequest: {}", request);
                }

                IAccountControlProcessor processor = new AccountControlProcessorImpl();
                AccountControlResponse response = processor.searchAccounts(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("AccountControlResponse: {}", response);
                }

                if (response.getRequestStatus() == SecurityRequestStatus.SUCCESS)
                {
                    if ((response.getUserList() != null) && (response.getUserList().size() != 0))
                    {
                        List<UserAccount> userList = response.getUserList();

                        if (DEBUG)
                        {
                            DEBUGGER.debug("List<UserAccount> {}", userList);
                        }

                        mView.addObject("command", new UserAccount());
                        mView.addObject("searchResults", userList);
                        mView.setViewName(this.searchUsersPage);
                    }
                    else
                    {
                        mView.addObject("command", new UserAccount());
                        mView.addObject(Constants.ERROR_RESPONSE, response.getResponse());
                    }
                }
                else if (response.getRequestStatus() == SecurityRequestStatus.UNAUTHORIZED)
                {
                    mView.setViewName(appConfig.getUnauthorizedPage());
                }
                else
                {
                    mView.addObject("command", new UserAccount());
                    mView.addObject(Constants.ERROR_RESPONSE, response.getResponse());
                }
            }
            catch (AccountControlException acx)
            {
                ERROR_RECORDER.error(acx.getMessage(), acx);

                mView.setViewName(appConfig.getErrorResponsePage());
            }
            
            mView.setViewName(this.searchUsersPage);
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

    @RequestMapping(value = "/add-user", method = RequestMethod.POST)
    public final ModelAndView doAddUser(@ModelAttribute("user") final UserAccount user, final BindingResult bindResult)
    {
        final String methodName = UserManagementController.CNAME + "#doAddUser(@ModelAttribute(\"user\") final UserAccount user, final BindingResult bindResult)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", user);
            DEBUGGER.debug("Value: {}", bindResult);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IAccountControlProcessor processor = new AccountControlProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            @SuppressWarnings("unchecked") Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked") Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }

            DEBUGGER.debug("Dumping request parameters:");
            @SuppressWarnings("unchecked") Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String requestElement = paramsEnumeration.nextElement();
                Object requestValue = hRequest.getParameter(requestElement);

                DEBUGGER.debug("Parameter: " + requestElement + "; Value: " + requestValue);
            }
        }

        if (appConfig.getServices().get(this.serviceName))
        {
            validator.validate(user, bindResult);

            if (bindResult.hasErrors())
            {
                mView.addObject("errors", bindResult.getAllErrors());
                mView.addObject("command", user);
                mView.setViewName(this.createUserPage);
            }

            try
            {
                RequestHostInfo hostInfo = new RequestHostInfo();
                hostInfo.setHostAddress(hRequest.getRemoteAddr());
                hostInfo.setHostName(hRequest.getRemoteHost());

                if (DEBUG)
                {
                    DEBUGGER.debug("RequestHostInfo: {}", hostInfo);
                }

                UserAccount newUser = new UserAccount();
                newUser.setGuid(user.getGuid());
                newUser.setSurname(user.getSurname());
                newUser.setExpiryDate(System.currentTimeMillis());
                newUser.setFailedCount(user.getFailedCount());
                newUser.setOlrLocked(false);
                newUser.setOlrSetup(true);
                newUser.setSuspended(user.isSuspended());
                newUser.setTcAccepted(false);
                newUser.setRole(user.getRole());
                newUser.setDisplayName(user.getGivenName() + " " + user.getSurname());
                newUser.setEmailAddr(user.getEmailAddr());
                newUser.setGivenName(user.getGivenName());
                newUser.setUsername(user.getUsername());

                if (DEBUG)
                {
                    DEBUGGER.debug("UserAccount: {}", newUser);
                }

                UserSecurity security = new UserSecurity();
                security.setPassword(RandomStringUtils.randomAlphanumeric(secConfig.getPasswordMaxLength()));
                security.setUserSalt(RandomStringUtils.randomAlphanumeric(secConfig.getSaltLength()));

                if (DEBUG)
                {
                    DEBUGGER.debug("UserSecurity: {}", security);
                }

                // search accounts
                AccountControlRequest request = new AccountControlRequest();
                request.setHostInfo(hostInfo);
                request.setUserAccount(newUser);
                request.setUserSecurity(security);
                request.setApplicationId(appConfig.getApplicationName());
                request.setAlgorithm(secConfig.getAuthAlgorithm());
                request.setControlType(ControlType.CREATE);
                request.setModType(ModificationType.NONE);
                request.setRequestor(userAccount);
                request.setIsLogonRequest(false);
                request.setServiceId(this.serviceId);
                request.setApplicationId(appConfig.getApplicationId());
                request.setApplicationName(appConfig.getApplicationName());

                if (DEBUG)
                {
                    DEBUGGER.debug("AccountControlRequest: {}", request);
                }

                AccountControlResponse response = processor.createNewUser(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("AccountControlResponse: {}", response);
                }

                if (response.getRequestStatus() == SecurityRequestStatus.SUCCESS)
                {
                    // account created
                    mView.addObject(Constants.RESPONSE_MESSAGE, this.messageAccountCreated);
                    mView.addObject("command", new UserAccount());
                    mView.setViewName(this.createUserPage);
                }
                else if (response.getRequestStatus() == SecurityRequestStatus.UNAUTHORIZED)
                {
                    mView.setViewName(appConfig.getUnauthorizedPage());
                }
                else
                {
                    mView.addObject("command", user);
                    mView.addObject(Constants.ERROR_RESPONSE, response.getResponse());
                    mView.setViewName(this.createUserPage);
                }
            }
            catch (AccountControlException acx)
            {
                ERROR_RECORDER.error(acx.getMessage(), acx);

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

    @RequestMapping(value = "/suspend-user", method = RequestMethod.POST)
    public final ModelAndView doSuspendUser(@ModelAttribute("user") final UserAccount user, final BindingResult bindResult)
    {
        final String methodName = UserManagementController.CNAME + "#doSuspendUser(@ModelAttribute(\"user\") final UserAccount user, final BindingResult bindResult)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", user);
            DEBUGGER.debug("Value: {}", bindResult);
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
            @SuppressWarnings("unchecked") Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            @SuppressWarnings("unchecked") Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }

            DEBUGGER.debug("Dumping request parameters:");
            @SuppressWarnings("unchecked") Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String requestElement = paramsEnumeration.nextElement();
                Object requestValue = hRequest.getParameter(requestElement);

                DEBUGGER.debug("Parameter: " + requestElement + "; Value: " + requestValue);
            }
        }

        if (appConfig.getServices().get(this.serviceName))
        {
            try
            {
                RequestHostInfo hostInfo = new RequestHostInfo();
                hostInfo.setHostAddress(hRequest.getRemoteAddr());
                hostInfo.setHostName(hRequest.getRemoteHost());

                if (DEBUG)
                {
                    DEBUGGER.debug("RequestHostInfo: {}", hostInfo);
                }

                // set the suspended flag
                user.setSuspended(true);

                if (DEBUG)
                {
                    DEBUGGER.debug("UserAccount: {}", user);
                }

                // search accounts
                AccountControlRequest request = new AccountControlRequest();
                request.setHostInfo(hostInfo);
                request.setUserAccount(user);
                request.setApplicationId(appConfig.getApplicationName());
                request.setAlgorithm(secConfig.getAuthAlgorithm());
                request.setControlType(ControlType.SUSPEND);
                request.setModType(ModificationType.NONE);
                request.setRequestor(userAccount);
                request.setIsLogonRequest(false);
                request.setServiceId(this.serviceId);
                request.setApplicationId(appConfig.getApplicationId());
                request.setApplicationName(appConfig.getApplicationName());

                if (DEBUG)
                {
                    DEBUGGER.debug("AccountControlRequest: {}", request);
                }

                IAccountControlProcessor processor = new AccountControlProcessorImpl();
                AccountControlResponse response = processor.modifyUserSuspension(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("AccountControlResponse: {}", response);
                }

                if (response.getRequestStatus() == SecurityRequestStatus.SUCCESS)
                {
                    // account suspended
                    mView.addObject(Constants.RESPONSE_MESSAGE, this.messageAccountSuspended);
                    mView.addObject("command", user);
                    mView.setViewName(this.viewUserPage);
                }
                else if (response.getRequestStatus() == SecurityRequestStatus.UNAUTHORIZED)
                {
                    mView.setViewName(appConfig.getUnauthorizedPage());
                }
                else
                {
                    mView.addObject("command", new UserAccount());
                    mView.addObject(Constants.ERROR_RESPONSE, response.getResponse());
                }
            }
            catch (AccountControlException acx)
            {
                ERROR_RECORDER.error(acx.getMessage(), acx);

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
