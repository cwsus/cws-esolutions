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
package com.cws.esolutions.core.processors.interfaces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.core.Constants;
import com.cws.esolutions.core.CoreServiceBean;
import com.cws.esolutions.core.config.ApplicationConfig;
import com.cws.esolutions.core.processors.dto.KnowledgeBaseRequest;
import com.cws.esolutions.core.processors.dto.KnowledgeBaseResponse;
import com.cws.esolutions.core.dao.processors.impl.KnowledgeBaseDAOImpl;
import com.cws.esolutions.core.dao.processors.interfaces.IKnowledgeBaseDAO;
import com.cws.esolutions.core.processors.exception.KnowledgeBaseException;
import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.access.control.impl.UserControlServiceImpl;
import com.cws.esolutions.security.access.control.impl.AdminControlServiceImpl;
import com.cws.esolutions.security.access.control.interfaces.IUserControlService;
import com.cws.esolutions.security.access.control.interfaces.IAdminControlService;
import com.cws.esolutions.security.audit.processors.impl.AuditProcessorImpl;
import com.cws.esolutions.security.audit.processors.interfaces.IAuditProcessor;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.processors.interfaces
 * IKnowledgeBaseProcessor.java
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
 * kh05451 @ Oct 29, 2012 9:44:46 AM
 *     Created.
 */
public interface IKnowledgeBaseProcessor
{
    static final IAuditProcessor auditor = new AuditProcessorImpl();
    static final IKnowledgeBaseDAO kbaseDAO = new KnowledgeBaseDAOImpl();
    static final CoreServiceBean appBean = CoreServiceBean.getInstance();
    static final SecurityServiceBean secBean = SecurityServiceBean.getInstance();
    static final IUserControlService userControl = new UserControlServiceImpl();
    static final IAdminControlService adminControl = new AdminControlServiceImpl();

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
     * @param request
     * @return KnowledgeBaseResponse
     * @throws KnowledgeBaseException
     */
    KnowledgeBaseResponse getPendingArticles(final KnowledgeBaseRequest request) throws KnowledgeBaseException;
}
