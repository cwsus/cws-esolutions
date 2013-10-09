/**
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

import java.util.UUID;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.sql.SQLException;
import org.apache.commons.lang.StringUtils;

import com.cws.esolutions.agent.Constants;
import com.cws.esolutions.core.utils.MQUtils;
import com.cws.esolutions.agent.dto.AgentRequest;
import com.cws.esolutions.agent.dto.AgentResponse;
import com.cws.esolutions.agent.enums.AgentStatus;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.core.processors.dto.Server;
import com.cws.esolutions.security.audit.dto.AuditEntry;
import com.cws.esolutions.security.audit.enums.AuditType;
import com.cws.esolutions.security.audit.dto.AuditRequest;
import com.cws.esolutions.core.processors.enums.ServerType;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.enums.ServerStatus;
import com.cws.esolutions.core.processors.enums.ServiceRegion;
import com.cws.esolutions.core.utils.exception.UtilityException;
import com.cws.esolutions.agent.processors.enums.SystemCheckType;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.agent.processors.dto.SystemManagerRequest;
import com.cws.esolutions.agent.processors.dto.SystemManagerResponse;
import com.cws.esolutions.agent.processors.enums.SystemManagementType;
import com.cws.esolutions.core.processors.dto.ServerManagementRequest;
import com.cws.esolutions.core.processors.dto.ServerManagementResponse;
import com.cws.esolutions.security.audit.exception.AuditServiceException;
import com.cws.esolutions.core.processors.exception.ServerManagementException;
import com.cws.esolutions.core.processors.interfaces.IServerManagementProcessor;
import com.cws.esolutions.security.access.control.exception.UserControlServiceException;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.processors.impl
 * ServerManagementProcessorImpl.java
 *
 * $Id: $
 * $Author: $
 * $Date: $
 * $Revision: $
 * @author kmhuntly@gmail.com
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Oct 29, 2012 9:44:46 AM
 *     Created.
 */
public class ServerManagementProcessorImpl implements IServerManagementProcessor
{
    @Override
    public ServerManagementResponse addNewServer(final ServerManagementRequest request) throws ServerManagementException
    {
        final String methodName = IServerManagementProcessor.CNAME + "#addNewServer(final ServerManagementRequest request) throws ServerManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ServerManagementRequest: {}", request);
        }

        ServerManagementResponse response = new ServerManagementResponse();

        final Server requestServer = request.getTargetServer();
        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("Server: {}", requestServer);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            boolean isServiceAuthorized = userControl.isUserAuthorizedForService(userAccount.getGuid(), request.getServiceId());

            if (DEBUG)
            {
                DEBUGGER.debug("isServiceAuthorized: {}", isServiceAuthorized);
            }

            if (isServiceAuthorized)
            {
                if (requestServer == null)
                {
                    throw new ServerManagementException("No server was provided. Cannot continue.");
                }

                // make sure all the platform data is there
                List<String[]> validator = null;

                try
                {
                    validator = serverDAO.getServersByAttribute(requestServer.getOperHostName());
                }
                catch (SQLException sqx)
                {
                    // don't do anything with it
                }

                if (DEBUG)
                {
                    DEBUGGER.debug("Validator: {}", validator);
                }

                if ((validator == null) || (validator.size() == 0))
                {
                    // valid server
                    if ((requestServer.getServerType() == ServerType.VIRTUALHOST) || (requestServer.getServerType() == ServerType.DMGRSERVER))
                    {
                        if (StringUtils.isEmpty(requestServer.getMgrUrl()))
                        {
                            throw new ServerManagementException("Server type provided was " + requestServer.getServerType() + " but additional information is required.");
                        }
                        else if ((requestServer.getServerType() == ServerType.DMGRSERVER) && (requestServer.getDmgrPort() == 0))
                        {
                            throw new ServerManagementException("Server type provided was " + requestServer.getServerType() + " but additional information is required.");
                        }
                    }

                    @SuppressWarnings("unchecked")
                    List<Object> insertData = new ArrayList<Object>(
                            Arrays.asList(
                                    (StringUtils.isNotEmpty(requestServer.getServerGuid())) ? requestServer.getServerGuid() : UUID.randomUUID().toString(),
                                    requestServer.getOsName(),
                                    requestServer.getServerStatus().name(),
                                    requestServer.getServerRegion().name(),
                                    requestServer.getServerType().name(),
                                    requestServer.getDomainName(),
                                    requestServer.getCpuType(),
                                    requestServer.getCpuCount(),
                                    requestServer.getServerModel(),
                                    requestServer.getSerialNumber(),
                                    requestServer.getInstalledMemory(),
                                    requestServer.getOperIpAddress(),
                                    requestServer.getOperHostName(),
                                    (StringUtils.isNotEmpty(requestServer.getMgmtIpAddress())) ? requestServer.getMgmtIpAddress() : Constants.NOT_SET,
                                    (StringUtils.isNotEmpty(requestServer.getMgmtHostName())) ? requestServer.getMgmtHostName() : Constants.NOT_SET,
                                    (StringUtils.isNotEmpty(requestServer.getBkIpAddress())) ? requestServer.getBkIpAddress() : Constants.NOT_SET,
                                    (StringUtils.isNotEmpty(requestServer.getBkHostName())) ? requestServer.getBkHostName() : Constants.NOT_SET,
                                    (StringUtils.isNotEmpty(requestServer.getNasIpAddress())) ? requestServer.getNasIpAddress() : Constants.NOT_SET,
                                    (StringUtils.isNotEmpty(requestServer.getNasHostName())) ? requestServer.getNasHostName() : Constants.NOT_SET,
                                    (StringUtils.isNotEmpty(requestServer.getNatAddress())) ? requestServer.getNatAddress() : Constants.NOT_SET,
                                    requestServer.getServerComments(),
                                    requestServer.getAssignedEngineer(),
                                    (StringUtils.isNotBlank(requestServer.getMgrUrl())) ? requestServer.getMgrUrl() : Constants.NOT_SET,
                                    requestServer.getDmgrPort(),
                                    (StringUtils.isNotBlank(requestServer.getServerRack())) ? requestServer.getServerRack() : Constants.NOT_SET,
                                    (StringUtils.isNotBlank(requestServer.getRackPosition())) ? requestServer.getRackPosition() : Constants.NOT_SET,
                                    (StringUtils.isNotBlank(requestServer.getOwningDmgr())) ? requestServer.getOwningDmgr() : Constants.NOT_SET));

                    if (DEBUG)
                    {
                        for (Object str : insertData)
                        {
                            DEBUGGER.debug("Value: {}", str);
                        }
                    }

                    boolean isComplete = serverDAO.addNewServer(insertData);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("isComplete: {}", isComplete);
                    }

                    if (isComplete)
                    {
                        response.setRequestStatus(CoreServicesStatus.SUCCESS);
                        response.setResponse("Successfully added " + requestServer.getOperHostName() + " to the asset datasource");
                    }
                    else
                    {
                        response.setRequestStatus(CoreServicesStatus.FAILURE);
                        response.setResponse("Failed to add " + requestServer.getOperHostName() + " to the asset datasource");
                    }
                }
                else
                {
                    response.setRequestStatus(CoreServicesStatus.FAILURE);
                    response.setResponse("Server " + requestServer.getOperHostName() + " already exists in the asset datasource.");
                }
            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.FAILURE);
                response.setResponse("The requested user was not authorized to perform the operation");
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new ServerManagementException(sqx.getMessage(), sqx);
        }
        catch (UserControlServiceException ucsx)
        {
            ERROR_RECORDER.error(ucsx.getMessage(), ucsx);

            throw new ServerManagementException(ucsx.getMessage(), ucsx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setReqInfo(reqInfo);
                auditEntry.setUserAccount(userAccount);
                auditEntry.setAuditType(AuditType.ADDSERVER);
                auditEntry.setAuditDate(System.currentTimeMillis());

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

    @Override
    public ServerManagementResponse updateServerData(final ServerManagementRequest request) throws ServerManagementException
    {
        final String methodName = IServerManagementProcessor.CNAME + "#updateServerData(final ServerManagementRequest request) throws ServerManagementException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ServerManagementRequest: {}", request);
        }

        ServerManagementResponse response = new ServerManagementResponse();

        final Server requestServer = request.getTargetServer();
        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("Server: {}", requestServer);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            boolean isServiceAuthorized = userControl.isUserAuthorizedForService(userAccount.getGuid(), request.getServiceId());

            if (DEBUG)
            {
                DEBUGGER.debug("isServiceAuthorized: {}", isServiceAuthorized);
            }

            if (isServiceAuthorized)
            {
                if ((requestServer.getServerType() == ServerType.VIRTUALHOST) || (requestServer.getServerType() == ServerType.DMGRSERVER))
                {
                    if (StringUtils.isEmpty(requestServer.getMgrUrl()))
                    {
                        throw new ServerManagementException("Server type provided was " + requestServer.getServerType() + " but additional information is required.");
                    }
                    else if ((requestServer.getServerType() == ServerType.DMGRSERVER) && (requestServer.getDmgrPort() == 0))
                    {
                        throw new ServerManagementException("Server type provided was " + requestServer.getServerType() + " but additional information is required.");
                    }
                }

                List<String> insertData = new ArrayList<String>(
                        Arrays.asList(
                                (StringUtils.isNotEmpty(requestServer.getServerGuid())) ? requestServer.getServerGuid() : UUID.randomUUID().toString(),
                                requestServer.getOsName(),
                                requestServer.getServerStatus().name(),
                                requestServer.getServerRegion().name(),
                                requestServer.getServerType().name(),
                                requestServer.getDomainName(),
                                requestServer.getCpuType(),
                                String.valueOf(requestServer.getCpuCount()),
                                requestServer.getServerModel(),
                                requestServer.getSerialNumber(),
                                String.valueOf(requestServer.getInstalledMemory()),
                                requestServer.getOperIpAddress(),
                                requestServer.getOperHostName(),
                                (StringUtils.isNotEmpty(requestServer.getMgmtIpAddress())) ? requestServer.getMgmtIpAddress() : Constants.NOT_SET,
                                (StringUtils.isNotEmpty(requestServer.getMgmtHostName())) ? requestServer.getMgmtHostName() : Constants.NOT_SET,
                                (StringUtils.isNotEmpty(requestServer.getBkIpAddress())) ? requestServer.getBkIpAddress() : Constants.NOT_SET,
                                (StringUtils.isNotEmpty(requestServer.getBkHostName())) ? requestServer.getBkHostName() : Constants.NOT_SET,
                                (StringUtils.isNotEmpty(requestServer.getNasIpAddress())) ? requestServer.getNasIpAddress() : Constants.NOT_SET,
                                (StringUtils.isNotEmpty(requestServer.getNasHostName())) ? requestServer.getNasHostName() : Constants.NOT_SET,
                                (StringUtils.isNotEmpty(requestServer.getNatAddress())) ? requestServer.getNatAddress() : Constants.NOT_SET,
                                requestServer.getServerComments(),
                                requestServer.getAssignedEngineer(),
                                (StringUtils.isNotBlank(requestServer.getMgrUrl())) ? requestServer.getMgrUrl() : Constants.NOT_SET,
                                String.valueOf(requestServer.getDmgrPort()),
                                (StringUtils.isNotBlank(requestServer.getServerRack())) ? requestServer.getServerRack() : Constants.NOT_SET,
                                (StringUtils.isNotBlank(requestServer.getRackPosition())) ? requestServer.getRackPosition() : Constants.NOT_SET,
                                (StringUtils.isNotBlank(requestServer.getOwningDmgr())) ? requestServer.getOwningDmgr() : Constants.NOT_SET));

                if (DEBUG)
                {
                    for (Object str : insertData)
                    {
                        DEBUGGER.debug("Value: {}", str);
                    }
                }

                boolean isComplete = serverDAO.modifyServerData(insertData);

                if (DEBUG)
                {
                    DEBUGGER.debug("isComplete: {}", isComplete);
                }

                if (isComplete)
                {
                    response.setRequestStatus(CoreServicesStatus.SUCCESS);
                    response.setResponse("Successfully added " + requestServer.getOperHostName() + " to the asset datasource");
                }
                else
                {
                    response.setRequestStatus(CoreServicesStatus.FAILURE);
                    response.setResponse("Failed to add " + requestServer.getOperHostName() + " to the asset datasource");
                }
            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.FAILURE);
                response.setResponse("The requested user was not authorized to perform the operation");
            }
        }
        catch (UserControlServiceException ucsx)
        {
            ERROR_RECORDER.error(ucsx.getMessage(), ucsx);

            throw new ServerManagementException(ucsx.getMessage(), ucsx);
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new ServerManagementException(sqx.getMessage(), sqx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setReqInfo(reqInfo);
                auditEntry.setUserAccount(userAccount);
                auditEntry.setAuditType(AuditType.UPDATESERVER);
                auditEntry.setAuditDate(System.currentTimeMillis());

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

    @Override
    public ServerManagementResponse listServers(final ServerManagementRequest request) throws ServerManagementException
    {
        final String methodName = IServerManagementProcessor.CNAME + "#listServers(final ServerManagementRequest request) throws ServerManagementException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ServerManagementRequest: {}", request);
        }

        ServerManagementResponse response = new ServerManagementResponse();

        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            boolean isServiceAuthorized = userControl.isUserAuthorizedForService(userAccount.getGuid(), request.getServiceId());

            if (DEBUG)
            {
                DEBUGGER.debug("isServiceAuthorized: {}", isServiceAuthorized);
            }

            if (isServiceAuthorized)
            {
                List<String[]> serverData = serverDAO.getInstalledServers();

                if (DEBUG)
                {
                    DEBUGGER.debug("serverList: {}", serverData);
                }

                if ((serverData != null) && (serverData.size() != 0))
                {
                    List<Server> serverList = new ArrayList<Server>();

                    for (String[] data : serverData)
                    {
                        if (DEBUG)
                        {
                            for (String str : data)
                            {
                                DEBUGGER.debug("Data: {}", str);
                            }
                        }

                        Server server = new Server();
                        server.setServerGuid(data[0]);
                        server.setOperHostName(data[1]);
                        server.setServerType(ServerType.valueOf(data[2]));

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Server: {}", server);
                        }

                        serverList.add(server);
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("serverList: {}", serverList);
                    }

                    response.setRequestStatus(CoreServicesStatus.SUCCESS);
                    response.setResponse("Successfully loaded installed server information.");
                    response.setServerList(serverList);
                }
            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.FAILURE);
                response.setResponse("The requested user was not authorized to perform the operation");
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new ServerManagementException(sqx.getMessage(), sqx);
        }
        catch (UserControlServiceException ucsx)
        {
            ERROR_RECORDER.error(ucsx.getMessage(), ucsx);
            
            throw new ServerManagementException(ucsx.getMessage(), ucsx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setReqInfo(reqInfo);
                auditEntry.setUserAccount(userAccount);
                auditEntry.setAuditType(AuditType.ADDSERVER);
                auditEntry.setAuditDate(System.currentTimeMillis());

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

    @Override
    public ServerManagementResponse listServersByType(final ServerManagementRequest request) throws ServerManagementException
    {
        final String methodName = IServerManagementProcessor.CNAME + "#listServersByType(final ServerManagementRequest request) throws ServerManagementException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ServerManagementRequest: {}", request);
        }

        ServerManagementResponse response = new ServerManagementResponse();

        final Server requestServer = request.getTargetServer();
        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("Server: {}", requestServer);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            boolean isServiceAuthorized = userControl.isUserAuthorizedForService(userAccount.getGuid(), request.getServiceId());

            if (DEBUG)
            {
                DEBUGGER.debug("isServiceAuthorized: {}", isServiceAuthorized);
            }

            if (isServiceAuthorized)
            {
                List<String[]> serverData = serverDAO.getServersByAttribute(requestServer.getServerType().name());

                if (DEBUG)
                {
                    DEBUGGER.debug("serverList: {}", serverData);
                }

                if ((serverData != null) && (serverData.size() != 0))
                {
                    List<Server> serverList = new ArrayList<Server>();

                    for (String[] data : serverData)
                    {
                        if (DEBUG)
                        {
                            for (String str : data)
                            {
                                DEBUGGER.debug("Data: {}", str);
                            }
                        }

                        Server server = new Server();
                        server.setServerGuid(data[0]);
                        server.setOsName(data[1]);
                        server.setServerStatus(ServerStatus.valueOf(data[2]));
                        server.setServerRegion(ServiceRegion.valueOf(data[3]));
                        server.setServerType(ServerType.valueOf(data[4]));
                        server.setDomainName(data[5]);
                        server.setCpuType(data[6]);
                        server.setCpuCount(Integer.parseInt(data[7]));
                        server.setServerRack(data[8]);
                        server.setRackPosition(data[9]);
                        server.setServerModel(data[10]);
                        server.setSerialNumber(data[11]);
                        server.setInstalledMemory(Integer.parseInt(data[12]));
                        server.setOperIpAddress(data[13]);
                        server.setOperHostName(data[14]);
                        server.setAssignedEngineer(data[15]);
                        server.setServerComments(data[16]);
                        server.setMgmtIpAddress(data[17]);
                        server.setMgmtHostName(data[18]);
                        server.setBkIpAddress(data[19]);
                        server.setBkHostName(data[20]);
                        server.setNasIpAddress(data[21]);
                        server.setNasHostName(data[22]);
                        server.setNatAddress(data[23]);
                        server.setMgrUrl(data[24]);
                        server.setDmgrPort(Integer.parseInt(data[25]));
                        server.setOwningDmgr(data[26]);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Server: {}", server);
                        }

                        serverList.add(server);
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("serverList: {}", serverList);
                    }

                    response.setRequestStatus(CoreServicesStatus.SUCCESS);
                    response.setResponse("Successfully loaded installed server information.");
                    response.setServerList(serverList);
                }
            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.FAILURE);
                response.setResponse("The requested user was not authorized to perform the operation");
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new ServerManagementException(sqx.getMessage(), sqx);
        }
        catch (UserControlServiceException ucsx)
        {
            ERROR_RECORDER.error(ucsx.getMessage(), ucsx);
            
            throw new ServerManagementException(ucsx.getMessage(), ucsx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setReqInfo(reqInfo);
                auditEntry.setUserAccount(userAccount);
                auditEntry.setAuditType(AuditType.LISTSERVERS);
                auditEntry.setAuditDate(System.currentTimeMillis());

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


    @Override
    public ServerManagementResponse getServerData(final ServerManagementRequest request) throws ServerManagementException
    {
        final String methodName = IServerManagementProcessor.CNAME + "#getServerData(final ServerManagementRequest request) throws ServerManagementException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ServerManagementRequest: {}", request);
        }

        ServerManagementResponse response = new ServerManagementResponse();

        final Server requestServer = request.getTargetServer();
        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("Server: {}", requestServer);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            boolean isServiceAuthorized = userControl.isUserAuthorizedForService(userAccount.getGuid(), request.getServiceId());

            if (DEBUG)
            {
                DEBUGGER.debug("isServiceAuthorized: {}", isServiceAuthorized);
            }

            if (isServiceAuthorized)
            {
                if (requestServer != null)
                {
                    List<String> serverDetail = serverDAO.getInstalledServer(requestServer.getServerGuid());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("serverDetail: {}", serverDetail);
                    }

                    if ((serverDetail != null) && (serverDetail.size() != 0))
                    {
                        Server targetServer = new Server();
                        targetServer.setServerGuid(serverDetail.get(0));
                        targetServer.setOsName(serverDetail.get(1));
                        targetServer.setServerStatus(ServerStatus.valueOf(serverDetail.get(2)));
                        targetServer.setServerRegion(ServiceRegion.valueOf(serverDetail.get(3)));
                        targetServer.setServerType(ServerType.valueOf(serverDetail.get(4)));
                        targetServer.setDomainName(serverDetail.get(5));
                        targetServer.setCpuType(serverDetail.get(6));
                        targetServer.setCpuCount(Integer.parseInt(serverDetail.get(7)));
                        targetServer.setServerRack(serverDetail.get(8));
                        targetServer.setRackPosition(serverDetail.get(9));
                        targetServer.setServerModel(serverDetail.get(10));
                        targetServer.setSerialNumber(serverDetail.get(11));
                        targetServer.setInstalledMemory(Integer.parseInt(serverDetail.get(12)));
                        targetServer.setOperIpAddress(serverDetail.get(13));
                        targetServer.setOperHostName(serverDetail.get(14));
                        targetServer.setAssignedEngineer(serverDetail.get(15));
                        targetServer.setServerComments(serverDetail.get(16));
                        targetServer.setMgmtIpAddress(serverDetail.get(17));
                        targetServer.setMgmtHostName(serverDetail.get(18));
                        targetServer.setBkIpAddress(serverDetail.get(19));
                        targetServer.setBkHostName(serverDetail.get(20));
                        targetServer.setNasIpAddress(serverDetail.get(21));
                        targetServer.setNasHostName(serverDetail.get(22));
                        targetServer.setNatAddress(serverDetail.get(23));
                        targetServer.setMgrUrl(serverDetail.get(24));
                        targetServer.setDmgrPort(Integer.parseInt(serverDetail.get(25)));
                        targetServer.setOwningDmgr(serverDetail.get(26));

                        if (DEBUG)
                        {
                            DEBUGGER.debug("targetServer: {}", targetServer);
                        }

                        response.setRequestStatus(CoreServicesStatus.SUCCESS);
                        response.setResponse("Successfully loaded installed server information.");
                        response.setServer(targetServer);
                    }
                    else
                    {
                        response.setRequestStatus(CoreServicesStatus.FAILURE);
                        response.setResponse("No server was located with the provided information");
                    }
                }
                else
                {
                    throw new ServerManagementException("No server search data was provided. Cannot continue");
                }
            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.FAILURE);
                response.setResponse("The requested user was not authorized to perform the operation");
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new ServerManagementException(sqx.getMessage(), sqx);
        }
        catch (UserControlServiceException ucsx)
        {
            ERROR_RECORDER.error(ucsx.getMessage(), ucsx);
            
            throw new ServerManagementException(ucsx.getMessage(), ucsx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setReqInfo(reqInfo);
                auditEntry.setUserAccount(userAccount);
                auditEntry.setAuditType(AuditType.ADDSERVER);
                auditEntry.setAuditDate(System.currentTimeMillis());

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

    @Override
    public ServerManagementResponse runNetstatCheck(final ServerManagementRequest request) throws ServerManagementException
    {
        final String methodName = IServerManagementProcessor.CNAME + "#runNetstatCheck(final ServerManagementRequest request)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ServerManagementRequest: ", request);
        }

        ServerManagementResponse response = new ServerManagementResponse();

        try
        {
            SystemManagerRequest systemReq = new SystemManagerRequest();
            systemReq.setMgmtType(SystemManagementType.SYSTEMCHECK);
            systemReq.setRequestType(SystemCheckType.NETSTAT);
            systemReq.setPortNumber(request.getPortNumber());

            if (DEBUG)
            {
                DEBUGGER.debug("SystemManagerRequest: {}", request);
            }

            Server sourceServer = request.getSourceServer();

            if (DEBUG)
            {
                DEBUGGER.debug("Server: {}", sourceServer);
            }

            AgentRequest agentRequest = new AgentRequest();
            agentRequest.setAppName(appConfig.getAppName());
            agentRequest.setRequestPayload(systemReq);

            if (DEBUG)
            {
                DEBUGGER.debug("AgentRequest: {}", agentRequest);
            }

            // always make the tcp conn to the oper hostname - thats where the agent should be listening
            String correlator = MQUtils.sendMqMessage(agentRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("correlator: {}", correlator);
            }

            if (StringUtils.isNotEmpty(correlator))
            {
                AgentResponse agentResponse = (AgentResponse) MQUtils.getMqMessage(correlator);

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
                response.setResponse("Failed to send message to configured request queue for action");
                response.setRequestStatus(CoreServicesStatus.FAILURE);
            }
        }
        catch (UtilityException ux)
        {
            ERROR_RECORDER.error(ux.getMessage(), ux);

            throw new ServerManagementException(ux.getMessage(), ux);
        }

        return response;
    }

    @Override
    public ServerManagementResponse runTelnetCheck(final ServerManagementRequest request) throws ServerManagementException
    {
        final String methodName = IServerManagementProcessor.CNAME + "#runTelnetCheck(final ServerManagementRequest request)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ServerManagementRequest: ", request);
        }

        ServerManagementResponse response = new ServerManagementResponse();

        try
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
            agentRequest.setAppName(appConfig.getAppName());
            agentRequest.setRequestPayload(systemReq);

            if (DEBUG)
            {
                DEBUGGER.debug("AgentRequest: {}", agentRequest);
            }

            // always make the tcp conn to the oper hostname - thats where the agent should be listening
            String correlator = MQUtils.sendMqMessage(agentRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("correlator: {}", correlator);
            }

            if (StringUtils.isNotEmpty(correlator))
            {
                AgentResponse agentResponse = (AgentResponse) MQUtils.getMqMessage(correlator);

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

                    response.setRequestStatus(CoreServicesStatus.SUCCESS);
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
                response.setResponse("Failed to send message to configured request queue for action");
                response.setRequestStatus(CoreServicesStatus.FAILURE);
            }
        }
        catch (UtilityException ux)
        {
            ERROR_RECORDER.error(ux.getMessage(), ux);

            throw new ServerManagementException(ux.getMessage(), ux);
        }

        return response;
    }

    @Override
    public ServerManagementResponse runRemoteDateCheck(final ServerManagementRequest request) throws ServerManagementException
    {
        final String methodName = IServerManagementProcessor.CNAME + "#runRemoteDateCheck(final ServerManagementRequest request)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ServerManagementRequest: ", request);
        }

        ServerManagementResponse response = new ServerManagementResponse();

        try
        {
            SystemManagerRequest systemReq = new SystemManagerRequest();
            systemReq.setMgmtType(SystemManagementType.SYSTEMCHECK);
            systemReq.setRequestType(SystemCheckType.REMOTEDATE);

            if (DEBUG)
            {
                DEBUGGER.debug("SystemManagerRequest: {}", request);
            }

            AgentRequest agentRequest = new AgentRequest();
            agentRequest.setAppName(appConfig.getAppName());
            agentRequest.setRequestPayload(systemReq);

            if (DEBUG)
            {
                DEBUGGER.debug("AgentRequest: {}", agentRequest);
            }

            // always make the tcp conn to the oper hostname - thats where the agent should be listening
            String correlator = MQUtils.sendMqMessage(agentRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("correlator: {}", correlator);
            }

            if (StringUtils.isNotEmpty(correlator))
            {
                AgentResponse agentResponse = (AgentResponse) MQUtils.getMqMessage(correlator);

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

                    response.setRequestStatus(CoreServicesStatus.SUCCESS);
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
                response.setResponse("Failed to send message to configured request queue for action");
                response.setRequestStatus(CoreServicesStatus.FAILURE);
            }
        }
        catch (UtilityException ux)
        {
            ERROR_RECORDER.error(ux.getMessage(), ux);

            throw new ServerManagementException(ux.getMessage(), ux);
        }

        return response;
    }
}
