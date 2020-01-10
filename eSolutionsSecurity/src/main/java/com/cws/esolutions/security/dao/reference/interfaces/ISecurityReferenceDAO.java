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
package com.cws.esolutions.security.dao.reference.interfaces;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.dao.reference.interfaces
 * File: ISecurityReferenceDAO.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly           11/23/2008 22:39:20             Created.
 */
import java.util.Map;
import java.util.List;
import org.slf4j.Logger;
import javax.sql.DataSource;
import java.sql.SQLException;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.SecurityServiceConstants;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author cws-khuntly
 * @version 1.0
 */
public interface ISecurityReferenceDAO
{
    static final SecurityServiceBean svcBean = SecurityServiceBean.getInstance();
    static final DataSource dataSource = svcBean.getDataSources().get(SecurityServiceConstants.INIT_SECURITYDS_MANAGER);

    static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityServiceConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    /**
     * Processes authentication for the selected security question and user. If successful,
     * a true response is returned back to the frontend signalling that further
     * authentication processing, if required, can take place.
     *
     * @return List - A list of all approved servers within the authorization datastore
     * @throws SQLException {@link java.sql.SQLException} if an exception occurs during processing
     */
    List<String> obtainApprovedServers() throws SQLException;

    /**
     * Processes authentication for the selected security question and user. If successful,
     * a true response is returned back to the frontend signalling that further
     * authentication processing, if required, can take place.
     *
     * @return List - A list of all approved servers within the authorization datastore
     * @throws SQLException {@link java.sql.SQLException} if an exception occurs during processing
     */
    List<String> obtainSecurityQuestionList() throws SQLException;

    /**
     * Processes authentication for the selected security question and user. If successful,
     * a true response is returned back to the frontend signalling that further
     * authentication processing, if required, can take place.
     *
     * @return List - A list of all approved servers within the authorization datastore
     * @throws SQLException {@link java.sql.SQLException} if an exception occurs during processing
     */
    Map<String, String> listAvailableServices() throws SQLException;

    /**
     * Processes authentication for the selected security question and user. If successful,
     * a true response is returned back to the frontend signalling that further
     * authentication processing, if required, can take place.
     *
     * @param group - The group to obtain available services for
     * @return List - A list of all approved servers within the authorization datastore
     * @throws SQLException {@link java.sql.SQLException} if an exception occurs during processing
     */
    List<String> listServicesForGroup(final String group) throws SQLException;
}
