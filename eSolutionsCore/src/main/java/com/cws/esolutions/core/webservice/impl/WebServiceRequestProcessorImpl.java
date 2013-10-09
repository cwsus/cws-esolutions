/**
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
package com.cws.esolutions.core.webservice.impl;

import javax.jws.WebMethod;
import javax.jws.WebService;

import com.cws.esolutions.core.processors.dto.SearchRequest;
import com.cws.esolutions.core.processors.dto.ArticleRequest;
import com.cws.esolutions.core.processors.dto.SearchResponse;
import com.cws.esolutions.core.processors.dto.ArticleResponse;
import com.cws.esolutions.core.processors.dto.DNSServiceRequest;
import com.cws.esolutions.core.processors.dto.DNSServiceResponse;
import com.cws.esolutions.core.processors.exception.DNSServiceException;
import com.cws.esolutions.security.processors.dto.AuthenticationRequest;
import com.cws.esolutions.security.processors.dto.AuthenticationResponse;
import com.cws.esolutions.core.processors.exception.KnowledgeBaseException;
import com.cws.esolutions.core.processors.exception.SearchRequestException;
import com.cws.esolutions.security.processors.exception.AuthenticationException;
import com.cws.esolutions.core.webservice.interfaces.IWebServiceRequestProcessor;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.listeners
 * requestProcessor.java
 *
 *
 *
 * $Id: $
 * $Author: $
 * $Date: $
 * $Revision: $
 * @author kmhuntly@gmail.com
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Nov 21, 2012 3:47:42 PM
 *     Created.
 */
@WebService(endpointInterface = "com.cws.esolutions.core.webservice.interfaces.IWebServiceRequestProcessor", portName = "AgentRequestProcessorPort", serviceName = "AgentRequestProcessor", targetNamespace = "http://agent.caspersbox.corp/s?q=esolutions")
public class WebServiceRequestProcessorImpl implements IWebServiceRequestProcessor
{
    /*
     * Authentication Processing
     */
    @WebMethod
    @Override
    public AuthenticationResponse processAgentLogon(final AuthenticationRequest request) throws AuthenticationException
    {
        final String methodName = IWebServiceRequestProcessor.CNAME + "#processAgentLogon(final AuthenticationRequest request) throws AuthenticationException";
        
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

    @WebMethod
    @Override
    public AuthenticationResponse obtainUserSecurityConfig(final AuthenticationRequest request) throws AuthenticationException
    {
        final String methodName = IWebServiceRequestProcessor.CNAME + "#obtainUserSecurityConfig(final AuthenticationRequest request)";
        
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

    @WebMethod
    @Override
    public AuthenticationResponse verifyUserSecurityConfig(final AuthenticationRequest request) throws AuthenticationException
    {
        final String methodName = IWebServiceRequestProcessor.CNAME + "#verifyUserSecurityConfig(final AuthenticationRequest request)";
        
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

    @WebMethod
    @Override
    public DNSServiceResponse performLookup(final DNSServiceRequest request) throws DNSServiceException
    {
        final String methodName = IWebServiceRequestProcessor.CNAME + "#performLookup(final DNSServiceRequest request) throws DNSServiceException";

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

    @WebMethod
    @Override
    public DNSServiceResponse getDataFromDatabase(final DNSServiceRequest request) throws DNSServiceException
    {
        final String methodName = IWebServiceRequestProcessor.CNAME + "#getDataFromDatabase(final DNSServiceRequest request)";

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

    @WebMethod
    @Override
    public DNSServiceResponse createNewService(final DNSServiceRequest request) throws DNSServiceException
    {
        final String methodName = IWebServiceRequestProcessor.CNAME + "#createNewService(final DNSServiceRequest request) throws DNSServiceException";

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

    @WebMethod
    @Override
    public DNSServiceResponse pushNewService(final DNSServiceRequest request) throws DNSServiceException
    {
        final String methodName = IWebServiceRequestProcessor.CNAME + "#pushNewService(final DNSServiceRequest request) throws DNSServiceException";

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

    @WebMethod
    @Override
    public DNSServiceResponse performSiteTransfer(final DNSServiceRequest request) throws DNSServiceException
    {
        final String methodName = IWebServiceRequestProcessor.CNAME + "#performSiteTransfer(final DNSServiceRequest request) throws DNSServiceException";

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

    @WebMethod
    @Override
    public ArticleResponse addNewArticle(final ArticleRequest request) throws KnowledgeBaseException
    {
        final String methodName = IWebServiceRequestProcessor.CNAME + "#getArticle(final ArticleRequest request) throws KnowledgeBaseException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ArticleRequest: ", request);
        }

        ArticleResponse response = kbase.addNewArticle(request);

        if (DEBUG)
        {
            DEBUGGER.debug("ArticleResponse: ", response);
        }

        return response;
    }

    @WebMethod
    @Override
    public ArticleResponse updateArticle(final ArticleRequest request) throws KnowledgeBaseException
    {
        final String methodName = IWebServiceRequestProcessor.CNAME + "#updateArticle(final ArticleRequest request) throws KnowledgeBaseException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ArticleRequest: ", request);
        }

        ArticleResponse response = kbase.updateArticle(request);

        if (DEBUG)
        {
            DEBUGGER.debug("ArticleResponse: ", response);
        }

        return response;
    }

    @WebMethod
    @Override
    public ArticleResponse approveArticle(final ArticleRequest request) throws KnowledgeBaseException
    {
        final String methodName = IWebServiceRequestProcessor.CNAME + "#approveArticle(final ArticleRequest request) throws KnowledgeBaseException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ArticleRequest: ", request);
        }

        ArticleResponse response = kbase.approveArticle(request);

        if (DEBUG)
        {
            DEBUGGER.debug("ArticleResponse: ", response);
        }

        return response;
    }

    @WebMethod
    @Override
    public ArticleResponse rejectArticle(final ArticleRequest request) throws KnowledgeBaseException
    {
        final String methodName = IWebServiceRequestProcessor.CNAME + "#rejectArticle(final ArticleRequest request) throws KnowledgeBaseException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ArticleRequest: ", request);
        }

        ArticleResponse response = kbase.rejectArticle(request);

        if (DEBUG)
        {
            DEBUGGER.debug("ArticleResponse: ", response);
        }

        return response;
    }

    @WebMethod
    @Override
    public ArticleResponse deleteArticle(final ArticleRequest request) throws KnowledgeBaseException
    {
        final String methodName = IWebServiceRequestProcessor.CNAME + "#deleteArticle(final ArticleRequest request) throws KnowledgeBaseException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ArticleRequest: ", request);
        }

        ArticleResponse response = kbase.deleteArticle(request);

        if (DEBUG)
        {
            DEBUGGER.debug("ArticleResponse: ", response);
        }

        return response;
    }

    @WebMethod
    @Override
    public ArticleResponse getArticle(final ArticleRequest request) throws KnowledgeBaseException
    {
        final String methodName = IWebServiceRequestProcessor.CNAME + "#getArticle(final ArticleRequest request) throws KnowledgeBaseException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ArticleRequest: ", request);
        }

        ArticleResponse response = kbase.getArticle(request);

        if (DEBUG)
        {
            DEBUGGER.debug("ArticleResponse: ", response);
        }

        return response;
    }

    @WebMethod
    @Override
    public ArticleResponse getPendingArticles(final ArticleRequest request) throws KnowledgeBaseException
    {
        final String methodName = IWebServiceRequestProcessor.CNAME + "#getPendingArticles(final ArticleRequest request) throws KnowledgeBaseException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        ArticleResponse response = kbase.getPendingArticles(request);

        if (DEBUG)
        {
            DEBUGGER.debug("ArticleResponse: ", response);
        }

        return response;
    }

    @WebMethod
    @Override
    public SearchResponse doArticleSearch(final SearchRequest request) throws SearchRequestException
    {
        final String methodName = IWebServiceRequestProcessor.CNAME + "#doArticleSearch(final request request) throws SearchRequestException";

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

    @WebMethod
    @Override
    public SearchResponse doServerSearch(final SearchRequest request) throws SearchRequestException
    {
        final String methodName = IWebServiceRequestProcessor.CNAME + "#doServerSearch(final request request) throws SearchRequestException";

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

    @WebMethod
    @Override
    public SearchResponse doMessageSearch(final SearchRequest request) throws SearchRequestException
    {
        final String methodName = IWebServiceRequestProcessor.CNAME + "#doMessageSearch(final SearchRequest request) throws SearchRequestException";

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

    @WebMethod
    @Override
    public SearchResponse doApplicationSearch(final SearchRequest request) throws SearchRequestException
    {
        final String methodName = IWebServiceRequestProcessor.CNAME + "#doApplicationSearch(final SearchRequest request) throws SearchRequestException";
        
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

    @WebMethod
    @Override
    public SearchResponse doProjectSearch(final SearchRequest request) throws SearchRequestException
    {
        final String methodName = IWebServiceRequestProcessor.CNAME + "#doProjectSearch(final SearchRequest request) throws SearchRequestException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("SearchRequest: ", request);
        }

        SearchResponse response = searchSvc.doProjectSearch(request);

        if (DEBUG)
        {
            DEBUGGER.debug("SearchResponse: ", response);
        }

        return response;
    }
}