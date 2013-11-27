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
package com.cws.esolutions.security.processors.interfaces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.security.config.AuthData;
import com.cws.esolutions.security.config.KeyConfig;
import com.cws.esolutions.security.SecurityConstants;
import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.config.FileSecurityConfig;
import com.cws.esolutions.security.keymgmt.interfaces.KeyManager;
import com.cws.esolutions.security.processors.dto.FileSecurityRequest;
import com.cws.esolutions.security.config.SecurityServiceConfiguration;
import com.cws.esolutions.security.processors.dto.FileSecurityResponse;
import com.cws.esolutions.security.keymgmt.factory.KeyManagementFactory;
import com.cws.esolutions.security.processors.exception.FileSecurityException;
/**
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.security.processors.interfaces
 * File: IFileSecurityProcessor.java
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
 * 35033355 @ Jul 12, 2013 3:00:17 PM
 *     Created.
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
