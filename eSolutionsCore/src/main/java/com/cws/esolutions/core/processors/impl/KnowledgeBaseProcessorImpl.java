/*
 * Copyright (c) 2009 - 2013 By: CWS, Inc.
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
 */
package com.cws.esolutions.core.processors.impl;
/**
 * @see com.cws.esolutions.core.processors.interfaces.IKnowledgeBaseProcessor
 */
import java.util.Date;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.sql.SQLException;
import org.apache.commons.lang.RandomStringUtils;

import com.cws.esolutions.security.enums.Role;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.core.processors.dto.Article;
import com.cws.esolutions.security.audit.dto.AuditEntry;
import com.cws.esolutions.security.audit.enums.AuditType;
import com.cws.esolutions.security.audit.dto.AuditRequest;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.enums.ArticleStatus;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.core.processors.dto.KnowledgeBaseRequest;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.core.processors.dto.KnowledgeBaseResponse;
import com.cws.esolutions.security.dao.usermgmt.enums.SearchRequestType;
import com.cws.esolutions.security.processors.dto.AccountControlRequest;
import com.cws.esolutions.security.audit.exception.AuditServiceException;
import com.cws.esolutions.security.processors.dto.AccountControlResponse;
import com.cws.esolutions.core.processors.exception.KnowledgeBaseException;
import com.cws.esolutions.core.processors.interfaces.IKnowledgeBaseProcessor;
import com.cws.esolutions.security.processors.impl.AccountControlProcessorImpl;
import com.cws.esolutions.security.processors.exception.AccountControlException;
import com.cws.esolutions.security.processors.interfaces.IAccountControlProcessor;
import com.cws.esolutions.security.access.control.exception.UserControlServiceException;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.processors.impl
 * File: KnowledgeBaseProcessorImpl.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
public class KnowledgeBaseProcessorImpl implements IKnowledgeBaseProcessor
{
    /**
     * @see com.cws.esolutions.core.processors.interfaces.IKnowledgeBaseProcessor#addNewArticle(com.cws.esolutions.core.processors.dto.KnowledgeBaseRequest)
     */
    @Override
    public KnowledgeBaseResponse addNewArticle(final KnowledgeBaseRequest request) throws KnowledgeBaseException
    {
        final String methodName = IKnowledgeBaseProcessor.CNAME + "#addNewArticle(final KnowledgeBaseRequest request) throws KnowledgeBaseException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("KnowledgeBaseRequest: {}", request);
        }

        KnowledgeBaseResponse response = new KnowledgeBaseResponse();

        final Article article = request.getArticle();
        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("Article: {}", article);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            boolean isServiceAuthorized = userControl.isUserAuthorizedForService(userAccount, request.getServiceId());

            if (DEBUG)
            {
                DEBUGGER.debug("isServiceAuthorized: {}", isServiceAuthorized);
            }

            if (isServiceAuthorized)
            {
                String articleId = appConfig.getArticlePrefix() + RandomStringUtils.randomAlphanumeric(appConfig.getArticleIdentifierLength());

                if (DEBUG)
                {
                    DEBUGGER.debug("articleId: {}", articleId);
                }

                List<String> insertList = new ArrayList<>(
                        Arrays.asList(
                                articleId,
                                userAccount.getGuid(),
                                article.getKeywords(),
                                article.getTitle(),
                                article.getSymptoms(),
                                article.getCause(),
                                article.getResolution()));

                if (DEBUG)
                {
                    DEBUGGER.debug("insertList: {}", insertList);
                }

                // user is authorized for request, continue
                boolean requestComplete = dao.doCreateArticle(insertList);

                if (DEBUG)
                {
                    DEBUGGER.debug("requestComplete: {}", requestComplete);
                }

                if (requestComplete)
                {
                    response.setRequestStatus(CoreServicesStatus.SUCCESS);
                    response.setArticleId(articleId);
                }
                else
                {
                    response.setRequestStatus(CoreServicesStatus.FAILURE);
                }
            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.UNAUTHORIZED);
            }

            if (DEBUG)
            {
                DEBUGGER.debug("KnowledgeBaseResponse: {}", response);
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new KnowledgeBaseException(sqx.getMessage(), sqx);
        }
        catch (UserControlServiceException ucsx)
        {
            ERROR_RECORDER.error(ucsx.getMessage(), ucsx);
            
            throw new KnowledgeBaseException(ucsx.getMessage(), ucsx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.CREATEARTICLE);
                auditEntry.setUserAccount(userAccount);
                auditEntry.setApplicationId(request.getApplicationId());
                auditEntry.setApplicationName(request.getApplicationName());

                if (DEBUG)
                {
                    DEBUGGER.debug("AuditEntry: {}", auditEntry);
                }

                AuditRequest auditRequest = new AuditRequest();
                auditRequest.setAuditEntry(auditEntry);

                if (DEBUG)
                {
                    DEBUGGER.debug("AuditRequest: {}", auditRequest);
                }

                auditor.auditRequest(auditRequest);
            }
            catch (AuditServiceException asx)
            {
                ERROR_RECORDER.error(asx.getMessage(), asx);
            }
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.core.processors.interfaces.IKnowledgeBaseProcessor#updateArticle(com.cws.esolutions.core.processors.dto.KnowledgeBaseRequest)
     */
    @Override
    public KnowledgeBaseResponse updateArticle(final KnowledgeBaseRequest request) throws KnowledgeBaseException
    {
        final String methodName = IKnowledgeBaseProcessor.CNAME + "#updateArticle(final KnowledgeBaseRequest request) throws KnowledgeBaseException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("KnowledgeBaseRequest: {}", request);
        }

        KnowledgeBaseResponse response = new KnowledgeBaseResponse();

        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            boolean isServiceAuthorized = userControl.isUserAuthorizedForService(userAccount, request.getServiceId());

            if (DEBUG)
            {
                DEBUGGER.debug("isServiceAuthorized: {}", isServiceAuthorized);
            }

            if (isServiceAuthorized)
            {
                Article article = request.getArticle();

                if (DEBUG)
                {
                    DEBUGGER.debug("Article: {}", article);
                }

                // user authorized, figure out what to do here
                List<String> dataList = new ArrayList<>(
                        Arrays.asList(
                                article.getArticleId(),
                                article.getKeywords(),
                                article.getTitle(),
                                article.getSymptoms(),
                                article.getCause(),
                                article.getResolution(),
                                article.getModifiedBy().getGuid()));

                if (DEBUG)
                {
                    DEBUGGER.debug("dataList: {}", dataList);
                }

                // user is authorized for request, continue
                boolean requestComplete = dao.doUpdateArticle(dataList);

                if (DEBUG)
                {
                    DEBUGGER.debug("requestComplete: {}", requestComplete);
                }

                if (requestComplete)
                {
                    response.setRequestStatus(CoreServicesStatus.SUCCESS);
                    response.setArticle(article);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("KnowledgeBaseResponse: {}", response);
                    }
                }
                else
                {
                    response.setRequestStatus(CoreServicesStatus.FAILURE);
                }
            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.UNAUTHORIZED);
            }

            if (DEBUG)
            {
                DEBUGGER.debug("KnowledgeBaseResponse: {}", response);
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new KnowledgeBaseException(sqx.getMessage(), sqx);
        }
        catch (UserControlServiceException ucsx)
        {
            ERROR_RECORDER.error(ucsx.getMessage(), ucsx);
            
            throw new KnowledgeBaseException(ucsx.getMessage(), ucsx);
        }
        finally
        {
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.UPDATEARTICLE);
                auditEntry.setUserAccount(userAccount);
                auditEntry.setApplicationId(request.getApplicationId());
                auditEntry.setApplicationName(request.getApplicationName());

                if (DEBUG)
                {
                    DEBUGGER.debug("AuditEntry: {}", auditEntry);
                }

                AuditRequest auditRequest = new AuditRequest();
                auditRequest.setAuditEntry(auditEntry);

                if (DEBUG)
                {
                    DEBUGGER.debug("AuditRequest: {}", auditRequest);
                }

                auditor.auditRequest(auditRequest);
            }
            catch (AuditServiceException asx)
            {
                ERROR_RECORDER.error(asx.getMessage(), asx);
            }
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.core.processors.interfaces.IKnowledgeBaseProcessor#updateArticleStatus(com.cws.esolutions.core.processors.dto.KnowledgeBaseRequest)
     */
    @Override
    public KnowledgeBaseResponse updateArticleStatus(final KnowledgeBaseRequest request) throws KnowledgeBaseException
    {
        final String methodName = IKnowledgeBaseProcessor.CNAME + "#updateArticleStatus(final KnowledgeBaseRequest request) throws KnowledgeBaseException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("KnowledgeBaseRequest: {}", request);
        }

        KnowledgeBaseResponse response = new KnowledgeBaseResponse();

        final Article article = request.getArticle();
        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("Article: {}", article);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }
        
        try
        {
            boolean isServiceAuthorized = userControl.isUserAuthorizedForService(userAccount, request.getServiceId());

            if (DEBUG)
            {
                DEBUGGER.debug("isServiceAuthorized: {}", isServiceAuthorized);
            }

            if (isServiceAuthorized)
            {
                boolean isComplete = dao.updateArticleStatus(article.getArticleId(), userAccount.getUsername(), article.getArticleStatus().name());

                if (DEBUG)
                {
                    DEBUGGER.debug("isComplete: {}", isComplete);
                }

                if (isComplete)
                {
                    response.setRequestStatus(CoreServicesStatus.SUCCESS);
                    response.setArticle(article);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("KnowledgeBaseResponse: {}", response);
                    }
                }
                else
                {
                    response.setRequestStatus(CoreServicesStatus.FAILURE);
                }
            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.UNAUTHORIZED);
            }

            if (DEBUG)
            {
                DEBUGGER.debug("KnowledgeBaseResponse: {}", response);
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new KnowledgeBaseException(sqx.getMessage(), sqx);
        }
        catch (UserControlServiceException ucsx)
        {
            ERROR_RECORDER.error(ucsx.getMessage(), ucsx);
            
            throw new KnowledgeBaseException(ucsx.getMessage(), ucsx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setUserAccount(userAccount);
                auditEntry.setApplicationId(request.getApplicationId());
                auditEntry.setApplicationName(request.getApplicationName());

                switch (article.getArticleStatus())
                {
                    case APPROVED:
                        auditEntry.setAuditType(AuditType.APPROVEARTICLE);

                        break;
                    case REJECTED:
                        auditEntry.setAuditType(AuditType.REJECTARTICLE);

                        break;
                    case DELETED:
                        auditEntry.setAuditType(AuditType.DELETEARTICLE);

                        break;
                    default:
                        auditEntry.setAuditType(AuditType.UPDATEARTICLE);

                        break;
                }

                if (DEBUG)
                {
                    DEBUGGER.debug("AuditEntry: {}", auditEntry);
                }

                AuditRequest auditRequest = new AuditRequest();
                auditRequest.setAuditEntry(auditEntry);

                if (DEBUG)
                {
                    DEBUGGER.debug("AuditRequest: {}", auditRequest);
                }

                auditor.auditRequest(auditRequest);
            }
            catch (AuditServiceException asx)
            {
                ERROR_RECORDER.error(asx.getMessage(), asx);
            }
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.core.processors.interfaces.IKnowledgeBaseProcessor#getArticle(com.cws.esolutions.core.processors.dto.KnowledgeBaseRequest)
     */
    @Override
    public KnowledgeBaseResponse getArticle(final KnowledgeBaseRequest request) throws KnowledgeBaseException
    {
        final String methodName = IKnowledgeBaseProcessor.CNAME + "#getArticle(final KnowledgeBaseRequest request) throws KnowledgeBaseException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("KnowledgeBaseRequest: {}", request);
        }

        KnowledgeBaseResponse response = new KnowledgeBaseResponse();

        final IAccountControlProcessor acctControl = new AccountControlProcessorImpl();

        // NO authorization required here, anyone can LOOK at this stuff
        // no audit either
        try
        {
            Article article = request.getArticle();

            if (DEBUG)
            {
                DEBUGGER.debug("Article: {}", article);
            }

            List<Object> responseList = dao.retrieveArticle(article.getArticleId(), request.isReview());

            if (DEBUG)
            {
                DEBUGGER.debug("responseList: {}", responseList);
            }

            if ((responseList != null) && (responseList.size() != 0))
            {
                article = new Article();
                article.setPageHits((Integer) responseList.get(0));
                article.setArticleId((String) responseList.get(1));
                article.setCreateDate((Date) responseList.get(2));
                article.setKeywords((String) responseList.get(4));
                article.setTitle((String) responseList.get(5));
                article.setSymptoms((String) responseList.get(6));
                article.setCause((String) responseList.get(7));
                article.setResolution((String) responseList.get(8));
                article.setArticleStatus(ArticleStatus.valueOf((String) responseList.get(9)));
                article.setReviewedOn((responseList.get(11) != null) ? (Date) responseList.get(11) : null);
                article.setModifiedOn((responseList.get(12) != null) ? (Date) responseList.get(12) : null);

                int x = 0;
                List<String> accountList = new ArrayList<>(
                        Arrays.asList(
                                (String) responseList.get(3),
                                (String) responseList.get(10),
                                (String) responseList.get(13)));

                if (DEBUG)
                {
                    DEBUGGER.debug("AccountList: {}", accountList);
                }

                for (String guid : accountList)
                {
                    UserAccount searchAccount = new UserAccount();
                    searchAccount.setGuid(guid);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("UserAccount: {}", searchAccount);
                    }

                    UserAccount svcAccount = new UserAccount();
                    svcAccount.setUsername(serviceAccount.get(0));
                    svcAccount.setGuid(serviceAccount.get(1));
                    svcAccount.setRole(Role.valueOf(serviceAccount.get(2)));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("UserAccount: {}", svcAccount);
                    }

                    AccountControlRequest searchRequest = new AccountControlRequest();
                    searchRequest.setHostInfo(request.getRequestInfo());
                    searchRequest.setUserAccount(searchAccount);
                    searchRequest.setApplicationName(request.getApplicationName());
                    searchRequest.setApplicationId(request.getApplicationId());
                    searchRequest.setSearchType(SearchRequestType.GUID);
                    searchRequest.setRequestor(svcAccount);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("AccountControlRequest: {}", searchRequest);
                    }

                    try
                    {
                        AccountControlResponse searchResponse = acctControl.loadUserAccount(searchRequest);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("AccountControlResponse: {}", searchResponse);
                        }

                        if (searchResponse.getRequestStatus() == SecurityRequestStatus.SUCCESS)
                        {
                            switch (x)
                            {
                                case 0:
                                    article.setAuthor(searchResponse.getUserAccount());

                                    break;
                                case 1:
                                    article.setModifiedBy(searchResponse.getUserAccount());

                                    break;
                                case 2:
                                    article.setReviewedBy(searchResponse.getUserAccount());

                                    break;
                                default:
                                    break;
                            }
                            
                        }
                    }
                    catch (AccountControlException acx)
                    {
                        ERROR_RECORDER.error(acx.getMessage(), acx);
                    }

                    x++;
                }

                if (DEBUG)
                {
                    DEBUGGER.debug("Article: {}", article);
                }

                response.setRequestStatus(CoreServicesStatus.SUCCESS);
                response.setArticle(article);
            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.FAILURE);
            }

            if (DEBUG)
            {
                DEBUGGER.debug("KnowledgeBaseResponse: {}", response);
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new KnowledgeBaseException(sqx.getMessage(), sqx);
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.core.processors.interfaces.IKnowledgeBaseProcessor#getPendingArticles(com.cws.esolutions.core.processors.dto.KnowledgeBaseRequest)
     */
    @Override
    public KnowledgeBaseResponse getPendingArticles(final KnowledgeBaseRequest request) throws KnowledgeBaseException
    {
        final String methodName = IKnowledgeBaseProcessor.CNAME + "#getPendingArticles(final KnowledgeBaseRequest request) throws KnowledgeBaseException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        KnowledgeBaseResponse response = new KnowledgeBaseResponse();

        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();
        final IAccountControlProcessor acctControl = new AccountControlProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            boolean isServiceAuthorized = userControl.isUserAuthorizedForService(userAccount, request.getServiceId());

            if (DEBUG)
            {
                DEBUGGER.debug("isServiceAuthorized: {}", isServiceAuthorized);
            }

            if (isServiceAuthorized)
            {
                int count = dao.getArticleCount(ArticleStatus.REVIEW.name());

                if (DEBUG)
                {
                    DEBUGGER.debug("count: {}", count);
                }

                List<Object[]> responseList = dao.searchPendingArticles(userAccount.getUsername(), request.getStartRow());

                if (DEBUG)
                {
                    DEBUGGER.debug("responseList: {}", responseList);
                }

                if ((responseList != null) && (!(responseList.isEmpty())))
                {
                    // build it here
                    List<Article> articleList = new ArrayList<>();

                    for (Object[] data : responseList)
                    {
                        Article article = new Article();
                        article.setPageHits((Integer) data[0]);
                        article.setArticleId((String) data[1]);
                        article.setCreateDate((Date) data[2]);
                        article.setKeywords((String) data[4]);
                        article.setTitle((String) data[5]);
                        article.setSymptoms((String) data[6]);
                        article.setCause((String) data[7]);
                        article.setResolution((String) data[8]);
                        article.setArticleStatus(ArticleStatus.valueOf((String) data[9]));

                        int x = 0;
                        List<String> accountList = new ArrayList<>(
                                Arrays.asList(
                                        (String) data[3],
                                        (String) data[12]));

                        if (DEBUG)
                        {
                            DEBUGGER.debug("AccountList: {}", accountList);
                        }

                        for (String guid : accountList)
                        {
                            UserAccount searchAccount = new UserAccount();
                            searchAccount.setGuid(guid);

                            if (DEBUG)
                            {
                                DEBUGGER.debug("UserAccount: {}", searchAccount);
                            }

                            UserAccount svcAccount = new UserAccount();
                            svcAccount.setUsername(serviceAccount.get(0));
                            svcAccount.setGuid(serviceAccount.get(1));
                            svcAccount.setRole(Role.valueOf(serviceAccount.get(2)));

                            if (DEBUG)
                            {
                                DEBUGGER.debug("UserAccount: {}", svcAccount);
                            }

                            AccountControlRequest searchRequest = new AccountControlRequest();
                            searchRequest.setHostInfo(request.getRequestInfo());
                            searchRequest.setUserAccount(searchAccount);
                            searchRequest.setApplicationName(request.getApplicationName());
                            searchRequest.setApplicationId(request.getApplicationId());
                            searchRequest.setSearchType(SearchRequestType.GUID);
                            searchRequest.setRequestor(svcAccount);

                            if (DEBUG)
                            {
                                DEBUGGER.debug("AccountControlRequest: {}", searchRequest);
                            }

                            try
                            {
                                AccountControlResponse searchResponse = acctControl.loadUserAccount(searchRequest);

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("AccountControlResponse: {}", searchResponse);
                                }

                                if (searchResponse.getRequestStatus() == SecurityRequestStatus.SUCCESS)
                                {
                                    switch (x)
                                    {
                                        case 0:
                                            article.setAuthor(searchResponse.getUserAccount());

                                            break;
                                        case 1:
                                            article.setModifiedBy(searchResponse.getUserAccount());

                                            break;
                                        default:
                                            break;
                                    }
                                    
                                }
                            }
                            catch (AccountControlException acx)
                            {
                                ERROR_RECORDER.error(acx.getMessage(), acx);
                            }

                            x++;
                        }

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Article: {}", article);
                        }

                        articleList.add(article);
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("articleList: {}", articleList);
                    }

                    response.setEntryCount(count);
                    response.setRequestStatus(CoreServicesStatus.SUCCESS);
                    response.setArticleList(articleList);
                }
                else
                {
                    response.setRequestStatus(CoreServicesStatus.FAILURE);
                }
            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.UNAUTHORIZED);
            }

            if (DEBUG)
            {
                DEBUGGER.debug("KnowledgeBaseResponse: {}", response);
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new KnowledgeBaseException(sqx.getMessage(), sqx);
        }
        catch (UserControlServiceException ucsx)
        {
            ERROR_RECORDER.error(ucsx.getMessage(), ucsx);
            
            throw new KnowledgeBaseException(ucsx.getMessage(), ucsx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.SHOWPENDING);
                auditEntry.setUserAccount(userAccount);
                auditEntry.setApplicationId(request.getApplicationId());
                auditEntry.setApplicationName(request.getApplicationName());

                if (DEBUG)
                {
                    DEBUGGER.debug("AuditEntry: {}", auditEntry);
                }

                AuditRequest auditRequest = new AuditRequest();
                auditRequest.setAuditEntry(auditEntry);

                if (DEBUG)
                {
                    DEBUGGER.debug("AuditRequest: {}", auditRequest);
                }

                auditor.auditRequest(auditRequest);
            }
            catch (AuditServiceException asx)
            {
                ERROR_RECORDER.error(asx.getMessage(), asx);
            }
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.core.processors.interfaces.IKnowledgeBaseProcessor#getTopArticles(com.cws.esolutions.core.processors.dto.KnowledgeBaseRequest)
     */
    @Override
    public KnowledgeBaseResponse getTopArticles(final KnowledgeBaseRequest request) throws KnowledgeBaseException
    {
        final String methodName = IKnowledgeBaseProcessor.CNAME + "#getTopArticles(final KnowledgeBaseRequest request) throws KnowledgeBaseException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        KnowledgeBaseResponse response = new KnowledgeBaseResponse();

        final IAccountControlProcessor acctControl = new AccountControlProcessorImpl();

        try
        {
            List<Object[]> responseList = dao.listTopArticles();

            if (DEBUG)
            {
                DEBUGGER.debug("responseList: {}", responseList);
            }

            if ((responseList != null) && (!(responseList.isEmpty())))
            {
                // build it here
                List<Article> articleList = new ArrayList<>();

                for (Object[] data : responseList)
                {
                    Article article = new Article();
                    article.setPageHits((Integer) data[0]);
                    article.setArticleId((String) data[1]);
                    article.setCreateDate((Date) data[2]);
                    article.setKeywords((String) data[4]);
                    article.setTitle((String) data[5]);
                    article.setSymptoms((String) data[6]);
                    article.setCause((String) data[7]);
                    article.setResolution((String) data[8]);
                    article.setArticleStatus(ArticleStatus.valueOf((String) data[9]));

                    int x = 0;
                    List<String> accountList = new ArrayList<>(
                            Arrays.asList(
                                    (String) data[3],
                                    (String) data[12]));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("AccountList: {}", accountList);
                    }

                    for (String guid : accountList)
                    {
                        UserAccount searchAccount = new UserAccount();
                        searchAccount.setGuid(guid);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("UserAccount: {}", searchAccount);
                        }

                        UserAccount svcAccount = new UserAccount();
                        svcAccount.setUsername(serviceAccount.get(0));
                        svcAccount.setGuid(serviceAccount.get(1));
                        svcAccount.setRole(Role.valueOf(serviceAccount.get(2)));

                        if (DEBUG)
                        {
                            DEBUGGER.debug("UserAccount: {}", svcAccount);
                        }

                        AccountControlRequest searchRequest = new AccountControlRequest();
                        searchRequest.setHostInfo(request.getRequestInfo());
                        searchRequest.setUserAccount(searchAccount);
                        searchRequest.setApplicationName(request.getApplicationName());
                        searchRequest.setApplicationId(request.getApplicationId());
                        searchRequest.setSearchType(SearchRequestType.GUID);
                        searchRequest.setRequestor(svcAccount);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("AccountControlRequest: {}", searchRequest);
                        }

                        try
                        {
                            AccountControlResponse searchResponse = acctControl.loadUserAccount(searchRequest);

                            if (DEBUG)
                            {
                                DEBUGGER.debug("AccountControlResponse: {}", searchResponse);
                            }

                            if (searchResponse.getRequestStatus() == SecurityRequestStatus.SUCCESS)
                            {
                                switch (x)
                                {
                                    case 0:
                                        article.setAuthor(searchResponse.getUserAccount());

                                        break;
                                    case 1:
                                        article.setModifiedBy(searchResponse.getUserAccount());

                                        break;
                                    default:
                                        break;
                                }
                                
                            }
                        }
                        catch (AccountControlException acx)
                        {
                            ERROR_RECORDER.error(acx.getMessage(), acx);
                        }

                        x++;
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Article: {}", article);
                    }

                    articleList.add(article);
                }

                if (DEBUG)
                {
                    DEBUGGER.debug("articleList: {}", articleList);
                }

                response.setRequestStatus(CoreServicesStatus.SUCCESS);
                response.setArticleList(articleList);

                if (DEBUG)
                {
                    DEBUGGER.debug("KnowledgeBaseResponse: {}", response);
                }
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new KnowledgeBaseException(sqx.getMessage(), sqx);
        }

        return response;
    }
}
