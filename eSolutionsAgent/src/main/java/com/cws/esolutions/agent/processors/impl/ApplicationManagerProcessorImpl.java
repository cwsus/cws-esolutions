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
import java.util.Arrays;
import java.util.ArrayList;

import com.cws.esolutions.agent.enums.AgentStatus;
import com.cws.esolutions.agent.jmx.mbeans.dto.MBeanRequest;
import com.cws.esolutions.agent.jmx.mbeans.dto.MBeanResponse;
import com.cws.esolutions.agent.jmx.mbeans.enums.MBeanRequestType;
import com.cws.esolutions.agent.jmx.mbeans.interfaces.ServiceMBean;
import com.cws.esolutions.agent.executors.dto.ExecuteCommandRequest;
import com.cws.esolutions.agent.processors.enums.StateManagementType;
import com.cws.esolutions.agent.executors.dto.ExecuteCommandResponse;
import com.cws.esolutions.agent.jmx.mbeans.factory.ServiceMBeanFactory;
import com.cws.esolutions.agent.executors.impl.ExecuteRequestCommandImpl;
import com.cws.esolutions.agent.processors.dto.ApplicationManagerRequest;
import com.cws.esolutions.agent.processors.dto.ApplicationManagerResponse;
import com.cws.esolutions.agent.jmx.mbeans.exception.ServiceMBeanException;
import com.cws.esolutions.agent.executors.exception.ExecuteCommandException;
import com.cws.esolutions.agent.executors.interfaces.IExecuteRequestCommand;
import com.cws.esolutions.agent.processors.exception.ApplicationManagerException;
import com.cws.esolutions.agent.processors.interfaces.IApplicationManagerProcessor;
/**
 * eSolutionsAgent
 * com.cws.esolutions.agent.processors.impl
 * ApplicationManagerProcessorImpl.java
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
 * kh05451 @ Oct 29, 2012 9:44:46 AM
 *     Created.
 */
public class ApplicationManagerProcessorImpl implements IApplicationManagerProcessor
{
    @Override
    public ApplicationManagerResponse manageServerState(final ApplicationManagerRequest request) throws ApplicationManagerException
    {
        final String methodName = ApplicationManagerProcessorImpl.CNAME + "#manageServerState(final ApplicationManagerRequest request) throws ApplicationManagerException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ApplicationManagerRequest: {}", request);
        }

        ApplicationManagerResponse response = new ApplicationManagerResponse();

        final String server = request.getTargetServer();
        final ServiceMBean serviceBean = ServiceMBeanFactory.createServiceMBean(jmxConfig.getJmxHandler());

        if (DEBUG)
        {
            DEBUGGER.debug("Server: {}", server);
            DEBUGGER.debug("ServiceMBean: {}", serviceBean);
        }

        try
        {
            if (request.getStateMgmtType() == StateManagementType.KILL)
            {
                // TODO: run kill here
                // this is unix-y and should probably be cross-platform...
                // windows would be taskkill /pid /f <pid>
                ExecuteCommandRequest cmdRequest = new ExecuteCommandRequest();
                cmdRequest.setCommand(new ArrayList<String>(Arrays.asList("kill", "-9", request.getProcessId())));
                cmdRequest.setPrintOutput(true);
                cmdRequest.setPrintError(true);
                cmdRequest.setTimeout(5000);
                cmdRequest.setPrintError(false);

                if (DEBUG)
                {
                    DEBUGGER.debug("ExecuteCommandRequest: {}", cmdRequest);
                }

                IExecuteRequestCommand execCommand = new ExecuteRequestCommandImpl();
                ExecuteCommandResponse cmdResponse = execCommand.executeCommand(cmdRequest);

                if (DEBUG)
                {
                    DEBUGGER.debug("ExecuteCommandResponse: {}", cmdResponse);
                }

                if (cmdResponse.getRequestStatus() == AgentStatus.SUCCESS)
                {
                    response.setRequestStatus(AgentStatus.SUCCESS);
                    response.setResponse("Successfully killed process " + request.getJvmName());
                }
                else
                {
                    // service operation failed
                    response.setRequestStatus(AgentStatus.FAILURE);
                    response.setResponse("Failed to perform service operation on target " + request.getJvmName());
                }
            }
            else
            {
                MBeanRequest mbeanRequest = new MBeanRequest();
                mbeanRequest.setTargetName(request.getJvmName());
                mbeanRequest.setForceOperation(request.forceOperation());
                mbeanRequest.setRequestTimeout(request.getTimeoutValue());
                mbeanRequest.setRequestType(MBeanRequestType.valueOf(request.getStateMgmtType().name()));

                if (DEBUG)
                {
                    DEBUGGER.debug("MBeanRequest: {}", mbeanRequest);
                }

                MBeanResponse mbeanResponse = serviceBean.performServerOperation(mbeanRequest);

                if (DEBUG)
                {
                    DEBUGGER.debug("MBeanResponse: {}", mbeanResponse);
                }

                if (mbeanResponse.getRequestStatus() == AgentStatus.SUCCESS)
                {
                    response.setRequestStatus(AgentStatus.SUCCESS);
                    response.setResponse("Successfully performed service operation on target " + request.getJvmName());
                }
                else
                {
                    // service operation failed
                    response.setRequestStatus(AgentStatus.FAILURE);
                    response.setResponse("Failed to perform service operation on target " + request.getJvmName());
                }
            }
        }
        catch (ServiceMBeanException smbx)
        {
            ERROR_RECORDER.error(smbx.getMessage(), smbx);

            throw new ApplicationManagerException(smbx.getMessage(), smbx);
        }
        catch (ExecuteCommandException ecx)
        {
            ERROR_RECORDER.error(ecx.getMessage(), ecx);
            
            throw new ApplicationManagerException(ecx.getMessage(), ecx);
        }

        return response;
    }

    @Override
    public ApplicationManagerResponse manageApplicationDeployment(final ApplicationManagerRequest request) throws ApplicationManagerException
    {
        final String methodName = ApplicationManagerProcessorImpl.CNAME + "#manageApplicationDeployment(final ApplicationManagerRequest request) throws ApplicationManagerException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ApplicationManagerRequest: {}", request);
        }

        ApplicationManagerResponse response = new ApplicationManagerResponse();

        final String server = request.getTargetServer();
        final ServiceMBean serviceBean = ServiceMBeanFactory.createServiceMBean(jmxConfig.getJmxHandler());

        if (DEBUG)
        {
            DEBUGGER.debug("Server: {}", server);
            DEBUGGER.debug("ServiceMBean: {}", serviceBean);
        }

        try
        {
            File deploymentFile = new File(request.getDeploymentFile());

            if (DEBUG)
            {
                DEBUGGER.debug("deploymentFile: {}", deploymentFile);
            }

            if (deploymentFile.canRead())
            {
                // the request goes to the dmgr (we should be running on the dmgr)
                MBeanRequest mbeanRequest = new MBeanRequest();
                mbeanRequest.setTargetName(request.getJvmName());
                mbeanRequest.setApplication(request.getApplName());
                mbeanRequest.setForceOperation(request.forceOperation());
                mbeanRequest.setRequestTimeout(request.getTimeoutValue());
                mbeanRequest.setRequestType(MBeanRequestType.valueOf(request.getDeploymentType().name()));

                if (DEBUG)
                {
                    DEBUGGER.debug("MBeanRequest: {}", mbeanRequest);
                }

                MBeanResponse mbeanResponse = serviceBean.performApplicationOperation(mbeanRequest);

                if (DEBUG)
                {
                    DEBUGGER.debug("MBeanResponse: {}", mbeanResponse);
                }

                if (mbeanResponse.getRequestStatus() == AgentStatus.SUCCESS)
                {
                    response.setRequestStatus(AgentStatus.SUCCESS);
                    response.setResponse("Successfully performed service operation on target " + request.getJvmName());
                }
                else
                {
                    // service operation failed
                    response.setRequestStatus(AgentStatus.FAILURE);
                    response.setResponse("Failed to perform service operation on target " + request.getJvmName());
                }
            }
            else
            {
                throw new ApplicationManagerException("Cannot read deployment file for processing");
            }
        }
        catch (ServiceMBeanException smbx)
        {
            ERROR_RECORDER.error(smbx.getMessage(), smbx);

            throw new ApplicationManagerException(smbx.getMessage(), smbx);
        }

        return response;
    }

    @Override
    public ApplicationManagerResponse manageApplicationState(final ApplicationManagerRequest request) throws ApplicationManagerException
    {
        final String methodName = ApplicationManagerProcessorImpl.CNAME + "#manageApplicationState(final ApplicationManagerRequest request) throws ApplicationManagerException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ApplicationManagerRequest: {}", request);
        }

        ApplicationManagerResponse response = new ApplicationManagerResponse();

        final String server = request.getTargetServer();
        final ServiceMBean serviceBean = ServiceMBeanFactory.createServiceMBean(jmxConfig.getJmxHandler());

        if (DEBUG)
        {
            DEBUGGER.debug("Server: {}", server);
            DEBUGGER.debug("ServiceMBean: {}", serviceBean);
        }

        try
        {
            // check to make sure the nodemanager is running
            // we dont have a nice way to do this except a telnet req
            ExecuteCommandRequest cmdRequest = new ExecuteCommandRequest();
            ExecuteCommandResponse cmdResponse = new ExecuteCommandResponse();
            IExecuteRequestCommand execCommand = new ExecuteRequestCommandImpl();

            cmdRequest.setCommand(new ArrayList<String>(
                    Arrays.asList(
                            "netstat",
                            "-an")));
            cmdRequest.setPrintOutput(true);
            cmdRequest.setPrintError(true);
            cmdRequest.setTimeout(5000);
            cmdRequest.setPrintError(false);

            if (DEBUG)
            {
                DEBUGGER.debug("ExecuteCommandRequest: {}", cmdRequest);
            }

            cmdResponse = execCommand.executeCommand(cmdRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("ExecuteCommandResponse: {}", cmdResponse);
            }

            if (request.getStateMgmtType() == StateManagementType.KILL)
            {
                // TODO: run kill here
            }
            else
            {
                if ((cmdResponse.getRequestStatus() == AgentStatus.SUCCESS) && (cmdResponse.getOutputStream().toString().contains(String.valueOf(jmxConfig.getNmPort()))))
                {
                    MBeanRequest mbeanRequest = new MBeanRequest();
                    mbeanRequest.setTargetName(request.getJvmName());
                    mbeanRequest.setForceOperation(request.forceOperation());
                    mbeanRequest.setRequestTimeout(request.getTimeoutValue());
                    mbeanRequest.setRequestType(MBeanRequestType.valueOf(request.getStateMgmtType().name()));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("MBeanRequest: {}", mbeanRequest);
                    }

                    MBeanResponse mbeanResponse = serviceBean.performApplicationOperation(mbeanRequest);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("MBeanResponse: {}", mbeanResponse);
                    }

                    if (mbeanResponse.getRequestStatus() == AgentStatus.SUCCESS)
                    {
                        response.setRequestStatus(AgentStatus.SUCCESS);
                        response.setResponse("Successfully performed service operation on target " + request.getJvmName());
                    }
                    else
                    {
                        // service operation failed
                        response.setRequestStatus(AgentStatus.FAILURE);
                        response.setResponse("Failed to perform service operation on target " + request.getJvmName());
                    }
                }
                else
                {
                    // nm not running
                    response.setRequestStatus(AgentStatus.FAILURE);
                    response.setResponse("NodeManager is not currently running on host. Cannot perform operation.");
                }
            }
        }
        catch (ServiceMBeanException smbx)
        {
            ERROR_RECORDER.error(smbx.getMessage(), smbx);

            throw new ApplicationManagerException(smbx.getMessage(), smbx);
        }
        catch (ExecuteCommandException ecx)
        {
            ERROR_RECORDER.error(ecx.getMessage(), ecx);
            
            throw new ApplicationManagerException(ecx.getMessage(), ecx);
        }

        return response;
    }
}
