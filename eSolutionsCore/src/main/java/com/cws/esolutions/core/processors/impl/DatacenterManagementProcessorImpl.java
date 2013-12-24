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
 * @see com.cws.esolutions.core.processors.interfaces.IDatacenterManagementProcessor
 */
import java.util.List;
import java.util.UUID;
import java.util.Arrays;
import java.util.ArrayList;
import java.sql.SQLException;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.audit.dto.AuditEntry;
import com.cws.esolutions.security.audit.enums.AuditType;
import com.cws.esolutions.core.processors.dto.DataCenter;
import com.cws.esolutions.security.audit.dto.AuditRequest;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.enums.ServiceStatus;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.security.audit.exception.AuditServiceException;
import com.cws.esolutions.core.processors.dto.DatacenterManagementRequest;
import com.cws.esolutions.core.processors.dto.DatacenterManagementResponse;
import com.cws.esolutions.core.processors.exception.DatacenterManagementException;
import com.cws.esolutions.core.processors.interfaces.IDatacenterManagementProcessor;
import com.cws.esolutions.security.services.exception.AccessControlServiceException;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.processors.impl
 * File: DatacenterManagementProcessorImpl.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
public class DatacenterManagementProcessorImpl implements IDatacenterManagementProcessor
{
    @Override
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

        final DataCenter dataCenter = request.getDataCenter();
        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("DataCenter: {}", dataCenter);
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
                if (dataCenter == null)
                {
                    throw new DatacenterManagementException("No server was provided. Cannot continue.");
                }

                // make sure all the platform data is there
                List<String[]> validator = null;

                try
                {
                    validator = datactrDAO.getDataCenterByAttribute(dataCenter.getDatacenterName(), request.getStartPage());
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
                    // valid server
                    List<String> insertData = new ArrayList<>(
                            Arrays.asList(
                                    UUID.randomUUID().toString(),
                                    dataCenter.getDatacenterName(),
                                    dataCenter.getDatacenterStatus().name(),
                                    dataCenter.getDatacenterDesc()));

                    if (DEBUG)
                    {
                        for (Object str : insertData)
                        {
                            DEBUGGER.debug("Value: {}", str);
                        }
                    }

                    boolean isComplete = datactrDAO.addNewDatacenter(insertData);

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

            throw new DatacenterManagementException(sqx.getMessage(), sqx);
        }
        catch (AccessControlServiceException acsx)
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
     * @see com.cws.esolutions.core.processors.interfaces.IDatacenterManagementProcessor#updateDatacenter(com.cws.esolutions.core.processors.dto.DatacenterManagementRequest)
     */
    @Override
    public DatacenterManagementResponse updateDatacenter(final DatacenterManagementRequest request) throws DatacenterManagementException
    {
        final String methodName = IDatacenterManagementProcessor.CNAME + "#updateDatacenter(final DatacenterManagementRequest request) throws DatacenterManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("DatacenterManagementRequest: {}", request);
        }

        DatacenterManagementResponse response = new DatacenterManagementResponse();

        final DataCenter dataCenter = request.getDataCenter();
        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("DataCenter: {}", dataCenter);
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
                if (dataCenter == null)
                {
                    throw new DatacenterManagementException("No datacenter was provided. Cannot continue.");
                }

                List<String> insertData = new ArrayList<>(
                    Arrays.asList(
                        dataCenter.getDatacenterGuid(),
                        dataCenter.getDatacenterName(),
                        dataCenter.getDatacenterStatus().name(),
                        dataCenter.getDatacenterDesc()));

                if (DEBUG)
                {
                    for (Object str : insertData)
                    {
                        DEBUGGER.debug("Value: {}", str);
                    }
                }

                boolean isComplete = datactrDAO.updateDatacenter(insertData);

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
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new DatacenterManagementException(sqx.getMessage(), sqx);
        }
        catch (AccessControlServiceException acsx)
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
                auditEntry.setAuditType(AuditType.UPDATEDATACENTER);
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
     * @see com.cws.esolutions.core.processors.interfaces.IDatacenterManagementProcessor#listDatacenters(com.cws.esolutions.core.processors.dto.DatacenterManagementRequest)
     */
    @Override
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
            boolean isServiceAuthorized = accessControl.isUserAuthorizedForService(userAccount, request.getServiceId());

            if (DEBUG)
            {
                DEBUGGER.debug("isServiceAuthorized: {}", isServiceAuthorized);
            }

            if (isServiceAuthorized)
            {
                int count = datactrDAO.getDatacenterCount();
                List<String[]> serverData = datactrDAO.getAvailableDataCenters(request.getStartPage());

                if (DEBUG)
                {
                    DEBUGGER.debug("serverList: {}", serverData);
                }

                if ((serverData != null) && (serverData.size() != 0))
                {
                    List<DataCenter> datacenterList = new ArrayList<>();

                    for (String[] data : serverData)
                    {
                        if (DEBUG)
                        {
                            for (String str : data)
                            {
                                DEBUGGER.debug("Data: {}", str);
                            }
                        }

                        DataCenter dataCenter = new DataCenter();
                        dataCenter.setDatacenterGuid(data[0]);
                        dataCenter.setDatacenterName(data[1]);
                        dataCenter.setDatacenterStatus(ServiceStatus.valueOf(data[2]));
                        dataCenter.setDatacenterDesc(data[3]);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("DataCenter: {}", dataCenter);
                        }

                        datacenterList.add(dataCenter);
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("datacenterList: {}", datacenterList);
                    }

                    response.setEntryCount(count);
                    response.setRequestStatus(CoreServicesStatus.SUCCESS);
                    response.setDatacenterList(datacenterList);
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

            throw new DatacenterManagementException(sqx.getMessage(), sqx);
        }
        catch (AccessControlServiceException acsx)
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
     * @see com.cws.esolutions.core.processors.interfaces.IDatacenterManagementProcessor#getDatacenter(com.cws.esolutions.core.processors.dto.DatacenterManagementRequest)
     */
    @Override
    public DatacenterManagementResponse getDatacenter(final DatacenterManagementRequest request) throws DatacenterManagementException
    {
        final String methodName = IDatacenterManagementProcessor.CNAME + "#getDatacenter(final DatacenterManagementRequest request) throws DatacenterManagementException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("DatacenterManagementRequest: {}", request);
        }

        DatacenterManagementResponse response = new DatacenterManagementResponse();

        final DataCenter dataCenter = request.getDataCenter();
        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("DataCenter: {}", dataCenter);
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
                if (dataCenter != null)
                {
                    List<String> data = datactrDAO.getDatacenter(dataCenter.getDatacenterGuid());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("data: {}", data);
                    }

                    if ((data != null) && (data.size() != 0))
                    {
                        DataCenter datactr = new DataCenter();
                        datactr.setDatacenterGuid(data.get(0));
                        datactr.setDatacenterName(data.get(1));
                        datactr.setDatacenterStatus(ServiceStatus.valueOf(data.get(2)));
                        datactr.setDatacenterDesc(data.get(3));

                        if (DEBUG)
                        {
                            DEBUGGER.debug("DataCenter: {}", datactr);
                        }

                        response.setRequestStatus(CoreServicesStatus.SUCCESS);
                        response.setDataCenter(datactr);
                    }
                    else
                    {
                        response.setRequestStatus(CoreServicesStatus.FAILURE);
                    }
                }
                else
                {
                    ERROR_RECORDER.error("No server search data was provided. Cannot continue");

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

            throw new DatacenterManagementException(sqx.getMessage(), sqx);
        }
        catch (AccessControlServiceException acsx)
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
