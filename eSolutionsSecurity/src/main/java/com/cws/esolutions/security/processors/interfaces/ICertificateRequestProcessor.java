/*
 * Copyright (c) 2009 - 2017 CaspersBox Web Services
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
package com.cws.esolutions.security.processors.interfaces;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.processors.interfaces
 * File: ICertificateRequestProcessor.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly   03/28/2017 01:41:00             Created.
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.SecurityServiceConstants;
import com.cws.esolutions.security.config.xml.SecurityConfig;
import com.cws.esolutions.security.config.xml.CertificateConfig;
import com.cws.esolutions.security.processors.dto.CertificateRequest;
import com.cws.esolutions.security.processors.dto.CertificateResponse;
import com.cws.esolutions.security.processors.impl.AuditProcessorImpl;
import com.cws.esolutions.security.processors.interfaces.IAuditProcessor;
import com.cws.esolutions.security.services.impl.AccessControlServiceImpl;
import com.cws.esolutions.security.dao.certmgmt.impl.CertificateManagerImpl;
import com.cws.esolutions.security.services.interfaces.IAccessControlService;
import com.cws.esolutions.security.dao.certmgmt.interfaces.ICertificateManager;
import com.cws.esolutions.security.processors.exception.CertificateRequestException;
/**
 * API allowing user authentication request processing.
 *
 * @author cws-khuntly
 * @version 1.0
 */
public interface ICertificateRequestProcessor
{
    static final IAuditProcessor auditor = new AuditProcessorImpl();
    static final String CNAME = ICertificateRequestProcessor.class.getName();
    static final ICertificateManager processor = new CertificateManagerImpl();
    static final SecurityServiceBean secBean = SecurityServiceBean.getInstance();
    static final IAccessControlService accessControl = new AccessControlServiceImpl();
    static final SecurityConfig secConfig = secBean.getConfigData().getSecurityConfig();
    static final CertificateConfig certConfig = secBean.getConfigData().getCertConfig();

    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityServiceConstants.ERROR_LOGGER);
    static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityServiceConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    CertificateResponse listActiveRequests(final CertificateRequest request) throws CertificateRequestException;

    /**
     * Request and generate a new certificate for a given user or website. This request requires all the necessary
     * information that would be required to generate a certificate using keytool or any other certificate generation
     * tool.
     *
     * @param request The request information for the certificate
     * @return A certificate response containing the data returned
     * @throws CertificateRequestException if an error occurs during processing
     */
    CertificateResponse generateCertificateRequest(final CertificateRequest request) throws CertificateRequestException;

    /**
     * Applies a certificate response from a certificate authority after a request has been
     * sent and processed.
     *
     * @param request The request information for the certificate
     * @return A certificate response containing the data returned
     * @throws CertificateRequestException if an error occurs during processing
     */
    CertificateResponse applyCertificateResponse(final CertificateRequest request) throws CertificateRequestException;
}
