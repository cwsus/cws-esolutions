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
package com.cws.esolutions.security.services.interfaces;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.security.access.control.interfaces
 * File: IUserControlService.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.SecurityConstants;
import com.cws.esolutions.core.dao.processors.impl.ServerDataDAOImpl;
import com.cws.esolutions.core.dao.processors.interfaces.IServerDataDAO;
import com.cws.esolutions.security.access.control.enums.AdminControlType;
import com.cws.esolutions.security.audit.processors.impl.AuditProcessorImpl;
import com.cws.esolutions.security.audit.processors.interfaces.IAuditProcessor;
import com.cws.esolutions.security.dao.reference.impl.UserServiceInformationDAOImpl;
import com.cws.esolutions.security.services.exception.AccessControlServiceException;
import com.cws.esolutions.security.dao.reference.interfaces.IUserServiceInformationDAO;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 */
public interface IAccessControlService
{
    static final String CONTROL_SVC_REQUEST = "serviceRequest";
    static final String CONTROL_USER_ADMIN = "userAdminFunction";
    static final String CNAME = IAccessControlService.class.getName();
    static final String CONTROL_SERVICE_ADMIN = "serviceAdminFunction";
    static final String EMAIL_SVC_ID = "4F0E7E62-BC28-414A-9FE8-C2B4E1875B4D";

    static final IAuditProcessor auditor = new AuditProcessorImpl();
    static final IServerDataDAO serverDAO = new ServerDataDAOImpl();
    static final IUserServiceInformationDAO sqlServiceDAO = new UserServiceInformationDAOImpl();

    static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityConstants.ERROR_LOGGER + CNAME);

    /**
     * Determines if the requested user has the proper level of authority to
     * access the requested resource. This method needs a little work - its
     * long-term goal is to allow both a servlet-based method as well as a
     * portlet service. It should also query an applicable user datastore,
     * in the event the session data may have been tampered.
     *
     * @param userAccount
     * @param serviceGuid
     * @return boolean
     * @throws UserControlServiceException
     */
    boolean isUserAuthorizedForService(final UserAccount userAccount, final String serviceGuid) throws UserControlServiceException;

    boolean isEmailAuthorized(final String sender, final String[] sources, final boolean isAlert) throws EmailControlServiceException;

    /**
     * Determines if the requested user has the proper level of authority to
     * access the requested resource. This method needs a little work - its
     * long-term goal is to allow both a servlet-based method as well as a
     * portlet service. It should also query an applicable user datastore,
     * in the event the session data may have been tampered.
     *
     * @param userAccount
     * @return boolean
     * @throws AdminControlServiceException
     */
    boolean adminControlService(final UserAccount userAccount) throws AdminControlServiceException;

    /**
     * Determines if the requested user has the proper level of authority to
     * access the requested resource. This method needs a little work - its
     * long-term goal is to allow both a servlet-based method as well as a
     * portlet service. It should also query an applicable user datastore,
     * in the event the session data may have been tampered.
     *
     * @param userAccount
     * @param controlType
     * @return boolean
     * @throws AdminControlServiceException
     */
    boolean adminControlService(final UserAccount userAccount, final AdminControlType controlType) throws AdminControlServiceException;
}
