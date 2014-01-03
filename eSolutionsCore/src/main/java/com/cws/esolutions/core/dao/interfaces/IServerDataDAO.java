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
package com.cws.esolutions.core.dao.interfaces;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.dao.processors.interfaces
 * File: IServerDataDAO.java
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

import com.cws.esolutions.core.CoreServiceConstants;
import com.cws.esolutions.core.CoreServiceBean;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 */
public interface IServerDataDAO
{
    static final CoreServiceBean appBean = CoreServiceBean.getInstance();

    static final String CNAME = IServerDataDAO.class.getName();
    static final DataSource dataSource = appBean.getDataSource().get("ApplicationDataSource");

    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(CoreServiceConstants.ERROR_LOGGER + CNAME);
    static final Logger DEBUGGER = LoggerFactory.getLogger(CoreServiceConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    boolean addNewServer(final List<Object> serverData) throws SQLException;

    boolean removeExistingServer(final String serverGuid) throws SQLException;

    boolean modifyServerData(final String serverGuid, final List<Object> serverData) throws SQLException;

    List<Object> getInstalledServer(final String serverGuid) throws SQLException;

    int getServerCount() throws SQLException;

    int validateServerHostName(final String hostName) throws SQLException;

    List<String[]> getInstalledServers(final int startRow) throws SQLException;

    List<String[]> getServersByAttribute(final String serverType, final int startRow) throws SQLException;

    List<String[]> getRetiredServers(final int startRow) throws SQLException;

    List<Object> getRetiredServer(final String guid) throws SQLException;

    void archiveServerData(final String value) throws SQLException;
}
