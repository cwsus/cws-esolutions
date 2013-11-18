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
package com.cws.esolutions.security.processors.impl;

import org.junit.Test;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;

import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.dto.UserSecurity;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.dao.userauth.enums.LoginType;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.security.processors.dto.FileSecurityRequest;
import com.cws.esolutions.security.processors.dto.FileSecurityResponse;
import com.cws.esolutions.security.processors.dto.AuthenticationRequest;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.security.dao.userauth.enums.AuthenticationType;
import com.cws.esolutions.security.processors.dto.AuthenticationResponse;
import com.cws.esolutions.security.processors.impl.FileSecurityProcessorImpl;
import com.cws.esolutions.security.processors.exception.FileSecurityException;
import com.cws.esolutions.security.processors.interfaces.IFileSecurityProcessor;
import com.cws.esolutions.security.processors.interfaces.IAuthenticationProcessor;
/**
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.security.processors.impl
 * File: FileSecurityProcessorImplTest.java
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
 * 35033355 @ Jul 15, 2013 10:17:32 AM
 *     Created.
 */
public class FileSecurityProcessorImplTest
{
    private UserAccount userAccount = new UserAccount();
    private RequestHostInfo hostInfo = new RequestHostInfo();

    @Before
    public final void setUp()
    {
        try
        {
            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/config/SecurityLogging.xml");

            IAuthenticationProcessor agentAuth = new AuthenticationProcessorImpl();

            hostInfo = new RequestHostInfo();
            hostInfo.setHostAddress("junit");
            hostInfo.setHostName("junit");

            UserAccount account = new UserAccount();
            account.setUsername("khuntly");
            hostInfo.setSessionId(RandomStringUtils.randomAlphanumeric(32));

            try
            {
                AuthenticationRequest userRequest = new AuthenticationRequest();
                userRequest.setApplicationName("eSolutions");
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
                        passRequest.setApplicationName("eSolutions");
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

                            Assert.assertEquals(LoginStatus.SUCCESS, userAccount.getStatus());
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
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            System.exit(1);
        }
    }

    @Test
    public final void testSignFile()
    {
        IFileSecurityProcessor processor = new FileSecurityProcessorImpl();

        FileSecurityRequest request = new FileSecurityRequest();
        request.setHostInfo(hostInfo);
        request.setUserAccount(userAccount);
        request.setAlgorithm("SHA1withRSA");
        request.setSignedFile(FileUtils.getFile("C:/Temp/myfile.sig"));
        request.setUnsignedFile(FileUtils.getFile("C:/Temp/myfile.txt"));

        try
        {
            FileSecurityResponse response = processor.signFile(request);

            Assert.assertEquals(SecurityRequestStatus.SUCCESS, response.getRequestStatus());
        }
        catch (FileSecurityException fsx)
        {
            Assert.fail(fsx.getMessage());
        }
    }

    @Test
    public final void testVerifyFile()
    {
        IFileSecurityProcessor processor = new FileSecurityProcessorImpl();

        FileSecurityRequest request = new FileSecurityRequest();
        request.setAlgorithm("SHA1withRSA");
        request.setHostInfo(hostInfo);
        request.setUserAccount(userAccount);
        request.setSignedFile(FileUtils.getFile("C:/Temp/myfile.sig"));
        request.setUnsignedFile(FileUtils.getFile("C:/Temp/myfile.txt"));

        try
        {
            FileSecurityResponse response = processor.verifyFile(request);

            Assert.assertTrue(response.isSignatureValid());
        }
        catch (FileSecurityException fsx)
        {
            Assert.fail(fsx.getMessage());
        }
    }

    @Test
    public final void testEncryptFile()
    {
        IFileSecurityProcessor processor = new FileSecurityProcessorImpl();

        FileSecurityRequest request = new FileSecurityRequest();
        request.setAlgorithm("RSA");
        request.setHostInfo(hostInfo);
        request.setUserAccount(userAccount);
        request.setDecryptedFile(FileUtils.getFile("C:/Temp/myfile.txt"));
        request.setEncryptedFile(FileUtils.getFile("C:/Temp/myfile.enc"));

        try
        {
            FileSecurityResponse response = processor.encryptFile(request);

            Assert.assertEquals(SecurityRequestStatus.SUCCESS, response.getRequestStatus());
        }
        catch (FileSecurityException fsx)
        {
            Assert.fail(fsx.getMessage());
        }
    }

    @Test
    public final void testDecryptFile()
    {
        IFileSecurityProcessor processor = new FileSecurityProcessorImpl();

        FileSecurityRequest request = new FileSecurityRequest();
        request.setAlgorithm("RSA");
        request.setHostInfo(hostInfo);
        request.setUserAccount(userAccount);
        request.setDecryptedFile(FileUtils.getFile("C:/Temp/myfile.txt"));
        request.setEncryptedFile(FileUtils.getFile("C:/Temp/myfile.enc"));

        try
        {
            FileSecurityResponse response = processor.decryptFile(request);

            Assert.assertEquals(SecurityRequestStatus.SUCCESS, response.getRequestStatus());
        }
        catch (FileSecurityException fsx)
        {
            Assert.fail(fsx.getMessage());
        }
    }

    @After
    public void tearDown() throws Exception
    {
        SecurityServiceInitializer.shutdown();
    }
}
