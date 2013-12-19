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
 * File: IPackageManagementProcessor.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.core.Constants;
import com.cws.esolutions.core.CoreServiceBean;
import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.core.processors.dto.PackageManagementRequest;
import com.cws.esolutions.core.processors.dto.PackageManagementResponse;
import com.cws.esolutions.security.audit.processors.impl.AuditProcessorImpl;
import com.cws.esolutions.security.access.control.impl.UserControlServiceImpl;
import com.cws.esolutions.security.access.control.impl.AdminControlServiceImpl;
import com.cws.esolutions.security.audit.processors.interfaces.IAuditProcessor;
import com.cws.esolutions.core.processors.exception.PackageManagementException;
import com.cws.esolutions.security.access.control.interfaces.IUserControlService;
import com.cws.esolutions.security.access.control.interfaces.IAdminControlService;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 */
public interface IPackageManagementProcessor
{
    static final IAuditProcessor auditor = new AuditProcessorImpl();
    static final CoreServiceBean appBean = CoreServiceBean.getInstance();
    static final String CNAME = IPackageManagementProcessor.class.getName();
    static final IUserControlService userControl = new UserControlServiceImpl();
    static final SecurityServiceBean secBean = SecurityServiceBean.getInstance();
    static final IAdminControlService adminControl = new AdminControlServiceImpl();

    static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CNAME);
    static final Logger WARN_RECORDER = LoggerFactory.getLogger(Constants.WARN_LOGGER + CNAME);

    PackageManagementResponse addNewPackage(final PackageManagementRequest request) throws PackageManagementException;

    PackageManagementResponse updatePackageData(final PackageManagementRequest request) throws PackageManagementException;

    PackageManagementResponse removePackageData(final PackageManagementRequest request) throws PackageManagementException;

    PackageManagementResponse getPackageData(final PackageManagementRequest request) throws PackageManagementException;
}
