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
package com.cws.esolutions.security.access.control.interfaces;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.security.access.control.interfaces
 * File: IAdminControlService.java
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
import com.cws.esolutions.security.access.control.enums.AdminControlType;
import com.cws.esolutions.security.access.control.exception.AdminControlServiceException;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 */
public interface IAdminControlService
{
    static final String CONTROL_SVC_REQUEST = "serviceRequest";
    static final String CONTROL_USER_ADMIN = "userAdminFunction";
    static final String CNAME = IAdminControlService.class.getName();
    static final String CONTROL_SERVICE_ADMIN = "serviceAdminFunction";

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
