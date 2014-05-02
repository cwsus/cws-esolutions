/*
 * Copyright (c) 2009 - 2014 CaspersBox Web Services
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
 * File: VirtualServiceManager.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.core.CoreServiceBean;
import com.cws.esolutions.core.CoreServiceConstants;
import com.cws.esolutions.core.config.xml.ApplicationConfig;
import com.cws.esolutions.core.processors.dto.VirtualServiceRequest;
import com.cws.esolutions.core.processors.dto.VirtualServiceResponse;
import com.cws.esolutions.core.processors.exception.VirtualServiceException;
/**
 * API allowing access into Virtual Machine processing, such as Oracle
 * VirtualBox or VMWare. Currently supports Oracle VirtualBox.
 *
 * @author khuntly
 * @version 1.0
 */
public interface VirtualServiceManager
{
    static final CoreServiceBean appBean = CoreServiceBean.getInstance();

    static final ApplicationConfig appConfig = appBean.getConfigData().getAppConfig();

    // loggers
    static final Logger DEBUGGER = LoggerFactory.getLogger(CoreServiceConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(CoreServiceConstants.ERROR_LOGGER + VirtualServiceManager.class.getName());

    /**
     * Lists virtual machines installed against a specified host.
     *
     * @param request A {@link com.cws.esolutions.core.processors.dto.VirtualServiceRequest} containing the
     * request information to process
     * @return A {@link com.cws.esolutions.core.processors.dto.VirtualServiceResponse} containing the
     * response associated with the request
     * @throws VirtualServiceException {@link com.cws.esolutions.core.processors.exception.VirtualServiceException} if an exception occurs during processing
     */
    VirtualServiceResponse listVirtualMachines(final VirtualServiceRequest request) throws VirtualServiceException;

    /**
     * Obtains detailed information regarding the provided virtual machine and returns to the requestor
     * for further processing.
     *
     * @param request A {@link com.cws.esolutions.core.processors.dto.VirtualServiceRequest} containing the
     * request information to process
     * @return A {@link com.cws.esolutions.core.processors.dto.VirtualServiceResponse} containing the
     * response associated with the request
     * @throws VirtualServiceException {@link com.cws.esolutions.core.processors.exception.VirtualServiceException} if an exception occurs during processing
     */
    VirtualServiceResponse getVirtualMachine(final VirtualServiceRequest request) throws VirtualServiceException;
    
    /**
     * Starts an associated virtual machine on the target host
     *
     * @param request A {@link com.cws.esolutions.core.processors.dto.VirtualServiceRequest} containing the
     * request information to process
     * @return A {@link com.cws.esolutions.core.processors.dto.VirtualServiceResponse} containing the
     * response associated with the request
     * @throws VirtualServiceException {@link com.cws.esolutions.core.processors.exception.VirtualServiceException} if an exception occurs during processing
     */
    VirtualServiceResponse startVirtualMachine(final VirtualServiceRequest request) throws VirtualServiceException;

    /**
     * Stops an associated virtual machine on the target host
     *
     * @param request A {@link com.cws.esolutions.core.processors.dto.VirtualServiceRequest} containing the
     * request information to process
     * @return A {@link com.cws.esolutions.core.processors.dto.VirtualServiceResponse} containing the
     * response associated with the request
     * @throws VirtualServiceException {@link com.cws.esolutions.core.processors.exception.VirtualServiceException} if an exception occurs during processing
     */
    VirtualServiceResponse stopVirtualMachine(final VirtualServiceRequest request) throws VirtualServiceException;
}
