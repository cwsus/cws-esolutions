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
package com.cws.esolutions.core.processors.impl;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.processors.impl
 * File: SystemCheckProcessorImpl.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import org.apache.commons.lang3.StringUtils;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.core.dto.CoreServicesRequest;
import com.cws.esolutions.core.dto.CoreServicesResponse;
import com.cws.esolutions.core.enums.CoreServicesStatus;
import com.cws.esolutions.core.processors.dto.Server;
import com.cws.esolutions.security.processors.dto.AuditEntry;
import com.cws.esolutions.security.processors.enums.AuditType;
import com.cws.esolutions.security.processors.dto.AuditRequest;
import com.cws.esolutions.core.processors.enums.SystemCheckType;
import com.cws.esolutions.core.processors.dto.SystemCheckRequest;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.dto.SystemCheckResponse;
import com.cws.esolutions.core.processors.dto.ServiceCheckRequest;
import com.cws.esolutions.core.processors.dto.ServiceCheckResponse;
import com.cws.esolutions.core.processors.exception.SystemCheckException;
import com.cws.esolutions.core.processors.interfaces.ISystemCheckProcessor;
import com.cws.esolutions.security.services.dto.AccessControlServiceRequest;
import com.cws.esolutions.security.services.dto.AccessControlServiceResponse;
import com.cws.esolutions.security.processors.exception.AuditServiceException;
import com.cws.esolutions.security.services.exception.AccessControlServiceException;
/**
 * @see com.cws.esolutions.core.processors.interfaces.ISystemCheckProcessor
 */
public class SystemCheckProcessorImpl implements ISystemCheckProcessor
{
    /**
     * @see com.cws.esolutions.core.processors.interfaces.ISystemCheckProcessor#runNetstatCheck(com.cws.esolutions.core.processors.dto.SystemCheckRequest)
     */
    public SystemCheckResponse runNetstatCheck(final SystemCheckRequest request) throws SystemCheckException
    {
        final String methodName = ISystemCheckProcessor.CNAME + "#runNetstatCheck(final SystemCheckRequest request) throws SystemCheckException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("SystemCheckRequest: {}", request);
        }

        CoreServicesResponse agentResponse = null;
        SystemCheckResponse response = new SystemCheckResponse();

        final Server server = request.getSourceServer();
        final RequestHostInfo reqInfo = request.getRequestInfo();
        final UserAccount userAccount = request.getUserAccount();

        if (DEBUG)
        {
            DEBUGGER.debug("Server: {}", server);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount: {}", userAccount);
        }

        try
        {
            // this will require admin and service authorization
            AccessControlServiceRequest accessRequest = new AccessControlServiceRequest();
            accessRequest.setUserAccount(userAccount);

            if (DEBUG)
            {
                DEBUGGER.debug("AccessControlServiceRequest: {}", accessRequest);
            }

            AccessControlServiceResponse accessResponse = accessControl.isUserAuthorized(accessRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("AccessControlServiceResponse accessResponse: {}", accessResponse);
            }

            if (!(accessResponse.getIsUserAuthorized()))
            {
                // unauthorized
                response.setRequestStatus(CoreServicesStatus.UNAUTHORIZED);

                // audit
                try
                {
                    AuditEntry auditEntry = new AuditEntry();
                    auditEntry.setHostInfo(reqInfo);
                    auditEntry.setAuditType(AuditType.NETSTAT);
                    auditEntry.setAuthorized(Boolean.FALSE);
                    auditEntry.setUserAccount(userAccount);
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
                catch (final AuditServiceException asx)
                {
                    ERROR_RECORDER.error(asx.getMessage(), asx);
                }

                return response;
            }

            ServiceCheckRequest systemReq = new ServiceCheckRequest();
            systemReq.setRequestType(SystemCheckType.NETSTAT);
            systemReq.setPortNumber(request.getPortNumber());

            if (DEBUG)
            {
                DEBUGGER.debug("ServiceCheckRequest: {}", request);
            }

            CoreServicesRequest agentRequest = new CoreServicesRequest();
            agentRequest.setAppName(appConfig.getAppName());
            agentRequest.setRequestPayload(systemReq);

            if (DEBUG)
            {
                DEBUGGER.debug("CoreServicesRequest: {}", agentRequest);
            }

            String correlator = null;

            if (DEBUG)
            {
                DEBUGGER.debug("correlator: {}", correlator);
            }

            if (StringUtils.isNotEmpty(correlator))
            {

            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.FAILURE);

                return response;
            }

            if (DEBUG)
            {
                DEBUGGER.debug("CoreServicesResponse: {}", agentResponse);
            }

            if (agentResponse.getRequestStatus() == CoreServicesStatus.SUCCESS)
            {
                ServiceCheckResponse systemRes = (ServiceCheckResponse) agentResponse.getResponsePayload();

                if (DEBUG)
                {
                    DEBUGGER.debug("ServiceCheckResponse: {}", systemRes);
                }

                response.setRequestStatus(CoreServicesStatus.valueOf(systemRes.getRequestStatus().name()));
                response.setResponseObject(systemRes.getResponseData());
            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.FAILURE);
            }
        }
        catch (final AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);

            throw new SystemCheckException(acsx.getMessage(), acsx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.NETSTAT);
                auditEntry.setUserAccount(userAccount);
                auditEntry.setAuthorized(Boolean.TRUE);
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
            catch (final AuditServiceException asx)
            {
                ERROR_RECORDER.error(asx.getMessage(), asx);
            }
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.core.processors.interfaces.ISystemCheckProcessor#runTelnetCheck(com.cws.esolutions.core.processors.dto.SystemCheckRequest)
     */
    public SystemCheckResponse runTelnetCheck(final SystemCheckRequest request) throws SystemCheckException
    {
        final String methodName = ISystemCheckProcessor.CNAME + "#runTelnetCheck(final SystemCheckRequest request) throws SystemCheckException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("SystemCheckRequest: {}", request);
        }

        CoreServicesResponse agentResponse = null;
        SystemCheckResponse response = new SystemCheckResponse();

        final Server server = request.getSourceServer();
        final RequestHostInfo reqInfo = request.getRequestInfo();
        final UserAccount userAccount = request.getUserAccount();

        if (DEBUG)
        {
            DEBUGGER.debug("Server: {}", server);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount: {}", userAccount);
        }

        try
        {
            // this will require admin and service authorization
            AccessControlServiceRequest accessRequest = new AccessControlServiceRequest();
            accessRequest.setUserAccount(userAccount);

            if (DEBUG)
            {
                DEBUGGER.debug("AccessControlServiceRequest: {}", accessRequest);
            }

            AccessControlServiceResponse accessResponse = accessControl.isUserAuthorized(accessRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("AccessControlServiceResponse accessResponse: {}", accessResponse);
            }

            if (!(accessResponse.getIsUserAuthorized()))
            {
                // unauthorized
                response.setRequestStatus(CoreServicesStatus.UNAUTHORIZED);

                // audit
                try
                {
                    AuditEntry auditEntry = new AuditEntry();
                    auditEntry.setHostInfo(reqInfo);
                    auditEntry.setAuditType(AuditType.TELNET);
                    auditEntry.setUserAccount(userAccount);
                    auditEntry.setAuthorized(Boolean.FALSE);
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
                catch (final AuditServiceException asx)
                {
                    ERROR_RECORDER.error(asx.getMessage(), asx);
                }

                return response;
            }

            ServiceCheckRequest systemReq = new ServiceCheckRequest();
            systemReq.setRequestType(SystemCheckType.TELNET);
            systemReq.setPortNumber(request.getPortNumber());
            systemReq.setTargetHost(request.getTargetServer().getOperHostName());

            if (DEBUG)
            {
                DEBUGGER.debug("ServiceCheckRequest: {}", request);
            }

            CoreServicesRequest agentRequest = new CoreServicesRequest();
            agentRequest.setAppName(appConfig.getAppName());
            agentRequest.setRequestPayload(systemReq);

            if (DEBUG)
            {
                DEBUGGER.debug("CoreServicesRequest: {}", agentRequest);
            }

            String correlator = null;

            if (DEBUG)
            {
                DEBUGGER.debug("correlator: {}", correlator);
            }

            if (StringUtils.isNotEmpty(correlator))
            {

            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.FAILURE);

                return response;
            }

            if (DEBUG)
            {
                DEBUGGER.debug("CoreServicesResponse: {}", agentResponse);
            }

            if (agentResponse.getRequestStatus() == CoreServicesStatus.SUCCESS)
            {
                ServiceCheckResponse systemRes = (ServiceCheckResponse) agentResponse.getResponsePayload();

                if (DEBUG)
                {
                    DEBUGGER.debug("ServiceCheckResponse: {}", systemRes);
                }

                response.setRequestStatus(CoreServicesStatus.valueOf(systemRes.getRequestStatus().name()));
                response.setResponseObject(systemRes.getResponseData());
            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.FAILURE);
            }
        }
        catch (final AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);

            throw new SystemCheckException(acsx.getMessage(), acsx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.TELNET);
                auditEntry.setUserAccount(userAccount);
                auditEntry.setAuthorized(Boolean.TRUE);
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
            catch (final AuditServiceException asx)
            {
                ERROR_RECORDER.error(asx.getMessage(), asx);
            }
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.core.processors.interfaces.ISystemCheckProcessor#runRemoteDateCheck(com.cws.esolutions.core.processors.dto.SystemCheckRequest)
     */
    public SystemCheckResponse runRemoteDateCheck(final SystemCheckRequest request) throws SystemCheckException
    {
        final String methodName = ISystemCheckProcessor.CNAME + "#runRemoteDateCheck(final SystemCheckRequest request) throws SystemCheckException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("SystemCheckResponse: {}", request);
        }

        CoreServicesResponse agentResponse = null;
        SystemCheckResponse response = new SystemCheckResponse();

        final Server server = request.getSourceServer();
        final RequestHostInfo reqInfo = request.getRequestInfo();
        final UserAccount userAccount = request.getUserAccount();

        if (DEBUG)
        {
            DEBUGGER.debug("Server: {}", server);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount: {}", userAccount);
        }

        try
        {
            // this will require admin and service authorization
            AccessControlServiceRequest accessRequest = new AccessControlServiceRequest();
            accessRequest.setUserAccount(userAccount);

            if (DEBUG)
            {
                DEBUGGER.debug("AccessControlServiceRequest: {}", accessRequest);
            }

            AccessControlServiceResponse accessResponse = accessControl.isUserAuthorized(accessRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("AccessControlServiceResponse accessResponse: {}", accessResponse);
            }

            if (!(accessResponse.getIsUserAuthorized()))
            {
                // unauthorized
                response.setRequestStatus(CoreServicesStatus.UNAUTHORIZED);

                // audit
                try
                {
                    AuditEntry auditEntry = new AuditEntry();
                    auditEntry.setHostInfo(reqInfo);
                    auditEntry.setAuditType(AuditType.REMOTEDATE);
                    auditEntry.setAuthorized(Boolean.FALSE);
                    auditEntry.setUserAccount(userAccount);
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
                catch (final AuditServiceException asx)
                {
                    ERROR_RECORDER.error(asx.getMessage(), asx);
                }

                return response;
            }

            ServiceCheckRequest systemReq = new ServiceCheckRequest();
            systemReq.setRequestType(SystemCheckType.REMOTEDATE);
            systemReq.setPortNumber(request.getPortNumber());

            if (DEBUG)
            {
                DEBUGGER.debug("ServiceCheckRequest: {}", request);
            }

            CoreServicesRequest agentRequest = new CoreServicesRequest();
            agentRequest.setAppName(appConfig.getAppName());
            agentRequest.setRequestPayload(systemReq);

            if (DEBUG)
            {
                DEBUGGER.debug("CoreServicesRequest: {}", agentRequest);
            }

            String correlator = null;

            if (DEBUG)
            {
                DEBUGGER.debug("correlator: {}", correlator);
            }

            if (StringUtils.isNotEmpty(correlator))
            {

            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.FAILURE);

                return response;
            }

            if (DEBUG)
            {
                DEBUGGER.debug("CoreServicesResponse: {}", agentResponse);
            }

            if (agentResponse.getRequestStatus() == CoreServicesStatus.SUCCESS)
            {
                ServiceCheckResponse systemRes = (ServiceCheckResponse) agentResponse.getResponsePayload();

                if (DEBUG)
                {
                    DEBUGGER.debug("ServiceCheckResponse: {}", systemRes);
                }

                response.setRequestStatus(CoreServicesStatus.valueOf(systemRes.getRequestStatus().name()));
                response.setResponseObject(systemRes.getResponseData());
            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.FAILURE);
            }
        }
        catch (final AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);

            throw new SystemCheckException(acsx.getMessage(), acsx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.REMOTEDATE);
                auditEntry.setUserAccount(userAccount);
                auditEntry.setAuthorized(Boolean.TRUE);
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
            catch (final AuditServiceException asx)
            {
                ERROR_RECORDER.error(asx.getMessage(), asx);
            }
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.core.processors.interfaces.ISystemCheckProcessor#runProcessListCheck(com.cws.esolutions.core.processors.dto.SystemCheckRequest)
     */
    public SystemCheckResponse runProcessListCheck(final SystemCheckRequest request) throws SystemCheckException
    {
        final String methodName = ISystemCheckProcessor.CNAME + "#runProcessListCheck(final SystemCheckRequest request) throws SystemCheckException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("SystemCheckResponse: {}", request);
        }

        CoreServicesResponse agentResponse = null;
        SystemCheckResponse response = new SystemCheckResponse();

        final Server server = request.getSourceServer();
        final RequestHostInfo reqInfo = request.getRequestInfo();
        final UserAccount userAccount = request.getUserAccount();

        if (DEBUG)
        {
            DEBUGGER.debug("Server: {}", server);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount: {}", userAccount);
        }

        try
        {
            // this will require admin and service authorization
            AccessControlServiceRequest accessRequest = new AccessControlServiceRequest();
            accessRequest.setUserAccount(userAccount);

            if (DEBUG)
            {
                DEBUGGER.debug("AccessControlServiceRequest: {}", accessRequest);
            }

            AccessControlServiceResponse accessResponse = accessControl.isUserAuthorized(accessRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("AccessControlServiceResponse accessResponse: {}", accessResponse);
            }

            if (!(accessResponse.getIsUserAuthorized()))
            {
                // unauthorized
                response.setRequestStatus(CoreServicesStatus.UNAUTHORIZED);

                // audit
                try
                {
                    AuditEntry auditEntry = new AuditEntry();
                    auditEntry.setHostInfo(reqInfo);
                    auditEntry.setAuditType(AuditType.PROCESSLIST);
                    auditEntry.setUserAccount(userAccount);
                    auditEntry.setAuthorized(Boolean.FALSE);
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
                catch (final AuditServiceException asx)
                {
                    ERROR_RECORDER.error(asx.getMessage(), asx);
                }

                return response;
            }

            ServiceCheckRequest systemReq = new ServiceCheckRequest();
            systemReq.setRequestType(SystemCheckType.PROCESSLIST);

            if (DEBUG)
            {
                DEBUGGER.debug("ServiceCheckRequest: {}", request);
            }

            CoreServicesRequest agentRequest = new CoreServicesRequest();
            agentRequest.setAppName(appConfig.getAppName());
            agentRequest.setRequestPayload(systemReq);

            if (DEBUG)
            {
                DEBUGGER.debug("CoreServicesRequest: {}", agentRequest);
            }

            String correlator = null;

            if (DEBUG)
            {
                DEBUGGER.debug("correlator: {}", correlator);
            }

            if (StringUtils.isNotEmpty(correlator))
            {

            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.FAILURE);

                return response;
            }

            if (DEBUG)
            {
                DEBUGGER.debug("CoreServicesResponse: {}", agentResponse);
            }

            if (agentResponse.getRequestStatus() == CoreServicesStatus.SUCCESS)
            {
                ServiceCheckResponse systemRes = (ServiceCheckResponse) agentResponse.getResponsePayload();

                if (DEBUG)
                {
                    DEBUGGER.debug("ServiceCheckResponse: {}", systemRes);
                }

                response.setRequestStatus(CoreServicesStatus.valueOf(systemRes.getRequestStatus().name()));
                response.setResponseObject(systemRes.getResponseData());
            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.FAILURE);
            }
        }
        catch (final AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);

            throw new SystemCheckException(acsx.getMessage(), acsx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.PROCESSLIST);
                auditEntry.setUserAccount(userAccount);
                auditEntry.setAuthorized(Boolean.TRUE);
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
            catch (final AuditServiceException asx)
            {
                ERROR_RECORDER.error(asx.getMessage(), asx);
            }
        }

        return response;
    }
}
