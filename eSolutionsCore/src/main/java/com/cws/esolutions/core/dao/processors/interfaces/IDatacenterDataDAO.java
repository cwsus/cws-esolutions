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
public interface IDatacenterDataDAO
{
    static final CoreServiceBean appBean = CoreServiceBean.getInstance();
    static final ResourceControllerBean resBean = appBean.getResourceBean();

    static final String CNAME = IDatacenterDataDAO.class.getName();
    static final DataSource dataSource = resBean.getDataSource().get("ApplicationDataSource");

    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CNAME);
    static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    boolean addNewDatacenter(final List<String> serverData) throws SQLException;

    boolean removeExistingDatacenter(final String serverGuid) throws SQLException;

    int getDatacenterCount() throws SQLException;

    List<String[]> getAvailableDataCenters(final int startRow) throws SQLException;

    List<String[]> getDataCenterByAttribute(final String attribute) throws SQLException;

    List<String> getDatacenter(final String attribute) throws SQLException;
}
