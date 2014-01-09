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
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.processors.impl
 * File: ApplicationManagementProcessorImpl.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.List;
import java.util.UUID;
import java.util.Arrays;
import java.util.ArrayList;
import java.sql.SQLException;
import org.apache.commons.lang.StringUtils;

import com.cws.esolutions.core.utils.MQUtils;
import com.cws.esolutions.agent.dto.AgentRequest;
import com.cws.esolutions.agent.dto.AgentResponse;
import com.cws.esolutions.agent.enums.AgentStatus;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.core.processors.dto.Server;
import com.cws.esolutions.core.processors.dto.Service;
import com.cws.esolutions.core.processors.dto.Application;
import com.cws.esolutions.security.processors.dto.AuditEntry;
import com.cws.esolutions.security.processors.enums.AuditType;
import com.cws.esolutions.security.processors.dto.AuditRequest;
import com.cws.esolutions.core.utils.exception.UtilityException;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.agent.processors.dto.FileManagerRequest;
import com.cws.esolutions.agent.processors.dto.FileManagerResponse;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.core.processors.dto.ApplicationManagementRequest;
import com.cws.esolutions.core.processors.dto.ApplicationManagementResponse;
import com.cws.esolutions.security.processors.exception.AuditServiceException;
import com.cws.esolutions.core.processors.exception.ApplicationManagementException;
import com.cws.esolutions.security.services.exception.AccessControlServiceException;
import com.cws.esolutions.core.processors.interfaces.IApplicationManagementProcessor;
/**
 * @see com.cws.esolutions.core.processors.interfaces.IApplicationManagementProcessor
 */
public class ApplicationManagementProcessorImpl implements IApplicationManagementProcessor
{
    /**
     * @see com.cws.esolutions.core.processors.interfaces.IApplicationManagementProcessor#addNewApplication(com.cws.esolutions.core.processors.dto.ApplicationManagementRequest)
     */
    @Override
    public ApplicationManagementResponse addNewApplication(final ApplicationManagementRequest request) throws ApplicationManagementException
    {
        final String methodName = IApplicationManagementProcessor.CNAME + "#addNewApplication(final ApplicationManagementRequest request) throws ApplicationManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ApplicationManagementRequest: {}", request);
        }

        ApplicationManagementResponse response = new ApplicationManagementResponse();

        final Application application = request.getApplication();
        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("Application: {}", application);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            // it also requires authorization for the service
            boolean isUserAuthorized = accessControl.isUserAuthorizedForService(userAccount, request.getServiceId());

            if (DEBUG)
            {
                DEBUGGER.debug("isUserAuthorized: {}", isUserAuthorized);
            }

            if (isUserAuthorized)
            {
                String applGuid = UUID.randomUUID().toString();

                if (DEBUG)
                {
                    DEBUGGER.debug("applGuid: {}", applGuid);
                }

                List<Object> validator = null;
                response = new ApplicationManagementResponse();

                try
                {
                    validator = appDAO.getApplicationData(applGuid);
                }
                catch (SQLException sqx)
                {
                    ERROR_RECORDER.error(sqx.getMessage(), sqx);
                }

                if (DEBUG)
                {
                    DEBUGGER.debug("validator: {}", validator);
                }

                if ((validator == null) || (validator.size() == 0))
                {
                    // project does't already exist. we can add it
                    // we are NOT adding any applications to the project YET
                    // if there are any to add we'll do that later (its a
                    // different table in the database)
                    if ((application.getPlatforms() != null) && (application.getPlatforms().size() != 0))
                    {
                        List<String> platforms = new ArrayList<>();

                        for (Service targetPlatform : application.getPlatforms())
                        {
                            if (DEBUG)
                            {
                                DEBUGGER.debug("Service: {}", targetPlatform);
                            }

                            // make sure its a valid platform
                            if (serviceDao.getServiceData(targetPlatform.getGuid()) == null)
                            {
                                throw new ApplicationManagementException("Provided platform is not valid. Cannot continue.");
                            }

                            platforms.add(targetPlatform.getGuid());
                        }

                        // ok, good platform. we can add the application in
                        List<Object> appDataList = new ArrayList<Object>(
                                Arrays.asList(
                                        applGuid,
                                        application.getName(),
                                        application.getVersion(),
                                        application.getInstallPath(),
                                        application.getPackageLocation(),
                                        application.getPackageInstaller(),
                                        application.getInstallerOptions(),
                                        application.getLogsDirectory(),
                                        application.getPlatforms().toString()));

                        if (DEBUG)
                        {
                            DEBUGGER.debug("appDataList: {}", appDataList);
                        }

                        boolean isApplicationAdded = appDAO.addNewApplication(appDataList);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("isApplicationAdded: {}", isApplicationAdded);
                        }

                        if (isApplicationAdded)
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
                        throw new ApplicationManagementException("No platform was assigned to the given application. Cannot continue.");
                    }
                }
                else
                {
                    // project already exists
                    response.setRequestStatus(CoreServicesStatus.FAILURE);
                }
            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.UNAUTHORIZED);
            }

            if (DEBUG)
            {
                DEBUGGER.debug("ApplicationManagementResponse: {}", response);
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new ApplicationManagementException(sqx.getMessage(), sqx);
        }
        catch (AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);
            
            throw new ApplicationManagementException(acsx.getMessage(), acsx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.ADDAPP);
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
     * @see com.cws.esolutions.core.processors.interfaces.IApplicationManagementProcessor#updateApplicationData(com.cws.esolutions.core.processors.dto.ApplicationManagementRequest)
     */
    @Override
    public ApplicationManagementResponse updateApplicationData(final ApplicationManagementRequest request) throws ApplicationManagementException
    {
        final String methodName = IApplicationManagementProcessor.CNAME + "#updateApplicationData(final ApplicationManagementRequest request) throws ApplicationManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ApplicationManagementRequest: {}", request);
        }

        ApplicationManagementResponse response = new ApplicationManagementResponse();

        final Application application = request.getApplication();
        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("Application: {}", application);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            // it also requires authorization for the service
            boolean isUserAuthorized = accessControl.isUserAuthorizedForService(userAccount, request.getServiceId());

            if (DEBUG)
            {
                DEBUGGER.debug("isUserAuthorized: {}", isUserAuthorized);
            }

            if (isUserAuthorized)
            {
                List<Object> appDataList = new ArrayList<Object>(
                        Arrays.asList(
                                application.getGuid(),
                                application.getName(),
                                application.getVersion(),
                                application.getInstallPath(),
                                application.getPackageLocation(),
                                application.getPackageInstaller(),
                                application.getInstallerOptions(),
                                application.getLogsDirectory(),
                                application.getPlatforms().toString()));

                if (DEBUG)
                {
                    DEBUGGER.debug("appDataList: {}", appDataList);
                }

                boolean isComplete = appDAO.updateApplication(appDataList);

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

            if (DEBUG)
            {
                DEBUGGER.debug("ApplicationManagementResponse: {}", response);
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new ApplicationManagementException(sqx.getMessage(), sqx);
        }
        catch (AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);
            
            throw new ApplicationManagementException(acsx.getMessage(), acsx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.UPDATEAPP);
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
     * @see com.cws.esolutions.core.processors.interfaces.IApplicationManagementProcessor#deleteApplicationData(com.cws.esolutions.core.processors.dto.ApplicationManagementRequest)
     */
    @Override
    public ApplicationManagementResponse deleteApplicationData(final ApplicationManagementRequest request) throws ApplicationManagementException
    {
        final String methodName = IApplicationManagementProcessor.CNAME + "#deleteApplicationData(final ApplicationManagementRequest request) throws ApplicationManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ApplicationManagementRequest: {}", request);
        }

        ApplicationManagementResponse response = new ApplicationManagementResponse();

        final Application application = request.getApplication();
        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("Application: {}", application);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            // it also requires authorization for the service
            boolean isUserAuthorized = accessControl.isUserAuthorizedForService(userAccount, request.getServiceId());

            if (DEBUG)
            {
                DEBUGGER.debug("isUserAuthorized: {}", isUserAuthorized);
            }

            if (isUserAuthorized)
            {
                boolean isComplete = appDAO.deleteApplication(application.getGuid());

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

            if (DEBUG)
            {
                DEBUGGER.debug("ApplicationManagementResponse: {}", response);
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new ApplicationManagementException(sqx.getMessage(), sqx);
        }
        catch (AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);
            
            throw new ApplicationManagementException(acsx.getMessage(), acsx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.DELETEAPP);
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
     * @see com.cws.esolutions.core.processors.interfaces.IApplicationManagementProcessor#listApplications(com.cws.esolutions.core.processors.dto.ApplicationManagementRequest)
     */
    @Override
    public ApplicationManagementResponse listApplications(final ApplicationManagementRequest request) throws ApplicationManagementException
    {
        final String methodName = IApplicationManagementProcessor.CNAME + "#listApplications(final ApplicationManagementRequest request) throws ApplicationManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ApplicationManagementRequest: {}", request);
        }

        ApplicationManagementResponse response = new ApplicationManagementResponse();

        final Application application = request.getApplication();
        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("Application: {}", application);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            // it also requires authorization for the service
            boolean isUserAuthorized = accessControl.isUserAuthorizedForService(userAccount, request.getServiceId());

            if (DEBUG)
            {
                DEBUGGER.debug("isUserAuthorized: {}", isUserAuthorized);
            }

            if (isUserAuthorized)
            {
                List<String[]> appData = appDAO.listInstalledApplications(request.getStartPage());

                if (DEBUG)
                {
                    DEBUGGER.debug("List<String[]>: {}", appData);
                }

                if ((appData != null) && (appData.size() != 0))
                {
                    List<Application> appList = new ArrayList<>();

                    for (String[] array : appData)
                    {
                        Application app = new Application();
                        app.setGuid(array[0]); // T1.APPLICATION_GUID
                        app.setName(array[1]); // T1.APPLICATION_NAME

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Application: {}", app);
                        }

                        appList.add(app);
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("List<Application>: {}", appList);
                    }

                    response.setApplicationList(appList);
                    response.setRequestStatus(CoreServicesStatus.SUCCESS);
                }
                else
                {
                    // no data
                    response.setRequestStatus(CoreServicesStatus.FAILURE);
                }

                if (DEBUG)
                {
                    DEBUGGER.debug("ApplicationManagementResponse: {}", response);
                }
            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.UNAUTHORIZED);
            }

            if (DEBUG)
            {
                DEBUGGER.debug("ApplicationManagementResponse: {}", response);
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new ApplicationManagementException(sqx.getMessage(), sqx);
        }
        catch (AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);

            throw new ApplicationManagementException(acsx.getMessage(), acsx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.LISTAPPS);
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
     * @see com.cws.esolutions.core.processors.interfaces.IApplicationManagementProcessor#getApplicationData(com.cws.esolutions.core.processors.dto.ApplicationManagementRequest)
     */
    @Override
    public ApplicationManagementResponse getApplicationData(final ApplicationManagementRequest request) throws ApplicationManagementException
    {
        final String methodName = IApplicationManagementProcessor.CNAME + "#getApplicationData(final ApplicationManagementRequest request) throws ApplicationManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ApplicationManagementRequest: {}", request);
        }

        ApplicationManagementResponse response = new ApplicationManagementResponse();

        final Application application = request.getApplication();
        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("Application: {}", application);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            // it also requires authorization for the service
            boolean isUserAuthorized = accessControl.isUserAuthorizedForService(userAccount, request.getServiceId());

            if (DEBUG)
            {
                DEBUGGER.debug("isUserAuthorized: {}", isUserAuthorized);
            }

            if (isUserAuthorized)
            {
                List<Service> appPlatforms = null;
                List<Object> appData = appDAO.getApplicationData(application.getGuid());

                if (DEBUG)
                {
                    DEBUGGER.debug("appData: {}", appData);
                }

                if ((appData != null) && (appData.size() != 0))
                {
                    if (DEBUG)
                    {
                        DEBUGGER.debug("List<Object>: {}", appData);
                    }

                    if (StringUtils.split((String) appData.get(8), ",").length >= 1) // T1.PLATFORM_GUID
                    {
                        String tmp = StringUtils.remove((String) appData.get(8), "[");
                        String platformList = StringUtils.remove(tmp, "]");

                        if (DEBUG)
                        {
                            DEBUGGER.debug("platformList: {}", platformList);
                        }

                        appPlatforms = new ArrayList<>();

                        for (String platformGuid : platformList.split(","))
                        {
                            List<String> platformData = serviceDao.getServiceData(StringUtils.trim(platformGuid));

                            if (DEBUG)
                            {
                                DEBUGGER.debug("platformData: {}", platformData);
                            }

                            if ((platformData != null) && (platformData.size() != 0))
                            {
                                Service platform = new Service();
                                platform.setGuid(platformData.get(0)); // T1.PLATFORM_GUID
                                platform.setName(platformData.get(1)); // T1.PLATFORM_NAME

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("Service: {}", platform);
                                }

                                appPlatforms.add(platform);
                            }
                            else
                            {
                                throw new ApplicationManagementException("Unable to locate a valid platform for the provided application. Cannot continue.");
                            }
                        }

                        if (DEBUG)
                        {
                            DEBUGGER.debug("List<Service>: {}", appPlatforms);
                        }
                    }

                    // then put it all together
                    Application resApplication = new Application();
                    resApplication.setPlatforms(appPlatforms);
                    resApplication.setGuid((String) appData.get(0)); // GUID
                    resApplication.setName((String) appData.get(1)); // NAME
                    resApplication.setVersion((double) appData.get(2)); // VERSION
                    resApplication.setInstallPath((String) appData.get(3)); // INSTALLATION_PATH
                    resApplication.setPackageLocation((String) appData.get(4)); // PACKAGE_LOCATION
                    resApplication.setPackageInstaller((String) appData.get(5)); // PACKAGE_INSTALLER
                    resApplication.setInstallerOptions((String) appData.get(6)); // INSTALLER_OPTIONS
                    resApplication.setLogsDirectory((String) appData.get(7)); // INSTALL_PATH
                    
                    if (DEBUG)
                    {
                        DEBUGGER.debug("Application: {}", resApplication);
                    }

                    response.setApplication(resApplication);
                    response.setRequestStatus(CoreServicesStatus.SUCCESS);
                }
                else
                {
                    ERROR_RECORDER.error("No applications were located for the provided data.");

                    response.setRequestStatus(CoreServicesStatus.FAILURE);
                }
            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.UNAUTHORIZED);
            }

            if (DEBUG)
            {
                DEBUGGER.debug("ApplicationManagementResponse: {}", response);
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new ApplicationManagementException(sqx.getMessage(), sqx);
        }
        catch (AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);
            
            throw new ApplicationManagementException(acsx.getMessage(), acsx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.LOADAPP);
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
     * @see com.cws.esolutions.core.processors.interfaces.IApplicationManagementProcessor#applicationFileRequest(com.cws.esolutions.core.processors.dto.ApplicationManagementRequest)
     */
    @Override
    public ApplicationManagementResponse applicationFileRequest(final ApplicationManagementRequest request) throws ApplicationManagementException
    {
        final String methodName = IApplicationManagementProcessor.CNAME + "#applicationFileRequest(final ApplicationManagementRequest request) throws ApplicationManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ApplicationManagementRequest: {}", request);
        }

        AgentResponse agentResponse = null;
        ApplicationManagementResponse response = new ApplicationManagementResponse();

        final Server server = request.getServer();
        final Application application = request.getApplication();
        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("Application: {}", application);
            DEBUGGER.debug("Server: {}", server);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            // it also requires authorization for the service
            boolean isUserAuthorized = accessControl.isUserAuthorizedForService(userAccount, request.getServiceId());

            if (DEBUG)
            {
                DEBUGGER.debug("isUserAuthorized: {}", isUserAuthorized);
            }

            if (isUserAuthorized)
            {
                if (DEBUG)
                {
                    DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
                }

                // need to authorize for project
                boolean isAuthorizedForRequest = accessControl.isUserAuthorizedForService(userAccount, request.getServiceId());

                if (DEBUG)
                {
                    DEBUGGER.debug("isAuthorizedForRequest: {}", isAuthorizedForRequest);
                }

                if (isAuthorizedForRequest)
                {
                    List<Object> appData = appDAO.getApplicationData(application.getGuid());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("appData: {}", appData);
                    }

                    if ((appData != null) && (appData.size() != 0))
                    {
                        FileManagerRequest fileRequest = new FileManagerRequest();

                        if (StringUtils.isEmpty(request.getRequestFile()))
                        {
                            fileRequest.setRequestFile((String) appData.get(3)); // TODO: this should be the root dir
                        }
                        else
                        {
                            fileRequest.setRequestFile(appData.get(3) + "/" + request.getRequestFile());
                        }

                        if (DEBUG)
                        {
                            DEBUGGER.debug("FileManagerRequest: {}", fileRequest);
                        }

                        AgentRequest agentRequest = new AgentRequest();
                        agentRequest.setAppName(appConfig.getAppName());
                        agentRequest.setRequestPayload(fileRequest);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("AgentRequest: {}", agentRequest);
                        }

                        String correlator = MQUtils.sendMqMessage(agentConfig.getConnectionName(),
                                new ArrayList<>(
                                        Arrays.asList(
                                                agentConfig.getUsername(),
                                                agentConfig.getPassword(),
                                                agentConfig.getSalt())),
                                                agentConfig.getRequestQueue(),
                                                server.getOperHostName(),
                                                agentRequest);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("correlator: {}", correlator);
                        }

                        if (StringUtils.isNotEmpty(correlator))
                        {
                            agentResponse = (AgentResponse) MQUtils.getMqMessage(agentConfig.getConnectionName(),
                                    new ArrayList<>(
                                            Arrays.asList(
                                                    agentConfig.getUsername(),
                                                    agentConfig.getPassword(),
                                                    agentConfig.getSalt())),
                                                    agentConfig.getRequestQueue(),
                                                    agentConfig.getTimeout(),
                                                    correlator);
                        }
                        else
                        {
                            response.setRequestStatus(CoreServicesStatus.FAILURE);

                            return response;
                        }

                        if (DEBUG)
                        {
                            DEBUGGER.debug("AgentResponse: {}", agentResponse);
                        }

                        if (agentResponse.getRequestStatus() == AgentStatus.SUCCESS)
                        {
                            FileManagerResponse fileResponse = (FileManagerResponse) agentResponse.getResponsePayload();

                            if (DEBUG)
                            {
                                DEBUGGER.debug("FileManagerResponse: {}", fileResponse);
                            }

                            if (fileResponse.getRequestStatus() == AgentStatus.SUCCESS)
                            {
                                if ((fileResponse.getFileData() != null) && (fileResponse.getFileData().length != 0))
                                {
                                    byte[] fileData = fileResponse.getFileData();

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("fileData: {}", fileData);
                                    }

                                    response.setFileData(fileData);
                                }
                                else
                                {
                                    // just a directory listing
                                    List<String> fileList = fileResponse.getDirListing();

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("fileList: {}", fileList);
                                    }

                                    response.setFileList(fileList);
                                }

                                response.setApplication(application);
                                response.setCurrentPath(request.getRequestFile());
                                response.setRequestStatus(CoreServicesStatus.SUCCESS);
                            }
                            else
                            {
                                response.setApplication(application);
                                response.setRequestStatus(CoreServicesStatus.FAILURE);
                            }
                        }
                        else
                        {
                            response.setApplication(application);
                            response.setRequestStatus(CoreServicesStatus.FAILURE);
                        }
                    }
                    else
                    {
                        ERROR_RECORDER.error("No application data was located and no target was found on the request. Cannot continue.");

                        response.setRequestStatus(CoreServicesStatus.FAILURE);
                    }
                }
                else
                {
                    response.setRequestStatus(CoreServicesStatus.UNAUTHORIZED);
                }
            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.UNAUTHORIZED);
            }

            if (DEBUG)
            {
                DEBUGGER.debug("ApplicationManagementResponse: {}", response);
            }
        }
        catch (AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);

            throw new ApplicationManagementException(acsx.getMessage(), acsx);
        }
        catch (UtilityException ux)
        {
            ERROR_RECORDER.error(ux.getMessage(), ux);

            throw new ApplicationManagementException(ux.getMessage(), ux);
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new ApplicationManagementException(sqx.getMessage(), sqx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.GETFILES);
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
     * @see com.cws.esolutions.core.processors.interfaces.IApplicationManagementProcessor#deployApplication(com.cws.esolutions.core.processors.dto.ApplicationManagementRequest)
     */
    @Override
    public ApplicationManagementResponse deployApplication(final ApplicationManagementRequest request) throws ApplicationManagementException
    {
        final String methodName = IApplicationManagementProcessor.CNAME + "#deployApplication(final ApplicationManagementRequest request) throws ApplicationManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ApplicationManagementRequest: {}", request);
        }

        ApplicationManagementResponse response = new ApplicationManagementResponse();

        final Application application = request.getApplication();
        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("Application: {}", application);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            // it also requires authorization for the service
            boolean isUserAuthorized = accessControl.isUserAuthorizedForService(userAccount, request.getServiceId());

            if (DEBUG)
            {
                DEBUGGER.debug("isUserAuthorized: {}", isUserAuthorized);
            }

            if (isUserAuthorized)
            {
                // do deployment work here
            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.UNAUTHORIZED);
            }

            if (DEBUG)
            {
                DEBUGGER.debug("ApplicationManagementResponse: {}", response);
            }
        }
        catch (AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);

            throw new ApplicationManagementException(acsx.getMessage(), acsx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.DEPLOYAPP);
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
