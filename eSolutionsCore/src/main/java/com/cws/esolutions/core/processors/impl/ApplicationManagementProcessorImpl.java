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

import java.util.List;
import java.util.UUID;
import java.util.Arrays;
import java.util.ArrayList;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;

import com.cws.esolutions.core.utils.MQUtils;
import com.cws.esolutions.security.enums.Role;
import com.cws.esolutions.agent.dto.AgentRequest;
import com.cws.esolutions.agent.dto.AgentResponse;
import com.cws.esolutions.agent.enums.AgentStatus;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.core.processors.dto.Server;
import com.cws.esolutions.core.processors.dto.Project;
import com.cws.esolutions.core.processors.dto.Platform;
import com.cws.esolutions.core.processors.dto.Application;
import com.cws.esolutions.core.processors.enums.NetworkPartition;
import com.cws.esolutions.core.processors.enums.ServerType;
import com.cws.esolutions.core.processors.enums.ServerStatus;
import com.cws.esolutions.core.processors.enums.ServiceStatus;
import com.cws.esolutions.core.processors.enums.ServiceRegion;
import com.cws.esolutions.core.utils.exception.UtilityException;
import com.cws.esolutions.agent.processors.dto.FileManagerRequest;
import com.cws.esolutions.agent.processors.dto.FileManagerResponse;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.core.dao.processors.impl.ProjectDataDAOImpl;
import com.cws.esolutions.core.dao.processors.interfaces.IProjectDataDAO;
import com.cws.esolutions.core.processors.dto.ApplicationManagementRequest;
import com.cws.esolutions.core.processors.dto.ApplicationManagementResponse;
import com.cws.esolutions.core.processors.exception.ApplicationManagementException;
import com.cws.esolutions.security.dao.reference.impl.UserServiceInformationDAOImpl;
import com.cws.esolutions.core.processors.interfaces.IApplicationManagementProcessor;
import com.cws.esolutions.security.dao.reference.interfaces.IUserServiceInformationDAO;
import com.cws.esolutions.security.access.control.exception.UserControlServiceException;
import com.cws.esolutions.security.access.control.exception.AdminControlServiceException;
import com.cws.esolutions.security.audit.dto.AuditEntry;
import com.cws.esolutions.security.audit.dto.AuditRequest;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.security.audit.enums.AuditType;
import com.cws.esolutions.security.audit.exception.AuditServiceException;
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
public class ApplicationManagementProcessorImpl implements IApplicationManagementProcessor
{
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
                boolean isUserAuthorized = userControl.isUserAuthorizedForService(userAccount.getGuid(), request.getServiceId());

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
                        // don't do anything with it
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
                            List<String> platforms = new ArrayList<String>();

                            for (Platform targetPlatform : application.getApplicationPlatforms())
                            {
                                if (DEBUG)
                                {
                                    DEBUGGER.debug("Platform: {}", targetPlatform);
                                }

                                // make sure its a valid platform
                                List<String> isPlatformValid = platformDao.getPlatformData(targetPlatform.getPlatformGuid());

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("isPlatformValid: {}", isPlatformValid);
                                }

                                if ((isPlatformValid.size() == 0) || (isPlatformValid == null))
                                {
                                    throw new ApplicationManagementException("Provided platform does not exist in the asset datasource. Cannot add application.");
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

                            if ((isProjectValid.size() == 0) || (isProjectValid == null))
                            {
                                throw new ApplicationManagementException("Provided project does not exist in the asset datasource. Cannot add application.");
                            }

                            // ok, good platform. we can add the application in
                            List<String> appDataList = new ArrayList<String>(
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
                    throw new ApplicationManagementException("The requesting user was NOT authorized to perform the operation");
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
                boolean isUserAuthorized = userControl.isUserAuthorizedForService(userAccount.getGuid(), request.getServiceId());

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
                        List<Platform> appPlatforms = new ArrayList<Platform>();

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
                    throw new ApplicationManagementException("The requesting user was NOT authorized to perform the operation");
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
                boolean isUserAuthorized = userControl.isUserAuthorizedForService(userAccount.getGuid(), request.getServiceId());

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
                    throw new ApplicationManagementException("The requesting user was NOT authorized to perform the operation");
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
                boolean isUserAuthorized = userControl.isUserAuthorizedForService(userAccount.getGuid(), request.getServiceId());

                if (DEBUG)
                {
                    DEBUGGER.debug("isUserAuthorized: {}", isUserAuthorized);
                }

                if (isUserAuthorized)
                {
                    List<String> serviceList = new ArrayList<String>();
                    List<Application> applicationList = new ArrayList<Application>();

                    if (userAccount.getRole() == Role.SITEADMIN)
                    {
                        IProjectDataDAO projectDAO = new ProjectDataDAOImpl();
                        List<String[]> projects = projectDAO.listAvailableProjects(request.getStartPage());

                        if (DEBUG)
                        {
                            DEBUGGER.debug("projects: {}", projects);
                        }

                        if ((projects != null) && (projects.size() != 0))
                        {
                            for (String[] project : projects)
                            {
                                if (DEBUG)
                                {
                                    DEBUGGER.debug("project: {}", project[0]);
                                }

                                serviceList.add(project[0]);
                            }
                        }
                        else
                        {
                            throw new ApplicationManagementException("No installed projects were located. Cannot continue.");
                        }
                    }
                    else
                    {
                        // pull a list of projects the user has access to
                        IUserServiceInformationDAO serviceDAO = new UserServiceInformationDAOImpl();
                        serviceList = serviceDAO.returnUserAuthorizedProjects(userAccount.getGuid());
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("List<String>: {}", serviceList);
                    }

                    if ((serviceList != null) && (serviceList.size() != 0))
                    {
                        // ok, get all the associated applications with the project
                        for (String projectId : serviceList)
                        {
                            if (DEBUG)
                            {
                                DEBUGGER.debug("projectId: {}", projectId);
                            }

                            List<String> projectInfo = projectDAO.getProjectData(projectId);

                            if (DEBUG)
                            {
                                DEBUGGER.debug("projectInfo: {}", projectInfo);
                            }

                            if ((projectInfo != null) && (projectInfo.size() != 0))
                            {
                                Project project = new Project();
                                project.setProjectGuid(projectInfo.get(0));
                                project.setProjectCode(projectInfo.get(1));

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("Project: {}", project);
                                }

                                List<String[]> appData = appDAO.getApplicationsByAttribute(project.getProjectGuid(), request.getStartPage());

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("appData: {}", appData);
                                }

                                if ((appData != null) && (appData.size() != 0))
                                {
                                    List<Platform> platforms = new ArrayList<Platform>();

                                    for (String[] data : appData)
                                    {
                                        if (DEBUG)
                                        {
                                            DEBUGGER.debug("data: {}", data);
                                        }

                                        // build the platform
                                        if (StringUtils.split(data[11], ",").length >= 1)
                                        {
                                            if (DEBUG)
                                            {
                                                DEBUGGER.debug("platformList: {}", StringUtils.split(data[11], ","));
                                            }

                                            for (String platformGuid : data[11].split(","))
                                            {
                                                String guid = StringUtils.remove(platformGuid, "[");
                                                guid = StringUtils.remove(guid, "]");
                                                guid = StringUtils.trim(guid);

                                                if (DEBUG)
                                                {
                                                    DEBUGGER.debug("guid: {}", guid);
                                                }

                                                List<String> platformData = platformDao.getPlatformData(guid);

                                                if (DEBUG)
                                                {
                                                    DEBUGGER.debug("platformData: {}", platformData);
                                                }

                                                if ((platformData != null) && (platformData.size() != 0))
                                                {
                                                    Platform platform = new Platform();
                                                    platform.setPlatformGuid(platformData.get(0));
                                                    platform.setPlatformName(platformData.get(1));

                                                    if (DEBUG)
                                                    {
                                                        DEBUGGER.debug("Platform: {}", platform);
                                                    }

                                                    platforms.add(platform);
                                                }
                                                else
                                                {
                                                    throw new ApplicationManagementException("No platform data was located for the associated application. Cannot continue.");
                                                }

                                                if (DEBUG)
                                                {
                                                    DEBUGGER.debug("platforms: {}", platforms);
                                                }
                                            }
                                        }
                                        else
                                        {
                                            throw new ApplicationManagementException("No platform data was located for the associated application. Cannot continue.");
                                        }

                                        Application resApplication = new Application();
                                        resApplication.setApplicationGuid(data[0]);
                                        resApplication.setApplicationName(data[1]);
                                        resApplication.setApplicationVersion(data[2]);
                                        resApplication.setBasePath(data[3]);
                                        resApplication.setScmPath(data[4]);
                                        resApplication.setApplicationCluster(data[5]);
                                        resApplication.setJvmName(data[6]);
                                        resApplication.setApplicationInstallPath(data[7]);
                                        resApplication.setApplicationLogsPath(data[8]);
                                        resApplication.setPidDirectory(data[9]);
                                        resApplication.setApplicationProject(project);
                                        resApplication.setApplicationPlatforms(platforms);

                                        if (DEBUG)
                                        {
                                            DEBUGGER.debug("Application: {}", resApplication);
                                        }

                                        applicationList.add(resApplication);
                                    }
                                }
                                else
                                {
                                    // moo
                                    continue;
                                }
                            }
                            else
                            {
                                throw new ApplicationManagementException("No project information was located. Cannot continue.");
                            }
                        }
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("applicationList: {}", applicationList);
                    }

                    if ((applicationList != null) && (applicationList.size() != 0))
                    {
                        response.setApplicationList(applicationList);
                        response.setResponse("Successfully loaded application list");
                        response.setRequestStatus(CoreServicesStatus.SUCCESS);
                    }
                    else
                    {
                        // no applications
                        response.setResponse("No applications were located for the provided information");
                        response.setRequestStatus(CoreServicesStatus.FAILURE);
                    }
                }
                else
                {
                    throw new ApplicationManagementException("The requesting user was NOT authorized to perform the operation");
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

    @Override
    public ApplicationManagementResponse listApplicationsByProject(final ApplicationManagementRequest request) throws ApplicationManagementException
    {
        final String methodName = IApplicationManagementProcessor.CNAME + "#listApplicationsByProject(final ApplicationManagementRequest request) throws ApplicationManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ApplicationManagementRequest: {}", request);
        }

        ApplicationManagementResponse response = new ApplicationManagementResponse();

        final Application application = request.getApplication();
        final Project project = application.getApplicationProject();
        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("Application: {}", application);
            DEBUGGER.debug("Project: {}", project);
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
                boolean isUserAuthorized = userControl.isUserAuthorizedForService(userAccount.getGuid(), request.getServiceId());

                if (DEBUG)
                {
                    DEBUGGER.debug("isUserAuthorized: {}", isUserAuthorized);
                }

                if ((isAdminAuthorized) && (isUserAuthorized))
                {
                    List<Application> applicationList = new ArrayList<Application>();
                    List<String[]> appData = appDAO.getApplicationsByAttribute(project.getProjectGuid(), request.getStartPage());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("appData: {}", appData);
                    }

                    if ((appData != null) && (appData.size() != 0))
                    {
                        List<Platform> platforms = new ArrayList<Platform>();

                        for (String[] data : appData)
                        {
                            if (DEBUG)
                            {
                                DEBUGGER.debug("data: {}", data);
                            }

                            // build the platform
                            String[] platformList = StringUtils.split(data[11], ",");

                            if (DEBUG)
                            {
                                DEBUGGER.debug("platformList: {}", platformList);
                            }

                            if ((platformList != null) && (platformList.length != 0))
                            {
                                for (String guid : platformList)
                                {
                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("guid: {}", guid);
                                    }

                                    List<String> platformData = platformDao.getPlatformData(guid);

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("platformData: {}", platformData);
                                    }

                                    if ((platformData != null) && (platformData.size() != 0))
                                    {
                                        Platform platform = new Platform();
                                        platform.setPlatformGuid(platformData.get(0));
                                        platform.setPlatformName(platformData.get(1));

                                        if (DEBUG)
                                        {
                                            DEBUGGER.debug("Platform: {}", platform);
                                        }

                                        platforms.add(platform);
                                    }
                                    else
                                    {
                                        throw new ApplicationManagementException("No platform data was located for the associated application. Cannot continue.");
                                    }

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("platforms: {}", platforms);
                                    }
                                }
                            }
                            else
                            {
                                throw new ApplicationManagementException("No platform data was located for the associated application. Cannot continue.");
                            }

                            Application resApplication = new Application();
                            resApplication.setApplicationGuid(data[0]);
                            resApplication.setApplicationName(data[1]);
                            resApplication.setApplicationVersion(data[2]);
                            resApplication.setBasePath(data[3]);
                            resApplication.setScmPath(data[4]);
                            resApplication.setApplicationCluster(data[5]);
                            resApplication.setJvmName(data[6]);
                            resApplication.setApplicationInstallPath(data[7]);
                            resApplication.setApplicationLogsPath(data[8]);
                            resApplication.setPidDirectory(data[9]);
                            resApplication.setApplicationProject(project);
                            resApplication.setApplicationPlatforms(platforms);

                            if (DEBUG)
                            {
                                DEBUGGER.debug("Application: {}", resApplication);
                            }

                            applicationList.add(resApplication);
                        }

                        if (DEBUG)
                        {
                            DEBUGGER.debug("applicationList: {}", applicationList);
                        }

                        response.setApplicationList(applicationList);
                        response.setRequestStatus(CoreServicesStatus.SUCCESS);
                        response.setResponse("Successfully loaded application listing.");

                        if (DEBUG)
                        {
                            DEBUGGER.debug("ApplicationManagementResponse: {}", response);
                        }
                    }
                    else
                    {
                        throw new ApplicationManagementException("No applications were located with the provided project. Cannot continue.");
                    }
                }
                else
                {
                    throw new ApplicationManagementException("The requesting user was NOT authorized to perform the operation");
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
                boolean isUserAuthorized = userControl.isUserAuthorizedForService(userAccount.getGuid(), request.getServiceId());

                if (DEBUG)
                {
                    DEBUGGER.debug("isUserAuthorized: {}", isUserAuthorized);
                }

                if (isUserAuthorized)
                {
                    List<String> appData = appDAO.getApplicationData(application.getApplicationGuid());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("appData: {}", appData);
                    }

                    if ((appData != null) && (appData.size() != 0))
                    {
                        Application resApplication = new Application();

                        if (StringUtils.split(appData.get(11), ",").length >= 1)
                        {
                            if (DEBUG)
                            {
                                DEBUGGER.debug("platformList: {}", StringUtils.split(appData.get(11), ","));
                            }

                            List<Platform> platformList = new ArrayList<Platform>();

                            for (String platformGuid : appData.get(11).split(","))
                            {
                                String guid = StringUtils.remove(platformGuid, "[");
                                guid = StringUtils.remove(guid, "]");
                                guid = StringUtils.trim(guid);

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("guid: {}", guid);
                                }

                                List<String> platformData = platformDao.getPlatformData(guid);

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("platformData: {}", platformData);
                                }

                                if ((platformData != null) && (platformData.size() != 0))
                                {
                                    Platform platform = new Platform();
                                    platform.setPlatformGuid(platformData.get(0));
                                    platform.setPlatformName(platformData.get(1));
                                    platform.setPlatformRegion(ServiceRegion.valueOf(platformData.get(2)));
                                    platform.setDescription(platformData.get(6));

                                    // load info
                                    // get the dmgr
                                    List<Object> dmgrData = serverDao.getInstalledServer(platformData.get(3));

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("dmgrData: {}", dmgrData);
                                    }

                                    if ((dmgrData != null) && (dmgrData.size() != 0))
                                    {
                                        Server dmgrServer = new Server();
                                        dmgrServer.setServerGuid((String) dmgrData.get(0)); // SYSTEM_GUID
                                        dmgrServer.setOsName((String) dmgrData.get(1)); // SYSTEM_OSTYPE
                                        dmgrServer.setServerStatus(ServerStatus.valueOf((String) dmgrData.get(2))); // SYSTEM_STATUS
                                        dmgrServer.setServerRegion(ServiceRegion.valueOf((String) dmgrData.get(3))); // SYSTEM_REGION
                                        dmgrServer.setNetworkPartition(NetworkPartition.valueOf((String) dmgrData.get(4))); // NETWORK_PARTITION
                                        dmgrServer.setServerType(ServerType.valueOf((String) dmgrData.get(6))); // SYSTEM_TYPE
                                        dmgrServer.setDomainName((String) dmgrData.get(7)); // DOMAIN_NAME
                                        dmgrServer.setCpuType((String) dmgrData.get(8)); // CPU_TYPE
                                        dmgrServer.setCpuCount((Integer) dmgrData.get(9)); // CPU_COUNT
                                        dmgrServer.setServerRack((String) dmgrData.get(10)); // SERVER_RACK
                                        dmgrServer.setRackPosition((String) dmgrData.get(11)); // RACK_POSITION
                                        dmgrServer.setServerModel((String) dmgrData.get(12)); // SERVER_MODEL
                                        dmgrServer.setSerialNumber((String) dmgrData.get(13)); // SERIAL_NUMBER
                                        dmgrServer.setInstalledMemory((Integer) dmgrData.get(14)); // INSTALLED_MEMORY
                                        dmgrServer.setOperIpAddress((String) dmgrData.get(15)); // OPER_IP
                                        dmgrServer.setOperHostName((String) dmgrData.get(16)); // OPER_HOSTNAME
                                        dmgrServer.setMgmtIpAddress((String) dmgrData.get(17)); // MGMT_IP
                                        dmgrServer.setMgmtHostName((String) dmgrData.get(18)); // MGMT_HOSTNAME
                                        dmgrServer.setBkIpAddress((String) dmgrData.get(19)); // BKUP_IP
                                        dmgrServer.setBkHostName((String) dmgrData.get(20)); // BKUP_HOSTNAME
                                        dmgrServer.setNasIpAddress((String) dmgrData.get(21)); // NAS_IP
                                        dmgrServer.setNasHostName((String) dmgrData.get(22)); // NAS_HOSTNAME
                                        dmgrServer.setNatAddress((String) dmgrData.get(23)); // NAT_ADDR
                                        dmgrServer.setServerComments((String) dmgrData.get(24)); // COMMENTS
                                        dmgrServer.setAssignedEngineer((String) dmgrData.get(25)); // ASSIGNED_ENGINEER
                                        dmgrServer.setDmgrPort((Integer) dmgrData.get(28)); // DMGR_PORT
                                        dmgrServer.setOwningDmgr((String) dmgrData.get(29)); // OWNING_DMGR
                                        dmgrServer.setMgrUrl((String) dmgrData.get(30)); // MGR_ENTRY

                                        if (DEBUG)
                                        {
                                            DEBUGGER.debug("Server: {}", dmgrServer);
                                        }

                                        platform.setPlatformDmgr(dmgrServer);
                                    }

                                    if (platformData.get(3).split(",").length >= 1)
                                    {
                                        List<Server> appServerList = new ArrayList<Server>();

                                        // list application servers
                                        for (String serverGuid : platformData.get(4).split(","))
                                        {
                                            String appGuid = StringUtils.remove(serverGuid, "[");
                                            appGuid = StringUtils.remove(appGuid, "]");
                                            appGuid = StringUtils.trim(appGuid);

                                            if (DEBUG)
                                            {
                                                DEBUGGER.debug("serverGuid: {}", appGuid);
                                            }

                                            List<Object> appServerData = serverDao.getInstalledServer(appGuid);

                                            if (DEBUG)
                                            {
                                                DEBUGGER.debug("appServerData: {}", appServerData);
                                            }

                                            if ((appServerData != null) && (appServerData.size() != 0))
                                            {
                                                Server server = new Server();
                                                server.setServerGuid((String) appServerData.get(0)); // SYSTEM_GUID
                                                server.setOsName((String) appServerData.get(1)); // SYSTEM_OSTYPE
                                                server.setServerStatus(ServerStatus.valueOf((String) appServerData.get(2))); // SYSTEM_STATUS
                                                server.setServerRegion(ServiceRegion.valueOf((String) appServerData.get(3))); // SYSTEM_REGION
                                                server.setNetworkPartition(NetworkPartition.valueOf((String) appServerData.get(4))); // NETWORK_PARTITION
                                                server.setServerType(ServerType.valueOf((String) appServerData.get(6))); // SYSTEM_TYPE
                                                server.setDomainName((String) appServerData.get(7)); // DOMAIN_NAME
                                                server.setCpuType((String) appServerData.get(8)); // CPU_TYPE
                                                server.setCpuCount((Integer) appServerData.get(9)); // CPU_COUNT
                                                server.setServerRack((String) appServerData.get(10)); // SERVER_RACK
                                                server.setRackPosition((String) appServerData.get(11)); // RACK_POSITION
                                                server.setServerModel((String) appServerData.get(12)); // SERVER_MODEL
                                                server.setSerialNumber((String) appServerData.get(13)); // SERIAL_NUMBER
                                                server.setInstalledMemory((Integer) appServerData.get(14)); // INSTALLED_MEMORY
                                                server.setOperIpAddress((String) appServerData.get(15)); // OPER_IP
                                                server.setOperHostName((String) appServerData.get(16)); // OPER_HOSTNAME
                                                server.setMgmtIpAddress((String) appServerData.get(17)); // MGMT_IP
                                                server.setMgmtHostName((String) appServerData.get(18)); // MGMT_HOSTNAME
                                                server.setBkIpAddress((String) appServerData.get(19)); // BKUP_IP
                                                server.setBkHostName((String) appServerData.get(20)); // BKUP_HOSTNAME
                                                server.setNasIpAddress((String) appServerData.get(21)); // NAS_IP
                                                server.setNasHostName((String) appServerData.get(22)); // NAS_HOSTNAME
                                                server.setNatAddress((String) appServerData.get(23)); // NAT_ADDR
                                                server.setServerComments((String) appServerData.get(24)); // COMMENTS
                                                server.setAssignedEngineer((String) appServerData.get(25)); // ASSIGNED_ENGINEER
                                                server.setDmgrPort((Integer) appServerData.get(28)); // DMGR_PORT
                                                server.setOwningDmgr((String) appServerData.get(29)); // OWNING_DMGR
                                                server.setMgrUrl((String) appServerData.get(30)); // MGR_ENTRY

                                                if (DEBUG)
                                                {
                                                    DEBUGGER.debug("Server: {}", server);
                                                }

                                                appServerList.add(server);
                                            }
                                        }

                                        if (DEBUG)
                                        {
                                            DEBUGGER.debug("appServerList: {}", appServerList);
                                        }

                                        platform.setAppServers(appServerList);
                                    }

                                    // list web servers
                                    if (platformData.get(4).split(",").length >= 1)
                                    {
                                        List<Server> webServerList = new ArrayList<Server>();

                                        for (String serverGuid : platformData.get(5).split(","))
                                        {
                                            String webGuid = StringUtils.remove(serverGuid, "[");
                                            webGuid = StringUtils.remove(webGuid, "]");
                                            webGuid = StringUtils.trim(webGuid);

                                            if (DEBUG)
                                            {
                                                DEBUGGER.debug("serverGuid: {}", webGuid);
                                            }

                                            List<Object> webServerData = serverDao.getInstalledServer(webGuid);

                                            if (DEBUG)
                                            {
                                                DEBUGGER.debug("webServerData: {}", webServerData);
                                            }

                                            if ((webServerData != null) && (webServerData.size() != 0))
                                            {
                                                Server server = new Server();
                                                server.setServerGuid((String) webServerData.get(0)); // SYSTEM_GUID
                                                server.setOsName((String) webServerData.get(1)); // SYSTEM_OSTYPE
                                                server.setServerStatus(ServerStatus.valueOf((String) webServerData.get(2))); // SYSTEM_STATUS
                                                server.setServerRegion(ServiceRegion.valueOf((String) webServerData.get(3))); // SYSTEM_REGION
                                                server.setNetworkPartition(NetworkPartition.valueOf((String) webServerData.get(4))); // NETWORK_PARTITION
                                                server.setServerType(ServerType.valueOf((String) webServerData.get(6))); // SYSTEM_TYPE
                                                server.setDomainName((String) webServerData.get(7)); // DOMAIN_NAME
                                                server.setCpuType((String) webServerData.get(8)); // CPU_TYPE
                                                server.setCpuCount((Integer) webServerData.get(9)); // CPU_COUNT
                                                server.setServerRack((String) webServerData.get(10)); // SERVER_RACK
                                                server.setRackPosition((String) webServerData.get(11)); // RACK_POSITION
                                                server.setServerModel((String) webServerData.get(12)); // SERVER_MODEL
                                                server.setSerialNumber((String) webServerData.get(13)); // SERIAL_NUMBER
                                                server.setInstalledMemory((Integer) webServerData.get(14)); // INSTALLED_MEMORY
                                                server.setOperIpAddress((String) webServerData.get(15)); // OPER_IP
                                                server.setOperHostName((String) webServerData.get(16)); // OPER_HOSTNAME
                                                server.setMgmtIpAddress((String) webServerData.get(17)); // MGMT_IP
                                                server.setMgmtHostName((String) webServerData.get(18)); // MGMT_HOSTNAME
                                                server.setBkIpAddress((String) webServerData.get(19)); // BKUP_IP
                                                server.setBkHostName((String) webServerData.get(20)); // BKUP_HOSTNAME
                                                server.setNasIpAddress((String) webServerData.get(21)); // NAS_IP
                                                server.setNasHostName((String) webServerData.get(22)); // NAS_HOSTNAME
                                                server.setNatAddress((String) webServerData.get(23)); // NAT_ADDR
                                                server.setServerComments((String) webServerData.get(24)); // COMMENTS
                                                server.setAssignedEngineer((String) webServerData.get(25)); // ASSIGNED_ENGINEER
                                                server.setDmgrPort((Integer) webServerData.get(28)); // DMGR_PORT
                                                server.setOwningDmgr((String) webServerData.get(29)); // OWNING_DMGR
                                                server.setMgrUrl((String) webServerData.get(30)); // MGR_ENTRY

                                                if (DEBUG)
                                                {
                                                    DEBUGGER.debug("Server: {}", server);
                                                }

                                                webServerList.add(server);
                                            }
                                        }

                                        if (DEBUG)
                                        {
                                            DEBUGGER.debug("webServerList: {}", webServerList);
                                        }

                                        platform.setWebServers(webServerList);
                                    }

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("Platform: {}", platform);
                                    }

                                    platformList.add(platform);

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("platformList: {}", platformList);
                                    }
                                }
                                else
                                {
                                    throw new ApplicationManagementException("Unable to locate a valid platform for the provided application. Cannot continue.");
                                }
                            }

                            // then the project...
                            List<String> projectList = projectDAO.getProjectData(appData.get(10));

                            if (DEBUG)
                            {
                                DEBUGGER.debug("projectList: {}", projectList);
                            }

                            if ((projectList != null) && (projectList.size() != 0))
                            {
                                Project project = new Project();
                                project.setProjectGuid(projectList.get(0));
                                project.setProjectCode(projectList.get(1));
                                project.setProjectStatus(ServiceStatus.valueOf(projectList.get(2)));
                                project.setPrimaryContact(projectList.get(3));
                                project.setSecondaryContact(projectList.get(4));
                                project.setContactEmail(projectList.get(5));
                                project.setIncidentQueue(projectList.get(6));
                                project.setChangeQueue(projectList.get(7));

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("Project: {}", project);
                                }

                                resApplication.setApplicationProject(project);
                            }
                            else
                            {
                                throw new ApplicationManagementException("Unable to load associated application project. Cannot continue.");
                            }

                            // then put it all together
                            resApplication.setApplicationPlatforms(platformList);
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

                            response.setApplication(resApplication);
                            response.setResponse("Successfully loaded application");
                            response.setRequestStatus(CoreServicesStatus.SUCCESS);

                            if (DEBUG)
                            {
                                DEBUGGER.debug("ApplicationManagementResponse: {}", response);
                            }
                        }
                    }
                    else
                    {
                        throw new ApplicationManagementException("No applications were located for the provided data.");
                    }
                }
                else
                {
                    throw new ApplicationManagementException("The requesting user was NOT authorized to perform the operation");
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

    @Override
    public ApplicationManagementResponse applicationFileRequest(final ApplicationManagementRequest request) throws ApplicationManagementException
    {
        final String methodName = IApplicationManagementProcessor.CNAME + "#applicationFileRequest(final ApplicationManagementRequest request) throws ApplicationManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ApplicationManagementRequest: {}", request);
        }

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
                boolean isUserAuthorized = userControl.isUserAuthorizedForService(userAccount.getGuid(), request.getServiceId());

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
                    boolean isAuthorizedForRequest = userControl.isUserAuthorizedForService(userAccount.getGuid(), request.getServiceId());

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
                                response.setResponse("Failed to send message to configured request queue for action");
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
                        throw new UserControlServiceException("Requesting user is NOT authorized for project");
                    }
                }
                else
                {
                    throw new ApplicationManagementException("The requesting user was NOT authorized to perform the operation");
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
                boolean isUserAuthorized = userControl.isUserAuthorizedForService(userAccount.getGuid(), request.getServiceId());

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
                    throw new ApplicationManagementException("The requesting user was NOT authorized to perform the operation");
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
