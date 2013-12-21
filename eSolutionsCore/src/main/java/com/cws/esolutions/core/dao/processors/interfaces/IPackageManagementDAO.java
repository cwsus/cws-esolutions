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
 * File: IPackageManagementDAO.java
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
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 */
public interface IPackageManagementDAO
{
    static final CoreServiceBean appBean = CoreServiceBean.getInstance();
    static final ResourceControllerBean resBean = appBean.getResourceBean();

    static final String CNAME = IApplicationDataDAO.class.getName();
    static final DataSource dataSource = resBean.getDataSource().get("ApplicationDataSource");

    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CNAME);
    static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    /**
     * Allows addition of a new application into the asset management database. The
     * <code>applicationData</code> parameter must contain the following information:
     *
     * 1. Application GUID - A unique identifier for the application. This is generated
     *    by the processor and requires no user intervention.
     * 2. Application Name - A name for the application. Must be unique.
     * 3. Application Version = Assigned version number, this is used to drive deployments
     * 4. Base Path - The root path for the application.
     * 5. SCM Path - If this application is SCM enabled, this should contain the path to
     *    the target.
     * 6. Cluster Name - The cluster this application will be installed to. Can be blank.
     * 7. JVM Name - The JVM name that this application will be installed to. Cannot be blank.
     * 8. Install Path - Relative to the "Base Path" above, this is where the binaries are stored.
     * 9. Logs Directory - Where application logs can be found on the filesystem. Can be relative
     *    to the base path as above, or located elsewhere.
     * 10. PID Directory - Where the PID will live for this application. Cannot be blank.
     * 11. Project GUID - The project this application is associated with.
     * 12. Platform GUID - The platform(s) this application is associated with. This
     *     drives deployment processing.
     *
     * @param packageData - The information to store for the application, as outlined above.
     * @return <code>true</code> if the data is successfully inserted, <code>false</code> otherwise
     * @throws SQLException if an error occurs during data processing
     */
    boolean addNewPackage(final List<String> packageData) throws SQLException;

    /**
     * Allows updates to be applied to an application in the asset management database. The
     * <code>applicationData</code> parameter must contain the following information:
     *
     * 1. Application GUID - A unique identifier for the application. This is generated
     *    by the processor and requires no user intervention.
     * 2. Application Name - A name for the application. Must be unique.
     * 3. Application Version = Assigned version number, this is used to drive deployments
     * 4. Base Path - The root path for the application.
     * 5. SCM Path - If this application is SCM enabled, this should contain the path to
     *    the target.
     * 6. Cluster Name - The cluster this application will be installed to. Can be blank.
     * 7. JVM Name - The JVM name that this application will be installed to. Cannot be blank.
     * 8. Install Path - Relative to the "Base Path" above, this is where the binaries are stored.
     * 9. Logs Directory - Where application logs can be found on the filesystem. Can be relative
     *    to the base path as above, or located elsewhere.
     * 10. PID Directory - Where the PID will live for this application. Cannot be blank.
     * 11. Project GUID - The project this application is associated with.
     * 12. Platform GUID - The platform(s) this application is associated with. This
     *     drives deployment processing.
     *
     * @param applicationData - The information to update for the application, as outlined above.
     * @return <code>true</code> if the data is successfully updated, <code>false</code> otherwise
     * @throws SQLException if an error occurs during data processing
     */
    boolean updatePackage(final List<String> packageData) throws SQLException;

    /**
     * Allows addition of a new application into the asset management database. The
     * <code>applicationData</code> parameter must contain the following information:
     *
     * 1. Application GUID - A unique identifier for the application. This is generated
     *    by the processor and requires no user intervention.
     * 2. Application Name - A name for the application. Must be unique.
     * 3. Application Version = Assigned version number, this is used to drive deployments
     * 4. Base Path - The root path for the application.
     * 5. SCM Path - If this application is SCM enabled, this should contain the path to
     *    the target.
     * 6. Cluster Name - The cluster this application will be installed to. Can be blank.
     * 7. JVM Name - The JVM name that this application will be installed to. Cannot be blank.
     * 8. Install Path - Relative to the "Base Path" above, this is where the binaries are stored.
     * 9. Logs Directory - Where application logs can be found on the filesystem. Can be relative
     *    to the base path as above, or located elsewhere.
     * 10. PID Directory - Where the PID will live for this application. Cannot be blank.
     * 11. Project GUID - The project this application is associated with.
     * 12. Platform GUID - The platform(s) this application is associated with. This
     *     drives deployment processing.
     *
     * @param appGuid - The information to update for the application, as outlined above.
     * @return <code>true</code> if the data is successfully inserted, <code>false</code> otherwise
     * @throws SQLException if an error occurs during data processing
     */
    boolean deletePackage(final String packageGuid) throws SQLException;

    /**
     * Gets a count of installed applications in the asset management database.
     * This is used to drive an applicable front-end so that results can be paged
     * through
     *
     * @return Total count of applications in the database
     * @throws SQLException if an error occurs during data processing
     */
    int getPackageCount() throws SQLException;

    /**
     * Lists applications stored within the asset management database. This listing
     * can then be utilized by the processor to massage and prepare for display.
     *
     * @param startRow - A starting row to obtain data from, correlated into
     *        pagination.
     * @return A string array of the information contained within the datasource.
     *         Only the application GUID and name are returned.
     * @throws SQLException if an error occurs during data processing
     */
    List<String[]> listInstalledPackages(final int startRow) throws SQLException;

    /**
     * Lists applications stored within the asset management database. This listing
     * can then be utilized by the processor to massage and prepare for display.
     *
     * @return A string array of the information contained within the datasource.
     *         Only the application GUID and name are returned.
     * @throws SQLException if an error occurs during data processing
     */
    List<String> getPackageData(final String appGuid) throws SQLException;

    /**
     * Lists applications stored within the asset management database. This listing
     * can then be utilized by the processor to massage and prepare for display.
     *
     * @param attribute - A search value to obtain information for
     * @param startRow - A starting row to obtain data from, correlated into
     *        pagination.
     * @return A string array of the information contained within the datasource.
     *         Only the application GUID and name are returned.
     * @throws SQLException if an error occurs during data processing
     */
    List<String[]> getPackagesByAttribute(final String attribute, final int startRow) throws SQLException;
}
