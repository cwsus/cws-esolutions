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
 * IServerDataDAO.java
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
 * kh05451 @ Jan 4, 2013 3:36:35 PM
 *     Created.
 */
public interface IServerDataDAO
{
    static final CoreServiceBean appBean = CoreServiceBean.getInstance();
    static final ResourceControllerBean resBean = appBean.getResourceBean();

    static final String CNAME = IServerDataDAO.class.getName();
    static final DataSource dataSource = resBean.getDataSource().get("ApplicationDataSource");

    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CNAME);
    static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    boolean addNewServer(final List<Object> serverData) throws SQLException;

    boolean removeExistingServer(final String serverGuid) throws SQLException;

    boolean modifyServerData(final String serverGuid, final List<Object> serverData) throws SQLException;

    List<Object> getInstalledServer(final String serverGuid) throws SQLException;

    int getServerCount() throws SQLException;

    int validateServerHostName(final String hostName) throws SQLException;

    List<Object[]> getServersForDmgr(final String dmgr) throws SQLException;

    List<Object[]> getInstalledServers(final int startRow) throws SQLException;

    List<Object[]> getServersByAttribute(final String serverType, final int startRow) throws SQLException;

    List<Object[]> getServersByAttributeWithRegion(final String attribute, final String region, final int startRow) throws SQLException;
}
