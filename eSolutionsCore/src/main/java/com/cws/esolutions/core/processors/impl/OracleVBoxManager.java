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
package com.cws.esolutions.core.processors.impl;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.processors.impl
 * File: OracleVBoxManager.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.Map;
import java.util.HashMap;
import org.virtualbox_4_2.IConsole;
import org.virtualbox_4_2.IMachine;
import org.virtualbox_4_2.ISession;
import org.virtualbox_4_2.IProgress;
import org.virtualbox_4_2.IVirtualBox;
import org.virtualbox_4_2.MachineState;
import org.apache.commons.lang.StringUtils;
import org.virtualbox_4_2.VirtualBoxManager;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.core.processors.dto.Server;
import com.cws.esolutions.security.utils.PasswordUtils;
import com.cws.esolutions.core.processors.enums.ServerType;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.core.processors.dto.VirtualServiceRequest;
import com.cws.esolutions.security.processors.dto.AuthenticationData;
import com.cws.esolutions.core.processors.dto.VirtualServiceResponse;
import com.cws.esolutions.core.processors.interfaces.VirtualServiceManager;
import com.cws.esolutions.core.processors.exception.VirtualServiceException;
/**
 * @see com.cws.esolutions.core.processors.interfaces.VirtualServiceManager
 */
public class OracleVBoxManager implements VirtualServiceManager
{
    private static final String CNAME = OracleVBoxManager.class.getName();

    /**
     * @see com.cws.esolutions.core.processors.interfaces.VirtualServiceManager#listVirtualMachines(com.cws.esolutions.core.processors.dto.VirtualServiceRequest)
     */
    @Override
    public synchronized VirtualServiceResponse listVirtualMachines(final VirtualServiceRequest request) throws VirtualServiceException
    {
        final String methodName = OracleVBoxManager.CNAME + "#listVirtualMachines(final VirtualServiceRequest request) throws VirtualServiceException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("VirtualServiceRequest: {}", request);
        }

        VirtualServiceResponse response = new VirtualServiceResponse();

        final Server server = request.getServer();
        final UserAccount userAccount = request.getUserAccount();
        final AuthenticationData userSecurity = request.getUserSecurity();
        final VirtualBoxManager vboxMgr = VirtualBoxManager.createInstance(null);

        if (DEBUG)
        {
            DEBUGGER.debug("Server: {}", server);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("AuthenticationData: {}", userSecurity);
            DEBUGGER.debug("VirtualBoxManager: {}", vboxMgr);
        }

        if ((userAccount == null) || (userSecurity == null))
        {
            throw new VirtualServiceException("No authentication information was provided. Cannot continue.");
        }

        if (server.getServerType() != ServerType.VIRTUALHOST)
        {
            throw new VirtualServiceException("Provided server is not a virtual server host system");
        }
        else if (StringUtils.isEmpty(server.getMgrUrl()))
        {
            throw new VirtualServiceException("No virtual manager URL or port number was found. Cannot continue.");
        }
        else
        {
            vboxMgr.connect(server.getMgrUrl(), userAccount.getUsername(),
                    PasswordUtils.decryptText(userSecurity.getPassword(), userSecurity.getUserSalt().length()));

            if (DEBUG)
            {
                DEBUGGER.debug("VirtualBoxManager: {}", vboxMgr);
            }

            IVirtualBox virtualBox = vboxMgr.getVBox();

            if (DEBUG)
            {
                DEBUGGER.debug("IVirtualBox: {}", virtualBox);
            }

            Map<String, String> machines = new HashMap<>();

            for (IMachine machine: virtualBox.getMachines())
            {
                if (DEBUG)
                {
                    DEBUGGER.debug("Machine: {}", machine);
                }

                machines.put(machine.getId(), machine.getName());
            }

            if (DEBUG)
            {
                DEBUGGER.debug("machines: {}", machines);
            }

            vboxMgr.disconnect();

            response.setRequestStatus(CoreServicesStatus.SUCCESS);
            response.setMachines(machines);

            if (DEBUG)
            {
                DEBUGGER.debug("VirtualServiceResponse: {}", response);
            }
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.core.processors.interfaces.VirtualServiceManager#startVirtualMachine(com.cws.esolutions.core.processors.dto.VirtualServiceRequest)
     */
    @Override
    public synchronized VirtualServiceResponse startVirtualMachine(final VirtualServiceRequest request) throws VirtualServiceException
    {
        final String methodName = OracleVBoxManager.CNAME + "#startVirtualMachine(final VirtualServiceRequest request) throws VirtualServiceException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("VirtualServiceRequest: {}", request);
        }

        IMachine machine = null;
        ISession session = null;
        IVirtualBox virtualBox = null;
        VirtualServiceResponse response = new VirtualServiceResponse();

        final Server server = request.getServer();
        final UserAccount userAccount = request.getUserAccount();
        final AuthenticationData userSecurity = request.getUserSecurity();
        final VirtualBoxManager vboxMgr = VirtualBoxManager.createInstance(null);

        if (DEBUG)
        {
            DEBUGGER.debug("Server: {}", server);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("AuthenticationData: {}", userSecurity);
            DEBUGGER.debug("VirtualBoxManager: {}", vboxMgr);
        }

        if ((userAccount == null) || (userSecurity == null))
        {
            throw new VirtualServiceException("Virtual service security information not provided. Cannot continue.");
        }

        try
        {
            if (server.getServerType() != ServerType.VIRTUALHOST)
            {
                throw new VirtualServiceException("Provided server is not a virtual server host system");
            }
            else if (StringUtils.isEmpty(server.getMgrUrl()))
            {
                throw new VirtualServiceException("No virtual manager URL or port number was found. Cannot continue.");
            }
            else
            {
                vboxMgr.connect(server.getMgrUrl(), userAccount.getUsername(),
                        PasswordUtils.decryptText(userSecurity.getPassword(), userSecurity.getUserSalt().length()));

                if (DEBUG)
                {
                    DEBUGGER.debug("VirtualBoxManager: {}", vboxMgr);
                }

                virtualBox = vboxMgr.getVBox();
                session = vboxMgr.getSessionObject();

                if (DEBUG)
                {
                    DEBUGGER.debug("IVirtualBox: {}", virtualBox);
                }

                machine = virtualBox.findMachine(server.getVirtualId());

                if (DEBUG)
                {
                    DEBUGGER.debug("IMachine: {}", machine);
                }

                if (machine != null)
                {
                    if (machine.getState() == MachineState.PoweredOff)
                    {
                        IProgress progress = machine.launchVMProcess(session, "headless", null);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("IProgress: {}", progress);
                        }

                        int x = 0;

                        while (x != appConfig.getConnectTimeout())
                        {
                            if (progress.getCompleted())
                            {
                                break;
                            }

                            this.wait(1);
                            x++;
                        }
                    }
                    else
                    {
                        throw new VirtualServiceException("Requested machine is not currently powered down. Cannot perform start request.");
                    }
                }
                else
                {
                    throw new VirtualServiceException("No machine was found with identifier " + server.getVirtualId());
                }
            }
        }
        catch (InterruptedException ix)
        {
            ERROR_RECORDER.error(ix.getMessage(), ix);

            throw new VirtualServiceException(ix.getMessage(), ix);
        }
        finally
        {
            if (machine != null)
            {
                machine.releaseRemote();
            }

            if (vboxMgr != null)
            {
                vboxMgr.closeMachineSession(session);
            }

            if (session != null)
            {
                session.releaseRemote();
            }

            if (virtualBox != null)
            {
                virtualBox.releaseRemote();
            }

            if (vboxMgr != null)
            {
                vboxMgr.disconnect();
            }
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.core.processors.interfaces.VirtualServiceManager#stopVirtualMachine(com.cws.esolutions.core.processors.dto.VirtualServiceRequest)
     */
    @Override
    public synchronized VirtualServiceResponse stopVirtualMachine(final VirtualServiceRequest request) throws VirtualServiceException
    {
        final String methodName = OracleVBoxManager.CNAME + "#stopVirtualMachine(final VirtualServiceRequest request) throws VirtualServiceException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("VirtualServiceRequest: {}", request);
        }

        IMachine machine = null;
        ISession session = null;
        boolean isComplete = false;
        IVirtualBox virtualBox = null;
        VirtualServiceResponse response = new VirtualServiceResponse();

        final Server server = request.getServer();
        final UserAccount userAccount = request.getUserAccount();
        final AuthenticationData userSecurity = request.getUserSecurity();
        final VirtualBoxManager vboxMgr = VirtualBoxManager.createInstance(null);

        if (DEBUG)
        {
            DEBUGGER.debug("Server: {}", server);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("AuthenticationData: {}", userSecurity);
            DEBUGGER.debug("VirtualBoxManager: {}", vboxMgr);
        }

        if ((userAccount == null) || (userSecurity == null))
        {
            throw new VirtualServiceException("Virtual service security information not provided. Cannot continue.");
        }

        try
        {
            if (server.getServerType() != ServerType.VIRTUALHOST)
            {
                throw new VirtualServiceException("Provided server is not a virtual server host system");
            }
            else if (StringUtils.isEmpty(server.getMgrUrl()))
            {
                throw new VirtualServiceException("No virtual manager URL or port number was found. Cannot continue.");
            }
            else
            {
                vboxMgr.connect(server.getMgrUrl(), userAccount.getUsername(),
                        PasswordUtils.decryptText(userSecurity.getPassword(), userSecurity.getUserSalt().length()));

                if (DEBUG)
                {
                    DEBUGGER.debug("VirtualBoxManager: {}", vboxMgr);
                }

                virtualBox = vboxMgr.getVBox();

                if (DEBUG)
                {
                    DEBUGGER.debug("IVirtualBox: {}", virtualBox);
                }

                machine = virtualBox.findMachine(server.getVirtualId());

                if (DEBUG)
                {
                    DEBUGGER.debug("IMachine: {}", machine);
                }

                if (machine != null)
                {
                    session = vboxMgr.openMachineSession(machine);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("ISession: {}", session);
                    }

                    IConsole console = session.getConsole();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("IConsole: {}", console);
                    }

                    if (console.getState() == MachineState.Running)
                    {
                        // shut it down
                        IProgress progress = console.powerDown();

                        if (DEBUG)
                        {
                            DEBUGGER.debug("IProgress: {}", progress);
                        }

                        int x = 0;

                        while (x != appConfig.getConnectTimeout())
                        {
                            if (progress.getCompleted())
                            {
                                isComplete = true;

                                break;
                            }

                            this.wait(1); // sleep for 1 second
                            x++;
                        }

                        if (isComplete)
                        {
                            response.setRequestStatus(CoreServicesStatus.SUCCESS);
                        }
                        else
                        {
                            response.setRequestStatus(CoreServicesStatus.FAILURE);
                        }
                    }
                    else
                    {
                        response.setRequestStatus(CoreServicesStatus.FAILURE);
                    }
                }
                else
                {
                    response.setRequestStatus(CoreServicesStatus.FAILURE);
                }
            }
        }
        catch (InterruptedException ix)
        {
            ERROR_RECORDER.error(ix.getMessage(), ix);

            throw new VirtualServiceException(ix.getMessage(), ix);
        }
        catch (Exception ex)
        {
            ERROR_RECORDER.error(ex.getMessage(), ex);

            throw new VirtualServiceException(ex.getMessage(), ex);
        }
        finally
        {
            if (machine != null)
            {
                machine.releaseRemote();
            }

            if (vboxMgr != null)
            {
                vboxMgr.closeMachineSession(session);
            }

            if (session != null)
            {
                session.releaseRemote();
            }

            if (virtualBox != null)
            {
                virtualBox.releaseRemote();
            }

            if (vboxMgr != null)
            {
                vboxMgr.disconnect();
            }
        }

        return response;
    }
}
