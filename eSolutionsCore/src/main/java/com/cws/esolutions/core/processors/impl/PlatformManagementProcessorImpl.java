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
 * File: PlatformManagementProcessorImpl.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.util.UUID;
import java.util.List;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.ArrayList;
import java.sql.SQLException;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.core.processors.dto.Platform;
import com.cws.esolutions.core.enums.CoreServicesStatus;
import com.cws.esolutions.core.processors.enums.ServiceStatus;
import com.cws.esolutions.core.processors.dto.PlatformManagementRequest;
import com.cws.esolutions.core.processors.dto.PlatformManagementResponse;
import com.cws.esolutions.utility.securityutils.processors.dto.AuditEntry;
import com.cws.esolutions.utility.services.dto.AccessControlServiceRequest;
import com.cws.esolutions.utility.securityutils.processors.enums.AuditType;
import com.cws.esolutions.utility.services.dto.AccessControlServiceResponse;
import com.cws.esolutions.utility.securityutils.processors.dto.AuditRequest;
import com.cws.esolutions.utility.securityutils.processors.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.exception.PlatformManagementException;
import com.cws.esolutions.core.processors.interfaces.IPlatformManagementProcessor;
import com.cws.esolutions.utility.services.exception.AccessControlServiceException;
import com.cws.esolutions.utility.securityutils.processors.exception.AuditServiceException;
/**
 * @see com.cws.esolutions.core.processors.interfaces.IPlatformManagementProcessor
 */
public class PlatformManagementProcessorImpl implements IPlatformManagementProcessor
{
    /**
     * @see com.cws.esolutions.core.processors.interfaces.IPlatformManagementProcessor#addNewService(com.cws.esolutions.core.processors.dto.PlatformManagementRequest)
     */
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
                        auditEntry.setAuditType(AuditType.ADDPLATFORM);
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

            if (Objects.isNull(platform))
            {
                throw new PlatformManagementException("No platform was provided. Cannot continue.");
            }

            // make sure all the platform data is there
            List<Object[]> validator = null;

            try
            {
            	validator = serviceDao.getPlatformsByAttribute(platform.getPlatformName(), request.getStartPage());
            }
            catch (final SQLException sqx)
            {
                ERROR_RECORDER.error(sqx.getMessage(), sqx);
            }

            if (DEBUG)
            {
                DEBUGGER.debug("Validator: {}", validator);
            }

            if ((validator == null) || (validator.size() == 0))
            {
                List<String> insertList = new ArrayList<String>(
                		Arrays.asList(
                				UUID.randomUUID().toString(),
                				platform.getPlatformName(),
                				platform.getPlatformStatus().toString(),
                				platform.getPlatformDescription()));

                if (DEBUG)
                {
                	DEBUGGER.debug("insertList: {}", insertList);
                }

                boolean isComplete = serviceDao.addPlatform(insertList);

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
        catch (final SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new PlatformManagementException(sqx.getMessage(), sqx);
        }
        catch (final AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);
            
            throw new PlatformManagementException(acsx.getMessage(), acsx);
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
                    auditEntry.setAuditType(AuditType.ADDPLATFORM);
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
     * @see com.cws.esolutions.core.processors.interfaces.IPlatformManagementProcessor#updateServiceData(com.cws.esolutions.core.processors.dto.PlatformManagementRequest)
     */
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
                        auditEntry.setAuditType(AuditType.UPDATEPLATFORM);
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

            List<String> insertData = new ArrayList<String>(
                    Arrays.asList(
                            platform.getPlatformGuid(),
                            platform.getPlatformName(),
                            platform.getPlatformRegion().name(),
                            platform.getPlatformPartition().name(),
                            platform.getPlatformStatus().name(),
                            platform.getPlatformDescription()));

            if (DEBUG)
            {
                for (Object str : insertData)
                {
                    DEBUGGER.debug("Value: {}", str);
                }
            }

            boolean isComplete = serviceDao.updatePlatform(platform.getPlatformGuid(), insertData);

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

            if (DEBUG)
            {
            	DEBUGGER.debug("PlatformManagementResponse: {}", response);
            }
        }
        catch (final AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);

            throw new PlatformManagementException(acsx.getMessage(), acsx);
        }
        catch (final SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new PlatformManagementException(sqx.getMessage(), sqx);
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
                    auditEntry.setAuditType(AuditType.UPDATEPLATFORM);
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
     * @see com.cws.esolutions.core.processors.interfaces.IPlatformManagementProcessor#listServices(com.cws.esolutions.core.processors.dto.PlatformManagementRequest)
     */
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
                        auditEntry.setAuditType(AuditType.LISTPLATFORMS);
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

            List<String[]> platformData = serviceDao.listPlatforms(request.getStartPage());

            if (DEBUG)
            {
                DEBUGGER.debug("serviceData: {}", platformData);
            }

            if ((platformData != null) && (platformData.size() != 0))
            {
                List<Platform> platformList = new ArrayList<Platform>();

                for (String[] data : platformData)
                {
                    Platform returnedPlatform = new Platform();
                    returnedPlatform.setPlatformGuid(data[0]);
                    returnedPlatform.setPlatformName(data[1]);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Platform: {}", returnedPlatform);
                    }

                    platformList.add(returnedPlatform);
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

            if (DEBUG)
            {
            	DEBUGGER.debug("PlatformManagementResponse: {}", response);
            }
        }
        catch (final SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new PlatformManagementException(sqx.getMessage(), sqx);
        }
        catch (final AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);
            
            throw new PlatformManagementException(acsx.getMessage(), acsx);
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
                    auditEntry.setAuditType(AuditType.LISTPLATFORMS);
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
     * @see com.cws.esolutions.core.processors.interfaces.IPlatformManagementProcessor#getServiceByAttribute(com.cws.esolutions.core.processors.dto.PlatformManagementRequest)
     */
    public PlatformManagementResponse getPlatformByAttribute(final PlatformManagementRequest request) throws PlatformManagementException
    {
        final String methodName = IPlatformManagementProcessor.CNAME + "#getPlatformByAttribute(final PlatformManagementRequest request) throws PlatformManagementException";
        
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
                        auditEntry.setAuditType(AuditType.LISTPLATFORMS);
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

            List<Object[]> platformData = serviceDao.getPlatformsByAttribute(platform.getPlatformName(), request.getStartPage());

            if (DEBUG)
            {
                DEBUGGER.debug("platformData: {}", platformData);
            }

            if ((platformData != null) && (platformData.size() != 0))
            {
                List<Platform> platformList = new ArrayList<Platform>();

                for (Object[] data : platformData)
                {
                	Platform returnedPlatform = new Platform();
                	returnedPlatform.setPlatformGuid((String) data[0]);
                	returnedPlatform.setPlatformName((String) data[1]);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Service: {}", returnedPlatform);
                    }

                    platformList.add(returnedPlatform);
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

            if (DEBUG)
            {
            	DEBUGGER.debug("PlatformManagementResponse: {}", response);
            }
        }
        catch (final SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new PlatformManagementException(sqx.getMessage(), sqx);
        }
        catch (final AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);
            
            throw new PlatformManagementException(acsx.getMessage(), acsx);
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
                    auditEntry.setAuditType(AuditType.LISTPLATFORMS);
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
     * @see com.cws.esolutions.core.processors.interfaces.IPlatformManagementProcessor#getServiceData(com.cws.esolutions.core.processors.dto.PlatformManagementRequest)
     */
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
                        auditEntry.setAuditType(AuditType.LOADPLATFORM);
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

            List<String> platformData = serviceDao.getPlatform(platform.getPlatformGuid());

            if (DEBUG)
            {
                DEBUGGER.debug("platformData: {}", platformData);
            }

            if ((platformData != null) && (platformData.size() != 0))
            {
                Platform responsePlatform = new Platform();
                responsePlatform.setPlatformGuid(platformData.get(0)); // GUID
                responsePlatform.setPlatformName(platformData.get(1)); // NAME
                responsePlatform.setPlatformStatus(ServiceStatus.valueOf(platformData.get(2))); // STATUS
                responsePlatform.setPlatformDescription(platformData.get(3)); // DESCRIPTION

                if (DEBUG)
                {
                    DEBUGGER.debug("Platform: {}", responsePlatform);
                }

                response.setRequestStatus(CoreServicesStatus.SUCCESS);
                response.setPlatform(responsePlatform);
            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.FAILURE);
            }

            if (DEBUG)
            {
            	DEBUGGER.debug("PlatformManagementResponse: {}", response);
            }
        }
        catch (final SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new PlatformManagementException(sqx.getMessage(), sqx);
        }
        catch (final AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);
            
            throw new PlatformManagementException(acsx.getMessage(), acsx);
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
                    auditEntry.setAuditType(AuditType.LOADPLATFORM);
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
     * @see com.cws.esolutions.core.processors.interfaces.IPlatformManagementProcessor#removeServiceData(com.cws.esolutions.core.processors.dto.PlatformManagementRequest)
     */
    public PlatformManagementResponse removePlatformData(final PlatformManagementRequest request) throws PlatformManagementException
    {
        final String methodName = IPlatformManagementProcessor.CNAME + "#removePlatformData(final PlatformManagementRequest request) throws PlatformManagementException";
        
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
                        auditEntry.setAuditType(AuditType.DELETEPLATFORM);
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

            boolean isComplete = serviceDao.removePlatform(platform.getPlatformGuid());

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

            if (DEBUG)
            {
            	DEBUGGER.debug("PlatformManagementResponse: {}", response);
            }
        }
        catch (final AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);

            throw new PlatformManagementException(acsx.getMessage(), acsx);
        }
        catch (final SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new PlatformManagementException(sqx.getMessage(), sqx);
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
                    auditEntry.setAuditType(AuditType.DELETEPLATFORM);
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