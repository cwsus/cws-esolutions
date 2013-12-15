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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.core.Constants;
import com.cws.esolutions.core.CoreServiceBean;
import com.cws.esolutions.core.config.ApplicationConfig;
import com.cws.esolutions.core.processors.dto.VirtualServiceRequest;
import com.cws.esolutions.core.processors.dto.VirtualServiceResponse;
import com.cws.esolutions.core.processors.exception.VirtualServiceException;
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
public interface VirtualServiceManager
{
    static final CoreServiceBean appBean = CoreServiceBean.getInstance();

    static final ApplicationConfig appConfig = appBean.getConfigData().getAppConfig();

    // loggers
    static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + VirtualServiceManager.class.getName());

    VirtualServiceResponse listVirtualMachines(final VirtualServiceRequest request) throws VirtualServiceException;

    VirtualServiceResponse startVirtualMachine(final VirtualServiceRequest request) throws VirtualServiceException;

    VirtualServiceResponse stopVirtualMachine(final VirtualServiceRequest request) throws VirtualServiceException;
}
