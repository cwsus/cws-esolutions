/**
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

import java.util.List;
import org.junit.Test;
import org.junit.After;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Before;
import java.util.Calendar;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import org.apache.commons.lang.RandomStringUtils;

import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.dto.UserSecurity;
import com.cws.esolutions.core.processors.dto.DNSEntry;
import com.cws.esolutions.core.processors.dto.DNSRecord;
import com.cws.esolutions.core.processors.enums.DNSRecordType;
import com.cws.esolutions.core.processors.enums.ServiceRegion;
import com.cws.esolutions.core.processors.enums.DNSRequestType;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.core.listeners.CoreServiceInitializer;
import com.cws.esolutions.security.dao.userauth.enums.LoginType;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.core.processors.dto.DNSServiceRequest;
import com.cws.esolutions.core.processors.dto.DNSServiceResponse;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.security.processors.dto.AuthenticationRequest;
import com.cws.esolutions.core.processors.exception.DNSServiceException;
import com.cws.esolutions.security.dao.userauth.enums.AuthenticationType;
import com.cws.esolutions.security.processors.dto.AuthenticationResponse;
import com.cws.esolutions.security.processors.impl.AuthenticationProcessorImpl;
import com.cws.esolutions.core.processors.interfaces.IDNSServiceRequestProcessor;
import com.cws.esolutions.security.processors.interfaces.IAuthenticationProcessor;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.processors.test
 * DNSServiceRequestImplTest.java
 *
 * $Id: $
 * $Author: $
 * $Date: $
 * $Revision: $
 * @author kmhuntly@gmail.com
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Oct 29, 2012 3:17:41 PM
 *     Created.
 */
public class DNSServiceRequestImplTest
{
    private UserAccount userAccount = new UserAccount();
    private RequestHostInfo hostInfo = new RequestHostInfo();

    private static final IDNSServiceRequestProcessor dnsService = new DNSServiceRequestProcessorImpl();

    @Before
    public final void setUp() throws Exception
    {
        try
        {
            CoreServiceInitializer.initializeService("eSolutionsCore/config/ServiceConfig.xml", "logging/logging.xml");

            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/config/SecurityLogging.xml");

            IAuthenticationProcessor agentAuth = new AuthenticationProcessorImpl();
            hostInfo.setHostAddress("127.0.0.1");
            hostInfo.setHostName("localhost");

            UserAccount account = new UserAccount();
            account.setUsername("khuntly");
            hostInfo.setSessionId(RandomStringUtils.randomAlphanumeric(32));

            try
            {
                AuthenticationRequest userRequest = new AuthenticationRequest();
                userRequest.setApplicationName("esolutions");
                userRequest.setAuthType(AuthenticationType.LOGIN);
                userRequest.setLoginType(LoginType.USERNAME);
                userRequest.setUserAccount(account);
                userRequest.setApplicationId("B760E92F-827A-42E7-9E8D-64334657BA83");

                AuthenticationResponse userResponse = agentAuth.processAgentLogon(userRequest);

                if (userResponse.getRequestStatus() == SecurityRequestStatus.SUCCESS)
                {
                    UserAccount authUser = userResponse.getUserAccount();

                    if (authUser.getStatus() == LoginStatus.SUCCESS)
                    {
                        UserSecurity userSecurity = new UserSecurity();
                        userSecurity.setPassword("Ariana21*");

                        AuthenticationRequest passRequest = new AuthenticationRequest();
                        passRequest.setApplicationName("esolutions");
                        passRequest.setAuthType(AuthenticationType.LOGIN);
                        passRequest.setLoginType(LoginType.PASSWORD);
                        passRequest.setUserAccount(authUser);
                        passRequest.setUserSecurity(userSecurity);
                        passRequest.setApplicationId("B760E92F-827A-42E7-9E8D-64334657BA83");

                        AuthenticationResponse passResponse = agentAuth.processAgentLogon(passRequest);

                        if (passResponse.getRequestStatus() == SecurityRequestStatus.SUCCESS)
                        {
                            userAccount = passResponse.getUserAccount();
                            hostInfo.setSessionId(RandomStringUtils.randomAlphanumeric(32));
                        }
                        else
                        {
                            Assert.fail("Account login failed");
                        }
                    }
                    else
                    {
                        Assert.fail("Account login failed");
                    }
                }
                else
                {
                    Assert.fail("Account login failed");
                }
            }
            catch (Exception e)
            {
                Assert.fail(e.getMessage());
            }
        }
        catch (Exception ex)
        {
            Assert.fail(ex.getMessage());

            System.exit(-1);
        }
    }

    @Test
    public final void testForwardLookup()
    {
        DNSRecord record = new DNSRecord();
        record.setRecordName("connect.us.hsbc");
        record.setRecordType(DNSRecordType.A);

        DNSServiceRequest request = new DNSServiceRequest();
        request.setRecord(record);
        request.setRequestInfo(hostInfo);
        request.setRequestType(DNSRequestType.LOOKUP);
        request.setServiceId("B52B1DE9-37A4-4554-B85E-2EA28C4EE3DD");
        request.setUserAccount(userAccount);

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

    @Test
    public final void testCreateService()
    {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMdd00");

        DNSRecord apexns1 = new DNSRecord();
        apexns1.setRecordType(DNSRecordType.NS);
        apexns1.setPrimaryAddress(new ArrayList<>(Arrays.asList("prodns1.caspersbox.com")));
        apexns1.setRecordClass("IN");

        DNSRecord apexns2 = new DNSRecord();
        apexns2.setRecordType(DNSRecordType.NS);
        apexns2.setPrimaryAddress(new ArrayList<>(Arrays.asList("prodns2.caspersbox.com")));
        apexns2.setRecordClass("IN");

        DNSRecord apexns3 = new DNSRecord();
        apexns3.setRecordType(DNSRecordType.NS);
        apexns3.setPrimaryAddress(new ArrayList<>(Arrays.asList("prodns3.caspersbox.com")));
        apexns3.setRecordClass("IN");

        DNSRecord apexns4 = new DNSRecord();
        apexns4.setRecordType(DNSRecordType.NS);
        apexns4.setPrimaryAddress(new ArrayList<>(Arrays.asList("prodns4.caspersbox.com")));
        apexns4.setRecordClass("IN");

        DNSRecord rp = new DNSRecord();
        rp.setRecordClass("IN");
        rp.setRecordType(DNSRecordType.RP);
        rp.setPrimaryAddress(new ArrayList<>(Arrays.asList("dnsadmins.caspersbox.com")));

        DNSRecord apexAddress1 = new DNSRecord();
        apexAddress1.setRecordType(DNSRecordType.A);
        apexAddress1.setPrimaryAddress(new ArrayList<>(Arrays.asList("127.0.0.1")));
        apexAddress1.setRecordClass("IN");

        DNSRecord apexAddress2 = new DNSRecord();
        apexAddress2.setRecordType(DNSRecordType.A);
        apexAddress2.setPrimaryAddress(new ArrayList<>(Arrays.asList("127.0.0.2")));
        apexAddress2.setRecordClass("IN");

        DNSRecord mxRecord = new DNSRecord();
        mxRecord.setRecordClass("IN");
        mxRecord.setRecordType(DNSRecordType.MX);
        mxRecord.setRecordPriority(10);
        mxRecord.setPrimaryAddress(new ArrayList<>(Arrays.asList("mail.example.com")));

        List<DNSRecord> apexRecords = new ArrayList<>(
                Arrays.asList(
                        apexns1,
                        apexns2,
                        apexns3,
                        apexns4,
                        rp,
                        apexAddress1,
                        apexAddress2,
                        mxRecord));

        DNSRecord cnameRecord = new DNSRecord();
        cnameRecord.setRecordType(DNSRecordType.CNAME);
        cnameRecord.setPrimaryAddress(new ArrayList<>(Arrays.asList("example.com.")));
        cnameRecord.setRecordClass("IN");
        cnameRecord.setRecordName("www");

        DNSRecord aRecord = new DNSRecord();
        aRecord.setRecordName("test");
        aRecord.setRecordClass("IN");
        aRecord.setRecordType(DNSRecordType.A);
        aRecord.setPrimaryAddress(new ArrayList<>(Arrays.asList("127.0.2.1")));

        DNSRecord aRecord1 = new DNSRecord();
        aRecord1.setRecordName("another");
        aRecord1.setRecordClass("IN");
        aRecord1.setRecordType(DNSRecordType.A);
        aRecord1.setPrimaryAddress(new ArrayList<>(Arrays.asList("127.0.2.1", "182.1.32.1", "49.49.1.3")));

        DNSRecord txtRecord = new DNSRecord();
        txtRecord.setRecordName("_domainkey");
        txtRecord.setRecordClass("IN");
        txtRecord.setRecordType(DNSRecordType.TXT);
        txtRecord.setPrimaryAddress(new ArrayList<>(Arrays.asList("DomainKey-Signature: a=rsa-sha1;s=newyork;d=example.com;c=simple;q=dns;b=dydVyOfAKCdLXdJOc8G2q8LoXSlEniSbav+yuU4zGffruD00lszZVoG4ZHRNiYzR;")));

        DNSRecord mailRecord = new DNSRecord();
        mailRecord.setMailRecord(true);
        mailRecord.setRecordName("mail");
        mailRecord.setRecordClass("IN");
        mailRecord.setRecordType(DNSRecordType.A);
        mailRecord.setPrimaryAddress(new ArrayList<>(Arrays.asList("172.1.6.1", "172.1.2.3", "172.4.3.1")));
        mailRecord.setSpfRecord("v=spf1 ip4:192.168.10.0/24 mx ?all");

        // _service._proto.name. TTL class SRV priority weight port target.
        DNSRecord srvRecord = new DNSRecord();
        srvRecord.setRecordService("ldap");
        srvRecord.setRecordProtocol("tcp");
        srvRecord.setRecordName("example.com");
        srvRecord.setRecordLifetime(900);
        srvRecord.setRecordClass("IN");
        srvRecord.setRecordType(DNSRecordType.SRV);
        srvRecord.setRecordPriority(100);
        srvRecord.setRecordWeight(10);
        srvRecord.setRecordPort(10389);
        srvRecord.setPrimaryAddress(new ArrayList<>(Arrays.asList("ldap.server.com")));

        List<DNSRecord> subRecords = new ArrayList<>(
                Arrays.asList(
                        cnameRecord,
                        aRecord,
                        aRecord1,
                        txtRecord,
                        srvRecord,
                        mailRecord));

        DNSEntry entry = new DNSEntry();
        entry.setApex("example.com");
        entry.setMaster("prodns.caspersbox.com");
        entry.setOwner("hostmaster.caspersbox.com");
        entry.setSerialNumber(sdf.format(cal.getTime())); // this would be generated, not static
        entry.setApexRecords(apexRecords);
        entry.setSubRecords(subRecords);

        DNSServiceRequest request = new DNSServiceRequest();
        request.setDnsEntry(entry); // first entry is the apex
        request.setRequestInfo(hostInfo);
        request.setRequestType(DNSRequestType.ADD);
        request.setServiceId("B52B1DE9-37A4-4554-B85E-2EA28C4EE3DD");
        request.setUserAccount(userAccount);
        request.setChangeRequest("CR1727171");
        request.setServiceRegion(ServiceRegion.DEV);

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

    @Test
    public void testPushNewService()
    {
        StringBuilder builder = new StringBuilder()
            .append("$ORIGIN .\n")
            .append("$TTL 900\n")
            .append("mysite.com IN SOA prodns.caspersbox.com hostmaster.caspersbox.com (\n")
            .append("       2013072300\n")
            .append("       900\n")
            .append("       3600\n")
            .append("       604800\n")
            .append("       3600\n")
            .append("       )\n")
            .append("       IN      NS              prodns1.caspersbox.com\n")
            .append("       IN      NS              prodns2.caspersbox.com\n")
            .append("       IN      NS              prodns3.caspersbox.com\n")
            .append("       IN      NS              prodns4.caspersbox.com\n")
            .append("       IN      RP              dnsadmins.caspersbox.com\n")
            .append("       IN      A               127.0.0.1\n")
            .append("       IN      A               127.0.0.2\n")
            .append("       IN      MX      10      mail.mysite.com\n")
            .append("\n")
            .append("$ORIGIN mysite.com\n")
            .append("www                        IN      CNAME                   mysite.com.\n")
            .append("test                       IN      A                       127.0.2.1\n")
            .append("another                    IN      A                       127.0.2.1\n")
            .append("                           IN      A                       182.1.32.1\n")
            .append("                           IN      A                       49.49.1.3\n")
            .append("_domainkey                 IN      TXT                     \"DomainKey-Signature: a=rsa-sha1;s=newyork;d=example.com;c=simple;q=dns;b=dydVyOfAKCdLXdJOc8G2q8LoXSlEniSbav+yuU4zGffruD00lszZVoG4ZHRNiYzR;\"\n")
            .append("_ldap._tcp.mysite.com.     IN      100     10      10389   ldap.server.com\n")
            .append("mail                       IN      A                       172.1.6.1\n")
            .append("                           IN      A                       172.1.2.3\n")
            .append("                           IN      A                       172.4.3.1\n")
            .append("                           IN      TXT                     \"v=spf1 ip4:192.168.10.0/24 mx ?all\"\n");

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMdd00");

        DNSRecord apexns1 = new DNSRecord();
        apexns1.setRecordName("mysite.com");
        apexns1.setRecordType(DNSRecordType.NS);
        apexns1.setPrimaryAddress(new ArrayList<>(Arrays.asList("prodns1.caspersbox.com")));
        apexns1.setRecordClass("IN");
        apexns1.setRecordOrigin(".");

        DNSRecord apexns2 = new DNSRecord();
        apexns2.setRecordType(DNSRecordType.NS);
        apexns2.setPrimaryAddress(new ArrayList<>(Arrays.asList("prodns2.caspersbox.com")));
        apexns2.setRecordClass("IN");
        apexns2.setRecordOrigin(".");
        apexns2.setRecordName("mysite.com");

        DNSRecord apexns3 = new DNSRecord();
        apexns3.setRecordType(DNSRecordType.NS);
        apexns3.setPrimaryAddress(new ArrayList<>(Arrays.asList("prodns3.caspersbox.com")));
        apexns3.setRecordClass("IN");
        apexns3.setRecordOrigin(".");
        apexns3.setRecordName("mysite.com");

        DNSRecord apexns4 = new DNSRecord();
        apexns4.setRecordType(DNSRecordType.NS);
        apexns4.setPrimaryAddress(new ArrayList<>(Arrays.asList("prodns4.caspersbox.com")));
        apexns4.setRecordClass("IN");
        apexns4.setRecordOrigin(".");
        apexns4.setRecordName("mysite.com");

        DNSRecord rp = new DNSRecord();
        rp.setRecordClass("IN");
        rp.setRecordType(DNSRecordType.RP);
        rp.setPrimaryAddress(new ArrayList<>(Arrays.asList("dnsadmins.caspersbox.com")));
        rp.setRecordOrigin(".");
        rp.setRecordName("mysite.com");

        DNSRecord apexAddress1 = new DNSRecord();
        apexAddress1.setRecordType(DNSRecordType.A);
        apexAddress1.setPrimaryAddress(new ArrayList<>(Arrays.asList("127.0.0.1")));
        apexAddress1.setRecordClass("IN");
        apexAddress1.setRecordOrigin(".");
        apexAddress1.setRecordName("mysite.com");

        DNSRecord apexAddress2 = new DNSRecord();
        apexAddress2.setRecordType(DNSRecordType.A);
        apexAddress2.setPrimaryAddress(new ArrayList<>(Arrays.asList("127.0.0.2")));
        apexAddress2.setRecordClass("IN");
        apexAddress2.setRecordOrigin(".");
        apexAddress2.setRecordName("mysite.com");

        DNSRecord mxRecord = new DNSRecord();
        mxRecord.setRecordClass("IN");
        mxRecord.setRecordType(DNSRecordType.MX);
        mxRecord.setRecordPriority(10);
        mxRecord.setPrimaryAddress(new ArrayList<>(Arrays.asList("mail.mysite.com")));
        mxRecord.setRecordOrigin(".");
        mxRecord.setRecordName("mysite.com");

        List<DNSRecord> apexRecords = new ArrayList<>(
                Arrays.asList(
                        apexns1,
                        apexns2,
                        apexns3,
                        apexns4,
                        rp,
                        apexAddress1,
                        apexAddress2,
                        mxRecord));

        DNSRecord cnameRecord = new DNSRecord();
        cnameRecord.setRecordType(DNSRecordType.CNAME);
        cnameRecord.setPrimaryAddress(new ArrayList<>(Arrays.asList("mysite.com.")));
        cnameRecord.setRecordClass("IN");
        cnameRecord.setRecordName("www");
        cnameRecord.setRecordOrigin("mysite.com");

        DNSRecord aRecord = new DNSRecord();
        aRecord.setRecordName("test");
        aRecord.setRecordClass("IN");
        aRecord.setRecordType(DNSRecordType.A);
        aRecord.setPrimaryAddress(new ArrayList<>(Arrays.asList("127.0.2.1")));
        aRecord.setRecordOrigin("mysite.com");

        DNSRecord aRecord1 = new DNSRecord();
        aRecord1.setRecordName("another");
        aRecord1.setRecordClass("IN");
        aRecord1.setRecordType(DNSRecordType.A);
        aRecord1.setPrimaryAddress(new ArrayList<>(Arrays.asList("127.0.2.1", "182.1.32.1", "49.49.1.3")));
        aRecord1.setRecordOrigin("mysite.com");

        DNSRecord txtRecord = new DNSRecord();
        txtRecord.setRecordName("_domainkey");
        txtRecord.setRecordClass("IN");
        txtRecord.setRecordType(DNSRecordType.TXT);
        txtRecord.setPrimaryAddress(new ArrayList<>(Arrays.asList("DomainKey-Signature: a=rsa-sha1;s=newyork;d=example.com;c=simple;q=dns;b=dydVyOfAKCdLXdJOc8G2q8LoXSlEniSbav+yuU4zGffruD00lszZVoG4ZHRNiYzR;")));
        txtRecord.setRecordOrigin("mysite.com");

        DNSRecord mailRecord = new DNSRecord();
        mailRecord.setMailRecord(true);
        mailRecord.setRecordName("mail");
        mailRecord.setRecordClass("IN");
        mailRecord.setRecordType(DNSRecordType.A);
        mailRecord.setPrimaryAddress(new ArrayList<>(Arrays.asList("172.1.6.1", "172.1.2.3", "172.4.3.1")));
        mailRecord.setSpfRecord("v=spf1 ip4:192.168.10.0/24 mx ?all");
        mailRecord.setRecordOrigin("mysite.com");

        // _service._proto.name. TTL class SRV priority weight port target.
        DNSRecord srvRecord = new DNSRecord();
        srvRecord.setRecordService("ldap");
        srvRecord.setRecordProtocol("tcp");
        srvRecord.setRecordName("mysite.com");
        srvRecord.setRecordLifetime(900);
        srvRecord.setRecordClass("IN");
        srvRecord.setRecordType(DNSRecordType.SRV);
        srvRecord.setRecordPriority(100);
        srvRecord.setRecordWeight(10);
        srvRecord.setRecordPort(10389);
        srvRecord.setPrimaryAddress(new ArrayList<>(Arrays.asList("ldap.server.com")));
        srvRecord.setRecordOrigin("mysite.com");

        List<DNSRecord> subRecords = new ArrayList<>(
                Arrays.asList(
                        cnameRecord,
                        aRecord,
                        aRecord1,
                        txtRecord,
                        srvRecord,
                        mailRecord));

        DNSEntry entry = new DNSEntry();
        entry.setApex("mysite.com");
        entry.setMaster("prodns.caspersbox.com");
        entry.setOwner("hostmaster.caspersbox.com");
        entry.setSerialNumber(sdf.format(cal.getTime())); // this would be generated, not static
        entry.setApexRecords(apexRecords);
        entry.setSubRecords(subRecords);
        entry.setZoneData(builder);
        entry.setProjectCode("PRDPRJ");
        entry.setFileName("db.mysite");
        entry.setOrigin(".");

        DNSServiceRequest request = new DNSServiceRequest();
        request.setDnsEntry(entry); // first entry is the apex
        request.setRequestInfo(hostInfo);
        request.setRequestType(DNSRequestType.ADD);
        request.setServiceId("B52B1DE9-37A4-4554-B85E-2EA28C4EE3DD");
        request.setUserAccount(userAccount);
        request.setChangeRequest("CR1727171");
        request.setServiceRegion(ServiceRegion.DEV);

        try
        {
            DNSServiceResponse response = dnsService.pushNewService(request);

            Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
        }
        catch (DNSServiceException dnsx)
        {
            Assert.fail(dnsx.getMessage());
        }
    }

    @Test
    public void testGetDataFromDatabase()
    {
        DNSEntry entry = new DNSEntry();
        entry.setProjectCode("PRDPRJ");
        entry.setApex("example.com");

        DNSServiceRequest request = new DNSServiceRequest();
        request.setDnsEntry(entry);
        request.setUserAccount(userAccount);
        request.setRequestInfo(hostInfo);
        request.setServiceId("B52B1DE9-37A4-4554-B85E-2EA28C4EE3DD");

        try
        {
            DNSServiceResponse response = dnsService.getDataFromDatabase(request);

            Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
        }
        catch (DNSServiceException dnsx)
        {
            Assert.fail(dnsx.getMessage());
        }
    }

    @After
    public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
