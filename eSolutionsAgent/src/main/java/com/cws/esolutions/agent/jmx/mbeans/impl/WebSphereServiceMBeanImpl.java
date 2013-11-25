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

import java.util.Set;
import java.util.Locale;
import java.util.Hashtable;
import javax.management.ObjectName;
import javax.management.Notification;
import javax.management.MBeanException;
import org.apache.commons.lang.StringUtils;
import javax.management.ReflectionException;
import com.ibm.websphere.management.Session;
import javax.management.NotificationListener;
import com.ibm.websphere.management.AdminClient;
import javax.management.InstanceNotFoundException;
import javax.management.AttributeNotFoundException;
import javax.management.MalformedObjectNameException;
import com.ibm.websphere.management.application.AppConstants;
import com.ibm.websphere.management.application.AppManagement;
import com.ibm.websphere.management.exception.ConnectorException;
import com.ibm.websphere.management.application.AppManagementProxy;
import com.ibm.websphere.management.configservice.ConfigServiceProxy;
import com.ibm.websphere.management.application.client.AppDeploymentTask;
import com.ibm.websphere.management.application.client.AppDeploymentController;

import com.cws.esolutions.agent.enums.AgentStatus;
import com.cws.esolutions.agent.jmx.dto.JMXConnectorObject;
import com.cws.esolutions.agent.jmx.mbeans.dto.MBeanRequest;
import com.cws.esolutions.agent.jmx.interfaces.JMXConnection;
import com.cws.esolutions.agent.jmx.mbeans.dto.MBeanResponse;
import com.cws.esolutions.agent.jmx.factory.JMXConnectionFactory;
import com.cws.esolutions.agent.jmx.mbeans.interfaces.ServiceMBean;
import com.cws.esolutions.agent.jmx.exception.JMXConnectorException;
import com.cws.esolutions.agent.jmx.mbeans.exception.ServiceMBeanException;
/**
 * eSolutionsAgent
 * com.cws.esolutions.agent.jmx.mbeans.impl
 * WebSphereServiceMBeanImpl.java
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
 * 35033355 @ May 15, 2013 8:50:06 AM
 *     Created.
 */
public class WebSphereServiceMBeanImpl implements ServiceMBean, NotificationListener
{
    private static final String CNAME = WebSphereServiceMBeanImpl.class.getName();

    @Override
    public MBeanResponse performServerOperation(final MBeanRequest request) throws ServiceMBeanException
    {
        final String methodName = WebSphereServiceMBeanImpl.CNAME + "#performServerOperation(final MBeanRequest request) throws ServiceMBeanException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("MBeanRequest: ", request);
        }

        MBeanResponse response = new MBeanResponse();

        try
        {
            JMXConnection jmxConnection = JMXConnectionFactory.createConnector(jmxConfig.getJmxHandler());
            JMXConnectorObject jmxObject = jmxConnection.getJMXConnector(null);

            if (DEBUG)
            {
                DEBUGGER.debug("JMXConnectorObject: {}", jmxObject);
            }

            AdminClient adminClient = (AdminClient) jmxObject.getConnector();

            if (DEBUG)
            {
                DEBUGGER.debug("AdminClient: {}", adminClient);
            }

            if (adminClient != null)
            {
                ObjectName nodeAgent = new ObjectName("WebSphere:type=NodeAgent,node=appnode,*");

                if (DEBUG)
                {
                    DEBUGGER.debug("ObjectName: {}", nodeAgent);
                }

                @SuppressWarnings("unchecked")
                Set<Object> objectSet = adminClient.queryNames(nodeAgent, null);

                if (DEBUG)
                {
                    DEBUGGER.debug("objectSet: {}", objectSet);
                }

                if (!(objectSet.isEmpty()))
                {
                    ObjectName nodeObject = (ObjectName) objectSet.iterator().next();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("nodeObject: {}", nodeObject);
                    }

                    if (nodeObject != null)
                    {
                        boolean isComplete = false;
                        ObjectName serverName = new ObjectName("WebSphere:name=" + request.getTargetName() + ",process=" + request.getTargetName() + ",node=appnode,type=Server,*");

                        if (DEBUG)
                        {
                            DEBUGGER.debug("serverObject: {}", serverName);
                        }

                        @SuppressWarnings("unchecked")
                        Set<Object> serverSet = adminClient.queryNames(serverName, null);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("serverSet: {}", serverSet);
                        }

                        switch (request.getRequestType())
                        {
                            case STOP:
                                // PK56519 - apparently the NA can only terminate, not gracefully stop, when performed from a jmx operation
                                // so we do it from the server runtime and check the status with the NA runtime
                                if (!(serverSet.isEmpty()))
                                {
                                    ObjectName serverObject = (ObjectName) serverSet.iterator().next();

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("serverObject: {}", serverObject);
                                    }

                                    if (serverObject != null)
                                    {
                                        adminClient.addNotificationListener(serverObject, this, null, null);

                                        int x = 0;

                                        // these are all void returns, so we're just going to invoke it
                                        // and then do a status from the nodeagent
                                        adminClient.invoke(serverObject, "stop", null, null);

                                        while (x != jmxConfig.getRequestTimeout())
                                        {
                                            String status = (String) adminClient.invoke(nodeObject, "getProcessStatus", new String[] { request.getTargetName() }, new String[] { "java.lang.String" });

                                            if (DEBUG)
                                            {
                                                DEBUGGER.debug("status: {}", status);
                                            }

                                            if (StringUtils.equals(status, "STOPPED"))
                                            {
                                                isComplete = true;

                                                break;
                                            }

                                            x++;
                                            Thread.sleep(1); // sleep for 1 second. request timeout is in seconds
                                        }

                                        if (isComplete)
                                        {
                                            response.setRequestStatus(AgentStatus.SUCCESS);
                                            response.setResponse("Successfully performed stop operation on " + request.getTargetName());
                                        }
                                        else
                                        {
                                            response.setRequestStatus(AgentStatus.FAILURE);
                                            response.setResponse("Stop operation was sent to " + request.getTargetName() + ", but server has not yet stopped.");
                                        }
                                    }
                                    else
                                    {
                                        throw new ServiceMBeanException("Unable to obtain server mbean object");
                                    }
                                }
                                else
                                {
                                    throw new ServiceMBeanException("Unable to obtain server mbean object");
                                }

                                break;
                            case START:
                                adminClient.addNotificationListener(nodeObject, this, null, null);
                                isComplete = (Boolean) adminClient.invoke(nodeObject, "launchProcess", new String[] { request.getTargetName() }, new String[] { "java.lang.String" });

                                if (isComplete)
                                {
                                    response.setRequestStatus(AgentStatus.SUCCESS);
                                    response.setResponse("Successfully performed start operation on " + request.getTargetName());
                                }
                                else
                                {
                                    response.setRequestStatus(AgentStatus.FAILURE);
                                    response.setResponse("Failed to perform start operation on " + request.getTargetName());
                                }

                                break;
                            case STATUS:
                                if (!(serverSet.isEmpty()))
                                {
                                    ObjectName serverObject = (ObjectName) serverSet.iterator().next();

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("serverObject: {}", serverObject);
                                    }

                                    if (serverObject != null)
                                    {
                                        StringBuilder sBuilder = new StringBuilder()
                                            .append("The servers name is: " + adminClient.getAttribute(serverObject, "name") + "\n")
                                            .append("The servers cell is: " + adminClient.getAttribute(serverObject, "cellName") + "\n")
                                            .append("The servers process type is: " + adminClient.getAttribute(serverObject, "processType") + "\n")
                                            .append("The servers version is: " + adminClient.getAttribute(serverObject, "serverVersion") + "\n")
                                            .append("The servers vendor is: " + adminClient.getAttribute(serverObject, "serverVendor") + "\n")
                                            .append("The servers platform version is: " + adminClient.getAttribute(serverObject, "platformVersion") + "\n")
                                            .append("The servers platform name is: " + adminClient.getAttribute(serverObject, "platformName") + "\n")
                                            .append("The servers current status is: " + adminClient.getAttribute(serverObject, "state") + "\n");

                                        if (DEBUG)
                                        {
                                            DEBUGGER.debug("sBuilder: {}", sBuilder);
                                        }

                                        response.setRequestStatus(AgentStatus.SUCCESS);
                                        response.setResponse("Successfully executed status request");
                                        response.setResponseData(sBuilder);
                                    }
                                    else
                                    {
                                        throw new ServiceMBeanException("Unable to obtain server mbean object");
                                    }
                                }
                                else
                                {
                                    throw new ServiceMBeanException("Unable to obtain server mbean object");
                                }

                                break;
                            default:
                                throw new ServiceMBeanException("An invalid operation type was specified. Cannot continue.");
                        }

                        if (DEBUG)
                        {
                            DEBUGGER.debug("MBeanResponse: {}", response);
                        }
                    }
                    else
                    {
                        throw new ServiceMBeanException("Failed to obtain nodeagent mbean");
                    }
                }
                else
                {
                    throw new ServiceMBeanException("Failed to obtain JMX objects");
                }
            }
            else
            {
                throw new JMXConnectorException("Unable to obtain JMX connection");
            }
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
        catch (ConnectorException cx)
        {
            ERROR_RECORDER.error(cx.getMessage(), cx);

            throw new ServiceMBeanException(cx.getMessage(), cx);
        }
        catch (AttributeNotFoundException anfx)
        {
            ERROR_RECORDER.error(anfx.getMessage(), anfx);

            throw new ServiceMBeanException(anfx.getMessage(), anfx);
        }
        catch (InstanceNotFoundException infx)
        {
            ERROR_RECORDER.error(infx.getMessage(), infx);

            throw new ServiceMBeanException(infx.getMessage(), infx);
        }
        catch (MBeanException mx)
        {
            ERROR_RECORDER.error(mx.getMessage(), mx);

            throw new ServiceMBeanException(mx.getMessage(), mx);
        }
        catch (ReflectionException rx)
        {
            ERROR_RECORDER.error(rx.getMessage(), rx);

            throw new ServiceMBeanException(rx.getMessage(), rx);
        }
        catch (InterruptedException ix)
        {
            ERROR_RECORDER.error(ix.getMessage(), ix);
            
            throw new ServiceMBeanException(ix.getMessage(), ix);
        }

        return response;
    }

    /**
     * At the moment the only thing this can do is test, and it hasn't been verified yet.
     * May add the ability to create a datasource here, but I'm not sure yet.
     *
     * @param request
     * @return MBeanResponse
     * @throws ServiceMBeanException
     * @see com.cws.esolutions.agent.jmx.mbeans.interfaces.ServiceMBean#performDataSourceOperation(com.cws.esolutions.agent.jmx.mbeans.dto.MBeanRequest)
     */
    @Override
    public MBeanResponse performDataSourceOperation(final MBeanRequest request) throws ServiceMBeanException
    {
        final String methodName = WebSphereServiceMBeanImpl.CNAME + "#performDataSourceOperation(final final MBeanRequest request) throws ServiceMBeanException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("MBeanRequest: ", request);
        }

        throw new ServiceMBeanException("DataSource operations are not currently supported for WebSphere servers");
        /*String dataSourceURI = null;
        ObjectName targetObject = null;
        AdminClient adminClient = null;
        JMXConnection jmxConnection = null;
        MBeanResponse response = new MBeanResponse();

        try
        {
            jmxConnection = JMXConnectionFactory.createConnector(jmxConfig.getJmxHandler());
            JMXConnectorObject jmxObject = jmxConnection.getJMXConnector(null);

            if (DEBUG)
            {
                DEBUGGER.debug("JMXConnectorObject: {}", jmxObject);
            }

            adminClient = (AdminClient) jmxObject.getConnector();

            if (DEBUG)
            {
                DEBUGGER.debug("AdminClient: {}", adminClient);
            }

            if (adminClient != null)
            {
                // run on the nodeagent
                ObjectName queryName = new ObjectName("WebSphere:cell=" + request.getCellName() + ",node=" + request.getNodeName() + ",process=nodeagent,type=DataSourceCfgHelper");

                if (DEBUG)
                {
                    DEBUGGER.debug("ObjectName: {}", queryName);
                }

                @SuppressWarnings("unchecked")
                Set<Object> objectSet = adminClient.queryNames(queryName, null);

                if (DEBUG)
                {
                    DEBUGGER.debug("objectSet: {}", objectSet);
                }

                if (!(objectSet.isEmpty()))
                {
                    Iterator iter = objectSet.iterator();
                    nodeObject = (ObjectName) objectSet.iterator().next();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Iterator: {}", iter);
                    }

                    while (iter.hasNext())
                    {
                        ObjectName iterName = (ObjectName) iter.next();

                        if (DEBUG)
                        {
                            DEBUGGER.debug("ObjectName: {}", iterName);
                        }

                        if (iterName != null)
                        {
                            // found object
                            targetObject = iterName;

                            if (DEBUG)
                            {
                                DEBUGGER.debug("ObjectName: {}", targetObject);
                            }

                            break;
                        }
                    }

                    if (targetObject != null)
                    {
                        Object result = adminClient.invoke(targetObject, "testConnection", dataSourceURI, new String[] { "java.lang.String" });

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Object: {}", result);
                        }

                        response.setResponseStatus(AgentStatus.SUCCESS);
                        response.setResponse("Successfully executed test connection for datasource");
                        response.setResponseData(result);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("MBeanResponse: {}", response);
                        }
                    }
                    else
                    {
                        throw new ServiceMBeanException("Failed to obtain nodeagent mbean");
                    }
                }
                else
                {
                    throw new ServiceMBeanException("Failed to obtain JMX objects");
                }
            }
            else
            {
                throw new JMXConnectorException("Unable to obtain JMX connection");
            }
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
        catch (ConnectorException cx)
        {
            ERROR_RECORDER.error(cx.getMessage(), cx);

            throw new ServiceMBeanException(cx.getMessage(), cx);
        }
        catch (AttributeNotFoundException anfx)
        {
            ERROR_RECORDER.error(anfx.getMessage(), anfx);

            throw new ServiceMBeanException(anfx.getMessage(), anfx);
        }
        catch (InstanceNotFoundException infx)
        {
            ERROR_RECORDER.error(infx.getMessage(), infx);

            throw new ServiceMBeanException(infx.getMessage(), infx);
        }
        catch (MBeanException mx)
        {
            ERROR_RECORDER.error(mx.getMessage(), mx);

            throw new ServiceMBeanException(mx.getMessage(), mx);
        }
        catch (ReflectionException rx)
        {
            ERROR_RECORDER.error(rx.getMessage(), rx);

            throw new ServiceMBeanException(rx.getMessage(), rx);
        }
        catch (InterruptedException ix)
        {
            ERROR_RECORDER.error(ix.getMessage(), ix);
            
            throw new ServiceMBeanException(ix.getMessage(), ix);
        }

        return response;
        */
    }

    @Override
    public MBeanResponse performApplicationOperation(final MBeanRequest request) throws ServiceMBeanException
    {
        final String methodName = WebSphereServiceMBeanImpl.CNAME + "#performApplicationOperation(final MBeanRequest request) throws ServiceMBeanException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("MBeanRequest: {}", request);
        }

        AdminClient adminClient = null;
        JMXConnection jmxConnection = null;
        MBeanResponse response = new MBeanResponse();

        final String targetCluster = "WebSphere:cell=" + request.getCellName() + ",node=" + request.getNodeAgentName() + ",cluster=" + request.getClusterName() + ",*";

        if (DEBUG)
        {
            DEBUGGER.debug("targetCluster: {}", targetCluster);
        }

        try
        {
            jmxConnection = JMXConnectionFactory.createConnector(jmxConfig.getJmxHandler());
            JMXConnectorObject jmxObject = jmxConnection.getDeploymentConnector();

            if (DEBUG)
            {
                DEBUGGER.debug("JMXConnectorObject: {}", jmxObject);
            }

            adminClient = (AdminClient) jmxObject.getConnector();

            if (DEBUG)
            {
                DEBUGGER.debug("AdminClient: {}", adminClient);
            }

            if (adminClient != null)
            {
                ObjectName appObject = new ObjectName("WebSphere:name=AppManagement,process=dmgr,node=dmgrnode,type=AppManagement,*");

                if (DEBUG)
                {
                    DEBUGGER.debug("ObjectName: {}", appObject);
                }

                @SuppressWarnings("unchecked")
                Set<Object> objectSet = adminClient.queryNames(appObject, null);

                if (DEBUG)
                {
                    DEBUGGER.debug("objectSet: {}", objectSet);
                }

                if (!(objectSet.isEmpty()))
                {
                    ObjectName mgmtObject = (ObjectName) objectSet.iterator().next();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("ObjectName: {}", mgmtObject);
                    }

                    if (mgmtObject != null)
                    {
                        adminClient.addNotificationListener(mgmtObject, this, null, null);
                        AppManagement appMgmt = AppManagementProxy.getJMXProxyForClient(adminClient);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("AppManagement: {}", appMgmt);
                        }

                        if (appMgmt != null)
                        {
                            Hashtable<String, Object> appTable = new Hashtable<>();
                            appTable.put(AppConstants.ADMINCLIENT, adminClient);
                            appTable.put(AppConstants.APPDEPL_CELL, "wascell"); // TODO: fix
                            appTable.put(AppConstants.APPDEPL_CLUSTER, request.getClusterName());
                            appTable.put(AppConstants.APPDEPL_LOCALE, Locale.getDefault());
                            appTable.put(AppConstants.APPDEPL_DISTRIBUTE_APP, true);
                            appTable.put(AppConstants.APPDEPL_INSTALL_DIR, request.getInstallPath());
                            appTable.put(AppConstants.APPDEPL_APP_VERSION, request.getAppVersion());
                            appTable.put(AppConstants.APPDEPL_ARCHIVE_UPLOAD, true);
                            appTable.put(AppConstants.APPDEPL_APPNAME, request.getApplication());
                            appTable.put(AppConstants.APPDEPL_MODULE_TO_SERVER, targetCluster);
                            appTable.put(AppConstants.APPDEPL_DFLTBNDG_VHOST, request.getVirtualHost());
                            appTable.put(AppConstants.APPDEPL_FILETRANSFER_UPLOAD, true);

                            if (DEBUG)
                            {
                                DEBUGGER.debug("appTable: {}", appTable);
                            }

                            AppDeploymentController deployController = AppDeploymentController.readArchive(request.getBinary().getPath(), appTable);

                            if (DEBUG)
                            {
                                DEBUGGER.debug("AppDeploymentController: {}", deployController);
                            }

                            AppDeploymentTask deployTask = deployController.getFirstTask();

                            if (DEBUG)
                            {
                                DEBUGGER.debug("AppDeploymentTask: {}", deployTask);
                            }

                            while (deployTask != null)
                            {
                                String[][] data = deployTask.getTaskData();

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("data: {}", (Object) data);
                                }

                                deployTask.setTaskData(data);

                                deployTask = deployController.getNextTask();
                            }

                            deployController.saveAndClose();

                            Session configSession = new Session();

                            if (DEBUG)
                            {
                                DEBUGGER.debug("Session: {}", configSession);
                            }

                            ConfigServiceProxy configProxy = new ConfigServiceProxy(adminClient);

                            if (DEBUG)
                            {
                                DEBUGGER.debug("ConfigServiceProxy: {}", configProxy);
                            }

                            appMgmt.installApplication(request.getBinary().getPath(), request.getApplication(), appTable, configSession.getSessionId());
                        }
                        else
                        {
                            throw new ServiceMBeanException("Unable to obtain application management instance");
                        }
                    }
                    else
                    {
                        // no mgmt object
                        throw new ServiceMBeanException("Unable to obtain application management object");
                    }
                }
                else
                {
                    // no mgmt object
                    throw new ServiceMBeanException("Unable to obtain application management objects");
                }
            }
            else
            {
                throw new JMXConnectorException("Unable to obtain JMX connection");
            }
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
        catch (ConnectorException cx)
        {
            ERROR_RECORDER.error(cx.getMessage(), cx);

            throw new ServiceMBeanException(cx.getMessage(), cx);
        }
        catch (AttributeNotFoundException anfx)
        {
            ERROR_RECORDER.error(anfx.getMessage(), anfx);

            throw new ServiceMBeanException(anfx.getMessage(), anfx);
        }
        catch (InstanceNotFoundException infx)
        {
            ERROR_RECORDER.error(infx.getMessage(), infx);

            throw new ServiceMBeanException(infx.getMessage(), infx);
        }
        catch (MBeanException mx)
        {
            ERROR_RECORDER.error(mx.getMessage(), mx);

            throw new ServiceMBeanException(mx.getMessage(), mx);
        }
        catch (ReflectionException rx)
        {
            ERROR_RECORDER.error(rx.getMessage(), rx);

            throw new ServiceMBeanException(rx.getMessage(), rx);
        }
        catch (Exception e)
        {
            ERROR_RECORDER.error(e.getMessage(), e);
            
            throw new ServiceMBeanException(e.getMessage(), e);
        }

        return response;
    }

    @Override
    public void handleNotification(final Notification notify, final Object object)
    {
        final String methodName = WebSphereServiceMBeanImpl.CNAME + "#handleNotification(final Notification notify, final Object object)";
        
        if (DEBUG)
        {
        	DEBUGGER.debug(methodName);
            DEBUGGER.debug("Notification: {}", notify);
            DEBUGGER.debug("Object: {}", object);
        }

        if (DEBUG)
        {
            DEBUGGER.debug("***************************************************");
            DEBUGGER.debug("* Notification received at {}", new java.util.Date().toString());
            DEBUGGER.debug("* type           = {}", notify.getType());
            DEBUGGER.debug("* message        = {}", notify.getMessage());
            DEBUGGER.debug("* source         = {}", notify.getSource());
            DEBUGGER.debug("* sequenceNum    = {}", notify.getSequenceNumber());
            DEBUGGER.debug("* timeStamp      = {}", new java.util.Date(notify.getTimeStamp()));
            DEBUGGER.debug("* userData       = {}", notify.getUserData());
            DEBUGGER.debug("***************************************************");
        }
    }
}
