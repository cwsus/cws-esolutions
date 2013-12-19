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
package com.cws.esolutions.core.dao.processors.interfaces;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.dao.processors.interfaces
 * File: IPlatformDataDAO.java
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

import com.cws.esolutions.core.Constants;
import com.cws.esolutions.core.CoreServiceBean;
import com.cws.esolutions.core.controllers.ResourceControllerBean;
/**
 * Interface for the Platform Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * platform information.
 *
 * @author khuntly
 * @version 1.0
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
     * Allows addition of a new platform into the asset management database. The
     * <code>platformData</code> parameter must contain the following information:
     *
     * 1. Platform GUID - A unique identifier for the platform. This is generated
     *    by the processor and requires no user intervention.
     * 2. Platform Name - A name for the platform. Must be unique.
     * 3. Platform Region = The associated service region for this platform.
     * 4. Platform Deployment Manager - The deployment manager associated with this platform.
     * 5. Platform Appservers - The list of associated application servers for this platform.
     *    This is NOT driven by the deployment manager because it is possible to break them
     *    out into different platforms.
     * 6. Platform Webservers - The list of associated web servers for this platform.
     * 7. Platform Status - The service status for this platform.
     * 8. Platform Description - A short description of this platform.
     *
     * @param platformData - The information to store for the application, as outlined above.
     * @return <code>true</code> if the data is successfully inserted, <code>false</code> otherwise
     * @throws SQLException if an error occurs during data processing
     */
    boolean addNewPlatform(final List<String> platformData) throws SQLException;

    /**
     * Allows updates to be applied to an platform in the asset management database. The
     * <code>applicationData</code> parameter must contain the following information:
     *
     * 1. Platform GUID - The unique identifier associated with the platform to update.
     * 2. Platform Name - A name for the platform. Must be unique.
     * 3. Platform Region = The associated service region for this platform.
     * 4. Platform Deployment Manager - The deployment manager associated with this platform.
     * 5. Platform Appservers - The list of associated application servers for this platform.
     *    This is NOT driven by the deployment manager because it is possible to break them
     *    out into different platforms.
     * 6. Platform Webservers - The list of associated web servers for this platform.
     * 7. Platform Status - The service status for this platform.
     * 8. Platform Description - A short description of this platform.
     *
     * @param platformData - The information to update for the platform, as outlined above.
     * @return <code>true</code> if the data is successfully updated, <code>false</code> otherwise
     * @throws SQLException if an error occurs during data processing
     */
    boolean updatePlatformData(final List<String> platformData) throws SQLException;

    /**
     * Removes the provided platform the asset database, marking it unavailable for future
     * use.
     *
     * @param platformGuid - The unique identifier for the platform to be removed.
     * @return <code>true</code> if the data is successfully updated, <code>false</code> otherwise
     * @throws SQLException if an error occurs during data processing
     */
    boolean deletePlatform(final String platformGuid) throws SQLException;

    /**
     * Returns the information associated with the provided platform identifier. The
     * information returned is as follows:
     *
     * From `esolutionssvc`.`service_platforms`:
     * 1. Platform GUID - The unique identifier associated with the platform to update.
     * 2. Platform Name - A name for the platform. Must be unique.
     * 3. Platform Region = The associated service region for this platform.
     * 4. Platform Appservers - The list of associated application servers for this platform.
     *    This is NOT driven by the deployment manager because it is possible to break them
     *    out into different platforms.
     * 5. Platform Webservers - The list of associated web servers for this platform.
     * 6. Platform Description - A short description of this platform.
     *
     * From `esolutionssvc`.`installed_systems`:
     * 1. System GUID
     * 2. System OS
     * 3. System Status
     * 4. Network Partition
     * 5. Domain Name
     * 6. CPU Type
     * 7. CPU Count
     * 8. Server rack
     * 9. Rack position
     * 10. Server model
     * 11. Serial Number
     * 12. Installed Memory
     * 13. Operational IP
     * 14. Operational Hostname
     * 15. Management IP
     * 16. Management Hostname
     * 17. Backup IP
     * 18. Backup Hostname
     * 19. NAS IP
     * 20. NAS Hostname
     * 21. NAT Address
     * 22. Comments
     * 23. Assigned Engineer
     * 24. Deployment Manager Post
     * 25. Deployment Manager console
     *
     * From `esolutionssvc`.`service_datacenters`
     * 1. Datacenter GUID
     * 2. Datacenter Name
     * 3. Datacenter Status
     * 4. Datacenter Description
     *
     * @param platformGuid - The platform identifier to obtain information for
     * @return <code>List<Object></code> - The information as obtained from the database
     * @throws SQLException if an error occurs during data processing
     */
    List<Object> getPlatformData(final String platformGuid) throws SQLException;

    /**
     * Gets a count of installed platforms in the asset management database.
     * This is used to drive an applicable front-end so that results can be paged
     * through
     *
     * @return Total count of platforms in the database
     * @throws SQLException if an error occurs during data processing
     */
    int getPlatformCount() throws SQLException;

    /**
     * Returns a list of platforms as obtained from the asset database that are listed with
     * a status of "ACTIVE". No other platforms are returned.
     *
     * @param startRow - The starting row for the data records. This is used to drive
     *        pagination in a front-end app.
     * @return <code>List<String[]></code> - The information as obtained from the database:
     *
     * 1. Platform GUID - The unique identifier associated with the platform to update.
     * 2. Platform Name - A name for the platform. Must be unique.
     *
     * @throws SQLException if an error occurs during data processing
     */
    List<String[]> listAvailablePlatforms(final int startRow) throws SQLException;

    /**
     * Returns a list of platforms as obtained from the asset database that are listed with
     * a status of "ACTIVE". No other platforms are returned.
     *
     * @param attribute - A search value to search the database against.
     * @param startRow - The starting row for the data records. This is used to drive
     *        pagination in a front-end app.
     * @return <code>List<String[]></code> - The information as obtained from the database:
     *
     * 1. Platform GUID - The unique identifier associated with the platform to update.
     * 2. Platform Name - A name for the platform. Must be unique.
     *
     * @throws SQLException if an error occurs during data processing
     */
    List<String[]> listPlatformsByAttribute(final String attribute, final int startRow) throws SQLException;
}
