/*
 * Copyright (c) 2009 - 2014 CaspersBox Web Services
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
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.UUID;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.sql.SQLException;
import org.apache.commons.lang.StringUtils;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.core.processors.dto.Server;
import com.cws.esolutions.core.processors.dto.Service;
import com.cws.esolutions.core.processors.enums.ServiceType;
import com.cws.esolutions.security.processors.dto.AuditEntry;
import com.cws.esolutions.security.processors.enums.AuditType;
import com.cws.esolutions.core.processors.enums.ServiceRegion;
import com.cws.esolutions.core.processors.enums.ServiceStatus;
import com.cws.esolutions.security.processors.dto.AuditRequest;
import com.cws.esolutions.core.processors.enums.NetworkPartition;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.core.processors.dto.ServiceManagementRequest;
import com.cws.esolutions.core.processors.dto.ServiceManagementResponse;
import com.cws.esolutions.security.processors.exception.AuditServiceException;
import com.cws.esolutions.core.processors.exception.ServiceManagementException;
import com.cws.esolutions.core.processors.interfaces.IServiceManagementProcessor;
import com.cws.esolutions.security.services.dto.AccessControlServiceRequest;
import com.cws.esolutions.security.services.dto.AccessControlServiceResponse;
import com.cws.esolutions.security.services.exception.AccessControlServiceException;
/**
 * @see com.cws.esolutions.core.processors.interfaces.IServiceManagementProcessor
 */
public class ServiceManagementProcessorImpl implements IServiceManagementProcessor
{
    /**
     * @see com.cws.esolutions.core.processors.interfaces.IServiceManagementProcessor#addNewService(com.cws.esolutions.core.processors.dto.ServiceManagementRequest)
     */
    public ServiceManagementResponse addNewService(final ServiceManagementRequest request) throws ServiceManagementException
    {
        final String methodName = IServiceManagementProcessor.CNAME + "#addNewService(final ServiceManagementRequest request) throws ServiceManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ServiceManagementRequest: {}", request);
        }

        ServiceManagementResponse response = new ServiceManagementResponse();

        final Service service = request.getService();
        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("Service: {}", service);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            // this will require admin and service authorization
        	AccessControlServiceRequest accessRequest = new AccessControlServiceRequest();
        	accessRequest.setUserAccount(userAccount);
        	accessRequest.setServiceGuid(request.getServiceId());

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
                try
                {
                    AuditEntry auditEntry = new AuditEntry();
                    auditEntry.setHostInfo(reqInfo);
                    auditEntry.setAuditType(AuditType.CREATEDNSRECORD);
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

                return response;
            }

            if (service == null)
            {
                throw new ServiceManagementException("No platform was provided. Cannot continue.");
            }

            // make sure all the platform data is there
            List<Object[]> validator = null;

            try
            {
                validator = serviceDao.getServicesByAttribute(service.getName(), request.getStartPage());
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
                List<String> serverList = new ArrayList<String>();
                for (Server server : service.getServers())
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

                List<String> insertData = new ArrayList<String>(
                        Arrays.asList(
                                UUID.randomUUID().toString(), // GUID
                                service.getType().name(), // SERVICE_TYPE
                                service.getName(), // NAME
                                service.getRegion().name(), // REGION
                                service.getPartition().name(), // NWPARTITION
                                service.getStatus().name(), // STATUS
                                serverList.toString(), // SERVERS
                                service.getDescription())); // DESCRIPTION
                
                if (DEBUG)
                {
                    for (Object str : insertData)
                    {
                        DEBUGGER.debug("Value: {}", str);
                    }
                }

                boolean isComplete = serviceDao.addService(insertData);

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
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new ServiceManagementException(sqx.getMessage(), sqx);
        }
        catch (AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);
            
            throw new ServiceManagementException(acsx.getMessage(), acsx);
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
     * @see com.cws.esolutions.core.processors.interfaces.IServiceManagementProcessor#updateServiceData(com.cws.esolutions.core.processors.dto.ServiceManagementRequest)
     */
    public ServiceManagementResponse updateServiceData(final ServiceManagementRequest request) throws ServiceManagementException
    {
        final String methodName = IServiceManagementProcessor.CNAME + "#updateServiceData(final ServiceManagementRequest request) throws ServiceManagementException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ServiceManagementRequest: {}", request);
        }

        ServiceManagementResponse response = new ServiceManagementResponse();

        final Service service = request.getService();
        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("Service: {}", service);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            // this will require admin and service authorization
        	AccessControlServiceRequest accessRequest = new AccessControlServiceRequest();
        	accessRequest.setUserAccount(userAccount);
        	accessRequest.setServiceGuid(request.getServiceId());

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
                try
                {
                    AuditEntry auditEntry = new AuditEntry();
                    auditEntry.setHostInfo(reqInfo);
                    auditEntry.setAuditType(AuditType.CREATEDNSRECORD);
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

                return response;
            }

            List<String> serverList = new ArrayList<String>();
            for (Server server : service.getServers())
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

            List<String> insertData = new ArrayList<String>(
                    Arrays.asList(
                            service.getGuid(),
                            service.getName(),
                            service.getRegion().name(),
                            service.getPartition().name(),
                            service.getStatus().name(),
                            serverList.toString(),
                            service.getDescription()));

            if (DEBUG)
            {
                for (Object str : insertData)
                {
                    DEBUGGER.debug("Value: {}", str);
                }
            }

            boolean isComplete = serviceDao.updateService(insertData);

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
        catch (AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);

            throw new ServiceManagementException(acsx.getMessage(), acsx);
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new ServiceManagementException(sqx.getMessage(), sqx);
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
     * @see com.cws.esolutions.core.processors.interfaces.IServiceManagementProcessor#removeServiceData(com.cws.esolutions.core.processors.dto.ServiceManagementRequest)
     */
    public ServiceManagementResponse removeServiceData(final ServiceManagementRequest request) throws ServiceManagementException
    {
        final String methodName = IServiceManagementProcessor.CNAME + "#removeServiceData(final ServiceManagementRequest request) throws ServiceManagementException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ServiceManagementRequest: {}", request);
        }

        ServiceManagementResponse response = new ServiceManagementResponse();

        final Service service = request.getService();
        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("Service: {}", service);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            // this will require admin and service authorization
        	AccessControlServiceRequest accessRequest = new AccessControlServiceRequest();
        	accessRequest.setUserAccount(userAccount);
        	accessRequest.setServiceGuid(request.getServiceId());

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
                try
                {
                    AuditEntry auditEntry = new AuditEntry();
                    auditEntry.setHostInfo(reqInfo);
                    auditEntry.setAuditType(AuditType.CREATEDNSRECORD);
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

                return response;
            }

            boolean isComplete = serviceDao.removeService(service.getGuid());

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
        catch (AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);

            throw new ServiceManagementException(acsx.getMessage(), acsx);
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new ServiceManagementException(sqx.getMessage(), sqx);
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
     * @see com.cws.esolutions.core.processors.interfaces.IServiceManagementProcessor#listServices(com.cws.esolutions.core.processors.dto.ServiceManagementRequest)
     */
    public ServiceManagementResponse listServices(final ServiceManagementRequest request) throws ServiceManagementException
    {
        final String methodName = IServiceManagementProcessor.CNAME + "#listServices(final ServiceManagementRequest request) throws ServiceManagementException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ServiceManagementRequest: {}", request);
        }

        ServiceManagementResponse response = new ServiceManagementResponse();

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
        	accessRequest.setUserAccount(userAccount);
        	accessRequest.setServiceGuid(request.getServiceId());

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
                try
                {
                    AuditEntry auditEntry = new AuditEntry();
                    auditEntry.setHostInfo(reqInfo);
                    auditEntry.setAuditType(AuditType.CREATEDNSRECORD);
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

                return response;
            }

            List<String[]> serviceData = serviceDao.listServices(request.getStartPage());

            if (DEBUG)
            {
                DEBUGGER.debug("serviceData: {}", serviceData);
            }

            if ((serviceData != null) && (serviceData.size() != 0))
            {
                List<Service> serviceList = new ArrayList<Service>();

                for (String[] data : serviceData)
                {
                    Service service = new Service();
                    service.setGuid(data[0]);
                    service.setType(ServiceType.valueOf(data[1]));
                    service.setName(data[2]);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Service: {}", service);
                    }

                    serviceList.add(service);
                }

                if (DEBUG)
                {
                    DEBUGGER.debug("serviceList: {}", serviceList);
                }

                // response.setEntryCount(count); // TODO
                response.setServiceList(serviceList);
                response.setRequestStatus(CoreServicesStatus.SUCCESS);
            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.FAILURE);
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new ServiceManagementException(sqx.getMessage(), sqx);
        }
        catch (AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);
            
            throw new ServiceManagementException(acsx.getMessage(), acsx);
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
     * @see com.cws.esolutions.core.processors.interfaces.IServiceManagementProcessor#listServicesByType(com.cws.esolutions.core.processors.dto.ServiceManagementRequest)
     */
    public ServiceManagementResponse listServicesByType(final ServiceManagementRequest request) throws ServiceManagementException
    {
        final String methodName = IServiceManagementProcessor.CNAME + "#listServicesByType(final ServiceManagementRequest request) throws ServiceManagementException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ServiceManagementRequest: {}", request);
        }

        ServiceManagementResponse response = new ServiceManagementResponse();

        final Service service = request.getService();
        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("Service: {}", service);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            // this will require admin and service authorization
        	AccessControlServiceRequest accessRequest = new AccessControlServiceRequest();
        	accessRequest.setUserAccount(userAccount);
        	accessRequest.setServiceGuid(request.getServiceId());

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
                try
                {
                    AuditEntry auditEntry = new AuditEntry();
                    auditEntry.setHostInfo(reqInfo);
                    auditEntry.setAuditType(AuditType.CREATEDNSRECORD);
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

                return response;
            }

            List<String[]> serviceData = serviceDao.listServices(request.getStartPage());

            if (DEBUG)
            {
                DEBUGGER.debug("serviceData: {}", serviceData);
            }

            if ((serviceData != null) && (serviceData.size() != 0))
            {
                List<Service> serviceList = new ArrayList<Service>();

                for (String[] data : serviceData)
                {
                    if (ServiceType.valueOf(data[1]) == service.getType())
                    {
                        Service resService = new Service();
                        resService.setGuid(data[0]);
                        resService.setType(ServiceType.valueOf(data[1]));
                        resService.setName(data[2]);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Service: {}", resService);
                        }

                        serviceList.add(resService);
                    }
                }

                if (DEBUG)
                {
                    DEBUGGER.debug("serviceList: {}", serviceList);
                }

                // response.setEntryCount(count); // TODO
                response.setServiceList(serviceList);
                response.setRequestStatus(CoreServicesStatus.SUCCESS);
            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.FAILURE);
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new ServiceManagementException(sqx.getMessage(), sqx);
        }
        catch (AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);
            
            throw new ServiceManagementException(acsx.getMessage(), acsx);
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
     * @see com.cws.esolutions.core.processors.interfaces.IServiceManagementProcessor#getServiceByAttribute(com.cws.esolutions.core.processors.dto.ServiceManagementRequest)
     */
    public ServiceManagementResponse getServiceByAttribute(final ServiceManagementRequest request) throws ServiceManagementException
    {
        final String methodName = IServiceManagementProcessor.CNAME + "#getServiceByAttribute(final ServiceManagementRequest request) throws ServiceManagementException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ServiceManagementRequest: {}", request);
        }

        ServiceManagementResponse response = new ServiceManagementResponse();

        final Service service = request.getService();
        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("Service: {}", service);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            // this will require admin and service authorization
        	AccessControlServiceRequest accessRequest = new AccessControlServiceRequest();
        	accessRequest.setUserAccount(userAccount);
        	accessRequest.setServiceGuid(request.getServiceId());

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
                try
                {
                    AuditEntry auditEntry = new AuditEntry();
                    auditEntry.setHostInfo(reqInfo);
                    auditEntry.setAuditType(AuditType.CREATEDNSRECORD);
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

                return response;
            }

            List<Object[]> serviceData = serviceDao.getServicesByAttribute(service.getName(), request.getStartPage());

            if (DEBUG)
            {
                DEBUGGER.debug("serviceData: {}", serviceData);
            }

            if ((serviceData != null) && (serviceData.size() != 0))
            {
                List<Service> serviceList = new ArrayList<Service>();

                for (Object[] data : serviceData)
                {
                    Service resService = new Service();
                    service.setGuid((String) data[0]);
                    service.setName((String) data[1]);
                    service.setScore(new Double(data[2].toString()));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Service: {}", resService);
                    }

                    serviceList.add(resService);
                }

                if (DEBUG)
                {
                    DEBUGGER.debug("serviceList: {}", serviceList);
                }

                response.setServiceList(serviceList);
                response.setRequestStatus(CoreServicesStatus.SUCCESS);
            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.FAILURE);
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new ServiceManagementException(sqx.getMessage(), sqx);
        }
        catch (AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);
            
            throw new ServiceManagementException(acsx.getMessage(), acsx);
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
     * @see com.cws.esolutions.core.processors.interfaces.IServiceManagementProcessor#getServiceData(com.cws.esolutions.core.processors.dto.ServiceManagementRequest)
     */
    public ServiceManagementResponse getServiceData(final ServiceManagementRequest request) throws ServiceManagementException
    {
        final String methodName = IServiceManagementProcessor.CNAME + "#getServiceData(final ServiceManagementRequest request) throws ServiceManagementException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ServiceManagementRequest: {}", request);
        }

        ServiceManagementResponse response = new ServiceManagementResponse();

        final Service service = request.getService();
        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("Service: {}", service);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            // this will require admin and service authorization
        	AccessControlServiceRequest accessRequest = new AccessControlServiceRequest();
        	accessRequest.setUserAccount(userAccount);
        	accessRequest.setServiceGuid(request.getServiceId());

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
                try
                {
                    AuditEntry auditEntry = new AuditEntry();
                    auditEntry.setHostInfo(reqInfo);
                    auditEntry.setAuditType(AuditType.CREATEDNSRECORD);
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

                return response;
            }

            List<Server> serverList = null;
            List<String> serviceData = serviceDao.getService(service.getGuid());

            if (DEBUG)
            {
                DEBUGGER.debug("serviceData: {}", serviceData);
            }

            if ((serviceData != null) && (serviceData.size() != 0))
            {
                if (ServiceType.valueOf(serviceData.get(0)) == ServiceType.PLATFORM)
                {
                    String appTmp = StringUtils.remove(serviceData.get(5), "["); // PLATFORM_SERVERS
                    String platformServers = StringUtils.remove(appTmp, "]");

                    if (DEBUG)
                    {
                        DEBUGGER.debug("String: {}", platformServers);
                    }

                    if (platformServers.split(",").length >= 1)
                    {
                        serverList = new ArrayList<Server>();

                        for (String serverGuid : platformServers.split(","))
                        {
                            List<Object> serverData = serverDao.getServer(StringUtils.trim(serverGuid));

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
                }

                Service resService = new Service();
                resService.setGuid(service.getGuid());
                resService.setType(ServiceType.valueOf(serviceData.get(0)));
                resService.setName(serviceData.get(1));
                resService.setRegion(ServiceRegion.valueOf(serviceData.get(2)));
                resService.setPartition(NetworkPartition.valueOf(serviceData.get(3)));
                resService.setServers(serverList);
                resService.setStatus(ServiceStatus.valueOf(serviceData.get(4)));
                resService.setDescription(serviceData.get(6));

                if (DEBUG)
                {
                    DEBUGGER.debug("Service: {}", resService);
                }

                response.setRequestStatus(CoreServicesStatus.SUCCESS);
                response.setService(resService);
            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.FAILURE);
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new ServiceManagementException(sqx.getMessage(), sqx);
        }
        catch (AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);
            
            throw new ServiceManagementException(acsx.getMessage(), acsx);
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
