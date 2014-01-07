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
package com.cws.esolutions.agent.server.processors.impl;
/*
 * Project: eSolutionsAgent
 * Package: com.cws.esolutions.agent.server.processors.impl
 * File: AgentRequestProcessorImplTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import org.junit.Test;
import java.net.Socket;
import org.junit.Assert;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ConnectException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.io.ObjectOutputStream;
import java.net.UnknownHostException;

import com.cws.esolutions.agent.AgentDaemon;
import com.cws.esolutions.agent.dto.AgentRequest;
import com.cws.esolutions.agent.dto.AgentResponse;
import com.cws.esolutions.agent.enums.AgentStatus;
import com.cws.esolutions.agent.processors.enums.SystemCheckType;
import com.cws.esolutions.agent.processors.dto.FileManagerRequest;
import com.cws.esolutions.agent.processors.dto.ServiceCheckRequest;

public class AgentRequestProcessorImplTest
{
    @Test
    public void setUp()
    {
        System.setProperty("LOG_ROOT", "C:/temp");
        System.setProperty("appConfig", "/src/main/resources/eSolutionsServer/config/eSolutionsServer.xml");
        System.setProperty("logConfig", "/src/main/resources/logging/logging.xml");
        
        AgentDaemon.main(new String[] {"start"});
    }

    @Test
    public final void runSystemCheckNetstat()
    {
        ServiceCheckRequest sRequest = new ServiceCheckRequest();
        sRequest.setRequestType(SystemCheckType.NETSTAT);

        AgentRequest request = new AgentRequest();
        request.setAppName("esolutions");
        request.setRequestPayload(sRequest);

        try
        {
            AgentResponse response = (AgentResponse) AgentRequestProcessorImplTest.executeTcpRequest("127.0.0.1", 8181, 10000, request);

            Assert.assertEquals(AgentStatus.SUCCESS, response.getRequestStatus());
        }
        catch (Exception ax)
        {
            Assert.fail(ax.getMessage());
        }
    }

    @Test
    public final void testFileManagementRetrieveFile()
    {
        FileManagerRequest fileRequest = new FileManagerRequest();
        fileRequest.setRequestFile("C:\\temp\\error.log");

        AgentRequest request = new AgentRequest();
        request.setAppName("esolutions");
        request.setRequestPayload(fileRequest);

        try
        {
            AgentResponse response = (AgentResponse) AgentRequestProcessorImplTest.executeTcpRequest("127.0.0.1", 8181, 10000, request);

            Assert.assertEquals(AgentStatus.SUCCESS, response.getRequestStatus());
        }
        catch (Exception ax)
        {
            Assert.fail(ax.getMessage());
        }
    }

    public static Object executeTcpRequest(final String hostName, final int portNumber, final int timeout, final Object object)
    {
        Socket socket = null;
        Object resObject = null;

        final String CRLF = "\r\n";
        final String TERMINATE_TELNET = "^]";

        try
        {
            synchronized(new Object())
            {
                if (InetAddress.getByName(hostName) != null)
                {
                    InetSocketAddress socketAddress = new InetSocketAddress(hostName, portNumber);

                    socket = new Socket();
                    socket.setSoTimeout(timeout);
                    socket.setSoLinger(false, 0);
                    socket.setKeepAlive(false);
                    socket.connect(socketAddress, timeout);

                    if (socket.isConnected())
                    {
                        ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
                        outStream.writeObject(object);

                        resObject = new ObjectInputStream(socket.getInputStream()).readObject();

                        PrintWriter pWriter = new PrintWriter(socket.getOutputStream(), true);

                        pWriter.println(TERMINATE_TELNET + CRLF);

                        pWriter.flush();
                        pWriter.close();
                    }
                    else
                    {
                        throw new ConnectException("Failed to connect to host " + hostName + " on port " + portNumber);
                    }
                }
                else
                {
                    throw new UnknownHostException("No host was found in DNS for the given name: " + hostName);
                }
            }
        }
        catch (Exception ex)
        {
            Assert.fail(ex.getMessage());
        }
        finally
        {
            try
            {
                if ((socket != null) && (!(socket.isClosed())))
                {
                    socket.close();
                }
            }
            catch (IOException iox)
            {
                Assert.fail(iox.getMessage());
            }
        }

        return resObject;
    }
}
