/*
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
package com.cws.esolutions.core.processors.impl;
/**
 * @see com.cws.esolutions.core.processors.interfaces.ISystemCheckProcessor
 */
import org.apache.commons.lang.StringUtils;

import com.cws.esolutions.core.utils.MQUtils;
import com.cws.esolutions.agent.dto.AgentRequest;
import com.cws.esolutions.agent.dto.AgentResponse;
import com.cws.esolutions.agent.enums.AgentStatus;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.core.processors.dto.Server;
import com.cws.esolutions.security.audit.dto.AuditEntry;
import com.cws.esolutions.security.audit.enums.AuditType;
import com.cws.esolutions.security.audit.dto.AuditRequest;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.core.utils.exception.UtilityException;
import com.cws.esolutions.agent.processors.enums.SystemCheckType;
import com.cws.esolutions.core.processors.dto.SystemCheckRequest;
import com.cws.esolutions.core.processors.dto.SystemCheckResponse;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.agent.processors.dto.SystemManagerRequest;
import com.cws.esolutions.agent.processors.dto.SystemManagerResponse;
import com.cws.esolutions.agent.processors.enums.SystemManagementType;
import com.cws.esolutions.security.audit.exception.AuditServiceException;
import com.cws.esolutions.core.processors.exception.SystemCheckException;
import com.cws.esolutions.core.processors.interfaces.ISystemCheckProcessor;
import com.cws.esolutions.security.access.control.exception.UserControlServiceException;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.processors.impl
 * File: SystemCheckProcessorImpl.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
public class SystemCheckProcessorImpl implements ISystemCheckProcessor
{
    /**
     * @see com.cws.esolutions.core.processors.interfaces.ISystemCheckProcessor#runNetstatCheck(com.cws.esolutions.core.processors.dto.SystemCheckRequest)
     */
    @Override
    public SystemCheckResponse runNetstatCheck(final SystemCheckRequest request) throws SystemCheckException
    {
        final String methodName = ISystemCheckProcessor.CNAME + "#runNetstatCheck(final SystemCheckRequest request) throws SystemCheckException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("SystemCheckRequest: ", request);
        }

        AgentResponse agentResponse = null;
        SystemCheckResponse response = new SystemCheckResponse();

        final Server sourceServer = request.getSourceServer();
        final RequestHostInfo reqInfo = request.getRequestInfo();
        final UserAccount userAccount = request.getUserAccount();

        if (DEBUG)
        {
            DEBUGGER.debug("Server: {}", sourceServer);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount: {}", userAccount);
        }

        try
        {
            boolean isServiceAuthorized = userControl.isUserAuthorizedForService(userAccount, request.getServiceId());

            if (DEBUG)
            {
                DEBUGGER.debug("isServiceAuthorized: {}", isServiceAuthorized);
            }

            if (isServiceAuthorized)
            {
                SystemManagerRequest systemReq = new SystemManagerRequest();
                systemReq.setMgmtType(SystemManagementType.SYSTEMCHECK);
                systemReq.setRequestType(SystemCheckType.NETSTAT);
                systemReq.setPortNumber(request.getPortNumber());

                if (DEBUG)
                {
                    DEBUGGER.debug("SystemManagerRequest: {}", request);
                }

                AgentRequest agentRequest = new AgentRequest();
                agentRequest.setHostname(sourceServer.getOperHostName());
                agentRequest.setAppName(appConfig.getAppName());
                agentRequest.setRequestPayload(systemReq);

                if (DEBUG)
                {
                    DEBUGGER.debug("AgentRequest: {}", agentRequest);
                }

                switch (agentConfig.getListenerType())
                {
                    case MQ:
                        // always make the tcp conn to the oper hostname - thats where the agent should be listening
                        String correlator = MQUtils.sendMqMessage(agentConfig.getConnectionName(), agentConfig.getRequestQueue(),  agentRequest);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("correlator: {}", correlator);
                        }

                        if (StringUtils.isNotEmpty(correlator))
                        {
                            agentResponse = (AgentResponse) MQUtils.getMqMessage(agentConfig.getConnectionName(), agentConfig.getResponseQueue(),  correlator);
                        }
                        else
                        {
                            response.setRequestStatus(CoreServicesStatus.FAILURE);

                            return response;
                        }

                        break;
                    case TCP:
                        break;
                }

                if (DEBUG)
                {
                    DEBUGGER.debug("AgentResponse: {}", agentResponse);
                }

                if (agentResponse.getRequestStatus() == AgentStatus.SUCCESS)
                {
                    SystemManagerResponse systemRes = (SystemManagerResponse) agentResponse.getResponsePayload();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("SystemManagerResponse: {}", systemRes);
                    }

                    response.setRequestStatus(CoreServicesStatus.valueOf(systemRes.getRequestStatus().name()));
                    response.setResponse(systemRes.getResponse());
                    response.setResponseObject(systemRes.getResponseData());
                }
                else
                {
                    response.setRequestStatus(CoreServicesStatus.FAILURE);
                }
            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.UNAUTHORIZED);
            }
        }
        catch (UtilityException ux)
        {
            ERROR_RECORDER.error(ux.getMessage(), ux);

            throw new SystemCheckException(ux.getMessage(), ux);
        }
        catch (UserControlServiceException ucsx)
        {
            ERROR_RECORDER.error(ucsx.getMessage(), ucsx);

            throw new SystemCheckException(ucsx.getMessage(), ucsx);
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
     * @see com.cws.esolutions.core.processors.interfaces.ISystemCheckProcessor#runTelnetCheck(com.cws.esolutions.core.processors.dto.SystemCheckRequest)
     */
    @Override
    public SystemCheckResponse runTelnetCheck(final SystemCheckRequest request) throws SystemCheckException
    {
        final String methodName = ISystemCheckProcessor.CNAME + "#runTelnetCheck(final SystemCheckRequest request) throws SystemCheckException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ServerManagementRequest: ", request);
        }

        AgentResponse agentResponse = null;
        SystemCheckResponse response = new SystemCheckResponse();

        final Server sourceServer = request.getSourceServer();
        final RequestHostInfo reqInfo = request.getRequestInfo();
        final UserAccount userAccount = request.getUserAccount();

        if (DEBUG)
        {
            DEBUGGER.debug("Server: {}", sourceServer);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount: {}", userAccount);
        }

        try
        {
            boolean isServiceAuthorized = userControl.isUserAuthorizedForService(userAccount, request.getServiceId());

            if (DEBUG)
            {
                DEBUGGER.debug("isServiceAuthorized: {}", isServiceAuthorized);
            }

            if (isServiceAuthorized)
            {
                SystemManagerRequest systemReq = new SystemManagerRequest();
                systemReq.setMgmtType(SystemManagementType.SYSTEMCHECK);
                systemReq.setRequestType(SystemCheckType.TELNET);
                systemReq.setPortNumber(request.getPortNumber());
                systemReq.setTargetServer(request.getTargetServer().getOperHostName());

                if (DEBUG)
                {
                    DEBUGGER.debug("SystemManagerRequest: {}", request);
                }

                AgentRequest agentRequest = new AgentRequest();
                agentRequest.setHostname(sourceServer.getOperHostName());
                agentRequest.setAppName(appConfig.getAppName());
                agentRequest.setRequestPayload(systemReq);

                if (DEBUG)
                {
                    DEBUGGER.debug("AgentRequest: {}", agentRequest);
                }

                switch (agentConfig.getListenerType())
                {
                    case MQ:
                        // always make the tcp conn to the oper hostname - thats where the agent should be listening
                        String correlator = MQUtils.sendMqMessage(agentConfig.getConnectionName(), agentConfig.getRequestQueue(),  agentRequest);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("correlator: {}", correlator);
                        }

                        if (StringUtils.isNotEmpty(correlator))
                        {
                            agentResponse = (AgentResponse) MQUtils.getMqMessage(agentConfig.getConnectionName(), agentConfig.getResponseQueue(),  correlator);
                        }
                        else
                        {
                            response.setRequestStatus(CoreServicesStatus.FAILURE);

                            return response;
                        }

                        break;
                    case TCP:
                        break;
                }

                if (DEBUG)
                {
                    DEBUGGER.debug("AgentResponse: {}", agentResponse);
                }

                if (agentResponse.getRequestStatus() == AgentStatus.SUCCESS)
                {
                    SystemManagerResponse systemRes = (SystemManagerResponse) agentResponse.getResponsePayload();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("SystemManagerResponse: {}", systemRes);
                    }

                    response.setRequestStatus(CoreServicesStatus.valueOf(systemRes.getRequestStatus().name()));
                    response.setResponse(systemRes.getResponse());
                    response.setResponseObject(systemRes.getResponseData());
                }
                else
                {
                    response.setResponse(agentResponse.getResponse());
                    response.setRequestStatus(CoreServicesStatus.FAILURE);
                }
            }
            else
            {
				response.setRequestStatus(CoreServicesStatus.UNAUTHORIZED);
            }
        }
        catch (UtilityException ux)
        {
            ERROR_RECORDER.error(ux.getMessage(), ux);

            throw new SystemCheckException(ux.getMessage(), ux);
        }
        catch (UserControlServiceException ucsx)
        {
            ERROR_RECORDER.error(ucsx.getMessage(), ucsx);

            throw new SystemCheckException(ucsx.getMessage(), ucsx);
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
     * @see com.cws.esolutions.core.processors.interfaces.ISystemCheckProcessor#runRemoteDateCheck(com.cws.esolutions.core.processors.dto.SystemCheckRequest)
     */
    @Override
    public SystemCheckResponse runRemoteDateCheck(final SystemCheckRequest request) throws SystemCheckException
    {
        final String methodName = ISystemCheckProcessor.CNAME + "#runRemoteDateCheck(final SystemCheckRequest request) throws SystemCheckException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("SystemCheckResponse: ", request);
        }

        AgentResponse agentResponse = null;
        SystemCheckResponse response = new SystemCheckResponse();

        final Server sourceServer = request.getSourceServer();
        final RequestHostInfo reqInfo = request.getRequestInfo();
        final UserAccount userAccount = request.getUserAccount();

        if (DEBUG)
        {
            DEBUGGER.debug("Server: {}", sourceServer);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount: {}", userAccount);
        }

        try
        {
            boolean isServiceAuthorized = userControl.isUserAuthorizedForService(userAccount, request.getServiceId());

            if (DEBUG)
            {
                DEBUGGER.debug("isServiceAuthorized: {}", isServiceAuthorized);
            }

            if (isServiceAuthorized)
            {
                SystemManagerRequest systemReq = new SystemManagerRequest();
                systemReq.setMgmtType(SystemManagementType.SYSTEMCHECK);
                systemReq.setRequestType(SystemCheckType.REMOTEDATE);
                systemReq.setPortNumber(request.getPortNumber());
                systemReq.setTargetServer(request.getTargetServer().getOperHostName());

                if (DEBUG)
                {
                    DEBUGGER.debug("SystemManagerRequest: {}", request);
                }

                AgentRequest agentRequest = new AgentRequest();
                agentRequest.setAppName(appConfig.getAppName());
                agentRequest.setRequestPayload(systemReq);
                agentRequest.setHostname(sourceServer.getOperHostName());

                if (DEBUG)
                {
                    DEBUGGER.debug("AgentRequest: {}", agentRequest);
                }

                switch (agentConfig.getListenerType())
                {
                    case MQ:
                        // always make the tcp conn to the oper hostname - thats where the agent should be listening
                        String correlator = MQUtils.sendMqMessage(agentConfig.getConnectionName(), agentConfig.getRequestQueue(),  agentRequest);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("correlator: {}", correlator);
                        }

                        if (StringUtils.isNotEmpty(correlator))
                        {
                            agentResponse = (AgentResponse) MQUtils.getMqMessage(agentConfig.getConnectionName(), agentConfig.getResponseQueue(),  correlator);
                        }
                        else
                        {
                            response.setRequestStatus(CoreServicesStatus.FAILURE);

                            return response;
                        }

                        break;
                    case TCP:
                        break;
                }

                if (DEBUG)
                {
                    DEBUGGER.debug("AgentResponse: {}", agentResponse);
                }

                if (agentResponse.getRequestStatus() == AgentStatus.SUCCESS)
                {
                    SystemManagerResponse systemRes = (SystemManagerResponse) agentResponse.getResponsePayload();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("SystemManagerResponse: {}", systemRes);
                    }

                    response.setRequestStatus(CoreServicesStatus.valueOf(systemRes.getRequestStatus().name()));
                    response.setResponse(systemRes.getResponse());
                    response.setResponseObject(systemRes.getResponseData());
                }
                else
                {
                    response.setResponse(agentResponse.getResponse());
                    response.setRequestStatus(CoreServicesStatus.FAILURE);
                }
            }
            else
            {
				response.setRequestStatus(CoreServicesStatus.UNAUTHORIZED);
            }
        }
        catch (UtilityException ux)
        {
            ERROR_RECORDER.error(ux.getMessage(), ux);

            throw new SystemCheckException(ux.getMessage(), ux);
        }
        catch (UserControlServiceException ucsx)
        {
            ERROR_RECORDER.error(ucsx.getMessage(), ucsx);

            throw new SystemCheckException(ucsx.getMessage(), ucsx);
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
     * @see com.cws.esolutions.core.processors.interfaces.ISystemCheckProcessor#runProcessListCheck(com.cws.esolutions.core.processors.dto.SystemCheckRequest)
     */
    @Override
    public SystemCheckResponse runProcessListCheck(final SystemCheckRequest request) throws SystemCheckException
    {
        final String methodName = ISystemCheckProcessor.CNAME + "#runProcessListCheck(final SystemCheckRequest request) throws SystemCheckException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("SystemCheckResponse: ", request);
        }

        AgentResponse agentResponse = null;
        SystemCheckResponse response = new SystemCheckResponse();

        final Server sourceServer = request.getSourceServer();
        final RequestHostInfo reqInfo = request.getRequestInfo();
        final UserAccount userAccount = request.getUserAccount();

        if (DEBUG)
        {
            DEBUGGER.debug("Server: {}", sourceServer);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount: {}", userAccount);
        }

        try
        {
            boolean isServiceAuthorized = userControl.isUserAuthorizedForService(userAccount, request.getServiceId());

            if (DEBUG)
            {
                DEBUGGER.debug("isServiceAuthorized: {}", isServiceAuthorized);
            }

            if (isServiceAuthorized)
            {
                SystemManagerRequest systemReq = new SystemManagerRequest();
                systemReq.setMgmtType(SystemManagementType.SYSTEMCHECK);
                systemReq.setRequestType(SystemCheckType.PROCESSLIST);
                systemReq.setTargetServer(request.getTargetServer().getOperHostName());

                if (DEBUG)
                {
                    DEBUGGER.debug("SystemManagerRequest: {}", request);
                }

                AgentRequest agentRequest = new AgentRequest();
                agentRequest.setAppName(appConfig.getAppName());
                agentRequest.setRequestPayload(systemReq);
                agentRequest.setHostname(sourceServer.getOperHostName());

                if (DEBUG)
                {
                    DEBUGGER.debug("AgentRequest: {}", agentRequest);
                }

                switch (agentConfig.getListenerType())
                {
                    case MQ:
                        // always make the tcp conn to the oper hostname - thats where the agent should be listening
                        String correlator = MQUtils.sendMqMessage(agentConfig.getConnectionName(), agentConfig.getRequestQueue(),  agentRequest);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("correlator: {}", correlator);
                        }

                        if (StringUtils.isNotEmpty(correlator))
                        {
                            agentResponse = (AgentResponse) MQUtils.getMqMessage(agentConfig.getConnectionName(), agentConfig.getResponseQueue(),  correlator);
                        }
                        else
                        {
                            response.setRequestStatus(CoreServicesStatus.FAILURE);

                            return response;
                        }

                        break;
                    case TCP:
                        break;
                }

                if (DEBUG)
                {
                    DEBUGGER.debug("AgentResponse: {}", agentResponse);
                }

                if (agentResponse.getRequestStatus() == AgentStatus.SUCCESS)
                {
                    SystemManagerResponse systemRes = (SystemManagerResponse) agentResponse.getResponsePayload();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("SystemManagerResponse: {}", systemRes);
                    }

                    response.setRequestStatus(CoreServicesStatus.valueOf(systemRes.getRequestStatus().name()));
                    response.setResponse(systemRes.getResponse());
                    response.setResponseObject(systemRes.getResponseData());
                }
                else
                {
                    response.setResponse(agentResponse.getResponse());
                    response.setRequestStatus(CoreServicesStatus.FAILURE);
                }
            }
            else
            {
				response.setRequestStatus(CoreServicesStatus.UNAUTHORIZED);
            }
        }
        catch (UtilityException ux)
        {
            ERROR_RECORDER.error(ux.getMessage(), ux);

            throw new SystemCheckException(ux.getMessage(), ux);
        }
        catch (UserControlServiceException ucsx)
        {
            ERROR_RECORDER.error(ucsx.getMessage(), ucsx);

            throw new SystemCheckException(ucsx.getMessage(), ucsx);
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
