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

import com.cws.esolutions.core.processors.dto.DNSRecord;
import com.cws.esolutions.core.enums.CoreServicesStatus;
import com.cws.esolutions.utility.coreutils.NetworkUtils;
import com.cws.esolutions.core.processors.enums.ServerType;
import com.cws.esolutions.utility.exception.UtilityException;
import com.cws.esolutions.core.processors.enums.DNSRecordType;
import com.cws.esolutions.core.processors.dto.DNSServiceRequest;
import com.cws.esolutions.core.processors.dto.DNSServiceResponse;
import com.cws.esolutions.core.processors.exception.DNSServiceException;
import com.cws.esolutions.core.processors.interfaces.IDNSServiceRequestProcessor;
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
                List<String[]> serverList = dao.getServersByAttribute(ServerType.DNSSLAVE.name(), 0);

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
}
