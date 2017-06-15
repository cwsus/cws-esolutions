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
 * File: IFileSecurityProcessor.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly   11/23/2008 22:39:20             Created.
 * Kevin Huntly         12/05/2008 13:36:09             Added method to process change requests
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.config.xml.KeyConfig;
import com.cws.esolutions.security.SecurityServiceConstants;
import com.cws.esolutions.security.config.xml.FileSecurityConfig;
import com.cws.esolutions.security.processors.impl.AuditProcessorImpl;
import com.cws.esolutions.security.processors.dto.FileSecurityRequest;
import com.cws.esolutions.security.processors.dto.FileSecurityResponse;
import com.cws.esolutions.security.config.xml.SecurityConfigurationData;
import com.cws.esolutions.security.processors.exception.FileSecurityException;
/**
 * API allowing file security processing - digital signatures, encryption
 * and verification.
 *
 * @author cws-khuntly
 * @version 1.0
 */
public interface IFileSecurityProcessor
{
    static final IAuditProcessor auditor = new AuditProcessorImpl();
    static final SecurityServiceBean secBean = SecurityServiceBean.getInstance();
    static final String CNAME = IFileSecurityProcessor.class.getName();
    static final KeyConfig keyConfig = secBean.getConfigData().getKeyConfig();
    static final SecurityConfigurationData secConfig = secBean.getConfigData();
    static final FileSecurityConfig fileSecurityConfig = secConfig.getFileSecurityConfig();

    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityServiceConstants.ERROR_LOGGER);
    static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityServiceConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    /**
     * Digitally signs a provided file with the given user account's keypair as obtained from the
     * authorization datastore.
     *
     * @param request The {@link com.cws.esolutions.security.processors.dto.FileSecurityRequest}
     * containing the necessary authentication information to process the request.
     * @return {@link com.cws.esolutions.security.processors.dto.FileSecurityResponse}
     * containing the response for the provided request.
     * @throws FileSecurityException {@link com.cws.esolutions.security.processors.exception.FileSecurityException} if an exception occurs during processing
     */
    FileSecurityResponse signFile(final FileSecurityRequest request) throws FileSecurityException;

    /**
     * Verifies a given digital signature against a provided file to ensure it has not been
     * altered during transmission.
     *
     * @param request The {@link com.cws.esolutions.security.processors.dto.FileSecurityRequest}
     * containing the necessary authentication information to process the request.
     * @return {@link com.cws.esolutions.security.processors.dto.FileSecurityResponse}
     * containing the response for the provided request.
     * @throws FileSecurityException {@link com.cws.esolutions.security.processors.exception.FileSecurityException} if an exception occurs during processing
     */
    FileSecurityResponse verifyFile(final FileSecurityRequest request) throws FileSecurityException;

    /**
     * Encrypts a provided file with the given user account's keypair as obtained from the
     * authorization datastore.
     *
     * @param request The {@link com.cws.esolutions.security.processors.dto.FileSecurityRequest}
     * containing the necessary authentication information to process the request.
     * @return {@link com.cws.esolutions.security.processors.dto.FileSecurityResponse}
     * containing the response for the provided request.
     * @throws FileSecurityException {@link com.cws.esolutions.security.processors.exception.FileSecurityException} if an exception occurs during processing
     */
    FileSecurityResponse encryptFile(final FileSecurityRequest request) throws FileSecurityException;

    /**
     * Decrypts a provided file with the given public key for review.
     *
     * @param request The {@link com.cws.esolutions.security.processors.dto.FileSecurityRequest}
     * containing the necessary authentication information to process the request.
     * @return {@link com.cws.esolutions.security.processors.dto.FileSecurityResponse}
     * containing the response for the provided request.
     * @throws FileSecurityException {@link com.cws.esolutions.security.processors.exception.FileSecurityException} if an exception occurs during processing
     */
    FileSecurityResponse decryptFile(final FileSecurityRequest request) throws FileSecurityException;
}
