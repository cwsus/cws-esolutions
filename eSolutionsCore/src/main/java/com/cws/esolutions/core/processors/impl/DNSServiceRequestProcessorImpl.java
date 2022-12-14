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
 * File: DNSServiceRequestProcessorImpl.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.util.List;
import java.util.ArrayList;
import java.sql.SQLException;
import java.security.Security;
import org.apache.commons.lang3.StringUtils;

import com.cws.esolutions.core.utils.NetworkUtils;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.core.CoreServicesConstants;
import com.cws.esolutions.core.enums.CoreServicesStatus;
import com.cws.esolutions.core.processors.dto.DNSEntry;
import com.cws.esolutions.core.processors.dto.DNSRecord;
import com.cws.esolutions.core.processors.enums.ServerType;
import com.cws.esolutions.security.processors.dto.AuditEntry;
import com.cws.esolutions.core.processors.enums.DNSRecordType;
import com.cws.esolutions.security.processors.enums.AuditType;
import com.cws.esolutions.security.processors.dto.AuditRequest;
import com.cws.esolutions.core.utils.exception.UtilityException;
import com.cws.esolutions.core.processors.dto.DNSServiceRequest;
import com.cws.esolutions.core.processors.dto.DNSServiceResponse;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.exception.DNSServiceException;
import com.cws.esolutions.security.services.dto.AccessControlServiceRequest;
import com.cws.esolutions.security.services.dto.AccessControlServiceResponse;
import com.cws.esolutions.security.processors.exception.AuditServiceException;
import com.cws.esolutions.core.processors.interfaces.IDNSServiceRequestProcessor;
import com.cws.esolutions.security.services.exception.AccessControlServiceException;
/**
 * @see com.cws.esolutions.core.processors.interfaces.IDNSServiceRequestProcessor
 */
public class DNSServiceRequestProcessorImpl implements IDNSServiceRequestProcessor
{
    /**
     * @see com.cws.esolutions.core.processors.interfaces.IDNSServiceRequestProcessor#performLookup(com.cws.esolutions.core.processors.dto.DNSServiceRequest)
     */
    public DNSServiceResponse performLookup(DNSServiceRequest request) throws DNSServiceException
    {
        final String methodName = IDNSServiceRequestProcessor.CNAME + "#performLookup(final DNSServiceRequest request) throws DNSServiceException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("DNSServiceRequest: {}", request);
        }

        DNSServiceResponse response = new DNSServiceResponse();

        final DNSRecord dnsRecord = request.getRecord();
        final String currentTimeout = Security.getProperty("networkaddress.cache.ttl");

        if (DEBUG)
        {
            DEBUGGER.debug("DNSRecord: {}", dnsRecord);
            DEBUGGER.debug("currentTimeout: {}", currentTimeout);
        }

        try
        {
            // no authorization required for service lookup
            if ((StringUtils.isNotEmpty(request.getResolverHost())) || (request.getUseSystemResolver()))
            {
                List<List<String>> responseData = NetworkUtils.executeDNSLookup(request.getResolverHost(), dnsRecord.getRecordName(), dnsRecord.getRecordType().toString(), request.getSearchPath());

                if (DEBUG)
                {
                    DEBUGGER.debug("responseData: {}", responseData);
                }

                List<DNSRecord> responseRecords = new ArrayList<DNSRecord>();

                for (List<String> responseInfo : responseData)
                {
                    if (DEBUG)
                    {
                        DEBUGGER.debug("responseInfo: {}", responseInfo);
                    }

                    DNSRecord responseRecord = new DNSRecord();
                    responseRecord.setRecordAddress(responseInfo.get(0));
                    responseRecord.setRecordName(responseInfo.get(1));
                    responseRecord.setRecordType(DNSRecordType.valueOf(responseInfo.get(2)));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("responseRecord: {}", responseRecord);
                    }

                    responseRecords.add(responseRecord);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("responseRecords: {}", responseRecords);
                    }
                }

                response.setDnsRecords(responseRecords);
                response.setRequestStatus(CoreServicesStatus.SUCCESS);
            }
            else
            {
                // this will run through the available slave servers
                List<Object[]> serverList = dao.getServersByAttribute(ServerType.DNSSLAVE.name(), 0);

                if (DEBUG)
                {
                    DEBUGGER.debug("serverList: {}", serverList);
                }

                if ((serverList != null) && (serverList.size() != 0))
                {
                    List<DNSRecord> responseRecords = new ArrayList<DNSRecord>();

                    for (Object[] data : serverList)
                    {
                        if (DEBUG)
                        {
                            DEBUGGER.debug("Value: {}", data);
                        }

                        String serverName = (String) data[15];

                        if (DEBUG)
                        {
                            DEBUGGER.debug("serverName: {}", serverName);
                        }

                        List<List<String>> responseData = NetworkUtils.executeDNSLookup(serverName, dnsRecord.getRecordName(), dnsRecord.getRecordType().toString(), request.getSearchPath());

                        if (DEBUG)
                        {
                            DEBUGGER.debug("responseData: {}", responseData);
                        }

                        for (List<String> responseInfo : responseData)
                        {
                            if (DEBUG)
                            {
                                DEBUGGER.debug("responseInfo: {}", responseInfo);
                            }

                            DNSRecord responseRecord = new DNSRecord();
                            responseRecord.setRecordAddress(responseInfo.get(0));
                            responseRecord.setRecordName(responseInfo.get(1));
                            responseRecord.setRecordType(DNSRecordType.valueOf(responseInfo.get(2)));

                            if (DEBUG)
                            {
                                DEBUGGER.debug("responseRecord: {}", responseRecord);
                            }

                            responseRecords.add(responseRecord);

                            if (DEBUG)
                            {
                                DEBUGGER.debug("responseRecords: {}", responseRecords);
                            }
                        }
                    }

                    response.setDnsRecords(responseRecords);
                    response.setRequestStatus(CoreServicesStatus.SUCCESS);
                }
                else
                {
                    response.setRequestStatus(CoreServicesStatus.FAILURE);
                }
            }
        }
        catch (final UtilityException ux)
        {
            ERROR_RECORDER.error(ux.getMessage(), ux);

            throw new DNSServiceException(ux.getMessage(), ux);
        }
        catch (final SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new DNSServiceException(sqx.getMessage(), sqx);
        }
        finally
        {
            // reset java dns timeout
            try
            {
                Security.setProperty("networkaddress.cache.ttl", currentTimeout);
            }
            catch (final NullPointerException npx) {}
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.core.processors.interfaces.IDNSServiceRequestProcessor#createNewService(com.cws.esolutions.core.processors.dto.DNSServiceRequest)
     */
    public DNSServiceResponse createNewService(final DNSServiceRequest request) throws DNSServiceException
    {
        final String methodName = IDNSServiceRequestProcessor.CNAME + "#createNewService(final DNSServiceRequest request) throws DNSServiceException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("DNSServiceRequest: {}" + request);
        }

        DNSServiceResponse response = new DNSServiceResponse();

        final DNSEntry entry = request.getDnsEntry();
        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("DNSEntry: {}", entry);
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
                    auditEntry.setAuditType(AuditType.CREATEDNSRECORD);
                    auditEntry.setAuthorized(Boolean.FALSE);
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
                catch (final AuditServiceException asx)
                {
                    ERROR_RECORDER.error(asx.getMessage(), asx);
                }

                return response;
            }

            // here we're going to generate the actual file given the provided
            // data. at this point everything should already be in the database
            // we could generate the zone file without having a "zone data" field,
            // but the zone data field makes it somewhat easier, since its already
            // been created for presentation/approval to the requestor
            List<DNSRecord> apexRecords = entry.getApexRecords();

            if (DEBUG)
            {
                DEBUGGER.debug("apexRecords: {}", apexRecords);
            }

            StringBuilder pBuilder = new StringBuilder()
                .append("$ORIGIN " + entry.getOrigin() + CoreServicesConstants.LINE_BREAK)
                .append("$TTL " + entry.getLifetime() + CoreServicesConstants.LINE_BREAK)
                .append(entry.getSiteName() + " IN SOA " + entry.getMaster() + " " + entry.getOwner() + " (" + CoreServicesConstants.LINE_BREAK)
                .append("       " + entry.getSerialNumber() + "," + CoreServicesConstants.LINE_BREAK)
                .append("       " + entry.getRefresh() + "," + CoreServicesConstants.LINE_BREAK)
                .append("       " + entry.getRetry() + "," + CoreServicesConstants.LINE_BREAK)
                .append("       " + entry.getExpiry() + "," + CoreServicesConstants.LINE_BREAK)
                .append("       " + entry.getMinimum() + "," + CoreServicesConstants.LINE_BREAK)
                .append("       )" + CoreServicesConstants.LINE_BREAK);

            for (DNSRecord record : apexRecords)
            {
                if (DEBUG)
                {
                    DEBUGGER.debug("DNSRecord: {}", record);
                }

                switch (record.getRecordType())
                {
                    case MX:
                        pBuilder.append("       " + record.getRecordClass() + "    " + record.getRecordType() + "    " + record.getRecordPriority() + "    " + record.getRecordAddress() + CoreServicesConstants.LINE_BREAK);

                        break;
                    default:
                        pBuilder.append("       " + record.getRecordClass() + "    " + record.getRecordType() + "    " + record.getRecordAddress() + CoreServicesConstants.LINE_BREAK);

                        break;
                }
            }

            if ((entry.getSubRecords() != null) && (entry.getSubRecords().size() != 0))
            {
                pBuilder.append(CoreServicesConstants.LINE_BREAK);
                pBuilder.append("$ORIGIN " + entry.getSiteName() + "." + CoreServicesConstants.LINE_BREAK); // always put the site name as the initial origin

                for (DNSRecord record : entry.getSubRecords())
                {
                    if (DEBUG)
                    {
                        DEBUGGER.debug("DNSRecord: {}", record);
                    }

                    if (!(StringUtils.equals(record.getRecordOrigin(), ".")) || StringUtils.equals(record.getRecordOrigin(), entry.getSiteName()))
                    {
                        if (!(StringUtils.contains(pBuilder.toString(), record.getRecordOrigin())))
                        {
                            pBuilder.append(CoreServicesConstants.LINE_BREAK);
                            pBuilder.append("$ORIGIN " + record.getRecordOrigin() + "." + CoreServicesConstants.LINE_BREAK);
                        }
                    }

                    switch (record.getRecordType())
                    {
                        case MX:
                            pBuilder.append("       " + record.getRecordClass() + "    " + record.getRecordType() + "    " + record.getRecordPriority() + "    " + record.getRecordAddress() + CoreServicesConstants.LINE_BREAK);

                            break;
                        case SRV:
                            if (StringUtils.isNotEmpty(record.getRecordName()))
                            {
                                pBuilder.append(record.getRecordService() + "." + record.getRecordProtocol() + "    " + record.getRecordName() + "    " +
                                        record.getRecordLifetime() + "    " + record.getRecordClass() + "    " + record.getRecordType() + "    " +
                                        record.getRecordPriority() + "    " + record.getRecordWeight() + "    " + record.getRecordPort() + "    " + record.getRecordAddress() + CoreServicesConstants.LINE_BREAK);
                            }
                            else
                            {
                                pBuilder.append(record.getRecordService() + "." + record.getRecordProtocol() + "    " + record.getRecordLifetime() + "    " +
                                        record.getRecordClass() + "    " + record.getRecordType() + "    " + record.getRecordPriority() + "    " +
                                        record.getRecordWeight() + "    " + record.getRecordPort() + "    " + record.getRecordAddress() + CoreServicesConstants.LINE_BREAK);
                            }

                            break;
                        default:
                            if ((record.getRecordAddress() != null) && (record.getRecordAddresses() == null))
                            {
                                pBuilder.append(record.getRecordName() + "    " + record.getRecordClass() + "    " + record.getRecordType() + "    " + record.getRecordAddress() + CoreServicesConstants.LINE_BREAK);
                            }
                            else
                            {
                                pBuilder.append(record.getRecordName() + "    " + record.getRecordClass() + "    " + record.getRecordType() + "    " + record.getRecordAddresses().get(0) + CoreServicesConstants.LINE_BREAK);

                                for (int x = 1; x != record.getRecordAddresses().size(); x++)
                                {
                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("recordAddress: {}: ", record.getRecordAddresses().get(x));
                                    }
            
                                    pBuilder.append("    " + record.getRecordClass() + "    " + record.getRecordType() + "    " + record.getRecordAddresses().get(x) + CoreServicesConstants.LINE_BREAK);
                                }
                            }

                            break;
                    }
                }
            }

            // return the successful response back and the zone data
            response.setRequestStatus(CoreServicesStatus.SUCCESS);
            response.setZoneData(pBuilder);
        }
        catch (final SecurityException sx)
        {
            ERROR_RECORDER.error(sx.getMessage(), sx);

            throw new DNSServiceException(sx.getMessage(), sx);
        }
        catch (final AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);

            throw new DNSServiceException(acsx.getMessage(), acsx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.CREATEDNSRECORD);
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
     * @see com.cws.esolutions.core.processors.interfaces.IDNSServiceRequestProcessor#pushNewService(com.cws.esolutions.core.processors.dto.DNSServiceRequest)
     */
    public DNSServiceResponse pushNewService(final DNSServiceRequest request) throws DNSServiceException
    {
        final String methodName = IDNSServiceRequestProcessor.CNAME + "#pushNewService(final DNSServiceRequest request) throws DNSServiceException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("DNSServiceRequest: {}" + request);
        }

        DNSServiceResponse response = new DNSServiceResponse();

        final DNSEntry entry = request.getDnsEntry();
        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("DNSEntry: {}", entry);
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
                DEBUGGER.debug("AccessControlServiceResponse: {}", accessResponse);
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
                    auditEntry.setAuthorized(Boolean.FALSE);
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
                catch (final AuditServiceException asx)
                {
                    ERROR_RECORDER.error(asx.getMessage(), asx);
                }

                return response;
            }

            // build me
        }
        catch (final SecurityException sx)
        {
            ERROR_RECORDER.error(sx.getMessage(), sx);

            throw new DNSServiceException(sx.getMessage(), sx);
        }
        catch (final AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);

            throw new DNSServiceException(acsx.getMessage(), acsx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.PUSHDNSRECORD);
                auditEntry.setAuthorized(Boolean.TRUE);
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
            catch (final AuditServiceException asx)
            {
                ERROR_RECORDER.error(asx.getMessage(), asx);
            }
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.core.processors.interfaces.IDNSServiceRequestProcessor#addRecordToEntry(DNSServiceRequest)
     */
    public DNSServiceResponse addRecordToEntry(final DNSServiceRequest request) throws DNSServiceException
    {
        final String methodName = IDNSServiceRequestProcessor.CNAME + "#addRecordToEntry(final DNSServiceRequest request) throws DNSServiceException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("DNSServiceRequest: {}", request);
        }

        final DNSServiceResponse response = new DNSServiceResponse();
        final DNSEntry dnsEntry = request.getDnsEntry();
        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("DNSEntry: {}", dnsEntry);
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
                    auditEntry.setAuditType(AuditType.CREATEDNSRECORD);
                    auditEntry.setAuthorized(Boolean.FALSE);
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
                catch (final AuditServiceException asx)
                {
                    ERROR_RECORDER.error(asx.getMessage(), asx);
                }

                return response;
            }

            // build me
        }
        catch (final SecurityException sx)
        {
            ERROR_RECORDER.error(sx.getMessage(), sx);

            throw new DNSServiceException(sx.getMessage(), sx);
        }
        catch (final AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);

            throw new DNSServiceException(acsx.getMessage(), acsx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.CREATEDNSRECORD);
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
     * @see com.cws.esolutions.core.processors.interfaces.IDNSServiceRequestProcessor#performSiteTransfer(com.cws.esolutions.core.processors.dto.DNSServiceRequest)
     */
    public DNSServiceResponse performSiteTransfer(final DNSServiceRequest request) throws DNSServiceException
    {
        final String methodName = IDNSServiceRequestProcessor.CNAME + "#performSiteTransfer(final DNSServiceRequest request) throws DNSServiceException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("DNSServiceRequest: {}" + request);
        }

        DNSServiceResponse response = new DNSServiceResponse();

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
                    auditEntry.setAuditType(AuditType.CREATEDNSRECORD);
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

            // TODO: build
            // this will take the IP address in the "masters" clause and 
            // change it to whatever the new one is. then it pushes the file
            // out to the associated servers and continues from there to the
            // slaves
        }
        catch (final SecurityException sx)
        {
            ERROR_RECORDER.error(sx.getMessage(), sx);

            throw new DNSServiceException(sx.getMessage(), sx);
        }
        catch (final AccessControlServiceException acsx)
        {
            ERROR_RECORDER.error(acsx.getMessage(), acsx);
            
            throw new DNSServiceException(acsx.getMessage(), acsx);
        }
        finally
        {
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.SITETXFR);
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
