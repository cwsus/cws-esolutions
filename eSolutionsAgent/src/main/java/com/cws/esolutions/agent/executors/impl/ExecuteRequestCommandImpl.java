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
package com.cws.esolutions.agent.executors.impl;
/*
 * Project: eSolutionsAgent
 * Package: com.cws.esolutions.agent.executors.impl
 * File: ExecuteRequestCommandImpl.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import com.cws.esolutions.agent.enums.AgentStatus;
import com.cws.esolutions.agent.executors.dto.ExecuteCommandRequest;
import com.cws.esolutions.agent.executors.dto.ExecuteCommandResponse;
import com.cws.esolutions.agent.executors.exception.ExecuteCommandException;
import com.cws.esolutions.agent.executors.interfaces.IExecuteRequestCommand;
/**
 * @see com.cws.esolutions.agent.executors.interfaces.IExecuteRequestCommand
 */
public class ExecuteRequestCommandImpl implements IExecuteRequestCommand
{
    @Override
    public synchronized ExecuteCommandResponse executeCommand(final ExecuteCommandRequest request) throws ExecuteCommandException
    {
        final String methodName = IExecuteRequestCommand.CNAME + "#executeCommandRequest(final ExecuteCommandRequest request) throws ExecuteCommandException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ExecuteCommandRequest: {}", request);
        }

        Worker worker = null;
        ExecuteCommandResponse response = new ExecuteCommandResponse();

        try
        {
            ProcessBuilder pBuilder = new ProcessBuilder(request.getCommand());
            pBuilder.directory();

            if (DEBUG)
            {
                DEBUGGER.debug("ProcessBuilder: {}", pBuilder);
            }

            worker = new Worker(pBuilder);
            worker.start();
            worker.join(request.getTimeout());

            if (DEBUG)
            {
                DEBUGGER.debug("Worker: {}", worker);
            }

            int exitCode = worker.getExitValue();

            if (DEBUG)
            {
                DEBUGGER.debug("exitCode: {}", exitCode);
            }

            response = new ExecuteCommandResponse();

            if (exitCode != -1)
            {
                response.setRequestStatus(AgentStatus.SUCCESS);

                if (request.printOutput())
                {
                    response.setOutputStream(worker.getOutputStream());
                }

                if (request.printError())
                {
                    response.setErrorStream(worker.getErrorStream());
                }
            }
            else
            {
                response.setRequestStatus(AgentStatus.FAILURE);
                response.setResponse("The command [" + request.getCommand() + "] failed.");
            }

            if (DEBUG)
            {
                DEBUGGER.debug("ExecuteCommandResponse: {}", response);
            }
        }
        catch (InterruptedException ix)
        {
            worker.interrupt();

            ERROR_RECORDER.error(ix.getMessage(), ix);

            throw new ExecuteCommandException(ix.getMessage(), ix);
        }

        return response;
    }
}