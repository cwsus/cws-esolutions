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
package com.cws.esolutions.agent.processors.impl;

import java.io.File;
import java.util.List;
import java.net.Socket;
import java.io.IOException;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.net.SocketException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import com.cws.esolutions.agent.enums.AgentStatus;
import com.cws.esolutions.agent.executors.dto.ExecuteCommandRequest;
import com.cws.esolutions.agent.executors.dto.ExecuteCommandResponse;
import com.cws.esolutions.agent.processors.dto.SystemManagerRequest;
import com.cws.esolutions.agent.processors.dto.SystemManagerResponse;
import com.cws.esolutions.agent.processors.interfaces.ISystemManagerProcessor;
import com.cws.esolutions.agent.executors.impl.ExecuteRequestCommandImpl;
import com.cws.esolutions.agent.executors.exception.ExecuteCommandException;
import com.cws.esolutions.agent.executors.interfaces.IExecuteRequestCommand;
import com.cws.esolutions.agent.processors.exception.SystemManagerException;
/**
 * eSolutionsAgent
 * com.cws.esolutions.agent.processors.impl
 * SystemManagerProcessorImpl.java
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
 * kh05451 @ Jan 2, 2013 1:49:02 PM
 *     Created.
 */
public class SystemManagerProcessorImpl implements ISystemManagerProcessor
{
    @Override
    public SystemManagerResponse runSystemCheck(final SystemManagerRequest request) throws SystemManagerException
    {
        final String methodName = ISystemManagerProcessor.CNAME + "#runSystemCheck(final SystemManagerRequest request) throws SystemManagerException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("SystemManagerRequest: {}", request);
        }

        Socket socket = null;
        File sourceFile = null;
        SystemManagerResponse response = new SystemManagerResponse();

        try
        {
            switch (request.getRequestType())
            {
                case NETSTAT:
                    ExecuteCommandRequest cmdRequest = new ExecuteCommandRequest();
                    ExecuteCommandResponse cmdResponse = new ExecuteCommandResponse();
                    IExecuteRequestCommand execCommand = new ExecuteRequestCommandImpl();

                    sourceFile = new File(scriptConfig.getNetstatCmd());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("scriptFile: {}", sourceFile);
                    }

                    if (!(sourceFile.canExecute()))
                    {
                        sourceFile.setExecutable(true);
                    }

                    List<String> commandList = new ArrayList<String>();
                    commandList.add(sourceFile.toString());

                    if (request.getPortNumber() != 0)
                    {
                        commandList.add(String.valueOf(request.getPortNumber()));
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("commandList: {}", commandList);
                    }

                    cmdRequest.setCommand(commandList);
                    cmdRequest.setPrintOutput(true);
                    cmdRequest.setPrintError(true);
                    cmdRequest.setTimeout(SCRIPT_TIMEOUT);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("ExecuteCommandRequest: {}", cmdRequest);
                    }

                    cmdResponse = execCommand.executeCommand(cmdRequest);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("ExecuteCommandResponse: {}", cmdResponse);
                    }

                    if (cmdResponse.getRequestStatus() == AgentStatus.SUCCESS)
                    {
                        if ((cmdResponse.getErrorStream().length() != 0) || (cmdResponse.getOutputStream().length() == 0))
                        {
                            response.setRequestStatus(AgentStatus.FAILURE);
                            response.setResponseData("Command request failed. Error response: " + cmdResponse.getErrorStream().toString());
                        }
                        else
                        {
                            response.setRequestStatus(AgentStatus.SUCCESS);
                            response.setResponseData(cmdResponse.getOutputStream().toString());
                        }
                    }
                    else
                    {
                        response.setRequestStatus(AgentStatus.FAILURE);
                        response.setResponseData("Command request failed. Error response: " + cmdResponse.getResponse());
                    }

                    break;
                case REMOTEDATE:
                    response.setRequestStatus(AgentStatus.SUCCESS);
                    response.setResponseData(System.currentTimeMillis());

                    break;
                case TELNET:
                    response = new SystemManagerResponse();

                    int targetPort = request.getPortNumber();
                    String targetServer = request.getTargetServer();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Target port: {}", targetPort);
                        DEBUGGER.debug("Target server: {}", targetServer);
                    }

                    if (targetPort == 0)
                    {
                        throw new SystemManagerException("Target port number was not assigned. Cannot action request.");
                    }

                    final String CRLF = "\r\n";
                    final String TERMINATE_TELNET = "^]";

                    synchronized (new Object())
                    {
                        InetSocketAddress socketAddress = new InetSocketAddress(targetServer, targetPort);

                        socket = new Socket();
                        socket.setSoTimeout(appBean.getConfigData().getAppConfig().getConnectTimeout());
                        socket.setSoLinger(false, 0);
                        socket.setKeepAlive(false);

                        try
                        {
                            socket.connect(socketAddress, appBean.getConfigData().getAppConfig().getConnectTimeout());

                            if (socket.isConnected())
                            {
                                PrintWriter pWriter = new PrintWriter(socket.getOutputStream(), true);

                                pWriter.println(TERMINATE_TELNET + CRLF);

                                pWriter.flush();
                                pWriter.close();

                                response.setRequestStatus(AgentStatus.SUCCESS);
                                response.setResponseData("Telnet connection to " + targetServer + " on port " + request.getPortNumber() + " successful.");
                            }
                            else
                            {
                                throw new ConnectException("Failed to connect to host " + targetServer + " on port " + request.getPortNumber());
                            }
                        }
                        catch (ConnectException cx)
                        {
                            response.setRequestStatus(AgentStatus.FAILURE);
                            response.setResponseData("Telnet connection to " + targetServer + " on port " + request.getPortNumber() + " failed with message: " + cx.getMessage());
                        }
                    }

                    break;
                default:
                    // unknown operation
                    throw new SystemManagerException("No valid operation was specified");
            }
        }
        catch (ExecuteCommandException ecx)
        {
            ERROR_RECORDER.error(ecx.getMessage(), ecx);

            throw new SystemManagerException(ecx.getMessage(), ecx);
        }
        catch (UnknownHostException uhx)
        {
            ERROR_RECORDER.error(uhx.getMessage(), uhx);

            throw new SystemManagerException(uhx.getMessage(), uhx);
        }
        catch (SocketException sx)
        {
            ERROR_RECORDER.error(sx.getMessage(), sx);

            throw new SystemManagerException(sx.getMessage(), sx);
        }
        catch (IOException iox)
        {
            ERROR_RECORDER.error(iox.getMessage(), iox);

            throw new SystemManagerException(iox.getMessage(), iox);
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
                ERROR_RECORDER.error(iox.getMessage(), iox);
            }
        }

        return response;
    }

    @Override
    public SystemManagerResponse installSoftwarePackage(final SystemManagerRequest request) throws SystemManagerException
    {
        final String methodName = ISystemManagerProcessor.CNAME + "#installSoftwarePackage(final SystemManagerRequest request) throws SystemManagerException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("SystemManagerRequest: {}", request);
        }

        throw new SystemManagerException("Not implemented yet");
    }
}
