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
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.cws.us.esolutions.Constants;
import com.cws.esolutions.core.utils.EmailUtils;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.dto.UserSecurity;
import com.cws.us.esolutions.ApplicationServiceBean;
import com.cws.esolutions.security.config.SecurityConfig;
import com.cws.esolutions.core.processors.dto.EmailMessage;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.core.controllers.ResourceController;
import com.cws.esolutions.core.exception.CoreServiceException;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.processors.enums.ControlType;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.security.processors.enums.ModificationType;
import com.cws.esolutions.security.processors.dto.AccountResetRequest;
import com.cws.esolutions.security.processors.dto.AccountResetResponse;
import com.cws.esolutions.security.processors.dto.AccountControlRequest;
import com.cws.esolutions.security.dao.usermgmt.enums.SearchRequestType;
import com.cws.esolutions.security.processors.dto.AccountControlResponse;
import com.cws.esolutions.security.access.control.enums.AdminControlType;
import com.cws.esolutions.security.processors.impl.AccountResetProcessorImpl;
import com.cws.esolutions.security.processors.exception.AccountResetException;
import com.cws.esolutions.security.access.control.impl.UserControlServiceImpl;
import com.cws.esolutions.security.access.control.impl.AdminControlServiceImpl;
import com.cws.esolutions.security.processors.impl.AccountControlProcessorImpl;
import com.cws.esolutions.security.processors.exception.AccountControlException;
import com.cws.esolutions.security.processors.interfaces.IAccountResetProcessor;
import com.cws.esolutions.security.access.control.interfaces.IUserControlService;
import com.cws.esolutions.security.access.control.interfaces.IAdminControlService;
import com.cws.esolutions.security.processors.interfaces.IAccountControlProcessor;
import com.cws.esolutions.security.access.control.exception.UserControlServiceException;
import com.cws.esolutions.security.access.control.exception.AdminControlServiceException;
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
    private String serviceId = null;
    private String serviceName = null;
    private String editUserPage = null;
    private String viewUserPage = null;
    private String viewUsersPage = null;
    private String messageSource = null;
    private String userResetEmail = null;
    private String createUserPage = null;
    private String searchUsersPage = null;
    private SecurityConfig secConfig = null;
    private String messageNoUsersFound = null;
    private String passwordResetSubject = null;
    private String messagePasswordReset = null;
    private String messageAccountCreated = null;
    private String messageAccountSuspended = null;
    private ApplicationServiceBean appConfig = null;

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

    public final void setEditUserPage(final String value)
    {
        final String methodName = UserManagementController.CNAME + "#setEditUserPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.editUserPage = value;
    }

    public final void setViewUsersPage(final String value)
    {
        final String methodName = UserManagementController.CNAME + "#setViewUsersPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.viewUsersPage = value;
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

    public final void setMessagePasswordReset(final String value)
    {
        final String methodName = UserManagementController.CNAME + "#setMessagePasswordReset(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messagePasswordReset = value;
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
        final String methodName = UserManagementController.CNAME + "#setAppConfig(final SecurityConfig value)";

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
    public ModelAndView showDefaultPage()
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
                IUserControlService userControl = new UserControlServiceImpl();
                IAdminControlService adminControl = new AdminControlServiceImpl();

                boolean isUserAuthorized = userControl.isUserAuthorizedForService(userAccount.getGuid(), this.serviceId);
                boolean isAdminAuthorized = adminControl.adminControlService(userAccount, AdminControlType.SERVICE_ADMIN);

                if (DEBUG)
                {
                    DEBUGGER.debug("isUserAuthorized: {}", isUserAuthorized);
                    DEBUGGER.debug("isAdminAuthorized: {}", isAdminAuthorized);
                }

                if ((isUserAuthorized) && (isAdminAuthorized))
                {
                    mView.addObject("command", new UserAccount());
                    mView.setViewName(this.searchUsersPage);
                }
                else
                {
                    mView.setViewName(appConfig.getUnauthorizedPage());
                }
            }
            catch (UserControlServiceException ucsx)
            {
                ERROR_RECORDER.error(ucsx.getMessage(), ucsx);

                mView.setViewName(appConfig.getErrorResponsePage());
            }
            catch (AdminControlServiceException acsx)
            {
                ERROR_RECORDER.error(acsx.getMessage(), acsx);

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

    @RequestMapping(value = "/add-user", method = RequestMethod.GET)
    public ModelAndView showAddUser()
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
                IUserControlService userControl = new UserControlServiceImpl();
                IAdminControlService adminControl = new AdminControlServiceImpl();

                boolean isUserAuthorized = userControl.isUserAuthorizedForService(userAccount.getGuid(), this.serviceId);
                boolean isAdminAuthorized = adminControl.adminControlService(userAccount, AdminControlType.USER_ADMIN);

                if (DEBUG)
                {
                    DEBUGGER.debug("isUserAuthorized: {}", isUserAuthorized);
                    DEBUGGER.debug("isAdminAuthorized: {}", isAdminAuthorized);
                }

                if ((isUserAuthorized) && (isAdminAuthorized))
                {
                    mView.addObject("command", new UserAccount());
                    mView.setViewName(this.createUserPage);
                }
                else
                {
                    mView.setViewName(appConfig.getUnauthorizedPage());
                }
            }
            catch (AdminControlServiceException acsx)
            {
                ERROR_RECORDER.error(acsx.getMessage(), acsx);

                mView.setViewName(appConfig.getUnauthorizedPage());
            }
            catch (UserControlServiceException ucsx)
            {
                ERROR_RECORDER.error(ucsx.getMessage(), ucsx);

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

    @RequestMapping(value = "/account/{userGuid}", method = RequestMethod.GET)
    public ModelAndView showAccountData(@PathVariable("userGuid") final String userGuid)
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
                IUserControlService userControl = new UserControlServiceImpl();
                IAdminControlService adminControl = new AdminControlServiceImpl();

                boolean isUserAuthorized = userControl.isUserAuthorizedForService(userAccount.getGuid(), this.serviceId);
                boolean isAdminAuthorized = adminControl.adminControlService(userAccount, AdminControlType.USER_ADMIN);

                if (DEBUG)
                {
                    DEBUGGER.debug("isUserAuthorized: {}", isUserAuthorized);
                    DEBUGGER.debug("isAdminAuthorized: {}", isAdminAuthorized);
                }

                if ((isUserAuthorized) && (isAdminAuthorized))
                {
                    // ensure authenticated access
                    RequestHostInfo reqInfo = new RequestHostInfo();
                    reqInfo.setHostAddress(hRequest.getRemoteAddr());
                    reqInfo.setHostName(hRequest.getRemoteHost());

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
                    request.setAppName(appConfig.getApplicationName());
                    request.setApplicationId(appConfig.getApplicationId());
                    request.setRequestor(userAccount);
                    request.setServiceId(this.serviceId);

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
                            mView.addObject("userAccount", response.getUserAccount());
                            mView.setViewName(this.viewUserPage);
                        }
                        else
                        {
                            mView.addObject(Constants.ERROR_MESSAGE, this.messageNoUsersFound);
                            mView.setViewName(this.searchUsersPage);
                        }
                    }
                    else
                    {
                        mView.addObject(Constants.ERROR_MESSAGE, response.getResponse());
                        mView.setViewName(this.searchUsersPage);
                    }
                }
                else
                {
                    mView.setViewName(appConfig.getUnauthorizedPage());
                }
            }
            catch (AccountControlException acx)
            {
                ERROR_RECORDER.error(acx.getMessage(), acx);

                mView.setViewName(appConfig.getErrorResponsePage());
            }
            catch (UserControlServiceException ucsx)
            {
                ERROR_RECORDER.error(ucsx.getMessage(), ucsx);

                mView.setViewName(appConfig.getUnauthorizedPage());
            }
            catch (AdminControlServiceException acsx)
            {
                ERROR_RECORDER.error(acsx.getMessage(), acsx);

                mView.setViewName(appConfig.getUnauthorizedPage());
            }
        }
        else
        {
            mView.setViewName(appConfig.getUnavailablePage());
        }

        return mView;
    }

    @RequestMapping(value = "/account/modify/{userName}", method = RequestMethod.GET)
    public ModelAndView editAccountData(@PathVariable("userName") final String userName)
    {
        final String methodName = UserManagementController.CNAME + "#showAccountData(@PathVariable(\"userName\") final String userName)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("username: {}", userName);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IAccountControlProcessor acctController = new AccountControlProcessorImpl();

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
                IUserControlService userControl = new UserControlServiceImpl();
                IAdminControlService adminControl = new AdminControlServiceImpl();

                boolean isUserAuthorized = userControl.isUserAuthorizedForService(userAccount.getGuid(), this.serviceId);
                boolean isAdminAuthorized = adminControl.adminControlService(userAccount, AdminControlType.USER_ADMIN);

                if (DEBUG)
                {
                    DEBUGGER.debug("isUserAuthorized: {}", isUserAuthorized);
                    DEBUGGER.debug("isAdminAuthorized: {}", isAdminAuthorized);
                }

                if ((isUserAuthorized) && (isAdminAuthorized))
                {
                    // ensure authenticated access
                    RequestHostInfo reqInfo = new RequestHostInfo();
                    reqInfo.setHostAddress(hRequest.getRemoteAddr());
                    reqInfo.setHostName(hRequest.getRemoteHost());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
                    }

                    UserAccount searchAccount = new UserAccount();
                    searchAccount.setUsername(userName);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("UserAccount: {}", searchAccount);
                    }

                    AccountControlRequest request = new AccountControlRequest();
                    request.setAppName(appConfig.getApplicationName());
                    request.setControlType(ControlType.LOOKUP);
                    request.setHostInfo(reqInfo);
                    request.setRequestor(userAccount);
                    request.setUserAccount(searchAccount);
                    request.setSearchType(SearchRequestType.USERNAME);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("AccountControlRequest: {}", request);
                    }

                    AccountControlResponse response = acctController.searchAccounts(request);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("AccountControlResponse: {}", response);
                    }

                    if (response.getRequestStatus() == SecurityRequestStatus.SUCCESS)
                    {
                        List<UserAccount> accountList = response.getUserList();

                        if (DEBUG)
                        {
                            DEBUGGER.debug("accountList: {}", accountList);
                        }

                        if ((accountList != null) && (accountList.size() != 0))
                        {
                            // we have a list of accounts (or at least one account
                            if (accountList.size() == 1)
                            {
                                UserAccount responseUser = accountList.get(0);

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("UserAccount: {}", responseUser);
                                }

                                mView.addObject("UserAccount", responseUser);
                                mView.setViewName(this.editUserPage);
                            }
                            else
                            {
                                // multiple user accounts were found
                                mView.addObject("UserAccounts", accountList);
                                mView.setViewName(this.viewUsersPage);
                            }
                        }
                        else
                        {
                            mView.addObject(Constants.RESPONSE_MESSAGE, this.messageNoUsersFound);
                            mView.setViewName(this.viewUsersPage);
                        }
                    }
                    else
                    {
                        mView.addObject(Constants.ERROR_MESSAGE, response.getResponse());
                        mView.setViewName(this.viewUsersPage);
                    }
                }
                else
                {
                    mView.setViewName(appConfig.getUnauthorizedPage());
                }
            }
            catch (AccountControlException acx)
            {
                ERROR_RECORDER.error(acx.getMessage(), acx);

                mView.setViewName(appConfig.getErrorResponsePage());
            }
            catch (UserControlServiceException ucsx)
            {
                ERROR_RECORDER.error(ucsx.getMessage(), ucsx);

                mView.setViewName(appConfig.getUnauthorizedPage());
            }
            catch (AdminControlServiceException acsx)
            {
                ERROR_RECORDER.error(acsx.getMessage(), acsx);

                mView.setViewName(appConfig.getUnauthorizedPage());
            }
        }
        else
        {
            mView.setViewName(appConfig.getUnavailablePage());
        }

        return mView;
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public ModelAndView doSearchUsers(@ModelAttribute("user") final UserAccount user, final BindingResult bindResult)
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
                IUserControlService userControl = new UserControlServiceImpl();
                IAdminControlService adminControl = new AdminControlServiceImpl();

                boolean isUserAuthorized = userControl.isUserAuthorizedForService(userAccount.getGuid(), this.serviceId);
                boolean isAdminAuthorized = adminControl.adminControlService(userAccount, AdminControlType.USER_ADMIN);

                if (DEBUG)
                {
                    DEBUGGER.debug("isUserAuthorized: {}", isUserAuthorized);
                    DEBUGGER.debug("isAdminAuthorized: {}", isAdminAuthorized);
                }

                if ((isUserAuthorized) && (isAdminAuthorized))
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
                    request.setAppName(appConfig.getApplicationName());
                    request.setApplicationId(appConfig.getApplicationName());
                    request.setControlType(ControlType.LOOKUP);
                    request.setModType(ModificationType.NONE);
                    request.setSearchType(SearchRequestType.ALL);
                    request.setRequestor(userAccount);
                    request.setIsLogonRequest(false);
                    request.setServiceId(this.serviceId);

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
                            mView.addObject(Constants.ERROR_MESSAGE, response.getResponse());
                        }
                    }
                    else
                    {
                        mView.addObject("command", new UserAccount());
                        mView.addObject(Constants.ERROR_MESSAGE, response.getResponse());
                    }
                }
                else
                {
                    mView.setViewName(appConfig.getUnauthorizedPage());
                }
            }
            catch (UserControlServiceException ucsx)
            {
                ERROR_RECORDER.error(ucsx.getMessage(), ucsx);

                mView.setViewName(appConfig.getErrorResponsePage());
            }
            catch (AdminControlServiceException acsx)
            {
                ERROR_RECORDER.error(acsx.getMessage(), acsx);

                mView.setViewName(appConfig.getErrorResponsePage());
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
    public ModelAndView doAddUser(@ModelAttribute("user") final UserAccount user, final BindingResult bindResult)
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
                IUserControlService userControl = new UserControlServiceImpl();
                IAdminControlService adminControl = new AdminControlServiceImpl();

                boolean isUserAuthorized = userControl.isUserAuthorizedForService(userAccount.getGuid(), this.serviceId);
                boolean isAdminAuthorized = adminControl.adminControlService(userAccount, AdminControlType.SERVICE_ADMIN);

                if (DEBUG)
                {
                    DEBUGGER.debug("isUserAuthorized: {}", isUserAuthorized);
                    DEBUGGER.debug("isAdminAuthorized: {}", isAdminAuthorized);
                }

                if ((isUserAuthorized) && (isAdminAuthorized))
                {
                    // TODO: validate
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
                    newUser.setDisplayName(user.getDisplayName());
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
                    request.setAppName(appConfig.getApplicationName());
                    request.setApplicationId(appConfig.getApplicationName());
                    request.setAlgorithm(secConfig.getAuthAlgorithm());
                    request.setControlType(ControlType.CREATE);
                    request.setModType(ModificationType.NONE);
                    request.setRequestor(userAccount);
                    request.setIsLogonRequest(false);
                    request.setServiceId(this.serviceId);

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
                        // account created
                        mView.addObject(Constants.RESPONSE_MESSAGE, this.messageAccountCreated);
                        mView.addObject("command", new UserAccount());
                        mView.setViewName(this.createUserPage);
                    }
                    else
                    {
                        mView.addObject("command", new UserAccount());
                        mView.addObject(Constants.ERROR_MESSAGE, response.getResponse());
                    }
                }
                else
                {
                    mView.setViewName(appConfig.getUnauthorizedPage());
                }
            }
            catch (UserControlServiceException ucsx)
            {
                ERROR_RECORDER.error(ucsx.getMessage(), ucsx);

                mView.setViewName(appConfig.getErrorResponsePage());
            }
            catch (AdminControlServiceException acsx)
            {
                ERROR_RECORDER.error(acsx.getMessage(), acsx);

                mView.setViewName(appConfig.getErrorResponsePage());
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

    @RequestMapping(value = "/suspend-user", method = RequestMethod.POST)
    public ModelAndView doSuspendUser(@ModelAttribute("user") final UserAccount user, final BindingResult bindResult)
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
                IUserControlService userControl = new UserControlServiceImpl();
                IAdminControlService adminControl = new AdminControlServiceImpl();

                boolean isUserAuthorized = userControl.isUserAuthorizedForService(userAccount.getGuid(), this.serviceId);
                boolean isAdminAuthorized = adminControl.adminControlService(userAccount, AdminControlType.SERVICE_ADMIN);

                if (DEBUG)
                {
                    DEBUGGER.debug("isUserAuthorized: {}", isUserAuthorized);
                    DEBUGGER.debug("isAdminAuthorized: {}", isAdminAuthorized);
                }

                if ((isUserAuthorized) && (isAdminAuthorized))
                {
                    // TODO: validate
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
                    request.setAppName(appConfig.getApplicationName());
                    request.setApplicationId(appConfig.getApplicationName());
                    request.setAlgorithm(secConfig.getAuthAlgorithm());
                    request.setControlType(ControlType.SUSPEND);
                    request.setModType(ModificationType.NONE);
                    request.setRequestor(userAccount);
                    request.setIsLogonRequest(false);
                    request.setServiceId(this.serviceId);

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
                    else
                    {
                        mView.addObject("command", new UserAccount());
                        mView.addObject(Constants.ERROR_MESSAGE, response.getResponse());
                    }
                }
                else
                {
                    mView.setViewName(appConfig.getUnauthorizedPage());
                }
            }
            catch (UserControlServiceException ucsx)
            {
                ERROR_RECORDER.error(ucsx.getMessage(), ucsx);

                mView.setViewName(appConfig.getErrorResponsePage());
            }
            catch (AdminControlServiceException acsx)
            {
                ERROR_RECORDER.error(acsx.getMessage(), acsx);

                mView.setViewName(appConfig.getErrorResponsePage());
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

    @RequestMapping(value = "/reset-user", method = RequestMethod.POST)
    public ModelAndView doResetUser(@ModelAttribute("user") final UserAccount user, final BindingResult bindResult)
    {
        final String methodName = UserManagementController.CNAME + "#doResetUser(@ModelAttribute(\"user\") final UserAccount user, final BindingResult bindResult)";

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
                IUserControlService userControl = new UserControlServiceImpl();
                IAdminControlService adminControl = new AdminControlServiceImpl();

                boolean isUserAuthorized = userControl.isUserAuthorizedForService(userAccount.getGuid(), this.serviceId);
                boolean isAdminAuthorized = adminControl.adminControlService(userAccount, AdminControlType.SERVICE_ADMIN);

                if (DEBUG)
                {
                    DEBUGGER.debug("isUserAuthorized: {}", isUserAuthorized);
                    DEBUGGER.debug("isAdminAuthorized: {}", isAdminAuthorized);
                }

                if ((isUserAuthorized) && (isAdminAuthorized))
                {
                    // TODO: validate
                    RequestHostInfo hostInfo = new RequestHostInfo();
                    hostInfo.setHostAddress(hRequest.getRemoteAddr());
                    hostInfo.setHostName(hRequest.getRemoteHost());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("RequestHostInfo: {}", hostInfo);
                    }

                    AccountResetRequest resetReq = new AccountResetRequest();
                    resetReq.setHostInfo(hostInfo);
                    resetReq.setRequestor(userAccount);
                    resetReq.setUserAccount(user);
                    resetReq.setAlgorithm(secConfig.getAuthAlgorithm());
                    resetReq.setAppName(appConfig.getApplicationName());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("AccountResetRequest: {}", resetReq);
                    }

                    if (secConfig.getSmsResetEnabled())
                    {
                        String smsCode = RandomStringUtils.randomAlphanumeric(8);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("smsCode: {}", smsCode);
                        }

                        resetReq.setSmsCode(smsCode);
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("AccountResetRequest: {}", resetReq);
                    }

                    IAccountResetProcessor resetProcess = new AccountResetProcessorImpl();
                    AccountResetResponse resetRes = resetProcess.resetUserPassword(resetReq);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("AccountResetResponse: {}", resetRes);
                    }

                    if (resetRes.getRequestStatus() == SecurityRequestStatus.SUCCESS)
                    {
                        // good, send email
                        String emailId = RandomStringUtils.randomAlphanumeric(16);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Message ID: {}", emailId);
                        }

                        StringBuilder targetURL = new StringBuilder()
                            .append(hRequest.getScheme() + "://" + hRequest.getServerName())
                            .append((hRequest.getServerPort() == 443) ? null : ":" + hRequest.getServerPort())
                            .append(hRequest.getContextPath() + this.resetURL + resetRes.getResponse());

                        if (DEBUG)
                        {
                            DEBUGGER.debug("targetURL: {}", targetURL);
                        }
                            
                        String emailBody = MessageFormat.format(IOUtils.toString(
                                this.getClass().getClassLoader().getResourceAsStream(this.userResetEmail)), new Object[]
                        {
                            userAccount.getGivenName(),
                            new Date(System.currentTimeMillis()),
                            hostInfo.getHostName(),
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
                        emailMessage.setMessageTo(new ArrayList<String>(Arrays.asList(userAccount.getEmailAddr())));

                        if (DEBUG)
                        {
                            DEBUGGER.debug("EmailMessage: {}", emailMessage);
                        }

                        EmailUtils.sendEmailMessage(emailMessage);

                        if (secConfig.getSmsResetEnabled())
                        {
                            // send an sms code
                            EmailMessage smsMessage = new EmailMessage();
                            smsMessage.setIsAlert(true); // set this to alert so it shows as high priority
                            smsMessage.setMessageBody(resetReq.getSmsCode());
                            emailMessage.setMessageTo(new ArrayList<String>(Arrays.asList(userAccount.getEmailAddr())));
                            emailMessage.setMessageFrom(new ArrayList<String>(Arrays.asList(appConfig.getSecEmailAddr())));

                            if (DEBUG)
                            {
                                DEBUGGER.debug("EmailMessage: {}", smsMessage);
                            }

                            EmailUtils.sendSmsMessage(smsMessage);
                        }

                        mView.addObject(Constants.RESPONSE_MESSAGE, this.messagePasswordReset);
                        mView.addObject("userAccount", user);
                        mView.setViewName(this.viewUserPage);
                    }
                    else
                    {
                        // some failure occurred
                        ERROR_RECORDER.error(resetRes.getResponse());

                        mView.addObject(Constants.ERROR_MESSAGE, resetRes.getResponse());
                        mView.addObject("userAccount", user);
                        mView.setViewName(this.viewUserPage);
                    }
                }
                else
                {
                    mView.setViewName(appConfig.getUnauthorizedPage());
                }
            }
            catch (UserControlServiceException ucsx)
            {
                ERROR_RECORDER.error(ucsx.getMessage(), ucsx);

                mView.setViewName(appConfig.getErrorResponsePage());
            }
            catch (AdminControlServiceException acsx)
            {
                ERROR_RECORDER.error(acsx.getMessage(), acsx);

                mView.setViewName(appConfig.getErrorResponsePage());
            }
            catch (IOException iox)
            {
                ERROR_RECORDER.error(iox.getMessage(), iox);

                mView.setViewName(appConfig.getErrorResponsePage());
            }
            catch (CoreServiceException csx)
            {
                ERROR_RECORDER.error(csx.getMessage(), csx);

                mView.setViewName(appConfig.getErrorResponsePage());
            }
            catch (MessagingException mx)
            {
                ERROR_RECORDER.error(mx.getMessage(), mx);

                mView.setViewName(appConfig.getErrorResponsePage());
            }
            catch (AccountResetException arx)
            {
                ERROR_RECORDER.error(arx.getMessage(), arx);

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
