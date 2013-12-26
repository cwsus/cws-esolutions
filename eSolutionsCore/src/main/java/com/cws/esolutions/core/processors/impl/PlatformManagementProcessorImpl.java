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
 * @see com.cws.esolutions.core.processors.interfaces.IPlatformManagementProcessor
 */
import java.util.UUID;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.sql.SQLException;
import org.apache.commons.lang.StringUtils;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.core.processors.dto.Server;
import com.cws.esolutions.core.processors.dto.Platform;
import com.cws.esolutions.security.processors.dto.AuditEntry;
import com.cws.esolutions.security.processors.enums.AuditType;
import com.cws.esolutions.core.processors.enums.ServiceRegion;
import com.cws.esolutions.core.processors.enums.ServiceStatus;
import com.cws.esolutions.security.processors.dto.AuditRequest;
import com.cws.esolutions.core.processors.enums.NetworkPartition;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.core.processors.dto.PlatformManagementRequest;
import com.cws.esolutions.core.processors.dto.PlatformManagementResponse;
import com.cws.esolutions.security.processors.exception.AuditServiceException;
import com.cws.esolutions.core.processors.exception.PlatformManagementException;
import com.cws.esolutions.core.processors.interfaces.IPlatformManagementProcessor;
import com.cws.esolutions.security.services.exception.AccessControlServiceException;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.processors.impl
 * File: PlatformManagementProcessorImpl.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
public class PlatformManagementProcessorImpl implements IPlatformManagementProcessor
{
    /**
     * @see com.cws.esolutions.core.processors.interfaces.IPlatformManagementProcessor#addNewPlatform(com.cws.esolutions.core.processors.dto.PlatformManagementRequest)
     */
    @Override
    public PlatformManagementResponse addNewPlatform(final PlatformManagementRequest request) throws PlatformManagementException
    {
        final String methodName = IPlatformManagementProcessor.CNAME + "#addNewPlatform(final PlatformManagementRequest request) throws PlatformManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("PlatformManagementRequest: {}", request);
        }

        PlatformManagementResponse response = new PlatformManagementResponse();

        final Platform platform = request.getPlatform();
        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("Platform: {}", platform);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            boolean isServiceAuthorized = accessControl.isUserAuthorizedForService(userAccount, request.getServiceId());

            if (DEBUG)
            {
                DEBUGGER.debug("isServiceAuthorized: {}", isServiceAuthorized);
            }

            if (isServiceAuthorized)
            {
                if (platform == null)
                {
                    throw new PlatformManagementException("No platform was provided. Cannot continue.");
                }

                // make sure all the platform data is there
                List<String[]> validator = null;

                try
                {
                    validator = platformDao.listPlatformsByAttribute(platform.getName(), request.getStartPage());
                }
                catch (SQLException sqx)
                {
                    ERROR_RECORDER.error(sqx.getMessage(), sqx);
                }

                if (DEBUG)
                {
                    DEBUGGER.debug("Validator: {}", validator);
                }

                if ((validator == null) || (validator.size() == 0))
                {
                    // valid platform
                    List<String> serverList = new ArrayList<>();
                    for (Server server : platform.getServers())
                    {
                        if (DEBUG)
                        {
                            DEBUGGER.debug("Server: {}", server);
                        }

                        serverList.add(server.getServerGuid());
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("List<String>: {}", serverList);
                    }

                    List<String> insertData = new ArrayList<>(
                            Arrays.asList(
                                    UUID.randomUUID().toString(),
                                    platform.getName(),
                                    platform.getRegion().name(),
                                    platform.getPartition().name(),
                                    platform.getStatus().name(),
                                    serverList.toString(),
                                    platform.getDescription()));

                    if (DEBUG)
                    {
                        for (Object str : insertData)
                        {
                            DEBUGGER.debug("Value: {}", str);
                        }
                    }

                    boolean isComplete = platformDao.addNewPlatform(insertData);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("isComplete: {}", isComplete);
                    }

                    if (isComplete)
                    {
                        response.setRequestStatus(CoreServicesStatus.SUCCESS);
                    }
                    else
                    {
                        response.setRequestStatus(CoreServicesStatus.FAILURE);
                    }
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
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new PlatformManagementException(sqx.getMessage(), sqx);
        }
        catch (AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);
            
            throw new PlatformManagementException(acsx.getMessage(), acsx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.ADDPLATFORM);
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
     * @see com.cws.esolutions.core.processors.interfaces.IPlatformManagementProcessor#updatePlatformData(com.cws.esolutions.core.processors.dto.PlatformManagementRequest)
     */
    @Override
    public PlatformManagementResponse updatePlatformData(final PlatformManagementRequest request) throws PlatformManagementException
    {
        final String methodName = IPlatformManagementProcessor.CNAME + "#updatePlatformData(final PlatformManagementRequest request) throws PlatformManagementException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("PlatformManagementRequest: {}", request);
        }

        PlatformManagementResponse response = new PlatformManagementResponse();

        final Platform platform = request.getPlatform();
        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("Platform: {}", platform);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            boolean isServiceAuthorized = accessControl.isUserAuthorizedForService(userAccount, request.getServiceId());

            if (DEBUG)
            {
                DEBUGGER.debug("isServiceAuthorized: {}", isServiceAuthorized);
            }

            if (isServiceAuthorized)
            {
                List<String> serverList = new ArrayList<>();
                for (Server server : platform.getServers())
                {
                    if (DEBUG)
                    {
                        DEBUGGER.debug("Server: {}", server);
                    }

                    serverList.add(server.getServerGuid());
                }

                if (DEBUG)
                {
                    DEBUGGER.debug("List<String>: {}", serverList);
                }

                List<String> insertData = new ArrayList<>(
                        Arrays.asList(
                                platform.getGuid(),
                                platform.getName(),
                                platform.getRegion().name(),
                                platform.getPartition().name(),
                                platform.getStatus().name(),
                                serverList.toString(),
                                platform.getDescription()));

                if (DEBUG)
                {
                    for (Object str : insertData)
                    {
                        DEBUGGER.debug("Value: {}", str);
                    }
                }

                boolean isComplete = platformDao.updatePlatformData(insertData);

                if (DEBUG)
                {
                    DEBUGGER.debug("isComplete: {}", isComplete);
                }

                if (isComplete)
                {
                    response.setRequestStatus(CoreServicesStatus.SUCCESS);
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
        catch (AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);

            throw new PlatformManagementException(acsx.getMessage(), acsx);
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new PlatformManagementException(sqx.getMessage(), sqx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.UPDATEPLATFORM);
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
     * @see com.cws.esolutions.core.processors.interfaces.IPlatformManagementProcessor#listPlatforms(com.cws.esolutions.core.processors.dto.PlatformManagementRequest)
     */
    @Override
    public PlatformManagementResponse listPlatforms(final PlatformManagementRequest request) throws PlatformManagementException
    {
        final String methodName = IPlatformManagementProcessor.CNAME + "#listPlatforms(final PlatformManagementRequest request) throws PlatformManagementException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("PlatformManagementRequest: {}", request);
        }

        PlatformManagementResponse response = new PlatformManagementResponse();

        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            boolean isServiceAuthorized = accessControl.isUserAuthorizedForService(userAccount, request.getServiceId());

            if (DEBUG)
            {
                DEBUGGER.debug("isServiceAuthorized: {}", isServiceAuthorized);
            }

            if (isServiceAuthorized)
            {
                int count = platformDao.getPlatformCount();

                if (DEBUG)
                {
                    DEBUGGER.debug("count: {}", count);
                }

                List<String[]> platformData = platformDao.listAvailablePlatforms(request.getStartPage());

                if (DEBUG)
                {
                    DEBUGGER.debug("platformData: {}", platformData);
                }

                if ((platformData != null) && (platformData.size() != 0))
                {
                    List<Platform> platformList = new ArrayList<>();

                    for (String[] data : platformData)
                    {
                        Platform platform = new Platform();
                        platform.setGuid(data[0]);
                        platform.setName(data[1]);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Platform: {}", platform);
                        }

                        platformList.add(platform);
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("platformList: {}", platformList);
                    }

                    response.setEntryCount(count);
                    response.setPlatformList(platformList);
                    response.setRequestStatus(CoreServicesStatus.SUCCESS);
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
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new PlatformManagementException(sqx.getMessage(), sqx);
        }
        catch (AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);
            
            throw new PlatformManagementException(acsx.getMessage(), acsx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.LISTPLATFORMS);
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
     * @see com.cws.esolutions.core.processors.interfaces.IPlatformManagementProcessor#listPlatformsByAttribute(com.cws.esolutions.core.processors.dto.PlatformManagementRequest)
     */
    @Override
    public PlatformManagementResponse listPlatformsByAttribute(final PlatformManagementRequest request) throws PlatformManagementException
    {
        final String methodName = IPlatformManagementProcessor.CNAME + "#listPlatformsByAttribute(final PlatformManagementRequest request) throws PlatformManagementException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("PlatformManagementRequest: {}", request);
        }

        PlatformManagementResponse response = new PlatformManagementResponse();

        final Platform reqPlatform = request.getPlatform();
        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("Platform: {}", reqPlatform);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            boolean isServiceAuthorized = accessControl.isUserAuthorizedForService(userAccount, request.getServiceId());

            if (DEBUG)
            {
                DEBUGGER.debug("isServiceAuthorized: {}", isServiceAuthorized);
            }

            if (isServiceAuthorized)
            {
                List<String[]> platformData = platformDao.listPlatformsByAttribute(reqPlatform.getName(), request.getStartPage());

                if (DEBUG)
                {
                    DEBUGGER.debug("platformData: {}", platformData);
                }

                if ((platformData != null) && (platformData.size() != 0))
                {
                    List<Platform> platformList = new ArrayList<>();

                    for (String[] data : platformData)
                    {
                        Platform platform = new Platform();
                        platform.setGuid(data[0]);
                        platform.setName(data[1]);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Platform: {}", platform);
                        }

                        platformList.add(platform);
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("platformList: {}", platformList);
                    }

                    response.setPlatformList(platformList);
                    response.setRequestStatus(CoreServicesStatus.SUCCESS);
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
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new PlatformManagementException(sqx.getMessage(), sqx);
        }
        catch (AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);
            
            throw new PlatformManagementException(acsx.getMessage(), acsx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.LISTPLATFORMS);
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
     * @see com.cws.esolutions.core.processors.interfaces.IPlatformManagementProcessor#getPlatformData(com.cws.esolutions.core.processors.dto.PlatformManagementRequest)
     */
    @Override
    public PlatformManagementResponse getPlatformData(final PlatformManagementRequest request) throws PlatformManagementException
    {
        final String methodName = IPlatformManagementProcessor.CNAME + "#getPlatformData(final PlatformManagementRequest request) throws PlatformManagementException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("PlatformManagementRequest: {}", request);
        }

        PlatformManagementResponse response = new PlatformManagementResponse();

        final Platform platform = request.getPlatform();
        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("Platform: {}", platform);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            boolean isServiceAuthorized = accessControl.isUserAuthorizedForService(userAccount, request.getServiceId());

            if (DEBUG)
            {
                DEBUGGER.debug("isServiceAuthorized: {}", isServiceAuthorized);
            }

            if (isServiceAuthorized)
            {
                List<Server> serverList = null;

                if (platform != null)
                {
                    List<Object> platformData = platformDao.getPlatformData(platform.getGuid());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("platformData: {}", platformData);
                    }

                    if ((platformData != null) && (platformData.size() != 0))
                    {
                        // appservers
                        String appTmp = StringUtils.remove((String) platformData.get(5), "["); // PLATFORM_SERVERS
                        String platformServers = StringUtils.remove(appTmp, "]");

                        if (DEBUG)
                        {
                            DEBUGGER.debug("String: {}", platformServers);
                        }

                        if (platformServers.split(",").length >= 1)
                        {
                            serverList = new ArrayList<>();

                            for (String serverGuid : platformServers.split(","))
                            {
                                List<Object> serverData = serverDao.getInstalledServer(StringUtils.trim(serverGuid));

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("serverData: {}", serverData);
                                }

                                if ((serverData != null) && (serverData.size() != 0))
                                {
                                    Server server = new Server();
                                    server.setServerGuid((String) serverData.get(0)); // SYSTEM_GUID
                                    server.setServerRegion(ServiceRegion.valueOf((String) serverData.get(3))); // SYSTEM_REGION
                                    server.setOperHostName((String) serverData.get(16)); // OPER_HOSTNAME

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("Server: {}", server);
                                    }

                                    serverList.add(server);
                                }
                            }

                            if (DEBUG)
                            {
                                DEBUGGER.debug("serverList: {}", serverList);
                            }
                        }

                        Platform resPlatform = new Platform();
                        resPlatform.setGuid((String) platformData.get(0));
                        resPlatform.setName((String) platformData.get(1));
                        resPlatform.setRegion(ServiceRegion.valueOf((String) platformData.get(2)));
                        resPlatform.setPartition(NetworkPartition.valueOf((String) platformData.get(3)));
                        resPlatform.setStatus(ServiceStatus.valueOf((String) platformData.get(4)));
                        resPlatform.setServers(serverList);
                        resPlatform.setDescription((String) platformData.get(6));

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Platform: {}", resPlatform);
                        }

                        response.setRequestStatus(CoreServicesStatus.SUCCESS);
                        response.setPlatformData(resPlatform);
                    }
                    else
                    {
                        response.setRequestStatus(CoreServicesStatus.FAILURE);
                    }
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
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new PlatformManagementException(sqx.getMessage(), sqx);
        }
        catch (AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);
            
            throw new PlatformManagementException(acsx.getMessage(), acsx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.LOADPLATFORM);
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
