/*
 * Copyright (c) 2009 - 2014 CaspersBox Web Services
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
 * File: OracleVBoxManagerTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.Assert;

import com.cws.esolutions.core.CoreServiceBean;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.core.processors.dto.Server;
import com.cws.esolutions.core.processors.enums.ServerType;
import com.cws.esolutions.core.listeners.CoreServiceInitializer;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.core.processors.dto.VirtualServiceRequest;
import com.cws.esolutions.core.processors.dto.VirtualServiceResponse;
import com.cws.esolutions.security.processors.dto.AuthenticationData;
import com.cws.esolutions.core.processors.factory.VirtualManagerFactory;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.core.processors.interfaces.VirtualServiceManager;
import com.cws.esolutions.core.processors.exception.VirtualServiceException;

public class OracleVBoxManagerTest
{
    private static UserAccount userAccount = null;
    private static RequestHostInfo hostInfo = null;

    private static final CoreServiceBean appBean = CoreServiceBean.getInstance();

    @Before
    public void setUp()
    {
        try
        {
            hostInfo.setHostAddress("junit");
            hostInfo.setHostName("junit");

            userAccount.setStatus(LoginStatus.SUCCESS);
            userAccount.setGuid("f42fb0ba-4d1e-1126-986f-800cd2650000");
            userAccount.setUsername("khuntly");

            CoreServiceInitializer.initializeService("eSolutionsCore/config/ServiceConfig.xml", "logging/logging.xml", true);

            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/logging/logging.xml", false);
        }
        catch (Exception ex)
        {
            Assert.fail(ex.getMessage());

            System.exit(-1);
        }
    }

    /**
     * Test method for {@link com.cws.esolutions.core.processors.interfaces.VirtualServiceManager#listVirtualMachines(com.cws.esolutions.core.processors.dto.VirtualServiceRequest)}.
     */
    @Test
    public void listVirtualMachines()
    {
        VirtualServiceManager virtManager = VirtualManagerFactory.createVirtualManager(appBean.getConfigData().getAppConfig().getVirtualManagerClass());

        UserAccount userAccount = new UserAccount();
        userAccount.setUsername("vbox");

        AuthenticationData userSecurity = new AuthenticationData();
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
        CoreServiceInitializer.shutdown();
    }
}
