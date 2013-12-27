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
 * @see com.cws.esolutions.core.processors.interfaces.IDNSServiceRequestProcessor
 */
import java.io.File;
import java.util.List;
import java.util.Arrays;
import java.util.Vector;
import org.xbill.DNS.Name;
import org.xbill.DNS.Type;
import java.util.ArrayList;
import java.io.IOException;
import java.util.Properties;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import java.sql.SQLException;
import java.security.Security;
import java.io.FileOutputStream;
import org.xbill.DNS.SimpleResolver;
import java.io.FileNotFoundException;
import java.net.UnknownHostException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.FileUtils;
import org.xbill.DNS.TextParseException;
import org.apache.commons.lang.StringUtils;

import com.cws.esolutions.core.Constants;
import com.cws.esolutions.core.utils.NetworkUtils;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.core.processors.dto.DNSEntry;
import com.cws.esolutions.core.processors.dto.DNSRecord;
import com.cws.esolutions.core.processors.enums.ServerType;
import com.cws.esolutions.security.processors.dto.AuditEntry;
import com.cws.esolutions.core.processors.enums.DNSRecordType;
import com.cws.esolutions.security.processors.enums.AuditType;
import com.cws.esolutions.security.processors.dto.AuditRequest;
import com.cws.esolutions.core.processors.dto.DNSServiceRequest;
import com.cws.esolutions.core.utils.exception.UtilityException;
import com.cws.esolutions.core.processors.dto.DNSServiceResponse;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.security.services.enums.AdminControlType;
import com.cws.esolutions.core.dao.processors.impl.ServerDataDAOImpl;
import com.cws.esolutions.core.dao.processors.interfaces.IServerDataDAO;
import com.cws.esolutions.core.processors.exception.DNSServiceException;
import com.cws.esolutions.security.processors.exception.AuditServiceException;
import com.cws.esolutions.core.processors.interfaces.IDNSServiceRequestProcessor;
import com.cws.esolutions.security.services.exception.AccessControlServiceException;
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
public class DNSServiceRequestProcessorImpl implements IDNSServiceRequestProcessor
{
    /*
     * Project: eSolutionsCore
     * Package: com.cws.esolutions.security.audit.processors.interfaces
     * File: IAuditProcessor.java
     *
     * History
     * Author               Date                            Comments
     * ----------------------------------------------------------------------------
     * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
     */
    static class ModifyEntryToAddRecord
    {
        static final String CNAME = ModifyEntryToAddRecord.class.getName();

        public static final DNSEntry addRecordToEntry(final DNSEntry entry, final DNSRecord record, final boolean isApex)
        {
            final String methodName = ModifyEntryToAddRecord.CNAME + "#addRecordToEntry(final DNSEntry entry, final DNSRecord record, final boolean isApex)";

            if (DEBUG)
            {
                DEBUGGER.debug(methodName);
                DEBUGGER.debug("Value: {}", entry);
                DEBUGGER.debug("Value: {}", record);
                DEBUGGER.debug("Value: {}", isApex);
            }

            if ((StringUtils.equals(entry.getApex(), record.getRecordName())) || (StringUtils.equals(entry.getApex(), record.getRecordOrigin())))
            {
                List<DNSRecord> list = null;

                if (isApex)
                {
                    if ((entry.getApexRecords() == null) || (entry.getApexRecords().size() == 0))
                    {
                        list = new ArrayList<>();
                    }
                    else
                    {
                        list = entry.getApexRecords();
                    }
                }
                else
                {
                    if ((entry.getSubRecords() == null) || (entry.getSubRecords().size() == 0))
                    {
                        list = new ArrayList<>();
                    }
                    else
                    {
                        list = entry.getSubRecords();
                    }
                }

                if (!(list.contains(record)))
                {
                    list.add(record);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("list: {}", list);
                    }

                    if (isApex)
                    {
                        entry.setApexRecords(list);
                    }
                    else
                    {
                        entry.setSubRecords(list);
                    }
                }
            }

            if (DEBUG)
            {
                DEBUGGER.debug("DNSEntry: {}", entry);
            }

            return entry;
        }
    }

    /**
     * @see com.cws.esolutions.core.processors.interfaces.IDNSServiceRequestProcessor#performLookup(com.cws.esolutions.core.processors.dto.DNSServiceRequest)
     */
    @Override
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
            Name name = Name.fromString(dnsRecord.getRecordName());
            Lookup lookup = new Lookup(name, Type.value(dnsRecord.getRecordType().name()));

            if (DEBUG)
            {
                DEBUGGER.debug("Name: {}", name);
                DEBUGGER.debug("Lookup: {}", lookup);
            }

            if (StringUtils.isNotEmpty(request.getResolverHost()))
            {
                SimpleResolver resolver = new SimpleResolver(request.getResolverHost());

                if (DEBUG)
                {
                    DEBUGGER.debug("SimpleResolver: {}", resolver);
                }

                lookup.setResolver(resolver);
            }

            lookup.setCache(null);

            if (request.getSearchPath() != null)
            {
                lookup.setSearchPath(request.getSearchPath());
            }

            if (DEBUG)
            {
                DEBUGGER.debug("Lookup: {}", lookup);
            }

            Record[] recordList = lookup.run();

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
                    responseRecord.setPrimaryAddress(new ArrayList<>(Arrays.asList(record.rdataToString())));
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
                    List<DNSRecord> responseList = new ArrayList<>();

                    if ((recordList != null) && (recordList.length != 0))
                    {
                        for (Record record : recordList)
                        {
                            if (DEBUG)
                            {
                                DEBUGGER.debug("Record: {}", record);
                            }

                            DNSRecord rec = new DNSRecord();
                            rec.setPrimaryAddress(new ArrayList<>(Arrays.asList(record.rdataToString())));
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
                // this will run through the available slave servers
                List<String[]> serverList = dao.getServersByAttribute(ServerType.DNSSLAVE.name(), 0);

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
                            for (Object obj : data)
                            {
                                DEBUGGER.debug("Value: {}", obj);
                            }
                        }

                        String serverName = (String) data[15];

                        if (DEBUG)
                        {
                            DEBUGGER.debug("serverName: {}", serverName);
                        }

                        NetworkUtils.isHostValid(serverName);

                        // do the lookup here
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
            catch (NullPointerException npx)
            {
                // dont do anything with it
            }
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.core.processors.interfaces.IDNSServiceRequestProcessor#createNewService(com.cws.esolutions.core.processors.dto.DNSServiceRequest)
     */
    @Override
    public DNSServiceResponse createNewService(final DNSServiceRequest request) throws DNSServiceException
    {
        final String methodName = IDNSServiceRequestProcessor.CNAME + "#createNewService(final DNSServiceRequest request) throws DNSServiceException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("DNServiceRequest: " + request);
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
            boolean isAdminAuthorized = accessControl.accessControlService(userAccount, AdminControlType.SERVICE_ADMIN);
            boolean isServiceAuthorized = accessControl.isUserAuthorizedForService(userAccount, request.getServiceId());

            if (DEBUG)
            {
                DEBUGGER.debug("isAdminAuthorized: {}", isAdminAuthorized);
                DEBUGGER.debug("isServiceAuthorized: {}", isServiceAuthorized);
            }

            if ((isAdminAuthorized) && (isServiceAuthorized))
            {
                StringBuilder sBuilder = new StringBuilder()
                    .append("$ORIGIN " + entry.getOrigin() + "\n")
                    .append("$TTL " + entry.getLifetime() + "\n")
                    .append(entry.getApex() + " IN SOA " + entry.getMaster() + " " + entry.getOwner() + " (\n")
                    .append(entry.getSerialNumber() + "\n")
                    .append(entry.getSlaveRefresh() + "\n")
                    .append(entry.getSlaveRetry() + "\n")
                    .append(entry.getSlaveExpiry() + "\n")
                    .append(entry.getCacheTime() + "\n")
                    .append(")\n");

                if ((entry.getApexRecords() != null) && (entry.getApexRecords().size() != 0))
                {
                    for (DNSRecord rec : entry.getApexRecords())
                    {
                        if (rec.getRecordType() == DNSRecordType.MX)
                        {
                            sBuilder.append(StringUtils.rightPad(rec.getRecordClass(), 8, ""));
                            sBuilder.append(StringUtils.rightPad(rec.getRecordType().name(), 8, ""));
                            sBuilder.append(StringUtils.rightPad(String.valueOf(rec.getRecordPriority()), 8, ""));
                            sBuilder.append(StringUtils.rightPad(rec.getPrimaryAddress().get(0), 8, ""));
                            sBuilder.append("\n");
                        }
                        else
                        {
                            sBuilder.append(StringUtils.rightPad(rec.getRecordClass(), 8, ""));
                            sBuilder.append(StringUtils.rightPad(rec.getRecordType().name(), 8, ""));
                            sBuilder.append(StringUtils.rightPad(rec.getPrimaryAddress().get(0), 8, ""));
                            sBuilder.append("\n");
                        }
                    }

                    sBuilder.append("\n");

                    if ((entry.getSubRecords() != null) && (entry.getSubRecords().size() != 0))
                    {
                        sBuilder.append("$ORIGIN " + entry.getApex() + "\n");

                        for (DNSRecord rec : entry.getSubRecords())
                        {
                            switch (rec.getRecordType())
                            {
                                case SRV:
                                    //_service._proto.name. TTL class SRV priority weight port target.
                                    StringBuilder srv = new StringBuilder()
                                        .append("_" + rec.getRecordService() + ".")
                                        .append("_" + rec.getRecordProtocol() + ".")
                                        .append(StringUtils.rightPad((StringUtils.endsWith(rec.getRecordName(),  ".") ? rec.getRecordName() : rec.getRecordName() + "."), 16, ""))
                                        .append(StringUtils.rightPad(rec.getRecordClass(), 8, ""))
                                        .append(StringUtils.rightPad(String.valueOf(rec.getRecordPriority()), 8, ""))
                                        .append(StringUtils.rightPad(String.valueOf(rec.getRecordWeight()), 8, ""))
                                        .append(StringUtils.rightPad(String.valueOf(rec.getRecordPort()),  8, ""))
                                        .append(rec.getPrimaryAddress().get(0));

                                    sBuilder.append(srv.toString() + "\n");

                                    break;
                                case MX:
                                    StringBuilder mx = new StringBuilder()
                                        .append(StringUtils.rightPad(rec.getRecordName(), 16, ""))
                                        .append(StringUtils.rightPad(rec.getRecordClass(), 8, ""))
                                        .append(StringUtils.rightPad(rec.getRecordType().name(), 8, ""))
                                        .append(StringUtils.rightPad(String.valueOf(rec.getRecordWeight()), 8, ""))
                                        .append(rec.getPrimaryAddress().get(0));

                                    sBuilder.append(mx.toString() + "\n");

                                    break;
                                case CNAME:
                                    sBuilder.append(StringUtils.rightPad(rec.getRecordName(), 16, ""));
                                    sBuilder.append(StringUtils.rightPad(rec.getRecordClass(), 8, ""));
                                    sBuilder.append(StringUtils.rightPad(rec.getRecordType().name(), 8, ""));
                                    sBuilder.append(StringUtils.rightPad((StringUtils.endsWith(rec.getPrimaryAddress().get(0),  ".") ? rec.getPrimaryAddress().get(0) : rec.getPrimaryAddress().get(0) + "."), 8, ""));
                                    sBuilder.append("\n");

                                    break;
                                case TXT:
                                    sBuilder.append(StringUtils.rightPad(rec.getRecordName(), 16, ""));
                                    sBuilder.append(StringUtils.rightPad(rec.getRecordClass(), 8, ""));
                                    sBuilder.append(StringUtils.rightPad(rec.getRecordType().name(), 8, ""));
                                    sBuilder.append("\"" + StringUtils.rightPad(rec.getPrimaryAddress().get(0), 8, "") + "\"");
                                    sBuilder.append("\n");

                                    break;
                                default:
                                    sBuilder.append(StringUtils.rightPad(rec.getRecordName(), 16, ""));
                                    sBuilder.append(StringUtils.rightPad(rec.getRecordClass(), 8, ""));
                                    sBuilder.append(StringUtils.rightPad(rec.getRecordType().name(), 8, ""));

                                    for (String addr : rec.getPrimaryAddress())
                                    {
                                        if (DEBUG)
                                        {
                                            DEBUGGER.debug("addr: {}", addr);
                                        }

                                        sBuilder.append(StringUtils.leftPad(StringUtils.rightPad(rec.getRecordClass(), 8, ""), 16, ""));
                                        sBuilder.append(StringUtils.rightPad(rec.getRecordType().name(), 8, ""));
                                        sBuilder.append(StringUtils.rightPad(addr, 8, ""));
                                        sBuilder.append("\n");
                                    }

                                    if (StringUtils.isNotEmpty(rec.getSpfRecord()))
                                    {
                                        sBuilder.append(StringUtils.leftPad(StringUtils.rightPad(rec.getRecordClass(), 8, ""), 16, ""));
                                        sBuilder.append(StringUtils.rightPad(DNSRecordType.TXT.name(), 8, ""));
                                        sBuilder.append("\"" + StringUtils.rightPad(rec.getSpfRecord(), 8, "") + "\"");
                                        sBuilder.append("\n");
                                    }

                                    break;
                            }
                        }
                    }

                    response.setDnsEntry(entry);
                    response.setZoneData(sBuilder);
                    response.setRequestStatus(CoreServicesStatus.SUCCESS);
                }
                else
                {
                    ERROR_RECORDER.error("No apex record was found. Cannot generate zone.");

                    throw new DNSServiceException("No apex record was found. Cannot generate zone.");
                }
            }
            else
            {
                // unauthorized
                response.setRequestStatus(CoreServicesStatus.UNAUTHORIZED);
            }
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
    @Override
    public DNSServiceResponse pushNewService(final DNSServiceRequest request) throws DNSServiceException
    {
        final String methodName = IDNSServiceRequestProcessor.CNAME + "#pushNewService(final DNSServiceRequest request) throws DNSServiceException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("DNServiceRequest: " + request);
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
            boolean isAdminAuthorized = accessControl.accessControlService(userAccount, AdminControlType.SERVICE_ADMIN);
            boolean isServiceAuthorized = accessControl.isUserAuthorizedForService(userAccount, request.getServiceId());

            if (DEBUG)
            {
                DEBUGGER.debug("isAdminAuthorized: {}", isAdminAuthorized);
                DEBUGGER.debug("isServiceAuthorized: {}", isServiceAuthorized);
            }

            if ((isAdminAuthorized) && (isServiceAuthorized))
            {
                // insert the apex into the database
                List<String> dnsList = new ArrayList<>(
                        Arrays.asList(
                                entry.getProjectCode(),
                                entry.getFileName(),
                                entry.getOrigin(),
                                String.valueOf(entry.getLifetime()),
                                entry.getApex(),
                                entry.getMaster(),
                                entry.getOwner(),
                                entry.getSerialNumber(),
                                String.valueOf(entry.getSlaveRefresh()),
                                String.valueOf(entry.getSlaveRetry()),
                                String.valueOf(entry.getSlaveExpiry()),
                                String.valueOf(entry.getCacheTime())));

                if (DEBUG)
                {
                    DEBUGGER.debug("dnsList: {}", dnsList);
                }

                // put in the apex
                boolean isApexAdded = dnsDao.addNewService(dnsList, true);

                if (DEBUG)
                {
                    DEBUGGER.debug("isApexAdded: {}", isApexAdded);
                }

                if (isApexAdded)
                {
                    // insert the apex records
                    for (DNSRecord record : entry.getApexRecords())
                    {
                        if (DEBUG)
                        {
                            DEBUGGER.debug("DNSRecord: {}", record);
                        }

                        List<String> dnsRecord = new ArrayList<>(
                                Arrays.asList(
                                        entry.getProjectCode(),
                                        entry.getFileName(),
                                        record.getRecordOrigin(),
                                        record.getRecordName(),
                                        record.getRecordClass(),
                                        record.getRecordType().name(),
                                        String.valueOf(record.getRecordPort()),
                                        String.valueOf(record.getRecordWeight()),
                                        record.getRecordService(),
                                        record.getRecordProtocol(),
                                        String.valueOf(record.getRecordPriority()),
                                        record.getPrimaryAddress().toString(),
                                        ((record.getSecondaryAddress() != null) && (record.getSecondaryAddress().size() != 0)) ? record.getSecondaryAddress().toString() : Constants.NOT_SET,
                                        ((record.getTertiaryAddress() != null) && (record.getTertiaryAddress().size() != 0)) ? record.getTertiaryAddress().toString() : Constants.NOT_SET));
                        if (DEBUG)
                        {
                            DEBUGGER.debug("dnsRecord: {}", dnsRecord);
                        }

                        boolean isRecordAdded = dnsDao.addNewService(dnsRecord, false);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("isRecordAdded: {}", isRecordAdded);
                        }

                        if (!(isRecordAdded))
                        {
                            throw new DNSServiceException("Failed to add record to database. Cannot continue");
                        }
                    }

                    // move on to subrecords if there are any
                    for (DNSRecord record : entry.getSubRecords())
                    {
                        if (DEBUG)
                        {
                            DEBUGGER.debug("DNSRecord: {}", record);
                        }

                        List<String> dnsRecord = new ArrayList<>(
                                Arrays.asList(
                                        entry.getProjectCode(),
                                        entry.getFileName(),
                                        record.getRecordOrigin(),
                                        record.getRecordName(),
                                        record.getRecordClass(),
                                        record.getRecordType().name(),
                                        String.valueOf(record.getRecordPort()),
                                        String.valueOf(record.getRecordWeight()),
                                        record.getRecordService(),
                                        record.getRecordProtocol(),
                                        String.valueOf(record.getRecordPriority()),
                                        record.getPrimaryAddress().toString(),
                                        ((record.getSecondaryAddress() != null) && (record.getSecondaryAddress().size() != 0)) ? record.getSecondaryAddress().toString() : Constants.NOT_SET,
                                        ((record.getTertiaryAddress() != null) && (record.getTertiaryAddress().size() != 0)) ? record.getTertiaryAddress().toString() : Constants.NOT_SET));

                        if (DEBUG)
                        {
                            DEBUGGER.debug("dnsRecord: {}", dnsRecord);
                        }

                        boolean isRecordAdded = dnsDao.addNewService(dnsRecord, false);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("isRecordAdded: {}", isRecordAdded);
                        }

                        if (!(isRecordAdded))
                        {
                            throw new DNSServiceException("Failed to add record to database. Cannot continue");
                        }
                    }

                    // here we're going to generate the actual file given the provided
                    // data. at this point everything should already be in the database
                    // we could generate the zone file without having a "zone data" field,
                    // but the zone data field makes it somewhat easier, since its already
                    // been created for presentation/approval to the requestor
                    if (entry.getZoneData() != null)
                    {
                        File zoneFile = FileUtils.getFile(Constants.TMP_DIR, entry.getFileName());

                        if (DEBUG)
                        {
                            DEBUGGER.debug("File: {}", zoneFile);
                        }

                        IOUtils.write(entry.getZoneData(), new FileOutputStream(zoneFile));

                        if ((zoneFile.exists()) && (zoneFile.length() != 0))
                        {
                            IServerDataDAO dao = new ServerDataDAOImpl();
                            List<String[]> dnsServers = dao.getServersByAttribute(ServerType.DNSMASTER.name() + " " + request.getServiceRegion().name(), 0);

                            if (DEBUG)
                            {
                                DEBUGGER.debug("dnsServers: {}", dnsServers);
                            }

                            if ((dnsServers != null) && (dnsServers.size() != 0))
                            {
                                final Properties sshProps = new Properties();
                                sshProps.load(this.getClass().getClassLoader().getResourceAsStream(sshConfig.getSshProperties()));

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("Properties: {}", sshProps);
                                }

                                final Properties authProps = new Properties();
                                authProps.put(Constants.ACCOUNT, sshConfig.getSshAccount());
                                authProps.put(Constants.PASSWORD, sshConfig.getSshPassword());
                                authProps.put(Constants.SALT, sshConfig.getSshSalt());
                                authProps.put(Constants.KEYFILE, sshConfig.getSshKey());

                                NetworkUtils.executeSCPTransfer(sshProps, authProps, new ArrayList<>(Arrays.asList(zoneFile)), zoneFile.getAbsolutePath(), (String) dnsServers.get(0)[18], true);

                                List<String[]> slaveServers = dao.getServersByAttribute(ServerType.DNSSLAVE.name() + " " + request.getServiceRegion().name(), 0);

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("slaveServers: {}", slaveServers);
                                }

                                if ((slaveServers != null) && (slaveServers.size() != 0))
                                {
                                    int errCount = 0;

                                    for (Object[] server : slaveServers)
                                    {
                                        try
                                        {
                                            NetworkUtils.executeSCPTransfer(sshProps, authProps, new ArrayList<>(Arrays.asList(zoneFile)), zoneFile.getAbsolutePath(), (String) server[18], true);
                                        }
                                        catch (UtilityException ux)
                                        {
                                            ERROR_RECORDER.error(ux.getMessage(), ux);

                                            errCount++;
                                        }
                                    }

                                    if (errCount == 0)
                                    {
                                        response.setRequestStatus(CoreServicesStatus.SUCCESS);
                                    }
                                    else
                                    {
                                        response.setRequestStatus(CoreServicesStatus.FAILURE);
                                    }
                                }
                            }
                        }
                        else
                        {
                            // zone file wasn't created
                            ERROR_RECORDER.error("Zone file was not created. Cannot continue.");

                            response.setRequestStatus(CoreServicesStatus.FAILURE);
                        }
                    }
                    else
                    {
                        // zone file wasn't created
                        ERROR_RECORDER.error("No zone data was provided in the request. Cannot continue.");

                        response.setRequestStatus(CoreServicesStatus.FAILURE);
                    }
                }
                else
                {
                    ERROR_RECORDER.error("Failed to insert apex record into the datastore. Cannot continue.");

                    response.setRequestStatus(CoreServicesStatus.FAILURE);
                }
            }
            else
            {
                // unauthorized
                response.setRequestStatus(CoreServicesStatus.UNAUTHORIZED);
            }
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
        catch (UtilityException ux)
        {
            ERROR_RECORDER.error(ux.getMessage(), ux);

            throw new DNSServiceException(ux.getMessage(), ux);
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new DNSServiceException(sqx.getMessage(), sqx);
        }
        catch (FileNotFoundException fnfx)
        {
            ERROR_RECORDER.error(fnfx.getMessage(), fnfx);

            throw new DNSServiceException(fnfx.getMessage(), fnfx);
        }
        catch (IOException iox)
        {
            ERROR_RECORDER.error(iox.getMessage(), iox);

            throw new DNSServiceException(iox.getMessage(), iox);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.PUSHDNSRECORD);
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
    @Override
    public DNSServiceResponse performSiteTransfer(final DNSServiceRequest request) throws DNSServiceException
    {
        final String methodName = IDNSServiceRequestProcessor.CNAME + "#performSiteTransfer(final DNSServiceRequest request) throws DNSServiceException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("DNServiceRequest: " + request);
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
            boolean isAdminAuthorized = accessControl.accessControlService(userAccount, AdminControlType.SERVICE_ADMIN);
            boolean isServiceAuthorized = accessControl.isUserAuthorizedForService(userAccount, request.getServiceId());

            if (DEBUG)
            {
                DEBUGGER.debug("isAdminAuthorized: {}", isAdminAuthorized);
                DEBUGGER.debug("isServiceAuthorized: {}", isServiceAuthorized);
            }

            if ((isAdminAuthorized) && (isServiceAuthorized))
            {
                // TODO: build
                // this will take the IP address in the "masters" clause and 
                // change it to whatever the new one is. then it pushes the file
                // out to the associated servers and continues from there to the
                // slaves
            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.UNAUTHORIZED);
            }
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
     * @see com.cws.esolutions.core.processors.interfaces.IDNSServiceRequestProcessor#getDataFromDatabase(com.cws.esolutions.core.processors.dto.DNSServiceRequest)
     */
    @Override
    public DNSServiceResponse getDataFromDatabase(final DNSServiceRequest request) throws DNSServiceException
    {
        final String methodName = IDNSServiceRequestProcessor.CNAME + "#getDataFromDatabase(final DNSServiceRequest request) throws DNSServiceException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("DNSServiceRequest: {}", request);
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
            boolean isServiceAuthorized = accessControl.isUserAuthorizedForService(userAccount, request.getServiceId());

            if (DEBUG)
            {
                DEBUGGER.debug("isServiceAuthorized: {}", isServiceAuthorized);
            }

            if (isServiceAuthorized)
            {
                List<Vector<String>> data = dnsDao.getServiceData(entry.getProjectCode());

                if (DEBUG)
                {
                    DEBUGGER.debug("data: {}", data);
                }

                if ((data != null) && (data.size() != 0))
                {
                    // ok, this handles multiple apexes (e.g. DNSEntry objects)
                    // find a way to pull it all together...
                    List<DNSEntry> entryList = new ArrayList<>();
                    List<DNSRecord> apexList = new ArrayList<>();
                    List<DNSRecord> subList = new ArrayList<>();

                    for (Vector<String> vector : data)
                    {
                        if (DEBUG)
                        {
                            DEBUGGER.debug("Vector: {}", vector);
                        }

                        if (Boolean.valueOf(vector.get(2)))
                        {
                            DNSEntry dnsEntry = new DNSEntry();
                            dnsEntry.setProjectCode(vector.get(0));
                            dnsEntry.setFileName(vector.get(1));
                            dnsEntry.setOrigin(vector.get(3));
                            dnsEntry.setLifetime(Integer.parseInt(vector.get(4)));
                            dnsEntry.setApex(vector.get(5));
                            dnsEntry.setMaster(vector.get(6));
                            dnsEntry.setOwner(vector.get(7));
                            dnsEntry.setSerialNumber(vector.get(8));
                            dnsEntry.setSlaveRefresh(Integer.parseInt(vector.get(9)));
                            dnsEntry.setSlaveRetry(Integer.parseInt(vector.get(10)));
                            dnsEntry.setSlaveExpiry(Integer.parseInt(vector.get(11)));
                            dnsEntry.setCacheTime(Integer.parseInt(vector.get(12)));

                            if (DEBUG)
                            {
                                DEBUGGER.debug("DNSEntry: {}", dnsEntry);
                            }

                            entryList.add(dnsEntry);
                        }
                        else
                        {
                            DNSRecord record = new DNSRecord();
                            record.setRecordOrigin(vector.get(3));
                            record.setRecordName(vector.get(5));
                            record.setRecordClass(vector.get(13));
                            record.setRecordType(DNSRecordType.valueOf(vector.get(14)));
                            record.setPrimaryAddress(new ArrayList<>(Arrays.asList(vector.get(20))));
                            record.setSecondaryAddress(new ArrayList<>(Arrays.asList(vector.get(21))));
                            record.setTertiaryAddress(new ArrayList<>(Arrays.asList(vector.get(22))));

                            switch (record.getRecordType())
                            {
                                case SRV:
                                    // _service._proto.name. TTL class SRV priority weight port target.
                                    record.setRecordService(vector.get(17));
                                    record.setRecordProtocol(vector.get(18));
                                    record.setRecordLifetime(Integer.parseInt(vector.get(4)));
                                    record.setRecordPriority(Integer.parseInt(vector.get(19)));
                                    record.setRecordWeight(Integer.parseInt(vector.get(16)));
                                    record.setRecordPort(Integer.parseInt(vector.get(15)));

                                    break;
                                case MX:
                                    record.setRecordWeight(Integer.parseInt(vector.get(16)));

                                    break;
                                default:
                                    break;
                            }

                            if (DEBUG)
                            {
                                DEBUGGER.debug("DNSRecord: {}", record);
                            }

                            // must correlate back
                            if (StringUtils.equals(record.getRecordOrigin(), "."))
                            {
                                // this is an apex record
                                apexList.add(record);
                            }
                            else
                            {
                                // subrecord
                                subList.add(record);
                            }
                        }
                    }

                    // assemble the records, in the proper order, into
                    // the right DNSEntry object for return
                    List<DNSEntry> responseList = new ArrayList<>();

                    for (int x = 0; x < entryList.size(); x++)
                    {
                        if (DEBUG)
                        {
                            DEBUGGER.debug("entry: {}", entryList.get(x));
                        }

                        DNSEntry modEntry = null;

                        // apex first
                        for (DNSRecord apex : apexList)
                        {
                            if (DEBUG)
                            {
                                DEBUGGER.debug("DNSRecord: {}", apex);
                            }

                            modEntry = ModifyEntryToAddRecord.addRecordToEntry(entryList.get(x), apex, true);

                            if (DEBUG)
                            {
                                DEBUGGER.debug("DNSEntry: {}", modEntry);
                            }
                        }

                        responseList.add(modEntry);
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("responseList: {}", responseList);
                    }

                    entryList.clear();
                    entryList = new ArrayList<>(responseList);

                    responseList.clear();
                    responseList = new ArrayList<>();

                    for (int x = 0; x < entryList.size(); x++)
                    {
                        if (DEBUG)
                        {
                            DEBUGGER.debug("entry: {}", entryList.get(x));
                        }

                        DNSEntry modEntry = null;

                        // apex first
                        for (DNSRecord sub : subList)
                        {
                            if (DEBUG)
                            {
                                DEBUGGER.debug("DNSRecord: {}", sub);
                            }

                            modEntry = ModifyEntryToAddRecord.addRecordToEntry(entryList.get(x), sub, false);

                            if (DEBUG)
                            {
                                DEBUGGER.debug("DNSEntry: {}", modEntry);
                            }
                        }

                        responseList.add(modEntry);
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("responseList: {}", responseList);
                    }

                    if (responseList.size() != 0)
                    {
                        response.setDnsEntries(responseList);
                        response.setRequestStatus(CoreServicesStatus.SUCCESS);
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

            throw new DNSServiceException(sqx.getMessage(), sqx);
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
                auditEntry.setAuditType(AuditType.LOADRECORD);
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
