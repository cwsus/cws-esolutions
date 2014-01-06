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
import com.cws.esolutions.core.config.xml.DNSConfig;
import com.cws.esolutions.core.config.xml.SSHConfig;
import com.cws.esolutions.core.dao.impl.DNSServiceDAOImpl;
import com.cws.esolutions.core.dao.impl.ServerDataDAOImpl;
import com.cws.esolutions.core.config.xml.ApplicationConfig;
import com.cws.esolutions.core.dao.interfaces.IDNSServiceDAO;
import com.cws.esolutions.core.dao.interfaces.IServerDataDAO;
import com.cws.esolutions.core.processors.dto.DNSServiceRequest;
import com.cws.esolutions.core.processors.dto.DNSServiceResponse;
import com.cws.esolutions.security.processors.impl.AuditProcessorImpl;
import com.cws.esolutions.core.processors.exception.DNSServiceException;
import com.cws.esolutions.security.processors.interfaces.IAuditProcessor;
import com.cws.esolutions.security.services.impl.AccessControlServiceImpl;
import com.cws.esolutions.security.services.interfaces.IAccessControlService;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 */
public interface IDNSServiceRequestProcessor
{
    static final IServerDataDAO dao = new ServerDataDAOImpl();
    static final IDNSServiceDAO dnsDao = new DNSServiceDAOImpl();
    static final IAuditProcessor auditor = new AuditProcessorImpl();
    static final CoreServiceBean appBean = CoreServiceBean.getInstance();
    static final String CNAME = IDNSServiceRequestProcessor.class.getName();
    static final SSHConfig sshConfig = appBean.getConfigData().getSshConfig();
    static final DNSConfig dnsConfig = appBean.getConfigData().getDNSConfig();
    static final IAccessControlService accessControl = new AccessControlServiceImpl();
    static final ApplicationConfig appConfig = appBean.getConfigData().getAppConfig();

    static final Logger DEBUGGER = LoggerFactory.getLogger(CoreServiceConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger INFO_RECORDER = LoggerFactory.getLogger(CoreServiceConstants.INFO_LOGGER + CNAME);
    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(CoreServiceConstants.ERROR_LOGGER + CNAME);

    /**
     * Performs a simple DNS lookup for the given service name of the provided
     * record type. For example, www.google.com of type A would return 173.194.113.179
     * (among others). Responses are returned to the requestor for utilization. If no
     * results are found using the default resolver (whatever is listed in /etc/hosts
     * or whatever is provided, as applicable) then the 
     *
     * @param request - The <code>DNSServiceRequest</code> housing the necessary data to process
     * @return <code>DNSServiceResponse</code> containing the response information, or error code
     * @throws DNSServiceException if an error occurs during processing
     */
    DNSServiceResponse performLookup(final DNSServiceRequest request) throws DNSServiceException;

    /**
     * This method is utilized to obtain information about a record within the database. At this point
     * it uses the associated project code and obtains all available information for that project code
     * and assembles into the DNS entries (and zones as necessary). The data is then returned to the
     * user and displayed in whatever format appropriate.
     *
     * @param request - The <code>DNSServiceRequest</code> housing the necessary data to process
     * @return <code>DNSServiceResponse</code> containing the response information, or error code
     * @throws DNSServiceException if an error occurs during processing
     */
    DNSServiceResponse getDataFromDatabase(final DNSServiceRequest request) throws DNSServiceException;

    /**
     * Creates a new zone service file. This is BIND-specific, it will NOT work with other
     * service implementations at this time.
     *
     * Requires a <code>DNSEntry</code> with associated <code>DNSRecord</code>s embedded
     *
     * @param request - The request object containing the necessary data
     * @return <code>DNSServiceResponse</code> - The response object containing data
     * @throws DNSServiceException if an exception occurs performing the create operation
     */
    DNSServiceResponse createNewService(final DNSServiceRequest request) throws DNSServiceException;

    /**
     * Sends a newly created DNS zone to configured nameservers for public consumption. The file
     * is generated and transferred to the given servers via SCP.
     *
     * @param request - The <code>DNSServiceRequest</code> housing the necessary data to process
     * @return <code>DNSServiceResponse</code> containing the response information, or error code
     * @throws DNSServiceException if an error occurs during processing
     */
    DNSServiceResponse pushNewService(final DNSServiceRequest request) throws DNSServiceException;

    /**
     * Sends a newly created DNS zone to configured nameservers for public consumption. The file
     * is generated and transferred to the given servers via SCP.
     *
     * @param request - The <code>DNSServiceRequest</code> housing the necessary data to process
     * @return <code>DNSServiceResponse</code> containing the response information, or error code
     * @throws DNSServiceException if an error occurs during processing
     */
    DNSServiceResponse performSiteTransfer(final DNSServiceRequest request) throws DNSServiceException;
}
