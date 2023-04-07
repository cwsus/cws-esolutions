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
 * File: ApplicationManagementProcessorImpl.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.io.File;
import java.util.UUID;
import java.util.List;
import java.util.Date;
import java.util.Arrays;
import java.util.Objects;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.sql.SQLException;
import org.apache.commons.lang3.StringUtils;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.core.processors.dto.Server;
import com.cws.esolutions.core.dto.CoreServicesRequest;
import com.cws.esolutions.core.processors.dto.Platform;
import com.cws.esolutions.core.dto.CoreServicesResponse;
import com.cws.esolutions.core.enums.CoreServicesStatus;
import com.cws.esolutions.core.processors.dto.Application;
import com.cws.esolutions.core.processors.enums.ServiceStatus;
import com.cws.esolutions.core.processors.dto.FileManagerRequest;
import com.cws.esolutions.core.processors.dto.FileManagerResponse;
import com.cws.esolutions.utility.securityutils.processors.dto.AuditEntry;
import com.cws.esolutions.utility.securityutils.processors.enums.AuditType;
import com.cws.esolutions.utility.services.dto.AccessControlServiceRequest;
import com.cws.esolutions.core.processors.dto.ApplicationManagementRequest;
import com.cws.esolutions.utility.services.dto.AccessControlServiceResponse;
import com.cws.esolutions.utility.securityutils.processors.dto.AuditRequest;
import com.cws.esolutions.core.processors.dto.ApplicationManagementResponse;
import com.cws.esolutions.utility.securityutils.processors.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.exception.ApplicationManagementException;
import com.cws.esolutions.utility.services.exception.AccessControlServiceException;
import com.cws.esolutions.core.processors.interfaces.IApplicationManagementProcessor;
import com.cws.esolutions.utility.securityutils.processors.exception.AuditServiceException;
/**
 * @see com.cws.esolutions.core.processors.interfaces.IApplicationManagementProcessor
 */
public class ApplicationManagementProcessorImpl implements IApplicationManagementProcessor
{
	private static final String CNAME = ApplicationManagementProcessorImpl.class.getName();

    /**
     * @see com.cws.esolutions.core.processors.interfaces.IApplicationManagementProcessor#addNewApplication(com.cws.esolutions.core.processors.dto.ApplicationManagementRequest)
     */
    public ApplicationManagementResponse addNewApplication(final ApplicationManagementRequest request) throws ApplicationManagementException
    {
        final String methodName = ApplicationManagementProcessorImpl.CNAME + "#addNewApplication(final ApplicationManagementRequest request) throws ApplicationManagementException";

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
                        auditEntry.setAuditType(AuditType.ADDAPP);
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
        
                        AuditRequest auditRequest = new AuditRequest();
                        auditRequest.setAuditEntry(auditEntry);
                        auditRequest.setHostInfo(reqInfo);
        
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

            String applGuid = UUID.randomUUID().toString();

            if (DEBUG)
            {
                DEBUGGER.debug("applGuid: {}", applGuid);
            }

            List<Object> validator = null;

            try
            {
                validator = appDAO.getApplication(applGuid);
            }
            catch (final SQLException sqx)
            {
                ERROR_RECORDER.error(sqx.getMessage(), sqx);
            }

            if (DEBUG)
            {
                DEBUGGER.debug("validator: {}", validator);
            }

            if ((validator != null) && (validator.size() != 0))
            {
                throw new ApplicationManagementException("Application already exists, cannot duplicate.");
            }

            // project doesn't already exist. we can add it
            // we are NOT adding any applications to the project YET
            // if there are any to add we'll do that later (its a
            // different table in the database)
            if (Objects.isNull(application.getPlatform()))
            {
                throw new ApplicationManagementException("No platform was provided. Cannot continue.");
            }

            List<String> appDataList = new ArrayList<String>(
            		Arrays.asList(
            				applGuid,
            				application.getName(),
            				application.getVersion(),
            				application.getPlatform().getPlatformGuid(),
            				application.getBasePath().toString(),
            				application.getLogsPath().toString(),
            				application.getInstallPath().toString()));

            if (DEBUG)
            {
                DEBUGGER.debug("appDataList: {}", appDataList);
            }

            appDAO.addApplication(appDataList);

            response.setRequestStatus(CoreServicesStatus.SUCCESS);

            if (DEBUG)
            {
                DEBUGGER.debug("ApplicationManagementResponse: {}", response);
            }
        }
        catch (final SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new ApplicationManagementException(sqx.getMessage(), sqx);
        }
        catch (final AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);
            
            throw new ApplicationManagementException(acsx.getMessage(), acsx);
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
                    auditEntry.setAuditType(AuditType.ADDAPP);
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
    
                    AuditRequest auditRequest = new AuditRequest();
                    auditRequest.setAuditEntry(auditEntry);
                    auditRequest.setHostInfo(reqInfo);
    
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
     * @see com.cws.esolutions.core.processors.interfaces.IApplicationManagementProcessor#updateApplicationData(com.cws.esolutions.core.processors.dto.ApplicationManagementRequest)
     */
    public ApplicationManagementResponse updateApplicationData(final ApplicationManagementRequest request) throws ApplicationManagementException
    {
        final String methodName = ApplicationManagementProcessorImpl.CNAME + "#updateApplicationData(final ApplicationManagementRequest request) throws ApplicationManagementException";

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
                        auditEntry.setAuditType(AuditType.UPDATEAPP);
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
        
                        AuditRequest auditRequest = new AuditRequest();
                        auditRequest.setAuditEntry(auditEntry);
                        auditRequest.setHostInfo(reqInfo);
        
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

            List<Object> appDataList = new ArrayList<Object>(
                    Arrays.asList(
                            application.getGuid(),
                            application.getName(),
                            application.getVersion(),
                            application.getInstallPath(),
                            application.getPackageLocation(),
                            application.getPackageInstaller(),
                            application.getInstallerOptions(),
                            application.getLogsPath()));

            if (DEBUG)
            {
                DEBUGGER.debug("appDataList: {}", appDataList);
            }

            appDAO.updateApplication(appDataList);
            
            response.setRequestStatus(CoreServicesStatus.SUCCESS);

            if (DEBUG)
            {
                DEBUGGER.debug("ApplicationManagementResponse: {}", response);
            }
        }
        catch (final SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new ApplicationManagementException(sqx.getMessage(), sqx);
        }
        catch (final AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);
            
            throw new ApplicationManagementException(acsx.getMessage(), acsx);
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
                    auditEntry.setAuditType(AuditType.UPDATEAPP);
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
    
                    AuditRequest auditRequest = new AuditRequest();
                    auditRequest.setAuditEntry(auditEntry);
                    auditRequest.setHostInfo(reqInfo);
    
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
     * @see com.cws.esolutions.core.processors.interfaces.IApplicationManagementProcessor#deleteApplicationData(com.cws.esolutions.core.processors.dto.ApplicationManagementRequest)
     */
    public ApplicationManagementResponse deleteApplicationData(final ApplicationManagementRequest request) throws ApplicationManagementException
    {
        final String methodName = ApplicationManagementProcessorImpl.CNAME + "#deleteApplicationData(final ApplicationManagementRequest request) throws ApplicationManagementException";

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
                        auditEntry.setAuditType(AuditType.DELETEAPP);
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
        
                        AuditRequest auditRequest = new AuditRequest();
                        auditRequest.setAuditEntry(auditEntry);
                        auditRequest.setHostInfo(reqInfo);
        
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

            appDAO.removeApplication(application.getGuid());

            response.setRequestStatus(CoreServicesStatus.SUCCESS);

            if (DEBUG)
            {
                DEBUGGER.debug("ApplicationManagementResponse: {}", response);
            }
        }
        catch (final SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new ApplicationManagementException(sqx.getMessage(), sqx);
        }
        catch (final AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);
            
            throw new ApplicationManagementException(acsx.getMessage(), acsx);
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
                    auditEntry.setAuditType(AuditType.DELETEAPP);
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
    
                    AuditRequest auditRequest = new AuditRequest();
                    auditRequest.setAuditEntry(auditEntry);
                    auditRequest.setHostInfo(reqInfo);
    
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
     * @see com.cws.esolutions.core.processors.interfaces.IApplicationManagementProcessor#listApplications(com.cws.esolutions.core.processors.dto.ApplicationManagementRequest)
     */
    public ApplicationManagementResponse listApplications(final ApplicationManagementRequest request) throws ApplicationManagementException
    {
        final String methodName = ApplicationManagementProcessorImpl.CNAME + "#listApplications(final ApplicationManagementRequest request) throws ApplicationManagementException";

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
                        auditEntry.setAuditType(AuditType.LISTAPPS);
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
        
                        AuditRequest auditRequest = new AuditRequest();
                        auditRequest.setAuditEntry(auditEntry);
                        auditRequest.setHostInfo(reqInfo);
        
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

            List<String[]> appData = appDAO.listApplications(request.getStartPage());

            if (DEBUG)
            {
                DEBUGGER.debug("List<String[]>: {}", appData);
            }

            if ((appData == null) || (appData.size() == 0))
            {
                throw new ApplicationManagementException("Obtaining list of applications failed. Please review SQL logs.");
            }

            List<Application> appList = new ArrayList<Application>();

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

            // response.setEntryCount(value); // TODO
            response.setApplicationList(appList);
            response.setRequestStatus(CoreServicesStatus.SUCCESS);

            if (DEBUG)
            {
                DEBUGGER.debug("ApplicationManagementResponse: {}", response);
            }
        }
        catch (final SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new ApplicationManagementException(sqx.getMessage(), sqx);
        }
        catch (final AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);

            throw new ApplicationManagementException(acsx.getMessage(), acsx);
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
                    auditEntry.setAuditType(AuditType.LISTAPPS);
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
    
                    AuditRequest auditRequest = new AuditRequest();
                    auditRequest.setAuditEntry(auditEntry);
                    auditRequest.setHostInfo(reqInfo);
    
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
     * @see com.cws.esolutions.core.processors.interfaces.IApplicationManagementProcessor#listApplicationsByAttribute(com.cws.esolutions.core.processors.dto.ApplicationManagementRequest)
     */
    public ApplicationManagementResponse listApplicationsByAttribute(final ApplicationManagementRequest request) throws ApplicationManagementException
    {
        final String methodName = ApplicationManagementProcessorImpl.CNAME + "#listApplicationsByAttribute(final ApplicationManagementRequest request) throws ApplicationManagementException";

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
                        auditEntry.setAuditType(AuditType.LISTAPPS);
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
        
                        AuditRequest auditRequest = new AuditRequest();
                        auditRequest.setAuditEntry(auditEntry);
                        auditRequest.setHostInfo(reqInfo);
        
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

            List<Object[]> appData = appDAO.getApplicationsByAttribute(application.getName(), request.getStartPage());

            if (DEBUG)
            {
                DEBUGGER.debug("List<String[]>: {}", appData);
            }

            if ((appData == null) || (appData.size() == 0))
            {
                throw new ApplicationManagementException("Obtaining list of applications failed. Please review SQL logs.");
            }

            List<Application> appList = new ArrayList<Application>();

            for (Object[] array : appData)
            {
                Application app = new Application();
                app.setGuid((String) array[0]); // T1.APPLICATION_GUID
                app.setName((String) array[1]); // T1.APPLICATION_NAME

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

            // response.setEntryCount(value); // TODO
            response.setApplicationList(appList);
            response.setRequestStatus(CoreServicesStatus.SUCCESS);

            if (DEBUG)
            {
                DEBUGGER.debug("ApplicationManagementResponse: {}", response);
            }
        }
        catch (final SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new ApplicationManagementException(sqx.getMessage(), sqx);
        }
        catch (final AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);

            throw new ApplicationManagementException(acsx.getMessage(), acsx);
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
                    auditEntry.setAuditType(AuditType.LISTAPPS);
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
    
                    AuditRequest auditRequest = new AuditRequest();
                    auditRequest.setAuditEntry(auditEntry);
                    auditRequest.setHostInfo(reqInfo);
    
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
     * @see com.cws.esolutions.core.processors.interfaces.IApplicationManagementProcessor#getApplicationData(com.cws.esolutions.core.processors.dto.ApplicationManagementRequest)
     */
    public ApplicationManagementResponse getApplicationData(final ApplicationManagementRequest request) throws ApplicationManagementException
    {
        final String methodName = ApplicationManagementProcessorImpl.CNAME + "#getApplicationData(final ApplicationManagementRequest request) throws ApplicationManagementException";

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
                        auditEntry.setAuditType(AuditType.LOADAPP);
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
        
                        AuditRequest auditRequest = new AuditRequest();
                        auditRequest.setAuditEntry(auditEntry);
                        auditRequest.setHostInfo(reqInfo);
        
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

            List<Object> appData = appDAO.getApplication(application.getGuid());

            if (DEBUG)
            {
                DEBUGGER.debug("appData: {}", appData);
            }

            if ((appData == null) || (appData.size() == 0))
            {
                throw new ApplicationManagementException("No application data was found.");
            }

            if (DEBUG)
            {
                DEBUGGER.debug("List<Object>: {}", appData);
            }

    		Platform reqPlatform = new Platform();
    		reqPlatform.setPlatformGuid((String) appData.get(3));

    		if (DEBUG)
    		{
    			DEBUGGER.debug("reqPlatform: {}", reqPlatform);
    		}

    		List<String> platformData = platformDAO.getPlatform((String) appData.get(3));

    		if (DEBUG)
    		{
    			DEBUGGER.debug("platformData: {}", platformData);
    		}

            if ((platformData != null) && (platformData.size() != 0))
            {
                Platform resPlatform = new Platform();
                resPlatform.setPlatformGuid(platformData.get(0)); // GUID
                resPlatform.setPlatformName(platformData.get(1)); // NAME
                resPlatform.setPlatformStatus(ServiceStatus.valueOf(platformData.get(2))); // STATUS
                resPlatform.setPlatformDescription(platformData.get(3)); // DESCRIPTION

                if (DEBUG)
                {
                	DEBUGGER.debug("Platform: {}", resPlatform);
                }

                // then put it all together
                Application resApplication = new Application();
                resApplication.setGuid((String) appData.get(0)); // GUID
                resApplication.setName((String) appData.get(1)); // NAME
                resApplication.setVersion((String) appData.get(2)); // VERSION
                resApplication.setPlatform(resPlatform); // PLAFORM
                resApplication.setInstallPath(new File((String) appData.get(4)));
                resApplication.setLogsPath(new File((String) appData.get(5)));
                resApplication.setPackageLocation(new File((String) appData.get(6)));
                resApplication.setOnlineDate(new Date(((Timestamp) appData.get(7)).getTime()));

	            if (DEBUG)
	            {
	                DEBUGGER.debug("Application: {}", resApplication);
	            }

	            response.setApplication(resApplication);
	            response.setRequestStatus(CoreServicesStatus.SUCCESS);

	            if (DEBUG)
	            {
	            	DEBUGGER.debug("ApplicationManagementResponse: {}", response);
	            }
            }
            else
            {
            	throw new ApplicationManagementException("Unable to obtain platform information for the provided application.");
            }
        }
        catch (final SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new ApplicationManagementException(sqx.getMessage(), sqx);
        }
        catch (final AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);
            
            throw new ApplicationManagementException(acsx.getMessage(), acsx);
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
                    auditEntry.setAuditType(AuditType.LOADAPP);
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
    
                    AuditRequest auditRequest = new AuditRequest();
                    auditRequest.setAuditEntry(auditEntry);
                    auditRequest.setHostInfo(reqInfo);
    
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
     * @see com.cws.esolutions.core.processors.interfaces.IApplicationManagementProcessor#applicationFileRequest(com.cws.esolutions.core.processors.dto.ApplicationManagementRequest)
     */
    public ApplicationManagementResponse applicationFileRequest(final ApplicationManagementRequest request) throws ApplicationManagementException
    {
        final String methodName = ApplicationManagementProcessorImpl.CNAME + "#applicationFileRequest(final ApplicationManagementRequest request) throws ApplicationManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ApplicationManagementRequest: {}", request);
        }

        CoreServicesResponse CoreServiceResponse = null;
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
                        auditEntry.setAuditType(AuditType.GETFILES);
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
        
                        AuditRequest auditRequest = new AuditRequest();
                        auditRequest.setAuditEntry(auditEntry);
                        auditRequest.setHostInfo(reqInfo);
        
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

            List<Object> appData = appDAO.getApplication(application.getGuid());

            if (DEBUG)
            {
                DEBUGGER.debug("appData: {}", appData);
            }

            if ((appData == null) || (appData.size() == 0))
            {
            	throw new ApplicationManagementException("No application data was found.");
            }

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

            CoreServicesRequest agentRequest = new CoreServicesRequest();
            agentRequest.setAppName(appConfig.getAppName());
            agentRequest.setRequestPayload(fileRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("CoreServicesRequest: {}", agentRequest);
            }

            String correlator = null;

            if (DEBUG)
            {
                DEBUGGER.debug("correlator: {}", correlator);
            }

            if (StringUtils.isEmpty(correlator))
            {
                throw new ApplicationManagementException("Failed to process request. Please review logs.");
            }

            if (DEBUG)
            {
                DEBUGGER.debug("CoreServiceResponse: {}", CoreServiceResponse);
            }

            if (CoreServiceResponse.getRequestStatus() != CoreServicesStatus.SUCCESS)
            {
            	throw new ApplicationManagementException("Failed to process request. Please review logs.");
            }

            FileManagerResponse fileResponse = (FileManagerResponse) CoreServiceResponse.getResponsePayload();

            if (DEBUG)
            {
                DEBUGGER.debug("FileManagerResponse: {}", fileResponse);
            }

            if (fileResponse.getRequestStatus() != CoreServicesStatus.SUCCESS)
            {
            	throw new ApplicationManagementException("Failed to process request. Please review logs.");
            }

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

            if (DEBUG)
            {
                DEBUGGER.debug("ApplicationManagementResponse: {}", response);
            }
        }
        catch (final AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);

            throw new ApplicationManagementException(acsx.getMessage(), acsx);
        }
        catch (final SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new ApplicationManagementException(sqx.getMessage(), sqx);
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
                    auditEntry.setAuditType(AuditType.GETFILES);
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
    
                    AuditRequest auditRequest = new AuditRequest();
                    auditRequest.setAuditEntry(auditEntry);
                    auditRequest.setHostInfo(reqInfo);
    
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
     * @see com.cws.esolutions.core.processors.interfaces.IApplicationManagementProcessor#deployApplication(com.cws.esolutions.core.processors.dto.ApplicationManagementRequest)
     */
    public ApplicationManagementResponse deployApplication(final ApplicationManagementRequest request) throws ApplicationManagementException
    {
        final String methodName = ApplicationManagementProcessorImpl.CNAME + "#deployApplication(final ApplicationManagementRequest request) throws ApplicationManagementException";

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
                        auditEntry.setAuditType(AuditType.DEPLOYAPP);
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
        
                        AuditRequest auditRequest = new AuditRequest();
                        auditRequest.setAuditEntry(auditEntry);
                        auditRequest.setHostInfo(reqInfo);
        
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

            // do deployment work here

            if (DEBUG)
            {
                DEBUGGER.debug("ApplicationManagementResponse: {}", response);
            }
        }
        catch (final AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);

            throw new ApplicationManagementException(acsx.getMessage(), acsx);
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
                    auditEntry.setAuditType(AuditType.DEPLOYAPP);
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
    
                    AuditRequest auditRequest = new AuditRequest();
                    auditRequest.setAuditEntry(auditEntry);
                    auditRequest.setHostInfo(reqInfo);
    
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
