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
import java.util.ArrayList;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.processors.dto.AuditEntry;
import com.cws.esolutions.security.processors.enums.AuditType;
import com.cws.esolutions.security.processors.dto.AuditRequest;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.security.processors.dto.CertificateRequest;
import com.cws.esolutions.security.processors.dto.CertificateResponse;
import com.cws.esolutions.security.processors.exception.AuditServiceException;
import com.cws.esolutions.security.dao.keymgmt.exception.KeyManagementException;
import com.cws.esolutions.security.processors.exception.CertificateRequestException;
import com.cws.esolutions.security.processors.interfaces.ICertificateRequestProcessor;
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
        final String methodName = ICertificateRequestProcessor.CNAME + "#(final CertificateRequest request) throws CertificateRequestException";

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

        if (DEBUG)
        {
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount: {}", authUser);
        	DEBUGGER.debug("subjectData: {}", subjectData);
        }

        try
        {
        	File csrFile = processor.createCertificateRequest(subjectData, request.getStorePassword(), request.getValidityPeriod(), request.getKeySize());

        	if (DEBUG)
        	{
        		DEBUGGER.debug("File: {}", csrFile);
        	}

        	response.setCsrFile(csrFile);
        }
        catch (KeyManagementException kmx)
        {
            ERROR_RECORDER.error(kmx.getMessage(), kmx);

            throw new CertificateRequestException(kmx.getMessage(), kmx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.LOGON);
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
