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

import org.slf4j.Logger;
import java.util.Arrays;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
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
import com.cws.esolutions.core.processors.dto.EmailMessage;
import com.cws.esolutions.core.processors.dto.SearchRequest;
import com.cws.esolutions.core.processors.dto.SearchResponse;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.dto.ArticleRequest;
import com.cws.esolutions.core.controllers.ResourceController;
import com.cws.esolutions.core.exception.CoreServiceException;
import com.cws.esolutions.core.processors.dto.ArticleResponse;
import com.cws.esolutions.core.processors.enums.ArticleStatus;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.core.processors.impl.SearchProcessorImpl;
import com.cws.esolutions.core.processors.interfaces.ISearchProcessor;
import com.cws.esolutions.core.processors.impl.KnowledgeBaseProcessorImpl;
import com.cws.esolutions.core.processors.exception.KnowledgeBaseException;
import com.cws.esolutions.core.processors.exception.SearchRequestException;
import com.cws.esolutions.core.processors.interfaces.IKnowledgeBaseProcessor;
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
    private String postURL = null;
    private String serviceId = null;
    private String requestURL = null;
    private String serviceName = null;
    private String articleList = null;
    private int newIdentifierLength = 8; // default of 8
    private String apprRejectPage = null;
    private String editArticlePage = null;
    private String showArticlePage = null;
    private String createArticlePage = null;
    private String confirmDeletePage = null;
    private String reviewArticlePage = null;
    private String createArticleEmail = null;
    private String pendingArticlesPage = null;
    private ApplicationServiceBean appConfig = null;

    private static final String CNAME = KnowledgeBaseController.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CNAME);

    public final void setPostURL(final String value)
    {
        final String methodName = KnowledgeBaseController.CNAME + "#setPostURL(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.postURL = value;
    }

    public final void setRequestURL(final String value)
    {
        final String methodName = KnowledgeBaseController.CNAME + "#setRequestURL(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.requestURL = value;
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

    public final void setApprRejectPage(final String value)
    {
        final String methodName = KnowledgeBaseController.CNAME + "#setApprRejectPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.apprRejectPage = value;
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

    public final void setConfirmDeletePage(final String value)
    {
        final String methodName = KnowledgeBaseController.CNAME + "#setConfirmDeletePage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.confirmDeletePage = value;
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

    @RequestMapping(value = "/default", method = RequestMethod.GET)
    public ModelAndView showDefaultPage()
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
            mView.addObject("postUrl", this.postURL);
            mView.addObject("command", new SearchRequest());
            mView.setViewName(appConfig.getSearchRequestPage());
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

    @RequestMapping(value = "/article/{articleId}", method = RequestMethod.GET)
    public ModelAndView showArticleDetail(@PathVariable("articleId") final String articleId)
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

                    ArticleRequest request = new ArticleRequest();
                    request.setArticle(article);
                    request.setRequestInfo(reqInfo);
                    request.setUserAccount(userAccount);
                    request.setServiceId(this.serviceId);
                    request.setIsReview(true);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("ArticleRequest: {}", request);
                    }

                    ArticleResponse response = kbase.getArticle(request);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("ArticleResponse: {}", response);
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
                        mView.addObject("postUrl", this.postURL);
                        mView.addObject("command", new SearchRequest());
                        mView.addObject(Constants.ERROR_MESSAGE, response.getResponse());
                        mView.setViewName(appConfig.getSearchRequestPage());
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
    public ModelAndView showCreateArticle()
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
            mView.setViewName(appConfig.getUnavailablePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/show-approvals", method = RequestMethod.GET)
    public ModelAndView showPendingApprovals()
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

                ArticleRequest request = new ArticleRequest();
                request.setRequestInfo(reqInfo);
                request.setUserAccount(userAccount);

                if (DEBUG)
                {
                    DEBUGGER.debug("ArticleRequest: {}", request);
                }

                ArticleResponse response = kbase.getPendingArticles(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("ArticleResponse: {}", response);
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
                        mView.addObject("postUrl", this.postURL);
                        mView.addObject("command", new SearchRequest());
                        mView.addObject(Constants.RESPONSE_MESSAGE, response.getResponse());
                        mView.setViewName(appConfig.getSearchRequestPage());
                    }
                }
                else
                {
                    mView.addObject("postUrl", this.postURL);
                    mView.addObject(Constants.ERROR_MESSAGE, response.getResponse());
                    mView.addObject("command", new SearchRequest());
                    mView.setViewName(appConfig.getSearchRequestPage());
                }
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

    @RequestMapping(value = "/create-article", method = RequestMethod.POST)
    public ModelAndView submitNewArticle(@ModelAttribute("article") final Article article, final BindingResult bindResult)
    {
        final String methodName = KnowledgeBaseController.CNAME + "#submitNewArticle(@ModelAttribute(\"article\") final Article article, final BindingResult bindResult)";

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

                article.setAuthor(userAccount.getDisplayName());
                article.setAuthorEmail(userAccount.getEmailAddr());

                if (DEBUG)
                {
                    DEBUGGER.debug("Article: {}", article);
                }

                ArticleRequest request = new ArticleRequest();
                request.setArticle(article);
                request.setRequestInfo(reqInfo);
                request.setUserAccount(userAccount);
                request.setServiceId(this.serviceId);

                if (DEBUG)
                {
                    DEBUGGER.debug("ArticleRequest: {}", request);
                }

                ArticleResponse response = kbase.addNewArticle(request);

                if (DEBUG)
                {
                    DEBUGGER.debug("ArticleResponse: {}", response);
                }

                if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    // article created
                    String emailId = RandomStringUtils.randomAlphanumeric(16);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("emailId: {}", emailId);
                    }

                    String emailBody = MessageFormat.format(IOUtils.toString(
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
                    emailMessage.setMessageSubject("[ " + emailId + " ] - " + ResourceController.returnSystemPropertyValue("nls.KnowledgeBaseServlet.KnowledgeBaseServlet",
                            "kbase.create.article.email", this.getClass().getClassLoader()));
                    emailMessage.setMessageTo(new ArrayList<String>(Arrays.asList(appConfig.getSecEmailAddr())));
                    emailMessage.setMessageFrom(new ArrayList<String>(Arrays.asList(userAccount.getEmailAddr())));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("EmailMessage: {}", emailMessage);
                    }

                    EmailUtils.sendEmailMessage(emailMessage);

                    mView.addObject(Constants.RESPONSE_MESSAGE, response.getResponse());
                    mView.setViewName(this.createArticlePage);
                }
                else
                {
                    // failure
                    mView.addObject("command", article);
                    mView.addObject(Constants.ERROR_MESSAGE, response.getResponse());
                    mView.setViewName(this.createArticlePage);
                }
            }
            catch (KnowledgeBaseException kbx)
            {
                ERROR_RECORDER.error(kbx.getMessage(), kbx);

                mView.setViewName(appConfig.getErrorResponsePage());
            }
            catch (IOException iox)
            {
                ERROR_RECORDER.error(iox.getMessage(), iox);

                mView.setViewName(appConfig.getErrorResponsePage());
            }
            catch (MessagingException mx)
            {
                ERROR_RECORDER.error(mx.getMessage(), mx);
            }
            catch (CoreServiceException csx)
            {
                ERROR_RECORDER.error(csx.getMessage(), csx);
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
    public ModelAndView submitArticleSearch(@ModelAttribute("searchReq") final SearchRequest searchReq, final BindingResult bindResult)
    {
        final String methodName = KnowledgeBaseController.CNAME + "#submitArticleSearch(@ModelAttribute(\"searchReq\") final SearchRequest searchReq, final BindingResult bindResult)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("SearchRequest: {}", searchReq);
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

                searchReq.setRequestInfo(reqInfo);
                searchReq.setUserAccount(userAccount);

                if (DEBUG)
                {
                    DEBUGGER.debug("SearchRequest: {}", searchReq);
                }

                SearchResponse searchRes = searchProcessor.doArticleSearch(searchReq);

                if (DEBUG)
                {
                    DEBUGGER.debug("VirtualServiceResponse: {}", searchRes);
                }

                if (searchRes.getRequestStatus() == CoreServicesStatus.SUCCESS)
                {
                    mView.addObject("postUrl", this.postURL);
                    mView.addObject("requestUrl", this.requestURL);
                    mView.addObject("command", new SearchRequest());
                    mView.addObject(Constants.SEARCH_RESULTS, searchRes.getResults());
                    mView.setViewName(appConfig.getSearchRequestPage());
                }
                else
                {
                    mView.addObject("postUrl", this.postURL);
                    mView.addObject("command", new SearchRequest());
                    mView.addObject(Constants.ERROR_MESSAGE, searchRes.getResponse());
                    mView.setViewName(appConfig.getSearchRequestPage());
                }
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
