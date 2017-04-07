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
package com.cws.esolutions.security.processors.impl;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.processors.impl
 * File: CertificateRequestProcessor.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   03/28/2017 01:41:00             Created.
 */
import java.io.File;
import java.util.List;
import java.util.Arrays;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.io.FileUtils;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.SecurityServiceConstants;
import com.cws.esolutions.security.processors.dto.AuditEntry;
import com.cws.esolutions.security.processors.enums.AuditType;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.processors.dto.AuditRequest;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.security.processors.dto.CertificateRequest;
import com.cws.esolutions.security.processors.dto.CertificateResponse;
import com.cws.esolutions.security.processors.exception.AuditServiceException;
import com.cws.esolutions.security.processors.exception.CertificateRequestException;
import com.cws.esolutions.security.processors.interfaces.ICertificateRequestProcessor;
import com.cws.esolutions.security.dao.certmgmt.exception.CertificateManagementException;
/**
 * @author khuntly
 * @version 1.0
 * @see com.cws.esolutions.security.processors.interfaces.ICertificateRequestProcessor
 */
public class CertificateRequestProcessorImpl implements ICertificateRequestProcessor
{
	/**
	 * @see com.cws.esolutions.security.processors.interfaces.ICertificateRequestProcessor#generateCertificateRequest(com.cws.esolutions.security.processors.dto.CertificateRequest)
	 */
	public CertificateResponse generateCertificateRequest(final CertificateRequest request) throws CertificateRequestException
	{
        final String methodName = ICertificateRequestProcessor.CNAME + "#generateCertificateRequest(final CertificateRequest request) throws CertificateRequestException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("CertificateRequest: {}", request);
        }

        CertificateResponse response = new CertificateResponse();

        final RequestHostInfo reqInfo = request.getHostInfo();
        final UserAccount authUser = request.getUserAccount();
        final List<String> subjectData = new ArrayList<String>(
        		Arrays.asList(
        				request.getCommonName(),
        				request.getOrganizationalUnit(),
        				request.getOrganizationName(),
        				request.getLocalityName(),
        				request.getStateName(),
        				request.getCountryName(),
        				request.getContactEmail()));
        final File rootDirectory = FileUtils.getFile(certConfig.getRootDirectory());
        final File privateKeyDirectory = FileUtils.getFile(certConfig.getPrivateKeyDirectory() + "/" + request.getCommonName());
        final File publicKeyDirectory = FileUtils.getFile(certConfig.getPublicKeyDirectory() + "/" + request.getCommonName());
        final File csrDirectory = FileUtils.getFile(certConfig.getCsrDirectory() + "/" + request.getCommonName());
        final File storeDirectory = FileUtils.getFile(certConfig.getStoreDirectory() + "/" + request.getCommonName());

        if (DEBUG)
        {
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("authUser: {}", authUser);
        	DEBUGGER.debug("subjectData: {}", subjectData);
        	DEBUGGER.debug("rootDirectory: {}", rootDirectory);
        	DEBUGGER.debug("privateKeyDirectory: {}", privateKeyDirectory);
        	DEBUGGER.debug("publicKeyDirectory: {}", publicKeyDirectory);
        	DEBUGGER.debug("csrDirectory: {}", csrDirectory);
        	DEBUGGER.debug("storeDirectory: {}", storeDirectory);
        }

        try
        {
            if (!(rootDirectory.canWrite()))
            {
            	if (!(rootDirectory.mkdirs()))
            	{
            		throw new CertificateRequestException("Root certificate directory either does not exist or cannot be written to. Cannot continue.");
            	}
            }

            if (!(certConfig.getRootCertificateFile().exists()))
            {
                throw new CertificateRequestException("Root certificate file does not exist. Cannot continue."); 
            }

            if (!(certConfig.getIntermediateCertificateFile().exists()))
            {
                throw new CertificateRequestException("Intermediate certificate file does not exist. Cannot continue."); 
            }

            if (!(privateKeyDirectory.canWrite()))
            {
                if (!(privateKeyDirectory.mkdirs()))
                {
                    throw new CertificateRequestException("Private directory either does not exist or cannot be written to. Cannot continue.");
                }
            }

            if (!(publicKeyDirectory.canWrite()))
            {
                if (!(publicKeyDirectory.mkdirs()))
                {
                    throw new CertificateRequestException("Private directory either does not exist or cannot be written to. Cannot continue.");
                }
            }

            if (!(csrDirectory.canWrite()))
            {
                if (!(csrDirectory.mkdirs()))
                {
                    throw new CertificateRequestException("CSR directory either does not exist or cannot be written to. Cannot continue.");
                }
            }

            if (!(storeDirectory.canWrite()))
            {
                if (!(storeDirectory.mkdirs()))
                {
                    throw new CertificateRequestException("Keystore directory either does not exist or cannot be written to. Cannot continue.");
                }
            }

            // check if an there's an existing entry, if so just return it
            if (FileUtils.getFile(csrDirectory + "/" + request.getCommonName() + SecurityServiceConstants.CSR_FILE_EXT).exists())
            {
        		response.setRequestStatus(SecurityRequestStatus.SUCCESS);
        		response.setCsrFile(FileUtils.getFile(csrDirectory + "/" + request.getCommonName() + SecurityServiceConstants.CSR_FILE_EXT));

        		return response;
            }

        	File csrFile = processor.createCertificateRequest(subjectData, request.getStorePassword(), request.getValidityPeriod(), request.getKeySize());

        	if (DEBUG)
        	{
        		DEBUGGER.debug("File: {}", csrFile);
        	}

        	if (csrFile != null)
        	{
        		response.setRequestStatus(SecurityRequestStatus.SUCCESS);
        		response.setCsrFile(csrFile);
        	}
        	else
        	{
        		response.setRequestStatus(SecurityRequestStatus.FAILURE);
        	}
        }
        catch (CertificateManagementException cmx)
        {
        	// clean up
        	try
        	{
				FileUtils.forceDelete(privateKeyDirectory);
				FileUtils.forceDelete(publicKeyDirectory);
	        	FileUtils.forceDelete(csrDirectory);
	        	FileUtils.forceDelete(storeDirectory);
			}
        	catch (IOException iox)
        	{
        		ERROR_RECORDER.error(iox.getMessage(), iox);
			}

            ERROR_RECORDER.error(cmx.getMessage(), cmx);

            throw new CertificateRequestException(cmx.getMessage(), cmx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.GENERATECERT);
                auditEntry.setUserAccount(authUser);
                auditEntry.setApplicationId(request.getApplicationId());
                auditEntry.setApplicationName(request.getApplicationName());

                if (DEBUG)
                {
                    DEBUGGER.debug("AuditEntry: {}", auditEntry);
                }

                AuditRequest auditRequest = new AuditRequest();
                auditRequest.setAuditEntry(auditEntry);

                if (DEBUG)
                {
                    DEBUGGER.debug("AuditRequest: {}", auditRequest);
                }

                auditor.auditRequest(auditRequest);
            }
            catch (AuditServiceException asx)
            {
                ERROR_RECORDER.error(asx.getMessage(), asx);
            }
        }

        return response;
	}

	/**
	 * @see com.cws.esolutions.security.processors.interfaces.ICertificateRequestProcessor#generateCertificateRequest(com.cws.esolutions.security.processors.dto.CertificateRequest)
	 */
	public CertificateResponse applyCertificateResponse(final CertificateRequest request) throws CertificateRequestException
	{
        final String methodName = ICertificateRequestProcessor.CNAME + "#applyCertificateResponse(final CertificateRequest request) throws CertificateRequestException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("CertificateRequest: {}", request);
        }

        CertificateResponse response = new CertificateResponse();

        final RequestHostInfo reqInfo = request.getHostInfo();
        final UserAccount authUser = request.getUserAccount();
        final List<String> subjectData = new ArrayList<String>(
        		Arrays.asList(
        				request.getCommonName(),
        				request.getOrganizationalUnit(),
        				request.getOrganizationName(),
        				request.getLocalityName(),
        				request.getStateName(),
        				request.getCountryName(),
        				request.getContactEmail()));
        final File rootDirectory = FileUtils.getFile(certConfig.getRootDirectory());
        final File storeDirectory = FileUtils.getFile(certConfig.getStoreDirectory() + "/" + request.getCommonName());
        final File certificateDirectory = FileUtils.getFile(certConfig.getCertificateDirectory() + "/" + request.getCommonName());
        final File keystoreFile = FileUtils.getFile(storeDirectory + "/" + request.getCommonName() + SecurityServiceConstants.KEYSTORE_FILE_EXT);
        final File certificateFile = FileUtils.getFile(certificateDirectory + "/" + request.getCommonName() + SecurityServiceConstants.CERTIFICATE_FILE_EXT);

        if (DEBUG)
        {
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("authUser: {}", authUser);
        	DEBUGGER.debug("subjectData: {}", subjectData);
        	DEBUGGER.debug("rootDirectory: {}", rootDirectory);
        	DEBUGGER.debug("storeDirectory: {}", storeDirectory);
        	DEBUGGER.debug("certificateDirectory: {}", certificateDirectory);
        	DEBUGGER.debug("keystoreFile: {}", keystoreFile);
        	DEBUGGER.debug("certificateFile: {}", certificateFile);
        }

        try
        {
            if (!(rootDirectory.canWrite()))
            {
            	if (!(rootDirectory.mkdirs()))
            	{
            		throw new CertificateRequestException("Root certificate directory either does not exist or cannot be written to. Cannot continue.");
            	}
            }

            if (!(certConfig.getRootCertificateFile().exists()))
            {
                throw new CertificateRequestException("Root certificate file does not exist. Cannot continue."); 
            }

            if (!(certConfig.getIntermediateCertificateFile().exists()))
            {
                throw new CertificateRequestException("Intermediate certificate file does not exist. Cannot continue."); 
            }

            if (!(certificateDirectory.canWrite()))
            {
                throw new CertificateRequestException("Certificate directory either does not exist or cannot be written to. Cannot continue.");
            }

            if (!(storeDirectory.canWrite()))
            {
                throw new CertificateRequestException("Keystore directory either does not exist or cannot be written to. Cannot continue.");
            }

            boolean isComplete = processor.applyCertificateRequest(request.getCommonName(), certificateFile, keystoreFile, request.getStorePassword());

        	if (DEBUG)
        	{
        		DEBUGGER.debug("File: {}", isComplete);
        	}

        	if (isComplete)
        	{
        		response.setRequestStatus(SecurityRequestStatus.SUCCESS);
        	}
        	else
        	{
        		response.setRequestStatus(SecurityRequestStatus.FAILURE);
        	}
        }
        catch (CertificateManagementException cmx)
        {
            ERROR_RECORDER.error(cmx.getMessage(), cmx);

            throw new CertificateRequestException(cmx.getMessage(), cmx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.APPLYCERT);
                auditEntry.setUserAccount(authUser);
                auditEntry.setApplicationId(request.getApplicationId());
                auditEntry.setApplicationName(request.getApplicationName());

                if (DEBUG)
                {
                    DEBUGGER.debug("AuditEntry: {}", auditEntry);
                }

                AuditRequest auditRequest = new AuditRequest();
                auditRequest.setAuditEntry(auditEntry);

                if (DEBUG)
                {
                    DEBUGGER.debug("AuditRequest: {}", auditRequest);
                }

                auditor.auditRequest(auditRequest);
            }
            catch (AuditServiceException asx)
            {
                ERROR_RECORDER.error(asx.getMessage(), asx);
            }
        }

        return response;
	}
}
