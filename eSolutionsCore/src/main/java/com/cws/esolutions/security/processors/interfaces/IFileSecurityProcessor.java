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

import com.cws.esolutions.core.Constants;
import com.cws.esolutions.core.CoreServiceBean;
import com.cws.esolutions.core.config.ApplicationConfig;
import com.cws.esolutions.core.processors.interfaces.IApplicationManagementProcessor;
import com.cws.esolutions.security.processors.dto.FileSecurityRequest;
import com.cws.esolutions.security.processors.dto.FileSecurityResponse;
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
    static final CoreServiceBean appBean = CoreServiceBean.getInstance();
    static final String CNAME = IApplicationManagementProcessor.class.getName();
    static final ApplicationConfig appConfig = appBean.getConfigData().getAppConfig();

    static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CNAME);
    static final Logger WARN_RECORDER = LoggerFactory.getLogger(Constants.WARN_LOGGER + CNAME);

    FileSecurityResponse signFile(final FileSecurityRequest request) throws FileSecurityException;

    FileSecurityResponse verifyFile(final FileSecurityRequest request) throws FileSecurityException;

    FileSecurityResponse encryptFile(final FileSecurityRequest request) throws FileSecurityException;

    FileSecurityResponse decryptFile(final FileSecurityRequest request) throws FileSecurityException;
}
