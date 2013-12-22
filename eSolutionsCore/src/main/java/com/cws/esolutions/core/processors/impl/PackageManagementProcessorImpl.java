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
 * File: PackageManagementProcessorImpl.java
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
import java.math.BigDecimal;
import java.sql.SQLException;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.core.processors.dto.Package;
import com.cws.esolutions.security.audit.dto.AuditEntry;
import com.cws.esolutions.security.audit.enums.AuditType;
import com.cws.esolutions.security.audit.dto.AuditRequest;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.core.processors.dto.PackageManagementRequest;
import com.cws.esolutions.core.processors.dto.PackageManagementResponse;
import com.cws.esolutions.security.audit.exception.AuditServiceException;
import com.cws.esolutions.core.processors.exception.PackageManagementException;
import com.cws.esolutions.core.processors.interfaces.IPackageManagementProcessor;
import com.cws.esolutions.security.access.control.exception.UserControlServiceException;
import com.cws.esolutions.security.access.control.exception.AdminControlServiceException;
/**
 * @see com.cws.esolutions.core.processors.interfaces.IPackageManagementProcessor
 */
public class PackageManagementProcessorImpl implements IPackageManagementProcessor
{
    /**
     * @see com.cws.esolutions.core.processors.interfaces.IPackageManagementProcessor#addNewPackage(com.cws.esolutions.core.processors.dto.PackageManagementRequest)
     */
    @Override
    public PackageManagementResponse addNewPackage(final PackageManagementRequest request) throws PackageManagementException
    {
        final String methodName = IPackageManagementProcessor.CNAME + "#addNewPackage(final PackageManagementRequest request) throws PackageManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("PackageManagementRequest: {}", request);
        }

        PackageManagementResponse response = new PackageManagementResponse();

        final Package reqPackage = request.getPackageValue();
        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("Package: {}", reqPackage);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

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
                String packageGuid = UUID.randomUUID().toString();

                if (DEBUG)
                {
                    DEBUGGER.debug("packageGuid: {}", packageGuid);
                }

                List<Object> validator = null;
                response = new PackageManagementResponse();

                try
                {
                    validator = dao.getPackageData(packageGuid);
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
                    // ok, good platform. we can add the application in
                    List<Object> dataList = new ArrayList<Object>(
                            Arrays.asList(
                                    packageGuid,
                                    reqPackage.getPackageName(),
                                    reqPackage.getPackageVersion(),
                                    reqPackage.getPackageLocation(),
                                    reqPackage.getPackageInstaller()));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("dataList: {}", dataList);
                    }

                    boolean isComplete = dao.addNewPackage(dataList);

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
                    // project already exists
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

            throw new PackageManagementException(sqx.getMessage(), sqx);
        }
        catch (UserControlServiceException ucsx)
        {
            ERROR_RECORDER.error(ucsx.getMessage(), ucsx);
            
            throw new PackageManagementException(ucsx.getMessage(), ucsx);
        }
        catch (AdminControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);
            
            throw new PackageManagementException(acsx.getMessage(), acsx);
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
     * @see com.cws.esolutions.core.processors.interfaces.IPackageManagementProcessor#updatePackageData(com.cws.esolutions.core.processors.dto.PackageManagementRequest)
     */
    @Override
    public PackageManagementResponse updatePackageData(final PackageManagementRequest request) throws PackageManagementException
    {
        final String methodName = IPackageManagementProcessor.CNAME + "#updatePackageData(final PackageManagementRequest request) throws PackageManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("PackageManagementRequest: {}", request);
        }

        PackageManagementResponse response = new PackageManagementResponse();

        final Package reqPackage = request.getPackageValue();
        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("Package: {}", reqPackage);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

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
                // get the current application information
				// ok, good platform. we can add the application in
				List<Object> dataList = new ArrayList<Object>(
					Arrays.asList(
						reqPackage.getPackageGuid(),
						reqPackage.getPackageName(),
						reqPackage.getPackageVersion(),
						reqPackage.getPackageLocation(),
						reqPackage.getPackageInstaller()));

				if (DEBUG)
				{
					DEBUGGER.debug("dataList: {}", dataList);
				}

				boolean isComplete = dao.addNewPackage(dataList);

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
        catch (UserControlServiceException ucsx)
        {
            ERROR_RECORDER.error(ucsx.getMessage(), ucsx);
            
            throw new PackageManagementException(ucsx.getMessage(), ucsx);
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new PackageManagementException(sqx.getMessage(), sqx);
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
     * @see com.cws.esolutions.core.processors.interfaces.IPackageManagementProcessor#removePackageData(com.cws.esolutions.core.processors.dto.PackageManagementRequest)
     */
    @Override
    public PackageManagementResponse removePackageData(final PackageManagementRequest request) throws PackageManagementException
    {
        final String methodName = IPackageManagementProcessor.CNAME + "#removePackageData(final PackageManagementRequest request) throws PackageManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("PackageManagementRequest: {}", request);
        }

        PackageManagementResponse response = new PackageManagementResponse();

        final Package reqPackage = request.getPackageValue();
        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("Package: {}", reqPackage);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

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
                boolean isComplete = dao.deletePackage(reqPackage.getPackageGuid());

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

            throw new PackageManagementException(sqx.getMessage(), sqx);
        }
        catch (UserControlServiceException ucsx)
        {
            ERROR_RECORDER.error(ucsx.getMessage(), ucsx);
            
            throw new PackageManagementException(ucsx.getMessage(), ucsx);
        }
        catch (AdminControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);
            
            throw new PackageManagementException(acsx.getMessage(), acsx);
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
     * @see com.cws.esolutions.core.processors.interfaces.IPackageManagementProcessor#getPackageData(com.cws.esolutions.core.processors.dto.PackageManagementRequest)
     */
    @Override
    public PackageManagementResponse getPackageData(final PackageManagementRequest request) throws PackageManagementException
    {
        final String methodName = IPackageManagementProcessor.CNAME + "#getPackageData(final PackageManagementRequest request) throws PackageManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("PackageManagementRequest: {}", request);
        }

        PackageManagementResponse response = new PackageManagementResponse();

        final Package reqPackage = request.getPackageValue();
        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("Package: {}", reqPackage);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

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
                List<Object> pkgData = dao.getPackageData(reqPackage.getPackageGuid());

                if (DEBUG)
                {
                    DEBUGGER.debug("List<Object>: {}", pkgData);
                }

                if ((pkgData != null) && (pkgData.size() != 0))
                {
                    Package resPackage = new Package();
                    resPackage.setPackageGuid((String) pkgData.get(0));
					reqPackage.setPackageName((String) pkgData.get(1));
					reqPackage.setPackageVersion((BigDecimal) pkgData.get(2));
					reqPackage.setPackageLocation((String) pkgData.get(3));
					reqPackage.setPackageInstaller((String) pkgData.get(4));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Package: {}", resPackage);
                    }

                    response.setPackageValue(resPackage);
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
                DEBUGGER.debug("PackageManagementResponse: {}", response);
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new PackageManagementException(sqx.getMessage(), sqx);
        }
        catch (UserControlServiceException ucsx)
        {
            ERROR_RECORDER.error(ucsx.getMessage(), ucsx);
            
            throw new PackageManagementException(ucsx.getMessage(), ucsx);
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
}
