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
package com.cws.esolutions.security.dao.audit.interfaces;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.dao.audit.interfaces
 * File: IAuditDAO.java
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

import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.SecurityServiceConstants;
/**
 * API allowing audit management tasks - storing audit requests and retrieving
 * for provided user accounts on demand.
 *
 * @author cws-khuntly
 * @version 1.0
 */
public interface IAuditDAO
{
    static final SecurityServiceBean svcBean = SecurityServiceBean.getInstance();
    static final DataSource dataSource = (DataSource) svcBean.getAuditDataSource();

    static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityServiceConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger AUDIT_RECORDER = LoggerFactory.getLogger(SecurityServiceConstants.AUDIT_LOGGER);

    /**
     * Inserts audit-related data into the audit datastore
     *
     * @param auditRequest - A <code>List</code> of the audit data to insert
     * @throws SQLException {@link java.sql.SQLException} if an exception occurs during processing
     */
    void auditRequestedOperation(final List<String> auditRequest) throws SQLException;

    /**
     * Inserts audit-related data into the audit datastore
     *
     * @param username - The username to obtain data for
     * @param startRow - A limit of rows to obtain, if there are more than this limit the remainder
     * will be paged
     * @return A <code>List</code> of the associated audit data for the account 
     * @throws SQLException {@link java.sql.SQLException} if an exception occurs during processing
     */
    List<Object> getAuditInterval(final String username, final int startRow) throws SQLException;
}
