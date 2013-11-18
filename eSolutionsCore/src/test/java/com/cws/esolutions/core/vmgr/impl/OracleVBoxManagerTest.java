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
package com.cws.esolutions.core.vmgr.impl;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.Assert;
import org.apache.commons.lang.RandomStringUtils;

import com.cws.esolutions.core.CoreServiceBean;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.dto.UserSecurity;
import com.cws.esolutions.core.processors.dto.Server;
import com.cws.esolutions.core.processors.enums.ServerType;
import com.cws.esolutions.core.vmgr.dto.VirtualServiceRequest;
import com.cws.esolutions.core.vmgr.dto.VirtualServiceResponse;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.core.listeners.CoreServiceInitializer;
import com.cws.esolutions.security.dao.userauth.enums.LoginType;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.core.vmgr.factory.VirtualManagerFactory;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.core.vmgr.interfaces.VirtualServiceManager;
import com.cws.esolutions.core.vmgr.exception.VirtualServiceException;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.security.processors.dto.AuthenticationRequest;
import com.cws.esolutions.security.dao.userauth.enums.AuthenticationType;
import com.cws.esolutions.security.processors.dto.AuthenticationResponse;
import com.cws.esolutions.security.processors.impl.AuthenticationProcessorImpl;
import com.cws.esolutions.security.processors.interfaces.IAuthenticationProcessor;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.vmgr.impl
 * OracleVBoxManagerTest.java
 *
 * $Id: $
 * $Author: $
 * $Date: $
 * $Revision: $
 * @author khuntly
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * khuntly @ Apr 19, 2013 7:47:32 AM
 *     Created.
 */
public class OracleVBoxManagerTest
{
    private UserAccount userAccount = null;
    private RequestHostInfo hostInfo = null;

    private static final CoreServiceBean appBean = CoreServiceBean.getInstance();

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
                        userSecurity.setPassword("Ariana16*");

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

    /**
     * Test method for {@link com.cws.esolutions.core.vmgr.impl.OracleVBoxManager#getVBoxManager()}.
     */
    @Test
    public final void testListVirtualMachines()
    {
        VirtualServiceManager virtManager = VirtualManagerFactory.createVirtualManager(appBean.getConfigData().getAppConfig().getVirtualManagerClass());

        UserAccount userAccount = new UserAccount();
        userAccount.setUsername("vbox");

        UserSecurity userSecurity = new UserSecurity();
        userSecurity.setUserSalt("vyhAFAC1oap0kJfQaf5jtCAxZfzRKJIf");
        userSecurity.setPassword("D5KrIXmupcw68p4dJrMpjkn1nvEysU0zvpylKIlPRuRc5qQRZeGVo/zwjmajpLQtH4Yq9Ndfs6BNqT3gaNueY6dq+5b/h2h60a0f8PxWhkg=");

        Server server = new Server();
        server.setMgrUrl("http://192.168.10.250:18083/");
        server.setServerType(ServerType.VIRTUALHOST);

        VirtualServiceRequest request = new VirtualServiceRequest();
        request.setServer(server);
        request.setUserAccount(userAccount);
        request.setUserSecurity(userSecurity);

        try
        {
            VirtualServiceResponse response = virtManager.listVirtualMachines(request);

            Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
        }
        catch (VirtualServiceException vsx)
        {
            Assert.fail(vsx.getMessage());
        }
    }

    @After
    public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
