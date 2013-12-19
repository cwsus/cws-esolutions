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
package com.cws.esolutions.security.dao.reference.interfaces;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.security.dao.reference.interfaces
 * File: ISecurityReferenceDAO.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.Map;
import java.util.List;
import org.slf4j.Logger;
import javax.sql.DataSource;
import java.sql.SQLException;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.security.SecurityConstants;
import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.core.controllers.ResourceControllerBean;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 */
public interface ISecurityReferenceDAO
{
    static final String CNAME = ISecurityReferenceDAO.class.getName();

    static final SecurityServiceBean svcBean = SecurityServiceBean.getInstance();
    static final ResourceControllerBean resBean = svcBean.getResourceBean();
    static final DataSource dataSource = resBean.getDataSource().get(SecurityConstants.INIT_SECURITYDS_MANAGER);

    static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityConstants.ERROR_LOGGER + CNAME);

    List<String> obtainApprovedServers() throws SQLException;

    /**
     * Obtains the configured user security questions from a datastore
     * for display.
     *
     * @return List<String> - a list of the available security questions
     * @throws SQLException
     */
    List<String> obtainSecurityQuestionList() throws SQLException;

    Map<String, String> listAvailableServices() throws SQLException;
}
