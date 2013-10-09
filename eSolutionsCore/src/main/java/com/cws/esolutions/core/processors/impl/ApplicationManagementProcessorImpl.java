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

import java.io.File;
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
import com.cws.esolutions.core.processors.dto.Application;
import com.cws.esolutions.security.audit.dto.AuditRequest;
import com.cws.esolutions.core.processors.enums.ServerType;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.enums.ServerStatus;
import com.cws.esolutions.core.processors.enums.ServiceStatus;
import com.cws.esolutions.core.processors.enums.ServiceRegion;
import com.cws.esolutions.agent.processors.enums.DeploymentType;
import com.cws.esolutions.core.utils.exception.UtilityException;
import com.cws.esolutions.agent.processors.dto.FileManagerRequest;
import com.cws.esolutions.agent.processors.dto.FileManagerResponse;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.security.audit.exception.AuditServiceException;
import com.cws.esolutions.agent.processors.dto.ApplicationManagerRequest;
import com.cws.esolutions.agent.processors.enums.ApplicationManagementType;
import com.cws.esolutions.core.processors.dto.ApplicationManagementRequest;
import com.cws.esolutions.core.processors.dto.ApplicationManagementResponse;
import com.cws.esolutions.core.processors.exception.ApplicationManagementException;
import com.cws.esolutions.security.dao.reference.impl.UserServiceInformationDAOImpl;
import com.cws.esolutions.core.processors.interfaces.IApplicationManagementProcessor;
import com.cws.esolutions.security.dao.reference.interfaces.IUserServiceInformationDAO;
import com.cws.esolutions.security.access.control.exception.UserControlServiceException;
import com.cws.esolutions.security.access.control.exception.AdminControlServiceException;
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
                        if (application.getPlatform() != null)
                        {
                            Platform targetPlatform = application.getPlatform();

                            if (DEBUG)
                            {
                                DEBUGGER.debug("Platform: {}", targetPlatform);
                            }

                            // make sure its a valid platform
                            List<String> isPlatformValid = platformDao.getPlatformData(application.getPlatform().getPlatformGuid());

                            if (DEBUG)
                            {
                                DEBUGGER.debug("isPlatformValid: {}", isPlatformValid);
                            }

                            if ((isPlatformValid != null) && (isPlatformValid.size() != 0))
                            {
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

                                if ((isProjectValid != null) && (isProjectValid.size() != 0))
                                {
                                    // ok, good platform. we can add the application in
                                    List<String> appDataList = new ArrayList<String>(
                                            Arrays.asList(
                                                    applGuid,
                                                    application.getApplicationName(),
                                                    application.getApplicationVersion(),
                                                    application.getScmPath(),
                                                    application.getApplicationCluster(),
                                                    application.getJvmName(),
                                                    application.getApplicationInstallPath(),
                                                    application.getApplicationLogsPath(),
                                                    application.getPidDirectory(),
                                                    targetProject.getProjectGuid(),
                                                    targetPlatform.getPlatformGuid()));

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
                                    throw new ApplicationManagementException("Provided project does not exist in the asset datasource. Cannot add application.");
                                }
                            }
                            else
                            {
                                throw new ApplicationManagementException("Provided platform does not exist in the asset datasource. Cannot add application.");
                            }
                        }
                        else
                        {
                            throw new ApplicationManagementException("No platform was assigned to the application. Cannot continue.");
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
                    auditEntry.setReqInfo(reqInfo);
                    auditEntry.setUserAccount(userAccount);
                    auditEntry.setAuditType(AuditType.ADDAPP);
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

                    Platform currentPlatform = new Platform();
                    currentPlatform.setPlatformGuid(currAppData.get(10));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Platform: {}", currentPlatform);
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
                    currentApp.setScmPath(currAppData.get(3));
                    currentApp.setApplicationCluster(currAppData.get(4));
                    currentApp.setJvmName(currAppData.get(5));
                    currentApp.setApplicationInstallPath(currAppData.get(6));
                    currentApp.setApplicationLogsPath(currAppData.get(7));
                    currentApp.setPidDirectory(currAppData.get(8));
                    currentApp.setApplicationProject(currentProject);
                    currentApp.setPlatform(currentPlatform);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Application: {}", currentApp);
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
                    auditEntry.setReqInfo(reqInfo);
                    auditEntry.setUserAccount(userAccount);
                    auditEntry.setAuditType(AuditType.MODIFYAPP);
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
                    auditEntry.setReqInfo(reqInfo);
                    auditEntry.setUserAccount(userAccount);
                    auditEntry.setAuditType(AuditType.MODIFYAPP);
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
                    // pull a list of projects the user has access to
                    List<Application> applicationList = new ArrayList<Application>();
                    IUserServiceInformationDAO serviceDAO = new UserServiceInformationDAOImpl();
                    List<String> serviceList = serviceDAO.returnUserAuthorizedProjects(userAccount.getGuid());

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

                            List<String[]> appData = null;

                            try
                            {
                                appData = appDAO.getApplicationsByAttribute(projectId);
                            }
                            catch (SQLException sqx)
                            {
                                continue;
                            }

                            if (DEBUG)
                            {
                                DEBUGGER.debug("List<String[]>: {}", appData);
                            }

                            for (String[] data : appData)
                            {
                                if (DEBUG)
                                {
                                    if (data != null)
                                    {
                                        for (String str : data)
                                        {
                                            DEBUGGER.debug("data: {}", str);
                                        }
                                    }
                                }

                                Server dmgrServer = null;
                                List<String> serverData = null;
                                List<Server> appServerList = null;
                                List<Server> webServerList = null;
                                Application resApplication = new Application();

                                // get platform data
                                if ((data != null) && (data[10] != null))
                                {
                                    List<String> platformData = platformDao.getPlatformData(data[10]);

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("platformData: {}", platformData);
                                    }

                                    if ((platformData != null) && (platformData.size() != 0))
                                    {
                                        serverData = serverDao.getInstalledServer(platformData.get(3));

                                        if (DEBUG)
                                        {
                                            DEBUGGER.debug("serverData: {}", serverData);
                                        }

                                        if ((serverData != null) && (serverData.size() != 0))
                                        {
                                            dmgrServer = new Server();
                                            dmgrServer.setServerGuid(serverData.get(0));
                                            dmgrServer.setOsName(serverData.get(1));
                                            dmgrServer.setServerStatus(ServerStatus.valueOf(serverData.get(2)));
                                            dmgrServer.setServerRegion(ServiceRegion.valueOf(serverData.get(3)));
                                            dmgrServer.setServerType(ServerType.valueOf(serverData.get(4)));
                                            dmgrServer.setDomainName(serverData.get(5));
                                            dmgrServer.setCpuType(serverData.get(6));
                                            dmgrServer.setCpuCount(Integer.parseInt(serverData.get(7)));
                                            dmgrServer.setServerRack(serverData.get(8));
                                            dmgrServer.setRackPosition(serverData.get(9));
                                            dmgrServer.setServerModel(serverData.get(10));
                                            dmgrServer.setSerialNumber(serverData.get(11));
                                            dmgrServer.setInstalledMemory(Integer.parseInt(serverData.get(12)));
                                            dmgrServer.setOperIpAddress(serverData.get(13));
                                            dmgrServer.setOperHostName(serverData.get(14));
                                            dmgrServer.setAssignedEngineer(serverData.get(15));
                                            dmgrServer.setServerComments(serverData.get(16));
                                            dmgrServer.setMgmtIpAddress(serverData.get(17));
                                            dmgrServer.setMgmtHostName(serverData.get(18));
                                            dmgrServer.setBkIpAddress(serverData.get(19));
                                            dmgrServer.setBkHostName(serverData.get(20));
                                            dmgrServer.setNasIpAddress(serverData.get(21));
                                            dmgrServer.setNasHostName(serverData.get(22));
                                            dmgrServer.setNatAddress(serverData.get(23));
                                            dmgrServer.setMgrUrl(serverData.get(24));
                                            dmgrServer.setOwningDmgr(serverData.get(25));

                                            if (DEBUG)
                                            {
                                                DEBUGGER.debug("Server: {}", dmgrServer);
                                            }

                                            serverData.clear();
                                        }

                                        if (platformData.get(1).split(",").length >= 1)
                                        {
                                            appServerList = new ArrayList<Server>();

                                            // list application servers
                                            for (String serverGuid : platformData.get(4).split(","))
                                            {
                                                String guid = StringUtils.remove(serverGuid, "[");
                                                guid = StringUtils.remove(guid, "]");
                                                guid = StringUtils.trim(guid);

                                                if (DEBUG)
                                                {
                                                    DEBUGGER.debug("serverGuid: {}", guid);
                                                }

                                                serverData = serverDao.getInstalledServer(guid);

                                                if (DEBUG)
                                                {
                                                    DEBUGGER.debug("serverData: {}", serverData);
                                                }

                                                if ((serverData != null) && (serverData.size() != 0))
                                                {
                                                    Server server = new Server();
                                                    server.setServerGuid(serverData.get(0));
                                                    server.setOsName(serverData.get(1));
                                                    server.setServerStatus(ServerStatus.valueOf(serverData.get(2)));
                                                    server.setServerRegion(ServiceRegion.valueOf(serverData.get(3)));
                                                    server.setServerType(ServerType.valueOf(serverData.get(4)));
                                                    server.setDomainName(serverData.get(5));
                                                    server.setCpuType(serverData.get(6));
                                                    server.setCpuCount(Integer.parseInt(serverData.get(7)));
                                                    server.setServerRack(serverData.get(8));
                                                    server.setRackPosition(serverData.get(9));
                                                    server.setServerModel(serverData.get(10));
                                                    server.setSerialNumber(serverData.get(11));
                                                    server.setInstalledMemory(Integer.parseInt(serverData.get(12)));
                                                    server.setOperIpAddress(serverData.get(13));
                                                    server.setOperHostName(serverData.get(14));
                                                    server.setAssignedEngineer(serverData.get(15));
                                                    server.setServerComments(serverData.get(16));
                                                    server.setMgmtIpAddress(serverData.get(17));
                                                    server.setMgmtHostName(serverData.get(18));
                                                    server.setBkIpAddress(serverData.get(19));
                                                    server.setBkHostName(serverData.get(20));
                                                    server.setNasIpAddress(serverData.get(21));
                                                    server.setNasHostName(serverData.get(22));
                                                    server.setNatAddress(serverData.get(23));
                                                    server.setMgrUrl(serverData.get(24));
                                                    server.setOwningDmgr(serverData.get(25));

                                                    if (DEBUG)
                                                    {
                                                        DEBUGGER.debug("Server: {}", server);
                                                    }

                                                    appServerList.add(server);

                                                    serverData.clear();
                                                }
                                            }

                                            if (DEBUG)
                                            {
                                                DEBUGGER.debug("appServerList: {}", appServerList);
                                            }
                                        }

                                        // list web servers
                                        if (platformData.get(1).split(",").length >= 1)
                                        {
                                            webServerList = new ArrayList<Server>();

                                            for (String serverGuid : platformData.get(5).split(","))
                                            {
                                                String guid = StringUtils.remove(serverGuid, "[");
                                                guid = StringUtils.remove(guid, "]");
                                                guid = StringUtils.trim(guid);

                                                if (DEBUG)
                                                {
                                                    DEBUGGER.debug("serverGuid: {}", guid);
                                                }

                                                serverData = serverDao.getInstalledServer(guid);

                                                if (DEBUG)
                                                {
                                                    DEBUGGER.debug("serverData: {}", serverData);
                                                }

                                                if ((serverData != null) && (serverData.size() != 0))
                                                {
                                                    Server server = new Server();
                                                    server.setServerGuid(serverData.get(0));
                                                    server.setOsName(serverData.get(1));
                                                    server.setServerStatus(ServerStatus.valueOf(serverData.get(2)));
                                                    server.setServerRegion(ServiceRegion.valueOf(serverData.get(3)));
                                                    server.setServerType(ServerType.valueOf(serverData.get(4)));
                                                    server.setDomainName(serverData.get(5));
                                                    server.setCpuType(serverData.get(6));
                                                    server.setCpuCount(Integer.parseInt(serverData.get(7)));
                                                    server.setServerRack(serverData.get(8));
                                                    server.setRackPosition(serverData.get(9));
                                                    server.setServerModel(serverData.get(10));
                                                    server.setSerialNumber(serverData.get(11));
                                                    server.setInstalledMemory(Integer.parseInt(serverData.get(12)));
                                                    server.setOperIpAddress(serverData.get(13));
                                                    server.setOperHostName(serverData.get(14));
                                                    server.setAssignedEngineer(serverData.get(15));
                                                    server.setServerComments(serverData.get(16));
                                                    server.setMgmtIpAddress(serverData.get(17));
                                                    server.setMgmtHostName(serverData.get(18));
                                                    server.setBkIpAddress(serverData.get(19));
                                                    server.setBkHostName(serverData.get(20));
                                                    server.setNasIpAddress(serverData.get(21));
                                                    server.setNasHostName(serverData.get(22));
                                                    server.setNatAddress(serverData.get(23));
                                                    server.setMgrUrl(serverData.get(24));
                                                    server.setOwningDmgr(serverData.get(25));

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
                                        }
                                
                                        Platform platform = new Platform();
                                        platform.setAppServers(appServerList);
                                        platform.setPlatformDmgr(dmgrServer);
                                        platform.setAppServers(appServerList);
                                        platform.setWebServers(webServerList);
                                        platform.setPlatformGuid(platformData.get(0));
                                        platform.setPlatformName(platformData.get(1));
                                        platform.setPlatformRegion(ServiceRegion.valueOf(platformData.get(2)));
                                        platform.setDescription(platformData.get(6));

                                        if (DEBUG)
                                        {
                                            DEBUGGER.debug("Platform: {}", platform);
                                        }

                                        resApplication.setPlatform(platform);
                                    }

                                    // get project data
                                    List<String> projectList = projectDAO.getProjectData(data[9]);

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

                                    resApplication.setApplicationGuid(data[0]);
                                    resApplication.setApplicationName(data[1]);
                                    resApplication.setApplicationVersion(data[2]);
                                    resApplication.setScmPath(data[3]);
                                    resApplication.setApplicationCluster(data[4]);
                                    resApplication.setJvmName(data[5]);
                                    resApplication.setApplicationInstallPath(data[6]);
                                    resApplication.setApplicationLogsPath(data[7]);
                                    resApplication.setPidDirectory(data[8]);

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("Application: {}", resApplication);
                                    }

                                    applicationList.add(resApplication);
                                }
                                else
                                {
                                    ERROR_RECORDER.error("Failed to obtain platform data for provided application.");

                                    continue;
                                }
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
                    auditEntry.setReqInfo(reqInfo);
                    auditEntry.setUserAccount(userAccount);
                    auditEntry.setAuditType(AuditType.MODIFYAPP);
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
                    List<String[]> appList = appDAO.getApplicationsByAttribute(application.getApplicationProject().getProjectGuid());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("appList: {}", appList);
                    }

                    if ((appList != null) && (appList.size() != 0))
                    {
                        List<Application> applicationList = new ArrayList<Application>();

                        for (String[] data : appList)
                        {
                            if (DEBUG)
                            {
                                for (String str : data)
                                {
                                    DEBUGGER.debug("data: {}", str);
                                }
                            }

                            Server dmgrServer = null;
                            List<String> serverData = null;
                            List<Server> appServerList = null;
                            List<Server> webServerList = null;
                            Application resApplication = new Application();

                            // get platform data
                            List<String> platformData = platformDao.getPlatformData(data[10]);

                            if (DEBUG)
                            {
                                DEBUGGER.debug("platformData: {}", platformData);
                            }

                            if ((platformData != null) && (platformData.size() != 0))
                            {
                                
                                serverData = serverDao.getInstalledServer(platformData.get(3));

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("serverData: {}", serverData);
                                }

                                if ((serverData != null) && (serverData.size() != 0))
                                {
                                    dmgrServer = new Server();
                                    dmgrServer.setServerGuid(serverData.get(0));
                                    dmgrServer.setOsName(serverData.get(1));
                                    dmgrServer.setServerStatus(ServerStatus.valueOf(serverData.get(2)));
                                    dmgrServer.setServerRegion(ServiceRegion.valueOf(serverData.get(3)));
                                    dmgrServer.setServerType(ServerType.valueOf(serverData.get(4)));
                                    dmgrServer.setDomainName(serverData.get(5));
                                    dmgrServer.setCpuType(serverData.get(6));
                                    dmgrServer.setCpuCount(Integer.parseInt(serverData.get(7)));
                                    dmgrServer.setServerRack(serverData.get(8));
                                    dmgrServer.setRackPosition(serverData.get(9));
                                    dmgrServer.setServerModel(serverData.get(10));
                                    dmgrServer.setSerialNumber(serverData.get(11));
                                    dmgrServer.setInstalledMemory(Integer.parseInt(serverData.get(12)));
                                    dmgrServer.setOperIpAddress(serverData.get(13));
                                    dmgrServer.setOperHostName(serverData.get(14));
                                    dmgrServer.setAssignedEngineer(serverData.get(15));
                                    dmgrServer.setServerComments(serverData.get(16));
                                    dmgrServer.setMgmtIpAddress(serverData.get(17));
                                    dmgrServer.setMgmtHostName(serverData.get(18));
                                    dmgrServer.setBkIpAddress(serverData.get(19));
                                    dmgrServer.setBkHostName(serverData.get(20));
                                    dmgrServer.setNasIpAddress(serverData.get(21));
                                    dmgrServer.setNasHostName(serverData.get(22));
                                    dmgrServer.setNatAddress(serverData.get(23));
                                    dmgrServer.setMgrUrl(serverData.get(24));
                                    dmgrServer.setOwningDmgr(serverData.get(25));

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("Server: {}", dmgrServer);
                                    }

                                    serverData.clear();
                                }

                                if (platformData.get(1).split(",").length >= 1)
                                {
                                    appServerList = new ArrayList<Server>();

                                    // list application servers
                                    for (String serverGuid : platformData.get(4).split(","))
                                    {
                                        String guid = StringUtils.remove(serverGuid, "[");
                                        guid = StringUtils.remove(guid, "]");
                                        guid = StringUtils.trim(guid);

                                        if (DEBUG)
                                        {
                                            DEBUGGER.debug("serverGuid: {}", guid);
                                        }

                                        serverData = serverDao.getInstalledServer(guid);

                                        if (DEBUG)
                                        {
                                            DEBUGGER.debug("serverData: {}", serverData);
                                        }

                                        if ((serverData != null) && (serverData.size() != 0))
                                        {
                                            Server server = new Server();
                                            server.setServerGuid(serverData.get(0));
                                            server.setOsName(serverData.get(1));
                                            server.setServerStatus(ServerStatus.valueOf(serverData.get(2)));
                                            server.setServerRegion(ServiceRegion.valueOf(serverData.get(3)));
                                            server.setServerType(ServerType.valueOf(serverData.get(4)));
                                            server.setDomainName(serverData.get(5));
                                            server.setCpuType(serverData.get(6));
                                            server.setCpuCount(Integer.parseInt(serverData.get(7)));
                                            server.setServerRack(serverData.get(8));
                                            server.setRackPosition(serverData.get(9));
                                            server.setServerModel(serverData.get(10));
                                            server.setSerialNumber(serverData.get(11));
                                            server.setInstalledMemory(Integer.parseInt(serverData.get(12)));
                                            server.setOperIpAddress(serverData.get(13));
                                            server.setOperHostName(serverData.get(14));
                                            server.setAssignedEngineer(serverData.get(15));
                                            server.setServerComments(serverData.get(16));
                                            server.setMgmtIpAddress(serverData.get(17));
                                            server.setMgmtHostName(serverData.get(18));
                                            server.setBkIpAddress(serverData.get(19));
                                            server.setBkHostName(serverData.get(20));
                                            server.setNasIpAddress(serverData.get(21));
                                            server.setNasHostName(serverData.get(22));
                                            server.setNatAddress(serverData.get(23));
                                            server.setMgrUrl(serverData.get(24));
                                            server.setOwningDmgr(serverData.get(25));

                                            if (DEBUG)
                                            {
                                                DEBUGGER.debug("Server: {}", server);
                                            }

                                            appServerList.add(server);

                                            serverData.clear();
                                        }
                                    }

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("appServerList: {}", appServerList);
                                    }
                                }

                                // list web servers
                                if (platformData.get(1).split(",").length >= 1)
                                {
                                    webServerList = new ArrayList<Server>();

                                    for (String serverGuid : platformData.get(5).split(","))
                                    {
                                        String guid = StringUtils.remove(serverGuid, "[");
                                        guid = StringUtils.remove(guid, "]");
                                        guid = StringUtils.trim(guid);

                                        if (DEBUG)
                                        {
                                            DEBUGGER.debug("serverGuid: {}", guid);
                                        }

                                        serverData = serverDao.getInstalledServer(guid);

                                        if (DEBUG)
                                        {
                                            DEBUGGER.debug("serverData: {}", serverData);
                                        }

                                        if ((serverData != null) && (serverData.size() != 0))
                                        {
                                            Server server = new Server();
                                            server.setServerGuid(serverData.get(0));
                                            server.setOsName(serverData.get(1));
                                            server.setServerStatus(ServerStatus.valueOf(serverData.get(2)));
                                            server.setServerRegion(ServiceRegion.valueOf(serverData.get(3)));
                                            server.setServerType(ServerType.valueOf(serverData.get(4)));
                                            server.setDomainName(serverData.get(5));
                                            server.setCpuType(serverData.get(6));
                                            server.setCpuCount(Integer.parseInt(serverData.get(7)));
                                            server.setServerRack(serverData.get(8));
                                            server.setRackPosition(serverData.get(9));
                                            server.setServerModel(serverData.get(10));
                                            server.setSerialNumber(serverData.get(11));
                                            server.setInstalledMemory(Integer.parseInt(serverData.get(12)));
                                            server.setOperIpAddress(serverData.get(13));
                                            server.setOperHostName(serverData.get(14));
                                            server.setAssignedEngineer(serverData.get(15));
                                            server.setServerComments(serverData.get(16));
                                            server.setMgmtIpAddress(serverData.get(17));
                                            server.setMgmtHostName(serverData.get(18));
                                            server.setBkIpAddress(serverData.get(19));
                                            server.setBkHostName(serverData.get(20));
                                            server.setNasIpAddress(serverData.get(21));
                                            server.setNasHostName(serverData.get(22));
                                            server.setNatAddress(serverData.get(23));
                                            server.setMgrUrl(serverData.get(24));
                                            server.setOwningDmgr(serverData.get(25));

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
                                }

                                Platform platform = new Platform();
                                platform.setPlatformGuid(platformData.get(0));
                                platform.setPlatformName(platformData.get(1));
                                platform.setPlatformRegion(ServiceRegion.valueOf(platformData.get(2)));
                                platform.setPlatformDmgr(dmgrServer);
                                platform.setAppServers(appServerList);
                                platform.setWebServers(webServerList);
                                platform.setDescription(platformData.get(0));

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("Platform: {}", platform);
                                }

                                resApplication.setPlatform(platform);
                            }

                            // get project data
                            List<String> projectList = projectDAO.getProjectData(data[9]);

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

                            resApplication.setApplicationGuid(data[0]);
                            resApplication.setApplicationName(data[1]);
                            resApplication.setApplicationVersion(data[2]);
                            resApplication.setScmPath(data[3]);
                            resApplication.setApplicationCluster(data[4]);
                            resApplication.setJvmName(data[5]);
                            resApplication.setApplicationInstallPath(data[6]);
                            resApplication.setApplicationLogsPath(data[7]);
                            resApplication.setPidDirectory(data[8]);

                            if (DEBUG)
                            {
                                DEBUGGER.debug("Application: {}", resApplication);
                            }

                            applicationList.add(resApplication);
                        }

                        response.setApplicationList(applicationList);
                        response.setResponse("Successfully loaded application list");
                        response.setRequestStatus(CoreServicesStatus.SUCCESS);
                    }
                    else
                    {
                        // no applications
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
                    auditEntry.setReqInfo(reqInfo);
                    auditEntry.setUserAccount(userAccount);
                    auditEntry.setAuditType(AuditType.MODIFYAPP);
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
                    List<String> appData = appDAO.getApplicationData(application.getApplicationGuid());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("appData: {}", appData);
                    }

                    if ((appData != null) && (appData.size() != 0))
                    {
                        Server dmgrServer = null;
                        List<String> serverData = null;
                        List<Server> appServerList = null;
                        List<Server> webServerList = null;
                        Application resApplication = new Application();
                        
                        // get platform data
                        List<String> platformData = platformDao.getPlatformData(appData.get(10));

                        if (DEBUG)
                        {
                            DEBUGGER.debug("platformData: {}", platformData);
                        }

                        if ((platformData != null) && (platformData.size() != 0))
                        {
                            serverData = serverDao.getInstalledServer(platformData.get(3));

                            if (DEBUG)
                            {
                                DEBUGGER.debug("serverData: {}", serverData);
                            }

                            if ((serverData != null) && (serverData.size() != 0))
                            {
                                dmgrServer = new Server();
                                dmgrServer.setServerGuid(serverData.get(0));
                                dmgrServer.setOsName(serverData.get(1));
                                dmgrServer.setServerStatus(ServerStatus.valueOf(serverData.get(2)));
                                dmgrServer.setServerRegion(ServiceRegion.valueOf(serverData.get(3)));
                                dmgrServer.setServerType(ServerType.valueOf(serverData.get(4)));
                                dmgrServer.setDomainName(serverData.get(5));
                                dmgrServer.setCpuType(serverData.get(6));
                                dmgrServer.setCpuCount(Integer.parseInt(serverData.get(7)));
                                dmgrServer.setServerRack(serverData.get(8));
                                dmgrServer.setRackPosition(serverData.get(9));
                                dmgrServer.setServerModel(serverData.get(10));
                                dmgrServer.setSerialNumber(serverData.get(11));
                                dmgrServer.setInstalledMemory(Integer.parseInt(serverData.get(12)));
                                dmgrServer.setOperIpAddress(serverData.get(13));
                                dmgrServer.setOperHostName(serverData.get(14));
                                dmgrServer.setAssignedEngineer(serverData.get(15));
                                dmgrServer.setServerComments(serverData.get(16));
                                dmgrServer.setMgmtIpAddress(serverData.get(17));
                                dmgrServer.setMgmtHostName(serverData.get(18));
                                dmgrServer.setBkIpAddress(serverData.get(19));
                                dmgrServer.setBkHostName(serverData.get(20));
                                dmgrServer.setNasIpAddress(serverData.get(21));
                                dmgrServer.setNasHostName(serverData.get(22));
                                dmgrServer.setNatAddress(serverData.get(23));
                                dmgrServer.setMgrUrl(serverData.get(24));
                                dmgrServer.setOwningDmgr(serverData.get(25));

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("Server: {}", dmgrServer);
                                }

                                serverData.clear();
                            }

                            if (platformData.get(3).split(",").length >= 1)
                            {
                                appServerList = new ArrayList<Server>();

                                // list application servers
                                for (String serverGuid : platformData.get(3).split(","))
                                {
                                    String guid = StringUtils.remove(serverGuid, "[");
                                    guid = StringUtils.remove(guid, "]");
                                    guid = StringUtils.trim(guid);

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("serverGuid: {}", guid);
                                    }

                                    serverData = serverDao.getInstalledServer(guid);

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("serverData: {}", serverData);
                                    }

                                    if ((serverData != null) && (serverData.size() != 0))
                                    {
                                        Server server = new Server();
                                        server.setServerGuid(serverData.get(0));
                                        server.setOsName(serverData.get(1));
                                        server.setServerStatus(ServerStatus.valueOf(serverData.get(2)));
                                        server.setServerRegion(ServiceRegion.valueOf(serverData.get(3)));
                                        server.setServerType(ServerType.valueOf(serverData.get(4)));
                                        server.setDomainName(serverData.get(5));
                                        server.setCpuType(serverData.get(6));
                                        server.setCpuCount(Integer.parseInt(serverData.get(7)));
                                        server.setServerRack(serverData.get(8));
                                        server.setRackPosition(serverData.get(9));
                                        server.setServerModel(serverData.get(10));
                                        server.setSerialNumber(serverData.get(11));
                                        server.setInstalledMemory(Integer.parseInt(serverData.get(12)));
                                        server.setOperIpAddress(serverData.get(13));
                                        server.setOperHostName(serverData.get(14));
                                        server.setAssignedEngineer(serverData.get(15));
                                        server.setServerComments(serverData.get(16));
                                        server.setMgmtIpAddress(serverData.get(17));
                                        server.setMgmtHostName(serverData.get(18));
                                        server.setBkIpAddress(serverData.get(19));
                                        server.setBkHostName(serverData.get(20));
                                        server.setNasIpAddress(serverData.get(21));
                                        server.setNasHostName(serverData.get(22));
                                        server.setNatAddress(serverData.get(23));
                                        server.setMgrUrl(serverData.get(24));
                                        server.setOwningDmgr(serverData.get(25));

                                        if (DEBUG)
                                        {
                                            DEBUGGER.debug("Server: {}", server);
                                        }

                                        appServerList.add(server);

                                        serverData.clear();
                                    }
                                }

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("appServerList: {}", appServerList);
                                }
                            }

                            // list web servers
                            if (platformData.get(4).split(",").length >= 1)
                            {
                                webServerList = new ArrayList<Server>();

                                for (String serverGuid : platformData.get(4).split(","))
                                {
                                    String guid = StringUtils.remove(serverGuid, "[");
                                    guid = StringUtils.remove(guid, "]");
                                    guid = StringUtils.trim(guid);

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("serverGuid: {}", guid);
                                    }

                                    serverData = serverDao.getInstalledServer(guid);

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("serverData: {}", serverData);
                                    }

                                    if ((serverData != null) && (serverData.size() != 0))
                                    {
                                        Server server = new Server();
                                        server.setServerGuid(serverData.get(0));
                                        server.setOsName(serverData.get(1));
                                        server.setServerStatus(ServerStatus.valueOf(serverData.get(2)));
                                        server.setServerRegion(ServiceRegion.valueOf(serverData.get(3)));
                                        server.setServerType(ServerType.valueOf(serverData.get(4)));
                                        server.setDomainName(serverData.get(5));
                                        server.setCpuType(serverData.get(6));
                                        server.setCpuCount(Integer.parseInt(serverData.get(7)));
                                        server.setServerRack(serverData.get(8));
                                        server.setRackPosition(serverData.get(9));
                                        server.setServerModel(serverData.get(10));
                                        server.setSerialNumber(serverData.get(11));
                                        server.setInstalledMemory(Integer.parseInt(serverData.get(12)));
                                        server.setOperIpAddress(serverData.get(13));
                                        server.setOperHostName(serverData.get(14));
                                        server.setAssignedEngineer(serverData.get(15));
                                        server.setServerComments(serverData.get(16));
                                        server.setMgmtIpAddress(serverData.get(17));
                                        server.setMgmtHostName(serverData.get(18));
                                        server.setBkIpAddress(serverData.get(19));
                                        server.setBkHostName(serverData.get(20));
                                        server.setNasIpAddress(serverData.get(21));
                                        server.setNasHostName(serverData.get(22));
                                        server.setNatAddress(serverData.get(23));
                                        server.setMgrUrl(serverData.get(24));
                                        server.setOwningDmgr(serverData.get(25));

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
                            }
                            
                            Platform platform = new Platform();
                            platform.setPlatformGuid(platformData.get(0));
                            platform.setPlatformName(platformData.get(1));
                            platform.setPlatformRegion(ServiceRegion.valueOf(platformData.get(2)));
                            platform.setPlatformDmgr(dmgrServer);
                            platform.setAppServers(appServerList);
                            platform.setWebServers(webServerList);
                            platform.setDescription(platformData.get(0));

                            if (DEBUG)
                            {
                                DEBUGGER.debug("Platform: {}", platform);
                            }

                            resApplication.setPlatform(platform);
                        }

                        // get project data
                        List<String> projectList = projectDAO.getProjectData(appData.get(9));

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

                        resApplication.setApplicationGuid(appData.get(0));
                        resApplication.setApplicationName(appData.get(1));
                        resApplication.setApplicationVersion(appData.get(2));
                        resApplication.setScmPath(appData.get(3));
                        resApplication.setApplicationCluster(appData.get(4));
                        resApplication.setJvmName(appData.get(5));
                        resApplication.setApplicationInstallPath(appData.get(6));
                        resApplication.setApplicationLogsPath(appData.get(7));
                        resApplication.setPidDirectory(appData.get(8));

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
                    auditEntry.setReqInfo(reqInfo);
                    auditEntry.setUserAccount(userAccount);
                    auditEntry.setAuditType(AuditType.MODIFYAPP);
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
                            resApplication.setScmPath(appData.get(3));
                            resApplication.setApplicationCluster(appData.get(4));
                            resApplication.setJvmName(appData.get(5));
                            resApplication.setApplicationInstallPath(appData.get(6));
                            resApplication.setApplicationLogsPath(appData.get(7));
                            resApplication.setPidDirectory(appData.get(8));

                            if (DEBUG)
                            {
                                DEBUGGER.debug("Application: {}", resApplication);
                            }

                            FileManagerRequest fileRequest = new FileManagerRequest();

                            if (StringUtils.isEmpty(request.getRequestFile()))
                            {
                                fileRequest.setRequestFile(appData.get(6)); // TODO: this should be the root dir
                            }
                            else
                            {
                                fileRequest.setRequestFile(appData.get(6) + "/" + request.getRequestFile());
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

                                    if (fileResponse != null)
                                    {
                                        if ((response.getFileData() != null) && (response.getFileData().length != 0))
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
            catch (AdminControlServiceException acsx)
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
                    auditEntry.setReqInfo(reqInfo);
                    auditEntry.setUserAccount(userAccount);
                    auditEntry.setAuditType(AuditType.MODIFYPROJECT);
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
                    List<String> appData = appDAO.getApplicationData(application.getApplicationGuid());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("appData: {}", appData);
                    }

                    if ((appData != null) && (appData.size() != 0))
                    {
                        Server dmgrServer = null;
                        List<String> serverData = null;
                        List<Server> appServerList = null;
                        List<Server> webServerList = null;
                        Application resApplication = new Application();
                        
                        // get platform data
                        List<String> platformData = platformDao.getPlatformData(appData.get(2));

                        if (DEBUG)
                        {
                            DEBUGGER.debug("platformData: {}", platformData);
                        }

                        if ((platformData != null) && (platformData.size() != 0))
                        {
                            serverData = serverDao.getInstalledServer(platformData.get(3));

                            if (DEBUG)
                            {
                                DEBUGGER.debug("serverData: {}", serverData);
                            }

                            if ((serverData != null) && (serverData.size() != 0))
                            {
                                dmgrServer = new Server();
                                dmgrServer.setServerGuid(serverData.get(0));
                                dmgrServer.setOsName(serverData.get(1));
                                dmgrServer.setServerStatus(ServerStatus.valueOf(serverData.get(2)));
                                dmgrServer.setServerRegion(ServiceRegion.valueOf(serverData.get(3)));
                                dmgrServer.setServerType(ServerType.valueOf(serverData.get(4)));
                                dmgrServer.setDomainName(serverData.get(5));
                                dmgrServer.setCpuType(serverData.get(6));
                                dmgrServer.setCpuCount(Integer.parseInt(serverData.get(7)));
                                dmgrServer.setServerRack(serverData.get(8));
                                dmgrServer.setRackPosition(serverData.get(9));
                                dmgrServer.setServerModel(serverData.get(10));
                                dmgrServer.setSerialNumber(serverData.get(11));
                                dmgrServer.setInstalledMemory(Integer.parseInt(serverData.get(12)));
                                dmgrServer.setOperIpAddress(serverData.get(13));
                                dmgrServer.setOperHostName(serverData.get(14));
                                dmgrServer.setAssignedEngineer(serverData.get(15));
                                dmgrServer.setServerComments(serverData.get(16));
                                dmgrServer.setMgmtIpAddress(serverData.get(17));
                                dmgrServer.setMgmtHostName(serverData.get(18));
                                dmgrServer.setBkIpAddress(serverData.get(19));
                                dmgrServer.setBkHostName(serverData.get(20));
                                dmgrServer.setNasIpAddress(serverData.get(21));
                                dmgrServer.setNasHostName(serverData.get(22));
                                dmgrServer.setNatAddress(serverData.get(23));
                                dmgrServer.setMgrUrl(serverData.get(24));
                                dmgrServer.setOwningDmgr(serverData.get(25));

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("Server: {}", dmgrServer);
                                }

                                serverData.clear();
                            }

                            if (platformData.get(3).split(",").length >= 1)
                            {
                                appServerList = new ArrayList<Server>();

                                // list application servers
                                for (String serverGuid : platformData.get(3).split(","))
                                {
                                    String guid = StringUtils.remove(serverGuid, "[");
                                    guid = StringUtils.remove(guid, "]");
                                    guid = StringUtils.trim(guid);

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("serverGuid: {}", guid);
                                    }

                                    serverData = serverDao.getInstalledServer(guid);

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("serverData: {}", serverData);
                                    }

                                    if ((serverData != null) && (serverData.size() != 0))
                                    {
                                        Server server = new Server();
                                        server.setServerGuid(serverData.get(0));
                                        server.setOsName(serverData.get(1));
                                        server.setServerStatus(ServerStatus.valueOf(serverData.get(2)));
                                        server.setServerRegion(ServiceRegion.valueOf(serverData.get(3)));
                                        server.setServerType(ServerType.valueOf(serverData.get(4)));
                                        server.setDomainName(serverData.get(5));
                                        server.setCpuType(serverData.get(6));
                                        server.setCpuCount(Integer.parseInt(serverData.get(7)));
                                        server.setServerRack(serverData.get(8));
                                        server.setRackPosition(serverData.get(9));
                                        server.setServerModel(serverData.get(10));
                                        server.setSerialNumber(serverData.get(11));
                                        server.setInstalledMemory(Integer.parseInt(serverData.get(12)));
                                        server.setOperIpAddress(serverData.get(13));
                                        server.setOperHostName(serverData.get(14));
                                        server.setAssignedEngineer(serverData.get(15));
                                        server.setServerComments(serverData.get(16));
                                        server.setMgmtIpAddress(serverData.get(17));
                                        server.setMgmtHostName(serverData.get(18));
                                        server.setBkIpAddress(serverData.get(19));
                                        server.setBkHostName(serverData.get(20));
                                        server.setNasIpAddress(serverData.get(21));
                                        server.setNasHostName(serverData.get(22));
                                        server.setNatAddress(serverData.get(23));
                                        server.setMgrUrl(serverData.get(24));
                                        server.setOwningDmgr(serverData.get(25));

                                        if (DEBUG)
                                        {
                                            DEBUGGER.debug("Server: {}", server);
                                        }

                                        appServerList.add(server);

                                        serverData.clear();
                                    }
                                }

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("appServerList: {}", appServerList);
                                }
                            }

                            // list web servers
                            if (platformData.get(4).split(",").length >= 1)
                            {
                                webServerList = new ArrayList<Server>();

                                for (String serverGuid : platformData.get(4).split(","))
                                {
                                    String guid = StringUtils.remove(serverGuid, "[");
                                    guid = StringUtils.remove(guid, "]");
                                    guid = StringUtils.trim(guid);

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("serverGuid: {}", guid);
                                    }

                                    serverData = serverDao.getInstalledServer(guid);

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("serverData: {}", serverData);
                                    }

                                    if ((serverData != null) && (serverData.size() != 0))
                                    {
                                        Server server = new Server();
                                        server.setServerGuid(serverData.get(0));
                                        server.setOsName(serverData.get(1));
                                        server.setServerStatus(ServerStatus.valueOf(serverData.get(2)));
                                        server.setServerRegion(ServiceRegion.valueOf(serverData.get(3)));
                                        server.setServerType(ServerType.valueOf(serverData.get(4)));
                                        server.setDomainName(serverData.get(5));
                                        server.setCpuType(serverData.get(6));
                                        server.setCpuCount(Integer.parseInt(serverData.get(7)));
                                        server.setServerRack(serverData.get(8));
                                        server.setRackPosition(serverData.get(9));
                                        server.setServerModel(serverData.get(10));
                                        server.setSerialNumber(serverData.get(11));
                                        server.setInstalledMemory(Integer.parseInt(serverData.get(12)));
                                        server.setOperIpAddress(serverData.get(13));
                                        server.setOperHostName(serverData.get(14));
                                        server.setAssignedEngineer(serverData.get(15));
                                        server.setServerComments(serverData.get(16));
                                        server.setMgmtIpAddress(serverData.get(17));
                                        server.setMgmtHostName(serverData.get(18));
                                        server.setBkIpAddress(serverData.get(19));
                                        server.setBkHostName(serverData.get(20));
                                        server.setNasIpAddress(serverData.get(21));
                                        server.setNasHostName(serverData.get(22));
                                        server.setNatAddress(serverData.get(23));
                                        server.setMgrUrl(serverData.get(24));
                                        server.setOwningDmgr(serverData.get(25));

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
                            }
                            
                            Platform platform = new Platform();
                            platform.setPlatformGuid(platformData.get(0));
                            platform.setPlatformName(platformData.get(1));
                            platform.setPlatformRegion(ServiceRegion.valueOf(platformData.get(2)));
                            platform.setPlatformDmgr(dmgrServer);
                            platform.setAppServers(appServerList);
                            platform.setWebServers(webServerList);
                            platform.setDescription(platformData.get(0));

                            if (DEBUG)
                            {
                                DEBUGGER.debug("Platform: {}", platform);
                            }

                            resApplication.setPlatform(platform);

                            if (DEBUG)
                            {
                                DEBUGGER.debug("Application: {}", resApplication);
                            }

                            ApplicationManagerRequest mgrRequest = new ApplicationManagerRequest();
                            mgrRequest.setAppBinary(application.getApplicationBinary());
                            mgrRequest.setJvmName(resApplication.getJvmName());
                            mgrRequest.setDeploymentType(DeploymentType.DEPLOY);
                            mgrRequest.setMgmtType(ApplicationManagementType.DEPLOY);
                            mgrRequest.setTargetDirectory(resApplication.getApplicationInstallPath());

                            if (DEBUG)
                            {
                                DEBUGGER.debug("ApplicationManagerRequest: {}", mgrRequest);
                            }

                            AgentRequest agentRequest = new AgentRequest();
                            agentRequest.setAppName(appConfig.getAppName());
                            agentRequest.setProjectId(appData.get(6));
                            agentRequest.setRequestPayload(mgrRequest);

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
                                    File responseData = (File) agentResponse.getResponsePayload();

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("FileData: {}", responseData);
                                    }

                                    //response.setFileData(responseData);
                                    response.setResponse("Successfully loaded file data");
                                    response.setRequestStatus(CoreServicesStatus.SUCCESS);
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
                        else
                        {
                            // no platform data, we dont know where to deploy to
                            throw new ApplicationManagementException("Unable to determine platform to process deployment. Cannot continue.");
                        }
                    }
                    else
                    {
                        // no data found for the given application
                        throw new ApplicationManagementException("Unable to determine application data to process deployment. Cannot continue.");
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
            catch (AdminControlServiceException acsx)
            {
                ERROR_RECORDER.error(acsx.getMessage(), acsx);

                throw new ApplicationManagementException(acsx.getMessage(), acsx);
            }
            catch (SQLException sqx)
            {
                ERROR_RECORDER.error(sqx.getMessage(), sqx);

                throw new ApplicationManagementException(sqx.getMessage(), sqx);
            }
            catch (UtilityException ux)
            {
                ERROR_RECORDER.error(ux.getMessage(), ux);

                throw new ApplicationManagementException(ux.getMessage(), ux);
            }
            finally
            {
                // audit
                try
                {
                    AuditEntry auditEntry = new AuditEntry();
                    auditEntry.setReqInfo(reqInfo);
                    auditEntry.setUserAccount(userAccount);
                    auditEntry.setAuditType(AuditType.MODIFYAPP);
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
        }
        else
        {
            throw new ApplicationManagementException("No audit host info was provided. Cannot continue");
        }
        
        return response;
    }
}
