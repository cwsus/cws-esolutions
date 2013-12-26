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
package com.cws.esolutions.core.processors.interfaces;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.processors.interfaces
 * File: IKnowledgeBaseProcessor.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.core.Constants;
import com.cws.esolutions.core.CoreServiceBean;
import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.core.config.xml.ApplicationConfig;
import com.cws.esolutions.core.processors.dto.KnowledgeBaseRequest;
import com.cws.esolutions.core.processors.dto.KnowledgeBaseResponse;
import com.cws.esolutions.security.processors.impl.AuditProcessorImpl;
import com.cws.esolutions.core.dao.processors.impl.KnowledgeBaseDAOImpl;
import com.cws.esolutions.security.processors.interfaces.IAuditProcessor;
import com.cws.esolutions.security.services.impl.AccessControlServiceImpl;
import com.cws.esolutions.core.dao.processors.interfaces.IKnowledgeBaseDAO;
import com.cws.esolutions.core.processors.exception.KnowledgeBaseException;
import com.cws.esolutions.security.services.interfaces.IAccessControlService;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 */
public interface IKnowledgeBaseProcessor
{
    static final IKnowledgeBaseDAO dao = new KnowledgeBaseDAOImpl();
    static final IAuditProcessor auditor = new AuditProcessorImpl();
    static final CoreServiceBean appBean = CoreServiceBean.getInstance();
    static final SecurityServiceBean secBean = SecurityServiceBean.getInstance();
    static final IAccessControlService accessControl = new AccessControlServiceImpl();
    static final List<String> serviceAccount = secBean.getConfigData().getSecurityConfig().getServiceAccount();

    static final String CNAME = IKnowledgeBaseProcessor.class.getName();
    static final ApplicationConfig appConfig = appBean.getConfigData().getAppConfig();

    static final Logger WARN_RECORDER = LoggerFactory.getLogger(Constants.WARN_LOGGER + CNAME);
    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CNAME);
    static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    /**
     * 
     * @param request
     * @return KnowledgeBaseResponse
     * @throws KnowledgeBaseException
     */
    KnowledgeBaseResponse addNewArticle(final KnowledgeBaseRequest request) throws KnowledgeBaseException;

    /**
     * @param request
     * @return KnowledgeBaseResponse
     * @throws KnowledgeBaseException
     */
    KnowledgeBaseResponse updateArticle(final KnowledgeBaseRequest request) throws KnowledgeBaseException;

    /**
     * @param request
     * @return KnowledgeBaseResponse
     * @throws KnowledgeBaseException
     */
    KnowledgeBaseResponse updateArticleStatus(final KnowledgeBaseRequest request) throws KnowledgeBaseException;

    /**
     * @param request
     * @return KnowledgeBaseResponse
     * @throws KnowledgeBaseException
     */
    KnowledgeBaseResponse getArticle(final KnowledgeBaseRequest request) throws KnowledgeBaseException;

    /**
     * Obtains a list of the top 15 articles as viewed by application users
     * and returns the list back for processing
     *
     * @param request - The request information
     * @return <code>KnowledgeBaseResponse</code> containing the list information
     * @throws KnowledgeBaseException if an exception occurs during processing
     */
    KnowledgeBaseResponse getTopArticles(final KnowledgeBaseRequest request) throws KnowledgeBaseException;

    /**
     * @param request
     * @return KnowledgeBaseResponse
     * @throws KnowledgeBaseException
     */
    KnowledgeBaseResponse getPendingArticles(final KnowledgeBaseRequest request) throws KnowledgeBaseException;
}
