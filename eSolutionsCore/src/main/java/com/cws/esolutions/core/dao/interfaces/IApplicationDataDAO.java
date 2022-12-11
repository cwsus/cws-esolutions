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
package com.cws.esolutions.core.dao.interfaces;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.dao.processors.interfaces
 * File: IApplicationDataDAO.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.util.List;
import org.slf4j.Logger;
import javax.sql.DataSource;
import java.sql.SQLException;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.core.CoreServicesBean;
import com.cws.esolutions.core.CoreServicesConstants;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author cws-khuntly
 * @version 1.0
 */
public interface IApplicationDataDAO
{
    static final CoreServicesBean appBean = CoreServicesBean.getInstance();

    static final String CNAME = IApplicationDataDAO.class.getName();
    static final DataSource dataSource = appBean.getDataSources().get("ApplicationDataSource");

    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(CoreServicesConstants.ERROR_LOGGER + CNAME);
    static final Logger DEBUGGER = LoggerFactory.getLogger(CoreServicesConstants.DEBUGGER);
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
     * 11. Service GUID - The platform(s) this application is associated with. This
     *     drives deployment processing.
     *
     * @param applicationData - The information to store for the application, as outlined above.
     * @throws SQLException {@link java.sql.SQLException} if an error occurs during data processing
     */
    boolean addApplication(final List<Object> applicationData) throws SQLException;

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
     * 11. Service GUID - The platform(s) this application is associated with. This
     *     drives deployment processing.
     *
     * @param applicationData - The information to update for the application, as outlined above.
     * @throws SQLException {@link java.sql.SQLException} if an error occurs during data processing
     */
    boolean updateApplication(final List<Object> applicationData) throws SQLException;

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
     * 11. Service GUID - The platform(s) this application is associated with. This
     *     drives deployment processing.
     *
     * @param appGuid - The information to update for the application, as outlined above.
     * @throws SQLException {@link java.sql.SQLException} if an error occurs during data processing
     */
    boolean removeApplication(final String appGuid) throws SQLException;

    /**
     * Lists applications stored within the asset management database. This listing
     * can then be utilized by the processor to massage and prepare for display.
     *
     * @param startRow - A starting row to obtain data from, correlated into
     *        pagination.
     * @return A string array of the information contained within the datasource.
     *         Only the application GUID and name are returned.
     * @throws SQLException {@link java.sql.SQLException} if an error occurs during data processing
     */
    List<String[]> listApplications(final int startRow) throws SQLException;

    /**
     * Lists applications stored within the asset management database. This listing
     * can then be utilized by the processor to massage and prepare for display.
     *
     * @param attribute - A search value to obtain information for
     * @param startRow - A starting row to obtain data from, correlated into
     *        pagination.
     * @return A string array of the information contained within the datasource.
     *         Only the application GUID and name are returned.
     * @throws SQLException {@link java.sql.SQLException} if an error occurs during data processing
     */
    List<Object[]> getApplicationsByAttribute(final String attribute, final int startRow) throws SQLException;

    /**
     * Lists applications stored within the asset management database. This listing
     * can then be utilized by the processor to massage and prepare for display.
     *
     * @param appGuid - The application GUID to obtain information for
     * @return A string array of the information contained within the datasource.
     *         Only the application GUID and name are returned.
     * @throws SQLException {@link java.sql.SQLException} if an error occurs during data processing
     */
    List<Object> getApplication(final String appGuid) throws SQLException;
}
