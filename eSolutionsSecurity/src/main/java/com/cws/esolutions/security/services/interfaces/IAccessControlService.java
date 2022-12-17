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
package com.cws.esolutions.security.services.interfaces;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.access.control.interfaces
 * File: IaccessControlService.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.SecurityServiceConstants;
import com.cws.esolutions.security.config.xml.SecurityConfig;
import com.cws.esolutions.security.services.dto.AccessControlServiceRequest;
import com.cws.esolutions.security.services.dto.AccessControlServiceResponse;
import com.cws.esolutions.security.dao.reference.impl.SecurityReferenceDAOImpl;
import com.cws.esolutions.security.dao.reference.interfaces.ISecurityReferenceDAO;
import com.cws.esolutions.security.services.exception.AccessControlServiceException;
/**
 * API allowing access control to functionality for role-based
 * or group-based functions.
 *
 * @author cws-khuntly
 * @version 1.0
 */
public interface IAccessControlService
{
	static final String CNAME = IAccessControlService.class.getName();
    static final SecurityServiceBean secBean = SecurityServiceBean.getInstance();
    static final SecurityConfig secConfig = secBean.getConfigData().getSecurityConfig();
    static final ISecurityReferenceDAO ref = (ISecurityReferenceDAO) new SecurityReferenceDAOImpl();

    static final Logger DEBUGGER = LogManager.getLogger(SecurityServiceConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger ERROR_RECORDER = LogManager.getLogger(SecurityServiceConstants.ERROR_LOGGER + IAccessControlService.CNAME);

    /**
     * Determines if the requested user has the proper level of authority to
     * access the requested resource. This method needs a little work - its
     * long-term goal is to allow both a servlet-based method as well as a
     * portlet service. It should also query an applicable user datastore,
     * in the event the session data may have been tampered.
     *
     * @param request The control service request data
     * @return <code>true</code> if authorization succeeded, <code>false</code> otherwise
     * @throws AccessControlServiceException {@link com.cws.esolutions.security.services.exception.AccessControlServiceException} if an exception occurs during processing
     */
    AccessControlServiceResponse isUserAuthorized(final AccessControlServiceRequest request) throws AccessControlServiceException;
}
