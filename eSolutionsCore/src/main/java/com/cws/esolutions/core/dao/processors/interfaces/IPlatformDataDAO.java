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
package com.cws.esolutions.core.dao.processors.interfaces;

import java.util.List;
import org.slf4j.Logger;
import javax.sql.DataSource;
import java.sql.SQLException;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.core.Constants;
import com.cws.esolutions.core.CoreServiceBean;
import com.cws.esolutions.core.controllers.ResourceControllerBean;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.dao.interfaces
 * IPlatformDataDAO.java
 *
 * $Id: $
 * $Author: $
 * $Date: $
 * $Revision: $
 * @author kmhuntly@gmail.com
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Jan 4, 2013 3:36:54 PM
 *     Created.
 */
public interface IPlatformDataDAO
{
    static final CoreServiceBean appBean = CoreServiceBean.getInstance();
    static final ResourceControllerBean resBean = appBean.getResourceBean();

    static final String CNAME = IPlatformDataDAO.class.getName();
    static final DataSource dataSource = resBean.getDataSource().get("ApplicationDataSource");

    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CNAME);
    static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    /**
     * Adds a new project to the asset database. Inbound information must include
     * the following (in order):
     * 0: Project GUID
     * 1: Project code
     * 2: Project region
     * 3: Primary owner
     * 4: Secondary owner (if no secondary owner exists, <code>null</code> should be provided
     * 5: Contact email
     * 6: Incident Queue
     * 7: Change Queue
     * 8: Project status (If no status is provided, "INACTIVE" is assumed
     *
     * @param projectData - An <code>ArrayList</code> of the aforementioned data
     * @return <code>true</code> if addition was successful, <code>false</code> otherwise
     * @throws SQLException if an error occurs while inserting data
     */
    boolean addNewPlatform(final List<String> platformData) throws SQLException;

    /**
     * Disables a project from being utilized. This does not physically remove
     * the data from the database, just sets its status to "INACTIVE".
     *
     * @param projectGuid - The GUID of the project to disable
     * @return <code>true</code> if addition was successful, <code>false</code> otherwise
     * @throws SQLException if an error occurs while inserting data
     */
    boolean deletePlatform(final String platformGuid) throws SQLException;

    boolean updatePlatformData(final List<String> platformData) throws SQLException;

    /**
     * Obtains data regarding the provided project GUID for display.
     *
     * @param projectGuid - The GUID of the project to obtain data for
     * @return <code>true</code> if addition was successful, <code>false</code> otherwise
     * @throws SQLException if an error occurs while inserting data
     */
    List<String> getPlatformData(final String platformGuid) throws SQLException;

    /**
     * Obtains data regarding the provided project GUID for display.
     *
     * @param projectGuid - The GUID of the project to obtain data for
     * @return <code>true</code> if addition was successful, <code>false</code> otherwise
     * @throws SQLException if an error occurs while inserting data
     */
    List<String[]> listAvailablePlatforms() throws SQLException;

    List<String[]> listPlatformsByAttribute(final String value) throws SQLException;
}
