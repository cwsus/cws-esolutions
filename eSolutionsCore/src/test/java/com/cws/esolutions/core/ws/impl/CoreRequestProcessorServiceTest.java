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
package com.cws.esolutions.core.ws.impl;
/*
 * Project: eSolutionsCore
 * Package:com.cws.esolutions.core.ws.impl
 * File: CoreRequestProcessorServiceTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.net.URL;
import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;
import javax.xml.ws.Service;
import javax.xml.namespace.QName;
import org.apache.commons.lang.RandomStringUtils;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.dto.UserSecurity;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.dao.userauth.enums.LoginType;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.security.processors.dto.AuthenticationRequest;
import com.cws.esolutions.security.dao.userauth.enums.AuthenticationType;
import com.cws.esolutions.security.processors.dto.AuthenticationResponse;
import com.cws.esolutions.core.ws.interfaces.ICoreRequestProcessorService;
import com.cws.esolutions.security.processors.exception.AuthenticationException;

public class CoreRequestProcessorServiceTest
{
    private ICoreRequestProcessorService webService = null;
    private RequestHostInfo reqInfo = new RequestHostInfo();

    @Before
    public void setUp() throws Exception
    {
        this.reqInfo.setHostAddress("junit.caspersbox.com");
        this.reqInfo.setHostName("junit.caspersbox.com");
        this.reqInfo.setSessionId(RandomStringUtils.randomAlphanumeric(32));

        URL url = new URL("https://esolutions.caspersbox.com:10944/eSolutions/ws/CoreRequestProcessorService?wsdl");
        QName qName = new QName("http://agent.caspersbox.corp/s?q=esolutions", "CoreRequestProcessorService");
        Service service = Service.create(url, qName);
        this.webService = service.getPort(ICoreRequestProcessorService.class);
    }

    @Test
    public void testUsernameAuthentication()
    {
        UserAccount account = new UserAccount();
        account.setUsername("khuntly");

        AuthenticationRequest request = new AuthenticationRequest();
        request.setApplicationName("eSolutions");
        request.setAuthType(AuthenticationType.LOGIN);
        request.setLoginType(LoginType.USERNAME);
        request.setUserAccount(account);
        request.setApplicationName("esolutions");
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");

        try
        {
            AuthenticationResponse response = this.webService.processAgentLogon(request);

            Assert.assertEquals(SecurityRequestStatus.SUCCESS, response.getRequestStatus());
        }
        catch (AuthenticationException ax)
        {
            Assert.fail(ax.getMessage());
        }
    }

    @Test
    public void testPasswordAuthentication()
    {
        UserAccount account = new UserAccount();
        account.setUsername("khuntly");
        account.setGuid("74d9729b-7fb2-4fef-874b-c9ee5d7a5a95");
        account.setOlrSetup(false);
        account.setOlrLocked(false);
        account.setSuspended(false);
        account.setStatus(LoginStatus.SUCCESS);
        account.setTcAccepted(true);

        UserSecurity userSecurity = new UserSecurity();
        userSecurity.setPassword("Ariana18*");

        AuthenticationRequest request = new AuthenticationRequest();
        request.setApplicationName("esolutions");
        request.setAuthType(AuthenticationType.LOGIN);
        request.setLoginType(LoginType.PASSWORD);
        request.setUserAccount(account);
        request.setUserSecurity(userSecurity);
        request.setHostInfo(this.reqInfo);
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");

        try
        {
            AuthenticationResponse response = this.webService.processAgentLogon(request);

            Assert.assertEquals(SecurityRequestStatus.SUCCESS, response.getRequestStatus());
        }
        catch (AuthenticationException ax)
        {
            Assert.fail(ax.getMessage());
        }
    }

    @Test
    public void testCombinedAuthentication()
    {
        UserAccount account = new UserAccount();
        account.setUsername("khuntly");

        UserSecurity userSecurity = new UserSecurity();
        userSecurity.setPassword("Ariana16*");

        AuthenticationRequest request = new AuthenticationRequest();
        request.setApplicationName("esolutions");
        request.setAuthType(AuthenticationType.LOGIN);
        request.setLoginType(LoginType.COMBINED);
        request.setUserAccount(account);
        request.setUserSecurity(userSecurity);
        request.setHostInfo(this.reqInfo);
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");

        try
        {
            AuthenticationResponse response = this.webService.processAgentLogon(request);

            Assert.assertEquals(SecurityRequestStatus.SUCCESS, response.getRequestStatus());
        }
        catch (AuthenticationException ax)
        {
            Assert.fail(ax.getMessage());
        }
    }

    @Test
    public void testFailedResponse()
    {
        UserAccount account = new UserAccount();
        account.setUsername("khuntly");

        UserSecurity userSecurity = new UserSecurity();
        userSecurity.setPassword("Ariana18*");

        AuthenticationRequest request = new AuthenticationRequest();
        request.setApplicationName("esolutions");
        request.setAuthType(AuthenticationType.LOGIN);
        request.setLoginType(LoginType.COMBINED);
        request.setUserAccount(account);
        request.setUserSecurity(userSecurity);
        request.setHostInfo(this.reqInfo);
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");

        try
        {
            AuthenticationResponse response = this.webService.processAgentLogon(request);

            Assert.assertEquals(SecurityRequestStatus.FAILURE, response.getRequestStatus());
        }
        catch (AuthenticationException ax)
        {
            Assert.fail(ax.getMessage());
        }
    }

    @Test
    public void testObtainUserSecurityConfig()
    {
        UserAccount account = new UserAccount();
        account.setUsername("khuntly");
        account.setGuid("74d9729b-7fb2-4fef-874b-c9ee5d7a5a95");

        AuthenticationRequest request = new AuthenticationRequest();
        request.setApplicationName("eSolutions");
        request.setAuthType(AuthenticationType.LOGIN);
        request.setLoginType(LoginType.USERNAME);
        request.setUserAccount(account);
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setApplicationName("esolutions");
        request.setHostInfo(this.reqInfo);

        try
        {
            AuthenticationResponse response = this.webService.obtainUserSecurityConfig(request);

            Assert.assertEquals(SecurityRequestStatus.SUCCESS, response.getRequestStatus());
        }
        catch (AuthenticationException ax)
        {
            Assert.fail(ax.getMessage());
        }
    }

    @Test
    public void testVerifyUserSecurityConfig()
    {
        UserAccount account = new UserAccount();
        account.setUsername("khuntly");
        account.setGuid("74d9729b-7fb2-4fef-874b-c9ee5d7a5a95");

        UserSecurity userSecurity = new UserSecurity();
        userSecurity.setSecAnswerOne("answerone");
        userSecurity.setSecAnswerTwo("answertwo");

        AuthenticationRequest request = new AuthenticationRequest();
        request.setApplicationName("eSolutions");
        request.setAuthType(AuthenticationType.LOGIN);
        request.setLoginType(LoginType.USERNAME);
        request.setUserAccount(account);
        request.setUserSecurity(userSecurity);
        request.setApplicationId("6236B840-88B0-4230-BCBC-8EC33EE837D9");
        request.setApplicationName("esolutions");
        request.setHostInfo(this.reqInfo);

        try
        {
            AuthenticationResponse response = this.webService.verifyUserSecurityConfig(request);

            Assert.assertEquals(SecurityRequestStatus.SUCCESS, response.getRequestStatus());
        }
        catch (AuthenticationException ax)
        {
            Assert.fail(ax.getMessage());
        }
    }

    @Test
    public final void testPerformLookup()
    {
        Assert.fail("Not yet implemented");
    }

    @Test
    public final void testCreateNewService()
    {
        Assert.fail("Not yet implemented");
    }

    @Test
    public final void testPerformServiceFailover()
    {
        Assert.fail("Not yet implemented");
    }

    @Test
    public final void testPerformSiteTransfer()
    {
        Assert.fail("Not yet implemented");
    }

    @Test
    public final void testListTopArticles()
    {
        Assert.fail("Not yet implemented");
    }

    @Test
    public final void testAddNewArticle()
    {
        Assert.fail("Not yet implemented");
    }

    @Test
    public final void testUpdateArticle()
    {
        Assert.fail("Not yet implemented");
    }

    @Test
    public final void testApproveArticle()
    {
        Assert.fail("Not yet implemented");
    }

    @Test
    public final void testRejectArticle()
    {
        Assert.fail("Not yet implemented");
    }

    @Test
    public final void testDeleteArticle()
    {
        Assert.fail("Not yet implemented");
    }

    @Test
    public final void testGetArticle()
    {
        Assert.fail("Not yet implemented");
    }

    @Test
    public final void testGetPendingArticles()
    {
        Assert.fail("Not yet implemented");
    }

    @Test
    public final void testDoArticleSearch()
    {
        Assert.fail("Not yet implemented");
    }

    @Test
    public final void testDoServerSearch()
    {
        Assert.fail("Not yet implemented");
    }

    @Test
    public final void testDoSiteSearch()
    {
        Assert.fail("Not yet implemented");
    }

    @Test
    public final void testDoMessageSearch()
    {
        Assert.fail("Not yet implemented");
    }

    @Test
    public final void testDoFileSearch()
    {
        Assert.fail("Not yet implemented");
    }
}