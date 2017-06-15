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
 * Package: com.cws.esolutions.security.dao.certmgmt.interfaces
 * File: ICertificateManager.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly   11/23/2008 22:39:20             Created.
 */
import java.io.File;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.SecurityServiceConstants;
import com.cws.esolutions.security.config.xml.CertificateConfig;
import com.cws.esolutions.security.dao.certmgmt.exception.CertificateManagementException;
/**
 * API allowing certificate management tasks.
 *
 * @author cws-khuntly
 * @version 1.0
 */
public interface ICertificateManager
{
	static final String CNAME = ICertificateManager.class.getName();
    static final SecurityServiceBean svcBean = SecurityServiceBean.getInstance();
    static final CertificateConfig certConfig = svcBean.getConfigData().getCertConfig();
    
    static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityServiceConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    /**
     * Creates a new certificate request for a user or server. The CSR is then provided back to the requestor
     * for processing by a certificate authority, resulting in a useable certificate.
     *
     * @param subjectData - The subject info for the certificate request.
     * @param storePassword - The password utilized against the keystore.
     * @param validityPeriod - How long (in days) the certificate should be valid for.
     * @param keySize - The size of the key to generate (e.g. 2048 bits)
     * @return A CSR file
     * @throws CertificateManagementException if an exception occurs while processing the request
     */
    File createCertificateRequest(final List<String> subjectData, final String storePassword, final int validityPeriod, final int keySize) throws CertificateManagementException;

    /**
     * Applies a certificate response from a certificate authority after a request has been
     * sent and processed.
     *
     * @param commonName - The common name (site name) of the certificate
     * @param certificateFile - The physical certificate file as obtained from the authority
     * @param keystoreFile - The keystore the certificate should be applied to
     * @param storePassword - The password utilized against the keystore.
     * @return true/false, true if the operation completed successfully, false otherwise.
     * @throws CertificateManagementException if an exception occurs while processing the request
     */
    boolean applyCertificateRequest(final String commonName, final File certificateFile, final File keystoreFile, final String storePassword) throws CertificateManagementException;
}
