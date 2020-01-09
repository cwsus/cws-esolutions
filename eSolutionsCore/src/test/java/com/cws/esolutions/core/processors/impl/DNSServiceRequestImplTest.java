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
 * File: DNSServiceRequestImplTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly   11/23/2008 22:39:20             Created.
 */
import org.junit.Test;
import org.junit.After;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Before;
import java.util.Calendar;
import java.util.ArrayList;
import java.text.SimpleDateFormat;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.core.processors.dto.DNSEntry;
import com.cws.esolutions.core.processors.dto.DNSRecord;
import com.cws.esolutions.core.processors.enums.DNSRecordType;
import com.cws.esolutions.core.processors.enums.ServiceRegion;
import com.cws.esolutions.core.processors.enums.DNSRecordClass;
import com.cws.esolutions.core.processors.enums.DNSRequestType;
import com.cws.esolutions.core.listeners.CoreServiceInitializer;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.core.processors.dto.DNSServiceRequest;
import com.cws.esolutions.core.processors.dto.DNSServiceResponse;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.core.processors.exception.DNSServiceException;
import com.cws.esolutions.core.processors.interfaces.IDNSServiceRequestProcessor;

public class DNSServiceRequestImplTest
{
    private static UserAccount userAccount = new UserAccount();
    private static RequestHostInfo hostInfo = new RequestHostInfo();

    private static final IDNSServiceRequestProcessor dnsService = new DNSServiceRequestProcessorImpl();

    @Before public void setUp()
    {
        hostInfo.setHostAddress("junit");
        hostInfo.setHostName("junit");

        userAccount.setStatus(LoginStatus.SUCCESS);
        userAccount.setGuid("f42fb0ba-4d1e-1126-986f-800cd2650000");
        userAccount.setUsername("khuntly");

        try
        {
            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/logging/logging.xml", false);
            CoreServiceInitializer.initializeService("eSolutionsCore/config/ServiceConfig.xml", "eSolutionsCore/logging/logging.xml", true, false);
        }
        catch (Exception ex)
        {
            Assert.fail(ex.getMessage());

            System.exit(-1);
        }
    }

    @Test public void performLookup()
    {
        DNSRecord record = new DNSRecord();
        record.setRecordName("www.google.com");
        record.setRecordType(DNSRecordType.A);

        DNSServiceRequest request = new DNSServiceRequest();
        request.setUseSystemResolver(Boolean.TRUE);
        request.setRecord(record);
        request.setRequestInfo(hostInfo);
        request.setRequestType(DNSRequestType.LOOKUP);
        request.setServiceId("B52B1DE9-37A4-4554-B85E-2EA28C4EE3DD");
        request.setUserAccount(userAccount);
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setApplicationName("eSolutions");

        try
        {
            DNSServiceResponse response = dnsService.performLookup(request);

            Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
        }
        catch (DNSServiceException dnsx)
        {
            Assert.fail(dnsx.getMessage());
        }
    }

    @Test public void createNewService()
    {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMdd00");

        // apex records
        DNSRecord nsRecord1 = new DNSRecord();
        nsRecord1.setRecordClass(DNSRecordClass.IN);
        nsRecord1.setRecordType(DNSRecordType.NS);
        nsRecord1.setRecordAddress("prodns1.caspersbox.com");

        DNSRecord nsRecord2 = new DNSRecord();
        nsRecord2.setRecordClass(DNSRecordClass.IN);
        nsRecord2.setRecordType(DNSRecordType.NS);
        nsRecord2.setRecordAddress("prodns2.caspersbox.com");

        DNSRecord nsRecord3 = new DNSRecord();
        nsRecord3.setRecordClass(DNSRecordClass.IN);
        nsRecord3.setRecordType(DNSRecordType.NS);
        nsRecord3.setRecordAddress("prodns3.caspersbox.com");

        DNSRecord nsRecord4 = new DNSRecord();
        nsRecord4.setRecordClass(DNSRecordClass.IN);
        nsRecord4.setRecordType(DNSRecordType.NS);
        nsRecord4.setRecordAddress("prodns4.caspersbox.com");

        DNSRecord rpRecord = new DNSRecord();
        rpRecord.setRecordClass(DNSRecordClass.IN);
        rpRecord.setRecordType(DNSRecordType.RP);
        rpRecord.setRecordAddress("dnsadmins.caspersbox.com");

        DNSRecord apexAddress1 = new DNSRecord();
        apexAddress1.setRecordType(DNSRecordType.A);
        apexAddress1.setRecordClass(DNSRecordClass.IN);
        apexAddress1.setRecordAddress("127.0.0.1");

        DNSRecord apexAddress2 = new DNSRecord();
        apexAddress2.setRecordType(DNSRecordType.A);
        apexAddress2.setRecordClass(DNSRecordClass.IN);
        apexAddress2.setRecordAddress("127.0.0.1");

        DNSRecord mxRecord = new DNSRecord();
        mxRecord.setRecordClass(DNSRecordClass.IN);
        mxRecord.setRecordType(DNSRecordType.MX);
        mxRecord.setRecordPriority(10);
        mxRecord.setRecordAddress("mail.mysite.com");

        DNSRecord spfRecord = new DNSRecord();
        spfRecord.setRecordClass(DNSRecordClass.IN);
        spfRecord.setRecordType(DNSRecordType.TXT);
        spfRecord.setRecordAddress("\"v=spf1 mx a ip4:127.0.0.1/8 ~all\"");

        // subrecords
        DNSRecord cnameRecord = new DNSRecord();
        cnameRecord.setRecordName("www");
        cnameRecord.setRecordClass(DNSRecordClass.IN);
        cnameRecord.setRecordType(DNSRecordType.CNAME);
        cnameRecord.setRecordAddress("mysite.com");

        DNSRecord mxAddress = new DNSRecord();
        mxAddress.setRecordName("mail");
        mxAddress.setRecordClass(DNSRecordClass.IN);
        mxAddress.setRecordType(DNSRecordType.A);
        mxAddress.setRecordAddress("127.0.0.1");

        DNSRecord aRecord = new DNSRecord();
        aRecord.setRecordName("test");
        aRecord.setRecordClass(DNSRecordClass.IN);
        aRecord.setRecordType(DNSRecordType.A);
        aRecord.setRecordAddresses(new ArrayList<String>(
    		Arrays.asList(
				"1.2.3.4",
				"4.3.2.1")));

        DNSRecord domainKeyRecord = new DNSRecord();
        domainKeyRecord.setRecordName("_domainkey");
        domainKeyRecord.setRecordClass(DNSRecordClass.IN);
        domainKeyRecord.setRecordType(DNSRecordType.TXT);
        domainKeyRecord.setRecordAddress("\"t=y; o=~;\"");

        DNSRecord dkimRecord = new DNSRecord();
        dkimRecord.setRecordName("_dkim._domainkey");
        dkimRecord.setRecordClass(DNSRecordClass.IN);
        dkimRecord.setRecordType(DNSRecordType.TXT);
        dkimRecord.setRecordAddress("\"k=rsa; p=MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA10BDb8liXHf9m+3HX5N2fl5m7N/F/n2Yi9MaDgf6zUPIDjucvQWsiUNoS9QS1JH2DkRMgx8m/AG4G+I8e6s6jt4AJwE5OBv00kHwfCQL+fLnftkUsHZ1MMOOHfCjzpNS0BYs06YWuh5IFK91EC1mQjviZJ3oxHAnSrFqGMWE86Sh8ZJnGDIEU4K3/w63MNWL17RnrNw+lo+sHTwvPQ8mJRZ7u+ZBLV33EBTYCVeF4sKZsuydUJcbXd3Jy9uI703lcw0L2BKRDmmlZiXtvlLhK1Waljc5FZPDmhT3uZZggnzmqn0w7OvZ241mJnnEHo4iE727kCSENdthvpFJhQZUWwIDAQAB\"");

        // _service._proto.name. TTL class SRV priority weight port target.
        DNSRecord srvRecord = new DNSRecord();
        srvRecord.setRecordService("_ldap");
        srvRecord.setRecordProtocol("_tcp");
        srvRecord.setRecordLifetime(900);
        srvRecord.setRecordClass(DNSRecordClass.IN);
        srvRecord.setRecordType(DNSRecordType.SRV);
        srvRecord.setRecordPriority(100);
        srvRecord.setRecordWeight(10);
        srvRecord.setRecordPort(10389);
        srvRecord.setRecordAddress("ldap.server.com");

        DNSRecord otherRecord = new DNSRecord();
        otherRecord.setRecordName("trustus");
        otherRecord.setRecordOrigin("new.mysite.com");
        otherRecord.setRecordClass(DNSRecordClass.IN);
        otherRecord.setRecordType(DNSRecordType.CNAME);
        otherRecord.setRecordAddress("8.l8.4.4");

        DNSRecord otherRecord1 = new DNSRecord();
        otherRecord1.setRecordName("nodont");
        otherRecord1.setRecordOrigin("new.mysite.com");
        otherRecord1.setRecordClass(DNSRecordClass.IN);
        otherRecord1.setRecordType(DNSRecordType.CNAME);
        otherRecord1.setRecordAddress("8.l8.4.4");

        // put it all together
        DNSEntry entry = new DNSEntry();

        // soa block
        entry.setOrigin(".");
        entry.setLifetime(900);
        entry.setSiteName("mysite.com");
        entry.setMaster("prodns1.caspersbox.com");
        entry.setOwner("hostmaster.caspersbox.com");
        entry.setSerialNumber(Integer.parseInt(sdf.format(cal.getTime())));
        entry.setRefresh(900);
        entry.setRetry(3600);
        entry.setExpiry(604800);
        entry.setMinimum(3600);

        // apex records
        entry.setApexRecords(new ArrayList<DNSRecord>(
    		Arrays.asList(
				nsRecord1,
				nsRecord2,
				nsRecord3,
				nsRecord4,
				rpRecord,
				apexAddress1,
				apexAddress2,
				mxRecord,
				spfRecord)));

        // subrecords
        entry.setSubRecords(new ArrayList<DNSRecord>(
    		Arrays.asList(
				cnameRecord,
				aRecord,
				mxAddress,
				domainKeyRecord,
				dkimRecord,
				srvRecord,
				otherRecord,
				otherRecord1)));

        DNSServiceRequest request = new DNSServiceRequest();
        request.setDnsEntry(entry); // first entry is the apex
        request.setRequestInfo(hostInfo);
        request.setRequestType(DNSRequestType.ADD);
        request.setServiceId("B52B1DE9-37A4-4554-B85E-2EA28C4EE3DD");
        request.setUserAccount(userAccount);
        request.setChangeRequest("CR1727171");
        request.setServiceRegion(ServiceRegion.DEV);
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setApplicationName("eSolutions");

        try
        {
            DNSServiceResponse response = dnsService.createNewService(request);

            Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
        }
        catch (DNSServiceException dnsx)
        {
            Assert.fail(dnsx.getMessage());
        }
    }

    @After public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
        CoreServiceInitializer.shutdown();
    }
}
