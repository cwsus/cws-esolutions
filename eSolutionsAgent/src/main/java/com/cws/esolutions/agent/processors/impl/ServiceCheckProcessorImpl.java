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
package com.cws.esolutions.agent.processors.impl;
/*
 * Project: eSolutionsAgent
 * Package: com.cws.esolutions.agent.processors.impl
 * File: ApplicationManagerProcessorImpl.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import java.io.File;
import java.net.Socket;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.net.SocketException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.exec.DefaultExecuteResultHandler;

import com.cws.esolutions.agent.enums.AgentStatus;
import com.cws.esolutions.agent.processors.exception.ServiceCheckException;
import com.cws.esolutions.agent.processors.interfaces.IServiceCheckProcessor;
import com.cws.esolutions.core.processors.dto.ServiceCheckRequest;
import com.cws.esolutions.core.processors.dto.ServiceCheckResponse;
/**
 * @see com.cws.esolutions.agent.processors.interfaces.IServiceCheckProcessor
 */
public class ServiceCheckProcessorImpl implements IServiceCheckProcessor
{
    public ServiceCheckResponse runSystemCheck(final ServiceCheckRequest request) throws ServiceCheckException
    {
        final String methodName = IServiceCheckProcessor.CNAME + "#runSystemCheck(final ServiceCheckRequest request) throws ServiceCheckException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ServiceCheckRequest: {}", request);
        }

        int exitCode = -1;
        Socket socket = null;
        File sourceFile = null;
        CommandLine command = null;
        BufferedWriter writer = null;
        ExecuteStreamHandler streamHandler = null;
        ByteArrayOutputStream outputStream = null;
        ServiceCheckResponse response = new ServiceCheckResponse();

        final DefaultExecutor executor = new DefaultExecutor();
        final ExecuteWatchdog watchdog = new ExecuteWatchdog(CONNECT_TIMEOUT * 1000);
        final DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        try
        {
            switch (request.getRequestType())
            {
                case NETSTAT:
                    sourceFile = scriptConfig.getScripts().get("netstat");

                    if (DEBUG)
                    {
                        DEBUGGER.debug("sourceFile: {}", sourceFile);
                    }

                    if (!(sourceFile.canExecute()))
                    {
                        throw new ServiceCheckException("Script file either does not exist or cannot be executed. Cannot continue.");
                    }

                    command = CommandLine.parse(sourceFile.getAbsolutePath());

                    if (request.getPortNumber() != 0)
                    {
                        command.addArgument(String.valueOf(request.getPortNumber()), true);
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("CommandLine: {}", command);
                    }

                    outputStream = new ByteArrayOutputStream();
                    streamHandler = new PumpStreamHandler(outputStream);

                    executor.setWatchdog(watchdog);
                    executor.setStreamHandler(streamHandler);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("ExecuteStreamHandler: {}", streamHandler);
                        DEBUGGER.debug("ExecuteWatchdog: {}", watchdog);
                        DEBUGGER.debug("DefaultExecuteResultHandler: {}", resultHandler);
                        DEBUGGER.debug("DefaultExecutor: {}", executor);
                    }

                    executor.execute(command, resultHandler);

                    resultHandler.waitFor();
                    exitCode = resultHandler.getExitValue();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("exitCode: {}", exitCode);
                    }

                    writer = new BufferedWriter(new FileWriter(LOGS_DIRECTORY + "/" + sourceFile.getName() + ".log"));
                    writer.write(outputStream.toString());
                    writer.flush();

                    response.setResponseData(outputStream.toString());

                    if (executor.isFailure(exitCode))
                    {
                        response.setRequestStatus(AgentStatus.FAILURE);
                    }
                    else
                    {
                        response.setRequestStatus(AgentStatus.SUCCESS);
                    }

                    break;
                case REMOTEDATE:
                    response.setRequestStatus(AgentStatus.SUCCESS);
                    response.setResponseData(System.currentTimeMillis());

                    break;
                case TELNET:
                    response = new ServiceCheckResponse();

                    int targetPort = request.getPortNumber();
                    String targetServer = request.getTargetHost();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Target port: {}", targetPort);
                        DEBUGGER.debug("Target server: {}", targetServer);
                    }

                    if (targetPort == 0)
                    {
                        throw new ServiceCheckException("Target port number was not assigned. Cannot action request.");
                    }

                    final String CRLF = "\r\n";
                    final String TERMINATE_TELNET = "^]";

                    synchronized (new Object())
                    {
                        InetSocketAddress socketAddress = new InetSocketAddress(targetServer, targetPort);

                        socket = new Socket();
                        socket.setSoTimeout(IServiceCheckProcessor.CONNECT_TIMEOUT);
                        socket.setSoLinger(false, 0);
                        socket.setKeepAlive(false);

                        try
                        {
                            socket.connect(socketAddress, IServiceCheckProcessor.CONNECT_TIMEOUT);

                            if (!(socket.isConnected()))
                            {
                                throw new ConnectException("Failed to connect to host " + targetServer + " on port " + request.getPortNumber());
                            }

                            PrintWriter pWriter = new PrintWriter(socket.getOutputStream(), true);
                            pWriter.println(TERMINATE_TELNET + CRLF);
                            pWriter.flush();
                            pWriter.close();

                            response.setRequestStatus(AgentStatus.SUCCESS);
                            response.setResponseData("Telnet connection to " + targetServer + " on port " + request.getPortNumber() + " successful.");
                        }
                        catch (ConnectException cx)
                        {
                            response.setRequestStatus(AgentStatus.FAILURE);
                            response.setResponseData("Telnet connection to " + targetServer + " on port " + request.getPortNumber() + " failed with message: " + cx.getMessage());
                        }
                    }

                    break;
                case PROCESSLIST:
                    sourceFile = scriptConfig.getScripts().get("processList");

                    if (DEBUG)
                    {
                        DEBUGGER.debug("sourceFile: {}", sourceFile);
                    }

                    if (!(sourceFile.canExecute()))
                    {
                        throw new ServiceCheckException("Script file either does not exist or cannot be executed. Cannot continue.");
                    }

                    command = CommandLine.parse(sourceFile.getAbsolutePath());

                    if (request.getPortNumber() != 0)
                    {
                        command.addArgument(String.valueOf(request.getPortNumber()), true);
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("CommandLine: {}", command);
                    }

                    outputStream = new ByteArrayOutputStream();
                    streamHandler = new PumpStreamHandler(outputStream);

                    executor.setWatchdog(watchdog);
                    executor.setStreamHandler(streamHandler);
                    
                    if (DEBUG)
                    {
                        DEBUGGER.debug("ExecuteStreamHandler: {}", streamHandler);
                        DEBUGGER.debug("ExecuteWatchdog: {}", watchdog);
                        DEBUGGER.debug("DefaultExecuteResultHandler: {}", resultHandler);
                        DEBUGGER.debug("DefaultExecutor: {}", executor);
                    }

                    executor.execute(command, resultHandler);

                    resultHandler.waitFor();
                    exitCode = resultHandler.getExitValue();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("exitCode: {}", exitCode);
                    }

                    writer = new BufferedWriter(new FileWriter(LOGS_DIRECTORY + "/" + sourceFile.getName() + ".log"));
                    writer.write(outputStream.toString());
                    writer.flush();

                    response.setResponseData(outputStream.toString());

                    if (executor.isFailure(exitCode))
                    {
                        response.setRequestStatus(AgentStatus.FAILURE);
                    }
                    else
                    {
                        response.setRequestStatus(AgentStatus.SUCCESS);
                    }

                    break;
                default:
                    // unknown operation
                    throw new ServiceCheckException("No valid operation was specified");
            }
        }
        catch (UnknownHostException uhx)
        {
            ERROR_RECORDER.error(uhx.getMessage(), uhx);

            throw new ServiceCheckException(uhx.getMessage(), uhx);
        }
        catch (SocketException sx)
        {
            ERROR_RECORDER.error(sx.getMessage(), sx);

            throw new ServiceCheckException(sx.getMessage(), sx);
        }
        catch (IOException iox)
        {
            ERROR_RECORDER.error(iox.getMessage(), iox);

            throw new ServiceCheckException(iox.getMessage(), iox);
        }
        catch (InterruptedException ix)
        {
            ERROR_RECORDER.error(ix.getMessage(), ix);

            throw new ServiceCheckException(ix.getMessage(), ix);
        }
        finally
        {
            try
            {
                if (writer != null)
                {
                    writer.close();
                }

                if ((socket != null) && (!(socket.isClosed())))
                {
                    socket.close();
                }
            }
            catch (IOException iox)
            {
                ERROR_RECORDER.error(iox.getMessage(), iox);
            }
        }

        return response;
    }
}
