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
package com.cws.esolutions.security.access.control.interfaces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.security.SecurityConstants;
import com.cws.esolutions.core.dao.processors.impl.ServerDataDAOImpl;
import com.cws.esolutions.core.dao.processors.interfaces.IServerDataDAO;
import com.cws.esolutions.security.audit.processors.impl.AuditProcessorImpl;
import com.cws.esolutions.security.access.control.exception.EmailControlServiceException;
import com.cws.esolutions.security.access.control.impl.UserControlServiceImpl;
import com.cws.esolutions.security.audit.processors.interfaces.IAuditProcessor;
import com.cws.esolutions.security.processors.impl.AccountControlProcessorImpl;
import com.cws.esolutions.security.processors.interfaces.IAccountControlProcessor;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.control.interfaces
 * IEmailControlService.java
 *
 *
 *
 * $Id: IEmailControlService.java 2276 2013-01-03 16:32:52Z kmhuntly@gmail.com $
 * $Author: $
 * $Date: $
 * $Revision: $
 * @author kmhuntly@gmail.com
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Nov 17, 2012 12:39:43 PM
 *     Created.
 */
public interface IEmailControlService
{
    static final String CNAME = IEmailControlService.class.getName();
    static final String EMAIL_SVC_ID = "4F0E7E62-BC28-414A-9FE8-C2B4E1875B4D";

    static final IAuditProcessor auditor = new AuditProcessorImpl();
    static final IServerDataDAO serverDAO = new ServerDataDAOImpl();
    static final IUserControlService userControl = new UserControlServiceImpl();
    static IAccountControlProcessor controlMgr = new AccountControlProcessorImpl();

    static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityConstants.ERROR_LOGGER + CNAME);

    boolean isEmailAuthorized(final String sender, final String[] sources, final boolean isAlert) throws EmailControlServiceException;
}
