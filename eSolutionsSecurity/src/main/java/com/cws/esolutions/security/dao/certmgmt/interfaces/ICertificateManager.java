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
package com.cws.esolutions.security.dao.certmgmt.interfaces;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.dao.keymgmt.interfaces
 * File: KeyManager.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.io.File;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.config.xml.KeyConfig;
import com.cws.esolutions.security.SecurityServiceConstants;
import com.cws.esolutions.security.config.xml.CertificateConfig;
import com.cws.esolutions.security.dao.keymgmt.exception.KeyManagementException;
/**
 * API allowing user key management tasks. Used in conjunction with the
 * {@link com.cws.esolutions.security.dao.keymgmt.factory.KeyManagementFactory}
 * to provide functionality for LDAP, SQL and file-based key repositories.
 *
 * @author khuntly
 * @version 1.0
 */
public interface ICertificateManager
{
	static final String CNAME = ICertificateManager.class.getName();
    static final SecurityServiceBean svcBean = SecurityServiceBean.getInstance();
    static final KeyConfig keyConfig = svcBean.getConfigData().getKeyConfig();
    static final CertificateConfig certConfig = svcBean.getConfigData().getCertConfig();
    
    static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityServiceConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    /**
     * 
     * @param subjectData
     * @param storePassword
     * @param validityPeriod
     * @param keySize
     * @return
     * @throws KeyManagementException
     */
    File createCertificateRequest(final List<String> subjectData, final String storePassword, final int validityPeriod, final int keySize) throws KeyManagementException;

    /**
     * 
     * @param commonName
     * @param certFile
     * @param storeFile
     * @param storePassword
     * @return
     * @throws KeyManagementException {@link com.cws.esolutions.security.dao.keymgmt.exception.KeyManagementException} if an exception occurs during processing
     */
    boolean applyCertificateRequest(final String commonName, final File certFile, final String storePassword) throws KeyManagementException;
}
