/*
 * Copyright (c) 2009 - 2020 CaspersBox Web Services
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
 * File: ISystemCheckProcessor.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.cws.esolutions.core.CoreServicesBean;
import com.cws.esolutions.core.CoreServicesConstants;
import com.cws.esolutions.core.config.xml.AgentConfig;
import com.cws.esolutions.core.config.xml.ApplicationConfig;
import com.cws.esolutions.core.processors.dto.SystemCheckRequest;
import com.cws.esolutions.core.processors.dto.SystemCheckResponse;
import com.cws.esolutions.security.processors.impl.AuditProcessorImpl;
import com.cws.esolutions.core.processors.exception.SystemCheckException;
import com.cws.esolutions.security.processors.interfaces.IAuditProcessor;
import com.cws.esolutions.security.services.impl.AccessControlServiceImpl;
import com.cws.esolutions.security.services.interfaces.IAccessControlService;
/**
 * API allowing access to system check functionality, such as telnet/netstat
 * against remote hosts.
 *
 * @author cws-khuntly
 * @version 1.0
 */
public interface ISystemCheckProcessor
{
    static final CoreServicesBean appBean = CoreServicesBean.getInstance();
    static final String CNAME = IServerManagementProcessor.class.getName();
    static final AgentConfig agentConfig = appBean.getConfigData().getAgentConfig();
    static final ApplicationConfig appConfig = appBean.getConfigData().getAppConfig();
    static final IAuditProcessor auditor = (IAuditProcessor) new AuditProcessorImpl();
    static final IAccessControlService accessControl = (IAccessControlService) new AccessControlServiceImpl();

    static final Logger DEBUGGER = LogManager.getLogger(CoreServicesConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger ERROR_RECORDER = LogManager.getLogger(CoreServicesConstants.ERROR_LOGGER + ISystemCheckProcessor.CNAME);

    /**
     * Performs a netstat request against a given host and returns the obtained
     * output to the requestor for further processing.
     *
     * @param request The {@link com.cws.esolutions.core.processors.dto.SystemCheckRequest}
     * containing the necessary information to process the request.
     * @return {@link com.cws.esolutions.core.processors.dto.SystemCheckResponse} containing
     * the response information for the given request
     * @throws SystemCheckException {@link com.cws.esolutions.core.processors.exception.SystemCheckException}
     * if an exception occurs during processing
     */
    SystemCheckResponse runNetstatCheck(final SystemCheckRequest request) throws SystemCheckException;

    /**
     * Performs a telnet request against a given host and returns the obtained
     * output to the requestor for further processing.
     *
     * @param request The {@link com.cws.esolutions.core.processors.dto.SystemCheckRequest}
     * containing the necessary information to process the request.
     * @return {@link com.cws.esolutions.core.processors.dto.SystemCheckResponse} containing
     * the response information for the given request
     * @throws SystemCheckException {@link com.cws.esolutions.core.processors.exception.SystemCheckException}
     * if an exception occurs during processing
     */
    SystemCheckResponse runTelnetCheck(final SystemCheckRequest request) throws SystemCheckException;

    /**
     * Performs a date request against a given host and returns the obtained
     * output to the requestor for further processing.
     *
     * @param request The {@link com.cws.esolutions.core.processors.dto.SystemCheckRequest}
     * containing the necessary information to process the request.
     * @return {@link com.cws.esolutions.core.processors.dto.SystemCheckResponse} containing
     * the response information for the given request
     * @throws SystemCheckException {@link com.cws.esolutions.core.processors.exception.SystemCheckException}
     * if an exception occurs during processing
     */
    SystemCheckResponse runRemoteDateCheck(final SystemCheckRequest request) throws SystemCheckException;

    /**
     * Performs a process list request against a given host and returns the obtained
     * output to the requestor for further processing.
     *
     * @param request The {@link com.cws.esolutions.core.processors.dto.SystemCheckRequest}
     * containing the necessary information to process the request.
     * @return {@link com.cws.esolutions.core.processors.dto.SystemCheckResponse} containing
     * the response information for the given request
     * @throws SystemCheckException {@link com.cws.esolutions.core.processors.exception.SystemCheckException}
     * if an exception occurs during processing
     */
    SystemCheckResponse runProcessListCheck(final SystemCheckRequest request) throws SystemCheckException;
}
