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
package com.cws.esolutions.core.ws.impl;

import javax.jws.WebParam;
import javax.jws.WebMethod;
import javax.jws.WebService;

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
/**
 * eSolutionsCore
 * com.cws.esolutions.core.webservice.impl
 * CoreRequestProcessorService.java
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
@WebService(targetNamespace = "http://esolutions.caspersbox.corp/s?q=esolutions",
    portName = "CoreRequestProcessorServicePort",
    serviceName = "CoreRequestProcessorServiceService",
    wsdlLocation = "wsdl/CoreRequestProcessorService.wsdl")
public class CoreRequestProcessorService implements ICoreRequestProcessorService
{
    /*
     * Authentication Processing
     */
    @Override
    @WebMethod(operationName = "processAgentLogon", action = "urn:ProcessAgentLogon")
    public AuthenticationResponse processAgentLogon(final @WebParam(name = "request") AuthenticationRequest request) throws AuthenticationException
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

    @Override
    @WebMethod(operationName = "obtainUserSecurityConfig", action = "urn:ObtainUserSecurityConfig")
    public AuthenticationResponse obtainUserSecurityConfig(final @WebParam(name = "request") AuthenticationRequest request) throws AuthenticationException
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

    @Override
    @WebMethod(operationName = "verifyUserSecurityConfig", action = "urn:VerifyUserSecurityConfig")
    public AuthenticationResponse verifyUserSecurityConfig(final @WebParam(name = "request") AuthenticationRequest request) throws AuthenticationException
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

    @Override
    @WebMethod(operationName = "performLookup", action = "urn:PerformLookup")
    public DNSServiceResponse performLookup(final @WebParam(name = "request") DNSServiceRequest request) throws DNSServiceException
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

    @Override
    @WebMethod(operationName = "getDataFromDatabase", action = "urn:GetDataFromDatabase")
    public DNSServiceResponse getDataFromDatabase(final @WebParam(name = "request") DNSServiceRequest request) throws DNSServiceException
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

    @Override
    @WebMethod(operationName = "createNewService", action = "urn:CreateNewService")
    public DNSServiceResponse createNewService(final @WebParam(name = "request") DNSServiceRequest request) throws DNSServiceException
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

    @Override
    @WebMethod(operationName = "pushNewService", action = "urn:PushNewService")
    public DNSServiceResponse pushNewService(final @WebParam(name = "request") DNSServiceRequest request) throws DNSServiceException
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

    @Override
    @WebMethod(operationName = "performSiteTransfer", action = "urn:PerformSiteTransfer")
    public DNSServiceResponse performSiteTransfer(final @WebParam(name = "request") DNSServiceRequest request) throws DNSServiceException
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

    @Override
    @WebMethod(operationName = "addNewArticle", action = "urn:AddNewArticle")
    public KnowledgeBaseResponse addNewArticle(final @WebParam(name = "request") KnowledgeBaseRequest request) throws KnowledgeBaseException
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

    @Override
    @WebMethod(operationName = "updateArticle", action = "urn:UpdateArticle")
    public KnowledgeBaseResponse updateArticle(final @WebParam(name = "request") KnowledgeBaseRequest request) throws KnowledgeBaseException
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

    @Override
    @WebMethod(operationName = "updateArticleStatus", action = "urn:UpdateArticleStatus")
    public KnowledgeBaseResponse updateArticleStatus(final @WebParam(name = "request") KnowledgeBaseRequest request) throws KnowledgeBaseException
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

    @Override
    @WebMethod(operationName = "getArticle", action = "urn:GetArticle")
    public KnowledgeBaseResponse getArticle(final @WebParam(name = "request") KnowledgeBaseRequest request) throws KnowledgeBaseException
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

    @Override
    @WebMethod(operationName = "getPendingArticles", action = "urn:GetPendingArticles")
    public KnowledgeBaseResponse getPendingArticles(final @WebParam(name = "request") KnowledgeBaseRequest request) throws KnowledgeBaseException
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

    @Override
    @WebMethod(operationName = "doArticleSearch", action = "urn:DoArticleSearch")
    public SearchResponse doArticleSearch(final @WebParam(name = "request") SearchRequest request) throws SearchRequestException
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

    @Override
    @WebMethod(operationName = "doServerSearch", action = "urn:DoServerSearch")
    public SearchResponse doServerSearch(final @WebParam(name = "request") SearchRequest request) throws SearchRequestException
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

    @Override
    @WebMethod(operationName = "doMessageSearch", action = "urn:DoMessageSearch")
    public SearchResponse doMessageSearch(final @WebParam(name = "request") SearchRequest request) throws SearchRequestException
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

    @Override
    @WebMethod(operationName = "doApplicationSearch", action = "urn:DoApplicationSearch")
    public SearchResponse doApplicationSearch(final @WebParam(name = "request") SearchRequest request) throws SearchRequestException
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

    @Override
    @WebMethod(operationName = "doProjectSearch", action = "urn:DoProjectSearch")
    public SearchResponse doProjectSearch(final @WebParam(name = "request") SearchRequest request) throws SearchRequestException
    {
        final String methodName = ICoreRequestProcessorService.CNAME + "#doProjectSearch(final SearchRequest request) throws SearchRequestException";
        
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