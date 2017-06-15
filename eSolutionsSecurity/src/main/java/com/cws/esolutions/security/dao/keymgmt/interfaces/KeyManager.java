/*
 * Copyright (c) 2009 - 2017 CaspersBox Web Services
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
package com.cws.esolutions.security.dao.keymgmt.interfaces;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.dao.keymgmt.interfaces
 * File: KeyManager.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly   11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
import java.security.KeyPair;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.config.xml.KeyConfig;
import com.cws.esolutions.security.SecurityServiceConstants;
import com.cws.esolutions.security.config.xml.SecurityConfig;
import com.cws.esolutions.security.config.xml.RepositoryConfig;
import com.cws.esolutions.security.config.xml.SecurityReturningAttributes;
import com.cws.esolutions.security.dao.reference.impl.SecurityReferenceDAOImpl;
import com.cws.esolutions.security.dao.keymgmt.exception.KeyManagementException;
import com.cws.esolutions.security.dao.reference.interfaces.ISecurityReferenceDAO;
/**
 * API allowing user key management tasks. Used in conjunction with the
 * {@link com.cws.esolutions.security.dao.keymgmt.factory.KeyManagementFactory}
 * to provide functionality for LDAP, SQL and file-based key repositories.
 *
 * @author cws-khuntly
 * @version 1.0
 */
public interface KeyManager
{
    static final ISecurityReferenceDAO secRef = new SecurityReferenceDAOImpl();
    static final SecurityServiceBean svcBean = SecurityServiceBean.getInstance();
    static final KeyConfig keyConfig = svcBean.getConfigData().getKeyConfig();
    static final SecurityConfig secConfig = svcBean.getConfigData().getSecurityConfig();
    static final RepositoryConfig repoConfig = svcBean.getConfigData().getRepoConfig();
    static final SecurityReturningAttributes securityAttributes = repoConfig.getSecurityAttributes();
    
    static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityServiceConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    /**
     * Obtains the keys associated with a given user account for use
     * with file encryption/signature processing
     *
     * @param guid - the GUID to validate data against
     * @return a <code>KeyPair</code> if key generation was successful
     * @throws KeyManagementException {@link com.cws.esolutions.security.dao.keymgmt.exception.KeyManagementException} if an exception occurs during processing
     */
    KeyPair returnKeys(final String guid) throws KeyManagementException;

    /**
     * Creates keys for the associated user account for use with file
     * encryption/signature processing.
     *
     * @param guid - the GUID to validate data against
     * @return boolean - <code>true</code> if verified, <code>false</code> otherwise
     * @throws KeyManagementException {@link com.cws.esolutions.security.dao.keymgmt.exception.KeyManagementException} if an exception occurs during processing
     */
    boolean createKeys(final String guid) throws KeyManagementException;

    /**
     * 
     * @param commonName
     * @param orgUnitName
     * @param orgName
     * @param localityName
     * @param stateName
     * @param countryName
     * @param contactEmail
     * @param validityPeriod
     * @param keySize
     * @return
     * @throws KeyManagementException {@link com.cws.esolutions.security.dao.keymgmt.exception.KeyManagementException} if an exception occurs during processing
     */
    // KeyPair createCertificateRequest(final String commonName, final String orgUnitName, final String orgName, final String localityName, final String stateName, final String countryName, final String contactEmail, final int validityPeriod, final int keySize) throws KeyManagementException;

    /**
     * Revokes and removes the keys associated with a provided user account so they cannot be re-used.
     *
     * @param guid - the GUID to validate data against
     * @return boolean - <code>true</code> if successful, <code>false</code> otherwise
     * @throws KeyManagementException {@link com.cws.esolutions.security.dao.keymgmt.exception.KeyManagementException} if an exception occurs during processing
     */
    boolean removeKeys(final String guid) throws KeyManagementException;
}
