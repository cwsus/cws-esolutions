/*
 * Copyright (c) 2009 - 2014 CaspersBox Web Services
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
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
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
 * @author khuntly
 * @version 1.0
 */
public interface IAuditDAO
{
    static final String CNAME = IAuditDAO.class.getName();
    static final SecurityServiceBean svcBean = SecurityServiceBean.getInstance();
    static final DataSource dataSource = svcBean.getDataSources().get(SecurityServiceConstants.INIT_AUDITDS_MANAGER);

    static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityServiceConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger AUDIT_RECORDER = LoggerFactory.getLogger(SecurityServiceConstants.AUDIT_LOGGER + CNAME);
    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityServiceConstants.ERROR_LOGGER + CNAME);

    void auditRequestedOperation(final List<String> auditRequest) throws SQLException;

    List<Object> getAuditInterval(final String username, final int startRow) throws SQLException;
}
