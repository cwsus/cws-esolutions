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
 * File: VirtualServiceManager.java
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
import com.cws.esolutions.core.config.xml.SSHConfig;
import com.cws.esolutions.core.dao.impl.ServerDataDAOImpl;
import com.cws.esolutions.core.dao.interfaces.IServerDataDAO;
import com.cws.esolutions.core.processors.dto.DNSServiceRequest;
import com.cws.esolutions.core.processors.dto.DNSServiceResponse;
import com.cws.esolutions.core.processors.exception.DNSServiceException;
import com.cws.esolutions.utility.services.impl.AccessControlServiceImpl;
import com.cws.esolutions.utility.services.interfaces.IAccessControlService;
import com.cws.esolutions.utility.securityutils.processors.impl.AuditProcessorImpl;
import com.cws.esolutions.utility.securityutils.processors.interfaces.IAuditProcessor;
/**
 * API allowing DNS service management and data retrieval
 *
 * @author cws-khuntly
 * @version 1.0
 */
public interface IDNSServiceRequestProcessor
{
    static final CoreServicesBean appBean = CoreServicesBean.getInstance();
    static final String CNAME = IDNSServiceRequestProcessor.class.getName();
    static final SSHConfig sshConfig = appBean.getConfigData().getSshConfig();
    static final IServerDataDAO dao = (IServerDataDAO) new ServerDataDAOImpl();
    static final IAuditProcessor auditor = (IAuditProcessor) new AuditProcessorImpl();
    static final IAccessControlService accessControl = (IAccessControlService) new AccessControlServiceImpl();

    static final Logger DEBUGGER = LogManager.getLogger(CoreServicesConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger ERROR_RECORDER = LogManager.getLogger(CoreServicesConstants.ERROR_LOGGER + CNAME);

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
}
