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
 * cws-khuntly   11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.core.CoreServiceBean;
import com.cws.esolutions.core.CoreServiceConstants;
import com.cws.esolutions.core.config.xml.SSHConfig;
import com.cws.esolutions.core.dao.impl.ServerDataDAOImpl;
import com.cws.esolutions.core.dao.interfaces.IServerDataDAO;
import com.cws.esolutions.core.processors.dto.DNSServiceRequest;
import com.cws.esolutions.core.processors.dto.DNSServiceResponse;
import com.cws.esolutions.security.processors.impl.AuditProcessorImpl;
import com.cws.esolutions.core.processors.exception.DNSServiceException;
import com.cws.esolutions.security.processors.interfaces.IAuditProcessor;
import com.cws.esolutions.security.services.impl.AccessControlServiceImpl;
import com.cws.esolutions.security.services.interfaces.IAccessControlService;
/**
 * API allowing DNS service management and data retrieval
 *
 * @author cws-khuntly
 * @version 1.0
 */
public interface IDNSServiceRequestProcessor
{
    static final IServerDataDAO dao = new ServerDataDAOImpl();
    static final IAuditProcessor auditor = new AuditProcessorImpl();
    static final CoreServiceBean appBean = CoreServiceBean.getInstance();
    static final String CNAME = IDNSServiceRequestProcessor.class.getName();
    static final SSHConfig sshConfig = appBean.getConfigData().getSshConfig();
    static final IAccessControlService accessControl = new AccessControlServiceImpl();

    static final Logger DEBUGGER = LoggerFactory.getLogger(CoreServiceConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(CoreServiceConstants.ERROR_LOGGER + CNAME);

    /**
     * Adds a new record to an existing DNS zone. The record can be a subdomain, CNAME, TXT
     * or any other valid record type. Additionally, it can be an apex record to the zone
     * if necessary.
     *
     * @param request - The {@link com.cws.esolutions.core.processors.dto.DNSServiceRequest}
     * housing the necessary data to process
     * @return The {@link com.cws.esolutions.core.processors.dto.DNSServiceResponse} containing the response information, or error code
     * @throws DNSServiceException {@link com.cws.esolutions.core.processors.exception.DNSServiceException} if an error occurs during processing
     */
    DNSServiceResponse addRecordToEntry(final DNSServiceRequest request) throws DNSServiceException;

    /**
     * Performs a simple DNS lookup for the given service name of the provided
     * record type. For example, www.google.com of type A would return 173.194.113.179
     * (among others). Responses are returned to the requestor for utilization. If no
     * results are found using the default resolver (whatever is listed in /etc/hosts
     * or whatever is provided, as applicable) then the 
     *
     * @param request - The {@link com.cws.esolutions.core.processors.dto.DNSServiceRequest}
     * housing the necessary data to process
     * @return The {@link com.cws.esolutions.core.processors.dto.DNSServiceResponse} containing the response information, or error code
     * @throws DNSServiceException {@link com.cws.esolutions.core.processors.exception.DNSServiceException} if an error occurs during processing
     */
    DNSServiceResponse performLookup(final DNSServiceRequest request) throws DNSServiceException;

    /**
     * Creates a new zone service file. This is BIND-specific, it will NOT work with other
     * service implementations at this time.
     *
     * Requires a {@link com.cws.esolutions.core.processors.dto.DNSEntry} with associated
     * {@link com.cws.esolutions.core.processors.dto.DNSRecord}s embedded
     *
     * @param request - The {@link com.cws.esolutions.core.processors.dto.DNSServiceRequest}
     * housing the necessary data to process
     * @return The {@link com.cws.esolutions.core.processors.dto.DNSServiceResponse} containing the response information, or error code
     * @throws DNSServiceException {@link com.cws.esolutions.core.processors.exception.DNSServiceException} if an error occurs during processing
     */
    DNSServiceResponse createNewService(final DNSServiceRequest request) throws DNSServiceException;

    /**
     * Sends a newly created DNS zone to configured nameservers for public consumption. The file
     * is generated and transferred to the given servers via SCP.
     *
     * @param request - The {@link com.cws.esolutions.core.processors.dto.DNSServiceRequest}
     * housing the necessary data to process
     * @return The {@link com.cws.esolutions.core.processors.dto.DNSServiceResponse} containing the response information, or error code
     * @throws DNSServiceException {@link com.cws.esolutions.core.processors.exception.DNSServiceException} if an error occurs during processing
     */
    DNSServiceResponse pushNewService(final DNSServiceRequest request) throws DNSServiceException;

    /**
     * Sends a newly created DNS zone to configured nameservers for public consumption. The file
     * is generated and transferred to the given servers via SCP.
     *
     * @param request - The {@link com.cws.esolutions.core.processors.dto.DNSServiceRequest}
     * housing the necessary data to process
     * @return The {@link com.cws.esolutions.core.processors.dto.DNSServiceResponse} containing the response information, or error code
     * @throws DNSServiceException {@link com.cws.esolutions.core.processors.exception.DNSServiceException} if an error occurs during processing
     */
    DNSServiceResponse performSiteTransfer(final DNSServiceRequest request) throws DNSServiceException;
}
