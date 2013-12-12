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
package com.cws.esolutions.core.ws.impl;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Use;
import javax.jws.soap.SOAPBinding.Style;

import com.cws.esolutions.core.processors.dto.SearchRequest;
import com.cws.esolutions.core.processors.dto.SearchResponse;
import com.cws.esolutions.core.processors.dto.DNSServiceRequest;
import com.cws.esolutions.core.processors.dto.DNSServiceResponse;
import com.cws.esolutions.core.processors.dto.KnowledgeBaseRequest;
import com.cws.esolutions.core.processors.dto.KnowledgeBaseResponse;
import com.cws.esolutions.core.processors.exception.DNSServiceException;
import com.cws.esolutions.security.processors.dto.AuthenticationRequest;
import com.cws.esolutions.security.processors.dto.AuthenticationResponse;
import com.cws.esolutions.core.ws.interfaces.ICoreRequestProcessorService;
import com.cws.esolutions.core.processors.exception.KnowledgeBaseException;
import com.cws.esolutions.core.processors.exception.SearchRequestException;
import com.cws.esolutions.security.processors.exception.AuthenticationException;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.dao.interfaces
 * File: IPackageDataDAO.java
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Jan 4, 2013 3:36:54 PM
 *     Created.
 */
/**
 * @see com.cws.esolutions.core.dao.processors.interfaces.IKnowledgeBaseDAO
 */
@WebService(targetNamespace = "http://esolutions.caspersbox.corp/s?q=esolutions",
    portName = "CoreRequestProcessorServicePort",
    serviceName = "CoreRequestProcessorService")
@SOAPBinding(style=Style.RPC, use=Use.LITERAL)
public class CoreRequestProcessorService implements ICoreRequestProcessorService
{
    /**
     * @see com.cws.esolutions.security.processors.interfaces.IAuthenticationProcessor#processAgentLogon(com.cws.esolutions.security.processors.dto.AuthenticationRequest)
     */
    @Override
    @WebMethod(operationName = "processAgentLogon")
    public AuthenticationResponse processAgentLogon(final AuthenticationRequest request) throws AuthenticationException
    {
        final String methodName = ICoreRequestProcessorService.CNAME + "#processAgentLogon(final AuthenticationRequest request) throws AuthenticationException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AuthenticationRequest: ", request);
        }

        AuthenticationResponse response = authProcessor.processAgentLogon(request);

        if (DEBUG)
        {
            DEBUGGER.debug("AuthenticationResponse: {}", response);
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.security.processors.interfaces.IAuthenticationProcessor#obtainUserSecurityConfig(com.cws.esolutions.security.processors.dto.AuthenticationRequest)
     */
    @Override
    @WebMethod(operationName = "obtainUserSecurityConfig")
    public AuthenticationResponse obtainUserSecurityConfig(final AuthenticationRequest request) throws AuthenticationException
    {
        final String methodName = ICoreRequestProcessorService.CNAME + "#obtainUserSecurityConfig(final AuthenticationRequest request)";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AuthenticationRequest: ", request);
        }

        AuthenticationResponse response = authProcessor.obtainUserSecurityConfig(request);

        if (DEBUG)
        {
            DEBUGGER.debug("AuthenticationResponse: {}", response);
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.security.processors.interfaces.IAuthenticationProcessor#verifyUserSecurityConfig(com.cws.esolutions.security.processors.dto.AuthenticationRequest)
     */
    @Override
    @WebMethod(operationName = "verifyUserSecurityConfig")
    public AuthenticationResponse verifyUserSecurityConfig(final AuthenticationRequest request) throws AuthenticationException
    {
        final String methodName = ICoreRequestProcessorService.CNAME + "#verifyUserSecurityConfig(final AuthenticationRequest request)";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AuthenticationRequest: ", request);
        }

        AuthenticationResponse response = authProcessor.verifyUserSecurityConfig(request);

        if (DEBUG)
        {
            DEBUGGER.debug("AuthenticationResponse: {}", response);
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.core.processors.interfaces.IDNSServiceRequestProcessor#performLookup(com.cws.esolutions.core.processors.dto.DNSServiceRequest)
     */
    @Override
    @WebMethod(operationName = "performLookup")
    public DNSServiceResponse performLookup(final DNSServiceRequest request) throws DNSServiceException
    {
        final String methodName = ICoreRequestProcessorService.CNAME + "#performLookup(final DNSServiceRequest request) throws DNSServiceException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("DNSServiceRequest: ", request);
        }

        DNSServiceResponse response = dnsSvc.performLookup(request);

        if (DEBUG)
        {
            DEBUGGER.debug("DNSServiceResponse: ", response);
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.core.processors.interfaces.IDNSServiceRequestProcessor#getDataFromDatabase(com.cws.esolutions.core.processors.dto.DNSServiceRequest)
     */
    @Override
    @WebMethod(operationName = "getDataFromDatabase")
    public DNSServiceResponse getDataFromDatabase(final DNSServiceRequest request) throws DNSServiceException
    {
        final String methodName = ICoreRequestProcessorService.CNAME + "#getDataFromDatabase(final DNSServiceRequest request)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("DNSServiceRequest: ", request);
        }

        DNSServiceResponse response = dnsSvc.performLookup(request);

        if (DEBUG)
        {
            DEBUGGER.debug("DNSServiceResponse: ", response);
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.core.processors.interfaces.IDNSServiceRequestProcessor#createNewService(com.cws.esolutions.core.processors.dto.DNSServiceRequest)
     */
    @Override
    @WebMethod(operationName = "createNewService")
    public DNSServiceResponse createNewService(final DNSServiceRequest request) throws DNSServiceException
    {
        final String methodName = ICoreRequestProcessorService.CNAME + "#createNewService(final DNSServiceRequest request) throws DNSServiceException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("DNSServiceRequest: ", request);
        }

        DNSServiceResponse response = dnsSvc.createNewService(request);

        if (DEBUG)
        {
            DEBUGGER.debug("DNSServiceResponse: ", response);
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.core.processors.interfaces.IDNSServiceRequestProcessor#pushNewService(com.cws.esolutions.core.processors.dto.DNSServiceRequest)
     */
    @Override
    @WebMethod(operationName = "pushNewService")
    public DNSServiceResponse pushNewService(final DNSServiceRequest request) throws DNSServiceException
    {
        final String methodName = ICoreRequestProcessorService.CNAME + "#pushNewService(final DNSServiceRequest request) throws DNSServiceException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("DNSServiceRequest: ", request);
        }

        DNSServiceResponse response = dnsSvc.pushNewService(request);

        if (DEBUG)
        {
            DEBUGGER.debug("DNSServiceResponse: {}", response);
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.core.processors.interfaces.IDNSServiceRequestProcessor#performSiteTransfer(com.cws.esolutions.core.processors.dto.DNSServiceRequest)
     */
    @Override
    @WebMethod(operationName = "performSiteTransfer")
    public DNSServiceResponse performSiteTransfer(final DNSServiceRequest request) throws DNSServiceException
    {
        final String methodName = ICoreRequestProcessorService.CNAME + "#performSiteTransfer(final DNSServiceRequest request) throws DNSServiceException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("DNSServiceRequest: ", request);
        }

        DNSServiceResponse response = dnsSvc.performSiteTransfer(request);

        if (DEBUG)
        {
            DEBUGGER.debug("DNSServiceResponse: ", response);
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.core.processors.interfaces.IKnowledgeBaseProcessor#addNewArticle(com.cws.esolutions.core.processors.dto.KnowledgeBaseRequest)
     */
    @Override
    @WebMethod(operationName = "addNewArticle")
    public KnowledgeBaseResponse addNewArticle(final KnowledgeBaseRequest request) throws KnowledgeBaseException
    {
        final String methodName = ICoreRequestProcessorService.CNAME + "#getArticle(final KnowledgeBaseRequest request) throws KnowledgeBaseException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("KnowledgeBaseRequest: ", request);
        }

        KnowledgeBaseResponse response = kbase.addNewArticle(request);

        if (DEBUG)
        {
            DEBUGGER.debug("KnowledgeBaseResponse: ", response);
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.core.processors.interfaces.IKnowledgeBaseProcessor#updateArticle(com.cws.esolutions.core.processors.dto.KnowledgeBaseRequest)
     */
    @Override
    @WebMethod(operationName = "updateArticle")
    public KnowledgeBaseResponse updateArticle(final KnowledgeBaseRequest request) throws KnowledgeBaseException
    {
        final String methodName = ICoreRequestProcessorService.CNAME + "#updateArticle(final KnowledgeBaseRequest request) throws KnowledgeBaseException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("KnowledgeBaseRequest: ", request);
        }

        KnowledgeBaseResponse response = kbase.updateArticle(request);

        if (DEBUG)
        {
            DEBUGGER.debug("KnowledgeBaseResponse: ", response);
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.core.processors.interfaces.IKnowledgeBaseProcessor#updateArticleStatus(com.cws.esolutions.core.processors.dto.KnowledgeBaseRequest)
     */
    @Override
    @WebMethod(operationName = "updateArticleStatus")
    public KnowledgeBaseResponse updateArticleStatus(final KnowledgeBaseRequest request) throws KnowledgeBaseException
    {
        final String methodName = ICoreRequestProcessorService.CNAME + "#updateArticleStatus(final KnowledgeBaseRequest request) throws KnowledgeBaseException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("KnowledgeBaseRequest: ", request);
        }

        KnowledgeBaseResponse response = kbase.updateArticleStatus(request);

        if (DEBUG)
        {
            DEBUGGER.debug("KnowledgeBaseResponse: ", response);
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.core.processors.interfaces.IKnowledgeBaseProcessor#getArticle(com.cws.esolutions.core.processors.dto.KnowledgeBaseRequest)
     */
    @Override
    @WebMethod(operationName = "getArticle")
    public KnowledgeBaseResponse getArticle(final KnowledgeBaseRequest request) throws KnowledgeBaseException
    {
        final String methodName = ICoreRequestProcessorService.CNAME + "#getArticle(final KnowledgeBaseRequest request) throws KnowledgeBaseException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("KnowledgeBaseRequest: ", request);
        }

        KnowledgeBaseResponse response = kbase.getArticle(request);

        if (DEBUG)
        {
            DEBUGGER.debug("KnowledgeBaseResponse: ", response);
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.core.processors.interfaces.IKnowledgeBaseProcessor#getPendingArticles(com.cws.esolutions.core.processors.dto.KnowledgeBaseRequest)
     */
    @Override
    @WebMethod(operationName = "getPendingArticles")
    public KnowledgeBaseResponse getPendingArticles(final KnowledgeBaseRequest request) throws KnowledgeBaseException
    {
        final String methodName = ICoreRequestProcessorService.CNAME + "#getPendingArticles(final KnowledgeBaseRequest request) throws KnowledgeBaseException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        KnowledgeBaseResponse response = kbase.getPendingArticles(request);

        if (DEBUG)
        {
            DEBUGGER.debug("KnowledgeBaseResponse: ", response);
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.core.processors.interfaces.IKnowledgeBaseProcessor#getTopArticles(com.cws.esolutions.core.processors.dto.KnowledgeBaseRequest)
     */
    @Override
    @WebMethod(operationName = "getTopArticles")
    public KnowledgeBaseResponse getTopArticles(final KnowledgeBaseRequest request) throws KnowledgeBaseException
    {
        final String methodName = ICoreRequestProcessorService.CNAME + "#getTopArticles(final KnowledgeBaseRequest request) throws KnowledgeBaseException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        KnowledgeBaseResponse response = kbase.getTopArticles(request);

        if (DEBUG)
        {
            DEBUGGER.debug("KnowledgeBaseResponse: ", response);
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.core.processors.interfaces.ISearchProcessor#doArticleSearch(com.cws.esolutions.core.processors.dto.SearchRequest)
     */
    @Override
    @WebMethod(operationName = "doArticleSearch")
    public SearchResponse doArticleSearch(final SearchRequest request) throws SearchRequestException
    {
        final String methodName = ICoreRequestProcessorService.CNAME + "#doArticleSearch(final request request) throws SearchRequestException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("request: ", request);
        }

        SearchResponse response = searchSvc.doArticleSearch(request);

        if (DEBUG)
        {
            DEBUGGER.debug("SearchResponse: ", response);
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.core.processors.interfaces.ISearchProcessor#doServerSearch(com.cws.esolutions.core.processors.dto.SearchRequest)
     */
    @Override
    @WebMethod(operationName = "doServerSearch")
    public SearchResponse doServerSearch(final SearchRequest request) throws SearchRequestException
    {
        final String methodName = ICoreRequestProcessorService.CNAME + "#doServerSearch(final request request) throws SearchRequestException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("request: ", request);
        }

        SearchResponse response = searchSvc.doArticleSearch(request);

        if (DEBUG)
        {
            DEBUGGER.debug("SearchResponse: ", response);
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.core.processors.interfaces.ISearchProcessor#doMessageSearch(com.cws.esolutions.core.processors.dto.SearchRequest)
     */
    @Override
    @WebMethod(operationName = "doMessageSearch")
    public SearchResponse doMessageSearch(final SearchRequest request) throws SearchRequestException
    {
        final String methodName = ICoreRequestProcessorService.CNAME + "#doMessageSearch(final SearchRequest request) throws SearchRequestException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("SearchRequest: ", request);
        }

        SearchResponse response = searchSvc.doArticleSearch(request);

        if (DEBUG)
        {
            DEBUGGER.debug("SearchResponse: ", response);
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.core.processors.interfaces.ISearchProcessor#doApplicationSearch(com.cws.esolutions.core.processors.dto.SearchRequest)
     */
    @Override
    @WebMethod(operationName = "doApplicationSearch")
    public SearchResponse doApplicationSearch(final SearchRequest request) throws SearchRequestException
    {
        final String methodName = ICoreRequestProcessorService.CNAME + "#doApplicationSearch(final SearchRequest request) throws SearchRequestException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("SearchRequest: ", request);
        }

        SearchResponse response = searchSvc.doApplicationSearch(request);

        if (DEBUG)
        {
            DEBUGGER.debug("SearchResponse: ", response);
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.core.processors.interfaces.ISearchProcessor#doServiceSearch(com.cws.esolutions.core.processors.dto.SearchRequest)
     */
    @Override
    @WebMethod(operationName = "doServiceSearch")
    public SearchResponse doServiceSearch(final SearchRequest request) throws SearchRequestException
    {
        final String methodName = ICoreRequestProcessorService.CNAME + "#doServiceSearch(final SearchRequest request) throws SearchRequestException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("SearchRequest: ", request);
        }

        SearchResponse response = searchSvc.doServiceSearch(request);

        if (DEBUG)
        {
            DEBUGGER.debug("SearchResponse: ", response);
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.core.processors.interfaces.ISearchProcessor#doSiteSearch(com.cws.esolutions.core.processors.dto.SearchRequest)
     */
    @Override
    @WebMethod(operationName = "doSiteSearch")
    public SearchResponse doSiteSearch(final SearchRequest request) throws SearchRequestException
    {
        final String methodName = ICoreRequestProcessorService.CNAME + "#doSiteSearch(final SearchRequest request) throws SearchRequestException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("SearchRequest: ", request);
        }

        SearchResponse response = searchSvc.doSiteSearch(request);

        if (DEBUG)
        {
            DEBUGGER.debug("SearchResponse: ", response);
        }

        return response;
    }
}
