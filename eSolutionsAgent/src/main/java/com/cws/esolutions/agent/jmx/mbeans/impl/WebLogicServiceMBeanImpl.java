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
package com.cws.esolutions.agent.jmx.mbeans.impl;

import java.io.IOException;
import weblogic.health.HealthState;
import javax.management.ObjectName;
import org.apache.commons.lang.StringUtils;
import javax.management.remote.JMXConnector;
import javax.management.MBeanServerConnection;
import weblogic.management.runtime.ServerStates;
import weblogic.server.ServerLifecycleException;
import weblogic.deploy.api.spi.DeploymentOptions;
import javax.enterprise.deploy.shared.ModuleType;
import javax.enterprise.deploy.spi.TargetModuleID;
import weblogic.management.runtime.JVMRuntimeMBean;
import javax.management.MalformedObjectNameException;
import weblogic.management.runtime.DomainRuntimeMBean;
import weblogic.management.runtime.ServerRuntimeMBean;
import javax.enterprise.deploy.spi.status.ProgressObject;
import weblogic.deploy.api.spi.WebLogicDeploymentManager;
import weblogic.management.runtime.JDBCServiceRuntimeMBean;
import javax.enterprise.deploy.spi.status.DeploymentStatus;
import weblogic.management.jmx.MBeanServerInvocationHandler;
import weblogic.management.runtime.JDBCDataSourceRuntimeMBean;
import javax.enterprise.deploy.spi.exceptions.TargetException;
import weblogic.management.runtime.ServerLifeCycleRuntimeMBean;
import weblogic.management.mbeanservers.domainruntime.DomainRuntimeServiceMBean;

import com.cws.esolutions.agent.enums.AgentStatus;
import com.cws.esolutions.agent.jmx.dto.JMXConnectorObject;
import com.cws.esolutions.agent.jmx.mbeans.dto.MBeanRequest;
import com.cws.esolutions.agent.jmx.mbeans.dto.MBeanResponse;
import com.cws.esolutions.agent.jmx.interfaces.JMXConnection;
import com.cws.esolutions.agent.jmx.factory.JMXConnectionFactory;
import com.cws.esolutions.agent.jmx.mbeans.interfaces.ServiceMBean;
import com.cws.esolutions.agent.jmx.exception.JMXConnectorException;
import com.cws.esolutions.agent.jmx.mbeans.exception.ServiceMBeanException;
/**
 * eSolutionsAgent
 * com.cws.esolutions.agent.jmx.mbeans.impl
 * WebLogicServiceMBeanImpl.java
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
 * kh05451 @ Oct 02, 2012 12:37:19
 *     Created.
 */
public class WebLogicServiceMBeanImpl implements ServiceMBean
{
    @Override
    public MBeanResponse performServerOperation(final MBeanRequest request) throws ServiceMBeanException
    {
        final String methodName = ServiceMBean.CNAME + "#performServerOperation(final MBeanRequest request) throws ServiceMBeanException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("MBeanRequest: {}", request);
        }

        int x = 0;
        String connectionID = null;
        JMXConnector jmxConnector = null;
        MBeanResponse response = new MBeanResponse();

        try
        {
            JMXConnection jmxConnection = JMXConnectionFactory.createConnector(jmxConfig.getJmxHandler());
            JMXConnectorObject jmxObject = jmxConnection.getJMXConnector(DomainRuntimeServiceMBean.MBEANSERVER_JNDI_NAME);
            jmxConnector = (JMXConnector) jmxObject.getConnector();
            jmxConnector.connect(null);

            connectionID = jmxConnector.getConnectionId();

            if (StringUtils.isNotEmpty(connectionID))
            {
                MBeanServerConnection mbeanConnection = jmxConnector.getMBeanServerConnection();
                    
                if (DEBUG)
                {
                    DEBUGGER.debug("MBeanConnection: {}", mbeanConnection);
                }

                if (mbeanConnection != null)
                {
                    DomainRuntimeServiceMBean domainRuntime = (DomainRuntimeServiceMBean) MBeanServerInvocationHandler.newProxyInstance(mbeanConnection,
                            new ObjectName(DomainRuntimeServiceMBean.OBJECT_NAME));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("DomainRuntimeServiceMBean: {}", domainRuntime);
                    }

                    ServerRuntimeMBean serverRT = domainRuntime.lookupServerRuntime(request.getTargetName());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("ServerRuntimeMBean: {}", serverRT);
                    }

                    if (serverRT != null)
                    {
                        // server is already running, otherwise this wouldnt be here
                        String serverState = serverRT.getState();

                        if (DEBUG)
                        {
                            DEBUGGER.debug("serverState: {}", serverState);
                        }

                        switch (request.getRequestType())
                        {
                            case STOP:
                                serverRT.shutdown();

                                while (x != jmxConfig.getRequestTimeout())
                                {
                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("State: {}", serverRT.getState());
                                    }

                                    if (StringUtils.equals(serverRT.getState(), ServerStates.SHUTDOWN))
                                    {
                                        response.setRequestStatus(AgentStatus.SUCCESS);
                                        response.setResponse("Successfully performed shutdown operation on " + request.getTargetName());

                                        break;
                                    }

                                    x++;
                                }

                                break;
                            case SUSPEND:
                                serverRT.suspend();

                                while (x != jmxConfig.getRequestTimeout())
                                {
                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("State: {}", serverRT.getState());
                                    }

                                    if (StringUtils.equals(serverRT.getState(), ServerStates.SUSPENDING))
                                    {
                                        response.setRequestStatus(AgentStatus.SUCCESS);
                                        response.setResponse("Successfully performed suspend operation on " + request.getTargetName());

                                        break;
                                    }

                                    x++;
                                }

                                break;
                            case RESUME:
                                serverRT.resume();

                                while (x != jmxConfig.getRequestTimeout())
                                {
                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("State: {}", serverRT.getState());
                                    }

                                    if (StringUtils.equals(serverRT.getState(), ServerStates.RUNNING))
                                    {
                                        response.setRequestStatus(AgentStatus.SUCCESS);
                                        response.setResponse("Successfully performed resume operation on " + request.getTargetName());

                                        break;
                                    }

                                    x++;
                                }

                                break;
                            case STATUS:
                                HealthState healthState = serverRT.getHealthState();

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("HealthState: {}", healthState);
                                }

                                StringBuilder sBuilder = new StringBuilder()
                                    .append("The servers Weblogic home is: " + serverRT.getWeblogicHome() + "\n")
                                    .append("The servers Weblogic version is: " + serverRT.getWeblogicVersion() + "\n")
                                    .append("The servers current health is: " + healthState.getState() + "\n")
                                    .append("The servers current status is: " + serverRT.getState() + "\n")
                                    .append("Does this server require a restart? " + serverRT.isRestartRequired() + "\n")
                                    .append("The servers open connection count: " + serverRT.getOpenSocketsCurrentCount() + "\n")
                                    .append("The servers default URL: " + serverRT.getDefaultURL() + "\n")
                                    .append("The servers listen address is: " + serverRT.getURL("http") + "\n")
                                    .append("Is the SSL listen port enabled ? " + serverRT.isSSLListenPortEnabled());

                                if (serverRT.isSSLListenPortEnabled())
                                {
                                    sBuilder.append("The servers listen address is: " + serverRT.getURL("https") + "\n");
                                }

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("sBuilder: {}", sBuilder);
                                }

                                response.setRequestStatus(AgentStatus.SUCCESS);
                                response.setResponse("Successfully executed status request");
                                response.setResponseData(sBuilder);
                                    
                                break;
                            case GC:
                                JVMRuntimeMBean jvmRuntime = serverRT.getJVMRuntime();

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("JVMRuntimeMBean: {}", jvmRuntime);
                                }

                                jvmRuntime.runGC();

                                response.setRequestStatus(AgentStatus.SUCCESS);
                                response.setResponse("Successfully executed GC request");

                                break;
                            default:
                            break;
                        }
                    }
                    else
                    {
                        // server not running
                        // only thing we can do here is start it
                        ServerLifeCycleRuntimeMBean runTime = null;
                        DomainRuntimeMBean domainBean = domainRuntime.getDomainRuntime();

                        if (DEBUG)
                        {
                            DEBUGGER.debug("DomainRuntimeMBean: {}", domainBean);
                        }

                        for (ServerLifeCycleRuntimeMBean run : domainBean.getServerLifeCycleRuntimes())
                        {
                            if (DEBUG)
                            {
                                DEBUGGER.debug("ServerLifeCycleRuntimeMBean: {}", run);
                            }

                            if (StringUtils.equals(run.getName(), request.getTargetName()))
                            {
                                runTime = run;
                            }
                        }

                        if (runTime != null)
                        {
                            runTime.start();

                            while (x != jmxConfig.getRequestTimeout())
                            {
                                if (StringUtils.equals(runTime.getState(), ServerStates.RUNNING))
                                {
                                    response.setRequestStatus(AgentStatus.SUCCESS);
                                    response.setResponse("Server has been successfully started");

                                    break;
                                }

                                x++;
                            }
                        }
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("MBeanResponse: {}", response);
                    }
                }
                else
                {
                    throw new JMXConnectorException("Unable to obtain MBean server connection");
                }
            }
            else
            {
                throw new JMXConnectorException("Unable to obtain JMX connection");
            }
        }
        catch (IOException iox)
        {
            ERROR_RECORDER.error(iox.getMessage(), iox);

            throw new ServiceMBeanException(iox.getMessage(), iox);
        }
        catch (JMXConnectorException jcx)
        {
            ERROR_RECORDER.error(jcx.getMessage(), jcx);

            throw new ServiceMBeanException(jcx.getMessage(), jcx);
        }
        catch (MalformedObjectNameException monx)
        {
            ERROR_RECORDER.error(monx.getMessage(), monx);

            throw new ServiceMBeanException(monx.getMessage(), monx);
        }
        catch (NullPointerException npx)
        {
            ERROR_RECORDER.error(npx.getMessage(), npx);

            throw new ServiceMBeanException(npx.getMessage(), npx);
        }
        catch (ServerLifecycleException slx)
        {
            ERROR_RECORDER.error(slx.getMessage(), slx);

            throw new ServiceMBeanException(slx.getMessage(), slx);
        }
        finally
        {
            if (StringUtils.isNotEmpty(connectionID))
            {
                try
                {
                    jmxConnector.close();
                }
                catch (IOException iox)
                {
                    ERROR_RECORDER.error(iox.getMessage(), iox);
                }
            }
        }

        return response;
    }

    @Override
    public MBeanResponse performDataSourceOperation(final MBeanRequest request) throws ServiceMBeanException
    {
        final String methodName = ServiceMBean.CNAME + "#performDataSourceOperation(final MBeanRequest request) throws ServiceMBeanException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("MBeanRequest: {}", request);
        }

        int x = 0;
        String connectionID = null;
        JMXConnector jmxConnector = null;
        JMXConnection jmxConnection = null;
        MBeanResponse mbeanResponse = null;
        JDBCDataSourceRuntimeMBean jdbcRT = null;
        MBeanServerConnection mbeanConnection = null;

        StringBuilder sBuilder = null;
        ServerRuntimeMBean serverRT = null;
        DomainRuntimeServiceMBean domainRuntime = null;
        
        try
        {
            jmxConnection = JMXConnectionFactory.createConnector(jmxConfig.getJmxHandler());
            JMXConnectorObject jmxObject = jmxConnection.getJMXConnector(DomainRuntimeServiceMBean.MBEANSERVER_JNDI_NAME);
            jmxConnector = (JMXConnector) jmxObject.getConnector();
            jmxConnector.connect(null);

            connectionID = jmxConnector.getConnectionId();

            if (DEBUG)
            {
                DEBUGGER.debug("JMXConnector: {}", jmxConnector);
                DEBUGGER.debug(connectionID);
            }

            if (StringUtils.isNotEmpty(connectionID))
            {
                mbeanConnection = jmxConnector.getMBeanServerConnection();

                if (DEBUG)
                {
                    DEBUGGER.debug("MBeanConnection: {}", mbeanConnection);
                }

                if (mbeanConnection != null)
                {
                    mbeanResponse = new MBeanResponse();

                    domainRuntime = (DomainRuntimeServiceMBean) MBeanServerInvocationHandler.newProxyInstance(mbeanConnection,
                            new ObjectName(DomainRuntimeServiceMBean.OBJECT_NAME));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("DomainRuntimeServiceMBean: {}", domainRuntime);
                    }

                    serverRT = domainRuntime.lookupServerRuntime(request.getTargetName());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("ServerRuntimeMBean: {}", serverRT);
                    }

                    if (serverRT != null)
                    {
                        JDBCServiceRuntimeMBean jdbcSvcMBean = serverRT.getJDBCServiceRuntime();

                        if (DEBUG)
                        {
                            DEBUGGER.debug("JDBCServiceRuntimeMBean: {}", jdbcSvcMBean);
                        }

                        for (JDBCDataSourceRuntimeMBean jdbcORT : jdbcSvcMBean.getJDBCDataSourceRuntimeMBeans())
                        {
                            if (DEBUG)
                            {
                                DEBUGGER.debug("JDBCDataSourceRuntimeMBean: {}", jdbcORT);
                            }

                            if (StringUtils.equals(request.getTargetName(), jdbcORT.getName()))
                            {
                                jdbcRT = jdbcORT;
                            }
                        }

                        if (DEBUG)
                        {
                            DEBUGGER.debug("JDBCDataSourceRuntimeMBean: {}", jdbcRT);
                        }

                        if (jdbcRT != null)
                        {
                            switch (request.getRequestType())
                            {
                                case SUSPEND:
                                    if (request.getForceOperation())
                                    {
                                        jdbcRT.forceSuspend();
                                    }
                                    else
                                    {
                                        jdbcRT.suspend();
                                    }

                                    while (x != jmxConfig.getRequestTimeout())
                                    {
                                        if (StringUtils.equals(jdbcRT.getState(), "Suspended"))
                                        {
                                            mbeanResponse.setRequestStatus(AgentStatus.SUCCESS);
                                            mbeanResponse.setResponse("DataSource successfully suspended");

                                            break;
                                        }

                                        x++;
                                    }

                                    break;
                                case STOP:
                                    if (request.getForceOperation())
                                    {
                                        jdbcRT.forceShutdown();
                                    }
                                    else
                                    {
                                        jdbcRT.shutdown();
                                    }

                                    while (x != jmxConfig.getRequestTimeout())
                                    {
                                        if (StringUtils.equals(jdbcRT.getState(), "Shutdown"))
                                        {
                                            mbeanResponse.setRequestStatus(AgentStatus.SUCCESS);
                                            mbeanResponse.setResponse("DataSource successfully shut down");

                                            break;
                                        }

                                        x++;
                                    }

                                    break;
                                case START:
                                    jdbcRT.start();

                                    while (x != jmxConfig.getRequestTimeout())
                                    {
                                        if (StringUtils.equals(jdbcRT.getState(), "Running"))
                                        {
                                            mbeanResponse.setRequestStatus(AgentStatus.SUCCESS);
                                            mbeanResponse.setResponse("DataSource successfully shut down");

                                            break;
                                        }

                                        x++;
                                    }

                                    break;
                                case RESUME:
                                    jdbcRT.resume();

                                    while (x != jmxConfig.getRequestTimeout())
                                    {
                                        if (StringUtils.equals(jdbcRT.getState(), "Running"))
                                        {
                                            mbeanResponse.setRequestStatus(AgentStatus.SUCCESS);
                                            mbeanResponse.setResponse("DataSource successfully resumed");

                                            break;
                                        }

                                        x++;
                                    }

                                    break;
                                case RESET:
                                    jdbcRT.reset();

                                    while (x != jmxConfig.getRequestTimeout())
                                    {
                                        if (StringUtils.equals(jdbcRT.getState(), "Running"))
                                        {
                                            mbeanResponse.setRequestStatus(AgentStatus.SUCCESS);
                                            mbeanResponse.setResponse("DataSource successfully reset");

                                            break;
                                        }

                                        x++;
                                    }

                                    break;
                                case STATUS:
                                    sBuilder = new StringBuilder()
                                        .append("The database product is " + jdbcRT.getDatabaseProductName() + "\n")
                                        .append("The database version is " + jdbcRT.getDatabaseProductVersion() + "\n")
                                        .append("The JDBC database driver name is " + jdbcRT.getDriverName() + "\n")
                                        .append("The JDBC database driver version is " + jdbcRT.getDriverVersion() + "\n")
                                        .append("This datasource is enabled: " + jdbcRT.isEnabled() + "\n")
                                        .append("This datasource's state is " + jdbcRT.getState() + "\n")
                                        .append("Current active connections :" + jdbcRT.getActiveConnectionsCurrentCount() + "\n");
                                    

                                    mbeanResponse.setRequestStatus(AgentStatus.SUCCESS);
                                    mbeanResponse.setResponse("Successfully executed status request");
                                    mbeanResponse.setResponseData(sBuilder);

                                    break;
                                default:
                                    throw new ServiceMBeanException("No valid operation lifecycle was provided");
                            }
                        }
                    }
                }
                else
                {
                    throw new IOException("Unable to obtain MBean server connection");
                }
            }
            else
            {
                throw new IOException("Unable to create JMX server connection");
            }
        }
        catch (Exception ex)
        {
            ERROR_RECORDER.error(ex.getMessage(), ex);

            throw new ServiceMBeanException(ex.getMessage(), ex);
        }
        finally
        {
            if (StringUtils.isNotEmpty(connectionID))
            {
                try
                {
                    jmxConnector.close();
                }
                catch (IOException iox)
                {
                    ERROR_RECORDER.error(iox.getMessage(), iox);
                }
            }
        }

        return mbeanResponse;
    }

    @Override
    public synchronized MBeanResponse performApplicationOperation(final MBeanRequest request) throws ServiceMBeanException
    {
        final String methodName = ServiceMBean.CNAME + "#performDataSourceOperation(final MBeanRequest request) throws ServiceMBeanException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("MBeanRequest: {}", request);
        }

        DeploymentOptions options = null;
        JMXConnection jmxConnection = null;
        MBeanResponse mbeanResponse = null;
        ProgressObject progressObject = null;
        TargetModuleID[] targetModuleID = null;
        DeploymentStatus deploymentStatus = null;
        WebLogicDeploymentManager deployManager = null;

        try
        {
            jmxConnection = JMXConnectionFactory.createConnector(jmxConfig.getJmxHandler());
            JMXConnectorObject jmxObject = jmxConnection.getDeploymentConnector();
            deployManager = (WebLogicDeploymentManager) jmxObject.getConnector();

            if (DEBUG)
            {
                DEBUGGER.debug("WebLogicDeploymentManager: {}", deployManager);
            }

            if ((deployManager.isConnected()) && (deployManager.isAuthenticated()))
            {
                switch (request.getRequestType())
                {
                    case DEPLOY:
                        options = new DeploymentOptions();
                        options.setAdminMode(true);
                        options.setName(request.getApplication());
                        options.setLibrary(request.isLibrary());

                        targetModuleID = deployManager.getAvailableModules(ModuleType.EAR, deployManager.getTargets());

                        for (TargetModuleID target : targetModuleID)
                        {
                            if (DEBUG)
                            {
                                DEBUGGER.debug("TargetModuleID: {}", target);
                            }

                            if (StringUtils.equals(request.getTargetName(), target.getModuleID()))
                            {
                                // undeploy
                                progressObject = deployManager.deploy(new TargetModuleID[] { target },
                                        request.getBinary(), null, options);
                                deploymentStatus = progressObject.getDeploymentStatus();

                                if (DEBUG)
                                {
                                    while (deploymentStatus.isRunning())
                                    {
                                        DEBUGGER.debug("DeploymentStatus: {}", deploymentStatus);
                                    }
                                }

                                if (!(deploymentStatus.isCompleted()))
                                {
                                    // continue forward
                                    break;
                                }
                            }
                        }

                        break;
                    case REDEPLOY:
                        break;
                    case UNDEPLOY:
                        options = new DeploymentOptions();
                        options.setAdminMode(true);
                        options.setName(request.getApplication());
                        options.setLibrary(request.isLibrary());

                        targetModuleID = deployManager.getAvailableModules(ModuleType.EAR, deployManager.getTargets());

                        for (TargetModuleID target : targetModuleID)
                        {
                            if (DEBUG)
                            {
                                DEBUGGER.debug("TargetModuleID: {}", target);
                            }

                            if (StringUtils.equals(request.getTargetName(), target.getModuleID()))
                            {
                                // undeploy
                                progressObject = deployManager.undeploy(new TargetModuleID[] { target });
                                deploymentStatus = progressObject.getDeploymentStatus();

                                if (DEBUG)
                                {
                                    while (deploymentStatus.isRunning())
                                    {
                                        DEBUGGER.debug("DeploymentStatus: {}", deploymentStatus);
                                    }
                                }

                                if (!(deploymentStatus.isCompleted()))
                                {
                                    // continue forward
                                    break;
                                }
                            }
                        }

                        break;
                    case START:
                        break;
                    case STOP:
                        break;
                    default:
                        break;
                }
            }
        }
        catch (IllegalStateException isx)
        {
            ERROR_RECORDER.error(isx.getMessage(), isx);

            throw new ServiceMBeanException(isx.getMessage(), isx);
        }
        catch (TargetException tx)
        {
            ERROR_RECORDER.error(tx.getMessage(), tx);

            throw new ServiceMBeanException(tx.getMessage(), tx);
        }
        catch (JMXConnectorException jcx)
        {
            ERROR_RECORDER.error(jcx.getMessage(), jcx);

            throw new ServiceMBeanException(jcx.getMessage(), jcx);
        }

        return mbeanResponse;
    }
}
