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
package com.cws.esolutions.agent.jmx.mbeans.impl;
/*
 * Project: eSolutionsAgent
 * Package: com.cws.esolutions.agent.jmx.mbeans.impl
 * File: WebSphereServiceMBeanImplTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.io.File;
import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;

import com.cws.esolutions.agent.AgentBean;
import com.cws.esolutions.agent.AgentDaemon;
import com.cws.esolutions.agent.enums.AgentStatus;
import com.cws.esolutions.agent.jmx.mbeans.dto.MBeanRequest;
import com.cws.esolutions.agent.jmx.mbeans.dto.MBeanResponse;
import com.cws.esolutions.agent.jmx.mbeans.enums.MBeanRequestType;
import com.cws.esolutions.agent.jmx.mbeans.interfaces.ServiceMBean;
import com.cws.esolutions.agent.jmx.mbeans.factory.ServiceMBeanFactory;
import com.cws.esolutions.agent.jmx.mbeans.exception.ServiceMBeanException;

public class WebSphereServiceMBeanImplTest
{
    private static final AgentBean agentBean = AgentBean.getInstance();

    @Before
    public void setUp()
    {
        System.setProperty("LOG_ROOT", "C:/temp");
        System.setProperty("appConfig", "/src/main/resources/eSolutionsServer/config/eSolutionsServer.xml");
        System.setProperty("logConfig", "/src/main/resources/logging/logging.xml");
        
        AgentDaemon.main(new String[] {"start"});
    }

    @Test
    public final void testPerformServerOperationGetStatus()
    {
        MBeanRequest request = new MBeanRequest();
        request.setCellName("wascell");
        request.setNodeAgentName("nodeagent");
        request.setRequestTimeout(900);
        request.setRequestType(MBeanRequestType.STATUS);
        request.setTargetName("LASGLE0101UK");
        request.setClusterName("LGLE01UK");

        ServiceMBean serviceBean = ServiceMBeanFactory.createServiceMBean(agentBean.getConfigData().getJmxConfig().getMbeanHandler());

        try
        {
            MBeanResponse response = serviceBean.performServerOperation(request);

            Assert.assertEquals(AgentStatus.SUCCESS, response.getRequestStatus());
        }
        catch (ServiceMBeanException smbx)
        {
            Assert.fail(smbx.getMessage());
        }
    }

    @Test
    public final void testPerformServerOperationStopServer()
    {
        MBeanRequest request = new MBeanRequest();
        request.setCellName("wascell");
        request.setNodeAgentName("nodeagent");
        request.setRequestTimeout(900);
        request.setRequestType(MBeanRequestType.STOP);
        request.setTargetName("server1");

        ServiceMBean serviceBean = ServiceMBeanFactory.createServiceMBean(agentBean.getConfigData().getJmxConfig().getMbeanHandler());

        try
        {
            MBeanResponse response = serviceBean.performServerOperation(request);

            Assert.assertEquals(AgentStatus.SUCCESS, response.getRequestStatus());
        }
        catch (ServiceMBeanException smbx)
        {
            Assert.fail(smbx.getMessage());
        }
    }

    @Test
    public final void testPerformServerOperationStartServer()
    {
        MBeanRequest request = new MBeanRequest();
        request.setCellName("wascell");
        request.setNodeAgentName("nodeagent");
        request.setRequestTimeout(900);
        request.setRequestType(MBeanRequestType.START);
        request.setTargetName("server1");

        ServiceMBean serviceBean = ServiceMBeanFactory.createServiceMBean(agentBean.getConfigData().getJmxConfig().getMbeanHandler());

        try
        {
            MBeanResponse response = serviceBean.performServerOperation(request);

            Assert.assertEquals(AgentStatus.SUCCESS, response.getRequestStatus());
        }
        catch (ServiceMBeanException smbx)
        {
            Assert.fail(smbx.getMessage());
        }
    }

    @Test
    public final void testPerformApplicationOperation()
    {
        MBeanRequest request = new MBeanRequest();
        request.setCellName("wascell");
        request.setNodeAgentName("nodeagent");
        request.setRequestTimeout(900);
        request.setRequestType(MBeanRequestType.DEPLOY);
        request.setTargetName("cluster1");
        request.setApplication("eSolutions");
        request.setApplication("eSolutions");
        request.setAppVersion("1.0");
        request.setBinary(new File("C:/Users/khuntly/Documents/GitHub/cws-esolutions/web/eSolutions/ears/eSolutions_web_sourceEAR/target/eSolutions-1.0.ear"));
        request.setCellName("wascell");
        request.setClusterName("cluster1");
        request.setForceOperation(false);
        request.setInstallPath("/var/tmp/myapp");
        request.setIsLibrary(false);
        request.setRequestType(MBeanRequestType.DEPLOY);
        request.setVirtualHost("default_host");


        ServiceMBean serviceBean = ServiceMBeanFactory.createServiceMBean(agentBean.getConfigData().getJmxConfig().getMbeanHandler());

        try
        {
            MBeanResponse response = serviceBean.performApplicationOperation(request);

            Assert.assertEquals(AgentStatus.SUCCESS, response.getRequestStatus());
        }
        catch (ServiceMBeanException smbx)
        {
            Assert.fail(smbx.getMessage());
        }
    }
}
