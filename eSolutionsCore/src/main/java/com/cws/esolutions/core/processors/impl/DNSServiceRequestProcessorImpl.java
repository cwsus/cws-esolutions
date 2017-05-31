/*
 * Copyright (c) 2009 - 2017 CaspersBox Web Services
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
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.List;
import java.util.Arrays;
import org.xbill.DNS.Name;
import org.xbill.DNS.Type;
import java.util.ArrayList;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import java.sql.SQLException;
import java.security.Security;
import org.xbill.DNS.SimpleResolver;
import java.net.UnknownHostException;
import org.xbill.DNS.TextParseException;
import org.apache.commons.lang.StringUtils;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.core.CoreServiceConstants;
import com.cws.esolutions.core.processors.dto.DNSEntry;
import com.cws.esolutions.core.processors.dto.DNSRecord;
import com.cws.esolutions.core.processors.enums.ServerType;
import com.cws.esolutions.security.processors.dto.AuditEntry;
import com.cws.esolutions.core.processors.enums.DNSRecordType;
import com.cws.esolutions.security.processors.enums.AuditType;
import com.cws.esolutions.security.processors.dto.AuditRequest;
import com.cws.esolutions.core.processors.dto.DNSServiceRequest;
import com.cws.esolutions.core.processors.dto.DNSServiceResponse;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
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
                catch (AuditServiceException asx)
                {
                    ERROR_RECORDER.error(asx.getMessage(), asx);
                }

                return response;
            }

            // build me
        }
        catch (SecurityException sx)
        {
            ERROR_RECORDER.error(sx.getMessage(), sx);

            throw new DNSServiceException(sx.getMessage(), sx);
        }
        catch (AccessControlServiceException acsx)
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
            catch (AuditServiceException asx)
            {
                ERROR_RECORDER.error(asx.getMessage(), asx);
            }
        }

        return response;
    }

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

        Lookup lookup = null;
        Record[] recordList = null;
        SimpleResolver resolver = null;
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
            Name name = Name.fromString(dnsRecord.getRecordName());
            lookup = new Lookup(name, Type.value(dnsRecord.getRecordType().name()));

            if (DEBUG)
            {
                DEBUGGER.debug("Name: {}", name);
                DEBUGGER.debug("Lookup: {}", lookup);
            }

            if (StringUtils.isNotEmpty(request.getResolverHost()))
            {
                resolver = new SimpleResolver(request.getResolverHost());

                if (DEBUG)
                {
                    DEBUGGER.debug("SimpleResolver: {}", resolver);
                }

                lookup.setResolver(resolver);
	            lookup.setCache(null);
	
	            if (request.getSearchPath() != null)
	            {
	                lookup.setSearchPath(request.getSearchPath());
	            }
	
	            if (DEBUG)
	            {
	                DEBUGGER.debug("Lookup: {}", lookup);
	            }
	
	            recordList = lookup.run();
	
	            if (DEBUG)
	            {
	                if (recordList != null)
	                {
	                    for (Record record : recordList)
	                    {
	                        DEBUGGER.debug("Record: {}", record);
	                    }
	                }
	            }
	
	            if (lookup.getResult() == Lookup.SUCCESSFUL)
	            {
	                if ((recordList != null) && (recordList.length == 1))
	                {
	                    Record record = recordList[0];
	
	                    if (DEBUG)
	                    {
	                        DEBUGGER.debug("Record: {}", record);
	                    }
	
	                    DNSRecord responseRecord = new DNSRecord();
	                    responseRecord.setRecordAddresses(new ArrayList<String>(Arrays.asList(record.rdataToString())));
	                    responseRecord.setRecordName(record.getName().toString());
	                    responseRecord.setRecordType(DNSRecordType.valueOf(Type.string(record.getType())));
	
	                    if (DEBUG)
	                    {
	                        DEBUGGER.debug("responseRecord: {}", responseRecord);
	                    }
	
	                    response.setDnsRecord(responseRecord);
	                    response.setRequestStatus(CoreServicesStatus.SUCCESS);
	
	                    if (DEBUG)
	                    {
	                        DEBUGGER.debug("DNSServiceResponse: {}", response);
	                    }
	                }
	                else
	                {
	                    List<DNSRecord> responseList = new ArrayList<DNSRecord>();
	
	                    if ((recordList != null) && (recordList.length != 0))
	                    {
	                        for (Record record : recordList)
	                        {
	                            if (DEBUG)
	                            {
	                                DEBUGGER.debug("Record: {}", record);
	                            }
	
	                            DNSRecord rec = new DNSRecord();
	                            rec.setRecordAddresses(new ArrayList<String>(Arrays.asList(record.rdataToString())));
	                            rec.setRecordName(record.getName().toString());
	                            rec.setRecordType(DNSRecordType.valueOf(Type.string(record.getType())));
	
	                            if (DEBUG)
	                            {
	                                DEBUGGER.debug("DNSRecord: {}", rec);
	                            }
	
	                            responseList.add(rec);
	                        }
	
	                        if (DEBUG)
	                        {
	                            DEBUGGER.debug("responseList: {}", responseList);
	                        }
	
	                        response.setDnsRecords(responseList);
	                        response.setRequestStatus(CoreServicesStatus.SUCCESS);
	                    }
	                    else
	                    {
	                        response.setRequestStatus(CoreServicesStatus.FAILURE);
	                    }
	                }
	
	                if (DEBUG)
	                {
	                    DEBUGGER.debug("DNSServiceResponse: {}", response);
	                }
	            }
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

                        resolver = new SimpleResolver(request.getResolverHost());

                        if (DEBUG)
                        {
                            DEBUGGER.debug("SimpleResolver: {}", resolver);
                        }

                        lookup.setResolver(resolver);
                        lookup.setCache(null);

                        if (request.getSearchPath() != null)
                        {
                            lookup.setSearchPath(request.getSearchPath());
                        }

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Lookup: {}", lookup);
                        }

                        recordList = lookup.run();

                        if (DEBUG)
                        {
                            if (recordList != null)
                            {
                                for (Record record : recordList)
                                {
                                    DEBUGGER.debug("Record: {}", record);
                                }
                            }
                        }

                        if (lookup.getResult() == Lookup.SUCCESSFUL)
                        {
                            if ((recordList != null) && (recordList.length == 1))
                            {
                                Record record = recordList[0];

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("Record: {}", record);
                                }

                                DNSRecord responseRecord = new DNSRecord();
                                responseRecord.setRecordAddresses(new ArrayList<String>(Arrays.asList(record.rdataToString())));
                                responseRecord.setRecordName(record.getName().toString());
                                responseRecord.setRecordType(DNSRecordType.valueOf(Type.string(record.getType())));

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("responseRecord: {}", responseRecord);
                                }

                                response.setDnsRecord(responseRecord);
                                response.setRequestStatus(CoreServicesStatus.SUCCESS);

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("DNSServiceResponse: {}", response);
                                }
                            }
                            else
                            {
                                List<DNSRecord> responseList = new ArrayList<DNSRecord>();

                                if ((recordList != null) && (recordList.length != 0))
                                {
                                    for (Record record : recordList)
                                    {
                                        if (DEBUG)
                                        {
                                            DEBUGGER.debug("Record: {}", record);
                                        }

                                        DNSRecord rec = new DNSRecord();
                                        rec.setRecordAddresses(new ArrayList<String>(Arrays.asList(record.rdataToString())));
                                        rec.setRecordName(record.getName().toString());
                                        rec.setRecordType(DNSRecordType.valueOf(Type.string(record.getType())));

                                        if (DEBUG)
                                        {
                                            DEBUGGER.debug("DNSRecord: {}", rec);
                                        }

                                        responseList.add(rec);
                                    }

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("responseList: {}", responseList);
                                    }

                                    response.setDnsRecords(responseList);
                                    response.setRequestStatus(CoreServicesStatus.SUCCESS);
                                }
                                else
                                {
                                    response.setRequestStatus(CoreServicesStatus.FAILURE);
                                }
                            }

                            if (DEBUG)
                            {
                                DEBUGGER.debug("DNSServiceResponse: {}", response);
                            }
                        }
                        else
                        {
                        	response.setRequestStatus(CoreServicesStatus.FAILURE);
                        }
                    }
                }
                else
                {
                    response.setRequestStatus(CoreServicesStatus.FAILURE);
                }
            }
        }
        catch (TextParseException tpx)
        {
            ERROR_RECORDER.error(tpx.getMessage(), tpx);

            throw new DNSServiceException(tpx.getMessage(), tpx);
        }
        catch (UnknownHostException uhx)
        {
            ERROR_RECORDER.error(uhx.getMessage(), uhx);

            throw new DNSServiceException(uhx.getMessage(), uhx);
        }
        catch (SQLException sqx)
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
            catch (NullPointerException npx) {}
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
                catch (AuditServiceException asx)
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
                .append("$ORIGIN " + entry.getOrigin() + CoreServiceConstants.LINE_BREAK)
                .append("$TTL " + entry.getLifetime() + CoreServiceConstants.LINE_BREAK)
                .append(entry.getSiteName() + " IN SOA " + entry.getMaster() + " " + entry.getOwner() + " (" + CoreServiceConstants.LINE_BREAK)
                .append("       " + entry.getSerialNumber() + "," + CoreServiceConstants.LINE_BREAK)
                .append("       " + entry.getRefresh() + "," + CoreServiceConstants.LINE_BREAK)
                .append("       " + entry.getRetry() + "," + CoreServiceConstants.LINE_BREAK)
                .append("       " + entry.getExpiry() + "," + CoreServiceConstants.LINE_BREAK)
                .append("       " + entry.getMinimum() + "," + CoreServiceConstants.LINE_BREAK)
                .append("       )" + CoreServiceConstants.LINE_BREAK);

            for (DNSRecord record : apexRecords)
            {
            	if (DEBUG)
            	{
            		DEBUGGER.debug("DNSRecord: {}", record);
            	}

            	switch (record.getRecordType())
            	{
            		case MX:
            			pBuilder.append("       " + record.getRecordClass() + "    " + record.getRecordType() + "    " + record.getRecordPriority() + "    " + record.getRecordAddress() + CoreServiceConstants.LINE_BREAK);

            			break;
            		default:
            			pBuilder.append("       " + record.getRecordClass() + "    " + record.getRecordType() + "    " + record.getRecordAddress() + CoreServiceConstants.LINE_BREAK);

            			break;
            	}
            }

            if ((entry.getSubRecords() != null) && (entry.getSubRecords().size() != 0))
            {
            	pBuilder.append(CoreServiceConstants.LINE_BREAK);
            	pBuilder.append("$ORIGIN " + entry.getSiteName() + "." + CoreServiceConstants.LINE_BREAK); // always put the site name as the initial origin

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
                			pBuilder.append(CoreServiceConstants.LINE_BREAK);
                			pBuilder.append("$ORIGIN " + record.getRecordOrigin() + "." + CoreServiceConstants.LINE_BREAK);
                		}
                	}

                	switch (record.getRecordType())
                	{
                		case MX:
                			pBuilder.append("       " + record.getRecordClass() + "    " + record.getRecordType() + "    " + record.getRecordPriority() + "    " + record.getRecordAddress() + CoreServiceConstants.LINE_BREAK);

                			break;
                		case SRV:
                			if (StringUtils.isNotEmpty(record.getRecordName()))
                			{
                				pBuilder.append(record.getRecordService() + "." + record.getRecordProtocol() + "    " + record.getRecordName() + "    " +
                						record.getRecordLifetime() + "    " + record.getRecordClass() + "    " + record.getRecordType() + "    " +
                						record.getRecordPriority() + "    " + record.getRecordWeight() + "    " + record.getRecordPort() + "    " + record.getRecordAddress() + CoreServiceConstants.LINE_BREAK);
                			}
                			else
                			{
                				pBuilder.append(record.getRecordService() + "." + record.getRecordProtocol() + "    " + record.getRecordLifetime() + "    " +
                						record.getRecordClass() + "    " + record.getRecordType() + "    " + record.getRecordPriority() + "    " +
                						record.getRecordWeight() + "    " + record.getRecordPort() + "    " + record.getRecordAddress() + CoreServiceConstants.LINE_BREAK);
                			}

                			break;
                		default:
                        	if ((record.getRecordAddress() != null) && (record.getRecordAddresses() == null))
                        	{
                        		pBuilder.append(record.getRecordName() + "    " + record.getRecordClass() + "    " + record.getRecordType() + "    " + record.getRecordAddress() + CoreServiceConstants.LINE_BREAK);
                        	}
                        	else
                        	{
                        		pBuilder.append(record.getRecordName() + "    " + record.getRecordClass() + "    " + record.getRecordType() + "    " + record.getRecordAddresses().get(0) + CoreServiceConstants.LINE_BREAK);

	    	                	for (int x = 1; x != record.getRecordAddresses().size(); x++)
	    	                    {
	    	                		if (DEBUG)
	    	                    	{
	    	                			DEBUGGER.debug("recordAddress: {}: ", record.getRecordAddresses().get(x));
	    	                    	}
	    	
	    	                		pBuilder.append("    " + record.getRecordClass() + "    " + record.getRecordType() + "    " + record.getRecordAddresses().get(x) + CoreServiceConstants.LINE_BREAK);
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
        catch (SecurityException sx)
        {
            ERROR_RECORDER.error(sx.getMessage(), sx);

            throw new DNSServiceException(sx.getMessage(), sx);
        }
        catch (AccessControlServiceException acsx)
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
            catch (AuditServiceException asx)
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
                catch (AuditServiceException asx)
                {
                    ERROR_RECORDER.error(asx.getMessage(), asx);
                }

                return response;
            }

            // build me
        }
        catch (SecurityException sx)
        {
            ERROR_RECORDER.error(sx.getMessage(), sx);

            throw new DNSServiceException(sx.getMessage(), sx);
        }
        catch (AccessControlServiceException acsx)
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
            catch (AuditServiceException asx)
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
                catch (AuditServiceException asx)
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
        catch (SecurityException sx)
        {
            ERROR_RECORDER.error(sx.getMessage(), sx);

            throw new DNSServiceException(sx.getMessage(), sx);
        }
        catch (AccessControlServiceException acsx)
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
            catch (AuditServiceException asx)
            {
                ERROR_RECORDER.error(asx.getMessage(), asx);
            }
        }

        return response;
    }
}
