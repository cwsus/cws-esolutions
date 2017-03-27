/*
 * Copyright (final c) 2009 - 2012 By: CWS, Inc.
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
 * Package: com.cws.esolutions.agent.server.processors.impl
 * File: AgentRequestProcessorImpl.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import com.cws.esolutions.agent.dto.AgentRequest;
import com.cws.esolutions.agent.dto.AgentResponse;
import com.cws.esolutions.agent.enums.AgentStatus;
import com.cws.esolutions.agent.exception.AgentException;
import com.cws.esolutions.agent.processors.dto.FileManagerRequest;
import com.cws.esolutions.agent.processors.dto.FileManagerResponse;
import com.cws.esolutions.agent.processors.dto.ServiceCheckRequest;
import com.cws.esolutions.agent.processors.dto.ServiceCheckResponse;
import com.cws.esolutions.agent.processors.impl.FileManagerProcessorImpl;
import com.cws.esolutions.agent.processors.impl.ServiceCheckProcessorImpl;
import com.cws.esolutions.agent.processors.exception.FileManagerException;
import com.cws.esolutions.agent.processors.exception.ServiceCheckException;
import com.cws.esolutions.agent.processors.exception.SystemManagerException;
import com.cws.esolutions.agent.processors.interfaces.IFileManagerProcessor;
import com.cws.esolutions.agent.processors.interfaces.IAgentRequestProcessor;
import com.cws.esolutions.agent.processors.interfaces.IServiceCheckProcessor;
import com.cws.esolutions.agent.processors.exception.ApplicationManagerException;
/**
 * @see com.cws.esolutions.agent.processors.interfaces.IAgentRequestProcessor
 */
public class AgentRequestProcessorImpl implements IAgentRequestProcessor
{
    private static final IFileManagerProcessor fileManager = new FileManagerProcessorImpl();
    private static final IServiceCheckProcessor svcCheck = new ServiceCheckProcessorImpl();

    public final AgentResponse processRequest(final AgentRequest request) throws AgentException
    {
        final String methodName = IAgentRequestProcessor.CNAME + "#processRequest(final AgentRequest request) throws AgentException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AgentRequest: {}", request);
        }

        AgentResponse response = new AgentResponse();

        try
        {
            Object payload = request.getRequestPayload();

            if (DEBUG)
            {
                DEBUGGER.debug("Payload: {}", payload);
            }

            if (payload instanceof ServiceCheckRequest)
            {
                ServiceCheckRequest req = (ServiceCheckRequest) payload;

                if (DEBUG)
                {
                    DEBUGGER.debug("ServiceCheckRequest: {}", req);
                }

                ServiceCheckResponse res = svcCheck.runSystemCheck(req);

                if (DEBUG)
                {
                    DEBUGGER.debug("ServiceCheckResponse: {}", res);
                }

                response.setRequestStatus(res.getRequestStatus());
                response.setResponsePayload(res);

                return response;
            }
            else if (payload instanceof FileManagerRequest)
            {
                FileManagerResponse res = null;
                FileManagerRequest req = (FileManagerRequest) payload;

                if (DEBUG)
                {
                    DEBUGGER.debug("FileManagerRequest: {}", req);
                }

                res = fileManager.retrieveFile(req);

                if (DEBUG)
                {
                    DEBUGGER.debug("FileManagerResponse: {}", res);
                }

                response.setRequestStatus(res.getRequestStatus());
                response.setResponsePayload(res);

                return response;
            }
            else
            {
                throw new AgentException("Payload provided is not currently supported. Cannot continue");
            }
        }
        catch (ApplicationManagerException amx)
        {
            ERROR_RECORDER.error(amx.getMessage(), amx);

            response.setRequestStatus(AgentStatus.FAILURE);
            response.setResponsePayload(amx);

            return response;
        }
        catch (SystemManagerException smx)
        {
            ERROR_RECORDER.error(smx.getMessage(), smx);

            response.setRequestStatus(AgentStatus.FAILURE);
            response.setResponsePayload(smx);

            return response;
        }
        catch (FileManagerException fmx)
        {
            ERROR_RECORDER.error(fmx.getMessage(), fmx);

            response.setRequestStatus(AgentStatus.FAILURE);
            response.setResponsePayload(fmx);

            return response;
        }
        catch (ServiceCheckException scx)
        {
            ERROR_RECORDER.error(scx.getMessage(), scx);

            response.setRequestStatus(AgentStatus.FAILURE);
            response.setResponsePayload(scx);

            return response;
        }
    }
}
