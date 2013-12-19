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
package com.cws.esolutions.security.processors.interfaces;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.security.processors.interfaces
 * File: IFileSecurityProcessor.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 * Kevin Huntly         12/05/2008 13:36:09             Added method to process change requests
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.security.SecurityConstants;
import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.config.xml.AuthData;
import com.cws.esolutions.security.config.xml.KeyConfig;
import com.cws.esolutions.security.config.xml.FileSecurityConfig;
import com.cws.esolutions.security.keymgmt.interfaces.KeyManager;
import com.cws.esolutions.security.processors.dto.FileSecurityRequest;
import com.cws.esolutions.security.processors.dto.FileSecurityResponse;
import com.cws.esolutions.security.keymgmt.factory.KeyManagementFactory;
import com.cws.esolutions.security.config.xml.SecurityServiceConfiguration;
import com.cws.esolutions.security.processors.exception.FileSecurityException;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 */
public interface IFileSecurityProcessor
{
    static final SecurityServiceBean secBean = SecurityServiceBean.getInstance();
    static final String CNAME = IFileSecurityProcessor.class.getName();
    static final AuthData authData = secBean.getConfigData().getAuthData();
    static final KeyConfig keyConfig = secBean.getConfigData().getKeyConfig();
    static final SecurityServiceConfiguration secConfig = secBean.getConfigData();
    static final FileSecurityConfig fileSecurityConfig = secConfig.getFileSecurityConfig();
    static final KeyManager keyManager = KeyManagementFactory.getKeyManager(keyConfig.getKeyManager());

    static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityConstants.ERROR_LOGGER + CNAME);
    static final Logger WARN_RECORDER = LoggerFactory.getLogger(SecurityConstants.WARN_LOGGER + CNAME);

    FileSecurityResponse signFile(final FileSecurityRequest request) throws FileSecurityException;

    FileSecurityResponse verifyFile(final FileSecurityRequest request) throws FileSecurityException;

    FileSecurityResponse encryptFile(final FileSecurityRequest request) throws FileSecurityException;

    FileSecurityResponse decryptFile(final FileSecurityRequest request) throws FileSecurityException;
}
