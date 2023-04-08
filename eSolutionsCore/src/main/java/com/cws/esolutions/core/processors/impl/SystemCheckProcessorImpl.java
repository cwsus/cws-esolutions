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
import java.util.Arrays;
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
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.dto.Server;
import com.cws.esolutions.core.dto.CoreServicesRequest;
import com.cws.esolutions.core.dto.CoreServicesResponse;
import com.cws.esolutions.core.enums.CoreServicesStatus;
import com.cws.esolutions.core.processors.enums.SystemCheckType;
import com.cws.esolutions.core.processors.dto.SystemCheckRequest;
import com.cws.esolutions.core.processors.dto.SystemCheckResponse;
import com.cws.esolutions.core.processors.exception.SystemCheckException;
import com.cws.esolutions.utility.securityutils.processors.dto.AuditEntry;
import com.cws.esolutions.core.processors.interfaces.ISystemCheckProcessor;
import com.cws.esolutions.utility.services.dto.AccessControlServiceRequest;
import com.cws.esolutions.utility.securityutils.processors.enums.AuditType;
import com.cws.esolutions.utility.securityutils.processors.exception.AuditServiceException;
import com.cws.esolutions.utility.services.dto.AccessControlServiceResponse;
import com.cws.esolutions.utility.services.exception.AccessControlServiceException;
import com.cws.esolutions.utility.securityutils.processors.dto.AuditRequest;
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

        final Server hostServer = request.getSourceServer();
        final RequestHostInfo reqInfo = request.getRequestInfo();
        final UserAccount userAccount = request.getUserAccount();

        if (DEBUG)
        {
            DEBUGGER.debug("Server: {}", hostServer);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount: {}", userAccount);
        }

        try
        {
            // this will require admin and service authorization
            AccessControlServiceRequest accessRequest = new AccessControlServiceRequest();
            accessRequest.setUserAccount(new ArrayList<String>(Arrays.asList(userAccount.getGuid(), userAccount.getUserRole().toString())));

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
                if (secConfig.getPerformAudit())
                {
                    // audit if a valid account. if not valid we cant audit much,
                    // but we should try anyway. not sure how thats going to work
                    try
                    {
                        AuditEntry auditEntry = new AuditEntry();
                        auditEntry.setAuditType(AuditType.NETSTAT);
                        auditEntry.setAuditDate(new Date(System.currentTimeMillis()));
                        auditEntry.setSessionId(userAccount.getSessionId());
                        auditEntry.setUserGuid(userAccount.getGuid());
                        auditEntry.setUserName(userAccount.getUsername());
                        auditEntry.setUserRole(userAccount.getUserRole().toString());
                        auditEntry.setAuthorized(Boolean.FALSE);
                        auditEntry.setApplicationId(request.getApplicationId());
                        auditEntry.setApplicationName(request.getApplicationName());
        
                        if (DEBUG)
                        {
                            DEBUGGER.debug("AuditEntry: {}", auditEntry);
                        }
        
                        List<String> auditHostInfo = new ArrayList<String>(
                        		Arrays.asList(
                        				reqInfo.getHostAddress(),
                        				reqInfo.getHostName()));

                        if (DEBUG)
                        {
                        	DEBUGGER.debug("List<String>: {}", auditHostInfo);
                        }

                        AuditRequest auditRequest = new AuditRequest();
                        auditRequest.setAuditEntry(auditEntry);
                        auditRequest.setHostInfo(auditHostInfo);
        
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

            SystemCheckRequest systemReq = new SystemCheckRequest();
            systemReq.setCheckType(SystemCheckType.NETSTAT);
            systemReq.setPortNumber(request.getPortNumber());
            systemReq.setSourceServer(hostServer);

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
                SystemCheckResponse systemRes = (SystemCheckResponse) agentResponse.getResponsePayload();

                if (DEBUG)
                {
                    DEBUGGER.debug("ServiceCheckResponse: {}", systemRes);
                }

                response.setRequestStatus(CoreServicesStatus.valueOf(systemRes.getRequestStatus().name()));
                response.setResponseObject(systemRes.getResponseObject());
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
            if (secConfig.getPerformAudit())
            {
                // audit if a valid account. if not valid we cant audit much,
                // but we should try anyway. not sure how thats going to work
                try
                {
                    AuditEntry auditEntry = new AuditEntry();
                    auditEntry.setAuditType(AuditType.NETSTAT);
                    auditEntry.setAuditDate(new Date(System.currentTimeMillis()));
                    auditEntry.setSessionId(userAccount.getSessionId());
                    auditEntry.setUserGuid(userAccount.getGuid());
                    auditEntry.setUserName(userAccount.getUsername());
                    auditEntry.setUserRole(userAccount.getUserRole().toString());
                    auditEntry.setAuthorized(Boolean.TRUE);
                    auditEntry.setApplicationId(request.getApplicationId());
                    auditEntry.setApplicationName(request.getApplicationName());
    
                    if (DEBUG)
                    {
                        DEBUGGER.debug("AuditEntry: {}", auditEntry);
                    }
    
                    List<String> auditHostInfo = new ArrayList<String>(
                    		Arrays.asList(
                    				reqInfo.getHostAddress(),
                    				reqInfo.getHostName()));

                    if (DEBUG)
                    {
                    	DEBUGGER.debug("List<String>: {}", auditHostInfo);
                    }

                    AuditRequest auditRequest = new AuditRequest();
                    auditRequest.setAuditEntry(auditEntry);
                    auditRequest.setHostInfo(auditHostInfo);
    
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
            accessRequest.setUserAccount(new ArrayList<String>(Arrays.asList(userAccount.getGuid(), userAccount.getUserRole().toString())));

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
                if (secConfig.getPerformAudit())
                {
                    // audit if a valid account. if not valid we cant audit much,
                    // but we should try anyway. not sure how thats going to work
                    try
                    {
                        AuditEntry auditEntry = new AuditEntry();
                        auditEntry.setAuditType(AuditType.TELNET);
                        auditEntry.setAuditDate(new Date(System.currentTimeMillis()));
                        auditEntry.setSessionId(userAccount.getSessionId());
                        auditEntry.setUserGuid(userAccount.getGuid());
                        auditEntry.setUserName(userAccount.getUsername());
                        auditEntry.setUserRole(userAccount.getUserRole().toString());
                        auditEntry.setAuthorized(Boolean.FALSE);
                        auditEntry.setApplicationId(request.getApplicationId());
                        auditEntry.setApplicationName(request.getApplicationName());
        
                        if (DEBUG)
                        {
                            DEBUGGER.debug("AuditEntry: {}", auditEntry);
                        }
        
                        List<String> auditHostInfo = new ArrayList<String>(
                        		Arrays.asList(
                        				reqInfo.getHostAddress(),
                        				reqInfo.getHostName()));

                        if (DEBUG)
                        {
                        	DEBUGGER.debug("List<String>: {}", auditHostInfo);
                        }

                        AuditRequest auditRequest = new AuditRequest();
                        auditRequest.setAuditEntry(auditEntry);
                        auditRequest.setHostInfo(auditHostInfo);
        
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

            SystemCheckRequest systemReq = new SystemCheckRequest();
            systemReq.setCheckType(SystemCheckType.TELNET);
            systemReq.setPortNumber(request.getPortNumber());
            systemReq.setSourceServer(request.getSourceServer());
            systemReq.setTargetServer(request.getTargetServer());

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
                SystemCheckResponse systemRes = (SystemCheckResponse) agentResponse.getResponsePayload();

                if (DEBUG)
                {
                    DEBUGGER.debug("ServiceCheckResponse: {}", systemRes);
                }

                response.setRequestStatus(CoreServicesStatus.valueOf(systemRes.getRequestStatus().name()));
                response.setResponseObject(systemRes.getResponseObject());
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
            if (secConfig.getPerformAudit())
            {
                // audit if a valid account. if not valid we cant audit much,
                // but we should try anyway. not sure how thats going to work
                try
                {
                    AuditEntry auditEntry = new AuditEntry();
                    auditEntry.setAuditType(AuditType.TELNET);
                    auditEntry.setAuditDate(new Date(System.currentTimeMillis()));
                    auditEntry.setSessionId(userAccount.getSessionId());
                    auditEntry.setUserGuid(userAccount.getGuid());
                    auditEntry.setUserName(userAccount.getUsername());
                    auditEntry.setUserRole(userAccount.getUserRole().toString());
                    auditEntry.setAuthorized(Boolean.TRUE);
                    auditEntry.setApplicationId(request.getApplicationId());
                    auditEntry.setApplicationName(request.getApplicationName());
    
                    if (DEBUG)
                    {
                        DEBUGGER.debug("AuditEntry: {}", auditEntry);
                    }
    
                    List<String> auditHostInfo = new ArrayList<String>(
                    		Arrays.asList(
                    				reqInfo.getHostAddress(),
                    				reqInfo.getHostName()));

                    if (DEBUG)
                    {
                    	DEBUGGER.debug("List<String>: {}", auditHostInfo);
                    }

                    AuditRequest auditRequest = new AuditRequest();
                    auditRequest.setAuditEntry(auditEntry);
                    auditRequest.setHostInfo(auditHostInfo);
    
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
            accessRequest.setUserAccount(new ArrayList<String>(Arrays.asList(userAccount.getGuid(), userAccount.getUserRole().toString())));

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
                if (secConfig.getPerformAudit())
                {
                    // audit if a valid account. if not valid we cant audit much,
                    // but we should try anyway. not sure how thats going to work
                    try
                    {
                        AuditEntry auditEntry = new AuditEntry();
                        auditEntry.setAuditType(AuditType.REMOTEDATE);
                        auditEntry.setAuditDate(new Date(System.currentTimeMillis()));
                        auditEntry.setSessionId(userAccount.getSessionId());
                        auditEntry.setUserGuid(userAccount.getGuid());
                        auditEntry.setUserName(userAccount.getUsername());
                        auditEntry.setUserRole(userAccount.getUserRole().toString());
                        auditEntry.setAuthorized(Boolean.FALSE);
                        auditEntry.setApplicationId(request.getApplicationId());
                        auditEntry.setApplicationName(request.getApplicationName());
        
                        if (DEBUG)
                        {
                            DEBUGGER.debug("AuditEntry: {}", auditEntry);
                        }
        
                        List<String> auditHostInfo = new ArrayList<String>(
                        		Arrays.asList(
                        				reqInfo.getHostAddress(),
                        				reqInfo.getHostName()));

                        if (DEBUG)
                        {
                        	DEBUGGER.debug("List<String>: {}", auditHostInfo);
                        }

                        AuditRequest auditRequest = new AuditRequest();
                        auditRequest.setAuditEntry(auditEntry);
                        auditRequest.setHostInfo(auditHostInfo);
        
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

            SystemCheckRequest systemReq = new SystemCheckRequest();
            systemReq.setCheckType(SystemCheckType.REMOTEDATE);
            systemReq.setTargetServer(request.getTargetServer());

            if (DEBUG)
            {
                DEBUGGER.debug("SystemCheckRequest: {}", request);
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
            	SystemCheckResponse systemRes = (SystemCheckResponse) agentResponse.getResponsePayload();

                if (DEBUG)
                {
                    DEBUGGER.debug("SystemCheckResponse: {}", systemRes);
                }

                response.setRequestStatus(CoreServicesStatus.valueOf(systemRes.getRequestStatus().name()));
                response.setResponseObject(systemRes.getResponseObject());
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
            if (secConfig.getPerformAudit())
            {
                // audit if a valid account. if not valid we cant audit much,
                // but we should try anyway. not sure how thats going to work
                try
                {
                    AuditEntry auditEntry = new AuditEntry();
                    auditEntry.setAuditType(AuditType.REMOTEDATE);
                    auditEntry.setAuditDate(new Date(System.currentTimeMillis()));
                    auditEntry.setSessionId(userAccount.getSessionId());
                    auditEntry.setUserGuid(userAccount.getGuid());
                    auditEntry.setUserName(userAccount.getUsername());
                    auditEntry.setUserRole(userAccount.getUserRole().toString());
                    auditEntry.setAuthorized(Boolean.TRUE);
                    auditEntry.setApplicationId(request.getApplicationId());
                    auditEntry.setApplicationName(request.getApplicationName());
    
                    if (DEBUG)
                    {
                        DEBUGGER.debug("AuditEntry: {}", auditEntry);
                    }
    
                    List<String> auditHostInfo = new ArrayList<String>(
                    		Arrays.asList(
                    				reqInfo.getHostAddress(),
                    				reqInfo.getHostName()));

                    if (DEBUG)
                    {
                    	DEBUGGER.debug("List<String>: {}", auditHostInfo);
                    }

                    AuditRequest auditRequest = new AuditRequest();
                    auditRequest.setAuditEntry(auditEntry);
                    auditRequest.setHostInfo(auditHostInfo);
    
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
            accessRequest.setUserAccount(new ArrayList<String>(Arrays.asList(userAccount.getGuid(), userAccount.getUserRole().toString())));

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
                if (secConfig.getPerformAudit())
                {
                    // audit if a valid account. if not valid we cant audit much,
                    // but we should try anyway. not sure how thats going to work
                    try
                    {
                        AuditEntry auditEntry = new AuditEntry();
                        auditEntry.setAuditType(AuditType.PROCESSLIST);
                        auditEntry.setAuditDate(new Date(System.currentTimeMillis()));
                        auditEntry.setSessionId(userAccount.getSessionId());
                        auditEntry.setUserGuid(userAccount.getGuid());
                        auditEntry.setUserName(userAccount.getUsername());
                        auditEntry.setUserRole(userAccount.getUserRole().toString());
                        auditEntry.setAuthorized(Boolean.FALSE);
                        auditEntry.setApplicationId(request.getApplicationId());
                        auditEntry.setApplicationName(request.getApplicationName());
        
                        if (DEBUG)
                        {
                            DEBUGGER.debug("AuditEntry: {}", auditEntry);
                        }
        
                        List<String> auditHostInfo = new ArrayList<String>(
                        		Arrays.asList(
                        				reqInfo.getHostAddress(),
                        				reqInfo.getHostName()));

                        if (DEBUG)
                        {
                        	DEBUGGER.debug("List<String>: {}", auditHostInfo);
                        }

                        AuditRequest auditRequest = new AuditRequest();
                        auditRequest.setAuditEntry(auditEntry);
                        auditRequest.setHostInfo(auditHostInfo);
        
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

            SystemCheckRequest systemReq = new SystemCheckRequest();
            systemReq.setCheckType(SystemCheckType.PROCESSLIST);
            systemReq.setTargetServer(request.getTargetServer());

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
                SystemCheckResponse systemRes = (SystemCheckResponse) agentResponse.getResponsePayload();

                if (DEBUG)
                {
                    DEBUGGER.debug("ServiceCheckResponse: {}", systemRes);
                }

                response.setRequestStatus(CoreServicesStatus.valueOf(systemRes.getRequestStatus().name()));
                response.setResponseObject(systemRes.getResponseObject());
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
            if (secConfig.getPerformAudit())
            {
                // audit if a valid account. if not valid we cant audit much,
                // but we should try anyway. not sure how thats going to work
                try
                {
                    AuditEntry auditEntry = new AuditEntry();
                    auditEntry.setAuditType(AuditType.PROCESSLIST);
                    auditEntry.setAuditDate(new Date(System.currentTimeMillis()));
                    auditEntry.setSessionId(userAccount.getSessionId());
                    auditEntry.setUserGuid(userAccount.getGuid());
                    auditEntry.setUserName(userAccount.getUsername());
                    auditEntry.setUserRole(userAccount.getUserRole().toString());
                    auditEntry.setAuthorized(Boolean.TRUE);
                    auditEntry.setApplicationId(request.getApplicationId());
                    auditEntry.setApplicationName(request.getApplicationName());
    
                    if (DEBUG)
                    {
                        DEBUGGER.debug("AuditEntry: {}", auditEntry);
                    }
    
                    List<String> auditHostInfo = new ArrayList<String>(
                    		Arrays.asList(
                    				reqInfo.getHostAddress(),
                    				reqInfo.getHostName()));

                    if (DEBUG)
                    {
                    	DEBUGGER.debug("List<String>: {}", auditHostInfo);
                    }

                    AuditRequest auditRequest = new AuditRequest();
                    auditRequest.setAuditEntry(auditEntry);
                    auditRequest.setHostInfo(auditHostInfo);
    
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
        }

        return response;
    }
}
