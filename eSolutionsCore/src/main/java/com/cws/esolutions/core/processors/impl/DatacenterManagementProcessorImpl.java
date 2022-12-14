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
import java.util.Objects;
import java.util.ArrayList;
import java.sql.SQLException;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.core.enums.CoreServicesStatus;
import com.cws.esolutions.core.processors.dto.Datacenter;
import com.cws.esolutions.security.processors.dto.AuditEntry;
import com.cws.esolutions.security.processors.enums.AuditType;
import com.cws.esolutions.core.processors.enums.ServiceStatus;
import com.cws.esolutions.security.processors.dto.AuditRequest;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.dto.DatacenterManagementRequest;
import com.cws.esolutions.core.processors.dto.DatacenterManagementResponse;
import com.cws.esolutions.security.services.dto.AccessControlServiceRequest;
import com.cws.esolutions.security.services.dto.AccessControlServiceResponse;
import com.cws.esolutions.security.processors.exception.AuditServiceException;
import com.cws.esolutions.core.processors.exception.DatacenterManagementException;
import com.cws.esolutions.core.processors.interfaces.IDatacenterManagementProcessor;
import com.cws.esolutions.security.services.exception.AccessControlServiceException;
/**
 * @see com.cws.esolutions.core.processors.interfaces.IDatacenterManagementProcessor
 */
public class DatacenterManagementProcessorImpl implements IDatacenterManagementProcessor
{
    /**
     * @see com.cws.esolutions.core.processors.interfaces.IDatacenterManagementProcessor#addNewDatacenter(com.cws.esolutions.core.processors.dto.DatacenterManagementRequest)
     */
    public DatacenterManagementResponse addNewDatacenter(final DatacenterManagementRequest request) throws DatacenterManagementException
    {
        final String methodName = IDatacenterManagementProcessor.CNAME + "#addNewDatacenter(final DatacenterManagementRequest request) throws DatacenterManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("DatacenterManagementRequest: {}", request);
        }

        DatacenterManagementResponse response = new DatacenterManagementResponse();

        final Datacenter dataCenter = request.getDatacenter();
        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("Datacenter: {}", dataCenter);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            // this will require admin and service authorization
            AccessControlServiceRequest accessRequest = new AccessControlServiceRequest();
            accessRequest.setUserAccount(userAccount);

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
                    auditEntry.setAuditType(AuditType.ADDDATACENTER);
                    auditEntry.setUserAccount(userAccount);
                    auditEntry.setAuthorized(Boolean.FALSE);
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
                catch (final AuditServiceException asx)
                {
                    ERROR_RECORDER.error(asx.getMessage(), asx);
                }

                return response;
            }

            if (Objects.isNull(dataCenter))
            {
                throw new DatacenterManagementException("No datacenter was provided. Cannot continue.");
            }

            // make sure all the platform data is there
            List<Object[]> validator = null;

            try
            {
                validator = datacenterDao.getDatacentersByAttribute(dataCenter.getName(), request.getStartPage());
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
                List<String> insertData = new ArrayList<String>(
                        Arrays.asList(
                                UUID.randomUUID().toString(), // GUID
                                dataCenter.getName(), // DCNAME
                                dataCenter.getStatus().name(), // STATUS
                                dataCenter.getDescription())); // DESCRIPTION
                
                if (DEBUG)
                {
                    for (Object str : insertData)
                    {
                        DEBUGGER.debug("Value: {}", str);
                    }
                }

                boolean isComplete = datacenterDao.addDatacenter(insertData);

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
                	ERROR_RECORDER.error("An error occurred while adding the new information.");

                    response.setRequestStatus(CoreServicesStatus.FAILURE);
                }
            }
            else
            {
            	ERROR_RECORDER.error("An entry already exists for the given information.");

                response.setRequestStatus(CoreServicesStatus.FAILURE);
            }
        }
        catch (final SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new DatacenterManagementException(sqx.getMessage(), sqx);
        }
        catch (final AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);
            
            throw new DatacenterManagementException(acsx.getMessage(), acsx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.ADDDATACENTER);
                auditEntry.setUserAccount(userAccount);
                auditEntry.setAuthorized(Boolean.TRUE);
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
            catch (final AuditServiceException asx)
            {
                ERROR_RECORDER.error(asx.getMessage(), asx);
            }
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.core.processors.interfaces.IDatacenterManagementProcessor#updateDatacenter(com.cws.esolutions.core.processors.dto.DatacenterManagementRequest)
     */
    public DatacenterManagementResponse updateDatacenter(final DatacenterManagementRequest request) throws DatacenterManagementException
    {
        final String methodName = IDatacenterManagementProcessor.CNAME + "#updateDatacenter(final DatacenterManagementRequest request) throws DatacenterManagementException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("DatacenterManagementRequest: {}", request);
        }

        DatacenterManagementResponse response = new DatacenterManagementResponse();

        final Datacenter dataCenter = request.getDatacenter();
        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("Datacenter: {}", dataCenter);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            // this will require admin and service authorization
            AccessControlServiceRequest accessRequest = new AccessControlServiceRequest();
            accessRequest.setUserAccount(userAccount);

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
                    auditEntry.setAuditType(AuditType.UPDATEDATACENTER);
                    auditEntry.setUserAccount(userAccount);
                    auditEntry.setAuthorized(Boolean.FALSE);
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
                catch (final AuditServiceException asx)
                {
                    ERROR_RECORDER.error(asx.getMessage(), asx);
                }

                return response;
            }

            List<String> insertData = new ArrayList<String>(
                    Arrays.asList(
                    		dataCenter.getGuid(),
                    		dataCenter.getName(),
                    		dataCenter.getStatus().toString(),
                    		dataCenter.getDescription()));

            if (DEBUG)
            {
                for (Object str : insertData)
                {
                    DEBUGGER.debug("Value: {}", str);
                }
            }

            boolean isComplete = datacenterDao.updateDatacenter(insertData);

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
        catch (final AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);

            throw new DatacenterManagementException(acsx.getMessage(), acsx);
        }
        catch (final SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new DatacenterManagementException(sqx.getMessage(), sqx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.UPDATEDATACENTER);
                auditEntry.setUserAccount(userAccount);
                auditEntry.setAuthorized(Boolean.TRUE);
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
            catch (final AuditServiceException asx)
            {
                ERROR_RECORDER.error(asx.getMessage(), asx);
            }
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.core.processors.interfaces.IDatacenterManagementProcessor#removeDatacenter(com.cws.esolutions.core.processors.dto.DatacenterManagementRequest)
     */
    public DatacenterManagementResponse removeDatacenter(final DatacenterManagementRequest request) throws DatacenterManagementException
    {
        final String methodName = IDatacenterManagementProcessor.CNAME + "#removeDatacenter(final DatacenterManagementRequest request) throws DatacenterManagementException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("DatacenterManagementRequest: {}", request);
        }

        DatacenterManagementResponse response = new DatacenterManagementResponse();

        final Datacenter dataCenter = request.getDatacenter();
        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("Datacenter: {}", dataCenter);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            // this will require admin and service authorization
            AccessControlServiceRequest accessRequest = new AccessControlServiceRequest();
            accessRequest.setUserAccount(userAccount);

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
                    auditEntry.setAuditType(AuditType.DELETEDATACENTER);
                    auditEntry.setUserAccount(userAccount);
                    auditEntry.setAuthorized(Boolean.FALSE);
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
                catch (final AuditServiceException asx)
                {
                    ERROR_RECORDER.error(asx.getMessage(), asx);
                }

                return response;
            }

            boolean isComplete = datacenterDao.removeDatacenter(dataCenter.getGuid());

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
        catch (final AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);

            throw new DatacenterManagementException(acsx.getMessage(), acsx);
        }
        catch (final SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new DatacenterManagementException(sqx.getMessage(), sqx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.DELETEDATACENTER);
                auditEntry.setUserAccount(userAccount);
                auditEntry.setAuthorized(Boolean.TRUE);
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
            catch (final AuditServiceException asx)
            {
                ERROR_RECORDER.error(asx.getMessage(), asx);
            }
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.core.processors.interfaces.IDatacenterManagementProcessor#listDatacenters(com.cws.esolutions.core.processors.dto.DatacenterManagementRequest)
     */
    public DatacenterManagementResponse listDatacenters(final DatacenterManagementRequest request) throws DatacenterManagementException
    {
        final String methodName = IDatacenterManagementProcessor.CNAME + "#listDatacenters(final DatacenterManagementRequest request) throws DatacenterManagementException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("DatacenterManagementRequest: {}", request);
        }

        DatacenterManagementResponse response = new DatacenterManagementResponse();

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
                    auditEntry.setAuditType(AuditType.LISTDATACENTERS);
                    auditEntry.setUserAccount(userAccount);
                    auditEntry.setAuthorized(Boolean.FALSE);
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
                catch (final AuditServiceException asx)
                {
                    ERROR_RECORDER.error(asx.getMessage(), asx);
                }

                return response;
            }

            List<String[]> datacenters = datacenterDao.listDatacenters(request.getStartPage());

            if (DEBUG)
            {
                DEBUGGER.debug("datacenters: {}", datacenters);
            }

            if ((datacenters != null) && (datacenters.size() != 0))
            {
                List<Datacenter> datacenterList = new ArrayList<Datacenter>();

                for (String[] data : datacenters)
                {
                	Datacenter dataCenter = new Datacenter();
                	dataCenter.setGuid(data[0]);
                	dataCenter.setName(data[1]);
                	dataCenter.setStatus(ServiceStatus.valueOf(data[2]));
                	dataCenter.setDescription(data[3]);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Datacenter: {}", dataCenter);
                    }

                    datacenterList.add(dataCenter);
                }

                if (DEBUG)
                {
                    DEBUGGER.debug("dataCenterList: {}", datacenterList);
                }

                // response.setEntryCount(count); // TODO
                response.setDatacenterList(datacenterList);
                response.setRequestStatus(CoreServicesStatus.SUCCESS);
            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.FAILURE);
            }
        }
        catch (final SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new DatacenterManagementException(sqx.getMessage(), sqx);
        }
        catch (final AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);
            
            throw new DatacenterManagementException(acsx.getMessage(), acsx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.LISTDATACENTERS);
                auditEntry.setUserAccount(userAccount);
                auditEntry.setAuthorized(Boolean.TRUE);
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
            catch (final AuditServiceException asx)
            {
                ERROR_RECORDER.error(asx.getMessage(), asx);
            }
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.core.processors.interfaces.IDatacenterManagementProcessor#getDatacenterByAttribute(com.cws.esolutions.core.processors.dto.DatacenterManagementRequest)
     */
    public DatacenterManagementResponse getDatacenterByAttribute(final DatacenterManagementRequest request) throws DatacenterManagementException
    {
        final String methodName = IDatacenterManagementProcessor.CNAME + "#getDatacenterByAttribute(final DatacenterManagementRequest request) throws DatacenterManagementException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("DatacenterManagementRequest: {}", request);
        }

        DatacenterManagementResponse response = new DatacenterManagementResponse();

        final Datacenter service = request.getDatacenter();
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
                    auditEntry.setAuditType(AuditType.LISTDATACENTERS);
                    auditEntry.setUserAccount(userAccount);
                    auditEntry.setAuthorized(Boolean.FALSE);
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
                catch (final AuditServiceException asx)
                {
                    ERROR_RECORDER.error(asx.getMessage(), asx);
                }

                return response;
            }

            List<Object[]> datacenterData = datacenterDao.getDatacentersByAttribute(service.getName(), request.getStartPage());

            if (DEBUG)
            {
                DEBUGGER.debug("datacenterData: {}", datacenterData);
            }

            if ((datacenterData != null) && (datacenterData.size() != 0))
            {
                List<Datacenter> datacenterList = new ArrayList<Datacenter>();

                for (Object[] data : datacenterData)
                {
                	Datacenter dataCenter = new Datacenter();
                	dataCenter.setGuid((String) data[0]);
                	dataCenter.setName((String) data[1]);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Datacenter: {}", dataCenter);
                    }

                    datacenterList.add(dataCenter);
                }

                if (DEBUG)
                {
                    DEBUGGER.debug("datacenterList: {}", datacenterList);
                }

                response.setDatacenterList(datacenterList);
                response.setRequestStatus(CoreServicesStatus.SUCCESS);
            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.FAILURE);
            }
        }
        catch (final SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new DatacenterManagementException(sqx.getMessage(), sqx);
        }
        catch (final AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);
            
            throw new DatacenterManagementException(acsx.getMessage(), acsx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.LISTDATACENTERS);
                auditEntry.setUserAccount(userAccount);
                auditEntry.setAuthorized(Boolean.TRUE);
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
            catch (final AuditServiceException asx)
            {
                ERROR_RECORDER.error(asx.getMessage(), asx);
            }
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.core.processors.interfaces.IDatacenterManagementProcessor#getDatacenterData(com.cws.esolutions.core.processors.dto.DatacenterManagementRequest)
     */
    public DatacenterManagementResponse getDatacenterData(final DatacenterManagementRequest request) throws DatacenterManagementException
    {
        final String methodName = IDatacenterManagementProcessor.CNAME + "#getDatacenterData(final DatacenterManagementRequest request) throws DatacenterManagementException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("DatacenterManagementRequest: {}", request);
        }

        DatacenterManagementResponse response = new DatacenterManagementResponse();

        final Datacenter dataCenter = request.getDatacenter();
        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("Datacenter: {}", dataCenter);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            // this will require admin and service authorization
            AccessControlServiceRequest accessRequest = new AccessControlServiceRequest();
            accessRequest.setUserAccount(userAccount);

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
                    auditEntry.setAuditType(AuditType.LOADDATACENTER);
                    auditEntry.setUserAccount(userAccount);
                    auditEntry.setAuthorized(Boolean.FALSE);
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
                catch (final AuditServiceException asx)
                {
                    ERROR_RECORDER.error(asx.getMessage(), asx);
                }

                return response;
            }

            List<String> datacenterData = datacenterDao.getDatacenter(dataCenter.getGuid());

            if (DEBUG)
            {
                DEBUGGER.debug("datacenterData: {}", datacenterData);
            }

            if ((datacenterData != null) && (datacenterData.size() != 0))
            {
                Datacenter resDatacenter = new Datacenter();
                resDatacenter.setGuid(datacenterData.get(0));
                resDatacenter.setName(datacenterData.get(1));
                resDatacenter.setStatus(ServiceStatus.valueOf(datacenterData.get(2)));
                resDatacenter.setDescription(datacenterData.get(3));

                if (DEBUG)
                {
                    DEBUGGER.debug("Datacenter: {}", resDatacenter);
                }

                response.setRequestStatus(CoreServicesStatus.SUCCESS);
                response.setDatacenter(resDatacenter);
            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.FAILURE);
            }
        }
        catch (final SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new DatacenterManagementException(sqx.getMessage(), sqx);
        }
        catch (final AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);
            
            throw new DatacenterManagementException(acsx.getMessage(), acsx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.LOADDATACENTER);
                auditEntry.setUserAccount(userAccount);
                auditEntry.setAuthorized(Boolean.TRUE);
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
            catch (final AuditServiceException asx)
            {
                ERROR_RECORDER.error(asx.getMessage(), asx);
            }
        }

        return response;
    }
}
