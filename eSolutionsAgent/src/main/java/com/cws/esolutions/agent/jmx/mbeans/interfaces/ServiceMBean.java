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
package com.cws.esolutions.agent.jmx.mbeans.interfaces;
/*
 * Project: eSolutionsAgent
 * Package: com.cws.esolutions.agent.jmx.mbeans.interfaces
 * File: ServiceMBean.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.agent.Constants;
import com.cws.esolutions.agent.AgentBean;
import com.cws.esolutions.agent.config.xml.JMXConfig;
import com.cws.esolutions.agent.jmx.mbeans.dto.MBeanRequest;
import com.cws.esolutions.agent.jmx.mbeans.dto.MBeanResponse;
import com.cws.esolutions.agent.config.xml.ApplicationConfig;
import com.cws.esolutions.agent.jmx.mbeans.exception.ServiceMBeanException;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 */
public interface ServiceMBean
{
    static final AgentBean appBean = AgentBean.getInstance();

    static final String CNAME = ServiceMBean.class.getName();
    static final JMXConfig jmxConfig = appBean.getConfigData().getJmxConfig();
    static final ApplicationConfig appConfig = appBean.getConfigData().getAppConfig();

    // loggers
    static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CNAME);

    public MBeanResponse performServerOperation(final MBeanRequest request) throws ServiceMBeanException;

    public MBeanResponse performDataSourceOperation(final MBeanRequest request) throws ServiceMBeanException;

    public MBeanResponse performApplicationOperation(final MBeanRequest request) throws ServiceMBeanException;
}
