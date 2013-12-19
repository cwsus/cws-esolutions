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
 * File: IUserServiceInformationDAO.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
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
public interface IUserServiceInformationDAO
{
    static final SecurityServiceBean svcBean = SecurityServiceBean.getInstance();
    static final ResourceControllerBean resBean = svcBean.getResourceBean();

    static final String CNAME = IUserServiceInformationDAO.class.getName();
    static final DataSource dataSource = resBean.getDataSource().get(SecurityConstants.INIT_SECURITYDS_MANAGER);

    static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityConstants.ERROR_LOGGER + CNAME);

    // project authorizations
    boolean addProjectIdForUser(final String userGuid, final String projectGuid) throws SQLException;

    /**
     * Removes a service id from a particular user. Returns true if the service
     * id was successfully removed, false under all other conditions.
     *
     * @param userGuid
     * @param projectGuid
     * @return boolean
     * @throws SQLException
     */
    boolean removeProjectIdForUser(final String userGuid, final String projectGuid) throws SQLException;

    /**
     * Removes a service id from a particular user. Returns true if the service
     * id was successfully removed, false under all other conditions.
     *
     * @param userGuid
     * @param projectGuid
     * @return boolean
     * @throws SQLException
     */
    boolean verifyProjectForUser(final String userGuid, final String projectGuid) throws SQLException;

    /**
     * Obtains and returns a list of the provided user's available services.
     * Typically, these are objects the user is authorized to access.
     *
     * @param userGuid
     * @return List<String>
     * @throws SQLException
     */
    List<String> returnUserAuthorizedProjects(final String userGuid) throws SQLException;

    // service authorizations
    /**
     * Allows an administrator to add access to a provided service for the given user account
     *
     * @param userGuid - The user's common name (globally unique identifier), as obtained LDAP
     * @param serviceGuid - The GUID of the service that the user is requesting access to
     * @return <code>true</code> if the operation completes successfully, <code>false</code> otherwise
     * @throws SQLException if a database access error occurs
     */
    boolean addServiceToUser(final String userGuid, final String serviceGuid) throws SQLException;

    /**
     * Allows an administrator to revoke access to a previously provided service - e.g. a user
     * had access to systems management but is no longer systems personnel
     *
     * @param userGuid - The user's common name (globally unique identifier), as obtained LDAP
     * @param serviceGuid - The GUID of the service that the user is requesting access to
     * @return <code>true</code> if the operation completes successfully, <code>false</code> otherwise
     * @throws SQLException if a database access error occurs
     */
    boolean removeServiceFromUser(final String userGuid, final String serviceGuid) throws SQLException;

    /**
     * Verifies that a provided user has access to the given service
     *
     * @param userGuid - The user's common name (globally unique identifier), as obtained LDAP
     * @param serviceGuid - The GUID of the service that the user is requesting access to
     * @return <code>true</code> if the user has access to the item, <code>false</code> otherwise
     * @throws SQLException if a database access error occurs
     */
    boolean verifyServiceForUser(final String userGuid, final String serviceGuid) throws SQLException;

    /**
     * Returns the list of services that are assigned to the given user account. This list is then
     * utilized by the security processors to ensure that the user has access to a given service
     * within the application prior to executing the request
     *
     * @param userGuid - The user's common name (globally unique identifier), as obtained LDAP
     * @return Map<String, String> - A hashmap of the given service UID's and their descriptions
     * @throws SQLException if a database error occurs attempting to access data
     */
    List<String> listServicesForUser(final String userGuid) throws SQLException;
}
