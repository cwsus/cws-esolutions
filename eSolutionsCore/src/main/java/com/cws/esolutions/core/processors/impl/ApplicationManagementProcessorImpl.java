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
import com.cws.esolutions.core.processors.dto.Project;
import com.cws.esolutions.core.processors.dto.Platform;
import com.cws.esolutions.security.audit.dto.AuditEntry;
import com.cws.esolutions.security.audit.enums.AuditType;
import com.cws.esolutions.security.audit.dto.AuditRequest;
import com.cws.esolutions.core.processors.dto.Application;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.core.utils.exception.UtilityException;
import com.cws.esolutions.agent.processors.dto.FileManagerRequest;
import com.cws.esolutions.agent.processors.dto.FileManagerResponse;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.security.audit.exception.AuditServiceException;
import com.cws.esolutions.core.processors.dto.ApplicationManagementRequest;
import com.cws.esolutions.core.processors.dto.ApplicationManagementResponse;
import com.cws.esolutions.core.processors.exception.ApplicationManagementException;
import com.cws.esolutions.core.processors.interfaces.IApplicationManagementProcessor;
import com.cws.esolutions.security.access.control.exception.UserControlServiceException;
import com.cws.esolutions.security.access.control.exception.AdminControlServiceException;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.dao.interfaces
 * File: IPackageDataDAO.java
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Jan 4, 2013 3:36:54 PM
 *     Created.
 */
/**
 * @see com.cws.esolutions.core.dao.processors.interfaces.IKnowledgeBaseDAO
 */
public class ApplicationManagementProcessorImpl implements IApplicationManagementProcessor
{
    /**
     * @see com.cws.esolutions.core.processors.interfaces.IApplicationManagementProcessor#addNewApplication(com.cws.esolutions.core.processors.dto.ApplicationManagementRequest)
     */
    @Override
    public ApplicationManagementResponse addNewApplication(final ApplicationManagementRequest request) throws ApplicationManagementException
    {
        final String methodName = IApplicationManagementProcessor.CNAME + "#modifyProjectData(final ApplicationManagementRequest request) throws ApplicationManagementException";

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

        if (reqInfo != null)
        {
            try
            {
                // this is an administrative function and requires admin level
                boolean isAdminAuthorized = adminControl.adminControlService(userAccount);

                if (DEBUG)
                {
                    DEBUGGER.debug("isAdminAuthorized: {}", isAdminAuthorized);
                }

                // it also requires authorization for the service
                boolean isUserAuthorized = userControl.isUserAuthorizedForService(userAccount, request.getServiceId());

                if (DEBUG)
                {
                    DEBUGGER.debug("isUserAuthorized: {}", isUserAuthorized);
                }

                if ((isAdminAuthorized) && (isUserAuthorized))
                {
                    String applGuid = (StringUtils.isNotEmpty(application.getApplicationGuid())) ? application.getApplicationGuid() : UUID.randomUUID().toString();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("applGuid: {}", applGuid);
                    }

                    List<String> validator = null;
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
                        if ((application.getApplicationPlatforms() != null) && (application.getApplicationPlatforms().size() != 0))
                        {
                            List<String> platforms = new ArrayList<>();

                            for (Platform targetPlatform : application.getApplicationPlatforms())
                            {
                                if (DEBUG)
                                {
                                    DEBUGGER.debug("Platform: {}", targetPlatform);
                                }

                                // make sure its a valid platform
                                if (platformDao.getPlatformData(targetPlatform.getPlatformGuid()) == null)
                                {
                                    throw new ApplicationManagementException("Provided platform is not valid. Cannot continue.");
                                }

                                platforms.add(targetPlatform.getPlatformGuid());
                            }

                            Project targetProject = application.getApplicationProject();

                            if (DEBUG)
                            {
                                DEBUGGER.debug("Project: {}", targetProject);
                            }

                            List<String> isProjectValid = projectDAO.getProjectData(targetProject.getProjectGuid());

                            if (DEBUG)
                            {
                                DEBUGGER.debug("isProjectValid: {}", isProjectValid);
                            }

                            if ((isProjectValid == null) || (isProjectValid.size() == 0))
                            {
                                throw new ApplicationManagementException("Provided project does not exist in the asset datasource. Cannot add application.");
                            }

                            // ok, good platform. we can add the application in
                            List<String> appDataList = new ArrayList<>(
                                    Arrays.asList(
                                            applGuid,
                                            application.getApplicationName(),
                                            application.getApplicationVersion(),
                                            application.getBasePath(),
                                            application.getScmPath(),
                                            application.getApplicationCluster(),
                                            application.getJvmName(),
                                            application.getApplicationInstallPath(),
                                            application.getApplicationLogsPath(),
                                            application.getPidDirectory(),
                                            targetProject.getProjectGuid(),
                                            platforms.toString()));

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
                                response.setResponse("Successfully added application " + application.getApplicationName() + " to the asset database");
                                response.setRequestStatus(CoreServicesStatus.SUCCESS);
                            }
                            else
                            {
                                response.setRequestStatus(CoreServicesStatus.FAILURE);
                                response.setResponse("Failed to add application " + application.getApplicationName() + " to the asset database");
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
                        response.setResponse("Failed to add " + application.getApplicationName() + " to the asset datasource as it already exists.");
                    }
                }
                else
                {
                    response.setRequestStatus(CoreServicesStatus.UNAUTHORIZED);
                    response.setResponse("The requesting user was NOT authorized to perform the operation");
                }
            }
            catch (SQLException sqx)
            {
                ERROR_RECORDER.error(sqx.getMessage(), sqx);
    
                throw new ApplicationManagementException(sqx.getMessage(), sqx);
            }
            catch (UserControlServiceException ucsx)
            {
                ERROR_RECORDER.error(ucsx.getMessage(), ucsx);
                
                throw new ApplicationManagementException(ucsx.getMessage(), ucsx);
            }
            catch (AdminControlServiceException acsx)
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
        }
        else
        {
            throw new ApplicationManagementException("No audit host info was provided. Cannot continue");
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

        if (reqInfo != null)
        {
            try
            {
                // this is an administrative function and requires admin level
                boolean isAdminAuthorized = adminControl.adminControlService(userAccount);

                if (DEBUG)
                {
                    DEBUGGER.debug("isAdminAuthorized: {}", isAdminAuthorized);
                }

                // it also requires authorization for the service
                boolean isUserAuthorized = userControl.isUserAuthorizedForService(userAccount, request.getServiceId());

                if (DEBUG)
                {
                    DEBUGGER.debug("isUserAuthorized: {}", isUserAuthorized);
                }

                if ((isAdminAuthorized) && (isUserAuthorized))
                {
                    // get the current application information
                    List<String> currAppData = appDAO.getApplicationData(application.getApplicationGuid());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("currAppData: {}", currAppData);
                    }

                    if (currAppData.get(11).split(",").length >= 1)
                    {
                        List<Platform> appPlatforms = new ArrayList<>();

                        for (String guid : currAppData.get(11).split(","))
                        {
                            if (DEBUG)
                            {
                                DEBUGGER.debug("guid: {}", guid);
                            }

                            Platform platform = new Platform();
                            platform.setPlatformGuid(guid);

                            if (DEBUG)
                            {
                                DEBUGGER.debug("Platform: {}", platform);
                            }

                            appPlatforms.add(platform);
                        }

                        if (DEBUG)
                        {
                            DEBUGGER.debug("List<Platform>: {}", appPlatforms);
                        }

                        Project currentProject = new Project();
                        currentProject.setProjectGuid(currAppData.get(9));

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Project: {}", currentProject);
                        }

                        Application currentApp = new Application();
                        currentApp.setApplicationGuid(currAppData.get(0));
                        currentApp.setApplicationName(currAppData.get(1));
                        currentApp.setApplicationVersion(currAppData.get(2));
                        currentApp.setBasePath(currAppData.get(3));
                        currentApp.setScmPath(currAppData.get(4));
                        currentApp.setApplicationCluster(currAppData.get(5));
                        currentApp.setJvmName(currAppData.get(6));
                        currentApp.setApplicationInstallPath(currAppData.get(7));
                        currentApp.setApplicationLogsPath(currAppData.get(8));
                        currentApp.setPidDirectory(currAppData.get(9));
                        currentApp.setApplicationProject(currentProject);
                        currentApp.setApplicationPlatforms(appPlatforms);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Application: {}", currentApp);
                        }
                    }
                    else
                    {
                        throw new ApplicationManagementException("No platform was located for the provided application. Cannot continue.");
                    }
                }
                else
                {
                    response.setRequestStatus(CoreServicesStatus.UNAUTHORIZED);
                    response.setResponse("The requesting user was NOT authorized to perform the operation");
                }
            }
            catch (SQLException sqx)
            {
                ERROR_RECORDER.error(sqx.getMessage(), sqx);
    
                throw new ApplicationManagementException(sqx.getMessage(), sqx);
            }
            catch (UserControlServiceException ucsx)
            {
                ERROR_RECORDER.error(ucsx.getMessage(), ucsx);
                
                throw new ApplicationManagementException(ucsx.getMessage(), ucsx);
            }
            catch (AdminControlServiceException acsx)
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
        }
        else
        {
            throw new ApplicationManagementException("No audit host info was provided. Cannot continue");
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

        if (reqInfo != null)
        {
            try
            {
                // this is an administrative function and requires admin level
                boolean isAdminAuthorized = adminControl.adminControlService(userAccount);

                if (DEBUG)
                {
                    DEBUGGER.debug("isAdminAuthorized: {}", isAdminAuthorized);
                }

                // it also requires authorization for the service
                boolean isUserAuthorized = userControl.isUserAuthorizedForService(userAccount, request.getServiceId());

                if (DEBUG)
                {
                    DEBUGGER.debug("isUserAuthorized: {}", isUserAuthorized);
                }

                if ((isAdminAuthorized) && (isUserAuthorized))
                {
                    boolean isComplete = appDAO.deleteApplication(application.getApplicationGuid());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("isComplete: {}", isComplete);
                    }

                    if (isComplete)
                    {
                        response.setRequestStatus(CoreServicesStatus.SUCCESS);
                        response.setResponse("Successfully deactivated application " + application.getApplicationName());
                    }
                    else
                    {
                        response.setRequestStatus(CoreServicesStatus.FAILURE);
                        response.setResponse("Failed to deactivate application " + application.getApplicationName());
                    }
                }
                else
                {
                    response.setRequestStatus(CoreServicesStatus.UNAUTHORIZED);
                    response.setResponse("The requesting user was NOT authorized to perform the operation");
                }
            }
            catch (SQLException sqx)
            {
                ERROR_RECORDER.error(sqx.getMessage(), sqx);
    
                throw new ApplicationManagementException(sqx.getMessage(), sqx);
            }
            catch (UserControlServiceException ucsx)
            {
                ERROR_RECORDER.error(ucsx.getMessage(), ucsx);
                
                throw new ApplicationManagementException(ucsx.getMessage(), ucsx);
            }
            catch (AdminControlServiceException acsx)
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
        }
        else
        {
            throw new ApplicationManagementException("No audit host info was provided. Cannot continue");
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

        if (reqInfo != null)
        {
            try
            {
                // it also requires authorization for the service
                boolean isUserAuthorized = userControl.isUserAuthorizedForService(userAccount, request.getServiceId());

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
                            // we're getting a full list here, and then we'll pull out as necessary
                            boolean isUserAuthorizedForProject = userControl.isUserAuthorizedForProject(userAccount, array[2]); // T2.PROJECT_GUID

                            if (DEBUG)
                            {
                                DEBUGGER.debug("isUserAuthorizedForProject: {}", isUserAuthorizedForProject);
                            }

                            if (isUserAuthorizedForProject)
                            {
                                Project project = new Project();
                                project.setProjectGuid(array[2]);
                                project.setProjectName(array[3]);

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("Project: {}", project);
                                }

                                Application app = new Application();
                                app.setApplicationGuid(array[0]); // T1.APPLICATION_GUID
                                app.setApplicationName(array[1]); // T1.APPLICATION_NAME
                                app.setApplicationProject(project);

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
                        }

                        if (DEBUG)
                        {
                            DEBUGGER.debug("applicationList: {}", appList);
                        }

                        response.setApplicationList(appList);
                        response.setResponse("Successfully loaded application list");
                        response.setRequestStatus(CoreServicesStatus.SUCCESS);
                    }
                    else
                    {
                        // no data
                        response.setResponse("No applications were located for the provided information");
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
                    response.setResponse("The requesting user was NOT authorized to perform the operation");
                }
            }
            catch (SQLException sqx)
            {
                ERROR_RECORDER.error(sqx.getMessage(), sqx);

                throw new ApplicationManagementException(sqx.getMessage(), sqx);
            }
            catch (UserControlServiceException ucsx)
            {
                ERROR_RECORDER.error(ucsx.getMessage(), ucsx);

                throw new ApplicationManagementException(ucsx.getMessage(), ucsx);
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
        }
        else
        {
            throw new ApplicationManagementException("No audit host info was provided. Cannot continue");
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

        if (reqInfo != null)
        {
            try
            {
                // it also requires authorization for the service
                boolean isUserAuthorized = userControl.isUserAuthorizedForService(userAccount, request.getServiceId());

                if (DEBUG)
                {
                    DEBUGGER.debug("isUserAuthorized: {}", isUserAuthorized);
                }

                if (isUserAuthorized)
                {
                    List<Platform> appPlatforms = null;
                    List<String> appData = appDAO.getApplicationData(application.getApplicationGuid());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("appData: {}", appData);
                    }

                    if ((appData != null) && (appData.size() != 0))
                    {
                        Project appProject = new Project();
                        appProject.setProjectGuid(appData.get(11)); // T2.PROJECT_GUID
                        appProject.setProjectName(appData.get(12)); // T2.PROJECT_NAME

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Project: {}", appProject);
                        }

                        if (StringUtils.split(appData.get(10), ",").length >= 1) // T1.PLATFORM_GUID
                        {
                            if (DEBUG)
                            {
                                DEBUGGER.debug("platformList: {}", StringUtils.split(appData.get(10), ","));
                            }

                            String tmp = StringUtils.remove(appData.get(10), "[");
                            String platformList = StringUtils.remove(tmp, "]");

                            if (DEBUG)
                            {
                                DEBUGGER.debug("platformList: {}", platformList);
                            }

                            appPlatforms = new ArrayList<>();

                            for (String platformGuid : platformList.split(","))
                            {
                                System.out.println(platformGuid);
                                List<Object> platformData = platformDao.getPlatformData(StringUtils.trim(platformGuid));

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("platformData: {}", platformData);
                                }

                                if ((platformData != null) && (platformData.size() != 0))
                                {
                                    Platform platform = new Platform();
                                    platform.setPlatformGuid((String) platformData.get(0)); // T1.PLATFORM_GUID
                                    platform.setPlatformName((String) platformData.get(1)); // T1.PLATFORM_NAME

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("Platform: {}", platform);
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
                                DEBUGGER.debug("List<Platform>: {}", appPlatforms);
                            }
                        }

                        // then put it all together
                        Application resApplication = new Application();
                        resApplication.setApplicationPlatforms(appPlatforms);
                        resApplication.setApplicationProject(appProject);
                        resApplication.setApplicationGuid(appData.get(0)); // T1.APPLICATION_GUID
                        resApplication.setApplicationName(appData.get(1)); // T1.APPLICATION_NAME
                        resApplication.setApplicationVersion(appData.get(2)); // T1.APPLICATION_VERSION
                        resApplication.setBasePath(appData.get(3)); // T1.BASE_PATH
                        resApplication.setScmPath(appData.get(4)); // T1.SCM_PATH
                        resApplication.setApplicationCluster(appData.get(5)); // T1.CLUSTER_NAME
                        resApplication.setJvmName(appData.get(6)); // T1.JVM_NAME
                        resApplication.setApplicationInstallPath(appData.get(7)); // T1.INSTALL_PATH
                        resApplication.setApplicationLogsPath(appData.get(8)); // T1.LOGS_DIRECTORY
                        resApplication.setPidDirectory(appData.get(9)); // T1.PID_DIRECTORY

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Application: {}", resApplication);
                        }

                        response.setApplication(resApplication);
                        response.setResponse("Successfully loaded application");
                        response.setRequestStatus(CoreServicesStatus.SUCCESS);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("ApplicationManagementResponse: {}", response);
                        }
                    }
                    else
                    {
                        throw new ApplicationManagementException("No applications were located for the provided data.");
                    }
                }
                else
                {
                    response.setRequestStatus(CoreServicesStatus.UNAUTHORIZED);
                    response.setResponse("The requesting user was NOT authorized to perform the operation");
                }
            }
            catch (SQLException sqx)
            {
                ERROR_RECORDER.error(sqx.getMessage(), sqx);
    
                throw new ApplicationManagementException(sqx.getMessage(), sqx);
            }
            catch (UserControlServiceException ucsx)
            {
                ERROR_RECORDER.error(ucsx.getMessage(), ucsx);
                
                throw new ApplicationManagementException(ucsx.getMessage(), ucsx);
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
        }
        else
        {
            throw new ApplicationManagementException("No audit host info was provided. Cannot continue");
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

        if (reqInfo != null)
        {
            try
            {
                // it also requires authorization for the service
                boolean isUserAuthorized = userControl.isUserAuthorizedForService(userAccount, request.getServiceId());

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
                    boolean isAuthorizedForRequest = userControl.isUserAuthorizedForService(userAccount, request.getServiceId());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("isAuthorizedForRequest: {}", isAuthorizedForRequest);
                    }

                    if (isAuthorizedForRequest)
                    {
                        List<String> appData = appDAO.getApplicationData(application.getApplicationGuid());

                        if (DEBUG)
                        {
                            DEBUGGER.debug("appData: {}", appData);
                        }

                        if ((appData != null) && (appData.size() != 0))
                        {
                            Application resApplication = new Application();
                            resApplication.setApplicationGuid(appData.get(0));
                            resApplication.setApplicationName(appData.get(1));
                            resApplication.setApplicationVersion(appData.get(2));
                            resApplication.setBasePath(appData.get(3));
                            resApplication.setScmPath(appData.get(4));
                            resApplication.setApplicationCluster(appData.get(5));
                            resApplication.setJvmName(appData.get(6));
                            resApplication.setApplicationInstallPath(appData.get(7));
                            resApplication.setApplicationLogsPath(appData.get(8));
                            resApplication.setPidDirectory(appData.get(9));

                            if (DEBUG)
                            {
                                DEBUGGER.debug("Application: {}", resApplication);
                            }

                            FileManagerRequest fileRequest = new FileManagerRequest();

                            if (StringUtils.isEmpty(request.getRequestFile()))
                            {
                                fileRequest.setRequestFile(appData.get(3)); // TODO: this should be the root dir
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

                            switch (agentConfig.getListenerType())
                            {
                                case MQ:
                                    String correlator = MQUtils.sendMqMessage(agentConfig.getConnectionName(), agentConfig.getRequestQueue(), agentRequest);

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("correlator: {}", correlator);
                                    }

                                    if (StringUtils.isNotEmpty(correlator))
                                    {
                                        agentResponse = (AgentResponse) MQUtils.getMqMessage(agentConfig.getConnectionName(), agentConfig.getResponseQueue(), correlator);
                                    }
                                    else
                                    {
                                        response.setResponse("Failed to send message to configured request queue for action");
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

                                    response.setApplication(resApplication);
                                    response.setCurrentPath(request.getRequestFile());
                                    response.setResponse("Successfully loaded file data");
                                    response.setRequestStatus(CoreServicesStatus.SUCCESS);
                                }
                                else
                                {
                                    response.setApplication(resApplication);
                                    response.setResponse("An error occurred while processing the remote request.");
                                    response.setRequestStatus(CoreServicesStatus.FAILURE);
                                }
                            }
                            else
                            {
                                response.setApplication(resApplication);
                                response.setResponse(agentResponse.getResponse());
                                response.setRequestStatus(CoreServicesStatus.FAILURE);
                            }
                        }
                        else
                        {
                            throw new ApplicationManagementException("No application data was located and no target was found on the request. Cannot continue.");
                        }
                    }
                    else
                    {
                        response.setRequestStatus(CoreServicesStatus.UNAUTHORIZED);
                        response.setResponse("The requesting user was NOT authorized to perform the operation");
                    }
                }
                else
                {
                    response.setRequestStatus(CoreServicesStatus.UNAUTHORIZED);
                    response.setResponse("The requesting user was NOT authorized to perform the operation");
                }
            }
            catch (UserControlServiceException ucsx)
            {
                ERROR_RECORDER.error(ucsx.getMessage(), ucsx);

                throw new ApplicationManagementException(ucsx.getMessage(), ucsx);
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
        }
        else
        {
            throw new ApplicationManagementException("No audit host info was provided. Cannot continue");
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

        if (reqInfo != null)
        {
            try
            {
                // it also requires authorization for the service
                boolean isUserAuthorized = userControl.isUserAuthorizedForService(userAccount, request.getServiceId());

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
                    response.setResponse("The requesting user was NOT authorized to perform the operation");
                }
            }
            catch (UserControlServiceException ucsx)
            {
                ERROR_RECORDER.error(ucsx.getMessage(), ucsx);

                throw new ApplicationManagementException(ucsx.getMessage(), ucsx);
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
        }
        else
        {
            throw new ApplicationManagementException("No audit host info was provided. Cannot continue");
        }
        
        return response;
    }
}
