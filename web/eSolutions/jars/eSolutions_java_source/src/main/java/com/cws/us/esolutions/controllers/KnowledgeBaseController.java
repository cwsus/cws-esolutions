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

import java.util.List;
import org.slf4j.Logger;
import java.util.Arrays;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.text.MessageFormat;
import org.slf4j.LoggerFactory;
import javax.mail.MessagingException;
import org.apache.commons.io.IOUtils;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.apache.commons.lang.RandomStringUtils;
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
import com.cws.us.esolutions.ApplicationServiceBean;
import com.cws.esolutions.core.processors.dto.Article;
import com.cws.us.esolutions.validators.ArticleValidator;
import com.cws.esolutions.core.processors.dto.EmailMessage;
import com.cws.esolutions.core.processors.dto.SearchRequest;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.dto.SearchResponse;
import com.cws.esolutions.core.controllers.ResourceController;
import com.cws.esolutions.core.exception.CoreServiceException;
import com.cws.esolutions.core.processors.enums.ArticleStatus;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.core.processors.dto.KnowledgeBaseRequest;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.core.processors.impl.SearchProcessorImpl;
import com.cws.esolutions.core.processors.dto.KnowledgeBaseResponse;
import com.cws.esolutions.core.processors.interfaces.ISearchProcessor;
import com.cws.esolutions.core.processors.impl.KnowledgeBaseProcessorImpl;
import com.cws.esolutions.core.processors.exception.KnowledgeBaseException;
import com.cws.esolutions.core.processors.exception.SearchRequestException;
import com.cws.esolutions.core.processors.interfaces.IKnowledgeBaseProcessor;
import com.cws.esolutions.security.access.control.impl.UserControlServiceImpl;
import com.cws.esolutions.security.access.control.interfaces.IUserControlService;
import com.cws.esolutions.security.access.control.exception.UserControlServiceException;
/**
 * eSolutions_java_source
 * com.cws.us.esolutions.controllers
 * KnowledgeBaseController.java
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
@RequestMapping("/knowledgebase")
public class KnowledgeBaseController
{
    private String prefix = null;
    private String serviceId = null;
    private String serviceName = null;
    private String articleList = null;
    private String defaultHandler = null;
    private String messageSource = null;
    private int newIdentifierLength = 8; // default of 8
    private String editArticlePage = null;
    private String showArticlePage = null;
    private String createArticlePage = null;
    private String reviewArticlePage = null;
    private String deleteArticleEmail = null;
    private String updateArticleEmail = null;
    private String createArticleEmail = null;
    private String rejectArticleEmail = null;
    private String pendingArticlesPage = null;
    private ArticleValidator validator = null;
    private String approveArticleEmail = null;
    private String messageArticleDeleted = null;
    private String messageArticleApproved = null;
    private String messageArticleRejected = null;
    private ApplicationServiceBean appConfig = null;

    private static final String CNAME = KnowledgeBaseController.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CNAME);

    public final void setAppConfig(final ApplicationServiceBean value)
    {
        final String methodName = KnowledgeBaseController.CNAME + "#setAppConfig(final CoreServiceBean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.appConfig = value;
    }

    public final void setValidator(final ArticleValidator value)
    {
        final String methodName = KnowledgeBaseController.CNAME + "#setValidator(final ArticleValidator value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.validator = value;
    }

    public final void setServiceName(final String value)
    {
        final String methodName = KnowledgeBaseController.CNAME + "#setServiceName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serviceName = value;
    }

    public final void setArticleList(final String value)
    {
        final String methodName = KnowledgeBaseController.CNAME + "#setArticleList(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.articleList = value;
    }

    public final void setDefaultHandler(final String value)
    {
        final String methodName = KnowledgeBaseController.CNAME + "#setDefaultHandler(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.defaultHandler = value;
    }

    public final void setShowArticlePage(final String value)
    {
        final String methodName = KnowledgeBaseController.CNAME + "#setShowArticlePage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.showArticlePage = value;
    }

    public final void setEditArticlePage(final String value)
    {
        final String methodName = KnowledgeBaseController.CNAME + "#setEditArticlePage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.editArticlePage = value;
    }

    public final void setCreateArticlePage(final String value)
    {
        final String methodName = KnowledgeBaseController.CNAME + "#setCreateArticlePage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.createArticlePage = value;
    }

    public final void setServiceId(final String value)
    {
        final String methodName = KnowledgeBaseController.CNAME + "#setServiceId(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serviceId = value;
    }

    public final void setPrefix(final String value)
    {
        final String methodName = KnowledgeBaseController.CNAME + "#setPrefix(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.prefix = value;
    }

    public final void setNewIdentifierLength(final int value)
    {
        final String methodName = KnowledgeBaseController.CNAME + "#setNewIdentifierLength(final int value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.newIdentifierLength = value;
    }

    public final void setCreateArticleEmail(final String value)
    {
        final String methodName = KnowledgeBaseController.CNAME + "#setCreateArticleEmail(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.createArticleEmail = value;
    }

    public final void setPendingArticlesPage(final String value)
    {
        final String methodName = KnowledgeBaseController.CNAME + "#setPendingArticlesPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.pendingArticlesPage = value;
    }

    public final void setReviewArticlePage(final String value)
    {
        final String methodName = KnowledgeBaseController.CNAME + "#setReviewArticlePage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.reviewArticlePage = value;
    }

    public final void setMessageSource(final String value)
    {
        final String methodName = KnowledgeBaseController.CNAME + "#setMessageSource(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageSource = value;
    }

    public final void setMessageArticleApproved(final String value)
    {
        final String methodName = KnowledgeBaseController.CNAME + "#setMessageArticleApproved(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageArticleApproved = value;
    }

    public final void setMessageArticleRejected(final String value)
    {
        final String methodName = KnowledgeBaseController.CNAME + "#setMessageArticleRejected(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageArticleRejected = value;
    }

    public final void setMessageArticleDeleted(final String value)
    {
        final String methodName = KnowledgeBaseController.CNAME + "#setMessageArticleDeleted(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageArticleDeleted = value;
    }

    public final void setApproveArticleEmail(final String value)
    {
        final String methodName = KnowledgeBaseController.CNAME + "#setApproveArticleEmail(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.approveArticleEmail = value;
    }

    public final void setUpdateArticleEmail(final String value)
    {
        final String methodName = KnowledgeBaseController.CNAME + "#setUpdateArticleEmail(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.updateArticleEmail = value;
    }

    public final void setDeleteArticleEmail(final String value)
    {
        final String methodName = KnowledgeBaseController.CNAME + "#setDeleteArticleEmail(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.deleteArticleEmail = value;
    }

    public final void setRejectArticleEmail(final String value)
    {
        final String methodName = KnowledgeBaseController.CNAME + "#setRejectArticleEmail(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.rejectArticleEmail = value;
    }

    @RequestMapping(value = "/default", method = RequestMethod.GET)
    public final ModelAndView showDefaultPage()
    {
        final String methodName = KnowledgeBaseController.CNAME + "#showDefaultPage()";

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

        // because anyone can access this, even unauthenticated,
        // null check first
        if ((userAccount != null) && (userAccount.getStatus() == LoginStatus.EXPIRED))
        {
            // redirect to password page
            mView = new ModelAndView(new RedirectView());
            mView.setViewName(appConfig.getExpiredRedirect());
            mView.addObject(Constants.ERROR_MESSAGE, appConfig.getMessagePasswordExpired());

            if (DEBUG)
            {
                DEBUGGER.debug("ModelAndView: {}", mView);
            }

            return mView;
        }

        if (appConfig.getServices().get(this.serviceName))
        {
            mView.addObject("isHelpSearch", true);
            mView.addObject("command", new SearchRequest());
            mView.setViewName(this.defaultHandler);
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

    // NOTE: this does NOT require ANY authorization. anyone can LOOK, authorization
    // is only required to modify/change/add/etc
    @RequestMapping(value = "/article/{articleId}", method = RequestMethod.GET)
    public final ModelAndView showArticleDetail(@PathVariable("articleId") final String articleId)
    {
        final String methodName = KnowledgeBaseController.CNAME + "#showArticleDetail(@PathVariable(\"articleId\") final String articleId)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("articleId: {}", articleId);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IKnowledgeBaseProcessor kbase = new KnowledgeBaseProcessorImpl();

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

        if ((userAccount != null) && (userAccount.getStatus() == LoginStatus.EXPIRED))
        {
            // redirect to password page
            mView = new ModelAndView(new RedirectView());
            mView.setViewName(appConfig.getExpiredRedirect());
            mView.addObject(Constants.ERROR_MESSAGE, appConfig.getMessagePasswordExpired());

            if (DEBUG)
            {
                DEBUGGER.debug("ModelAndView: {}", mView);
            }

            return mView;
        }

        if (appConfig.getServices().get(this.serviceName))
        {
            if (StringUtils.isNotEmpty(articleId))
            {
                try
                {
                    RequestHostInfo reqInfo = new RequestHostInfo();
                    reqInfo.setHostAddress(hRequest.getRemoteAddr());
                    reqInfo.setHostName(hRequest.getRemoteHost());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
                    }

                    Article article = new Article();
                    article.setArticleId(articleId);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Article: {}", article);
                    }

                    KnowledgeBaseRequest request = new KnowledgeBaseRequest();
                    request.setArticle(article);
                    request.setRequestInfo(reqInfo);
                    request.setUserAccount(userAccount);
                    request.setServiceId(this.serviceId);
                    request.setApplicationId(appConfig.getApplicationId());
                    request.setApplicationName(appConfig.getApplicationName());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("KnowledgeBaseRequest: {}", request);
                    }

                    KnowledgeBaseResponse response = kbase.getArticle(request);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("KnowledgeBaseResponse: {}", response);
                    }

                    if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                    {
                        Article resArticle = response.getArticle();

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Article: {}", resArticle);
                        }

                        mView.addObject("article", resArticle);
                        mView.setViewName(this.showArticlePage);
                    }
                    else
                    {
                        mView.addObject("isHelpSearch", true);
                        mView.addObject("command", new SearchRequest());
                        mView.addObject(Constants.ERROR_RESPONSE, response.getResponse());
                        mView.setViewName(this.defaultHandler);
                    }
                }
                catch (KnowledgeBaseException kbx)
                {
                    ERROR_RECORDER.error(kbx.getMessage(), kbx);

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

    @RequestMapping(value = "/create-article", method = RequestMethod.GET)
    public final ModelAndView showCreateArticle()
    {
        final String methodName = KnowledgeBaseController.CNAME + "#showCreateArticle()";

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
            try
            {
                IUserControlService control = new UserControlServiceImpl();

                boolean isUserAuthorized = control.isUserAuthorizedForService(userAccount.getGuid(), this.serviceId);

                if (DEBUG)
                {
                    DEBUGGER.debug("isUserAuthorized: {}", isUserAuthorized);
                }

                if (isUserAuthorized)
                {
                    Article article = new Article();
                    article.setAuthor(userAccount.getDisplayName());
                    article.setAuthorEmail(userAccount.getEmailAddr());
                    article.setArticleStatus(ArticleStatus.NEW);
                    article.setArticleId(this.prefix + RandomStringUtils.randomNumeric(this.newIdentifierLength));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Article: {}", article);
                    }

                    mView.addObject("command", article);
                    mView.setViewName(this.createArticlePage);
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

    @RequestMapping(value = "/edit-article/article/{articleId}", method = RequestMethod.GET)
    public final ModelAndView showEditArticle(@PathVariable("articleId") final String articleId)
    {
        final String methodName = KnowledgeBaseController.CNAME + "#showEditArticle(@PathVariable(\"articleId\") final String articleId)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("articleId: {}", articleId);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IKnowledgeBaseProcessor kbase = new KnowledgeBaseProcessorImpl();

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
            if (StringUtils.isNotEmpty(articleId))
            {
                try
                {
                    IUserControlService control = new UserControlServiceImpl();

                    boolean isUserAuthorized = control.isUserAuthorizedForService(userAccount.getGuid(), this.serviceId);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("isUserAuthorized: {}", isUserAuthorized);
                    }

                    if (isUserAuthorized)
                    {
                        RequestHostInfo reqInfo = new RequestHostInfo();
                        reqInfo.setHostAddress(hRequest.getRemoteAddr());
                        reqInfo.setHostName(hRequest.getRemoteHost());

                        if (DEBUG)
                        {
                            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
                        }

                        Article article = new Article();
                        article.setArticleId(articleId);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Article: {}", article);
                        }

                        KnowledgeBaseRequest request = new KnowledgeBaseRequest();
                        request.setArticle(article);
                        request.setRequestInfo(reqInfo);
                        request.setUserAccount(userAccount);
                        request.setServiceId(this.serviceId);
                        request.setApplicationId(appConfig.getApplicationId());
                        request.setApplicationName(appConfig.getApplicationName());

                        if (DEBUG)
                        {
                            DEBUGGER.debug("KnowledgeBaseRequest: {}", request);
                        }

                        KnowledgeBaseResponse response = kbase.getArticle(request);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("KnowledgeBaseResponse: {}", response);
                        }

                        if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                        {
                            Article resArticle = response.getArticle();

                            if (DEBUG)
                            {
                                DEBUGGER.debug("Article: {}", resArticle);
                            }

                            mView.addObject("article", resArticle);
                            mView.setViewName(this.editArticlePage);
                        }
                        else
                        {
                            mView.addObject("command", new SearchRequest());
                            mView.addObject("isHelpSearch", true);
                            mView.addObject(Constants.ERROR_RESPONSE, response.getResponse());
                            mView.setViewName(this.defaultHandler);
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
                catch (KnowledgeBaseException kbx)
                {
                    ERROR_RECORDER.error(kbx.getMessage(), kbx);

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

    @RequestMapping(value = "/show-approvals", method = RequestMethod.GET)
    public final ModelAndView showPendingApprovals()
    {
        final String methodName = KnowledgeBaseController.CNAME + "#showPendingApprovals()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IKnowledgeBaseProcessor kbase = new KnowledgeBaseProcessorImpl();

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
                IUserControlService control = new UserControlServiceImpl();

                boolean isUserAuthorized = control.isUserAuthorizedForService(userAccount.getGuid(), this.serviceId);

                if (DEBUG)
                {
                    DEBUGGER.debug("isUserAuthorized: {}", isUserAuthorized);
                }

                if (isUserAuthorized)
                {
                    RequestHostInfo reqInfo = new RequestHostInfo();
                    reqInfo.setHostName(hRequest.getRemoteHost());
                    reqInfo.setHostAddress(hRequest.getRemoteAddr());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
                    }

                    KnowledgeBaseRequest request = new KnowledgeBaseRequest();
                    request.setRequestInfo(reqInfo);
                    request.setUserAccount(userAccount);
                    request.setServiceId(this.serviceId);
                    request.setApplicationId(appConfig.getApplicationId());
                    request.setApplicationName(appConfig.getApplicationName());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("KnowledgeBaseRequest: {}", request);
                    }

                    KnowledgeBaseResponse response = kbase.getPendingArticles(request);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("KnowledgeBaseResponse: {}", response);
                    }

                    if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                    {
                        List<Article> pendingArticles = response.getArticleList();

                        if (DEBUG)
                        {
                            DEBUGGER.debug("pendingArticles: {}", pendingArticles);
                        }

                        if ((pendingArticles != null) && (pendingArticles.size() != 0))
                        {
                            mView.addObject(this.articleList, pendingArticles);
                            mView.setViewName(this.pendingArticlesPage);
                        }
                        else
                        {
                            mView.addObject("command", new SearchRequest());
                            mView.addObject("isHelpSearch", true);
                            mView.addObject(Constants.MESSAGE_RESPONSE, response.getResponse());
                            mView.setViewName(this.defaultHandler);
                        }
                    }
                    else
                    {
                        mView.addObject("isHelpSearch", true);
                        mView.addObject(Constants.ERROR_RESPONSE, response.getResponse());
                        mView.addObject("command", new SearchRequest());
                        mView.setViewName(this.defaultHandler);
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
            catch (KnowledgeBaseException kbx)
            {
                ERROR_RECORDER.error(kbx.getMessage(), kbx);

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

    @RequestMapping(value = "/delete-article/article/{article}", method = RequestMethod.GET)
    public final ModelAndView deleteSelectedArticle(@PathVariable("article") final String article)
    {
        final String methodName = KnowledgeBaseController.CNAME + "#submitNewArticle(@PathVariable(\"article\") final String article)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", article);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IKnowledgeBaseProcessor kbase = new KnowledgeBaseProcessorImpl();

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
                IUserControlService control = new UserControlServiceImpl();

                boolean isUserAuthorized = control.isUserAuthorizedForService(userAccount.getGuid(), this.serviceId);

                if (DEBUG)
                {
                    DEBUGGER.debug("isUserAuthorized: {}", isUserAuthorized);
                }

                if (isUserAuthorized)
                {
                    RequestHostInfo reqInfo = new RequestHostInfo();
                    reqInfo.setHostName(hRequest.getRemoteHost());
                    reqInfo.setHostAddress(hRequest.getRemoteAddr());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
                    }

                    Article reqArticle = new Article();
                    reqArticle.setArticleId(article);
                    reqArticle.setArticleStatus(ArticleStatus.DELETED);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Article: {}", reqArticle);
                    }

                    KnowledgeBaseRequest request = new KnowledgeBaseRequest();
                    request.setArticle(reqArticle);
                    request.setRequestInfo(reqInfo);
                    request.setUserAccount(userAccount);
                    request.setServiceId(this.serviceId);
                    request.setApplicationId(appConfig.getApplicationId());
                    request.setApplicationName(appConfig.getApplicationName());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("KnowledgeBaseRequest: {}", request);
                    }

                    KnowledgeBaseResponse response = kbase.updateArticleStatus(request);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("KnowledgeBaseResponse: {}", response);
                    }

                    if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                    {
                        final String emailId = RandomStringUtils.randomAlphanumeric(16);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("emailId: {}", emailId);
                        }

                        final String emailBody = MessageFormat.format(IOUtils.toString(
                                this.getClass().getClassLoader().getResourceAsStream(this.deleteArticleEmail)), new Object[] { reqArticle.getArticleId() });

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Email body: {}", emailBody);
                        }

                        // good, now generate an email with the information
                        EmailMessage emailMessage = new EmailMessage();
                        emailMessage.setIsAlert(false);
                        emailMessage.setMessageBody(emailBody);
                        emailMessage.setMessageId(RandomStringUtils.randomAlphanumeric(16));
                        emailMessage.setMessageSubject("[ " + emailId + " ] - " + ResourceController.returnSystemPropertyValue(this.messageSource,
                                this.deleteArticleEmail, this.getClass().getClassLoader()));
                        emailMessage.setMessageTo(new ArrayList<String>(Arrays.asList(appConfig.getSecEmailAddr())));
                        emailMessage.setMessageFrom(new ArrayList<String>(Arrays.asList(userAccount.getEmailAddr())));

                        if (DEBUG)
                        {
                            DEBUGGER.debug("EmailMessage: {}", emailMessage);
                        }

                        EmailUtils.sendEmailMessage(emailMessage);

                        mView.addObject(Constants.RESPONSE_MESSAGE, this.messageArticleDeleted);
                        mView.setViewName(appConfig.getSearchRequestPage());
                    }
                    else
                    {
                        // failure
                        mView.addObject(Constants.ERROR_RESPONSE, response.getResponse());
                        mView.setViewName(appConfig.getSearchRequestPage());
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
            catch (KnowledgeBaseException kbx)
            {
                ERROR_RECORDER.error(kbx.getMessage(), kbx);

                mView.setViewName(appConfig.getErrorResponsePage());
            }
            catch (IOException iox)
            {
                ERROR_RECORDER.error(iox.getMessage(), iox);

                mView.addObject(Constants.ERROR_MESSAGE, appConfig.getMessageEmailSendFailed());
                mView.addObject("command", new Article());
                mView.setViewName(this.createArticlePage);
            }
            catch (MessagingException mx)
            {
                ERROR_RECORDER.error(mx.getMessage(), mx);

                mView.addObject(Constants.ERROR_MESSAGE, appConfig.getMessageEmailSendFailed());
                mView.addObject("command", new Article());
                mView.setViewName(this.createArticlePage);
            }
            catch (CoreServiceException csx)
            {
                ERROR_RECORDER.error(csx.getMessage(), csx);

                mView.addObject(Constants.ERROR_MESSAGE, appConfig.getMessageEmailSendFailed());
                mView.addObject("command", new Article());
                mView.setViewName(this.createArticlePage);
            }

            if (DEBUG)
            {
                DEBUGGER.debug("ModelAndView: {}", mView);
            }
        }
        else
        {
            mView.setViewName(appConfig.getUnavailablePage());
        }

        return mView;
    }

    @RequestMapping(value = "/approve-article/article/{article}", method = RequestMethod.GET)
    public final ModelAndView approveSelectedArticle(@PathVariable("article") final String article)
    {
        final String methodName = KnowledgeBaseController.CNAME + "#approveSelectedArticle(@PathVariable(\"article\") final String article)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", article);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IKnowledgeBaseProcessor kbase = new KnowledgeBaseProcessorImpl();

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
                IUserControlService control = new UserControlServiceImpl();

                boolean isUserAuthorized = control.isUserAuthorizedForService(userAccount.getGuid(), this.serviceId);

                if (DEBUG)
                {
                    DEBUGGER.debug("isUserAuthorized: {}", isUserAuthorized);
                }

                if (isUserAuthorized)
                {
                    RequestHostInfo reqInfo = new RequestHostInfo();
                    reqInfo.setHostName(hRequest.getRemoteHost());
                    reqInfo.setHostAddress(hRequest.getRemoteAddr());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
                    }

                    Article reqArticle = new Article();
                    reqArticle.setArticleId(article);
                    reqArticle.setArticleStatus(ArticleStatus.APPROVED);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Article: {}", reqArticle);
                    }

                    KnowledgeBaseRequest request = new KnowledgeBaseRequest();
                    request.setArticle(reqArticle);
                    request.setRequestInfo(reqInfo);
                    request.setUserAccount(userAccount);
                    request.setServiceId(this.serviceId);
                    request.setApplicationId(appConfig.getApplicationId());
                    request.setApplicationName(appConfig.getApplicationName());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("KnowledgeBaseRequest: {}", request);
                    }

                    KnowledgeBaseResponse response = kbase.updateArticleStatus(request);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("KnowledgeBaseResponse: {}", response);
                    }

                    if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                    {
                        final String emailId = RandomStringUtils.randomAlphanumeric(16);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("emailId: {}", emailId);
                        }

                        final String emailBody = MessageFormat.format(IOUtils.toString(
                                this.getClass().getClassLoader().getResourceAsStream(this.approveArticleEmail)), new Object[] { reqArticle.getArticleId() });

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Email body: {}", emailBody);
                        }

                        // good, now generate an email with the information
                        EmailMessage emailMessage = new EmailMessage();
                        emailMessage.setIsAlert(false);
                        emailMessage.setMessageBody(emailBody);
                        emailMessage.setMessageId(RandomStringUtils.randomAlphanumeric(16));
                        emailMessage.setMessageSubject("[ " + emailId + " ] - " + ResourceController.returnSystemPropertyValue(this.messageSource,
                                this.approveArticleEmail, this.getClass().getClassLoader()));
                        emailMessage.setMessageTo(new ArrayList<String>(Arrays.asList(appConfig.getSecEmailAddr())));
                        emailMessage.setMessageFrom(new ArrayList<String>(Arrays.asList(userAccount.getEmailAddr())));

                        if (DEBUG)
                        {
                            DEBUGGER.debug("EmailMessage: {}", emailMessage);
                        }

                        EmailUtils.sendEmailMessage(emailMessage);

                        mView.addObject(Constants.RESPONSE_MESSAGE, this.messageArticleApproved);
                        mView.setViewName(appConfig.getSearchRequestPage());
                    }
                    else
                    {
                        // failure
                        mView.addObject(Constants.ERROR_RESPONSE, response.getResponse());
                        mView.setViewName(appConfig.getSearchRequestPage());
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
            catch (KnowledgeBaseException kbx)
            {
                ERROR_RECORDER.error(kbx.getMessage(), kbx);

                mView.setViewName(appConfig.getErrorResponsePage());
            }
            catch (IOException iox)
            {
                ERROR_RECORDER.error(iox.getMessage(), iox);

                mView.addObject(Constants.ERROR_MESSAGE, appConfig.getMessageEmailSendFailed());
                mView.addObject("command", new Article());
                mView.setViewName(this.createArticlePage);
            }
            catch (MessagingException mx)
            {
                ERROR_RECORDER.error(mx.getMessage(), mx);

                mView.addObject(Constants.ERROR_MESSAGE, appConfig.getMessageEmailSendFailed());
                mView.addObject("command", new Article());
                mView.setViewName(this.createArticlePage);
            }
            catch (CoreServiceException csx)
            {
                ERROR_RECORDER.error(csx.getMessage(), csx);

                mView.addObject(Constants.ERROR_MESSAGE, appConfig.getMessageEmailSendFailed());
                mView.addObject("command", new Article());
                mView.setViewName(this.createArticlePage);
            }

            if (DEBUG)
            {
                DEBUGGER.debug("ModelAndView: {}", mView);
            }
        }
        else
        {
            mView.setViewName(appConfig.getUnavailablePage());
        }

        return mView;
    }

    @RequestMapping(value = "/reject-article/article/{article}", method = RequestMethod.GET)
    public final ModelAndView rejectSelectedArticle(@PathVariable("article") final String article)
    {
        final String methodName = KnowledgeBaseController.CNAME + "#rejectSelectedArticle(@PathVariable(\"article\") final String article)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", article);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IKnowledgeBaseProcessor kbase = new KnowledgeBaseProcessorImpl();

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
                IUserControlService control = new UserControlServiceImpl();

                boolean isUserAuthorized = control.isUserAuthorizedForService(userAccount.getGuid(), this.serviceId);

                if (DEBUG)
                {
                    DEBUGGER.debug("isUserAuthorized: {}", isUserAuthorized);
                }

                if (isUserAuthorized)
                {
                    RequestHostInfo reqInfo = new RequestHostInfo();
                    reqInfo.setHostName(hRequest.getRemoteHost());
                    reqInfo.setHostAddress(hRequest.getRemoteAddr());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
                    }

                    Article reqArticle = new Article();
                    reqArticle.setArticleId(article);
                    reqArticle.setArticleStatus(ArticleStatus.REJECTED);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Article: {}", reqArticle);
                    }

                    KnowledgeBaseRequest request = new KnowledgeBaseRequest();
                    request.setArticle(reqArticle);
                    request.setRequestInfo(reqInfo);
                    request.setUserAccount(userAccount);
                    request.setServiceId(this.serviceId);
                    request.setApplicationId(appConfig.getApplicationId());
                    request.setApplicationName(appConfig.getApplicationName());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("KnowledgeBaseRequest: {}", request);
                    }

                    KnowledgeBaseResponse response = kbase.updateArticleStatus(request);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("KnowledgeBaseResponse: {}", response);
                    }

                    if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                    {
                        final String emailId = RandomStringUtils.randomAlphanumeric(16);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("emailId: {}", emailId);
                        }

                        final String emailBody = MessageFormat.format(IOUtils.toString(
                                this.getClass().getClassLoader().getResourceAsStream(this.rejectArticleEmail)), new Object[] { reqArticle.getArticleId() });

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Email body: {}", emailBody);
                        }

                        // good, now generate an email with the information
                        EmailMessage emailMessage = new EmailMessage();
                        emailMessage.setIsAlert(false);
                        emailMessage.setMessageBody(emailBody);
                        emailMessage.setMessageId(RandomStringUtils.randomAlphanumeric(16));
                        emailMessage.setMessageSubject("[ " + emailId + " ] - " + ResourceController.returnSystemPropertyValue(this.messageSource,
                                this.rejectArticleEmail, this.getClass().getClassLoader()));
                        emailMessage.setMessageTo(new ArrayList<String>(Arrays.asList(appConfig.getSecEmailAddr())));
                        emailMessage.setMessageFrom(new ArrayList<String>(Arrays.asList(userAccount.getEmailAddr())));

                        if (DEBUG)
                        {
                            DEBUGGER.debug("EmailMessage: {}", emailMessage);
                        }

                        EmailUtils.sendEmailMessage(emailMessage);

                        mView.addObject(Constants.RESPONSE_MESSAGE, this.messageArticleRejected);
                        mView.setViewName(appConfig.getSearchRequestPage());
                    }
                    else
                    {
                        // failure
                        mView.addObject(Constants.ERROR_RESPONSE, response.getResponse());
                        mView.setViewName(appConfig.getSearchRequestPage());
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
            catch (KnowledgeBaseException kbx)
            {
                ERROR_RECORDER.error(kbx.getMessage(), kbx);

                mView.setViewName(appConfig.getErrorResponsePage());
            }
            catch (IOException iox)
            {
                ERROR_RECORDER.error(iox.getMessage(), iox);

                mView.addObject(Constants.ERROR_MESSAGE, appConfig.getMessageEmailSendFailed());
                mView.addObject("command", new Article());
                mView.setViewName(this.createArticlePage);
            }
            catch (MessagingException mx)
            {
                ERROR_RECORDER.error(mx.getMessage(), mx);

                mView.addObject(Constants.ERROR_MESSAGE, appConfig.getMessageEmailSendFailed());
                mView.addObject("command", new Article());
                mView.setViewName(this.createArticlePage);
            }
            catch (CoreServiceException csx)
            {
                ERROR_RECORDER.error(csx.getMessage(), csx);

                mView.addObject(Constants.ERROR_MESSAGE, appConfig.getMessageEmailSendFailed());
                mView.addObject("command", new Article());
                mView.setViewName(this.createArticlePage);
            }

            if (DEBUG)
            {
                DEBUGGER.debug("ModelAndView: {}", mView);
            }
        }
        else
        {
            mView.setViewName(appConfig.getUnavailablePage());
        }

        return mView;
    }

    @RequestMapping(value = "/validate-article", method = RequestMethod.POST)
    public final ModelAndView doValidateSubmission(@ModelAttribute("article") final Article article, final BindingResult bindResult)
    {
        final String methodName = KnowledgeBaseController.CNAME + "#doValidateSubmission(@ModelAttribute(\"article\") final Article article, final BindingResult bindResult)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Article: {}", article);
            DEBUGGER.debug("BindingResult: {}", bindResult);
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
            validator.validate(article, bindResult);

            if (bindResult.hasErrors())
            {
                // validation failed
                mView.addObject(Constants.ERROR_MESSAGE, appConfig.getMessageValidationFailed());
                mView.addObject("command", new Article());

                return mView;
            }

            article.setAuthor(userAccount.getUsername());
            article.setAuthorEmail(userAccount.getEmailAddr());

            if (DEBUG)
            {
                DEBUGGER.debug("Article: {}", article);
            }

            mView.addObject("article", article);
            mView.setViewName(this.reviewArticlePage);

            if (DEBUG)
            {
                DEBUGGER.debug("ModelAndView: {}", mView);
            }
        }
        else
        {
            mView.setViewName(appConfig.getUnavailablePage());
        }

        return mView;
    }

    @RequestMapping(value = "/submit-article", method = RequestMethod.POST)
    public final ModelAndView submitArticleData(@ModelAttribute("article") final Article article, final BindingResult bindResult)
    {
        final String methodName = KnowledgeBaseController.CNAME + "#submitArticleData(@ModelAttribute(\"article\") final Article article, final BindingResult bindResult)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Article: {}", article);
            DEBUGGER.debug("BindingResult: {}", bindResult);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IKnowledgeBaseProcessor kbase = new KnowledgeBaseProcessorImpl();

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
            validator.validate(article, bindResult);

            if (bindResult.hasErrors())
            {
                // validation failed
                mView.addObject(Constants.ERROR_MESSAGE, appConfig.getMessageValidationFailed());
                mView.addObject("command", new Article());

                return mView;
            }

            try
            {
                IUserControlService control = new UserControlServiceImpl();

                boolean isUserAuthorized = control.isUserAuthorizedForService(userAccount.getGuid(), this.serviceId);

                if (DEBUG)
                {
                    DEBUGGER.debug("isUserAuthorized: {}", isUserAuthorized);
                }

                if (isUserAuthorized)
                {
                    RequestHostInfo reqInfo = new RequestHostInfo();
                    reqInfo.setHostName(hRequest.getRemoteHost());
                    reqInfo.setHostAddress(hRequest.getRemoteAddr());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
                    }

                    KnowledgeBaseRequest request = new KnowledgeBaseRequest();
                    request.setArticle(article);
                    request.setRequestInfo(reqInfo);
                    request.setUserAccount(userAccount);
                    request.setServiceId(this.serviceId);
                    request.setApplicationId(appConfig.getApplicationId());
                    request.setApplicationName(appConfig.getApplicationName());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("KnowledgeBaseRequest: {}", request);
                    }

                    KnowledgeBaseResponse response = kbase.addNewArticle(request);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("KnowledgeBaseResponse: {}", response);
                    }

                    if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                    {
                        // article created
                        final String emailId = RandomStringUtils.randomAlphanumeric(16);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("emailId: {}", emailId);
                        }

                        final String emailBody = MessageFormat.format(IOUtils.toString(
                                this.getClass().getClassLoader().getResourceAsStream(this.createArticleEmail)), new Object[]
                        {
                            article.getArticleId(),
                            userAccount.getDisplayName(),
                            article.getTitle(),
                            article.getSymptoms(),
                            article.getCause(),
                            article.getKeywords(),
                            article.getResolution()
                        });

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Email body: {}", emailBody);
                        }

                        // good, now generate an email with the information
                        EmailMessage emailMessage = new EmailMessage();
                        emailMessage.setIsAlert(false);
                        emailMessage.setMessageBody(emailBody);
                        emailMessage.setMessageId(RandomStringUtils.randomAlphanumeric(16));
                        emailMessage.setMessageSubject("[ " + emailId + " ] - " + ResourceController.returnSystemPropertyValue(this.messageSource,
                                this.createArticleEmail, this.getClass().getClassLoader()));
                        emailMessage.setMessageTo(new ArrayList<String>(Arrays.asList(appConfig.getSecEmailAddr())));
                        emailMessage.setMessageFrom(new ArrayList<String>(Arrays.asList(userAccount.getEmailAddr())));

                        if (DEBUG)
                        {
                            DEBUGGER.debug("EmailMessage: {}", emailMessage);
                        }

                        EmailUtils.sendEmailMessage(emailMessage);

                        mView.addObject(Constants.MESSAGE_RESPONSE, response.getResponse());
                        mView.addObject("command", new Article());
                        mView.setViewName(this.createArticlePage);
                    }
                    else
                    {
                        // failure
                        mView.addObject("command", article);
                        mView.addObject(Constants.ERROR_RESPONSE, response.getResponse());
                        mView.setViewName(this.createArticlePage);
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
            catch (KnowledgeBaseException kbx)
            {
                ERROR_RECORDER.error(kbx.getMessage(), kbx);

                mView.setViewName(appConfig.getErrorResponsePage());
            }
            catch (IOException iox)
            {
                ERROR_RECORDER.error(iox.getMessage(), iox);

                mView.addObject(Constants.ERROR_MESSAGE, appConfig.getMessageEmailSendFailed());
                mView.addObject("command", new Article());
                mView.setViewName(this.createArticlePage);
            }
            catch (MessagingException mx)
            {
                ERROR_RECORDER.error(mx.getMessage(), mx);

                mView.addObject(Constants.ERROR_MESSAGE, appConfig.getMessageEmailSendFailed());
                mView.addObject("command", new Article());
                mView.setViewName(this.createArticlePage);
            }
            catch (CoreServiceException csx)
            {
                ERROR_RECORDER.error(csx.getMessage(), csx);

                mView.addObject(Constants.ERROR_MESSAGE, appConfig.getMessageEmailSendFailed());
                mView.addObject("command", new Article());
                mView.setViewName(this.createArticlePage);
            }

            if (DEBUG)
            {
                DEBUGGER.debug("ModelAndView: {}", mView);
            }
        }
        else
        {
            mView.setViewName(appConfig.getUnavailablePage());
        }

        return mView;
    }

    @RequestMapping(value = "/edit-article", method = RequestMethod.POST)
    public final ModelAndView doEditArticle(@ModelAttribute("article") final Article article, final BindingResult bindResult)
    {
        final String methodName = KnowledgeBaseController.CNAME + "#doEditArticle(@ModelAttribute(\"article\") final Article article, final BindingResult bindResult)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Article: {}", article);
            DEBUGGER.debug("BindingResult: {}", bindResult);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IKnowledgeBaseProcessor kbase = new KnowledgeBaseProcessorImpl();

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
            validator.validate(article, bindResult);

            if (bindResult.hasErrors())
            {
                // validation failed
                mView.addObject(Constants.ERROR_MESSAGE, appConfig.getMessageValidationFailed());
                mView.addObject("command", new Article());

                return mView;
            }

            try
            {
                IUserControlService control = new UserControlServiceImpl();

                boolean isUserAuthorized = control.isUserAuthorizedForService(userAccount.getGuid(), this.serviceId);

                if (DEBUG)
                {
                    DEBUGGER.debug("isUserAuthorized: {}", isUserAuthorized);
                }

                if (isUserAuthorized)
                {
                    RequestHostInfo reqInfo = new RequestHostInfo();
                    reqInfo.setHostName(hRequest.getRemoteHost());
                    reqInfo.setHostAddress(hRequest.getRemoteAddr());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
                    }

                    KnowledgeBaseRequest request = new KnowledgeBaseRequest();
                    request.setArticle(article);
                    request.setRequestInfo(reqInfo);
                    request.setUserAccount(userAccount);
                    request.setServiceId(this.serviceId);
                    request.setApplicationId(appConfig.getApplicationId());
                    request.setApplicationName(appConfig.getApplicationName());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("KnowledgeBaseRequest: {}", request);
                    }

                    KnowledgeBaseResponse response = kbase.updateArticle(request);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("KnowledgeBaseResponse: {}", response);
                    }

                    if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                    {
                        // article created
                        final String emailId = RandomStringUtils.randomAlphanumeric(16);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("emailId: {}", emailId);
                        }

                        final String emailBody = MessageFormat.format(IOUtils.toString(
                                this.getClass().getClassLoader().getResourceAsStream(this.updateArticleEmail)), new Object[]
                        {
                            article.getArticleId(),
                            userAccount.getDisplayName(),
                            article.getTitle(),
                            article.getSymptoms(),
                            article.getCause(),
                            article.getKeywords(),
                            article.getResolution()
                        });

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Email body: {}", emailBody);
                        }

                        // good, now generate an email with the information
                        EmailMessage emailMessage = new EmailMessage();
                        emailMessage.setIsAlert(false);
                        emailMessage.setMessageBody(emailBody);
                        emailMessage.setMessageId(RandomStringUtils.randomAlphanumeric(16));
                        emailMessage.setMessageSubject("[ " + emailId + " ] - " + ResourceController.returnSystemPropertyValue(this.messageSource,
                                this.updateArticleEmail, this.getClass().getClassLoader()));
                        emailMessage.setMessageTo(new ArrayList<String>(Arrays.asList(appConfig.getSecEmailAddr())));
                        emailMessage.setMessageFrom(new ArrayList<String>(Arrays.asList(userAccount.getEmailAddr())));

                        if (DEBUG)
                        {
                            DEBUGGER.debug("EmailMessage: {}", emailMessage);
                        }

                        EmailUtils.sendEmailMessage(emailMessage);

                        mView.addObject(Constants.MESSAGE_RESPONSE, response.getResponse());
                        mView.addObject("command", new Article());
                        mView.setViewName(this.createArticlePage);
                    }
                    else
                    {
                        // failure
                        mView.addObject("command", article);
                        mView.addObject(Constants.ERROR_RESPONSE, response.getResponse());
                        mView.setViewName(this.createArticlePage);
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
            catch (KnowledgeBaseException kbx)
            {
                ERROR_RECORDER.error(kbx.getMessage(), kbx);

                mView.setViewName(appConfig.getErrorResponsePage());
            }
            catch (IOException iox)
            {
                ERROR_RECORDER.error(iox.getMessage(), iox);

                mView.addObject(Constants.ERROR_MESSAGE, appConfig.getMessageEmailSendFailed());
                mView.addObject("command", new Article());
                mView.setViewName(this.createArticlePage);
            }
            catch (MessagingException mx)
            {
                ERROR_RECORDER.error(mx.getMessage(), mx);

                mView.addObject(Constants.ERROR_MESSAGE, appConfig.getMessageEmailSendFailed());
                mView.addObject("command", new Article());
                mView.setViewName(this.createArticlePage);
            }
            catch (CoreServiceException csx)
            {
                ERROR_RECORDER.error(csx.getMessage(), csx);

                mView.addObject(Constants.ERROR_MESSAGE, appConfig.getMessageEmailSendFailed());
                mView.addObject("command", new Article());
                mView.setViewName(this.createArticlePage);
            }

            if (DEBUG)
            {
                DEBUGGER.debug("ModelAndView: {}", mView);
            }
        }
        else
        {
            mView.setViewName(appConfig.getUnavailablePage());
        }

        return mView;
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public final ModelAndView submitArticleSearch(@ModelAttribute("request") final SearchRequest request, final BindingResult bindResult)
    {
        final String methodName = KnowledgeBaseController.CNAME + "#submitArticleSearch(@ModelAttribute(\"searchReq\") final SearchRequest searchReq, final BindingResult bindResult)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("SearchRequest: {}", request);
            DEBUGGER.debug("BindingResult: {}", bindResult);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final ISearchProcessor searchProcessor = new SearchProcessorImpl();
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

        if ((userAccount != null) && (userAccount.getStatus() == LoginStatus.EXPIRED))
        {
            // redirect to password page
            mView = new ModelAndView(new RedirectView());
            mView.setViewName(appConfig.getExpiredRedirect());
            mView.addObject(Constants.ERROR_MESSAGE, appConfig.getMessagePasswordExpired());

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
                IUserControlService control = new UserControlServiceImpl();

                boolean isUserAuthorized = control.isUserAuthorizedForService(userAccount.getGuid(), this.serviceId);

                if (DEBUG)
                {
                    DEBUGGER.debug("isUserAuthorized: {}", isUserAuthorized);
                }

                if (isUserAuthorized)
                {
                    RequestHostInfo reqInfo = new RequestHostInfo();
                    reqInfo.setHostName(hRequest.getRemoteHost());
                    reqInfo.setHostAddress(hRequest.getRemoteAddr());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
                    }

                    request.setRequestInfo(reqInfo);
                    request.setUserAccount(userAccount);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("SearchRequest: {}", request);
                    }

                    SearchResponse searchRes = searchProcessor.doArticleSearch(request);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("VirtualServiceResponse: {}", searchRes);
                    }

                    if (searchRes.getRequestStatus() == CoreServicesStatus.SUCCESS)
                    {
                        mView.addObject(Constants.SEARCH_RESULTS, searchRes.getResults());
                    }
                    else
                    {
                        mView.addObject(Constants.ERROR_RESPONSE, searchRes.getResponse());
                    }

                    mView.addObject("command", new SearchRequest());
                    mView.addObject("isHelpSearch", true);
                    mView.setViewName(this.defaultHandler);
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
            catch (SearchRequestException srx)
            {
                ERROR_RECORDER.error(srx.getMessage(), srx);

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
